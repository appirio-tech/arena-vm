package com.topcoder.client.launcher.common.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.ZipFile;

import com.topcoder.client.launcher.common.Utility;
import com.topcoder.client.launcher.common.archive.ApplicationArchive;
import com.topcoder.client.launcher.common.archive.UrlApplicationArchive;
import com.topcoder.client.launcher.common.archive.ZipApplicationArchive;
import com.topcoder.client.launcher.common.file.ApplicationFile;
import com.topcoder.client.launcher.common.task.AfterInstallTask;
import com.topcoder.client.launcher.common.task.AfterUpdateTask;
import com.topcoder.client.launcher.common.task.ApplicationTaskException;
import com.topcoder.client.launcher.common.task.ApplicationTaskProgressListener;
import com.topcoder.client.launcher.common.task.BeforeInstallTask;
import com.topcoder.client.launcher.common.task.BeforeUninstallTask;
import com.topcoder.client.launcher.common.task.ExecuteTask;

import edu.emory.mathcs.backport.java.util.Arrays;

public class Application implements ApplicationTaskProgressListener {
    public static final String APPLICATION_FILE_LIST = "filelist.properties";

    private ApplicationInfo info;

    private String baseDirectory;

    private URL[] classpath;

    private Application[] dependencies;

    private Set children = new HashSet();

    private Set listeners = new HashSet();

    public Application(ApplicationInfo info, String baseDirectory) {
        Utility.validateNotNull(info, "info");
        Utility.validateNotNull(baseDirectory, "baseDirectory");

        this.info = info;
        this.baseDirectory = baseDirectory;
    }

    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public ApplicationInfo getInfo() {
        return info;
    }

    public void addTaskProgressListener(ApplicationTaskProgressListener listener) {
        listeners.add(listener);
    }

    public void removeTaskProgressListener(ApplicationTaskProgressListener listener) {
        listeners.remove(listener);
    }

    public File getJarFile() {
        return new File(baseDirectory, info.getJarName());
    }

    public void addDepending(Application app) {
        children.add(app);
    }

    public void removeDepending(Application app) {
        children.remove(app);
    }

    public void setDependencies(Set dependencies) {
        classpath = new URL[dependencies.size() + 1];
        this.dependencies = new Application[dependencies.size()];

        try {
            classpath[0] = getJarFile().toURI().toURL();

            Iterator iter = dependencies.iterator();
            for (int i = 0; i < dependencies.size(); ++i) {
                Application app = (Application) iter.next();
                classpath[i + 1] = app.getJarFile().toURI().toURL();
                this.dependencies[i] = app;
            }
        } catch (MalformedURLException e) {
            // should never happen
        }
    }

    public File updateNoTask() throws IOException {
        if (!info.isInstalled()) {
            // When the application is not installed, no need to update
            return null;
        }

        Utility.debug("Update application " + info.getName() + ", DIR=" + baseDirectory + ", URL=" + info.getBaseUrl());

        newTask("Checking local application file", 1);
        progress(0, info.getJarName());
        // Check the hash of the local storage.
        ZipFile localJar = new ZipFile(new File(baseDirectory, info.getJarName()));

        try {
            try {
                if (MessageDigest.isEqual(Utility.computeZipHash(localJar), info.getHash())) {
                    // If the hashes are the same, no need to update.
                    Utility.debug("Remote jar file hash and local jar file hash are the same, skip update.");
                    return null;
                }
            } finally {
                localJar.close();
            }
        } finally {
            progress(1, "Complete");
            finish();
        }

        Utility.debug("New jar file on remote server has been found.");

        // Otherwise, need to update the zip file
        File localZip = getJarFile();
        ApplicationArchive local = new ZipApplicationArchive(localZip);
        ApplicationArchive remote = new UrlApplicationArchive(info.getBaseUrl(), APPLICATION_FILE_LIST);
        ApplicationArchive updated = new ZipApplicationArchive();

        newTask("Checking files to be updated", remote.size());

        try {
            int index = 0;

            // Enumerate all files in remote.
            for (Iterator iter = remote.iterator(); iter.hasNext(); ++index) {
                ApplicationFile file = (ApplicationFile) iter.next();
                ApplicationFile localFile = local.get(file.getFilename());

                progress(index, file.getFilename());

                if ((localFile != null) && (localFile.contentSame(file))) {
                    // If there is a local file which is the same as the remote file
                    // Use the local file
                    Utility.debug(file.getFilename() + " is scheduled to use local copy because local file hash matches remote file hash.");
                    updated.add(localFile);
                } else {
                    // Otherwise, use the remote file
                    Utility.debug(file.getFilename() + " is scheduled to be downloaded from remote server.");
                    updated.add(file);
                }
            }

            progress(remote.size(), "Complete");
        } finally {
            finish();
        }

        Utility.debug("Repack the local jar file.");

        File tmpZip = File.createTempFile(".tc", ".jar", new File(baseDirectory));
        updated.addTaskProgressListener(this);

        try {
            updated.writeTo(tmpZip);
        } catch (IOException e) {
            // When the update fails, delete the tmp file on exit
            tmpZip.delete();
            tmpZip.deleteOnExit();
            throw e;
        } finally {
            updated.removeTaskProgressListener(this);
            local.dispose();
            remote.dispose();
            updated.dispose();
        }

        return tmpZip;
    }

    public void update() throws IOException, ApplicationTaskException {
        File tmpZip = updateNoTask();

        if (tmpZip == null) {
            // No update
            return;
        }

        Utility.debug("Rename the updated local jar file from " + tmpZip + " to " + getJarFile());

        if (!Utility.deleteRecursive(getJarFile())) {
            tmpZip.delete();
            tmpZip.deleteOnExit();
            throw new IOException("The old application jar file cannot be deleted.");
        }

        if (!tmpZip.renameTo(getJarFile())) {
            tmpZip.delete();
            tmpZip.deleteOnExit();
            throw new IOException("The updated application jar file cannot be renamed.");
        }

        Utility.debug("Execute after update task, class=" + info.getClassName());

        newTask("Executing update task of the application", 1);
        progress(0, "");

        AfterUpdateTask task;

        try {
            task = (AfterUpdateTask) loadTask(classpath, info.getClassName(), AfterUpdateTask.class);

            if (task != null) {
                task.afterUpdate(baseDirectory, info.getBaseUrl());
            }

            progress(1, "Complete");
        } finally {
            task = null;
            Utility.gc();
            finish();
        }
    }

    private Object loadTask(URL[] classpath, String className, Class taskInterface) throws ApplicationTaskException {
        URLClassLoader loader = null;
        Class appClass = null;

        try {
            loader = URLClassLoader.newInstance(classpath, getClass().getClassLoader());
            appClass = Class.forName(className, true, loader);

            Object obj = appClass.newInstance();

            if (taskInterface.isInstance(obj)) {
                return obj;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new ApplicationTaskException("Create instance of " + className + " in application fails.", e);
        } finally {
            appClass = null;
            loader = null;
            Utility.gc();
        }
    }

    public void uninstall() throws IOException, ApplicationTaskException {
        if (!info.isInstalled()) {
            // When the application is not installed, no need to uninstall
            return;
        }

        Utility.debug("Uninstall application " + info.getName() + ", DIR=" + baseDirectory);

        newTask("Uninstalling", 3);

        progress(0, "Checking dependency");

        Utility.debug("Check dependency before uninstall");

        // Check if no application are depending on this one
        StringBuffer sb = new StringBuffer();

        for (Iterator iter = children.iterator(); iter.hasNext();) {
            Application app = (Application) iter.next();
            if (app.getInfo().isInstalled()) {
                sb.append('\n');
                sb.append(app.getInfo().getName());
            }
        }

        if (sb.length() != 0) {
            Utility.debug("Dependent installed application found.");
            throw new ApplicationTaskException("Following application(s) must be uninstalled first: " + sb.toString());
        }

        Utility.debug("Execute before uninstall task, class=" + info.getClassName());

        try {
            BeforeUninstallTask task;
            progress(1, "Executing uninstall task of the application");
            try {
                task = (BeforeUninstallTask) loadTask(classpath, info.getClassName(), BeforeUninstallTask.class);

                if (task != null) {
                    task.beforeUninstall(baseDirectory, info.getBaseUrl());
                }

            } catch (Exception e) {
                // ignore all uninstall task errors
            } finally {
                task = null;
                Utility.gc();

                info.setInstalled(false);

                progress(2, "Deleting the application directory");

                Utility.debug("Delete application directory " + baseDirectory);

                if (!Utility.deleteRecursive(new File(baseDirectory))) {
                    throw new IOException("The application directory cannot be deleted.");
                }

                progress(3, "Uninstall complete");
            }
        } finally {
            finish();
        }
    }

    public void execute() throws ApplicationTaskException {
        if (!info.isInstalled() || !info.isExecutable()) {
            // When the application is not installed or not executable, no need to execute
            return;
        }

        ExecuteTask task;

        Utility.debug("Execute application " + info.getName() + ", class=" + info.getClassName());
        Utility.debug("Classpath: " + Arrays.toString(classpath));

        try {
            task = (ExecuteTask) loadTask(classpath, info.getClassName(), ExecuteTask.class);

            if (task == null) {
                throw new ApplicationTaskException("An application must implement ExecuteTask interface.");
            }

            task.execute(baseDirectory, info.getBaseUrl());
        } finally {
            task = null;
            Utility.gc();
        }
    }

    public void reinstall() throws IOException {
        // Download the jar file
        Utility.debug("Redownload application " + info.getName() + ", File="
                      + new File(baseDirectory, info.getJarName()) + ", URL=" + new URL(info.getBaseUrl(), info.getJarName()));
        OutputStream os = null;

        try {
            os = new FileOutputStream(new File(baseDirectory, info.getJarName()));
            Utility.downloadFileProgress(new URL(info.getBaseUrl(), info.getJarName()), os, this);
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

    public void install() throws IOException, ApplicationTaskException {
        if (info.isInstalled()) {
            // When the application is already installed, no need to install but update
            update();

            return;
        }

        Utility
            .debug("Install application " + info.getName() + ", DIR=" + baseDirectory + ", URL=" + info.getBaseUrl());

        newTask("Installing", 6);

        try {
            Utility.debug("Create local directory");
            progress(0, "Creating local application directory");
            File base = new File(baseDirectory);

            base.mkdirs();

            if (!base.isDirectory()) {
                throw new IOException("The base directory of the application cannot be created.");
            }

            Utility.debug("Check dependency");
            progress(1, "Checking dependency");

            try {
                // Perform the dependency checking
                StringBuffer sb = new StringBuffer();

                for (int i = 0; i < dependencies.length; ++i) {
                    if (!dependencies[i].getInfo().isInstalled()) {
                        sb.append('\n');
                        sb.append(dependencies[i].getInfo().getName());
                    }
                }

                if (sb.length() != 0) {
                    Utility.debug("Missing dependent application(s)");
                    throw new ApplicationTaskException("Following dependent application(s) must be installed first: "
                                                       + sb.toString());
                }

                progress(2, "Downloading pre-install task of the application");

                // Check pre-install jar
                if (info.getPreInstallJarName().length() != 0) {
                    Utility.debug("Execute pre-install task, URL="
                                  + new URL(info.getBaseUrl(), info.getPreInstallJarName()) + ", class="
                                  + info.getPreInstallClassName());
                    File preinstalljar = new File(baseDirectory, info.getPreInstallJarName());

                    if (!preinstalljar.createNewFile()) {
                        throw new IOException("The pre-install jar file cannot be created.");
                    }

                    OutputStream os = null;

                    try {
                        os = new FileOutputStream(preinstalljar);
                        Utility.downloadFile(new URL(info.getBaseUrl(), info.getPreInstallJarName()), os);
                    } finally {
                        if (os != null) {
                            os.close();
                        }
                    }

                    // Set the classpathes
                    URL[] classes = new URL[classpath.length];
                    System.arraycopy(classpath, 0, classes, 0, classpath.length);
                    classes[0] = preinstalljar.toURI().toURL();

                    progress(3, "Executing pre-install task of the application");

                    // Perform the check task. If any error, should throw Exception
                    BeforeInstallTask task;

                    try {
                        task = (BeforeInstallTask) loadTask(classes, info.getPreInstallClassName(),
                                                            BeforeInstallTask.class);

                        if (task != null) {
                            task.beforeInstall(baseDirectory, info.getBaseUrl());
                        }
                    } finally {
                        task = null;
                        Utility.gc();
                    }

                    // No need to keep this preinstall jar.
                    Utility.deleteRecursive(preinstalljar);
                }

                Utility.debug("Download application jar file, File=" + new File(baseDirectory, info.getJarName())
                              + ", URL=" + new URL(info.getBaseUrl(), info.getJarName()));
                progress(4, "Downloading the application");

                // All checking passed. Download the jar file
                OutputStream os = null;

                try {
                    os = new FileOutputStream(new File(baseDirectory, info.getJarName()));
                    Utility.downloadFileProgress(new URL(info.getBaseUrl(), info.getJarName()), os, this);
                } finally {
                    if (os != null) {
                        os.close();
                    }
                }

                Utility.debug("Execute after install task, class=" + info.getClassName());
                newTask("Installing", 6);
                progress(5, "Executing post-install task of the application");

                // Execute the post-install task
                AfterInstallTask task;

                try {
                    task = (AfterInstallTask) loadTask(classpath, info.getClassName(), AfterInstallTask.class);

                    if (task != null) {
                        task.afterInstall(baseDirectory, info.getBaseUrl());
                    }
                } finally {
                    task = null;
                    Utility.gc();
                }

                // Mark the application installed
                info.setInstalled(true);

                progress(6, "Install complete");
            } catch (ApplicationTaskException e) {
                Utility.deleteRecursive(base);
                throw e;
            } catch (IOException e) {
                Utility.deleteRecursive(base);
                throw e;
            }
        } finally {
            finish();
        }
    }

    public void newTask(String name, int max) {
        for (Iterator iter = listeners.iterator(); iter.hasNext();) {
            ApplicationTaskProgressListener listener = (ApplicationTaskProgressListener) iter.next();

            listener.newTask(name, max);
        }
    }

    public void finish() {
        for (Iterator iter = listeners.iterator(); iter.hasNext();) {
            ApplicationTaskProgressListener listener = (ApplicationTaskProgressListener) iter.next();

            listener.finish();
        }
    }

    public void progress(int progress, String comment) {
        for (Iterator iter = listeners.iterator(); iter.hasNext();) {
            ApplicationTaskProgressListener listener = (ApplicationTaskProgressListener) iter.next();

            listener.progress(progress, comment);
        }
    }

    public String toString() {
        return info.getName();
    }
}
