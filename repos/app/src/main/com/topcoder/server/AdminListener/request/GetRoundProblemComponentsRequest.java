/*
* @author John Waymouth
*/
package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;


public class GetRoundProblemComponentsRequest extends ContestMonitorRequest implements ProcessedAtBackEndRequest {

    private int roundID;
    private int problemID;
    private int divisionID;
    private boolean isGlobal;
    
    public GetRoundProblemComponentsRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roundID);
        writer.writeInt(problemID);
        writer.writeInt(divisionID);
        writer.writeBoolean(isGlobal);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        roundID = reader.readInt();
        problemID = reader.readInt();
        divisionID = reader.readInt();
        isGlobal = reader.readBoolean();
    }

    public GetRoundProblemComponentsRequest(int roundID, int problemID, int divisionID) {
        this.roundID = roundID;
        this.problemID = problemID;
        this.divisionID = divisionID;
        this.isGlobal = false;
    }

    /**
     * Constructs new GetRoundProblemComponentsRequest that represents the request to get all components of all problems
     * assigned to specified round.
     * @param roundID an int representing the ID of requested round.
     * @since Admin Tool 2.0
     */    
    public GetRoundProblemComponentsRequest(int roundID) {
        this.roundID = roundID;
        this.isGlobal = true;
    }

    public int getRoundID() {
        return roundID;
    }

    public int getProblemID() {
        return problemID;
    }

    public int getDivisionID() {
        return divisionID;
    }

    /**
     * Gets the scope of this request.
     * @return true if this request requires to get data for all components of all problems assigned to specified round;
     * false if this request requires to get data for components for specified problem of specified round.
     * @since  Admin Tool 2.0
     */
    public boolean isGlobal() {
        return isGlobal;
    }

    public GetRoundProblemComponentsRequest(boolean global) {
        isGlobal = global;
    }
}
