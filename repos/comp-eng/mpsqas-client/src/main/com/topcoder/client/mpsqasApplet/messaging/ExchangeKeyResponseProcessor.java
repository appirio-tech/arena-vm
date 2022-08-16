package com.topcoder.client.mpsqasApplet.messaging;

import java.util.Set;

import com.topcoder.netCommon.mpsqas.communication.message.ExchangeKeyResponse;

/**
 * Interface for exchanging key response processors.
 *
 * @author visualage
 */
public interface ExchangeKeyResponseProcessor {
    public void processExchangeKey(byte[] replyKey);
}
