package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class JumpRequest
        extends Message {

    protected String pattern;

    public JumpRequest() {
    }

    public JumpRequest(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeString(pattern);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        pattern = reader.readString();
    }
}
