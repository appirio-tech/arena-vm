package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.server.mpsqas.room.ViewApplicationRoom;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.message.ViewApplicationMoveRequest;
import com.topcoder.netCommon.mpsqas.communication.message.ViewApplicationMoveResponse;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.netCommon.mpsqas.ApplicationInformation;
import org.apache.log4j.Logger;

/**
 *
 * @author Logan Hanks
 */
public class ViewApplicationMoveRequestImpl
        extends ViewApplicationMoveRequest
        implements MessageProcessor, ViewApplicationRoom {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (!mpeer.isLoggedIn()) {
            Logger logger = Logger.getLogger(getClass());

            logger.error("Unauthorized user attempted view application room move");
            mpeer.sendErrorMessage("Not logged in");
            return;
        }
        mpeer.moveToNewRoom(this);

    }

    public void enter(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        try {
            ApplicationInformation info = mpeer.getServices().getApplicationInformation(getApplicationId());

            mpeer.setCurrentApplicationId(getApplicationId());
            mpeer.sendMessage(new ViewApplicationMoveResponse(info));
        } catch (Exception e) {
            mpeer.sendErrorMessage("Error retrieving application", e);
        }
    }
}
