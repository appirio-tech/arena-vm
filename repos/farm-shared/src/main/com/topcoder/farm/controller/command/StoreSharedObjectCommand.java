/*
 * StoreSharedObjectCommand
 * 
 * Created 09/18/2006
 */
package com.topcoder.farm.controller.command;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.controller.api.ClientControllerNode;
import com.topcoder.farm.controller.exception.DuplicatedIdentifierException;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class StoreSharedObjectCommand extends AbstractControllerClientCommand {
    private String objectKey;
    private Object object;

    public StoreSharedObjectCommand() {
    }

    public StoreSharedObjectCommand(String id, String objectKey, Object object) {
        super(id);
        this.objectKey = objectKey;
        this.object = object;
    }

    protected Object bareExecute(ClientControllerNode controller) throws DuplicatedIdentifierException {
        controller.storeSharedObject(getId(), objectKey, object);
        return null;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        super.customReadObject(cs);
        objectKey = cs.readString();
        object = cs.readObject();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        super.customWriteObject(cs);
        cs.writeString(objectKey);
        cs.writeObject(object);
    }
}
