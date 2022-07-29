package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class DirectionalMoveRequest
        extends Message {

    protected int distance;

    public DirectionalMoveRequest() {
    }

    public DirectionalMoveRequest(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeInt(distance);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        distance = reader.readInt();
    }
}
