package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public final class RegistrationObject extends RoundIDCommand implements CustomSerializable {

    private int eventID;

    public RegistrationObject() {
    }

    public RegistrationObject(int roundID, int eventID) {
        super(roundID);
        this.eventID = eventID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(eventID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        eventID = reader.readInt();
    }

    public int getEventID() {
        return eventID;
    }

}
