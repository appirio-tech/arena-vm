/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:09:45 AM
 */
package com.topcoder.server.AdminListener.response;

public class DeleteContestAck extends ContestManagementAck {

    public DeleteContestAck(Throwable exception) {
        super(exception);
    }

    public DeleteContestAck() {
        super();
    }
}
