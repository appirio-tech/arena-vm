/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:09:45 AM
 */
package com.topcoder.server.AdminListener.response;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

public class VerifyRoundAck extends ContestManagementAck {

    private String text;
    
    public VerifyRoundAck() {
        super();
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(text);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        text = reader.readString();
    }

    public VerifyRoundAck(Throwable exception) {
        super(exception);
    }

    public VerifyRoundAck(String text) {
        super();
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String toString() {
        return "VerifyRoundAck: text=" + text;
    }
}
