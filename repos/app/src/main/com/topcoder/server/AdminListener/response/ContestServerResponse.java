package com.topcoder.server.AdminListener.response;

public class ContestServerResponse extends GenericResponse {

    public ContestServerResponse() {
    }

    public ContestServerResponse(int recipientId, Object responseObject) {
        super(recipientId, responseObject);
    }
}

