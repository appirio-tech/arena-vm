package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;
import java.io.ObjectStreamException;

public class RecalculateScoreRequest extends ContestMonitorRequest implements ProcessedAtBackEndRequest {

    private String handle;
    private int roundId;

    public RecalculateScoreRequest() {
        
    }
    
    public RecalculateScoreRequest(int roundId, String handle) {
    	this.roundId = roundId;
        this.handle = handle;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(handle);
        writer.writeInt(roundId);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        handle = reader.readString();
        roundId = reader.readInt();
    }

    public String getHandle() {
        return handle;
    }
    
    public int getRoundId() {
    	return roundId;
    }

}