/*
 * Copyright (C) -2013 TopCoder Inc., All Rights Reserved.
 */
 
/*
 * LongTestServices
 * 
 * Created 04/23/2006
 */
package com.topcoder.server.ejb.TestServices.longtest;

import java.rmi.RemoteException;

import com.topcoder.server.ejb.TestServices.longtest.model.LongTestGroup;
import com.topcoder.services.tester.common.LongTestResults;
import com.topcoder.shared.problem.ProblemCustomSettings;

/**
 * Interface defining protocol that must be provided 
 * by implementator of LongTestServices
 *
 * All long test related 
 * 
 * <p>
 *  Version 1.1 (TC Competition Engine Code Execution Time Issue) change notes:
 *  <ul>
 *      <li>Added parameter <code>executionTimeLimit</code> to method
 *      {@link #startTestGroup(int, Integer, int, int)}.</li>
 *  </ul> 
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TC Competition Engine - CPP Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Added parameter <code>cppApprovedPath</code> to method
 *      {@link #startTestGroup(int, Integer, int, int, String)}.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TC Competition Engine - Python Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Added parameter <code>pythonCommand</code>,<code>pythonApprovedPath</code> to method
 *      {@link #startTestGroup(int, Integer, int, int, String, String, String)}.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #void startTestGroup(int groupId, Integer roundType, ProblemCustomSettings)} method.</li>
 * </ol>
 * </p>
 *
 * @author Diego Belfer (mural), savon_cn
 * @version 1.4
 */
public interface LongTestServices {

    /**
     * Create a testGroup for the specified solution.  
     * The test group will contain all specified args as test cases.
     * The test group must be started using startTestGroupForSolution.
     *  
     * @param componentId Id of the component for which the solution is for
     * @param solutionId Id of the solution to Test
     * @param args Array containing individual test case arg to run for the solution

     * 
     * @return The Id of the LongTestGroup created.
     *  
     * @throws LongTestServiceException If the test group could not be created
     */
    int createTestGroupForSolution(int componentId,
            int solutionId, String[] args)
                            throws LongTestServiceException, RemoteException;

    

    /**
     * Starts a testGroup previously created.   
     * To receive notification of test group finalization, LongTestServiceEventListener
     * must be used.
     * 
     * NOTE: This method must be invoked only 1 time for each test group id.
     *  
     * @param groupId Id of the group to start
     * @param roundType round type used by tester to filter test
     * @param custom problem customization.
     *  
     * @throws LongTestServiceException If the test group could not be started
     */
    void startTestGroup(int groupId, Integer roundType, ProblemCustomSettings custom) throws LongTestServiceException, RemoteException;
    
    /**
     * Used by tester to notify the service about the finalization of a test request.
     * Launchs an event of long test finalization
     * 
     * @param requestId Id of the test request finalized 
     * @param testResult Results of the test execution
     * 
     * @throws LongTestServiceException If the result could not be properly managed 
     */
    void reportTestResult(int requestId, LongTestResults testResult) 
                            throws LongTestServiceException, RemoteException;


    /**
     * Cancels the test group and remove it from the queue.
     * All information for the test group will keep intact.
     * If the test group was not already completed or cancelled,
     * invoking this method will launch an event of test group finalization.
     * 
     * @param testGroupId Id of the test group 
     * 
     * @throws LongTestServiceException if cancellation process fails
     */
    void cancelTestGroup(int testGroupId) throws LongTestServiceException, RemoteException;


    /**
     * Deletes all information related to the test group. If it was still scheduled, it 
     * will be removed from the queue
     * 
     * @param testGroupId Id of the test group to delete
     * 
     * @throws LongTestServiceException If the test group deletion fails
     */
    void deleteTestGroup(int testGroupId) throws LongTestServiceException, RemoteException;

    /**
     * Returns all available test group information.
     *  
     * @param testGroupId Id of the group
     * 
     * @return All test group information or null if the test group is not found
     * 
     * @throws LongTestServiceException If some error arises when trying to retrieve the information
     */
    LongTestGroup findTestGroup(int testGroupId) throws LongTestServiceException, RemoteException;
}