package com.topcoder.netCommon.contest;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines a class holding the maximum score of a problem.
 * 
 * @author Qi Liu
 * @version $Id: ProblemData.java 72046 2008-07-31 06:47:43Z qliu $
 */
public final class ProblemData implements Serializable, CustomSerializable {
    /** Represents the maximum score of a problem. */
    private int m_pointValue;

    /**
     * Creates a new instance of <code>ProblemData</code>. It is required by custom serialization.
     */
    public ProblemData() {
    }

    /**
     * Creates a new instance of <code>ProblemData</code>. The maximum score is given.
     * 
     * @param pointValue the maximum score.
     */
    public ProblemData(int pointValue) {
        m_pointValue = pointValue;
    }

    /**
     * Gets the maximum score of a problem.
     * 
     * @return the maximum score.
     */
    public int getPointValue() {
        return m_pointValue;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(m_pointValue);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        m_pointValue = reader.readInt();
    }
}
