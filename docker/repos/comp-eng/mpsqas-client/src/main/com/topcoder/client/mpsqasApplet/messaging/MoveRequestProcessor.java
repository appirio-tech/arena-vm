package com.topcoder.client.mpsqasApplet.messaging;

/**
 * Defines methods for a move request processor.
 *
 * @author mitalub
 */
public interface MoveRequestProcessor {

    public void viewProblem(int problemId);

    public void viewContest(int contestId);

    public void viewApplication(int applicationId);

    public void viewUser(int userId);

    public void viewTeamProblem(int problemId);
    
    public void viewLongProblem(int problemId);

    public void viewComponent(int componentId);

    public void viewWebService(int componentId);

    public void loadWriterApplication();

    public void loadTesterApplication();

    public void loadAllProblems();

    public void loadAllTeamProblems();
    
    public void loadAllLongProblems();

    public void moveRelative(int distance);

    public void jump(String pattern);

    public void loadFoyerRoom();

    public void loadMainApplicationRoom();

    public void loadMainProblemRoom();

    public void loadMainTeamProblemRoom();
    
    public void loadMainLongProblemRoom();

    public void loadMainUserRoom();

    public void loadMainContestRoom();

    public void loadPendingApprovalRoom();

    public void loadTeamPendingApprovalRoom();
    
    public void loadLongPendingApprovalRoom();

    public void createProblem();

    public void createTeamProblem();
    
    public void createLongProblem();
}
