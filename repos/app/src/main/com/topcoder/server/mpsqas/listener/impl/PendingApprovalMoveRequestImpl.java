package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.server.mpsqas.room.PendingApprovalRoom;
import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.netCommon.mpsqas.StatusConstants;
import com.topcoder.netCommon.mpsqas.MessageConstants;
import com.topcoder.server.common.ServerContestConstants;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.rmi.RemoteException;

/**
 *
 * @author Logan Hanks
 */
public class PendingApprovalMoveRequestImpl
        extends PendingApprovalMoveRequest
        implements MessageProcessor, PendingApprovalRoom {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (!mpeer.isLoggedIn()) {
            Logger logger = Logger.getLogger(getClass());

            logger.error("Unauthorized user attempted pending approval "
                    + "room move");
            mpeer.sendErrorMessage("Not logged in");
            return;
        }
        if (!mpeer.isAdmin()) {
            Logger logger = Logger.getLogger(getClass());

            logger.error("Non-admin " + mpeer.getUsername() + " (" +
                    mpeer.getUserId()
                    + ") attempted pending approval room move");
            mpeer.sendErrorMessage("Not an admin");
            return;
        }
        mpeer.moveToNewRoom(this);
    }

    public void enter(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        try {
            ArrayList problemsPendingApproval = mpeer.getProblemServices()
                    .getSingleProblems(MessageConstants.PROBLEMS_WITH_STATUS,
                            StatusConstants.PROPOSAL_PENDING_APPROVAL);
            ArrayList problemsPendingSubmission = mpeer.getProblemServices()
                    .getSingleProblems(MessageConstants.PROBLEMS_WITH_STATUS,
                            StatusConstants.SUBMISSION_PENDING_APPROVAL);

            MainProblemMoveResponse response = new MainProblemMoveResponse();

            response.addProblems(MessageConstants.PENDING_APPROVAL_PROBLEMS,
                    problemsPendingApproval);
            response.addProblems(MessageConstants.PENDING_SUBMISSION_PROBLEMS,
                    problemsPendingSubmission);
            mpeer.sendMessage(response);
        } catch (Exception e) {
            mpeer.sendErrorMessage("Error retrieving problems", e);
        }
    }
}
