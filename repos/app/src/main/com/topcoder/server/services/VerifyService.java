package com.topcoder.server.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.util.concurrent.ConcurrentHashSet;

public class VerifyService {
    private static URL verifyPropertyUrl;
    private static final String CLASS_PROPERTY = "class";
    private static final String COUNT_PROPERTY = "count";
    private static final String NUMBER_PROPERTY = "number";
    private static final String OPERATOR_PROPERTY = "operator";
    private static final Map VERIFY_MAP = new ConcurrentHashMap();
    private static final Set VERIFIED_SET = new ConcurrentHashSet();
    private static Configuration configuration;
    private static long propertyLastModify = -1;
    private static final Logger LOG = Logger.getLogger(VerifyService.class);

    private static class Configuration {
        public ByteArrayOutputStream classFileBuffer;
        public Properties verifyClassProperty;
    }

    public static void start() {
        LOG.info("Verification service starting.");
        VERIFY_MAP.clear();
        VERIFIED_SET.clear();
        verifyPropertyUrl = VerifyService.class.getResource("/Verify.properties");
        if (verifyPropertyUrl == null) {
            throw new RuntimeException("The verification service configuration cannot be found.");
        }

        // Try to get configuration and see if there is any error.
        getConfig();
        LOG.info("Verification service started.");
    }

    public static void stop() {
        LOG.info("Verification service stopping.");
        VERIFY_MAP.clear();
        VERIFIED_SET.clear();
        verifyPropertyUrl = null;
        configuration = null;
        propertyLastModify = -1;
        LOG.info("Verification service stopped.");
    }

    private static Configuration getConfig() {
        try {
            URLConnection conn = verifyPropertyUrl.openConnection();

            conn.setIfModifiedSince(propertyLastModify);
            conn.connect();

            if (conn.getLastModified() > propertyLastModify) {
                synchronized (VerifyService.class) {
                    if (conn.getLastModified() > propertyLastModify) {
                        LOG.info("Refreshing the verification service property");
                        propertyLastModify = conn.getLastModified();
                        InputStream is = null;
                        Properties properties = new Properties();
                        
                        try {
                            is = conn.getInputStream();
                            properties.load(is);
                        } finally {
                            if (is != null) {
                                is.close();
                            }
                        }

                        LOG.info("Verification service configuration reloaded.");

                        LOG.info("Loading verification class template.");
                        ByteArrayOutputStream tmpClassFileBuffer = new ByteArrayOutputStream();
                        byte[] buffer = new byte[4096];
                        int len = 0;

                        is = VerifyService.class.getResourceAsStream(properties.getProperty(CLASS_PROPERTY).trim());

                        try {
                            while ((len = is.read(buffer, 0, buffer.length)) >= 0) {
                                tmpClassFileBuffer.write(buffer, 0, len);
                            }
                        } finally {
                            is.close();
                        }

                        LOG.info("Verification class template file loaded.");
                        Configuration config = new Configuration();
                        config.classFileBuffer = tmpClassFileBuffer;
                        config.verifyClassProperty = properties;
                        configuration = config;
                    }
                }
            }
        } catch (IOException e) {
            LOG.error("I/O exception when loading the verification service configuration: " + e);
        }

        return configuration;
    }

    public static byte[] buildVerifyClass(Integer connectionID) {
        Configuration config = getConfig();
        byte[] createdClass = config.classFileBuffer.toByteArray();
        int count = Integer.parseInt(config.verifyClassProperty.getProperty(COUNT_PROPERTY, "-1").trim());
        int result = 0;
        Random random = new Random();

        if (count >= 0) {
            LOG.info("Generating random formula, number of operations:" + count);
            for (int i=0;i<=count;++i) {
                int pos = Integer.parseInt(config.verifyClassProperty.getProperty(NUMBER_PROPERTY + "_" + (i+1)).trim());

                // We do not want 0s, since it may cause division by 0.
                short number;
                while((number = (short)random.nextInt(1 << 16)) == 0);

                // Overwrite the place in class file.
                createdClass[pos] = (byte)(number >> 8);
                createdClass[pos+1] = (byte) (number & 0xff);

                if (i == 0) {
                    // If this is the first number, no operation generated.
                    result = number;
                } else {
                    // Otherwise, generate the operation.
                    pos = Integer.parseInt(config.verifyClassProperty.getProperty(OPERATOR_PROPERTY + "_" + i).trim());
                    byte op;
                    switch (random.nextInt(5)) {
                    case 0:   //iadd
                        result += (int)number;
                        op = (byte) 0x60;
                        break;
                    case 1:   //isub
                        result -= (int)number;
                        op = (byte) 0x64;
                        break;
                    case 2:   //imul
                        result *= (int)number;
                        op = (byte) 0x68;
                        break;
                    case 3:   //idiv
                        result /= (int)number;
                        op = (byte) 0x6c;
                        break;
                    default:  //irem
                        result %= (int)number;
                        op = (byte) 0x70;
                        break;
                    }
                    createdClass[pos] = op;
                }
            }

            LOG.info("Generated formula for connection ID=" + connectionID + ", expected=" + result);
        }

        VERIFY_MAP.put(connectionID, new Integer(result));

        return createdClass;
    }

    public static void removeConnection(Integer connectionID) {
        VERIFIED_SET.remove(connectionID);
        VERIFY_MAP.remove(connectionID);
    }

    public static boolean isVerified(Integer connectionID) {
        return VERIFIED_SET.contains(connectionID);
    }

    public static boolean verifyClient(Integer connectionID, int result) {
        LOG.info("Verify connection ID=" + connectionID + ", value=" + result + ", expected=" + VERIFY_MAP.get(connectionID));

        if (new Integer(result).equals(VERIFY_MAP.get(connectionID))) {
            VERIFIED_SET.add(connectionID);
            VERIFY_MAP.remove(connectionID);

            return true;
        }

        return false;
    }
}
