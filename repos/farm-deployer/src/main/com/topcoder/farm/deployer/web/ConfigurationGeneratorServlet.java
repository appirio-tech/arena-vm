/*
 * Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.farm.deployer.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * Servlet responsible for delivering configuration XML upon request.
 * </p>
 * This servlet expect 2 parameters: 
 * <li> type: containing the node type requesting the configuration. (controller|processor)
 * <li> id: The external id (name) of the node requesting its configuration
 * 
 * <p>
 * Config file returned is obtained from the specific deployment folder for the node
 * or from the default node type folder. The name of the file should be config.xml
 * </p>
 *
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Update Farm Deployer Improvement):
 * <ol>
 * <li>Update {@link #doGet(HttpServletRequest, HttpServletResponse)} method.</li>
 * <li>Update {@link #resolveConfigFile(String, String,String,String)} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version $Id$
 */
public class ConfigurationGeneratorServlet extends HttpServlet {
    private Log log = LogFactory.getLog(ConfigurationGeneratorServlet.class);
    
    /**
     * <p>
     * the default servlet doGet method.
     * </p>
     * @param req
     *         the http request.
     * @param resp
     *         the http response.
     * @throws IOException
     *          the java io exception.
     * @throws ServletException
     *          if any error occur during servlet visiting.
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String type = req.getParameter("type");
        String id = req.getParameter("id");
        String osType = req.getParameter("os_type");
        String osVersion = req.getParameter("os_version");
        if ("processor".equals(type) || "controller".equals(type)) {
            resp.setContentType("text/xml");
            sendConfiguration(resp, resolveConfigFile(type, id, osType, osVersion));
        } else {
            log.warn("Invalid request received type="+type);
            log.warn(req);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void sendConfiguration(HttpServletResponse resp, File configFile) throws FileNotFoundException, IOException {
        FileInputStream is = getConfigInputStream(configFile);
        try {
            IOUtils.copy(is, resp.getOutputStream());
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
    /**
     * <p>
     * resolve the specific configuration from url request parameters.
     * </p>
     * @param type
     *       the farm type.
     * @param id
     *       the node name.
     * @param osType
     *       the os type. 
     * @param osVersion
     *       the os version.
     * @return the file.
     * @throws ServletException
     *          if any error occur during servlet visiting.
     */
    private File resolveConfigFile(String type, String id,String osType,String osVersion) throws ServletException {
        try {
            return DeploymentHelper.resolveDeploymentFile(type, id, osType, osVersion, "config.xml");
        } catch (Exception e) {
            throw new ServletException("Configuration file not found for : "+type+"-"+id+"-"+osType+"-"+osVersion);
        }
    }
    
    private FileInputStream getConfigInputStream(File configFile) throws FileNotFoundException {
        return new FileInputStream(configFile);
    }
}
