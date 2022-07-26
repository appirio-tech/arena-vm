package com.topcoder.server.AdminListener.request;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

/**
 * This is a request to be sent to Admin Listener to refresh the permissions.
 *
 * Copyright (c) 2003 TopCoder, Inc.  All rights reserved.
 * 
 * @author  TCSDESIGNER
 * @version 1.0 11/03/2003
 * @since   Admin Tool 2.0
 */
public class RefreshAccessRequest extends MonitorRequest {

    /**
     * the round that is being requested
     */
    private int roundId;
    
    public RefreshAccessRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roundId);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        roundId = reader.readInt();
    }
    
    /**
     * Constructs a new RefreshAccessRequest from a round id and sets the subject
     * to null.
     * @param roundId -  the round to switch to
     */
    
    public RefreshAccessRequest(int roundId) {
        this.roundId = roundId;
    }

    /**
     * @return the round id associated with this request
     */
    public int getRoundId() {
        return roundId;
    }
}