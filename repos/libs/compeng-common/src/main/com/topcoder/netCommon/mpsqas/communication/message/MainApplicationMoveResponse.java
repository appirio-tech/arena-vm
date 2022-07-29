package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;
import java.util.*;

/**
 *
 * @author Logan Hanks
 */
public class MainApplicationMoveResponse
        extends MoveResponse {

    private ArrayList applications;

    public MainApplicationMoveResponse() {
        this(new ArrayList());
    }

    public MainApplicationMoveResponse(ArrayList applications) {
        this.applications = applications;
    }

    public ArrayList getApplications() {
        return applications;
    }

    public void addApplication(ApplicationInformation application) {
        applications.add(application);
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeArrayList(applications);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        applications = reader.readArrayList();
    }
}

