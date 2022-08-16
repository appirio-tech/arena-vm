/*
 * LongTestId
 * 
 * Created 09/14/2006
 */
package com.topcoder.server.farm.longtester;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Long Test identifier. 
 * 
 * Identifies a long test case scheduled in the farm.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: LongTestId.java 54869 2006-12-01 18:02:46Z thefaxman $
 */
public class LongTestId implements Serializable, CustomSerializable {
    private int testedType;
    private Object testedId; 
    private int testAction;
    private long testCaseId;
    
    public LongTestId() {
    }
    
    public LongTestId(int testedType, Object testedId, long testCaseId, int testAction) {
        this.testedType = testedType;
        this.testedId = testedId;
        this.testAction = testAction;
        this.testCaseId = testCaseId;
    }

    public int getTestAction() {
        return testAction;
    }

    public void setTestAction(int testAction) {
        this.testAction = testAction;
    }

    public long getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(long testCaseId) {
        this.testCaseId = testCaseId;
    }

    public Object getTestedId() {
        return testedId;
    }

    public void setTestedId(Object testedId) {
        this.testedId = testedId;
    }

    public int getTestedType() {
        return testedType;
    }

    public void setTestedType(int testedType) {
        this.testedType = testedType;
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        this.testedType = reader.readInt();
        this.testedId = reader.readObject();
        this.testAction = reader.readInt();
        this.testCaseId = reader.readLong();    
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(this.testedType);
        writer.writeObject(this.testedId);
        writer.writeInt(this.testAction);
        writer.writeLong(this.testCaseId); 
    }
    
    public String toString() {
        return "LongTestId=[type="+testedType+", testedId="+testedId+", testAction="+testAction+", testCaseId="+testCaseId+"]";
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + testAction;
        result = PRIME * result + (int) (testCaseId ^ (testCaseId >>> 32));
        result = PRIME * result + ((testedId == null) ? 0 : testedId.hashCode());
        result = PRIME * result + testedType;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final LongTestId other = (LongTestId) obj;
        if (testAction != other.testAction)
            return false;
        if (testCaseId != other.testCaseId)
            return false;
        if (testedId == null) {
            if (other.testedId != null)
                return false;
        } else if (!testedId.equals(other.testedId))
            return false;
        if (testedType != other.testedType)
            return false;
        return true;
    }
}