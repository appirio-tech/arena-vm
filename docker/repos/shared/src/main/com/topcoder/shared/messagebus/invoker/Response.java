/*
 * Response
 * 
 * Created Oct 26, 2007
 */
package com.topcoder.shared.messagebus.invoker;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * @author Diego Belfer (Mural)
 * @version $Id$
 */
public class Response implements Serializable, CustomSerializable {
    public static final int SUCCESSFUL = 0;
    public static final int ACK = 1;
    public static final int TARGET_EXCEPTION = 2;

    private int responseType;
    private Object result;
    
    public Response() {
    }
    
    public Response(int responseType, Object result) {
        this.responseType = responseType;
        this.result = result;
    }

    public Object getResult() {
        return result;
    }

    public boolean isSucceeded() {
        return responseType == SUCCESSFUL || responseType == ACK;
    }

    public boolean isException() {
        return responseType == TARGET_EXCEPTION;
    }

    public int getResponseType() {
        return responseType;
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        this.responseType = reader.readInt();
        this.result = reader.readObject();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(responseType);
        writer.writeObject(result);
    }
}