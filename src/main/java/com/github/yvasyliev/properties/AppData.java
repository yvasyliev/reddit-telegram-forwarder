package com.github.yvasyliev.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class AppData extends Properties {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppData.class);

    @Value("app_data.properties")
    private String appPropertiesPath;

    @Autowired
    private Properties properties;

    public void load() throws IOException {
        var path = Paths.get(appPropertiesPath);
        if (Files.exists(path)) {
            try (var inputStream = Files.newInputStream(path)) {
                load(inputStream);
            }
            properties.putAll(this);
            LOGGER.info("Properties are read: {} < {}", this, path);
        } else {
            LOGGER.info("Properties don't exist: {}", path);
        }
    }

    public void store() throws IOException {
        var path = Paths.get(appPropertiesPath);
        LOGGER.info("Storing properties: {} > {}", this, path);
        try (var outputStream = Files.newOutputStream(path)) {
            store(outputStream, null);
        }
    }
}
