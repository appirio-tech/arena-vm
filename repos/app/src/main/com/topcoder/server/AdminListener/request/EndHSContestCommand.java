package com.topcoder.server.AdminListener.request;

public final class EndHSContestCommand extends RoundIDCommand {

    public EndHSContestCommand() {
    }

    public EndHSContestCommand(int roundId) {
        super(roundId);
    }
}
