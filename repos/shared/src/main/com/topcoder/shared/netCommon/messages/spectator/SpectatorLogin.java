/**
 * SpectatorLogin.java
 *
 * Description:		Specifies a login request for a spectator application
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.shared.netCommon.messages.spectator;

import com.topcoder.shared.netCommon.messages.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;


public class SpectatorLogin extends Message {

    /** userid for authentication */
    private String userid;

    /** password for authentication */
    private SealedSerializable password;

    /**
     * No-arg constructor needed by customserialization
     *
     */
    public SpectatorLogin() {
    }

    /**
     *  Constructor of a Spectator Login
     *
     *  @param userid   the userid for authentication
     *  @param password the password for authentication
     */
    public SpectatorLogin(String userid, SealedSerializable password) {
        super();
        this.userid = userid;
        this.password = password;
    }


    /**
     * Gets the userid for authentication
     * @returns the userid
     */
    public String getUserID() {
        return userid;
    }

    /**
     * Gets the password for authentication
     * @returns the password
     */
    public SealedSerializable getPassword() {
        return password;
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
        writer.writeString(userid);
        writer.writeObject(password);
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
        userid = reader.readString();
        password = (SealedSerializable) reader.readObject();
    }

    /**
     * Gets the string representation of this object
     *
     * @returns the string representation of this object
     */
    public String toString() {
        return new StringBuffer().append("(SpectatorLogin)[").append(userid).append(", ").append(password).append("]").toString();
    }
}


/* @(#)SpectatorLogin.java */
