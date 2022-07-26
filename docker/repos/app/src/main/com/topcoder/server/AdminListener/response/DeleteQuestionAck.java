/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:09:45 AM
 */
package com.topcoder.server.AdminListener.response;

public class DeleteQuestionAck extends ContestManagementAck {

    public DeleteQuestionAck(Throwable exception) {
        super(exception);
    }

    public DeleteQuestionAck() {
        super();
    }
}
