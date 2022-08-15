/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.listener.wss.listeners;

import java.util.Map;
import java.util.UUID;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.topcoder.netCommon.contestantMessages.request.SSOLoginRequest;
import com.topcoder.netCommon.contestantMessages.response.LoginResponse;
import com.topcoder.server.listener.wss.MainListenerConnector;
import com.topcoder.server.listener.wss.WebSocketServer;
import com.topcoder.server.listener.wss.WebSocketServerHelper;
import com.topcoder.server.services.CoreServices;

/**
 * The data listener to process the login request using sso cookie value.
 *
 * <p>
 * Version 1.1 (TCC Web Socket Refactoring) changes:
 * <ol>
 *  <li>Changed code to use backend request directly.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in Version 1.2 (Module Assembly - Web Socket Listener - Track User Actions From Web Arena):
 * <ol>
 *     <li>Updated to store user handle to sessionToUserHandleMap.</li>
 * </ol>
 * </p>
 *
 * @author gondzo, gevak, freegod
 * @version 1.2
 */
public class SSOLoginDataListener extends BaseDataListener<SSOLoginRequest> {
    /**
     * Constructor.
     *
     * @param server the WebSocket server instance
     * @throw IllegalArgumentException if the server argument is null.
     */
    public SSOLoginDataListener(WebSocketServer server) {
        super(server);
    }

    /**
     * The login process method.
     *
     * @param client  the current client
     * @param data    the login request
     * @param ackRequest  the ackRequest
     */
    public void onData(SocketIOClient client, SSOLoginRequest data, AckRequest ackRequest) {
        String sso = data.getSSO();
        String jwt = data.getJWT();

        WebSocketServerHelper.info("Process SSO Login request for session: " + client.getSessionId() + ", sso: " + sso);
        try {
            Map<UUID, SocketIOClient> sessionToConnectionMap =  getServer().getSessionToConnectionMap();
            if (!sessionToConnectionMap.containsKey(client.getSessionId())) {
                // First connection.
                sessionToConnectionMap.put(client.getSessionId(), client);
            }
            if (jwt != null) {
                getServer().getSessionToJwtMap().put(client.getSessionId(), jwt);
            }
            String userHandle;
            try {
                userHandle = CoreServices.getHandleBySSO(data.getSSO());
                getServer().getSessionToUserHandleMap().put(client.getSessionId(), userHandle);
            } catch (Exception e) {
                WebSocketServerHelper.info("sso is invalid");
            }

            // Send login request.
            MainListenerConnector mlc = getServer().getMainListenerConnector();
            mlc.write(client.getSessionId(), data);
            WebSocketServerHelper.info("Login request for sso: " + sso + " processed.");
        } catch (Exception e) {
            WebSocketServerHelper.error("Error processing login request.", e);
            client.sendEvent(LoginResponse.class.getSimpleName(), new LoginResponse(false));
        }
    }
}
