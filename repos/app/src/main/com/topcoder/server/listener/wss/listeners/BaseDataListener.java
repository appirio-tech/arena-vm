/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.listener.wss.listeners;

import java.util.Map;
import java.util.UUID;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.topcoder.server.listener.wss.WebSocketServer;
import com.topcoder.server.listener.wss.WebSocketServerHelper;

/**
 * The base class for all DataListener implementations.
 *
 * @author Standlove
 * @version 1.0
 */
public abstract class BaseDataListener<T> implements DataListener<T> {
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
    protected BaseDataListener(WebSocketServer server) {
        WebSocketServerHelper.checkNull(server, "server");
        this.server = server;
    }

    /**
     * Getter for the WebSocketServer instance.
     *
     * @return the WebSocketServer instance.
     */
    protected WebSocketServer getServer() {
        return server;
    }
}
