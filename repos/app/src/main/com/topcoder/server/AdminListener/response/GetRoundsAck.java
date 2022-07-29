/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:09:45 AM
 */
package com.topcoder.server.AdminListener.response;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;
import java.util.*;

public class GetRoundsAck extends ContestManagementAck {

    private Collection rounds;
    
    public GetRoundsAck() {
        super();
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObjectArray(rounds.toArray());
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        rounds = Arrays.asList(reader.readObjectArray());
    }

    public GetRoundsAck(Throwable exception) {
        super(exception);
    }

    public GetRoundsAck(Collection rounds) {
        super();
        this.rounds = rounds;
    }

    public Collection getRounds() {
        return rounds;
    }
}
