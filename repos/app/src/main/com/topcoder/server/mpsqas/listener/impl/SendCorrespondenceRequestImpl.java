package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.netCommon.mpsqas.ApplicationConstants;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.server.mpsqas.room.*;
import com.topcoder.netCommon.mpsqas.communication.message.Room;
import com.topcoder.netCommon.mpsqas.communication.message.SendCorrespondenceRequest;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import org.apache.log4j.Logger;

/**
 * Calls on the bean to send a correspondence message.
 *
 * @author mitalub
 */
public class SendCorrespondenceRequestImpl
        extends SendCorrespondenceRequest
        implements MessageProcessor {

    public void process(Peer peer) {
        Logger logger = Logger.getLogger(getClass());

        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (!mpeer.isLoggedIn()) {
            logger.error("Unauthorized user attempted to send correspondence.");
            mpeer.sendErrorMessage("Not logged in");
        } else {
            try {
                Room currentRoom = mpeer.getCurrentRoom();
                if (currentRoom instanceof ViewTeamProblemRoom) {
                    if (mpeer.getCurrentProblemId() == -1) {
                        mpeer.sendErrorMessage("This component does not exist yet. "
                                + "Please save it before sending correspondence.");
                    } else if (mpeer.getServices().sendProblemCorrespondence(
                            getCorrespondence(), mpeer.getCurrentProblemId(),
                            mpeer.getUserId())) {
                        mpeer.sendMessage("Correspondence sent.");
                    } else {
                        mpeer.sendErrorMessage(ApplicationConstants.SERVER_ERROR);
                    }
                } else {
                    if (mpeer.getCurrentComponentId() == -1) {
                        mpeer.sendErrorMessage("This component does not exist yet. "
                                + "Please save it before sending correspondence.");
                    } else if (mpeer.getServices().sendComponentCorrespondence(
                            getCorrespondence(), mpeer.getCurrentComponentId(),
                            mpeer.getUserId())) {
                        mpeer.sendMessage("Correspondence sent.");
                    } else {
                        mpeer.sendErrorMessage(ApplicationConstants.SERVER_ERROR);
                    }
                }
            } catch (Exception e) {
                logger.error("Error sending component.", e);
                mpeer.sendErrorMessage(ApplicationConstants.SERVER_ERROR, e);
            }
        }
    }
}
