package com.topcoder.server.AdminListener.request;

public final class RefreshProbsCommand extends RoundIDCommand {

    public RefreshProbsCommand() {
    }

    public RefreshProbsCommand(int roundId) {
        super(roundId);
    }
}
