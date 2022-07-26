package com.topcoder.server.AdminListener.request;

public class CreateSystestsRequest extends RoundIDCommand implements ProcessedAtBackEndRequest {

    public CreateSystestsRequest() {
        super();
    }
    
    public CreateSystestsRequest(int roundId) {
        super(roundId);
    }
}

