/*
 * ReconnectRequest.java Created on January 17, 2005, 1:36 PM
 */

package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.SealedSerializable;

/**
 * Defines a request to reconnect to the server.<br>
 * Use: When there is a temporary interruption of the connection to the server, and a new connection is established
 * later, this request can be sent to re-establish the state of the connection without logging in again.<br>
 * Note: This request can be sent instead of <code>LoginRequest</code>. Still, key exchanging and possibly client
 * verification must be done before sending this request. An encrypted hash object is used to authenticate that this new
 * connection is indeed a continuation of the previously broken connection from the same client. The hash object must be
 * encrypted using the key exchanged.
 * 
 * @author Ryan Fairfax
 * @version $Id: ReconnectRequest.java 72292 2008-08-12 09:10:29Z qliu $
 * @see ExchangeKeyRequest
 * @see LoginRequest
 */
public class ReconnectRequest extends BaseRequest {
    /** Represents the encrypted hash object. */
    private SealedSerializable hash;

    /** Represents the ID of the broken connection. */
    private long connectionID;

    /**
     * Creates a new instance of <code>ReconnectRequest</code>. It is required by custom serialization.
     */
    public ReconnectRequest() {
    }

    /**
     * Creates a new instance of <code>ReconnectRequest</code>.
     * 
     * @param cid the ID of the broken connection.
     * @param h the encrypted hash object.
     */
    public ReconnectRequest(long cid, SealedSerializable h) {
        this.connectionID = cid;
        this.hash = h;
    }

    /**
     * Gets the ID of the broken connection.
     * 
     * @return the ID of the broken connection.
     */
    public long getConnectionID() {
        return connectionID;
    }

    /**
     * Gets the encrypted hash object.
     * 
     * @return the encrypted hash object.
     */
    public SealedSerializable getHash() {
        return hash;
    }

    public int getRequestType() {
        return ContestConstants.RECONNECT;
    }

    /**
     * Serializes the object
     * 
     * @param writer the custom serialization writer
     * @throws IOException exception during writing
     * @see com.topcoder.shared.netCommon.CSWriter
     * @see java.io.IOException
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeLong(connectionID);
        writer.writeObject(hash);
    }

    /**
     * Creates the object from a serialization stream
     * 
     * @param reader the custom serialization reader
     * @throws IOException exception during reading
     * @see com.topcoder.shared.netCommon.CSWriter
     * @see java.io.IOException
     * @see java.io.ObjectStreamException
     */
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        connectionID = reader.readLong();
        hash = (SealedSerializable) reader.readObject();
    }

    /**
     * Gets the string representation of this object
     * 
     * @return the string representation of this object
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.ReconnectRequest) [");
        ret.append("connectionid = ");
        ret.append(connectionID);
        ret.append(", ");
        ret.append("hash = ");
        if (hash == null) {
            ret.append("null");
        } else {
            ret.append(hash.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
