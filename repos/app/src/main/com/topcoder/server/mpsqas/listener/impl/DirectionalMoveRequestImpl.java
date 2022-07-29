package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.message.DirectionalMoveRequest;
import com.topcoder.netCommon.mpsqas.communication.Peer;

/**
 *
 * @author Logan Hanks
 */
public class DirectionalMoveRequestImpl
        extends DirectionalMoveRequest
        implements MessageProcessor {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        mpeer.moveToRoom(getDistance());
    }
}

