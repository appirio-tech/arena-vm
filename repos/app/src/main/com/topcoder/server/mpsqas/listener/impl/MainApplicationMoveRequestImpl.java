package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.server.mpsqas.room.MainApplicationRoom;
import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.netCommon.mpsqas.communication.Peer;

import java.util.ArrayList;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;

/**
 *
 * @author Logan Hanks
 */
public class MainApplicationMoveRequestImpl
        extends MainApplicationMoveRequest
        implements MessageProcessor, MainApplicationRoom {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (!mpeer.isLoggedIn()) {
            Logger logger = Logger.getLogger(getClass());

            logger.error("Unauthorized user attempted main application room move");
            mpeer.sendErrorMessage("Not logged in");
            return;
        }

        if (!mpeer.isAdmin()) {
            Logger logger = Logger.getLogger(getClass());

            logger.warn("Non-admin " + mpeer.getUsername() + " (" + mpeer.getUserId() + ") attempted to enter main application room");
            mpeer.sendErrorMessage("Not an admin");
            return;
        }

        mpeer.moveToNewRoom(this);
    }

    public void enter(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (mpeer.isAdmin()) {
            try {
                ArrayList result = mpeer.getServices().getPendingApplications();

                mpeer.sendMessage(new MainApplicationMoveResponse(result));
            } catch (RemoteException e) {
                mpeer.sendErrorMessage("Error retrieving pending applications", e);
            }
        } else {
            Logger logger = Logger.getLogger(getClass());

            logger.warn("Non-admin " + mpeer.getUsername() + " (" + mpeer.getUserId() + ") attempted to enter main application room");
            mpeer.sendErrorMessage("Not an admin");
        }
    }
}
