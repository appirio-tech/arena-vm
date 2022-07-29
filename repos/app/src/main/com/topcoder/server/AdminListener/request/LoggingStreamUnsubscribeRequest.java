/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 4, 2002
 * Time: 6:01:24 PM
 */
package com.topcoder.server.AdminListener.request;

import com.topcoder.server.util.logging.net.*;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import java.io.*;

public class LoggingStreamUnsubscribeRequest extends MonitorRequest implements Serializable {

    private StreamID streamID;
    
    public LoggingStreamUnsubscribeRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(streamID);
    }
    
    public void customReadObject(CSReader reader)  throws IOException {
        streamID = (StreamID)reader.readObject();
    }

    public LoggingStreamUnsubscribeRequest(StreamID streamID) {
        this.streamID = streamID;
    }

    public StreamID getStreamID() {
        return streamID;
    }
}
