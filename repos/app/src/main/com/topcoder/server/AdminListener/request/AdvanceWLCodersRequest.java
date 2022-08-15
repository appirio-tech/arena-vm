package com.topcoder.server.AdminListener.request;

import java.io.IOException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

public final class AdvanceWLCodersRequest extends RoundIDCommand {

    private int targetRoundId;

    public AdvanceWLCodersRequest() {
    }

    public AdvanceWLCodersRequest(int roundID, int targetRoundId) {
        super(roundID);
        this.targetRoundId = targetRoundId;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(targetRoundId);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        targetRoundId = reader.readInt();
    }

    public int getTargetRoundId() {
        return targetRoundId;
    }

}
