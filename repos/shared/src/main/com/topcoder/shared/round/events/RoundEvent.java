/*
 * RoundEvent
 * 
 * Created 10/02/2007
 */
package com.topcoder.shared.round.events;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Base class for all Round Events
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public abstract class RoundEvent implements Serializable, CustomSerializable {
    private int roundId;
    private Integer roundTypeId;
    
    public RoundEvent() {
    }
    
    public RoundEvent(int roundId, Integer roundTypeId) {
        this.roundId = roundId;
        this.roundTypeId = roundTypeId;
    }

    public RoundEvent(int roundId) {
        this.roundId = roundId;
    }

    public int getRoundId() {
        return roundId;
    }

    public int getRoundTypeId() {
        return roundTypeId.intValue();
    } 
    
    public boolean isRoundTypeIdSet() {
        return roundTypeId != null;
    }
    
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        this.roundId = reader.readInt();
        this.roundTypeId = (Integer) reader.readObject();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(this.roundId);
        writer.writeObject(this.roundTypeId);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder(30);
        return sb.append(this.getClass().getSimpleName()).append("[").append(roundId).append(",").append(roundTypeId).append("]").toString();
    }

}
