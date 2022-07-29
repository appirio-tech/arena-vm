package com.topcoder.server.AdminListener.request;

public final class EndContestCommand extends RoundIDCommand {

    public EndContestCommand() {
    }

    public EndContestCommand(int roundId) {
        super(roundId);
    }
}
