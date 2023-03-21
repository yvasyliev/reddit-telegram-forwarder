package com.github.yvasyliev.properties;

import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class AppData extends Properties {
    @Value("app_data.properties")
    private String appPropertiesPath;

    public void load() throws IOException {
        try (InputStream inputStream = Files.newInputStream(Paths.get(appPropertiesPath))) {
            load(inputStream);
        }
    }

    public void store() throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(appPropertiesPath))) {
            store(outputStream, null);
        }
    }
}
