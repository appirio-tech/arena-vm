/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.listener.wss;

import java.util.Map;
import java.util.UUID;

import com.corundumstudio.socketio.SocketIOClient;

/**
 * Helper class providing helper methods to log and check login.
 * 
 * <p>
 * Version 1.1 (Module Assembly - TCC Web Socket - Coder Profile and Active Users) changes:
 * <ol>
 *  <li>Updated {@link #checkLogin(SocketIOClient, Map)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Version 1.2 (Web Socket Listener - Add configuration for using SSL or not v1.0) changes:
 * <ol>
 *  <li>Added {@link #IS_START_WITH_SSL} field.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Version 1.3 (Module Assembly - Web Socket Listener - Track User Actions From Web Arena) changes:
 * <ol>
 *     <li>Added {@link #RECORD_ACTION_BUNDLE_NAME} field.</li>
 *     <li>Added {@link #RECORD_ACTION_KEY} field.</li>
 * </ol>
 * </p>
 *
 * @author Standlove, freegod, savon_cn
 * @version 1.3
 */
public class WebSocketServerHelper {
    /**
     * The logger
     */
//    private static final Logger logger = Logger.getLogger(WebSocketServer.class);

    /**
     * The bundle name for the WebSocketServer.
     */
    public static final String WEB_SOCKET_SERVER_BUNDLE_NAME = "WebSocketServer";

    /**
     * The keyStoreFileName property name in the WebSocketServer bundle.
     */
    public static final String KEYSTORE_FILE_NAME_KEY = "keyStoreFileName";

    /**
     * The keyStorePassword property name in the WebSocketServer bundle.
     */
    public static final String KEYSTORE_PASSSWORD_KEY = "keyStorePassword";

    /**
     * The websocket server start with ssl.
     * @since 1.2
     */
    public static final String IS_START_WITH_SSL = "isStartWithSSL";

    /**
     * The bundle name for recordActions.
     * @since 1.3
     */
    public static final String RECORD_ACTION_BUNDLE_NAME = "recordActions";

    /**
     * The Actions property name in recordActions bundle.
     * @since 1.3
     */
    public static final String RECORD_ACTION_KEY = "Actions";


    /**
     * Private empty constructor.
     */
    private WebSocketServerHelper() {
    }

//    /**
//     * Log with INFO level
//     *
//     * @param message the message to log
//     */
//    public static void info(String message) {
//        logger.info(message);
//    }
//
//    /**
//     * Log with INFO level
//     *
//     * @param message the message to log
//     * @param cause the exception to log
//     */
//    public static void info(String message, Throwable cause) {
//        logger.info(message, cause);
//    }
//
//    /**
//     * Log with ERROR level.
//     *
//     * @param message the message to log
//     */
//    public static void error(String message) {
//        logger.error(message);
//    }
//
//    /**
//     * Log with ERROR level.
//     *
//     * @param message the message to log
//     * @param cause the exception to log
//     */
//    public static void error(String message, Throwable cause) {
//        logger.error(message, cause);
//    }
//
    /**
     * Return true if the string is null or empty, return false otherwise.
     *
     * @param value the string value.
     * @return true if the string is null or empty, return false otherwise.
     */
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    /**
     * Check the provided value is null or not.
     *
     * @param value the value to check.
     * @param name the name of the value.
     * @throws IllegalArgumentException if the value is null.
     */
    public static void checkNull(Object value, String name) {
        if (value == null) {
            throw new IllegalArgumentException("The " + name + " argument is required.");
        }
    }

    /**
     * Check the provided value is null or empty string.
     *
     * @param value the value to check.
     * @param name the name of the value.
     * @throws IllegalArgumentException if the value is null or empty string.
     */
    public static void checkNullOrEmpty(String value, String name) {
        if (isNullOrEmpty(value)) {
            throw new IllegalArgumentException("The " + name + " argument is required.");
        }
    }

    /**
     * Check the current user is already logged in or not.
     *
     * @param client the current client
     * @param sessionToConnectionMap session id to connection mappings
     * @return true if user is logged in, return false otherwise
     */
    public static boolean checkLogin(SocketIOClient client, Map<UUID, SocketIOClient> sessionToConnectionMap) {
        return sessionToConnectionMap.containsKey(client.getSessionId());
    }
}
