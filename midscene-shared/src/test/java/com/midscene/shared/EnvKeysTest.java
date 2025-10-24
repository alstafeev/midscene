package com.midscene.shared;

import java.util.Set;

final class EnvKeysTest {

    private EnvKeysTest() {}

    static void run() {
        TestSupport.assertTrue(
                EnvKeys.basicEnvKeys().contains(EnvKeys.MIDSCENE_RUN_DIR),
                "basic keys include run dir");
        TestSupport.assertFalse(
                EnvKeys.isBasicKey(EnvKeys.OPENAI_API_KEY),
                "openai api key is not basic");

        TestSupport.assertTrue(
                EnvKeys.booleanEnvKeys().contains(EnvKeys.MIDSCENE_CACHE),
                "boolean keys contain cache flag");
        TestSupport.assertTrue(
                EnvKeys.booleanEnvKeys().contains(EnvKeys.MIDSCENE_MCP_USE_PUPPETEER_MODE),
                "boolean keys include puppeteer flag");

        TestSupport.assertTrue(
                EnvKeys.globalEnvKeys().contains(EnvKeys.MIDSCENE_PREFERRED_LANGUAGE),
                "global keys include preferred language");
        TestSupport.assertTrue(
                EnvKeys.stringEnvKeys().contains(EnvKeys.DOCKER_CONTAINER),
                "string keys include docker container");

        TestSupport.assertTrue(
                EnvKeys.modelEnvKeys().contains(EnvKeys.MIDSCENE_MODEL_NAME),
                "model keys include default model name");
        TestSupport.assertTrue(
                EnvKeys.modelEnvKeys().contains(EnvKeys.MIDSCENE_VQA_MODEL_NAME),
                "model keys include vqa model");
        TestSupport.assertTrue(
                EnvKeys.modelEnvKeys().contains(EnvKeys.MIDSCENE_PLANNING_MODEL_NAME),
                "model keys include planning model");

        Set<String> allKeys = EnvKeys.allEnvKeys();
        TestSupport.assertTrue(
                allKeys.contains(EnvKeys.MIDSCENE_DANGEROUSLY_PRINT_ALL_CONFIG),
                "all keys include unused flag");
        TestSupport.assertTrue(
                allKeys.contains(EnvKeys.MIDSCENE_MODEL_NAME),
                "all keys include model name");
        TestSupport.assertTrue(
                allKeys.contains(EnvKeys.MIDSCENE_RUN_DIR),
                "all keys include run dir");
    }
}
