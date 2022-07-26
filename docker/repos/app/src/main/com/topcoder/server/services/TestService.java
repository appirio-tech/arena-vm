/*
 * Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import com.topcoder.server.common.ChallengeAttributes;
import com.topcoder.server.ejb.TestServices.TestServices;
import com.topcoder.server.ejb.TestServices.TestServicesException;
import com.topcoder.server.ejb.TestServices.TestServicesLocator;
import com.topcoder.server.ejb.TestServices.to.ComponentAndDependencyFiles;
import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.server.tester.Solution;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.server.webservice.WebServiceRemoteFile;
import com.topcoder.shared.language.JavaLanguage;

/**
 * This class will contain all the static methods for use by
 * anyone who wants to send a test message to the actual Testers using JMS.
 *
 * <p>
 * Changes in version 1.1 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *     <li>Updated {@link #recordSystemTestResult(int, int, int, int, int, Object, boolean, double, int, int, String)}
 *     to handle changes failure message.</li>
 *     <li>Added {@link #getComponentSolution(int)} for retrieving component by ID.</li>
 * </ol>
 * </p>
 *
 * @author Hao Kung, gevak
 * @version 1.1
 */
public final class TestService {
    /**
     * Category for logging.
     */
    private static final Logger s_trace = Logger.getLogger(TestService.class);


    /* Static initialization block for the topic stuff */
    static {
        s_trace.debug("Initializing TestServicesBean...");
        try {
            TestServicesLocator.getService();
        } catch (Exception e) {
            error("", e);
        }
    }

    public static void ejbServerIsUp() throws NamingException, CreateException, RemoteException {
        TestServicesLocator.getService();
    }


    private TestService() {
    }

    public static void start() {
    }

    public static void stop() {
    }

    /**
     * Records system test result.
     *
     * @param contestId Contest ID.
     * @param coderId Coder ID.
     * @param roundId Round ID.
     * @param problemId Problem ID.
     * @param testCaseId Test case ID.
     * @param result Result.
     * @param succeeded Succeeded.
     * @param execTime Execution time.
     * @param failure_reason Failure reason code.
     * @param systemTestVersion System test version.
     * @param message Failure message.
     */
    public static void recordSystemTestResult(int contestId, int coderId, int roundId, int problemId,
       int testCaseId, Object result, boolean succeeded, double execTime, int failure_reason,
       int systemTestVersion, String message) {
       long st = 0;
       if (s_trace.isDebugEnabled()) {
           st = System.currentTimeMillis();
           s_trace.debug("Before recordSystemTestResults coderId="+coderId+" testCaseId="+testCaseId);
       }
        try {
            getTestServices().recordSystemTestResult(contestId, coderId, roundId, problemId,
                    testCaseId, result, succeeded, execTime, failure_reason, systemTestVersion, message);
            if (s_trace.isDebugEnabled()) {
                s_trace.debug("After recordSystemTestResults coderId="+coderId+" testCaseId="+testCaseId+" took:" + (System.currentTimeMillis()-st));
            }
        } catch (Exception e) {
            error("", e);
        }
    }

    public static Solution getSolution(String className) throws TestServicesException {
        try {
            return getTestServices().getComponentSolution(className);
        } catch (Exception e) {
            s_trace.error(e);
            throw new TestServicesException(e.getMessage());
        }
    }

    public static ArrayList retrieveTestCases(int componentID) {
        try {
            return getTestServices().retrieveTestCases(componentID);
        } catch (Exception e) {
            error("", e);
        }
        return null;
    }

    /*
    public static UserTestAttributes getUserTest( int componentID )
    {
        return new UserTestAttributes(CoreServices.getSimpleComponent(componentID));
    }
    */

    public static ComponentFiles getComponentFiles(int contestId, int roundId, int componentID, int coderId, int classFileType) {
        try {
            return getTestServices().getComponentFiles(contestId, roundId, componentID, coderId, classFileType);
        } catch (Exception e) {
            error("Failed to getComponentFiles", e);
        }
        return null;
    }


    public static ComponentAndDependencyFiles getComponentAndDependencyFiles(int contestId, int roundId, int problemId, int componentId, int coderId, int componentFilesType, int dependencyFilesType, boolean solution) {
        try {
            return getTestServices().getComponentAndDependencyFiles(contestId, roundId, problemId, componentId, coderId, componentFilesType, dependencyFilesType, solution);
        } catch (Exception e) {
            error("Failed to getComponentAndDependencyFiles", e);
            return null;
        }
    }

    public static ComponentFiles[] getDependencyComponentFiles(int contestId, int roundId, int componentID, int coderId, int classFileType) {
        try {
            return getTestServices().getDependencyComponentFiles(contestId, roundId, componentID, coderId, classFileType);
        } catch (Exception e) {
            error("Failed to getComponentDependecyFiles", e);
        }
        return null;
    }

    public static HashMap getCompiledWebServiceClientFiles(int problemId, int languageId) {
        s_trace.info("getCompiledWebServiceClientFiles(" + problemId + ", " + languageId + ")");
        try {
            WebServiceRemoteFile[] wsrf = getTestServices().getWebServiceClientsForProblem(problemId, languageId);
            return processWebServiceRemoteFiles(wsrf, languageId);
        } catch (Exception e) {
            error("Failed to getCompiledWebServiceClientFiles", e);
            return new HashMap();
        }
    }

    public static HashMap processWebServiceRemoteFiles(WebServiceRemoteFile[] wsrf, int languageId) {
        try {
            HashMap hash = new HashMap();

            // extract the WS dependencies
            for (int i = 0; i < wsrf.length; i++) {
                switch (wsrf[i].getType()) {
                case WebServiceRemoteFile.WEB_SERVICE_IMPLEMENTATION:
                case WebServiceRemoteFile.WEB_SERVICE_HELPER:
                case WebServiceRemoteFile.WEB_SERVICE_USER_HELPER:
                    // this is the webservice itself
                    break;

                case WebServiceRemoteFile.WEB_SERVICE_INTERFACE:
                case WebServiceRemoteFile.WEB_SERVICE_CLIENT_HEADER:
                case WebServiceRemoteFile.WEB_SERVICE_CLIENT_SOURCE:

                    if (wsrf[i].hasCompiledObjectFiles()) {
                        WebServiceRemoteFile[] cwsrf = wsrf[i].getCompiledObjectFiles();

                        for (int j = 0; j < cwsrf.length; j++) {
                            if (languageId == JavaLanguage.ID) {
                                s_trace.info("putting: " + getJavaPackageFromPath(cwsrf[j].getPath()));
                                hash.put(getJavaPackageFromPath(cwsrf[j].getPath()), cwsrf[j].getContents());
                            } else {
                                hash.put(cwsrf[j].getPath(), cwsrf[j].getContents());
                            }
                        }
                    }
                }
            }
            return hash;
        } catch (Exception e) {
            error("Failed to processCompiledWebServiceClientFiles", e);
        }
        return new HashMap();
    }


    private static String getJavaPackageFromPath(String path) {
        int idx = path.indexOf(".class");
        if (idx == -1) {
            throw new IllegalArgumentException("Invalid java class file name: " + path);
        }
        String name = path.substring(0, idx).replace('/', '.');
        if (name.startsWith(".")) name = name.substring(1);
        return name;
    }

    public static String recordChallengeResults(ChallengeAttributes chal) {
        try {
            return getTestServices().recordChallengeResults(chal);
        } catch (Exception e) {
            error("", e);
        }
        return null;
    }

    public static int getSystestsLeft(int roundID) throws RemoteException, TestServicesException {
        return getTestServices().getSystestsLeft(roundID);
    }

    public static int getTotalSystests(int roundID) throws RemoteException, TestServicesException {
        return getTestServices().getTotalSystests(roundID);
    }

    private static void error(Object message, Throwable t) {
        s_trace.error(message, t);
    }

    private static TestServices getTestServices() throws RemoteException {
        try {
            return TestServicesLocator.getService();
        } catch (NamingException e) {
            s_trace.error("Cannot obtain TestServices",e);
            throw new RemoteException("Cannot obtain TestServices:"+e.getMessage());
        } catch (CreateException e) {
            s_trace.error("Cannot obtain TestServices",e);
            throw new RemoteException("Cannot obtain TestServices:"+e.getMessage());
        }
    }
}
