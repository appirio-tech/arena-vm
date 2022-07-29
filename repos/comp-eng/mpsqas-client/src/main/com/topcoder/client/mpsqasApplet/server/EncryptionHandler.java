package com.topcoder.client.mpsqasApplet.server;

import java.security.GeneralSecurityException;

import com.topcoder.shared.netCommon.SealedSerializable;

public interface EncryptionHandler {
    byte[] generateRequestKey();
    void setReplyKey(byte[] key);
    Object unsealObject(SealedSerializable obj) throws GeneralSecurityException;
    SealedSerializable sealObject(Object obj) throws GeneralSecurityException;
    boolean ready();
}
