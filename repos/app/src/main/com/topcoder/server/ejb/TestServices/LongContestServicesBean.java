/*
 * Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
 */
/* 
 * LongContestServicesBean
 * 
 * Created 05/14/2007
 */
package com.topcoder.server.ejb.TestServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.topcoder.arena.code.CodeService;
import com.topcoder.farm.client.util.HierarchicalIdBuilder;
import com.topcoder.farm.controller.configuration.ApplicationContextProvider;
import com.topcoder.farm.processor.api.CodeProcessingRequest;
import com.topcoder.farm.processor.api.CodeProcessingRequestMetadata.ActionType;
import com.topcoder.farm.processor.api.CodeProcessingRequestMetadata.AppType;
import com.topcoder.farm.processor.api.CodeProcessingResult;
import com.topcoder.farm.shared.util.LRUCache;
import com.topcoder.farm.shared.util.concurrent.ReadWriteLock.Lock;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.common.CodeUtil;
import com.topcoder.server.common.LongSubmissionId;
import com.topcoder.server.common.RemoteFile;
import com.topcoder.server.common.Results;
import com.topcoder.server.common.ServerContestConstants;
import com.topcoder.server.ejb.GenericCounter;
import com.topcoder.server.ejb.DBServices.DBServices;
import com.topcoder.server.ejb.DBServices.DBServicesLocator;
import com.topcoder.server.ejb.ProblemServices.ProblemServicesLocator;
import com.topcoder.server.ejb.TestServices.longtest.LongTestEmailBodyBuilder;
import com.topcoder.server.ejb.dao.ComponentDao;
import com.topcoder.server.ejb.dao.RoundDao;
import com.topcoder.server.ejb.dao.SolutionDao;
import com.topcoder.server.ejb.dao.UserDao;
import com.topcoder.server.farm.compiler.CompilerInvoker;
import com.topcoder.server.farm.longtester.LongTesterException;
import com.topcoder.server.farm.longtester.LongTesterInvoker;
import com.topcoder.server.farm.longtester.LongTesterSummaryItem;
import com.topcoder.server.farm.longtester.MarathonCodeCompileRequest;
import com.topcoder.server.farm.longtester.MarathonCodeProcessingResultHandler;
import com.topcoder.server.services.CoreServices;
import com.topcoder.server.tester.CodeCompilation;
import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.server.tester.LongSubmission;
import com.topcoder.server.tester.Solution;
import com.topcoder.server.util.ArrayUtils;
import com.topcoder.server.util.DBUtils;
import com.topcoder.services.tester.common.LongTestAttributes;
import com.topcoder.services.tester.common.LongTestResults;
import com.topcoder.shared.common.LongRoundScores;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.language.BaseLanguage;
import com.topcoder.shared.language.Language;
import com.topcoder.shared.messaging.messages.LongCompileRequest;
import com.topcoder.shared.messaging.messages.LongCompileResponse;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.ProblemCustomSettings;
import com.topcoder.shared.problem.SimpleComponent;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.EmailEngine;
import com.topcoder.shared.util.TCSEmailMessage;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.utilities.LongRecalcPlaced;

/**
 * Test services for long contest rounds. <p>
 * 
 * Code in this class is the result of splitting TestServicesBean class.
 * </p>
 * 
 * <p>
 *  Version 1.1 (TC Competition Engine Code Execution Time Issue) change notes:
 *  <ul>
 *      <li>Update {@link #queueLongTestCases(int, long, int, boolean)}, {@link #startLongSystemTests(int)},
 *      {@link #queueLongSystemTestCase(int, long[])} to use the execution time limit for each problem.</li>
 *  </ul> 
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TC Competition Engine - CPP Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Update {@link #queueLongTestCases(int, long, int, boolean)}, {@link #startLongSystemTests(int)},
 *      {@link #queueLongSystemTestCase(int, int[], long[])} to use the approved path for each problem.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TC Competition Engine - Python Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Update {@link #queueLongTestCases(int, long, int, boolean)}, {@link #startLongSystemTests(int)},
 *      {@link #queueLongSystemTestCase(int, int[], long[])} to use the python approved path for each problem.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (TopCoder Competition Engine Improvement Series 1):
 * <ol>
 *      <li>Add {@link #LastSubmissionBean} class.</li>
 *      <li>Update {@link #getLastSubmitTime(int,int,int,boolean,Connection)} method to return LastSubmissionBean.</li>
 *      <li>Update {@link #getRemainingSubmissionTime(int, int, int, boolean,Connection)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.5 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #queueLongTestCases(int, long, int, boolean)}, {@link #startLongSystemTests(int)},
 *      {@link #queueLongSystemTestCase(int, int[], long[])} to use the approved path for each problem.</li>
 *      <li>Update {@link #startLongSystemTests(final int roundId)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.6 (Return Peak Memory Usage for Marathon Match Cpp v1.0):
 * <ol>
 *      <li>Update {@link #recordLongTestResult(LongSubmissionId, long, int, LongTestResults)} method.</li>
 *      <li>Update {@link #getLongTestResults(int roundId, int coderId, int componentId, int resultType)} method.</li>
 * </ol>
 * </p>
 * @autor Diego Belfer (Mural), savon_cn, gevak, TCSASSEMBLER
 * @version 1.6
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class LongContestServicesBean implements LongContestServices {
    private static final String GENERIC_COUNTER_CLIENT_ID = "LongContestServices#Test";
    private static final Logger s_trace = Logger.getLogger(LongContestServicesBean.class);
    private static final ComponentDao componentDao = new ComponentDao();
    private static final UserDao userDao = new UserDao();
    private static final RoundDao roundDao = new RoundDao();
    private static final SolutionDao solutionDao = new SolutionDao();
    private static final Map longComponentStateIdCache = Collections.synchronizedMap(new LRUCache(1500));
    
    private static final char TYPE_ROUND = 'R';
    private static final char TYPE_COMPONENT = 'C';
    private static final char TYPE_CODER = 'U';
    
    /**
     * Event notificator used to notify Long Contest events.
     */
    private static LongContestServiceEventNotificator eventPublisher;
    /**
     * Test counter object used to detect the end of the test for a submission
     */
    private static GenericCounter testCounter;
    /**
     * Locks used to avoid simultaneous operations on the same component state
     */
    private static final ComponentStateRWLockManager componentLock = new ComponentStateRWLockManager();
    /**
     * mutex for result id generation
     */
    private static final Object resultGenerationIdMutex = new Object();
    /**
     * mutex for result updates
     */
    //TODO different mutexes for different rounds
    static final Object updateResultsMutex = new Object();
    /**
     * result generation id suffix
     */
    private static long resultGenerationId = 0;
    

//    private static final LongSubmitter longSubmitter = new LongSubmitter("TestSvc");
    
    /**
     * Caching queue status to reduce load. 
     */
    private static final Object queueStatusMutex = new Object();
    private static long queueStatusTimeout;
    private static List questStatusCachedValue;

    
    private ComponentFiles getLongComponentFiles(int contestId, int roundId, int componentId, int coderId, boolean example, Connection conn) throws SQLException {
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("In getLongComponentFiles(), contestId=" + contestId + ", roundId=" + roundId + ", componentId=" + componentId +
                    ", coderId=" + coderId + ", example=" + example);
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(200);
    
        sqlStr.append("SELECT c.class_name, s.language_id, scf.path,scf.class_file,scf.sort_order ");
        sqlStr.append("FROM long_submission s, long_submission_class_file scf, long_component_state cs, component c ");
        sqlStr.append("WHERE cs.round_id = ? ");
        sqlStr.append("AND cs.long_component_state_id = scf.long_component_state_id ");
        sqlStr.append("AND cs.component_id = c.component_id ");
        sqlStr.append("AND s.long_component_state_id = cs.long_component_state_id ");
        sqlStr.append("AND cs.component_id = ? ");
        sqlStr.append("AND cs.coder_id = ? ");
        if(!example)
            sqlStr.append("AND scf.submission_number = cs.submission_number AND scf.example = 0 ");
        else
            sqlStr.append("AND scf.submission_number = cs.example_submission_number AND scf.example = 1 ");
        sqlStr.append("AND s.submission_number = scf.submission_number ");
        sqlStr.append("AND s.example = scf.example ");
        sqlStr.append("ORDER BY scf.sort_order ");
    
    
        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            ps.setInt(2, componentId);
            ps.setInt(3, coderId);
    
            rs = ps.executeQuery();
            if (rs.next()) {
                String className = rs.getString(1);
                int languageID = rs.getInt(2);
                ComponentFiles componentFiles = ComponentFiles.getInstance(languageID, coderId, contestId, roundId, componentId, className);
                do {
                    String path = rs.getString(3);
                    byte[] clazzBytes = rs.getBytes(4);
                    componentFiles.addClassFile(path, clazzBytes);
                } while (rs.next());
                s_trace.debug("Loaded componentFiles: " + componentFiles);
                return componentFiles;
            } else {
                throw new RuntimeException("Record not found while looking for component files roundId = " + roundId + " componentId = " + componentId +
                        " coderId = " + coderId + " example = " + example);
            }
        } finally {
            DBMS.close(ps, rs);
        }
    }

    private void recordLongCompileStatus(CodeCompilation sub) throws SQLException {
        s_trace.debug("recordCompileStatus");
        int roundId = sub.getRoundID();
        int componentId = sub.getComponentID();
        int coderId = sub.getCoderID();
        try {
            Connection conn = DBUtils.initDBBlock();
            long componentStateID = getLongComponentStateID(coderId, roundId, componentId, conn);
            saveLongCompilation(conn, sub.getProgramText(), componentStateID, sub.getLanguage());
        } finally {
            DBUtils.endDBBlock();
        }
    }

    private void saveLongCompilation(Connection con, String programText, long componentStateID, int languageID) throws SQLException {
        StringBuilder sqlStr = new StringBuilder();
        PreparedStatement ps = null;
        sqlStr.append("UPDATE long_compilation SET compilation_text = ?, language_id = ?");
        sqlStr.append("WHERE long_component_state_id = ?");
        try {
            ps = con.prepareStatement(sqlStr.toString());
            ps.setBytes(1, programText.getBytes());
            ps.setLong(2, languageID);
            ps.setLong(3, componentStateID);
            ps.executeUpdate();
        } finally {
            DBMS.close(ps);
        }
    }

    private void saveLongClassFiles(Connection con, long componentStateID, List classFiles, int submissionNumber, boolean example) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("INSERT INTO long_submission_class_file (long_component_state_id,sort_order,path,class_file, example, submission_number) VALUES (?,?,?,?,?,?)");
            ps.setLong(1, componentStateID);
            int k = 1;
            for (Iterator it = classFiles.iterator(); it.hasNext();) {
                RemoteFile file = (RemoteFile) it.next();
                s_trace.debug("saving " + file.getPath());
                ps.setInt(2, k++);
                ps.setString(3, file.getPath());
                ps.setBytes(4, file.getContents());
                ps.setInt(5, example ? 1 : 0);
                ps.setInt(6, submissionNumber+1);
                ps.executeUpdate();
                s_trace.debug("saved ");
            }
        } finally {
            DBMS.close(ps);
        }
    }

    private long getLongComponentStateID(long coderId, long roundId, long componentId, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String cacheKey = coderId+"."+roundId+"."+componentId;
            Long id = (Long) longComponentStateIdCache.get(cacheKey);
            if (id != null) {
                return id.longValue();
            }
            String cmd = "SELECT long_component_state_id FROM long_component_state " +
                         "  WHERE round_id = ? AND component_id = ? AND coder_id = ?";
            ps = conn.prepareStatement(cmd);
            ps.setLong(1, roundId);
            ps.setLong(2, componentId);
            ps.setLong(3, coderId);
            rs = ps.executeQuery();
            if(!rs.next())return -1;
            long result = rs.getLong(1);
            longComponentStateIdCache.put(cacheKey, new Long(result));
            return result;
        } finally {
            DBMS.close(ps, rs);
        }
    }

    private void saveLongComponent(long contestId, long roundId, long componentId, long coderId, String programText, int languageID) throws SQLException {
        s_trace.debug("Saving long component cID:" + coderId + ",rID:" + roundId + ",pID:" + componentId + ",txt:" + programText +",lang:" + languageID);
        PreparedStatement ps = null;
        try {
            Connection conn = DBUtils.initDBBlock();
            long componentStateID = getLongComponentStateID(coderId, roundId, componentId, conn);
            if (componentStateID == -1) {
                throw new RuntimeException("no problem state");
            }
            String sqlCmd = "UPDATE long_compilation SET compilation_text = ?, language_id = ? WHERE long_component_state_id = ?";
            ps = conn.prepareStatement(sqlCmd.toString());
            ps.setBytes(1, programText.getBytes());
            ps.setInt(2, languageID);
            ps.setLong(3, componentStateID);
        
            int rows = ps.executeUpdate();
            if (rows != 1) {
                throw new RuntimeException("long_compilation missing");
            } 
        } finally {
            DBMS.close(ps);
            DBUtils.endDBBlock();
        }
    }
    
    private void updateLongComponentState(Connection conn, long componentStateID, int numSubmissions, boolean example) throws SQLException {
        PreparedStatement ps = null;
        StringBuilder sqlStr = new StringBuilder();
        if(example) {
            sqlStr.replace(0, sqlStr.length(), "UPDATE long_component_state SET status_id = ?, example_submission_number = ? ");
            sqlStr.append("WHERE long_component_state_id = ?");
        } else {
            sqlStr.replace(0, sqlStr.length(), "UPDATE long_component_state SET points = ?, status_id = ?, submission_number = ? ");
            sqlStr.append("WHERE long_component_state_id = ?");
        }
        try {
            ps = conn.prepareStatement(sqlStr.toString());
            int idx = 0;
            if (!example) {
                idx = 1;
                ps.setDouble(1, 0);
            }
            ps.setInt(1+idx, ContestConstants.NOT_CHALLENGED);
            ps.setInt(2+idx, numSubmissions+1);
            ps.setLong(3+idx, componentStateID);
            ps.executeUpdate();
        } finally {
            DBMS.close(ps);
        }
    }

    private void saveLongSubmission(Connection conn, long componentStateID, int numSubmissions, String programText, long currentTime, int languageID, boolean example) throws SQLException {
        s_trace.debug("saveLongSubmission("+componentStateID+" "+numSubmissions+" "+languageID+")");
        PreparedStatement ps = null;
        StringBuilder sqlStr = new StringBuilder();
        sqlStr.replace(0, sqlStr.length(), "INSERT INTO long_submission (long_component_state_id, submission_number, ");
        sqlStr.append("submission_text, open_time, submit_time, submission_points, language_id, example) ");
        sqlStr.append("VALUES (?,?,?,?,?,?,?, ?)");
    
        try {
            ps = conn.prepareStatement(sqlStr.toString());
            //int idx = 1;
            ps.setLong(1, componentStateID);
            ps.setInt(2, numSubmissions + 1);
            ps.setBytes(3, programText.getBytes());
            ps.setLong(4, getLongOpenTime(conn, componentStateID));
            ps.setLong(5, currentTime);
            ps.setDouble(6, 0);
            ps.setInt(7, languageID);
            ps.setInt(8, example ? 1 : 0);
            ps.execute();
            s_trace.debug("saved");
        } finally {
            DBMS.close(ps);
        }
    }

    private int getLongNumSubmissions(Connection conn, long componentStateID, boolean example) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql;
        if(example)
            sql = "SELECT example_submission_number FROM long_component_state WHERE long_component_state_id = ?";
        else
            sql = "SELECT submission_number FROM long_component_state WHERE long_component_state_id = ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setLong(1, componentStateID);
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } finally {
            DBMS.close(ps, rs);
        }
    }

    private void clearLongClassFiles(Connection conn, int roundId, long coderId, int componentId, long componentStateID, int numSubmissions, boolean example) throws SQLException, LongTesterException {
        s_trace.debug("clearLongClassFiles");
    
        PreparedStatement ps = null;
        StringBuilder sqlStr = new StringBuilder();
        sqlStr.replace(0, sqlStr.length(), "DELETE FROM long_submission_class_file ");
        sqlStr.append(" WHERE long_component_state_id = ? AND submission_number = ? AND example = ? ");
        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setLong(1, componentStateID);
            ps.setInt(2, numSubmissions);
            ps.setInt(3, example ? 1 : 0);
            ps.executeUpdate();
    
            ps.close();
    
            updateLongComponentStateStatus(componentStateID, ContestConstants.NOT_CHALLENGED, conn);
        } finally {
            DBMS.close(ps);
        }
    }

    private static long getLongOpenTime(Connection connection, long componentStateID) throws SQLException {
        //String sql = "SELECT open_time FROM compilation WHERE component_state_id = ?";
        StringBuilder sqlStr = new StringBuilder();
        sqlStr.replace(0, sqlStr.length(), "SELECT cc.open_time FROM ");
        sqlStr.append("long_compilation cc, long_component_state cs ");
        sqlStr.append("WHERE cs.long_component_state_id = ? ");
        sqlStr.append("AND cs.long_component_state_id = cc.long_component_state_id");
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sqlStr.toString());
            preparedStatement.setLong(1, componentStateID);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);
        } finally {
            DBMS.close(preparedStatement, resultSet);
        }
    }

    private void storeCompiledSubmission(final LongSubmission sub) throws SQLException, LongTesterException {
        final int roundId = sub.getRoundID();
        final int coderId = sub.getCoderID();
        final int componentId = sub.getComponentID();
        final long currentTime = System.currentTimeMillis();
        
        s_trace.info("storeCompiledSubmission " +coderId + " " + roundId + " " + componentId);
        try {
            Connection conn = DBUtils.initDBBlock();
            DBUtils.invoke(conn, new DBUtils.UnitOfWork() {
                public Object doWork(Connection conn) throws SQLException, LongTesterException {
                    long componentStateID = getLongComponentStateID(coderId, roundId, componentId, conn);
                    
                    int numSubmissions = getLongNumSubmissions(conn, componentStateID, sub.isExample());
            
                    saveLongSubmission(conn, componentStateID, numSubmissions, sub.getProgramText(), currentTime, sub.getLanguage(), sub.isExample());
            
                    // Save the class files to the submission table
                    updateLongComponentState(conn, componentStateID, numSubmissions, sub.isExample());
            
                    if (sub.getClassFiles() != null) {
                        List classFiles = sub.getClassFiles().getClassFiles();
                        s_trace.debug("sub.getClassFiles() != null, size = " + classFiles.size());
                        //clearCompiledClasses(conn, componentStateID);
                        saveLongClassFiles(conn, componentStateID, classFiles,numSubmissions, sub.isExample());
                    } else {
                        s_trace.debug("sub.getClassFiles() == null");
                    }
            
                    deleteLongSystemTests(conn, roundId, coderId, componentId, sub.isExample());
                    // Lastly update room_result/coder status
                    if (!userDao.isAdminUser(coderId, conn)) {
                        updateLongCompResultToAttended(conn, coderId, roundId);
                    }
                    return null;
                }
            });
        } catch (SQLException e) {
            throw e;
        } catch (LongTesterException e) {
            throw e;
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            //This should never happen 
            throw new RuntimeException(e);
        } finally {
            DBUtils.endDBBlock();
        }
    }

    private void storeCompiledAdminResubmission(final LongSubmission sub) throws SQLException, LongContestServicesException {
        final int roundId = sub.getRoundID();
        final int coderId = sub.getCoderID();
        final int componentId = sub.getComponentID();
        s_trace.debug("storeCompiledAdminResubmission "+coderId + " " + roundId + " " + componentId);
        try {
            Connection conn = DBUtils.initDBBlock();
            DBUtils.invoke(conn, new DBUtils.UnitOfWork() {
                public Object doWork(Connection conn) throws SQLException, LongTesterException {
                    long componentStateID = getLongComponentStateID(coderId, roundId, componentId, conn);

                    int numSubmissions = getLongNumSubmissions(conn, componentStateID, sub.isExample());
    
                    clearLongClassFiles(conn, roundId, coderId, componentId, componentStateID, numSubmissions, sub.isExample());
                    if (sub.getClassFiles() != null) {
                        List classFiles = sub.getClassFiles().getClassFiles();
                        s_trace.debug("sub.getClassFiles() != null, size = " + classFiles.size());
                        //clearCompiledClasses(conn, componentStateID);
                        saveLongClassFiles(conn, componentStateID, classFiles,numSubmissions-1, sub.isExample());
                    } else {
                        s_trace.debug("sub.getClassFiles() == null");
                    }
            
                    deleteLongSystemTests(conn, roundId, coderId, componentId, sub.isExample());
                    return null;
                }
            });
        } catch (LongContestServicesException e) {
            throw e;
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw (RuntimeException) e;
        } finally {
            DBUtils.endDBBlock();
        }
    }

    private void updateLongCompResultToAttended(Connection conn, long coderId, long roundId) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("update long_comp_result set attended ='Y' where round_id = ? and coder_id = ?");
            ps.setLong(1, roundId);
            ps.setLong(2, coderId);
            ps.execute();
        } finally {
            DBMS.close(ps);
        }
    }

    /**
     * Create long test result record.
     * @param id the long submission id.
     * @param testCaseId the test case id.
     * @param testAction the user test action.
     * @param results the long test results.
     * @throws LongContestServicesException if any error occur during long contest result creation.
     */
    public void recordLongTestResult(LongSubmissionId id, long testCaseId, int testAction, LongTestResults results) throws LongContestServicesException {
        s_trace.debug("recordLongTestResult");
    
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int roundId = id.getRoundId();
        int coderId = id.getCoderId();
        int componentId = id.getComponentId();
        double score = results.getScore();
        String message = results.getMessage();
        long time = results.getTime();
        boolean example = id.isExample();
        String stdout = results.getStdout();
        String stderr = results.getStderr();
        long peakMemoryUsed = results.getPeakMemoryUsed();
    
        if (s_trace.isDebugEnabled()) {
			s_trace.debug(coderId + " " + roundId + " " + componentId+" "+testCaseId+" "+score);
        }
    
        try {
            conn = DBMS.getConnection();
    
            long componentStateID = getLongComponentStateID(coderId, roundId, componentId, conn);
    
            int submissionNumber = id.getSubmissionNumber();
            String cmd ="INSERT INTO long_system_test_result (coder_id, round_id, " +
                        "component_id, test_case_id, score, fatal_errors, processing_time, submission_number, example, test_action, peak_memory_used) "+
                        "values (?,?,?,?,?,?,?,?,?,?,?)";
            ps = conn.prepareStatement(cmd);
            ps.setInt(1, coderId);
            ps.setInt(2, roundId);
            ps.setInt(3, componentId);
            ps.setLong(4, testCaseId);
            if (Double.isNaN(score)) {
                s_trace.warn("NaN received as score: " + coderId + " " + roundId + " " + componentId+" "+testCaseId+" "+score);
            }
            if (Double.isInfinite(score)) {
                s_trace.warn("Infinite received as score: " + coderId + " " + roundId + " " + componentId+" "+testCaseId+" "+score);
            }
            ps.setDouble(5,score);
            if(message != null) {
                ps.setBytes(6, DBMS.serializeBlobObject(message));
            } else {
                ps.setNull(6, Types.BINARY);
            }
            //ps.setBoolean(7, succeeded);
            ps.setLong(7, time);
            ps.setInt(8, submissionNumber);
            ps.setInt(9, example ? 1 : 0);
            ps.setInt(10, testAction);
            ps.setLong(11, peakMemoryUsed);
    
            ps.execute();
            ps.close();
    
            if (results.getResultObject() != null) {
                cmd = "INSERT INTO long_system_test_data (coder_id, round_id, component_id, test_case_id, submission_number, example, data, test_action) " +
                      "   VALUES (?,?,?,?,?,?,?,?)";
                ps = conn.prepareStatement(cmd);
                ps.setInt(1, coderId);
                ps.setInt(2, roundId);
                ps.setInt(3, componentId);
                ps.setLong(4, testCaseId);
                ps.setInt(5, submissionNumber);
                ps.setInt(6, example ? 1 : 0);
                ps.setBytes(7, DBMS.serializeBlobObject(results.getResultObject()));
                ps.setInt(8, testAction);
                ps.execute();
                ps.close();
            }
            //insert output if needed
            if ( (example | LongContestProperties.LONG_STORE_OUTPUT_NONEXAMPLE) && testAction != ServicesConstants.LONG_SYSTEM_TEST_ACTION) {
                cmd = "INSERT INTO long_system_test_output (coder_id, round_id, " +
                      "component_id, test_case_id, stdout, stderr, submission_number, example) " +
                      "VALUES (?,?,?,?,?,?,?,?) ";
                ps = conn.prepareStatement(cmd);
    
                ps.setInt(1, coderId);
                ps.setInt(2, roundId);
                ps.setInt(3, componentId);
                ps.setLong(4, testCaseId);
                if(stdout != null && !stdout.trim().equals("")) {
                    ps.setString(5, stdout);
                } else {
                    ps.setNull(5, Types.BINARY);
                }
                if(stderr != null && !stderr.trim().equals("")) {
                    ps.setString(6, stderr);
                } else {
                    ps.setNull(6, Types.BINARY);
                }
    
                ps.setInt(7, submissionNumber);
                ps.setInt(8, example ? 1 : 0);
    
                ps.execute();
                ps.close();
            }
    
            if (testAction != ServicesConstants.LONG_SYSTEM_TEST_ACTION) {
                boolean lastTest = updateCounterWithLastTest(componentStateID, testCaseId, conn);
                if (lastTest) {
                    if (s_trace.isDebugEnabled()) {
                        s_trace.debug("Last test for user "+coderId);
                    }
                    if (updateLongComponentStateStatus(componentStateID, ContestConstants.SYSTEM_TEST_SUCCEEDED, ContestConstants.NOT_CHALLENGED, conn)) {
                        getEventPublisher().notifyTestCompleted(roundId, coderId, componentId, submissionNumber, example);
                        if (!example) {
                            calculateScoreOnSubmissionTested(id, conn);
                        } else {
                            sendNotificationEmail(id, null, conn);
                        }
                    }
                }
            } else {
                processEndOfLongSystemTest(id, componentStateID, testCaseId, conn);
            }
            s_trace.debug("recordLongTestResult end");
        } catch (Exception e) {
            printException(e);
            throw new LongContestServicesException("INTERNAL_SERVER_ERROR");
        } finally {
            DBMS.close(conn, ps, rs);
        }
    }

    private void calculateScoreOnSubmissionTested(LongSubmissionId id, Connection conn) throws SQLException, LongTesterException {
        try {
            int roundType = getRoundTypeByComponentId(id.getComponentId());
            LongRoundScores longTestResults = getLongTestResults(id.getComponentId(), id.getRoundId(), ServicesConstants.LONG_TEST_ACTION, conn);
            Solution solution = solutionDao.getComponentSolution(id.getComponentId(), conn);

            TestInvoker.getInstance().calculateTestScores(id, solution, longTestResults, roundType);
        } catch (Exception e) {
            s_trace.error("Failed to schedule score calculation for id="+id+". Continuing...", e);
        }
    }

    private void initCounterWithTests(long componentStateId, int testCaseCount, Connection conn) throws SQLException {
        getTestCounter(conn).initCounter(buildTestCounterId(componentStateId), testCaseCount, conn);
    }

    private boolean updateCounterWithLastTest(long longComponentStateId, long testCaseId, Connection conn) throws SQLException {
        long st = System.currentTimeMillis();
        boolean result = getTestCounter(conn).decrementCounter(buildTestCounterId(longComponentStateId), 1, String.valueOf(testCaseId), conn);
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("updateCounterWithLastTest took "+(System.currentTimeMillis() - st)+"ms");
        }
        return result;
    }

    private void addTestCasesToCounter(long componentStateId, int testCasesCount, Connection conn) throws SQLException {
        getTestCounter(conn).incrementCounter(buildTestCounterId(componentStateId), testCasesCount, conn);
    }
    
    private String buildTestCounterId(long componentStateId) {
        return String.valueOf(componentStateId);
    }

    private static GenericCounter getTestCounter(Connection cnn) throws SQLException {
        if (testCounter == null) {
            testCounter = GenericCounter.create(GENERIC_COUNTER_CLIENT_ID, cnn);
        }
        return testCounter;
    }

    private boolean sendNotificationEmail(LongSubmissionId id, Double overallScore, Connection conn) {
        try {
            if (!LongContestProperties.LONG_SENDS_MAILS) {
                return true;
            }
            if (s_trace.isDebugEnabled()) {
                s_trace.debug("Sending email notification to user "+id.getCoderId());
            }
            Object[] classNameAndProblemId = componentDao.getClassNameAndProblemIdForComponent(id.getComponentId(), conn);
            String className = (String) classNameAndProblemId[0];
            Integer problemId = (Integer) classNameAndProblemId[1];
            String[] handleAndEmail = userDao.getHandleAndEmail(id.getCoderId(), conn);
            int contestId = roundDao.getContestIdOfRound(id.getRoundId(), conn);
            String handle = handleAndEmail[0];
            String email = handleAndEmail[1];
            LongTestAttributes lt = new LongTestAttributes(id.getComponentId(), id.getRoundId(), contestId, id.getCoderId(), 0, className, null, email);
            lt.setSubmissionNumber(id.getSubmissionNumber());
            lt.setIsExample(id.isExample());
            lt.setHandle(handle);
            lt.setProblemID(problemId.intValue());
            TCSEmailMessage em = buildMessage(lt, overallScore, conn);
            s_trace.info("message for coder ID " + lt.getCoderID() + " will be sent to " + lt.getEmail());
            s_trace.debug("body :");
            s_trace.debug(em.getBody());
            EmailEngine.send(em);
            s_trace.info("Email sent");
            return true;
        } catch (Exception e) {
            s_trace.error("Email generation and sending failed!", e);
            return false;
        }
    }

    private TCSEmailMessage buildMessage(LongTestAttributes lt, Double overallScore, Connection cnn) throws Exception, SQLException {
        TCSEmailMessage em = new TCSEmailMessage();
        LongTestEmailBodyBuilder emailBuilder = new LongTestEmailBodyBuilder();
        em.setToAddress(lt.getEmail(), TCSEmailMessage.TO);
        em.setSubject("Test Complete: " + lt.getClassName() + (lt.isExample() ? " example" : "") + " submission "+lt.getSubmissionNumber());
        String body;
        if (lt.isExample()) {
            body = emailBuilder.buildExampleMessageBody(lt, getLongTestExampleScores(lt.getComponentID(), lt.getRoundID(), lt.getCoderID(), lt.getSubmissionNumber(), cnn));
        } else {
            body = emailBuilder.buildSubmissionMessageBody(lt, overallScore);
        }
        em.setBody(body);
        em.setFromAddress("competitions@topcoder.com");
        return em;
    }

    /**
     * Check if the test case was the last one for the round
     * If this test case was the last for the component state, it changes the status of
     * the component state to SYSTEM_TEST_SUCCEEDED.
     * If it was the last test for the round, it schedules system test score calculation
     */
    private boolean processEndOfLongSystemTest(LongSubmissionId id, long componentStateID, long testCaseId, Connection conn) throws SQLException, LongTesterException {
        boolean lastTest = updateCounterWithLastTest(componentStateID, testCaseId, conn);
        if (lastTest) {
            if (s_trace.isDebugEnabled()) {
                s_trace.debug("Last system test for user "+id.getCoderId());
			}
          	//If it was the last test for the component state
          	//We must update its state to SYSTEM_TEST_SUCCEEDED
          	if (updateLongComponentStateStatus(componentStateID, ContestConstants.SYSTEM_TEST_SUCCEEDED, ContestConstants.NOT_CHALLENGED, conn)) {
          	    getEventPublisher().notifySystemTestCompleted(id.getRoundId(), id.getCoderId(), id.getComponentId(), id.getSubmissionNumber());
            	if (!roundHasPendingSystemTests(id.getRoundId(), conn)) {
                	if (s_trace.isDebugEnabled()) {
                    	s_trace.debug("Last system test for round "+id.getRoundId());
	                }
					calculateSystemTestScore(id.getRoundId(), id.getComponentId(), conn);
				}
			}
		}
		return false;
    }
    
    /**
     * Returns true if all long components states of the round
     * that should be system tested have a status <> NOT_CHALLENGED
     *
     * @param roundId The id of the round
     * @param conn the connection to use
     */
    private boolean roundHasPendingSystemTests(int roundId, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        long st = System.currentTimeMillis();
        try {
            String sqlCmd = "SELECT FIRST 1 lcs.round_id FROM long_component_state lcs" +
                            "   WHERE lcs.round_id = ? AND lcs.submission_number > 0 AND " +
                            "          lcs.status_id = ?" +
                            "               AND NOT EXISTS (SELECT gu.user_id FROM group_user gu WHERE gu.user_id = lcs.coder_id " + 
                            "                      AND (group_id = "+ServerContestConstants.ADMIN_LEVEL_ONE_GROUP_ID+" OR group_id = "+ServerContestConstants.ADMIN_LEVEL_TWO_GROUP_ID+"))";

            ps = conn.prepareStatement(sqlCmd);
            ps.setInt(1, roundId);
            ps.setInt(2, ContestConstants.NOT_CHALLENGED);
            rs = ps.executeQuery();
            if (s_trace.isDebugEnabled()) {
                s_trace.debug("roundHasPendingSystemTests took "+(System.currentTimeMillis() - st)+"ms");
            }
            return (rs.next());
        } finally {
            DBMS.close(ps, rs);
        }
    }

    private long getResultsGenerationId() {
        synchronized (resultGenerationIdMutex ) {
            long value = System.currentTimeMillis();
            value &= 0xFFFFFFFFFFFFFF00L;
            if (value == (resultGenerationId & 0xFFFFFFFFFFFFFF00L)) {
                resultGenerationId++;
            } else {
               resultGenerationId = value;
            }
            return resultGenerationId;
        }
    }

    private boolean testAndSetLastResult(int roundId, long generationId, Connection cnn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = cnn.prepareStatement("SELECT round_id FROM long_test_score_id WHERE round_id = ?");
            ps.setInt(1, roundId);
            rs = ps.executeQuery();
            boolean mustInsert = !rs.next();
            rs.close();
            ps.close();
            if (mustInsert) {
                ps = cnn.prepareStatement("INSERT INTO long_test_score_id (round_id, generation_id) VALUES (?,?)");
                ps.setInt(1, roundId);
                ps.setLong(2, generationId);
            } else {
                ps = cnn.prepareStatement("UPDATE long_test_score_id SET generation_id = ? WHERE round_id = ? AND generation_id < ?");
                ps.setLong(1, generationId);
                ps.setInt(2, roundId);
                ps.setLong(3, generationId);
            }
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            printException(e);
            return false;
        } finally {
            DBMS.close(ps);
        }
    }

    private boolean testLastResult(int roundId, long generationId, Connection cnn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = cnn.prepareStatement("SELECT generation_id FROM long_test_score_id WHERE round_id = ?");
            ps.setInt(1, roundId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong(1) < generationId ;
            }
            return true;
        } catch (Exception e) {
            printException(e);
            return false;
        } finally {
            DBMS.close(ps, rs);
        }
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#updateFinalScores(com.topcoder.server.common.LongSubmissionId, com.topcoder.shared.common.LongRoundScores)
     */
    public void updateFinalScores(LongSubmissionId id, LongRoundScores lrr){
        s_trace.debug("updateFinalScores("+lrr.getComponentID()+")");

        Connection conn = null;
        PreparedStatement ps = null, ps2=null, ps3=null;
        ResultSet rs=null;

        try {
            conn = DBMS.getConnection();
            int componentID = lrr.getComponentID();
            int roundID = lrr.getRoundID();
            Double overallScore = null;
            boolean scoreUpdated = false;
            if (testLastResult(roundID, lrr.getResultGenerationId(), conn)) {
                synchronized (updateResultsMutex) {
                    if (testAndSetLastResult(roundID, lrr.getResultGenerationId(), conn)) {
                        scoreUpdated = true;
                        List coders = lrr.getCoderIds();
                        List finalScores = lrr.getFinalScores();
                        String sql = "UPDATE long_component_state SET points = ? WHERE long_component_state_id = ?";
                        String sql2 = "UPDATE long_submission SET submission_points = ? "
                            + " WHERE long_component_state_id = ? and submission_number = ? and example = 0 ";
                        String sql3 = "UPDATE long_comp_result SET point_total = "
                                + "(SELECT sum(points) "
                                + "FROM long_component_state "
                                + "WHERE round_id = ? and coder_id = ?) "
                            + "WHERE round_id = ? and coder_id = ?";
                        ps = conn.prepareStatement(sql);
                        ps2 = conn.prepareStatement(sql2);
                        ps3 = conn.prepareStatement(sql3);
                        for(int i = 0; i < coders.size(); i++){
                            int coderID = ((Integer)coders.get(i)).intValue();
                            double score = ((Double)finalScores.get(i)).doubleValue();
                            long csi = getLongComponentStateID(coderID, roundID, componentID, conn);
                            s_trace.debug(csi+" "+score);
                            ps.setDouble(1,score);
                            ps.setLong(2,csi);
                            ps.execute();
                            ps3.setInt(1,roundID);
                            ps3.setInt(2,coderID);
                            ps3.setInt(3,roundID);
                            ps3.setInt(4,coderID);
                            ps3.execute();

                            int numSubmissions = getLongNumSubmissions(conn, csi,false);
                            ps2.setDouble(1,score);
                            ps2.setLong(2,csi);
                            ps2.setInt(3,numSubmissions);
                            ps2.execute();
                        }
                    }
                }
            }
            if (!scoreUpdated) {
                s_trace.info("Discarding results, a new generated result was set.");
            }
            if (id != null) {
                getEventPublisher().notifyOverallScoreRecalculated(getScoresForRound(id.getRoundId(), id.getComponentId(), conn));
                overallScore = new Double(getScoreForRound(id.getRoundId(), id.getComponentId(), id.getCoderId(), conn));
                sendNotificationEmail(id, overallScore, conn);
                return;
            }
        } catch (Exception e) {
            printException(e);
        } finally {
            DBMS.close(ps3);
            DBMS.close(ps2);
            DBMS.close(conn, ps, rs);
        }
    }

    /**
     * Returns the overall score stored in table <code>long_component_state</code> for the user in the given round
     *
     * @param roundId The id of the Round
     * @param componentId The component id 
     * @param coderId The id of the Coder
     * @return The score obtained
     */
    private double getScoreForRound(int roundId, int componentId, int coderId, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT points FROM long_component_state WHERE round_id = ? AND component_id = ? and coder_id = ?");
            ps.setInt(1, roundId);
            ps.setInt(2, componentId);
            ps.setInt(3, coderId);
            rs = ps.executeQuery();
            rs.next();
            return rs.getDouble(1);
        } finally {
            DBMS.close(ps, rs);
        }
    }

    private LongRoundOverallScore getScoresForRound(int roundId, int componentId, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        LongRoundOverallScore scores = new LongRoundOverallScore(roundId, componentId);
        try {
            ps = conn.prepareStatement("SELECT coder_id, points FROM long_component_state WHERE round_id = ? AND component_id = ? ORDER BY coder_id");
            ps.setInt(1, roundId);
            ps.setInt(2, componentId);
            rs = ps.executeQuery();
            while (rs.next()) {
                scores.addScore(rs.getInt(1), rs.getDouble(2));
            }
            return scores;
        } finally {
            DBMS.close(ps, rs);
        }
    }
    
    /**
     * <p>
     * queue the long problem test case.
     * </p>
     * @param rid the round id.
     * @param coderId the coder id.
     * @param compid the component id.
     * @param example true=the test case is checked with example.
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#queueLongTestCases(int, long, int, boolean)
     */
    public void queueLongTestCases(int rid, long coderId, int compid, boolean example){
        int cid = (int) coderId;
        s_trace.debug("queueLongTestCases("+rid+","+cid+","+compid+")");

        try {
            //TODO we should move the mem limit out of the XML to component table
            ProblemComponent problemComponent = ProblemServicesLocator.getService().getProblemComponent(compid, true);

            PreparedStatement ps =null, ps2=null;
            ResultSet rs=null;
            
            int roundType = problemComponent.getRoundType();
            LongSubmissionId id;

            LongTesterInvoker longTesterInvoker;
            long[] testCaseIds;

            Solution solution;
            ComponentFiles componentFiles;
            try {
                Connection conn = DBUtils.initDBBlock();
                
                StringBuilder sqlStr = new StringBuilder(200);
                sqlStr.setLength(0);
                sqlStr
                        .append("SELECT cs.long_component_state_id, cs.submission_number, cs.example_submission_number, r.contest_id");
                sqlStr.append("   FROM long_component_state cs, round r");
                sqlStr.append("   WHERE cs.round_id = ? ");
                sqlStr.append("    AND cs.coder_id = ? ");
                sqlStr.append("    AND cs.component_id = ? ");
                sqlStr.append("    AND r.round_id = cs.round_id ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, rid);
                ps.setInt(2, cid);
                ps.setInt(3, compid);
                rs = ps.executeQuery();
                rs.next();
                long componentStateId = rs.getLong(1);
                int submissionNumber = rs.getInt(2);
                int exampleSubmissionNumber = rs.getInt(3);
                int subNumber = example ? exampleSubmissionNumber : submissionNumber;
                int contestId = rs.getInt(4);
                rs.close();
                ps.close();
                id = new LongSubmissionId(rid, cid, compid, example, subNumber);
                sqlStr.setLength(0);
                sqlStr.append("SELECT test_case_id, args FROM system_test_case");
                sqlStr.append(" WHERE component_id = ? and status = 1 and example_flag = ? AND system_flag = 0 ORDER BY test_number, test_case_id");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, compid);
                ps.setInt(2, example ? 1 : 0);
                rs = ps.executeQuery();
                List testCaseIdsList = new LinkedList();
                longTesterInvoker = TestInvoker.getInstance();
                while (rs.next()) {
                    int testCaseId = rs.getInt(1);
                    Object[] args = ((ArrayList) DBMS.getBlobObject(rs, 2)).toArray();
                    longTesterInvoker.storeTestCase(id, testCaseId, args);
                    testCaseIdsList.add(new Long(testCaseId));
                }
                testCaseIds = ArrayUtils.getLongArrayFromList(testCaseIdsList);
                solution = solutionDao.getComponentSolution(compid, conn);
                componentFiles = getLongComponentFiles(contestId, rid, compid, cid, example, conn);
                initCounterWithTests(componentStateId, testCaseIds.length, conn);
            } finally {
                DBMS.close(ps2);
                DBMS.close(ps, rs);
                DBUtils.endDBBlock();
            }            
            ProblemCustomSettings custom = problemComponent.getProblemCustomSettings();
            longTesterInvoker.testSubmission(id, componentFiles, solution, testCaseIds, roundType, custom);
        } catch (Exception e) {
            printException(e);
        }
    }


    private int recentSubmissions(final int rid, final long cid, final int compid, final int hours){
        s_trace.debug("recentSubmissions("+rid+","+cid+","+compid+","+hours+")");
        PreparedStatement ps =null, ps2=null;
        ResultSet rs=null;
        try {
            Connection conn = DBUtils.initDBBlock();
            long csid = getLongComponentStateID(cid, rid, compid, conn);
            if(csid == -1){
                return 0;
            }
            long currentTime = System.currentTimeMillis();

            ps = conn.prepareStatement(
                    "SELECT count(*) FROM long_submission " +
                    " WHERE long_component_state_id = ? AND" +
                    " submit_time > ?");
            ps.setLong(1,csid);
            ps.setLong(2,currentTime-hours*60*60*1000);
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (Exception e) {
            printException(e);
            return -1;
        } finally {
            DBMS.close(ps2);
            DBMS.close(ps, rs);
            DBUtils.endDBBlock();
        }
    }
    
    
    /**
     * Gets the remaining time the user must wait before making a submission 
     *  
     * @param roundId The round id
     * @param componentId The component id
     * @param coderId The coder id
     * @param example if the check must be done for example submission
     * @param cnn The connection to use
     * @return a long representing the time in ms the user must wait before submitting a (example) submission
     * 
     * @throws SQLException If any SQL error occurs.
     */
    private long getRemainingSubmissionTime(int roundId, int componentId, int coderId,
            boolean example, Connection cnn) throws SQLException {
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("getRemainingSubmissionTime("+roundId+","+componentId+","+coderId+","+example+")");
        }
        LastSubmissionBean submission = getLastSubmitTime(roundId, componentId, coderId, example, cnn);
        long lastSubmit = submission.getLastSubmitTime();
        if (lastSubmit > 0) {
            long now = System.currentTimeMillis();
            long nextSubmit;
            if (example) {
                Integer exampleSubmissionRate = submission.getExampleSubmissionRate();
                int exampleInterval = exampleSubmissionRate == null ?
                    LongContestProperties.LONG_EXAMPLE_SUBMISSION_RATE : exampleSubmissionRate * 60;
                nextSubmit = lastSubmit + exampleInterval * 1000;
            } else {
                Integer submissionRate = submission.getSubmissionRate();
                int submissionInterval = submissionRate == null ? LongContestProperties.LONG_FULL_SUBMISSION_RATE :
                    submissionRate * 60;
                nextSubmit = lastSubmit + submissionInterval * 1000;
            }
            long remaining = (nextSubmit - now);
            if (s_trace.isDebugEnabled()) {
                s_trace.debug("now " + now + " last: " + lastSubmit + " diff: " + (now-lastSubmit) + " example= " + example);
            }
            if (remaining >= 1000) {
                return remaining;
            }
        }
        return 0;
    }

    /**
     * Get the last submission information.
     *
     * @param roundId Round ID.
     * @param componentId Component ID.
     * @param coderId Coder ID.
     * @param example Flag indicating if it's an example submission.
     * @param cnn DB connection.
     * @return Last submission information.
     * @throws SQLException If any SQL error occurs.
     */
    private LastSubmissionBean getLastSubmitTime(int roundId, int componentId, int coderId,
            boolean example, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        LastSubmissionBean bean = new LastSubmissionBean();
        try {
            ps = cnn.prepareStatement(
                                "SELECT ls.submit_time, lcc.submission_rate, lcc.example_submission_rate " +
                                " FROM long_submission ls, long_component_state lcs, " +
                                " outer long_component_configuration lcc " +
                                " WHERE lcs.round_id = ? " +
                                "   AND lcs.coder_id = ? " +
                                "   AND lcs.component_id = ?" +
                                "   AND ls.long_component_state_id = lcs.long_component_state_id " +
                                "   AND lcs.component_id = lcc.component_id " +
                                "   AND example = ?" +
                                " ORDER BY submit_time DESC");
            ps.setInt(1, roundId);
            ps.setInt(2, coderId);
            ps.setInt(3, componentId);
            ps.setInt(4, example ? 1 : 0);
            rs = ps.executeQuery();
            if (!rs.next()) {
                bean.setLastSubmitTime(-1);
                return bean;
            }
            bean.setLastSubmitTime(rs.getLong(1));
            int submissionRate = rs.getInt(2);
            if (!rs.wasNull()) {
                bean.setSubmissionRate(submissionRate);
            }
            int exampleSubmissionRate = rs.getInt(3);
            if (!rs.wasNull()) {
                bean.setExampleSubmissionRate(exampleSubmissionRate);
            }
            return bean;
        } finally {
            DBMS.close(ps, rs);
        }
    }

    //This is an admin method, concurrent calls to recompileAllRound should not take place,
    //but we provide an small check, just in case.
    private static volatile boolean recompiling = false;

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#recompileAllRound(int)
     */
    public void recompileAllRound(int roundId)  throws LongContestServicesException {
        Connection conn = null;
        PreparedStatement ps =null, ps2=null;
        ResultSet rs=null;
        //We don't want two threads creating and destroying the same compiler
        if (recompiling) {
            throw new LongContestServicesException("ALREADY_RECOMPILING");
        }
        try {
            recompiling = true;
            s_trace.info("Recompiling all round: "+roundId);

            conn = DBMS.getConnection();

            ps = conn.prepareStatement(" SELECT rc.component_id, r.contest_id " +
                                       "         FROM round r, round_component rc " +
                                       "         WHERE r.round_id = ? AND rc.round_id = r.round_id");

            ps.setLong(1, roundId);
            rs = ps.executeQuery();
            rs.next();

            int componentId = rs.getInt(1);
            int contestId = rs.getInt(2);
            rs.close();
            ps.close();

            ProblemComponent problemComponent = ProblemServicesLocator.getService().getProblemComponent(componentId, false);

            ps2 = conn.prepareStatement("DELETE FROM long_submission_class_file WHERE long_component_state_id = ? and submission_number>=? and example=0");

            ps = conn.prepareStatement(
                    "SELECT a.coder_id, b.language_id, b.submission_text, a.submission_number, a.long_component_state_id" +
                    "   FROM long_component_state a, long_submission b " +
                    "   WHERE a.round_id = ? and a.submission_number > 0 and " +
                    "         b.long_component_state_id = a.long_component_state_id and b.submission_number = a.submission_number and" +
                    "         b.example = 0");
            ps.setInt(1, roundId);
            rs = ps.executeQuery();

            CompilerInvoker compiler = CompilerInvoker.create("ReRound");
            try {
                while (rs.next()) {
                    int coderId = rs.getInt(1);
                    int languageId = rs.getInt(2);
                    String text = rs.getString(3);
                    int subNum = rs.getInt(4);
                    int lcsId = rs.getInt(5);

                    LongSubmission sb = new LongSubmission(coderId, componentId, roundId, contestId, languageId, text, false);
                    LongSubmission submission = compiler.compileLongSubmission(sb, problemComponent);
                    if (submission.getCompileStatus()) {
                        s_trace.info("Submission sucessfully recompiled lcsId="+lcsId+" subNum="+subNum);
                        ps2.setInt(1, lcsId);
                        ps2.setInt(2, subNum);
                        ps2.execute();
                        recordLongCompileStatus(submission);
                        saveLongClassFiles(conn, lcsId, submission.getClassFiles().getClassFiles(), subNum-1, false);
                    } else {
                        s_trace.error("Could not recompile submission lcsId="+lcsId+" subNum="+subNum);
                        s_trace.error(submission.getCompileError());
                    }
                }
            } finally {
                compiler.releaseCompiler();
            }
        } catch (Exception e) {
            printException(e);
            throw new LongContestServicesException("INTERNAL_SERVER_ERROR");
        } finally {
            recompiling = false;
            DBMS.close(ps2);
            DBMS.close(conn, ps, rs);
        }
    }

    /**
     * Runnable class containing behaviour for system test schedule, that allows paralelims
     *
     * <p>
     * Version 1.1 (TC Competition Engine Code Execution Time Issue) change notes:
     *  <ul>
     *      <li>Added {@link #executionTimeLimit} field.</li>
     *  </ul>
     * </p>
     *
     * <p>
     * Changes in version 1.2 (TC Competition Engine - CPP Language Upgrade And Customization Support v1.0):
     * <ol>
     *      <li>Added {@link #cppApprovedPath} field.</li>
     * </ol>
     * </p>
     *
     * <p>
     * Changes in version 1.3 (TC Competition Engine - Python Language Upgrade And Customization Support v1.0):
     * <ol>
     *      <li>Added {@link #pythonCommand} field.</li>
     *      <li>Added {@link #pythonApprovedPath} field.</li>
     * </ol>
     * </p>
     *
     * <p>
     * Changes in version 1.4 (TopCoder Competition Engine Improvement Series 3 v1.0):
     * <ol>
     *      <li>Add {@link #problemCustomSettings} field.</li>
     * </ol>
     * </p>
     * @author Diego Belfer (mural), savon_cn
     * @version 1.4
     */
    class ScheduleSystemTestsTask implements Runnable {
        int offset, len;
        private List submissions;
        private LongTesterInvoker longTesterInvoker;
        private int roundId;
        private int componentId;
        private int contestId;
        private long[] testCaseIds;
        private int roundType;
        /**
         * the problem custom settings.
         * @since 1.4
         */
        private ProblemCustomSettings problemCustomSettings;
        boolean success;
        private Connection conn;
        private boolean resetTestCount;
        private Solution solution;

        public ScheduleSystemTestsTask(int offset, int len, List submissions,
        			Solution solution,
                    LongTesterInvoker longTesterInvoker, int roundId, int componentId,
                    int contestId, long[] testCaseIds, int roundType, ProblemCustomSettings custom, boolean resetTestCount, Connection conn) {

            this.offset = offset;
            this.len = len;
            this.submissions = submissions;
            this.longTesterInvoker = longTesterInvoker;
            this.roundId = roundId;
            this.componentId = componentId;
            this.contestId = contestId;
            this.testCaseIds = testCaseIds;
            this.roundType = roundType;
            this.problemCustomSettings = custom;
            this.conn = conn;
            this.resetTestCount = resetTestCount;
            this.solution = solution;
        }

        public void run() {
            try {
                s_trace.info("Scheduling tests for "+ submissions.size() + " users");
                for (int i = offset; i < len; i++) {
                    CoderSubmissionPair p = (CoderSubmissionPair) submissions.get(i);
                    LongSubmissionId id = new LongSubmissionId(roundId, p.coderId, componentId, false, p.submissionNumber);
                    ComponentFiles componentFiles = getLongComponentFiles(contestId, roundId, componentId, p.coderId, false, conn);
                    if (resetTestCount) {
                        initCounterWithTests(p.componentStateId, testCaseIds.length, conn);
                    } else {
                        addTestCasesToCounter(p.componentStateId, testCaseIds.length, conn);
                    }
                    longTesterInvoker.systemTestSubmission(id, componentFiles, testCaseIds, roundType, problemCustomSettings, solution);
                }
                success = true;
            } catch (Exception e) {
                s_trace.error("Exception scheduling system test", e);
            }
        }
    };

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#getLongTestQueueStatus()
     */
    public List getLongTestQueueStatus() throws LongContestServicesException {
        s_trace.info("Querying queue status.");
        synchronized (queueStatusMutex) {
            if (queueStatusTimeout < System.currentTimeMillis()) {
                questStatusCachedValue = uncachedQueueStatus();
                queueStatusTimeout = System.currentTimeMillis() + LongContestProperties.LONG_QUEUE_STATUS_CACHE_TIME_MS;
            }
        }
        return questStatusCachedValue;
    }
    
    private void queueStatusCacheClear() {
        synchronized (queueStatusMutex) {
            queueStatusTimeout = 0;
        }
    }


    private List uncachedQueueStatus() throws LongContestServicesException {
        s_trace.info("uncachedQueueStatus");
        PreparedStatement psRound = null;
        PreparedStatement psLanguage = null;
        ResultSet rs = null;
        Connection cnn = null;
        try {
            List status = TestInvoker.getInstance().queueStatus();
            if (status.size() == 0) {
                return status;
            }
            cnn = DBMS.getConnection();
            List results = new ArrayList(status.size());
            psRound = cnn.prepareStatement("SELECT c.name, r.name, r.round_type_id FROM contest c, round r WHERE round_id = ? AND c.contest_id = r.contest_id");
            psLanguage = cnn.prepareStatement("SELECT language_id FROM long_component_state lcs, long_submission ls " +
                                              " WHERE lcs.round_id = ? AND lcs.coder_id = ? AND ls.long_component_state_id = lcs.long_component_state_id " +
                                              "      AND ls.example = ? AND ls.submission_number = ?" );
            Map rounds = new HashMap();
            Map contests = new HashMap();
            Map roundTypes = new HashMap();
            for (Iterator it = status.iterator(); it.hasNext(); ) {
                LongTesterSummaryItem item = (LongTesterSummaryItem) it.next();
                if (item.getAction() !=  ServicesConstants.LONG_TEST_ACTION && item.getAction() !=  ServicesConstants.LONG_SYSTEM_TEST_ACTION) {
                    continue;
                } 
                if (userDao.isAdminUser(item.getCoderId(), cnn)) {
                    continue;
                }
                Integer roundId = new Integer(item.getRoundId());
                String roundName = (String) rounds.get(roundId);
                String contestName = null;
                int roundTypeID = 0;
                if (roundName == null) {
                    psRound.setInt(1, item.getRoundId());
                    rs = psRound.executeQuery();
                    rs.next();
                    contestName = rs.getString(1);
                    roundName = rs.getString(2);
                    roundTypeID = rs.getInt(3);
                    contests.put(roundId, contestName);
                    rounds.put(roundId, roundName);
                    roundTypes.put(roundId, new Integer(roundTypeID));
                    rs.close();
                } else {
                    contestName = (String) contests.get(roundId);
                    roundTypeID = ((Integer) roundTypes.get(roundId)).intValue();
                }
                psLanguage.setInt(1, item.getRoundId());
                psLanguage.setInt(2, item.getCoderId());
                psLanguage.setInt(3, item.isExample() ? 1 : 0 );
                psLanguage.setInt(4, item.getSubmissionNumber());
                rs = psLanguage.executeQuery();
                rs.next();
                String languageName = BaseLanguage.getLanguage(rs.getInt(1)).getName();
                rs.close();
                results.add(new LongTestQueueStatusItem(
                                item.getAction(),
                                item.getCoderId(),
                                item.getMinReceivedDate(),
                                contestName,
                                item.getRoundId(),
                                roundTypeID,
                                roundName,
                                languageName,
                                item.isExample() ? "Example Test" : "Full Submission",
                                item.getCount()));
            }
            return results;
        } catch (Exception e) {
            s_trace.error("Cannot obtain round or submission information",e);
            throw new LongContestServicesException("INTERNAL_SERVER_ERROR");
        } finally {
            DBMS.close(psLanguage, rs);
            DBMS.close(cnn, psRound, null);
        }
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#getNumberOfSubmissionOnLongTestQueue()
     */
    public int getNumberOfSubmissionOnLongTestQueue() throws LongContestServicesException {
        try {
            int count = 0;
            List queue = TestInvoker.getInstance().queueStatus();
            for (Iterator it = queue.iterator(); it.hasNext(); ) {
                LongTesterSummaryItem item = (LongTesterSummaryItem) it.next();
                if (item.getAction() == ServicesConstants.LONG_TEST_ACTION) {
                    count++;
                }
            }
            return count;
        } catch (LongTesterException e) {
            s_trace.error("Cannot obtain number of submissions in queue",e);
            throw new LongContestServicesException("INTERNAL_SERVER_ERROR");
        }
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#calculateSystemTestScore(int, int)
     */
    public void calculateSystemTestScore(int roundId, int componentId) throws LongContestServicesException {
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            calculateSystemTestScore(roundId, componentId, conn);
        } catch (Exception e) {
            s_trace.error("Exception thrown when trying to schedule system test score calculation.", e);
            throw new LongContestServicesException("INTERNAL_SERVER_ERROR");
        } finally {
            DBMS.close(conn);
        }
    }

    private void calculateSystemTestScore(int roundId, int componentId, Connection conn) throws SQLException, LongTesterException {
        s_trace.info("Scheduling request for long test score calculation for round="+roundId+" component="+componentId);
        LongRoundScores longTestResults = getLongTestResults(componentId, roundId, ServicesConstants.LONG_SYSTEM_TEST_ACTION, conn);
        Solution solution = solutionDao.getComponentSolution(componentId, conn);
        int roundType = getRoundTypeByComponentId(componentId);
        TestInvoker.getInstance().calculateSystemTestScores(roundId, solution, longTestResults, roundType);
    }

    private void deleteLongSystemTests(Connection conn, int roundId, int coderId, int componentId, boolean example) throws SQLException, LongTesterException {
        PreparedStatement ps = null;
        try {
    
            TestInvoker.getInstance().cancelTestsForUser(roundId, coderId);
    
            String sql = "DELETE FROM long_system_test_output WHERE round_id = ? AND coder_id = ? and component_id = ? AND example = ? ";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, roundId);
            ps.setInt(2, coderId);
            ps.setInt(3, componentId);
            ps.setInt(4, example ? 1 : 0);
    
            ps.execute();
            ps.close();
    
            sql = "DELETE FROM long_system_test_result " +
                    "WHERE round_id = ? AND coder_id = ? and component_id = ? " +
                    "AND example = ? AND test_action = "+ServicesConstants.LONG_TEST_ACTION;
            ps = conn.prepareStatement(sql);
            ps.setInt(1, roundId);
            ps.setInt(2, coderId);
            ps.setInt(3, componentId);
            ps.setInt(4, example ? 1 : 0);
    
            ps.execute();
            ps.close();
            
            sql = "DELETE FROM long_system_test_data " +
                  "WHERE round_id = ? AND coder_id = ? and component_id = ? " +
                  "AND example = ? AND test_action = "+ServicesConstants.LONG_TEST_ACTION;
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, roundId);
            ps.setInt(2, coderId);
            ps.setInt(3, componentId);
            ps.setInt(4, example ? 1 : 0);
    
            ps.execute();
        } finally {
            DBMS.close(ps);
        }
    }

    private List getLongTestExampleScores(int componentID, int roundID, int coderID, int submissionNumber, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList results = new ArrayList();
        try {
            StringBuilder sqlStr = new StringBuilder(200);
            sqlStr.append("SELECT lstr.score FROM long_system_test_result lstr, system_test_case stc");
            sqlStr.append("  WHERE lstr.test_case_id = stc.test_case_id AND lstr.component_id = ? AND lstr.round_id = ?  AND lstr.coder_id = ? AND lstr.submission_number = ?");
            sqlStr.append("        AND lstr.example = 1 AND lstr.test_action = ").append(ServicesConstants.LONG_TEST_ACTION);
            sqlStr.append(" ORDER BY stc.test_number, stc.test_case_id");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentID);
            ps.setInt(2, roundID);
            ps.setInt(3, coderID);
            ps.setInt(4, submissionNumber);
            rs = ps.executeQuery();
            while (rs.next()) {
                results.add(new Double(rs.getDouble(1)));
            }
        } finally {
            DBMS.close(ps, rs);
        }
        return results;
    }

	private LongRoundScores getLongTestResults(int componentID, int roundID, int testAction, Connection conn) throws SQLException {
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("getLongTestResults("+componentID+","+roundID+","+testAction+")");
        }
        StringBuilder msg = new StringBuilder(200);
        PreparedStatement ps = null;
        ResultSet rs = null;
    
        try {
            int cases = 0;
            ArrayList<Integer> coders = new ArrayList<Integer>();
            ArrayList<Integer> tests = new ArrayList<Integer>();
            ArrayList<List<Double>> scores = new ArrayList<List<Double>>();
            ArrayList<Double> tmp = null;
            int systemFlag = (testAction == ServicesConstants.LONG_SYSTEM_TEST_ACTION ? 1 : 0);
            //no examples, they're not scored
            msg.append("SELECT test_case_id FROM system_test_case WHERE component_id = ? AND status = 1 AND example_flag = 0 ");
            msg.append(" AND system_flag = ").append(systemFlag);
            msg.append(" ORDER BY test_number, test_case_id");
            ps = conn.prepareStatement(msg.toString());
            ps.setInt(1, componentID);
            rs = ps.executeQuery();
            while(rs.next()){
                tests.add(new Integer(rs.getInt(1)));
                cases++;
            }
            ps.close();
            rs.close();
            
            msg.replace(0,msg.length(),"SELECT str.coder_id, str.test_case_id, str.score ");
            msg.append("FROM long_system_test_result str, system_test_case stc, long_component_state cs WHERE ");
            msg.append("str.round_id = ? AND ");
            msg.append("stc.test_case_id = str.test_case_id AND ");
            msg.append("stc.status = 1 AND ");
            msg.append("stc.example_flag = 0 AND ");
            msg.append("stc.system_flag = ").append(systemFlag).append(" AND ");
            msg.append("str.component_id = ? AND ");
            msg.append("cs.coder_id = str.coder_id AND ");
            msg.append("cs.round_id = str.round_id AND ");
            msg.append("cs.component_id = str.component_id AND ");
            msg.append("str.submission_number = cs.submission_number AND ");
            msg.append("str.example = 0 AND ");
            msg.append("cs.coder_id NOT IN (SELECT user_id FROM group_user WHERE group_id IN (" + ServerContestConstants.ADMIN_LEVEL_ONE_GROUP_ID + "," + ServerContestConstants.ADMIN_LEVEL_TWO_GROUP_ID + "))");
            //If we are getting results for system test we don't use the status_id filter
            
            //rfairfax - removed status, this fixes example submissions messing up scores
            //if (testAction != ServicesConstants.LONG_SYSTEM_TEST_ACTION) {
            //    msg.append("AND cs.status_id = 150");
            //}
            msg.append("ORDER BY str.coder_id, stc.test_number, stc.test_case_id");
            ps = conn.prepareStatement(msg.toString());
            ps.setInt(1, roundID);
            ps.setInt(2, componentID);
    
            long resultsGeneratedId = getResultsGenerationId();
            rs = ps.executeQuery();
            int prevCoder = -1;
            boolean valid = false;
            int cnt = 0;
            while(rs.next()){
                int c = rs.getInt(1);
                int tc = rs.getInt(2);
                double s = rs.getDouble(3);
                s_trace.debug(c+" "+tc+" "+s+" "+valid+" "+cnt);
                if(c != prevCoder){
                    if(valid && cnt == cases){
                        coders.add(new Integer(prevCoder));
                        scores.add(tmp);
                    }
                    tmp = new ArrayList();
                    valid = true;
                    cnt = 0;
                    prevCoder = c;
                }
                if(!valid || tc != ((Integer)tests.get(cnt++)).intValue()){
                    valid = false;
                }else{
                    tmp.add(new Double(s));
                }
            }
            if(valid && cnt == cases){
                coders.add(new Integer(prevCoder));
                scores.add(tmp);
            }
            LongRoundScores lrr = new LongRoundScores(resultsGeneratedId,scores,tests,coders,componentID, roundID);
            s_trace.info("RoundScores generated for roundId="+roundID+" generatedId="+resultsGeneratedId);
            if (s_trace.isDebugEnabled()) {
                s_trace.debug(coders+" "+tests+" "+scores);
            }
            return lrr;
        } finally {
            DBMS.close(ps, rs);
        }
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#updateLongSystemTestFinalScores(com.topcoder.shared.common.LongRoundScores[])
     */
    public void updateLongSystemTestFinalScores(LongRoundScores[] lrr) throws LongContestServicesException {
        long resultGenerationId = lrr[0].getResultGenerationId();
    	int roundID = lrr[0].getRoundID();
        s_trace.debug("updateLongSystemTestFinalScores("+roundID+")");
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBMS.getConnection();
    
            if (!testLastResult(roundID, resultGenerationId, conn)) {
                s_trace.info("Discarding results, a new generated result was set.");
                return;
            }
    
            synchronized (updateResultsMutex) {
                if (!testAndSetLastResult(roundID, resultGenerationId, conn)) {
                    s_trace.info("Discarding results, a new generated result was set.");
                    return;
                }
    
                StringBuilder sqlStr = new StringBuilder(200);
    
                sqlStr.append("UPDATE long_comp_result SET system_point_total = 0 ");
                sqlStr.append("     WHERE round_id = ? AND attended ='Y'");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, roundID);
                ps.execute();
                ps.close();
    
                sqlStr.setLength(0);
                sqlStr.append("UPDATE long_component_state SET points = 0");
                sqlStr.append("     WHERE round_id = ? AND points IS NOT NULL");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, roundID);
                ps.execute();
                ps.close();
    
                for (int i = 0; i < lrr.length; i++) {
                    LongRoundScores results = lrr[i];
                    updateSystemTestFinalScoresForComponent(results, conn);
                }
    
                sqlStr.setLength(0);
                sqlStr.append("UPDATE long_comp_result SET system_point_total = ");
                sqlStr.append("    (SELECT sum(points) ");
                sqlStr.append("     FROM long_component_state lcs ");
                sqlStr.append("     WHERE lcs.round_id = long_comp_result.round_id AND");
                sqlStr.append("           lcs.coder_id = long_comp_result.coder_id) ");
                sqlStr.append(" WHERE round_id = ? AND attended ='Y'");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, roundID);
                ps.executeUpdate();
                ps.close();
                LongRecalcPlaced.recalcPlacesWithSystemTestPoints(conn, roundID);
                getEventPublisher().notifyRoundSystemTestingCompleted(roundID);
            }
        } catch (Exception e) {
            printException(e);
        } finally {
            DBMS.close(conn, ps, null);
        }
    }

	private void updateSystemTestFinalScoresForComponent(LongRoundScores lrr, Connection conn) throws SQLException {
        s_trace.debug("updateSystemTestFinalScoresForComponent(roundId="+lrr.getRoundID()+",componentId="+lrr.getComponentID()+")");
    
        PreparedStatement ps = null;
        try {
            List coders = lrr.getCoderIds();
            List finalScores = lrr.getFinalScores();
            int componentID = lrr.getComponentID();
            int roundID = lrr.getRoundID();
            String sql = "UPDATE long_component_state SET points = ? " +
                         "       WHERE round_id = ? AND coder_id = ? AND component_id = ?";
            ps = conn.prepareStatement(sql);
    
            if (s_trace.isDebugEnabled()) {
                s_trace.debug("Updating final scores of "+coders.size());
            }
            for(int i = 0;  i < coders.size(); i++){
                int coderID = ((Integer)coders.get(i)).intValue();
                double score = ((Double)finalScores.get(i)).doubleValue();
                if (s_trace.isDebugEnabled()) {
                    s_trace.debug("Setting final score of "+coderID+" to "+score);
                }
                ps.setDouble(1,score);
                ps.setInt(2,roundID);
                ps.setInt(3,coderID);
                ps.setInt(4,componentID);
                ps.execute();
            }
        } finally {
            DBMS.close(ps);
        }
    }

    private boolean queueEmpty(int rid, long cid, int compid) {
        s_trace.debug("queueEmpty("+rid+","+cid+","+compid+")");
        try {
            return TestInvoker.getInstance().isQueueEmptyForUser(rid, cid);
        } catch (Exception e) {
            printException(e);
            return false;
        }
    }

    /**
     * <p>
     * start to test the long problem
     * </p>
     * @param roundId the round id.
     * @return the execution status.
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#startLongSystemTests(int)
     */
    public int startLongSystemTests(final int roundId) throws LongContestServicesException {
        s_trace.debug("startLongSystemTests("+roundId+")");
    
        Connection conn = null;
        Connection conn1 = null;
        PreparedStatement ps =null;
        ResultSet rs=null;
    
        try {
            final LongTesterInvoker longTesterInvoker = TestInvoker.getInstance();
    
            conn = DBMS.getConnection();
            deleteLongSystemTests(roundId, conn);
            try {
    
                if (!TestInvoker.getInstance().isQueueEmptyForRound(roundId)) {
                    s_trace.warn("There are pending users test for the round." + roundId);
                    throw new RuntimeException("There are pending users test for the round.");
                }
                StringBuilder sqlStr = new StringBuilder(200);
                //We mark all component states having a real submission as NOT_CHALLENGED
                sqlStr.append("UPDATE long_component_state SET status_id = ").append(ContestConstants.NOT_CHALLENGED);
                sqlStr.append("  WHERE round_id = ? and submission_number > 0");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setLong(1, roundId);
                ps.executeUpdate();
                ps.close();
    
                sqlStr.setLength(0);
                sqlStr.append(" SELECT rc.component_id, r.contest_id ");
                sqlStr.append("         FROM round r, round_component rc ");
                sqlStr.append("         WHERE r.round_id = ? AND rc.round_id = r.round_id");
    
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setLong(1, roundId);
                rs = ps.executeQuery();
                rs.next();

                final int componentId = rs.getInt(1);
                final int contestId = rs.getInt(2);
                rs.close();
                ps.close();
    
                ProblemComponent problemComponent = ProblemServicesLocator.getService().getProblemComponent(componentId, false);
                final int roundType = problemComponent.getRoundType();
                Solution solution = solutionDao.getComponentSolution(componentId, conn);
                longTesterInvoker.storePrimarySolution(roundId, solution);
    
    
                List testCaseIdsList = new LinkedList();
                sqlStr.setLength(0);
                sqlStr.append(" SELECT stc.test_case_id, stc.args ");
                sqlStr.append("         FROM system_test_case stc");
                sqlStr.append("         WHERE stc.component_id = ? ");
                sqlStr.append("               AND stc.status = 1 AND stc.system_flag = 1 ORDER BY stc.test_number, stc.test_case_id");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setLong(1, componentId);
                rs = ps.executeQuery();
                while (rs.next()) {
                    long testCaseId = rs.getLong(1);
                    longTesterInvoker.storeTestCase(roundId, testCaseId, ((ArrayList)DBMS.getBlobObject(rs, 2)).toArray());
                    testCaseIdsList.add(new Long(testCaseId));;
                }
                if (testCaseIdsList.size() == 0) {
                    throw new RuntimeException("Missing system test cases, or no real submission found");
                }
                rs.close();
                ps.close();
                final long[] testCaseIds = ArrayUtils.getLongArrayFromList(testCaseIdsList);
                testCaseIdsList.clear();
    
                //We add to the queue only real submission.
                //example should always be 0, because it identifies the submission
                String sqlCmd= " SELECT lcs.coder_id, lcs.submission_number, long_component_state_id" +
                               "         FROM long_component_state lcs" +
                               "         WHERE lcs.round_id = ?" +
                               "               AND lcs.submission_number > 0 " +
                               "               AND NOT EXISTS (SELECT gu.user_id FROM group_user gu WHERE gu.user_id = lcs.coder_id " + 
                               "                      AND (group_id = "+ServerContestConstants.ADMIN_LEVEL_ONE_GROUP_ID+" OR group_id = "+ServerContestConstants.ADMIN_LEVEL_TWO_GROUP_ID+"))";
                
                ps = conn.prepareStatement(sqlCmd);
                ps.setLong(1, roundId);
                rs = ps.executeQuery();
                ArrayList submissions = new ArrayList(500);
                while (rs.next()) {
                    submissions.add(new CoderSubmissionPair(rs.getInt(1), rs.getInt(2), rs.getLong(3)));
                }
                DBMS.close(ps, rs);
    
                conn1 = DBMS.getConnection();
                int len = submissions.size() / 2;
                ProblemCustomSettings custom = problemComponent.getProblemCustomSettings();
                
                ScheduleSystemTestsTask fstTask = new ScheduleSystemTestsTask(
                                0,
                                len,
                                submissions,
                                solution,
                                longTesterInvoker,
                                roundId,
                                componentId,
                                contestId,
                                testCaseIds,
                                roundType,
                                custom, false, conn);
    
                ScheduleSystemTestsTask sndTask = new ScheduleSystemTestsTask(
                                len,
                                submissions.size(),
                                submissions,
                                solution,
                                longTesterInvoker,
                                roundId,
                                componentId,
                                contestId,
                                testCaseIds,
                                roundType,
                                custom, false, conn1);
                Thread fstTaskThread = new Thread(fstTask);
                fstTaskThread.start();
                sndTask.run();
                fstTaskThread.join();
                if (!fstTask.success || !sndTask.success) {
                    throw new Exception("Some of the scheduling tasks failed");
                }
                return testCaseIds.length * submissions.size();
            } catch (LongContestServicesException e) {
                throw e;
            } catch (Exception e) {
                printException(e);
                try {
                    s_trace.info("Cancelling system tests for round: "+roundId);
                    TestInvoker.getInstance().cancelSystemTestsForRound(roundId);
                } catch (Exception e1) {
                    s_trace.error("When cancelling system tests for round: "+roundId, e1);
                }
                throw new LongContestServicesException("INTERNAL_SERVER_ERROR");
            }
        } catch (LongContestServicesException e) {
            throw e;
        } catch (Exception e) {
            printException(e);
            throw new LongContestServicesException("INTERNAL_SERVER_ERROR");
        } finally {
            DBMS.close(conn, ps, rs);
            DBMS.close(conn1);
        }
    }

    private static class CoderSubmissionPair {
            int coderId;
            int submissionNumber;
            long componentStateId;
    
            public CoderSubmissionPair(int coderId, int submissionNumber, long componentStateId) {
                this.coderId = coderId;
                this.submissionNumber = submissionNumber;
                this.componentStateId = componentStateId;
            }
        }

    
    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#queueLongSystemTestCase(int, int[], long[])
     */
    public void queueLongSystemTestCase(int roundId, long[] testCaseIds) throws LongContestServicesException {
        s_trace.info("queueLongSystemTestCase("+roundId+","+ArrayUtils.asString(testCaseIds)+")");
        
        try {
            Connection cnn = DBUtils.initDBBlock();
            int[] coderIds = getCodersToSystemTest(roundId, cnn);
            queueLongSystemTestCase(roundId, coderIds, testCaseIds);
        } catch (SQLException e) {
            printException(e);
            throw new LongContestServicesException("INTERNAL_SERVER_ERROR");
        } catch (RuntimeException e) {
            printException(e);
            throw new LongContestServicesException("INTERNAL_SERVER_ERROR");
        } finally {
            DBUtils.endDBBlock();
        }
        
    }
    private int[] getCodersToSystemTest(int roundId, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sqlCmd= " SELECT lcs.coder_id" +
                           "         FROM long_component_state lcs" +
                           "         WHERE lcs.round_id = ?" +
                           "               AND lcs.submission_number > 0 " +
                           "               AND NOT EXISTS (SELECT gu.user_id FROM group_user gu WHERE gu.user_id = lcs.coder_id " + 
                           "                      AND (group_id = "+ServerContestConstants.ADMIN_LEVEL_ONE_GROUP_ID+" OR group_id = "+ServerContestConstants.ADMIN_LEVEL_TWO_GROUP_ID+"))";
            ps = cnn.prepareStatement(sqlCmd);
            ps.setInt(1, roundId);
            rs = ps.executeQuery();
            List ids = new ArrayList(500);
            while (rs.next()) {
                ids.add(new Integer(rs.getInt(1)));
            }
            return ArrayUtils.getIntArray(ids);
        } finally {
            DBMS.close(ps, rs);
        }
    }

    /**
     * <p>
     * queue the long system test case.
     * </p>
     * @param roundId the round id.
     * @param coderIds the coder id array.
     * @param testCaseIds the test case id array.
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#queueLongSystemTestCase(int, int[], long[])
     */
    public void queueLongSystemTestCase(int roundId, int[] coderIds, long[] testCaseIds) throws LongContestServicesException {
        s_trace.info("queueLongSystemTestCase("+roundId+","+ArrayUtils.asString(coderIds)+","+ArrayUtils.asString(testCaseIds)+")");
    
        Connection conn = null;
        PreparedStatement ps =null;
        ResultSet rs=null;
    
        try {
            final LongTesterInvoker longTesterInvoker = TestInvoker.getInstance();
    
            conn = DBUtils.initDBBlock();
    
            deleteLongSystemTestCase(roundId, coderIds, testCaseIds, conn);
    
            StringBuilder sqlStr = new StringBuilder(200);
            //We mark all component states having a real submission as NOT_CHALLENGED
            sqlStr.append("UPDATE long_component_state SET status_id = ").append(ContestConstants.NOT_CHALLENGED);
            sqlStr.append("  WHERE round_id = ? and coder_id = ? and submission_number > 0");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setLong(1, roundId);
            for (int i = 0; i < coderIds.length; i++) {
                int coderId = coderIds[i];
                ps.setLong(2, coderId);
                ps.executeUpdate();
            }
            ps.close();
    
            sqlStr.setLength(0);
            sqlStr.append(" SELECT rc.component_id, r.contest_id ");
            sqlStr.append("         FROM round r, round_component rc ");
            sqlStr.append("         WHERE r.round_id = ? AND rc.round_id = r.round_id");
    
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setLong(1, roundId);
            rs = ps.executeQuery();
            rs.next();

            final int componentId = rs.getInt(1);
            final int contestId = rs.getInt(2);
            rs.close();
            ps.close();
    
            ProblemComponent problemComponent = ProblemServicesLocator.getService().getProblemComponent(componentId, false);
            final int roundType = problemComponent.getRoundType();
//            if (!longTesterInvoker.isStoredPrimarySolution(roundId)) {
                Solution solution = solutionDao.getComponentSolution(componentId, conn);
                longTesterInvoker.storePrimarySolution(roundId, solution);
//            }
    
            storeLongSystemTestCases(roundId, testCaseIds, conn);
    
            ArrayList submissions = new ArrayList(coderIds.length);
            //We add to the queue only real submission.
            //example should always be 0, because it identifies the submission
            sqlStr.setLength(0);
            sqlStr.append(" SELECT lcs.submission_number, lcs.long_component_state_id");
            sqlStr.append("         FROM long_component_state lcs");
            sqlStr.append("         WHERE lcs.round_id = ? AND coder_id = ?");
            sqlStr.append("               AND lcs.submission_number > 0 ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setLong(1, roundId);
            for (int i=0; i < coderIds.length; i++) {
                int coderId = coderIds[i];
                ps.setLong(2, coderId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    submissions.add(new CoderSubmissionPair(coderId, rs.getInt(1), rs.getLong(2)));
                }
                DBMS.close(rs);
            }
            DBMS.close(null, ps, rs);
    
            ProblemCustomSettings custom = problemComponent.getProblemCustomSettings();
            ScheduleSystemTestsTask schedule = new ScheduleSystemTestsTask(
                            0,
                            submissions.size(),
                            submissions,
                            solution,
                            longTesterInvoker,
                            roundId,
                            componentId,
                            contestId,
                            testCaseIds,
                            roundType,
                            custom,
                            true,
                            conn);
    
            schedule.run();
            if (!schedule.success) {
                throw new Exception("Some of the scheduling tasks failed");
            }
        } catch (LongContestServicesException e) {
            throw e;
        } catch (Exception e) {
            printException(e);
            throw new LongContestServicesException("INTERNAL_SERVER_ERROR");
        } finally {
            DBMS.close(ps, rs);
            DBUtils.endDBBlock();
        }
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#adminSubmitLong(com.topcoder.shared.messaging.messages.LongCompileRequest)
     */
    public LongCompileResponse adminSubmitLong(LongCompileRequest req) throws CompilationTimeoutException, LongContestServicesException {
        Lock lock = componentLock.getLock((int) req.getRoundID(), (int) req.getComponentID(), (int) req.getCoderID(), true);
        if (!lock.tryLock()) {
            throw new LongContestServicesException("SERVER_BUSY");
        }
        try {
            LongSubmission ls = compileLong(req);
            if(ls.getCompileStatus()){
                openComponentIfNotOpened(ls.getContestID(), ls.getRoundID(), ls.getComponentID(), ls.getCoderID());
                try {
                    DBUtils.initDBBlock();
                    recordLongCompileStatus(ls);
                    storeCompiledAdminResubmission(ls);
                    queueLongTestCases(ls.getRoundID(), ls.getCoderID(), ls.getComponentID(), ls.isExample());
                } finally {
                    DBUtils.endDBBlock();
                }
            }
            return new LongCompileResponse(ls.getCompileError(), ls.getCompileStatus());
        } catch (LongContestServicesException e) {
            throw e;
        } catch (Exception e) {
            s_trace.error("Exception while submiting long", e);
            throw new LongContestServicesException("INTERNAL_SERVER_ERROR");
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#getAllowedLanguagesForRound(int)
     */
    public Language[] getAllowedLanguagesForRound(final int roundId) throws LongContestServicesException {
        try {
            return DBServicesLocator.getService().getAllowedLanguagesForRound(roundId);
        } catch (Exception e) {
            s_trace.error("Could not obtain languages for round", e);
            throw new LongContestServicesException("INTERNAL_SERVER_ERROR");
        }
    }

    /**
     * Removes all system test execution related data for the round.
     *
     * @param roundId Id of the round
     * @param conn connection to user
     * @throws SQLException
     */
    private void deleteLongSystemTests(int roundId, Connection conn) throws SQLException, LongTesterException {
        PreparedStatement ps = null;
        try {
            TestInvoker.getInstance().cancelSystemTestsForRound(roundId);
    
            String cmd = "DELETE FROM long_system_test_result " +
                             "     WHERE round_id = ? AND " +
                             "           test_action = " + ServicesConstants.LONG_SYSTEM_TEST_ACTION;
            ps = conn.prepareStatement(cmd);
            ps.setInt(1, roundId);
            ps.executeUpdate();
            ps.close();
            
            cmd = "DELETE FROM long_system_test_data " +
                             "     WHERE round_id = ? AND " +
                             "          test_action = " + ServicesConstants.LONG_SYSTEM_TEST_ACTION;
            ps = conn.prepareStatement(cmd);
            ps.setInt(1, roundId);
            ps.executeUpdate();
        } finally {
            DBMS.close(ps);
        }
    }

    /**
     * Removes system test cases execution related data for the round, coders and tests.
     *
     * @param roundId Id of the round
     * @param coderIds ids of the coders
     * @param testCaseIds Ids of the test cases
     * @param conn connection to use
     * @throws SQLException
     */
    private void deleteLongSystemTestCase(int roundId, int[] coderIds, long[] testCaseIds, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        try {
            StringBuilder sqlStr = new StringBuilder(200);
            sqlStr.append("DELETE FROM long_system_test_result ")
                  .append("     WHERE round_id = ? AND coder_id = ? AND ")
                  .append(DBUtils.sqlStrInList("test_case_id", testCaseIds))
                  .append("     AND  test_action = ").append(ServicesConstants.LONG_SYSTEM_TEST_ACTION);
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            
            
            sqlStr.setLength(0);
            sqlStr.append("DELETE FROM long_system_test_data ")
                  .append("     WHERE round_id = ? AND coder_id = ? AND ")
                  .append(DBUtils.sqlStrInList("test_case_id", testCaseIds))
                  .append("     AND  test_action = ").append(ServicesConstants.LONG_SYSTEM_TEST_ACTION);
            ps1 = conn.prepareStatement(sqlStr.toString());
            ps1.setInt(1, roundId);
            
            for (int i = 0; i < coderIds.length; i++) {
                ps.setInt(2, coderIds[i]);
                ps.executeUpdate();
                ps1.setInt(2, coderIds[i]);
                ps1.executeUpdate();
            }
        } finally {
            DBMS.close(ps);
            DBMS.close(ps1);
        }
    }

    /**
     * Stores missing system test cases using the LongTesterInvoker.
     *
     * @param roundId Id of the round
     * @param testCaseIds Ids of the test cases which must be stored if they are not already.
     * @param conn connection to use
     * @throws LongContestServicesException 
     */
    private void storeLongSystemTestCases(int roundId, long[] testCaseIds, Connection conn) throws SQLException, LongTesterException, LongContestServicesException {
        LongTesterInvoker longTesterInvoker = TestInvoker.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            for (int i = 0; i < testCaseIds.length; i++) {
                long testCaseId = testCaseIds[i];
//                if (!longTesterInvoker.isStoredTestCase(roundId, testCaseId)) {
                    if (ps == null) {
                        String sqlStr = " SELECT stc.args " +
                                        "         FROM system_test_case stc" +
                                        "         WHERE stc.test_case_id = ? " +
                                        "               AND stc.status = 1 AND stc.system_flag = 1 ORDER BY stc.test_number, stc.test_case_id";
                        ps = conn.prepareStatement(sqlStr);
                    }
                    ps.setLong(1, testCaseId);
                    rs = ps.executeQuery();
                    if (!rs.next()) {
                        throw new IllegalStateException("Missing system test case: "+testCaseId);
                    }
                    longTesterInvoker.storeTestCase(roundId, testCaseId, ((ArrayList)DBMS.getBlobObject(rs, 1)).toArray());
                    rs.close();
                    ps.close();
//                }
            }
        } finally {
            DBMS.close(ps, rs);
        }
    }

    /**
     * Sets the specified status to the long component state
     * @param longComponentStateId Id of the long component state
     * @param status new status to set
     */
    private void updateLongComponentStateStatus(long longComponentStateId, int status, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        try {
            String sqlStr = "UPDATE long_component_state SET status_id = ? WHERE long_component_state_id = ?";
            ps = conn.prepareStatement(sqlStr);
            ps.setInt(1, status);
            ps.setLong(2, longComponentStateId);
            ps.executeUpdate();
        } finally {
            DBMS.close(ps);
        }
    }

    /**
     * Sets the specified status to the long component state if the current status
     * is the one specified by <code>currentStatus</code>.
     *
     * @param longComponentStateId Id of the long component state
     * @param status new status to set
     * @param currentStatus the status the component state must have.
     *
     * @return true if component state status was updated
     */
    private boolean updateLongComponentStateStatus(long longComponentStateId, int status, int currentStatus, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        try {
            String sqlStr = "UPDATE long_component_state SET status_id = ? WHERE long_component_state_id = ? and status_id = ?";
            ps = conn.prepareStatement(sqlStr);
            ps.setInt(1, status);
            ps.setLong(2, longComponentStateId);
            ps.setInt(3, currentStatus);
            return ps.executeUpdate() == 1;
        } finally {
            DBMS.close(ps);
        }
    }
    
    public void save(int contestId, int roundId, int componentId, int coderId, String programText, int languageId) throws LongContestServicesException {
        s_trace.info("save: "+contestId+", "+roundId+", "+componentId+", "+coderId);
        Lock lock = componentLock.getLock(roundId, componentId, coderId, true);
        if (!lock.tryLock()) {
            throw new LongContestServicesException("SERVER_BUSY");
        }
        try {
            Connection cnn = DBUtils.initDBBlock();
            assertValidSave(roundId, componentId, coderId, cnn);
            openComponentIfNotOpened(contestId, roundId, componentId, coderId);
            saveLongComponent(contestId, roundId, componentId, coderId, programText, languageId);
            getEventPublisher().notifySaved(roundId, coderId, componentId, programText, languageId);
        } catch (LongContestServicesException e) {
            s_trace.info("Failed to save: "+e.getMessage());
            throw e;
        } catch (Exception e) {
            s_trace.error("Cound not save long component", e);
            throw new LongContestServicesException("INTERNAL_SERVER_ERROR");
        } finally {
            lock.unlock();
            DBUtils.endDBBlock();
        }
    }

    private void assertValidSave(int roundId, int componentId, int coderId, Connection cnn) throws SQLException, LongContestServicesException {
        boolean practiceRound = isPracticeRound(roundId, cnn);
        boolean adminUser = userDao.isAdminUser(coderId, cnn);
        assertBasicChecks(roundId, componentId, coderId, practiceRound, adminUser, cnn);
    }

    public boolean openComponentIfNotOpened(int contestId, int roundId, int componentId, int coderId) throws LongContestServicesException {
        s_trace.info("openComponentIfNotOpened: "+contestId+", "+roundId+", "+componentId+", "+coderId);
        Lock lock = componentLock.getLock(roundId, componentId, coderId, true);
        if (!lock.tryLock()) {
            throw new LongContestServicesException("SERVER_BUSY");
        }
        try {
            createLongCompResultIfNotExists(roundId, coderId);
            DBServices dbs = DBServicesLocator.getService();
            if (!dbs.isLongComponentOpened(coderId, roundId, componentId)) { // Is there a record of the user opening the problem?
                long openTime = dbs.coderOpenLongComponent(coderId, contestId, roundId,  componentId);
                getEventPublisher().componentOpened(roundId, coderId, componentId, openTime);
                return true;
            }
            return false;
        } catch (Exception e) {
            s_trace.error("Cound not open long component", e);
            throw new LongContestServicesException("INTERNAL_SERVER_ERROR");
        } finally {
            lock.unlock();
        }
    }

    private void createLongCompResultIfNotExists(int roundId, int coderId) throws SQLException {
        try {
            Connection cnn = DBUtils.initDBBlock();
            if (!existsLongCompResults(roundId, coderId, cnn)) {
                createLongCompResults(roundId, coderId, false, cnn);
            }
        } finally {
            DBUtils.endDBBlock();
        }
    }
    
    public ProblemComponent getProblemComponent(int roundId, int componentId, int coderId) throws LongContestServicesException {
        s_trace.info("getProblemComponent: "+roundId+", "+componentId+", "+coderId);
        Lock lock = componentLock.getLock(roundId, componentId, coderId, true);
        if (!lock.tryLock()) {
            throw new LongContestServicesException("SERVER_BUSY");
        }
        try {
            try {
                Connection cnn = DBUtils.initDBBlock();
                int contestId = roundDao.getContestIdOfRound(roundId, cnn);
                int codingPhaseStatus = roundDao.getSegmentPhaseStatus(roundId, ContestConstants.CODING_SEGMENT_ID, cnn);
                if (codingPhaseStatus == 0) { 
                    openComponentIfNotOpened(contestId, roundId, componentId, coderId);
                } 
                if (codingPhaseStatus == -1) {
                    throw new LongContestServicesException("ROUND_NOT_STARTED");
                }
            } finally {
                DBUtils.endDBBlock();                
            }
            return ProblemServicesLocator.getService().getProblemComponent(componentId, false);
        } catch (LongContestServicesException e) {
            s_trace.info("Could not getProblemComponent: "+e.getMessage());
            throw e;
        } catch (Exception e) {
            s_trace.error("Cound not getProblemComponent", e);
            throw new LongContestServicesException("INTERNAL_SERVER_ERROR");
        } finally {
            lock.unlock();
        }
         
    }
    
    /**
     * Submits a long submission. 
     * 
     * <li> Checks Round and Component status
     * <li> Checks User is allowed to submit in the round
     * <li> Verifies recent submissions
     * <li> Checks queue for the user
     * <li> Compiles the submission and store the result
     * <li> Queues Test cases if compilation succeeded.
     *
     * @param req The LongCompileRequest to submit.
     * @return The LongCompileResponse generated when trying to submit.
     *
     * @throws CompilationTimeoutException If compilation request timeout.
     * @throws LongContestServicesException If the submission could not be done
     */
    public LongCompileResponse submit(LongCompileRequest req) throws CompilationTimeoutException, LongContestServicesException {
        int contestId = (int) req.getContestID();
        int roundId = (int) req.getRoundID();
        int componentId = (int) req.getComponentID();
        int coderId = (int)req.getCoderID();
        boolean example = req.isExample();
        int languageId = req.getLanguageID();
        s_trace.info("submit: "+roundId+", "+componentId+", "+coderId+", "+example);
        Lock lock = componentLock.getLock(roundId, componentId, coderId, true);
        if (!lock.tryLock()) {
            throw new LongContestServicesException("SERVER_BUSY");
        }
        try {
        	assertValidSubmit(roundId, componentId, coderId, example, languageId);
        	openComponentIfNotOpened(contestId, roundId, componentId, coderId);
            LongSubmission ls = compileLong(req);
            LongCompileResponse response = handleLongCompilerResult(ls);
            if (response.getCompileStatus() == true) {
                queueStatusCacheClear();
                //FIXME dropping compiler output of submit success. (It was not been reported)
                response = new LongCompileResponse("Your code size is: "+req.getCode().getBytes().length+"\n", true);
                getEventPublisher().notifySubmissionMade(roundId, coderId, componentId, req.isExample(), ls.getSubmissionNumber());
            }
            if (s_trace.isDebugEnabled()) {
                s_trace.debug("Compile success: " + response.getCompileStatus() + ", info: " + response.getCompileError());
            }
            return response;
        } catch (LongContestServicesException e) {
            s_trace.info("Could not submit long component: "+e.getMessage());
            throw e;
        } catch (Exception e) {
            s_trace.error("Could not submit long component", e);
            throw new LongContestServicesException("INTERNAL_SERVER_ERROR");
        } finally {
            lock.unlock();
        }
    }

    private LongCompileResponse handleLongCompilerResult(LongSubmission ls) throws LongContestServicesException, SQLException, LongTesterException {
        if(ls.getCompileStatus()){
            try {
                DBUtils.initDBBlock();
                recordLongCompileStatus(ls);
                storeCompiledSubmission(ls);
                queueLongTestCases(ls.getRoundID(), ls.getCoderID(), ls.getComponentID(), ls.isExample());
            } finally {
                DBUtils.endDBBlock();
            }
        }
        return new LongCompileResponse(ls.getCompileError(), ls.getCompileStatus());
    }

    private void assertValidSubmit(int roundId, int componentId, int coderId, boolean example, int languageId) throws SQLException, LongContestServicesException {
        try {
            Connection cnn = DBUtils.initDBBlock();
            boolean practiceRound = isPracticeRound(roundId, cnn);
            boolean adminUser = userDao.isAdminUser(coderId, cnn);
            assertBasicChecks(roundId, componentId, coderId, practiceRound, adminUser, cnn);
            if (!adminUser) {
                assertSubmissionRate(roundId, componentId, coderId, example, cnn);
            }
            assertCanSubmit(roundId, componentId, coderId, example, languageId);
        } finally {
            DBUtils.endDBBlock();
        }
    }
    
    private LongSubmission compileLong(LongCompileRequest req) throws CompilationTimeoutException, LongContestServicesException {
        try {
            LongSubmission ls = new LongSubmission(req.getCoderID(), req.getComponentID(), req.getRoundID(), req.getContestID(), req.getLanguageID(), req.getCode(), req.isExample());
//            ls = longSubmitter.compileLong(ls);
        	
            HierarchicalIdBuilder idBuilder = new HierarchicalIdBuilder();
            idBuilder.add(TYPE_ROUND, ls.getRoundID());
            idBuilder.add(TYPE_CODER, ls.getCoderID());
            String requestTag = idBuilder.buildId(TYPE_COMPONENT, ls.getComponentID());
            
            ProblemComponent problemComponent = ProblemServicesLocator.getService().getProblemComponent(ls.getComponentID(), true, false, false);
            // clear information we don't need for compilation b/c it can lead to messages that are too large
            // for the processing queue
            problemComponent.setNotes(null);
            problemComponent.setTestCases(null);
            problemComponent.setIntro(null);
            int codeLength = ls.getCode().getBytes().length;
            int lengthLimit = problemComponent.getCodeLengthLimit();
            s_trace.info("code length limit:" + lengthLimit + ", source length:" + codeLength);
            if (codeLength > lengthLimit) {
                ls.setCompileError("Your code is too long to be compiled.\nThe allowed code length is " + lengthLimit + " bytes.\nYour code length is " + codeLength + " bytes.\n");
                ls.setCompileStatus(false);
                return ls;
            } 
            
            int timeout = problemComponent.getProblemCustomSettings().getCompileTimeLimit();
            
        	//String requestId = UUID.randomUUID().toString();
        	
        	MarathonCodeCompileRequest mRequest = new MarathonCodeCompileRequest(problemComponent, ls);
			CodeProcessingRequest cpr = new CodeProcessingRequest(mRequest, requestTag, AppType.MARATHON,
					ActionType.COMPILE, CodeUtil.toLanguageType(req.getLanguageID()), "marathonCompiler",
					MarathonCodeProcessingResultHandler.class.getName(), ls.getRoundID(), null);
			cpr.getMetadata().setSynchronous(true);
			cpr.getMetadata().setSyncTimeout(timeout);
			Future<CodeProcessingResult> resultPromise = getCodeServices().sendToProcessor(cpr);
    		CodeProcessingResult result = resultPromise.get(timeout, TimeUnit.SECONDS);
    		// we can get a null result if a timeout occurs
    		//return result == null ? null : (LongSubmission) result.getResultData();
    		if (result == null) {
    			s_trace.warn("no result returned. assuming timeout");
    			throw new CompilationTimeoutException("COMPILER_TIMEOUT");
    		}
    		
    		// check result for error
    		if (result.getResultData() == null && result.getErrorMessage() != null) {
    			s_trace.warn("long contest error: " + result.getErrorMessage() + ";" + result.getErrorDetails());
    			throw new LongContestServicesException(result.getErrorMessage());
    		}
    		
    		return (LongSubmission)result.getResultData();
        } catch (LongContestServicesException le) {
        	throw le;
        } catch (Exception e) {
            s_trace.warn("Exception while submiting long", e);
            throw new LongContestServicesException("INTERNAL_SERVER_ERROR", e);
        }
    }
    
    private CodeService getCodeServices() {
    	return ApplicationContextProvider.getContext().getBean(CodeService.class);
    }
    
    private void assertCanSubmit(int roundId, int componentId, int coderId, boolean example, int languageId) throws LongContestServicesException, SQLException {
        try {
            DBUtils.initDBBlock();
            if (!isAllowedLanguage(roundId, languageId)) {
                s_trace.warn("We received an invalid language in request for round="+roundId+" coder="+coderId+" language="+languageId);
                throw new LongContestServicesException("LANGUAGE_NOT_ALLOWED");
            } else if(recentSubmissions(roundId, coderId, componentId, LongContestProperties.LONG_SUBMISSION_INTERVAL) >= LongContestProperties.MAX_LONG_SUBMISSIONS) {
                throw new LongContestServicesException("PERIOD_RATE", new Object[]{new Integer(LongContestProperties.MAX_LONG_SUBMISSIONS), new Integer(LongContestProperties.LONG_SUBMISSION_INTERVAL)});
            } else if(!queueEmpty(roundId, coderId,componentId)){
                throw new LongContestServicesException("SCORE_PENDING");
            }
        } finally {
            DBUtils.endDBBlock();
        }
    }

    public LongCoderHistory getCoderHistory(int roundId, int coderId) throws LongContestServicesException {
        s_trace.info("getCoderHistory("+roundId+","+coderId+")");
        LongCoderHistory result = null;
        Connection cnn = null;
        try {
            cnn = DBMS.getConnection();
            result = new LongCoderHistory();
            result.setFullSubmissions(getSubmissions(roundId, coderId, false, cnn));
            result.setExampleSubmissions(getSubmissions(roundId, coderId, true, cnn));
            if (result.getFullSubmissions().length > 0 && result.getExampleSubmissions().length > 0) {
                //only one can have pending tests
                if (result.getFullSubmissions()[0].getTimestamp().after(result.getExampleSubmissions()[0].getTimestamp())) {
                    result.getExampleSubmissions()[0].setHasPendingTests(false);
                } else {
                    result.getFullSubmissions()[0].setHasPendingTests(false);
                }
            }
        } catch (Exception e) {
            s_trace.error("Could not obtain coder history", e);
            throw new LongContestServicesException("INTERNAL_SERVER_ERROR");
        } finally {
            DBMS.close(cnn);
        }
        return result;
    }
    
    private LongSubmissionData[] getSubmissions(int roundId, int coderId, boolean example, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = cnn.prepareStatement(
                    "SELECT cs.component_id" + 
                    "     , s.submission_number" +
                    "     , s.submit_time" +
                    "     , s.language_id" +                     
                    "     , s.submission_points" +
                    "     , cs.status_id" + 
                    "  FROM long_submission s" + 
                    "     , long_component_state cs" + 
                    " WHERE cs.long_component_state_id = s.long_component_state_id" + 
                    "   AND cs.round_id = ?" + 
                    "   AND cs.coder_id = ?" + 
                    "   and s.example = ?" + 
                    " ORDER BY cs.component_id, s.submission_number desc");
            ps.setInt(1, roundId);
            ps.setInt(2, coderId);
            ps.setInt(3, example ? 1 : 0);
            rs = ps.executeQuery();
            List submissions = new LinkedList();
            boolean first = true;
            while (rs.next()) {
                submissions.add(
                        new LongSubmissionData(
                                rs.getInt(2), 
                                new Date(rs.getLong(3)), 
                                rs.getInt(4), rs.getDouble(5), 
                                first & (rs.getInt(6) == ContestConstants.NOT_CHALLENGED)));
                first = false;
            }
            return (LongSubmissionData[]) submissions.toArray(new LongSubmissionData[submissions.size()]);
            
        } finally {
            DBMS.close(ps, rs);
        }
    }

    
    public LongSubmissionData getSubmission(int roundId, int coderId, int componentId, boolean example, int submissionNumber) throws LongContestServicesException {
        s_trace.info("getSubmission("+roundId+","+coderId+","+componentId+","+example+","+submissionNumber+")");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Connection cnn = DBUtils.initDBBlock();
            ps = cnn.prepareStatement(
                    "SELECT cs.component_id" + 
                    "     , s.submission_number" +
                    "     , s.submit_time" +
                    "     , s.language_id" +                     
                    "     , s.submission_points" +
                    "     , cs.status_id" + 
                    "     , s.submission_text" +
                    "     , cs.example_submission_number" +
                    "     , cs.submission_number" +
                    "  FROM long_submission s" + 
                    "     , long_component_state cs" + 
                    " WHERE cs.long_component_state_id = s.long_component_state_id" + 
                    "   AND cs.round_id = ?" + 
                    "   AND cs.coder_id = ?" + 
                    "   and s.example = ?" + 
                    "   and s.submission_number = ?");
            ps.setInt(1, roundId);
            ps.setInt(2, coderId);
            ps.setInt(3, example ? 1 : 0);
            ps.setInt(4, submissionNumber);
            rs = ps.executeQuery();
            rs.next();
            return new LongSubmissionData(
                                rs.getInt(2), 
                                new Date(rs.getLong(3)), 
                                rs.getInt(4), rs.getDouble(5), 
                                rs.getInt(6) == ContestConstants.NOT_CHALLENGED && submissionNumber == (example ? rs.getInt(8) : rs.getInt(9)), 
                                rs.getString(7));
        } catch (Exception e) {
            s_trace.error("Could not obtain submission information", e);
            throw new LongContestServicesException("INTERNAL_SERVER_ERROR");
        } finally {
            DBMS.close(ps, rs);
            DBUtils.endDBBlock();
        }
    }
    /**
     * Get the long test result.
     * @param roundId the round id.
     * @param coderId the coder id.
     * @param componentId the problem component id.
     * @param resultType the result type.
     * @throws LongContestServicesException if any error occur during get long test results.
     */
    public LongTestResult[] getLongTestResults(int roundId, int coderId, int componentId, int resultType) throws LongContestServicesException {
        s_trace.info("getLongTestResults("+roundId+","+coderId+","+componentId+","+resultType+")");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Connection cnn = DBUtils.initDBBlock();
            ps = cnn.prepareStatement(
                    "select stc.expected_result" + 
                    "     , str.fatal_errors" + 
                    "     , sto.stdout" + 
                    "     , sto.stderr" + 
                    "     , str.score" + 
                    "     , str.processing_time" + 
                    "     , std.data" +
                    "     , str.peak_memory_used" +
                    "  from long_system_test_result str" + 
                    "     , OUTER(long_system_test_output sto)" + 
                    "     , OUTER(long_system_test_data std)" +
                    "     , system_test_case stc" + 
                    " where str.round_id = ?" + 
                    "   and str.component_id = ?" + 
                    "   and str.coder_id = ?" + 
                    "   and sto.round_id = str.round_id" + 
                    "   and sto.component_id = str.component_id" + 
                    "   and sto.coder_id = str.coder_id" + 
                    "   and sto.test_case_id = str.test_case_id" +
                    "   and stc.test_case_id = str.test_case_id" + 
                    "   and stc.example_flag = ?" + 
                    "   and stc.system_flag = ?" +
                    "   and stc.status = 1" +
                    "   and std.round_id = str.round_id" + 
                    "   and std.component_id = str.component_id" + 
                    "   and std.coder_id = str.coder_id" + 
                    "   and std.test_case_id = str.test_case_id" +
                    " order by stc.test_case_id");
            ps.setInt(1, roundId);
            ps.setInt(2, componentId);
            ps.setInt(3, coderId);
            ps.setInt(4, resultType == LONG_TEST_RESULT_TYPE_EXAMPLE ? 1 : 0);
            ps.setInt(5, resultType == LONG_TEST_RESULT_TYPE_SYSTEM ? 1 : 0);
            rs = ps.executeQuery();
            List tests = new LinkedList();
            while (rs.next()) {
                Object arg = DBMS.getBlobObject(rs, 1);
                Object fatalErrors = DBMS.getBlobObject(rs, 2);
                String stdOut = rs.getString(3);
                String strErr = rs.getString(4);
                Object result = DBMS.getBlobObject(rs, 7);
                tests.add(new LongTestResult(
                        arg.toString(), 
                        rs.getDouble(5),
                        rs.getLong(8),
                        rs.getLong(6), 
                        stdOut == null ? "" : stdOut, 
                        strErr == null ? "" : strErr,
                        fatalErrors == null ? "" : fatalErrors.toString(),
                        result));
            }
            return (LongTestResult[]) tests.toArray(new LongTestResult[tests.size()]);
        } catch (Exception e) {
            s_trace.error("Could not obtain example results", e);
            throw new LongContestServicesException("INTERNAL_SERVER_ERROR");
        } finally {
            DBMS.close(ps, rs);
            DBUtils.endDBBlock();
        }
    }

    private void assertBasicChecks(int roundId, int componentId, int coderId, boolean practiceRound, boolean adminUser, Connection cnn) throws SQLException, LongContestServicesException  {
        if (!practiceRound) {
            int value = roundDao.getSegmentPhaseStatus(roundId, ContestConstants.CODING_SEGMENT_ID, cnn);
            if (value == -1) {
                throw new LongContestServicesException("ROUND_NOT_STARTED");
            }
            if (value == 1 && !adminUser) {
                throw new LongContestServicesException("ROUND_ENDED");
            }
            if (!adminUser && !roundDao.isCoderRegistered(coderId, roundId, cnn)) {
                throw new LongContestServicesException("NOT_REGISTERED");
            }
        }
        if (!componentDao.isComponentAccepttingSubmissions(componentId, cnn)) {
            throw new LongContestServicesException("ROUND_NOT_ACCEPTING_SUBMISSIONS");
        }
    }

    private boolean isPracticeRound(int roundId, Connection cnn) throws SQLException {
        return ContestConstants.isPracticeRoundType(roundDao.getRoundTypeId(roundId, cnn));
    }

    
    private void assertSubmissionRate(int roundId, int componentId, int coderId, boolean example, Connection cnn) throws LongContestServicesException, SQLException {
        long remaining = getRemainingSubmissionTime(roundId, componentId, coderId, example, cnn);
        if (remaining >= 1000) {
            throw new LongContestServicesException("SUBMISSION_RATE", new Object[]{new Integer(example ? 1 : 0), new Long(remaining)});
        }
    }
    
    private boolean isAllowedLanguage(int roundId, final int languageID) throws LongContestServicesException {
        Language[] langs = getAllowedLanguagesForRound(roundId);
        return -1 != ArrayUtils.firstMatch(langs, 0, new ArrayUtils.Matcher() {
            public boolean match(Object object) {
                return ((Language) object).getId() == languageID;
            }
        });
    }
    
    public void register(int roundId, int coderId, List surveyData) throws LongContestServicesException {
        s_trace.info("register("+roundId+","+coderId+")");
        Connection cnn = null;
        try {
            Results results = DBServicesLocator.getService().registerCoderWithChecks(coderId, roundId, surveyData);
            if (!results.isSuccess()) {
                throw new LongContestServicesException(results.getMsg());
            }
            cnn = DBMS.getConnection();
            createLongCompResults(roundId, coderId, false, cnn);
            getEventPublisher().notifyCoderRegistered(roundId, coderId);
        } catch (LongContestServicesException e) {
            s_trace.info("register failed: "+e.getMessage());
            throw e;
        } catch (Exception e) {
            s_trace.error("Could not register user", e);
            throw new LongContestServicesException("INTERNAL_SERVER_ERROR");
        } finally {
            DBMS.close(cnn);
        }
    }

    private boolean existsLongCompResults(int roundId, int coderId, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = cnn.prepareStatement("SELECT coder_id FROM long_comp_result WHERE round_id = ?  AND coder_id = ?");
            ps.setInt(1, roundId);
            ps.setInt(2, coderId);
            rs = ps.executeQuery();
            return rs.next();
        } finally {
            DBMS.close(ps,rs);
        }
    }
    
    private void createLongCompResults(int roundId, int coderId, boolean attended, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = cnn.prepareStatement("INSERT INTO long_comp_result (round_id, coder_id, attended) values (?,?,?)");
            ps.setInt(1, roundId);
            ps.setInt(2, coderId);
            ps.setString(3, attended ? "Y" : "N");
            ps.executeUpdate();
        } finally {
            DBMS.close(ps);
        }
    }

    public int getTotalSystemTests(int roundId) throws LongContestServicesException {
        s_trace.info("getTotalSystemTest("+roundId+")");
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection cnn = null;
        try {
            cnn = DBMS.getConnection();
            ps = cnn.prepareStatement(
                    "select count(*)" + 
                    " from  system_test_case stc, round_component rc" + 
                    " where rc.round_id = ?" + 
                    "   and stc.component_id = rc.component_id" + 
                    "   and stc.system_flag = 1 and stc.status = 1");
            ps.setInt(1, roundId);
            rs = ps.executeQuery();
            rs.next();
            int testCaseCount = rs.getInt(1);
            DBMS.close(ps, rs);
            ps = cnn.prepareStatement(
                    "SELECT count(*)" +
                    "         FROM long_component_state lcs" + 
                    "         WHERE lcs.round_id = ?" +
                    "               AND lcs.submission_number > 0" +
                    "               AND NOT EXISTS (SELECT gu.user_id FROM group_user gu WHERE gu.user_id = lcs.coder_id " + 
                    "                      AND (group_id = "+ServerContestConstants.ADMIN_LEVEL_ONE_GROUP_ID+" OR group_id = "+ServerContestConstants.ADMIN_LEVEL_TWO_GROUP_ID+"))");
            ps.setInt(1, roundId);
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1)*testCaseCount;
        } catch (Exception e) {
            s_trace.error("Could not obtain total system tests", e);
            throw new LongContestServicesException("INTERNAL_SERVER_ERROR");
        } finally {
            DBMS.close(cnn, ps, rs);
        }
    }
    
    
    public int getSystemTestsDone(int roundId) throws LongContestServicesException {
        s_trace.info("getSystemTestsDone("+roundId+")");
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection cnn = null;
        try {
            cnn = DBMS.getConnection();
            ps = cnn.prepareStatement(
                    "select count(*) " +
                    " from  system_test_case stc, long_system_test_result str " +
                    " where stc.component_id in (select component_id from round_component where round_id = ?) " +
                    "   and str.test_case_id  = stc.test_case_id " +
                    "   and stc.system_flag = 1 and stc.status = 1" +
                    "   and str.round_id = ? ");
            ps.setInt(1, roundId);
            ps.setInt(2, roundId);
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (Exception e) {
            s_trace.error("Could not obtain system tests done", e);
            throw new LongContestServicesException("INTERNAL_SERVER_ERROR");
        } finally {
            DBMS.close(cnn, ps, rs);
        }
    }
    
    
    private void printException(Exception e) {
        try {
            if (e instanceof SQLException) {
                if (e.getMessage() == null || e.getMessage().indexOf("Already closed") == -1) {
                    String sqlErrorDetails = DBMS.getSqlExceptionString((SQLException) e);
                    s_trace.error("Test services EJB: SQLException caught\n" + sqlErrorDetails, e);
                }
            } else {
                s_trace.error("Test services EJB: Exception caught", e);
            }
        } catch (Exception ex) {
            s_trace.error("Test services EJB: Error printing exception!");
        }
    }
    
    public LongContestServiceEventNotificator getEventPublisher() {
        if (eventPublisher == null) {
            eventPublisher = new LongContestServiceEventNotificator();
        }
        return eventPublisher;
    }

    /**
     * <p>
     * get round type by component id.
     * </p>
     * @param componentId
     *         the component id.
     * @return the round type.
     */
    private int getRoundTypeByComponentId(int componentId) {
        SimpleComponent component = CoreServices.getSimpleComponent(componentId);
        if(component!=null)
            return component.getRoundType();
        return -1;
    }

    /**
     * Last submission bean. It simply encapsulates last submission information.
     *
     * @author gevak
     * @version 1.0
     */
    private class LastSubmissionBean {
        /**
         * <p>
         * The last submit time, in minutes.
         * </p>
         */
        private long lastSubmitTime;

        /**
         * <p>
         * The submission rate interval, in minutes.
         * </p>
         */
        private Integer submissionRate;

        /**
         * <p>
         * The example submission rate interval, in minutes.
         * </p>
         */
        private Integer exampleSubmissionRate;

        /**
         * Creates instance.
         */
        public LastSubmissionBean() {
            
        }

        /**
         * <p>
         * Gets the last submit time.
         * </p>
         * @return The last submit time, in minutes.
         */
        public long getLastSubmitTime() {
            return lastSubmitTime;
        }

        /**
         * <p>
         * Sets the last submit time.
         * </p>
         * @param lastSubmitTime The last submit time, in minutes.
         */
        public void setLastSubmitTime(long lastSubmitTime) {
            this.lastSubmitTime = lastSubmitTime;
        }

        /**
         * <p>
         * Gets the submission rate interval.
         * </p>
         * @return The submission rate interval, in minutes.
         */
        public Integer getSubmissionRate() {
            return submissionRate;
        }

        /**
         * <p>
         * Sets the submission rate interval.
         * </p>
         * @param submissionRate The submission rate interval, in minutes.
         */
        public void setSubmissionRate(Integer submissionRate) {
            this.submissionRate = submissionRate;
        }

        /**
         * <p>
         * Gets the example submission rate interval.
         * </p>
         * @return The example submission rate interval, in minutes.
         */
        public Integer getExampleSubmissionRate() {
            return exampleSubmissionRate;
        }

        /**
         * <p>
         * Sets the example submission rate interval.
         * </p>
         * @param exampleSubmissionRate The example submission rate interval, in minutes.
         */
        public void setExampleSubmissionRate(Integer exampleSubmissionRate) {
            this.exampleSubmissionRate = exampleSubmissionRate;
        }
    }

}

