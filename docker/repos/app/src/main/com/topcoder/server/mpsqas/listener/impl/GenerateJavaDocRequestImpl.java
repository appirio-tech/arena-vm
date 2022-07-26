package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.netCommon.mpsqas.ApplicationConstants;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 *
 * @author mitalub
 */
public class GenerateJavaDocRequestImpl
        extends GenerateJavaDocRequest
        implements MessageProcessor {

    public void process(Peer peer) {
        Logger logger = Logger.getLogger(getClass());
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (mpeer.isLoggedIn()) {
            try {
                ArrayList results = mpeer.getServices().generateJavaDocs(
                        mpeer.getCurrentWebServiceId());
                if (Boolean.TRUE.equals(results.get(0))) {
                    String html = mpeer.getServices().getBriefJavaDocs(
                            mpeer.getCurrentWebServiceId());
                    if ("".equals(html)) {
                        mpeer.sendErrorMessage("Java docs generated with no errors, "
                                + "but no resulting html file can be found.");
                    } else {
                        mpeer.sendMessage(new GenerateJavaDocResponse(html));
                    }
                } else {
                    mpeer.sendErrorMessage((String) results.get(1));
                }
            } catch (Exception e) {
                logger.error("Error calling bean to get java docs.", e);
                mpeer.sendErrorMessage(ApplicationConstants.SERVER_ERROR);
            }
        } else {
            logger.info("Unautorized user trying to generate java docs.");
            mpeer.sendErrorMessage("Not logged in.");
        }
    }
}
