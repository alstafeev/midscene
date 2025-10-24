package com.midscene.shared;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * File system helpers mirroring {@code packages/shared/src/common.ts}.
 */
public final class RunDirectoryHelper {
    public static final String DEFAULT_RUN_DIR_NAME = "midscene_run";

    private RunDirectoryHelper() {}

    public enum SubDirectory {
        DUMP("dump"),
        CACHE("cache"),
        REPORT("report"),
        TMP("tmp"),
        LOG("log"),
        OUTPUT("output");

        private final String dirName;

        SubDirectory(String dirName) {
            this.dirName = dirName;
        }

        public String dirName() {
            return dirName;
        }

        public static SubDirectory fromString(String name) {
            for (SubDirectory value : values()) {
                if (value.dirName.equalsIgnoreCase(name)) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Unknown sub directory: " + name);
        }
    }

    public static String getRunDirName() {
        return EnvironmentUtils.getBasicEnvValue(EnvKeys.MIDSCENE_RUN_DIR)
                .filter(value -> !value.isBlank())
                .orElse(DEFAULT_RUN_DIR_NAME);
    }

    public static Path getRunBaseDir() {
        Path workingDirectory = Paths.get(System.getProperty("user.dir"));
        Path candidate = workingDirectory.resolve(getRunDirName());
        try {
            return ensureDirectory(candidate);
        } catch (IOException ex) {
            Path fallback = Paths.get(System.getProperty("java.io.tmpdir"), DEFAULT_RUN_DIR_NAME);
            try {
                return ensureDirectory(fallback);
            } catch (IOException inner) {
                throw new IllegalStateException("Failed to create run directory at " + fallback, inner);
            }
        }
    }

    public static Path getRunSubDir(SubDirectory subDirectory) {
        Path base = getRunBaseDir();
        Path sub = base.resolve(subDirectory.dirName());
        try {
            return ensureDirectory(sub);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to create sub directory " + subDirectory.dirName(), ex);
        }
    }

    public static Path getRunSubDir(String name) {
        return getRunSubDir(SubDirectory.fromString(name));
    }

    private static Path ensureDirectory(Path path) throws IOException {
        try {
            Files.createDirectories(path);
        } catch (FileAlreadyExistsException ignored) {
            if (!Files.isDirectory(path)) {
                throw ignored;
            }
        }
        return path;
    }

    public static String sanitizeTopic(String topic) {
        return topic.replace(':', '-').toLowerCase(Locale.ROOT);
    }
}
