/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:09:45 AM
 */
package com.topcoder.server.AdminListener.response;

public class SetProblemsAck extends ContestManagementAck {

    public SetProblemsAck(Throwable exception) {
        super(exception);
    }

    public SetProblemsAck() {
        super();
    }
}
