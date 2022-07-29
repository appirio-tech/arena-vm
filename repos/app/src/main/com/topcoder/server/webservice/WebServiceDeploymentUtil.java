package com.topcoder.server.webservice;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: TopCoder</p>
 * @author Jeremy Nuanes
 * @version 1.0
 */

import com.topcoder.server.webservice.exception.WebServiceDeploymentException;

import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class WebServiceDeploymentUtil {

    private WebServiceDeploymentUtil() {
    }

    public static String install(File warFile) throws IOException, WebServiceDeploymentException, SocketException {
        if (!warFile.exists())
            throw new IOException("WAR file: " + warFile.getPath() + " does not exist!");
        StringBuffer tempSB = new StringBuffer("http://");
        tempSB.append(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.TOMCAT_SERVER));
        if (WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.TOMCAT_SERVER_PORT) != null)
            tempSB.append(":" + WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.TOMCAT_SERVER_PORT));
        tempSB.append("/manager/install?path=/");
        tempSB.append(warFile.getName().substring(0, warFile.getName().length() - 4));
        tempSB.append("&war=jar:file:");
        tempSB.append(warFile.getPath().replace('\\', '/')); // just in case.
        tempSB.append("!/");
        removeSpaces(tempSB);
//    System.out.println(tempSB.toString());
        HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(tempSB.toString()).openConnection());
        httpURLConnection.setRequestProperty("Authorization", new StringBuffer("Basic ").append(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.TOMCAT_MANAGER_AUTHORIZATION)).toString());
        return getMessage(httpURLConnection.getInputStream());
    }

    //Only removes the context not the war file
    public static String remove(File warFile) throws IOException, WebServiceDeploymentException, SocketException {
        if (!warFile.exists())
            throw new IOException("WAR file: " + warFile.getPath() + " does not exist!");
        StringBuffer tempSB = new StringBuffer("http://");
        tempSB.append(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.TOMCAT_SERVER));
        if (WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.TOMCAT_SERVER_PORT) != null)
            tempSB.append(":" + WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.TOMCAT_SERVER_PORT));
        tempSB.append("/manager/remove?path=/");
        tempSB.append(warFile.getName().substring(0, warFile.getName().length() - 4));
        removeSpaces(tempSB);
//    System.out.println(tempSB.toString());
        HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(tempSB.toString()).openConnection());
        httpURLConnection.setRequestProperty("Authorization", new StringBuffer("Basic ").append(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.TOMCAT_MANAGER_AUTHORIZATION)).toString());
        return getMessage(httpURLConnection.getInputStream());
    }

    private static void removeSpaces(StringBuffer tempSB) {
        for (int i = 0; i != tempSB.length(); ++i)
            if (tempSB.charAt(i) == ' ')
                tempSB.deleteCharAt(i);
    }

    //Removes context and the WAR file once WAR is gone Generator needs to be run again
    public static String completeRemove(File warFile) throws IOException, WebServiceDeploymentException, SocketException {
        if (!warFile.exists())
            throw new IOException("WAR file: " + warFile.getPath() + " does not exist!");
        StringBuffer message = new StringBuffer(remove(warFile));
        if (warFile.delete())
            message.append(" !  Deleted: ").append(warFile.getPath());
        else
            throw new IOException(message.append(" !  Could not delete: ").append(warFile.getPath()).toString());
        return message.toString();
    }

    // Needs to block on the readLine so the message OK or FAIL can be received
    private static String getMessage(InputStream inputStream) throws IOException, WebServiceDeploymentException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer tempSB = new StringBuffer();
        String line = null;
        while ((line = reader.readLine()) != null)
            tempSB.append(line);
        if (tempSB.indexOf("FAIL - ") != -1)
            throw new WebServiceDeploymentException(tempSB.toString());
        return tempSB.toString();
    }
}
