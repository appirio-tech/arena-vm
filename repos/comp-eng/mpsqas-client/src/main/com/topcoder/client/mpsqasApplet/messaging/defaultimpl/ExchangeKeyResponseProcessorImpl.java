package com.topcoder.client.mpsqasApplet.messaging.defaultimpl;

import java.util.Set;

import com.topcoder.client.mpsqasApplet.messaging.ExchangeKeyResponseProcessor;
import com.topcoder.client.mpsqasApplet.server.EncryptionHandler;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.common.ResponseClassTypes;

/**
 * Default implementation of Exchange Key Response Processor.
 *
 * @author visualage
 */
public class ExchangeKeyResponseProcessorImpl implements ExchangeKeyResponseProcessor {

    public void init() {
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.EXCHANGE_KEY);
    }

    /**
     * Save the partial key and form a full key.
     */
    public void processExchangeKey(byte[] replyKey) {
        MainObjectFactory.getEncryptionHandler().setReplyKey(replyKey);
    }
}
