package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.server.mpsqas.room.ViewComponentRoom;
import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.netCommon.mpsqas.MessageConstants;
import com.topcoder.netCommon.mpsqas.ApplicationConstants;
import com.topcoder.netCommon.mpsqas.ComponentInformation;
import org.apache.log4j.Logger;

/**
 *  Processes a ViewComponentMoveRequest by getting the ComponentInformation
 *  from the bean and using it in a response.  If the request is a cached move
 *  the response to the client is not sent, the server just makes a note
 *  of the move, it is assumed the client has cached the ComponentInformation
 *  and is using the cached one.
 *
 *  @author mitalub
 */
public class ViewComponentMoveRequestImpl
        extends ViewComponentMoveRequest
        implements MessageProcessor, ViewComponentRoom {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (!mpeer.isLoggedIn()) {
            Logger logger = Logger.getLogger(getClass());

            logger.error("Unauthorized user attempting to view a component.");
            mpeer.sendErrorMessage("Not logged in");
            return;
        }

        mpeer.moveToNewRoom(this);
    }

    public void enter(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;
        Logger logger = Logger.getLogger(getClass());

        try {
            ComponentInformation c = mpeer.getProblemServices()
                    .getComponentInformation(getComponentId(),
                            mpeer.getUserId());
            if (c == null) {
                logger.error("Bean returned null getting component.");
                mpeer.sendErrorMessage("Could not retrieve component "
                        + "information.  Do you have permission to view it? ");
            } else {
                mpeer.setCurrentProblemId(c.getProblemId());
                ViewComponentMoveResponse response = new
                        ViewComponentMoveResponse(c,
                                c.getUserType() != ApplicationConstants.PROBLEM_TESTER);
                mpeer.sendMessage(response);
            }

            mpeer.setCurrentComponentId(getComponentId());
        } catch (Exception e) {
            mpeer.sendErrorMessage("Error viewing component:", e);
        }
    }
}


