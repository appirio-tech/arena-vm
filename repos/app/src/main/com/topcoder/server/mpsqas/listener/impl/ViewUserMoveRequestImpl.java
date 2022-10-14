package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.server.mpsqas.room.ViewUserRoom;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.netCommon.mpsqas.UserInformation;
import org.apache.log4j.Logger;

/**
 *
 * @author Logan Hanks
 */
public class ViewUserMoveRequestImpl
        extends ViewUserMoveRequest
        implements MessageProcessor, ViewUserRoom {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (!mpeer.isLoggedIn()) {
            Logger logger = Logger.getLogger(getClass());

            logger.error("Unauthorized user attempted view user room move");
            mpeer.sendErrorMessage("Not logged in");
            return;
        }
        if (!mpeer.isAdmin()) {
            Logger logger = Logger.getLogger(getClass());

            logger.error("Non-admin " + mpeer.getUsername() + " (" + mpeer.getUserId() + ") attempted view user room move");
            mpeer.sendErrorMessage("Not an admin");
            return;
        }
        mpeer.moveToNewRoom(this);
    }

    public void enter(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        try {
            UserInformation result = mpeer.getServices().getUserInformation(getUserId());
            if (result == null) {
                mpeer.sendErrorMessage("Cannot retrieve user information.");
            } else {
                mpeer.setCurrentUserId(getUserId());
                mpeer.sendMessage(new ViewUserMoveResponse(result));
            }
        } catch (Exception e) {
            mpeer.sendErrorMessage("Error retrieving user information", e);
        }
    }
}
