package com.midscene.shared;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration manager that merges environment variables with JSON overrides.
 */
public final class ConfigManager {
    private final Map<String, String> values = new ConcurrentHashMap<>();

    public ConfigManager() {
        reloadFromEnvironment();
    }

    public void reloadFromEnvironment() {
        EnvKeys.basicEnvKeys().forEach(key ->
                EnvironmentUtils.getBasicEnvValue(key).ifPresent(value -> values.put(key, value)));
    }

    public void loadFromJson(Path path) {
        Objects.requireNonNull(path, "path");
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Config file does not exist: " + path);
        }
        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            Map<String, Object> raw = SimpleJsonParser.parseObject(content);
            merge(flatten(raw, ""));
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load config from " + path, ex);
        }
    }

    public void set(String key, String value) {
        if (value == null) {
            values.remove(key);
        } else {
            values.put(key, value);
        }
    }

    public Optional<String> get(String key) {
        if (values.containsKey(key)) {
            return Optional.ofNullable(values.get(key));
        }
        return EnvironmentUtils.getEnv(key);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return get(key).map(EnvironmentUtils::toBoolean).orElse(defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return get(key).flatMap(EnvironmentUtils::toInteger).orElse(defaultValue);
    }

    public Map<String, String> snapshot() {
        return Collections.unmodifiableMap(new HashMap<>(values));
    }

    private void merge(Map<String, String> entries) {
        entries.forEach((key, value) -> {
            if (value != null) {
                values.put(key, value);
            }
        });
    }

    private Map<String, String> flatten(Map<String, Object> input, String prefix) {
        Map<String, String> result = new HashMap<>();
        input.forEach((key, value) -> {
            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;
            if (value instanceof Map<?, ?> nested) {
                @SuppressWarnings("unchecked")
                Map<String, Object> cast = (Map<String, Object>) nested;
                result.putAll(flatten(cast, fullKey));
            } else if (value instanceof Number number) {
                if (number.doubleValue() == number.longValue()) {
                    result.put(fullKey, Long.toString(number.longValue()));
                } else {
                    result.put(fullKey, number.toString());
                }
            } else if (value instanceof Boolean bool) {
                result.put(fullKey, Boolean.toString(bool));
            } else if (value != null) {
                result.put(fullKey, value.toString());
            }
        });
        return result;
    }
}
