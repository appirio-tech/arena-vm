package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.server.mpsqas.room.ViewTeamProblemRoom;
import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.netCommon.mpsqas.ApplicationConstants;
import com.topcoder.netCommon.mpsqas.ProblemInformation;
import org.apache.log4j.Logger;

/**
 * Process a ViewTeamProblemMoveRequest by getting the problem information
 * from the bean.  If the request specifies that this is a cached move,
 * the response is not sent, it assumes the applet did the move itself.
 *
 * @author mitalub
 */
public class ViewTeamProblemMoveRequestImpl
        extends ViewTeamProblemMoveRequest
        implements MessageProcessor, ViewTeamProblemRoom {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (!mpeer.isLoggedIn()) {
            Logger logger = Logger.getLogger(getClass());

            logger.error("Unauthorized user attempting to view a problem.");
            mpeer.sendErrorMessage("Not logged in");
            return;
        }

        mpeer.moveToNewRoom(this);
    }

    public void enter(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;
        Logger logger = Logger.getLogger(getClass());

        try {
            ProblemInformation p = mpeer.getProblemServices()
                    .getProblemInformation(getProblemId(),
                            mpeer.getUserId());
            if (p == null) {
                logger.error("Bean returned null getting ProblemInformation.");
                mpeer.sendErrorMessage("Could not retrieve problem "
                        + "information. Do you have permission to view it?");
            } else {
                ViewTeamProblemMoveResponse response =
                        new ViewTeamProblemMoveResponse(p,
                                p.getUserType() != ApplicationConstants.PROBLEM_TESTER);
                mpeer.sendMessage(response);
            }
            mpeer.setCurrentProblemId(getProblemId());
        } catch (Exception e) {
            mpeer.sendErrorMessage("Error getting problem information:", e);
        }
    }
}


