package com.topcoder.server.webservice;

/**
 * Title:        WST2
 * Description:  Test suite for WebServices 2
 * Copyright:    Copyright (c) 2002
 * Company:      TopCoder
 * @author       Eric Kjellman
 * @version 1.0
 */

import junit.framework.*;

import com.topcoder.server.webservice.*;
import com.topcoder.server.webservice.remoteserver.*;

import java.io.*;
import java.util.*;
import java.lang.InterruptedException;
import java.lang.Process;
import java.lang.Runtime;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import javax.xml.parsers.ParserConfigurationException;

import com.topcoder.server.common.RemoteFile;
import com.topcoder.services.message.handler.*;
import com.topcoder.server.services.TeamServices;
import com.topcoder.server.mpsqas.webservice.*;

public class WST2 extends junit.framework.TestCase {

    private int componentID;
    private int teamID;
    private int assigneeID;
    private int roundID;
    private int javaLanguageID;
    private int userID;
    private String basepath;
    private String path;
    private WebServiceProblem problem;
    private String problemname;
    private RemoteFile interfaceFile;
    private RemoteFile implementationFile;
    private RemoteFile[] helperFiles;
    private String classPath;
    private TeamServices teamservices;
    private HashMap files;

    public WST2(String name) {
        super(name);
    }

    protected void setUp() {

        try {
//            roundID = 1;
//            teamID = 1;
//            assigneeID = 1;
            componentID = 1;
            basepath = "/";
            path = "/export/home/coresys/app/src/testcases/com/topcoder/server/webservice";
            problemname = "GracefulWords";
            interfaceFile = new RemoteFile(new File(path + "/GracefulWordsIF.java"), basepath);
            implementationFile = new RemoteFile(new File(path + "/GracefulWordsImpl.java"), basepath);
            RemoteFile[] helperFiles = new RemoteFile[]{};
            javaLanguageID = 1;
            userID = 1;
            files = new HashMap();
        } catch (Exception e) {
            fail("setUp failed" + e);
        }

    }

//    public void testCreateService() throws Exception {
//	System.out.println(roundID + " " + teamID + " " + assigneeID + " " + componentID);
//	teamservices.assignComponent(roundID, teamID, assigneeID, componentID);
//        problem = new WebServiceProblem(componentID, problemname, interfaceFile, implementationFile, helperFiles);
//
//        WebServiceGenerator.createService(problem);
//        /* TODO:
//           Create tests to see if the service is properly deployed.
//        */
//    }


//    public void testDeployService() throws Exception {
//        WebServiceProblem problem = new WebServiceProblem(componentID, problemname, interfaceFile, implementationFile, helperFiles);
//        WebServiceDeploymentResult a = com.topcoder.server.mpsqas.webservice.WebServiceWaiter.deployService(problem);
//        System.out.println("WS Deployment Result: " + a.success() + " " + a.getExceptionText());
//        Assert.assertEquals(true, a.success());
//    }

    public void testProperDeploy() throws Exception {
        WebServiceProblem problem = new WebServiceProblem(componentID, problemname, interfaceFile, implementationFile, helperFiles);
        WebServiceDeploymentResult a = com.topcoder.server.mpsqas.webservice.WebServiceWaiter.deployService(problem);
        if (!a.success()) {
            fail("Web Deployment Failed");
        }
//        Connection conn = DBMS.getConnection();
//        ArrayList b = com.topcoder.server.ejb.MPSQASServices.compileSolution(files, javaLanguageID, componentID, conn)
        com.topcoder.server.ejb.MPSQASServices.MPSQASServicesBean MQbean = new com.topcoder.server.ejb.MPSQASServices.MPSQASServicesBean();
        files.put((Object) "1", (Object) "/export/home/coresys/app/testData/webservice/hello/Hello.java");
        ArrayList b = MQbean.compileSolution(files, javaLanguageID, componentID, userID);
        for (int i = 0; i < b.size(); i++) {
            System.out.println("Result " + i + ": " + b.get(i).toString());
        }
    }
}
