/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.listener.wss.listeners;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.topcoder.server.listener.wss.WebSocketServer;
import com.topcoder.netCommon.contestantMessages.request.LogoutRequest;

/**
 * The data listener to process the logout request.
 *
 * <p>
 * Version 1.1 - Module Assembly - TopCoder Competition Engine - Web Socket Listener
 * <ol>Use main listener to process logout rather than EJB services</ol>
 * </p>
 *
 * <p>
 * Version 1.2 (TCC Web Socket Refactoring) changes:
 * <ol>
 *  <li>Changed code to use backend request directly.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (Module Assembly - Web Socket Listener - Track User Actions From Web Arena):
 * <ol>
 *     <li>Updated to remove user handle from sessionToUserHandleMap</li>
 * </ol>
 * </p>
 *
 * @author Standlove, gondzo, gevak, freegod
 * @version 1.3
 */
public class LogoutDataListener extends GenericListener<LogoutRequest> {
    /**
     * Constructor.
     *
     * @param server the WebSocket server instance
     * @throw IllegalArgumentException if the server argument is null.
     */
    public LogoutDataListener(WebSocketServer server) {
        super(server);
    }

    /**
     * The logout process method.
     *
     * @param client  the current client
     * @param data    the logout request
     * @param ackRequest  the ackRequest
     */
    public void onData(SocketIOClient client, LogoutRequest data, AckRequest ackRequest) {
        super.onData(client, data, ackRequest);
        getServer().getSessionToConnectionMap().remove(client.getSessionId());
        getServer().getSessionToUserHandleMap().remove(client.getSessionId());
    }
}
