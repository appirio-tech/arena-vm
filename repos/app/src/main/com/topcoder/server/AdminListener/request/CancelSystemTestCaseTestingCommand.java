package com.topcoder.server.AdminListener.request;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;


public final class CancelSystemTestCaseTestingCommand extends RoundIDCommand implements CustomSerializable {

    private int testCaseId;

    public CancelSystemTestCaseTestingCommand() {
    }

    public CancelSystemTestCaseTestingCommand(int roundID, int testCaseId) {
        super(roundID);
        this.testCaseId = testCaseId;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(testCaseId);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        testCaseId = reader.readInt();
    }

    public int getTestCaseId() {
        return testCaseId;
    }
}
