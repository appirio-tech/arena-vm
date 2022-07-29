package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class CompileResponse
        extends Message {

    private boolean success;
    private String output;

    public CompileResponse() {
    }

    public CompileResponse(boolean success, String output) {
        this.success = success;
        this.output = output;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getOutput() {
        return output;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeBoolean(success);
        writer.writeString(output);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        success = reader.readBoolean();
        output = reader.readString();
    }
}
