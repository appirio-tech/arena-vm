package com.topcoder.client.launcher.management;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.SwingUtilities;

import com.topcoder.client.launcher.common.Utility;
import com.topcoder.client.launcher.common.application.Application;
import com.topcoder.client.launcher.common.application.ApplicationList;
import com.topcoder.client.launcher.management.ui.MainFrame;

public class Entrance {
    private static final String MANAGEMENT_JAR_INFO = "managementapp.properties";

    public static File update(String baseDirectory, String jarName, URL baseUrl) throws IOException {
        Utility.debug("Update management application. DIR=" + baseDirectory + ", URL=" + baseUrl);

        ApplicationList list = new ApplicationList(baseUrl, baseDirectory, MANAGEMENT_JAR_INFO, false);
        Application app = list.get("ManagementApp");

        if (app == null) {
            throw new IOException("The management property file is invalid.");
        }
        
        app.setBaseDirectory(baseDirectory);
        app.getInfo().setInstalled(true);

        // Update the application, no post update task, since the JAR is not renamed yet.
        return app.updateNoTask();
    }

    public static void execute(String baseDirectory, URL baseUrl, String defaultAppId) {
        Utility.debug("Execute management application. DIR=" + baseDirectory + ", URL=" + baseUrl + ", Default App="
                      + defaultAppId);

        // Launch the application UI
        MainFrame frame = new MainFrame(baseDirectory, baseUrl, defaultAppId);
        Object syncRoot = frame.getSyncRoot();

        SwingUtilities.invokeLater(frame);

        // Wait until the execution of the management application ends.
        synchronized (syncRoot) {
            try {
                syncRoot.wait();
            } catch (InterruptedException e) {
                // ignore
            }
        }

        Utility.debug("Exiting management application.");
    }

    public static void main(String[] args) throws Exception {
        // Launch the application UI
        try {
            execute(args[0], new URL(args[1]), null);
        } catch (Exception e) {
            System.out.println("Usage: java -cp classpath " + Entrance.class.getName()
                               + " <local work directory> <remote base URL>");
        }
    }
}
