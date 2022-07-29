package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class NewStatusMessage
        extends Message {

    private boolean urgent;
    private StringBuffer message;

    public NewStatusMessage() {
    }

    public NewStatusMessage(boolean urgent, String message, Exception exception) {
        StringWriter writer = new StringWriter();

        exception.printStackTrace(new PrintWriter(writer));
        this.urgent = urgent;
        this.message = new StringBuffer(message);
        this.message.append("\n");
        this.message.append(writer.getBuffer());
    }

    public NewStatusMessage(String message, Exception exception) {
        this(false, message, exception);
    }

    public NewStatusMessage(boolean urgent, String message) {
        this.urgent = urgent;
        this.message = new StringBuffer(message);
    }

    public NewStatusMessage(String message) {
        this(false, message);
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeString(message.toString());
        writer.writeBoolean(urgent);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        message = new StringBuffer(reader.readString());
        urgent = reader.readBoolean();
    }

    public String getMessage() {
        return message.toString();
    }

    public boolean isUrgent() {
        return urgent;
    }

    public String toString() {
        return "NewStatusMessage[message=\"" + getMessage() + "\"]";
    }
}

