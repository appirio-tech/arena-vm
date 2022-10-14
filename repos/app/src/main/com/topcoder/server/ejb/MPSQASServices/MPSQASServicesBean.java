/*
 * Copyright (C) 2007-2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.ejb.MPSQASServices;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.mpsqas.ApplicationConstants;
import com.topcoder.netCommon.mpsqas.ApplicationInformation;
import com.topcoder.netCommon.mpsqas.ContestInformation;
import com.topcoder.netCommon.mpsqas.Correspondence;
import com.topcoder.netCommon.mpsqas.CustomBuildSetting;
import com.topcoder.netCommon.mpsqas.LookupValues;
import com.topcoder.netCommon.mpsqas.MessageConstants;
import com.topcoder.netCommon.mpsqas.NamedIdItem;
import com.topcoder.netCommon.mpsqas.PaymentInformation;
import com.topcoder.netCommon.mpsqas.ProblemInformation;
import com.topcoder.netCommon.mpsqas.ProblemRoundType;
import com.topcoder.netCommon.mpsqas.StatusConstants;
import com.topcoder.netCommon.mpsqas.UserInformation;
import com.topcoder.netCommon.mpsqas.WebServiceInformation;
import com.topcoder.security.TCSubject;
import com.topcoder.security.login.AuthenticationException;
import com.topcoder.server.common.RemoteFile;
import com.topcoder.server.common.ServerContestConstants;
import com.topcoder.server.ejb.BaseEJB;
import com.topcoder.server.ejb.MPSQASServices.dao.UserTestGroupAssociationDao;
import com.topcoder.server.ejb.MPSQASServices.event.MPSQASServiceEventNotificator;
import com.topcoder.server.ejb.MPSQASServices.event.MPSQASTestResult;
import com.topcoder.server.ejb.ProblemServices.ProblemServices;
import com.topcoder.server.ejb.ProblemServices.ProblemServicesLocator;
import com.topcoder.server.ejb.TestServices.TestServices;
import com.topcoder.server.ejb.TestServices.TestServicesLocator;
import com.topcoder.server.ejb.TestServices.longtest.model.LongTestCase;
import com.topcoder.server.ejb.TestServices.longtest.model.LongTestCaseResult;
import com.topcoder.server.ejb.TestServices.longtest.model.LongTestGroup;
import com.topcoder.server.ejb.dao.ComponentDao;
import com.topcoder.server.ejb.dao.SolutionDao;
import com.topcoder.server.farm.common.RoundUtils;
import com.topcoder.server.farm.compiler.CompilerInvoker;
import com.topcoder.server.farm.compiler.CompilerTimeoutException;
import com.topcoder.server.farm.tester.TesterInvokerException;
import com.topcoder.server.farm.tester.mpsqas.MPSQASTesterInvoker;
import com.topcoder.server.mpsqas.broadcast.BroadcastPublisher;
import com.topcoder.server.mpsqas.broadcast.NewCorrespondenceBroadcast;
import com.topcoder.server.mpsqas.javadoc.JavaDocException;
import com.topcoder.server.mpsqas.javadoc.JavaDocGenerator;
import com.topcoder.server.services.CoreServices;
import com.topcoder.server.services.authenticate.CommonOLTPAuthenticatorClient;
import com.topcoder.server.webservice.WebServiceRemoteFile;
import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.services.compiler.util.LongContestCodeGeneratorHelper;
import com.topcoder.services.tester.common.LongTestRequest;
import com.topcoder.services.tester.invoke.FarmSolutionInvokator;
import com.topcoder.services.util.Formatter;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.DataTypeFactory;
import com.topcoder.shared.problem.InvalidTypeException;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.ProblemConstants;
import com.topcoder.shared.problem.ProblemCustomSettings;
import com.topcoder.shared.problem.SimpleComponent;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.EmailEngine;
import com.topcoder.shared.util.IdGeneratorClient;
import com.topcoder.shared.util.TCResourceBundle;
import com.topcoder.shared.util.TCSEmailMessage;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.web.ejb.pacts.BasePayment;
import com.topcoder.web.ejb.pacts.PactsClientServices;
import com.topcoder.web.ejb.pacts.ProblemTestingPayment;
import com.topcoder.web.ejb.pacts.ProblemWritingPayment;

/**
 * Bean to control all application server work for MPSQAS.
 *
 * <p>
 *  Version 1.1 (TC Competition Engine Code Execution Time Issue) change notes:
 *  <ul>
 *      <li>Add parameter executionTimeLimit to method {@link #startLongTest(int, int, int, int, Connection)}</li>.
 *  </ul> 
 * </p>
 * 
 * <p>
 *  Version 1.2 (TC Competition Engine - Code Compilation Issues) change notes:
 *  <ul>
 *      <li>Update method {@link #compileSolution(HashMap, int, int, int, Connection)} to populate the compile time limit.</li>.
 *  </ul> 
 * </p>
 * 
 * <p>
 *  Version 1.3 (TC Competition Engine - Code Compilation Issues) change notes:
 *  <ul>
 *      <li>Add method {@link #isRestrictedUser(String)} to check whether the specific user handle is in the restricted list.</li>.
 *      <li>Update method {@link #authenticateUser(String,String)} to restrict some configurable users to login the MPSQAS.</li>.
 *  </ul> 
 * </p>
 *
 * <p>
 * Changes in 1.4 (Round Type Option Support For SRM Problem):
 * <ol>
 * <li>Added {@link #getRoundTypeByComponentId()}  method.</li>
 * <li>Update {@link #compareSolutions(int)}  method.</li>
 * <li>Update {@link #runMPSQASTest(int, int, String, Object[], Connection)}  method.</li>
 * <li>Update {@link #test(Object[], int, int, int, Connection)}  method.</li>
 * <li>Update {@link #test(Object[], int, int, String, Connection)}  method.</li>
 * <li>Update {@link #test(Object[][], int, int, String, Connection)}  method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.5 (Fix Tester Choosing Issue for Testing Writer Solution v1.0):
 * <ol>
 * <li>Added {@link #getComponentTypeByComponentId(int)}  method.</li>
 * <li>Update {@link #test(Object[], int, int, int, Connection)}  method.</li>
 * <li>Update {@link #test(Object[], int, int, String, Connection)}  method.</li>
 * <li>Update {@link #test(Object[][], int, int, String, Connection)}  method.</li>
 * <li>Updated {@link #compareSolutions(int componentId)} to add component type in MPSQAS file</li>
 * <li>Update {@link #runMPSQASTest(int, int, String, Object[], Connection)}  method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.6 (TC Competition Engine - CPP Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Update {@link #startLongTest(int, int, int, int, String, Connection)} method.</li>
 *      <li>Update {@link #test(Object[], int, int, int,Connection)} method.</li>
 *      <li>Update {@link #systemTests(int componentId, int userId, int testType)} method.</li>
 *      <li>Update {@link #compileSolution(HashMap, int, int, int,Connection)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.7 (TC Competition Engine - Python Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Update {@link #startLongTest(int, int, int, int, String, String, String, Connection)} method.</li>
 *      <li>Update {@link #test(Object[], int, int, int,Connection)} method.</li>
 *      <li>Update {@link #systemTests(int componentId, int userId, int testType)} method.</li>
 *      <li>Update {@link #compileSolution(HashMap, int, int, int,Connection)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.8 (TC Competition Engine - C++ and Python Customization Support for SRM v1.0):
 * <ol>
 *      <li>Update {@link #compileSolution(HashMap, int, int, int,Connection)} method.</li>
 *      <li>Update {@link #compareSolutions(int)}  method.</li>
 *      <li>Added {@link #getCppApprovedPathByComponentId(int componentId)} method.</li>
 *      <li>Update {@link #runMPSQASTest(int, int, String, Object[], Connection)}  method.</li>
 *      <li>Update {@link #test(Object[], int, int, int, Connection)}  method.</li>
 *      <li>Update {@link #test(Object[], int, int, String, Connection)}  method.</li>
 *      <li>Update {@link #test(Object[][], int, int, String, Connection)}  method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.9 (BUGR-9137 - Python Enable For SRM):
 * <ol>
 *      <li>Update {@link #compareSolutions(int)}  method.</li>
 *      <li>Added {@link #wrapperPythonCommandAndApprovePath(MPSQASFiles, int)} method.</li>
 *      <li>Update {@link #runMPSQASTest(int, int, String, Object[], Connection)}  method.</li>
 *      <li>Update {@link #test(Object[], int, int, int, Connection)}  method.</li>
 *      <li>Update {@link #test(Object[], int, int, String, Connection)}  method.</li>
 *      <li>Update {@link #test(Object[][], int, int, String, Connection)}  method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 2.0 (Release Assembly - Dynamic Round Type List For Long and Individual Problems):
 * <ol>
 * <li>
 * Updated {@link #authenticateUser(String,String)} method to populate lookup values into successful response.
 * </li>
 * <li>
 * Added {@link #getLookupValues(Connection)} and {@link #getProblemRoundTypes(Connection)} methods
 * for retrieving lookup data from persistence.
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 2.1 (Release Assembly - TopCoder Competition Engine Improvement Series 2 v1.0):
 * <ol>
 * <li>
 * Updated {@link #getLookupValues()} method to populate custom build settings into lookup data.
 * </li>
 * <li>
 * Added {@link #getCustomBuildSettings(Connection)} method for retrieving custom build settings
 * from persistence.
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 2.2 (TopCoder Competition Engine - Customize Execution Time and Memory Limits for SRM v1.0):
 * <ol>
 *      <li>Update {@link #compareSolutions(int)}  method.</li>
 *      <li>Update {@link #runMPSQASTest(int, int, String, Object[], Connection)}  method.</li>
 *      <li>Update {@link #test(Object[], int, int, int, Connection)}  method.</li>
 *      <li>Update {@link #test(Object[], int, int, String, Connection)}  method.</li>
 *      <li>Update {@link #test(Object[][], int, int, String, Connection)}  method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 2.3 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #systemTests(int componentId, int userId, int testType)} method.</li>
 *      <li>Update {@link #startLongTest(int userId, int roundType, ProblemCustomSettings, Connection)} method.</li>
 *      <li>Update {@link #compareSolutions(int)}  method.</li>
 *      <li>Update {@link #compileSolution(HashMap, int, int, int,Connection)} method.</li>
 *      <li>Update {@link #runMPSQASTest(int, int, String, Object[], Connection)}  method.</li>
 *      <li>Update {@link #test(Object[], int, int, int, Connection)}  method.</li>
 *      <li>Update {@link #test(Object[], int, int, String, Connection)}  method.</li>
 *      <li>Update {@link #test(Object[][], int, int, String, Connection)}  method.</li>
 *      <li>Remove {getCppApprovedPathByComponentId(int componentId),
 *              wrapperPythonCommandAndApprovePath(MPSQASFiles mpsqasFiles, int componentId)} methods</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 2.4 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *     <li>Updated {@link #compareSolutions(int)} to use customize answer checking approach.</li>
 *     <li>Updated {@link #compileSolution(HashMap, int, int, int, Connection} to persist has check answer flag.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 2.5 (Return Peak Memory Usage for Marathon Match Cpp v1.0):
 * <ol>
 *      <li>Update {@link #buildResponseMessageForTestGroup(LongTestGroup testGroup, Connection cnn)} method.</li>
 * </ol>
 * </p>
 * @author mitalub, savon_cn, gevak, TCSASSEMBLER
 * @version 2.5
 */
public class MPSQASServicesBean extends BaseEJB {

    private static final Logger s_trace = Logger.getLogger(MPSQASServicesBean.class);

    private ProblemServices problemServices = null;
    private TestServices testServices = null;
    private static boolean VERBOSE = true;//false;
    /**
     * Notificator used to notify listeners of this services
     * about asynchronous responses.
     * For example: results of scheduled test.
     */
    private MPSQASServiceEventNotificator notificator;
    /**
     * the restricted user list configuration key.
     */
    private static final String RESTRICT_USERS_CONF_KEY = "mpsqas.restrict.users";
    /**
     * the restricted user list configuration file name.
     */
    private static final String RESTRICT_USERS_CONF = "MPSQASRestricted";  
    /**
     * the restricted user list configuration.
     */
    private static final TCResourceBundle restrictedUsersBundle = new TCResourceBundle(RESTRICT_USERS_CONF);
    /**
     * the default restricted users.
     */
    private static final String[] DEFAULT_RESTRICT_USERS = {"writer","tester","writer2","tester2"};
    /**
     * the restricted users list initialized only once.
     */
    private static final String[] RESTRICTED_USER_LIST = restrictedUsersBundle.getProperty(RESTRICT_USERS_CONF_KEY)==null?
        DEFAULT_RESTRICT_USERS:restrictedUsersBundle.getProperty(RESTRICT_USERS_CONF_KEY).trim().split(",");

    /**
     * Listener for receive incoming asynchronous response from the LongTestServices
     */
    protected static MPSQASLongTestServiceListener longTestEventListener = null;

    /**
     * Data Access Object for manage associations between
     * users and scheduled test groups.
     */
    private UserTestGroupAssociationDao userTestGroupDao = new UserTestGroupAssociationDao();
    private ComponentDao componentDao = new ComponentDao();
    private SolutionDao solutionDao = new SolutionDao();

    /**
     * Tester for non long solutions
     */
    private static MPSQASTesterInvoker mpsqasTester = MPSQASTesterInvoker.create("MPSQAS") ;

    /**
     * Compiler used for all solutions
     */
    private static CompilerInvoker compiler = CompilerInvoker.create("MPSQAS");
    /******************************************************************************
     * User Session Methods                                                       *
     ******************************************************************************/

    /**
     * Checks with the database to make sure the user exits and that
     * the user has the correct password.  Finally, it checks if the user is an
     * admin. The method returns an ArrayList containing a boolean indicating
     * the success of the
     * login.  If false, the ArrayList also contains a String that is the error.
     * If true, the ArrayList contains an Integer (coder id) and 3 Booleans
     * (indicating admin status writer status, and tester status).
     *
     * @param handle   The handle of the user trying to log in.
     * @param password The password they are trying to use.
     * @return either [Boolean.TRUE, Integer(coder_id), Boolean(isAdmin),
     *                 Boolean(isWriter), Boolean(isTester)]
     *         or     [Boolean.FALSE, String(errorMessage)]
     */
    public ArrayList authenticateUser(String handle, String password) {
        debug(handle + " logging into MPSQAS.");
        ArrayList retVal = new ArrayList(6);
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //check whether the handle is among the restricted user list
            if(isRestrictedUser(handle)) {
                retVal.add(new Boolean(false));
                retVal.add("This user "+handle+" is not allowed to login into MPSQAS.");
                return retVal;
            }
            conn = DBMS.getConnection();
            StringBuilder sqlStr = new StringBuilder(256);

            //use the new security tool based auth
            //the permissions should eventually be moved to proper
            //security tool perms as well
            TCSubject tcsubject = CommonOLTPAuthenticatorClient.authenticate(handle, password);
            if (tcsubject == null) {
                retVal.add(new Boolean(false));
                retVal.add("Incorrect handle / password.");
                return retVal;
            }


            int userId = (int)tcsubject.getUserId();
            boolean isAdmin = false;

            sqlStr.replace(0, sqlStr.length(), "SELECT * FROM group_user WHERE user_id=? AND group_id=?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, userId);
            ps.setInt(2, ApplicationConstants.ADMIN_GROUP);
            rs = ps.executeQuery();
            isAdmin = rs.next();

            sqlStr.replace(0, sqlStr.length(), "SELECT * FROM group_user WHERE user_id=? AND group_id=?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, userId);
            ps.setInt(2, ApplicationConstants.PROBLEM_WRITER_GROUP);
            rs = ps.executeQuery();
            boolean isWriter = rs.next() || isAdmin;

            sqlStr.replace(0, sqlStr.length(), "SELECT * FROM group_user WHERE user_id=? AND group_id=?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, userId);
            ps.setInt(2, ApplicationConstants.PROBLEM_TESTER_GROUP);
            rs = ps.executeQuery();
            boolean isTester = rs.next() || isAdmin;

            debug("successful login, userId = " + userId);
            retVal.add(new Boolean(true));
            retVal.add(new Integer(userId));
            retVal.add(new Boolean(isAdmin));
            retVal.add(new Boolean(isWriter));
            retVal.add(new Boolean(isTester));
        } catch (AuthenticationException e) {
            retVal = new ArrayList();
            retVal.add(new Boolean(false));
            retVal.add("Username/password incorrect.");
        } catch (Exception e) {
            s_trace.error("Error authenticating user:", e);
            retVal = new ArrayList();
            retVal.add(new Boolean(false));
            retVal.add(ApplicationConstants.SERVER_ERROR);
        } finally {
            //closeConnection(conn, ps);
            close(conn, ps, rs);
        }
        return retVal;
    }
   /**
     * <p>
     * to check whether the specific user handle is in the restricted list.
     * </p>
     *
     * @param handle the user handle
     * @return true=the user handle is in the restricted list
     *         false=no
     */
    public boolean isRestrictedUser(String handle) {        
        if(RESTRICTED_USER_LIST!=null&&RESTRICTED_USER_LIST.length>0) {
            //check the handle with the help of the restricted user array
            for(int i=0;i<RESTRICTED_USER_LIST.length;i++) {
                if(RESTRICTED_USER_LIST[i].trim().equals(handle)) 
                    return true;
            }
        }
        return false;
    }
    /******************************************************************************
     * Correspondence Services                                                    *
     ******************************************************************************/

    /**
     * Adds the message to the database for the specified problem and user.
     * Returns a boolean indicating success.
     */
    public boolean sendProblemCorrespondence(Correspondence message,
            int problemId, int userId) {
        ArrayList receivers = message.getReceiverUserIds();
        message = sendCorrespondence(message, problemId, userId, "problem_id");

        if (message == null) {
            return false;
        }
        //email about it
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        //StringBuilder sqlStr = null;
        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement("SELECT handle FROM user WHERE user_id = ?");
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            rs.next();
            String senderHandle = rs.getString("handle");
            rs.close();
            ps.close();

            ps = conn.prepareStatement(
                    "SELECT name FROM problem WHERE problem_id = ?");
            ps.setInt(1, problemId);
            rs = ps.executeQuery();
            rs.next();
            String name = rs.getString("name");
            rs.close();
            ps.close();

            ps = conn.prepareStatement("SELECT e.address FROM user u, email e WHERE e.user_id = u.user_id and e.primary_ind = 1 and u.user_id = ?");
            ArrayList emails = new ArrayList();
            for (int i = 0; i < receivers.size(); i++) {
                ps.setInt(1, ((Integer) receivers.get(i))
                        .intValue());
                rs = ps.executeQuery();
                rs.next();
                emails.add(rs.getString("address"));
                rs.close();
            }

            TCSEmailMessage email;
            StringBuilder body = new StringBuilder(256);
            body.append("Hi,\n\n");
            body.append("Correspondence has been added to ");
            body.append(name);
            body.append(", a problem with which you are associated.\n\n");
            body.append(senderHandle);
            body.append(" says:\n");
            body.append(ApplicationConstants.HORIZONTAL_RULE);
            body.append(message.getMessage());
            body.append("\n");
            body.append(ApplicationConstants.HORIZONTAL_RULE);
            body.append("\n\n-mpsqas.\n\n");
            body.append("This is an automated message from mpsqas.");

            for (int i = 0; i < emails.size(); i++) {
                email = new TCSEmailMessage();
                email.setFromAddress(ApplicationConstants.FROM_EMAIL_ADDRESS);
                email.setBody(body.toString());
                email.setSubject("New Correspondence for " + name);
                email.addToAddress((String) emails.get(i), TCSEmailMessage.TO);
                EmailEngine.send(email);
            }
        } catch (Exception e) {
            s_trace.error("Error sending email about correspondence.", e);
        } finally {
            close(conn, ps, rs);
        }


        //broadcast about it
        BroadcastPublisher.broadcast(new NewCorrespondenceBroadcast(
                message, NewCorrespondenceBroadcast.PROBLEM_CORRESPONDENCE,
                problemId));

        return true;
    }

    /**
     * Adds the message to the database for the specified component and
     * user.  Returns a boolean indicating success.
     */
    public boolean sendComponentCorrespondence(Correspondence message,
            int componentId, int userId) {
        ArrayList receivers = message.getReceiverUserIds();
        message = sendCorrespondence(message, componentId, userId, "component_id");

        if(message == null) {
            return false;
        }

        //email about it
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        //StringBuilder sqlStr = null;
        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement("SELECT handle FROM user WHERE user_id = ?");
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            rs.next();
            String senderHandle = rs.getString("handle");
            rs.close();
            ps.close();

            String name = componentDao.getClassNameForComponent(componentId, conn);

            ps = conn.prepareStatement("SELECT e.address FROM user u, email e WHERE e.user_id = u.user_id and e.primary_ind = 1 and u.user_id = ?");
            ArrayList emails = new ArrayList();

            for (int i = 0; i < receivers.size(); i++) {
                ps.setInt(1, ((Integer) receivers.get(i))
                        .intValue());
                rs = ps.executeQuery();
                rs.next();
                emails.add(rs.getString("address"));
                rs.close();
            }

            TCSEmailMessage email;
            StringBuilder body = new StringBuilder(256);
            body.append("Hi,\n\n");
            body.append("Correspondence has been added to ");
            body.append(name);
            body.append(", a component with which you are associated.\n\n");
            body.append(senderHandle);
            body.append(" says:\n");
            body.append(ApplicationConstants.HORIZONTAL_RULE);
            body.append(message.getMessage());
            body.append("\n");
            body.append(ApplicationConstants.HORIZONTAL_RULE);
            body.append("\n\n-mpsqas.\n\n");
            body.append("This is an automated message from mpsqas.");

            for (int i = 0; i < emails.size(); i++) {
                email = new TCSEmailMessage();
                email.setFromAddress(ApplicationConstants.FROM_EMAIL_ADDRESS);
                email.setBody(body.toString());
                email.setSubject("New Correspondence for " + name);
                email.addToAddress((String) emails.get(i), TCSEmailMessage.TO);
                EmailEngine.send(email);
            }
        } catch (Exception e) {
            s_trace.error("Error sending email about correspondence.", e);
        } finally {
            close(conn, ps, rs);
        }

        //broadcast about it
        BroadcastPublisher.broadcast(new NewCorrespondenceBroadcast(
                message, NewCorrespondenceBroadcast.COMPONENT_CORRESPONDENCE,
                componentId));

        return true;
    }

    /**
     * Adds a correspondence message to the database for the specified
     * problem or component. It returns a boolean indicating if it is successful
     * or not.
     * The addition of the correspondence message is broadcasted to the
     * applet servers so all users viewing the problem hear about it.
     */
    private Correspondence sendCorrespondence(Correspondence message, int id,
            int userId, String idType) {
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        Correspondence ret = null;
        ResultSet rs = null;

        try {
            conn = DBMS.getConnection();

            int correspondenceId = IdGeneratorClient.getSeqIdAsInt(DBMS.JMA_SEQ);

            StringBuilder sqlStr = new StringBuilder(256);
            sqlStr.append("INSERT INTO correspondence ");
            sqlStr.append("(correspondence_id ");
            sqlStr.append(",from_coder_id ");
            sqlStr.append(",");
            sqlStr.append(idType);
            sqlStr.append(",message ");
            sqlStr.append(",reply_id ");
            sqlStr.append(",sent_time) ");
            sqlStr.append("VALUES (?, ?, ?, ?, ?, current)");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, correspondenceId);
            ps.setInt(2, userId);
            ps.setInt(3, id);
            ps.setBytes(4, DBMS.serializeTextString(message.getMessage()));
            if (message.getReplyToId() != -1) {
                ps.setInt(5, message.getReplyToId());
            } else {
                ps.setNull(5, Types.DECIMAL);
            }
            ps.executeUpdate();

            //mark the correspondence as read by this user.
            sqlStr = new StringBuilder(256);
            sqlStr.append("INSERT INTO correspondence_read_xref ");
            sqlStr.append("(correspondence_id ");
            sqlStr.append(",user_id ");
            sqlStr.append(",timestamp) ");
            sqlStr.append("VALUES (?, ?, current) ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, correspondenceId);
            ps.setInt(2, userId);
            ps.executeUpdate();

            //populate a correspondence object completely for the broadcast.
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT c.correspondence_id AS id ");
            sqlStr.append(",u.handle AS sender ");
            sqlStr.append(",c.message AS message ");
            sqlStr.append(",c.sent_time AS date ");
            sqlStr.append(",NVL(c.reply_id, -1) AS reply_to_id ");
            sqlStr.append("FROM correspondence c ");
            sqlStr.append(",user u ");
            sqlStr.append("WHERE u.user_id = c.from_coder_id ");
            sqlStr.append("AND c.correspondence_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, correspondenceId);
            rs = ps.executeQuery();
            rs.next();
            ret = new Correspondence(rs.getTimestamp("date").toString(),
                    rs.getString("sender"),
                    DBMS.getTextString(rs, "message"),
                    rs.getInt("id"),
                    rs.getInt("reply_to_id"));
        } catch (Exception e) {
            s_trace.error("Error inserting correspondence:", e);
            return null;
        } finally {
            //closeConnection(conn, ps);
            close(conn, ps, rs);
        }

        return ret;
    }

    /**
     * Returns a list of ProblemIds of problems with correspondence not
     * yet read by the specified user.
     *
     * @param userId The user to get new correspondence fot.
     */
    public ArrayList getUnreadCorrespondence(int userId) {
        ArrayList problems = new ArrayList();
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBMS.getConnection();

            boolean isAdmin = isAdmin(userId);

            //Team problem unread:
            StringBuilder sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT DISTINCT p.problem_id ");
            sqlStr.append(",p.name ");
            sqlStr.append("FROM problem p ");
            sqlStr.append(",correspondence c ");
            sqlStr.append("WHERE p.problem_id = c.problem_id ");
            sqlStr.append("AND p.problem_type_id = ? ");
            sqlStr.append("AND NOT EXISTS (SELECT xx.correspondence_id ");
            sqlStr.append("FROM correspondence_read_xref xx ");
            sqlStr.append("WHERE xx.user_id = ? and xx.correspondence_id = c.correspondence_id)");
            if (!isAdmin) {
                sqlStr.append("AND p.problem_id IN (SELECT ct.problem_id ");
                sqlStr.append("FROM component ct ");
                sqlStr.append(",component_user_xref cu ");
                sqlStr.append("WHERE cu.user_id = ? ");
                sqlStr.append("AND cu.component_id = ct.component_id) ");
            }
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, ServerContestConstants.TEAM_PROBLEM);
            ps.setInt(2, userId);
            if (!isAdmin) ps.setInt(3, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                problems.add(new NamedIdItem(rs.getString(2), rs.getInt(1), NamedIdItem.TEAM_PROBLEM));
            }
            rs.close();
            ps.close();

            //Single problem unread
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT DISTINCT p.problem_id ");
            sqlStr.append(",ct.class_name ");
            sqlStr.append("FROM problem p ");
            sqlStr.append(",correspondence c ");
            sqlStr.append(",component ct ");
            sqlStr.append("WHERE ct.component_id = c.component_id ");
            sqlStr.append("AND ct.problem_id = p.problem_id ");
            sqlStr.append("AND p.problem_type_id = ? ");
            sqlStr.append("AND NOT EXISTS (SELECT xx.correspondence_id ");
            sqlStr.append("FROM correspondence_read_xref xx ");
            sqlStr.append("WHERE xx.user_id = ? and xx.correspondence_id = c.correspondence_id)");
            if (!isAdmin) {
                sqlStr.append("AND ct.component_id IN (SELECT cu.component_id ");
                sqlStr.append("FROM component_user_xref cu ");
                sqlStr.append("WHERE cu.user_id = ?) ");
            }
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, ServerContestConstants.SINGLE_PROBLEM);
            ps.setInt(2, userId);
            if (!isAdmin) ps.setInt(3, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                problems.add(new NamedIdItem(rs.getString(2), rs.getInt(1), NamedIdItem.SINGLE_PROBLEM));
            }
            rs.close();
            ps.close();

            // Long problem unread
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT DISTINCT p.problem_id ");
            sqlStr.append(",ct.class_name ");
            sqlStr.append("FROM problem p ");
            sqlStr.append(",correspondence c ");
            sqlStr.append(",component ct ");
            sqlStr.append("WHERE ct.component_id = c.component_id ");
            sqlStr.append("AND ct.problem_id = p.problem_id ");
            sqlStr.append("AND p.problem_type_id = ? ");
            sqlStr.append("AND NOT EXISTS (SELECT xx.correspondence_id ");
            sqlStr.append("FROM correspondence_read_xref xx ");
            sqlStr.append("WHERE xx.user_id = ? and xx.correspondence_id = c.correspondence_id)");
            if (!isAdmin) {
                sqlStr.append("AND ct.component_id IN (SELECT cu.component_id ");
                sqlStr.append("FROM component_user_xref cu ");
                sqlStr.append("WHERE cu.user_id = ?) ");
            }
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, ServerContestConstants.LONG_PROBLEM);
            ps.setInt(2, userId);
            if (!isAdmin) ps.setInt(3, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                problems.add(new NamedIdItem(rs.getString(2), rs.getInt(1), NamedIdItem.LONG_PROBLEM));
            }

            //components unread
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT DISTINCT ct.component_id ");
            sqlStr.append(",ct.class_name ");
            sqlStr.append("FROM problem p ");
            sqlStr.append(",correspondence c ");
            sqlStr.append(",component ct ");
            sqlStr.append("WHERE ct.component_id = c.component_id ");
            sqlStr.append("AND ct.problem_id = p.problem_id ");
            sqlStr.append("AND p.problem_type_id = ? ");
            sqlStr.append("AND NOT EXISTS (SELECT xx.correspondence_id ");
            sqlStr.append("FROM correspondence_read_xref xx ");
            sqlStr.append("WHERE xx.user_id = ? and xx.correspondence_id = c.correspondence_id)");
            if (!isAdmin) {
                sqlStr.append("AND ct.component_id IN (SELECT cu.component_id ");
                sqlStr.append("FROM component_user_xref cu ");
                sqlStr.append("WHERE cu.user_id = ?) ");
            }
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, ServerContestConstants.TEAM_PROBLEM);
            ps.setInt(2, userId);
            if (!isAdmin) ps.setInt(3, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                problems.add(new NamedIdItem(rs.getString(2), rs.getInt(1), NamedIdItem.COMPONENT));
            }
        } catch (Exception e) {
            s_trace.error("Error getting unread messages for " + userId, e);
            e.printStackTrace();
        } finally {
            //closeConnection(conn, ps);
            close(conn, ps, rs);
        }

        return problems;
    }

    /**
     * Returns a list of correpondences associated with the specified problem.
     *
     * @param problemId The problem for which to get the correspondence.
     */
    public ArrayList getProblemCorrespondence(int problemId) {
        return getCorrespondence(problemId, "problem_id");
    }

    /**
     * Returns a list of correspondence associated with the specified component.
     *
     * @param componentId The component for which to get the correspondence.
     */
    public ArrayList getComponentCorrespondence(int componentId) {
        return getCorrespondence(componentId, "component_id");
    }

    /**
     * Gets a list of correspondence for a problem or component.
     *
     * @param id The problem of component id.
     * @param idType Either "component_id" or "problem_id"
     */
    private ArrayList getCorrespondence(int id, String idType) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = null;
        ArrayList correspondence = new ArrayList();

        try {
            conn = DBMS.getConnection();

            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT c.correspondence_id AS id ");
            sqlStr.append(",u.handle AS sender ");
            sqlStr.append(",c.message AS message ");
            sqlStr.append(",c.sent_time AS date ");
            sqlStr.append(",NVL(c.reply_id, -1) AS reply_to_id ");
            sqlStr.append("FROM correspondence c ");
            sqlStr.append(",user u ");
            sqlStr.append("WHERE u.user_id = c.from_coder_id ");
            sqlStr.append("AND c.");
            sqlStr.append(idType);
            sqlStr.append(" = ? ");
            sqlStr.append("ORDER BY c.sent_time ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, id);
            rs = ps.executeQuery();

            while (rs.next()) {
                correspondence.add(new Correspondence(
                        rs.getTimestamp("date").toString(),
                        rs.getString("sender"),
                        DBMS.getTextString(rs, "message"),
                        rs.getInt("id"),
                        rs.getInt("reply_to_id")));
            }
        } catch (Exception e) {
            s_trace.error("Error getting correspondence.", e);
        } finally {
            //closeConnection(conn, ps);
            close(conn, ps, rs);
        }

        return correspondence;
    }

    /**
     * Returns a list of UserInformations who can potentially receive
     * correspondence for the specified problem.
     */
    public ArrayList getProblemCorrespondenceReceivers(int problemId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder();
        ArrayList receivers = new ArrayList();

        try {
            conn = DBMS.getConnection();

            //First get list of all components
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT component_id ");
            sqlStr.append("FROM component ");
            sqlStr.append("WHERE problem_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, problemId);
            rs = ps.executeQuery();
            ArrayList componentIds = new ArrayList();
            while (rs.next()) {
                componentIds.add(new Integer(rs.getInt("component_id")));
            }

            //Get lists of all users for each component
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT user_id ");
            sqlStr.append("FROM component_user_xref ");
            sqlStr.append("WHERE component_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ArrayList groups = new ArrayList();
            ArrayList group;
            for (int i = 0; i < componentIds.size(); i++) {
                ps.setInt(1, ((Integer) componentIds.get(i)).intValue());
                rs = ps.executeQuery();
                group = new ArrayList();
                while (rs.next()) {
                    group.add(new Integer(rs.getInt("user_id")));
                }
                groups.add(group);
            }

            //culminate list into a big list
            ArrayList allUsers = new ArrayList();
            for (int i = 0; i < groups.size(); i++) {
                group = (ArrayList) groups.get(i);
                for (int j = 0; j < group.size(); j++) {
                    if (!allUsers.contains(group.get(j))) {
                        allUsers.add(group.get(j));
                    }
                }
            }

            //And now add the users that are in all the groups
            ps = conn.prepareStatement("SELECT handle FROM user WHERE user_id = ?");
            boolean inAll = true;
            UserInformation userInfo;
            for (int i = 0; i < allUsers.size(); i++) {
                inAll = true;
                for (int j = 0; inAll && j < groups.size(); j++) {
                    if (!((ArrayList) groups.get(j)).contains(allUsers.get(i))) {
                        inAll = false;
                    }
                }

                if (inAll) {
                    ps.setInt(1, ((Integer) allUsers.get(i)).intValue());
                    rs = ps.executeQuery();
                    rs.next();
                    userInfo = new UserInformation(rs.getString("handle"),
                            ((Integer) allUsers.get(i)).intValue());
                    if (!receivers.contains(userInfo)) receivers.add(userInfo);
                }
            }
        } catch (Exception e) {
            s_trace.error("Error getting receivers for component correspondence.", e);
        } finally {
            //closeConnection(conn, ps);
            close(conn, ps, rs);
        }
        return receivers;
    }

    /**
     * Returns a list of UserInformations who can potentially receive
     * correspondence for the specified component.
     */
    public ArrayList getComponentCorrespondenceReceivers(int componentId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder();
        ArrayList receivers = new ArrayList();

        try {
            conn = DBMS.getConnection();

            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT u.handle AS handle ");
            sqlStr.append(",u.user_id AS user_id ");
            sqlStr.append("FROM user u ");
            sqlStr.append("JOIN component_user_xref cu ");
            sqlStr.append("ON cu.user_id = u.user_id ");
            sqlStr.append("AND cu.component_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            rs = ps.executeQuery();

            UserInformation userInfo;
            while (rs.next()) {
                userInfo = new UserInformation(rs.getString("handle"),
                        rs.getInt("user_id"));
                if (!receivers.contains(userInfo)) receivers.add(userInfo);
            }
        } catch (Exception e) {
            s_trace.error("Error getting receivers for component correspondence.", e);
        } finally {
            //closeConnection(conn, ps);
            close(conn, ps, rs);
        }
        return receivers;
    }

    /**
     * Marks correspondence as read for the specified problem and user.
     */
    public boolean markProblemCorrespondenceRead(int problemId, int userId) {
        return markCorrespondenceRead(problemId, userId, "problem_id");
    }

    /**
     * Marks correspondence as read for the specified component and user
     */
    public boolean markComponentCorrespondenceRead(int componentId, int userId) {
        return markCorrespondenceRead(componentId, userId, "component_id");
    }

    /**
     * Marks correspondence read for the specified problem or component and
     * specified user.
     * idType must be "component_id" or "problem_id".
     */
    private boolean markCorrespondenceRead(int id, int userId, String idType) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = null;

        try {
            conn = DBMS.getConnection();

            sqlStr = new StringBuilder(256);
            sqlStr.replace(0, sqlStr.length(), "");
            sqlStr.append("INSERT INTO correspondence_read_xref ");
            sqlStr.append("(correspondence_id ");
            sqlStr.append(",user_id ");
            sqlStr.append(",timestamp) ");
            sqlStr.append("SELECT correspondence_id ");
            sqlStr.append(",");
            sqlStr.append(userId); //XXX: no ? allowed in select
            sqlStr.append(",current ");
            sqlStr.append("FROM correspondence c ");
            sqlStr.append("WHERE ");
            sqlStr.append(idType);
            sqlStr.append(" = ? ");
            sqlStr.append("AND NOT EXISTS (SELECT x.correspondence_id ");
            sqlStr.append("FROM correspondence_read_xref x ");
            sqlStr.append("WHERE x.user_id = ? and x.correspondence_id = c.correspondence_id) ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, id);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            s_trace.error("Error marking correspondence as read.", e);
            return false;
        } finally {
            //closeConnection(conn, ps);
            close(conn, ps, rs);
        }
        return true;
    }

    /*****************************************************************************
     * Admin Problem Services                                                    *
     *****************************************************************************/

    /**
     * Processes an admin's reply to a proposal or submission
     * (approved or disproved). It returns an ArrayList constaining a boolean
     * representing if things went ok, and if not, the second element is a
     * String that is the error.
     *
     * @param problemId  The problemId the admin is replying to.
     * @param approved   A boolean representing if the problem is approved
     * @param message    A message about the problem proposal
     * @param userId     The userId of the admin replying to the problem
     */
    public ArrayList processPendingReply(int problemId, boolean approved,
            String message, int userId) {
        if (VERBOSE) s_trace.debug("In MPSQASServicesBean.processPendingReply()..");
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList replyInfo = new ArrayList(2);
        try {
            conn = DBMS.getConnection();

            StringBuilder sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT handle ");
            sqlStr.append("FROM user ");
            sqlStr.append("WHERE user_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            rs.next();
            String adminHandle = rs.getString(1);

            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT p.status_id ");
            sqlStr.append(",cu.user_id ");
            sqlStr.append(",p.name ");
            sqlStr.append(",e.address ");
            sqlStr.append(",u.handle ");
            sqlStr.append("FROM problem p ");
            sqlStr.append(",user u ");
            sqlStr.append(",component_user_xref cu ");
            sqlStr.append(",email e ");
            sqlStr.append("WHERE p.problem_id = ? ");
            sqlStr.append("AND cu.user_id = u.user_id ");
            sqlStr.append("AND cu.user_type_id = ? ");
            sqlStr.append("AND e.user_id = u.user_id ");
            sqlStr.append("AND e.primary_ind = 1 ");
            sqlStr.append("AND cu.component_id in (SELECT component_id ");
            sqlStr.append("FROM component ");
            sqlStr.append("WHERE problem_id = ?)");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, problemId);
            ps.setInt(2, ApplicationConstants.PROBLEM_WRITER);
            ps.setInt(3, problemId);
            rs = ps.executeQuery();

            if (rs.next()) {
                int status = rs.getInt(1);
                //int submittedBy = rs.getInt(2);
                String name = rs.getString(3);
                String emailAddy = rs.getString(4);
                String handle = rs.getString(5);
                int newstatus = status;

                if (status == StatusConstants.PROPOSAL_PENDING_APPROVAL
                        || status == StatusConstants.SUBMISSION_PENDING_APPROVAL) {
                    if (status == StatusConstants.PROPOSAL_PENDING_APPROVAL) {
                        if (approved)
                            newstatus = StatusConstants.PROPOSAL_APPROVED;
                        else
                            newstatus = StatusConstants.PROPOSAL_REJECTED;
                    } else {
                        if (approved)
                            newstatus = StatusConstants.SUBMISSION_APPROVED;
                        else
                            newstatus = StatusConstants.SUBMISSION_REJECTED;
                    }

                    sqlStr = new StringBuilder(256);
                    sqlStr.append("UPDATE problem ");
                    sqlStr.append("SET status_id = ? ");
                    sqlStr.append("WHERE problem_id = ?");
                    ps = conn.prepareStatement(sqlStr.toString());
                    ps.setInt(1, newstatus);
                    ps.setInt(2, problemId);
                    //int numUpdates = ps.executeUpdate();

                    if (message != null && message.trim().length() > 0) {
                        sqlStr = new StringBuilder(256);
                        sqlStr.append("SELECT problem_type_id ");
                        sqlStr.append("FROM problem ");
                        sqlStr.append("WHERE problem_id = ? ");
                        ps = conn.prepareStatement(sqlStr.toString());
                        ps.setInt(1, problemId);
                        rs = ps.executeQuery();
                        rs.next();
                        int problemType = rs.getInt("problem_type_id");
                        rs.close();
                        ps.close();
                        int id;
                        String column;
                        if (problemType == ServerContestConstants.TEAM_PROBLEM) {  //correspondence at problem level
                            id = problemId;
                            column = "problem_id";
                        } else {  //email at component level, find component
                            sqlStr = new StringBuilder(256);
                            sqlStr.append("SELECT component_id ");
                            sqlStr.append("FROM component ");
                            sqlStr.append("WHERE problem_id = ? ");
                            ps = conn.prepareStatement(sqlStr.toString());
                            ps.setInt(1, problemId);
                            rs = ps.executeQuery();
                            rs.next();
                            id = rs.getInt("component_id");
                            column = "component_id";
                        }

                        int correspondenceId = IdGeneratorClient.getSeqIdAsInt(DBMS.JMA_SEQ);
                        sqlStr = new StringBuilder(256);
                        sqlStr.append("INSERT INTO correspondence ");
                        sqlStr.append("(correspondence_id ");
                        sqlStr.append(",from_coder_id ");
                        sqlStr.append(",");
                        sqlStr.append(column);
                        sqlStr.append(",message ");
                        sqlStr.append(",sent_time) ");
                        sqlStr.append("VALUES (?, ?, ?, ?, current)");
                        ps = conn.prepareStatement(sqlStr.toString());
                        ps.setInt(1, correspondenceId);
                        ps.setInt(2, userId);
                        ps.setInt(3, id);
                        ps.setBytes(4, DBMS.serializeTextString(message));
                        ps.executeUpdate();
                    }

                    try {
                        //send an email to the user
                        TCSEmailMessage email = new TCSEmailMessage();
                        String type = (status == StatusConstants.PROPOSAL_PENDING_APPROVAL)
                                ? "Proposal"
                                : "Submission";
                        String statusS = (approved) ? "Accepted" : "Rejected";
                        StringBuilder emailBody = new StringBuilder(256);
                        emailBody.append("Hi ");
                        emailBody.append(handle);
                        emailBody.append(",\n\n");
                        emailBody.append("Your ");
                        emailBody.append(type);
                        emailBody.append(" of ");
                        emailBody.append(name);
                        emailBody.append(" was ");
                        emailBody.append(statusS);
                        emailBody.append(" by TopCoder admin ");
                        emailBody.append(adminHandle);
                        emailBody.append(".\n");
                        if (message != null && message.trim().length() > 0) {
                            emailBody.append("\n");
                            emailBody.append(adminHandle);
                            emailBody.append(" says: \n\n");
                            emailBody.append(ApplicationConstants.HORIZONTAL_RULE);
                            emailBody.append(message);
                            emailBody.append("\n");
                            emailBody.append(ApplicationConstants.HORIZONTAL_RULE);
                        }

                        emailBody.append("\nLog in to the applet to work on your problem ");
                        emailBody.append("further.\n\n");
                        emailBody.append("-mpsqas\n\n");
                        emailBody.append("This is an automated message from MPSQAS.\n");
                        email.setSubject("TopCoder Problem " + type + " " + statusS);
                        email.setBody(emailBody.toString());

                        email.setFromAddress(ApplicationConstants.FROM_EMAIL_ADDRESS);
                        email.addToAddress(emailAddy, TCSEmailMessage.TO);
                        EmailEngine.send(email);
                    } catch (Exception e) {
                        s_trace.error("Error sending email.", e);
                    }

                    replyInfo.add(new Boolean(true));
                } else  //Not PENDING status
                {
                    replyInfo.add(new Boolean(false));
                    replyInfo.add("The problem to which you are replying is not pending approval.");
                }
            } else //!rs.next() when getting problem
            {
                replyInfo.add(new Boolean(false));
                replyInfo.add("The problem to which you are replying does not exist in the database.");
            }
        } catch (Exception e) {
            replyInfo = new ArrayList(2);
            replyInfo.add(new Boolean(false));
            replyInfo.add(ApplicationConstants.SERVER_ERROR);
            s_trace.error("Error inserting reply to problem proposal: ", e);
        } finally {
            //closeConnection(conn, ps);
            close(conn, ps, rs);
        }
        return replyInfo;
    }

    /** Sets the problem status of the problem. */
    public boolean setProblemStatus(int problemId, int status) {
        boolean result = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr;

        try {
            conn = DBMS.getConnection();

            sqlStr = new StringBuilder(256);
            sqlStr.append("UPDATE problem SET status_id = ? WHERE problem_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, status);
            ps.setInt(2, problemId);

            result = ps.executeUpdate() == 1;
        } catch (Exception e) {
            s_trace.error("Error setting problem status.", e);
        } finally {
            //closeConnection(conn, ps);
            close(conn, ps, rs);
        }
        return result;
    }

    /** Sets the primary solution of the problem. */
    public boolean setPrimarySolution(int componentId, int primarySolutionId) {
        boolean result = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr;

        try {
            conn = DBMS.getConnection();

            sqlStr = new StringBuilder(256);
            sqlStr.append("UPDATE component_solution_xref ");
            sqlStr.append("SET primary_solution = ? ");
            sqlStr.append("WHERE component_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, ApplicationConstants.SECONDARY_SOLUTION);
            ps.setInt(2, componentId);
            ps.executeUpdate();

            if (primarySolutionId > 0) {
                sqlStr.append("AND solution_id = ? ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, ApplicationConstants.PRIMARY_SOLUTION);
                ps.setInt(2, componentId);
                ps.setInt(3, primarySolutionId);
                ps.executeUpdate();
            }

            result = true;
        } catch (Exception e) {
            s_trace.error("Error setting primary solution.", e);
        } finally {
            //closeConnection(conn, ps);
            close(conn, ps, rs);
        }
        return result;
    }

    /** Sets the problem testers for all of the problem's components. */
    public boolean setTestersForProblem(int problemId, ArrayList testerIds) {
        boolean result = false;
        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        StringBuilder sqlStr;

        try {
            conn = DBMS.getConnection();

            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT component_id FROM component WHERE problem_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, problemId);
            rs = ps.executeQuery();

            ArrayList componentIds = new ArrayList();
            while (rs.next()) componentIds.add(new Integer(rs.getInt("component_id")));

            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT user_id ");
            sqlStr.append("FROM component_user_xref ");
            sqlStr.append("WHERE component_id = ? ");
            sqlStr.append("AND user_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());

            sqlStr = new StringBuilder(256);
            sqlStr.append("INSERT INTO component_user_xref ");
            sqlStr.append("(component_id ");
            sqlStr.append(",user_id ");
            sqlStr.append(",user_type_id) ");
            sqlStr.append("VALUES (?, ?, ?) ");
            ps2 = conn.prepareStatement(sqlStr.toString());
            ps2.setInt(3, ApplicationConstants.PROBLEM_TESTER);

            for (int i = 0; i < componentIds.size(); i++) {
                for (int j = 0; j < testerIds.size(); j++) {
                    ps.setInt(1, ((Integer) componentIds.get(i)).intValue());
                    ps.setInt(2, ((Integer) testerIds.get(j)).intValue());
                    rs = ps.executeQuery();
                    if (!rs.next()) {  //he's not already in there, insert him
                        ps2.setInt(1, ((Integer) componentIds.get(i)).intValue());
                        ps2.setInt(2, ((Integer) testerIds.get(j)).intValue());
                        ps2.executeUpdate();
                    }
                }
            }

            //email about it
            try {
                ps = conn.prepareStatement(
                        "SELECT name FROM problem WHERE problem_id = ?");
                ps.setInt(1, problemId);
                rs = ps.executeQuery();
                rs.next();
                String name = rs.getString("name");
                rs.close();
                ps.close();

                ps = conn.prepareStatement("SELECT e.address FROM user u, email e WHERE e.user_id = u.user_id and e.primary_ind = 1 and u.user_id = ?");
                TCSEmailMessage email;
                StringBuilder body = new StringBuilder();
                body.append("Hi,\n\n");
                body.append("You have been scheduled to test the following problem:\n");
                body.append(name);
                body.append("\n\nPlease log in frequently to help polish the problem.");
                body.append("\n\n-mpsqas\n\nThis is an automated message from mpsqas.");

                for (int i = 0; i < testerIds.size(); i++) {
                    email = new TCSEmailMessage();
                    email.setFromAddress(ApplicationConstants.FROM_EMAIL_ADDRESS);
                    email.setBody(body.toString());
                    email.setSubject("Problem testing problem: " + name);
                    ps.setInt(1, ((Integer) testerIds.get(i)).intValue());
                    rs = ps.executeQuery();
                    rs.next();
                    email.addToAddress(rs.getString("address"), TCSEmailMessage.TO);
                    EmailEngine.send(email);
                }
            } catch (Exception e) {
                s_trace.error("Error sending email about testing problems.", e);
            }

            result = true;
        } catch (Exception e) {
            s_trace.error("Error setting problem testers.", e);
        } finally {
            //closeConnection(conn, ps);
            close(conn, ps, rs);
        }
        return result;
    }

    /** Sets the problem testers for a component. */
    public boolean setTestersForComponent(int componentId, ArrayList testerIds) {
        boolean result = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr;

        try {
            conn = DBMS.getConnection();

            //first delete old testers
            sqlStr = new StringBuilder(256);
            sqlStr.append("DELETE FROM component_user_xref ");
            sqlStr.append("WHERE component_id = ? ");
            sqlStr.append("AND user_type_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            ps.setInt(2, ApplicationConstants.PROBLEM_TESTER);
            ps.executeUpdate();

            //now insert new testers
            sqlStr = new StringBuilder(256);
            sqlStr.append("INSERT INTO component_user_xref ");
            sqlStr.append("(component_id ");
            sqlStr.append(",user_id ");
            sqlStr.append(",user_type_id) ");
            sqlStr.append("VALUES (?, ?, ?) ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            ps.setInt(3, ApplicationConstants.PROBLEM_TESTER);
            for (int i = 0; i < testerIds.size(); i++) {
                ps.setInt(2, ((Integer) testerIds.get(i)).intValue());
                ps.executeUpdate();
            }

            result = true;

            //email about it
            try {
                String name = componentDao.getClassNameForComponent(componentId, conn);

                ps = conn.prepareStatement("SELECT e.address FROM user u, email e WHERE e.user_id = u.user_id and e.primary_ind = 1 and u.user_id = ?");
                TCSEmailMessage email;
                StringBuilder body = new StringBuilder();
                body.append("Hi,\n\n");
                body.append(
                        "You have been scheduled to test the following component:\n");
                body.append(name);
                body.append(
                        "\n\nPlease log in frequently to help polish the component.");
                body.append("\n\n-mpsqas\n\nThis is an automated message from mpsqas.");

                for (int i = 0; i < testerIds.size(); i++) {
                    email = new TCSEmailMessage();
                    email.setFromAddress(ApplicationConstants.FROM_EMAIL_ADDRESS);
                    email.setBody(body.toString());
                    email.setSubject("Problem testing component: " + name);
                    ps.setInt(1, ((Integer) testerIds.get(i)).intValue());
                    rs = ps.executeQuery();
                    rs.next();
                    email.addToAddress(rs.getString("address"), TCSEmailMessage.TO);
                    EmailEngine.send(email);
                }
            } catch (Exception e) {
                s_trace.error("Error sending email about testing problems.", e);
            }
        } catch (Exception e) {
            s_trace.error("Error setting problem status.", e);
        } finally {
            //closeConnection(conn, ps);
            close(conn, ps, rs);
        }
        return result;
    }

    /******************************************************************************
     * Compile and Testing services                                               *
     ******************************************************************************/

    /**
     * Compiles code from a user, and if the compile is sucessful
     * inserts the results into the database for testing
     *
     * @param files The Files of the code to compile.  This method assumes
     *              there is only one file in the HashMap.
     * @param language The language to compile.
     * @param componentId The component id to compile.
     * @param userId The user id of the person compiling.
     */
    public ArrayList compileSolution(
            HashMap files, int language, int componentId, int userId) {
        ArrayList results = new ArrayList();
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            results = compileSolution(files, language, componentId, userId, conn);
        } catch (Exception e) {
            s_trace.error("Error compiling:", e);
            results = new ArrayList();
            results.add(new Boolean(false));
            results.add(ApplicationConstants.SERVER_ERROR);
        } finally {
            //closeConnection(conn, null);
            close(conn, null, null);
        }
        return results;
    }

    /**
     * Compiles code from a user, and if the compile is sucessful
     * inserts the results into the database for testing
     *
     * @param files The Files of the code to compile.  This method assumes
     *              there is only one file in the HashMap.
     * @param language The language to compile.
     * @param componentId The component id to compile.
     * @param userId The user id of the person compiling.
     * @param conn A connection to use
     */
    private ArrayList compileSolution(
            HashMap files, int language, int componentId, int userId,
            Connection conn) throws Exception {
        if (VERBOSE) s_trace.debug("In MPSQASServicesBean.compile");
        ArrayList results = new ArrayList(2);
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            StringBuilder sqlStr = new StringBuilder(256);
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT class_name, problem_id ");
            sqlStr.append("FROM component WHERE component_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            rs = ps.executeQuery();
            rs.next();

        //get the solution id
            debug("calling ProblemServices.getSolutionId()");
            int solutionId = getProblemServices().getSolutionId(componentId, userId);
            String className = rs.getString(1);
            int problemId = rs.getInt(2);
            String packageName = "com.topcoder.tester.solutions.s" + solutionId;
            String code = (String) files.get(files.keySet().iterator().next());
            HashMap dependencies = getDependencySolutionSourceFiles(problemId, componentId, userId, conn);


            if (language == ContestConstants.JAVA) {
                //If the solution is a Java solution, we add the package declaration and
                //and the necessary imports.
                code = ApplicationConstants.AUTO_GENERATED_END_COMMENT_FLAG + code;
                //prepend package and imports to code
                for (Iterator it = dependencies.keySet().iterator(); it.hasNext();) {
                    code = "import " + (String) it.next() + ";" + code;
                }
                code = "package " + packageName + ";" + code;
            }

            //restore code
            HashMap toCompilerFiles = new HashMap();
            toCompilerFiles.put(packageName + "." + className, code);

            //If the component is a Long contest component
            //we must generated wrapper class used to run the solutions
            //Wrapper class differs for primary solution and tester solution
            boolean allowThreadingIfRoundAllowsIt = true;
            ProblemComponent pc = getProblemServices().getProblemComponent(componentId,false);
            if (pc.getComponentTypeID() == ProblemConstants.LONG_COMPONENT) {
                if (solutionId == getProblemServices().getPrimarySolutionId(componentId)) {
                    if (language != ContestConstants.JAVA) {
                        //If it is a primary solution and its language is not java. error.
                        results.add(Boolean.FALSE);
                        results.add("Only Java primary solutions allowed");
                        return results;
                    }
                    String proxyCode = LongContestCodeGeneratorHelper.generateLongTestProxyCode(pc, packageName, language);
                    toCompilerFiles.put(packageName+"."+ProblemConstants.TESTER_IO_CLASS, proxyCode);
                    allowThreadingIfRoundAllowsIt = false;
                } else {
                    String wrapperCode = LongContestCodeGeneratorHelper.generateWrapperForUserCode(pc, packageName, language);
                    toCompilerFiles.put(packageName+"."+ProblemConstants.WRAPPER_CLASS, wrapperCode);

                    String name = pc.getExposedClassName();
                    if(name == null || name.equals("")) {
                        name = "ExposedWrapper";
                    }

                    wrapperCode = LongContestCodeGeneratorHelper.generateWrapperForExposedCode(pc, packageName, language);
                    if(language == ContestConstants.JAVA)
                        wrapperCode = "package " + packageName + ";" + wrapperCode;

                    toCompilerFiles.put(packageName+"."+name, wrapperCode);
                }
            } else {
                if (solutionId == getProblemServices().getPrimarySolutionId(componentId)) {
                    if (language != ContestConstants.JAVA) {
                        //If it is a primary solution and its language is not java. error.
                        results.add(Boolean.FALSE);
                        results.add("Only Java primary solutions allowed");
                        return results;
                    }
                }
                // We now allow other programming languages for non-primary solution
            }
            toCompilerFiles.putAll(dependencies);


            MPSQASFiles mfiles = new MPSQASFiles();
            mfiles.setSourceFiles(toCompilerFiles);
            mfiles.setPackageName(packageName);
            mfiles.setClassName(className);
            mfiles.setMethodName(pc.getMethodName());
            mfiles.setWebServiceFiles(getWebServiceClientSourceFiles(problemId, language, conn));
            mfiles.setComponentType(pc.getComponentTypeID());
            mfiles.setLanguage(language);
            mfiles.setThreadingAllowed(allowThreadingIfRoundAllowsIt && RoundUtils.isThreadingAllowed(pc.getRoundType()));
            mfiles.setArgTypes(Arrays.asList(pc.getParamTypes()));
            mfiles.setResultType(pc.getReturnType());
            mfiles.setRoundType(pc.getRoundType());
            ProblemCustomSettings custom = pc.getProblemCustomSettings();
            mfiles.setProblemCustomSettings(custom);
            
            String name = pc.getExposedClassName();
            if(name == null || name.equals("")) {
                name = "ExposedWrapper";
            }
            mfiles.setExposedClassName(name);
            try {
                mfiles = compiler.compileMPSQAS(mfiles);
            } catch (CompilerTimeoutException e) {
                mfiles.setCompileStatus(false);
                mfiles.setStdErr("Compiler timed out.");
            } catch (Exception e) {
                s_trace.error("Could not compile solution", e);
                mfiles.setCompileStatus(false);
                mfiles.setStdErr("Could not compile solution. Error :"+e.getMessage());
            }


            if (mfiles.getCompileStatus()) {
                //update the solution class in the database
               //backUpSolution(solutionId, userId);
                sqlStr = new StringBuilder(256);
                sqlStr.append("UPDATE solution ");
                sqlStr.append("SET solution_text = ? ");
                sqlStr.append(",language_id = ? ");
                sqlStr.append(",package = ? ");
                sqlStr.append("WHERE solution_id = ?");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setBytes(1, DBMS.serializeTextString(code));
                ps.setInt(2, language);
                ps.setString(3, packageName);
                ps.setInt(4, solutionId);
                ps.executeUpdate();

                //remove old solution class files
                sqlStr = new StringBuilder(256);
                sqlStr.append("DELETE FROM solution_class_file ");
                sqlStr.append("WHERE solution_id = ? ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, solutionId);
                ps.executeUpdate();

                sqlStr = new StringBuilder(256);
                sqlStr.append("INSERT INTO solution_class_file ");
                sqlStr.append("(solution_id ");
                sqlStr.append(",sort_order ");
                sqlStr.append(",path ");
                sqlStr.append(",class_file ) ");
                sqlStr.append("VALUES (?, ?, ?, ?) ");
                ps = conn.prepareStatement(sqlStr.toString());
                int order = 1;
                for (Iterator iter = mfiles.getClassFiles().keySet().iterator(); iter.hasNext();) {
                    name = (String) iter.next();
                    ps.setInt(1, solutionId);
                    ps.setInt(2, order++);
                    ps.setString(3, name);
                    ps.setBytes(4, (byte[]) mfiles.getClassFiles().get(name));
                    ps.executeUpdate();
                }

                // Set is check answer flag.
                boolean hasCheckAnswer = new FarmSolutionInvokator(
                        getTestServices().getComponentSolution(componentId)).hasCheckAnswer();
                sqlStr = new StringBuilder(256);
                sqlStr.append("UPDATE solution ");
                sqlStr.append("SET has_check_answer = ? ");
                sqlStr.append("WHERE solution_id = ?");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setBoolean(1, hasCheckAnswer);
                ps.setInt(2, solutionId);
                ps.executeUpdate();

                results.add(new Boolean(true));
            } else {
				s_trace.debug("Compilation failed");
                results.add(new Boolean(false));
                String msg = Formatter.truncateOutErr(mfiles.getStdOut(), mfiles.getStdErr());
                results.add(msg.length() == 0 ? "Compile problems, but standard error is null." : msg);
            }

        }  catch (Exception e) {
            s_trace.error("Error in compile solution:", e);
            results.add(new Boolean(false));
            results.add("Internal server error");
        } finally {
            close(null, ps, rs);
        }
        return results;
     }

    /**
     * Returns a hashmap of path -> source file of all the component
     * classes for this problem written by the specified user.
     */
    private HashMap getDependencySolutionSourceFiles(int problemId,
            int componentId, int userId, Connection conn) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        HashMap files = new HashMap();
        //TODO the language is not taken into account
        try {
            StringBuilder sqlStr = new StringBuilder(256);
            boolean isAdmin = isAdmin(userId);
            sqlStr.append("SELECT s.package AS package ");
            sqlStr.append("      ,c.class_name AS class_name ");
            sqlStr.append("      ,s.solution_text AS solution_text ");
            sqlStr.append("FROM solution s ");
            sqlStr.append("    ,component c ");
            sqlStr.append("    ,component_solution_xref cs ");
            sqlStr.append("WHERE c.problem_id = ? ");
            sqlStr.append("  AND c.component_id <> ? ");
            sqlStr.append("  AND c.status_id = ? ");
            sqlStr.append("  AND c.component_id = cs.component_id ");
            sqlStr.append("  AND cs.solution_id = s.solution_id ");
            if (isAdmin) {
                sqlStr.append("AND cs.primary_solution = ? ");
            } else {
                sqlStr.append("  AND s.coder_id = ? ");
            }
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, problemId);
            ps.setInt(2, componentId);
            ps.setInt(3, StatusConstants.ACTIVE);
            if (isAdmin) {
                ps.setInt(4, ApplicationConstants.PRIMARY_SOLUTION);
            } else {
                ps.setInt(4, userId);
            }
            rs = ps.executeQuery();

            while (rs.next()) {
                files.put(rs.getString("package") + "." + rs.getString("class_name"),
                        DBMS.getTextString(rs, "solution_text"));
            }

            rs.close();
            ps.close();

        }  catch (Exception e) {
            s_trace.error("Error in compile solution:", e);
        } finally {
            close(null, ps, rs);
        }
        return files;
    }

    /**
     * Returns a hashmap of path -> source file of all the web service
     * client source files for the specified component and language.
     */
    private HashMap getWebServiceClientSourceFiles(int problemId, int language,
            Connection conn) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = null;
        HashMap files = new HashMap();

        try {
           sqlStr = new StringBuilder(256);
           sqlStr.append("SELECT wssf.path AS path ");
           sqlStr.append(",wssf.source AS source ");
           sqlStr.append("FROM web_service_source_file wssf ");
           sqlStr.append("JOIN problem_web_service_xref pws ");
           sqlStr.append("ON pws.problem_id = ? ");
           sqlStr.append("AND pws.web_service_id = wssf.web_service_id ");
           sqlStr.append("JOIN web_service w ");
           sqlStr.append("ON w.web_service_id = wssf.web_service_id ");
           sqlStr.append("AND w.status_id = ? ");
           sqlStr.append("WHERE wssf.language_id = ? ");
           ps = conn.prepareStatement(sqlStr.toString());
           ps.setInt(1, problemId);
           ps.setInt(2, StatusConstants.ACTIVE);
           ps.setInt(3, language);
           rs = ps.executeQuery();

           while (rs.next()) {
               files.put(rs.getString("path"), DBMS.getTextString(rs, "source"));
           }

           rs.close();
           ps.close();
        }  catch (Exception e) {
            s_trace.error("Error in compile solution:", e);
        } finally {
            close(null, ps, rs);
        }
        return files;
    }

    /**
     * Calls the tester to test some code and returns a string that is
     * the test results.  It gets the class files from the database.
     * Can test just the user's solution or all solutions.
     * If the component is a long compoment, all tester solutions
     * will be scheduled for testing. Finalization of the test should be
     * received using MPSQASServiceEventListener
     *
     * @param args An Object[] of arguments to pass to the method
     * @param componentId The component to test
     * @param userId The userId of the tester
     * @param type Specifies whether to test users solution only
     *                       or all solutions. (TEST_ONE or TEST_ALL)
     */
    public String test(Object[] args, int componentId, int userId, int type) {
        StringBuilder testResults = new StringBuilder(256);
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            testResults.append(test(args, componentId, userId, type, conn));
        } catch (Exception e) {
            s_trace.error("Error testing.", e);
            testResults.append(ApplicationConstants.SERVER_ERROR);
        } finally {
            //closeConnection(conn, null);
            close(conn, null, null);
        }
        return testResults.toString();
    }

    /**
     * @see  MPSQASServicesBean#test(Object[], int, int, int)
     *
     * @param args
     *        the arguments of mpsqas files.
     * @param componentId
     *        the component id.
     * @param userId
     *        the user id.
     * @param type
     *        the test type.
     * @param conn
     *        the jdbc connection.
     * @return the test result message.
     */
    public String test(Object[] args, int componentId, int userId, int type,
            Connection conn) throws Exception {
        PreparedStatement ps = null;
        StringBuilder testResults = new StringBuilder(256);
        ResultSet rs = null;

        try {
            debug("Test type = " + type);
            int roundType = getRoundTypeByComponentId(componentId);
            StringBuilder sqlStr = new StringBuilder(256);

            ArrayList argTypes = new ArrayList();
            ArrayList argValues = new ArrayList();

            //get arg type Strings
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT p.data_type_id ");
            sqlStr.append(",dt.data_type_desc ");
            sqlStr.append(",p.sort_order ");
            sqlStr.append("FROM parameter p JOIN data_type dt ");
            sqlStr.append("ON p.data_type_id = dt.data_type_id ");
            sqlStr.append("WHERE p.component_id = ? ");
            sqlStr.append("ORDER BY p.sort_order ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            rs = ps.executeQuery();
            while (rs.next()) {
                argTypes.add(getDataType(rs.getString(2)));
            }
            
            boolean mustStartLongTest = false;
            
            if (argTypes.size() == args.length) {
                argValues = new ArrayList(Arrays.asList(args));

                sqlStr = new StringBuilder(256);
                sqlStr.append("SELECT c.class_name ");
                sqlStr.append(",c.method_name ");
                sqlStr.append(",dt.data_type_desc ");
                sqlStr.append(",c.component_type_id ");
                sqlStr.append("FROM component c ");
                sqlStr.append(",data_type dt ");
                sqlStr.append("WHERE c.component_id = ? ");
                sqlStr.append("AND c.result_type_id = dt.data_type_id ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, componentId);
                rs = ps.executeQuery();
                rs.next();

                String className = rs.getString(1);
                String methodName = rs.getString(2);
                String resultType = rs.getString(3);
                int componentType = rs.getInt(4);

                if (componentType == ProblemConstants.LONG_COMPONENT) {
                    //If It is a Long Component, we must check no other tests has been scheduled
                    //for the user.
                    if (userTestGroupDao.findTestGroupCountForUser(userId, conn) > 0) {
                        testResults.append("You have scheduled tests!\n");
                        testResults.append("Wait for test completion or cancel them if you prefer.");
                        return testResults.toString();
                    }
                }

                MPSQASFiles mfiles = new MPSQASFiles();
                MPSQASFiles resultmfiles;
                mfiles.setArgTypes(argTypes);
                mfiles.setArgVals(argValues);
                mfiles.setResultType(getDataType(resultType));
                mfiles.setClassName(className);
                //set the problem component type
                mfiles.setComponentType(componentType);

                sqlStr = new StringBuilder(256);
                sqlStr.append("SELECT s.solution_id ");
                sqlStr.append(",cs.primary_solution ");
                sqlStr.append(",u.handle ");
                sqlStr.append(",s.language_id ");
                sqlStr.append("FROM component_solution_xref cs ");
                sqlStr.append(",solution s ");
                sqlStr.append(",user u ");
                sqlStr.append("WHERE cs.component_id = ? ");
                sqlStr.append("AND s.coder_id = u.user_id ");
                sqlStr.append("AND s.solution_id = cs.solution_id ");
                if (type == MessageConstants.TEST_ONE) {
                    sqlStr.append("AND s.solution_id = ? ");
                }
                sqlStr.append("ORDER BY cs.primary_solution DESC");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, componentId);
                if (type == MessageConstants.TEST_ONE) {
                    ps.setInt(2,
                            getProblemServices().getSolutionId(componentId, userId));
                }
                rs = ps.executeQuery();

                String packageName;
                String fileName;
                HashMap classFiles;
                int language;
                int solutionId;
                int primary;
                ProblemComponent problemComponent = null;
                String handle;
                ResultSet rs2;
                sqlStr = new StringBuilder(256);
                sqlStr.append("SELECT path ");
                sqlStr.append(",class_file ");
                sqlStr.append(",sort_order ");
                sqlStr.append("FROM solution_class_file ");
                sqlStr.append("WHERE solution_id = ? ");
                sqlStr.append("ORDER BY sort_order ");
                ps = conn.prepareStatement(sqlStr.toString());

                if (rs.next()) {
                    do {
                        solutionId = rs.getInt(1);
                        primary = rs.getInt(2);
                        handle = rs.getString(3);
                        language = rs.getInt(4);

                        testResults.append(handle);
                        testResults.append("'s solution:\n");

                        if (componentType == ProblemConstants.LONG_COMPONENT && primary == 0) {
                            //If it is a long component and the solution is not the primary solution
                            //schedule test for solution
                            if (problemComponent == null) {
                                problemComponent = getProblemServices().getProblemComponent(componentId, false);
                            }
                            testResults.append(prepareLongTest(userId, componentId,
                                                        solutionId, new String[] {(String) args[0]}, conn));

                            testResults.append("\n");
                            mustStartLongTest = true;
                        } else {
                            packageName = ServicesConstants.SOLUTIONS_PACKAGE + "s" + solutionId;

                            classFiles = new HashMap();
                            ps.setInt(1, solutionId);
                            rs2 = ps.executeQuery();
                            if (rs2.next()) {
                                do {
                                    fileName = rs2.getString(1);
                                    if (language == ContestConstants.JAVA) {
                                        //fileName is package + class for java, remove ".class" too
                                        fileName = fileName.substring(0, fileName.lastIndexOf("."))
                                                .replace('/', '.');
                                    }
                                    debug("fileName = " + fileName);
                                    classFiles.put(fileName, rs2.getBytes(2));
                                } while (rs2.next());

                                mfiles.setClassFiles(classFiles);
                                mfiles.setPackageName(packageName);
                                mfiles.setLanguage(language);
                                mfiles.setSolutionId(solutionId);
                                mfiles.setRoundType(roundType);
                                SimpleComponent component = CoreServices.getSimpleComponent(componentId);
                                mfiles.setProblemCustomSettings(component.getProblemCustomSettings());
                                
                                
                                if (primary == ApplicationConstants.PRIMARY_SOLUTION) {
                                    debug("Testing checkData.");
                                    mfiles.setMethodName("checkData");
                                    resultmfiles = getMPSQASTester().mpsqasTest(mfiles);
                                    testResults.append(resultmfiles.getMethodName()
                                            + "() called:\n");
                                    if (resultmfiles.getResultValue() != null) {
                                        testResults.append(resultmfiles.getResultValue());
                                    }
                                    if (resultmfiles.getExceptionText() != null) {
                                        testResults.append("\n" + resultmfiles.getExceptionText());
                                    }
                                    testResults.append("\n\n");
                                }

                                debug("Testing " + methodName);
                                mfiles.setMethodName(methodName);
                                resultmfiles = getMPSQASTester().mpsqasTest(mfiles);
                                testResults.append(resultmfiles.getMethodName() + "() called:\n");
                                if (resultmfiles.getResultValue() != null) {
                                    testResults.append(resultmfiles.getResultValue());
                                }
                                if (resultmfiles.getExceptionText() != null) {
                                    testResults.append("\n" + resultmfiles.getExceptionText());
                                }

                                testResults.append("\n\n");
                            } else {
                                testResults.append("(no compiled files to test)");
                            }
                        }
                    } while (rs.next());
                    if (mustStartLongTest) {
                        testResults.append(startLongTest(userId, problemComponent.getRoundType(),
                                problemComponent.getProblemCustomSettings(), conn)).append("\n");;
                    }
                } else {
                    testResults.append("No compiled solution to test.");
                }
            } else {
                testResults.append("Number of argument values does not equal number ");
                testResults.append("number of argument types, try saving problem.");
            }
        } catch (TesterInvokerException te) {
            s_trace.error("Error testing.", te);
            testResults.append(te.getMessage());
        } finally {
            close(null, ps, rs);
        }
        return testResults.toString();
    }

    /**
     * Call tests service to schedule tests for the solution and then
     * It associates testGroup created with the user. Test are prepared but not started.
     * Call {@link MPSQASServicesBean#startLongTest(int, int, int, Connection)} to start tests
     *
     * @param userId Id of the user requesting the test
     * @param componentId Id of the component
     * @param solutionId Id of the solution to test
     * @param args Array containing all tests cases inputs
     * @param cnn connection to used for database access
     *
     * @return String with information about the operation
     */
    private String prepareLongTest(int userId, int componentId, int solutionId, String[] args, Connection cnn) {
        s_trace.debug("Testing non primary solution of long component");
        try {
            int testGroupId = getTestServices().createTestGroupForSolution(componentId, solutionId, args);
            userTestGroupDao.associateUserAndTestGroup(userId, testGroupId, cnn);
            return "Scheduled for testing. Results will be notified as soon as they become available.";
        } catch (Exception e) {
            s_trace.error("Error trying to create test group.", e);
            return e.getMessage();
        }
    }
    
    
    /**
     * Call tests service to schedule tests for the solution and then
     * It associates testGroup created with the user
     *
     * @param userId Id of the user requesting the test
     * @param roundType the round type of the problem
     * @param custom problem customization.
     * @param cnn connection to used for database access
     *
     * @return String with information about the operation
     */
    private String startLongTest(int userId, int roundType, ProblemCustomSettings custom, Connection cnn) {
        s_trace.debug("Testing non primary solution of long component");
        try {
            Integer roundTypeArg = null;
            if (roundType != -1) {
                roundTypeArg = new Integer(roundType);
            }
            List ids = userTestGroupDao.findTestGroupIdsForUser(userId, cnn);
            for (Iterator it = ids.iterator(); it.hasNext();) {
                Number id = (Number) it.next();
                getTestServices().startTestGroup(id.intValue(), roundTypeArg, custom);
            }
            return "";
        } catch (Exception e) {
            s_trace.error("Error trying to start test groups.", e);
            return e.getMessage();
        }
    }


    /**
     * @see MPSQASServices#testGroupFinalized(int)
     */
    public void testGroupFinalized(int testGroupId) {
        s_trace.info("TestGroupFinalized("+testGroupId+")");
        Connection cnn = null;
        try {
            cnn = DBMS.getConnection();
            int userId = userTestGroupDao.findUserIdForTestGroup(testGroupId, cnn);
            if (userId == -1) {
                s_trace.warn("Could not find user for test group, ignoring");
                //The test group is not of a mpsqas user.
                return;
            }
            LongTestGroup testGroup = getTestServices().findTestGroup(testGroupId);
            boolean removeTestGroupsForUser = true;
            try {
                if (testGroup.getCodeType() !=  LongTestRequest.CODE_TYPE_SOLUTION) {
                    s_trace.error("Unexpected code type for test group. TestGroup dropped");
                    return;
                }
                //Build a response message for the test group
                String resultMessage = buildResponseMessageForTestGroup(testGroup, cnn);

                //If this is the last test group of a set of test groups, we must calculate the overall score
                //for all solutions of the test group set. Remember we have one test group for each solution
                if (userTestGroupDao.findTestGroupCountForUser(userId, cnn) > 1) {
                    int pendingCount = userTestGroupDao.findPendingTestGroupCountForUser(userId, cnn);
                    if (pendingCount == 0 && testGroupId == userTestGroupDao.findLastTestGroupFinishedForUser(userId, cnn)) {
                        StringBuilder testResults = new StringBuilder(resultMessage);
                        testResults .append("\n\n");
                        testResults.append(buildOverallScoreMessage(userId, cnn));
                        resultMessage = testResults.toString();
                    } else {
                        removeTestGroupsForUser = false;
                    }
                }
                getNotificator().notifyAvailableTestResults(new MPSQASTestResult(userId, resultMessage));
            } finally {
                if (removeTestGroupsForUser) {
                    List testGroupIds = userTestGroupDao.findTestGroupIdsForUserAndRemove(userId, cnn);
                    for (Iterator it = testGroupIds.iterator(); it.hasNext();) {
                        Integer id = (Integer) it.next();
                        try {
                            getTestServices().deleteTestGroup(id.intValue());
                        } catch (Exception e) {
                            s_trace.error("Could not delete test group. Will continue..",e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            s_trace.error("Exception caught.", e);
        } finally {
            DBMS.close(cnn);
        }

    }



    /**
     * Builds the message string containing all the information about the test group execution
     *
     * @param testGroup Test group which the message is built for
     * @param cnn Connection to use
     *
     * @return The message built
     *
     * @throws SQLException If a SQLException is thrown during the process
     */
    private String buildResponseMessageForTestGroup(LongTestGroup testGroup, Connection cnn) throws SQLException {
        String handle = getHandleForSolution(testGroup.getSolutionId().intValue(), cnn);
        StringBuilder testResults = new StringBuilder(200);
        testResults.append(handle);
        testResults.append("'s solution: ").append(testGroup.getStatusAsString()).append("\n");
        int testCaseIndex = 1;
        for (Iterator it = testGroup.getTestCases().iterator(); it.hasNext(); testCaseIndex++) {
            LongTestCase testCase = (LongTestCase) it.next();
            LongTestCaseResult result = testCase.getResult();
            testResults.append("For test case ").append(testCaseIndex).append(":\n");
            testResults.append("Args = ").append(ApplicationConstants.makePretty(testCase.getArg())).append("\n");
            testResults.append("Status = ").append(testCase.getStatusAsString()).append("\n");
            if (testCase.getStatus() == LongTestCase.STATUS_COMPLETED) {
                testResults.append("Message = ").append(result.getMessage()).append("\n");
                testResults.append(
                        Formatter.getTestResults(result.getProcessingTime() + " m",
                                                result.getPeakMemoryUsed(),
                                                result.getScore(),
                                                result.getStdout(),
                                                result.getStderr()));
            }
        }
        return testResults.toString();
    }

    /**
     * Calculates and builds the overall score of all test group of the user.
     * It assumes that all test groups are for the same component and
     * each one contains the same test cases
     *
     * @param userId Id of the user associated to the test groups
     * @param cnn Connection to use
     */
    private String buildOverallScoreMessage(int userId, Connection cnn) {
        String OVERALL_SCORE_FAILS = "OVERALL SCORE COULD NOT BE CALCULATED.\n";
        StringBuilder testResults = new StringBuilder(200);
        try {
            //With run tests over all solutions, so we can calculate the overall score for
            //each solution
            int componentId = -1;
            List testGroupIds = userTestGroupDao.findTestGroupIdsForUser(userId, cnn);
            String[] handles =  new String[testGroupIds.size()];
            double[][] scores =  new double[testGroupIds.size()][];
            int index = 0;
            for (Iterator it = testGroupIds.iterator(); it.hasNext(); index++) {
                Integer id = (Integer) it.next();
                LongTestGroup group = getTestServices().findTestGroup(id.intValue());
                handles[index] = getHandleForSolution(group.getSolutionId().intValue(), cnn);
                scores[index] = new double[group.getTestCases().size()];
                componentId = group.getComponentId();
                int testCaseIndex = 0;
                for (Iterator itTestCase = group.getTestCases().iterator(); itTestCase.hasNext(); testCaseIndex++) {
                    LongTestCase testCase = (LongTestCase) itTestCase.next();
                    if (testCase.getStatus() == LongTestCase.STATUS_COMPLETED) {
                        scores[index][testCaseIndex] = testCase.getResult().getScore().doubleValue();
                    } else {
                        scores[index][testCaseIndex] = 0;  //The test was cancelled, it's weird
                    }
                }
            };
            int solutionId = getProblemServices().getPrimarySolutionId(componentId);

            MPSQASFiles result = runMPSQASTest(solutionId, componentId, "score", new Object[] {scores}, cnn);

            if (result.getTestStatus()) {
                double[] overallScore = (double[]) result.getResult();
                testResults.append("OVERALL SCORES\n");
                for (int i = 0; i < handles.length; i++) {
                    testResults.append(handles[i]).append(":  ").append(overallScore[i]).append("\n");
                }
            } else {
                testResults.setLength(0);
                testResults.append(OVERALL_SCORE_FAILS);
                testResults.append("Primary solution failed when calculating the score.\n");
                testResults.append("StdOut: "+result.getStdOut());
                testResults.append("\nStdErr: "+result.getStdErr());
                testResults.append("\nException: "+result.getExceptionText()).append("\n");
            }
        } catch (IllegalArgumentException e) {
            testResults.setLength(0);
            testResults.append(OVERALL_SCORE_FAILS);
            testResults.append(e.getMessage());
        } catch (TesterInvokerException e) {
            testResults.setLength(0);
            testResults.append(OVERALL_SCORE_FAILS);
            testResults.append(e.getMessage());
        } catch (Exception e) {
            testResults.setLength(0);
            testResults.append(OVERALL_SCORE_FAILS);
            testResults.append("Internal server error.\n");
            s_trace.error(OVERALL_SCORE_FAILS, e);
        }
        return testResults.toString();
    }

    /**
     * @see MPSQASServices#cancelTests(int)
     */
    public String cancelTests(int userId) {
        Connection cnn = null;
        try {
            cnn = DBMS.getConnection();
            List testGroupIds = userTestGroupDao.findTestGroupIdsForUserAndRemove(userId, cnn);
            for (Iterator it = testGroupIds.iterator(); it.hasNext();) {
                Integer id = (Integer) it.next();
                try {
                    getTestServices().deleteTestGroup(id.intValue());
                } catch (Exception e) {
                    s_trace.warn("Cannot delete test group: "+id.intValue());
                }
            }
            return "Scheduled tests cancelled";
        } catch (Exception e) {
            s_trace.error("Exception catched. Returning error message", e);
            return ApplicationConstants.SERVER_ERROR;
        } finally {
            DBMS.close(cnn);
        }
    }



    /**
     * <p>
     * the system test.
     * </p>
     * @param componentId the component id.
     * @param userId the user id.
     * @param testType the test type.
     * @see MPSQASServices#systemTests(int, int, int)
     */
    public String systemTests(int componentId, int userId, int testType) {
        Connection cnn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder results = new StringBuilder(200);
        try {
            cnn = DBMS.getConnection();
            if (userTestGroupDao.findTestGroupCountForUser(userId, cnn) > 0 ) {
                results.append("You have scheduled tests!\n");
                results.append("Wait for test completion or cancel them if you prefer.");
                return results.toString();
            }

            ProblemComponent problemComponent = getProblemServices().getProblemComponent(componentId, false);
            if (problemComponent.getComponentTypeID() != ProblemConstants.LONG_COMPONENT) {
                return "Only solutions for long contest problems could be scheduled for system test";
            }

            StringBuilder sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT args ");
            sqlStr.append("FROM system_test_case ");
            sqlStr.append("WHERE component_id = ? ");
            sqlStr.append("ORDER BY test_number, test_case_id ");
            ps = cnn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            rs = ps.executeQuery();

            ArrayList args = new ArrayList(ApplicationConstants.MIN_TEST_CASES);
            while (rs.next()) {
                args.add(((ArrayList) DBMS.getBlobObject(rs, 1)).get(0));
            }
            rs.close();
            ps.close();
            if (args.size() == 0) {
                return "There are no test cases for this problem.";
            }
            String[] testCasesArgs = (String[]) args.toArray(new String[args.size()]);

            sqlStr.setLength(0);
            sqlStr.append("SELECT s.solution_id ");
            sqlStr.append(",cs.primary_solution ");
            sqlStr.append(",u.handle ");
            sqlStr.append("FROM component_solution_xref cs ");
            sqlStr.append(",solution s ");
            sqlStr.append(",user u ");
            sqlStr.append("WHERE cs.component_id = ? ");
            sqlStr.append("AND s.coder_id = u.user_id ");
            sqlStr.append("AND s.solution_id = cs.solution_id ");
            if (testType == MessageConstants.TEST_ONE) {
                sqlStr.append("AND s.solution_id = ? ");
            } else {
                sqlStr.append("AND cs.primary_solution = 0 ");
            }
            ps = cnn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            if (testType == MessageConstants.TEST_ONE) {
                ps.setInt(2,
                        getProblemServices().getSolutionId(componentId, userId));
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                int solutionId = rs.getInt(1);
                int primary = rs.getInt(2);
                String handle = rs.getString(3);

                if (primary != 0) {
                    return "Only tester solution can be scheduled for system test";
                }

                results.append(handle);
                results.append("'s solution:\n");
                results.append(prepareLongTest(userId, componentId, solutionId,
                                        testCasesArgs, cnn));
                results.append("\n");
            }
            results.append(startLongTest(userId, problemComponent.getRoundType(),
                    problemComponent.getProblemCustomSettings(), cnn)).append("\n");
            return results.toString();
        } catch (Exception e) {
            s_trace.error("Exception catched. Returning error message", e);
            return ApplicationConstants.SERVER_ERROR;
        } finally {
            DBMS.close(cnn, ps, rs);
        }
    }

    /**
     * Get the user's handle of the user owning of the  solution
     *
     * @param solutionId Id of the solution
     * @param cnn Connection to access database
     *
     * @return Handle of the user
     *
     * @throws SQLException
     */
    private String getHandleForSolution(int solutionId, Connection cnn) throws SQLException {
        StringBuilder sqlStr = new StringBuilder(256);
        sqlStr.append("SELECT u.handle ");
        sqlStr.append("  FROM solution s, user u ");
        sqlStr.append("  WHERE s.solution_id = ? ");
        sqlStr.append("     AND s.coder_id = u.user_id ");

        ResultSet rs = null;
        PreparedStatement ps = cnn.prepareStatement(sqlStr.toString());
        try {
            ps.setInt(1, solutionId);
            rs = ps.executeQuery();
            rs.next();
            return rs.getString(1);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
        }
    }

    /**
     * Tests a solution with the specified args.  Returns the MPSQASFiles
     * the tester returns.
     */
    public MPSQASFiles test(Object[] args, int componentId, int solutionId,
            String methodName) {
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            return test(args, componentId, solutionId, methodName, conn);
        } catch (Exception e) {
            s_trace.error("Error performing test.", e);
            return null;
        } finally {
            //closeConnection(conn, null);
            close(conn, null, null);
        }
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
     * <p>
     * get component type by component id.
     * </p>
     * @param componentId
     *         the component id.
     * @return the component type of problem 1=Main Individual Problem, 2=Main Long Problem..
     */
    private int getComponentTypeByComponentId(int componentId) {
        SimpleComponent component = CoreServices.getSimpleComponent(componentId);
        if(component!=null)
            return component.getComponentTypeID();
        return ProblemConstants.LONG_COMPONENT;
    }
    
    /**
     * Tests a solution with the specified args.  Returns the MPSQASFiles
     * the tester returns.
     *
     * @param args
     *        the arguments of mpsqas files.
     * @param componentId
     *        the component id.
     * @param solutionId
     *        the solution id.
     * @param methodName
     *        the method name.
     * @param conn
     *        the jdbc connection.
     * @return the mpsqas file with test result.
     */
    public MPSQASFiles test(Object[] args, int componentId, int solutionId,
            String methodName, Connection conn) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder testResults = new StringBuilder(256);
        MPSQASFiles mfiles = new MPSQASFiles();

        try {
            int roundType = getRoundTypeByComponentId(componentId);
            
            StringBuilder sqlStr = new StringBuilder(256);

            ArrayList argTypes = new ArrayList();
            ArrayList argValues = new ArrayList();

            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT p.data_type_id ");
            sqlStr.append(",dt.data_type_desc ");
            sqlStr.append(",p.sort_order ");
            sqlStr.append("FROM parameter p JOIN data_type dt ");
            sqlStr.append("ON p.data_type_id = dt.data_type_id ");
            sqlStr.append("WHERE p.component_id = ? ");
            sqlStr.append("ORDER BY p.sort_order ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            rs = ps.executeQuery();
            while (rs.next()) {
                argTypes.add(getDataType(rs.getString(2)));
            }

            if (argTypes.size() == args.length) {
                argValues = new ArrayList(Arrays.asList(args));

                sqlStr = new StringBuilder(256);
                sqlStr.append("SELECT c.class_name ");
                sqlStr.append(",c.method_name ");
                sqlStr.append(",dt.data_type_desc ");
                sqlStr.append(",c.component_type_id ");
                sqlStr.append("FROM component c ");
                sqlStr.append(",data_type dt ");
                sqlStr.append("WHERE c.component_id = ? ");
                sqlStr.append("AND c.result_type_id = dt.data_type_id ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, componentId);
                rs = ps.executeQuery();
                rs.next();

                String className = rs.getString(1);
                String resultType = rs.getString(3);
                int componentType = rs.getInt(4);

                mfiles.setArgTypes(argTypes);
                mfiles.setArgVals(argValues);
                mfiles.setResultType(getDataType(resultType));
                mfiles.setClassName(className);

                sqlStr = new StringBuilder(256);
                sqlStr.append("SELECT u.handle");
                sqlStr.append(",s.language_id ");
                sqlStr.append("FROM component_solution_xref cs ");
                sqlStr.append(",solution s ");
                sqlStr.append(",user u ");
                sqlStr.append("WHERE cs.component_id = ? ");
                sqlStr.append("AND s.coder_id = u.user_id ");
                sqlStr.append("AND s.solution_id = cs.solution_id ");
                sqlStr.append("AND s.solution_id = ? ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, componentId);
                ps.setInt(2, solutionId);
                rs = ps.executeQuery();

                String packageName;
                String fileName;
                HashMap classFiles;
                int language;
                //int primary;
                String handle;
                ResultSet rs2;
                sqlStr = new StringBuilder(256);
                sqlStr.append("SELECT path ");
                sqlStr.append(",class_file ");
                sqlStr.append(",sort_order ");
                sqlStr.append("FROM solution_class_file ");
                sqlStr.append("WHERE solution_id = ? ");
                sqlStr.append("ORDER BY sort_order ");
                ps = conn.prepareStatement(sqlStr.toString());

                if (rs.next()) {
                    handle = rs.getString(1);
                    language = rs.getInt(2);
                    packageName = ServicesConstants.SOLUTIONS_PACKAGE + "s"
                            + solutionId;
                    testResults.append(handle);
                    testResults.append("'s solution:\n");

                    classFiles = new HashMap();
                    ps.setInt(1, solutionId);
                    rs2 = ps.executeQuery();
                    if (rs2.next()) {
                        do {
                            fileName = rs2.getString(1);
                            if (language == ContestConstants.JAVA) {
                                //fileName is package + class for java, remove ".class" too
                                fileName = fileName.substring(0, fileName.lastIndexOf("."))
                                        .replace('/', '.');
                            }
                            debug("fileName = " + fileName);
                            classFiles.put(fileName, rs2.getBytes(2));
                        } while (rs2.next());

                        mfiles.setClassFiles(classFiles);
                        mfiles.setPackageName(packageName);
                        mfiles.setLanguage(language);
                        mfiles.setSolutionId(solutionId);
                        debug("Testing " + methodName);
                        mfiles.setMethodName(methodName);
                        mfiles.setRoundType(roundType);
                        SimpleComponent component = CoreServices.getSimpleComponent(componentId);
                        mfiles.setProblemCustomSettings(component.getProblemCustomSettings());
                        //add the component type.
                        mfiles.setComponentType(componentType);
                        mfiles = getMPSQASTester().mpsqasTest(mfiles);
                    } else {
                        testResults.append("(no compiled files to test)");
                    }
                } else {
                    testResults.append("No compiled solution to test.");
                }
            } else {
                testResults.append("Number of argument values does not equal number ");
                testResults.append("number of argument types, try saving problem.");
            }

            if (testResults.length() != 0) {
                mfiles.setExceptionText(testResults.toString());
            }
        } catch (TesterInvokerException te) {
            s_trace.error("Error testing.", te);
            testResults.append(te.getMessage());
            mfiles.setExceptionText(testResults.toString());
            mfiles.setTestStatus(false);
        } finally {
            close (null, ps, rs);
        }
        return mfiles;
    }

    /**
     * Tests a solution method with mutiple argument sets and returns
     * an array of all MPSQASFiles returned by the tester.
     */
    public MPSQASFiles[] test(Object[][] args, int componentId,
            int solutionId, String methodName) {
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            return test(args, componentId, solutionId, methodName, conn);
        } catch (Exception e) {
            s_trace.error("Error performing test.", e);
        } finally {
            //closeConnection(conn, null);
            close(conn, null, null);
        }
        return null;
    }

    /**
     * Tests a solution method with mutiple argument sets and returns
     * an array of all MPSQASFiles returned by the tester.
     *
     * @param args
     *        the arguments of mpsqas files.
     * @param componentId
     *        the component id.
     * @param solutionId
     *        the solution id.
     * @param methodName
     *        the method name.
     * @param conn
     *        the jdbc connection.
     * @return the mpsqas file with test result.
     */
    public MPSQASFiles[] test(Object[][] args, int componentId,
            int solutionId, String methodName, Connection conn) {
        s_trace.debug("In MPSQASServicesBean.test()..");
        PreparedStatement ps = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        MPSQASFiles mfiles = null;
        MPSQASFiles[] mfilesArray = new MPSQASFiles[args.length];

        try {
            int roundType = getRoundTypeByComponentId(componentId);
            StringBuilder sqlStr = new StringBuilder(256);

            ArrayList argTypes = new ArrayList();
            ArrayList argValues = new ArrayList();

            //get arg type Strings
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT p.data_type_id ");
            sqlStr.append(",dt.data_type_desc ");
            sqlStr.append(",p.sort_order ");
            sqlStr.append("FROM parameter p JOIN data_type dt ");
            sqlStr.append("ON p.data_type_id = dt.data_type_id ");
            sqlStr.append("WHERE p.component_id = ? ");
            sqlStr.append("ORDER BY p.sort_order ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            rs = ps.executeQuery();
            while (rs.next()) {
                argTypes.add(new DataType(rs.getInt(1), rs.getString(2)));
            }

            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT c.class_name ");
            sqlStr.append(",c.method_name ");
            sqlStr.append(",dt.data_type_desc ");
            sqlStr.append(",c.component_type_id ");
            sqlStr.append("FROM component c ");
            sqlStr.append(",data_type dt ");
            sqlStr.append("WHERE c.component_id = ? ");
            sqlStr.append("AND c.result_type_id = dt.data_type_id ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            rs = ps.executeQuery();
            rs.next();

            String className = rs.getString(1);
            String resultType = rs.getString(3);
            int componentType = rs.getInt(4);

            mfiles = new MPSQASFiles();
            mfiles.setArgTypes(argTypes);
            mfiles.setResultType(getDataType(resultType));
            mfiles.setClassName(className);

            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT u.handle");
            sqlStr.append(",s.language_id ");
            sqlStr.append("FROM component_solution_xref cs ");
            sqlStr.append(",solution s ");
            sqlStr.append(",user u ");
            sqlStr.append("WHERE cs.component_id = ? ");
            sqlStr.append("AND s.coder_id = u.user_id ");
            sqlStr.append("AND s.solution_id = cs.solution_id ");
            sqlStr.append("AND s.solution_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            ps.setInt(2, solutionId);
            rs = ps.executeQuery();

            String packageName;
            String fileName;
            HashMap classFiles;
            //String handle;
            int language;
            //int primary;

            //ResultSet rs2;
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT path ");
            sqlStr.append(",class_file ");
            sqlStr.append(",sort_order ");
            sqlStr.append("FROM solution_class_file ");
            sqlStr.append("WHERE solution_id = ? ");
            sqlStr.append("ORDER BY sort_order ");
            ps = conn.prepareStatement(sqlStr.toString());

            if (rs.next()) {
                //handle = rs.getString(1);
                language = rs.getInt(2);
                packageName = ServicesConstants.SOLUTIONS_PACKAGE + "s"
                        + solutionId;

                classFiles = new HashMap();
                ps.setInt(1, solutionId);
                rs2 = ps.executeQuery();
                if (rs2.next()) {
                    do {
                        fileName = rs2.getString(1);
                        if (language == ContestConstants.JAVA) {
                            //fileName is package + class for java, remove ".class" too
                            fileName = fileName.substring(0, fileName.lastIndexOf("."))
                                    .replace('/', '.');
                        }
                        debug("fileName = " + fileName);
                        classFiles.put(fileName, rs2.getBytes(2));
                    } while (rs2.next());

                    mfiles.setClassFiles(classFiles);
                    mfiles.setPackageName(packageName);
                    mfiles.setLanguage(language);
                    mfiles.setMethodName(methodName);
                    mfiles.setSolutionId(solutionId);
                    mfiles.setRoundType(roundType);
                    //add the component type.
                    mfiles.setComponentType(componentType);
                    SimpleComponent component = CoreServices.getSimpleComponent(componentId);
                    mfiles.setProblemCustomSettings(component.getProblemCustomSettings());
                    
                    //boolean go;
                    s_trace.debug("About to enter test loop...");
                    for (int i = 0; i < args.length; i++) {
                        s_trace.debug("In loop, i = " + i);
                        argValues = new ArrayList(Arrays.asList(args[i]));

                        s_trace.debug("About to test with argValues=" + argValues);
                        mfiles.setArgVals(argValues);
                        try {
                            mfilesArray[i] = getMPSQASTester().mpsqasTest(mfiles);
                        } catch (Exception e) {
                            s_trace.error("Error performing test.", e);
                            mfilesArray[i] = new MPSQASFiles();
                            mfilesArray[i].setTestStatus(false);
                            mfilesArray[i].setExceptionText("Test failed.");
                        }
                    }
                } else {
                    for (int i = 0; i < args.length; i++) {
                        mfilesArray[i] = new MPSQASFiles();
                        mfilesArray[i].setTestStatus(false);
                        mfilesArray[i].setExceptionText("No compiled files to test.");
                    }
                }
            } else {
                for (int i = 0; i < args.length; i++) {
                    mfilesArray[i] = new MPSQASFiles();
                    mfilesArray[i].setTestStatus(false);
                    mfilesArray[i].setExceptionText("No compiled solution to test.");
                }
            }
        } catch (Exception e) {
            s_trace.error("Error testing.", e);
            for (int i = 0; i < args.length; i++) {
                mfilesArray[i] = new MPSQASFiles();
                mfilesArray[i].setTestStatus(false);
                mfilesArray[i].setExceptionText("Exception during testing.");
            }
        } finally {
            close(null, null, rs2);
            close(null, ps, rs);
        }

        return mfilesArray;
    }

    /**
     * Runs all the solutions to a problem through the test cases and compares the
     * results to ensure they are all correct.  Returns an ArrayList who's
     * first element is a boolean as to whether the solutions
     * always agree, and the second element is a String showing the results.
     *
     * @param componentId The problem for which to compare the results.
     * @return the compile solutioin result.
     */
    public ArrayList compareSolutions(int componentId) {
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder compareResults = new StringBuilder(256);
        ArrayList results = new ArrayList(2);
        StringBuilder sqlStr = null;
        int i, j;

        try {
            conn = DBMS.getConnection();
            int roundType = getRoundTypeByComponentId(componentId);

            //get the test cases information
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT args ");
            sqlStr.append(",expected_result ");
            sqlStr.append(",test_case_id ");
            sqlStr.append("FROM system_test_case ");
            sqlStr.append("WHERE component_id = ? ");
            sqlStr.append("ORDER BY test_number, test_case_id ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            rs = ps.executeQuery();

            ArrayList args = new ArrayList(ApplicationConstants.MIN_TEST_CASES);
            ArrayList expected = new ArrayList(ApplicationConstants.MIN_TEST_CASES);

            while (rs.next()) {
                args.add(((ArrayList) DBMS.getBlobObject(rs, 1)).toArray());
                expected.add(DBMS.getBlobObject(rs, 2));
            }
            rs.close();
            ps.close();

            if (args.size() == 0) {
                compareResults.append("There are no test cases for this problem.");
            }

            //get some problem information
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT c.class_name ");
            sqlStr.append(",c.method_name ");
            sqlStr.append(",dt.data_type_desc ");
            sqlStr.append(",c.component_type_id ");
            sqlStr.append("FROM component c ");
            sqlStr.append(",data_type dt ");
            sqlStr.append("WHERE c.component_id = ? ");
            sqlStr.append("AND c.result_type_id = dt.data_type_id ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            rs = ps.executeQuery();
            rs.next();

            String className = rs.getString(1);
            String methodName = rs.getString(2);
            String resultType = rs.getString(3);
            int componentType = rs.getInt(4);
            
            rs.close();
            ps.close();

            //get arg type Strings
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT p.data_type_id ");
            sqlStr.append(",dt.data_type_desc ");
            sqlStr.append(",p.sort_order ");
            sqlStr.append("FROM parameter p JOIN data_type dt ");
            sqlStr.append("ON p.data_type_id = dt.data_type_id ");
            sqlStr.append("WHERE p.component_id = ? ");
            sqlStr.append("ORDER BY p.sort_order ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            rs = ps.executeQuery();

            ArrayList argTypes = new ArrayList();
            while (rs.next()) {
                argTypes.add(new DataType(rs.getInt(1), rs.getString(2)));
            }
            rs.close();
            ps.close();

            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT s.solution_id ");
            sqlStr.append(",cs.primary_solution ");
            sqlStr.append(",u.handle ");
            sqlStr.append(",s.language_id ");
            sqlStr.append("FROM component_solution_xref cs ");
            sqlStr.append(",solution s ");
            sqlStr.append(",user u ");
            sqlStr.append("WHERE cs.component_id = ? ");
            sqlStr.append("AND s.coder_id = u.user_id ");
            sqlStr.append("AND s.solution_id = cs.solution_id ");
            sqlStr.append("ORDER BY cs.primary_solution DESC");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, componentId);
            rs = ps.executeQuery();

            String packageName;
            String fileName;
            HashMap classFiles;
            int language;
            int solutionId;
            int primary;
            String handle;
            ResultSet rs2;
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT path ");
            sqlStr.append(",class_file ");
            sqlStr.append(",sort_order ");
            sqlStr.append("FROM solution_class_file ");
            sqlStr.append("WHERE solution_id = ? ");
            sqlStr.append("ORDER BY sort_order ");
            ps = conn.prepareStatement(sqlStr.toString());

            ArrayList al_mpsqasFiles = new ArrayList();
            MPSQASFiles[] mpsqasFiles;
            //MPSQASFiles[] resultMpsqasFiles;
            MPSQASFiles mfiles;
            ArrayList primarys = new ArrayList();
            ArrayList handles = new ArrayList();

            while (rs.next()) {
                solutionId = rs.getInt(1);
                primary = rs.getInt(2);
                handle = rs.getString(3);
                language = rs.getInt(4);
                packageName = ServicesConstants.SOLUTIONS_PACKAGE + "s"
                        + solutionId;

                handles.add(handle);
                primarys.add(new Boolean(primary ==
                        ApplicationConstants.PRIMARY_SOLUTION));

                mfiles = new MPSQASFiles();
                mfiles.setArgTypes(argTypes);
                mfiles.setResultType(getDataType(resultType));
                mfiles.setClassName(className);
                mfiles.setMethodName(methodName);
                mfiles.setRoundType(roundType);
                //add the component type.
                mfiles.setComponentType(componentType);
                SimpleComponent component = CoreServices.getSimpleComponent(componentId);
                mfiles.setProblemCustomSettings(component.getProblemCustomSettings());

                classFiles = new HashMap();
                ps.setInt(1, solutionId);
                rs2 = ps.executeQuery();
                while (rs2.next()) {
                    fileName = rs2.getString(1);
                    if (language == ContestConstants.JAVA) {
                        //fileName is package + class for java, remove ".class" too
                        fileName = fileName.substring(0, fileName.lastIndexOf("."))
                                .replace('/', '.');
                    }
                    debug("fileName = " + fileName);
                    classFiles.put(fileName, rs2.getBytes(2));
                }

                mfiles.setClassFiles(classFiles);
                mfiles.setPackageName(packageName);
                mfiles.setLanguage(language);
                mfiles.setSolutionId(solutionId);
                al_mpsqasFiles.add(mfiles);
            }

            mpsqasFiles = new MPSQASFiles[al_mpsqasFiles.size()];
            for (i = 0; i < al_mpsqasFiles.size(); i++) {
                mpsqasFiles[i] = (MPSQASFiles) al_mpsqasFiles.get(i);
            }

            boolean agree;
            boolean alwaysAgree = true;
            FarmSolutionInvokator solutionInvokator =
                    new FarmSolutionInvokator(getTestServices().getComponentSolution(componentId));
            for (i = 0; i < args.size(); i++) {
                compareResults.append("For test case " + i + ":\n");
                compareResults.append("Args     = "
                        + ApplicationConstants.makePretty(args.get(i)) + "\n");
                compareResults.append("Expected = "
                        + ApplicationConstants.makePretty(expected.get(i)) + "\n");

                agree = true;
                for (j = 0; j < mpsqasFiles.length; j++) {
                    mpsqasFiles[j].setArgVals(new ArrayList(Arrays.asList(
                            (Object[]) args.get(i))));
                    mfiles = getMPSQASTester().mpsqasTest(mpsqasFiles[j]);
                    String comparisonResult = "";
                    if (mfiles.getTestStatus()) {
                        comparisonResult = solutionInvokator.compare(
                                (Object[]) args.get(i), expected.get(i), mfiles.getResult());
                    }
                    if (!mfiles.getTestStatus() || comparisonResult.length() > 0) {
                        agree = alwaysAgree = false;
                        if (Boolean.TRUE.equals(primarys.get(j))) {
                            compareResults.append(
                                    "\nPRIMARY SOLUTION DOES NOT AGREE WITH EXPECTED RESULT:");
                        }
                        compareResults.append("\n" + handles.get(j)
                                + "'s solution did not agree.  It returns:\n");
                        if (mfiles.getTestStatus()) {
                            compareResults.append(ApplicationConstants.makePretty(
                                    mfiles.getResult()));
                            compareResults.append("\nAnswer check result:\n" + comparisonResult);
                        } else {
                            compareResults.append("(null)");
                        }
                    }
                }

                if (agree) {
                    compareResults.append("All solutions agree.");
                }
                compareResults.append("\n\n");
            }

            if (args.size() > 0) {
                if (alwaysAgree) {
                    compareResults.insert(0,
                            "All solutions always agree for all test cases:\n\n");
                    compareResults.append(
                            "All solutions always agree for all test cases.");
                    results.add(Boolean.TRUE);
                } else {
                    compareResults.insert(0, "SOLUTIONS DISAGREE:\n\n");
                    compareResults.append("SOLUTIONS DISAGREE");
                    results.add(Boolean.FALSE);
                }
            } else {
                results.add(Boolean.FALSE);
            }
        } catch (Exception e) {
            s_trace.error("Error comparing results for problem " + componentId, e);
            e.printStackTrace();
            results = new ArrayList();
            results.add(Boolean.FALSE);
            compareResults.insert(0, ApplicationConstants.SERVER_ERROR + "\n\n");
        } finally {
            //closeConnection(conn, ps, rs);
            close(conn, ps, rs);
        }

        results.add(compareResults.toString());
        return results;
    }

    /******************************************************************************
     * Contest Services                                                           *
     ******************************************************************************/

    /**
     * Returns an ArrayList of upcoming contests that the user_id has permission
     * to view.
     *
     * @param userId The userId of the user trying to view contests, or -1 for
     * all contests
     */
    public ArrayList getContests(int userId) {
        ArrayList contestTable = new ArrayList();

        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ResultSet rs2 = null;

        try {
            conn = DBMS.getConnection();

            StringBuilder sqlStr = new StringBuilder(256);

            sqlStr.append("SELECT * FROM group_user WHERE group_id=? AND user_id=?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, ApplicationConstants.ADMIN_GROUP);
            ps.setInt(2, userId);
            rs = ps.executeQuery();

            boolean admin = false;

            if (rs.next() || userId < 0) {
                admin = true;
                sqlStr = new StringBuilder(256);
                sqlStr.append("SELECT c.name ");
                sqlStr.append(",r.name ");
                sqlStr.append(",rs.start_time ");
                sqlStr.append(",r.round_id ");
                sqlStr.append("FROM contest c ");
                sqlStr.append(",round r ");
                sqlStr.append(",round_segment rs ");
                sqlStr.append("WHERE c.contest_id = r.contest_id ");
                sqlStr.append("AND r.round_id = rs.round_id ");
                sqlStr.append("AND rs.segment_id = ? ");
                sqlStr.append("AND rs.start_time > ? ");
                sqlStr.append("ORDER BY rs.start_time ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, ApplicationConstants.CODING_SEGMENT);
                ps.setTimestamp(2, new Timestamp(
                        System.currentTimeMillis() - ApplicationConstants.DISPLAY_OLD_CONTEST));
            } else {
                sqlStr = new StringBuilder(256);
                sqlStr.append("SELECT c.name ");
                sqlStr.append(",r.name ");
                sqlStr.append(",rs.start_time ");
                sqlStr.append(",r.round_id ");
                sqlStr.append("FROM contest c ");
                sqlStr.append(",round r ");
                sqlStr.append(",round_segment rs ");
                sqlStr.append("WHERE c.contest_id = r.contest_id ");
                sqlStr.append("AND rs.round_id = r.round_id ");
                sqlStr.append("AND rs.segment_id = ? ");
                sqlStr.append("AND rs.start_time > ? ");
                sqlStr.append("AND r.round_id IN (SELECT DISTINCT round_id ");
                sqlStr.append("FROM round_component ");
                sqlStr.append("WHERE component_id IN (SELECT DISTINCT component_id ");
                sqlStr.append("FROM component_user_xref ");
                sqlStr.append("WHERE user_id = ?)) ");
                sqlStr.append("ORDER BY rs.start_time");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, ApplicationConstants.CODING_SEGMENT);
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()
                        - ApplicationConstants.DISPLAY_OLD_CONTEST));
                ps.setInt(3, userId);
            }

            rs = ps.executeQuery();

            //find out if user is writer / tester of a contest
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT DISTINCT cu.user_type_id ");
            sqlStr.append("FROM component_user_xref cu ");
            sqlStr.append("WHERE cu.component_id IN (SELECT component_id ");
            sqlStr.append("FROM round_component ");
            sqlStr.append("WHERE round_id = ?) ");
            sqlStr.append("AND cu.user_id=?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(2, userId);

            int count;
            String role;
            ContestInformation contestInfo;

            while (rs.next()) {
                contestInfo = new ContestInformation();

                if (userId >= 0) {
                    contestInfo.setContestName(rs.getString(1) + ", " + rs.getString(2));
                    contestInfo.setStartCoding(rs.getTimestamp(3).toString());

                    //get user's role
                    ps.setInt(1, rs.getInt(4));
                    rs2 = ps.executeQuery();
                    count = 0;
                    role = "";
                    if (admin) {
                        role += "Admin";
                        count++;
                    }
                    while (rs2.next()) {
                        if (count > 0) role += " & ";
                        switch (rs2.getInt(1)) {
                        case ApplicationConstants.PROBLEM_TESTER:
                            role += "Tester";
                            break;
                        case ApplicationConstants.PROBLEM_WRITER:
                            role += "Writer";
                            break;
                        default:
                            role += "Unknown(" + rs2.getInt(1) + ")";
                        }
                    }

                    contestInfo.setRole(role);
                }
                contestInfo.setRoundId(rs.getInt(4));
                contestTable.add(contestInfo);
            }
        } catch (Exception e) {
            s_trace.error("Error getting contest table:", e);
        } finally {
            close(null, null, rs2);
            close(conn, ps, rs);
        }
        //closeConnection(conn, ps);
        return contestTable;
    }

    /**
     * Gets all the contest information about a round from the database and
     * fills out a * ContestInformation instance. Returns the ContestInformation
     * about the contest.
     *
     * @param roundId  The round for which to get information
     * @param userId The user getting the info... makes sure he has permission
     */
    public ContestInformation getContestInformation(int roundId, int userId) {

        Connection conn = null;
        PreparedStatement ps = null;
        ContestInformation contestInformation = new ContestInformation();
        StringBuilder sqlStr = null;
        ResultSet rs = null;

        try {
            conn = DBMS.getConnection();

            if (!isAdmin(userId)) {
                sqlStr = new StringBuilder(256);
                sqlStr.append("SELECT rc.component_id ");
                sqlStr.append("FROM round_component rc ");
                sqlStr.append("WHERE rc.component_id IN ");
                sqlStr.append("(SELECT component_id ");
                sqlStr.append("FROM component_user_xref ");
                sqlStr.append("WHERE user_id = ?) ");
                sqlStr.append("AND rc.round_id = ? ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, userId);
                ps.setInt(2, roundId);
                rs = ps.executeQuery();
                if (!rs.next()) {  //user shouldn't be viewing this contest
                    return null;
                }
                rs.close();
                ps.close();
            }

            //get the contest / round name
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT c.name AS contest_name ");
            sqlStr.append(",r.name AS round_name ");
            sqlStr.append(",r.round_type_id AS round_type ");
            sqlStr.append("FROM contest c ");
            sqlStr.append(",round r ");
            sqlStr.append("WHERE r.round_id = ? ");
            sqlStr.append("AND r.contest_id = c.contest_id ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            rs = ps.executeQuery();

            if (!rs.next()) {
                throw new Exception("No contest/round scheduled for requested round: "
                        + roundId);
            }

            contestInformation.setContestName(rs.getString("contest_name"));
            contestInformation.setRoundName(rs.getString("round_name"));
            //int roundType = rs.getInt("round_type");
            rs.close();
            ps.close();

            //get times
            sqlStr = new StringBuilder();
            sqlStr.append("SELECT start_time AS start_time ");
            sqlStr.append(",end_time AS end_time ");
            sqlStr.append("FROM round_segment ");
            sqlStr.append("WHERE round_id = ? ");
            sqlStr.append("AND segment_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());

            //coding phase time
            ps.setInt(1, roundId);
            ps.setInt(2, ApplicationConstants.CODING_SEGMENT);
            rs = ps.executeQuery();

            if (!rs.next()) {
                debug("No coding segment scheduled for round " + roundId);
                contestInformation.setStartCoding("NOT SCHEDULED");
                contestInformation.setEndCoding("NOT SCHEDULED");
            } else {
                contestInformation.setStartCoding(rs.getTimestamp("start_time")
                        .toString());
                contestInformation.setEndCoding(rs.getTimestamp("end_time")
                        .toString());
            }
            rs.close();

            //challenge phase time
            ps.setInt(2, ApplicationConstants.CHALLENGE_SEGMENT);
            rs = ps.executeQuery();

            if (!rs.next()) {
                debug("No challenge segment scheduled for round " + roundId);
                contestInformation.setStartChallenge("NOT SCHEDULED");
                contestInformation.setEndChallenge("NOT SCHEDULED");
            } else {
                contestInformation.setStartChallenge(rs.getTimestamp("start_time")
                        .toString());
                contestInformation.setEndChallenge(rs.getTimestamp("end_time")
                        .toString());
            }
            rs.close();
            ps.close();

            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT u.handle AS handle ");
            sqlStr.append(",cu.user_type_id AS type ");
            sqlStr.append("FROM user u ");
            sqlStr.append(",component_user_xref cu ");
            sqlStr.append("WHERE u.user_id = cu.user_id ");
            sqlStr.append("AND cu.component_id IN (SELECT component_id ");
            sqlStr.append("FROM round_component WHERE round_id = ?) ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            rs = ps.executeQuery();

            String handle;
            int type;
            ArrayList writers = new ArrayList();
            ArrayList testers = new ArrayList();
            while (rs.next()) {
                handle = rs.getString("handle");
                type = rs.getInt("type");
                if (type == ApplicationConstants.PROBLEM_WRITER
                        && !writers.contains(handle))
                    writers.add(handle);
                if (type == ApplicationConstants.PROBLEM_TESTER
                        && !testers.contains(handle))
                    testers.add(handle);
            }
            contestInformation.setProblemWriters(writers);
            contestInformation.setProblemTesters(testers);

            ArrayList singleProblems =
                    getProblemServices().getSingleProblems(
                            MessageConstants.SCHED_PROBLEMS_FOR_CONTEST, roundId);
            contestInformation.setSingleProblems(singleProblems);

            ArrayList teamProblems =
                    getProblemServices().getTeamProblems(
                            MessageConstants.SCHED_PROBLEMS_FOR_CONTEST, roundId);
            contestInformation.setTeamProblems(teamProblems);

            ArrayList longProblems =
                    getProblemServices().getLongProblems(
                            MessageConstants.SCHED_PROBLEMS_FOR_CONTEST, roundId);
            contestInformation.setLongProblems(longProblems);
        } catch (Exception e) {
            s_trace.error("Error retrieving contest information for round "
                    + roundId, e);
            contestInformation = null;
        } finally {
            //closeConnection(conn, ps, rs);
            close(conn, ps, rs);
        }

        return contestInformation;
    }

    /**
     * Sets the status of any problems in the specified round to false.
     * Reconcile payments.  (And any other post contest matters can be added
     * to this)
     *
     * @param roundId The round id of the contest to work with.
     */
    public void wrapUpContest(int roundId) {
        if (VERBOSE) s_trace.debug("Wrapping up contest " + roundId);

        if (roundId < ApplicationConstants.REAL_CONTEST_ID_LOWER_BOUND) {
            //either admin test or practice room, ignore.
            return;
        }

        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBMS.getConnection();
            StringBuilder sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT p.problem_id ");
            sqlStr.append("FROM problem p, component c, round_component rc ");
            sqlStr.append("WHERE p.problem_id = c.problem_id ");
            sqlStr.append("AND c.component_id = rc.component_id ");
            sqlStr.append("AND rc.round_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            rs = ps.executeQuery();

            ArrayList ids = new ArrayList();
            while (rs.next()) ids.add(new Integer(rs.getInt(1)));
            rs.close();
            ps.close();

            //Update status to used of all problems used
            sqlStr = new StringBuilder();
            sqlStr.append("UPDATE problem ");
            sqlStr.append("SET status_id = ? ");
            sqlStr.append("WHERE problem_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, StatusConstants.USED);
            int update = 0;
            for (int i = 0; i < ids.size(); i++) {
                ps.setInt(2, ((Integer) ids.get(i)).intValue());
                update += ps.executeUpdate();
            }
            debug(update + " problems changed to USED in wrapping up " + roundId);
        } catch (Exception e) {
            s_trace.error("Error wraping up " + roundId, e);
        } finally {
            //closeConnection(conn, ps, rs);
            close(conn, ps, rs);
        }
    }

    /*****************************************************************************
     * Web Service Services                                                      *
     *****************************************************************************/

    /**
     * Returns a populated WebServiceInformation object for use by MPSQAS.
     */
    public WebServiceInformation getWebServiceInformation(int webServiceId,
            int userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr;
        WebServiceInformation webService = null;

        try {
            conn = DBMS.getConnection();

            //first get the user type of the web service
            int userType = -1;
            if (isAdmin(userId)) {
                userType = ApplicationConstants.PROBLEM_ADMIN;
            } else {
                sqlStr = new StringBuilder(256);
                sqlStr.append("SELECT problem_id ");
                sqlStr.append("FROM problem_web_service_xref ");
                sqlStr.append("WHERE web_service_id = ? ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, webServiceId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    userType = getProblemServices().getUserTypeForProblem(
                            rs.getInt("problem_id"), userId);
                }
                if (userType == -1) {
                    throw new Exception("User (" + userId + ") requesting a web service he " +
                            "doesn't have permission to view.");
                }
            }

            webService = new WebServiceInformation();
            webService.setUserType(userType);

            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT w.web_service_name AS name ");
            sqlStr.append(",pw.problem_id AS problem_id ");
            sqlStr.append("FROM web_service w ");
            sqlStr.append(",problem_web_service_xref pw ");
            sqlStr.append("WHERE w.web_service_id = pw.web_service_id ");
            sqlStr.append("AND w.web_service_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, webServiceId);
            rs = ps.executeQuery();
            rs.next();
            webService.setName(rs.getString("name"));
            webService.setProblemId(rs.getInt("problem_id"));
            webService.setWebServiceId(webServiceId);

            List files = getWebServiceServer(webServiceId);
            WebServiceRemoteFile file;
            for (int i = 0; i < files.size(); i++) {
                file = (WebServiceRemoteFile) files.get(i);
                //substrings strip out the ".java"
                if (file.getType() == WebServiceRemoteFile.WEB_SERVICE_INTERFACE) {
                    webService.setInterfaceClass(
                            file.getName().substring(0, file.getName().lastIndexOf(".")));
                } else if (file.getType() ==
                        WebServiceRemoteFile.WEB_SERVICE_IMPLEMENTATION) {
                    webService.setImplementationClass(
                            file.getName().substring(0, file.getName().lastIndexOf(".")));
                } else {
                    debug("setting as helper class.");
                    webService.getHelperClasses().add(
                            file.getName().substring(0, file.getName().lastIndexOf(".")));
                }
                webService.setSource(file.getName(), new String(file.getContents()));
            }
        } catch (Exception e) {
            s_trace.error("Exception getting web service.", e);
            webService = null;
        } finally {
            //closeConnection(conn, ps);
            close(conn, ps, rs);
        }

        return webService;
    }

    /**
     *  Checks if the specified user has permission to deploy the specifed
     *  web service.
     */
    public boolean hasPermissionToDeploy(int webServiceId, int userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = null;
        boolean hasPermission = false;

        try {
            conn = DBMS.getConnection();

            //first make sure the user has permission to deploy the web service
            if (isAdmin(userId)) {
                hasPermission = true;
            } else {
                sqlStr = new StringBuilder(256);
                sqlStr.append("SELECT problem_id ");
                sqlStr.append("FROM problem_web_service_xref ");
                sqlStr.append("WHERE web_service_id = ? ");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, webServiceId);
                rs = ps.executeQuery();
                int userType = -1;
                if (rs.next()) {
                    userType = getProblemServices().getUserTypeForProblem(
                            rs.getInt("problem_id"), userId);
                }
                if (userType == ApplicationConstants.PROBLEM_WRITER) {
                    hasPermission = true;
                }
            }
        } catch (Exception e) {
            s_trace.error("Error checking permission.", e);
        } finally {
            //closeConnection(conn, ps);
            close(conn, ps, rs);
        }
        return hasPermission;
    }

    /**
     * Returns a list of WebServiceRemoteFile containing the source for
     * the web service (the interface, implementation, and helper classes.
     */
    public List getWebServiceServer(int webServiceId) throws RemoteException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = null;
        ArrayList files = new ArrayList();

        try {
            conn = DBMS.getConnection();

            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT web_service_file_type_id AS file_type ");
            sqlStr.append(",path AS path ");
            sqlStr.append(",source AS source ");
            sqlStr.append(",language_id AS lang ");
            sqlStr.append("FROM web_service_source_file ");
            sqlStr.append("WHERE web_service_id = ? ");
            sqlStr.append("AND language_id = ? ");
            sqlStr.append("AND web_service_file_type_id IN (?, ?, ?) ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, webServiceId);
            ps.setInt(2, ContestConstants.JAVA);
            ps.setInt(3, WebServiceRemoteFile.WEB_SERVICE_INTERFACE);
            ps.setInt(4, WebServiceRemoteFile.WEB_SERVICE_IMPLEMENTATION);
            ps.setInt(5, WebServiceRemoteFile.WEB_SERVICE_USER_HELPER);
            rs = ps.executeQuery();
            while (rs.next()) {
                files.add(new WebServiceRemoteFile(rs.getString("path"),
                        rs.getBytes("source"),
                        rs.getInt("file_type"),
                        rs.getInt("lang")));
            }
        } catch (Exception e) {
            s_trace.error("Error getting web service server.", e);
            files = null;
        } finally {
            //closeConnection(conn, ps);
            close(conn, ps, rs);
        }

        return files;
    }

    /**
     * Sets the web service server files for the specified web service.
     */
    public boolean setWebServiceServer(int webServiceId, List webServiceFiles) {
        boolean success = true;
        Connection conn = null;
        PreparedStatement ps = null;
        //ResultSet rs = null;
        StringBuilder sqlStr = null;

        try {
            conn = DBMS.getConnection();

            clearWebServiceServer(webServiceId, conn);

            sqlStr = new StringBuilder(256);
            sqlStr.append("INSERT INTO web_service_source_file ");
            sqlStr.append("(web_service_source_file_id ");
            sqlStr.append(",web_service_file_type_id ");
            sqlStr.append(",web_service_id ");
            sqlStr.append(",language_id ");
            sqlStr.append(",path ");
            sqlStr.append(",source) ");
            sqlStr.append("VALUES (?, ?, ?, ?, ?, ?) ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(3, webServiceId);

            WebServiceRemoteFile file;
            for (int i = 0; i < webServiceFiles.size(); i++) {
                file = (WebServiceRemoteFile) webServiceFiles.get(i);
                ps.setInt(1, IdGeneratorClient.getSeqIdAsInt(DBMS.WEB_SERVICE_SOURCE_FILE_SEQ));
                ps.setInt(2, file.getType());
                ps.setInt(4, ContestConstants.JAVA);
                ps.setString(5, file.getPath());
                ps.setBytes(6, file.getContents());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            s_trace.error("Error inserting web service.", e);
            success = false;
        } finally {
            close(conn, ps, null);
        }
        return success;
    }

    /**
     * Removes the java server files from web_service_source_file for the
     * specified service using the passed connection. (Returns number of
     * deleted rows.
     */
    private int clearWebServiceServer(int webServiceId, Connection conn)
            throws Exception {

        PreparedStatement ps = null;
        int intRes = -1;

        try {
            StringBuilder sqlStr = new StringBuilder(256);

            sqlStr.append("DELETE FROM web_service_compilation ");
            sqlStr.append("WHERE web_service_source_file_id IN ");
            sqlStr.append("(SELECT web_service_source_file_id FROM web_service_source_file WHERE web_service_id = ?) ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, webServiceId);
            ps.executeUpdate();

            sqlStr = new StringBuilder(256);
            sqlStr.append("DELETE FROM web_service_source_file ");
            sqlStr.append("WHERE web_service_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, webServiceId);
            intRes = ps.executeUpdate();
        } catch (Exception e) {
            s_trace.error("Error in ClearWebServiceServer.", e);
        }
        return intRes;
    }

    /**
     * Uses the javadoc generator to generate the web services for the
     * Java interface file of the specified web service.
     *
     * Returns an ArrayList... boolean success, String error message
     */
    public ArrayList generateJavaDocs(int webServiceId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr;
        ArrayList results = new ArrayList();

        try {
            conn = DBMS.getConnection();

            //get the java interface file
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT source AS contents ");
            sqlStr.append(",path AS path ");
            sqlStr.append("FROM web_service_source_file ");
            sqlStr.append("WHERE web_service_id = ? ");
            sqlStr.append("AND language_id = ? ");
            sqlStr.append("AND web_service_file_type_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, webServiceId);
            ps.setInt(2, ContestConstants.JAVA);
            ps.setInt(3, WebServiceRemoteFile.WEB_SERVICE_INTERFACE);
            rs = ps.executeQuery();
            if (!rs.next()) {
                results.add(Boolean.FALSE);
                results.add("No Java interface file for web service in database.");
                return results;
            }

            //do generation
            RemoteFile[] inputFiles = new RemoteFile[1];
            RemoteFile[] outputFiles = null;
            inputFiles[0] = new RemoteFile(rs.getString("path"),
                    rs.getBytes("contents"));
            try {
                outputFiles = JavaDocGenerator.generateJavaDocs(inputFiles);
            } catch (JavaDocException jde) {
                s_trace.error("JavaDocGeneration exception. ", jde);
                results.add(Boolean.FALSE);
                results.add(jde.getMessage());
                return results;
            }

            if (outputFiles == null || outputFiles.length == 0) {
                results.add(Boolean.FALSE);
                results.add("Javadoc Generator returned no files.");
            }

            //insert java doc files into db
            sqlStr = new StringBuilder(256);
            sqlStr.append("DELETE FROM web_service_java_doc ");
            sqlStr.append("WHERE web_service_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, webServiceId);
            ps.executeUpdate();

            sqlStr = new StringBuilder(256);
            sqlStr.append("INSERT INTO web_service_java_doc ");
            sqlStr.append("(web_service_java_doc_id ");
            sqlStr.append(",web_service_id ");
            sqlStr.append(",path ");
            sqlStr.append(",content) ");
            sqlStr.append("VALUES (?, ?, ?, ?) ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(2, webServiceId);
            for (int i = 0; i < outputFiles.length; i++) {
                ps.setInt(1, IdGeneratorClient.getSeqIdAsInt(DBMS.WEB_SERVICE_JAVA_DOC_SEQ));
                ps.setString(3, outputFiles[i].getPath());
                ps.setBytes(4, outputFiles[i].getContents());
                ps.executeUpdate();
            }

            results.add(Boolean.TRUE);
        } catch (Exception e) {
            s_trace.error("Error generating web services.", e);
            results.add(Boolean.FALSE);
            results.add(ApplicationConstants.SERVER_ERROR);
        } finally {
            //closeConnection(conn, ps, rs);
            close(conn, ps, rs);
        }

        return results;
    }

    /**
     * Returns an html file with the java doc for the specified web service id.
     * (just the interface java docs)
     */
    public String getBriefJavaDocs(int webServiceId) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        StringBuilder sqlStr = null;
        String html = "";

        try {
            conn = DBMS.getConnection();

            //get the source file name
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT path AS path ");
            sqlStr.append("FROM web_service_source_file ");
            sqlStr.append("WHERE web_service_id = ? ");
            sqlStr.append("AND language_id = ? ");
            sqlStr.append("AND web_service_file_type_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, webServiceId);
            ps.setInt(2, ContestConstants.JAVA);
            ps.setInt(3, WebServiceRemoteFile.WEB_SERVICE_INTERFACE);
            rs = ps.executeQuery();
            if (!rs.next()) {
                debug("Returning \"\" because no entry in "
                        + "web_service_source_file");
                return "";
            }
            String path = rs.getString("path");
            rs.close();
            ps.close();

            //remove relative path
            if (path.lastIndexOf("/") != -1)
                path = path.substring(path.lastIndexOf("/") + 1);

            //remove .java,
            if (path.lastIndexOf(".") != -1)
                path = path.substring(0, path.lastIndexOf("."));

            String htmlFile = "%" + path + ".html";
            debug("htmlFile = " + htmlFile);

            //get the file
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT content ");
            sqlStr.append("FROM web_service_java_doc ");
            sqlStr.append("WHERE web_service_id = ? ");
            sqlStr.append("AND path like ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, webServiceId);
            ps.setString(2, htmlFile);
            rs = ps.executeQuery();
            if (!rs.next()) {
                debug("Returning \"\" because no entry in web_service_java_doc");
                return "";
            }
            html = DBMS.getTextString(rs, 1);
        } catch (Exception e) {
            s_trace.error("Error getting brief java docs.", e);
            return "";
        } finally {
            //closeConnection(conn, ps, rs);
            close(conn, ps, rs);
        }
        return html;
    }


    /******************************************************************************
     * Application Services                                                       *
     ******************************************************************************/

    /**
     * Returns a list of pending applications (ApplicationInformations).
     */
    public ArrayList getPendingApplications() {
        if (VERBOSE) s_trace.debug("In MPSQASServicesBean.getPendingApplications().");

        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList apps = null;

        try {
            conn = DBMS.getConnection();

            StringBuilder sqlStr = new StringBuilder(256);

            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT u.handle ");
            sqlStr.append(",d.user_type_id ");
            sqlStr.append(",r.rating ");
            sqlStr.append(",r.num_ratings ");
            sqlStr.append(",d.dev_app_id ");
            sqlStr.append("FROM user u ");
            sqlStr.append(",rating r ");
            sqlStr.append(",development_application d ");
            sqlStr.append("WHERE u.user_id = d.user_id ");
            sqlStr.append("AND r.coder_id = u.user_id ");
            sqlStr.append("AND d.dev_app_status_id = ? ");
            sqlStr.append("ORDER BY d.user_type_id");

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, ApplicationConstants.APPLICATION_PENDING);
            rs = ps.executeQuery();

            apps = new ArrayList();
            ApplicationInformation appInfo;

            while (rs.next()) {
                appInfo = new ApplicationInformation();
                appInfo.setHandle(rs.getString(1));

                if (rs.getInt(2) == ApplicationConstants.PROBLEM_WRITER) {
                    appInfo.setApplicationType("Writer");
                } else if (rs.getInt(2) == ApplicationConstants.PROBLEM_TESTER) {
                    appInfo.setApplicationType("Tester");
                } else {
                    appInfo.setApplicationType("Unknown");
                }

                appInfo.setRating(rs.getInt(3));
                appInfo.setEvents(rs.getInt(4));
                appInfo.setId(rs.getInt(5));

                apps.add(appInfo);
            }
        } catch (Exception e) {
            s_trace.error("Error getting problem testers: ", e);
            apps = null;
        } finally {
            close(conn, ps, rs);
        }

        //closeConnection(conn, ps);
        return apps;
    }

    /**
     * Fills out an ApplicationInformation with information about an application.
     *
     * @param applicationId The dev_app_id of the application to get information about.
     */
    public ApplicationInformation getApplicationInformation(int applicationId) {
        if (VERBOSE) s_trace.debug("In MPSQASServices.getApplicationInformation().");
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ApplicationInformation info = null;

        try {
            conn = DBMS.getConnection();
            StringBuilder sqlStr = new StringBuilder(256);

            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT u.handle ");
            sqlStr.append(",r.rating ");
            sqlStr.append(",r.num_ratings ");
            sqlStr.append(",u.first_name ");
            sqlStr.append(",u.last_name ");
            sqlStr.append(",e.address ");
            sqlStr.append(",d.message ");
            sqlStr.append(",d.user_type_id ");
            sqlStr.append("FROM user u ");
            sqlStr.append(",rating r ");
            sqlStr.append(",coder c ");
            sqlStr.append(",development_application d ");
            sqlStr.append(",email e ");
            sqlStr.append("WHERE u.user_id = c.coder_id ");
            sqlStr.append("AND u.user_id = r.coder_id ");
            sqlStr.append("AND u.user_id = d.user_id ");
            sqlStr.append("AND e.user_id = u.user_id ");
            sqlStr.append("AND e.primary_ind = 1 ");
            sqlStr.append("AND d.dev_app_id = ? ");
            sqlStr.append("AND d.dev_app_status_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, applicationId);
            ps.setInt(2, ApplicationConstants.APPLICATION_PENDING);
            rs = ps.executeQuery();

            if (!rs.next()) {
                throw new Exception("No application with status PENDING with id " + applicationId);
            }

            info = new ApplicationInformation();
            info.setHandle(rs.getString(1));
            info.setRating(rs.getInt(2));
            info.setEvents(rs.getInt(3));
            info.setName(rs.getString(4) + " " + rs.getString(5));
            info.setEmail(rs.getString(6));
            info.setMessage(DBMS.getTextString(rs, 7));

            if (rs.getInt(8) == ApplicationConstants.PROBLEM_WRITER) {
                info.setApplicationType("Problem Writer Application:");
            } else if (rs.getInt(8) == ApplicationConstants.PROBLEM_TESTER) {
                info.setApplicationType("Problem Tester Application:");
            } else {
                info.setApplicationType("Unknown application type:");
            }
        } catch (Exception e) {
            s_trace.error("Error getting application information for dev_app_id = " + applicationId, e);
            info = null;
        } finally {
            close(conn, ps, rs);
        }
        //closeConnection(conn, ps);
        return info;
    }

    /**
     * Inserts an application into the database.  Does some checking first to
     * make sure everything is going to be OK.
     *
     * @param message The message left with the application.
     * @param applicationType The type of application.
     * @param userId The user submitting the application.
     */
    public ArrayList saveApplication(String message, int applicationType, int userId) {
        if (VERBOSE) s_trace.debug("In MPSQASServicesBean.saveApplication().");

        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList response;

        try {
            conn = DBMS.getConnection();

            StringBuilder sqlStr = new StringBuilder(256);

            int applyUserGroup = -1;  //too much information that means the same thing.
            int applyUserType = -1;

            if (applicationType == MessageConstants.WRITER_APPLICATION) {
                applyUserGroup = ApplicationConstants.PROBLEM_WRITER_GROUP;
                applyUserType = ApplicationConstants.PROBLEM_WRITER;
            } else if (applicationType == MessageConstants.TESTER_APPLICATION) {
                applyUserGroup = ApplicationConstants.PROBLEM_TESTER_GROUP;
                applyUserType = ApplicationConstants.PROBLEM_TESTER;
            }

            sqlStr.replace(0, sqlStr.length(), "SELECT user_id FROM group_user WHERE (group_id=? OR group_id=?) AND user_id=?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, ApplicationConstants.ADMIN_GROUP);
            ps.setInt(2, applyUserGroup);
            ps.setInt(3, userId);
            rs = ps.executeQuery();

            if (rs.next()) //they are already in this group.
            {
                response = new ArrayList(2);
                response.add(new Boolean(false));
                response.add("You are already a member of this group.  No need to apply.");
                //closeConnection(conn, ps);
                return response;
            }

            sqlStr.replace(0, sqlStr.length(), "SELECT dev_app_id FROM development_application WHERE user_id=? AND user_type_id=? AND dev_app_status_id=?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, userId);
            ps.setInt(2, applyUserType);
            ps.setInt(3, ApplicationConstants.APPLICATION_PENDING);
            rs = ps.executeQuery();

            if (rs.next()) //they applied already
            {
                response = new ArrayList(2);
                response.add(new Boolean(false));
                response.add("You have already applied for this and the application is pending.");
                //closeConnection(conn, ps);
                return response;
            }

            sqlStr.replace(0, sqlStr.length(), "INSERT INTO development_application (dev_app_id,user_id,user_type_id,");
            sqlStr.append("dev_app_status_id,message) VALUES (?,?,?,?,?)");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, IdGeneratorClient.getSeqIdAsInt(DBMS.JMA_SEQ));
            ps.setInt(2, userId);
            ps.setInt(3, applyUserType);
            ps.setInt(4, ApplicationConstants.APPLICATION_PENDING);
            ps.setBytes(5, DBMS.serializeTextString(message));
            int numUpdates = ps.executeUpdate();

            if (numUpdates != 1) throw new Exception("Wrong number of rows inserted: " + numUpdates);

            response = new ArrayList(1);
            response.add(new Boolean(true));
        } catch (Exception e) {
            s_trace.error("Error inserting into development_application: ", e);

            response = new ArrayList(2);
            response.add(new Boolean(false));
            response.add(ApplicationConstants.SERVER_ERROR);
        } finally {
            close(conn, ps, rs);
        }

        //closeConnection(conn, ps);
        return response;
    }

    /**
     * Replies to an application by updateding the status in the database and emailing the user letting
     * him know the new status.
     * Returns an ArrayList with a Boolean indicating sucess, and an error message if no success.
     *
     * @param applicationId Id of the application to reply to.
     * @param accepted boolean indicating if the application was accepted.
     * @param message String indicating if a reason.
     * @param userId User Id of admin who is replying.
     */
    public ArrayList processApplicationReply(int applicationId, boolean accepted,
            String message, int userId) {
        if (VERBOSE) s_trace.debug("In MPSQASServicesBean.replyToApplication()");
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList response = new ArrayList();

        try {
            conn = DBMS.getConnection();
            StringBuilder sqlStr = new StringBuilder(256);

            //make sure this application exists and is pending.
            sqlStr.append("SELECT user_id ");
            sqlStr.append(",user_type_id ");
            sqlStr.append("FROM development_application ");
            sqlStr.append("WHERE dev_app_id = ? ");
            sqlStr.append("AND dev_app_status_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, applicationId);
            ps.setInt(2, ApplicationConstants.APPLICATION_PENDING);
            rs = ps.executeQuery();

            if (!rs.next()) {
                response.add(new Boolean(false));
                response.add("This application does not exist or is not pending approval.");
                //closeConnection(conn, ps);
                return response;
            }

            int appUserId = rs.getInt(1);
            int appUserType = rs.getInt(2);
            int newStatus = accepted ? ApplicationConstants.APPLICATION_ACCEPTED
                    : ApplicationConstants.APPLICATION_REJECTED;

            //update the application table
            sqlStr = new StringBuilder(256);
            sqlStr.append("UPDATE development_application ");
            sqlStr.append("SET dev_app_status_id = ? ");
            sqlStr.append("WHERE dev_app_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, newStatus);
            ps.setInt(2, applicationId);
            //int rowsUpdated = ps.executeUpdate();
            ps.executeUpdate();

            //add the user to the proper group
            if (accepted) {
                int newGroup = -1;
                if (appUserType == ApplicationConstants.PROBLEM_WRITER) {
                    newGroup = ApplicationConstants.PROBLEM_WRITER_GROUP;
                } else if (appUserType == ApplicationConstants.PROBLEM_TESTER) {
                    newGroup = ApplicationConstants.PROBLEM_TESTER_GROUP;
                }

                //make sure they are not already there
                sqlStr.replace(0, sqlStr.length(), "SELECT user_id FROM group_user WHERE group_id=? and user_id=?");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, newGroup);
                ps.setInt(2, appUserId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    response.add(new Boolean(false));
                    response.add("The user is already in the group.");
                    //closeConnection(conn, ps);
                    return response;
                }

                sqlStr.replace(0, sqlStr.length(), "INSERT INTO group_user (user_id,group_id) VALUES (?,?)");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, appUserId);
                ps.setInt(2, newGroup);
                //rowsUpdated = ps.executeUpdate();
                ps.executeUpdate();
            }

            //email the user
            try {
                sqlStr = new StringBuilder();
                sqlStr.append("SELECT u.handle, e.address FROM user u, email e WHERE e.user_id = u.user_id and e.primary_ind = 1 and u.user_id = ?");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, appUserId);
                rs = ps.executeQuery();
                rs.next();
                String appHandle = rs.getString(1);
                String emailAddy = rs.getString(2);

                ps.setInt(1, userId);
                rs = ps.executeQuery();
                rs.next();
                String adminHandle = rs.getString(1);

                String accOrRej = accepted ? "Accepted" : "Rejected";
                String appType = appUserType == ApplicationConstants.PROBLEM_WRITER ?
                        "Problem Writer" : "Problem Tester";

                TCSEmailMessage email = new TCSEmailMessage();
                StringBuilder emailBody = new StringBuilder(256);

                emailBody.replace(0, emailBody.length(), "Hi " + appHandle + ",\n\n");
                emailBody.append("Your " + appType + " Application was " + accOrRej + " by admin " + adminHandle + ".\n");

                if (message.trim().length() > 0) {
                    emailBody.append("\n");
                    emailBody.append(adminHandle);
                    emailBody.append(" says:\n");
                    emailBody.append(ApplicationConstants.HORIZONTAL_RULE);
                    emailBody.append(message);
                    emailBody.append("\n");
                    emailBody.append(ApplicationConstants.HORIZONTAL_RULE);
                }

                if (accepted) {
                    emailBody.append("\nNavigate to www.topcoder.com/contest/mpsqas/developercontract.html to get the required contract. ");
                    emailBody.append("Log into MPSQAS at any time to begin proposing problems.\n");
                }

                emailBody.append("\n-mpsqas\n");
                emailBody.append("\nThis is an automated email generated by MPSQAS.");

                email.setSubject(appType + " Application " + accOrRej);
                email.setBody(emailBody.toString());
                email.setFromAddress(ApplicationConstants.FROM_EMAIL_ADDRESS);
                email.addToAddress(emailAddy, TCSEmailMessage.TO);
                email.addToAddress("contest@topcoder.com", TCSEmailMessage.CC);
                EmailEngine.send(email);
            } catch (Exception e) {
                s_trace.error("Error sending email in response to application reply, id: " + applicationId, e);
            }

            response = new ArrayList(1);
            response.add(new Boolean(true));
        } catch (Exception e) {
            s_trace.error("Error processing application reply, id: " + applicationId, e);

            response = new ArrayList(2);
            response.add(new Boolean(false));
            response.add(ApplicationConstants.SERVER_ERROR);
        } finally {
            close(conn, ps, rs);
        }

        //closeConnection(conn, ps);
        return response;
    }

    /******************************************************************************
     * User Services                                                              *
     ******************************************************************************/

    /**
     * Returns a list of UserInformations for users specified by forType and id.
     *
     * forType = ALL_TESTERS
     *  Returns a list of all testers, id ignored
     * forType = TESTERS_FOR_COMPONENT
     *  Returns a list of testers for a component, id = component_id
     * forType = TESTERS_FOR_PROBLEM
     *  Returns a list of testers for a problem, id = problem_id
     * forType = ALL_USERS
     *  Returns a list of all registered testers and writers, id ignored.
     *  Payment info also filled out.
     * forType = ALL_ADMINS
     *  Returns a list of all admins, id ignored.
     *
     * @param forType Constants describing which type of users to get.
     * @param id If required, specify an id to specify which users to get
     */
    public ArrayList getUsers(int forType, int id) {
        //TESTERS_FOR_PROBLEM is a special case, can't fit it in with this query
        if (forType == ApplicationConstants.TESTERS_FOR_PROBLEM) {
            return getProblemTesters(id);
        }

        StringBuilder sqlStr = null;
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        ArrayList users = new ArrayList();

        try {
            conn = DBMS.getConnection();
            sqlStr = new StringBuilder(256);
            //ResultSet rs, rs2;

            //first get all the writers and testers
            sqlStr.append("SELECT DISTINCT u.user_id AS user_id ");
            sqlStr.append(",u.handle AS handle ");
            sqlStr.append(",u.first_name AS first_name ");
            sqlStr.append(",u.last_name AS last_name ");

            sqlStr.append("FROM user u ");
            sqlStr.append(",coder c ");
            if (forType == ApplicationConstants.TESTERS_FOR_COMPONENT
                    || forType == ApplicationConstants.WRITERS_FOR_COMPONENT) {
                sqlStr.append(",component_user_xref cu ");
            }
            if (forType == ApplicationConstants.ALL_TESTERS
                    || forType == ApplicationConstants.ALL_USERS
                    || forType == ApplicationConstants.ALL_ADMINS) {
                sqlStr.append(",group_user gu ");
            }

            sqlStr.append("WHERE c.coder_id = u.user_id ");
            sqlStr.append("AND u.status = 'A'");
            if (forType == ApplicationConstants.ALL_USERS) {
                sqlStr.append("AND (gu.group_id = ? OR gu.group_id = ?) ");
                sqlStr.append("AND gu.user_id = u.user_id ");
            } else if (forType == ApplicationConstants.TESTERS_FOR_COMPONENT
                    || forType == ApplicationConstants.WRITERS_FOR_COMPONENT) {
                sqlStr.append("AND cu.user_type_id = ? ");
                sqlStr.append("AND cu.component_id = ? ");
                sqlStr.append("AND cu.user_id = u.user_id ");
            } else if (forType == ApplicationConstants.ALL_TESTERS
                    || forType == ApplicationConstants.ALL_ADMINS) {
                sqlStr.append("AND gu.user_id = u.user_id ");
                sqlStr.append("AND gu.group_id = ? ");
            }
            if (forType == ApplicationConstants.ALL_USERS) {
                sqlStr.append("GROUP BY u.user_id ");
                sqlStr.append(",u.handle ");
                sqlStr.append(",u.first_name ");
                sqlStr.append(",u.last_name ");
            }

            ps = conn.prepareStatement(sqlStr.toString());
            int index = 0;
            if (forType == ApplicationConstants.ALL_USERS) {
                ps.setInt(++index, ApplicationConstants.PROBLEM_WRITER_GROUP);
                ps.setInt(++index, ApplicationConstants.PROBLEM_TESTER_GROUP);
            } else if (forType == ApplicationConstants.ALL_TESTERS) {
                ps.setInt(++index, ApplicationConstants.PROBLEM_TESTER_GROUP);
            } else if (forType == ApplicationConstants.ALL_ADMINS) {
                ps.setInt(++index, ApplicationConstants.ADMIN_GROUP);
            } else if (forType == ApplicationConstants.TESTERS_FOR_COMPONENT) {
                ps.setInt(++index, ApplicationConstants.PROBLEM_TESTER);
                ps.setInt(++index, id);
            } else if (forType == ApplicationConstants.WRITERS_FOR_COMPONENT) {
                ps.setInt(++index, ApplicationConstants.PROBLEM_WRITER);
                ps.setInt(++index, id);
            }

            rs = ps.executeQuery();

            //payment info query
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT SUM(paid) ");
            sqlStr.append(",SUM(pending) ");
            sqlStr.append("FROM problem_payment ");
            sqlStr.append("WHERE coder_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());

            UserInformation userInfo;
            while (rs.next()) {
                userInfo = new UserInformation(rs.getString("handle"),
                        rs.getInt("user_id"));
                userInfo.setFirstName(rs.getString("first_name"));
                userInfo.setLastName(rs.getString("last_name"));

                //get payment info
                if (forType == ApplicationConstants.ALL_USERS) {
                    ps.setInt(1, rs.getInt("user_id"));
                    rs2 = ps.executeQuery();
                    rs2.next();
                    userInfo.setPaid(rs2.getDouble(1));
                    userInfo.setPending(rs2.getDouble(2));
                }

                users.add(userInfo);
            }
        } catch (Exception e) {
            s_trace.error("Error getting user information: ", e);
            debug("sqlStr was: " + sqlStr);
        } finally {
            //closeConnection(conn, ps);
            close(null, null, rs2);
            close(conn, ps, rs);
        }

        return users;
    }

    /**
     * Returns a list of problem testers for a problem... that is testers
     * who test all the problems components.
     */
    private ArrayList getProblemTesters(int problemId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = null;
        ArrayList users = new ArrayList();

        try {
            conn = DBMS.getConnection();
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT component_id FROM component WHERE problem_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, problemId);
            rs = ps.executeQuery();

            ArrayList groups = new ArrayList();
            while (rs.next())
                groups.add(getUsers(ApplicationConstants.TESTERS_FOR_COMPONENT,
                        rs.getInt("component_id")));

            //culminate list into big list
            ArrayList allUsers = new ArrayList();
            ArrayList group;
            for (int i = 0; i < groups.size(); i++) {
                group = (ArrayList) groups.get(i);
                for (int j = 0; j < group.size(); j++)
                    if (!allUsers.contains(group.get(j))) allUsers.add(group.get(j));
            }

            //and now add the users that are in all groups
            boolean inAll = true;
            for (int i = 0; i < allUsers.size(); i++) {
                inAll = true;
                for (int j = 0; inAll && j < groups.size(); j++)
                    if (!((ArrayList) groups.get(j)).contains(allUsers.get(i)))
                        inAll = false;
                if (inAll) users.add(allUsers.get(i));
            }
        } catch (Exception e) {
            s_trace.error("Error getting problem testers.", e);
        } finally {
            //closeConnection(conn, ps);
            close(conn, ps, rs);
        }
        return users;
    }


    /**
     * Returns a UserInformation with information about the specified user.
     *
     * @param userId The user id of the user to get information for.
     */
    public UserInformation getUserInformation(int userId) {
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        UserInformation userInfo = null;

        try {
            conn = DBMS.getConnection();
            StringBuilder sqlStr = new StringBuilder(256);

            //get general information
            sqlStr.append("SELECT u.handle ");
            sqlStr.append(",u.first_name ");
            sqlStr.append(",u.last_name ");
            sqlStr.append(",e.address ");
            sqlStr.append("FROM user u ");
            sqlStr.append(",coder c ");
            sqlStr.append(",email e ");
            sqlStr.append("WHERE u.user_id = c.coder_id ");
            sqlStr.append("AND u.user_id = ? ");
            sqlStr.append("AND e.user_id = u.user_id ");
            sqlStr.append("AND e.primary_ind = 1 ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            rs.next();

            userInfo = new UserInformation(rs.getString(1), userId);
            userInfo.setFirstName(rs.getString(2));
            userInfo.setLastName(rs.getString(3));
            userInfo.setEmail(rs.getString(4));

            //get user type
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT user_id FROM group_user WHERE group_id = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, ApplicationConstants.PROBLEM_WRITER_GROUP);
            rs = ps.executeQuery();
            userInfo.setWriter(rs.next());

            ps.setInt(1, ApplicationConstants.PROBLEM_TESTER_GROUP);
            rs = ps.executeQuery();
            userInfo.setTester(rs.next());

            //get payment information
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT SUM(paid) ");
            sqlStr.append(",SUM(pending) ");
            sqlStr.append("FROM problem_payment ");
            sqlStr.append("WHERE coder_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            rs.next();
            userInfo.setPaid(rs.getDouble(1));
            userInfo.setPending(rs.getDouble(2));

            ArrayList problems = new ArrayList();
            ProblemInformation problem = null;

            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT p.problem_id AS id ");
            sqlStr.append(",p.problem_type_id AS problem_type ");
            sqlStr.append(",p.name AS name ");
            sqlStr.append(",p.status_id AS status ");
            sqlStr.append(",p.modify_date AS modify_date ");
            sqlStr.append(",p.proposed_division_id AS division ");
            sqlStr.append(",p.proposed_difficulty_id AS difficulty ");
            sqlStr.append(",NVL(pp.paid, 0) AS paid ");
            sqlStr.append(",NVL(pp.pending, 0) AS pending ");
            sqlStr.append("FROM problem p ");
            sqlStr.append("LEFT OUTER JOIN problem_payment pp ");
            sqlStr.append("ON p.problem_id = pp.problem_id ");
            sqlStr.append("AND pp.coder_id = ? ");
            sqlStr.append("WHERE p.problem_id IN (SELECT DISTINCT c.problem_id ");
            sqlStr.append("FROM component c ");
            sqlStr.append("JOIN component_user_xref cu ");
            sqlStr.append("ON cu.component_id = c.component_id ");
            ;
            sqlStr.append("AND cu.user_id = ?) ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            rs = ps.executeQuery();

            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT COUNT(*) FROM component WHERE problem_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            //ResultSet rs2;

            while (rs.next()) {
                problem = new ProblemInformation();
                problem.setDifficulty(rs.getInt("difficulty"));
                problem.setDivision(rs.getInt("division"));
                problem.setStatus(rs.getInt("status"));
                problem.setLastModified(rs.getTimestamp("modify_date").toString());
                problem.setProblemId(rs.getInt("id"));
                problem.setProblemTypeID(rs.getInt("problem_type"));
                problem.setName(rs.getString("name"));
                problem.setPaid(rs.getDouble("paid"));
                problem.setPending(rs.getDouble("pending"));
                ps.setInt(1, rs.getInt("id"));
                rs2 = ps.executeQuery();
                ;
                rs2.next();
                problem.setNumComponents(rs2.getInt(1));
                problem.setUserType(getProblemServices().getUserTypeForProblem(
                        rs.getInt("id"), userId));
                problems.add(problem);
            }
            userInfo.setProblems(problems);
        } catch (Exception e) {
            s_trace.error("Error getting user information for userid " + userId, e);
            e.printStackTrace();
            userInfo = null;
        } finally {
            //closeConnection(conn, ps);
            close(null, null, rs2);
            close(conn, ps, rs);
        }

        return userInfo;
    }

    /**
     * Update paid to be paid + pending_payment for all users in list to record
     * payment in DB.
     *
     * @param userIds ArrayList of user ids being paid.
     */
    public boolean recordPayment(ArrayList userIds) {
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        boolean ok = true;
        try {
            conn = DBMS.getConnection();
            StringBuilder sqlStr = new StringBuilder(256);

            sqlStr.append("UPDATE problem_payment ");
            sqlStr.append("SET paid = paid + pending ");
            sqlStr.append(",pending = 0 ");
            sqlStr.append("WHERE coder_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());

            for (int i = 0; i < userIds.size(); i++) {
                ps.setInt(1, ((Integer) userIds.get(i)).intValue());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            s_trace.error("Error updating payment.", e);
            ok = false;
        } finally {
            //closeConnection(conn, ps);
            close(conn, ps, null);
        }
        return ok;
    }

    /**
     * Sets the pending amounts in problem_payment for the specified user.
     * Inserts rows in problem_payment if needed.
     *
     * @param userId  The user id to set the pending amounts of.
     * @param amounts HashMap of Integer problem_id -> Double pending amount.
     */
    public boolean storePendingAmounts(int userId, HashMap amounts) {
        Connection conn = null;
        PreparedStatement isTherePS = null;
        PreparedStatement insertPS = null;
        PreparedStatement updatePS = null;
        ResultSet rs = null;
        StringBuilder sqlStr = null;
        boolean success = false;

        try {
            conn = DBMS.getConnection();

            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT problem_id ");
            sqlStr.append("FROM problem_payment ");
            sqlStr.append("WHERE problem_id = ? ");
            sqlStr.append("AND coder_id = ? ");
            isTherePS = conn.prepareStatement(sqlStr.toString());
            isTherePS.setInt(2, userId);

            sqlStr = new StringBuilder(256);
            sqlStr.append("INSERT INTO problem_payment ");
            sqlStr.append("(coder_id ");
            sqlStr.append(",problem_id ");
            sqlStr.append(",paid ");
            sqlStr.append(",pending) ");
            sqlStr.append("VALUES (?, ?, ?, ?) ");
            insertPS = conn.prepareStatement(sqlStr.toString());
            insertPS.setInt(1, userId);
            insertPS.setDouble(3, 0);
            insertPS.setDouble(4, 0);

            sqlStr = new StringBuilder(256);
            sqlStr.append("UPDATE problem_payment ");
            sqlStr.append("SET pending = ? ");
            sqlStr.append("WHERE problem_id = ? ");
            sqlStr.append("AND coder_id = ? ");
            updatePS = conn.prepareStatement(sqlStr.toString());
            updatePS.setInt(3, userId);

            Iterator iter = amounts.entrySet().iterator();
            int problemId;
            double payment;
            Map.Entry entry;
            while (iter.hasNext()) {
                entry = (Map.Entry) iter.next();
                problemId = ((Integer) entry.getKey()).intValue();
                payment = ((Double) entry.getValue()).doubleValue();

                //check if there is a row in probem_payment for this
                isTherePS.setInt(1, problemId);
                rs = isTherePS.executeQuery();

                //if not insert one
                if (!rs.next()) {
                    insertPS.setInt(2, problemId);
                    insertPS.executeUpdate();
                }

                //do the update
                updatePS.setDouble(1, payment);
                updatePS.setInt(2, problemId);
                updatePS.executeUpdate();
            }

            isTherePS.close();
            updatePS.close();
            insertPS.close();

            success = true;
        } catch (Exception e) {
            s_trace.error("Error updating pending payment.", e);
        } finally {
            //closeConnection(conn, null, rs);
            close(conn, isTherePS, rs);
            close(null, updatePS, null);
            close(null, insertPS, null);
        }
        return success;
    }

    /******************************************************************************
     * Private utility methods                                                    *
     ******************************************************************************/

    /**
     * Returns true iff the user is an admin.
     *
     * @param userId The id of the user to check.
     */
    private boolean isAdmin(int userId) {
        boolean isAdmin = false;
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr;

        try {
            conn = DBMS.getConnection();
            sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT u.handle ");
            sqlStr.append("FROM user u ");
            sqlStr.append(",group_user gu ");
            sqlStr.append("WHERE u.user_id = gu.user_id ");
            sqlStr.append("AND gu.group_id = ? ");
            sqlStr.append("AND gu.user_id = ? ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, ApplicationConstants.ADMIN_GROUP);
            ps.setInt(2, userId);
            rs = ps.executeQuery();
            isAdmin = rs.next();
        } catch (Exception e) {
            s_trace.error("Error checking admin status: ", e);
        } finally {
            //closeConnection(conn, ps);
            close(conn, ps, rs);
        }
        return isAdmin;
    }



    /**
     * Runs a test against the specfied solution. It does not make any check
     *
     * @param solutionId
     *         the solution id.
     * @param componentId
     *         the component id.
     * @param methodName
     *         the method name.
     * @param args
     *         the arguments of mpsqas.
     * @param conn
     *         the jdbc connection.
     * @return the mpsqas file with run test result.
     * @throws SQLException If an SQL exception is thrown during the process
     * @throws TesterWaiterException if the TesterWaiter throws the exception
     * @throws IllegalArgumentException if the specified solutions is not compiled.
     */
    public MPSQASFiles runMPSQASTest(int solutionId, int componentId,
                String methodName, Object[] args, Connection conn) throws TesterInvokerException, SQLException, IllegalArgumentException {

        String className = componentDao.getClassNameForComponent(componentId, conn);
        int language = solutionDao.getLanguageForSolution(solutionId, conn);
        int roundType = getRoundTypeByComponentId(componentId);
        int componentType = getComponentTypeByComponentId(componentId);
        MPSQASFiles mfiles = new MPSQASFiles();
        mfiles.setArgTypes(new ArrayList());
        mfiles.setArgVals(new ArrayList(Arrays.asList(args)));
        mfiles.setClassName(className);
        mfiles.setPackageName(ServicesConstants.SOLUTIONS_PACKAGE + "s" + solutionId);
        mfiles.setMethodName(methodName);
        mfiles.setLanguage(language);
        mfiles.setSolutionId(solutionId);
        mfiles.setClassFiles(getClassFiles(solutionId, language, conn));
        mfiles.setRoundType(roundType);
        mfiles.setComponentType(componentType);
        SimpleComponent component = CoreServices.getSimpleComponent(componentId);
        ProblemCustomSettings custom = component.getProblemCustomSettings();
        mfiles.setProblemCustomSettings(custom);
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("Sending test request for solutionId="+solutionId+" className="+ className+" method="+methodName
                + " language="+language+" roundType="+roundType+" componentType="
                + componentType + " cppApprovedPath=" + custom.getCppApprovedPath()
                + " pythonApprovedPath="+custom.getPythonApprovedPath());
        }
        return getMPSQASTester().mpsqasTest(mfiles);
    }

    /**
     * Returns a HashMap containing the class files for the solution
     *
     * @param solutionId Id of the solution
     * @param conn Connection to use
     * @return a non empty map containing the class files for the solution
     *
     * @throws SQLException If an SQLException is thrown during the process
     * @throws IllegalArgumentException if the solution is not compiled
     */
    private HashMap getClassFiles(int solutionId, int language, Connection conn) throws SQLException, IllegalArgumentException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            StringBuilder sqlStr = new StringBuilder(256);
            sqlStr.append("SELECT path ");
            sqlStr.append(",class_file ");
            sqlStr.append(",sort_order ");
            sqlStr.append("FROM solution_class_file ");
            sqlStr.append("WHERE solution_id = ? ");
            sqlStr.append("ORDER BY sort_order ");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, solutionId);
            rs = ps.executeQuery();
            if (rs.next()) {
                HashMap classFiles = new HashMap();;
                do {
                    String fileName = rs.getString(1);
                    if (language == ContestConstants.JAVA) {
                        //fileName is package + class for java, remove ".class" too
                        fileName = fileName.substring(0, fileName.lastIndexOf("."))
                            .replace('/', '.');

                    }
                    classFiles.put(fileName, rs.getBytes(2));

                } while (rs.next());
                return classFiles;
            }
            throw new IllegalArgumentException("Solution is not compiled");
        } finally {
            DBMS.close(null, ps, rs);
        }
    }


    private static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        DBMS.close(conn, ps, rs);
    }

    /** Returns an instance of ProblemServices. */
    private ProblemServices getProblemServices() throws NamingException,
            CreateException, RemoteException {
        if (problemServices == null) {
            problemServices = ProblemServicesLocator.getService();
        }
        return problemServices;
    }

    /** Returns an instance of TestServices. */
    private TestServices getTestServices() throws NamingException, CreateException, RemoteException {
        if (testServices == null) {
            testServices = TestServicesLocator.getService();
            if (longTestEventListener == null) {
                longTestEventListener = new MPSQASLongTestServiceListener();
            }
        }
        return testServices;
    }

    /******************************************************************************
     * Required Bean Methods                                                      *
     ******************************************************************************/

    public void ejbCreate() {
        /*
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBMS.getConnection();
            ps = conn.prepareStatement(
                    "SELECT data_type_id, language_id, display_value " +
                    "FROM data_type_mapping");
            rs = ps.executeQuery();
            HashMap mappings = new HashMap();

            while (rs.next()) {
                int dataTypeId = rs.getInt(1);
                int languageId = rs.getInt(2);
                String desc = rs.getString(3);
                HashMap mapping = (HashMap) mappings.get(new Integer(dataTypeId));

                if (mapping == null) {
                    mapping = new HashMap();
                    mappings.put(new Integer(dataTypeId), mapping);
                }
                mapping.put(new Integer(languageId), desc);
            }
            ps = conn.prepareStatement(
                    "SELECT data_type_id, data_type_desc FROM data_type");
            rs = ps.executeQuery();
            while (rs.next()) {
                DataType type = new DataType(
                        rs.getInt(1),
                        rs.getString(2),
                        (HashMap) mappings.get(new Integer(rs.getInt(1))));
            }
        } catch (Exception ex) {
            s_trace.error("MPSQASServicesBeain.ejbCreate: unable to get data types: "
                    + ex, ex);
        } finally {
            //closeConnection(conn, s);
            close(conn, ps, rs);
        }
        */
    }

    public DataType getDataType(String id)
            throws InvalidTypeException {
        return DataTypeFactory.getDataType(id);
    }

    public DataType getDataType(int id)
            throws InvalidTypeException {
        return DataTypeFactory.getDataType(id);
    }

    private static void debug(Object message) {
        s_trace.debug(message);
    }


    /**
     * @return Returns the notificator.
     */
    public MPSQASServiceEventNotificator getNotificator() {
        if (notificator == null) {
            notificator = new MPSQASServiceEventNotificator();
        }
        return notificator;
    }

    public void generateWriterPayment(int componentId, int coderId, double amount) {
        try {
            PactsClientServices services = PactsClientServicesLocator.getService();
            List payments = services.findCoderPayments(coderId, ProblemWritingPayment.PROBLEM_WRITING_PAYMENT, componentId);
            if(payments != null && payments.size() > 0) {
                //update
                BasePayment payment = (BasePayment)payments.get(0);
                payment.setGrossAmount(amount);
                payment.setNetAmount(amount);
                services.updatePayment(payment);
            } else {
                //add
                services.addPayment(new ProblemWritingPayment(coderId, amount, componentId));
            }
        } catch (Exception e) {
            s_trace.error("Failed to get PACTs EJB", e);
        }
    };

    public void generateTesterPayment(int roundId, int coderId, double amount) {
        try {
            PactsClientServices services = PactsClientServicesLocator.getService();
            List payments = services.findCoderPayments(coderId, ProblemTestingPayment.PROBLEM_TESTING_PAYMENT, roundId);
            if(payments != null && payments.size() > 0) {
                //update
                BasePayment payment = (BasePayment)payments.get(0);
                payment.setGrossAmount(amount);
                payment.setNetAmount(amount);
                services.updatePayment(payment);
            } else {
                //add
                services.addPayment(new ProblemTestingPayment(coderId, amount, roundId));
            }
        } catch (Exception e) {
            s_trace.error("Failed to get PACTs EJB", e);
        }
    };

    public List getWriterPayments(int componentId) {
        ArrayList ret = new ArrayList();
        //Lookup payments from PACTs here
        try {
            List payments = PactsClientServicesLocator.getService().findPayments(
                ProblemWritingPayment.PROBLEM_WRITING_PAYMENT, componentId);

            s_trace.debug("Got writer payments");
            for(int i =0; i < payments.size(); i++) {
                BasePayment payment = (BasePayment)payments.get(i);
                ret.add(new PaymentInformation((int)payment.getCoderId(),
                        payment.getGrossAmount(), payment.getCurrentStatus().getDesc()+": "+payment.getCurrentStatus().getReasonsText()));
            }
        } catch (Exception e) {
            //ejb lookup failed
            s_trace.error("Failed to get PACTs EJB", e);
        }
        return ret;
    };

    public List getTesterPayments(int componentId) {
        ArrayList ret = new ArrayList();
        //Lookup payments from PACTs here
        try {
            List payments = PactsClientServicesLocator.getService().findPayments(
                ProblemTestingPayment.PROBLEM_TESTING_PAYMENT, componentId);

            s_trace.debug("Got tester payments");
            for(int i =0; i < payments.size(); i++) {
                BasePayment payment = (BasePayment)payments.get(i);
                ret.add(new PaymentInformation((int)payment.getCoderId(),
                        payment.getGrossAmount(), payment.getCurrentStatus().getDesc()+": "+payment.getCurrentStatus().getReasonsText()));
            }
        } catch (Exception e) {
            //ejb lookup failed
            s_trace.error("Failed to get PACTs EJB", e);
        }
        return ret;
    };


    public MPSQASTesterInvoker getMPSQASTester() {
        return mpsqasTester;
    }

    /**
     * Retrieves lookup values from persistence.
     *
     * @return Retrieved lookup values.
     * @throws RemoteException If any error occurs.
     * @since 2.0
     */
    public LookupValues getLookupValues() throws RemoteException {
        LookupValues lookupValues = new LookupValues();
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            lookupValues.setProblemRoundTypes(getProblemRoundTypes(conn));
            lookupValues.setCustomBuildSettings(getCustomBuildSettings(conn));
        } catch (SQLException sqle) {
            String errorMessage = "Failed to get lookup values";
            s_trace.error(errorMessage, sqle);
            throw new RemoteException(errorMessage, sqle);
        } finally {
            close(conn, null, null);
        }
        return lookupValues;
    }

    /**
     * Retrieves problem round types from persistence.
     *
     * @param conn DB connection to use.
     * @return Retrieved problem round types.
     * @throws SQLException If any SQL error occurs.
     * @since 2.0
     */
     private ArrayList<ProblemRoundType> getProblemRoundTypes(Connection conn) throws SQLException {
        ArrayList<ProblemRoundType> result = new ArrayList<ProblemRoundType>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sqlStr = new StringBuilder(256);
        // Construct SQL query.
        sqlStr.append("SELECT ");
        sqlStr.append("problem_round_type_id, ");
        sqlStr.append("problem_round_type_desc, ");
        sqlStr.append("problem_type ");
        sqlStr.append("FROM problem_round_type_lu");
        try {
            ps = conn.prepareStatement(sqlStr.toString());
            // Execute query.
            rs = ps.executeQuery();
            while (rs.next()) {
                // Construct entity from DB table record.
                ProblemRoundType problemRoundType = new ProblemRoundType();
                problemRoundType.setId(rs.getInt(1));
                problemRoundType.setDescription(rs.getString(2));
                problemRoundType.setProblemType(rs.getInt(3));
                // Add entity to the resulting list.
                result.add(problemRoundType);
            }
        } catch (SQLException sqle) {
            s_trace.error("Failed to get problem round types", sqle);
            throw sqle;
        } finally {
            close(null, ps, rs);
        }
        return result;
    }

     /**
      * Retrieves custom build setting lookup values from persistence.
      *
      * @param conn DB connection to use.
      * @return Retrieved custom build settings, grouped by type. Key represents a type (see constants defined
     * in {@link com.topcoder.netCommon.mpsqas.CustomBuildSetting}), value represents
     * a list of settings of the corresponding type.
      * @throws SQLException If any SQL error occurs.
      * @since 2.1
      */
      private HashMap<Integer, ArrayList<CustomBuildSetting>> getCustomBuildSettings(Connection conn)
              throws SQLException {
          HashMap<Integer, ArrayList<CustomBuildSetting>> result =
                  new HashMap<Integer, ArrayList<CustomBuildSetting>>();
         PreparedStatement ps = null;
         ResultSet rs = null;
         StringBuilder sqlStr = new StringBuilder(256);
         // Construct SQL query.
         sqlStr.append("SELECT ");
         sqlStr.append("custom_build_setting_id, ");
         sqlStr.append("custom_build_setting_type_id, ");
         sqlStr.append("custom_build_setting_value, ");
         sqlStr.append("custom_build_setting_desc ");
         sqlStr.append("FROM custom_build_setting");
         try {
             ps = conn.prepareStatement(sqlStr.toString());
             // Execute query.
             rs = ps.executeQuery();
             while (rs.next()) {
                 // Construct entity from DB table record.
                 CustomBuildSetting customBuildSetting = new CustomBuildSetting();
                 customBuildSetting.setId(rs.getInt(1));
                 customBuildSetting.setType(rs.getInt(2));
                 customBuildSetting.setValue(rs.getString(3));
                 customBuildSetting.setDescription(rs.getString(4));
                 // Add entity to the resulting list.
                 if (!result.containsKey(customBuildSetting.getType())) {
                     result.put(customBuildSetting.getType(), new ArrayList<CustomBuildSetting>());
                 }
                 result.get(customBuildSetting.getType()).add(customBuildSetting);
             }
         } catch (SQLException sqle) {
             s_trace.error("Failed to get custom build settings", sqle);
             throw sqle;
         } finally {
             close(null, ps, rs);
         }
         return result;
     }
}
