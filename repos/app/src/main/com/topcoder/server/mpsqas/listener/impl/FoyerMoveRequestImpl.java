package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.server.mpsqas.room.FoyerRoom;
import com.topcoder.netCommon.mpsqas.communication.message.FoyerMoveRequest;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.message.FoyerMoveResponse;
import com.topcoder.netCommon.mpsqas.communication.Peer;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 *
 * @author Logan Hanks
 */
public class FoyerMoveRequestImpl
        extends FoyerMoveRequest
        implements MessageProcessor, FoyerRoom {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (!mpeer.isLoggedIn()) {
            Logger logger = Logger.getLogger(getClass());

            logger.error("Unauthorized user attempted foyer room move");
            mpeer.sendErrorMessage("Not logged in");
            return;
        }
        mpeer.moveToNewRoom(this);
    }

    public void enter(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        try {
            ArrayList problems = mpeer.getServices().getUnreadCorrespondence(mpeer.getUserId());

            mpeer.sendMessage(new FoyerMoveResponse(problems));
        } catch (Exception e) {
            Logger logger = Logger.getLogger(getClass());
            logger.error("Error getting unread correspondence.", e);
            mpeer.sendMessage(new FoyerMoveResponse(new ArrayList()));
        }
    }
}
