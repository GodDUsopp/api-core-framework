package com.enterprise.api.config;

import com.enterprise.api.exceptions.ConfigurationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigManager {
    private static final Map<String, String> GLOBAL_CONFIG = new ConcurrentHashMap<>();
    private static boolean isInitialized = false;

    public static synchronized void init(String jsonFilePath) {
        if (isInitialized) return;

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> configData = mapper.readValue(
                    new File(jsonFilePath),
                    new TypeReference<Map<String, String>>() {}
            );
            GLOBAL_CONFIG.putAll(configData);
            isInitialized = true;
        } catch (Exception e) {
            throw new ConfigurationException("Failed to load environment JSON configuration from: " + jsonFilePath, e);
        }
    }

    public static String get(String key) {
        if (!GLOBAL_CONFIG.containsKey(key)) {
            throw new ConfigurationException("Configuration key not found in Global Map: " + key);
        }
        return GLOBAL_CONFIG.get(key);
    }
}