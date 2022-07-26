package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class ViewApplicationMoveResponse
        extends MoveResponse {

    private ApplicationInformation application;

    public ViewApplicationMoveResponse() {
    }

    public ViewApplicationMoveResponse(ApplicationInformation application) {
        this.application = application;
    }

    public ApplicationInformation getApplication() {
        return application;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeObject(application);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        application = (ApplicationInformation) reader.readObject();
    }

    public String toString() {
        return "ViewApplicationMoveResponse[info=" + application + "]";
    }
}

