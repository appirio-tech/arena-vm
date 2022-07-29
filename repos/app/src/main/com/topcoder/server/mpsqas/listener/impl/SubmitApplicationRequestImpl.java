package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.netCommon.mpsqas.communication.message.SubmitApplicationRequest;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.message.NewStatusMessage;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.rmi.RemoteException;

/**
 *
 * @author Logan Hanks
 */
public class SubmitApplicationRequestImpl
        extends SubmitApplicationRequest
        implements MessageProcessor {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (!mpeer.isLoggedIn()) {
            Logger logger = Logger.getLogger(getClass());

            logger.error("Unauthorized user attempted view user room move");
            mpeer.sendErrorMessage("Not logged in");
            return;
        }

        int appType = mpeer.getCurrentApplicationId();

        if (appType == -1) {
            mpeer.sendErrorMessage("Request an application type first");
            return;
        }

        try {
            ArrayList result = mpeer.getServices().saveApplication(getApplicationTest(), appType, mpeer.getUserId());

            if (((Boolean) result.get(0)).booleanValue()) {
                mpeer.sendMessage(new NewStatusMessage("Application saved"));
            } else {
                mpeer.sendMessage(new NewStatusMessage(true, result.get(1).toString()));
            }
        } catch (Exception e) {
            mpeer.sendErrorMessage("Error saving application", e);
        }
    }
}
