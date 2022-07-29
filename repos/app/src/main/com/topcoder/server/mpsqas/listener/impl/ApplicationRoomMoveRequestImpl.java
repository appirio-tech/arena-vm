package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.server.mpsqas.room.ApplicationRoom;
import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.netCommon.mpsqas.MessageConstants;
import org.apache.log4j.Logger;

/**
 *
 * @author Logan Hanks
 */
public class ApplicationRoomMoveRequestImpl
        extends ApplicationRoomMoveRequest
        implements MessageProcessor, ApplicationRoom {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (!mpeer.isLoggedIn()) {
            Logger logger = Logger.getLogger(getClass());

            logger.error("Unauthorized user attempted application room move");
            mpeer.sendErrorMessage("Not logged in");
            return;
        }
        switch (getApplicationType()) {
        case MessageConstants.TESTER_APPLICATION:
            if (mpeer.isTester()) {
                FoyerMoveRequestImpl foyer = new FoyerMoveRequestImpl();

                mpeer.sendMessage(new NewStatusMessage(true,
                        "You are already a problem tester.  No need to apply."));
                mpeer.moveToNewRoom(foyer);
                return;
            }
            break;
        case MessageConstants.WRITER_APPLICATION:
            if (mpeer.isWriter()) {
                FoyerMoveRequestImpl foyer = new FoyerMoveRequestImpl();

                mpeer.sendMessage(new NewStatusMessage(true,
                        "You are already a problem writer.  No need to apply."));
                mpeer.moveToNewRoom(foyer);
                return;
            }
            break;
        }
        mpeer.moveToNewRoom(this);
    }

    public void enter(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;
        mpeer.setCurrentApplicationId(getApplicationType());
        mpeer.sendMessage(new ApplicationRoomMoveResponse(
                getApplicationType()));
    }
}
