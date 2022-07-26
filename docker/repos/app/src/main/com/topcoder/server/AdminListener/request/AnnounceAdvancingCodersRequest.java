package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public class AnnounceAdvancingCodersRequest extends RoundIDCommand {

    private int numAdvancing;

    public AnnounceAdvancingCodersRequest() {
    }

    public AnnounceAdvancingCodersRequest(int roundID, int numAdvancing) {
        super(roundID);
        this.numAdvancing = numAdvancing;
    }

    public int getNumAdvancing() {
        return numAdvancing;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(numAdvancing);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        numAdvancing = reader.readInt();
    }
}



