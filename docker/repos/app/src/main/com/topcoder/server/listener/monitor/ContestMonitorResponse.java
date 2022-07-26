/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 7, 2002
 * Time: 12:20:29 AM
 */
package com.topcoder.server.listener.monitor;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public abstract class ContestMonitorResponse implements Serializable, CustomSerializable {

    private int recipientID;

    public int getRecipientID() {
        return recipientID;
    }

    public void setRecipientID(int recipientID) {
        this.recipientID = recipientID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(recipientID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        recipientID = reader.readInt();
    }
}
