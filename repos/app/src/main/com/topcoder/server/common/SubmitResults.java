package com.topcoder.server.common;

public class SubmitResults extends Results {

    private final int m_points;

    public SubmitResults(boolean success, String message, int points) {
        super(success, message);
        m_points = points;
    }

    public int getPoints() {
        return m_points;
    }
}
