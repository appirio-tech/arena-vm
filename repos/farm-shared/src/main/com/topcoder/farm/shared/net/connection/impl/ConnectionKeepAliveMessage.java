/*
 * ConnectionKeepAliveMessage
 *
 * Created 12/06/2006
 */
package com.topcoder.farm.shared.net.connection.impl;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * KeepAlive message used to avotoken connection drops
 *
 * @author Diego Belfer (mural)
 * @version $token$
 */
public class ConnectionKeepAliveMessage implements Serializable, CustomSerializable{
    private long token;

    /**
     * Creates a new ConnectionKeepAliveMessage.
     */
    public ConnectionKeepAliveMessage() {
    }

    /**
     * Creates a new ConnectionKeepAliveMessage containg the given
     * token.
     *
     * @param token The toke to set
     */
    public ConnectionKeepAliveMessage(long token) {
        this.token = token;
    }

    /**
     * Returns the token set for this message
     * @return the token
     */
    public long getToken() {
        return token;
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        token = reader.readLong();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeLong(token);
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "ConnectionKeepAliveMessage[token="+token+"]";
    }
}
