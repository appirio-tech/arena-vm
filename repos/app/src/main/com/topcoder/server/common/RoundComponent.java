/**
 * Class RoundProblem
 *
 * Author: Hao Kung
 *
 * Description: This class will contain information about a RoundProblem
 */
package com.topcoder.server.common;

import java.io.IOException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.problem.SimpleComponent;

public final class RoundComponent implements Serializable, CustomSerializable {

    private int m_points;
    private SimpleComponent m_component;

    public RoundComponent() {
    }

    public RoundComponent(int points, SimpleComponent c) {
        m_points = points;
        m_component = c;
    }

    public final int getPointVal() {
        return m_points;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(m_points);
        writer.writeObject(m_component);
    }

    public void customReadObject(CSReader reader) throws IOException {
        m_points = reader.readInt();
        m_component = (SimpleComponent) reader.readObject();
    }

    public final SimpleComponent getComponent() {
        return m_component;
    }

    public String toString() {
        return "(RoundComponent)[points = " + m_points + ", component = " + m_component + "]";
    }

}
