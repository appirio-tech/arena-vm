package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.server.mpsqas.room.MainContestRoom;
import com.topcoder.netCommon.mpsqas.communication.message.UpcomingContestsMoveRequest;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.message.UpcomingContestsMoveResponse;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.rmi.RemoteException;

/**
 *
 * @author Logan Hanks
 */
public class UpcomingContestsMoveRequestImpl
        extends UpcomingContestsMoveRequest
        implements MessageProcessor, MainContestRoom {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (!mpeer.isLoggedIn()) {
            Logger logger = Logger.getLogger(getClass());

            logger.error("Unauthorized user attempted upcoming contests room move");
            mpeer.sendErrorMessage("Not logged in");
            return;
        }

        mpeer.moveToNewRoom(this);
    }

    public void enter(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        try {
            ArrayList result = mpeer.getServices().getContests(mpeer.getUserId());

            mpeer.sendMessage(new UpcomingContestsMoveResponse(result));
        } catch (RemoteException e) {
            mpeer.sendErrorMessage("Error retrieving contests", e);
        }
    }
}
