package com.topcoder.server.AdminListener.response;

public class CommandFailedResponse extends CommandResponse {

    public CommandFailedResponse() {
    }

    public CommandFailedResponse(String message) {
        super(message);
    }
}
