package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.server.mpsqas.room.MainTeamProblemRoom;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.message.MainTeamProblemMoveRequest;
import com.topcoder.netCommon.mpsqas.communication.message.MainTeamProblemMoveResponse;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.netCommon.mpsqas.MessageConstants;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.rmi.RemoteException;

/**
 *
 * @author Logan Hanks
 */
public class MainTeamProblemMoveRequestImpl
        extends MainTeamProblemMoveRequest
        implements MessageProcessor, MainTeamProblemRoom {

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
            MainTeamProblemMoveResponse response = new MainTeamProblemMoveResponse();

            if (mpeer.isTester()) {
                ArrayList problems = mpeer.getProblemServices().getTeamProblems(MessageConstants.USER_TESTING_PROBLEMS, mpeer.getUserId());

                response.addProblems(MessageConstants.USER_TESTING_PROBLEMS, problems);
            }
            if (mpeer.isWriter()) {
                ArrayList problems = mpeer.getProblemServices().getTeamProblems(MessageConstants.USER_WRITTEN_PROBLEMS, mpeer.getUserId());

                response.addProblems(MessageConstants.USER_WRITTEN_PROBLEMS, problems);
            }
            mpeer.sendMessage(response);
        } catch (Exception e) {
            mpeer.sendErrorMessage("Error retrieving problems", e);
        }
    }
}

