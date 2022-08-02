package app.funclub.anadearmas.services;

import app.funclub.anadearmas.exceptions.PropertiesWriterException;

import java.util.Properties;

@FunctionalInterface
public interface PropertiesWriter {
    void write(String propertiesPath, Properties properties) throws PropertiesWriterException;
}
