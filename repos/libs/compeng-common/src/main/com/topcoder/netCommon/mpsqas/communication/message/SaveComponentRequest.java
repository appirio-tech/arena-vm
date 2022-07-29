package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 * @author mitalub
 */
public class SaveComponentRequest extends Message {

    public SaveComponentRequest() {
    }

    public SaveComponentRequest(ComponentInformation component) {
        this.component = component;
    }

    public ComponentInformation getComponent() {
        return component;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeObject(component);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        component = (ComponentInformation) reader.readObject();
    }

    private ComponentInformation component;
}
