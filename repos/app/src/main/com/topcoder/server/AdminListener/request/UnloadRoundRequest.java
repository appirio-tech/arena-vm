package com.topcoder.server.AdminListener.request;

public class UnloadRoundRequest extends RoundIDCommand {

    public UnloadRoundRequest() {
    }

    public UnloadRoundRequest(int roundID) {
        super(roundID);
    }
}

