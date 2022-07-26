package com.topcoder.client.contestMonitor.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

final class PropertiesFile {

    private final String fileName;
    private final Properties properties;

    PropertiesFile(String fileName) {
        this.fileName = fileName;
        properties = loadProperties(fileName);
    }

    String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    int getIntProperty(String key, int defaultValue) {
        return Integer.parseInt(getProperty(key, "" + defaultValue));
    }

    boolean getBooleanProperty(String key, boolean defaultValue) {
        return getProperty(key, "" + defaultValue).toLowerCase().startsWith("t");
    }

    private void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    void setIntProperty(String key, int value) {
        setProperty(key, "" + value);
    }

    void store(String header) throws IOException {
        OutputStream outputStream = new FileOutputStream(fileName);
        try {
            properties.store(outputStream, header);
        } finally {
            outputStream.close();
        }
    }

    private Properties loadProperties(String fileName) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = getClass().getResourceAsStream(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return properties;
        }
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

}
