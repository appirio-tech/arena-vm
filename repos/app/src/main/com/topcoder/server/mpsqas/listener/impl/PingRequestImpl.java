package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.netCommon.mpsqas.communication.message.PingRequest;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import org.apache.log4j.Logger;

/**
 * Doesn't do much of anything except get called when a ping comes in.
 */
public class PingRequestImpl
        extends PingRequest
        implements MessageProcessor {

    public void process(Peer peer) {
    }
}
