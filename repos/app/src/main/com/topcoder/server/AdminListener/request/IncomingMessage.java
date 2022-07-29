package com.topcoder.server.AdminListener.request;

public class IncomingMessage extends GenericRequest {

    public IncomingMessage(int senderId, Object responseObject) {
        super(senderId, responseObject);
    }
}
