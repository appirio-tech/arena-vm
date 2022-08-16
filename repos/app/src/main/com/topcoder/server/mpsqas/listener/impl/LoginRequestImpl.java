package com.topcoder.server.mpsqas.listener.impl;

import java.security.GeneralSecurityException;

import com.topcoder.netCommon.mpsqas.communication.message.LoginRequest;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.message.LoginResponse;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.shared.netCommon.MessageEncryptionHandler;
import org.apache.log4j.Logger;

/**
 * Implementation of a server-side handler for login requests.
 *
 * @author Logan Hanks
 */
public class LoginRequestImpl
        extends LoginRequest
        implements MessageProcessor {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        Logger.getLogger("LoginRequestImpl").debug("processing login request: " + handle);

        BeanAuthenticator auth = new BeanAuthenticator();

        auth.setServices(mpeer.getServices());
        LoginResponse response;

        try {
            response = auth.authenticate(handle, (String) MessageEncryptionHandler.unsealObject(password, mpeer.getEncryptKey()));
        } catch (GeneralSecurityException e) {
            response = new LoginResponse("Login failed due to decryption failure");
        }

        Logger.getLogger("LoginRequestImpl").debug("response is " + response.toString());

        mpeer.sendMessage(response);
        mpeer.setLoggedIn(response.isSuccess());
        mpeer.setAdmin(response.isAdmin());
        if (response.isSuccess()) {
            mpeer.setUserId(response.getId());
            mpeer.setUsername(handle);
            mpeer.setWriter(response.isWriter());
            mpeer.setTester(response.isTester());
            mpeer.moveToNewRoom(new FoyerMoveRequestImpl());
        }
    }
}
