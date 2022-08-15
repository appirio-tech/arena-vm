package com.topcoder.server.AdminListener.response;

public class ModifyMessageAck extends ContestManagementAck {

    public ModifyMessageAck(Throwable exception) {
        super(exception);
    }

    public ModifyMessageAck() {
        super();
    }
}
