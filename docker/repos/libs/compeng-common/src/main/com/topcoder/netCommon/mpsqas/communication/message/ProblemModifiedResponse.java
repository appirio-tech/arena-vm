package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class ProblemModifiedResponse
        extends Message {

    private String modifierName;

    public ProblemModifiedResponse() {
    }

    public ProblemModifiedResponse(String modifierName) {
        this.modifierName = modifierName;
    }

    public String getModifierName() {
        return modifierName;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeString(modifierName);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        modifierName = reader.readString();
    }
}

