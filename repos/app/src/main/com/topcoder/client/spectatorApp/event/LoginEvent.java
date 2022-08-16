/**
 * LoginEvent.java
 *
 * Description:		Contain information about the response to a login request
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


public class LoginEvent extends java.util.EventObject {

    /** Userid of the login */
    public String userid;
    /** Password of the login */
    public String password;
    /** Error message */
    public String errorMsg;

    /**
     *  Constructor of a Login Event
     *
     *  @param source   the source of the event
     *  @param userid   the userid of the login
     *  @param password the password of the login
     */
    public LoginEvent(Object source, String userid, String password) {
        this(source, userid, password, null);
    }

    /**
     *  Constructor of a Login Event
     *
     *  @param source   the source of the event
     *  @param userid   the userid of the login
     *  @param password the password of the login
     *  @param errormsg the errormsg related to the login
     */
    public LoginEvent(Object source, String userid, String password, String errorMsg) {
        super(source);
        this.userid = userid;
        this.password = password;
        this.errorMsg = errorMsg;
    }

    /** Gets the userid */
    public String getUserId() {
        return userid;
    }

    /** Gets the password */
    public String getPassword() {
        return password;
    }

    /** Gets the errorMsg.  Will be null if not applicable */
    public String getErrorMsg() {
        return errorMsg;
    }
}


/* @(#)LoginEvent.java */
