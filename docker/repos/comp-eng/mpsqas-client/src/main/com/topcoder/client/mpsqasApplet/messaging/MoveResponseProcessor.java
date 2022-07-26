package com.topcoder.client.mpsqasApplet.messaging;

import com.topcoder.netCommon.mpsqas.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An interface for a class which is interested in hearing about move
 * responses.
 *
 * @author mitalub
 */
public interface MoveResponseProcessor {

    public void loadApplicationRoom(int applicationType);

    public void loadFoyerRoom(ArrayList problems);

    public void loadMainApplicationRoom(ArrayList applications);

    public void loadMainProblemRoom(HashMap problems);

    public void loadMainUserRoom(ArrayList users);

    public void loadMainContestRoom(ArrayList contests);

    public void loadViewApplicationRoom(ApplicationInformation application);

    public void loadViewContestRoom(ContestInformation contest);

    public void loadViewProblemRoom(ProblemInformation problem,
            boolean statementEditable);

    public void loadViewTeamProblemRoom(ProblemInformation problem,
            boolean statementEditable);
    
    public void loadViewLongProblemRoom(ProblemInformation problem,
            boolean statementEditable);

    public void loadViewUserRoom(UserInformation user);

    public void loadMovingRoom();

    public void loadLoginRoom();

    public void loadMainTeamProblemRoom(HashMap problems);
    
    public void loadMainLongProblemRoom(HashMap problems);

    public void loadEmptyViewProblemRoom();

    public void loadEmptyViewTeamProblemRoom();
    
    public void loadEmptyViewLongProblemRoom();

    public void loadWebServiceRoom(WebServiceInformation webServiceInformation,
            boolean editable);

    public void loadViewComponentRoom(ComponentInformation componentInformation,
            boolean isStatementEditable);
}
