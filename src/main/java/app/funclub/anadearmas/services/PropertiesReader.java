package app.funclub.anadearmas.services;

import app.funclub.anadearmas.exceptions.PropertiesReaderException;

import java.util.Properties;

@FunctionalInterface
public interface PropertiesReader {
    Properties read(String propertiesPath) throws PropertiesReaderException;
}
