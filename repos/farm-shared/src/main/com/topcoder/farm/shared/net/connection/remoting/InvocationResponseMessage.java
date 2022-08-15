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
public class InvocationResponseMessage implements Serializable, CustomSerializable {
    private int id;
    private Object responseObject;
    
    public InvocationResponseMessage() {
        
    }
    
    public InvocationResponseMessage(int id, Object responseObject) {
        this.id = id;
        this.responseObject = responseObject;
    }

    public int getId() {
        return id;
    }

    public Object getResponseObject() {
        return responseObject;
    }
    
    public String toString() {
        return "["+id+","+responseObject+"]";
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        id = cs.readInt();
        responseObject = cs.readObject();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        cs.writeInt(id);
        cs.writeObject(responseObject);
    }
}
