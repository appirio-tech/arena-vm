/*
 * KeepAliveInitializationDataResponse Created 03/19/2006
 */
package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response message with the initialization data for the keep-alive feature. This message contains all
 * configurable server parameters that the Client requires to implement keep-alive feature.<br>
 * Use: This response is sent after successfully reconnected or logged in. The client should initialize the keep-alive
 * time intervals accordingly.<br>
 * 
 * @author Diego Belfer (mural)
 * @version $Id: KeepAliveInitializationDataResponse.java 72313 2008-08-14 07:16:48Z qliu $
 */
public class KeepAliveInitializationDataResponse extends BaseResponse {
    /**
     * Represents the time (in Milliseconds) the client waits for activity before sending a KeepAliveRequest message
     */
    private long timeout;

    /**
     * Represents the time (in Milliseconds) the client waits for activity before sending a KeepAliveRequest message,
     * when http tunnel option is used
     */
    private long httpTimeout;

    /**
     * Creates a new instance of <code>KeepAliveInitializationDataResponse</code>. It is required by custom
     * serialization.
     */
    public KeepAliveInitializationDataResponse() {
    }

    /**
     * Gets the timeout of regular socket connections.
     * 
     * @return the timeout.
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Sets the timeout of regular socket connections.
     * 
     * @param keepAliveConnectionTimeout the timeout to set.
     */
    public void setTimeout(long keepAliveConnectionTimeout) {
        this.timeout = keepAliveConnectionTimeout;
    }

    /**
     * Gets the timeout of HTTP tunnelling connections.
     * 
     * @return the timeout.
     */
    public long getHttpTimeout() {
        return httpTimeout;
    }

    /**
     * Sets the timeout of HTTP tunnelling connections.
     * 
     * @param keepAliveHttpConnectionTimeout the timeout to set.
     */
    public void setHttpTimeout(long keepAliveHttpConnectionTimeout) {
        this.httpTimeout = keepAliveHttpConnectionTimeout;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.KeepAliveInitializationDataResponse) [");
        ret.append("timeout = ");
        ret.append(timeout);
        ret.append(", ");
        ret.append("httpTimeout = ");
        ret.append(httpTimeout);
        ret.append("]");
        return ret.toString();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeLong(timeout);
        writer.writeLong(httpTimeout);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        timeout = reader.readLong();
        httpTimeout = reader.readLong();
    }
}