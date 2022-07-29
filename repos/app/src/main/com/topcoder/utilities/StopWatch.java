package com.topcoder.utilities;

import org.apache.log4j.Category;

/**
 * Simple utility class for logging ellapsed times.
 */
public class StopWatch {

    private long m_startTime;
    private long m_stopTime;
    private Category m_trace;

    public StopWatch(Category trace, String message) {
        m_trace = trace;
        m_startTime = System.currentTimeMillis();
        m_trace.debug("!!! TIMER - " + message + " Starting at time: " + m_startTime);
    }

    public void start() {
        m_startTime = System.currentTimeMillis();
    }

    public void stop(String message) {
        m_stopTime = System.currentTimeMillis();
        m_trace.debug("!!! TIMER - " + message + " " + (m_stopTime - m_startTime));
        m_startTime = m_stopTime;
    }

}
