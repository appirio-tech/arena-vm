package com.topcoder.shared.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.Properties;

final class PropertiesResourceBundle {


    private final Properties properties;
    private final String baseName;
    private final URL url;

    private PropertiesResourceBundle(String baseName) {
        this.baseName = baseName;
        String resourceName = baseName + ".properties";
        url = LoaderUtil.getResource(resourceName);
        if (url == null) {
            throwMissingResourceException("Can't find bundle");
        }
        properties = new Properties();
        load();
    }

    static PropertiesResourceBundle getBundle(String baseName) {
        return new PropertiesResourceBundle(baseName);
    }

    private void throwMissingResourceException(Object message) {
        throwMissingResourceException(message, "");
    }

    private void throwMissingResourceException(Object message, String key) {
        String s = message + ", base name: " + baseName;
        if (key.length() > 0) {
            s += ", key: " + key;
        }
        throw new MissingResourceException(s, baseName, key);
    }

    String getString(String key) {
        String property = properties.getProperty(key);
        if (property == null) {
            throwMissingResourceException("Can't find resource", key);
        }
        return property;
    }

    void setString(String key, String value) {
        properties.setProperty(key, value);
    }

    String getBaseName() {
        return baseName;
    }

    void load() {
        try {
            InputStream inputStream = url.openStream();
            try {
                properties.load(inputStream);
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throwMissingResourceException(e);
        }
    }

    void store() {
        String protocol = url.getProtocol();
        if (!protocol.equals("file")) {
            throw new UnsupportedOperationException("protocol = " + protocol);
        }
        String fileName = url.getFile();
        try {
            OutputStream outputStream = new FileOutputStream(fileName);
            try {
                properties.store(outputStream, null);
            } finally {
                outputStream.close();
            }
        } catch (IOException e) {
            throwMissingResourceException(e);
        }
    }

}
