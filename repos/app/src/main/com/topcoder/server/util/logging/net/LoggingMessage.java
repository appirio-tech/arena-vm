/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 4, 2002
 * Time: 5:16:09 PM
 */
package com.topcoder.server.util.logging.net;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import org.apache.log4j.spi.*;

import java.io.*;

public class LoggingMessage implements CustomSerializable, Serializable {

    private StreamID streamID;
    //private String event;
    private TCLoggingEvent event;
    
    public LoggingMessage() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(streamID);
        writer.writeObject(event);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        streamID = (StreamID)reader.readObject();
        event = (TCLoggingEvent) reader.readObject();
    }

    public LoggingMessage(StreamID streamID, TCLoggingEvent event) {
        this.streamID = streamID;
        this.event = event;
    }

    public StreamID getStreamID() {
        return streamID;
    }

    public TCLoggingEvent getEvent() {
        return event;
    }

}
