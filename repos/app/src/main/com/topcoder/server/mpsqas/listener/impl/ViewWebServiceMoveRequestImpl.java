package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.server.mpsqas.room.ViewWebServiceRoom;
import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.netCommon.mpsqas.MessageConstants;
import com.topcoder.netCommon.mpsqas.ApplicationConstants;
import com.topcoder.netCommon.mpsqas.WebServiceInformation;
import org.apache.log4j.Logger;

/**
 * Calls the bean to fill out a WebServiceInformation to send to the client.
 *
 * @author mitalub
 */
public class ViewWebServiceMoveRequestImpl
        extends ViewWebServiceMoveRequest
        implements MessageProcessor, ViewWebServiceRoom {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (!mpeer.isLoggedIn()) {
            Logger logger = Logger.getLogger(getClass());

            logger.error("Unauthorized user attempting to view a problem.");
            mpeer.sendErrorMessage("Not logged in");
            return;
        }

        mpeer.moveToNewRoom(this);
    }

    public void enter(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;
        Logger logger = Logger.getLogger(getClass());

        try {
            WebServiceInformation w = mpeer.getServices()
                    .getWebServiceInformation(getWebServiceId(),
                            mpeer.getUserId());
            if (w == null) {
                mpeer.sendErrorMessage("Server error getting web service.");
            } else {
                mpeer.setCurrentWebServiceId(getWebServiceId());
                ViewWebServiceMoveResponse response = new
                        ViewWebServiceMoveResponse(w, w.getUserType()
                        != ApplicationConstants.PROBLEM_TESTER);
                mpeer.sendMessage(response);
            }
        } catch (Exception e) {
            mpeer.sendErrorMessage("Error viewing problem:", e);
        }
    }
}
