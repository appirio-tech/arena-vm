package com.topcoder.server.common;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

public class SubmitEvent extends TCEvent implements CustomSerializable {

    private Submission m_submission;

    public SubmitEvent() {
    }

    public SubmitEvent(Submission submission) {
        super(SUBMIT_TYPE, USER_TARGET, submission.getCoderID());
        m_submission = submission;
    }

    public Submission getSubmission() {
        return m_submission;
    }

    // TODO: implement for real?
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
    }

}
