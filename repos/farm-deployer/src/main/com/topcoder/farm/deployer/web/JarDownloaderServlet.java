/*
 * Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.farm.deployer.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.topcoder.farm.deployer.version.VersionObtainer;
import com.topcoder.farm.deployer.version.VersionObtainer.FileVersion;

/**
 * This Servlet serves files deployed in the jars deployer folder .
 *  
 * If the jar requested doesn't exist in the jars folder, and its name matchs the format
 * farm-deployment-{type}-{{id}}-{version}.jar, it will try to generated the jar dinamically 
 * from the deployment configuration folders.  
 *
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Update Farm Deployer Improvement):
 * <ol>
 * <li>Update {@link #resolveFile(HttpServletRequest req, HttpServletResponse resp)} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version $Id$
 */
public class JarDownloaderServlet extends HttpServlet {
    private static final String IF_MODIFIED_SINCE = "If-Modified-Since";
    private static final String VERSION_ID = "version-id";
    private static final String HEADER_LASTMOD      = "Last-Modified";    
    private static final String HEADER_JNLP_VERSION = "x-java-jnlp-version-id";
    private static final VersionObtainer versioner = new VersionObtainer();

    
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resolveFile(req, resp);
    }
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        
        File jarFile = resolveFile(req, resp);
        if (jarFile == null) {
            return;
        }
        if (!hasChanged(req, jarFile)) {
            resp.sendError(HttpServletResponse.SC_NOT_MODIFIED);
        }
        FileInputStream is = new FileInputStream(jarFile);
        try {
            IOUtils.copy(is, resp.getOutputStream());
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
    
    private boolean hasChanged(HttpServletRequest req, File jarFile) {
        if (req.getHeader(IF_MODIFIED_SINCE) == null) {
            return true;
        }
        long dateHeader = req.getDateHeader(IF_MODIFIED_SINCE);
        return jarFile.lastModified() > dateHeader;
    }
    /**
     * <p>
     * resolve the jar file request.
     * </p>
     * @param req
     *        the http request.
     * @param resp
     *        the http response.
     * @return the jar file.
     * @throws IOException
     *          if any error during jar file download.
     */
    private File resolveFile(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String versionRequested  = req.getParameter(VERSION_ID);
        String fileRequested = req.getServletPath().substring(1);
        File jarsFolder = DeployerConfiguration.getInstance().getJarsFolder();
        FileVersion fileInfo = versioner.fromFileAndVersion(fileRequested, versionRequested);
        File jarFile = new File(jarsFolder, fileInfo.getOriginalFileName());
        if (!jarFile.exists()) {
            if (DeploymentHelper.isGeneratedDeploymentJarName(fileRequested)) {
                String[] values = DeploymentHelper.extractGeneratedDeploymentJar(fileInfo.getOriginalFileName());
                String type = values[0];
                String id = values[1];
                String version = values[2];
                String osType = values[3];
                String osVersion = values[4];
                DeploymentHelper.generateDeploymentJar(type, id, osType, osVersion, version, jarFile);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
        }
        resp.setContentType("application/java-archive");
        if (fileInfo.getVersion() != null && fileInfo.getVersion().length()>0) {
            resp.setHeader(HEADER_JNLP_VERSION, fileInfo.getVersion());
        }
        resp.setDateHeader(HEADER_LASTMOD, jarFile.lastModified());
        return jarFile;
    }
    
    protected void doPost(HttpServletRequest arg0, HttpServletResponse arg1) throws IOException {
        doGet(arg0, arg1);
    }
}
