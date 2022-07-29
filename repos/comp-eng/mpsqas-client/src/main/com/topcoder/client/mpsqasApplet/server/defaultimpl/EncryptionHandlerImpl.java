package com.topcoder.client.mpsqasApplet.server.defaultimpl;

import java.security.GeneralSecurityException;
import java.security.Key;

import com.topcoder.client.mpsqasApplet.server.EncryptionHandler;
import com.topcoder.shared.netCommon.MessageEncryptionHandler;
import com.topcoder.shared.netCommon.SealedSerializable;

public class EncryptionHandlerImpl implements EncryptionHandler {
    private Key key;
    private MessageEncryptionHandler handler;

    public synchronized byte[] generateRequestKey() {
        key = null;
        handler = new MessageEncryptionHandler();
        return handler.generateRequestKey();
    }

    public synchronized void setReplyKey(byte[] key) {
        handler.setReplyKey(key);
        this.key = handler.getFinalKey();
        handler = null;
    }

    public Object unsealObject(SealedSerializable obj) throws GeneralSecurityException {
        return MessageEncryptionHandler.unsealObject(obj, key);
    }

    public SealedSerializable sealObject(Object obj) throws GeneralSecurityException {
        return MessageEncryptionHandler.sealObject(obj, key);
    }
    
    public synchronized boolean ready() {
        return key != null;
    }
}
