/*
 * SystemTestRequestImpl
 * 
 * Created 04/27/2006
 */
package com.topcoder.server.mpsqas.listener.impl;

import org.apache.log4j.Logger;

import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.message.NewStatusMessage;
import com.topcoder.netCommon.mpsqas.communication.message.SystemTestRequest;


/**
 * Listener implementation of the SystemTestRequest
 * 
 * @author Diego Belfer (Mural)
 * @version $Id: SystemTestRequestImpl.java 54869 2006-12-01 18:02:46Z thefaxman $
 */
public class SystemTestRequestImpl
        extends SystemTestRequest
        implements MessageProcessor {

    
    
    /**
     * Invokes systemTests method on the MPSQASServices for the user
     * of the current peer
     */
    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;
        Logger logger = Logger.getLogger(getClass());

        try {
            String results = mpeer.getServices().systemTests(
                    mpeer.getCurrentComponentId(), 
                    mpeer.getUserId(), 
                    getTestType());
            mpeer.sendMessage(new NewStatusMessage("<pre>" + results + "</pre>"));
        } catch (Exception e) {
            logger.error("Exception invoking system tests.", e);
            mpeer.sendErrorMessage("Server error when invoking system test.");
        }

    }
}
