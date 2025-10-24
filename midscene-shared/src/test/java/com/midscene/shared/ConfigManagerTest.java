package com.midscene.shared;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

final class ConfigManagerTest {

    static void run() throws IOException {
        Map<String, String> env = new HashMap<>();
        env.put(EnvKeys.MIDSCENE_DEBUG_MODE, "true");
        env.put(EnvKeys.MIDSCENE_RUN_DIR, "run");
        EnvironmentUtils.setEnvProvider(env::get);

        ConfigManager manager = new ConfigManager();
        TestSupport.assertTrue(manager.getBoolean(EnvKeys.MIDSCENE_DEBUG_MODE, false), "debug mode env");
        TestSupport.assertEquals("run", manager.get(EnvKeys.MIDSCENE_RUN_DIR).orElse(null), "run dir env");

        Path tempFile = Files.createTempFile("midscene-config", ".json");
        Files.writeString(tempFile, "{" +
                "\"MIDSCENE_DEBUG_MODE\": false,\n" +
                "\"custom\": { \"nested\": 123 }\n" +
                "}");

        manager.loadFromJson(tempFile);

        TestSupport.assertFalse(manager.getBoolean(EnvKeys.MIDSCENE_DEBUG_MODE, true), "json overrides env");
        TestSupport.assertEquals(123, manager.getInt("custom.nested", 0), "nested override");
        TestSupport.assertEquals("123", manager.get("custom.nested").orElse(null), "nested string");

        EnvironmentUtils.resetEnvProvider();
    }
}
