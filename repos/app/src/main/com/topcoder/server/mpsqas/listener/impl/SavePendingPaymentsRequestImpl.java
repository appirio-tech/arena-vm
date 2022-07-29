package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.ApplicationConstants;
import org.apache.log4j.Logger;

/**
 * @author mitalub
 */
public class SavePendingPaymentsRequestImpl
        extends SavePendingPaymentsRequest
        implements MessageProcessor {

    public void process(Peer peer) {
        Logger logger = Logger.getLogger(getClass());
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (mpeer.isAdmin()) {
            boolean success = false;
            try {
                success = mpeer.getServices().storePendingAmounts(
                        mpeer.getCurrentUserId(), getPayments());
            } catch (Exception e) {
                logger.error("Error saving payments.", e);
            }

            if (success) {
                mpeer.sendMessage("Pending payments saved.");
            } else {
                mpeer.sendErrorMessage(ApplicationConstants.SERVER_ERROR);
            }
        } else {
            logger.info("Non-admin user " + mpeer.getUsername() + " trying to "
                    + "save payment info.");
            mpeer.sendErrorMessage("Not an admin.");
        }
    }
}
