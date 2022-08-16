package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.server.mpsqas.room.MainProblemRoom;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.message.MainProblemMoveRequest;
import com.topcoder.netCommon.mpsqas.communication.message.MainProblemMoveResponse;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.netCommon.mpsqas.MessageConstants;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 *
 * @author Logan Hanks
 */
public class MainProblemMoveRequestImpl
        extends MainProblemMoveRequest
        implements MessageProcessor, MainProblemRoom {

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
            MainProblemMoveResponse response = new MainProblemMoveResponse();

            if (mpeer.isTester()) {
                ArrayList problems = mpeer.getProblemServices().getSingleProblems(MessageConstants.USER_TESTING_PROBLEMS, mpeer.getUserId());

                response.addProblems(MessageConstants.USER_TESTING_PROBLEMS, problems);
            }
            if (mpeer.isWriter()) {
                ArrayList problems = mpeer.getProblemServices().getSingleProblems(MessageConstants.USER_WRITTEN_PROBLEMS, mpeer.getUserId());

                response.addProblems(MessageConstants.USER_WRITTEN_PROBLEMS, problems);
            }
            mpeer.sendMessage(response);
        } catch (Exception e) {
            mpeer.sendErrorMessage("Error retrieving problems", e);
        }
    }
}

