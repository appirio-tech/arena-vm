/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.listener.wss.listeners;

import javax.crypto.NullCipher;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.topcoder.server.listener.wss.WebSocketServer;
import com.topcoder.server.listener.wss.WebSocketServerHelper;
import com.topcoder.server.listener.wss.request.LoginRequest;
import com.topcoder.netCommon.contestantMessages.response.LoginResponse;

import com.topcoder.server.listener.wss.MainListenerConnector;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.SealedSerializable;

/**
 * The data listener to process the login request.
 *
 * <p>
 * Version 1.1 - Module Assembly - TopCoder Competition Engine - Web Socket Listener
 * <ol>Use main listener to process login rather than EJB services</ol>
 * </p>
 * 
 * <p>
 * Version 1.2 (Module Assembly - TCC Web Socket - Coder Profile and Active Users) changes:
 * <ol>
 *  <li>Updated {@link #onData(SocketIOClient, LoginRequest, AckRequest)} to set sessionToConnectionMap.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Version 1.3 (TCC Web Socket Refactoring) changes:
 * <ol>
 *  <li>Minor code style and logging polishing.</li>
 *  <li>Changed code to return {@link LoginResponse} in case of login failure.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (Module Assembly - Web Socket Listener - Track User Actions From Web Arena):
 * <ol>
 *     <li>Updated {@link #onData(com.corundumstudio.socketio.SocketIOClient,
 *     com.topcoder.server.listener.wss.request.LoginRequest, com.corundumstudio.socketio.AckRequest)}
 *     to store user handle when login.</li>
 * </ol>
 * </p>
 *
 * @author Standlove, gondzo, freegod, gevak
 * @version 1.4
 */
public class LoginDataListener extends BaseDataListener<LoginRequest> {
    /**
     * Constructor.
     *
     * @param server the WebSocket server instance
     * @throw IllegalArgumentException if the server argument is null.
     */
    public LoginDataListener(WebSocketServer server) {
        super(server);
    }

    /**
     * The login process method.
     *
     * @param client  the current client
     * @param data    the login request
     * @param ackRequest  the ackRequest
     */
    public void onData(SocketIOClient client, LoginRequest data, AckRequest ackRequest) {
        String username = data.getUsername();
        String password = data.getPassword();

        WebSocketServerHelper.info("Process Login request for user: " + username);

        if (!getServer().getSessionToConnectionMap().containsKey(client.getSessionId())) {
            // First connection.
            getServer().getSessionToConnectionMap().put(client.getSessionId(), client);
        }
        getServer().getSessionToUserHandleMap().put(client.getSessionId(), username);

        try {
            // Send login request.
            MainListenerConnector mlc = getServer().getMainListenerConnector();
            Object request = new com.topcoder.netCommon.contestantMessages.request.LoginRequest(
                username, new SealedSerializable(password, new NullCipher()), ContestConstants.LOGIN);
            mlc.write(client.getSessionId(), request);
            WebSocketServerHelper.info("Login request for user: " + username + " processed.");
        } catch (Exception e) {
            WebSocketServerHelper.error("Error processing login request.", e);
            client.sendEvent(LoginResponse.class.getSimpleName(), new LoginResponse(false));
        }
    }
}
