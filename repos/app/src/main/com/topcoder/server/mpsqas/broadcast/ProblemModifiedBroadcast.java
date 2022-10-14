package com.topcoder.server.mpsqas.broadcast;

/**
 * A broadcast specifying that someone has modified a problem.
 */
public class ProblemModifiedBroadcast extends Broadcast {

    private int problemId;

    /**
     * Connection id of user who modified the problem, so they don't
     * get the broadcast.
     */
    private int connectionId;
    private String handle;

    public ProblemModifiedBroadcast(int problemId, String handle,
            int connectionId) {
        this.problemId = problemId;
        this.handle = handle;
        this.connectionId = connectionId;
    }

    public int getProblemId() {
        return problemId;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public String getHandle() {
        return handle;
    }
}
