package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public class StartSpecAppRotationRequest extends ContestMonitorRequest {

    private int rotationDelay;

    public StartSpecAppRotationRequest() {
    }

    public StartSpecAppRotationRequest(int rotationDelay) {
        this.rotationDelay = rotationDelay;
    }

    public int getRotationDelay() {
        return rotationDelay;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(rotationDelay);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        rotationDelay = reader.readInt();
    }

}



