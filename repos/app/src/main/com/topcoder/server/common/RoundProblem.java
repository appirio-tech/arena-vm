/**
 * Class RoundProblem
 *
 * Author: Hao Kung
 *
 * Description: This class will contain information about a RoundProblem
 */
package com.topcoder.server.common;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.problem.Problem;

public class RoundProblem implements Serializable, CustomSerializable {

    int m_points;
    Problem m_problem;

    public final int getPointVal() {
        return m_points;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(m_points);
        writer.writeInt(m_index);
        writer.writeObject(m_problem);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        m_points = reader.readInt();
        m_index = reader.readInt();
        m_problem = (Problem) reader.readObject();
    }

    public final Problem getProblem() {
        return m_problem;
    }

    private int m_index;

    public int getIndex() {
        return m_index;
    }

    public RoundProblem(int points, Problem p, int index) {
        m_points = points;
        m_problem = p;
        m_index = index;
    }
}
