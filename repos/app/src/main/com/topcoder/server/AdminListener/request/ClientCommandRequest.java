package com.topcoder.server.AdminListener.request;

public class ClientCommandRequest extends GenericRequest {

    public ClientCommandRequest() {
    }

    public ClientCommandRequest(int senderId, Object requestObject) {
        super(senderId, requestObject);
    }
}
