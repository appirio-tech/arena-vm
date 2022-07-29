package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 * Lets the applet know the component id structure of the component it is
 * viewing.
 *
 * @author mitalub
 */
public class NewComponentIdStructureResponse
        extends Message {

    private ComponentIdStructure componentIdStructure;

    public NewComponentIdStructureResponse() {
    }

    public NewComponentIdStructureResponse(ComponentIdStructure componentIdStructure) {
        this.componentIdStructure = componentIdStructure;
    }

    public ComponentIdStructure getComponentIdStructure() {
        return componentIdStructure;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeObject(componentIdStructure);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        componentIdStructure = (ComponentIdStructure) reader.readObject();
    }
}
