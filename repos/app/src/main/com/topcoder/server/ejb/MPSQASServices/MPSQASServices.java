package com.topcoder.server.ejb.MPSQASServices;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJBObject;

import com.topcoder.netCommon.mpsqas.ApplicationInformation;
import com.topcoder.netCommon.mpsqas.ContestInformation;
import com.topcoder.netCommon.mpsqas.Correspondence;
import com.topcoder.netCommon.mpsqas.LookupValues;
import com.topcoder.netCommon.mpsqas.UserInformation;
import com.topcoder.netCommon.mpsqas.WebServiceInformation;
import com.topcoder.services.common.MPSQASFiles;

/**
 * Class to declare abstract methods in MPSQASServicesBean
 *
 * <p>
 * Changes in version 1.1 (Release Assembly - Dynamic Round Type List For Long and Individual Problems):
 * <ol>
 * <li>
 * Added {@ling #getLookupValues()} method.
 * </li>
 * </ol>
 * </p>
 *
 * @author talub, gevak
 * @version 1.1
 */
public interface MPSQASServices extends EJBObject {

    /** User Session Methods */
    public ArrayList authenticateUser(String handle, String password) throws RemoteException;

    /** Correspondence Services */
    public boolean sendProblemCorrespondence(Correspondence message, int problemId, int userId) throws RemoteException;

    public boolean sendComponentCorrespondence(Correspondence message, int componentId, int userId) throws RemoteException;

    public ArrayList getUnreadCorrespondence(int userId) throws RemoteException;

    public ArrayList getProblemCorrespondence(int problemId) throws RemoteException;

    public ArrayList getComponentCorrespondence(int componentId) throws RemoteException;

    public ArrayList getProblemCorrespondenceReceivers(int problemId) throws RemoteException;

    public ArrayList getComponentCorrespondenceReceivers(int componentId) throws RemoteException;

    public boolean markProblemCorrespondenceRead(int problemId, int userId) throws RemoteException;

    public boolean markComponentCorrespondenceRead(int componentId, int userId) throws RemoteException;

    /** Compile and Testing Services */
    public ArrayList compileSolution(HashMap files, int language, int componentId, int userId) throws RemoteException;

    public String test(Object[] args, int componentId, int userId, int type) throws RemoteException;

    public MPSQASFiles test(Object[] args, int userId, int componentId, int solutionId, String method) throws RemoteException;

    public MPSQASFiles[] test(Object[][] args, int userId, int componentId, int solutionId, String method) throws RemoteException;

    public ArrayList compareSolutions(int componentId) throws RemoteException;

    /** Contest Services */
    public ArrayList getContests(int userId) throws RemoteException;

    public ContestInformation getContestInformation(int roundId, int userId) throws RemoteException;

    public void wrapUpContest(int roundId) throws RemoteException;

    /** Application Services */
    public ArrayList getPendingApplications() throws RemoteException;

    public ApplicationInformation getApplicationInformation(int applicationId) throws RemoteException;

    public ArrayList saveApplication(String message, int applicationType, int userId) throws RemoteException;

    public ArrayList processApplicationReply(int applicationId, boolean accepted, String message, int userId) throws RemoteException;

    /** User Services */
    public ArrayList getUsers(int forType, int id) throws RemoteException;

    public UserInformation getUserInformation(int userId) throws RemoteException;

    public boolean recordPayment(ArrayList userIds) throws RemoteException;

    public boolean storePendingAmounts(int userId, HashMap amounts) throws RemoteException;

    /** Admin Services */
    public ArrayList processPendingReply(int problemId, boolean approved, String message, int userId) throws RemoteException;

    public boolean setProblemStatus(int problemId, int status) throws RemoteException;

    public boolean setPrimarySolution(int componentId, int primarySolutionId) throws RemoteException;

    public boolean setTestersForProblem(int problemId, ArrayList testerIds) throws RemoteException;

    public boolean setTestersForComponent(int componentId, ArrayList testerIds) throws RemoteException;
    
    public void generateWriterPayment(int componentId, int coderId, double amount) throws RemoteException;
    
    public void generateTesterPayment(int round, int coderId, double amount) throws RemoteException;
    
    public List getWriterPayments(int componentId) throws RemoteException;
    
    public List getTesterPayments(int componentId) throws RemoteException;

    /** Web Service services */
    public WebServiceInformation getWebServiceInformation(int webServiceId, int userId) throws RemoteException;

    public boolean hasPermissionToDeploy(int webServiceId, int userId) throws RemoteException;

    public List getWebServiceServer(int webServiceId) throws RemoteException;

    public boolean setWebServiceServer(int webServiceId, List webServiceFiles) throws RemoteException;

    public ArrayList generateJavaDocs(int webServiceId) throws RemoteException;

    public String getBriefJavaDocs(int webServiceId) throws RemoteException;

    /**
     * Process the test group as finalized.
     * Notes: Used for by listener of the LongTestServices
     *  
     * @param testGroupId Id of the group finalized
     */
    public void testGroupFinalized(int testGroupId) throws RemoteException;
    
    /**
     * Cancel all pending scheduled tests of the user
     * 
     * @param userId Id of the user owning test to cancel 
     * 
     * @return String with information for the user.
     */
    public String cancelTests(int userId) throws RemoteException;

    /**
     * Runs test cases associated to the component against user solution or
     * all compoment solutions
     *  
     * @param componentId Id of the component
     * @param userId Id of the user
     * @param testType MessageConstants#TEST_ONE test user solution;
     *                 MessageConstants#TEST_ALL test all solutions of the component
     * 
     * @return String with information for the user.
     */
    public String systemTests(int componentId, int userId, int testType) throws RemoteException;

    /**
     * Retrieves lookup values from persistence.
     *
     * @return Retrieved lookup values.
     * @throws RemoteException If any error occurs.
     * @since 1.1
     */
    public LookupValues getLookupValues() throws RemoteException;
}
