package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.server.mpsqas.room.MainLongProblemRoom;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.message.MainLongProblemMoveRequest;
import com.topcoder.netCommon.mpsqas.communication.message.MainLongProblemMoveResponse;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.netCommon.mpsqas.MessageConstants;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 *
 * @author mktong
 */
public class MainLongProblemMoveRequestImpl
        extends MainLongProblemMoveRequest
        implements MessageProcessor, MainLongProblemRoom {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (!mpeer.isLoggedIn()) {
            Logger logger = Logger.getLogger(getClass());

            logger.error("Unauthorized user attempted upcoming contests room move");
            mpeer.sendErrorMessage("Not logged in");
            return;
        }

        mpeer.moveToNewRoom(this);
    }

    public void enter(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        try {
            MainLongProblemMoveResponse response = new MainLongProblemMoveResponse();

            if (mpeer.isTester()) {
                ArrayList problems = mpeer.getProblemServices().getLongProblems(MessageConstants.USER_TESTING_PROBLEMS, mpeer.getUserId());

                response.addProblems(MessageConstants.USER_TESTING_PROBLEMS, problems);
            }
            if (mpeer.isWriter()) {
                ArrayList problems = mpeer.getProblemServices().getLongProblems(MessageConstants.USER_WRITTEN_PROBLEMS, mpeer.getUserId());

                response.addProblems(MessageConstants.USER_WRITTEN_PROBLEMS, problems);
            }
            mpeer.sendMessage(response);
        } catch (Exception e) {
            mpeer.sendErrorMessage("Error retrieving problems", e);
        }
    }
}

