package com.midscene.shared;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Helper methods for working with environment variables and performing
 * environment detection similar to the TypeScript utilities.
 */
public final class EnvironmentUtils {
    private static final Map<String, String> OVERRIDES = new ConcurrentHashMap<>();
    private static volatile Function<String, String> envProvider = System::getenv;

    private EnvironmentUtils() {}

    public static Optional<String> getEnv(String key) {
        Objects.requireNonNull(key, "key");
        if (OVERRIDES.containsKey(key)) {
            return Optional.ofNullable(OVERRIDES.get(key));
        }
        return Optional.ofNullable(envProvider.apply(key));
    }

    public static Optional<String> getBasicEnvValue(String key) {
        if (!EnvKeys.isBasicKey(key)) {
            throw new IllegalArgumentException("getBasicEnvValue with key " + key + " is not supported.");
        }
        return getEnv(key);
    }

    public static boolean isDebugModeEnabled() {
        return getBasicEnvValue(EnvKeys.MIDSCENE_DEBUG_MODE).map(EnvironmentUtils::toBoolean).orElse(false);
    }

    public static boolean isDocker() {
        return getEnv(EnvKeys.DOCKER_CONTAINER).map(EnvironmentUtils::toBoolean).orElse(false);
    }

    public static boolean isCi() {
        return getEnv("CI").map(EnvironmentUtils::toBoolean).orElse(false);
    }

    public static boolean toBoolean(String value) {
        if (value == null) {
            return false;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            return false;
        }
        return !(normalized.equals("false") || normalized.equals("0") || normalized.equals("no"));
    }

    public static Optional<Integer> toInteger(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(value.trim()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public static void override(String key, String value) {
        if (value == null) {
            OVERRIDES.remove(key);
        } else {
            OVERRIDES.put(key, value);
        }
    }

    public static void clearOverrides() {
        OVERRIDES.clear();
    }

    public static void setEnvProvider(Function<String, String> provider) {
        envProvider = provider != null ? provider : System::getenv;
    }

    public static void resetEnvProvider() {
        envProvider = System::getenv;
        clearOverrides();
    }
}
