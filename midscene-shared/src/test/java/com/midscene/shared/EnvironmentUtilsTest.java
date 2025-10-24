package com.midscene.shared;

import java.util.HashMap;
import java.util.Map;

final class EnvironmentUtilsTest {

    static void run() {
        try {
            EnvironmentUtils.getBasicEnvValue("UNKNOWN");
            throw new AssertionError("Expected exception for unknown key");
        } catch (IllegalArgumentException ex) {
            TestSupport.assertContains(ex.getMessage(), "UNKNOWN", "unknown key message");
        }

        Map<String, String> env = new HashMap<>();
        env.put(EnvKeys.MIDSCENE_DEBUG_MODE, "true");
        env.put(EnvKeys.DOCKER_CONTAINER, "1");
        env.put("CI", "false");
        EnvironmentUtils.setEnvProvider(env::get);

        TestSupport.assertTrue(EnvironmentUtils.isDebugModeEnabled(), "debug mode");
        TestSupport.assertTrue(EnvironmentUtils.isDocker(), "docker detection");
        TestSupport.assertFalse(EnvironmentUtils.isCi(), "ci detection");

        EnvironmentUtils.override(EnvKeys.MIDSCENE_DEBUG_MODE, "false");
        TestSupport.assertFalse(EnvironmentUtils.isDebugModeEnabled(), "override priority");

        EnvironmentUtils.resetEnvProvider();

        TestSupport.assertTrue(EnvironmentUtils.toInteger("42").orElseThrow() == 42, "parse int");
        TestSupport.assertTrue(EnvironmentUtils.toInteger("invalid").isEmpty(), "invalid int");
        TestSupport.assertTrue(EnvironmentUtils.toInteger(null).isEmpty(), "null int");
    }
}
