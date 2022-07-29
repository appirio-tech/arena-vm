package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.IOException;
import java.io.ObjectStreamException;


public class AllocatePrizesRequest extends RoundIDCommand {

    private boolean shouldCommit;

    public AllocatePrizesRequest() {
    }

    public AllocatePrizesRequest(int roundId, boolean shouldCommit) {
        super(roundId);
        this.shouldCommit = shouldCommit;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeBoolean(shouldCommit);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        shouldCommit = reader.readBoolean();
    }

    public boolean getShouldCommit() {
        return shouldCommit;
    }
}

