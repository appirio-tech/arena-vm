/**
 * SpectatorLoginResult.java
 *
 * Description:		Specifies the result of a login request for a spectator application
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.shared.netCommon.messages.spectator;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public class SpectatorLoginResult extends SpectatorLogin {

    /** indicator of successful login */
    private boolean success;

    /** message related to login attempt */
    private String message;

    /**
     * No-arg constructor needed by customserialization
     *
     */
    public SpectatorLoginResult() {
    }

    /**
     *  Constructor of a Spectator Login
     *
     *  @param userid   the userid for authentication
     *  @param password the password for authentication
     *  @param success whether the login request was successful or not
     *  @param message the message related to the login
     */
    public SpectatorLoginResult(String userid, SealedSerializable password, boolean success, String message) {
        super(userid, password);
        this.success = success;
        this.message = message;
    }


    /**
     * Whether the login attempt was successful or not
     * @returns true if successul, false if not
     */
    public boolean isSuccessful() {
        return success;
    }

    /**
     * Gets the message related to the login attempt.  A null may be returned for a successful login
     * @returns the message related to the login attempt;
     */
    public String getMessage() {
        return message;
    }

    /**
     * Serializes the object
     *
     * @param writer the custom serialization writer
     * @throws java.io.IOException exception during writing
     *
     * @see com.topcoder.netCommon.CSWriter
     * @see java.io.IOException
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeBoolean(success);
        writer.writeString(message);
    }

    /**
     * Creates the object from a serialization stream
     *
     * @param writer the custom serialization reader
     * @throws java.io.IOException           exception during reading
     * @throws java.io.ObjectStreamException exception during reading
     *
     * @see com.topcoder.netCommon.CSWriter
     * @see java.io.IOException
     * @see java.io.ObjectStreamException
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        success = reader.readBoolean();
        message = reader.readString();
    }

    /**
     * Gets the string representation of this object
     *
     * @returns the string representation of this object
     */
    public String toString() {
        return new StringBuffer().append("(SpectatorLoginResult)[").append(getUserID()).append(", ").append(getPassword()).append(", ").append(success).append(", ").append(message).append("]").toString();
    }

}


/* @(#)SpectatorLoginResult.java */
