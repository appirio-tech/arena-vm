package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.netCommon.mpsqas.ComponentIdStructure;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.message.SaveComponentRequest;
import com.topcoder.netCommon.mpsqas.communication.message.NewStatusMessage;
import com.topcoder.netCommon.mpsqas.communication.message.NewComponentIdStructureResponse;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.rmi.RemoteException;

/**
 * Calls on the bean to save a component
 *
 * @author mitalub
 */
public class SaveComponentRequestImpl
        extends SaveComponentRequest
        implements MessageProcessor {

    public void process(Peer peer) {
        Logger logger = Logger.getLogger(getClass());

        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (!mpeer.isLoggedIn()) {
            logger.error("Unauthorized user attempted to submit problem.");
            mpeer.sendErrorMessage("Not logged in");
        } else {
            try {
                ArrayList result = mpeer.getProblemServices().saveComponent(
                        getComponent(),
                        mpeer.getUserId(),
                        mpeer.getId());

                if (((Boolean) result.get(0)).booleanValue()) {
                    mpeer.sendMessage(new NewStatusMessage(false,
                            "Component saved."));
                    ComponentIdStructure idstruct = (ComponentIdStructure) result.get(1);
                    mpeer.setCurrentComponentId(idstruct.getComponentId());
                    mpeer.sendMessage(new NewComponentIdStructureResponse(idstruct));
                } else {
                    mpeer.sendErrorMessage((String) result.get(1));
                }
            } catch (Exception e) {
                logger.error("Error saving component.", e);
                mpeer.sendErrorMessage("Server error when saving component.", e);
            }
        }
    }
}
