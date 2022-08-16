/*
 * Copyright (C) 2006 - 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.ejb.ProblemServices;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import com.topcoder.farm.shared.util.concurrent.ReadWriteLock;
import com.topcoder.farm.shared.util.concurrent.ReadWriteLock.ReadLock;
import com.topcoder.farm.shared.util.concurrent.ReadWriteLock.WriteLock;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.mpsqas.ApplicationConstants;
import com.topcoder.netCommon.mpsqas.ComponentIdStructure;
import com.topcoder.netCommon.mpsqas.ComponentInformation;
import com.topcoder.netCommon.mpsqas.MessageConstants;
import com.topcoder.netCommon.mpsqas.ProblemIdStructure;
import com.topcoder.netCommon.mpsqas.ProblemInformation;
import com.topcoder.netCommon.mpsqas.SolutionInformation;
import com.topcoder.netCommon.mpsqas.StatusConstants;
import com.topcoder.netCommon.mpsqas.UserInformation;
import com.topcoder.netCommon.mpsqas.WebServiceIdStructure;
import com.topcoder.netCommon.mpsqas.WebServiceInformation;
import com.topcoder.server.common.ServerContestConstants;
import com.topcoder.server.ejb.BaseEJB;
import com.topcoder.server.ejb.MPSQASServices.MPSQASServices;
import com.topcoder.server.ejb.MPSQASServices.MPSQASServicesLocator;
import com.topcoder.server.ejb.dao.ComponentDao;
import com.topcoder.server.mpsqas.broadcast.BroadcastPublisher;
import com.topcoder.server.mpsqas.broadcast.ComponentModifiedBroadcast;
import com.topcoder.server.mpsqas.broadcast.ProblemModifiedBroadcast;
import com.topcoder.server.services.CoreServices;
import com.topcoder.server.util.ArrayUtils;
import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.shared.language.BaseLanguage;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.problem.ComponentCategory;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.DataTypeFactory;
import com.topcoder.shared.problem.DataValue;
import com.topcoder.shared.problem.DataValueParseException;
import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.InvalidTypeException;
import com.topcoder.shared.problem.Problem;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.ProblemConstants;
import com.topcoder.shared.problem.ProblemCustomSettings;
import com.topcoder.shared.problem.SimpleComponent;
import com.topcoder.shared.problem.TestCase;
import com.topcoder.shared.problem.WebService;
import com.topcoder.shared.problemParser.ProblemComponentFactory;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.IdGeneratorClient;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.util.idgenerator.IDGenerationException;

//Note: setAutoCommit(false), commits, and rollbacks have
//been commented out because they tie up the db for too long.
//Refactoring should be done so testing / compiling
//is done before entering transactions, and then the
//setAutoCommit(false), commits, and rollbacks can
//be added again

/**
 * <p>
 *  Version 1.1 (TC Competition Engine Code Execution Time Issue) change notes:
 *  <ul>
 *      <li>Update {@link #getComponentInformation(int, int, Connection)} to populate the execution time limit.</li>
 *  </ul> 
 * </p>
 * 
 * <p>
 *  Version 1.2 (TC Competition Engine - Code Compilation Issues) change notes:
 *  <ul>
 *      <li>Update {@link #getComponentInformation(int, int, Connection)} to populate the compile time limit.</li>
 *  </ul> 
 * </p>
 *
 * <p>
 * Changes in 1.3 (Round Type Option Support For SRM Problem 1.0):
 * <ol>
 * <li>Updated {@link #getSimpleComponent(int componentId)}  method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in 1.4 (Fix Tester Choosing Issue for Testing Writer Solution v1.0):
 * <ol>
 * <li>Update {@link #saveComponent(ComponentInformation, int, int, Connection, boolean)}  method.</li>
 * <li>Update {@link #saveComponentStatement(ProblemComponent,int, int)}  method.</li>
 * <li>Update {@link #insertTestCases(TestCase[],Object[], Object[], int, Connection)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.5 (TC Competition Engine - CPP Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Update {@link #getComponentInformation(int, int, Connection)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.6 (TC Competition Engine - Python Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Update {@link #getComponentInformation(int, int, Connection)} method.</li>
 * </ol>
 * </p> 
 *
 * <p>
 * Changes in 1.7 (TC Competition Engine - C++ and Python Customization Support for SRM v1.0):
 * <ol>
 *      <li>Updated {@link #getSimpleComponent(int componentId)}  method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in 1.8 (Release Assembly - TopCoder Competition Engine Improvement Series 1):
 * <ol>
 * <li>
 * Added {@link #saveLongComponentConfiguration(ProblemComponent, Connection)} method for
 * saving submission rate settings to DB.
 * </li>
 * <li>
 * Updated {@link #getProblemComponent(int, boolean)} method
 * to retrieve submission rate settings from DB.
 * </li>
 * <li>
 * Updated {@link #getComponentInformation(int, int, Connection)} method
 * to populate submission rate settings.
 * </li>
 * <li>
 * Updated {@link #saveComponent(ComponentInformation, int, int, Connection, boolean)} method
 * to save submission rate settings to DB via call
 * to {@link #saveLongComponentConfiguration(ProblemComponent, Connection)}.
 * </li>
 * <li>
 * Updated {@link #saveComponentStatement(ProblemComponent, int, int)} method
 * to save submission rate settings to DB via call
 * to {@link #saveLongComponentConfiguration(ProblemComponent, Connection)}.
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.9 (TopCoder Competition Engine - Customize Execution Time and Memory Limits for SRM v1.0):
 * <ol>
 *      <li>Update {@link #getSimpleComponent(int componentId)} method.</li>
 *      <li>Update {@link #getComponentInformation(int, int, Connection)} to populate the execution time limit.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 2.0 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #getComponentInformation(int,int,Connection)} method.</li>
 *      <li>Update {@link #getSimpleComponent(int componentId)} method.</li>
 *      <li>Update {@link #getMemLimitForComponent(int componentId)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 2.1 (TopCoder Competition Engine - Return Use Custom Checker Flag for Problem):
 * <ol>
 *      <li>Updated {@link #getProblemComponent(int, boolean)} method to populate custom checker flag
 *      to <code>ProblemComponent</code>.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 2.2 (TopCoder Competition Engine - Stack Size Configuration For SRM Problems v1.0):
 * <ol>
 *      <li>Update {@link #getSimpleComponent(int componentId)} method.</li>
 * </ol>
 * </p>
 *
 * @author savon_cn, gevak, Selena
 * @version 2.2
 */
public class ProblemServicesBean extends BaseEJB {

    private final static int DEFAULT_RETURN_TYPE = 1;
    //private static final boolean VERBOSE = false;

    private final static Logger s_trace =  Logger.getLogger(ProblemServicesBean.class);
    //private final static Logger logger = s_trace;

    private MPSQASServices mpsqasServices = null;

    private ComponentDao componentDao = new ComponentDao();


    /**
     * Locks in use to avoid concurrent modifications of the same component simultaneously.
     * There is still concurrency issues with many of these methods which should be
     * refactored.
     */
    private static Map componentsLocks = new HashMap();
    private static Object componentsLocksMutex = new Object();

    private static Connection getConnection() throws SQLException {
        return com.topcoder.shared.util.DBMS.getConnection();
        //return DBMS.getConnection();
    }

    /**
     * Gets the problem of a given id. The <code>Problem</code> objects
     * returned also contain the <code>ProblemComponent</code> objects that
     * this problem consists of.
     */
    public Problem getProblem(int problemId) throws ProblemServicesException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(200);
        java.sql.Connection conn = null;
        Problem prob = null;
        try {
            conn = getConnection();

            sqlStr.append("SELECT p.problem_text, p.problem_type_id, p.name ");
            sqlStr.append("FROM problem p ");
            sqlStr.append("WHERE p.problem_id = ?");

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, problemId);
            rs = ps.executeQuery();
            prob = new Problem();
            if (rs.next()) {
                prob.setProblemId(problemId);
                prob.setProblemText(DBMS.getTextString(rs, 1));
                prob.setProblemTypeID(rs.getInt(2));
                prob.setName(rs.getString(3));
                sqlStr = new StringBuilder(100);
                sqlStr.append("SELECT c.component_id ");
                sqlStr.append(" FROM component c ");
                sqlStr.append(" WHERE c.problem_id = ? ");
                sqlStr.append("   AND c.status_id = ? ");

                closeConnection(null, ps, rs);

                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, problemId);
                ps.setInt(2, StatusConstants.ACTIVE);
                rs = ps.executeQuery();
                ArrayList al = new ArrayList(5);
                while (rs.next()) {
                    al.add(getProblemComponent(rs.getInt(1), false));
                }
                ProblemComponent[] components = new ProblemComponent[al.size()];
                debug("count = " + components.length);
                for (int i = 0; i < components.length; i++) {
                    components[i] = (ProblemComponent) al.get(i);
                }
                prob.setProblemComponents(components);
            } else {
                throw new ProblemServicesException("no problem found for id: " +
                        problemId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ProblemServicesException("" + e);
        } finally {
            closeConnection(conn, ps, rs);
        }
        return prob;
    }

    private void populateTestCaseIds(Connection conn, ProblemComponent ret, boolean unsafe) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            TestCase[] testCases = ret.getTestCases();
            if (testCases.length > 0 && testCases[0].getId() == null) {
                // if the IDs are not in XML, we need to populate it from DB
                if (unsafe) {
                    // All test cases for MPSQAS
                    ps = conn.prepareStatement("SELECT test_case_id FROM system_test_case WHERE component_id = ? ORDER BY test_number, test_case_id");
                } else {
                    // Only example test cases for others
                    ps = conn.prepareStatement("SELECT test_case_id FROM system_test_case WHERE component_id = ? AND example_flag = 1 ORDER BY test_number, test_case_id");
                }
                ps.setInt(1, ret.getComponentId());
                rs = ps.executeQuery();
                int index = 0;
                for (int i=0;i<testCases.length;++i) {
                    if (rs.next()) {
                        testCases[i].setId(new Integer(rs.getInt(1)));
                    }
                }

                ret.setTestCases(testCases);
            }
        } finally {
            closeConnection(null, ps, rs);
        }
    }

    /**
     * Returns a ProblemComponent for a given component id. Parses the XML in the
     * component_text field for the problem statement.  The definition part of
     * the ProblemComponent is populated from table columns, not from the xml,
     * to help keep the xml and table columns in sync.
     * Submission rate settings are loaded from DB.
     *
     * @param componentId The component to get the ProblemComponent for.
     * @param unsafe Should the problem statement contain unsafe information
     *               intended for mpsqas only.
     */
    public ProblemComponent getProblemComponent(int componentId, boolean unsafe) {
        PreparedStatement ps = null;
        StringBuilder sqlStr = new StringBuilder(256);
        ProblemComponent ret = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = getConnection();

            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT c.component_text AS xml ");
            sqlStr.append(",c.component_type_id AS component_type ");
            sqlStr.append(",c.problem_id AS problem ");
            sqlStr.append(",cfg.submission_rate AS submission_rate ");
            sqlStr.append(",cfg.example_submission_rate AS example_submission_rate ");
            sqlStr.append(",s.has_check_answer AS has_check_answer ");
            sqlStr.append("FROM component c ");
            sqlStr.append("LEFT OUTER JOIN long_component_configuration cfg ON c.component_id = cfg.component_id ");
            sqlStr.append("LEFT OUTER JOIN component_solution_xref cs ON cs.component_id = c.component_id AND cs.primary_solution = 1 ");
            sqlStr.append("LEFT OUTER JOIN solution s ON cs.solution_id = s.solution_id ");
            sqlStr.append("WHERE c.component_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            rs = ps.executeQuery();
            rs.next();
            String xml = DBMS.getTextString(rs, "xml");
            ret = parseProblemStatement(xml, unsafe, componentId);
            ret.setComponentTypeID(rs.getInt("component_type"));
            ret.setProblemId(rs.getInt("problem"));
            ret.setComponentId(componentId);
            ret.setCategories(getComponentCategories(componentId, conn));
            if (rs.getObject("submission_rate") != null) {
                ret.setSubmissionRate(rs.getInt("submission_rate"));
            }
            if (rs.getObject("example_submission_rate") != null) {
                ret.setExampleSubmissionRate(rs.getInt("example_submission_rate"));
            }
            ret.setCustomChecker(rs.getBoolean("has_check_answer"));

            // Populate the test case IDs if it does not exist in XML
            populateTestCaseIds(conn, ret, unsafe);
        } catch (Exception e) {
            s_trace.error("Error getting problem component: ", e);
            s_trace.debug("SQL was: " + sqlStr);
        } finally {
            //closeConnection(conn, ps);
            closeConnection(conn, ps, rs);
        }

        return ret;
    }

    /**
     * Fills out a ProblemInformation object with all the
     * current information about a problem.
     *
     * @param problemId The id of the problem.
     * @param userId    The userId of the person requesting the information.
     */
    public ProblemInformation getProblemInformation(int problemId, int userId)
            throws RemoteException {
        ProblemInformation problem = new ProblemInformation();
        problem.setProblemId(problemId);

        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            StringBuilder sqlStr;

            //Find out the user type
            int userType = getUserTypeForProblem(problemId, userId, conn);
            if (userType == -1) return null;

            problem.setUserType(userType);

            //get generic problem information
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT p.status_id ");
            sqlStr.append(",NVL(p.proposed_difficulty_id, -1) ");
            sqlStr.append(",NVL(p.proposed_division_id, -1) ");
            sqlStr.append(",p.modify_date ");
            sqlStr.append(",p.name ");
            sqlStr.append(",p.problem_text ");
            sqlStr.append(",p.problem_type_id ");
            sqlStr.append("FROM problem p ");
            sqlStr.append("WHERE p.problem_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, problemId);
            rs = ps.executeQuery();
            rs.next();

            problem.setStatus(rs.getInt(1));
            problem.setDifficulty(rs.getInt(2));
            problem.setDivision(rs.getInt(3));
            problem.setLastModified(rs.getTimestamp(4).toString());
            problem.setName(rs.getString(5));
            problem.setProblemText(DBMS.getTextString(rs, 6));
            problem.setProblemTypeID(rs.getInt(7));

            //get the web services
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT w.web_service_id AS id ");
            sqlStr.append(",w.web_service_name AS name ");
            sqlStr.append("FROM web_service w ");
            sqlStr.append(",problem_web_service_xref pw ");
            sqlStr.append("WHERE w.web_service_id = pw.web_service_id ");
            sqlStr.append("AND pw.problem_id = ? ");
            sqlStr.append("AND w.status_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, problemId);
            ps.setInt(2, StatusConstants.ACTIVE);
            rs = ps.executeQuery();

            ArrayList al_webServices = new ArrayList();
            WebServiceInformation webService;
            while (rs.next()) {
                webService = new WebServiceInformation();
                webService.setWebServiceId(rs.getInt("id"));
                webService.setName(rs.getString("name"));
                webService.setProblemId(problemId);
                al_webServices.add(webService);
            }
            WebService[] webServices = new WebService[al_webServices.size()];
            for (int i = 0; i < webServices.length; i++) {
                webServices[i] = (WebService) al_webServices.get(i);
            }
            problem.setWebServices(webServices);


            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT component_id ");
            sqlStr.append("FROM component ");
            sqlStr.append("WHERE problem_id = ? ");
            sqlStr.append("AND status_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, problemId);
            ps.setInt(2, StatusConstants.ACTIVE);
            rs = ps.executeQuery();
            ArrayList componentsAL = new ArrayList();
            ComponentInformation component;
            while (rs.next()) {
                component = getComponentInformation(rs.getInt(1), userId, conn);
                if (component != null) {
                    componentsAL.add(component);
                }
            }
            ComponentInformation[] components = new ComponentInformation[
                    componentsAL.size()];
            for (int i = 0; i < componentsAL.size(); i++) {
                components[i] = (ComponentInformation) componentsAL.get(i);
            }
            problem.setProblemComponents(components);

            problem.setCorrespondence(getMPSQASServices().getProblemCorrespondence(
                    problemId));
            problem.setCorrespondenceReceivers(getMPSQASServices()
                    .getProblemCorrespondenceReceivers(problemId));
            getMPSQASServices().markProblemCorrespondenceRead(problemId, userId);

            problem.setAvailableTesters(getMPSQASServices().getUsers(
                    ApplicationConstants.ALL_TESTERS, -1));
            problem.setScheduledTesters(getMPSQASServices().getUsers(
                    ApplicationConstants.TESTERS_FOR_PROBLEM, problemId));

        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            problem = null;
        } catch (Exception e) {
            s_trace.error("Error getting problem information for problem:", e);
            problem = null;
        } finally {
            //closeConnection(conn, ps);
            closeConnection(conn, ps, rs);
        }

        return problem;
    }

    /**
     * Returns a populated ComponentInformation for use by mpsqas containing
     * info representing the component with the specified id specific to the
     * user with the specified user id.
     */
    public ComponentInformation getComponentInformation(int componentId,
            int userId) throws RemoteException {
        Connection conn = null;
        try {
            conn = getConnection();
            return getComponentInformation(componentId, userId, conn);
        } catch (Exception e) {
            s_trace.error("Error getting component.", e);
            return null;
        } finally {
            closeConnection(conn, null, null);
        }
    }


    /**
     * Returns a populated ComponentInformation.
     * @param componentId the component Id.
     * @param userId the user id.
     * @param conn the jdbc connection.
     * @return the component information.
     */
    private ComponentInformation getComponentInformation(int componentId,
            int userId, Connection conn) {
        debug("getting component with id=" + componentId);

        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(256);
        ComponentInformation component = new ComponentInformation();
        component.setComponentId(componentId);

        try {
            int userType = getUserTypeForComponent(componentId, userId, conn);
            if (userType == -1) return null;

            //we can get a bunch of info from the arena's method
            ProblemComponent pc = getProblemComponent(componentId, true);
            component.setUnsafe(pc.isUnsafe());
            component.setValid(pc.isValid());
            component.setMessages(pc.getMessages());
            component.setIntro(pc.getIntro());
            component.setClassName(pc.getClassName());
            component.setExposedClassName(pc.getExposedClassName());
            component.setMethodNames(pc.getAllMethodNames());
            component.setReturnTypes(pc.getAllReturnTypes());
            component.setParamTypes(pc.getAllParamTypes());
            component.setParamNames(pc.getAllParamNames());
            component.setExposedMethodNames(pc.getAllExposedMethodNames());
            component.setExposedReturnTypes(pc.getAllExposedReturnTypes());
            component.setExposedParamTypes(pc.getAllExposedParamTypes());
            component.setExposedParamNames(pc.getAllExposedParamNames());
            component.setSpec(pc.getSpec());
            component.setNotes(pc.getNotes());
            component.setConstraints(pc.getConstraints());
            component.setTestCases(pc.getTestCases());
            component.setComponentTypeID(pc.getComponentTypeID());
            component.setProblemId(pc.getProblemId());
            component.setDefaultSolution(pc.getDefaultSolution());
            component.setCodeLengthLimit(pc.getCodeLengthLimit());
            ProblemCustomSettings pcs = pc.getProblemCustomSettings();
            //set the execution time limit to algo problem and long problem.
            if(pc.getComponentTypeID() == ProblemConstants.MAIN_COMPONENT
                    && pcs.getExecutionTimeLimit() == ProblemComponent.DEFAULT_EXECUTION_TIME_LIMIT) {
                pcs.setExecutionTimeLimit(ProblemComponent.DEFAULT_SRM_EXECUTION_TIME_LIMIT);
            }
            component.setProblemCustomSettings(pcs);
            component.setSubmissionRate(pc.getSubmissionRate());
            component.setExampleSubmissionRate(pc.getExampleSubmissionRate());

            component.setRoundType(pc.getRoundType());
            component.setCategories(pc.getCategories());
            component.setUserType(userType);

            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT NVL(modify_date, current) FROM component ");
            sqlStr.append("WHERE component_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            rs = ps.executeQuery();
            rs.next();
            component.setLastModified(rs.getTimestamp(1).toString());
            rs.close();
            ps.close();

            //get the correspondence
            component.setCorrespondence(getMPSQASServices()
                    .getComponentCorrespondence(componentId));
            component.setCorrespondenceReceivers(getMPSQASServices()
                    .getComponentCorrespondenceReceivers(componentId));
            getMPSQASServices().markComponentCorrespondenceRead(componentId, userId);

            //get the testers / writers
            component.setAvailableTesters(getMPSQASServices().getUsers(
                    ApplicationConstants.ALL_TESTERS, -1));
            component.setScheduledTesters(getMPSQASServices().getUsers(
                    ApplicationConstants.TESTERS_FOR_COMPONENT, componentId));
            component.setWriters(getMPSQASServices().getUsers(
                    ApplicationConstants.WRITERS_FOR_COMPONENT, componentId));

            //get the roundId and name, this is needed for tester payments
            sqlStr = new StringBuilder(256);
            sqlStr.append("select r.round_id, c.name||' - ' ||r.name as name, count(*) ");
            sqlStr.append("from round r, round_component rc, contest c ");
            sqlStr.append("where rc.round_id = r.round_id ");
            sqlStr.append("and rc.component_id = ? ");
            sqlStr.append("and r.round_type_id not in ("+ContestConstants.PRACTICE_ROUND_TYPE_ID+","+ContestConstants.LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID+","+
                                                        ContestConstants.INTEL_LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID+","+ContestConstants.INTRO_EVENT_ROUND_TYPE_ID+","+
                                                        ContestConstants.AMD_LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID+" ) ");
            sqlStr.append("and c.contest_id = r.contest_id ");
            sqlStr.append("group by 1,2 ");
            sqlStr.append("order by 3 desc, 1 desc");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, component.getComponentId());
            rs = ps.executeQuery();
            if(rs.next()) {
                component.setRoundID(rs.getInt("round_id"));
                component.setRoundName(rs.getString("name"));
            } else {
                component.setRoundID(-1);
                component.setRoundName("Unassigned");
            }
            rs.close();
            ps.close();

            component.setWriterPayments((ArrayList)getMPSQASServices().getWriterPayments(component.getProblemId()));
            component.setTesterPayments((ArrayList)getMPSQASServices().getTesterPayments(component.getRoundID()));

            //what is the status of the problem?
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT status_id ");
            sqlStr.append("FROM problem ");
            sqlStr.append("WHERE problem_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, component.getProblemId());
            rs = ps.executeQuery();
            rs.next();
            int status = rs.getInt(1);
            component.setStatus(status);
            rs.close();
            ps.close();

            //get the users solution, if it exists
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT s.solution_text ");
            sqlStr.append(",s.solution_id ");
            sqlStr.append(",cs.primary_solution ");
            sqlStr.append(",s.language_id ");
            sqlStr.append("FROM solution s ");
            sqlStr.append(",component_solution_xref cs ");
            sqlStr.append("WHERE cs.component_id = ? ");
            sqlStr.append("AND cs.solution_id = s.solution_id ");
            if (userType == ApplicationConstants.PROBLEM_ADMIN) {
                sqlStr.append("AND cs.primary_solution = ? ");
            } else {
                sqlStr.append("AND s.coder_id = ? ");
            }
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            if (userType == ApplicationConstants.PROBLEM_ADMIN) {
                ps.setInt(2, ApplicationConstants.PRIMARY_SOLUTION);
            } else {
                ps.setInt(2, userId);
            }
            rs = ps.executeQuery();

            SolutionInformation solution;
            if (rs.next()) {
                solution = new SolutionInformation();
                solution.setSolutionId(rs.getInt(2));
                solution.setText(removeAutoGeneratedCode(DBMS.getTextString(rs, 1)));
                solution.setPrimary(rs.getInt(3) == ApplicationConstants.PRIMARY_SOLUTION);
                int languageId = rs.getInt(4);
                try {
                    solution.setLanguage(BaseLanguage.getLanguage(languageId));
                } catch (Exception e) {
                    s_trace.warn("language for solution " + solution.getSolutionId() + " could not be recognized: "+languageId);
                    solution.setLanguage(JavaLanguage.JAVA_LANGUAGE);
                    //Just in case language for solution is not defined, sould not happen
                }
                component.setSolution(solution);
            }

            rs.close();
            ps.close();

            //get the list of all solutions
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT u.handle ");
            sqlStr.append(",cs.primary_solution ");
            sqlStr.append(",s.solution_text ");
            sqlStr.append(",s.solution_id ");
            sqlStr.append(",u.user_id ");
            sqlStr.append(",s.language_id ");
            sqlStr.append("FROM user u ");
            sqlStr.append(",solution s ");
            sqlStr.append(",component_solution_xref cs ");
            sqlStr.append("WHERE cs.component_id = ? ");
            sqlStr.append("AND cs.solution_id = s.solution_id ");
            sqlStr.append("AND u.user_id = s.coder_id ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            rs = ps.executeQuery();

            while (rs.next()) {
                solution = new SolutionInformation();
                solution.setHandle(rs.getString(1));
                solution.setPrimary(rs.getInt(2) ==
                        ApplicationConstants.PRIMARY_SOLUTION);
                solution.setSolutionId(rs.getInt(4));
                if (userType == ApplicationConstants.PROBLEM_TESTER &&
                        status < StatusConstants.FINAL_TESTING &&
                        rs.getInt(5) != userId) {
                    solution.setText("Solution not available until Final Testing.");
                } else {
                    solution.setText(rs.getString(3));
                }
                int languageId = rs.getInt(6);
                try {
                    solution.setLanguage(BaseLanguage.getLanguage(languageId));
                } catch (Exception e) {
                    s_trace.warn("language for solution " + solution.getSolutionId()+ " could not be recognized: "+languageId);
                    solution.setLanguage(JavaLanguage.JAVA_LANGUAGE);
                    //Just in case language for solution is not defined, sould not happen
                }
                component.addSolution(solution);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            s_trace.error("Error getting component information: ", e);
        } finally {
            //closeConnection(null, ps);
            closeConnection(null, ps, rs);
        }

        return component;
    }

    /**
     * Gets the categories for the component
     *
     * @param componentId Id of the component
     * @param conn
     *
     * @return ArrayList of ComponentCategory, never null.
     *
     * @throws SQLException If an exception us thrown during the process
     */
    private ArrayList getComponentCategories(int componentId, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            StringBuilder sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT ccl.component_category_id, ");
            sqlStr.append("ccl.component_category_desc, ");
            sqlStr.append("NVL(ccx.component_id,-1) ");
            sqlStr.append("FROM component_category_lu ccl ");
            sqlStr.append("LEFT OUTER JOIN component_category_xref ccx ON ");
            sqlStr.append("ccl.component_category_id = ccx.component_category_id ");
            sqlStr.append("AND ccx.component_id = ? ");
            sqlStr.append("ORDER BY ccl.component_category_id");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            rs = ps.executeQuery();
            ArrayList ccs = new ArrayList();
            while(rs.next()){
                int categoryId = rs.getInt(1);
                String desc = rs.getString(2);
                boolean checked = rs.getInt(3)!=-1;
                ComponentCategory cc = new ComponentCategory(desc, checked, categoryId);
                ccs.add(cc);
            }
            return ccs;
        } finally {
            DBMS.close(null, ps, rs);
        }
    }

    /**
     * Removes the imports and packages that head up a solution text file
     * in the database that were added at compile time.
     */
    private String removeAutoGeneratedCode(String code) {
        if (code.indexOf(ApplicationConstants.AUTO_GENERATED_END_COMMENT_FLAG)
                == -1) {
            return code;
        } else {
            return code.substring(code.indexOf(
                    ApplicationConstants.AUTO_GENERATED_END_COMMENT_FLAG)
                    + ApplicationConstants.AUTO_GENERATED_END_COMMENT_FLAG.length());
        }
    }


    /**
     * Saves information on a problem.  The information saved is based
     * on the user's role in the problem and the status of the problem.
     * The method performs error checking and does not allow the save
     * to complete if any errors occur.  The status of the problem
     * is changed to SUBMISSION_PENDING_APPROVAL if the current status is
     * PROPOSAL_APPROVED or SUBMISSION_REJECTED and to PROPOSAL_PENDING_APPROVAL
     * if the current status is PROPOSAL_REJECTED.  Also saves the components
     * of the problem.  Returns an ArrayList which is
     * {Boolean.TRUE, ProblemIdStructure(id structure)} if the
     * save went ok and {Boolean.FALSE, String(message)} if not.
     *
     * @param problem ProblemInformation object containing the problem
     * @param userId The user id of the user trying to save the problem
     * @param connectionId The connection id of the user trying to save
     *                     the problem (so the connection doesn't get the
     *                     Problem Modified broadcast).
     */
    public ArrayList saveProblem(ProblemInformation problem, int userId,
            int connectionId) throws RemoteException {
        ArrayList submitInfo = new ArrayList(2);
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;

        try {
            boolean justInserted = false;
            conn = getConnection();
//      conn.setAutoCommit(false);
            int problemId = problem.getProblemId();
            StringBuilder sqlStr = new StringBuilder();

            String errors = checkGeneralProblemInfo(problem, conn);
            if (errors.length() != 0) {
                submitInfo.add(Boolean.FALSE);
                submitInfo.add(errors);
                return submitInfo;
            }

            if (problemId == -1) {
                debug("Inserting new problem for \"" +
                        problem.getName() + "\", problemId = " +
                        problemId);
                problemId = IdGeneratorClient.getSeqIdAsInt(DBMS.PROBLEM_SEQ);

                //this problem didn't exist before, we have to insert it.
                sqlStr = new StringBuilder(256);
                sqlStr.append("INSERT INTO problem ");
                sqlStr.append("(problem_id ");
                sqlStr.append(",problem_type_id ");
                sqlStr.append(",status_id ");
                sqlStr.append(",name) ");
                sqlStr.append("VALUES (?, ?, ?, ?) ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, problemId);
                ps.setInt(2, problem.getProblemTypeID());
                //PROPOSAL_REJECTED so it will change to PROPOSAL_PENDING_APPROVAL
                //later in this method
                ps.setInt(3, StatusConstants.PROPOSAL_REJECTED);
                ps.setString(4, problem.getName());
                ps.executeUpdate();     // only execute if component validates - mktong
                problem.setProblemId(problemId);
                justInserted = true;
            }

            //Find out the user type
            int userType = getUserTypeForProblem(problemId, userId, conn);
            if (userType != ApplicationConstants.PROBLEM_WRITER &&
                    userType != ApplicationConstants.PROBLEM_ADMIN &&
                    userType != ApplicationConstants.PROBLEM_TESTER &&
                    !justInserted) {
                submitInfo.add(Boolean.FALSE);
                submitInfo.add("You do not have permission to save the statement.");
                return submitInfo;
            }

            //next, get some quick info about the problem
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT status_id ");
            sqlStr.append("FROM problem ");
            sqlStr.append("WHERE problem_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, problemId);
            rs = ps.executeQuery();
            rs.next();
            int status = rs.getInt(1);

            if (userType != ApplicationConstants.PROBLEM_TESTER || justInserted) {
                //save the statement if the user is a writer

                //first save top level problem info
                sqlStr = new StringBuilder(256);
                sqlStr.append("UPDATE problem ");
                sqlStr.append("SET problem_text = ? ");
                sqlStr.append(",proposed_difficulty_id = ? ");
                sqlStr.append(",proposed_division_id = ? ");
                sqlStr.append(",name = ? ");
                sqlStr.append("WHERE problem_id = ?");
                ps = conn.prepareStatement(sqlStr.toString());

                if (problem.getProblemText() != null) {
                    ps.setBytes(1, DBMS.serializeTextString(problem.getProblemText()));
                } else {
                    ps.setBytes(1, DBMS.serializeTextString(""));
                }
                ps.setInt(2, problem.getDifficulty());
                ps.setInt(3, problem.getDivision());
                ps.setString(4, problem.getName());
                ps.setInt(5, problemId);
                ps.executeUpdate();
            }

            //make sure the list of web services in the problem matches the
            //list in the db
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT pw.web_service_id AS web_service_id ");
            sqlStr.append("FROM problem_web_service_xref pw ");
            sqlStr.append(",web_service w ");
            sqlStr.append("WHERE pw.web_service_id = w.web_service_id ");
            sqlStr.append("AND pw.problem_id = ? ");
            sqlStr.append("AND w.status_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, problemId);
            ps.setInt(2, StatusConstants.ACTIVE);
            rs = ps.executeQuery();

            ArrayList oldWebServiceIds = new ArrayList();
            while (rs.next()) {
                oldWebServiceIds.add(new Integer(rs.getInt("web_service_id")));
            }

            ArrayList newWebServiceIds = new ArrayList();
            for (int i = 0; i < problem.getWebServices().length; i++) {
                newWebServiceIds.add(new Integer(problem.getWebServices()[i]
                        .getWebServiceId()));
            }
            Collections.sort(oldWebServiceIds);
            Collections.sort(newWebServiceIds);
            if (!oldWebServiceIds.equals(newWebServiceIds)) {
                if (userType == ApplicationConstants.PROBLEM_TESTER) {
                    submitInfo.add(Boolean.FALSE);
                    submitInfo.add("You do not have permission to modify the problem's "
                            + "web services.");
                    return submitInfo;
                }

                sqlStr = new StringBuilder(256);
                sqlStr.append("UPDATE web_service ");
                sqlStr.append("SET status_id = ? ");
                sqlStr.append("WHERE web_service_id = ? ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, StatusConstants.INACTIVE);

                //see if any need to be removed from the component
                for (int i = 0; i < oldWebServiceIds.size(); i++) {
                    if (!newWebServiceIds.contains(oldWebServiceIds.get(i))) {
                        ps.setInt(2, ((Integer) oldWebServiceIds.get(i)).intValue());
                        ps.executeUpdate();
                    }
                }

                sqlStr = new StringBuilder(256);
                sqlStr.append("INSERT INTO web_service ");
                sqlStr.append("(web_service_id ");
                sqlStr.append(",web_service_name ");
                sqlStr.append(",status_id) ");
                sqlStr.append("VALUES (?, ?, ?)");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(3, StatusConstants.ACTIVE);

                sqlStr = new StringBuilder(256);
                sqlStr.append("INSERT INTO problem_web_service_xref ");
                sqlStr.append("(problem_id ");
                sqlStr.append(",web_service_id) ");
                sqlStr.append("VALUES (?, ?)");
                ps2 = conn.prepareStatement(sqlStr.toString());
                ps2.setInt(1, problemId);

                sqlStr = new StringBuilder(256);
                sqlStr.append("SELECT web_service_id ");
                sqlStr.append("FROM web_service ");
                sqlStr.append("WHERE web_service_name = ? ");
                PreparedStatement ps3 = conn.prepareStatement(sqlStr.toString());

                //see if any need to be inserted
                for (int i = 0; i < problem.getWebServices().length; i++) {
                    if (problem.getWebServices()[i].getWebServiceId() == -1) {
                        //make sure another one by its name doesnt already exist
                        ps3.setString(1, problem.getWebServices()[i].getName());
                        rs = ps3.executeQuery();
                        if (rs.next()) {
                            submitInfo.add(Boolean.FALSE);
                            submitInfo.add("A web service with name " +
                                    problem.getWebServices()[i].getName() +
                                    " already exists, please rename the service.");
                            return submitInfo;
                        }

                        int webServiceId = IdGeneratorClient.getSeqIdAsInt(DBMS.WEB_SERVICE_SEQ);
                        ps.setInt(1, webServiceId);
                        ps.setString(2, problem.getWebServices()[i].getName());
                        ps.executeUpdate();

                        ps2.setInt(2, webServiceId);
                        ps2.executeUpdate();
                    }
                }
            }

            //get the current list of component ids to see if this list differs
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT component_id ");
            sqlStr.append("FROM component ");
            sqlStr.append("WHERE problem_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, problemId);
            rs = ps.executeQuery();
            ArrayList currentComponentIds = new ArrayList();
            ArrayList newComponentIds = new ArrayList();
            while (rs.next()) {
                currentComponentIds.add(new Integer(rs.getInt(1)));
            }
            for (int i = 0; i < problem.getProblemComponents().length; i++) {
                newComponentIds.add(new Integer(problem.getProblemComponents()[i]
                        .getComponentId()));
            }
            Collections.sort(currentComponentIds);
            Collections.sort(newComponentIds);
            if (!currentComponentIds.equals(newComponentIds)) {
                if (userType == ApplicationConstants.PROBLEM_TESTER) {
                    submitInfo.add(Boolean.FALSE);
                    submitInfo.add(
                            "You do not have permission to modify the components.");
                    return submitInfo;
                }

                sqlStr = new StringBuilder(256);
                sqlStr.append("UPDATE component ");
                sqlStr.append("SET status_id = ? ");
                sqlStr.append("WHERE component_id = ? ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, StatusConstants.INACTIVE);

                //see if any components have been deleted and set them to
                //inactive
                for (int i = 0; i < currentComponentIds.size(); i++) {
                    if (newComponentIds.indexOf(currentComponentIds.get(i)) == -1) {
                        ps.setInt(2, ((Integer) currentComponentIds.get(i)).intValue());
                        ps.executeUpdate();
                    }
                }
            }

            sqlStr = new StringBuilder(256);
            sqlStr.append("INSERT INTO component ");
            sqlStr.append("(component_id ");
            sqlStr.append(",problem_id ");
            sqlStr.append(",class_name ");
            sqlStr.append(",method_name ");
            sqlStr.append(",component_text ");
            sqlStr.append(",component_type_id ");
            sqlStr.append(",status_id ");
            sqlStr.append(",result_type_id) ");
            sqlStr.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(7, StatusConstants.ACTIVE);
            ps.setInt(8, DEFAULT_RETURN_TYPE);

            sqlStr = new StringBuilder(256);
            sqlStr.append("INSERT INTO component_user_xref ");
            sqlStr.append("(component_id ");
            sqlStr.append(",user_id ");
            sqlStr.append(",user_type_id) ");
            sqlStr.append("VALUES (?, ?, ?) ");
            ps2 = conn.prepareStatement(sqlStr.toString());

            int componentId;
            ArrayList cresult;
            for (int i = 0; i < problem.getProblemComponents().length; i++) {
                //make sure components know they came from this problem
                problem.getProblemComponents()[i].setProblemId(problemId);

                //only save the component if it has already been added or
                //if it is a new problem.
                if (problem.getProblemTypeID() == ServerContestConstants.SINGLE_PROBLEM ||
                        problem.getProblemTypeID() == ServerContestConstants.LONG_PROBLEM ||
                        problem.getProblemComponents()[i].getComponentId() != -1) {
                    //save the component
                    cresult = saveComponent((ComponentInformation) problem
                            .getProblemComponents()[i], userId, connectionId, conn, true);
                    if (Boolean.FALSE.equals(cresult.get(0))) {
                        if (justInserted) {
                            sqlStr = new StringBuilder(256);
                            sqlStr.append("delete from problem ");
                            sqlStr.append("where problem_id = ? ");
                            ps = conn.prepareStatement(sqlStr.toString());
                            ps.setInt(1, problem.getProblemId());
                            ps.executeUpdate();
                        }

                        //component save didn't go... return error
                        submitInfo.add(Boolean.FALSE);
                        submitInfo.add("Error submitting "
                                + problem.getProblemComponents()[i].getClassName()
                                + " component:\n"
                                + (String) cresult.get(1));
                        return submitInfo;
                    }
                } else  //if it is a new team component, just insert class & method
                {
                    componentId = IdGeneratorClient.getSeqIdAsInt(DBMS.COMPONENT_SEQ);

                    problem.getProblemComponents()[i].setComponentId(componentId);
                    problem.getProblemComponents()[i].setProblemId(problemId);
                    problem.getProblemComponents()[i].setReturnType(
                            getDataType(DEFAULT_RETURN_TYPE));

                    ps.setInt(1, componentId);
                    ps.setInt(2, problem.getProblemId());
                    ps.setString(3, problem.getProblemComponents()[i].getClassName());
                    ps.setString(4, problem.getProblemComponents()[i].getMethodName());
                    ps.setBytes(5, DBMS.serializeTextString(
                            problem.getProblemComponents()[i].toXML()));
                    ps.setInt(6, problem.getProblemComponents()[i].getComponentTypeID());
                    ps.executeUpdate();

                    ps2.setInt(1, componentId);
                    ps2.setInt(2, userId);
                    ps2.setInt(3, ApplicationConstants.PROBLEM_WRITER);
                    ps2.executeUpdate();
                }
            }

            ps.close();
            ps2.close();

            //the submission went ok, update the status
            if (userType == ApplicationConstants.PROBLEM_WRITER || justInserted) {
                int newStatus = status;
                if (status == StatusConstants.PROPOSAL_REJECTED) {
                    newStatus = StatusConstants.PROPOSAL_PENDING_APPROVAL;
                }
                if (status == StatusConstants.PROPOSAL_APPROVED
                        || status == StatusConstants.SUBMISSION_REJECTED) {
                    newStatus = StatusConstants.SUBMISSION_PENDING_APPROVAL;
                }

                sqlStr.replace(0, sqlStr.length(), "");
                sqlStr.append("UPDATE problem SET status_id = ? WHERE problem_id = ?");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, newStatus);
                ps.setInt(2, problemId);
                ps.executeUpdate();
            }

            ProblemIdStructure idstruct = getProblemIdStructure(problemId, conn);

            //if it is a SINGLE_PROBLEM or LONG_PROBLEM, the broadcast will be taken care of by
            //the saveComponent method.
            if (problem.getProblemTypeID() != ServerContestConstants.SINGLE_PROBLEM &&
                    problem.getProblemTypeID() != ServerContestConstants.LONG_PROBLEM) {
                broadcastProblemChange(problemId, userId, connectionId, conn);
            }

            submitInfo.add(Boolean.TRUE);
            submitInfo.add(idstruct);
        } catch (Exception e) {
            s_trace.error("Error in saveProblem.", e);
            submitInfo.add(new Boolean(false));
            submitInfo.add(ApplicationConstants.SERVER_ERROR);
        } finally {
            if (submitInfo.size() < 1 || submitInfo.get(0) == null ||
                    (!(submitInfo.get(0) instanceof Boolean))) {
//        debug("Rolling back in saveProblem because submitInfo is "
//                    + "incomplete.");
//        rollback(conn);
            } else if (submitInfo.get(0).equals(Boolean.FALSE)) {
//        debug("Rolling back in saveProblem because success is false.");
//        rollback(conn);
            } else {
//        debug("Commiting in saveProblem because success is true.");
//        commit(conn);
            }
            //closeConnection(conn);
            closeConnection(null, ps2, null);
            closeConnection(conn, ps, rs);
        }

        return submitInfo;
    }

    /**
     * Saves information on a component. The information saved is based
     * on the user's role in the component and the status of the component.
     * The method performs error checking and does not allow the save
     * to complete if any errors occur.
     * Returns an ArrayList which is
     * {Boolean.TRUE, ComponentIdStructure(idstructure)} if the
     * save went ok and {Boolean.FALSE, String(message)} if not.
     *
     * @param component ComponentInformation object containing the problem
     * @param userId The user id of the user trying to save the problem
     * @param connectionId The connection id of the user trying to save
     *                     the problem (so the connection doesn't get the
     *                     Problem Modified broadcast).
     */
    public ArrayList saveComponent(ComponentInformation component, int userId,
            int connectionId) throws RemoteException {
        ArrayList result = new ArrayList();
        Connection conn = null;
        try {
            conn = getConnection();
//      conn.setAutoCommit(false);
            result = saveComponent(component, userId, connectionId, conn, false);
        } catch (Exception e) {
            s_trace.error("Exception while saving component.", e);
            result = new ArrayList();
            result.add(Boolean.FALSE);
            result.add(ApplicationConstants.SERVER_ERROR);
        } finally {
            if (result == null || result.size() < 1 || result.get(0) == null
                    || !(result.get(0) instanceof Boolean)) {
//        debug("Rolling back in saveComponent because result is not " +
//                    "complete.");
//        rollback(conn);
            } else if (result.get(0).equals(Boolean.FALSE)) {
//        debug("Rolling back in saveComponent because result is false.");
//        rollback(conn);
            } else {
//        debug("Commiting in saveComponent because result is true.");
//        commit(conn);
            }
            //closeConnection(conn);
            closeConnection(conn, null, null);
        }
        return result;
    }

    /**
     * Saves the component. Takes a Connection as parameter to use.
     *
     * @param component
     *         the problem component.
     * @param userId
     *         the user id.
     * @param connectionId
     *         the connection id.
     * @param conn
     *         the sql connection.
     * @param fromSubmitProblem
     *         whether the component is from submit problem.
     * @return the message result with save status.
     */
    private ArrayList saveComponent(ComponentInformation component, int userId,
            int connectionId, Connection conn, boolean fromSubmitProblem) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(256);
        ArrayList result = new ArrayList();

        try {
            int componentId = component.getComponentId();

            String errors = checkGeneralComponentInfo(component, conn);
            if (errors.length() != 0) {
                result.add(Boolean.FALSE);
                result.add(errors);
                return result;
            }
            //NOTE: it is guarenteed at this point that the component xml is valid,
            //otherwise we would have gotten an error above
            boolean newProblem = false;
            if (componentId == -1)  //needs to be inserted
            {
                newProblem = true;
                componentId = IdGeneratorClient.getSeqIdAsInt(DBMS.COMPONENT_SEQ);
                component.setComponentId(componentId);
                debug("Inserting new component for \"" +
                        component.getClassName() + "\", componentId = " +
                        componentId);

                sqlStr = new StringBuilder(256);
                sqlStr.append("INSERT INTO component ");
                sqlStr.append("(component_id ");
                sqlStr.append(",problem_id ");
                sqlStr.append(",result_type_id ");
                sqlStr.append(",class_name ");
                sqlStr.append(",method_name ");
                sqlStr.append(",status_id ");
                sqlStr.append(",component_type_id) ");
                sqlStr.append("VALUES (?, ?, ?, ?, ?, ?, ?)");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, componentId);
                ps.setInt(2, component.getProblemId());
                ps.setInt(3, getDataType(component.getReturnType().getDescription())
                        .getID());
                ps.setString(4, component.getMethodName());
                ps.setString(5, component.getClassName());
                ps.setInt(6, StatusConstants.ACTIVE);
                s_trace.debug("component_type_id = " + component.getComponentTypeID());
                ps.setInt(7, component.getComponentTypeID());
                ps.executeUpdate();

                //Let this guy be the component writer.
                sqlStr = new StringBuilder(256);
                sqlStr.append("INSERT INTO component_user_xref ");
                sqlStr.append("(component_id ");
                sqlStr.append(",user_id ");
                sqlStr.append(",user_type_id) ");
                sqlStr.append("VALUES (?, ?, ?) ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, componentId);
                ps.setInt(2, userId);
                ps.setInt(3, ApplicationConstants.PROBLEM_WRITER);
                ps.executeUpdate();
            }
            
            ReadWriteLock lockForComponent = getLockForComponent(componentId);
            WriteLock writeLock = lockForComponent.writeLock();
            writeLock.lock();
            try {
                int userType = getUserTypeForComponent(componentId, userId, conn);
                if (userType != ApplicationConstants.PROBLEM_WRITER &&
                        userType != ApplicationConstants.PROBLEM_ADMIN &&
                        userType != ApplicationConstants.PROBLEM_TESTER) {
                    result.add(Boolean.FALSE);
                    result.add("You do not have permission to save the statement.");
                    return result;
                }
                //save the statement stuff
                sqlStr = new StringBuilder(256);
                sqlStr.append("UPDATE component ");
                sqlStr.append("SET method_name = ? ");
                sqlStr.append(",class_name = ? ");
                sqlStr.append(",result_type_id = ? ");
                sqlStr.append(",component_type_id = ? ");
                sqlStr.append("WHERE component_id = ?");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setString(1, component.getMethodName());
                ps.setString(2, component.getClassName());
                ps.setInt(3, getDataType(component.getReturnType().getDescription())
                        .getID());
                ps.setInt(4, component.getComponentTypeID());
                ps.setInt(5, componentId);
                ps.executeUpdate();
                ps.close();                
                //delete old parameters
                sqlStr.replace(0, sqlStr.length(), "");
                sqlStr.append("DELETE FROM parameter WHERE component_id = ? ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, componentId);
                ps.executeUpdate();
                ps.close();
                //insert new parameters
                insertParameters(component, conn);
                //what is the status of the problem?
                sqlStr = new StringBuilder(256);
                sqlStr.append("SELECT status_id ");
                sqlStr.append("FROM problem ");
                sqlStr.append("WHERE problem_id = ? ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, component.getProblemId());
                rs = ps.executeQuery();
                rs.next();
                int status = rs.getInt(1);
                boolean pastProposal = status == StatusConstants.PROPOSAL_APPROVED
                        || status == StatusConstants.SUBMISSION_PENDING_APPROVAL
                        || status == StatusConstants.SUBMISSION_REJECTED
                        || status == StatusConstants.SUBMISSION_APPROVED
                        || status == StatusConstants.TESTING
                        || status == StatusConstants.FINAL_TESTING
                        || status == StatusConstants.READY
                        || status == StatusConstants.USED;
                //if the status is proposal accepted, or better,
                //insert / update the solution
                if (pastProposal) {
                    //int solutionId = getSolutionId(componentId, userId, conn);
                    getSolutionId(componentId, userId, conn);

                    //compile the solution
                    SolutionInformation solution = component.getSolution();
                    String solutionText = solution.getText();
                    HashMap classFiles = new HashMap();
                    String fileName = component.getClassName() + "." + solution.getLanguage().getDefaultExtension();
                    classFiles.put(fileName, solutionText);
                    ArrayList compileResult = getMPSQASServices().compileSolution(
                            classFiles, solution.getLanguage().getId(),
                            componentId, userId);
                    if (Boolean.FALSE.equals(compileResult.get(0))) {
                        result.add(Boolean.FALSE);
                        result.add("Solution does not compile.");
                        return result;
                    }

                    //make sure all test cases pass check data and the solution return
                    //something for all of them
                    if (component.getTestCases().length <
                            ApplicationConstants.MIN_TEST_CASES) {
                        result.add(Boolean.FALSE);
                        result.add("You must have at least "
                                + ApplicationConstants.MIN_TEST_CASES + " test cases.");
                        return result;
                    }

                    //get the primary solution id, for check data and test cases
                    int primarySolutionId = getPrimarySolutionId(componentId, conn);

                    if (primarySolutionId == -1) {
                        result.add(Boolean.FALSE);
                        result.add("No primary solution to run test cases against.");
                        return result;
                    }

                    MPSQASFiles tresults;
                    Object[] args = new Object[component.getTestCases().length];
                    Object[] expectedValues = new Object[component.getTestCases().length];
                    //int i = 0;
                    for(int i = 0; i < expectedValues.length;i++)
                    {
                        try {
                            args[i] = DataValue.parseValuesToObjects(
                                    component.getTestCases()[i].getInput(),
                                    component.getParamTypes());
                        } catch (Exception e) {
                            result.add(Boolean.FALSE);
                            result.add("Cannot parse args for test case "
                                    + i + ":  " + e.getMessage());
                            return result;
                        }

                        try {
                            tresults = getMPSQASServices().test((Object[]) args[i], componentId,
                                    primarySolutionId, "checkData");
                        } catch (Exception e) {
                            e.printStackTrace();
                            result.add(Boolean.FALSE);
                            result.add("Test case " + i + " doesn't pass checkData.");
                            return result;
                        }

                        if (!tresults.getTestStatus() || !"".equals(tresults.getResult())) {
                            result.add(Boolean.FALSE);
                            result.add("Test case " + i + " doesn't pass checkData.");
                            return result;
                        }

                        try {
                            tresults = getMPSQASServices().test((Object[]) args[i], componentId,
                                    primarySolutionId, component.getMethodName());
                        } catch (Exception e) {
                            e.printStackTrace();
                            result.add(Boolean.FALSE);
                            result.add("Cannot get expected result for test case " + i + ".");
                            return result;
                        }

                        if (!tresults.getTestStatus()) {
                            result.add(Boolean.FALSE);
                            result.add("Cannot get expected result for test case " + i + ".");
                            return result;
                        }

                        expectedValues[i] = tresults.getResult();

                        try {
                            component.getTestCases()[i].setOutput(
                                    DataValue.convertObjectToDataValue(
                                            tresults.getResult(),
                                            getDataType(component.getReturnType().getDescription()))
                                            .encode());
                        } catch (Exception e) {
                            result.add(Boolean.FALSE);
                            result.add("Cannot parse output for test case "
                                    + i + ":  " + e.getMessage());
                            return result;
                        }

                    }
                    //three threads, currently disabled
                    /*
                    MPSQASTesterThread thread1 = null;
                    MPSQASTesterThread thread2 = null;
                    MPSQASTesterThread thread3 = null;

                    while(i < component.getTestCases().length)
                    {
                        if(thread1 == null)
                        {
                            try {
                                args[i] = DataValue.parseValuesToObjects(
                                        component.getTestCases()[i].getInput(),
                                        component.getParamTypes());
                            } catch (Exception e) {
                                result.add(Boolean.FALSE);
                                result.add("Cannot parse args for test case "
                                        + i + ":  " + e.getMessage());
                                return result;
                            }

                            //create thread with current options
                            thread1 = new MPSQASTesterThread(i, args[i], primarySolutionId, componentId, conn, component.getMethodName());
                            thread1.start();
                            i++;
                        }
                        else if(thread2 == null)
                        {
                            try {
                                args[i] = DataValue.parseValuesToObjects(
                                        component.getTestCases()[i].getInput(),
                                        component.getParamTypes());
                            } catch (Exception e) {
                                result.add(Boolean.FALSE);
                                result.add("Cannot parse args for test case "
                                        + i + ":  " + e.getMessage());
                                return result;
                            }

                            //create thread with current options
                            thread2 = new MPSQASTesterThread(i, args[i], primarySolutionId, componentId, conn, component.getMethodName());
                            thread2.start();
                            i++;
                        }
                        else if(thread3 == null)
                        {
                            try {
                                args[i] = DataValue.parseValuesToObjects(
                                        component.getTestCases()[i].getInput(),
                                        component.getParamTypes());
                            } catch (Exception e) {
                                result.add(Boolean.FALSE);
                                result.add("Cannot parse args for test case "
                                        + i + ":  " + e.getMessage());
                                return result;
                            }

                            //create thread with current options
                            thread3 = new MPSQASTesterThread(i, args[i], primarySolutionId, componentId, conn, component.getMethodName());
                            thread3.start();
                            i++;
                        }
                        else if(thread1.isDone())
                        {
                            if(thread1.getRetVal() != "")
                            {
                                result.add(Boolean.FALSE);
                                result.add(thread1.getRetVal());
                                return result;
                            }
                            else
                            {
                                //record results and start new thread
                                expectedValues[thread1.getNum()] = thread1.getResults().getResult();

                                try {
                                    component.getTestCases()[thread1.getNum()].setOutput(
                                            DataValue.convertObjectToDataValue(
                                                    thread1.getResults().getResult(),
                                                    getDataType(component.getReturnType().getDescription()))
                                            .encode());
                                } catch (Exception e) {
                                    result.add(Boolean.FALSE);
                                    result.add("Cannot parse output for test case "
                                            + thread1.getNum() + ":  " + e.getMessage());
                                    return result;
                                }

                                thread1 = null;
                            }
                        }
                        else if(thread2.isDone())
                        {
                            if(thread2.getRetVal() != "")
                            {
                                result.add(Boolean.FALSE);
                                result.add(thread2.getRetVal());
                                return result;
                            }
                            else
                            {
                                //record results and start new thread
                                expectedValues[thread2.getNum()] = thread2.getResults().getResult();

                                try {
                                    component.getTestCases()[thread2.getNum()].setOutput(
                                            DataValue.convertObjectToDataValue(
                                                    thread2.getResults().getResult(),
                                                    getDataType(component.getReturnType().getDescription()))
                                            .encode());
                                } catch (Exception e) {
                                    result.add(Boolean.FALSE);
                                    result.add("Cannot parse output for test case "
                                            + thread2.getNum() + ":  " + e.getMessage());
                                    return result;
                                }

                                thread2 = null;
                            }
                        }
                        else if(thread3.isDone())
                        {
                            if(thread3.getRetVal() != "")
                            {
                                result.add(Boolean.FALSE);
                                result.add(thread3.getRetVal());
                                return result;
                            }
                            else
                            {
                                //record results and start new thread
                                expectedValues[thread3.getNum()] = thread3.getResults().getResult();

                                try {
                                    component.getTestCases()[thread3.getNum()].setOutput(
                                            DataValue.convertObjectToDataValue(
                                                    thread3.getResults().getResult(),
                                                    getDataType(component.getReturnType().getDescription()))
                                            .encode());
                                } catch (Exception e) {
                                    result.add(Boolean.FALSE);
                                    result.add("Cannot parse output for test case "
                                            + thread3.getNum() + ":  " + e.getMessage());
                                    return result;
                                }

                                thread3 = null;
                            }
                        }
                        else
                        {
                            //wait a bit and check again
                            Thread.sleep(100);
                        }
                    }

                    while( !( thread1 == null && thread2 == null && thread3 == null  ) )
                    {
                        //wait for all exist threads to finish
                        if(thread1 != null && thread1.isDone())
                        {
                            if(thread1.getRetVal() != "")
                            {
                                result.add(Boolean.FALSE);
                                result.add(thread1.getRetVal());
                                return result;
                            }
                            else
                            {
                                //record results and start new thread
                                expectedValues[thread1.getNum()] = thread1.getResults().getResult();

                                try {
                                    component.getTestCases()[thread1.getNum()].setOutput(
                                            DataValue.convertObjectToDataValue(
                                                    thread1.getResults().getResult(),
                                                    getDataType(component.getReturnType().getDescription()))
                                            .encode());
                                } catch (Exception e) {
                                    result.add(Boolean.FALSE);
                                    result.add("Cannot parse output for test case "
                                            + thread1.getNum() + ":  " + e.getMessage());
                                    return result;
                                }

                                thread1 = null;
                            }
                        }
                        else if(thread2 != null && thread2.isDone())
                        {
                            if(thread2.getRetVal() != "")
                            {
                                result.add(Boolean.FALSE);
                                result.add(thread2.getRetVal());
                                return result;
                            }
                            else
                            {
                                //record results and start new thread
                                expectedValues[thread2.getNum()] = thread2.getResults().getResult();

                                try {
                                    component.getTestCases()[thread2.getNum()].setOutput(
                                            DataValue.convertObjectToDataValue(
                                                    thread2.getResults().getResult(),
                                                    getDataType(component.getReturnType().getDescription()))
                                            .encode());
                                } catch (Exception e) {
                                    result.add(Boolean.FALSE);
                                    result.add("Cannot parse output for test case "
                                            + thread2.getNum() + ":  " + e.getMessage());
                                    return result;
                                }

                                thread2 = null;
                            }
                        }
                        else if(thread3 != null && thread3.isDone())
                        {
                            if(thread3.getRetVal() != "")
                            {
                                result.add(Boolean.FALSE);
                                result.add(thread3.getRetVal());
                                return result;
                            }
                            else
                            {
                                //record results and start new thread
                                expectedValues[thread3.getNum()] = thread3.getResults().getResult();

                                try {
                                    component.getTestCases()[thread3.getNum()].setOutput(
                                            DataValue.convertObjectToDataValue(
                                                    thread3.getResults().getResult(),
                                                    getDataType(component.getReturnType().getDescription()))
                                            .encode());
                                } catch (Exception e) {
                                    result.add(Boolean.FALSE);
                                    result.add("Cannot parse output for test case "
                                            + thread3.getNum() + ":  " + e.getMessage());
                                    return result;
                                }

                                thread3 = null;
                            }
                        }
                        else
                        {
                            //wait a bit and check again
                            Thread.sleep(100);
                        }
                    }
                     */

                    insertTestCases(component.getTestCases(), args, expectedValues,
                            componentId, conn);
                }
                //update the component_text now that the test cases have been
                //populated and all.
                sqlStr = new StringBuilder(256);
                sqlStr.append("UPDATE component ");
                sqlStr.append("SET component_text = ? ");
                sqlStr.append("WHERE component_id = ? ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setBytes(1, DBMS.serializeTextString(component.toXML()));
                ps.setInt(2, componentId);
                ps.executeUpdate();
                
                if(!newProblem) {
                    //we need to clear the simple component cache
                    CoreServices.removeSimpleComponentFromCache(componentId);
                }
                
                //update categories
                int selectedCategoriesCount = updateComponentCategories(component, conn);
                if(selectedCategoriesCount == 0 && !newProblem){
                    result.add(Boolean.FALSE);
                    result.add("Please select at least one category");
                    return result;
                }

                // Save submission rate settings.
                saveLongComponentConfiguration(component, conn);

                broadcastComponentChange(componentId, userId, connectionId, conn,
                        !fromSubmitProblem);
                ComponentIdStructure idstruct = getComponentIdStructure(componentId,
                        conn);
                result.add(Boolean.TRUE);
                result.add(idstruct);
            } finally {
                writeLock.unlock();
            }
        } catch (Exception e) {
            s_trace.error("Error saving component.", e);
            result.add(Boolean.FALSE);
            result.add(ApplicationConstants.SERVER_ERROR);
        } finally {
            closeConnection(null, ps, rs);
        }
        return result;
    }

    /**
     * Updates components categories.
     *
     * @param component Component information
     * @param conn Connection to use
     *
     * @throws SQLException If an exception is thrown during the process
     *
     * @return The number of selected categories
     */
    private int updateComponentCategories(ProblemComponent component, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        try {
            int checkedCount = 0;
            int componentId = component.getComponentId();
            ArrayList ccs = component.getCategories();
            StringBuilder sqlStr = new StringBuilder(256);
            sqlStr.append("DELETE from component_category_xref ");
            sqlStr.append("WHERE component_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            ps.executeUpdate();
            ps.close();

            sqlStr.setLength(0);
            sqlStr.append("INSERT INTO component_category_xref ");
            sqlStr.append("(component_category_id,component_id) VALUES (?,?)");
            ps = conn.prepareStatement(sqlStr.toString());

            for(int i = 0; i<ccs.size(); i++){
                ComponentCategory cc = (ComponentCategory)ccs.get(i);
                if(!cc.getChecked())continue;
                checkedCount++;
                ps.setInt(1, cc.getId());
                ps.setInt(2, componentId);
                ps.executeUpdate();
            }
            return checkedCount;
        } finally {
            DBMS.close(ps);
        }
    }

    /**
     * Saves just the problem statement for a problem (the problem_text and name
     * field of the database).  Returns an ArrayList whose first element
     * is a Boolean representing the success of the save and the second is a
     * String error message if the save was not successful.
     */
    public ArrayList saveProblemStatement(Problem problem, int userId,
            int connectionId) {
        java.sql.Connection conn = null;
        StringBuilder sqlStr = new StringBuilder();
        PreparedStatement ps = null;
        ArrayList result = new ArrayList();

        try {
            conn = getConnection();
//      conn.setAutoCommit(false);

            int problemId = problem.getProblemId();
            if (problemId == -1) {
                result.add(Boolean.FALSE);
                result.add("This problem does not yet exist, submit it first.");
                return result;
            }

            int userTypeId = getUserTypeForProblem(problemId, userId, conn);
            if (userTypeId != ApplicationConstants.PROBLEM_WRITER &&
                    userTypeId != ApplicationConstants.PROBLEM_ADMIN) {
                result.add(Boolean.FALSE);
                result.add("You do not have permission to save the statement.");
                return result;
            }

            sqlStr.append("UPDATE problem ");
            sqlStr.append("SET name = ? ");
            sqlStr.append(",problem_text = ? ");
            sqlStr.append("WHERE problem_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setString(1, problem.getName());
            ps.setBytes(2, DBMS.serializeTextString(problem.getProblemText()));
            ps.setInt(3, problemId);
            if (ps.executeUpdate() != 1) {
                result.add(Boolean.FALSE);
                result.add("This problem does not exist, please contact us.");
                return result;
            }

            broadcastProblemChange(problemId, userId, connectionId, conn);

            result.add(Boolean.TRUE);
        } catch (Exception e) {
            s_trace.error("Error saving problem statement.", e);
            result.add(Boolean.FALSE);
            result.add(ApplicationConstants.SERVER_ERROR);
        } finally {
            if (result == null || result.size() < 1 || result.get(0) == null
                    || !(result.get(0) instanceof Boolean)) {
//        debug("Rolling back in saveProblemStatement because result is " +
//                    "not complete.");
//        rollback(conn);
            } else if (result.get(0).equals(Boolean.FALSE)) {
//        debug("Rolling back in saveProblemStatement because result is " +
//                    "false.");
//        rollback(conn);
            } else {
//        debug("Commiting in saveProblemStatement because result is "
//                    + "true.");
//        commit(conn);
            }
            //closeConnection(conn, ps);
            closeConnection(conn, ps, null);

        }
        return result;
    }

    /**
     * Saves just the problem statement for a component (the component_text,
     * class_name, method_name, result_type, parameters and settings)
     * Returns an ArrayList whose first element
     * is a Boolean representing the success of the save and the second is a
     * String error message if the save was not successful.
     *
     * @param component
     *        the problem component.
     * @param userId
     *        the user id.
     * @param connectionId
     *        the connection id.
     * @return the save result message with status.
     */
    public ArrayList saveComponentStatement(ProblemComponent component,
            int userId, int connectionId) {
        java.sql.Connection conn = null;
        StringBuilder sqlStr = new StringBuilder();
        PreparedStatement ps = null;
        ArrayList result = new ArrayList();

        try {
            conn = getConnection();
//      conn.setAutoCommit(false);

            int componentId = component.getComponentId();
            if (componentId == -1) {
                result.add(Boolean.FALSE);
                result.add("This component does not yet exist, submit it first.");
                return result;
            }
            
            //we need to clear the simple component cache
            CoreServices.removeSimpleComponentFromCache(componentId);
            
            ReadWriteLock lockForComponent = getLockForComponent(componentId);
            WriteLock writeLock = lockForComponent.writeLock();
            writeLock.lock();
            try {
                int userTypeId = getUserTypeForComponent(componentId, userId, conn);
                if (userTypeId != ApplicationConstants.PROBLEM_WRITER &&
                        userTypeId != ApplicationConstants.PROBLEM_ADMIN &&
                        userTypeId != ApplicationConstants.PROBLEM_TESTER) {
                    result.add(Boolean.FALSE);
                    result.add("You do not have permission to save the statement.");
                    return result;
                }

                String errors = checkGeneralComponentInfo(component, conn);
                if (errors.length() > 0) {
                    result.add(Boolean.FALSE);
                    result.add(errors);
                    return result;
                }

                //make a quick attempt at getting the results for the test cases
                ArrayList unknownCasesAL = new ArrayList();
                for (int i = 0; i < component.getTestCases().length; i++) {
                    if (component.getTestCases()[i].getOutput().equals(
                            com.topcoder.shared.problem.TestCase.UNKNOWN_OUTPUT)
                            || component.getTestCases()[i].getOutput().equals(
                                    com.topcoder.shared.problem.TestCase.ERROR)) {
                        unknownCasesAL.add(component.getTestCases()[i]);
                    }
                }

                com.topcoder.shared.problem.TestCase[] unknownCases = new
                        com.topcoder.shared.problem.TestCase[unknownCasesAL.size()];
                for (int i = 0; i < unknownCases.length; i++) {
                    unknownCases[i] = (com.topcoder.shared.problem.TestCase)
                            unknownCasesAL.get(i);
                }

                if (unknownCases.length > 0) {
                    s_trace.debug("Filling test case output of " + unknownCases.length +
                            " cases.");
                    fillTestCasesOutput(unknownCases, component.getComponentId());
                }


                //delete old parameters
                sqlStr = new StringBuilder(256);
                sqlStr.append("DELETE FROM parameter WHERE component_id = ? ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, componentId);
                ps.executeUpdate();
                ps.close();

                //insert new parameters
                insertParameters(component, conn);

                //update categories
                updateComponentCategories(component, conn);

                //insert test cases into the system_test_case table, some of
                //the cases may be incomplete (no expected result)
                Object[] args = new Object[component.getTestCases().length];
                Object[] results = new Object[component.getTestCases().length];
                for (int i = 0; i < component.getTestCases().length; i++) {
                    try {
                        args[i] = DataValue.parseValuesToObjects(
                                component.getTestCases()[i].getInput(),
                                component.getParamTypes());
                    } catch (DataValueParseException e) {
                        result.add(Boolean.FALSE);
                        result.add("Cannot parse args for test case " + i + ":  "
                                + e.getMessage());
                        return result;

                    }

                    if (component.getTestCases()[i].getOutput().equals(
                            com.topcoder.shared.problem.TestCase.UNKNOWN_OUTPUT) ||
                            component.getTestCases()[i].getOutput().equals(
                                    com.topcoder.shared.problem.TestCase.ERROR)) {
                        results[i] = null;
                    } else {
                        try {
                            results[i] = DataValue.parseValueToObject(
                                    component.getTestCases()[i].getOutput(),
                                    getDataType(component.getReturnType().getDescription()));
                        } catch (DataValueParseException e) {
                            result.add(Boolean.FALSE);
                            result.add("Cannot parse output for test case " + i + ":  "
                                    + e.getMessage());
                            return result;
                        }
                    }
                }

                insertTestCases(component.getTestCases(), args, results,
                        componentId, conn);

                // Component text should be updated after inserting the test cases
                sqlStr = new StringBuilder(256);
                sqlStr.append("UPDATE component ");
                sqlStr.append("SET class_name = ? ");
                sqlStr.append(",method_name = ? ");
                sqlStr.append(",result_type_id = ? ");
                sqlStr.append(",component_text = ? ");
                sqlStr.append("WHERE component_id = ? ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setString(1, component.getClassName());
                ps.setString(2, component.getMethodName());
                ps.setInt(3, getDataType(component.getReturnType().getDescription())
                        .getID());
                ps.setBytes(4, DBMS.serializeTextString(component.toXML()));
                ps.setInt(5, componentId);
                if (ps.executeUpdate() != 1) {
                    result.add(Boolean.FALSE);
                    result.add("This component does not exist, please contact us.");
                    return result;
                }
                ps.close();

                saveLongComponentConfiguration(component, conn);
            } finally {
                writeLock.unlock();
            }

            broadcastComponentChange(componentId, userId, connectionId, conn, true);

            result.add(Boolean.TRUE);
        } catch (Exception e) {
            s_trace.error("Error saving component statement.", e);
            result.add(Boolean.FALSE);
            result.add(ApplicationConstants.SERVER_ERROR);
        } finally {
            if (result == null || result.size() < 1 || result.get(0) == null
                    || !(result.get(0) instanceof Boolean)) {
//        debug("Rolling back in saveComponentStatement because result " +
//                    "not complete.");
//        rollback(conn);
            } else if (result.get(0).equals(Boolean.FALSE)) {
//        debug("Rolling back in saveComponentStatement because result " +
//                    "is false.");
//        rollback(conn);
            } else {
//        debug("Commiting in saveComponentStatement because result is " +
//                    "true.");
//        commit(conn);
            }
            //closeConnection(conn, ps);
            closeConnection(conn, ps, null);
        }
        return result;
    }

    /**
     * Sends a broadcast notifying that the problem has been changed.
     */
    private void broadcastProblemChange(int problemId, int userId,
            int connectionId, Connection conn) {
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("SELECT handle FROM user WHERE user_id = ? ");
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            rs.next();
            String handle = rs.getString("handle");
            BroadcastPublisher.broadcast(new ProblemModifiedBroadcast(problemId,
                    handle, connectionId));
        } catch (Exception e) {
            s_trace.error("Error sending problem modified broadcast.", e);
        } finally {
            closeConnection(null, ps, rs);
        }
    }

    /**
     * Sends a broadcast notifying that the problem has been changed.
     */
    private void broadcastComponentChange(int componentId, int userId,
            int connectionId, Connection conn, boolean withProblem) {
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("SELECT handle FROM user WHERE user_id = ? ");
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            rs.next();
            String handle = rs.getString("handle");
            BroadcastPublisher.broadcast(new ComponentModifiedBroadcast(componentId,
                    handle, connectionId));
            rs.close();
            ps.close();

            if (withProblem) {
                //if it's a team problem, send the problem modified too.
                StringBuilder sqlStr = new StringBuilder(256);
                sqlStr.append("SELECT p.problem_id AS id ");
                sqlStr.append(",p.problem_type_id AS type ");
                sqlStr.append("FROM problem p ");
                sqlStr.append("JOIN component c ON c.problem_id = p.problem_id ");
                sqlStr.append("AND c.component_id = ? ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, componentId);
                rs = ps.executeQuery();
                rs.next();
                if (rs.getInt("type") == ServerContestConstants.TEAM_PROBLEM) {
                    BroadcastPublisher.broadcast(new ProblemModifiedBroadcast(
                            rs.getInt("id"), handle, connectionId));
                }
            }
        } catch (Exception e) {
            s_trace.error("Error sending component modified broadcast.", e);
        } finally {
            closeConnection(null, ps, rs);
        }
    }

    /**
     * Inserts the parameters for a problem component into the database.
     */
    private void insertParameters(ProblemComponent info, Connection conn)
            throws Exception {
        //int problemId = info.getProblemId();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            int componentId = info.getComponentId();
            StringBuilder sqlStr = new StringBuilder(100);
            sqlStr.append("INSERT INTO parameter ");
            sqlStr.append("(parameter_id ");
            sqlStr.append(",component_id ");
            sqlStr.append(",data_type_id ");
            sqlStr.append(",name ");
            sqlStr.append(",sort_order) ");
            sqlStr.append("VALUES (?, ?, ?, ?, ?)");
            ps = conn.prepareStatement(sqlStr.toString());

            DataType[] types = info.getParamTypes();
            String[] names = info.getParamNames();
            for (int j = 0; j < types.length; j++) {
                int parameterId = IdGeneratorClient.getSeqIdAsInt(DBMS.PARAMETER_SEQ);
                ps.setInt(1, parameterId);
                ps.setInt(2, componentId);
                ps.setInt(3, getDataType(types[j].getDescription()).getID());
                ps.setString(4, names[j]);
                ps.setInt(5, j + 1);
            //int added = ps.executeUpdate();
                ps.executeUpdate();
            }
            ps.close();
        } catch (Exception e) {
            s_trace.error("Error in insert Parameters", e);
        }   finally {
            closeConnection(null, ps, rs);
        }
    }

    //

    /**
     * Retrieves a list of briefly populated ProblemInformation objects.
     * The problems to get are specified by the two parameters.
     * Only team problems are returned.
     *
     * @param forType Integer describing which problems to get.
     * @param id An id further describing which problems to get (round_id, etc..)
     *
     * forType = MessageConstants.SCHEDULED_PROBLEMS_FOR_CONTEST
     *   All problems scheduled for contest, id = round_id
     * forType = MessageConstants.PROBLEMS_WITH_STATUS
     *   All problems with specified status, id = status
     * forType = MessageConstants.USER_WRITTEN_PROBLEMS
     *   All problems a user is writing, id = user_id
     * forType = MessageConstants.USER_TESTING_PROBLEMS
     *   All problems a user is testing, id = user_id
     * forType = MessageConstants.ALL_PROBLEMS
     *   All problems, id = n.a.
     */
    public ArrayList getTeamProblems(int forType, int id) {
        Connection conn = null;
        ArrayList problems = new ArrayList();
        StringBuilder sqlStr = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PreparedStatement ps2 = null;
        ResultSet rs2 = null;

        try {
            conn = getConnection();


            sqlStr = new StringBuilder(256);

            //First, build the query
            //Selects:
            sqlStr.append("SELECT p.problem_id ");
            sqlStr.append(",p.modify_date ");
            sqlStr.append(",p.status_id ");
            sqlStr.append(",p.name ");

            //Froms:
            sqlStr.append("FROM problem p ");

            //Wheres:
            sqlStr.append("WHERE p.problem_type_id = ? ");

            if (forType == MessageConstants.SCHED_PROBLEMS_FOR_CONTEST) {
                sqlStr.append("AND p.problem_id IN (SELECT c.problem_id ");
                sqlStr.append("FROM component c ");
                sqlStr.append("JOIN round_component rc ");
                sqlStr.append("ON rc.component_id = c.component_id ");
                sqlStr.append("AND rc.round_id = ?) ");
            }
            if (forType == MessageConstants.PROBLEMS_WITH_STATUS) {
                sqlStr.append("AND p.status_id = ? ");
            }
            if (forType == MessageConstants.USER_WRITTEN_PROBLEMS
                    || forType == MessageConstants.USER_TESTING_PROBLEMS) {
                sqlStr.append("AND p.problem_id IN ");
                sqlStr.append("(SELECT DISTINCT c.problem_id ");
                sqlStr.append("FROM component_user_xref cu ");
                sqlStr.append(",component c ");
                sqlStr.append("WHERE cu.user_id = ? ");
                sqlStr.append("AND cu.user_type_id = ? ");
                sqlStr.append("AND cu.component_id = c.component_id) ");
            }
            ps = conn.prepareStatement(sqlStr.toString());

            int index = 1;

            //Fill in fields
            ps.setInt(index++, ServerContestConstants.TEAM_PROBLEM);
            if (forType == MessageConstants.SCHED_PROBLEMS_FOR_CONTEST) {
                ps.setInt(index++, id);
            }
            if (forType == MessageConstants.PROBLEMS_WITH_STATUS) {
                ps.setInt(index++, id);
            }
            if (forType == MessageConstants.USER_WRITTEN_PROBLEMS) {
                ps.setInt(index++, id);
                ps.setInt(index++, ApplicationConstants.PROBLEM_WRITER);
            }
            if (forType == MessageConstants.USER_TESTING_PROBLEMS) {
                ps.setInt(index++, id);
                ps.setInt(index++, ApplicationConstants.PROBLEM_TESTER);
            }

            rs = ps.executeQuery();
            ProblemInformation problem;
            ComponentInformation component;

            //Make the query to get the components for each problem
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT c.component_id ");
            sqlStr.append(",c.class_name ");
            sqlStr.append(",c.method_name ");
            sqlStr.append(",NVL(c.modify_date, current) ");
            sqlStr.append(",c.component_type_id ");
            sqlStr.append(",NVL(u.handle, '(unknown)') ");
            sqlStr.append(",NVL(u.user_id, -1) ");
            sqlStr.append("FROM component c ");
            sqlStr.append(",user u ");
            sqlStr.append(",component_user_xref cu ");
            sqlStr.append("WHERE c.problem_id = ? ");
            sqlStr.append("AND c.component_id = cu.component_id ");
            sqlStr.append("AND cu.user_id = u.user_id ");
            sqlStr.append("AND cu.user_type_id = ? ");
            sqlStr.append("AND c.status_id = ? ");
            //testers should only see their components
            if (forType == MessageConstants.USER_TESTING_PROBLEMS) {
                sqlStr.append("AND c.component_id IN (SELECT component_id ");
                sqlStr.append("FROM component_user_xref ");
                sqlStr.append("WHERE problem_id = ? ");
                sqlStr.append("AND user_type_id = ? ");
                sqlStr.append("AND user_id = ?) ");
            }
            //if for contest, only see components in the contest
            if (forType == MessageConstants.SCHED_PROBLEMS_FOR_CONTEST) {
                sqlStr.append("AND c.component_id IN (SELECT component_id ");
                sqlStr.append("FROM round_component ");
                sqlStr.append("WHERE round_id = ?) ");
            }

            sqlStr.append("ORDER BY c.component_type_id ");
            ps2 = conn.prepareStatement(sqlStr.toString());
            ps2.setInt(2, ApplicationConstants.PROBLEM_WRITER);
            ps2.setInt(3, StatusConstants.ACTIVE);
            if (forType == MessageConstants.USER_TESTING_PROBLEMS) {
                ps2.setInt(5, ApplicationConstants.PROBLEM_TESTER);
                ps2.setInt(6, id);
            }
            ArrayList componentsAL;
            ComponentInformation[] components;

            //extract info
            while (rs.next()) {
                problem = new ProblemInformation();
                problem.setProblemId(rs.getInt(1));
                problem.setLastModified(rs.getTimestamp(2).toString());
                problem.setStatus(rs.getInt(3));
                problem.setName(rs.getString(4));

                ps2.setInt(1, rs.getInt(1));
                if (forType == MessageConstants.USER_TESTING_PROBLEMS) {
                    ps2.setInt(4, rs.getInt(1));
                }
                if (forType == MessageConstants.SCHED_PROBLEMS_FOR_CONTEST) {
                    ps2.setInt(4, id);
                }
                rs2 = ps2.executeQuery();
                componentsAL = new ArrayList();
                while (rs2.next()) {
                    component = new ComponentInformation();
                    component.setComponentId(rs2.getInt(1));
                    component.setClassName(rs2.getString(2));
                    component.setMethodName(rs2.getString(3));
                    component.setLastModified(rs2.getTimestamp(4).toString());
                    component.setComponentTypeID(rs2.getInt(5));
                    problem.setWriter(new UserInformation(rs2.getString(6),
                            rs2.getInt(7)));
                    componentsAL.add(component);
                }
                rs2.close();
                components = new ComponentInformation[componentsAL.size()];
                for (int i = 0; i < components.length; i++) {
                    components[i] = (ComponentInformation) componentsAL.get(i);
                }
                problem.setProblemComponents(components);
                problems.add(problem);
            }
        } catch (Exception e) {
            s_trace.error("Error getting available problems: ", e);
            s_trace.debug("SQL was: " + sqlStr);
        } finally {
            closeConnection(null, ps2, rs2);
            closeConnection(conn, ps, rs);
        }
        return problems;
    }

    /**
     * Retrieves a list of briefly populated ProblemInformation objects.
     * The problems to get are specified by the two parameters.
     * Only single problems are returned.
     *
     * @param forType Integer describing which problems to get.
     * @param id An id further describing which problems to get (round_id, etc..)
     *
     * forType = MessageConstants.SCHEDULED_PROBLEMS_FOR_CONTEST
     *   All problems scheduled for contest, id = round_id
     * forType = MessageConstants.PROBLEMS_WITH_STATUS
     *   All problems with specified status, id = status
     * forType = MessageConstants.USER_WRITTEN_PROBLEMS
     *   All problems a user is writing, id = user_id
     * forType = MessageConstants.USER_TESTING_PROBLEMS
     *   All problems a user is testing, id = user_id
     * forType = MessageConstants.ALL_PROBLEMS
     *   All problems, id = n.a.
     */
    public ArrayList getSingleProblems(int forType, int id) {
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList problems = new ArrayList();
        StringBuilder sqlStr = new StringBuilder(256);

        try {
            conn = getConnection();

            //First, build the query
            //Selects:
            sqlStr.append("SELECT c.class_name ");
            sqlStr.append(",NVL(u.handle, 'unknown') ");
            sqlStr.append(",NVL(u.user_id, -1) ");
            if (forType == MessageConstants.SCHED_PROBLEMS_FOR_CONTEST) {
                sqlStr.append(",rc.difficulty_id ");
                sqlStr.append(",rc.division_id ");
                sqlStr.append(",rc.points ");
            } else {
                sqlStr.append(",p.proposed_difficulty_id ");
                sqlStr.append(",p.proposed_division_id ");
            }
            sqlStr.append(",p.problem_id ");
            sqlStr.append(",p.modify_date ");
            sqlStr.append(",c.method_name ");
            sqlStr.append(",p.status_id ");
            sqlStr.append(",p.name ");

            //Froms:
            sqlStr.append(" FROM problem p ");
            if (forType == MessageConstants.SCHED_PROBLEMS_FOR_CONTEST) {
                sqlStr.append(" ,round_component rc ");
            }
            sqlStr.append(",component c ");
            sqlStr.append("LEFT OUTER JOIN component_user_xref cu ");
            sqlStr.append("ON c.component_id = cu.component_id ");
            sqlStr.append("AND cu.user_type_id = ? ");
            sqlStr.append("LEFT OUTER JOIN user u ON u.user_id = cu.user_id ");

            //Wheres:
            sqlStr.append("WHERE c.problem_id = p.problem_id ");
            if (forType == MessageConstants.SCHED_PROBLEMS_FOR_CONTEST) {
                sqlStr.append("AND c.component_id = rc.component_id ");
                sqlStr.append("AND rc.round_id = ? ");
            }
            if (forType == MessageConstants.PROBLEMS_WITH_STATUS) {
                sqlStr.append("AND p.status_id = ? ");
            }
            if (forType == MessageConstants.USER_WRITTEN_PROBLEMS) {
                sqlStr.append("AND u.user_id = ? ");
            }
            if (forType == MessageConstants.USER_TESTING_PROBLEMS) {
                sqlStr.append("AND c.component_id IN ");
                sqlStr.append("(SELECT component_id ");
                sqlStr.append("FROM component_user_xref ");
                sqlStr.append("WHERE user_id = ? ");
                sqlStr.append("AND user_type_id = ?)");
            }
            sqlStr.append(" AND p.problem_type_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());

            int index = 1;

            //Fill in fields
            ps.setInt(index++, ApplicationConstants.PROBLEM_WRITER);
            if (forType == MessageConstants.SCHED_PROBLEMS_FOR_CONTEST) {
                ps.setInt(index++, id);
            }
            if (forType == MessageConstants.PROBLEMS_WITH_STATUS) {
                ps.setInt(index++, id);
            }
            if (forType == MessageConstants.USER_WRITTEN_PROBLEMS) {
                ps.setInt(index++, id);
            }
            if (forType == MessageConstants.USER_TESTING_PROBLEMS) {
                ps.setInt(index++, id);
                ps.setInt(index++, ApplicationConstants.PROBLEM_TESTER);
            }
            ps.setInt(index++, ServerContestConstants.SINGLE_PROBLEM);

            rs = ps.executeQuery();

//      sqlStr = new StringBuilder(256);
//      sqlStr.append("SELECT paid ");
//      sqlStr.append(       ",pending_payment ");
//      sqlStr.append("FROM problem_user ");
//      sqlStr.append("WHERE user_id = ? ");
//      sqlStr.append("AND problem_id = ? ");
//      ps = conn.prepareStatement(sqlStr.toString());
//      ps.setInt(1, id);
//      ResultSet rs2;

            ProblemInformation problemInfo;

            //extract info
            while (rs.next()) {
                ProblemComponent stmt = new ProblemComponent();
                index = 1;
                stmt.setClassName(rs.getString(index++));
                problemInfo = new ProblemInformation();
                problemInfo.setProblemComponents(new ProblemComponent[]{stmt});
                problemInfo.setWriter(new UserInformation(rs.getString(index++),
                        rs.getInt(index++)));
                problemInfo.setDifficulty(rs.getInt(index++));
                problemInfo.setDivision(rs.getInt(index++));
                if (forType == MessageConstants.SCHED_PROBLEMS_FOR_CONTEST) {
                    problemInfo.setPoints(rs.getDouble(index++));
                }
                problemInfo.setProblemId(rs.getInt(index++));
                problemInfo.setLastModified(rs.getTimestamp(index++).toString());
                stmt.setMethodName(rs.getString(index++));
                problemInfo.setStatus(rs.getInt(index++));
                problemInfo.setName(rs.getString(index++));

//        if(forType == MessageConstants.USER_TESTING_PROBLEMS
//           || forType == MessageConstants.USER_WRITTEN_PROBLEMS)
//        {
//          ps.setInt(2, problemInfo.getProblemId());
//          rs2 = ps.executeQuery();
//          rs2.next();
//          problemInfo.setPaid(rs2.getDouble(1));
//          problemInfo.setPendingPayment(rs2.getDouble(2));
//        }
                problems.add(problemInfo);
            }
            rs.close();
        } catch (Exception e) {
            s_trace.error("Error getting available problems: ", e);
            s_trace.debug("SQL was: " + sqlStr);
        } finally {
            closeConnection(conn, ps, rs);
        }

        return problems;
    }

    /**
     * Retrieves a list of briefly populated ProblemInformation objects.
     * The problems to get are specified by the two parameters.
     * Only long problems are returned.
     *
     * @param forType Integer describing which problems to get.
     * @param id An id further describing which problems to get (round_id, etc..)
     *
     * forType = MessageConstants.SCHEDULED_PROBLEMS_FOR_CONTEST
     *   All problems scheduled for contest, id = round_id
     * forType = MessageConstants.PROBLEMS_WITH_STATUS
     *   All problems with specified status, id = status
     * forType = MessageConstants.USER_WRITTEN_PROBLEMS
     *   All problems a user is writing, id = user_id
     * forType = MessageConstants.USER_TESTING_PROBLEMS
     *   All problems a user is testing, id = user_id
     * forType = MessageConstants.ALL_PROBLEMS
     *   All problems, id = n.a.
     */
    public ArrayList getLongProblems(int forType, int id) {
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList problems = new ArrayList();
        StringBuilder sqlStr = new StringBuilder(256);

        try {
            conn = getConnection();

            //First, build the query
            //Selects:
            sqlStr.append("SELECT c.class_name ");
            sqlStr.append(",NVL(u.handle, 'unknown') ");
            sqlStr.append(",NVL(u.user_id, -1) ");
            if (forType == MessageConstants.SCHED_PROBLEMS_FOR_CONTEST) {
                sqlStr.append(",rc.difficulty_id ");
                sqlStr.append(",rc.division_id ");
                sqlStr.append(",rc.points ");
            } else {
                sqlStr.append(",p.proposed_difficulty_id ");
                sqlStr.append(",p.proposed_division_id ");
            }
            sqlStr.append(",p.problem_id ");
            sqlStr.append(",p.modify_date ");
            sqlStr.append(",c.method_name ");
            sqlStr.append(",p.status_id ");
            sqlStr.append(",p.name ");

            //Froms:
            sqlStr.append(" FROM problem p ");
            if (forType == MessageConstants.SCHED_PROBLEMS_FOR_CONTEST) {
                sqlStr.append(" ,round_component rc ");
            }
            sqlStr.append(",component c ");
            sqlStr.append("LEFT OUTER JOIN component_user_xref cu ");
            sqlStr.append("ON c.component_id = cu.component_id ");
            sqlStr.append("AND cu.user_type_id = ? ");
            sqlStr.append("LEFT OUTER JOIN user u ON u.user_id = cu.user_id ");

            //Wheres:
            sqlStr.append("WHERE c.problem_id = p.problem_id ");
            if (forType == MessageConstants.SCHED_PROBLEMS_FOR_CONTEST) {
                sqlStr.append("AND c.component_id = rc.component_id ");
                sqlStr.append("AND rc.round_id = ? ");
            }
            if (forType == MessageConstants.PROBLEMS_WITH_STATUS) {
                sqlStr.append("AND p.status_id = ? ");
            }
            if (forType == MessageConstants.USER_WRITTEN_PROBLEMS) {
                sqlStr.append("AND u.user_id = ? ");
            }
            if (forType == MessageConstants.USER_TESTING_PROBLEMS) {
                sqlStr.append("AND c.component_id IN ");
                sqlStr.append("(SELECT component_id ");
                sqlStr.append("FROM component_user_xref ");
                sqlStr.append("WHERE user_id = ? ");
                sqlStr.append("AND user_type_id = ?)");
            }
            sqlStr.append(" AND p.problem_type_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());

            int index = 1;

            //Fill in fields
            ps.setInt(index++, ApplicationConstants.PROBLEM_WRITER);
            if (forType == MessageConstants.SCHED_PROBLEMS_FOR_CONTEST) {
                ps.setInt(index++, id);
            }
            if (forType == MessageConstants.PROBLEMS_WITH_STATUS) {
                ps.setInt(index++, id);
            }
            if (forType == MessageConstants.USER_WRITTEN_PROBLEMS) {
                ps.setInt(index++, id);
            }
            if (forType == MessageConstants.USER_TESTING_PROBLEMS) {
                ps.setInt(index++, id);
                ps.setInt(index++, ApplicationConstants.PROBLEM_TESTER);
            }
            ps.setInt(index++, ServerContestConstants.LONG_PROBLEM);

            rs = ps.executeQuery();

//      sqlStr = new StringBuilder(256);
//      sqlStr.append("SELECT paid ");
//      sqlStr.append(       ",pending_payment ");
//      sqlStr.append("FROM problem_user ");
//      sqlStr.append("WHERE user_id = ? ");
//      sqlStr.append("AND problem_id = ? ");
//      ps = conn.prepareStatement(sqlStr.toString());
//      ps.setInt(1, id);
//      ResultSet rs2;

            ProblemInformation problemInfo;

            //extract info
            while (rs.next()) {
                ProblemComponent stmt = new ProblemComponent();
                index = 1;
                stmt.setClassName(rs.getString(index++));
                problemInfo = new ProblemInformation();
                problemInfo.setProblemComponents(new ProblemComponent[]{stmt});
                problemInfo.setWriter(new UserInformation(rs.getString(index++),
                        rs.getInt(index++)));
                problemInfo.setDifficulty(rs.getInt(index++));
                problemInfo.setDivision(rs.getInt(index++));
                if (forType == MessageConstants.SCHED_PROBLEMS_FOR_CONTEST) {
                    problemInfo.setPoints(rs.getDouble(index++));
                }
                problemInfo.setProblemId(rs.getInt(index++));
                problemInfo.setLastModified(rs.getTimestamp(index++).toString());
                stmt.setMethodName(rs.getString(index++));
                problemInfo.setStatus(rs.getInt(index++));
                problemInfo.setName(rs.getString(index++));

//        if(forType == MessageConstants.USER_TESTING_PROBLEMS
//           || forType == MessageConstants.USER_WRITTEN_PROBLEMS)
//        {
//          ps.setInt(2, problemInfo.getProblemId());
//          rs2 = ps.executeQuery();
//          rs2.next();
//          problemInfo.setPaid(rs2.getDouble(1));
//          problemInfo.setPendingPayment(rs2.getDouble(2));
//        }
                problems.add(problemInfo);
            }
            rs.close();
        } catch (Exception e) {
            s_trace.error("Error getting available problems: ", e);
            s_trace.debug("SQL was: " + sqlStr);
        } finally {
            closeConnection(conn, ps, rs);
        }

        return problems;
    }

    /**
     * Takes all challenges for all components in a round and turns them into
     * test cases for the components.  Returns an ArrayList that is either
     * {Boolean.TRUE} or {Boolean.FALSE, errorMessage}.
     *
     * @param roundId The round to create test cases from.
     * @param userId  The user doing the update.
     * @param conn    A Connection Object to use.
     */
    public ArrayList refreshTestCases(int roundId, int userId)
            throws ProblemServicesException {
        Connection conn = null;
        ArrayList result = new ArrayList();
        try {
            conn = getConnection();

            ResultSet rs = null;
            PreparedStatement ps = null;
            StringBuilder sqlStr = null;

            sqlStr = new StringBuilder();
            sqlStr.append("SELECT DISTINCT rc.component_id AS component_id ");
            sqlStr.append(",c.class_name AS class_name ");
            sqlStr.append("FROM round_component rc ");
            sqlStr.append(",component c ");
            sqlStr.append("WHERE c.component_id = rc.component_id ");
            sqlStr.append("AND rc.round_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            rs = ps.executeQuery();

            ArrayList componentIds = new ArrayList();
            ArrayList componentNames = new ArrayList();
            while (rs.next()) {
                debug("Doing component: " + rs.getInt("component_id"));
                componentIds.add(new Integer(rs.getInt("component_id")));
                componentNames.add(rs.getString("class_name"));
            }
            rs.close();
            ps.close();

            ArrayList cresult;
            for (int i = 0; i < componentIds.size(); i++) {
                cresult = refreshTestCasesForComponent(
                        ((Integer) componentIds.get(i)).intValue(), userId, conn, roundId);
                if (Boolean.FALSE.equals(cresult.get(0))) {
                    result.add(Boolean.FALSE);
                    result.add("Error refreshing test cases for " + componentNames.get(i)
                            + ":\n" + cresult.get(1));
                    return result;
                }
            }

            result.add(Boolean.TRUE);
            return result;
        } catch (Exception e) {
            result.add(Boolean.FALSE);
            s_trace.error("Error refreshing test cases. ", e);
            throw new ProblemServicesException(e.getMessage());
        } finally {
            closeConnection(conn, null, null);
        }
    }

    private ArrayList refreshTestCasesForComponent(int componentId, int userId,
            Connection conn, int roundId) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = null;
        ArrayList result = new ArrayList();
        ArrayList savResult = new ArrayList();

        try {
            ReadWriteLock lock = getLockForComponent(componentId);
            WriteLock writeLock = lock.writeLock();
            writeLock.lock();
            try {
                ComponentInformation component = getComponentInformation(componentId, userId, conn);
                if (component == null) {
                    result.add(Boolean.FALSE);
                    result.add("The user does not have permission to retrieve this problem.");
                    return result;
                }
                sqlStr = new StringBuilder(250);
                // Get a list of all challenges that were successful
                sqlStr.append("SELECT args FROM challenge WHERE component_id = ? AND round_id = ? ");
                sqlStr.append(" AND succeeded = 1");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, componentId);
                ps.setInt(2, roundId);
                rs = ps.executeQuery();
                ArrayList al_testCases = new ArrayList(20);
                Set argsInUse = new HashSet();
                if (component.getTestCases() != null) {
                    for (int i = 0; i < component.getTestCases().length; i++) {
                        TestCase test = component.getTestCases()[i];
                        al_testCases.add(test);
                        argsInUse.add(Arrays.asList(test.getInput()));
                    }
                } else {
                    result.add(Boolean.FALSE);
                    result.add("Original test cases for this component are null!  "
                            + "Probably because this is an old-style component and was "
                            + "submitted with MPSQAS version 1.X.");
                    return result;
                }
                com.topcoder.shared.problem.TestCase testCase;
                ArrayList al_input;
                DataValue[] dv_input;
                String[] s_input;
                while (rs.next()) {
                    try {
                        al_input = (ArrayList) DBMS.getBlobObject(rs, 1);
                        debug("Challenge becoming test case, args = " + al_input);

                        dv_input = DataValue.convertObjectsToDataValues(al_input.toArray(),
                                component.getParamTypes());

                        s_input = new String[dv_input.length];
                        for (int i = 0; i < dv_input.length; i++)
                            s_input[i] = dv_input[i].encode();

                        testCase = new com.topcoder.shared.problem.TestCase
                        (null, s_input, (Element) null, false);
                        List inputArgsAsList = Arrays.asList(s_input);
                        if (!argsInUse.contains(inputArgsAsList)) {
                            debug("Adding as test cases.");
                            al_testCases.add(testCase);
                            argsInUse.add(inputArgsAsList);
                        } else {
                            debug("Not adding as test case, already existed.");
                        }
                    } catch (DataValueParseException e) {
                        s_trace.error("Error converting objects to data values.", e);
                        result.add(Boolean.FALSE);
                        result.add("Error converting arg Objects to DataValues: "
                                + e.getMessage());
                        return result;
                    }
                }
                rs.close();
                ps.close();
                com.topcoder.shared.problem.TestCase[] testCases = new com.topcoder.shared.problem.TestCase[al_testCases.size()];
                for (int i = 0; i < al_testCases.size(); i++)
                    testCases[i] = (com.topcoder.shared.problem.TestCase) al_testCases.get(i);
                component.setTestCases(testCases);
                savResult = saveComponent(component, userId, -1, conn, false);
            } finally {
                writeLock.unlock();
            }
        } catch (Exception e) {
            s_trace.error("Error refreshTestCasesForComponent: ", e);
        }   finally {
            closeConnection(null, ps, rs);
        }
        return savResult;
    }

    private static void closeConnection(Connection conn, PreparedStatement ps,
            ResultSet rs) {
        DBMS.close(conn, ps, rs);
    }

    /**
     * Backs up a problem statement by adding it to problem_statement_history.
     *
     * @param problemId id of problem statement to back up.
     */
    /*
    private void backUpProblemStatement(int problemId, int userId) {
        if (VERBOSE) s_trace.debug("Backing up problem statement " + problemId);

        java.sql.Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();
            StringBuilder sqlStr = new StringBuilder(256);

            sqlStr.append("INSERT INTO problem_statement_history ");
            sqlStr.append("(problem_id ");
            sqlStr.append(",problem_statement ");
            sqlStr.append(",modify_date ");
            sqlStr.append(",user_id) ");
            sqlStr.append("SELECT problem_id ");
            sqlStr.append(",problem_text ");
            sqlStr.append(",current ");
            sqlStr.append("," + userId + " ");
            sqlStr.append("FROM problem ");
            sqlStr.append("WHERE problem_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, problemId);
            ps.executeUpdate();
        } catch (Exception e) {
            s_trace.error("Error backing up problem:", e);
        }
        closeConnection(conn, ps);
    }
    */

    /**
     * Parses XML into a ProblemComponent using the ProblemComponentFactory.
     *
     * @param xml The XML to parse.
     * @param componentId The ProblemComponent to populated, with the Definition
     *                section already populated.
     * @param unsafe Does the problem statement contain information intended
     *               for mpsqas only.
     */
    public ProblemComponent parseProblemStatement(String xml, boolean unsafe,
            int componentId) {
        s_trace.debug("parseProblemStatement(String, boolean) called...");
        try {
            ProblemComponentFactory factory = new ProblemComponentFactory();
            ProblemComponent problemComponent = factory.build(xml, unsafe);
            problemComponent.setComponentId(componentId);

            //make sure the test cases have their output

            ArrayList unknownCasesAL = new ArrayList();
            for (int i = 0; i < problemComponent.getTestCases().length; i++) {
                if (problemComponent.getTestCases()[i].getOutput().equals(
                        com.topcoder.shared.problem.TestCase.UNKNOWN_OUTPUT)
                        || problemComponent.getTestCases()[i].getOutput().equals(
                                com.topcoder.shared.problem.TestCase.ERROR)) {
                    unknownCasesAL.add(problemComponent.getTestCases()[i]);
                }
            }

            com.topcoder.shared.problem.TestCase[] unknownCases = new
                    com.topcoder.shared.problem.TestCase[unknownCasesAL.size()];
            for (int i = 0; i < unknownCases.length; i++) {
                unknownCases[i] = (com.topcoder.shared.problem.TestCase)
                        unknownCasesAL.get(i);
            }

            if (unknownCases.length > 0) {
                s_trace.debug("Filling test case output of " + unknownCases.length +
                        " cases.");
                fillTestCasesOutput(unknownCases, problemComponent.getComponentId());
            }

            return problemComponent;
        } catch (IOException ex) {
            s_trace.error("Error parsing problem statement", ex);
            return null;
        }
    }

    /**
     * Checks if the general problem information for a problem is valid.
     * Returns an error message if it is not, or the empty string if it is.
     *
     * @param problem The Problem to check.
     * @param conn A connection to use.
     */
    private String checkGeneralProblemInfo(ProblemInformation problem,
            Connection conn) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder errorMessage = new StringBuilder();
        StringBuilder sqlStr = new StringBuilder(256);

        try {
            if (problem.getProblemComponents().length < 1) {
                errorMessage.append("A problem must have at least 1 component.  ");
            }

        //make sure all required fields are filled.
            if (problem.getName() == null || problem.getName().equals("")) {
                errorMessage.append("Please give the problem a name.  ");
            }

        //only do the db checks if it passed the non-db checks.
            if (errorMessage.length() == 0) {
                sqlStr.append("SELECT problem_id ");
                sqlStr.append("FROM problem ");
                sqlStr.append("WHERE name = ? ");
                sqlStr.append("AND problem_id != ? ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setString(1, problem.getName());
                ps.setInt(2, problem.getProblemId());
                rs = ps.executeQuery();
                if (rs.next()) {
                    errorMessage.append("A problem with this name already exists.  ");
                }
            }
        } catch (Exception e) {
            s_trace.error("Error in checkGeneralProblemInfo", e);
        } finally {
            closeConnection(null, ps, rs);
        }
        return errorMessage.toString();
    }

    /**
     * Checks if the general component information for a component is valid.
     * Returns an error message if it is not, or the empty string if it is.
     *
     * @param component The ComponentInformation of the problem to check.
     * @param conn  A connection to use.
     */
    private String checkGeneralComponentInfo(ProblemComponent component,
            Connection conn) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder errorMessage = new StringBuilder();
        StringBuilder sqlStr = new StringBuilder(256);
        int i;

        //Make sure all required fields filled
        try {
            if (component.getComponentTypeID() == ProblemConstants.LONG_COMPONENT) {
                if (component.getClassName().trim().length() == 0 ||
                        component.toXML().trim().length() == 0) {
                    errorMessage.append("Please fill out all fields.  ");
                }
                if (component.getAllMethodNames().length == 0) {
                    errorMessage.append("Please define at least one method.  ");
                }
            }
            else if (component.getMethodName().trim().length() == 0 ||
                component.getClassName().trim().length() == 0 ||
                component.toXML().trim().length() == 0) {
                errorMessage.append("Please fill out all fields.  ");
            }

        //Do some checking to make sure the input data seems ok
             boolean charactersOk = true;
             for (i = 0; i < component.getMethodName().length(); i++) {
                 if (!Character.isLetterOrDigit(component.getMethodName().charAt(i))) {
                     charactersOk = false;
                 }
             }

             for (i = 0; i < component.getClassName().length(); i++) {
                 if (!Character.isLetterOrDigit(component.getClassName().charAt(i))) {
                      charactersOk = false;
                 }
             }

             if (errorMessage.length() == 0 &&
                     (Character.isDigit(component.getMethodName()
                     .charAt(0)) ||
                     Character.isDigit(component.getClassName()
                     .charAt(0)))) {
                 charactersOk = false;
             }

             if (!charactersOk) {
                 errorMessage.append("Class or Method name contains incorrect "
                         + "characters.  ");
             }

             //Make sure we have no other components in this problem with that class
             //name.
             sqlStr.replace(0, sqlStr.length(), "SELECT 1 ");
             sqlStr.append("FROM component ");
             sqlStr.append("WHERE class_name = ? ");
//         sqlStr.append(  "AND problem_id == ? ");
             sqlStr.append("AND component_id != ? ");
             ps = conn.prepareStatement(sqlStr.toString());
             ps.setString(1, component.getClassName());
//         ps.setInt(2, component.getProblemId());
             ps.setInt(2, component.getComponentId());
             rs = ps.executeQuery();
             if (rs.next()) {
                 errorMessage.append("A component with this class name already exists. ");
                 errorMessage.append("Please rename the class.  ");
             }

             sqlStr.replace(0, sqlStr.length(),
                     "SELECT data_type_id FROM data_type WHERE data_type_desc = ?");
             ps = conn.prepareStatement(sqlStr.toString());

             if (component.getComponentTypeID() == ProblemConstants.LONG_COMPONENT) {
                 // Make sure the param types are valid.
                 String[] methodNames = component.getAllMethodNames();
                 DataType[][] allTypes = component.getAllParamTypes();
                 for (int n=0; n<allTypes.length; n++) {
                     DataType[] types = allTypes[n];
                     for (i = 0; i < types.length; i++) {
                         ps.setString(1, types[i].getDescription());
                         rs = ps.executeQuery();
                             if (!rs.next()) {
                                 errorMessage.append("Unrecognized parameter type for method '"
                                         + methodNames[n] + "'.  " + types[i].getDescription() + " " + types[i].getID() + "  ");
                             }
                     }
                 }

                 // Make sure the return type is valid.
                 DataType[] returnTypes = component.getAllReturnTypes();
                 for (int n=0; n<returnTypes.length; n++) {
                     ps.setString(1, returnTypes[n].getDescription());
                     rs = ps.executeQuery();
                     if (!rs.next()) {
                         errorMessage.append("Unrecognized return type for method '"
                                 + methodNames[n] + "'.  ");
                     }
                 }
             } else {
                 // Make sure the param types are valid.
                 DataType[] types = component.getParamTypes();
                 for (i = 0; i < types.length; i++) {
                     ps.setString(1, types[i].getDescription());
                     rs = ps.executeQuery();
                         if (!rs.next()) {
                             errorMessage.append("Unrecognized parameter type.  "
                                     + types[i].getDescription() + " " + types[i].getID() + "  ");
                         }
                 }

                // Make sure the return type is valid.
                ps.setString(1, component.getReturnType().getDescription());
                rs = ps.executeQuery();
                if (!rs.next()) {
                    errorMessage.append("Unrecognized return type.  ");
                }
             }

             if (component.getComponentId() != -1) {
                 boolean oneChecked = false;
                 for (Iterator it = component.getCategories().iterator(); it.hasNext() && !oneChecked;) {
                    ComponentCategory category = (ComponentCategory) it.next();
                    oneChecked |= category.getChecked();
                }
                if (!oneChecked) {
                     errorMessage.append("Please select at least one category.  ");
                 }
             }

            if (errorMessage.length() == 0) {
                ProblemComponentFactory factory = new ProblemComponentFactory();

                ProblemComponent comp = factory.build(component.toXML(), true);

                if (!comp.isValid()) {
                    errorMessage.append("Problem statement XML parses with errors.  ");
                }
            }
        } catch (Exception e) {
            s_trace.error("Error in checkGeneralProblemInfo", e);
        } finally {
            closeConnection(null, ps, rs);
        }
        return errorMessage.toString();
    }

    public DataType getDataType(String id)
            throws InvalidTypeException {
        return DataTypeFactory.getDataType(id);
    }

    public DataType getDataType(int id)
            throws InvalidTypeException {
        return DataTypeFactory.getDataType(id);
    }

    /**
     * Determines the user type of a user for a component.
     *
     * @param componentId The problem id we are figuring out the user for.
     * @param userId The userId whose status we are finding.
     * @param conn A Connection to use
     */
    public int getUserTypeForComponent(int componentId, int userId) {
        Connection conn = null;
        try {
            conn = getConnection();
            return getUserTypeForComponent(componentId, userId, conn);
        } catch (Exception e) {
            s_trace.error("Error getting user type.", e);
        } finally {
            closeConnection(conn, null, null);
        }
        return -1;
    }
    private int getUserTypeForComponent(int componentId, int userId,
            Connection conn) throws Exception {
        int userType = -1;
        StringBuilder sqlStr = new StringBuilder(256);
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            sqlStr.append("SELECT user_id FROM group_user WHERE group_id = ? ");
            sqlStr.append("AND user_id=?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, ApplicationConstants.ADMIN_GROUP);
            ps.setInt(2, userId);
            rs = ps.executeQuery();


            if (rs.next())  //user is admin
            {
                userType = ApplicationConstants.PROBLEM_ADMIN;
            } else {
                sqlStr.replace(0, sqlStr.length(),
                        "SELECT user_type_id ");
                sqlStr.append("FROM component_user_xref ");
                sqlStr.append("WHERE user_id = ? ");
                sqlStr.append("AND component_id = ? ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, userId);
                ps.setInt(2, componentId);
                rs = ps.executeQuery();

                //make sure there is a row
                if (!rs.next()) {
                    userType = -1;
                } else {
                    userType = ApplicationConstants.PROBLEM_TESTER;
                    do {
                            if (rs.getInt(1) == ApplicationConstants.PROBLEM_WRITER) {
                            userType = ApplicationConstants.PROBLEM_WRITER;
                        }
                    } while (rs.next());
                }
            }
        } catch (Exception e) {
            s_trace.error("Error in getUserTypeForComponent", e);
        } finally {
            closeConnection(null, ps, rs);
        }
        return userType;
    }

    /**
     * Determines the user type of a user for a problem.
     *
     * @param problemId The problem id we are figuring out the user for.
     * @param userId The userId whose status we are finding.
     * @param conn A connection to use.
     */
    public int getUserTypeForProblem(int problemId, int userId) {
        Connection conn = null;
        try {
            conn = getConnection();
            return getUserTypeForComponent(problemId, userId, conn);
        } catch (Exception e) {
            s_trace.error("Error getting user type.", e);
        } finally {
            closeConnection(conn, null, null);
        }
        return -1;
    }
    private int getUserTypeForProblem(int problemId, int userId, Connection conn)
            throws Exception {
        int userType = -1;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(256);

        try {
            sqlStr.append("SELECT user_id FROM group_user WHERE group_id = ? ");
            sqlStr.append("AND user_id=?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, ApplicationConstants.ADMIN_GROUP);
            ps.setInt(2, userId);
            rs = ps.executeQuery();

            if (rs.next())  //user is admin
            {
                userType = ApplicationConstants.PROBLEM_ADMIN;
            } else {
                sqlStr.replace(0, sqlStr.length(),
                        "SELECT user_type_id ");
                sqlStr.append("FROM component_user_xref ");
                sqlStr.append("WHERE user_id = ? ");
                sqlStr.append("AND component_id IN (SELECT component_id ");
                sqlStr.append("FROM component ");
                sqlStr.append("WHERE problem_id = ?) ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, userId);
                ps.setInt(2, problemId);
                rs = ps.executeQuery();

            //make sure there is a row
                if (!rs.next()) {
                    userType = -1;
                } else {
                    //if they are writer for at least 1 component, they are writer
                    //for the problem, otherwise they are the tester
                    userType = ApplicationConstants.PROBLEM_TESTER;
                    do {
                        if (rs.getInt(1) == ApplicationConstants.PROBLEM_WRITER) {
                            userType = ApplicationConstants.PROBLEM_WRITER;
                        }
                    } while (rs.next());
                }
            }
        } catch (Exception e) {
            s_trace.error("Error in getUserTypeForProblem", e);
        } finally {
            closeConnection(null, ps, rs);
        }
        return userType;
    }

    /**
     * Looks up the solution id for a user's solution to a problem.
     * If the user does not have a solution, one is inserted.
     */
    public int getSolutionId(int componentId, int userId) {
        Connection conn = null;
        try {
            conn = getConnection();
            return getSolutionId(componentId, userId, conn);
        } catch (Exception e) {
            s_trace.error("Error getting solution id.", e);
        } finally {
            //closeConnection(conn, null);
            closeConnection(conn, null, null);
        }
        return -1;
    }

    /**
     * Looks up the solution id for a user's solution to a problem.
     * If the user does not have a solution, one is inserted.
     */
    private int getSolutionId(int componentId, int userId, Connection conn)
            throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int solutionId = -1;

        try {
            int userType = getUserTypeForComponent(componentId, userId, conn);
            StringBuilder sqlStr = new StringBuilder(256);

            //see if this coder already has a solution
            sqlStr.replace(0, sqlStr.length(), "");
            sqlStr.append("SELECT cs.solution_id ");
            sqlStr.append("FROM component_solution_xref cs ");
            sqlStr.append(",solution s ");
            sqlStr.append("WHERE cs.component_id = ? ");
            sqlStr.append("AND cs.solution_id = s.solution_id ");
            if (userType == ApplicationConstants.PROBLEM_ADMIN) {
                sqlStr.append("AND cs.primary_solution = ? ");
            } else {
                sqlStr.append("AND s.coder_id = ? ");
            }
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            if (userType == ApplicationConstants.PROBLEM_ADMIN) {
                ps.setInt(2, ApplicationConstants.PRIMARY_SOLUTION);
            } else {
                ps.setInt(2, userId);
            }
            rs = ps.executeQuery();

            if (rs.next()) {
                solutionId = rs.getInt(1);
            } else //insert it
            {
                solutionId = IdGeneratorClient.getSeqIdAsInt(DBMS.JMA_SEQ);
                sqlStr = new StringBuilder(256);
                sqlStr.append("INSERT INTO solution ");
                sqlStr.append("(solution_id ");
                sqlStr.append(",coder_id ");
                sqlStr.append(",solution_text ");
                sqlStr.append(",language_id");
                sqlStr.append(",modify_date) ");
                sqlStr.append("VALUES (?, ?, ?, ?, current)");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, solutionId);
                ps.setInt(2, userId);
                ps.setBytes(3, DBMS.serializeTextString(""));
                ps.setInt(4, JavaLanguage.ID);
                ps.executeUpdate();

                sqlStr.replace(0, sqlStr.length(), "");
                sqlStr.append("INSERT INTO component_solution_xref ");
                sqlStr.append("(component_id, ");
                sqlStr.append("solution_id ");
                sqlStr.append(",primary_solution) ");
                sqlStr.append("VALUES (?, ?, ?)");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, componentId);
                ps.setInt(2, solutionId);
                ps.setInt(3, (userType != ApplicationConstants.PROBLEM_TESTER)
                        ? ApplicationConstants.PRIMARY_SOLUTION :
                        ApplicationConstants.SECONDARY_SOLUTION);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            s_trace.error("Error in getSolutionId", e);
        } finally {
            //closeConnection(conn, null);
            closeConnection(null, ps , rs);
        }

        return solutionId;
    }
    
    /**
     * <p>
     * get the simple component.
     * </p>
     * @param componentId
     *         the component id.
     * @return the SimpleComponent entity.
     * @throws ProblemServicesException
     *          if any error occur when get simple component.
     */
    public SimpleComponent getSimpleComponent(int componentId) throws ProblemServicesException {
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("Getting simple component #" + componentId);
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(200);
        java.sql.Connection conn = null;

        try {
            conn = getConnection();
            sqlStr.append("SELECT c.problem_id,c.component_id, c.class_name, c.method_name, " +
                            "c.result_type_id, c.component_type_id, c.component_text as xml ");
            sqlStr.append("FROM component c WHERE c.component_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            rs = ps.executeQuery();

            if (rs.next()) {
                int idx = 1;
                SimpleComponent component = new SimpleComponent();
                component.setProblemID(rs.getInt(idx++));
                component.setComponentID(rs.getInt(idx++));
                component.setClassName(rs.getString(idx++));
                component.setMethodName(rs.getString(idx++));
                component.setReturnType(getDataType(rs.getInt(idx++)));
                component.setComponentTypeID(rs.getInt(idx++));
                ProblemComponent pc = parseProblemStatement(DBMS.getTextString(rs, "xml"), false, componentId);
                idx++;
                component.setRoundType(pc.getRoundType());
                ProblemCustomSettings custom = component.getProblemCustomSettings();
                ProblemCustomSettings pcs = pc.getProblemCustomSettings();
                custom.setGccBuildCommand(pcs.getGccBuildCommand());
                custom.setCppApprovedPath(pcs.getCppApprovedPath());
                custom.setPythonCommand(pcs.getPythonCommand());
                custom.setPythonApprovedPath(pcs.getPythonApprovedPath());
                //set the execution time limit to algo problem and long problem.
                if(pc.getComponentTypeID() == ProblemConstants.MAIN_COMPONENT
                        && pcs.getExecutionTimeLimit() == ProblemComponent.DEFAULT_EXECUTION_TIME_LIMIT) {
                    custom.setExecutionTimeLimit(ProblemComponent.DEFAULT_SRM_EXECUTION_TIME_LIMIT);
                } else {
                    custom.setExecutionTimeLimit(pcs.getExecutionTimeLimit());
                }
                custom.setMemLimit(pcs.getMemLimit());
                custom.setStackLimit(pcs.getStackLimit());
                rs.close();
                ps.close();
                ps = conn.prepareStatement("SELECT p.data_type_id , p.sort_order FROM " +
                        " parameter p " +
                        " WHERE p.component_id = ? ORDER BY p.sort_order");
                ps.setInt(1, componentId);
                rs = ps.executeQuery();
                ArrayList al = new ArrayList();
                while (rs.next()) {
                    al.add(getDataType(rs.getInt(1)));
                }
                DataType[] dt = new DataType[al.size()];
                for (int i = 0; i < dt.length; i++) {
                    dt[i] = (DataType) al.get(i);
                }
                component.setParamTypes(dt);

                if (s_trace.isDebugEnabled()) {
                    s_trace.debug("Loaded component " + component);
                }

                loadWebServiceDependencies(conn, component);

                return component;
            } else {
                s_trace.error("no component found for id: " + componentId);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ProblemServicesException(e.getMessage());
        } finally {
            closeConnection(conn, ps, rs);
        }
    }


    private void loadWebServiceDependencies(Connection conn, SimpleComponent component) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT ws.web_service_id FROM web_service ws INNER JOIN problem_web_service_xref pws ON ws.web_service_id = pws.web_service_id WHERE pws.problem_id = ?");
            ps.setInt(1, component.getProblemID());
            rs = ps.executeQuery();
            List dependencies = new LinkedList();
            while (rs.next()) {
                dependencies.add(new Long(rs.getLong(1)));
            }
            if (dependencies.size() > 0) {
                component.setWebServiceDependencies((Long[]) dependencies.toArray(new Long[dependencies.size()]));
            }
        } finally {
            closeConnection(null, ps, rs);
        }
    }


//    private void loadComponentDependencies(Connection conn, SimpleComponent component) throws SQLException {
//        // this assumes only main component may have dependencies
//        if (component.getComponentTypeID() != ContestConstants.COMPONENT_TYPE_MAIN) {
//            return;
//        }
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//        try {
//            ps = conn.prepareStatement("SELECT component_id FROM problem_component_xref WHERE problem_id = ? AND component_id <> ?");
//            ps.setInt(1,component.getProblemID());
//            ps.setInt(2,component.getComponentID());
//            rs = ps.executeQuery();
//            List dependencies = new LinkedList();
//            while (rs.next()) {
//                dependencies.add(new Long(rs.getLong(1)));
//            }
//            if (dependencies.size() > 0) {
//                component.setComponentDependencies((Long[]) dependencies.toArray(new Long[dependencies.size()]));
//            }
//        }
//        finally {
//            closeConnection(null,ps,rs);
//        }
//    }

    /**
     * Fills in the expected result for a list of test case elements, with either
     * the expected result or an error if an error occurred generating the
     * output.
     */
    private com.topcoder.shared.problem.TestCase[] fillTestCasesOutput(
            com.topcoder.shared.problem.TestCase[] testCases, int componentId) {
        s_trace.debug("In fillTestCasesOutput for component with id=" + componentId);

        java.sql.Connection conn = null;;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder();

        try {
            conn = getConnection();
            int solutionId = getPrimarySolutionId(componentId, conn);
            if (solutionId != -1) {
                sqlStr = new StringBuilder(256);
                sqlStr.append("SELECT method_name ");
                sqlStr.append(",result_type_id ");
                sqlStr.append("FROM component ");
                sqlStr.append("WHERE component_id = ? ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, componentId);
                rs = ps.executeQuery();
                rs.next();
                String methodName = rs.getString(1);
                DataType returnType = getDataType(rs.getInt(2));

                sqlStr = new StringBuilder(256);
                sqlStr.append("SELECT p.data_type_id ");
                sqlStr.append(",p.sort_order ");
                sqlStr.append("FROM parameter p ");
                sqlStr.append("WHERE  p.component_id = ? ");
                sqlStr.append("ORDER BY p.sort_order ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, componentId);
                rs = ps.executeQuery();
                ArrayList al = new ArrayList(10);
                while (rs.next()) {
                    al.add(getDataType(rs.getInt(1)));
                }
                DataType[] paramTypes = new DataType[al.size()];
                for (int i = 0; i < al.size(); i++) {
                    paramTypes[i] = (DataType) al.get(i);
                }

                ArrayList testCasesToTest = new ArrayList();
                ArrayList argsToTest = new ArrayList();
                Object[] args;
                //String[] argStrings;
                //boolean argsParsed;
                for (int i = 0; i < testCases.length; i++) {
                    try {
                        args = DataValue.parseValuesToObjects(testCases[i].getInput(),
                                paramTypes);
                        testCasesToTest.add(testCases[i]);
                        argsToTest.add(args);
                    } catch (Exception e) {
                        //argsParsed = false;
                        testCases[i].setOutput(
                                com.topcoder.shared.problem.TestCase.ERROR);
                    }
                }

                if (argsToTest.size() > 0) {
                    Object[][] allArgValues = new Object[argsToTest.size()][];
                    for (int i = 0; i < argsToTest.size(); i++) {
                        allArgValues[i] = (Object[]) argsToTest.get(i);
                    }

                    s_trace.debug("About to call test with " + allArgValues.length +
                            " cases.");
                    MPSQASFiles tresults[] = getMPSQASServices().test(allArgValues,
                            componentId, solutionId, methodName);
                    s_trace.debug("Got " + tresults.length + " MPSQASFiles back.");

                    for (int i = 0; i < testCasesToTest.size(); i++) {
                        if (tresults[i].getTestStatus()) {
                            ((com.topcoder.shared.problem.TestCase) testCasesToTest.get(i))
                                    .setOutput(DataValue.convertObjectToDataValue(
                                            tresults[i].getResult(),
                                            returnType).encode());
                        } else {
                            ((com.topcoder.shared.problem.TestCase) testCasesToTest.get(i))
                                    .setOutput(com.topcoder.shared.problem.TestCase.ERROR);
                        }
                    }
                }
            }
        } catch (Exception e) {
            s_trace.error("Error getting output of test cases for statement.", e);
        } finally {
            closeConnection(conn, ps, rs);
        }
        return testCases;
    }


    /**
     * Add the given args/results as test cases for the component. Args are verified to avoid test case duplication.<p>
     *
     * This method is intended to be called with args and results of successful challenges.<p>
     *
     *
     * @param componentId The id of the component
     * @param args An Object[] containing args (Object[]) for each test case
     * @param results An Object[] containing test case results. results[i] must be the result for args[i]
     * @return An int array containing ids of the newly created test cases.
     *
     * @throws ProblemServicesException If test cases could not be added.
     */
    public int[] addTestCasesToComponent(int componentId, Object[][] args, Object[] results) throws ProblemServicesException {
        s_trace.info("addTestCasesToComponent("+componentId+",...) testCasesCount="+args.length);
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        ReadWriteLock componentLock = getLockForComponent(componentId);
        //We get a write lock while changing component_text, and downgrade it to a read lock after text is updated.
        //This works only for this method. Since we know we are always adding test cases and verifying existence against
        //component text. Other methods should use a write lock
        WriteLock writeLock = componentLock.writeLock();
        try {
            writeLock.lock();
            try {
                conn = DBMS.getConnection();
                ProblemComponent component = parseProblemStatement(componentDao.getComponentText(componentId, conn), true, componentId);
                DataType returnDataType = getDataType(component.getReturnType().getDescription());
                DataType[] paramTypes = component.getParamTypes();

                // Populate the test case IDs from DB
                populateTestCaseIds(conn, component, true);

                //Create a set with args, to quickly check duplicate ones
                TestCase[] testCases = component.getTestCases();
                Set argsValue = new HashSet();
                for (int i = 0; i < testCases.length; i++) {
                    argsValue.add(Arrays.asList(testCases[i].getInput()));
                }

                //For each new test case, check if it already exists. If it does not add it to testToAdd
                List testToAdd = new LinkedList();
                for (int i = 0; i < args.length; i++) {
                    DataValue[] values = DataValue.convertObjectsToDataValues(args[i], paramTypes);
                    ArrayList inputArgs = new ArrayList(values.length);
                    for (int j = 0; j < values.length; j++) {
                        DataValue value = values[j];
                        inputArgs.add(value.encode());
                    }
                    if (!argsValue.contains(inputArgs)) {
                        argsValue.add(inputArgs);
                        testToAdd.add(new Object[] {new Integer(i), inputArgs});
                    }
                }

                if (testToAdd.size() == 0) {
                    if (s_trace.isDebugEnabled()) {
                        s_trace.debug("All test cases already existed");
                    }
                    return new int[0];
                }

                TestCase[] allTestCases = new TestCase[testCases.length+testToAdd.size()];
                System.arraycopy(testCases, 0, allTestCases, 0, testCases.length);
                Object[][] newArgs = new Object[testToAdd.size()][];
                Object[] newResults = new Object[testToAdd.size()];
                TestCase[] newTestCases = new TestCase[testToAdd.size()];
                int index = 0;
                for (Iterator it = testToAdd.iterator(); it.hasNext(); ) {
                    Object[] pair = (Object[]) it.next();
                    int i = ((Integer) pair[0]).intValue();
                    List inputArgs = (List) pair[1];
                    if (s_trace.isInfoEnabled()) {
                        s_trace.info("Converting challenge into test case: args="+ArrayUtils.asString(args[i])+" result="+results[i]);
                    }

                    TestCase testCase = new TestCase(null,
                                                (String[]) inputArgs.toArray(new String[inputArgs.size()]),
                                                DataValue.convertObjectToDataValue(results[i], returnDataType).encode(),
                                                (Element) null,
                                                false);
                    newTestCases[index] = testCase;
                    newArgs[index] = args[i];
                    newResults[index] = results[i];
                    allTestCases[index+testCases.length] = testCase;
                    index++;
                }

                ReadLock readLock = componentLock.readLock();
                readLock.lock();
                int[] testCaseIds;
                try {
                    testCaseIds = insertNewTestCases(newTestCases, newArgs, newResults, componentId, conn);
                } finally {
                    readLock.unlock();
                }

                //Update component XML data
                component.setTestCases(allTestCases);
                componentDao.setComponentText(componentId, component.toXML(), conn);
                writeLock.unlock();

                return testCaseIds;
            } catch (Exception e) {
                DBMS.printException(e);
                throw new ProblemServicesException("Exception when trying to add test cases to component: "+e.getMessage());
            } finally {
                if (writeLock.isLocked()) writeLock.unlock();
                DBMS.close(conn, ps, rs);
            }
        } catch (InterruptedException e) {
            s_trace.error(e,e);
            throw new ProblemServicesException("Exception when trying to add test cases to component: "+e.getMessage());
        }
    }

    /**
     * Gets a ReadLock for the given component
     *
     * @param componentId The component id;
     * @return The ReadWrite lock for the component;
     */
    private ReadWriteLock getLockForComponent(int componentId) {
        synchronized (componentsLocksMutex) {
            ReadWriteLock lock = (ReadWriteLock) componentsLocks.get(new Integer(componentId));
            if (lock == null) {
                lock = new ReadWriteLock();
                componentsLocks.put(new Integer(componentId), lock);
            }
            return lock;
        }
    }

    private int[] insertNewTestCases(TestCase[] cases, Object[][] args, Object[] results, int componentId, Connection conn) throws SQLException, IDGenerationException {
        int[] ids = new int[cases.length];
        PreparedStatement ps = null;
        try {
            String sqlInsert = "INSERT INTO system_test_case " +
                            	"(test_case_id " +
                            	",component_id " +
                            	",args " +
                            	",expected_result " +
                            	",modify_date " +
                            	",status " +
                            	",example_flag " +
                                ",system_flag " +
                                ",test_number) " +
                                "VALUES (?, ?, ?, ?, current, ?, ?, ?, (select COUNT(*) from system_test_case where component_id = ?))";
            ps = conn.prepareStatement(sqlInsert);

            for (int i = 0; i < cases.length; i++) {
                TestCase testCase = cases[i];
                int id = IdGeneratorClient.getSeqIdAsInt(DBMS.JMA_SEQ);
                cases[i].setId(new Integer(id));
                ids[i] = id;
                ps.setInt(1, id);
                ps.setInt(2, componentId);
                ps.setBytes(3, DBMS.serializeBlobObject(new ArrayList(Arrays.asList(args[i]))));
                if (results[i] == null) {
                    ps.setNull(4, Types.BLOB);
                    ps.setInt(5, StatusConstants.INCOMPLETE);
                } else {
                    ps.setBytes(4, DBMS.serializeBlobObject(results[i]));
                    ps.setInt(5, StatusConstants.COMPLETE);
                }
                ps.setInt(6, testCase.isExample() ?  ApplicationConstants.EXAMPLE : ApplicationConstants.NOT_EXAMPLE);
                ps.setBoolean(7, testCase.isSystemTest());
                ps.setInt(8, componentId);
                ps.executeUpdate();
            }
            return ids;
        } finally {
            DBMS.close(ps);
        }
    }

    /**
     * Inserts test cases into the system_test_case table.  First replaces
     * existing test cases in the table for the specified component with the
     * passed test cases.  Then, if there are more cases inserts, or if there
     * are less, deletes the remaining cases for the component.
     *
     * @param cases The TestCase object to insert
     * @param args  An Array of Object[] that are the arguments for the test c
     *              cases in Object form.
     * @param results An Array of Objects that are the expected results for the
     *                test cases.
     * @param componentId The componentId these test cases go with.
     * @param conn An initialized Connection object to use.
     */
    private void insertTestCases(com.topcoder.shared.problem.TestCase[] cases,
            Object[] args, Object[] results, int componentId, Connection conn)
            throws Exception {
        PreparedStatement ps = null;
        PreparedStatement psUpdate = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(256);
        try {
            // Get all case IDs
            Set idSet = new HashSet();
            sqlStr.append("SELECT test_case_id FROM system_test_case ");
            sqlStr.append("WHERE component_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            rs = ps.executeQuery();
            while (rs.next()) {
                idSet.add(new Integer(rs.getInt(1)));
            }
            rs.close();
            rs = null;
            ps.close();
            ps = null;
            
            //insert or update all cases
            sqlStr = new StringBuilder(256);
            sqlStr.append("INSERT INTO system_test_case ");
            sqlStr.append("(test_case_id ");
            sqlStr.append(",component_id ");
            sqlStr.append(",args ");
            sqlStr.append(",expected_result ");
            sqlStr.append(",modify_date ");
            sqlStr.append(",status ");
            sqlStr.append(",example_flag ");
            sqlStr.append(",system_flag ");
            sqlStr.append(",test_number) ");
            sqlStr.append("VALUES (?, ?, ?, ?, current, ?, ?, ?, ?) ");
            ps = conn.prepareStatement(sqlStr.toString());

            sqlStr.setLength(0);
            sqlStr.append("UPDATE system_test_case ");
            sqlStr.append("SET args = ? ");
            sqlStr.append(",expected_result = ? ");
            sqlStr.append(",modify_date = current ");
            sqlStr.append(",status = ? ");
            sqlStr.append(",example_flag = ? ");
            sqlStr.append(",system_flag = ? ");
            sqlStr.append(",test_number = ? ");
            sqlStr.append("WHERE test_case_id = ? ");
            psUpdate = conn.prepareStatement(sqlStr.toString());

            for (int caseIndex = 0; caseIndex < cases.length; ++caseIndex) {
                // If we already have an ID assigned, use it. Otherwise, generate a new one
                if (cases[caseIndex].getId() == null) {
                    cases[caseIndex].setId(new Integer(IdGeneratorClient.getSeqIdAsInt(DBMS.JMA_SEQ)));
                }
                PreparedStatement pp;
                if (idSet.contains(cases[caseIndex].getId())) {
                    // If contains, use update to update the record
                    idSet.remove(cases[caseIndex].getId());
                    psUpdate.setBytes(1, DBMS.serializeBlobObject(new ArrayList(Arrays.asList((Object[]) args[caseIndex]))));
                    if (results[caseIndex] == null) {
                        psUpdate.setNull(2, Types.BLOB);
                        psUpdate.setInt(3, StatusConstants.INCOMPLETE);
                    } else {
                        psUpdate.setBytes(2, DBMS.serializeBlobObject(results[caseIndex]));
                        psUpdate.setInt(3, StatusConstants.COMPLETE);
                    }
                    psUpdate.setInt(4, cases[caseIndex].isExample() ?
                              ApplicationConstants.EXAMPLE :
                              ApplicationConstants.NOT_EXAMPLE);
                    psUpdate.setBoolean(5, cases[caseIndex].isSystemTest());
                    psUpdate.setInt(6, caseIndex);
                    psUpdate.setInt(7, cases[caseIndex].getId().intValue());
                    pp = psUpdate;
                } else {
                    // Otherwise, insert it
                    ps.setInt(1, cases[caseIndex].getId().intValue());
                    ps.setInt(2, componentId);
                    ps.setBytes(3, DBMS.serializeBlobObject(
                            new ArrayList(Arrays.asList((Object[]) args[caseIndex]))));
                    if (results[caseIndex] == null) {
                        ps.setNull(4, Types.BLOB);
                        ps.setInt(5, StatusConstants.INCOMPLETE);
                    } else {
                        ps.setBytes(4, DBMS.serializeBlobObject(results[caseIndex]));
                        ps.setInt(5, StatusConstants.COMPLETE);
                    }
                    ps.setInt(6, cases[caseIndex].isExample() ?
                        ApplicationConstants.EXAMPLE :
                        ApplicationConstants.NOT_EXAMPLE);
                    ps.setBoolean(7, cases[caseIndex].isSystemTest());
                    ps.setInt(8, caseIndex);
                    pp = ps;
                }
                if (pp.executeUpdate() != 1) {
                    //throw an exception, causing a roll back
                    throw new Exception("ps.executeUpdate() != 1 when saving test case.");
                }
            }
            psUpdate.close();
            psUpdate = null;
            ps.close();
            ps = null;

            // Delete extra test cases
            sqlStr.setLength(0);
            sqlStr.append("DELETE FROM system_test_case WHERE test_case_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());

            for (Iterator iter = idSet.iterator(); iter.hasNext(); ) {
                Integer id = (Integer) iter.next();
                ps.setInt(1, id.intValue());
                if (ps.executeUpdate() != 1) {
                    throw new Exception("ps.executeUpdate() != 1 when deleting extra test case.");
                }
            }
        } catch (Exception e) {
            s_trace.error("Error in insertTestCases.", e);
        } finally {
            closeConnection(null, psUpdate, null);
            closeConnection(null, ps, rs);
        }
    }

    /**
     * Returns the problem id structure of the specified problem.
     */
    private ProblemIdStructure getProblemIdStructure(int problemId,
            Connection conn) throws Exception {
        StringBuilder sqlStr;
        PreparedStatement ps = null;;
        ResultSet rs = null;
        ProblemIdStructure problemIdStructure = null;

        try {
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT problem_id AS problem_id ");
            sqlStr.append(",name AS problem_name ");
            sqlStr.append("FROM problem ");
            sqlStr.append("WHERE problem_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, problemId);
            rs = ps.executeQuery();

            rs.next();
            String problemName = rs.getString("problem_name");

            ArrayList components = new ArrayList();

            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT component_id AS component_id ");
            sqlStr.append("FROM component ");
            sqlStr.append("WHERE problem_id = ? ");
            sqlStr.append("AND status_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, problemId);
            ps.setInt(2, StatusConstants.ACTIVE);
            rs = ps.executeQuery();

            while (rs.next()) {
                components.add(getComponentIdStructure(rs.getInt("component_id"), conn));
            }

            problemIdStructure = new ProblemIdStructure(problemId, problemName);
            problemIdStructure.setComponents(components);

            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT w.web_service_id AS web_service_id ");
            sqlStr.append(",w.web_service_name AS web_service_name ");
            sqlStr.append("FROM web_service w ");
            sqlStr.append(",problem_web_service_xref pw ");
            sqlStr.append("WHERE w.web_service_id = pw.web_service_id ");
            sqlStr.append("AND w.status_id = ? ");
            sqlStr.append("AND pw.problem_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, StatusConstants.ACTIVE);
            ps.setInt(2, problemId);
            rs = ps.executeQuery();

        //ArrayList webServices;
            while (rs.next()) {
                problemIdStructure.addWebService(new WebServiceIdStructure(
                        rs.getInt("web_service_id"), rs.getString("web_service_name")));
            }

        } catch (Exception e) {
            s_trace.error("Error in getProblemIdStructure.", e);
        } finally {
            closeConnection(null, ps, rs);
        }

        return problemIdStructure;
    }

    /**
     * Returns the component id structure of the specified component.
     */
    private ComponentIdStructure getComponentIdStructure(int componentId, Connection conn) throws Exception {
        try {
            String className = componentDao.getClassNameForComponent(componentId, conn);
            StringBuffer statement = new StringBuffer();
            statement.append("SELECT test_case_id FROM system_test_case ");
            statement.append("WHERE component_id = ? ");
            statement.append("ORDER BY test_number, test_case_id ");
            PreparedStatement ps = null;
            ResultSet rs = null;
            List list = new ArrayList();

            try {
                ps = conn.prepareStatement(statement.toString());
                ps.setInt(1, componentId);
                rs = ps.executeQuery();
                while (rs.next()) {
                    list.add(new Integer(rs.getInt(1)));
                }
            } finally {
                closeConnection(null, ps, rs);
            }

            return new ComponentIdStructure(componentId, className, (Integer[]) list.toArray(new Integer[list.size()]));
        } catch (Exception e) {
            s_trace.error("Error in getComponentIdStructure.", e);
        }
       return null;
    }

    /**
     * Commits a connection.
     */
    /*
    private void commit(Connection conn) {
        try {
            if (conn != null) {
                conn.commit();
            } else {
                s_trace.error("Hmm, commit called with null connection.");
            }
        } catch (Exception e) {
            s_trace.error("Error commiting connection.", e);
        }
    }
    */

    /**
     * Rolls back a connection.
     */
    /*
    private void rollback(Connection conn) {
        try {
            if (conn != null) {
                conn.rollback();
            } else {
                s_trace.error("Hmm, rollback called with null connection.");
            }
        } catch (Exception e) {
            s_trace.error("Error rollback connection.", e);
        }
    }
    */
    private static void info(Object message) {
        debug(message);
    }

    private static void debug(Object message) {
        s_trace.debug(message);
    }
    /**
     * Returns a reference to a MPSQASServicesBean.
     */
    private MPSQASServices getMPSQASServices() throws NamingException,
            CreateException, RemoteException {
        if (mpsqasServices == null) {
            mpsqasServices = MPSQASServicesLocator.getService();
        }
        return mpsqasServices;
    }

    public class MPSQASTesterThread extends Thread
    {
        private int num = 0;
        private Object args = null;
        private boolean done = false;
        private MPSQASFiles tresults = null;
        private int primarySolutionId = 0;
        private int componentId = 0;
        private Connection conn = null;
        private String retval = "";
        private String methodName = "";

        MPSQASTesterThread(int num, Object args, int solutionId, int componentId, Connection conn, String methodName)
        {
            done = false;
            this.num = num;
            this.args = args;
            this.primarySolutionId = solutionId;
            this.componentId = componentId;
            this.conn = conn;
            this.methodName = methodName;
            this.tresults = null;
        }

        public boolean isDone()
        {
            return done;
        }

        public String getRetVal()
        {
            return retval;
        }

        public int getNum()
        {
            return num;
        }

        public MPSQASFiles getResults()
        {
            return tresults;
        }

        public void run()
        {
            try {
                tresults = getMPSQASServices().test((Object[]) args, componentId,
                                primarySolutionId, "checkData");
            } catch (Exception e) {
                e.printStackTrace();
                retval = "Test case " + num + " doesn't pass checkData.";
                done = true;
                return;
            }

            if (!tresults.getTestStatus() || !"".equals(tresults.getResult())) {
                retval = "Test case " + num + " doesn't pass checkData.";
                done = true;
                return;
            }

            try {
                tresults = getMPSQASServices().test((Object[]) args, componentId,
                        primarySolutionId, methodName);
            } catch (Exception e) {
                e.printStackTrace();
                retval = "Cannot get expected result for test case " + num + ".";
                done = true;
                return;
            }

            if (!tresults.getTestStatus()) {
                retval = "Cannot get expected result for test case " + num + ".";
                done = true;
                return ;
            }

            done = true;
            return;
        }
    }

    /**
     * Get the id of the primary solution for a component.
     * @see ProblemServices#getPrimarySolutionId(int)
     */
    public int getPrimarySolutionId(int componentId) {
        Connection conn = null;
        try {
            conn = getConnection();
            return getPrimarySolutionId(componentId, conn);
        } catch (Exception e) {
            s_trace.error("Error getting primary solution id for componentId=" + componentId, e);
        } finally {
            DBMS.close(conn);
        }
        return -1;
    }


    private int getPrimarySolutionId(int componentId, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            StringBuilder sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT solution_id ");
            sqlStr.append("FROM component_solution_xref ");
            sqlStr.append("WHERE component_id = ? ");
            sqlStr.append("AND primary_solution = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            ps.setInt(2, ApplicationConstants.PRIMARY_SOLUTION);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return -1;
            }
        } finally {
            DBMS.close(null, ps, rs);
        }
    }

    /**
     * @see ProblemServices#getMemLimitForComponent(int)
     */
    public int getMemLimitForComponent(int componentId) {
        ProblemComponent problemComponent = getProblemComponent(componentId, true);
        return problemComponent.getProblemCustomSettings().getMemLimit();
    }

    /**
     * Saves long component configuration.
     *
     * @param component Problem component entity.
     * @param conn DB connection to use.
     * @throws SQLException if SQL error occurs.
     * @since 1.8
     */
    private void saveLongComponentConfiguration(ProblemComponent component, Connection conn)
            throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(256);
        try {
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT COUNT(*) FROM long_component_configuration ");
            sqlStr.append("WHERE component_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, component.getComponentId());
            rs = ps.executeQuery();
            rs.next();
            int recordCount = rs.getInt(1);
            int submissionRate = component.getSubmissionRate();
            int exampleSubmissionRate = component.getExampleSubmissionRate();
            if (recordCount == 0) {
                if (submissionRate > 0|| exampleSubmissionRate > 0) {
                    sqlStr = new StringBuilder(256);
                    sqlStr.append("INSERT INTO long_component_configuration(component_id, ");
                    sqlStr.append("submission_rate, ");
                    sqlStr.append("example_submission_rate) ");
                    sqlStr.append("VALUES(?,?,?)");
                    ps = conn.prepareStatement(sqlStr.toString());
                    ps.setInt(1, component.getComponentId());
                    if (submissionRate <= 0) {
                        ps.setNull(2, java.sql.Types.INTEGER);
                    } else {
                        ps.setInt(2, submissionRate);
                    }
                    if (exampleSubmissionRate <= 0) {
                        ps.setNull(3, java.sql.Types.INTEGER);
                    } else {
                        ps.setInt(3, exampleSubmissionRate);
                    }
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "Error occur while inserting data into long_component_configuration");
                    }
                }
            } else {
                if (submissionRate <= 0 && submissionRate <= 0) {
                    sqlStr = new StringBuilder(256);
                    sqlStr.append("DELETE FROM long_component_configuration ");
                    sqlStr.append("WHERE component_id = ?");
                    ps = conn.prepareStatement(sqlStr.toString());
                    ps.setInt(1, component.getComponentId());
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Error occur while deleting data into long_component_configuration");
                    }
                } else {
                    sqlStr = new StringBuilder(256);
                    sqlStr.append("UPDATE long_component_configuration ");
                    sqlStr.append("SET submission_rate = ?, ");
                    sqlStr.append("example_submission_rate = ? ");
                    sqlStr.append("WHERE component_id = ?");
                    ps = conn.prepareStatement(sqlStr.toString());
                    if (submissionRate <= 0) {
                        ps.setNull(1, java.sql.Types.INTEGER);
                    } else {
                        ps.setInt(1, submissionRate);
                    }
                    if (exampleSubmissionRate <= 0) {
                        ps.setNull(2, java.sql.Types.INTEGER);
                    } else {
                        ps.setInt(2, exampleSubmissionRate);
                    }
                    ps.setInt(3, component.getComponentId());
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Error occur while updating data into long_component_configuration");
                    }
                }
            }
        } catch(SQLException e) {
            s_trace.error("Error saving long_component_configuration.", e);
        } finally {
            closeConnection(null, ps, rs);
        }
    }
}
