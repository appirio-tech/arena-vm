/*
 * ExceptionData
 * 
 * Created Nov 6, 2007
 */
package com.topcoder.shared.messagebus.invoker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.PrintWriter;
import java.io.Serializable;

import com.topcoder.shared.exception.LocalizableException;
import com.topcoder.shared.i18n.Message;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ExceptionData implements CustomSerializable, Serializable {
    private Message localizableMessage;
    private String  detailMessage; 
    private String  exceptionClassName;
    private String  stackTraceDump;
    
    public ExceptionData() {
    }
    
    public ExceptionData(Message localizableMessage, String detailMessage, String exceptionClassName, String stackTraceDump) {
        this.localizableMessage = localizableMessage;
        this.detailMessage = detailMessage;
        this.exceptionClassName = exceptionClassName;
        this.stackTraceDump = stackTraceDump;
    }

    public Message getLocalizableMessage() {
        return localizableMessage;
    }

    public String getExceptionClassName() {
        return exceptionClassName;
    }

    public String getStackTraceDump() {
        return stackTraceDump;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        localizableMessage = (Message) reader.readObject();
        detailMessage = reader.readString();
        exceptionClassName = reader.readString();
        stackTraceDump = reader.readString();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(localizableMessage);
        writer.writeString(detailMessage);
        writer.writeString(exceptionClassName);
        writer.writeString(stackTraceDump);
    }

    public static Object buildFrom(Exception e) {
        String stacktrace = buildStactTraceString(e);
        Message message = null;
        if (e instanceof LocalizableException) {
            message = ((LocalizableException) e).getLocalizableMessage();
        }
        return new ExceptionData(message, e.getMessage(), e.getClass().getName(), stacktrace);
    }
    
    protected static String buildStactTraceString(Exception e) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(bs);
        e.printStackTrace(pw);
        pw.close();
        return new String(bs.toByteArray());
    }

}
