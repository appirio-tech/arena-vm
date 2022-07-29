/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 4, 2002
 * Time: 6:15:49 PM
 */
package com.topcoder.server.AdminListener.response;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.*;
import java.util.*;

public class GetLoggingStreamsAck implements CustomSerializable {

    private Collection streams = new Vector();
    
    public GetLoggingStreamsAck() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        //super.customWriteObject(writer);
        writer.writeObjectArray(streams.toArray());
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        //super.customReadObject(reader);
        streams = Arrays.asList(reader.readObjectArray());
    }

    public GetLoggingStreamsAck(Collection streams) {
        this.streams.addAll(streams);
    }

    public Collection getStreams() {
        return streams;
    }
}
