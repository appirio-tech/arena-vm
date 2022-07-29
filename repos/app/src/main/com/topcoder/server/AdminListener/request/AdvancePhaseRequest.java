package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;

public class AdvancePhaseRequest extends RoundIDCommand {

    private Integer phaseId;

    public AdvancePhaseRequest() {
    }

    public AdvancePhaseRequest(int roundId, Integer phaseId) {
        super(roundId);
        this.phaseId = phaseId;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        if (phaseId == null) {
            writer.writeBoolean(true);
            writer.writeInt(0);
        } else {
            writer.writeBoolean(false);
            writer.writeInt(phaseId.intValue());
        }
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        boolean isPhaseNull = reader.readBoolean();
        int phase = reader.readInt();
        if (isPhaseNull) {
            phaseId = null;
        } else {
            phaseId = new Integer(phase);
        }
    }

    public Integer getPhaseId() {
        return phaseId;
    }
}
