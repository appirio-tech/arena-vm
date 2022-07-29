package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contestantMessages.request.ReconnectRequest;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.SealedSerializable;

/**
 * Defines a response to notify the client about the result of a logging in attempt.<br>
 * Use: This response will only be sent after a <code>LoginRequest</code>, no matter if the process succeeds or
 * fails. When the login attempt succeeds, the ID of the connection as well as an encrypted hash are sent back to the
 * client in the response. The hash code is encrypted by the symmetric encryption key negotiated by the server and the
 * client.
 * 
 * @author Lars Backstrom
 * @version $Id: LoginResponse.java 72313 2008-08-14 07:16:48Z qliu $
 * @see ReconnectRequest
 */
public class LoginResponse extends BaseResponse {
    /** Represents a flag indicating if the logging in is successful. */
    private boolean success;

    /** Represents the ID of the connection. */
    private long connectionID;

    /** Represents the encrypted hash used in reconnection. */
    private SealedSerializable hashCode;

    /**
     * Creates a new instance of <code>LoginResponse</code>. It is required by custom serialization.
     */
    public LoginResponse() {
    }

    /**
     * Creates a new instance of <code>LoginResponse</code>. This constructor should be used when the login attempt
     * fails, since it left the hash and the ID of the connection uninitialized.
     * 
     * @param success <code>true</code> if the login attempt succeeds; <code>false</code> otherwise.
     */
    public LoginResponse(boolean success) {
        super();
        this.success = success;
        this.connectionID = 0;
        this.hashCode = null;
    }

    /**
     * Creates a new instance of <code>LoginResponse</code>.
     * 
     * @param success <code>true</code> if the login attempt succeeds; <code>false</code> otherwise.
     * @param connectionID the ID of the connection.
     * @param hashCode the encrypted hash used in reconnection.
     */
    public LoginResponse(boolean success, long connectionID, SealedSerializable hashCode) {
        super();
        this.success = success;
        this.connectionID = connectionID;
        this.hashCode = hashCode;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeBoolean(success);
        writer.writeLong(connectionID);
        writer.writeObject(hashCode);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        success = reader.readBoolean();
        connectionID = reader.readLong();
        hashCode = (SealedSerializable) reader.readObject();
    }

    /**
     * Gets a flag indicating if the login attempt succeeds.
     * 
     * @return <code>true</code> if the login attempt succeeds; <code>false</code> otherwise.
     */
    public boolean getSuccess() {
        return success;
    }

    /**
     * Gets the ID of the connection.
     * 
     * @return the ID of the connection.
     */
    public long getConnectionID() {
        return connectionID;
    }

    /**
     * Gets the encrypted hash used in reconnection.
     * 
     * @return the encrypted hash.
     */
    public SealedSerializable getHashCode() {
        return hashCode;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.LoginResponse) [");
        ret.append("success = ");
        ret.append(success);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
