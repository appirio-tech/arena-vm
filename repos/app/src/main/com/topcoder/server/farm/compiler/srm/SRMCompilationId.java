/*
 * SRMCompilationId
 * 
 * Created 12/14/2006
 */
package com.topcoder.server.farm.compiler.srm;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * @author Diego Belfer (mural)
 * @version $Id: SRMCompilationId.java 56700 2007-01-29 21:13:11Z thefaxman $
 */
public class SRMCompilationId implements Serializable, CustomSerializable {
    private int contestId;
    private int roundId;
    private int problemId;
    private int coderId;
    
    public SRMCompilationId() {
    }
    
    public SRMCompilationId(int contestId, int roundId, int problemId, int coderId) {
        this.contestId = contestId;
        this.roundId = roundId;
        this.problemId = problemId;
        this.coderId = coderId;
    }

    public int getCoderId() {
        return coderId;
    }

    public void setCoderId(int coderId) {
        this.coderId = coderId;
    }

    public int getContestId() {
        return contestId;
    }

    public void setContestId(int contestId) {
        this.contestId = contestId;
    }

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public int getRoundId() {
        return roundId;
    }

    public void setRoundId(int roundId) {
        this.roundId = roundId;
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        contestId = reader.readInt();
        roundId = reader.readInt();
        problemId = reader.readInt();
        coderId = reader.readInt();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(contestId);
        writer.writeInt(roundId);
        writer.writeInt(problemId);
        writer.writeInt(coderId);
    }
    
    public String toString() {
        return "c"+contestId+"r"+roundId+"p"+problemId+"u"+coderId;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + coderId;
        result = PRIME * result + contestId;
        result = PRIME * result + problemId;
        result = PRIME * result + roundId;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SRMCompilationId other = (SRMCompilationId) obj;
        if (coderId != other.coderId)
            return false;
        if (contestId != other.contestId)
            return false;
        if (problemId != other.problemId)
            return false;
        if (roundId != other.roundId)
            return false;
        return true;
    }
}
