/*
 * Copyright (c) TopCoder
 *
 * Created on Jul 16, 2011
 */
package com.topcoder.farm.controller.dao;

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
public class InvocationHeader implements Serializable, CustomSerializable {
    private static final long serialVersionUID = 1L;
    private String clientId;
    private String requestId;
    private Object attachment;
    
    public InvocationHeader() {
    }
    
    public InvocationHeader(String clientId, String requestId, Object attachment) {
        this.clientId = clientId;
        this.requestId = requestId;
        this.attachment = attachment;
    }

    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
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
    
    @Override
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        clientId = reader.readString();
        requestId = reader.readString();
        attachment = reader.readObject();
    }
    
    @Override
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(clientId);
        writer.writeString(requestId);
        writer.writeObject(attachment);
    }
}
