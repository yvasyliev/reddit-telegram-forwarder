package app.funclub.anadearmas.services.impl;

import app.funclub.anadearmas.exceptions.PropertiesReaderException;
import app.funclub.anadearmas.services.PropertiesReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertiesReaderImpl implements PropertiesReader {
    @Override
    public Properties read(String propertiesPath) throws PropertiesReaderException {
        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(Paths.get(propertiesPath))) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new PropertiesReaderException(e);
        }
        return properties;
    }
}
