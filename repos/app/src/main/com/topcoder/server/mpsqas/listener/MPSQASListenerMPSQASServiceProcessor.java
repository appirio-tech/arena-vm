/*
 * MPSQASListenerMPSQASServiceProcessor
 * 
 * Created 04/26/2006
 */
package com.topcoder.server.mpsqas.listener;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.topcoder.netCommon.mpsqas.communication.message.NewStatusMessage;
import com.topcoder.server.ejb.MPSQASServices.event.MPSQASServiceEventListener;
import com.topcoder.server.ejb.MPSQASServices.event.MPSQASTestResult;
import com.topcoder.server.mpsqas.listener.impl.MPSQASProcessorPeer;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.HTMLCharacterHandler;

/**
 * Used by the MPSQASListener 
 * to process incoming events from the MPSQASServices.
 * 
 * @author Diego Belfer (Mural)
 * @version $Id: MPSQASListenerMPSQASServiceProcessor.java 72316 2008-08-14 07:55:46Z qliu $
 */
public class MPSQASListenerMPSQASServiceProcessor implements MPSQASServiceEventListener.MPSQASServiceEventProcessor {
    private Logger logger = Logger.getLogger(getClass());
    private MPSQASProcessor mpsqasProcessor;
    
    public MPSQASListenerMPSQASServiceProcessor(MPSQASProcessor mpsqasProcessor) {
        this.mpsqasProcessor  = mpsqasProcessor;
    }
    
    /**
     * Receives the test result from MPSQASServices and
     * sends a message with the text to all connection of the user 
     * that scheduled the test 
     */
    public void availableTestResults(MPSQASTestResult result) {
        if (logger.isDebugEnabled()) {
            logger.debug("availableTestResults received for user" + result.getUserId());
        }
        int userId = result.getUserId();
        List peers = mpsqasProcessor.getPeers();
        for (Iterator it = peers.iterator(); it.hasNext();) {
            MPSQASProcessorPeer peer = (MPSQASProcessorPeer) it.next();
            if (peer.getUserId() == userId) {
                if (logger.isDebugEnabled()) {
                    logger.debug("sending result of test to connection :" + peer.getId());
                }
                peer.sendMessage(new NewStatusMessage(
                        "<pre>" + HTMLCharacterHandler.encodeSimple(result.getResultText()) + "</pre>"));
            }
        }
    }
}
