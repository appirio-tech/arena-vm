package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.server.mpsqas.room.ViewContestRoom;
import com.topcoder.netCommon.mpsqas.communication.message.ViewContestMoveRequest;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.message.ViewContestMoveResponse;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.netCommon.mpsqas.ContestInformation;
import org.apache.log4j.Logger;

import java.rmi.RemoteException;

/**
 *
 * @author Logan Hanks
 */
public class ViewContestMoveRequestImpl
        extends ViewContestMoveRequest
        implements MessageProcessor, ViewContestRoom {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (!mpeer.isLoggedIn()) {
            Logger logger = Logger.getLogger(getClass());

            logger.error("Unauthorized user attempted view contest room move");
            mpeer.sendErrorMessage("Not logged in");
            return;
        }

        mpeer.moveToNewRoom(this);
    }

    public void enter(Peer peer) {
        Logger logger = Logger.getLogger(getClass());
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        try {
            ContestInformation result = mpeer.getServices()
                    .getContestInformation(getContestId(), mpeer.getUserId());
            if (result == null) {
                logger.error("Bean returned null getting ContestInformation.");
                mpeer.sendErrorMessage("Cannot retrieve contest information. "
                        + "Do you have permission to view it?");
            } else {
                mpeer.setCurrentContestId(getContestId());
                mpeer.sendMessage(new ViewContestMoveResponse(result));
            }
        } catch (RemoteException e) {
            mpeer.sendErrorMessage("Error retrieving contest information", e);
        }
    }
}

