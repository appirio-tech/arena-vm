package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.netCommon.mpsqas.communication.message.CompileRequest;
import com.topcoder.netCommon.mpsqas.communication.message.NewStatusMessage;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Handles a compile request by calling the bean.
 *
 * @author mitalub
 */
public class CompileRequestImpl
        extends CompileRequest
        implements MessageProcessor {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;
        Logger logger = Logger.getLogger(getClass());

        try {
            ArrayList results = mpeer.getServices().compileSolution(getCodeFiles(),
                    getLanguage().getId(),
                    mpeer.getCurrentComponentId(),
                    mpeer.getUserId());
            if (Boolean.TRUE.equals(results.get(0))) {
                mpeer.sendMessage(new NewStatusMessage("Compile successful."));
            } else {
                mpeer.sendMessage(new NewStatusMessage("<pre>" +
                        (String) results.get(1) + "</pre>"));
            }
        } catch (Exception e) {
            logger.error("Exception submitting compile.", e);
            mpeer.sendErrorMessage("Server error when submitting compile.");
        }
    }
}
