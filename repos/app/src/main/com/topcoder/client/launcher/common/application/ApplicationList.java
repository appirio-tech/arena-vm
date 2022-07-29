package com.topcoder.client.launcher.common.application;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.topcoder.client.launcher.common.Utility;

public class ApplicationList {
    public static final String APPLICATION_LIST = "applist.properties";

    private static final String INSTALL_APPLICATION_LIST = "installed.properties";

    private static final String APPLICATION_COUNT = "app_count";

    private static final String APPLICATION_ID = "id";

    private Map map = new HashMap();

    private String baseDirectory;

    public ApplicationList(URL baseUrl, String baseDirectory, String applicationListName, boolean loadInstalled) throws IOException {
        URL url;
        
        this.baseDirectory = baseDirectory;

        try {
            url = new URL(baseUrl, applicationListName);
        } catch (MalformedURLException e) {
            throw new IOException(e.getMessage());
        }

        Utility.debug("Read application list from " + url);

        InputStream is = null;
        Properties properties = new Properties();

        try {
            is = new BufferedInputStream(url.openStream());
            properties.load(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }

        is = null;

        try {
            // Parse all application information
            int count = Integer.parseInt(properties.getProperty(APPLICATION_COUNT, "0"));

            Utility.debug("Application list contains " + count + " application(s).");
            
            for (int index = 0; index < count; ++index) {
                ApplicationInfo info = new ApplicationInfo(properties, index);
                Application app = new Application(info, new File(baseDirectory, info.getId()).getPath());

                add(app);
            }
            
            Utility.debug("Set application dependencies.");

            // Set dependencies
            for (Iterator iter = map.values().iterator(); iter.hasNext();) {
                Application app = (Application) iter.next();
                String[] depends = app.getInfo().getDependencies();
                Set dependApps = new HashSet();

                for (int i = 0; i < depends.length; ++i) {
                    String id = depends[i].trim();

                    if (id.length() == 0) {
                        // Ignore empty ID
                        continue;
                    }

                    Application dependApp = (Application) map.get(id);

                    if (dependApp == null) {
                        throw new IOException(
                                              "Inconsistent application list file. Missing dependent application with ID '"
                                              + depends[i].trim() + "'.");
                    }

                    dependApp.addDepending(app);
                    dependApps.add(dependApp);
                }

                app.setDependencies(dependApps);
            }
            
            if (!loadInstalled) {
                return;
            }
            
            Utility.debug("Load installed application from " + new File(baseDirectory, INSTALL_APPLICATION_LIST));

            // Load the installed application list
            properties.clear();

            // Load the installed application list
            try {
                is = new BufferedInputStream(new FileInputStream(new File(baseDirectory, INSTALL_APPLICATION_LIST)));
                properties.load(is);
            } catch (IOException e) {
                // if IOException occurs, consider no application is installed.
            } finally {
                if (is != null) {
                    is.close();
                }
            }

            count = Integer.parseInt(properties.getProperty(APPLICATION_COUNT, "0"));
            
            Utility.debug("There is/are " + count + " installed application(s).");

            for (int index = 0; index < count; ++index) {
                String id = properties.getProperty(APPLICATION_ID + "_" + index);
                Application app = (Application) map.get(id);

                if (app != null) {
                    app.getInfo().setInstalled(true);
                } else if (id != null) {
                    // The application has been removed from the list, delete the installed one.
                    Utility.deleteRecursive(new File(baseDirectory, id));
                }
            }
        } catch (NullPointerException e) {
            throw new IOException("Some property in the application list is missing.");
        } catch (NumberFormatException e) {
            throw new IOException("A number (application count) is expected in the application list.");
        } catch (IllegalArgumentException e) {
            throw new IOException("The property file is invalid, caused by " + e.getMessage());
        }
    }

    public ApplicationList(URL baseUrl, String baseDirectory) throws IOException {
        this(baseUrl, baseDirectory, APPLICATION_LIST, true);
    }

    public Application get(String id) {
        return (Application) map.get(id);
    }
    
    public boolean contains(String id) {
        return map.containsKey(id);
    }
    
    public void add(Application app) {
        map.put(app.getInfo().getId(), app);
    }
    
    public void remove(String id) {
        map.remove(id);
    }

    public Iterator iterator() {
        return map.values().iterator();
    }
    
    public void saveList(OutputStream out) throws IOException {
        Utility.debug("Save application list");
        
        Properties properties = new Properties();
        
        properties.setProperty(APPLICATION_COUNT, Integer.toString(map.size()));
        
        Utility.debug("" + map.size() + " application(s) has/have been saved.");
        
        int index = 0;
        
        for (Iterator iter = iterator(); iter.hasNext(); ++index) {
            Application app = (Application) iter.next();
            
            app.getInfo().saveTo(properties, index);
        }
        
        try {
            properties.store(out, "Application list file. DO NOT MODIFY.");
        } finally {
            out.flush();
        }
    }

    public void saveInstalled() throws IOException {
        Utility.debug("Save installed application to " + new File(baseDirectory, INSTALL_APPLICATION_LIST));
        
        Properties properties = new Properties();
        int count = 0;

        for (Iterator iter = map.values().iterator(); iter.hasNext();) {
            Application app = (Application) iter.next();

            if (app.getInfo().isInstalled()) {
                // The application is installed, save it
                properties.setProperty(APPLICATION_ID + "_" + count, app.getInfo().getId());
                ++count;
            }
        }

        // Update the count
        properties.setProperty(APPLICATION_COUNT, Integer.toString(count));
        
        Utility.debug("There is/are " + count + " installed application(s).");

        // Save it
        OutputStream to = new BufferedOutputStream(new FileOutputStream(new File(baseDirectory,
                                                                                 INSTALL_APPLICATION_LIST)));

        try {
            properties.store(to, "Installed application list, DO NOT MODIFY");
        } finally {
            to.close();
        }
    }
}
