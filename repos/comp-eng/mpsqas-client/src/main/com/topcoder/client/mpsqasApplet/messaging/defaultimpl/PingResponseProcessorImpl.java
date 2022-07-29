package com.topcoder.client.mpsqasApplet.messaging.defaultimpl;

import com.topcoder.client.mpsqasApplet.messaging.PingResponseProcessor;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.common.ResponseClassTypes;

/**
 * Default implementation of Ping Response Processor.
 *
 * @author mitalub
 */
public class PingResponseProcessorImpl implements PingResponseProcessor {

    public void init() {
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.PING);
    }

    /**
     * Pings back to server.
     */
    public void processPing() {
        MainObjectFactory.getPingRequestProcessor().pingServer();
    }
}
