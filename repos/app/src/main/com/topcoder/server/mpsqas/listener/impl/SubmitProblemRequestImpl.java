package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.netCommon.mpsqas.ProblemIdStructure;
import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.rmi.RemoteException;

/**
 * Calls on the bean to submit a problem.
 */
public class SubmitProblemRequestImpl
        extends SubmitProblemRequest
        implements MessageProcessor {

    public void process(Peer peer) {
        Logger logger = Logger.getLogger(getClass());
        logger.info("processing submit problem request.");

        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (!mpeer.isLoggedIn()) {
            logger.error("Unauthorized user attempted to submit problem.");
            mpeer.sendErrorMessage("Not logged in");
        } else {
            try {
                ArrayList result = mpeer.getProblemServices().saveProblem(getProblem(),
                        mpeer.getUserId(),
                        mpeer.getId());

                if (((Boolean) result.get(0)).booleanValue()) {
                    mpeer.sendMessage(new NewStatusMessage(false,
                            "Problem submitted."));
                    ProblemIdStructure idstruct = (ProblemIdStructure) result.get(1);
                    mpeer.setCurrentProblemId(idstruct.getProblemId());
                    if (mpeer.getCurrentRoom() instanceof ViewProblemMoveRequest) {
                        mpeer.setCurrentComponentId(idstruct.getComponents()[0]
                                .getComponentId());
                    }
                    mpeer.sendMessage(new NewProblemIdStructureResponse(idstruct));
                } else {
                    mpeer.sendErrorMessage((String) result.get(1));
                }
            } catch (Exception e) {
                mpeer.sendErrorMessage("Server Error when submitting problem.", e);
            }
        }
    }
}
