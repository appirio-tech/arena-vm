/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.listener.wss.listeners;

import org.apache.log4j.Logger;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.PopUpGenericResponse;
import com.topcoder.server.listener.wss.MainListenerConnector;
import com.topcoder.server.listener.wss.WebSocketServer;
import com.topcoder.server.listener.wss.WebSocketServerHelper;

/**
 * The generic listener. It checks login and then passes request directly to backend.
 *
 * @author gevak
 * @version 1.0
 */
public class GenericListener<T> extends BaseDataListener<T> {
	private static final Logger logger = Logger.getLogger(GenericListener.class);
	
    /**
     * Constructor.
     *
     * @param server the WebSocket server instance
     * @throw IllegalArgumentException if the server argument is null.
     */
    public GenericListener(WebSocketServer server) {
        super(server);
    }

    /**
     * The get active users process method.
     *
     * @param client  the current client
     * @param data    the active users request
     * @param ackRequest  the ackRequest
     */
    public void onData(SocketIOClient client, T request, AckRequest ackRequest) {
        String requestName = request.getClass().getCanonicalName();
        
        boolean doLog = logger.isInfoEnabled();
        if (doLog) {
        	logger.info("Processing " + requestName + " request obtained from client.");
        }

        // Check login.
    	if (!WebSocketServerHelper.checkLogin(client, getServer().getSessionToConnectionMap())) {
    		if (doLog) {
    			logger.info("User must login first to send " + requestName + ".");
    		}
            client.sendEvent(PopUpGenericResponse.class.getSimpleName(),
                new PopUpGenericResponse("Unauthorized", "Must login first.",
                ContestConstants.GENERIC, ContestConstants.LABEL));
            return;
    	}

        // Process request.
        try {
            MainListenerConnector mlc = getServer().getMainListenerConnector();
            mlc.write(client.getSessionId(), request);
            if (doLog) {
            	logger.info(requestName + " request sent to server.");
            }
        } catch (Exception e) {
            logger.error("Error processing " + requestName + " request: " + e.getMessage(), e);
        }
    }

}
