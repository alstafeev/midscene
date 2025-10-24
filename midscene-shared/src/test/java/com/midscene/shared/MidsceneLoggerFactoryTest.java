package com.midscene.shared;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

final class MidsceneLoggerFactoryTest {

    static void run() throws IOException {
        String originalUserDir = System.getProperty("user.dir");
        Path tempDir = Files.createTempDirectory("midscene-logger");
        System.setProperty("user.dir", tempDir.toString());

        try {
            MidsceneLoggerFactory.DebugFunction debug = MidsceneLoggerFactory.getDebug("automation:test");
            debug.log("hello", "world");

            Path logDir = RunDirectoryHelper.getRunSubDir(RunDirectoryHelper.SubDirectory.LOG);
            Path logFile = logDir.resolve("automation-test.log");
            TestSupport.assertTrue(Files.exists(logFile), "log file created");

            List<String> lines = Files.readAllLines(logFile);
            TestSupport.assertEquals(1, lines.size(), "log line count");
            TestSupport.assertContains(lines.getFirst(), "hello world", "log content");
        } finally {
            System.setProperty("user.dir", originalUserDir);
            MidsceneLoggerFactory.cleanupLogStreams();
        }
    }
}
