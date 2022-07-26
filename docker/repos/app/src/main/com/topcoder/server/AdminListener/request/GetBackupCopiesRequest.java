package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

/**
 * This is a request to be sent to Admin Listener server to receive a list
 * of existing backup copies for specified round.
 *
 * @author  Giorgos Zervas
 * @version 11/26/2003
 * @since   Admin Tool 2.0
 */
public class GetBackupCopiesRequest extends ContestMonitorRequest implements ProcessedAtBackEndRequest {

    /**
     * An ID of requested round.
     */
    private int roundID = 0;

    public GetBackupCopiesRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roundID);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        roundID = reader.readInt();
    }
    
    /**
     * Constructs new GetBackupCopiesRequest with given ID of round to 
     * get the list of existing backup copies for.
     * 
     * @param roundID an ID of round to get list of existing backup copies for
     */
    public GetBackupCopiesRequest(int roundID) {
        this.roundID = roundID;
    }

    /**
     * Gets the ID of round to get list of existing backup copies for.
     *
     * @return the ID of requested round.
     */
    public int getRoundID() {
        return roundID;
    }
}
