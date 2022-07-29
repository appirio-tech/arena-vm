package com.topcoder.server.listener;

import java.security.Key;

import com.topcoder.netCommon.contestantMessages.NetCommonCSHandler;
import com.topcoder.server.security.PrivateKeyObtainer;
import com.topcoder.shared.netCommon.CSHandler;
import com.topcoder.shared.netCommon.CSHandlerFactory;

/**
 * The default <code>CSHandlerFactory</code> implemenentation.
 *
 * @author  Timur Zambalayev
 */
final class DefaultCSHandlerFactory implements CSHandlerFactory {
    private Key encryptKey;

    /**
     * Creates a new <code>DefaultCSHandlerFactory</code> object.
     */
    DefaultCSHandlerFactory() {
        encryptKey = PrivateKeyObtainer.obtainPrivateKey();
    }

    public CSHandler newInstance() {
        return new NetCommonCSHandler(encryptKey);
    }

}
