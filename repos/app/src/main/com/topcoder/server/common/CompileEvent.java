package com.topcoder.server.common;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

public class CompileEvent extends TCEvent {

    private Submission m_submission;

    public CompileEvent(Submission submission) {
        super(COMPILE_TYPE, USER_TARGET, submission.getCoderID());
        m_submission = submission;
    }

    public CompileEvent() {
    }

    public Submission getSubmission() {
        return m_submission;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObject(m_submission);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        m_submission = (Submission) reader.readObject();
    }

}
