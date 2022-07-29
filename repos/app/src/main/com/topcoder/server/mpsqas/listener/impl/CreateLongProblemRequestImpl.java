package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.server.mpsqas.room.ViewProblemRoom;
import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import org.apache.log4j.Logger;

public class CreateLongProblemRequestImpl
        extends CreateLongProblemRequest
        implements MessageProcessor, ViewProblemRoom {

    MPSQASProcessorPeer mpeer;

    public void process(Peer peer) {
        mpeer = (MPSQASProcessorPeer) peer;

        if (!mpeer.isLoggedIn()) {
            Logger logger = Logger.getLogger(getClass());

            logger.error("Unauthorized user attempted pending approval room move");
            mpeer.sendErrorMessage("Not logged in");
            return;
        }
        if (!mpeer.isWriter()) {
            Logger logger = Logger.getLogger(getClass());

            logger.error("Non-writer " + mpeer.getUsername() + " (" + mpeer.getUserId() + ") attempted to create a problem");
            mpeer.sendErrorMessage("Not a writer");
            return;
        }
        mpeer.moveToNewRoom(this);
    }

    public void enter(Peer peer) {
        mpeer = (MPSQASProcessorPeer) peer;

        mpeer.setCurrentProblemId(mpeer.NEW_PROBLEM);
        mpeer.sendMessage(new CreateLongProblemResponse());
    }

    public int getProblemId() {
        return mpeer.getCurrentProblemId();
    }
}
