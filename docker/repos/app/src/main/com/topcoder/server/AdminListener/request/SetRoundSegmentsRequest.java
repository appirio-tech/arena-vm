/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:07:44 AM
 */
package com.topcoder.server.AdminListener.request;

import com.topcoder.server.contest.*;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

public class SetRoundSegmentsRequest extends ContestManagementRequest {

    private int id;
    private RoundSegmentData segments;
    
    public SetRoundSegmentsRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeObject(segments);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        id = reader.readInt();
        segments = (RoundSegmentData)reader.readObject();
    }

    public SetRoundSegmentsRequest(int id, RoundSegmentData segments) {
        this.id = id;
        this.segments = segments;
    }

    public int getId() {
        return id;
    }

    public RoundSegmentData getSegments() {
        return segments;
    }
}
