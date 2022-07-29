package com.topcoder.server.processor;

import java.util.*;

/**
 * Contains a list of PendingRequests and the Set of connections that correspond to
 * the submitters of the requests.
 */
final class RequestSet {

    private LinkedList m_requests;
    private Set m_connections;

    RequestSet(LinkedList requests, Set connections) {
        m_requests = requests;
        m_connections = connections;
    }

    LinkedList getRequests() {
        return m_requests;
    }

    Set getConnections() {
        return m_connections;
    }
}
