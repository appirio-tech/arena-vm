/*
 * InitializerServlet
 * 
 * Created 08/31/2006
 */
package com.topcoder.farm.deployer.web;

import java.io.File;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * This servlet initializes deployer application
 * 
 * Basically initializes configuration for the deployer.
 * 
 *  
 * Note: The context environment variable <i>deploymentFolder</i> must be set in
 * order to initialization succed. It must contain the full path to the deployer folder in where
 * deployment files reside. 
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InitializerServlet extends HttpServlet {
    private Log log = LogFactory.getLog(InitializerServlet.class);
    
    public void init() throws ServletException {
        try {
            initLog4J();
            Context initCtx = new InitialContext();
            String folder = (String) initCtx.lookup("java:comp/env/deploymentFolder");
            File deployFile = new File(folder);
            DeployerConfiguration.getInstance().setDeployerRootFolder(deployFile);
            log.info("Deployment folder is "+deployFile.getAbsolutePath());
        } catch (NamingException e) {
            throw new ServletException("Could not initialize deployer. Remember to configure context enviroment variable deploymentFolder");
        }
    }

    private void initLog4J() {
        String prefix =  getServletContext().getRealPath("/");
        String file = getInitParameter("log4j-init-file");
        // if the log4j-init-file is not set, then no point in trying
        if(file != null) {
            DOMConfigurator.configure(prefix+file);
        }
    }
}
