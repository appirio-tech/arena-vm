/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 4, 2002
 * Time: 6:01:24 PM
 */
package com.topcoder.server.AdminListener.request;

import com.topcoder.server.util.logging.net.*;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;
import java.util.Date;

public class LoggingStreamSubscribeRequest extends MonitorRequest{

    private StreamID streamID;
    
    public LoggingStreamSubscribeRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(streamID);
    }
    
    public void customReadObject(CSReader reader)  throws IOException {
        streamID = (StreamID)reader.readObject();
    }

    public LoggingStreamSubscribeRequest(StreamID streamID) {
        this.streamID = streamID;
    }

    public StreamID getStreamID() {
        return streamID;
    }
}
