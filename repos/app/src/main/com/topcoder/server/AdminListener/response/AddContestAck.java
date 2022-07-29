/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:09:45 AM
 */
package com.topcoder.server.AdminListener.response;

public class AddContestAck extends ContestManagementAck {

    public AddContestAck(Throwable exception) {
        super(exception);
    }

    public AddContestAck() {
        super();
    }
}
