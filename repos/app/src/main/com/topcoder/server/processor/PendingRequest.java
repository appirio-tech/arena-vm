package com.topcoder.server.processor;

/**
 * Wrapper class around an incoming request.  Stores the type, connection id and actual request object.
 */
final class PendingRequest {

    static final int NEW_CONNECTION = 0;
    static final int LOST_CONNECTION = 1;
    static final int PROCESS_REQUEST = 2;

    int type;
    int connectionID;
    Object request;

    /**
     * Create a new PendingRequest with the given type, connection id and request object.
     */
    PendingRequest(int theType, int cID, Object req) {
        type = theType;
        connectionID = cID;
        request = req;
    }
}
