package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.netCommon.mpsqas.communication.message.ExchangeKeyRequest;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.message.ExchangeKeyResponse;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.shared.netCommon.MessageEncryptionHandler;
import org.apache.log4j.Logger;

/**
 * Implementation of a server-side handler for login requests.
 *
 * @author Logan Hanks
 */
public class ExchangeKeyRequestImpl
        extends ExchangeKeyRequest
        implements MessageProcessor {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;
        Logger log = Logger.getLogger("ExchangeKeyRequestImpl");
        log.debug("processing key exchanging request.");

        MessageEncryptionHandler handler = new MessageEncryptionHandler();
        handler.setRequestKey(getKey());
        byte[] replyKey = handler.generateReplyKey();

        // Associate the key to the peer
        mpeer.setEncryptKey(handler.getFinalKey());

        ExchangeKeyResponse response = new ExchangeKeyResponse(replyKey);
        mpeer.sendMessage(response);
    }
}
