package com.topcoder.server.AdminListener.request;

public final class ClearTestCasesCommand extends RoundIDCommand {

    public ClearTestCasesCommand() {
    }

    public ClearTestCasesCommand(int roundId) {
        super(roundId);
    }
}
