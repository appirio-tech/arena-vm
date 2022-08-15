package com.topcoder.client.launcher.tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.zip.ZipFile;

import com.topcoder.client.launcher.common.Utility;
import com.topcoder.client.launcher.common.application.Application;
import com.topcoder.client.launcher.common.application.ApplicationInfo;
import com.topcoder.client.launcher.common.application.ApplicationList;
import com.topcoder.client.launcher.common.archive.ApplicationArchive;
import com.topcoder.client.launcher.common.archive.UrlApplicationArchive;
import com.topcoder.client.launcher.common.archive.ZipApplicationArchive;
import com.topcoder.client.launcher.common.file.ApplicationFile;
import com.topcoder.client.launcher.common.task.ExecuteTask;

public class Manager {
    private static void error() {
        System.err.println("Usage:");
        System.err.println("java -cp classpath " + Manager.class.getName()
                           + " <app_list_file> add <app_id> <app_name> <app_version> <base_url>"
                           + " <app_jar_file> <app_class> <dependent_app_ids> <pre_install_jar> <pre_install_class>");
        System.err.println("or");
        System.err.println("java -cp classpath " + Manager.class.getName() + " <app_list_file> del <app_id>");
        System.exit(-1);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            error();
        } else if (!args[1].equals("del") && args.length < 11) {
            error();
        }

        File appListFile = new File(args[0]);

        if (appListFile.isDirectory()) {
            appListFile = new File(appListFile, ApplicationList.APPLICATION_LIST);
        }

        if (!appListFile.exists()) {
            // When the file list does not exist, create an empty list
            appListFile.createNewFile();
        }

        ApplicationList appList = new ApplicationList(appListFile.getParentFile().toURI().toURL(), ".", appListFile
                                                      .getName(), false);

        String id = args[2];

        if (args[1].equals("delete")) {
            if (!appList.contains(id)) {
                System.err.println("The application with ID '" + id + "' does not exist.");
                System.exit(-1);
            }

            appList.remove(id);

            OutputStream os = new BufferedOutputStream(new FileOutputStream(appListFile));

            try {
                appList.saveList(os);
            } finally {
                os.close();
            }
        } else if (args[1].equals("add")) {
            String name = args[3];
            String version = args[4];

            // Base URL must be a directory
            if (!args[5].endsWith("/")) {
                args[5] += "/";
            }

            URL baseUrl = new URL(args[5]);
            File jarFile = new File(args[6]);
            String jarName = jarFile.getName();
            String className = args[7];
            String dependencies = args[8];
            File preInstallJarFile = new File(args[9]);
            String preInstallJar = preInstallJarFile.getName();
            String preInstallClass = args[10];
            byte[] hash;
            ZipFile zip = new ZipFile(jarFile);

            try {
                hash = Utility.computeZipHash(zip);
            } finally {
                zip.close();
            }

            ClassLoader loader;
            Class appClass;
            boolean executable = false;
            
            try {
                loader = URLClassLoader.newInstance(new URL[] {jarFile.toURI().toURL()});
                appClass = loader.loadClass(className);
                executable = ExecuteTask.class.isAssignableFrom(appClass);
            } catch (ClassNotFoundException e) {
                System.err.println("The class name cannot be found in the application JAR file.");
                System.exit(-1);
            } finally {
                appClass = null;
                loader = null;
                Utility.gc();
            }

            ApplicationInfo info = new ApplicationInfo(id, name, version, jarName, hash, className, baseUrl, dependencies,
                                                       executable, preInstallJar, preInstallClass);
            Application app = new Application(info, ".");

            // Add the application to the list
            appList.add(app);

            // Extract all application file and build the application file list
            ApplicationArchive jarArchive = new ZipApplicationArchive(jarFile);
            ApplicationArchive urlArchive = new UrlApplicationArchive(Application.APPLICATION_FILE_LIST);

            for (Iterator iter = jarArchive.iterator(); iter.hasNext();) {
                urlArchive.add((ApplicationFile) iter.next());
            }

            // Write out all the files
            urlArchive.writeTo(jarFile.getParentFile());

            // Write out the new application list
            OutputStream os = new BufferedOutputStream(new FileOutputStream(appListFile));

            try {
                appList.saveList(os);
            } finally {
                os.close();
            }
        } else {
            error();
        }
    }
}
