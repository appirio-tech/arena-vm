package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.server.mpsqas.room.MainTeamProblemRoom;
import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.netCommon.mpsqas.MessageConstants;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Handles a View All Problems Request by making sure the user is an admin and
 * then getting the problems from the bean.
 *
 * @author mitalub
 */
public class ViewAllTeamProblemsRequestImpl
        extends ViewAllTeamProblemsRequest
        implements MessageProcessor, MainTeamProblemRoom {

    /** Checks if user is an admin, and if so moves to the room. */
    public void process(Peer peer) {
        Logger logger = Logger.getLogger(getClass());
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;
        if (!mpeer.isAdmin()) {
            logger.error("ERROR: Non-admin " + mpeer.getUserId() + " trying to view" +
                    " all team problems.");
            mpeer.sendErrorMessage("Only admins can view all problems.");
        } else {
            mpeer.moveToNewRoom(this);
        }
    }

    /** Gets the list of problems from the bean and sends the move response. */
    public void enter(Peer peer) {
        Logger logger = Logger.getLogger(getClass());
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;
        ArrayList problems = new ArrayList();
        try {
            problems = mpeer.getProblemServices().getTeamProblems(
                    MessageConstants.ALL_PROBLEMS, -1);
        } catch (Exception e) {
            logger.error("Error getting all problems from bean.", e);
            mpeer.sendErrorMessage("Error getting problems from bean.");
        }

        MainTeamProblemMoveResponse m = new MainTeamProblemMoveResponse();
        m.addProblems(MessageConstants.ALL_PROBLEMS, problems);
        mpeer.sendMessage(m);
    }
}
