package com.midscene.shared;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Logging facade that mimics the behaviour of the TypeScript {@code getDebug} helper.
 */
public final class MidsceneLoggerFactory {
    private static final String TOPIC_PREFIX = "midscene";
    private static final Map<String, DebugFunction> DEBUG_FUNCTIONS = new ConcurrentHashMap<>();
    private static final Map<String, FileHandler> FILE_HANDLERS = new ConcurrentHashMap<>();

    private MidsceneLoggerFactory() {}

    public static DebugFunction getDebug(String topic) {
        Objects.requireNonNull(topic, "topic");
        String sanitizedTopic = RunDirectoryHelper.sanitizeTopic(topic);
        String fullTopic = TOPIC_PREFIX + ":" + topic;
        return DEBUG_FUNCTIONS.computeIfAbsent(fullTopic, key -> createDebugFunction(topic, sanitizedTopic));
    }

    public static void enableDebug(String topic) {
        Objects.requireNonNull(topic, "topic");
        Logger logger = Logger.getLogger(TOPIC_PREFIX + "." + topic);
        logger.setLevel(Level.FINE);
    }

    public static void cleanupLogStreams() {
        FILE_HANDLERS.values().forEach(handler -> {
            handler.flush();
            handler.close();
        });
        FILE_HANDLERS.clear();
        DEBUG_FUNCTIONS.clear();
    }

    private static DebugFunction createDebugFunction(String originalTopic, String sanitizedTopic) {
        Logger logger = Logger.getLogger(TOPIC_PREFIX + "." + originalTopic);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.FINE);
        logger.setFilter(record -> true);

        FileHandler handler = FILE_HANDLERS.computeIfAbsent(originalTopic, ignored -> {
            try {
                Path logFile = RunDirectoryHelper.getRunSubDir(RunDirectoryHelper.SubDirectory.LOG)
                        .resolve(sanitizedTopic + ".log");
                FileHandler fileHandler = new FileHandler(logFile.toString(), true);
                fileHandler.setEncoding(StandardCharsets.UTF_8.name());
                fileHandler.setLevel(Level.FINE);
                fileHandler.setFormatter(new IsoLikeFormatter());
                logger.addHandler(fileHandler);
                return fileHandler;
            } catch (IOException ex) {
                throw new IllegalStateException("Failed to configure logger for topic " + originalTopic, ex);
            }
        });

        return args -> {
            if (args.length == 0) {
                return;
            }
            String message = formatArgs(args);
            logger.log(Level.FINE, message);
            handler.flush();
        };
    }

    private static String formatArgs(Object[] args) {
        if (args.length == 1) {
            Object arg = args[0];
            return Objects.toString(arg);
        }
        return Arrays.stream(args)
                .map(obj -> {
                    if (obj instanceof String str && str.contains("{0}")) {
                        return MessageFormat.format(str, Arrays.copyOfRange(args, 1, args.length));
                    }
                    return Objects.toString(obj);
                })
                .reduce((left, right) -> left + " " + right)
                .orElse("");
    }

    private static final class IsoLikeFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return String.format("[%1$tFT%1$tT.%1$tL%1$tz] %2$s%n", record.getMillis(), formatMessage(record));
        }
    }

    @FunctionalInterface
    public interface DebugFunction {
        void log(Object... args);
    }
}
