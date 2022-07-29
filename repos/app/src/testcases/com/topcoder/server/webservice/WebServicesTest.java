package com.topcoder.server.webservice;

/**
 * Title:        WebServicesTest
 * Description:  Test suite for WebServices
 * Copyright:    Copyright (c) 2002
 * Company:      TopCoder
 * @author       Eric Kjellman
 * @version 1.0
 */

import junit.framework.*;

import com.topcoder.server.webservice.*;
import com.topcoder.server.webservice.remoteserver.*;

import java.io.File;
//import java.io.FileFilter;
import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.FilenameFilter;
import java.io.IOException;
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.lang.InterruptedException;
//import java.lang.Process;
//import java.lang.Runtime;
//import java.rmi.RemoteException;
//import java.rmi.NotBoundException;
//import java.net.MalformedURLException;
//import javax.xml.parsers.ParserConfigurationException;
import com.topcoder.server.common.RemoteFile;

//import com.topcoder.services.message.handler.*;

public class WebServicesTest extends junit.framework.TestCase {

    private int problemID;
    private String basepath;
    private String path;
    private WebServiceProblem problem;
    private String problemname;
    private RemoteFile interfaceFile;
    private RemoteFile implementationFile;
    private RemoteFile[] helperFiles = null;
    //private String classPath;

    public WebServicesTest(String name) {
        super(name);
    }

    protected void setUp() {

        try {
            problemID = 1;
            basepath = "/";
            path = "/export/home/coresys/app/src/testcases/com/topcoder/server/webservice";
            problemname = "GracefulWords";
            interfaceFile = new RemoteFile(new File(path + "/GracefulWordsIF.java"), basepath);
            implementationFile = new RemoteFile(new File(path + "/GracefulWordsImpl.java"), basepath);
            //RemoteFile[] helperFiles = new RemoteFile[] {};
        } catch (Exception e) {
            fail("setUp failed");
        }

    }

    public void testWSProblemCreate() throws IOException, FileNotFoundException {

        problem = new WebServiceProblem(problemID, problemname, interfaceFile, implementationFile, helperFiles);
        Assert.assertEquals(problem.getProblemId(), problemID);
        Assert.assertEquals(problem.getServiceName(), problemname);
        Assert.assertEquals(problem.getInterfaceFile(), interfaceFile);
        Assert.assertEquals(problem.getImplementationFile(), implementationFile);
        Assert.assertEquals(problem.getHelperFiles(), helperFiles);
    }

    public void testRemoteFileCreate() throws IOException, FileNotFoundException {
        System.out.println("Testing RemoteFile with: " + path + "/GracefulWordIF.java");
        interfaceFile = new RemoteFile(new File(path + "/GracefulWordsIF.java"), basepath);
        Assert.assertEquals(interfaceFile.getName(), "GracefulWordsIF.java");
        Assert.assertEquals(interfaceFile.getBasePath(), path);
        Assert.assertEquals(interfaceFile.getPath(), path + "/GracefulWordsIF.java");
    }

    public void testWebServiceGeneratorTest() throws Exception {
        String[] args = {"/export/home/coresys/app/testData"};
        WebServiceGeneratorTest.main(args);
    }

    public void testCSServerCreateInstanceOf() throws Exception {
        //ICSServer a = CSServer.createInstanceOf();
        CSServer.createInstanceOf();
    }

    public void testCreateService() throws Exception {
        problem = new WebServiceProblem(problemID, problemname, interfaceFile, implementationFile, helperFiles);

        WebServiceGenerator.createService(problem);
        /* TODO:
           Create tests to see if the service is properly deployed.
        */
    }

    /*
     * TODO: Complete these tests.
     *
    public void testCSServerGenerateCSClient() throws Exception {
    	String serviceName = "GracefulWords"
    	RemoteFile wsdlRemoteFile = new RemoteFile(new File(""), basepath);
    	// TODO: Figure out how to get the path for a wsdl file
    	RemoteFile a = generateCSClient(serviceName, wsdlRemoteFile);
	}

    public void testCSServerGetInstanceOf() throws Exception {
    	String server = "someserver";
    	String port = "someport";
    	ICSServer a = CSServer.getInstanceOf(someserver, someport);
    	// TODO: Figure out what the server and port should be named in test.
	}

    // right now this test is meaningless, so it's not being included.
    public void testProcessMessage() throws Exception {
    	ObjectMessage msg = "some valid message";
		WebServiceMessageHandler wsmh = new WebServiceMessageHandler();
		Assert.assertEquals(wsmh.processMessage(msg), true);
	}
    */

}
