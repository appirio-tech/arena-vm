package com.topcoder.server.ejb.ProblemServices;

import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.ejb.EJBObject;

import com.topcoder.netCommon.mpsqas.ComponentInformation;
import com.topcoder.netCommon.mpsqas.ProblemInformation;
import com.topcoder.shared.problem.Problem;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.SimpleComponent;

public interface ProblemServices extends EJBObject {

    //for arena
    public Problem getProblem(int problemId)
            throws RemoteException, ProblemServicesException;

    public ProblemComponent getProblemComponent(int problemId, boolean unsafe)
            throws RemoteException, ProblemServicesException;
    
	public ProblemComponent getProblemComponent(int componentId, boolean unsafe, boolean loadTests,
			boolean loadCategories) throws RemoteException, ProblemServicesException;

    //for mpsqas
    public ProblemInformation getProblemInformation(int problemId, int userId)
            throws RemoteException;

    public ComponentInformation getComponentInformation(int componentId,
            int userId)
            throws RemoteException;

    public ArrayList saveProblem(ProblemInformation problem, int userId,
            int connectionId)
            throws RemoteException;

    public ArrayList saveComponent(ComponentInformation problem, int userId,
            int connectionId)
            throws RemoteException;

    public ArrayList getSingleProblems(int forType, int id)
            throws RemoteException, ProblemServicesException;

    public ArrayList getTeamProblems(int forType, int id)
            throws RemoteException, ProblemServicesException;

    public ArrayList getLongProblems(int forType, int id)
        throws RemoteException, ProblemServicesException;

    public ArrayList saveProblemStatement(Problem problem, int userId,
            int connectionId)
            throws RemoteException;

    public ArrayList saveComponentStatement(ProblemComponent component,
            int userId, int connectionId)
            throws RemoteException;

    public ProblemComponent parseProblemStatement(String xml, boolean unsafe,
            int componentId)
            throws RemoteException, ProblemServicesException;

    public ArrayList refreshTestCases(int roundId, int userId)
            throws RemoteException, ProblemServicesException;

    public int getSolutionId(int problemId, int userId)
            throws RemoteException;

    /**
     * Returns the Memory Limit in Megabytes for the component
     *
     * @param componentId Id of the Component
     * @return the Memory Limits in MB
     *
     * @throws RemoteException
     */
    public int getMemLimitForComponent(int componentId) throws RemoteException;

    /**
     * Finds the id of the primary solutionfor a specified component
     *
     * @param componentId Id of the component for which the primary solution is searched
     *
     * @return Id of the solution, -1 if it is not found.
     * @throws RemoteException
     */
    public int getPrimarySolutionId(int componentId) throws RemoteException;

    public SimpleComponent getSimpleComponent(int componentID)
            throws RemoteException, ProblemServicesException;

    public int getUserTypeForComponent(int componentId, int userId) throws RemoteException, Exception;

    public int getUserTypeForProblem(int problemId, int userId) throws RemoteException, Exception;

    //ScreeningProblemLabel[] getComponentLabels(int userID, int problemType) throws RemoteException;

    //Added By Budi
    //public ScreeningProblemLabel getComponentLabel(int type, int id) throws RemoteException, ProblemServicesException;


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
    public int[] addTestCasesToComponent(int componentId, Object[][] args, Object[] results) throws ProblemServicesException, RemoteException;
}
