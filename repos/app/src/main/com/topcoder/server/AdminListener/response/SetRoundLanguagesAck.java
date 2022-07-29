/*
 * SetRoundLanguagesAck
 * 
 * Created 05/15/2007
 */
package com.topcoder.server.AdminListener.response;

/**
 * @autor Diego Belfer (Mural)
 * @version $Id: SetRoundLanguagesAck.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class SetRoundLanguagesAck extends ContestManagementAck {

    public SetRoundLanguagesAck(Throwable exception) {
        super(exception);
    }

    public SetRoundLanguagesAck() {
        super();
    }
}
