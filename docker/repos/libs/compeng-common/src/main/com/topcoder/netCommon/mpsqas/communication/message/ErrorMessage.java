package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class ErrorMessage
        extends Message {

    private StringBuffer message;

    public ErrorMessage(String message, Exception exception) {
        StringWriter writer = new StringWriter();

        exception.printStackTrace(new PrintWriter(writer));
        this.message = new StringBuffer(message);
        this.message.append(writer.getBuffer());
    }

    public ErrorMessage(String message) {
        this.message = new StringBuffer(message);
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeString(message.toString());
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        message = new StringBuffer(reader.readString());
    }

    public String getMessage() {
        return message.toString();
    }
}

