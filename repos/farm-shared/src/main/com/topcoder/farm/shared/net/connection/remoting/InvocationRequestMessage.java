/*
 * InvocationRequestMessage
 * 
 * Created 07/18/2006
 */
package com.topcoder.farm.shared.net.connection.remoting;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InvocationRequestMessage implements Serializable, CustomSerializable {
    
    /**
     * A message of type SYNC is expected to be processed and 
     * the response to the message be sent back. 
     */
    public static final int TYPE_SYNC = 1;
    
    /**
     * For a message of type ASYNC the sender does not expect any response.
     * It should be processed and the result if any discared  
     */
    public static final int TYPE_ASYNC = 2;
    
    /**
     * A sender sending a message of type ACK will wait for a response message
     * without result that the receiver must send as soon as it picked up the message
     * It allows to ensure the message was not lost. 
     */
    public static final int TYPE_ACK = 4;
    private int id;
    private Object requestObject;
    private int sync;
    
    public InvocationRequestMessage() {
    }
    
    public InvocationRequestMessage(int id, Object requestObject, int sync) {
        this.id = id;
        this.requestObject = requestObject;
        this.sync = sync;
    }

    public int getId() {
        return id;
    }

    public Object getRequestObject() {
        return requestObject;
    }

    public boolean isSync() {
        return sync == TYPE_SYNC;
    }
    
    public boolean requiresAck() {
        return sync == TYPE_ACK;
    }
    
    public String toString() {
        return "InvocationRequestMessage[id="+id+", sync="+sync+", requestObject="+requestObject+"]";
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        id = cs.readInt();
        sync = cs.readInt();
        requestObject = cs.readObject();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        cs.writeInt(id);
        cs.writeInt(sync);
        cs.writeObject(requestObject);
    }
}
