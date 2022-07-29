package com.topcoder.server.AdminListener.response;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;
import java.io.Serializable;

import java.util.*;


public class RoundAccessResponse implements CustomSerializable, Serializable {

    private Collection rounds;
    
    public RoundAccessResponse() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObjectArray(rounds.toArray());
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        rounds = Arrays.asList(reader.readObjectArray());
    }

    public RoundAccessResponse(Collection rounds) {
        this.rounds = rounds;
    }

    public Collection getRounds() {
        return rounds;
    }
}

