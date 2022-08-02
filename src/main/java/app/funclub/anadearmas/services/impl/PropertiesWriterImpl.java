package app.funclub.anadearmas.services.impl;

import app.funclub.anadearmas.exceptions.PropertiesWriterException;
import app.funclub.anadearmas.services.PropertiesWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertiesWriterImpl implements PropertiesWriter {
    @Override
    public void write(String propertiesPath, Properties properties) throws PropertiesWriterException {
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(propertiesPath))) {
            properties.store(outputStream, null);
        } catch (IOException e) {
            throw new PropertiesWriterException(e);
        }
    }
}
