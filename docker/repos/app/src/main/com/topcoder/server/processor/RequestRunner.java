package com.topcoder.server.processor;

//import org.apache.log4j.Category;

import java.util.LinkedList;

import com.topcoder.shared.util.logging.Logger;

/**
 * Responsible for pulling RequestSets from the RequestProcessor and then running through
 * them and reporting when they are completed.
 */
final class RequestRunner implements Runnable {

    private static final long MAX_TIME_BATCH = 5000L;
    //public static final int SLEEP_TIME = 250;
    private static final int SETS_PER_DEBUG = 500;

    private static Logger trace = Logger.getLogger(RequestRunner.class);

    private boolean m_stopped;
    private int m_setsProcessed = 0;
    private int m_totalRequestsProcessed = 0;
    private int m_totalConnectionsProcessed = 0;
    private int m_minSetSize = Integer.MAX_VALUE;
    private int m_maxSetSize = 0;

    RequestRunner() {
    }

    void stop() {
        trace.debug("RequestRunner stop");
        m_stopped = true;
    }

    public void run() {
        try {
            while (!m_stopped) {
                RequestSet requestSet = RequestProcessor.getNextRequestSet();
                if (requestSet != null) {
                    int setSize = requestSet.getRequests().size();
                    if (trace.isDebugEnabled()) {
                        trace.debug("Processing request set of size " + setSize);
                    }
                    try {
                        LinkedList requestQueue = requestSet.getRequests();
                        long st = System.currentTimeMillis();
                        while (requestQueue.size() > 0) {
                            PendingRequest request = (PendingRequest) requestQueue.removeFirst();
                            try {
                                switch (request.type) {
                                case PendingRequest.NEW_CONNECTION:
                                    RequestProcessor.handleNewConnection(request.connectionID, (String) request.request);
                                    break;
                                case PendingRequest.LOST_CONNECTION:
                                    RequestProcessor.handleLostConnection(request.connectionID, true);
                                    break;
                                case PendingRequest.PROCESS_REQUEST:
                                    RequestProcessor.handleProcess(request.connectionID, request.request);
                                    break;
                                default:
                                    trace.error("Unknown pending request type: " + request.type);
                                }
                            } catch (Throwable t) {
                                trace.error("Error processing request", t);
                            }
                        }
                        long stFinal = System.currentTimeMillis() - st;
                        if (stFinal > MAX_TIME_BATCH) {
                            trace.info("Set of size " + setSize + " took: "+stFinal);
                        }
                    } catch (Throwable t) {
                        trace.error("Error process request set", t);
                    } finally {
                        RequestProcessor.completedRequestSet(requestSet);
                        m_setsProcessed++;
                        m_totalRequestsProcessed += setSize;
                        if (setSize < m_minSetSize) m_minSetSize = setSize;
                        if (setSize > m_maxSetSize) m_maxSetSize = setSize;
                        m_totalConnectionsProcessed += requestSet.getConnections().size();

                        if (m_setsProcessed > 0 && ((m_setsProcessed % SETS_PER_DEBUG) == 0)) {
                            float averageSetSize = (float) m_totalRequestsProcessed / m_setsProcessed;
                            float averageConnectionSize = (float) m_totalConnectionsProcessed / m_setsProcessed;
                            trace.info("Total Sets = " + m_setsProcessed + " AverageSize = " + averageSetSize + " AverageConnections = " + averageConnectionSize);
                            trace.info("Max Size = " + m_maxSetSize + " Min = " + m_minSetSize);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            trace.fatal("RequestRunner error in main loop", t);
        } finally {
            if (!m_stopped) {
                trace.fatal("RequestRunner exiting main loop while not stopped.", new RuntimeException("Stack"));
            }
        }
    }
}
