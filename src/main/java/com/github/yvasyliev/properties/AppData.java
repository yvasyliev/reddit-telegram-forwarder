package com.github.yvasyliev.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class AppData extends Properties {
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
        }
    }

    public void store() throws IOException {
        try (var outputStream = Files.newOutputStream(Paths.get(appPropertiesPath))) {
            store(outputStream, null);
        }
    }
}
