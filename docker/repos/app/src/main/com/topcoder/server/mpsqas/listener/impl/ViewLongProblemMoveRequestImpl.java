/**
 * @author mktong
 */
package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.server.mpsqas.room.ViewLongProblemRoom;
import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.netCommon.mpsqas.ApplicationConstants;
import com.topcoder.netCommon.mpsqas.ProblemInformation;
import org.apache.log4j.Logger;

public class ViewLongProblemMoveRequestImpl
        extends ViewLongProblemMoveRequest
        implements MessageProcessor, ViewLongProblemRoom {

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
                mpeer.sendErrorMessage("Could not retrieve problem information. "
                        + "Do you have permission to view it?");
            } else {
                mpeer.setCurrentProblemId(getProblemId());
                mpeer.setCurrentComponentId(p.getProblemComponents()[0]
                        .getComponentId());
                ViewLongProblemMoveResponse response = new ViewLongProblemMoveResponse(p,
                        p.getUserType() != ApplicationConstants.PROBLEM_TESTER);

                mpeer.sendMessage(response);
            }
        } catch (Exception e) {
            mpeer.sendErrorMessage("Error viewing problem:", e);
        }
    }
}
