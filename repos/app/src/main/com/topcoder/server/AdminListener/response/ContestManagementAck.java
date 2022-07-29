/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:11:57 AM
 */
package com.topcoder.server.AdminListener.response;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.*;

public abstract class ContestManagementAck implements CustomSerializable, Serializable {

    private boolean success;
    private Throwable exception;
    private String message;

    public ContestManagementAck() {
        this.success = true;
    }

    public ContestManagementAck(Throwable exception) {
        this.success = false;
        this.exception = exception;
    }

    public ContestManagementAck(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ContestManagementAck(boolean success, String message, Throwable exception) {
        this.success = success;
        this.message = message;
        this.exception = exception;
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeBoolean(success);
        writer.writeString(message);
        
        //write out the exception.  Darned serialization
        if(exception == null)
            writer.writeBoolean(false);
        else {
            writer.writeBoolean(true);
            writer.writeString(exception.getMessage());
        }

        //this will work in 1.5
        /*writer.writeInt(exception.getStackTrace().length);
        for(int i = 0; i < exception.getStackTrace().length; i++) {
            writer.writeString(exception.getStackTrace()[i].getClassName());
            writer.writeString(exception.getStackTrace()[i].getMethodName());
            writer.writeString(exception.getStackTrace()[i].getFileName());
            writer.writeInt(exception.getStackTrace()[i].getLineNumber());
        }*/
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        success = reader.readBoolean();
        message = reader.readString();
        
        if(reader.readBoolean())
            exception = new Exception(reader.readString());
        /*int sz = reader.readInt();
        StackTraceElement[] arr = new StackTraceElement[sz];
        for(int i = 0; i < sz; i++) {
            arr[i] = new StackTraceElement(reader.readString(), reader.readString(), reader.readString(), reader.readInt());
        }
        exception.setStackTrace(arr);*/
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean hasException() {
        return exception != null;
    }

    public Throwable getException() {
        return exception;
    }

    public boolean hasMessage() {
        return message != null;
    }

    public String getMessage() {
        return message;
    }
}
