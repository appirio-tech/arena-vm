package com.topcoder.client.mpsqasApplet.messaging.message;

import com.topcoder.netCommon.mpsqas.communication.message.Message;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CSReader;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * Response specifying that a message be displayed in a pop up window.
 *
 * @author mitalub
 */
public class PopupResponse extends Message {

    private String message;

    public PopupResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(message);
    }

    public void customReadObject(CSReader reader) throws IOException,
            ObjectStreamException {
        message = reader.readString();
    }
}

