package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.shared.problem.HTMLCharacterHandler;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.netCommon.mpsqas.MessageConstants;
import com.topcoder.netCommon.mpsqas.communication.message.TestRequest;
import com.topcoder.netCommon.mpsqas.communication.message.NewStatusMessage;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import org.apache.log4j.Logger;


/**
 * Handles a TestRequest by calling the bean.
 *
 * @author mitalub
 */
public class TestRequestImpl
        extends TestRequest
        implements MessageProcessor {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;
        Logger logger = Logger.getLogger(getClass());

        try {
            logger.info("params: " + getParameters());
            String results = mpeer.getServices().test(
                    getParameters(),
                    mpeer.getCurrentComponentId(),
                    mpeer.getUserId(),
                    getTestType());

            mpeer.sendMessage(new NewStatusMessage(
                    "<pre>" + HTMLCharacterHandler.encodeSimple(results) + "</pre>"));
        } catch (Exception e) {
            logger.error("Exception submitting test.", e);
            mpeer.sendErrorMessage("Server error when submitting test.");
        }

    }
}
