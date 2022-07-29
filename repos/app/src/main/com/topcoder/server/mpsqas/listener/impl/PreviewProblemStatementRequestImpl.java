package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.netCommon.mpsqas.MessageConstants;
import com.topcoder.shared.problemParser.ProblemComponentFactory;
import com.topcoder.shared.problem.ProblemComponent;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 *
 * @author Logan Hanks
 */
public class PreviewProblemStatementRequestImpl
        extends PreviewProblemStatementRequest
        implements MessageProcessor {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (!mpeer.isLoggedIn()) {
            Logger logger = Logger.getLogger(getClass());

            logger.error("Unauthorized user attempted to preview problem "
                    + "statement");
            mpeer.sendErrorMessage("Not logged in");
            return;
        }

        try {
            if (getType() == MessageConstants.COMPONENT_STATEMENT) {
                ProblemComponent stmt = mpeer.getProblemServices()
                        .parseProblemStatement(getComponent().toXML(), true,
                                getComponent().getComponentId());
                mpeer.sendMessage(new PreviewProblemStatementResponse(stmt));
            } else {
                //for an entire problem, gotta parse each component
                for (int i = 0; i < getProblem().getProblemComponents().length; i++) {
                    getProblem().getProblemComponents()[i] =
                            mpeer.getProblemServices().parseProblemStatement(
                                    getProblem().getProblemComponents()[i].toXML(),
                                    true,
                                    getProblem().getProblemComponents()[i]
                            .getComponentId());
                }
                mpeer.sendMessage(new PreviewProblemStatementResponse(
                        getProblem()));
            }
        } catch (Exception e) {
            mpeer.sendErrorMessage("Error parsing problem statement:\n", e);
        }
    }
}
