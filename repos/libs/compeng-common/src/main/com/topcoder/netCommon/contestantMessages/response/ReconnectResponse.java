/*
 * ReconnectResponse.java Created on January 17, 2005, 3:13 PM
 */

package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.SealedSerializable;

/**
 * Defines a response to notify the client about the result of a reconnection. When the reconnection is successful, an
 * ID of the current connection and a different encrypted hash is sent as well.<br>
 * Use: This response is specific to <code>ReconnectRequest</code>. When the reconnection fails, the client should
 * log off the user immediately and ask the user to re-login. When the reconnection succeeds, the new connection ID and
 * the new encrypted hash should replace the previous ones.
 * 
 * @author Ryan Fairfax
 * @version $Id: ReconnectResponse.java 72343 2008-08-15 06:09:22Z qliu $
 */
public class ReconnectResponse extends BaseResponse {
    /**
     * Creates a new instance of <code>ReconnectResponse</code>. It is required by custom serialization.
     */
    public ReconnectResponse() {
    }

    /** Represents a flag indicating the reconnection is successful. */
    private boolean success;

    /** Represents the new hash code used to identify the connection. */
    private SealedSerializable hashCode;

    /** Represents the ID of the new connection. */
    private long connectionID;

    /**
     * Creates a new instance of <code>ReconnectResponse</code>. This constructor should be used when the
     * reconnection fails, since it leaves connection ID and hash uninitialized.
     * 
     * @param success <code>true</code> if the reconnection is successful; <code>false</code> otherwise.
     */
    public ReconnectResponse(boolean success) {
        super();
        this.success = success;
        hashCode = null;
        connectionID = 0;
    }

    /**
     * Creates a new instance of <code>ReconnectResponse</code>.
     * 
     * @param success <code>true</code> if the reconnection is successful; <code>false</code> otherwise.
     * @param hash the new hash used to identify the connection.
     * @param connectionID the ID of the new connection.
     */
    public ReconnectResponse(boolean success, SealedSerializable hash, long connectionID) {
        super();
        this.success = success;
        this.hashCode = hash;
        this.connectionID = connectionID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeBoolean(success);
        writer.writeObject(hashCode);
        writer.writeLong(connectionID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        success = reader.readBoolean();
        hashCode = (SealedSerializable) reader.readObject();
        connectionID = reader.readLong();
    }

    /**
     * Gets a flag indicating if reconnection is successful.
     * 
     * @return <code>true</code> if the reconnection is successful; <code>false</code> otherwise.
     */
    public boolean getSuccess() {
        return success;
    }

    /**
     * Gets the new encrypted hash identifying the connection.
     * 
     * @return the new encrypted hash.
     */
    public SealedSerializable getHashCode() {
        return hashCode;
    }

    /**
     * Gets the ID of the new connection.
     * 
     * @return the ID of the new connection.
     */
    public long getConnectionID() {
        return connectionID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.ReconnectResponse) [");
        ret.append("success = ");
        ret.append(success);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
