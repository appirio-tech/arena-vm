package com.topcoder.server.AdminListener.request;

import com.topcoder.server.contest.*;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

/**
 * This is a request to be sent to Admin Listener server to save the details
 * of round room assignment algorithm for concrete round in database. The ID
 * of round may be retrieved with <code>RoundRoomAssignment.getRoundID()</code>
 * method.
 *
 * @author  TCSDESIGNER
 * @version 1.0 07/31/2003
 * @since   Admin Tool 2.0
 */
public class SaveRoundRoomAssignmentRequest extends ContestManagementRequest {

    /**
     * The details of round room assignment algortihm for some round.
     */
    private RoundRoomAssignment details = null;
    
    public SaveRoundRoomAssignmentRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(details);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        details = (RoundRoomAssignment)reader.readObject();
    }

    /**
     * Constructs new SaveRoundRoomAssignmentRequest with specified details of
     * round room assignment algorithm that need to be saved.
     *
     * @param  details a RoundRoomAssignment instance containing the details of
     *         round room assignment algorithm for some round
     * @throws IllegalArgumentException if given argument is null
     */
    public SaveRoundRoomAssignmentRequest(RoundRoomAssignment details) {
        if( details == null )
            throw new IllegalArgumentException("details cannot be null");
        this.details = details;
    }

    /**
     * Gets the details of round room assignment algorithm for some round that
     * need to be saved. The ID of round may be retrieved by <code>
     * getRoundID()</code> method from this object.
     *
     * @return a RoundRoomAssignment instance containing the details of round 
     *         room assignment algorithm for some round.
     * @see    RoundRoomAssignment#getRoundID()
     */
    public RoundRoomAssignment getDetails() {
        return details;
    }
}
