package com.college.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new IllegalStateException(
                        "config.properties was not found in test resources."
                );
            }

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load config.properties.", e
            );
        }
    }

    public static String get(String key) {
        String systemValue = System.getProperty(key);

        if (systemValue != null) {
            return systemValue;
        }

        String fileValue = properties.getProperty(key);

        if (fileValue == null) {
            throw new IllegalArgumentException(
                    "Configuration key not found: " + key
            );
        }

        return fileValue;
    }
}