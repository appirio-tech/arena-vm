/*
 * LongSubmissionId
 * 
 * Created 09/29/2006
 */
package com.topcoder.server.common;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Long submission identifier
 * 
 * @author Diego Belfer (mural)
 * @version $Id: LongSubmissionId.java 54869 2006-12-01 18:02:46Z thefaxman $
 */
public class LongSubmissionId implements Serializable, CustomSerializable {
    private int roundId;
    private int componentId;
    private int coderId;
    private boolean example;
    private int submissionNumber;
    
    public LongSubmissionId() {
    }
    
    public LongSubmissionId(int roundId, int coderId, int componentId, boolean example, int submissionNumber) {
        this.roundId = roundId;
        this.coderId = coderId;
        this.componentId = componentId;
        this.example = example;
        this.submissionNumber = submissionNumber;
    }
    
    public int getCoderId() {
        return coderId;
    }
    public void setCoderId(int coderId) {
        this.coderId = coderId;
    }
    public int getComponentId() {
        return componentId;
    }
    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }
    public boolean isExample() {
        return example;
    }
    public void setExample(boolean example) {
        this.example = example;
    }
    public int getRoundId() {
        return roundId;
    }
    public void setRoundId(int roundId) {
        this.roundId = roundId;
    }
    public int getSubmissionNumber() {
        return submissionNumber;
    }
    public void setSubmissionNumber(int submissionNumber) {
        this.submissionNumber = submissionNumber;
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        this.roundId = reader.readInt();
        this.coderId = reader.readInt();
        this.componentId = reader.readInt();
        this.example = reader.readBoolean();
        this.submissionNumber = reader.readInt();
        
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(this.roundId);
        writer.writeInt(this.coderId);
        writer.writeInt(this.componentId);
        writer.writeBoolean(this.example);
        writer.writeInt(this.submissionNumber);
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + coderId;
        result = PRIME * result + componentId;
        result = PRIME * result + (example ? 1231 : 1237);
        result = PRIME * result + roundId;
        result = PRIME * result + submissionNumber;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final LongSubmissionId other = (LongSubmissionId) obj;
        if (coderId != other.coderId)
            return false;
        if (componentId != other.componentId)
            return false;
        if (example != other.example)
            return false;
        if (roundId != other.roundId)
            return false;
        if (submissionNumber != other.submissionNumber)
            return false;
        return true;
    }
}
