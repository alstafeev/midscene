package com.midscene.shared;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Constants that mirror the env key declarations from the TypeScript shared package.
 */
public final class EnvKeys {
    private EnvKeys() {}

    public static final String MIDSCENE_OPENAI_INIT_CONFIG_JSON = "MIDSCENE_OPENAI_INIT_CONFIG_JSON";
    public static final String MIDSCENE_MODEL_NAME = "MIDSCENE_MODEL_NAME";
    public static final String MIDSCENE_LANGSMITH_DEBUG = "MIDSCENE_LANGSMITH_DEBUG";
    public static final String MIDSCENE_DEBUG_AI_PROFILE = "MIDSCENE_DEBUG_AI_PROFILE";
    public static final String MIDSCENE_DEBUG_AI_RESPONSE = "MIDSCENE_DEBUG_AI_RESPONSE";
    public static final String MIDSCENE_DANGEROUSLY_PRINT_ALL_CONFIG = "MIDSCENE_DANGEROUSLY_PRINT_ALL_CONFIG";
    public static final String MIDSCENE_DEBUG_MODE = "MIDSCENE_DEBUG_MODE";
    public static final String MIDSCENE_MCP_USE_PUPPETEER_MODE = "MIDSCENE_MCP_USE_PUPPETEER_MODE";
    public static final String MIDSCENE_MCP_CHROME_PATH = "MIDSCENE_MCP_CHROME_PATH";
    public static final String MIDSCENE_MCP_ANDROID_MODE = "MIDSCENE_MCP_ANDROID_MODE";
    public static final String DOCKER_CONTAINER = "DOCKER_CONTAINER";
    public static final String MIDSCENE_FORCE_DEEP_THINK = "MIDSCENE_FORCE_DEEP_THINK";
    public static final String MIDSCENE_RUN_DIR = "MIDSCENE_RUN_DIR";
    public static final String MIDSCENE_CACHE = "MIDSCENE_CACHE";
    public static final String MIDSCENE_REPORT_TAG_NAME = "MIDSCENE_REPORT_TAG_NAME";
    public static final String MIDSCENE_CACHE_MAX_FILENAME_LENGTH = "MIDSCENE_CACHE_MAX_FILENAME_LENGTH";

    private static final Set<String> BASIC_ENV_KEYS;
    private static final Set<String> BOOLEAN_ENV_KEYS;

    static {
        LinkedHashSet<String> basicKeys = new LinkedHashSet<>();
        basicKeys.add(MIDSCENE_DEBUG_MODE);
        basicKeys.add(MIDSCENE_DEBUG_AI_PROFILE);
        basicKeys.add(MIDSCENE_DEBUG_AI_RESPONSE);
        basicKeys.add(MIDSCENE_RUN_DIR);
        BASIC_ENV_KEYS = Collections.unmodifiableSet(basicKeys);

        LinkedHashSet<String> booleanKeys = new LinkedHashSet<>();
        booleanKeys.add(MIDSCENE_CACHE);
        booleanKeys.add(MIDSCENE_LANGSMITH_DEBUG);
        booleanKeys.add(MIDSCENE_FORCE_DEEP_THINK);
        booleanKeys.add(MIDSCENE_MCP_USE_PUPPETEER_MODE);
        booleanKeys.add(MIDSCENE_MCP_ANDROID_MODE);
        BOOLEAN_ENV_KEYS = Collections.unmodifiableSet(booleanKeys);
    }

    public static Set<String> basicEnvKeys() {
        return BASIC_ENV_KEYS;
    }

    public static boolean isBasicKey(String key) {
        return BASIC_ENV_KEYS.contains(key);
    }

    public static boolean isBooleanKey(String key) {
        return BOOLEAN_ENV_KEYS.contains(key);
    }
}
