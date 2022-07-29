/*
 * Request
 * 
 * Created Nov 6, 2007
 */
package com.topcoder.shared.messagebus.invoker;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Map;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class Request implements Serializable, CustomSerializable {
    /**
     * Waits for execution and includes result in
     * the response
     */
    public static final int REQUEST_GET_RESULT = 0;
    /**
     * Sends response as soon as the request is received,
     * discard result.
     */
    public static final int REQUEST_ACK_RECEIVED = 1;
    
    /**
     * Waits for execution but no result is  included in the response.
     */
    public static final int REQUEST_DROP_RESULT = 2;
    
    /**
     * Just send a request
     */
    public static final int REQUEST_ASYNC = 3;
    
    private int requestType;
    private String namespace;
    private String actionName;
    private Map namedArguments;
    
    public Request() {
    }
    
    public Request(int requestType, String namespace, String actionName, Map namedArguments) {
        this.requestType = requestType;
        this.namespace = namespace;
        this.actionName = actionName;
        this.namedArguments = namedArguments;
    }

    public int getRequestType() {
        return requestType;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getActionName() {
        return actionName;
    }

    public Map getNamedArguments() {
        return namedArguments;
    }
    
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        this.requestType = reader.readInt();
        this.namespace = reader.readString();
        this.actionName = reader.readString();
        this.namedArguments = reader.readHashMap();
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(requestType);
        writer.writeString(namespace);
        writer.writeString(actionName);
        writer.writeMap(namedArguments);
    }
}