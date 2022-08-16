/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.server.AdminListener.response;

/**
 * this is the response message to set round event.
 */
public class SetRoundEventsAck extends ContestManagementAck {
    
    /**
     * <p>
     * the set round event response with exception
     * </p>
     * @param exception
     *          the thrown exception.
     */
    public SetRoundEventsAck(Throwable exception) {
        super(exception);
    }
    /**
     * <p>
     * the default set round event response.
     * </p>
     */
    public SetRoundEventsAck() {
        super();
    }
}
