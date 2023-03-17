package com.github.yvasyliev.properties;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class AppProperties extends Properties {
    @Autowired
    private String appPropertiesPath;

    @PostConstruct
    public void load() throws IOException {
        try (InputStream inputStream = Files.newInputStream(Paths.get(appPropertiesPath))) {
            load(inputStream);
        }
    }

    @PreDestroy
    public void store() throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(appPropertiesPath))) {
            store(outputStream, null);
        }
    }
}
