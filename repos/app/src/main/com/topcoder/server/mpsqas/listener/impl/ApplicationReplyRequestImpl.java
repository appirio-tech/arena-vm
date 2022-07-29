package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.netCommon.mpsqas.communication.message.ApplicationReplyRequest;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.message.ApplicationReplyResponse;
import com.topcoder.netCommon.mpsqas.communication.Peer;

import java.util.ArrayList;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;

/**
 *
 * @author Logan Hanks
 */
public class ApplicationReplyRequestImpl
        extends ApplicationReplyRequest
        implements MessageProcessor {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (mpeer.isAdmin()) {
            try {
                ArrayList result = mpeer.getServices().processApplicationReply(mpeer.getCurrentApplicationId(), isAccepted(), getMessage(), mpeer.getUserId());

                boolean success = ((Boolean) result.get(0)).booleanValue();

                if (success) {
                } else {
                    mpeer.sendMessage(new ApplicationReplyResponse(false, result.get(1).toString()));
                }
            } catch (RemoteException e) {
                mpeer.sendErrorMessage("Error processing application reply", e);
                ;
            }
        } else {
            Logger logger = Logger.getLogger(getClass());

            logger.warn("User " + mpeer.getUsername() + " (" + mpeer.getUserId() + ") attempted to reply to an application");
        }
    }
}

