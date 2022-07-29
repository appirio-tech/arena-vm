package com.topcoder.client.ui;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;

import com.topcoder.client.ui.impl.XMLUIManager;

/**
 * Defines the UI manager factory. It will create UI managers defined in a directory. In the directory,
 * ui.xml defines the information of the manager. It will create a XMLUIManager for each ui.xml.
 *
 * @version 1.0
 * @author visualage
 * @see com.topcoder.client.ui.XMLUIManager
 */
public class UIFactory {
    /** Represents the manager factory configuration file name.*/
    private static final String CONFIG_FILE = "ui.xml";

    /** Reperesents the number of bytes to be read at once.*/
    private static final int SKIP_BYTES = 4096;

    /**
     * Creates a new instance of <code>UIFactory</code>. The private constructor prevents from creating new instance.
     */
    private UIFactory() {
    }

    /**
     * Creates a new UI manager from the directory. A new instance of <code>XMLUIManager</code> is created according
     * to the ui.xml file represented by the URL.
     * @param url the URL used to create the UI manager.
     * @throws UIManagerException the directory is not valid for a UI manager.
     * @return a new UI manager instance created from the directory.
     */
    public static UIManager getUIManager(URL url) throws UIManagerException {
        // Get the XML UI manager
        return new XMLUIManager(url);
    }

    /**
     * Creates a new UI manager from resource. The resource must be a valid XML configuration file. The class
     * used to load resource and the resource name are given.
     * @param clazz the class used to load the resource.
     * @param name the resource name.
     * @throws UIManagerException the resource is not a valid UI manager configuration.
     * @return a new UI manager created from the resource.
     */
    public static UIManager getUIManagerFromResource(Class clazz, String name) throws UIManagerException {
        URL url = clazz.getResource(name);

        if (url == null) {
            throw new UIManagerException("The resource '" + name + "' cannot be found.");
        }

        return getUIManager(url);
    }

    /**
     * Creates a list of new UI managers from the directory. Each jar file under the given directory is used
     * as a JAR file defining a UI manager. <code>getUIManager</code> is called for each of these jar files.
     * When there is no UI manager defined under the directory, an empty array is returned.
     * @param baseDir the directory used to create a list of new UI managers.
     * @throws UIManagerException a sub-directory under the given directory is not a valid UI manager directory.
     * @return an array of UI managers created from the given directory.
     */
    public static UIManager[] getAllUIManagers(File baseDir) throws UIManagerException {
        if (!baseDir.isDirectory()) {
            return new UIManager[0];
        }

        File[] files = baseDir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".jar");
                }
            });
        List list = new ArrayList();
        PublicKey publicKey = null;
        byte[] buffer = new byte[SKIP_BYTES];

        if (UIFactory.class.getSigners() != null) {
            publicKey = ((Certificate) UIFactory.class.getSigners()[0]).getPublicKey();
        }

        for (int i = 0; i < files.length; ++i) {
            if (files[i].isFile()) {
                try {
                    // Create a new URL
                    URL url = new URL("jar:" + files[i].toURI().toURL().toString() + "!/" + CONFIG_FILE);
                    InputStream is = null;

                    // Check if it is signed properly
                    try {
                        JarURLConnection conn = (JarURLConnection) url.openConnection();

                        is = conn.getInputStream();
                        while(is.read(buffer) != -1);
                        if (publicKey != null) {
                            if (conn.getCertificates() == null) {
                                throw new UIManagerException("The file is not signed.");
                            } else {
                                conn.getCertificates()[0].verify(publicKey);
                            }
                        }
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                    
                    list.add(getUIManager(url));
                } catch (GeneralSecurityException e) {
                    throw new UIManagerException("The file is not signed by a proper signature.", e);
                } catch (IOException e) {
                    throw new UIManagerException("The file is not a UI scheme file.", e);
                }
            }
        }

        return (UIManager[]) list.toArray(new UIManager[0]);
    }
}
