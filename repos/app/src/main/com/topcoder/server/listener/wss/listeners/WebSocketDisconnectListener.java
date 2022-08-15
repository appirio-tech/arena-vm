/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.listener.wss.listeners;

import java.util.Map;
import java.util.UUID;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.topcoder.server.listener.wss.WebSocketServer;
import com.topcoder.server.listener.wss.WebSocketServerHelper;
import com.topcoder.server.services.CoreServices;

import com.topcoder.server.listener.wss.MainListenerConnector;

/**
 * The disconnect listener to process the disconnect event.
 *
 * <p>
 * Version 1.1 (TCC Web Socket Refactoring) changes:
 * <ol>
 *  <li>Changed code to use backend request directly.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (Module Assembly - Web Socket Listener - Track User Actions From Web Arena):
 * <ol>
 *     <li>Updated to remove user handle from sessionToUserHandleMap.</li>
 * </ol>
 * </p>
 *
 * @author Standlove, gevak, freegod
 * @version 1.2
 */
public class WebSocketDisconnectListener implements DisconnectListener {

    /**
     * The WebSocket server instance
     */
    private final WebSocketServer server;

    /**
     * Constructor.
     *
     * @param server the WebSocket server instance
     * @throw IllegalArgumentException if the server argument is null.
     */
    public WebSocketDisconnectListener(WebSocketServer server) {
        WebSocketServerHelper.checkNull(server, "server");
        this.server = server;
    }

    /**
     * Process the disconnect event.
     *
     * @param client the WebSocket client
     */
    public void onDisconnect(SocketIOClient client) {
        try {
            //send the logout request
            MainListenerConnector mlc = server.getMainListenerConnector();
            Object request = new com.topcoder.netCommon.contestantMessages.request.LogoutRequest();
            mlc.write(client.getSessionId(), request);
            server.getSessionToConnectionMap().remove(client.getSessionId());
            server.getSessionToUserHandleMap().remove(client.getSessionId());
        } catch (Exception e) {
            WebSocketServerHelper.info("error processing disconnect request", e);
            //does not matter, the connection will time out
        }
    }
}
