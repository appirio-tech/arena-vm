package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 * @author mitalub
 */
public class ViewComponentMoveRequest extends Message {

    public ViewComponentMoveRequest() {
    }

    public ViewComponentMoveRequest(int componentId) {
        this.componentId = componentId;
    }


    public int getComponentId() {
        return componentId;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeInt(componentId);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        componentId = reader.readInt();
    }

    private int componentId;
}
