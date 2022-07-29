/*
 * RemoveSharedObjectsCommand
 * 
 * Created 09/19/2006
 */
package com.topcoder.farm.controller.command;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.controller.api.ClientControllerNode;
import com.topcoder.farm.controller.exception.SharedObjectReferencedException;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class RemoveSharedObjectsCommand extends AbstractControllerClientCommand {
    private String objectKeyPrefixToMatch;

    public RemoveSharedObjectsCommand() {
    }

    public RemoveSharedObjectsCommand(String id, String objectKeyPrefixToMatch) {
        super(id);
        this.objectKeyPrefixToMatch = objectKeyPrefixToMatch;
    }

    protected Object bareExecute(ClientControllerNode controller) throws SharedObjectReferencedException  {
        controller.removeSharedObjects(getId(), objectKeyPrefixToMatch);
        return null;
    }

    public String getObjectKeyPrefixToMatch() {
        return objectKeyPrefixToMatch;
    }

    public void setObjectKeyPrefixToMatch(String objectKey) {
        this.objectKeyPrefixToMatch = objectKey;
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        super.customReadObject(cs);
        objectKeyPrefixToMatch = cs.readString();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        super.customWriteObject(cs);
        cs.writeString(objectKeyPrefixToMatch);
    }
}
