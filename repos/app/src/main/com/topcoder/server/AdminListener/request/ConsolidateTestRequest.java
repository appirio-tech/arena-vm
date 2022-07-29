package com.topcoder.server.AdminListener.request;

public class ConsolidateTestRequest extends RoundIDCommand implements ProcessedAtBackEndRequest {

    public ConsolidateTestRequest(int roundId) {
        super(roundId);
    }
}

