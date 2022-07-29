/*
 * Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.farm.deployer.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.deployer.process.ProcessRunner;
import com.topcoder.farm.deployer.process.ProcessRunnerException;
import com.topcoder.farm.deployer.process.ProcessTimeoutException;
import com.topcoder.farm.deployer.version.VersionObtainer;
import com.topcoder.farm.deployer.version.VersionObtainer.FileVersion;
import com.topcoder.farm.shared.util.zip.ZipUtil;

/**
 * <p>
 * Helper class providing all deployment related features.
 * </p>
 *
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Update Farm Deployer Improvement):
 * <ol>
 * <li>Adds {@link #WINDOWS_OS_TYPE} field.</li>
 * <li>Adds {@link #OS_64_BIT} field.</li>
 * <li>Adds {@link #CONTROLLER_TYPE_NAME} field.</li>
 * <li>Update {@link #resolveDeploymentFile(String, String, String, String, String)} method.</li>
 * <li>Update {@link #getResourcesXML(String, String, String, String, boolean)} method.</li>
 * <li>Update {@link #generateDeploymentJar(String, String, String, String, String, File)} method.</li>
 * <li>Update {@link #getPropertiesXML(String type, String id, String osType, String osVersion)} method.</li>
 * <li>Update {@link #getDefaultDeploymentFolder(String type,String osType, String osVersion)} method.</li>
 * <li>Update {@link #extractGeneratedDeploymentJar(String fileName)} method.</li>
 * <li>Update {@link #getGeneratedDeploymentJar(String, String,String, String, String)} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version $Id$
 */
public class DeploymentHelper {
    /**
     * Encoding used for text files
     */
    public static final String ENCODING = "ISO-8859-1";

    /**
     * Name of the file where the version is stored. <p>
     * This file will be the classpath of the deployer clients
     */
    public static final String VERSION_FILE = "version";

    /**
     * Name of the file where deployment contents are stored. <p>
     * This file will be the classpath of the deployer clients
     */
    public static final String DEPLOY_ZIP_FILE = "deploy.zip";
    
    private static final Log log = LogFactory.getLog(DeploymentHelper.class);
    private static final String DEPLOYMENT_PROPERTIES_FILE = "deployment.properties";
    private static final String SYSTEM_PROPS_FILENAME = "system-properties.properties";
    private static final String FARM_DEPLOYMENT_PREFIX = "farm-deployment-";
    private static final String shellExtension = (File.separatorChar == '/' ? "sh" : "bat");
    private static final VersionObtainer versionObtainer = new VersionObtainer();
    /**
     * <p>
     * the windows os name.
     * </p>
     */
    private static final String WINDOWS_OS_TYPE = "windows";
    /**
     * <p>
     * the 64bit os.
     * </p>
     */
    private static final String OS_64_BIT = "64bit";
    
    /**
     * <p>
     * the default controller type name.
     * </p>
     */
    private static final String CONTROLLER_TYPE_NAME = "controller";
    /**
     * <p>
     * Returns a file object for the corresponding system file for the node type and id.
     * 
     * If a file with the same name exists in the specific deployment folder, that is return else
     * searchs for that file in the default folder for the node type.
     * </p>
     *
     * @param type node type (controller|processor)
     * @param id external Id of the node (name)
     * @param fileName The name of the file
     * @param osType
     *       the os type. 
     * @param osVeresion
     *       the os version.
     * @return The file that should be use for the node
     *
     * @throws FileNotFoundException If the file could not be found
     */
    public static File resolveDeploymentFile(String type, String id, String osType, String osVersion, String fileName) throws FileNotFoundException {
        File file = new File(getSpecificDeploymentFolder(type, id), fileName);
        if (!file.exists()) {
            file = new File(getDefaultDeploymentFolder(type,osType,osVersion), fileName);
            if (!file.exists()) {
                throw new FileNotFoundException("Deployment file not found : "+file.getAbsolutePath());
            }
        }
        return file;
    }
    
    /**
     * <p>
     * Returns an JNLP XML section containing jars resources that must be downloaded 
     * by Java Web Start
     * 
     * Jars names are obtained from properties file {@link DeploymentHelper#DEPLOYMENT_PROPERTIES_FILE}
     * under the <code>jars</code> key
     * 
     * The <code>version</code> key obtained from the same file contains 
     * the version of the deployment for that node. This value is used to verify update status 
     * of the generated resource jars and to update it if it is necessary.
     * A jar name is add to the obtained jar list. This name, which has the form 
     * farm-deployment-{type}-{{id}}-{version}.jar is generated dinamically from files 
     * deployed for the given node.  
     * </p>
     *
     * @param type The node type (controller|processor)
     * @param id External Id of the node (name)
     * @param versionSupported If version tag should be used
     * @param osType
     *       the os type. 
     * @param osVeresion
     *       the os version.
     * @return a String containg the generated XML section
     *   
     * @throws IOException If an IOException is thrown during the process 
     */
    public static String getResourcesXML(String type, String id, String osType, String osVersion, boolean versionSupported) throws IOException {
        File file = resolveDeploymentFile(type, id,osType,osVersion,DEPLOYMENT_PROPERTIES_FILE);
        Properties properties = loadProperties(file);
        String[] jarsList = properties.getProperty("jars").split(",");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < jarsList.length; i++) {
            String jar = jarsList[i].trim();
            addResourceToXML(sb, jar, versionSupported);
        }
        String version = properties.getProperty(VERSION_FILE);
        String generatedName = getGeneratedDeploymentJar(type, id, osType,osVersion, version);
        addResourceToXML(sb, generatedName, versionSupported);
        return sb.toString();
    }

    private static void addResourceToXML(StringBuilder sb, String jar, boolean versionSupported) {
        FileVersion version = versionObtainer.fromString(jar);
        sb.append("     <jar href=\"");
        if (versionSupported) {
                sb.append(version.getUnversionedFileName());
                sb.append("\"");
                if (version.getVersion().length() != 0) {
                    sb.append(" version=\""+version.getVersion()+"\"");
                }
        } else {
            sb.append(version.getOriginalFileName()+"\"");
        }
        sb.append("/>\n");
    }

    /**
     * Returns true is the given fileName corresponds to a generated deployment jar
     *  
     * @param fileName The fileName to check for
     * @return true is the given fileName corresponds to a generated deployment jar
     */
    public static boolean isGeneratedDeploymentJarName(String fileName) {
        return fileName.startsWith(FARM_DEPLOYMENT_PREFIX);
    }
    
    /**
     * Decompose the fileName into type, id and version 
     * 
     * @param fileName The fileName to decompose
     * 
     * @return an array containing type, id and version in that order
     */
    public static String[] extractGeneratedDeploymentJar(String fileName) {
        String fileNameSuffix = fileName.substring(FARM_DEPLOYMENT_PREFIX.length());
        fileNameSuffix = fileNameSuffix.substring(0, fileNameSuffix.lastIndexOf('.'));
        int pos = fileNameSuffix.indexOf('-');
        String osType = fileNameSuffix.substring(0, pos);
        int pos2 = fileNameSuffix.indexOf('-',pos+1);
        String osVersion = fileNameSuffix.substring(pos+1,pos2);
        int pos3 = fileNameSuffix.indexOf('-',pos2+1);
        String type = fileNameSuffix.substring(pos2+1, pos3);
        int pos4 = fileNameSuffix.indexOf("}-", pos3+1);
        String id = fileNameSuffix.substring(pos3+2, pos4);
        String version = fileNameSuffix.substring(pos4+1);
        return new String[]{type,id,version,osType,osVersion};
    }

    /**
     * <p>
     * Generates the dinamically generate resource jar for the given node and version.
     * The generated jar is also signed and ready for JWS deployment.
     * </p>
     * @param type The node type (controller|processor)
     * @param id External Id of the node (name)
     * @param osType
     *       the os type. 
     * @param osVersion
     *       the os version.
     * @param version The version to use as part of the name
     * @param jarFile The jar destination file  
     * @throws IOException If an IOException is thrown during generation
     */
    public static void generateDeploymentJar(String type, String id, String osType, String osVersion, String version, File jarFile) throws IOException {
        log.info("Generating deployment jar for type="+type+" id="+id +" version="+version+" os_type="+osType+" os_version="+osVersion);
        File zipContents = null;
        try {
            zipContents = resolveDeploymentFile(type, id, osType, osVersion, "deploy-in-appfolder");
        } catch (Exception e) {
        }
        File jarContents = null;
        try {
            jarContents = resolveDeploymentFile(type, id, osType, osVersion, "deploy-in-classpath");
        } catch (Exception e) {
        }
        File specificTempFolder = new File(DeployerConfiguration.getInstance().getTempFolder(), type+"-"+id);
        FileUtils.forceMkdir(specificTempFolder);
        FileUtils.forceMkdir(jarFile.getParentFile());
        File versionFile = new File(specificTempFolder, VERSION_FILE);
        File zipFile = new File(specificTempFolder, DEPLOY_ZIP_FILE);
        try {
            log.debug("Generating deploy file "+zipFile);
            ZipUtil.zip(zipContents, zipFile);
            FileUtils.writeStringToFile(versionFile, version, ENCODING);
            log.debug("Generating jar file " + jarFile);
            ZipUtil.zip(new File[] {jarContents,zipFile, versionFile}, jarFile);
            try {
                signJar(jarFile);
            } catch (IOException e) {
                jarFile.delete();
                throw e;
            }
        } finally {
            FileUtils.deleteDirectory(specificTempFolder);
        }
    }

    private static void signJar(File jarFile) throws IOException {
        log.info("Signing jar file "+jarFile.getAbsolutePath());
        DeployerConfiguration config = DeployerConfiguration.getInstance();
        File signJarFile = new File(config.getDeployerRootFolder(), "signfile."+shellExtension);
        if (!signJarFile.exists()) {
            throw new FileNotFoundException("Required file not found: "+signJarFile.getAbsolutePath());
        }
        ProcessRunner runner = new ProcessRunner(
                new String[] {signJarFile.getAbsolutePath(),jarFile.getAbsolutePath()}, 
                config.getDeployerRootFolder(),
                30000);
        int exitCode = 0;
        try {
            exitCode = runner.run();
        } catch (ProcessRunnerException e) {
            log.error("Exception while trying to sign generated jar", e);
            throw new IOException("Exception while trying to sign generated jar");
        } catch (ProcessTimeoutException e) {
            throw new IOException("Timeout - Could not sign generated jar");
        } 
        if (exitCode != 0) {
            throw new IOException("Could not sign generated jar. Exitcode = " + exitCode);
        }
    }

    /**
     * Returns an JNLP XML section containing the properties to set as System properties by JWS
     * 
     * The system properties are the result of overlapping the properties defined in the file
     * {@link DeploymentHelper#SYSTEM_PROPS_FILENAME} in the default node type folder with the one
     * in the specific node folder.  
     *  
     * @param type The node type (controller|processor)
     * @param id External Id of the node (name)
     * @param osType
     *       the os type. 
     * @param osVersion
     *       the os version.
     * @return The generated xml section
     */
    public static String getPropertiesXML(String type, String id, String osType, String osVersion) {
        Map<Object, Object> values = new HashMap();
        addPropertiesIfExist(new File(getDefaultDeploymentFolder(type,osType,osVersion), SYSTEM_PROPS_FILENAME), values);
        addPropertiesIfExist(new File(getSpecificDeploymentFolder(type, id), SYSTEM_PROPS_FILENAME), values);
        if (values.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(200);
        for (Map.Entry entry : values.entrySet()) {
            addPropertyToXMLString(sb, (String) entry.getKey(), (String) entry.getValue());
        }
        return sb.toString();
    }

    private static void addPropertyToXMLString(StringBuilder sb, String name, String value) {
        sb.append("<property name=\"")
            .append(name)
            .append("\" value=\"")
            .append(value)
            .append("\"/>\n");
    }

    private static void addPropertiesIfExist(File file, Map<Object, Object> map) {
        try {
            if (file.exists()) {
                map.putAll(loadProperties(file));
            }
        } catch (Exception e) {
            log.warn("Exception when loading system properties from file: "+file, e);
        }
    }

    private static Properties loadProperties(File file) throws FileNotFoundException, IOException {
        Properties properties = new Properties();
        FileInputStream is = new FileInputStream(file);
        try {
            properties.load(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
        return properties;
    }
    
    private static File getSpecificDeploymentFolder(String type, String id)  {
        File folder = DeployerConfiguration.getInstance().getDeploymentFolder();
        File specificFolder = new File(folder, type+"-"+id);
        return specificFolder;
    }
    
    /**
     * <p>
     * get the default deployment folder.
     * </p>
     * @param type
     *       the farm type.
     * @param osType
     *       the os type. 
     * @param osVersion
     *       the os version.
     * @return the default deployment folder.
     */
    private static File getDefaultDeploymentFolder(String type,String osType, String osVersion) {
        File folder = DeployerConfiguration.getInstance().getDeploymentFolder();
        StringBuilder sb = new StringBuilder(type);
        //we only need to handle the processor
        if(!type.equalsIgnoreCase(CONTROLLER_TYPE_NAME)) {
            if(osType!=null&&osType.equalsIgnoreCase(WINDOWS_OS_TYPE)) {
                sb.append("-").append(WINDOWS_OS_TYPE).append("-def");
            }
            if(osVersion!=null && osVersion.equalsIgnoreCase(OS_64_BIT)) {
                sb.append("-").append(osVersion);
            }
        }
        File deployFolder = new File(folder, sb.toString());
        return deployFolder;
    }
    
    /**
     * <p>
     * get the generated deployment jar file name
     * </p>
     * @param type
     *       the farm type.
     * @param id
     *       the processor id.
     * @param osType
     *       the os type. 
     * @param osVersion
     *       the os version.
     * @param version the jar version
     * @return the file name.
     */
    private static String getGeneratedDeploymentJar(String type, String id,String osType, String osVersion, String version) {
        return FARM_DEPLOYMENT_PREFIX+osType+"-"+osVersion+"-"+type+"-{"+id+"}-"+version+".jar";
    }
}
