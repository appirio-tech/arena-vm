package com.topcoder.server.mpsqas.listener;

import java.security.Key;

import com.topcoder.server.mpsqas.listener.impl.MessageProcessorFactory;
import com.topcoder.server.security.PrivateKeyObtainer;
import com.topcoder.shared.netCommon.CSHandler;
import com.topcoder.shared.netCommon.CSHandlerFactory;

/**
 * Factory that provides new instances of message handlers.
 *
 * @author Logan Hanks
 * @see com.topcoder.shared.netCommon.CSHandler
 * @see MessageProcessorFactory
 * @see com.topcoder.netCommon.mpsqas.communication.MPSQASMessageHandler
 */
public class MPSQASMessageHandlerFactory
        implements CSHandlerFactory {
    private Key encryptKey;

    public MPSQASMessageHandlerFactory() {
        encryptKey = PrivateKeyObtainer.obtainPrivateKey();
    }

    public CSHandler newInstance() {
        return new MessageProcessorFactory(encryptKey);
    }
}
