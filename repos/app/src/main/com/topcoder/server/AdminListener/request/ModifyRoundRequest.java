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

public class ModifyRoundRequest extends ContestManagementRequest {

    private RoundData round;
    private int id;

    public ModifyRoundRequest(int id, RoundData round) {
        this.round = round;
        this.id = id;
    }
    
    public ModifyRoundRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeObject(round);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        id = reader.readInt();
        round = (RoundData)reader.readObject();
    }

    public int getId() {
        return id;
    }

    public RoundData getRound() {
        return round;
    }
}
