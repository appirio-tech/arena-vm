package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.netCommon.mpsqas.MessageConstants;
import com.topcoder.netCommon.mpsqas.ProblemInformation;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class SaveProblemStatementRequestImpl
        extends SaveProblemStatementRequest
        implements MessageProcessor {

    public void process(Peer peer) {
        Logger logger = Logger.getLogger(getClass());
        logger.info("processing save problem request.");
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        ArrayList saveResult = null;
        try {
            if (!mpeer.isLoggedIn()) {
                logger.error("User not logged in and trying to save statement: ID:"
                        + mpeer.getId());
                mpeer.sendErrorMessage("Not logged in.");
                return;
            }

            if (getType() == MessageConstants.COMPONENT_STATEMENT) {
                saveResult = mpeer.getProblemServices().saveComponentStatement(
                        getComponent(), mpeer.getUserId(), mpeer.getId());
            } else {
                saveResult = mpeer.getProblemServices().saveProblemStatement(
                        getProblem(), mpeer.getUserId(), mpeer.getId());
            }
            if (Boolean.TRUE.equals(saveResult.get(0))) {
                mpeer.sendMessage(new NewStatusMessage("Statement saved."));
            } else {
                mpeer.sendErrorMessage((String) saveResult.get(1));
            }
        } catch (Exception e) {
            logger.error("Error saving statment.", e);
            mpeer.sendErrorMessage("Error saving statement.", e);
        }
    }
}
