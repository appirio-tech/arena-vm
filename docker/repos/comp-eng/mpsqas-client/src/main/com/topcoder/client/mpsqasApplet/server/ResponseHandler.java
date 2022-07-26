package com.topcoder.client.mpsqasApplet.server;

import com.topcoder.client.mpsqasApplet.messaging.*;
import com.topcoder.netCommon.mpsqas.communication.message.Message;

public interface ResponseHandler {

    public void processMessage(Message message);

    public void registerResponseProcessor(Object responseProcessor,
            Class responseType);

    public void unregisterResponseProcessor(Object responseProcessor,
            Class responseType);
}
