package com.topcoder.client.launcher.common.application;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import com.topcoder.client.launcher.common.Utility;

public class ApplicationInfo {
    private static final String APPLICATION_ID = "id";

    private static final String APPLICATION_NAME = "name";

    private static final String APPLICATION_JAR = "jar";

    private static final String APPLICATION_JAR_HASH = "jar_hash";

    private static final String APPLICATION_CLASS = "class";

    private static final String APPLICATION_BASE_URL = "base_url";

    private static final String APPLICATION_DEPENDENCY = "dependency";

    private static final String APPLICATION_PRE_INSTALL_JAR = "pre_install_jar";

    private static final String APPLICATION_PRE_INSTALL_CLASS = "pre_install_class";
    
    private static final String APPLICATION_EXECUTABLE = "executable";
    
    private static final String APPLICATION_VERSION = "version";

    private String id;

    private String name;

    private String jarName;

    private byte[] jarHash;

    private String className;

    private URL baseUrl;

    private String[] dependencies;

    private String preInstallJar;

    private String preInstallClassName;

    private boolean installed;
    
    private boolean executable;
    
    private String version;

    public ApplicationInfo(String id, String name, String version, String jarName, byte[] jarHash, String className, URL baseUrl,
        String dependencies, boolean executable, String preInstallJar, String preInstallClassName) {
        this.id = id.trim();
        this.name = name.trim();
        this.version = version.trim();
        this.jarName = jarName.trim();
        this.jarHash = jarHash;
        this.className = className.trim();
        this.baseUrl = baseUrl;
        this.dependencies = getDependency(dependencies);
        this.preInstallJar = preInstallJar.trim();
        this.preInstallClassName = preInstallClassName.trim();
        this.executable = executable;
    }

    public ApplicationInfo(Properties properties, int index) throws MalformedURLException {
        id = getProperty(properties, APPLICATION_ID, index).trim();
        name = getProperty(properties, APPLICATION_NAME, index).trim();
        version = getProperty(properties, APPLICATION_VERSION, index).trim();
        jarName = getProperty(properties, APPLICATION_JAR, index).trim();
        jarHash = Utility.decodeHashString(getProperty(properties, APPLICATION_JAR_HASH, index).trim());
        className = getProperty(properties, APPLICATION_CLASS, index).trim();
        baseUrl = new URL(getProperty(properties, APPLICATION_BASE_URL, index).trim());
        preInstallJar = getProperty(properties, APPLICATION_PRE_INSTALL_JAR, index).trim();
        preInstallClassName = getProperty(properties, APPLICATION_PRE_INSTALL_CLASS, index).trim();
        dependencies = getDependency(getProperty(properties, APPLICATION_DEPENDENCY, index));
        executable = Boolean.parseBoolean(getProperty(properties, APPLICATION_EXECUTABLE, index));
    }

    private String[] getDependency(String depend) {
        depend = depend.trim();

        if (depend.length() == 0) {
            return new String[0];
        }

        String[] result = depend.split(",");

        for (int i = 0; i < result.length; ++i) {
            result[i] = result[i].trim();
        }

        return result;
    }

    public void saveTo(Properties properties, int index) {
        setProperty(properties, APPLICATION_ID, index, id);
        setProperty(properties, APPLICATION_NAME, index, name);
        setProperty(properties, APPLICATION_VERSION, index, version);
        setProperty(properties, APPLICATION_JAR, index, jarName);
        setProperty(properties, APPLICATION_JAR_HASH, index, Utility.encodeHashString(jarHash));
        setProperty(properties, APPLICATION_CLASS, index, className);
        setProperty(properties, APPLICATION_BASE_URL, index, baseUrl.toString());
        setProperty(properties, APPLICATION_PRE_INSTALL_JAR, index, preInstallJar);
        setProperty(properties, APPLICATION_PRE_INSTALL_CLASS, index, preInstallClassName);
        setProperty(properties, APPLICATION_EXECUTABLE, index, Boolean.toString(executable));

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < dependencies.length; ++i) {
            if (i != 0) {
                sb.append(",");
            }

            sb.append(dependencies[i]);
        }

        setProperty(properties, APPLICATION_DEPENDENCY, index, sb.toString());
    }

    private void setProperty(Properties properties, String name, int index, String value) {
        properties.setProperty(name + "_" + index, value);
    }

    private String getProperty(Properties properties, String name, int index) {
        String value = properties.getProperty(name + "_" + index);

        if (value == null) {
            throw new IllegalArgumentException("The property '" + name + "' in the application of index '" + index
                + "' cannot be found.");
        }

        return value;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public String getVersion() {
        return version;
    }

    public String getJarName() {
        return jarName;
    }

    public byte[] getHash() {
        return jarHash;
    }

    public String getClassName() {
        return className;
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    public String[] getDependencies() {
        return dependencies;
    }

    public String getPreInstallJarName() {
        return preInstallJar;
    }

    public String getPreInstallClassName() {
        return preInstallClassName;
    }

    public boolean isInstalled() {
        return installed;
    }
    
    public boolean isExecutable() {
        return executable;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }
}
