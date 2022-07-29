/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:09:45 AM
 */
package com.topcoder.server.AdminListener.response;

public class ModifyContestAck extends ContestManagementAck {

    public ModifyContestAck(Throwable exception) {
        super(exception);
    }

    public ModifyContestAck() {
        super();
    }
}
