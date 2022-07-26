/*
 * Copyright (C) -2013 TopCoder Inc., All Rights Reserved.
 */
 
/*
 * LongTestServicesBean
 * 
 * Created 04/16/2006
 */
package com.topcoder.server.ejb.TestServices.longtest;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import com.topcoder.server.ejb.TestServices.longtest.dao.LongTestDao;
import com.topcoder.server.ejb.TestServices.longtest.event.LongTestServiceEventNotificator;
import com.topcoder.server.ejb.TestServices.longtest.model.LongTestCase;
import com.topcoder.server.ejb.TestServices.longtest.model.LongTestGroup;
import com.topcoder.server.ejb.dao.SolutionDao;
import com.topcoder.server.farm.longtester.LongTesterException;
import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.server.tester.Solution;
import com.topcoder.server.util.DBUtils;
import com.topcoder.services.tester.common.LongTestResults;
import com.topcoder.shared.problem.ProblemCustomSettings;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.logging.Logger;

/**
 * Default implementation of LongTestServices
 * 
 * <p>
 *  Version 1.1 (TC Competition Engine Code Execution Time Issue) change notes:
 *  <ul>
 *      <li>Added parameter <code>executionTimeLimit</code> to methods
 *      {@link #startTestGroup(int, Integer, int, int)},
 *      {@link #enqueueTests(int, int, int, Integer, int, int, List, Connection)}.</li>
 *  </ul> 
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TC Competition Engine - CPP Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Added parameter <code>cppApprovedPath</code> to method
 *      {@link #startTestGroup(int, Integer, int, int, String)},
 *      {@link #enqueueTests(int, int, int, Integer, int, int, String, List, Connection)}</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TC Competition Engine - Python Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Added parameter <code>pythonCommand</code>,<code>pythonApprovedPath</code> to method
 *      {@link #startTestGroup(int, Integer, int, int, String, String, String)},
 *      {@link #enqueueTests(int, int, int, Integer, int, int, String, String, String, List, Connection)}</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #void startTestGroup(int groupId, Integer roundType, ProblemCustomSettings)} method.</li>
 *      <li>Update{@link #enqueueTests(int, int, int, Integer, ProblemCustomSettings, List, Connection)}</li>
 * </ol>
 * </p>
 * @author Diego Belfer (Mural), savon_cn
 * @version 1.4
 */
public class LongTestServicesBean implements LongTestServices {
    /**
     * Category for logging.
     */
    private static final Logger log = Logger.getLogger(LongTestServicesBean.class);
   
    /**
     * Notificator used to notify long test service events
     */
    private LongTestServiceEventNotificator notificator;
    
    /**
     * DAO object to access long test related tables
     */
    private LongTestDao ltgDao = new LongTestDao();
    /**
     * DAO object to access solution related tables
     */
    private SolutionDao solutionDao = new SolutionDao();

    public LongTestServicesBean() {
       
    }
    
    /**
     * @see com.topcoder.services.lcontest.test.LongTestServices#createTestGroupForSolution(int, int, java.lang.String[])
     */
    public int createTestGroupForSolution(int componentId, int solutionId, String[] args) throws LongTestServiceException {
        return createTestGroupForSolution(componentId, solutionId, null, args);
    }

    /**
     * @see LongTestServicesBean#createTestGroupForSolution(int, int, int[])
     * @see LongTestServicesBean#createTestGroupForSolution(int, int, String[])
     */
    private int createTestGroupForSolution(final int componentId, final int solutionId, final int[] testCasesId, final String[] args) throws LongTestServiceException {
        Connection cnn = null;
        try {
            cnn = DBMS.getConnection(); 
            if (isValidSolutionForTesting(componentId, solutionId, cnn)) {
                Integer grpId = (Integer) DBUtils.invoke(cnn, new DBUtils.UnitOfWork() {
                    public Object doWork(Connection cnn) throws Exception {
                        int groupId = ltgDao.createTestGroupForSolution(componentId, solutionId, cnn);
                        log.debug("Created testGroup="+groupId);
                        if (testCasesId == null) {
                            ltgDao.createTestCasesForTestGroup(groupId, args, cnn);
                        } else {
                            ltgDao.createTestCasesForTestGroup(groupId, testCasesId, cnn);
                        }
                        return new Integer(groupId);
                    }
                });
                int groupId = grpId.intValue();
                //enqueueTests(groupId, componentId, solutionId, roundType, memLimit, testCaseIds, args,  cnn);
                if (log.isInfoEnabled()) {
                    log.info("TestGroup " + groupId + " created for solutionId " + solutionId + " of componentId " + componentId);
                }
                return groupId;
            } else {
                throw new LongTestServiceException("Solution cannot be tested. Verify that the tester solution is compiled.");
            }
        } catch (LongTestServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception catched. Throwing LongTestServiceException", e);
            throw new LongTestServiceException("Could not create test group for solution");
        } finally {
            DBMS.close(cnn);
        }
    }

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
    public void startTestGroup(int groupId, Integer roundType, ProblemCustomSettings custom) throws LongTestServiceException {
        Connection cnn = null;
        try {
            if (log.isInfoEnabled()) {
                log.info("Starting TestGroup " + groupId);
            }
            cnn = DBMS.getConnection(); 
            LongTestGroup group = ltgDao.findTestGroup(groupId, cnn);
            List testCases = ltgDao.findTestCasesForGroup(groupId, cnn);
            enqueueTests(groupId, group.getComponentId(), group.getSolutionId().intValue(), roundType, custom, testCases, cnn);
        } catch (Exception e) {
            log.error("Exception catched. Throwing LongTestServiceException", e);
            throw new LongTestServiceException("Could not start test group.");
        } finally {
            DBMS.close(cnn);
        }
    }

    /**
     * Push the tests cases to the test queue.
     *
     * @param groupId the id of the specific test group.
     * @param componentId the id of the problem.
     * @param solutionId the id of the solution.
     * @param roundType the round type of the problem.
     * @param custom problem customization.
     * @param testCases the test cases.
     * @param cnn the database connection.
     * @throws SQLException if any error occurs when operating databse.
     * @throws LongTestServiceException if any error occurs.
     */
    private void enqueueTests(int groupId, int componentId, int solutionId, Integer roundType,
            ProblemCustomSettings custom, List testCases, Connection cnn) throws LongTestServiceException, SQLException {
        Solution primarySolution = solutionDao.getComponentSolution(componentId, cnn);
        String className = primarySolution.getClassName();
        ComponentFiles componentFiles = getComponentFilesForSolution(componentId, solutionId, className, cnn);
        Object[][] value = new Object[testCases.size()][];
        long[] testCaseIds = new long[testCases.size()];
        int i = 0;
        for (Iterator it = testCases.iterator(); it.hasNext();) {
            LongTestCase test = (LongTestCase) it.next();
            String arg = test.getArg();
            value[i] = new Object[] {arg};
            testCaseIds[i] = test.getId();
            i++;
        }
        try {
            if (log.isDebugEnabled()) {
                log.debug("Enqueueing for testGroup="+groupId);
            }
            TestInvoker.getInstance().testSolution(groupId, componentFiles, primarySolution, testCaseIds, value, roundType, custom);
        } catch (LongTesterException e) {
            //Something strange happened. Mark test case as failed
            log.error("Cannot schedule TestRequest", e);
            log.info("Cancelling test group:" + groupId);
            try {
                cancelTestGroup(groupId);
            } catch (Exception e1) {
                log.error("Exception trying to cancel test group id="+groupId, e1);
            }
            throw new LongTestServiceException("Could not schedule tests using tester."+e.getMessage());
        }
    }
    
    /**
     * @see com.topcoder.services.lcontest.test.LongTestServices#reportTestResult(int, com.topcoder.services.tester.common.LongTestResults)
     */
    public void reportTestResult(final int requestId, final LongTestResults testResult) {
        Connection cnn = null;
        if (log.isDebugEnabled()) {
            log.debug("reportTestResult(test_case_id=" + requestId+"testResult.score="+testResult.getScore());
        }
        try {
            cnn = DBMS.getConnection();
            final LongTestCase testCase = ltgDao.findTestCase(requestId, cnn); 
            if (testCase == null) {
                log.error("reportTestResult received for an invalid test_case_id: " + requestId);
                return;
            }
            DBUtils.invoke(cnn, new DBUtils.UnitOfWork() {
                public Object doWork(Connection cnn) throws Exception {
                    ltgDao.createTestResult(requestId, testResult, cnn);
                    ltgDao.updateTestCaseStatus(requestId, LongTestCase.STATUS_PENDING, LongTestCase.STATUS_COMPLETED, cnn);
                    ltgDao.updateLastTestExecutedInGroup(testCase.getGroupId(), requestId, cnn);
                    return null;
                }
            });
            //If this test case was the last test
            if (ltgDao.updateGroupAsCompletedIfNecessary(testCase.getGroupId(), requestId, cnn)) {
                getNotificator().notifyTestGroupFinished(testCase.getGroupId());
            } 
        } catch (Exception e) {
            log.error("Exception catched", e);
        } finally {
            DBMS.close(cnn);
        }
    }

   /**
     * @see com.topcoder.services.lcontest.test.LongTestServices#cancelTestGroup(int)
     */
    public void cancelTestGroup(final int testGroupId) {
        try {
            Boolean notify = (Boolean) DBUtils.invokeAndClose(DBMS.getConnection(), new DBUtils.UnitOfWork() {
                public Object doWork(Connection cnn) throws Exception {
                    //REMOVE from QUEUE all pendings TEST
                    TestInvoker.getInstance().cancelTestsForGroup(testGroupId);
                    
                    //SET status to CANCELLED for all test_cases of the group not run yet
                    ltgDao.updateTestCaseStatusForGroup(testGroupId, LongTestCase.STATUS_PENDING, LongTestCase.STATUS_CANCELLED, cnn);
                    
                    //SET status to CANCELLED for the group if it is running or pending
                    if (ltgDao.updateTestGroupStatus(testGroupId, LongTestGroup.STATUS_PENDING, LongTestGroup.STATUS_CANCELLED, cnn)) {
                        return Boolean.TRUE;
                    }
                    return Boolean.FALSE;
                }
            });
            if (notify.booleanValue()) {
                getNotificator().notifyTestGroupFinished(testGroupId);
            }
        } catch (Exception e) {
            log.error("Exception catched", e);
        }
    }
    
    /**
     * @see com.topcoder.services.lcontest.test.LongTestServices#deleteTestGroup(int)
     */
    public void deleteTestGroup(final int testGroupId) throws LongTestServiceException {
        try {
            TestInvoker.getInstance().cancelTestsForGroup(testGroupId);
            
            Connection cnn =  DBMS.getConnection();
            try {
                ltgDao.deleteTestGroup(testGroupId, cnn);
            } finally {
                DBMS.close(cnn);
            }
        } catch (Exception e) {
            log.error("Exception catched", e);
            throw new LongTestServiceException("Internal error");
        }
    }

    
    /**
     * @see com.topcoder.server.ejb.TestServices.longtest.LongTestServices#findTestGroup(int)
     */
    public LongTestGroup findTestGroup(int testGroupId) throws LongTestServiceException, RemoteException {
        Connection cnn = null;
        try {
            cnn = DBMS.getConnection();
            LongTestGroup group = ltgDao.findTestGroup(testGroupId, cnn);
            if (group != null) {
                group.setTestCases(ltgDao.findTestCasesAndResultForGroup(testGroupId, cnn));
            }
            return group;
        } catch (Exception e) {
            log.error("Exception catched", e);
            throw new LongTestServiceException("Internal error");
        } finally {
            DBMS.close(cnn);
        }
    }
    
    /**
     * Gets a ComponentFiles object for the solution
     *   
     * @throws IllegalStateException if the solution is not found
     * @throws LongTestServiceException if the solution is not compiled
     */
    private ComponentFiles getComponentFilesForSolution(int componentId, int solutionId, String className, Connection cnn) throws SQLException, LongTestServiceException {
        if (log.isDebugEnabled()) {
            log.debug("getComponentFilesForSolution solutionId=" + solutionId);
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuffer sqlStr = null;

        try {
            //Obtain the language ID for the solution
            sqlStr = new StringBuffer(200);
            sqlStr.append("SELECT s.language_id, s.package FROM solution s WHERE s.solution_id = ?");
            ps = cnn.prepareStatement(sqlStr.toString());
            ps.setInt(1, solutionId);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new IllegalStateException("Solution not found");
            }
            int languageID = rs.getInt(1);
            String packageName = rs.getString(2); //package is resolved during compilation
            if (packageName == null) {
                throw new LongTestServiceException("Solution not compiled");
            }
            String classesPrefix = packageName.replace('.', '/');
            rs.close();
            ps.close();
            
            //Obtain all solution class files
            sqlStr = new StringBuffer(200);
            sqlStr.append("SELECT scf.path, scf.class_file, scf.sort_order");
            sqlStr.append(" FROM solution_class_file scf");
            sqlStr.append(" WHERE scf.solution_id = ?");
            sqlStr.append(" ORDER BY scf.sort_order");
        
            ps = cnn.prepareStatement(sqlStr.toString());
            ps.setInt(1, solutionId);
            rs = ps.executeQuery();
            if (rs.next()) {
                ComponentFiles componentFiles = ComponentFiles.getInstance(languageID, componentId, className, classesPrefix);
                do {
                    String path = rs.getString(1);
                    byte[] clazzBytes = rs.getBytes(2);
                    componentFiles.addClassFile(path, clazzBytes);
                } while (rs.next());
                
                if (log.isDebugEnabled()) {
                    log.debug("Loaded componentFiles: " + componentFiles);
                }
                return componentFiles;
            }
            throw new LongTestServiceException("Solution not compiled");
        } finally {
            DBMS.close(null, ps, rs);
        }
    }

    /**
     * Performs a quick check. Verifies the solution is a tester solution for the specified component
     * and that the package name have been caculated (Signs of compilation)
     * 
     * @param componentId Id of the component which the solution is for
     * @param solutionId Id of the solution to verify
     * @param cnn Database connection used to query the database
     * 
     * @return True If the solution is not a primary solution,
     *                  it is a solution for the specified component
     *                  and the package name have been caculated. 
     *         False Otherwise
     *         
     * @throws SQLException If a SQLException is thrown in the process
     */
    private boolean isValidSolutionForTesting(int componentId, int solutionId, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            StringBuffer sqlStr = null;
            sqlStr = new StringBuffer(200);
            sqlStr.append("SELECT s.solution_id ");
            sqlStr.append("     FROM solution s, component_solution_xref cs");
            sqlStr.append("     WHERE s.solution_id = ? AND cs.component_id = ?");
            sqlStr.append("           AND cs.solution_id = s.solution_id AND s.package IS NOT NULL");
            sqlStr.append("           AND cs.primary_solution = 0");
            ps = cnn.prepareStatement(sqlStr.toString());
            ps.setInt(1, solutionId);
            ps.setInt(2, componentId);
            rs = ps.executeQuery();
            return rs.next();
        } finally {
            DBMS.close(null, ps, rs);
        }
    }
    

    /**
     * @return Returns the notificator.
     */
    private LongTestServiceEventNotificator getNotificator() {
        if (notificator == null) {
            notificator = new LongTestServiceEventNotificator();
        }
        return notificator;
    }
}
