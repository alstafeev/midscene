package com.midscene.shared;

import org.junit.jupiter.api.Test;

/**
 * Bridges the legacy custom test harness with Maven's JUnit-based execution.
 */
class SharedTestSuite {

    @Test
    void runAllSuites() throws Exception {
        EnvironmentUtilsTest.run();
        EnvKeysTest.run();
        RunDirectoryHelperTest.run();
        MidsceneLoggerFactoryTest.run();
        ConfigManagerTest.run();
    }
}
