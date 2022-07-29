/*
 * Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
 */
 
package com.topcoder.server.ejb.TestServices;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.topcoder.farm.shared.util.concurrent.ReadWriteLock;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.round.RoundType;
import com.topcoder.server.common.ChallengeAttributes;
import com.topcoder.server.common.LongSubmissionId;
import com.topcoder.server.common.RemoteFile;
import com.topcoder.server.common.Results;
import com.topcoder.server.common.ServerContestConstants;
import com.topcoder.server.common.Submission;
import com.topcoder.server.common.SubmitResults;
import com.topcoder.server.ejb.BaseEJB;
import com.topcoder.server.ejb.TestServices.longtest.LongTestServiceException;
import com.topcoder.server.ejb.TestServices.longtest.LongTestServices;
import com.topcoder.server.ejb.TestServices.longtest.LongTestServicesBean;
import com.topcoder.server.ejb.TestServices.longtest.model.LongTestGroup;
import com.topcoder.server.ejb.TestServices.to.ComponentAndDependencyFiles;
import com.topcoder.server.ejb.TestServices.to.SystemTestResult;
import com.topcoder.server.ejb.TestServices.to.SystemTestResultPartialComparator;
import com.topcoder.server.ejb.TestServices.to.SystemTestResultsBatch;
import com.topcoder.server.ejb.dao.RoundDao;
import com.topcoder.server.ejb.dao.SolutionDao;
import com.topcoder.server.ejb.dao.UserDao;
import com.topcoder.server.processor.Processor;
import com.topcoder.server.tester.CodeCompilation;
import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.server.tester.Solution;
import com.topcoder.server.util.ArrayUtils;
import com.topcoder.server.util.DBUtils;
import com.topcoder.server.webservice.WebServiceRemoteFile;
import com.topcoder.services.tester.common.LongTestResults;
import com.topcoder.shared.common.LongRoundScores;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.language.Language;
import com.topcoder.shared.messaging.messages.LongCompileRequest;
import com.topcoder.shared.messaging.messages.LongCompileResponse;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.ProblemCustomSettings;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.Formatters;
import com.topcoder.shared.util.IdGeneratorClient;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.util.idgenerator.IDGenerationException;

/**
 * <p>
 * Version 1.1 (TC Competition Engine Code Execution Time Issue) change notes:
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
 *      <li>Added parameter <code>pythonApprovedPath</code> to method
 *      {@link #startTestGroup(int, Integer, int, int, String, String)}.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #startTestGroup(int testGroupId, Integer roundType, ProblemCustomSettings)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.5 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *     <li>Added {@link #getComponentSolution(int)} for retrieving component by ID.</li>
 *     <li>Update {@link #recordSystemTestResultsWithFailure(Connection, int,
            SystemTestResult[], int, int)} to handle failure message.</li>
       <li>Updated {@link #recordSystemTestResult(int, int, int, int, int, Object, boolean, double, int, int, String)}
       to handle failure message.</li>
       <li>Updated {@link #recordSystemTestResultWithFailure(Connection, int, int, int, int,
       Object, int, boolean, double, int, String)} to handle failure message.</li>
       <li>Updated {@link #recordChallengeRecord(ChallengeAttributes, Connection)} to support check answer response</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.6 (Return Time Infos When Opening Problems v1.0) :
 * <ol>
 *      <li>Update {@link #recordCompileStatus(CodeCompilation sub)} method.</li>
 *      <li>Update {@link #saveCompilation(Connection con, String programText, long componentStateID, 
 *      	int languageID, boolean compilationStatus, long compiledTime)} method.</li>
 *      <li>Update {@link #submitProblem(Submission sub, long codingLength, boolean restrictPerUserTime)} method.</li>
 *      <li>Update {@link #saveComponent(long contestId, long roundId, long componentId,
 *              long coderId, String programText, int languageID)} method.</li>
 * </ol>
 * </p>
 * @author savon_cn, gevak, TCSASSEMBLER
 * @version 1.6
 */
public class TestServicesBean extends BaseEJB implements LongContestServices, LongTestServices {

    /**
     * Category for logging.
     */
    private static final Logger s_trace = Logger.getLogger(TestServicesBean.class);

    private static final SolutionDao solutionDao = new SolutionDao();
    
    private static final RoundDao roundDao = new RoundDao();

    /**
     * Locks container to handle simultaneous result storage update for SRM system tests.
     */
    private static final ComponentStateRWLockManager resultsStorageLocks = new ComponentStateRWLockManager();
    private static final LongTestServices longTestServices = new LongTestServicesBean();
    private static final LongContestServices longContestTestServices = new LongContestServicesBean();

    private static final UserDao userDao = new UserDao();
//  private static long s_dbTimeDiff = 0; // Difference in millis b/t this server and the DB

    public Solution getComponentSolution(String className) throws TestServicesException {
        debug("In TestServicesBean.getComponentSolution(" + className + ")");
        java.sql.Connection conn = null;
        try {
            conn = DBMS.getConnection();
            return solutionDao.getComponentSolution(className, conn);
        } catch (Exception e) {
            printException(e);
            throw new TestServicesException("Error loading solution");
        } finally {
            DBMS.close(conn);
        }
    }

    /**
     * Retrieves component solution.
     *
     * @param componentId Component ID.
     * @return Solution.
     * @throws RemoteException If remote error occurs.
     * @throws TestServicesException If service error occurs.
     * @since 1.1
     */
    public Solution getComponentSolution(int componentId) throws TestServicesException {
        debug("In TestServicesBean.getComponentSolution(" + componentId + ")");
        java.sql.Connection conn = null;
        try {
            conn = DBMS.getConnection();
            return solutionDao.getComponentSolution(componentId, conn);
        } catch (Exception e) {
            printException(e);
            throw new TestServicesException("Error loading solution");
        } finally {
            DBMS.close(conn);
        }
    }

    public ComponentFiles getComponentFiles(int contestId, int roundId, int componentId, int coderId, int classFileType) throws TestServicesException {
        if (s_trace.isDebugEnabled()) {
            debug("In getComponentFiles(), contestId=" + contestId + ", roundId=" + roundId + ", componentId=" + componentId + ", coderId=" + coderId + ", classFileType=" + classFileType);
        }
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            return getComponentFiles(contestId, roundId, componentId, coderId, classFileType, conn);
        } catch (SQLException e) {
            s_trace.error("Could not obtain DB Connection");
            printException(e);
            throw new TestServicesException("Error loading component files contestId = " + contestId + " roundId = " + roundId + " componentId = " + componentId + " coderId = " + coderId + " classFileType = " + classFileType);
        } finally {
            DBMS.close(conn);
        }
    }

    private ComponentFiles getComponentFiles(int contestId, int roundId, int componentId, int coderId, int classFileType, Connection conn) throws TestServicesException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(200);
        if (classFileType == ContestConstants.COMPILED_CLASS) {
            if (DBMS.DB == DBMS.INFORMIX) {
                sqlStr.append("SELECT c.class_name,compl.language_id,ccf.path,ccf.class_file,ccf.sort_order ");
                sqlStr.append("FROM compilation compl, compilation_class_file ccf JOIN component_state cs ON cs.component_state_id = ccf.component_state_id ");
                sqlStr.append(" JOIN component c ON cs.component_id = c.component_id ");
                sqlStr.append("WHERE cs.round_id = ? ");
                sqlStr.append("AND compl.component_state_id = ccf.component_state_id ");
                sqlStr.append("AND cs.component_id = ? ");
                sqlStr.append("AND cs.coder_id = ? ");
                sqlStr.append("ORDER BY ccf.sort_order ");
            } else {
                String sql = "SELECT c.class_name, cs.language_id, ccf.path, ccf.class_file, ccf.sort_order " +
                        "FROM compilation_class_file ccf, component_state cs, component c " +
                        "WHERE cs.round_id = ? AND cs.component_id = ? AND cs.coder_id = ? AND " +
                        "cs.component_state_id = ccf.component_state_id AND cs.component_id = c.component_id " +
                        "ORDER BY ccf.sort_order";
                sqlStr.append(sql);
            }
        } else if (classFileType == ContestConstants.SUBMITTED_CLASS) {
            sqlStr.append("SELECT c.class_name, s.language_id, scf.path,scf.class_file,scf.sort_order ");
            sqlStr.append("FROM submission s, submission_class_file scf INNER JOIN component_state cs ON cs.component_state_id = scf.component_state_id ");
            sqlStr.append("INNER JOIN component c ON cs.component_id = c.component_id ");
            sqlStr.append("WHERE cs.round_id = ? ");
            sqlStr.append("AND s.component_state_id = cs.component_state_id ");
            sqlStr.append("AND cs.component_id = ? ");
            sqlStr.append("AND cs.coder_id = ? ");
            sqlStr.append("AND cs.submission_number = scf.submission_number ");
            sqlStr.append("AND s.submission_number = scf.submission_number ");
            sqlStr.append("ORDER BY scf.sort_order ");
        } else {
            throw new IllegalArgumentException("Unknown classFileType " + classFileType);
        }

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
                debug("Loaded componentFiles: " + componentFiles);
                return componentFiles;
            } else {
                throw new TestServicesException("Record not found");
            }
        } catch (Exception e) {
            printException(e);
            throw new TestServicesException("Error loading component files contestId = " + contestId + " roundId = " + roundId + " componentId = " + componentId + " coderId = " + coderId + " classFileType = " + classFileType);
        } finally {
            close(null, ps, rs);
        }
    }

    public ComponentFiles[] getAllComponentFiles(int contestId, int roundId, int componentId, int coderId, int classFileType) throws TestServicesException {
        ComponentFiles[] deps = getDependencyComponentFiles(contestId, roundId, componentId, coderId, classFileType);
        ComponentFiles[] ret = new ComponentFiles[deps.length + 1];
        ret[0] = getComponentFiles(contestId, roundId, componentId, coderId, classFileType);
        System.arraycopy(deps, 0, ret, 1, deps.length);
        return ret;
    }

    public ComponentAndDependencyFiles getComponentAndDependencyFiles(int contestId, int roundId, int problemId, int componentId, int coderId, int componentFilesType, int dependencyFilesType, boolean solution) throws TestServicesException {
        debug("getComponentAndDependencyFiles(contestId=" + contestId + ", roundId=" + roundId + ", componentId=" + componentId + ", coderId=" + coderId + ", componentFilesType=" + componentFilesType + ", dependencyFilesType=" + dependencyFilesType);
        Connection cnn = null;
        try {
            cnn = DBMS.getConnection();
            ComponentFiles componentFiles = getComponentFiles(contestId, roundId, componentId, coderId, componentFilesType, cnn);
            ComponentFiles[] dependencyComponentFiles = getDependencyComponentFiles(contestId, roundId, componentId, coderId, dependencyFilesType, cnn);
            WebServiceRemoteFile[] webServiceRemoteFiles = getWebServiceClientsForProblem(problemId, JavaLanguage.ID, cnn);
            ComponentAndDependencyFiles componentAndDependencyFiles = new ComponentAndDependencyFiles(componentFiles, dependencyComponentFiles, webServiceRemoteFiles);
            if (solution) {
                componentAndDependencyFiles.setSolution(solutionDao.getComponentSolution(componentId, cnn));
            }
            return componentAndDependencyFiles;
        } catch (Exception e) {
            s_trace.error("Could not obtain all component files : componentId=" + componentId + " coderId=" + coderId, e);
            throw new TestServicesException("Could not obtain all component files: " + e.getMessage());
        } finally {
            DBMS.close(cnn);
        }
    }

    public boolean isTeamComponent(long componentId) throws TestServicesException {
        debug("In isTeamComponent...componentId=" + componentId);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(200);

        try {
            conn = DBMS.getConnection();

            sqlStr.append("SELECT component_type_id ");
            sqlStr.append("FROM component ");
            sqlStr.append("WHERE component_id = ? ");

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setLong(1, componentId);

            rs = ps.executeQuery();

            boolean ret = false;

            if (rs.next()) {
                int type = rs.getInt(1);
                if (type == 2) {
                    ret = true;
                }
            }
            return ret;
        } catch (Exception e) {
            printException(e);
            throw new TestServicesException(e.toString());
        } finally {
            //gp why didn't this close the connection?  added it
            close(conn, ps, rs);
        }
    }

    public ComponentFiles[] getDependencyComponentFiles(int contestId, int roundId, int componentId, int coderId, int classFileType) throws TestServicesException {
        debug("In getDependencyComponentFiles...componentId=" + componentId);
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            return getDependencyComponentFiles(contestId, roundId, componentId, coderId, classFileType, conn);
        } catch (SQLException e) {
            s_trace.error("Could not obtain DB Connection");
            printException(e);
            throw new TestServicesException("Error loading dependency component files contestId = " + contestId + " roundId = " + roundId + " componentId = " + componentId + " coderId = " + coderId + " classFileType = " + classFileType);
        } finally {
            DBMS.close(conn);
        }
    }

    private ComponentFiles[] getDependencyComponentFiles(int contestId, int roundId, int componentId, int coderId, int classFileType, Connection conn) throws TestServicesException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(200);

        try {
            if (DBMS.DB == DBMS.INFORMIX) {
                if (classFileType == ContestConstants.COMPILED_CLASS) {
                    sqlStr.append("SELECT c.class_name ");
                    sqlStr.append("      ,compl.language_id ");
                    sqlStr.append("      ,ccf.path ");
                    sqlStr.append("      ,ccf.class_file ");
                    sqlStr.append("      ,ccf.sort_order ");
                    sqlStr.append("      ,c.component_id ");
                    sqlStr.append("FROM compilation_class_file ccf ");
                    sqlStr.append("    ,component_state cs ");
                    sqlStr.append("    ,component c ");
                    sqlStr.append("    ,compilation compl ");
                    sqlStr.append("    ,team_coder_component_xref tc ");
                    sqlStr.append("WHERE ccf.component_state_id = cs.component_state_id ");
                    sqlStr.append("    AND c.component_id = cs.component_id ");
                    sqlStr.append("    AND compl.component_state_id = ccf.component_state_id ");
                    sqlStr.append("    AND cs.component_id = tc.component_id ");
                    sqlStr.append("    AND cs.coder_id = tc.coder_id ");
                    sqlStr.append("    AND tc.active = 1 ");
                    sqlStr.append("    AND tc.round_id = ? ");
                    sqlStr.append("    AND cs.round_id = tc.round_id ");
                    sqlStr.append("    AND tc.component_id <> ? ");
                    sqlStr.append("    AND tc.team_id in (SELECT team_id FROM team_coder_xref WHERE coder_id = ?) ");
                } else if (classFileType == ContestConstants.SUBMITTED_CLASS) {
                    sqlStr.append("SELECT c.class_name ");
                    sqlStr.append("      ,s.language_id ");
                    sqlStr.append("      ,ccf.path ");
                    sqlStr.append("      ,ccf.class_file ");
                    sqlStr.append("      ,ccf.sort_order ");
                    sqlStr.append("      ,c.component_id ");
                    sqlStr.append("FROM submission_class_file ccf ");
                    sqlStr.append("    ,component_state cs ");
                    sqlStr.append("    ,component c ");
                    sqlStr.append("    ,submission s ");
                    sqlStr.append("    ,team_coder_component_xref tc ");
                    sqlStr.append("WHERE ccf.component_state_id = cs.component_state_id ");
                    sqlStr.append("    AND s.component_state_id = ccf.component_state_id ");
                    sqlStr.append("    AND c.component_id = cs.component_id ");
                    sqlStr.append("    AND cs.component_id = tc.component_id ");
                    sqlStr.append("    AND cs.coder_id = tc.coder_id ");
                    sqlStr.append("    AND tc.active = 1 ");
                    sqlStr.append("    AND tc.round_id = ? ");
                    sqlStr.append("    AND cs.round_id = tc.round_id ");
                    sqlStr.append("    AND cs.submission_number = ccf.submission_number ");
                    sqlStr.append("    AND tc.component_id <> ? ");
                    sqlStr.append("    AND tc.team_id in (SELECT team_id FROM team_coder_xref WHERE coder_id = ?) ");
                } else {
                    String msg = "ERROR: Unknown COMPILATION TYPE: " + classFileType;
                    s_trace.error(msg);
                    throw new IllegalArgumentException(msg);
                }
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, roundId);
                ps.setInt(2, componentId);
                ps.setInt(3, coderId);
            } else {
                String teamIds = getTeamIds(conn, coderId);
                switch (classFileType) {
                    case ContestConstants.SUBMITTED_CLASS:
                        sqlStr.append("SELECT c.class_name ");
                        sqlStr.append("      ,s.language_id ");
                        sqlStr.append("      ,ccf.path ");
                        sqlStr.append("      ,ccf.class_file ");
                        sqlStr.append("      ,ccf.sort_order ");
                        sqlStr.append("      ,c.component_id ");
                        sqlStr.append("FROM submission_class_file ccf ");
                        sqlStr.append("    ,component_state cs ");
                        sqlStr.append("    ,component c ");
                        sqlStr.append("    ,submission s ");
                        sqlStr.append("    ,team_coder_component_xref tc ");
                        sqlStr.append("WHERE ccf.component_state_id = cs.component_state_id ");
                        sqlStr.append("    AND s.component_state_id = ccf.component_state_id ");
                        sqlStr.append("    AND c.component_id = cs.component_id ");
                        sqlStr.append("    AND cs.component_id = tc.component_id ");
                        sqlStr.append("    AND cs.coder_id = tc.coder_id ");
                        sqlStr.append("    AND tc.active = 1 ");
                        sqlStr.append("    AND tc.round_id = ? ");
                        sqlStr.append("    AND cs.round_id = tc.round_id ");
                        sqlStr.append("    AND cs.submission_number = ccf.submission_number ");
                        sqlStr.append("    AND tc.component_id <> ? ");
                        if (teamIds.length() > 0) {
                            sqlStr.append("    AND tc.team_id in (" + teamIds + ") ");
                        }
                        break;
                    default:
                        throw new RuntimeException("classFileType = " + classFileType);
                }
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, roundId);
                ps.setInt(2, componentId);
            }

            rs = ps.executeQuery();
            String last = null;
            ComponentFiles componentFiles = null;
            Vector r = new Vector();
            while (rs.next()) {
                String className = rs.getString(1);
                int languageID = rs.getInt(2);
                int componentId2 = rs.getInt(6);
                if (last == null || !className.equals(last)) {
                    componentFiles = ComponentFiles.getInstance(languageID, coderId, contestId, roundId, componentId2, className);
                    r.add(componentFiles);
                    last = className;
                }
                String path = rs.getString(3);
                debug("rs.next with className=" + className + ", path=" + path);
                byte[] clazzBytes = rs.getBytes(4);
                componentFiles.addClassFile(path, clazzBytes);
            }
            ComponentFiles[] ret = new ComponentFiles[r.size()];
            r.copyInto(ret);
            return ret;
        } catch (Exception e) {
            printException(e);
            throw new TestServicesException(e.toString());
        } finally {
            close(null, ps, rs);
        }
    }

    private static String getTeamIds(Connection conn, int coderId) throws SQLException {
        String sql = "SELECT team_id FROM team_coder_xref WHERE coder_id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        try {
            preparedStatement.setInt(1, coderId);
            ResultSet resultSet = preparedStatement.executeQuery();
            try {
                int count = 0;
                String teamIds = "";
                while (resultSet.next()) {
                    int teamId = resultSet.getInt(1);
                    if (count != 0) {
                        teamIds += ", ";
                    }
                    teamIds += teamId;
                    count++;
                }
                return teamIds;
            } finally {
                resultSet.close();
            }
        } finally {
            preparedStatement.close();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////
    // This method writes the challenge to the database. First, it makes sure that
    // nobody else has already successfully challenged the problem. If someone has, it
    // returns a String message saying so.
    //////////////////////////////////////////////////////////////////////////////////
    public String recordChallengeResults(ChallengeAttributes chal) throws TestServicesException {
        debug("recordChallengeResults: " + chal);
        java.sql.Connection conn = null;

        if (chal.isSystemFailure()) {
            s_trace.warn("Attempt to save challenge with system failure result to the database: " + chal);
            return "";
        }

        // Attempt to change the status of the defendants coder_compilations record
        try {
            conn = DBMS.getConnection();
            long componentStateID = getComponentStateID(chal.getDefendantId(), chal.getLocation().getRoundID(), chal.getComponentId(), conn);
            boolean isPracticeRoom = ContestConstants.isPracticeRoomTypeID(getRoomTypeId(conn, chal.getLocation().getRoomID()));
            boolean succesfulChallenge = chal.isSuccesfulChallenge();
            //If room is a practice room, we don't test anything
            if (!isPracticeRoom) {
                //TODO This is subject to phatom reads even inside a transaction, so since duplicate challenge may occur we avoid
                //transaction and locks. We should use, a Higher ISOLATION level or JAVA synchronization
                if (duplicateChallenge(conn, chal)) {
                    s_trace.warn("A challenge was denied because they sent a duplicate challenge.");
                    s_trace.warn(chal.getChalText());
                    return "Your challenge was denied because it was a duplicate challenge";
                }
            }
            //We try to update the status, if the is not a practice one, we must enforce previous status.
            //Only 1 succeeded challenge can occur. If update fails, someone already beat it
            if (!updateChallengeStatus(succesfulChallenge, componentStateID, !isPracticeRoom, conn)) {
                s_trace.warn("A challenge was denied because someone beat them to it.");
                s_trace.warn(chal.getChalText());
                return "Your challenge was denied because someone has already successfully challenged this problem";
            }
            try {
                debug("Challenge past updateChallengeStatus()");
                recordChallengeRecord(chal, conn);
                debug("Challenge recorded");

                // If the challenge was successful, attempt to deduct the points from the defendant
                if (succesfulChallenge) {
                    debug("Challenge successful");
                    penalizeDefendant(chal, conn);
                }

                updateChallenger(chal, conn);
                debug("Challenge done");
                return "";
            } catch (Exception e) {
                s_trace.error("CHALLENGE: component state updated but failed to update related information", e);
                printException(e);
                return "Error recording challenge results to database";
            }
        } catch (Exception e) {
            printException(e);
            return "Error recording challenge results to database";
        } finally {
            close(conn, null, null);
        }
    }

    public ArrayList retrieveTestCases(int componentID) throws TestServicesException {
        debug("In retrieveTestCases...");
        java.sql.Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList retVal = new ArrayList();
        ArrayList ids = new ArrayList();
        ArrayList args = new ArrayList();
        ArrayList results = new ArrayList();

        StringBuilder sqlStr = new StringBuilder(200);

        sqlStr.append("SELECT test_case_id, args, expected_result FROM system_test_case");
        sqlStr.append(" WHERE component_id = ? and status = 1 order by test_number, test_case_id");

        try {
            con = DBMS.getConnection();
            ps = con.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentID);
            rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(new Integer(rs.getInt(1)));
                args.add(DBMS.getBlobObject(rs, 2));
                results.add(DBMS.getBlobObject(rs, 3));
            }
            retVal.add(ids);
            retVal.add(args);
            retVal.add(results);
            return retVal;
        } catch (Exception e) {
            printException(e);
            throw new TestServicesException(e.getMessage());
        } finally {
            close(con, ps, rs);
        }
    }
    // TODO: AFTER THIS IS CALLED, RELOAD THE ROOM STATUS

    /**
     * The resetSystemTestState is responsible for removing system test results and obtaining a new
     * system test version.
     *
     * The value returned by this method should be used as arg when reporting system test result for a submission.
     *
     * A negative value indicates, no system test case should be enqueue
     *
     * @see TestServices#recordSystemTestResult(int, int, int, int, int, Object, boolean, double, int, int)
     */
    public int resetSystemTestState(int contestId, int coderId, int roundId, int componentId) throws TestServicesException {
        info("resetSystemTestState(" + contestId + ", " + coderId + ", " + roundId + ", " + componentId + ")");
        return resetAndRemoveTestForSubmission(coderId, roundId, componentId, true);
    }

    /**
     * The removeSystemTestsForSubmission is responsible for removing system test results and changing the version to
     * avoid storing new test results
     */
    public void removeSystemTestsForSubmission(int contestId, int coderId, int roundId, int componentId) throws TestServicesException {
        info("removeSystemTestsForSubmission(" + contestId + ", " + coderId + ", " + roundId + ", " + componentId + ")");
        resetAndRemoveTestForSubmission(coderId, roundId, componentId, false);
    }

    private int resetAndRemoveTestForSubmission(int coderId, int roundId, int componentId, boolean resetting) throws TestServicesException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ReadWriteLock rwLock = null;
        ReadWriteLock.Lock wlock = null;
        ReadWriteLock.Lock rlock = null;
        try {
            rwLock = resultsStorageLocks.getLock(roundId, componentId, coderId);
            wlock = rwLock.writeLock();
            wlock.lock();
            try {
                conn = DBMS.getConnection();
                long componentStateID = getComponentStateID(coderId, roundId, componentId, conn);
                int nextSystestId = Math.abs(getCurrentSystemTestVersion(conn, componentStateID)) + 1;
                if (!updateNewSystemTestVersion(conn, componentStateID, !resetting ? -nextSystestId : nextSystestId, resetting)) {
                    s_trace.error("Could not update system test version, the Component state changed while reseting its state, or it was challenged");
                    return -nextSystestId;
                }
                //We now downgrade the lock in order to allow reading on the component state system test version
                rlock = rwLock.readLock();
                rlock.lock();
                wlock.unlock();
                //We remove old system test results
                deleteSystemTestResults(coderId, roundId, componentId, conn);
                return nextSystestId;
            } finally {
                resultsStorageLocks.safeUnlock(wlock);
                resultsStorageLocks.safeUnlock(rlock);
            }
        } catch (Exception e) {
            s_trace.error("Exception while reseting system test result");
            printException(e);
            throw new TestServicesException(e.getMessage());
        } finally {
            close(conn, ps, rs);
        }
    }

    /**
     * Removes all system test results for the round/coder/component.
     *
     * @param roundId Id of the round
     * @param coderId Id of the coder
     * @param componentId Id of the component
     * @param conn connection to use
     * @throws SQLException
     */
    private void deleteSystemTestResults(int coderId, int roundId, int componentId, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        try {
            StringBuilder sqlStr = new StringBuilder(200);
            sqlStr.setLength(0);
            sqlStr.append("DELETE FROM system_test_result ");
            sqlStr.append("     WHERE round_id = ? AND coder_id = ? AND component_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            ps.setInt(2, coderId);
            ps.setInt(3, componentId);
            ps.executeUpdate();
        } finally {
            DBMS.close(ps);
        }
    }

    /**
     * The recordSystemTestResult is responsible for recording the results
     * of a particular test case.
     */
    public void recordSystemTestResult(SystemTestResultsBatch resultBatch) throws TestServicesException {
        //We sort elements, coder. component. round, systemTestVersion(desc)
        SystemTestResult[] results = resultBatch.getResults();
        info("recordSystemTestResult("+ArrayUtils.asString(results)+")");
        long st = System.currentTimeMillis();

        Arrays.sort(results, SystemTestResultPartialComparator.INSTANCE);

        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            
            int start = 0;
            RoundType roundType = null;
            int roundIdOfRoundType = 0;
            while (start < results.length) {
                SystemTestResult res = results[start];
                int coderId = res.getCoderId();
                int roundId = res.getRoundId();
                int componentId = res.getComponentId();
                int maxSTVersion = res.getSystemTestVersion();

                if (roundIdOfRoundType != roundId) {
                    roundIdOfRoundType = roundId;
                    roundType = RoundType.get(roundDao.getRoundTypeId(roundId, conn));
                }
                
                //We must detect if some test failed and the last test to add
                int maxIndexToStore = start;
                while (maxIndexToStore < results.length &&
                        coderId == results[maxIndexToStore].getCoderId() &&
                        roundId == results[maxIndexToStore].getRoundId() &&
                        componentId == results[maxIndexToStore].getComponentId() &&
                        maxSTVersion == results[maxIndexToStore].getSystemTestVersion()
                        && results[maxIndexToStore].isSucceeded()) {
                    maxIndexToStore++;
                }
                boolean failed = false;
                int firstFailedTest = maxIndexToStore;
                if (maxIndexToStore < results.length && coderId == results[maxIndexToStore].getCoderId() &&
                        roundId == results[maxIndexToStore].getRoundId() &&
                        componentId == results[maxIndexToStore].getComponentId() &&
                        maxSTVersion == results[maxIndexToStore].getSystemTestVersion()) {

                    failed = true;
                    maxIndexToStore++;
                    //If we must include all tests regardless failures
                    if (!roundType.mustStopSystemTestsOnFailure()) {
                        while (maxIndexToStore < results.length && coderId == results[maxIndexToStore].getCoderId() &&
                                roundId == results[maxIndexToStore].getRoundId() &&
                                componentId == results[maxIndexToStore].getComponentId() &&
                                maxSTVersion == results[maxIndexToStore].getSystemTestVersion()) {
                            maxIndexToStore++;
                        }
                    }
                }

                ReadWriteLock.Lock lock = resultsStorageLocks.getLock(roundId, componentId, coderId, failed);
                //We have to do this to avoid deadlock when getting connections and locks
                if (!lock.tryLock()) {
                    if (conn != null) {
                        DBMS.close(conn);
                        conn = null;
                    }
                    lock.lock();
                }
                try {
                    if (conn == null) {
                        conn = DBMS.getConnection();
                    }
                    long[] componentStateAndPointsForSubmission = getComponentStateAndPointsForSubmission(conn, roundId, componentId, coderId);
                    long componentStateID = componentStateAndPointsForSubmission[0];
                    int points = (int) componentStateAndPointsForSubmission[1];
                    int updateStatus = updateAndVerifySystemTestVersion(roundId, componentStateID, !failed, maxSTVersion, roundType, conn);
                    boolean mustStoreResult =  updateStatus != -1;
                    if (updateStatus == 1) {
                        points = 0;
                    }
                    if (mustStoreResult) {
                        while (start < maxIndexToStore) {
                            //If test succeeded or we updated the state on failure.. We must store the test.
                            start = recordSystemTestResultsWithFailure(conn, points, results, start, maxIndexToStore);
                            if (start != maxIndexToStore) {
                                if (failed && start > firstFailedTest) {
                                    points = 0;
                                }
                                //Something failed while storing test at start position, skip it... Just continue with following test.
                                start++;
                                //Refresh the connection.. Something could happen with it.
                                DBMS.close(conn);
                                conn = DBMS.getConnection();
                            }
                        }
                    }
                } catch (SQLException e) {
                    s_trace.error("Exception while obtaning component state information coderId="+coderId+" componentId="+componentId, e);
                    s_trace.info("Processing rest of the results of other coders. Resetting connection");
                    DBMS.close(conn);
                    conn = null;
                } finally {
                    lock.unlock();
                }
                //We skip the rest of the test cases for the component state.
                while (start < results.length &&
                        coderId == results[start].getCoderId() &&
                        roundId == results[start].getRoundId() &&
                        componentId == results[start].getComponentId()) {
                    start++;
                }
            }
            if (s_trace.isDebugEnabled()) {
                s_trace.debug("recordSystemTestResult took "+(System.currentTimeMillis() - st));
            }
        } catch (Exception e) {
            s_trace.error("Unexpected exception storing system test results.", e);
            throw new TestServicesException("Could not stored all tests results." + e.getMessage());
        } finally {
            DBMS.close(conn);
        }
    }

    /**
     * Records system test results in case of failure.
     *
     * @param conn DB connection.
     * @param points Points.
     * @param results Results.
     * @param start Start index.
     * @param maxIndexToStore Maximal index to store.
     * @return Updated start index.
     */
    private int recordSystemTestResultsWithFailure(Connection conn, int points,
            SystemTestResult[] results, int start, int maxIndexToStore) {
        PreparedStatement ps = null;
        // INSERT INTO SYSTEM_TEST_RESULTS
        String sqlCommand = "INSERT INTO system_test_result (coder_id, round_id, "
                + "component_id, test_case_id, received, deduction_amount, succeeded, processing_time, "
                + "failure_type_id, message) values (?,?,?,?,?,?,?,?,?,?)";
        try {
            ps = conn.prepareStatement(sqlCommand);
            while (start < maxIndexToStore) {
                SystemTestResult res = results[start];
                if (s_trace.isDebugEnabled()) {
                    s_trace.debug("Storing result: " + res);
                }
                ps.setInt(1, res.getCoderId());
                ps.setInt(2, res.getRoundId());
                ps.setInt(3, res.getComponentId());
                ps.setInt(4, res.getTestCaseId());
                if (res.getResultObj() != null && !res.isSucceeded()) {
                    ps.setBytes(5, DBMS.serializeBlobObject(res.getResultObj()));
                } else {
                    ps.setNull(5, Types.BINARY);
                }
                ps.setDouble(6, res.isSucceeded() ? 0 : -((double) points / 100.0));
                ps.setBoolean(7, res.isSucceeded());
                ps.setDouble(8, res.getExecTime());
                if (res.getFailure_reason() == 0) {
                    ps.setNull(9, Types.DECIMAL);
                } else {
                    ps.setInt(9, res.getFailure_reason());
                }
                if (res.getMessage() == null) {
                    ps.setNull(10, Types.VARCHAR);
                } else {
                    ps.setString(10, res.getMessage());
                }
                ps.executeUpdate();
                start++;
            }
        } catch (Exception e) {
            s_trace.error("Could not insert system test result: " + results[start], e);
        } finally {
            DBMS.close(ps);
        }
        return start;
    }

    /**
     * The recordSystemTestResult is responsible for recording the results
     * of a particular test case.
     *
     * @param contestId Contest ID.
     * @param coderId Coder ID.
     * @param roundId Round ID.
     * @param componentId Component ID.
     * @param testCaseId Test case ID.
     * @param resultObj Result object.
     * @param succeeded Succeeded.
     * @param execTime Execution time.
     * @param failure_reason Failure reason code.
     * @param systemTestVersion System test version.
     * @param message Failure message.
     * @throws TestServicesException If service error occurs.
     */
    public void recordSystemTestResult(int contestId, int coderId, int roundId, int componentId, int testCaseId,
        Object resultObj, boolean succeeded, double execTime, int failure_reason, int systemTestVersion, String message)
        throws TestServicesException {
        info("recordSystemTestResult(" + contestId + ", " + coderId + ", " + roundId + ", " + componentId
                + ", " + testCaseId + ", " + resultObj + ", " + succeeded + ", " + systemTestVersion
                + ", " + message + " )");
        Connection conn = null;
        ReadWriteLock.Lock lock = null;
        try {
            lock = resultsStorageLocks.getLock(roundId, componentId, coderId, !succeeded);
            lock.lock();
            conn = DBUtils.initDBBlock();
            long[] componentStateAndPointsForSubmission = getComponentStateAndPointsForSubmission(conn, roundId, componentId, coderId);
            long componentStateID = componentStateAndPointsForSubmission[0];
            int points = (int) componentStateAndPointsForSubmission[1];
            RoundType roundType = RoundType.get(roundDao.getRoundTypeId(roundId, conn));
            int updateStatus = updateAndVerifySystemTestVersion(roundId, componentStateID, succeeded, systemTestVersion, roundType, conn);
            if (updateStatus != -1) {
                if (updateStatus == 1) {
                    points = 0;
                }
                //If test succeeded or we updated the state on failure.. We must store the test.
                recordSystemTestResultWithFailure(conn, coderId, roundId, componentId, testCaseId,
                        resultObj, points, succeeded, execTime, failure_reason, message);
            }
        } catch (Exception e) {
            s_trace.error("Exception while storing system test result");
            printException(e);
            throw new TestServicesException(e.getMessage());
        } finally {
            resultsStorageLocks.safeUnlock(lock);
            DBUtils.endDBBlock();
        }
    }

    /**
     * Verifies that is possible to store system test results with the given version.
     * Updates the system test version to its negative value if the succeeded argument is false
     * 
     * @return -1 if the results should not be stored
     *          0 if the results should be stored
     *          1 if the results should be stored, no deduction of points should be stored with the results
     */
    private int updateAndVerifySystemTestVersion(int roundId, long componentStateID, boolean succeeded, int systemTestVersion, RoundType roundType, Connection conn) throws SQLException {
        int result = 0;
        boolean mustValidateVersion = succeeded;
        if (!succeeded) {
            //We try to update the state, if we fail some other test failed first. 
            if (!updateComponentStateAfterSystemTestFailure(conn, componentStateID, systemTestVersion)) {
                //If the roundType requires full testing  we must store the result anyway, otherwise we exit
                if (roundType.mustStopSystemTestsOnFailure()) {
                    s_trace.info("Dropping Failed System Test, other test failed first.");
                    return -1;
                } else {
                    //We know the system test version has changed, we need to verify it is only a 
                    //a failure (negative value) and not another testing procedure (different number)
                    mustValidateVersion = true;
                    result = 1;
                }
            }
        }
        if (mustValidateVersion) {
            int currentSystemTestVersion = getCurrentSystemTestVersion(conn, componentStateID);
            //We must check the systemTestflag is the same than the one in component state;
            if (currentSystemTestVersion != systemTestVersion) {
                if (roundType.mustStopSystemTestsOnFailure() || currentSystemTestVersion != -systemTestVersion) {
                    s_trace.info("Dropping System Test, system test version changed.");
                    return -1;
                }
            }
        }
        return result;
    }

    /**
     * Updates component state status of all "ComponentStates" of the given round. <p>
     *
     * Status is set to {@link ContestConstants#SYSTEM_TEST_SUCCEEDED} or {@link ContestConstants#SYSTEM_TEST_FAILED}
     * if all system tests for the related submission succeeded or not
     * and if the current status of the component state is
     * {@link ContestConstants#NOT_CHALLENGED} or {@link ContestConstants#CHALLENGE_FAILED}.
     *
     * If status is changed to SYSTEM_TEST_FAILED, points are deducted from the room.
     *
     * @param roundId The if of the round.
     * @throws TestServicesException If the update process failed.
     */
    public void updateComponentStateAndPointsFromSystemTestResults(int roundId) throws TestServicesException {
        info("updateComponentStateAndPointsFromSystemTestResults(" + roundId + ")");
        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psFailed = null;
        ResultSet rs = null;
        try {
            conn = DBMS.getConnection();
            String sqlCmd = "SELECT a.component_id, a.component_state_id, a.points, a.system_test_version, a.coder_id, count(*) "+
                            " FROM component_state a, system_test_result b "+
                            "  WHERE a.round_id = ? AND a.status_id IN (" + ContestConstants.NOT_CHALLENGED + "," + ContestConstants.CHALLENGE_FAILED + ")" +
                            "        AND b.round_id = a.round_id AND b.coder_id = a.coder_id AND b.component_id = a.component_id "+
                            "  GROUP BY 1, 2, 3, 4, 5 ORDER BY 1, 2";

            String updateSuccessCmd = "UPDATE component_state SET status_id = ? " +
                               " WHERE component_state_id = ? AND (status_id = ? OR status_id = ?)";
            psUpdate = conn.prepareStatement(updateSuccessCmd);
            psUpdate.setInt(3, ContestConstants.NOT_CHALLENGED);
            psUpdate.setInt(4, ContestConstants.CHALLENGE_FAILED);

            String updateFailedCmd = "UPDATE component_state SET status_id = ?, points = 0 " +
                                " WHERE component_state_id = ? AND (status_id = ? OR status_id = ?)";
            psFailed = conn.prepareStatement(updateFailedCmd);
            psFailed.setInt(3, ContestConstants.NOT_CHALLENGED);
            psFailed.setInt(4, ContestConstants.CHALLENGE_FAILED);

            ps = conn.prepareStatement(sqlCmd);
            ps.setInt(1, roundId);
            rs = ps.executeQuery();
            int lastComponentId = 0;
            int expectedTestCases = 0;
            while (rs.next()) {
                int componentId = rs.getInt(1);
                long componentStateId = rs.getLong(2);
                int points = (int) Math.round(rs.getDouble(3) * 100);
                int systemFlag = rs.getInt(4);
                int coderId = rs.getInt(5);
                int testCases = rs.getInt(6);

                //If a system test failed, the flag is <= 0
                if (systemFlag <= 0) {
                    psFailed.setInt(1, ContestConstants.SYSTEM_TEST_FAILED);
                    psFailed.setLong(2, componentStateId);
                    psFailed.executeUpdate();
                    deductPointsFromRoomResult(conn, roundId, coderId, points);
                } else {
                    if (componentId != lastComponentId) {
                        lastComponentId = componentId;
                        expectedTestCases = getTestCasesCount(conn, componentId);
                    }
                    //If no test failed, we may still have pending tests or a db failure. Only if #test cases match
                    //we update the status
                    if (testCases == expectedTestCases) {
                        psUpdate.setInt(1, ContestConstants.SYSTEM_TEST_SUCCEEDED);
                        psUpdate.setLong(2, componentStateId);
                        psUpdate.executeUpdate();
                    } else {
                        s_trace.warn("Invalid number of test cases for componentState=" + componentStateId);
                    }
                }
            }
        } catch (Exception e) {
            printException(e);
            throw new TestServicesException(e.getMessage());
        } finally {
            DBMS.close(psUpdate);
            DBMS.close(psFailed);
            close(conn, ps, rs);
        }
    }

    private long[] getComponentStateAndPointsForSubmission(Connection conn, int roundId, int componentId, int coderId) throws SQLException {
        StringBuilder sqlStr = new StringBuilder();
        PreparedStatement ps = null;
        ResultSet rs = null;
        // First get the points that were earned for the submission
        sqlStr.append("SELECT points, component_state_id FROM component_state");
        sqlStr.append(" WHERE round_id = ? AND component_id = ? AND coder_id = ?");
        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            ps.setInt(2, componentId);
            ps.setInt(3, coderId);
            rs = ps.executeQuery();
            rs.next();
            long points = Math.round(rs.getDouble(1) * 100);
            long componentStateId = rs.getLong(2);
            return new long[]{componentStateId, points};
        } finally {
            close(null, ps, rs);
        }
    }

    private void deductPointsFromRoomResult(Connection conn, int roundId, int coderId, int points) throws SQLException {
        StringBuilder sqlStr = new StringBuilder();
        PreparedStatement ps = null;
        sqlStr.replace(0, sqlStr.length(), "UPDATE room_result SET ");
        sqlStr.append("point_total = point_total - ? ");
        sqlStr.append("WHERE round_id = ? AND coder_id = ?");
        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setDouble(1, (double) points / 100.0);
            ps.setInt(2, roundId);
            ps.setInt(3, coderId);
            ps.executeUpdate();
        } finally {
            close(null, ps, null);
        }
    }

    private boolean updateComponentStateAfterSystemTestFailure(Connection conn, long componentStateID, long systemTestVersion) throws SQLException {
        PreparedStatement ps = null;
        StringBuilder sqlStr = new StringBuilder();
        try {
            sqlStr.replace(0, sqlStr.length(), "UPDATE component_state SET system_test_version = ?");
            sqlStr.append("WHERE component_state_id = ? AND system_test_version = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setLong(1, -systemTestVersion);
            ps.setLong(2, componentStateID);
            ps.setLong(3, systemTestVersion);
            return ps.executeUpdate() == 1;
        } finally {
            close(null, ps, null);
        }
    }

    public int getCurrentSystemTestVersion(int contestId, int coderId, int roundId, int componentId) throws TestServicesException {
        info("getCurrentSystemTestVersion(" + contestId + ", " + coderId + ", " + roundId + ", " + componentId + ")");
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            long csId = getComponentStateID(coderId, roundId, componentId, conn);
            int systemTestVersion = getCurrentSystemTestVersion(conn, csId);
            return systemTestVersion;
        } catch (SQLException e) {
            printException(e);
            throw new TestServicesException(e.getMessage());
        } finally {
            close(conn, null, null);
        }
    }

    public Set getPendingSystemTestCaseIdsForSubmission(int contestId, int coderId, int roundId, int componentId) throws TestServicesException, RemoteException {
        info("getPendingSystemTestCaseIdsForSubmission(" + contestId + ", " + coderId + ", " + roundId + ", " + componentId + ")");
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sqlCmd = "SELECT test_case_id FROM system_test_case st WHERE component_id = ? " +
                        "           AND status = 1 AND NOT EXISTS (SELECT coder_id FROM system_test_result str" +
                        "               WHERE round_id = ? AND component_id = ? AND coder_id = ? AND str.test_case_id = st.test_case_id) ORDER BY st.test_number, st.test_case_id";
        HashSet ids = new HashSet(50);
        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(sqlCmd);
            ps.setInt(1, componentId);
            ps.setInt(2, roundId);
            ps.setInt(3, componentId);
            ps.setInt(4, coderId);
            rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(new Integer(rs.getInt(1)));
            }
            return ids;
        } catch (SQLException e) {
            printException(e);
            throw new TestServicesException("Failed to obtaing pending system test cases");
        } finally {
            close(conn, ps, rs);
        }
    }

    public void deleteSystemTestResultsAndFixVersion(int roundId, int testCaseId) throws TestServicesException {
        info("deleteSystemTestResultsAndFixVersion(" + roundId + ", " + testCaseId + ")");
        Connection conn = null;
        PreparedStatement ps = null;

        String componentStateFixCmd = "UPDATE component_state SET component_state.system_test_version = ABS(component_state.system_test_version) " +
                                      "   WHERE component_state.round_id = ? " +
                                      "         AND EXISTS (SELECT str.round_id FROM system_test_result str" +
                                      "                         WHERE str.round_id = component_state.round_id " +
                                      "                                 AND str.component_id = component_state.component_id " +
                                      "                                 AND str.coder_id = component_state.coder_id " +
                                      "                                 AND str.test_case_id = ? " +
                                      "                                 AND succeeded = ?)";

        String deleteCmd = "DELETE FROM system_test_result WHERE round_id = ? AND test_case_id = ?";

        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(componentStateFixCmd);
            ps.setInt(1, roundId);
            ps.setInt(2, testCaseId);
            ps.setBoolean(3, false);
            ps.execute();
            ps.close();

            ps = conn.prepareStatement(deleteCmd);
            ps.setInt(1, roundId);
            ps.setInt(2, testCaseId);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            printException(e);
            throw new TestServicesException("Failed to delete system test results for the given test case");
        } finally {
            close(conn, ps, null);
        }
    }

    /**
     * Returns the current system test version value for the component state.
     */
    private int getCurrentSystemTestVersion(Connection conn, long componentStateID) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //We get the last id used to run system tests for the component
            String selectSQL = "SELECT system_test_version FROM component_state " +
                               "     WHERE component_state_id = ?";

            ps = conn.prepareStatement(selectSQL);
            ps.setLong(1, componentStateID);
            rs = ps.executeQuery();
            rs.next();
            int currentFlag = rs.getInt(1);
            if (s_trace.isDebugEnabled()) {
                s_trace.debug("Current flag for componentStateId=" + componentStateID + " is " + currentFlag);
            }
            return currentFlag;
        } finally {
            close(null, ps, rs);
        }
    }

    /**
     * Sets the new system test version to the component state. The value is only updated if the current value of
     * the flag is <code>nextSystestFlag</code> or <code>-nextSystestFlag</code> (No other flag was generated)
     * and the status of the component is {@link ContestConstants#NOT_CHALLENGED} or {@link ContestConstants#CHALLENGE_FAILED}.
     *
     * If <code>checkComponentStatus</code> is false, the flag is updated without any check
     *
     * @return true if the component state was successfully updated
     */
    private boolean updateNewSystemTestVersion(Connection conn, long componentStateID, int nextSystestFlag, boolean checkComponentStatus) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //  We update the last id used to run system test on the component
            String updateSQL = "UPDATE component_state SET system_test_version = ? " +
                               "     WHERE component_state_id = ? " +
                               (checkComponentStatus ? "           AND ABS(system_test_version) = ? AND status_id IN (?,?)" : "");
            ps = conn.prepareStatement(updateSQL);
            ps.setInt(1, nextSystestFlag);
            ps.setLong(2, componentStateID);
            if (checkComponentStatus) {
                ps.setInt(3, nextSystestFlag - 1);
                ps.setInt(4, ContestConstants.NOT_CHALLENGED);
                ps.setInt(5, ContestConstants.CHALLENGE_FAILED);
            }
            return ps.executeUpdate() == 1;
        } finally {
            close(null, ps, rs);
        }
    }

    /**
     * Returns the number of active test cases for the given component
     *
     * @param conn The connection to use
     * @param componentId The component
     * @return The number of test cases for the given component
     * @throws SQLException If an exception is thrown during the process
     */
    private int getTestCasesCount(Connection conn, int componentId) throws SQLException {
        s_trace.debug("getTestCasesCount(" + componentId + ")");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sqlCmd = "SELECT COUNT(*) FROM system_test_case " +
                            "   WHERE component_id = ? AND status = 1";
            ps = conn.prepareStatement(sqlCmd);
            ps.setInt(1, componentId);
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } finally {
            close(null, ps, rs);
        }
    }

    /**
     * This method is responsible for recording the results
     * of a particular test case.
     *
     * @param contestId Contest ID.
     * @param coderId Coder ID.
     * @param roundId Round ID.
     * @param componentId Component ID.
     * @param testCaseId Test case ID.
     * @param resultObj Result object.
     * @param succeeded Succeeded.
     * @param execTime Execution time.
     * @param failure_reason Failure reason code.
     * @param systemTestVersion System test version.
     * @param message Failure message.
     * @throws SQLException If DB error occurs.
     * @throws TestServicesException If service error occurs.
     */
    private void recordSystemTestResultWithFailure(Connection conn, int coderId, int roundId, int componentId,
        int testCaseId, Object resultObj, int points, boolean succeeded,
        double execTime, int failure_reason, String message)
        throws SQLException, TestServicesException {
        StringBuilder sqlStr = new StringBuilder();
        PreparedStatement ps = null;
        // INSERT INTO SYSTEM_TEST_RESULTS
        sqlStr.replace(0, sqlStr.length(), "INSERT INTO system_test_result (coder_id, round_id, ");
        sqlStr.append("component_id, test_case_id, received, deduction_amount, succeeded, ");
        sqlStr.append("processing_time, failure_type_id, message) values (?,?,?,?,?,?,?,?,?,?)");
        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, coderId);
            ps.setInt(2, roundId);
            ps.setInt(3, componentId);
            ps.setInt(4, testCaseId);
            if (resultObj != null && !succeeded) {
                ps.setBytes(5, DBMS.serializeBlobObject(resultObj));
            } else {
                ps.setNull(5, Types.BINARY);
            }
            ps.setDouble(6, succeeded ? 0 : -((double) points / 100.0));
            ps.setBoolean(7, succeeded);
            ps.setDouble(8, execTime);
            if (failure_reason == 0) {
                ps.setNull(9, Types.DECIMAL);
            } else {
                ps.setInt(9, failure_reason);
            }
            if (message == null) {
                ps.setNull(10, Types.VARCHAR);
            } else {
                ps.setString(10, message);
            }
            ps.executeUpdate();
        } finally {
            close(null, ps, null);
        }
    }

    private boolean duplicateChallenge(Connection conn, ChallengeAttributes chal) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT args FROM challenge WHERE defendant_id = ? AND challenger_id = ? AND round_id = ? AND component_id = ?");
            ps.setInt(1, chal.getDefendantId());
            ps.setInt(2, chal.getChallengerId());
            ps.setInt(3, chal.getLocation().getRoundID());
            ps.setInt(4, chal.getComponentId());
            rs = ps.executeQuery();
            while (rs.next()) {
                Object o = DBMS.getBlobObject(rs, 1);
                if (o instanceof List) {
                    List dbArgs = (List) o;
                    Object curArgs = chal.getArgs();
                    if (Processor.argsEqual(dbArgs.toArray(), curArgs)) {
                        return true;
                    }
                }
            }
            return false;
        } finally {
            close(null, ps, rs);
        }
    }

    private boolean updateChallengeStatus(boolean succeeded, long componentStateID, boolean enforceCorrectSatus, java.sql.Connection conn) throws SQLException {
        PreparedStatement ps = null;
        try {
            String sqlCmd = null;
            if (enforceCorrectSatus) {
                sqlCmd = "UPDATE component_state SET status_id = ? WHERE component_state_id = ? AND status_id IN (?,?)";
            } else {
                sqlCmd = "UPDATE component_state SET status_id = ? WHERE component_state_id = ?";
            }
            ps = conn.prepareStatement(sqlCmd);
            if (succeeded) {
                ps.setInt(1, ContestConstants.CHALLENGE_SUCCEEDED);
            } else {
                ps.setInt(1, ContestConstants.CHALLENGE_FAILED);
            }
            ps.setLong(2, componentStateID);
            if (enforceCorrectSatus) {
                ps.setInt(3, ContestConstants.NOT_CHALLENGED);
                ps.setInt(4, ContestConstants.CHALLENGE_FAILED);
            }
            return ps.executeUpdate() == 1;
        } finally {
            close(null, ps, null);
        }
    }

    /**
     * Persists a challnge record.
     *
     * @param ca Challenge attributes.
     * @param conn DB connection.
     * @throws SQLException If DB error occurs.
     * @throws TestServicesException If service error occurs.
     * @throws IDGenerationException If ID generation error occurs.
     */
    private void recordChallengeRecord(ChallengeAttributes ca, Connection conn)
        throws SQLException, TestServicesException, IDGenerationException {
        StringBuilder sb = new StringBuilder(128);
        PreparedStatement ps = null;
        sb.append("INSERT INTO challenge (challenge_id, challenger_id, defendant_id, round_id, "
                + "component_id, succeeded, ");
        sb.append("challenger_points, defendant_points, message, submit_time, args, expected, "
                + "received, status_id, check_answer_response) ");
        sb.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        try {
            ps = conn.prepareStatement(sb.toString());
            ps.setInt(1, IdGeneratorClient.getSeqIdAsInt(DBMS.CHALLENGE_SEQ));
            ps.setInt(2, ca.getChallengerId());
            ps.setInt(3, ca.getDefendantId());
            ps.setInt(4, ca.getLocation().getRoundID());
            ps.setInt(5, ca.getComponentId());

            if (ca.isSuccesfulChallenge()) {
                ps.setInt(6, 1);
                ps.setDouble(7, (double) ca.getChalValue() / 100.0);
                ps.setDouble(8, -1 * ((double) ca.getPointValue() / 100.0));
                ps.setString(9, ca.getChallengeHistoryMessage());
            } else {
                ps.setInt(6, 0);
                ps.setDouble(7, (double) ca.getPenaltyValue() / 100.0);
                ps.setDouble(8, 0);
                ps.setString(9, ca.getChallengeHistoryMessage());
            }

            Object expectedResult = checkValidResult("Expected result", ca.getExpectedResult());
            Object resultValue = checkValidResult("Result value", ca.getResultValue());

            ca.getComponent().getParamTypes();
            ps.setLong(10, System.currentTimeMillis());
            ps.setBytes(11, DBMS.serializeBlobObject(new ArrayList(Arrays.asList(ca.getArgs())))); // TODO ugly - break these up
            ps.setBytes(12, DBMS.serializeBlobObject(expectedResult));
            ps.setBytes(13, DBMS.serializeBlobObject(resultValue));
            ps.setInt(14, ContestConstants.NORMAL_CHALLENGE);
            ps.setString(15, ca.getCheckAnswerResponse());
            ps.executeUpdate();
        } finally {
            close(null, ps, null);
        }
    }

    private Object checkValidResult(String field, Object value) {
        if (value == null) {
            s_trace.error("CHALLENGE " + field + " IS NULL. THIS SHOULD NEVER HAPPEN!!!. Storing as Constants.makePretty(value)");
            value = ContestConstants.makePretty(value);
        }
        return value;
    }

    private void penalizeDefendant(ChallengeAttributes ca, java.sql.Connection conn) throws SQLException {
        // Make sure that the challenge was truely successful...
        // if not we don't want to penalize the defendant
        if (!ca.isSuccesfulChallenge()) {
            return;
        }

        StringBuilder sb = new StringBuilder(128);
        PreparedStatement ps = null;

        sb.append("UPDATE component_state SET points = 0 WHERE round_id = ? AND coder_id = ? AND component_id = ? ");

        try {
            ps = conn.prepareStatement(sb.toString());
            ps.setInt(1, ca.getLocation().getRoundID());
            ps.setInt(2, ca.getDefendantId());
            ps.setInt(3, ca.getComponentId());
            ps.executeUpdate();
            deductPointsFromRoomResult(conn, ca.getLocation().getRoundID(), ca.getDefendantId(), ca.getPointValue());
        } finally {
            close(null, ps, null);
        }
    }

    private void updateChallenger(ChallengeAttributes ca, java.sql.Connection conn) throws SQLException, TestServicesException {
        StringBuilder sb = new StringBuilder(128);
        PreparedStatement ps = null;
        //sb.append("UPDATE room_result SET point_total = point_total + cast(? as numeric(7,2)) ");
        sb.append("UPDATE room_result SET point_total = point_total + ? ");
        sb.append("WHERE round_id = ? AND coder_id = ?");
        try {
            ps = conn.prepareStatement(sb.toString());
            if (ca.isSuccesfulChallenge()) {
                ps.setDouble(1, (double) ca.getChalValue() / 100.0);
            } else {
                ps.setDouble(1, (double) ca.getPenaltyValue() / 100.0);
            }
            ps.setInt(2, ca.getLocation().getRoundID());
            ps.setInt(3, ca.getChallengerId());
            int check = ps.executeUpdate();
            if (check != 1) {
                throw new TestServicesException("Expected 1 row to be updated: " + check);
            }
        } finally {
            close(null, ps, null);
        }
    }

    private int getSystestsLeft(Connection conn, int roundID) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlStr;
        String sqlStr1;
        if (DBMS.DB == DBMS.INFORMIX) {
            sqlStr = "SELECT COUNT(*) FROM component_state cs, system_test_case stc" +
                         " WHERE cs.component_id = stc.component_id AND cs.round_id = ?" +
                         "  AND cs.system_test_version >= 0 " +
                         "  AND cs.status_id IN (" + ContestConstants.NOT_CHALLENGED + "," + ContestConstants.CHALLENGE_FAILED + ")" +
                         "  AND cs.coder_id NOT IN (SELECT user_id FROM group_user WHERE group_id IN (" + ServerContestConstants.ADMIN_LEVEL_ONE_GROUP_ID + "," + ServerContestConstants.ADMIN_LEVEL_TWO_GROUP_ID + "))";

            sqlStr1 = "SELECT COUNT(*) FROM system_test_result stc" +
                      " WHERE round_id = ? and EXISTS (SELECT * FROM component_state st " +
                      "                                 WHERE st.round_id=stc.round_id " +
                      "                                         AND st.coder_id=stc.coder_id AND st.component_id = stc.component_id " +
                      "                                         AND st.system_test_version >= 0 " +
                      "                                         AND st.coder_id NOT IN (SELECT user_id FROM group_user WHERE group_id IN (" + ServerContestConstants.ADMIN_LEVEL_ONE_GROUP_ID + "," + ServerContestConstants.ADMIN_LEVEL_TWO_GROUP_ID + "))" +
                      "                                         AND status_id IN (" + ContestConstants.NOT_CHALLENGED + "," + ContestConstants.CHALLENGE_FAILED + "))";
        } else {
            sqlStr = "SELECT COUNT(*) FROM component_state cs, system_test_case stc" +
                     " WHERE cs.component_id = stc.component_id AND cs.round_id = ?" +
                     " AND cs.system_test_version >= 0 " +
                     " AND cs.status_id IN (" + ContestConstants.NOT_CHALLENGED + "," + ContestConstants.CHALLENGE_FAILED + ")";

            sqlStr1 = "SELECT COUNT(*) FROM system_test_result stc" +
                      " WHERE round_id = ? and EXISTS (SELECT * FROM component_state st " +
                      "                                 WHERE st.round_id=stc.round_id " +
                      "                                         AND st.coder_id=stc.coder_id AND st.component_id = stc.component_id " +
                      "                                         AND cs.system_test_version >= 0 " +
                      "                                         AND status_id IN (" + ContestConstants.NOT_CHALLENGED + "," + ContestConstants.CHALLENGE_FAILED + "))";
        }

        try {
            ps = conn.prepareStatement(sqlStr);
            ps.setInt(1, roundID);
            rs = ps.executeQuery();
            int totalCases = 0;
            if (rs.next()) {
                totalCases = rs.getInt(1);
            }
            rs.close();
            ps.close();

            ps = conn.prepareStatement(sqlStr1);
            ps.setInt(1, roundID);
            rs = ps.executeQuery();
            int readyCases = 0;
            if (rs.next()) {
                readyCases = rs.getInt(1);
            }
            return totalCases - readyCases;
        } finally {
            DBMS.close(null, ps, rs);
        }
    }

    public int getSystestsLeft(int roundID) throws TestServicesException {
        Connection connection = null;
        try {
            connection = DBMS.getConnection();
            return getSystestsLeft(connection, roundID);
        } catch (SQLException e) {
            printException(e);
            throw new TestServicesException(e.getMessage());
        } finally {
            close(connection, null, null);
        }
    }

    public int getTotalSystests(int roundID) throws TestServicesException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBMS.getConnection();
            String sqlStr;
            if (DBMS.DB == DBMS.INFORMIX) {
                sqlStr = "SELECT COUNT(*) FROM component_state cs, system_test_case stc" +
                             " WHERE cs.component_id = stc.component_id AND cs.round_id = ?" +
                             "  AND cs.status_id IN (" + ContestConstants.NOT_CHALLENGED + "," + ContestConstants.CHALLENGE_FAILED + ")" +
                             "  AND cs.coder_id NOT IN (SELECT user_id FROM group_user WHERE group_id IN (" + ServerContestConstants.ADMIN_LEVEL_ONE_GROUP_ID + "," + ServerContestConstants.ADMIN_LEVEL_TWO_GROUP_ID + "))";
            } else {
                sqlStr = "SELECT COUNT(*) FROM component_state cs, system_test_case stc" +
                         " WHERE cs.component_id = stc.component_id AND cs.round_id = ?" +
                         " AND cs.status_id IN (" + ContestConstants.NOT_CHALLENGED + "," + ContestConstants.CHALLENGE_FAILED + ")";
            }
            ps = conn.prepareStatement(sqlStr);
            ps.setInt(1, roundID);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            printException(e);
            throw new TestServicesException(e.getMessage());
        } finally {
            close(conn, ps, rs);
        }
    }

    /**
     * The recordCompileStatus is responsible for recording the start-time, status,
     * and stop-time of the compilation procedure.
     *
     * @param sub        Submission object containing the users submission debug
     */
    public void recordCompileStatus(CodeCompilation sub) throws TestServicesException {
        debug("recordCompileStatus");
        java.sql.Connection conn = null;
        int roundId = sub.getRoundID();
        int componentId = sub.getComponentID();
        int coderId = sub.getCoderID();
        long currentTime = (new Date()).getTime();
        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            long componentStateID = getComponentStateID(coderId, roundId, componentId, conn);
            saveCompilation(conn, sub.getProgramText(), componentStateID, sub.getLanguage(), sub.getCompileStatus(), currentTime);
            if (sub.getClassFiles() != null) {
                List classFiles = sub.getClassFiles().getClassFiles();
                debug("sub.getClassFiles() != null, size = " + classFiles.size());
                clearCompiledClasses(conn, componentStateID);
                saveCompiledClassFiles(conn, componentStateID, classFiles);
            } else {
                debug("sub.getClassFiles() == null");
            }
            //updateComponentStateAfterCompilation(conn, sub.getLanguage(), componentStateID);
            updateComponentStateAfterCompilation(conn, componentStateID);
            conn.commit();
        } catch (Exception e) {
            rollback(conn);
            printException(e);
            throw new TestServicesException(e.getMessage());
        } finally {
            close(conn, null, null);
        }
    }

    private void updateComponentStateAfterCompilation(Connection conn, long componentStateID) throws SQLException {
        StringBuilder sqlStr = new StringBuilder();
        PreparedStatement ps = null;
        sqlStr.append("UPDATE component_state SET status_id = ?");
        sqlStr.append("WHERE component_state_id = ? ");
        //sqlStr.append("AND status_id not in (?,?) ");
        sqlStr.append("AND status_id not in (?) ");
        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, ContestConstants.COMPILED_UNSUBMITTED);
            //ps.setInt(2, languageID);
            ps.setLong(2, componentStateID);
            ps.setInt(3, ContestConstants.NOT_CHALLENGED);
            //ps.setInt(4, ContestConstants.CHALLENGE_FAILED);
            ps.executeUpdate();
        } finally {
            close(null, ps, null);
        }
    }
    /**
     * Save the compilation result.
     * @param con the connection entity.
     * @param programText the program text
     * @param componentStateID the component state id.
     * @param languageID the language id.
     * @param compilationStatus the compilation status.
     * @param compiledTime the compiled time.
     * @throws SQLException if any SQL error occur.
     */
    private void saveCompilation(Connection con, String programText, long componentStateID, int languageID,
            boolean compilationStatus, long compiledTime) throws SQLException {
        StringBuilder sqlStr = new StringBuilder();
        PreparedStatement ps = null;
        sqlStr.append("UPDATE compilation SET compilation_text = ?, language_id = ?, saved_time = ?");
        if (compilationStatus) {
            sqlStr.append(", compiled_time = ?");
        }
        sqlStr.append("WHERE component_state_id = ?");
        try {
            ps = con.prepareStatement(sqlStr.toString());
            ps.setBytes(1, programText.getBytes());
            ps.setLong(2, languageID);
            //Compilation will save the submission
            ps.setLong(3, compiledTime);
            if (compilationStatus) {
                ps.setLong(4, compiledTime);
                ps.setLong(5, componentStateID);
            } else {
                ps.setLong(4, componentStateID);
            }
            
            ps.executeUpdate();
        } finally {
            close(null, ps, null);
        }
    }

    private void saveCompiledClassFiles(Connection con, long componentStateID, List classFiles) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("INSERT INTO compilation_class_file (component_state_id,sort_order,path,class_file) VALUES (?,?,?,?)");
            ps.setLong(1, componentStateID);
            int k = 1;
            for (Iterator it = classFiles.iterator(); it.hasNext();) {
                RemoteFile file = (RemoteFile) it.next();
                debug("saving " + file.getPath());
                ps.setInt(2, k++);
                ps.setString(3, file.getPath());
                ps.setBytes(4, file.getContents());
                ps.executeUpdate();
                debug("saved ");
            }
        } finally {
            close(null, ps, null);
        }
    }

    private void clearCompiledClasses(Connection con, long componentStateID) throws SQLException {
        PreparedStatement ps2 = null;
        try {
            ps2 = con.prepareStatement("DELETE FROM compilation_class_file WHERE component_state_id = ?");
            ps2.setLong(1, componentStateID);
            ps2.executeUpdate();
        } finally {
            close(null, ps2, null);
        }
    }

    /**
     * Helper function which gets the problemstateid for a given coder's problem
     */
    private long getComponentStateID(long coderId, long roundId, long componentId, Connection conn) throws SQLException {

        PreparedStatement ps = null;
        StringBuilder sqlStr = new StringBuilder(200);
        ResultSet rs = null;

        try {
            sqlStr.append("SELECT component_state_id FROM component_state ");
            sqlStr.append("WHERE round_id = ? AND component_id = ? AND coder_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setLong(1, roundId);
            ps.setLong(2, componentId);
            ps.setLong(3, coderId);
            rs = ps.executeQuery();
            if (!rs.next()) {
                return -1;
            }
            return rs.getLong(1);
        } finally {
            close(null, ps, rs);
        }
    }

    /**
     * This method saves a coder's problem
     * @param contestId the contest id.
     * @param roundId the round id.
     * @param componentId the component id.
     * @param coderId the coder id.
     * @param programText the programe text.
     * @param languageID the language id.
     * @return the save action results
     */
    public Results saveComponent(long contestId, long roundId, long componentId, long coderId, String programText, int languageID) {
        debug("Saving component cID:" + coderId + ",rID:" + roundId + ",pID:" + componentId + ",txt:" + programText + ",lang:" + languageID);
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        StringBuilder sqlStr = new StringBuilder(200);
        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            sqlStr.append("UPDATE compilation SET compilation_text = ?, language_id = ?, saved_time = ? WHERE component_state_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            long componentStateID = getComponentStateID(coderId, roundId, componentId, conn);
            if (componentStateID == -1) {
                s_trace.error("No component state id found?  bad");
                return new Results(false, "Code could not save due to No problem state, contact admin about this");
            }
            ps.setBytes(1, programText.getBytes());
            ps.setInt(2, languageID);
            //Always update the open_time for last_save_time
            long currentTime = (new Date()).getTime();
            ps.setLong(3, currentTime);
            ps.setLong(4, componentStateID);

            int rows = ps.executeUpdate();
            if (rows != 1) {
                s_trace.error("Save failed. updated rows: " + rows);
                rollback(conn);
                return new Results(false, "Your code did not save due to a database error.");
            } else {
                conn.commit();
                return new Results(true, "Code saved successfully.");
            }
        } catch (Exception e) {
            rollback(conn);
            printException(e);
            return new Results(false, "Save failed.");
        } finally {
            close(conn, ps, null);
        }
    }

    /**
     * Given a submission object just save it to the db, assumes its okay
     */
    public SubmitResults replaySubmit(Submission sub) {
        debug("replaySubmit");
        java.sql.Connection conn = null;
        int roundId = sub.getLocation().getRoundID();
        int coderId = sub.getCoderID();
        int componentId = sub.getComponent().getComponentID();
        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            long componentStateID = getComponentStateID(coderId, roundId, componentId, conn);
            int numSubmissions = getNumSubmissions(conn, componentStateID);
            updateComponentState(conn, sub.getUpdatedPoints(), componentStateID, numSubmissions);
            transferCompilationToSubmission(conn, componentStateID);
            updateCoderPoints(conn, roundId, coderId, sub.getUpdatedPoints());
            conn.commit();
            return new SubmitResults(true, "Submitted problem for " + sub.getUpdatedPoints() + " points.", sub.getUpdatedPoints());
        } catch (Exception e) {
            rollback(conn);
            printException(e);
            return new SubmitResults(false, "ERROR while submitting: " + e.getMessage(), 0);
        } finally {
            close(conn, null, null);
        }
    }

    /**
     * This method gets called when a coder submits a problem, sets the updatedPointValue on submission
     * if sucessful, returns false otherwise
     * Takes care of storing whatever needs to be stored on a submission
     *
     * @param sub  Submission object containing the users submission debug
     * @param codingLength Time took for submission
     * @param restrictPerUserTime if it is restrict per user time.
     */
    public SubmitResults submitProblem(Submission sub, long codingLength, boolean restrictPerUserTime) {
//        s_trace.info("submitProblem start");
        debug("submitProblem");

        StringBuilder msg = new StringBuilder();
        java.sql.Connection conn = null;
        int roundId = sub.getLocation().getRoundID();
        int coderId = sub.getCoderID();
        int componentId = sub.getComponent().getComponentID();
        debug(coderId + " " + roundId + " " + componentId);
        long currentTime = System.currentTimeMillis();

        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            boolean isAdmin = false;
            if (!ContestConstants.ACCEPT_MULTIPLE_SUBMISSIONS || restrictPerUserTime) {
                //If we will need this value, we need to fill it with the proper one
                isAdmin = userDao.isAdminUser(coderId, conn);
            }
            ResultSet rs = null;
            PreparedStatement ps = null;
            long componentStateID;
            int status;
            int numSubmissions;
            try {
                String sqlCmd = "SELECT component_state_id, status_id, submission_number FROM component_state " +
                                "WHERE round_id = ? AND component_id = ? AND coder_id = ?";
                ps = conn.prepareStatement(sqlCmd);
                ps.setLong(1, roundId);
                ps.setLong(2, componentId);
                ps.setLong(3, coderId);
                rs = ps.executeQuery();
                rs.next();
                componentStateID = rs.getLong(1);
                status = rs.getInt(2);
                numSubmissions = rs.getInt(3);
            } finally {
                DBMS.close(ps, rs);
            }
            
            if (!canSubmit(status, componentStateID, msg, isAdmin)) {
                return new SubmitResults(false, msg.toString(), 0);
            }

            int pointVal = sub.getRoundPointVal();
            long openTime = getOpenTime(conn, componentStateID);
            int newPointVal = computeSubmissionPoints(conn, componentStateID, currentTime, pointVal, codingLength, numSubmissions, restrictPerUserTime, openTime, isAdmin);

            if (newPointVal == 0) {
                return new SubmitResults(false, "You cannot submit after your time has expired", 0);
            }
            updateComponentState(conn, newPointVal, componentStateID, numSubmissions);

            saveSubmission(conn, componentStateID, numSubmissions, sub.getProgramText(), currentTime, newPointVal, sub.getLanguage(), openTime);
            // Save the class files to the submission table
            transferCompilationToSubmission(conn, componentStateID);
            // Lastly update room_result/coder status
            updateCoderPoints(conn, roundId, coderId, newPointVal);
            sub.setSubmitTime(currentTime);

            conn.commit();
            //s_trace.info("submitProblem end");
            return new SubmitResults(true, "Submitted problem for " + newPointVal + " points.", newPointVal);
        } catch (Exception e) {
            rollback(conn);
            printException(e);
            return new SubmitResults(false, "ERROR while submitting: " + e.getMessage(), 0);
        } finally {
            close(conn, null, null);
        }
    }

    private static boolean exceededCodingLength(Connection conn, long currentTime, long codingLength, long componentStateID) throws TestServicesException {

        long firstOpenCoderCompTimes = 0;
        double elapsedTime = 0;
        boolean elapsed = false;
        try {
            firstOpenCoderCompTimes = getEarliestOpenTimeForAllCoderCompInRound(conn, componentStateID);
            elapsedTime = currentTime - firstOpenCoderCompTimes;
            if (elapsedTime > codingLength) {
                elapsed = true;
            }
        } catch (Exception e) {
            printException(e);
        }
        return elapsed;
    }

    private static long getEarliestOpenTimeForAllCoderCompInRound(Connection conn, long componentStateID) throws SQLException, TestServicesException {

        long earliestOpenTimeForAllCoderCompInRound = 0;

        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder();
        sqlStr.append(" select min(c.open_time) ");
        sqlStr.append(" from compilation c ");
        sqlStr.append(" , component_state cs_orig ");
        sqlStr.append(" , component_state cs_other ");
        sqlStr.append(" where cs_orig.component_state_id = ? ");
        sqlStr.append(" and cs_orig.round_id = cs_other.round_id ");
        sqlStr.append(" and cs_orig.coder_id = cs_other.coder_id ");
        sqlStr.append(" and c.component_state_id = cs_other.component_state_id ");

        try {
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setLong(1, componentStateID);
            rs = ps.executeQuery();
            if (rs.next()) {
                earliestOpenTimeForAllCoderCompInRound = rs.getLong(1);
            } else {
                throw new TestServicesException("No open times based on componentStateID:" + componentStateID);
            }
        } finally {
            close(null, ps, rs);
        }
        return earliestOpenTimeForAllCoderCompInRound;
    }
    private static int computeSubmissionPoints(Connection conn, long componentStateID, long currentTime, int pointVal, final long codingLength, int numSubmissions, boolean restrictPerCoderTime, long openTime, boolean isAdmin) throws SQLException, TestServicesException {
        //s_trace.info("computeSubmissionPoints start");
        try {
            if (openTime > 0) {
                double newPointVal = 0;
                double elapsedTime = currentTime - openTime;
                debug("Submit max points: " + pointVal);
                if (restrictPerCoderTime && exceededCodingLength(conn, currentTime, codingLength, componentStateID) ) {
                    if (isAdmin) {
                        newPointVal = 1;
                    } else {
                        newPointVal = 0;
                    }
                } else {
                    newPointVal = pointVal * (.3 + .7 / (10.0 * Math.pow(elapsedTime / (double) codingLength, 2.0) + 1));
                }
                newPointVal = Formatters.getDouble(newPointVal).doubleValue();
                if (newPointVal > 0 && numSubmissions > 0) {
                    // Penalize them 10%
                    double maxPenalty = pointVal * 3 / 10;
                    maxPenalty = Formatters.getDouble(maxPenalty).doubleValue();
                    double newPenalizedPointValue = newPointVal - ((pointVal * 0.1) * numSubmissions);
                    newPenalizedPointValue = Formatters.getDouble(newPenalizedPointValue).doubleValue();
                    if (newPenalizedPointValue < maxPenalty) {
                        debug("Penalty less that 3/10 no more penalty");
                        newPointVal = maxPenalty;
                    } else {
                        debug("-----AFTER PENALTY, THAT IS REDUCED TO " + newPenalizedPointValue);
                        newPointVal = newPenalizedPointValue;
                    }
                }
                debug("-----THAT SUBMISSION TOOK " + (elapsedTime / 1000) + " SECONDS out of " + (codingLength / (60 * 1000)) + "mins");
                debug("-----" + newPointVal + " POINTS EARNED FOR THAT SUBMISSION");
                return (int) Math.round( newPointVal * 100 );
            } else {
                throw new TestServicesException("No open time found for componentStateID #" + componentStateID);
            }
        } finally {
           // s_trace.info("computeSubmissionPoints end");
        }
    }

    private boolean canSubmit(int status, long componentStateID, StringBuilder msg, boolean isAdmin) throws SQLException, TestServicesException {
        debug("submit componentState = " + status);
        switch (status) {
            case ContestConstants.NOT_CHALLENGED:
                
                if (!isAdmin && !ContestConstants.ACCEPT_MULTIPLE_SUBMISSIONS) {
                    msg.append("ERROR: You cannot resubmit this problem");
                    return false;
                }
                break;
            case ContestConstants.COMPILED_UNSUBMITTED:
                // They are good to go
                break;
            default:
                debug("In canSubmit(): You cannot submit unless you have successfully compiled first.");
                msg.append("ERROR: You cannot submit unless you have successfully compiled first.");
                return false;
        }
        return true;
    }

    private void updateComponentState(Connection conn, int newPointVal, long componentStateID, int numSubmissions) throws SQLException {
        //s_trace.info("updateComponentState start");
        PreparedStatement ps = null;
        String sqlCmd = "UPDATE component_state SET points = ?, status_id = ?, submission_number = ? " + 
                        "WHERE component_state_id = ?";
        try {
            ps = conn.prepareStatement(sqlCmd);
            ps.setDouble(1, (double) newPointVal / 100.0);
            ps.setInt(2, ContestConstants.NOT_CHALLENGED);
            ps.setInt(3, numSubmissions + 1);
            ps.setLong(4, componentStateID);
            ps.executeUpdate();
        } finally {
            DBMS.close(ps);
            //s_trace.info("updateComponentState end");
        }
    }

    private void saveSubmission(Connection conn, long componentStateID, int numSubmissions, String programText, long currentTime, int newPointVal, int languageID, long openTime) throws SQLException {
        //s_trace.info("saveSubmission start");
        debug("saveSubmission(" + componentStateID + " " + numSubmissions + " " + languageID + ")");
        PreparedStatement ps = null;
        String sqlCmd = "INSERT INTO submission (component_state_id, submission_number, "+
                        " submission_text, open_time, submit_time, submission_points, language_id) "+
                        " VALUES (?,?,?,?,?,?,?)";
        try {
            ps = conn.prepareStatement(sqlCmd);
            ps.setLong(1, componentStateID);
            ps.setInt(2, numSubmissions + 1);
            ps.setBytes(3, programText.getBytes());
            ps.setLong(4, openTime);
            ps.setLong(5, currentTime);
            ps.setDouble(6, (double) newPointVal / 100.0);
            ps.setInt(7, languageID);
            ps.execute();
            debug("saved");
        } finally {
            close(null, ps, null);
            //s_trace.info("saveSubmission end");
        }
    }

    private int getNumSubmissions(Connection conn, long componentStateID) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT submission_number FROM component_state WHERE component_state_id = ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setLong(1, componentStateID);
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } finally {
            close(null, ps, rs);
        }
    }

    private int getRoomTypeId(Connection conn, long rid) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql;
        sql = "SELECT room_type_id FROM room WHERE room_id = ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setLong(1, rid);
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } finally {
            close(null, ps, rs);
        }
    }

    private void transferCompilationToSubmission(Connection conn, long componentStateID) throws SQLException {
        //s_trace.info("transferCompilationToSubmission start");
        PreparedStatement ps = null;
        String sqlCmd;
        if (DBMS.DB == DBMS.INFORMIX) {
            sqlCmd = "INSERT INTO submission_class_file (component_state_id, submission_number, path,class_file,sort_order) " +
                     " SELECT cs.component_state_id,cs.submission_number,ccf.path,ccf.class_file,ccf.sort_order " +
                     " FROM component_state cs, compilation_class_file ccf " +
                     " WHERE cs.component_state_id = ? AND ccf.component_state_id = cs.component_state_id";
        } else {
            sqlCmd = "INSERT INTO submission_class_file (component_state_id, submission_number, path, class_file, sort_order) " +
                    "SELECT cs.component_state_id, cs.submission_number, ccf.path, ccf.class_file, ccf.sort_order " +
                    "FROM component_state cs, compilation_class_file ccf " +
                    "WHERE cs.component_state_id = ? AND cs.component_state_id = ccf.component_state_id";
        }
        try {
            ps = conn.prepareStatement(sqlCmd);
            ps.setLong(1, componentStateID);
            ps.executeUpdate();
        } finally {
            DBMS.close(ps);
            //s_trace.info("transferCompilationToSubmission start");
        }
    }

    private static long getOpenTime(Connection connection, long componentStateID) throws SQLException {
        //String sql = "SELECT open_time FROM compilation WHERE component_state_id = ?";
        //s_trace.info("getOpenTime start");
        String sqlCmd = "SELECT cc.open_time FROM compilation cc " +
                        "WHERE cc.component_state_id = ? ";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sqlCmd);
            preparedStatement.setLong(1, componentStateID);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);
        } finally {
            DBMS.close(preparedStatement, resultSet);
            //s_trace.info("getOpenTime end");
        }
    }

    private void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e2) {
                s_trace.error("Failed to rollback!", e2);
            }
        }
    }

    /*
    private static final String TRANSER_COMPONENT_QUERY = "UPDATE component_state SET " +
    "coder_id = ? "+
    "WHERE " +
    "round_id=? "+
    "AND component_id=? "+
    "AND coder_id IN { "+
    "SELECT coder_id FROM team_coder_component_xref WHERE " +
    "round_id = ? " +
    "AND team_id = ? " +
    "AND component_id = ? " +
    ")";
    private void transferComponent(Connection conn,int roundID,int teamID, int userID, int componentID) throws SQLException {
    PreparedStatement ps = null;
    try {
    ps = conn.prepareStatement(TRANSER_COMPONENT_QUERY);
    int idx = 1;
    ps.setInt(idx++,userID);
    ps.setInt(idx++,roundID);
    ps.setInt(idx++,componentID);
    ps.setInt(idx++,roundID);
    ps.setInt(idx++,teamID);
    ps.setInt(idx++,componentID);
    ps.executeUpdate();
    }
    finally {
    close(null,ps,null);
    }
    }
    private static final String GET_TEAM_MEMBERS_FOR_ROUND_QUERY = "SELECT coder_id FROM room_result WHERE " +
    "round_id = ? AND coder_id IN ( SELECT coder_id FROM team_coder_xref WHERE team_id = ? )";
     */
    /**
     * Its only safe to call this during the coding phase
     *
     * @param conn
     * @param roundID
     * @param teamID
     * @throws SQLException
     */
    /*
    private void updateTeamPoints(Connection conn,int roundID,int teamID) throws SQLException {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
    ps = conn.prepareStatement(GET_TEAM_MEMBERS_FOR_ROUND_QUERY);
    ps.setInt(1,roundID);
    ps.setInt(2,teamID);
    rs = ps.executeQuery();
    while (rs.next()) {
    int coderID = rs.getInt(1);
    updateCoderPoints(conn,roundID,coderID,0);
    }
    }
    finally {
    close(null,ps,rs);
    }
    }
     */
    public void setWebServiceClients(String serviceName, int languageID, List sourceFiles) throws TestServicesException {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement("INSERT INTO web_service_source_file (web_service_source_file_id,web_service_id,language_id,web_service_file_type_id,path,source) VALUES (?,?,?,?,?,?)");
            for (Iterator it = sourceFiles.iterator(); it.hasNext();) {
                WebServiceRemoteFile file = (WebServiceRemoteFile) it.next();
                long sourceFileID = IdGeneratorClient.getSeqIdAsInt(DBMS.WEB_SERVICE_SOURCE_FILE_SEQ);
                long webServiceID = getWebServiceID(conn, serviceName);
                int idx = 1;
                ps.setLong(idx++, sourceFileID);
                ps.setLong(idx++, webServiceID);
                ps.setInt(idx++, languageID);
                ps.setInt(idx++, file.getType());
                ps.setString(idx++, file.getPath());
                ps.setBytes(idx++, file.getContents());
                ps.executeUpdate();
            }
            conn.commit();
        } catch (Exception e) {
            rollback(conn);
            printException(e);
            throw new TestServicesException(e.getMessage());
        } finally {
            close(conn, ps, null);
        }
    }

    public void saveCompiledWebServiceClients(long sourceFileID, WebServiceRemoteFile[] classFiles) throws TestServicesException {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBMS.getConnection();
            conn.setAutoCommit(false);
            clearCompiledWebServiceClients(conn, sourceFileID);
            ps = conn.prepareStatement("INSERT INTO web_service_compilation (web_service_source_file_id,sort_order,web_service_file_type_id,path,class_file) VALUES (?,?,?,?,?)");
            for (int i = 0; i < classFiles.length; i++) {
                WebServiceRemoteFile file = classFiles[i];
                int idx = 1;
                ps.setLong(idx++, sourceFileID);
                ps.setInt(idx++, i + 1);
                ps.setInt(idx++, WebServiceRemoteFile.WEB_SERVICE_CLIENT_OBJECT);
                ps.setString(idx++, file.getPath());
                ps.setBytes(idx++, file.getContents());
                ps.executeUpdate();
            }
            conn.commit();
        } catch (Exception e) {
            rollback(conn);
            s_trace.error(e);
            throw new TestServicesException(e.getMessage());
        } finally {
            close(conn, ps, null);
        }
    }

    private void clearCompiledWebServiceClients(Connection conn, long sourceFileID) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("DELETE FROM web_service_compilation WHERE web_service_source_file_id = ?");
            ps.setLong(1, sourceFileID);
            ps.executeUpdate();
        } finally {
            close(null, ps, null);
        }
    }

    private long getWebServiceID(Connection conn, String serviceName) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT web_service_id FROM web_service WHERE web_service_name = ?");
            ps.setString(1, serviceName);
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } finally {
            close(null, ps, rs);
        }
    }

    private static final String GET_WEB_SERVICE_STUBS_QUERY = "SELECT wssf.web_service_source_file_id "
            + ",wssf.path "
            + ",wssf.source "
            + ",wssf.web_service_file_type_id "
            + ",wssf.language_id "
            + "FROM web_service_source_file wssf "
            + " JOIN web_service ws ON wssf.web_service_id = ws.web_service_id "
            + " JOIN problem_web_service_xref pws ON ws.web_service_id = pws.web_service_id "
            + " AND ws.status_id = 1 "
            + "WHERE pws.problem_id = ? ";

    public WebServiceRemoteFile[] getWebServiceClientsForProblem(long problemID, int languageID) throws TestServicesException {
        if (s_trace.isDebugEnabled()) {
            debug("Getting client stubs for problem #" + problemID + ", language " + languageID);
        }
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            return getWebServiceClientsForProblem(problemID, languageID, conn);
        } catch (SQLException e) {
            s_trace.error("Could not obtain DB Connection");
            printException(e);
            throw new TestServicesException("Error loading getWebServiceClientsForProblem problemId = " + problemID + " languageId = " + languageID);
        } finally {
            DBMS.close(conn);
        }
    }

    private WebServiceRemoteFile[] getWebServiceClientsForProblem(long problemID, int languageID, Connection conn) throws TestServicesException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            List r = null;
            ps = conn.prepareStatement(GET_WEB_SERVICE_STUBS_QUERY + " AND wssf.language_id = ?");
            ps.setLong(1, problemID);
            ps.setInt(2, languageID);
            rs = ps.executeQuery();
            r = new Vector();
            if (rs.next()) {
                do {
                    int idx = 1;
                    long sourceFileID = rs.getLong(idx++);
                    String path = rs.getString(idx++);
                    byte[] source = rs.getBytes(idx++);
                    int type = rs.getInt(idx++);
                    WebServiceRemoteFile[] classFiles = getCompiledWebServiceClients(conn, sourceFileID);

                    r.add(new WebServiceRemoteFile(sourceFileID, path, source, type, classFiles, languageID));
                } while (rs.next());
            }
            return (WebServiceRemoteFile[]) r.toArray(new WebServiceRemoteFile[r.size()]);
        } catch (Exception e) {
            printException(e);
            throw new TestServicesException(e.getMessage());
        } finally {
            close(null, ps, rs);
        }
    }

    private static final String GET_COMPILED_CLIENTS_QUERY = "SELECT " +
            "wssf.language_id," +
            "wsc.path," +
            "wsc.class_file," +
            "wsc.sort_order" +
            " FROM web_service_compilation wsc " +
            " JOIN web_service_source_file wssf " +
            " ON wssf.web_service_source_file_id=wsc.web_service_source_file_id " +
            " WHERE wssf.web_service_source_file_id = ? " +
            " ORDER BY sort_order";



    private WebServiceRemoteFile[] getCompiledWebServiceClients(Connection conn, long sourceFileID) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (s_trace.isDebugEnabled()) {
                debug("Loading compiled web service client stubs for #" + sourceFileID);
            }
            ps = conn.prepareStatement(GET_COMPILED_CLIENTS_QUERY);
            ps.setLong(1, sourceFileID);
            rs = ps.executeQuery();
            List ret = new Vector();
            if (rs.next()) {
                do {
                    int idx = 1;
                    int languageID = rs.getInt(idx++);
                    String path = rs.getString(idx++);
                    byte[] clazzBytes = rs.getBytes(idx++);
                    ret.add(new WebServiceRemoteFile(sourceFileID, path, clazzBytes, WebServiceRemoteFile.WEB_SERVICE_CLIENT_OBJECT, null, languageID));
                } while (rs.next());
            }
            return (WebServiceRemoteFile[]) ret.toArray(new WebServiceRemoteFile[ret.size()]);
        } finally {
            close(null, ps, rs);
        }
    }

    /**
     * This method updates a coder's points for the round...only call this during the coding phase
     */
    private void updateCoderPoints(Connection conn, int roundId, int coderId, int pointChange) throws SQLException {
       // s_trace.info("updateCoderPoints start");
        PreparedStatement ps = null;
        StringBuilder sqlStr = new StringBuilder(200);
        //long currentTime = 0;
        try {

            if (DBMS.DB == DBMS.INFORMIX) {
                //sqlStr.replace(0, sqlStr.length(), "UPDATE room_result SET ");
                //sqlStr.append("point_total = point_total+? WHERE round_id = ? AND coder_id = ?");
                sqlStr.replace(0, sqlStr.length(), "UPDATE room_result set point_total = ( ");
                sqlStr.append("select sum(points) from component_state where coder_id = ? and round_id = ?) ");
                sqlStr.append("where coder_id = ? and round_id = ? ");
                ps = conn.prepareStatement(sqlStr.toString());
                //ps.setDouble(1, pointChange);
                ps.setInt(1, coderId);
                ps.setInt(2, roundId);
                ps.setInt(3, coderId);
                ps.setInt(4, roundId);
            } else {
                String sql = "UPDATE room_result SET point_total=? WHERE coder_id=? AND round_id=?";
                ps = conn.prepareStatement(sql);
                int pointTotal = ComponentStateTable.getPointTotal(conn, coderId, roundId);
                ps.setInt(1, pointTotal);
                ps.setInt(2, coderId);
                ps.setInt(3, roundId);
            }

            int rows = ps.executeUpdate();

            if (rows != 1) {
                s_trace.error("ERROR: updateCoderPoints() failed while updating ROOM_RESULT");
            } else {
                debug("Added " + ((double) pointChange / 100) + " points for " + coderId);
            }
        } finally {
            DBMS.close(ps);
            //s_trace.info("updateCoderPoints end");
        }
        
    }

    private static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
            printException(e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                printException(e);
            } finally {
                try {
                    if (conn != null) {
                        conn.setAutoCommit(true);
                        conn.close();
                    }
                } catch (Exception e) {
                    printException(e);
                }
            }
        }
    }

    // Query utilities to make life easier
    private static void printException(Exception e) {
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

    private static void info(Object message) {
        s_trace.info(message);
    }

    private static void debug(Object message) {
        s_trace.debug(message);
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.longtest.LongTestServices#cancelTestGroup(int)
     */
    public void cancelTestGroup(int testGroupId) throws RemoteException, LongTestServiceException {
        longTestServices.cancelTestGroup(testGroupId);
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.longtest.LongTestServices#createTestGroupForSolution(int, int, String[])
     */
    public int createTestGroupForSolution(int componentId, int solutionId, String[] args) throws LongTestServiceException, RemoteException {
        return longTestServices.createTestGroupForSolution(componentId, solutionId, args);
    }

    /**
     * <p>
     * start to prepare with test specific test group.
     * </p>
     * @param testGroupId the test group id.
     * @param roundType the round type.
     * @param custom problem customization.
     * @see com.topcoder.server.ejb.TestServices.longtest.LongTestServices#startTestGroup(int, Integer, int)
     */
    public void startTestGroup(int testGroupId, Integer roundType, ProblemCustomSettings custom) throws LongTestServiceException, RemoteException {
        longTestServices.startTestGroup(testGroupId, roundType, custom);
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.longtest.LongTestServices#deleteTestGroup(int)
     */
    public void deleteTestGroup(int testGroupId) throws LongTestServiceException, RemoteException {
        longTestServices.deleteTestGroup(testGroupId);
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.longtest.LongTestServices#reportTestResult(int, com.topcoder.services.tester.common.LongTestResults)
     */
    public void reportTestResult(int requestId, LongTestResults testResult) throws RemoteException, LongTestServiceException {
        longTestServices.reportTestResult(requestId, testResult);
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.longtest.LongTestServices#findTestGroup(int)
     */
    public LongTestGroup findTestGroup(int testGroupId) throws LongTestServiceException, RemoteException {
        return longTestServices.findTestGroup(testGroupId);
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#adminSubmitLong(LongCompileRequest);
     */
    public LongCompileResponse adminSubmitLong(LongCompileRequest req) throws CompilationTimeoutException, LongContestServicesException, RemoteException {
        return longContestTestServices.adminSubmitLong(req);
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#calculateSystemTestScore(int, int)
     */
    public void calculateSystemTestScore(int roundId, int componentId) throws LongContestServicesException, RemoteException {
        longContestTestServices.calculateSystemTestScore(roundId, componentId);
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#getAllowedLanguagesForRound(int)
     */
    public Language[] getAllowedLanguagesForRound(int roundId) throws LongContestServicesException, RemoteException {
        return longContestTestServices.getAllowedLanguagesForRound(roundId);
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#getLongTestQueueStatus()
     */
    public List getLongTestQueueStatus() throws LongContestServicesException, RemoteException {
        return longContestTestServices.getLongTestQueueStatus();
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#getNumberOfSubmissionOnLongTestQueue()
     */
    public int getNumberOfSubmissionOnLongTestQueue() throws LongContestServicesException, RemoteException {
        return longContestTestServices.getNumberOfSubmissionOnLongTestQueue();
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#queueEmpty(int, long, int)
     */
    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#queueLongSystemTestCase(int, int[], long[])
     */
    public void queueLongSystemTestCase(int roundId, int[] coderIds, long[] testCaseIds) throws LongContestServicesException, RemoteException {
        longContestTestServices.queueLongSystemTestCase(roundId, coderIds, testCaseIds);
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#queueLongSystemTestCase(int, long[])
     */
    public void queueLongSystemTestCase(int roundId, long[] testCaseIds) throws LongContestServicesException, RemoteException {
        longContestTestServices.queueLongSystemTestCase(roundId, testCaseIds);
    }
    
    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#queueLongTestCases(int, long, int, boolean)
     */
    public void queueLongTestCases(int roundID, long coderID, int componentID, boolean example) throws RemoteException, LongContestServicesException {
        longContestTestServices.queueLongTestCases(roundID, coderID, componentID, example);
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#recompileAllRound(int)
     */
    public void recompileAllRound(int roundId) throws LongContestServicesException, RemoteException {
        longContestTestServices.recompileAllRound(roundId);
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#recordLongTestResult(com.topcoder.server.common.LongSubmissionId, long, int, com.topcoder.services.tester.common.LongTestResults)
     */
    public void recordLongTestResult(LongSubmissionId lt, long testId, int testAction, LongTestResults result) throws RemoteException, LongContestServicesException {
        longContestTestServices.recordLongTestResult(lt, testId, testAction, result);
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#startLongSystemTests(int)
     */
    public int startLongSystemTests(int roundId) throws LongContestServicesException, RemoteException {
        return longContestTestServices.startLongSystemTests(roundId);
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#updateFinalScores(com.topcoder.server.common.LongSubmissionId, com.topcoder.shared.common.LongRoundScores)
     */
    public void updateFinalScores(LongSubmissionId id, LongRoundScores lrr) throws RemoteException, LongContestServicesException {
        longContestTestServices.updateFinalScores(id, lrr);
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#updateLongSystemTestFinalScores(com.topcoder.shared.common.LongRoundScores[])
     */
    public void updateLongSystemTestFinalScores(LongRoundScores[] lrr) throws RemoteException, LongContestServicesException {
        longContestTestServices.updateLongSystemTestFinalScores(lrr);
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#openComponentIfNotOpened(int, int, int, int)
     */
    public boolean openComponentIfNotOpened(int contestId, int roundId, int componentId, int coderId) throws LongContestServicesException, RemoteException {
        return longContestTestServices.openComponentIfNotOpened(contestId, roundId, componentId, coderId);
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#save(int, int, int, int, java.lang.String, int)
     */
    public void save(int contestId, int roundId, int componentId, int coderId, String programText, int languageId) throws LongContestServicesException, RemoteException {
        longContestTestServices.save(contestId, roundId, componentId, coderId, programText, languageId);
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#submit(com.topcoder.shared.messaging.messages.LongCompileRequest)
     */
    public LongCompileResponse submit(LongCompileRequest req) throws CompilationTimeoutException, LongContestServicesException, RemoteException {
        return longContestTestServices.submit(req);
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#getCoderHistory(int, int)
     */
    public LongCoderHistory getCoderHistory(int roundId, int coderId) throws LongContestServicesException, RemoteException {
        return longContestTestServices.getCoderHistory(roundId, coderId);
    }

    /**
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#getLongTestResults(int, int, int, int)
     */
    public LongTestResult[] getLongTestResults(int roundId, int coderId, int componentId, int resultType) throws LongContestServicesException, RemoteException {
        return longContestTestServices.getLongTestResults(roundId, coderId, componentId, resultType);
    }

    /**
     * @throws RemoteException
     * @see com.topcoder.server.ejb.TestServices.LongContestServices#getSubmission(int, int, int, boolean, int)
     */
    public LongSubmissionData getSubmission(int roundId, int coderId, int componentId, boolean example, int submissionNumber) throws LongContestServicesException, RemoteException {
        return longContestTestServices.getSubmission(roundId, coderId, componentId, example, submissionNumber);
    }

    public void register(int roundId, int coderId, List surveyData) throws LongContestServicesException, RemoteException {
        longContestTestServices.register(roundId, coderId, surveyData);
    }

    public ProblemComponent getProblemComponent(int roundId, int componentId, int coderId) throws LongContestServicesException, RemoteException {
        return longContestTestServices.getProblemComponent(roundId, componentId, coderId);
    }

    public int getSystemTestsDone(int roundId) throws LongContestServicesException, RemoteException {
        return longContestTestServices.getSystemTestsDone(roundId);
    }

    public int getTotalSystemTests(int roundId) throws LongContestServicesException, RemoteException {
        return longContestTestServices.getTotalSystemTests(roundId);
    }
}
