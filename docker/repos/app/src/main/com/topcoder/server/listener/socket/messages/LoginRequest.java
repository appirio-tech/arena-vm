/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.listener.socket.messages;

import java.io.IOException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.messages.Message;


/**
 * The request to login.
 *
 * <p>
 * Changes in version 1.1 (Module Assembly - TCC Web Socket - Get Registered Rounds and
 * Round Problems):
 * <ol>
 *      <li>Remove implementing Serializable.</li>
 *      <li>Add extending Message to implement CustomSerializable.</li>
 * </ol>
 * </p>
 *
 * @see com.topcoder.shared.netCommon.messages.Message
 * @author TCSASSEMBLER, dexy
 * @version 1.1
 */
public class LoginRequest extends Message {
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2956100669824625515L;

    /** The user name. */
    private String username;

    /** The password. */
    private String password;

    /**
     * Instantiates a new login request.
     */
    public LoginRequest() {
    }

    /**
     * Creates a new instance of this class.
     *
     * @param   username    the username
     * @param   password    the password
     */
    public LoginRequest(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    /**
     * Getter for the user name.
     *
     * @return the user name.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter for the user name.
     *
     * @param username the user name.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter for the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter for the password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /* (non-Javadoc)
     * @see com.topcoder.shared.netCommon.messages.Message#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    @Override
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(username);
        writer.writeString(password);
    }

    /* (non-Javadoc)
     * @see com.topcoder.shared.netCommon.messages.Message#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    @Override
    public void customReadObject(CSReader reader) throws IOException {
        username = reader.readString();
        password = reader.readString();
    }

}
