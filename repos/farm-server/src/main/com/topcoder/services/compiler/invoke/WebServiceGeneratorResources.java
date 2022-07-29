package com.topcoder.services.compiler.invoke;

/**
 * <p>Title: WebServiceGeneratorResources</p>
 * <p>Description: Constants used by the webservice package classes read from a
 *    properties file</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: TopCoder</p>
 * @author Jeremy Nuanes
 * @version 1.0
 */

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class WebServiceGeneratorResources {

//  private static Logger logger = Logger.getLogger( WebServiceGeneratorResources.class );
    public static String TOMCAT_MANAGER_AUTHORIZATION = "TOMCAT_MANAGER_AUTHORIZATION";
    public static String TOMCAT_SERVER_PORT = "TOMCAT_SERVER_PORT";
    public static String TOMCAT_SERVER = "TOMCAT_SERVER";
    public static String UNIX_TEMP_LOCATION = "UNIX_TEMP_LOCATION";
    public static String WINDOWS_TEMP_LOCATION = "WINDOWS_TEMP_LOCATION";
    public static String JAVA_LOCATION = "JAVA_LOCATION";
    public static String WSDLEXE_FULL_PATH = "WSDLEXE_FULL_PATH";
    public static String SOAPCPP_FULL_PATH = "SOAPCPP_FULL_PATH";
    public static String WSCOMPILE_FULL_PATH = "WSCOMPILE_FULL_PATH";
    public static String WSDEPLOY_FULL_PATH = "WSDEPLOY_FULL_PATH";
    public static String WAR_STORAGE_LOCATION = "WAR_STORAGE_LOCATION";
    public static String REMOTE_SERVER = "REMOTE_SERVER";
    public static String REMOTE_SERVER_PORT = "REMOTE_SERVER_PORT";
    public static String WEB_SERVICE_SOURCE_BASE_PATH = "WEB_SERVICE_SOURCE_BASE_PATH";
    public static String WS_JAXRPC_RI_JAR = "WS_JAXRPC_RI_JAR";
    public static String WS_JAXRPC_API_JAR = "WS_JAXRPC_API_JAR";
    public static String WS_ACTIVATION_JAR = "WS_ACTIVATION_JAR";
    public static String CPP_SOAP_HEADER_FILE = "CPP_SOAP_HEADER_FILE";
    public static String CPP_SOAP_OBJECT_FILE = "CPP_SOAP_OBJECT_FILE";
    public static ResourceBundle properties = null;

    public static String getProperty(String property) {
        return (String) properties.getString(property);
    }

    static {
        try {
            properties = ResourceBundle.getBundle("WebServiceGenerator");
        } catch (MissingResourceException e) {
//       logger.error("Failed to load WebServiceGeneratorResources", e);
        } catch (Throwable e) {
//       logger.error("Resources missing", e);
        }
    }

    private WebServiceGeneratorResources() {
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Resource Bundle Test");
        System.out.println(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.UNIX_TEMP_LOCATION));
        System.out.println(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.WINDOWS_TEMP_LOCATION));
        System.out.println(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.JAVA_LOCATION));
        System.out.println(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.WSDLEXE_FULL_PATH));
        System.out.println(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.SOAPCPP_FULL_PATH));
        System.out.println(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.WSCOMPILE_FULL_PATH));
        System.out.println(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.WSDEPLOY_FULL_PATH));
        System.out.println(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.WAR_STORAGE_LOCATION));
        System.out.println(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.REMOTE_SERVER));
        System.out.println(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.REMOTE_SERVER_PORT));
        System.out.println(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.TOMCAT_SERVER));
        System.out.println(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.TOMCAT_SERVER_PORT));
        System.out.println(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.TOMCAT_MANAGER_AUTHORIZATION));
    }
}
