/*
 * Copyright (c) TopCoder
 *
 * Created on Jul 14, 2011
 */
package com.topcoder.farm.shared.invocation;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * @author mural
 * @version $Id$
 */
public class InvocationFeedback implements Serializable, CustomSerializable {
    private static final long serialVersionUID = 1L;
    private String requestId;
    private Object attachment;
    private Object feedbackData;
    
    public InvocationFeedback() {
    }
    
    public InvocationFeedback(String requestId, Object attachment, Object feedbackData) {
        this.requestId = requestId;
        this.attachment = attachment;
        this.feedbackData = feedbackData;
    }

    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        this.requestId = cs.readString();
        this.attachment = cs.readObject();
        this.feedbackData = cs.readObject();
    }

    public void customWriteObject(CSWriter cs) throws IOException {
        cs.writeString(this.requestId);
        cs.writeObject(this.attachment);
        cs.writeObject(this.feedbackData);
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Object getAttachment() {
        return attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }

    public Object getFeedbackData() {
        return feedbackData;
    }

    public void setFeedbackData(Object feedbackData) {
        this.feedbackData = feedbackData;
    }
}
