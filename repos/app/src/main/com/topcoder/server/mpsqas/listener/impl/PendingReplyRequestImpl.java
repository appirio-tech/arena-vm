package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.netCommon.mpsqas.communication.message.PendingReplyRequest;
import com.topcoder.netCommon.mpsqas.communication.message.NewStatusMessage;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Processes an admins reply to a pending problem by making use of the bean.
 *
 * @author mitalub
 */
public class PendingReplyRequestImpl
        extends PendingReplyRequest
        implements MessageProcessor {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;
        Logger logger = Logger.getLogger(getClass());

        if (!mpeer.isAdmin()) {
            logger.info(mpeer.getUserId() + " is trying to reply to a proposal but "
                    + "is not an admin.");
            mpeer.sendErrorMessage("You must be an admin to reply to a problem.");
        }

        try {
            ArrayList result = mpeer.getServices().processPendingReply(
                    mpeer.getCurrentProblemId(),
                    isApproved(),
                    getMessage(),
                    mpeer.getUserId());
            if (Boolean.TRUE.equals(result.get(0))) {
                mpeer.sendMessage(new NewStatusMessage("Reply submitted."));
            } else {
                mpeer.sendMessage(new NewStatusMessage(true, (String) result.get(1)));
            }
        } catch (Exception e) {
            logger.error("Error submitting pending reply.", e);
            mpeer.sendErrorMessage("Server error submitting pending reply:", e);
        }
    }
}
