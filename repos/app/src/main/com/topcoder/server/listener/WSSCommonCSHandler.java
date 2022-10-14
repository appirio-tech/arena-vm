/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.server.listener;

import java.io.IOException;
import java.security.Key;

import com.topcoder.netCommon.contestantMessages.NetCommonCSHandler;
import com.topcoder.netCommon.contestantMessages.request.RegisteredRoundListRequest;
import com.topcoder.netCommon.contestantMessages.request.RoundProblemsRequest;
import com.topcoder.netCommon.contestantMessages.response.RegisteredRoundListResponse;
import com.topcoder.netCommon.contestantMessages.response.RoundProblemsResponse;
import com.topcoder.server.listener.socket.messages.LoginRequest;
import com.topcoder.server.listener.socket.messages.SocketMessage;


/**
 * Defines a custom serialization handler shared by MainListenerConnector and WebSocketConnector.
 * It allows specific handling for application requests/responses.
 *
 * @author dexy
 * @version 1.0
 * @see com.topcoder.netCommon.contestantMessages.NetCommonCSHandler
 */
public class WSSCommonCSHandler extends NetCommonCSHandler {

    /** The Constant ROUNDPROBLEMSREQUEST used when exchanging RoundProblemsRequest objects. */
    private static final byte ROUNDPROBLEMSREQUEST = 40;

    /** The Constant ROUNDPROBLEMSRESPONSE used when exchanging RoundProblemsResponse objects. */
    private static final byte ROUNDPROBLEMSRESPONSE = 41;

    /** The Constant REGISTEREDROUNDLISTRESPONSE used when exchanging RegisteredRoundListResponse objects. */
    private static final byte REGISTEREDROUNDLISTRESPONSE = 42;

    /** The Constant REGISTEREDROUNDLISTREQUEST used when exchanging RegisteredRoundListRequest objects. */
    private static final byte REGISTEREDROUNDLISTREQUEST = 43;

    /** The Constant WSSLOGINREQUEST used when exchanging LoginRequest objects. */
    private static final byte WSSLOGINREQUEST = 44;

    /** The Constant SOCKETMESSAGE used when exchanging SocketMessage objects. */
    private static final byte SOCKETMESSAGE = 45;

    /**
     * Instantiates a new WSSCommonCustomSerializable handler.
     */
    public WSSCommonCSHandler() {
        super(null);
    }

    /**
     * Instantiates a new WSSCommonCSHandler.
     *
     * @param key the key
     */
    public WSSCommonCSHandler(Key key) {
        super(key);
    }

    /* (non-Javadoc)
     * @see com.topcoder.netCommon.contestantMessages.NetCommonCSHandler#writeObjectOverride2(java.lang.Object)
     */
    @Override
    protected boolean writeObjectOverride2(Object object) throws IOException {
        if (object instanceof RoundProblemsRequest) {
            writeByte(ROUNDPROBLEMSREQUEST);
            customWriteObject(object);
            return true;
        }
        if (object instanceof RoundProblemsResponse) {
            writeByte(ROUNDPROBLEMSRESPONSE);
            customWriteObject(object);
            return true;
        }
        if (object instanceof RegisteredRoundListRequest) {
            writeByte(REGISTEREDROUNDLISTREQUEST);
            customWriteObject(object);
            return true;
        }
        if (object instanceof RegisteredRoundListResponse) {
            writeByte(REGISTEREDROUNDLISTRESPONSE);
            customWriteObject(object);
            return true;
        }
        if (object instanceof LoginRequest) {
            writeByte(WSSLOGINREQUEST);
            customWriteObject(object);
            return true;
        }
        if (object instanceof SocketMessage) {
            writeByte(SOCKETMESSAGE);
            customWriteObject(object);
            return true;
        }

        return false;
    }

    /* (non-Javadoc)
     * @see com.topcoder.netCommon.contestantMessages.NetCommonCSHandler#readObjectOverride2(byte)
     */
    @Override
    protected Object readObjectOverride2(byte type) throws IOException {
        switch (type) {
        case ROUNDPROBLEMSREQUEST: {
            RoundProblemsRequest roundProblemsRequest = new RoundProblemsRequest();
            roundProblemsRequest.customReadObject(this);
            return roundProblemsRequest;
        }
        case ROUNDPROBLEMSRESPONSE: {
            RoundProblemsResponse roundProblemsResponse = new RoundProblemsResponse();
            roundProblemsResponse.customReadObject(this);
            return roundProblemsResponse;
        }
        case REGISTEREDROUNDLISTRESPONSE: {
            RegisteredRoundListResponse registeredRoundListResponse = new RegisteredRoundListResponse();
            registeredRoundListResponse.customReadObject(this);
            return registeredRoundListResponse;
        }
        case REGISTEREDROUNDLISTREQUEST: {
            RegisteredRoundListRequest registeredRoundListRequest = new RegisteredRoundListRequest();
            registeredRoundListRequest.customReadObject(this);
            return registeredRoundListRequest;
        }
        case WSSLOGINREQUEST: {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.customReadObject(this);
            return loginRequest;
        }
        case SOCKETMESSAGE: {
            SocketMessage socketMessage = new SocketMessage();
            socketMessage.customReadObject(this);
            return socketMessage;
        }
        default:
            return super.readObjectOverride2(type);
        }
    }

}
