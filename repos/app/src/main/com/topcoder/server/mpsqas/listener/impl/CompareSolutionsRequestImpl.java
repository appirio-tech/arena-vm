package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.netCommon.mpsqas.ApplicationConstants;
import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 *  Calls on the bean to test all solutions against all test cases.
 *
 *  @author mitalub
 */
public class CompareSolutionsRequestImpl
        extends CompareSolutionsRequest
        implements MessageProcessor {

    public void process(Peer peer) {
        Logger logger = Logger.getLogger(getClass());
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;
        try {
            ArrayList results = mpeer.getServices().compareSolutions(
                    mpeer.getCurrentComponentId());
            mpeer.sendMessage("<pre>" + (String) results.get(1) + "</pre>");
        } catch (Exception e) {
            logger.error("Error calling on bean to compare solutions.", e);
            mpeer.sendErrorMessage(ApplicationConstants.SERVER_ERROR);
        }
    }
}
