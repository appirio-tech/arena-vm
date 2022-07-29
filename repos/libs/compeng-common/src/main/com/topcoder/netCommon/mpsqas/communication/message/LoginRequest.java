package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 * Represents a login request message.
 * @author Logan Hanks
 */
public class LoginRequest
        extends Message {

    protected String handle;
    protected SealedSerializable password;

    /**
     * Construct an empty request before deserializing by calling <tt>customReadObject</tt>.
     * @see #customReadObject
     */
    public LoginRequest() {
    }

    /**
     * Construct a login request message, ready for sending.
     *
     * @param handle   the handle of the user that is logging in
     * @param password the user's password
     */
    public LoginRequest(String handle, SealedSerializable password) {
        this.handle = handle;
        this.password = password;
    }

    public String getHandle() {
        return handle;
    }

    public SealedSerializable getPassword() {
        return password;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeString(handle);
        writer.writeObject(password);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        handle = reader.readString();
        password = (SealedSerializable) reader.readObject();
    }
}
