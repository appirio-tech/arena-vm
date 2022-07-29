package com.topcoder.server.AdminListener.request;

public class LoadRoundRequest extends RoundIDCommand {

    public LoadRoundRequest() {
    }

    public LoadRoundRequest(int roundID) {
        super(roundID);
    }
}

