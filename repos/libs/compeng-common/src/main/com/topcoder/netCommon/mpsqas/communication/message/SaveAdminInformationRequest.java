package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;
import java.util.*;

/**
 *
 * @author Logan Hanks
 */
public class SaveAdminInformationRequest
        extends Message {

    private int status;
    private int primarySolutionId;
    private ArrayList problemTesterIds;

    public SaveAdminInformationRequest() {
    }

    public SaveAdminInformationRequest(int status, int primarySolutionId, ArrayList problemTesterIds) {
        this.status = status;
        this.primarySolutionId = primarySolutionId;
        this.problemTesterIds = problemTesterIds;
    }

    public int getStatus() {
        return status;
    }

    public int getPrimarySolutionId() {
        return primarySolutionId;
    }

    public ArrayList getProblemTesterIds() {
        return problemTesterIds;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeInt(status);
        writer.writeInt(primarySolutionId);
        writer.writeArrayList(problemTesterIds);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        status = reader.readInt();
        primarySolutionId = reader.readInt();
        problemTesterIds = reader.readArrayList();
    }
}

