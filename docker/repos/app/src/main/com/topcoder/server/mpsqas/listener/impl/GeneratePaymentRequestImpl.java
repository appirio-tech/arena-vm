package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * Calls on the bean to save admin problem information
 *
 * @author mitalub
 */
public class GeneratePaymentRequestImpl
        extends GeneratePaymentRequest
        implements MessageProcessor {

    public void process(Peer peer) {
        Logger logger = Logger.getLogger(getClass());

        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (!mpeer.isAdmin()) {
            logger.error("Unauthorized user attempted to save admin stuff.");
            mpeer.sendErrorMessage("Not an admin.");
        } else {
            logger.info("Processing payment for " + this.getCoderID() + ":" + this.getAmount() + "(" + this.getType());
            
            //process payment in MPSQASServicesBean
            try {
                if(this.getType() == this.WRITER_PAYMENT) {
                    mpeer.getServices().generateWriterPayment(mpeer.getCurrentProblemId(),this.getCoderID(), this.getAmount());
                } else if(this.getType() == this.TESTER_PAYMENT) {
                    mpeer.getServices().generateTesterPayment(this.getRoundID(),this.getCoderID(), this.getAmount());
                }
            } catch (Exception e) {
                logger.error("EJB error", e);
            }
            
            //send payment result to user
            try {
                mpeer.sendMessage(new GeneratePaymentResponse(
                        (ArrayList)mpeer.getServices().getWriterPayments(mpeer.getCurrentProblemId()),
                        (ArrayList)mpeer.getServices().getTesterPayments(this.getRoundID())));
            } catch (Exception e) {
                logger.error("EJB error", e);
                mpeer.sendErrorMessage("Error saving payments");
            }
        }
    }
}
