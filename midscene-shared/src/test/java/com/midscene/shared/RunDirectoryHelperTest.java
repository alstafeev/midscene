package com.midscene.shared;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

final class RunDirectoryHelperTest {

    static void run() throws IOException {
        String originalUserDir = System.getProperty("user.dir");
        try {
            Path tempDir = Files.createTempDirectory("midscene-test");
            System.setProperty("user.dir", tempDir.toString());

            Map<String, String> env = new HashMap<>();
            env.put(EnvKeys.MIDSCENE_RUN_DIR, "custom_run");
            EnvironmentUtils.setEnvProvider(env::get);

            Path baseDir = RunDirectoryHelper.getRunBaseDir();
            TestSupport.assertEquals(tempDir.resolve("custom_run"), baseDir, "base dir override");
            TestSupport.assertTrue(Files.exists(baseDir), "base dir created");

            Path logDir = RunDirectoryHelper.getRunSubDir(RunDirectoryHelper.SubDirectory.LOG);
            TestSupport.assertEquals(baseDir.resolve("log"), logDir, "log dir path");
            TestSupport.assertTrue(Files.exists(logDir), "log dir created");

            EnvironmentUtils.resetEnvProvider();

            Files.deleteIfExists(baseDir.resolve("log"));
            Files.deleteIfExists(baseDir);

            Path conflict = tempDir.resolve(RunDirectoryHelper.DEFAULT_RUN_DIR_NAME);
            Files.writeString(conflict, "conflict");

            Path fallback = RunDirectoryHelper.getRunBaseDir();
            TestSupport.assertFalse(fallback.equals(conflict), "fallback different from conflict");
            TestSupport.assertTrue(fallback.startsWith(Path.of(System.getProperty("java.io.tmpdir"))), "fallback uses tmp");
            TestSupport.assertTrue(Files.exists(fallback), "fallback created");
        } finally {
            System.setProperty("user.dir", originalUserDir);
            EnvironmentUtils.resetEnvProvider();
        }
    }
}
