package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.server.mpsqas.room.*;
import com.topcoder.netCommon.mpsqas.ComponentIdStructure;
import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.rmi.RemoteException;

/**
 * Calls on the bean to save admin problem information
 *
 * @author mitalub
 */
public class SaveAdminInformationRequestImpl
        extends SaveAdminInformationRequest
        implements MessageProcessor {

    public void process(Peer peer) {
        Logger logger = Logger.getLogger(getClass());

        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (!mpeer.isAdmin()) {
            logger.error("Unauthorized user attempted to save admin stuff.");
            mpeer.sendErrorMessage("Not an admin.");
        } else {
            StringBuffer results = new StringBuffer(256);
            if (getStatus() != -1) {
                try {
                    if (mpeer.getServices().setProblemStatus(
                            mpeer.getCurrentProblemId(), getStatus())) {
                        results.append("Problem status saved.  ");
                    } else {
                        results.append("Error saving problem status.  ");
                    }
                } catch (Exception e) {
                    logger.error("Error saving problem status.", e);
                    results.append("Error saving problem status.  ");
                }
            }

            if (getPrimarySolutionId() != -1) {
                try {
                    if (mpeer.getServices().setPrimarySolution(
                            mpeer.getCurrentComponentId(), getPrimarySolutionId())) {
                        results.append("Primary solution saved.  ");
                    } else {
                        results.append("Error saving primary solution.  ");
                    }
                } catch (Exception e) {
                    logger.error("Error saving primary solution.", e);
                    results.append("Error saving primary solution.  ");
                }
            }

            if (getProblemTesterIds() != null) {
                try {
                    boolean result;
                    if (mpeer.getCurrentRoom() instanceof ViewTeamProblemRoom) {
                        result = mpeer.getServices().setTestersForProblem(
                                mpeer.getCurrentProblemId(), getProblemTesterIds());
                    } else {
                        result = mpeer.getServices().setTestersForComponent(
                                mpeer.getCurrentComponentId(), getProblemTesterIds());
                    }

                    if (result) {
                        results.append("Problem testers saved. ");
                    } else {
                        results.append("Error saving problem testers.  ");
                    }
                } catch (Exception e) {
                    logger.error("Error saving problem testers.", e);
                    results.append("Error saving problem testers.  ");
                }
            }

            mpeer.sendMessage(results.toString());
        }
    }
}
