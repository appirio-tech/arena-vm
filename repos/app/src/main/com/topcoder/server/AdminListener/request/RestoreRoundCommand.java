package com.topcoder.server.AdminListener.request;

public final class RestoreRoundCommand extends RoundIDCommand {

    public RestoreRoundCommand() {
    }

    public RestoreRoundCommand(int roundID) {
        super(roundID);
    }

}
