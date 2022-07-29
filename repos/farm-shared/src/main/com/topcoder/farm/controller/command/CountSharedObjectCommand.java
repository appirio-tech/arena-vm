/*
 * CountSharedObjectCommand
 * 
 * Created 09/19/2006
 */
package com.topcoder.farm.controller.command;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.controller.api.ClientControllerNode;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class CountSharedObjectCommand extends AbstractControllerClientCommand {
    private String objectKeyPrefix;

    public CountSharedObjectCommand() {
    }

    public CountSharedObjectCommand(String id, String objectKeyPrefix) {
        super(id);
        this.objectKeyPrefix = objectKeyPrefix;
    }

    protected Object bareExecute(ClientControllerNode controller) {
        return controller.countSharedObjects(getId(), objectKeyPrefix);
    }

    public String getobjectKeyPrefix() {
        return objectKeyPrefix;
    }

    public void setobjectKeyPrefix(String objectKeyPrefix) {
        this.objectKeyPrefix = objectKeyPrefix;
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        super.customReadObject(cs);
        objectKeyPrefix = cs.readString();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        super.customWriteObject(cs);
        cs.writeString(objectKeyPrefix);
    }
}
