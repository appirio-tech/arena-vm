package com.topcoder.shared.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.MissingResourceException;

public final class SimpleResourceBundle {


    private final PropertiesResourceBundle resourceBundle;

    private SimpleResourceBundle(String baseName) {
        resourceBundle = PropertiesResourceBundle.getBundle(baseName);
    }

    public static SimpleResourceBundle getBundle(String baseName) {
        return new SimpleResourceBundle(baseName);
    }

    public static SimpleResourceBundle getVariationBundle(String baseName) {
        return getVariationBundle("variations", baseName);
    }

    private static SimpleResourceBundle getVariationBundle(String variationsBaseName, String baseName) {
        SimpleResourceBundle bundle = getBundle(variationsBaseName);
        String variationName = bundle.getString(baseName);
        return getBundle(baseName + "_" + variationName);
    }

    public String getString(String key) {
        String string = resourceBundle.getString(key);
        return string;
    }

    public void setString(String key, String value) {
        resourceBundle.setString(key, value);
    }

    public void store() {
        resourceBundle.store();
    }

    public void load() {
        resourceBundle.load();
    }

    public String getTrimmedString(String key) {
        return getString(key).trim();
    }

    public boolean getBoolean(String key) {
        String s = getTrimmedString(key);
        return Boolean.valueOf(s).booleanValue();
    }
    
    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            String s = getTrimmedString(key);
            return Boolean.valueOf(s).booleanValue();
        } catch (MissingResourceException e) {
            return defaultValue;
        }
    }

    public void setBoolean(String key, boolean value) {
        setString(key, Boolean.toString(value));
    }

    public int getInt(String key) {
        String s = getTrimmedString(key);
        return Integer.parseInt(s);
    }

    public int getInt(String key, int defaultValue) {
        try { 
            String s = getTrimmedString(key);
            return Integer.parseInt(s);
        } catch (MissingResourceException e) {
            return defaultValue;
        }
    }
    
    public void setInt(String key, int value) {
        setString(key, Integer.toString(value));
    }

    public long getLong(String key) {
        String s = getTrimmedString(key);
        return Long.parseLong(s);
    }
    
    public long getLong(String key, long defaultValue) {
        try {
            String s = getTrimmedString(key);
            return Long.parseLong(s);
        } catch (MissingResourceException e) {
            return defaultValue;
        }
    }

    public void setLong(String key, long value) {
        setString(key, Long.toString(value));
    }

    public double getDouble(String key) {
        String s = getString(key);
        return Double.parseDouble(s);
    }
    
    public double getDouble(String key, double defaultValue) {
        try {
            String s = getString(key);
            return Double.parseDouble(s);
        } catch (MissingResourceException e) {
            return defaultValue;
        } 
    }

    public void setDouble(String key, double value) {
        setString(key, Double.toString(value));
    }

    public Timestamp getTimestamp(String key) throws ParseException {
        String s = getString(key);
        return DateUtil.toTimestamp(s);
    }

    public void setTimestamp(String key, Timestamp value) {
        setString(key, DateUtil.toString(value));
    }
}
