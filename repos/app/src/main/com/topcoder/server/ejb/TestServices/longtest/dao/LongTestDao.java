/*
* Copyright (C) 2006 - 2014 TopCoder Inc., All Rights Reserved.
*/


package com.topcoder.server.ejb.TestServices.longtest.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.topcoder.server.ejb.TestServices.longtest.model.LongTestCase;
import com.topcoder.server.ejb.TestServices.longtest.model.LongTestCaseResult;
import com.topcoder.server.ejb.TestServices.longtest.model.LongTestGroup;
import com.topcoder.server.util.DBUtils;
import com.topcoder.services.tester.common.LongTestRequest;
import com.topcoder.services.tester.common.LongTestResults;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.IdGeneratorClient;
import com.topcoder.util.idgenerator.IDGenerationException;

/**
 * Data Access Object (DAO) class for persistence of 
 * {@link com.topcoder.server.ejb.TestServices.longtest.model.*} objects.
 * 
 * All access to tables related to long test service, must be done using this
 * class.
 * 
 * This is not a DAO pattern exactly, but it is a step to remove SQL code from
 * services. 
 * Connection is passed to all methods because currently 
 * we don't manage connection and transaction using the app server.
 *
 * <p>
 * Changes in version 1.1 (Return Peak Memory Usage for Marathon Match Cpp v1.0):
 * <ol>
 *      <li>Update {@link #findTestResult(int testId, Connection cnn)} method.</li>
 *      <li>Update {@link #assembleTestResult(ResultSet rs, int startIndex)} method.</li>
 *      <li>Update {@link #findTestCasesAndResultForGroup(int testGroupId, Connection cnn)} method.</li>
 *      <li>Update {@link #createTestResult(int testId, LongTestResults testResult, Connection cnn)} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), savon_cn
 * @version 1.1
 */
public class LongTestDao {
    //Implemetation notes:
    //SQL commands include Informix optimizer directives to avoid full scans
    //when tables are small. The use of full scans on this kind of tables, where a
    //a lot of updates take place, generates lock timeouts 
    
    /**
     * Create a long test group  for the specified component and solution
     * 
     * @param componentId Id of the component for which the solution is.
     * @param solutionId Id of the solution for which the test group will be created 
     * @param cnn Connection to use
     * 
     * @return If of the created long test group
     * 
     * @throws SQLException If a SQLException is thrown in the process
     */
    public int createTestGroupForSolution(int componentId, int solutionId, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        try {
            int groupId = IdGeneratorClient.getSeqIdAsInt(DBMS.LONG_TEST_GROUP_SEQ);
            StringBuffer sqlStr = new StringBuffer(200);
            sqlStr.append("INSERT INTO long_test_group ");
            sqlStr.append("         (ltg_id, type_id, status_id, component_id, solution_id, pending_tests, status_date)");
            sqlStr.append("  VALUES (?,?,?,?,?,?,?)");
            ps = cnn.prepareStatement(sqlStr.toString());
            ps.setInt(1, groupId);
            ps.setInt(2, LongTestRequest.CODE_TYPE_SOLUTION);
            ps.setInt(3, LongTestGroup.STATUS_PENDING);
            ps.setInt(4, componentId);
            ps.setInt(5, solutionId);
            ps.setInt(6, 0);
            ps.setTimestamp(7, newTimeStamp());
            ps.execute();
            return groupId;
        } catch (IDGenerationException e) {
            handleException(e);
            return 0; //Never reached
        } finally {
            DBMS.close(ps);
        }

    }

    private Timestamp newTimeStamp() {
        return new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Creates a set of test cases for the specified test group.
     * 
     * @param groupId Id of the group to which add the test cases
     * @param testCasesId Ids of the system test cases to add as test cases to the test group 
     * @param cnn Connection to use
     * 
     * @return Array containg all test cases ids.
     * 
     * @throws SQLException If a SQLException is thrown in the process
     */
    public long[] createTestCasesForTestGroup(final int groupId, final int[] testCasesId, Connection cnn) throws SQLException {
        final long[] ids =  new long[testCasesId.length];
        try {
            DBUtils.invoke(cnn, new DBUtils.UnitOfWork() {
                public Object doWork(Connection cnn) throws SQLException, IDGenerationException {
                    StringBuffer sqlStr = new StringBuffer(200);
                    sqlStr.append("INSERT INTO long_test_case ");
                    sqlStr.append("     (ltc_id, ltg_id, test_case_id, status_id)");
                    sqlStr.append("     VALUES (?,?,?,?)");
                    PreparedStatement ps = cnn.prepareStatement(sqlStr.toString());
                    try {
                        for (int i = 0; i < testCasesId.length; i++) {
                            int testId = IdGeneratorClient.getSeqIdAsInt(DBMS.LONG_TEST_CASE_SEQ);
                            ps.setInt(1, testId);
                            ps.setInt(2, groupId);
                            ps.setInt(3, testCasesId[i]);
                            ps.setInt(4, LongTestCase.STATUS_PENDING);
                            ps.execute();
                            ids[i] = testId;
                        }
                        DBMS.close(ps);
                        addPendingTests(groupId, testCasesId.length, cnn);
                        return null;
                    } finally {
                        DBMS.close(ps);
                    }
                }
            });
        } catch (Exception e) {
            handleException(e);
        }
        return ids;
    }
    
    /**
     * Creates a set of test cases for the specified test group.
     * 
     * @param groupId Id of the group to which add the test cases
     * @param args String array containing a test case arg in each bucket 
     * @param cnn Connection to use
     * 
     * @return Array containing all test cases ids.
     * 
     * @throws SQLException If a SQLException is thrown in the process
     */
    public long[] createTestCasesForTestGroup(final int groupId, final String[] args, Connection cnn) throws SQLException {
        final long[] ids =  new long[args.length];
        try {
            DBUtils.invoke(cnn, new DBUtils.UnitOfWork() {
                public Object doWork(Connection cnn) throws SQLException, IDGenerationException {
                    PreparedStatement ps = null;
                    try {
                        StringBuffer sqlStr = new StringBuffer(200);
                        sqlStr.append("INSERT INTO long_test_case ");
                        sqlStr.append("     (ltc_id, ltg_id, arg, status_id)");
                        sqlStr.append("     VALUES (?,?,?,?)");
                        ps = cnn.prepareStatement(sqlStr.toString());
                        for (int i = 0; i < args.length; i++) {
                            int testId = IdGeneratorClient.getSeqIdAsInt(DBMS.LONG_TEST_CASE_SEQ);
                            ps.setInt(1, testId);
                            ps.setInt(2, groupId);
                            ps.setString(3, args[i]);
                            ps.setInt(4, LongTestCase.STATUS_PENDING);
                            ps.execute();
                            ids[i] = testId;
                        }
                        DBMS.close(ps);
                        addPendingTests(groupId, args.length, cnn);
                        return null;
                    }finally {
                        DBMS.close(ps);
                    }
                }
            });
        } catch (Exception e) {
            handleException(e);
        } 
        return ids;
    }


    /**
     * Finds a test case
     * @param testId id of the test case to find
     * @param cnn Connection to use
     * 
     * @return The LongTestCase or null if it is not found
     * 
     * @throws SQLException If a SQLException is thrown in the process
     */
    public LongTestCase findTestCase(int testId, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            StringBuffer sqlStr = new StringBuffer(200);
            sqlStr.append("SELECT {+AVOID_FULL(long_test_case)} ltc_id, ltg_id, status_id, arg, test_case_id");
            sqlStr.append("    FROM long_test_case WHERE ltc_id = ?");
            ps = cnn.prepareStatement(sqlStr.toString());
            ps.setInt(1, testId);
            rs = ps.executeQuery();
            if (!rs.next()) {
                return null;
            }
            return assembleTestCase(rs);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
        }
    }

    /**
     * Finds a test group
     * @param testGroupId id of the test group to find
     * @param cnn Connection to use
     * 
     * @return The LongTestGroup or null if it is not found
     * 
     * @throws SQLException If a SQLException is thrown in the process
     */
    public LongTestGroup findTestGroup(int testGroupId, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            StringBuffer sqlStr = new StringBuffer(200);
            sqlStr.append("SELECT {+AVOID_FULL(long_test_group)} ltg_id, type_id, status_id, component_id,");
            sqlStr.append("       solution_id, pending_tests");
            sqlStr.append("    FROM long_test_group WHERE ltg_id = ?");
            ps = cnn.prepareStatement(sqlStr.toString());
            ps.setInt(1, testGroupId);
            rs = ps.executeQuery();
            if (!rs.next()) {
                return null;
            }
            return assembleTestGroup(rs);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
        }
    }

    
    /**
     * Finds a test case result 
     * @param testId Id of the test case owning the result
     * @param cnn Connection to use
     * 
     * @return The LongTestCaseResult or null if it is not found
     * 
     * @throws SQLException If a SQLException is thrown in the process
     */
    public LongTestCaseResult findTestResult(int testId, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            StringBuffer sqlStr = new StringBuffer(200);
            sqlStr.append("SELECT success, score, message, ");
            sqlStr.append("       processing_time, stdout, stderr, peak_memory_used"); 
            sqlStr.append("    FROM long_test_result WHERE ltc_id = ?");
            ps = cnn.prepareStatement(sqlStr.toString());
            ps.setInt(1, testId);
            rs = ps.executeQuery();
            if (!rs.next()) {
                return null;
            }
            return assembleTestResult(rs, 0);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
        }
    }
    
    
    /**
     * Sets a new status to the group
     * 
     * @param groupId Id of the group
     * @param currentStatus Status that the group must have, to update take effects 
     * @param newStatus new status to set
     * @param cnn Connection to use
     * 
     * @return true If the group status was updated
     * 
     * @throws SQLException If a SQLException is thrown in the process
     */
    public boolean updateTestGroupStatus(int groupId, int currentStatus, int newStatus, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        try {
            StringBuffer sqlStr = new StringBuffer(200);
            sqlStr.append("UPDATE {+AVOID_FULL(long_test_group)} long_test_group");
            sqlStr.append("    SET status_id = ?, status_date = ? ");
            sqlStr.append("    WHERE ltg_id = ? AND status_id = ?");
            ps = cnn.prepareStatement(sqlStr.toString());
            ps.setInt(1, newStatus);
            ps.setTimestamp(2, newTimeStamp());
            ps.setInt(3, groupId);
            ps.setInt(4, currentStatus);
            return ps.executeUpdate() == 1;
        } finally {
            DBMS.close(ps);
        }
    }
    
    /**
     * Sets a new status to the test case
     * 
     * @param testId Id of the test case
     * @param currentStatus Status that the test case must have, to update take effects 
     * @param newStatus new status to set
     * @param cnn Connection to use
     * 
     * @return true If the test case status was updated
     * 
     * @throws SQLException If a SQLException is thrown in the process
     */
    public boolean updateTestCaseStatus(int testId, int currentStatus, int newStatus, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        try {
            StringBuffer sqlStr = new StringBuffer(200);
            sqlStr.append("UPDATE {+AVOID_FULL(long_test_case)} long_test_case");
            sqlStr.append("    SET status_id = ? ");
            sqlStr.append("    WHERE ltc_id = ? AND status_id = ?");
            ps = cnn.prepareStatement(sqlStr.toString());
            ps.setInt(1, newStatus);
            ps.setInt(2, testId);
            ps.setInt(3, currentStatus);
            return ps.executeUpdate() == 1;
        } finally {
            DBMS.close(ps);
        }
    }
    
    
    /**
     * Create a LongTestResult for the test case
     * 
     * @param testId Id of the test case
     * @param testResult LongTestResults containing information to use for filling 
     *                   the new LongTestCaseResult
     * @param cnn Connection to use
     * 
     * @throws SQLException If a SQLException is thrown in the process
     */
    public void createTestResult(int testId, LongTestResults testResult, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        try {
            StringBuffer sqlStr = new StringBuffer(100);
            StringBuffer sqlValueStr = new StringBuffer(30);
            sqlStr.append("INSERT INTO long_test_result"); 
            sqlStr.append("    (ltc_id, success, score, processing_time, create_date, peak_memory_used");
            sqlValueStr.append("    VALUES (?, ?, ?, ?, ?, ?");
            
            //Work around... I got -79716 System or internal error
            //when setting values with setNull
            if (testResult.getMessage() != null && testResult.getMessage().length() != 0) {
                sqlStr.append(", message");
                sqlValueStr.append(", ?");
            }
            if (testResult.getStdout() != null && testResult.getStdout().length() != 0) {
                sqlStr.append(", stdout");
                sqlValueStr.append(", ?");
            }
            if (testResult.getStderr() != null && testResult.getStderr().length() != 0) {
                sqlStr.append(", stderr");
                sqlValueStr.append(", ?");
            }
            sqlStr.append(")");
            sqlStr.append(sqlValueStr.toString());
            sqlStr.append(")");

            ps = cnn.prepareStatement(sqlStr.toString());
            ps.setInt(1, testId);
            ps.setBoolean(2, testResult.isSuccess());
            ps.setDouble(3, testResult.getScore());
            ps.setLong(4, testResult.getTime());
            ps.setTimestamp(5, newTimeStamp());
            ps.setLong(6, testResult.getPeakMemoryUsed());
            int i = 7;
            i = DBUtils.setIfNotEmptyOrNull(ps, i, testResult.getMessage());
            i = DBUtils.setIfNotEmptyOrNull(ps, i, testResult.getStdout());
            i = DBUtils.setIfNotEmptyOrNull(ps, i, testResult.getStderr());
            ps.executeUpdate();
        } finally {
            DBMS.close(ps);
        }
    }
    
    /**
     * Update test group with information about the last executed/cancelled test case
     * 
     * @param testGroupId Id of the group
     * @param testCaseId Id of the test case 
     * @param cnn Connection to use
     * 
     * @throws SQLException If a SQLException is thrown in the process
     */ 
    public void updateLastTestExecutedInGroup(int testGroupId, int testCaseId, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        try {
            StringBuffer sqlStr = new StringBuffer(100);
            sqlStr.append("UPDATE {+AVOID_FULL(long_test_group)} long_test_group");
            sqlStr.append("    SET pending_tests = pending_tests - 1,");
            sqlStr.append("        last_ltc_id = ?");
            sqlStr.append("    WHERE ltg_id = ?");
            
            ps = cnn.prepareStatement(sqlStr.toString());
            ps.setInt(1, testCaseId);
            ps.setInt(2, testGroupId);
            ps.executeUpdate();
        } finally {
            DBMS.close(ps);
        }
    }
    
    /**
     * Sets the test group status as completed if no pendings tests exists and the last
     * test executed/cancelled was <code>lastTestId</code>
     * 
     * @param testGroupId Id of the test group
     * @param lastTestId Id of the last test
     * 
     * @param cnn Connection to use 
     * 
     * @return true if the status was changed to completed
     * 
     * @throws SQLException If a SQLException is thrown in the process
     */
    public boolean updateGroupAsCompletedIfNecessary(int testGroupId, int lastTestId, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        try {
            StringBuffer sqlStr = new StringBuffer(100);
            sqlStr.append("UPDATE {+AVOID_FULL(long_test_group)} long_test_group");
            sqlStr.append("  SET status_id = ?, status_date = ?");
            sqlStr.append("  WHERE ltg_id = ? AND pending_tests = 0 AND last_ltc_id = ? AND status_id = ?");
            
            ps = cnn.prepareStatement(sqlStr.toString());
            ps.setInt(1, LongTestGroup.STATUS_COMPLETED);
            ps.setTimestamp(2, newTimeStamp());
            ps.setInt(3, testGroupId);
            ps.setInt(4, lastTestId);
            ps.setInt(5, LongTestGroup.STATUS_PENDING);
            return ps.executeUpdate() == 1;
        } finally {
            DBMS.close(ps);
        }
    }

    /**
     * Set a new status to all existing test cases of a test group with status equal to <code>currentStatus</code> 
     * 
     * @param testGroupId Id of the test group
     * @param currentStatus Status that the test must have to change take effect
     * @param newStatus New status to set to the test cases
     * @param cnn Connection to use 
     * 
     * @throws SQLException If a SQLException is thrown in the process
     */
    public void updateTestCaseStatusForGroup(int testGroupId, int currentStatus, int newStatus, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        try {
            StringBuffer sqlStr = new StringBuffer(100);
            sqlStr.append("UPDATE {+AVOID_FULL(long_test_case)} long_test_case");
            sqlStr.append("  SET status_id = ?");
            sqlStr.append("  WHERE ltg_id = ? AND status_id = ?");
            
            ps = cnn.prepareStatement(sqlStr.toString());
            ps.setInt(1, newStatus);
            ps.setInt(2, testGroupId);
            ps.setInt(3, currentStatus);
            ps.executeUpdate();
            ps.close();
        } finally {
            DBMS.close(ps);
        }
    }

    /**
     * Sets a new status to the group
     * 
     * @param groupId Id of the group
     * @param currentStatus Array with the status that the group must have, to update take effect 
     * @param newStatus new status to set
     * @param cnn Connection to use
     * 
     * @return true If the group status was updated
     * 
     * @throws SQLException If a SQLException is thrown in the process
     */
    public boolean updateTestGroupStatus(int testGroupId, int[] currentStatus, int newStatus, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        try {
            //OPT HINT, avoid full scan use index to access 
            StringBuffer sqlStr = new StringBuffer(100);
            sqlStr.append("UPDATE {+AVOID_FULL(long_test_group)} long_test_group");
            sqlStr.append("  SET status_id = ?");
            sqlStr.append("  WHERE ltg_id = ?");
            sqlStr.append("        AND ").append(DBUtils.sqlStrInList("status_id", currentStatus));
            
            ps = cnn.prepareStatement(sqlStr.toString());
            ps.setInt(1, newStatus);
            ps.setInt(2, testGroupId);
            return ps.executeUpdate() == 1;
        } finally {
            DBMS.close(ps);
        }
    }
    
    /**
     * Delete all test group related
     * 
     * @param testGroupId Id of the test group to delete
     * @param cnn Connection to use
     * 
     * @throws SQLException If a SQLException is thrown in the process
     */
    public void deleteTestGroup(final int testGroupId, Connection cnn) throws SQLException {
        try {
           DBUtils.invoke(cnn, new DBUtils.UnitOfWork() {
                public Object doWork(Connection cnn) throws SQLException {
                    PreparedStatement ps = null;
                    try {
                        deleteFromTestCaseChildForGroup(testGroupId, "long_test_result", cnn);
            
                        StringBuffer sqlStr = new StringBuffer(100);
                        sqlStr.append("DELETE {+AVOID_FULL(long_test_case)} FROM long_test_case "); 
                        sqlStr.append("  WHERE ltg_id = ?");
                        ps = cnn.prepareStatement(sqlStr.toString());
                        ps.setInt(1, testGroupId);
                        ps.executeUpdate();
                        
                        sqlStr.setLength(0);
                        sqlStr.append("DELETE {+AVOID_FULL(long_test_group)} FROM long_test_group "); 
                        sqlStr.append("  WHERE ltg_id = ?");
                        ps = cnn.prepareStatement(sqlStr.toString());
                        ps.setInt(1, testGroupId);
                        ps.executeUpdate();
                        return null;
                    } finally {
                        DBMS.close(ps);
                    }
                }
            });
        } catch (Exception e) {
            handleException(e);
        } 
    }

    /**
     * Deletes from the table all records related to test case with id = testCaseId 
     */
    private void deleteFromTestCaseChildForGroup(int testCaseId, String tableName, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        try {
            StringBuffer sqlStr = new StringBuffer(150);
            sqlStr.append("DELETE {+AVOID_FULL("+tableName+")} FROM ").append(tableName);
            sqlStr.append("  WHERE ltc_id IN (SELECT {+AVOID_FULL(long_test_case)} ltc_id FROM long_test_case where ltg_id = ?)");
            ps = cnn.prepareStatement(sqlStr.toString());
            ps.setInt(1, testCaseId);
            ps.executeUpdate();
        } finally {
            DBMS.close(ps);
        }
    }
    
    /**
     * Finds all test cases and the test cases results for a test group.
     * 
     * @param testGroupId Id of the group
     * @param cnn Connection to use
     * 
     * @return a List with all test cases for the group
     * 
     * @throws SQLException If a SQLException is thrown in the process
     */
    public List findTestCasesAndResultForGroup(int testGroupId, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            List testCases = new ArrayList();
            StringBuffer sqlStr = new StringBuffer(200);
            sqlStr.append("SELECT {+AVOID_FULL(long_test_case), AVOID_FULL(long_test_result)}");
            sqlStr.append("       ltc.ltc_id, ltc.ltg_id, ltc.status_id, ltc.arg, ltc.test_case_id, ");
            sqlStr.append("       success, score, message, processing_time, stdout, stderr, peak_memory_used");
            sqlStr.append("    FROM long_test_case ltc");
            sqlStr.append("         LEFT JOIN long_test_result ltr ON ltr.ltc_id = ltc.ltc_id");
            sqlStr.append("    WHERE ltg_id = ? ORDER BY ltc.ltc_id");
            ps = cnn.prepareStatement(sqlStr.toString());
            ps.setInt(1, testGroupId);
            rs = ps.executeQuery();
            while (rs.next()) {
                LongTestCase testCase = assembleTestCase(rs);
                testCase.setResult(assembleTestResult(rs, 5));
                testCases.add(testCase);
            }
            return testCases;
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
        }
    }
    
    
    /**
     * Finds all test cases for a test group.
     * 
     * @param testGroupId Id of the group
     * @param cnn Connection to use
     * 
     * @return a List with all test cases for the group
     * 
     * @throws SQLException If a SQLException is thrown in the process
     */
    public List findTestCasesForGroup(int testGroupId, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            List testCases = new ArrayList();
            StringBuffer sqlStr = new StringBuffer(200);
            sqlStr.append("SELECT {+AVOID_FULL(long_test_case)}");
            sqlStr.append("       ltc.ltc_id, ltc.ltg_id, ltc.status_id, ltc.arg, ltc.test_case_id ");
            sqlStr.append("    FROM long_test_case ltc");
            sqlStr.append("    WHERE ltg_id = ? ORDER BY ltc.ltc_id");
            ps = cnn.prepareStatement(sqlStr.toString());
            ps.setInt(1, testGroupId);
            rs = ps.executeQuery();
            while (rs.next()) {
                LongTestCase testCase = assembleTestCase(rs);
                testCases.add(testCase);
            }
            return testCases;
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
        }
    }
    
    /**
     * Increments pending_tests of a test group by testCountToAdd
     */
    private void addPendingTests(int groupId, int testCountToAdd, Connection cnn) throws SQLException {
        PreparedStatement ps;
        String sqlStr = "UPDATE {+AVOID_FULL(long_test_group)}long_test_group " +
                            "SET pending_tests = pending_tests + ? WHERE ltg_id = ?";
        ps = cnn.prepareStatement(sqlStr);
        ps.setInt(1, testCountToAdd);
        ps.setInt(2, groupId);
        ps.executeUpdate();
    }

    
    /**
     * Assembles a LongTestCase from a resulset
     * Field order:
     * id, groupId, status, arg, systemTestId
     */
    private LongTestCase assembleTestCase(ResultSet rs) throws SQLException {
        LongTestCase testCase = new LongTestCase();
        testCase.setId(rs.getInt(1));
        testCase.setGroupId(rs.getInt(2));
        testCase.setStatus(rs.getInt(3));
        testCase.setArg(rs.getString(4));
        testCase.setSystemTestId(DBUtils.getInt(rs, 5));
        return testCase;
    }
    
    /**
     * Assembles a LongTestCaseResult from a resulset, starting at column startIndex+1
     * If success is null, returns null
     * Field order:
     * success, score, message, processingTime, stdout, stderr
     */
    private LongTestCaseResult assembleTestResult(ResultSet rs, int startIndex) throws SQLException {
        Boolean success = DBUtils.getBoolean(rs, 1+startIndex);
        if (success == null) {
            return null;
        }
        return new LongTestCaseResult(success.booleanValue(), DBUtils.getDouble(rs, 2+startIndex), 
                                      DBUtils.getStringEmpty(rs, 3+startIndex), 
                                      DBUtils.getLong(rs, 4+startIndex), 
                                      DBUtils.getStringEmpty(rs, 5+startIndex), 
                                      DBUtils.getStringEmpty(rs, 6+startIndex),
                                      DBUtils.getLong(rs, 7+startIndex));
    }
    /**
     * Assembles a LongTestGroup from a resulset
     * Field order:
     * id, codeType, status, componentId, solutionId, pendingTests
     */
    private LongTestGroup assembleTestGroup(ResultSet rs) throws SQLException {
        LongTestGroup testGroup = new LongTestGroup();
        testGroup.setId(rs.getInt(1));
        testGroup.setCodeType(rs.getInt(2));
        testGroup.setStatus(rs.getInt(3));
        testGroup.setComponentId(rs.getInt(4));
        testGroup.setSolutionId(DBUtils.getInt(rs, 5));
        testGroup.setPendingTests(rs.getInt(6));
        return testGroup;
    }
    
    /**
     * Helper, receives an exception and rethrows it as a SQLException or a RuntimeException  
     */
    private void handleException(Exception e) throws SQLException {
        if (e instanceof SQLException) {
            throw (SQLException) e;
        } if (e instanceof IDGenerationException) {
            throw (SQLException) new SQLException("ID generation exception").initCause(e);
        } else {
            throw (RuntimeException) e;
        }
    }
}
