package com.topcoder.server.AdminListener.request;

public final class RefreshBroadcastsCommand extends RoundIDCommand {

    public RefreshBroadcastsCommand() {
    }

    public RefreshBroadcastsCommand(int roundID) {
        super(roundID);
    }
}
