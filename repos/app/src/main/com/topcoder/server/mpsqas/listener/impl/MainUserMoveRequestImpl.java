package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.server.mpsqas.room.MainUserRoom;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.message.MainUserMoveRequest;
import com.topcoder.netCommon.mpsqas.communication.message.MainUserMoveResponse;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.netCommon.mpsqas.ApplicationConstants;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.rmi.RemoteException;

/**
 *
 * @author Logan Hanks
 */
public class MainUserMoveRequestImpl
        extends MainUserMoveRequest
        implements MessageProcessor, MainUserRoom {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (!mpeer.isLoggedIn()) {
            Logger logger = Logger.getLogger(getClass());

            logger.error("Unauthorized user attempted main user room move");
            mpeer.sendErrorMessage("Not logged in");
            return;
        }

        if (!mpeer.isAdmin()) {
            Logger logger = Logger.getLogger(getClass());

            logger.warn("Non-admin " + mpeer.getUsername() + " (" + mpeer.getUserId() + ") attempted to enter main user room");
            mpeer.sendErrorMessage("Not an admin");
            return;
        }

        mpeer.moveToNewRoom(this);

    }

    public void enter(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        try {
            ArrayList result = mpeer.getServices().getUsers(ApplicationConstants.ALL_USERS, -1);

            mpeer.sendMessage(new MainUserMoveResponse(result));
        } catch (Exception e) {
            mpeer.sendErrorMessage("Error retrieving all users", e);
        }
    }
}
