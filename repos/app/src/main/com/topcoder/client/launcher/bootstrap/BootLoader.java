package com.topcoder.client.launcher.bootstrap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import javax.swing.JOptionPane;

/**
 * The main class of the bootstrap application for TopCoder Application Launcher.
 * 
 * @author visualage
 * @version 1.0
 */
public class BootLoader {
    private static final String MANAGEMENT_BASE_URL_PROPERTY = "com.topcoder.client.launcher.managementbaseurl";

    private static final String MANAGEMENT_CLASS_PROPERTY = "com.topcoder.client.launcher.managementclass";

    private static final String STORE_DIRECTORY_PROPERTY = "com.topcoder.client.launcher.storedirectory";

    private static final String STORE_DIRECTORY_PROPERTY_WINDOWS = "com.topcoder.client.launcher.storedirectory.windows";

    private static final String STORE_DIRECTORY_PROPERTY_OTHER = "com.topcoder.client.launcher.storedirectory.other";
    
    private static final String DEFAULT_APPLICATION_ID_PROPERTY = "com.topcoder.client.launcher.defaultappid";

    private static final Properties DEFAULT_PROPERTIES = new Properties();

    private static final String MANAGEMENT_JAR_FILE = "Management.jar";

    private static final int BUFFER_SIZE = 4 * 1024;

    private static final Runtime RUNTIME = Runtime.getRuntime();
    
    private static String defaultAppId = null;

    static {
        DEFAULT_PROPERTIES.setProperty(MANAGEMENT_BASE_URL_PROPERTY, "http://www.topcoder.com/contestant/");
        DEFAULT_PROPERTIES.setProperty(MANAGEMENT_CLASS_PROPERTY, "com.topcoder.client.launcher.management.Entrance");
        DEFAULT_PROPERTIES.setProperty(STORE_DIRECTORY_PROPERTY_WINDOWS, "Application Data/TC App");
        DEFAULT_PROPERTIES.setProperty(STORE_DIRECTORY_PROPERTY_OTHER, ".tcapp");
    }

    private static void gc() {
        long nowFree = RUNTIME.freeMemory();
        long free;

        do {
            Thread.yield();
            free = nowFree;
            RUNTIME.gc();
            nowFree = RUNTIME.freeMemory();
        } while (nowFree > free);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            // Read properties from resource first.
            InputStream input = null;
            Properties properties = new Properties(DEFAULT_PROPERTIES);

            try {
                input = BootLoader.class.getClassLoader().getResourceAsStream(
                    "com/topcoder/client/launcher/bootstrap/bootstrap.properties");

                properties.load(input);
            } catch (IOException e) {
                // ignore, use the default properties
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        // ignore
                    }
                    input = null;
                }
            }

            if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
                properties.setProperty(STORE_DIRECTORY_PROPERTY, properties
                    .getProperty(STORE_DIRECTORY_PROPERTY_WINDOWS));
            } else {
                properties
                    .setProperty(STORE_DIRECTORY_PROPERTY, properties.getProperty(STORE_DIRECTORY_PROPERTY_OTHER));
            }
            
            defaultAppId = properties.getProperty(DEFAULT_APPLICATION_ID_PROPERTY);

            // Parse the command line
            if (args.length > 0) {
                defaultAppId = args[0];
            }
            
            if (args.length > 1) {
                properties.setProperty(MANAGEMENT_BASE_URL_PROPERTY, args[1]);
            }

            if (args.length > 2) {
                properties.setProperty(MANAGEMENT_CLASS_PROPERTY, args[2]);
            }

            if (args.length > 3) {
                properties.setProperty(STORE_DIRECTORY_PROPERTY, args[3]);
            }

            // Check if the args are valid
            URL url = null;

            url = new URL(new URL(properties.getProperty(MANAGEMENT_BASE_URL_PROPERTY)), MANAGEMENT_JAR_FILE);

            File storeDir = new File(properties.getProperty(STORE_DIRECTORY_PROPERTY));

            if (!storeDir.isAbsolute()) {
                storeDir = new File(System.getProperty("user.home"), properties.getProperty(STORE_DIRECTORY_PROPERTY));
            }

            // Create directories
            if (!storeDir.exists()) {
                storeDir.mkdirs();
            }

            if (!storeDir.isDirectory()) {
                throw new IOException("The store directory must be an existing directory.");
            }

            // Update the application
            try {
                updateManagementApplication(storeDir.getAbsolutePath(), properties
                    .getProperty(MANAGEMENT_CLASS_PROPERTY), properties.getProperty(MANAGEMENT_BASE_URL_PROPERTY));
            } catch (InvocationTargetException e) {
                throw e.getCause();
            } catch (Exception e) {
                // If any error happens while launching the management app
                // We consider the JAR file is broken, and thus redownload it.
                try {
                    downloadManagementJar(url, storeDir.getAbsolutePath());
                } catch (IOException ee) {
                    throw new IOException("Download management application fails, caused by " + ee.getMessage());
                }
            }

            launchManagementApplication(storeDir.getAbsolutePath(), properties.getProperty(MANAGEMENT_CLASS_PROPERTY),
                properties.getProperty(MANAGEMENT_BASE_URL_PROPERTY));
        } catch (Throwable e) {
            // Create an UI to popup the error of bootstrap application.
            popupError(e);
        }
    }

    private static void popupError(Throwable e) {
        JOptionPane.showMessageDialog(null, e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        System.exit(-1);
    }

    private static void downloadManagementJar(URL url, String storeDirectory) throws IOException {
        InputStream is = null;
        OutputStream dest = null;

        try {
            is = url.openStream();
            dest = new FileOutputStream(new File(storeDirectory, MANAGEMENT_JAR_FILE));

            byte[] buffer = new byte[BUFFER_SIZE];
            int read;

            while ((read = is.read(buffer)) >= 0) {
                dest.write(buffer, 0, read);
            }

            dest.flush();
        } finally {
            if (dest != null) {
                dest.close();
            }

            if (is != null) {
                is.close();
            }
        }
    }

    private static void updateManagementApplication(String storeDirectory, String managementClassName, String baseUrl)
        throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException,
        IOException {
        ClassLoader loader = null;
        Class managementClass = null;
        Method updateMethod = null;
        File renameFrom = null;
        File jarFile = new File(storeDirectory, MANAGEMENT_JAR_FILE);

        try {
            loader = URLClassLoader.newInstance(new URL[] {jarFile.toURI().toURL()});
            managementClass = loader.loadClass(managementClassName);
            updateMethod = managementClass.getMethod("update", new Class[] {String.class, String.class, URL.class});
            renameFrom = (File) updateMethod.invoke(null, new Object[] {storeDirectory, MANAGEMENT_JAR_FILE,
                new URL(baseUrl)});

            if (renameFrom == null) {
                return;
            }

            updateMethod = null;
            managementClass = null;
            loader = null;

            // Free the memory until the URLClassLoader is unloaded from memory
            gc();

            // Delete the old one
            for (int i = 0; i < 5; ++i) {
                if (jarFile.delete()) {
                    break;
                }
                gc();
            }

            // Rename
            if (!renameFrom.renameTo(jarFile)) {
                throw new IOException("Cannot rename the updated management application jar file.");
            }
        } catch (MalformedURLException e) {
            throw new IOException("The management application cannot be updated, caused by " + e.getMessage());
        }
    }

    private static void launchManagementApplication(String storeDirectory, String managementClassName, String baseUrl)
        throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException,
        IOException {
        ClassLoader loader = null;
        Class managementClass = null;
        Method launchMethod = null;
        File jarFile = new File(storeDirectory, MANAGEMENT_JAR_FILE);

        try {
            loader = URLClassLoader.newInstance(new URL[] {jarFile.toURI().toURL()});
            managementClass = loader.loadClass(managementClassName);
            launchMethod = managementClass.getMethod("execute", new Class[] {String.class, URL.class, String.class});
            launchMethod.invoke(null, new Object[] {storeDirectory, new URL(baseUrl), defaultAppId});

            launchMethod = null;
            managementClass = null;
            loader = null;

            // Free the memory until the URLClassLoader is unloaded from memory
            gc();
        } catch (MalformedURLException e) {
            throw new IOException("The management application cannot be loaded, caused by " + e.getMessage());
        }
    }
}
