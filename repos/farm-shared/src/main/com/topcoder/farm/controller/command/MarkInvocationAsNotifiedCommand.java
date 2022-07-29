/*
 * MarkInvocationAsNotifiedCommand
 *
 * Created 11/01/2006
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
public class MarkInvocationAsNotifiedCommand extends AbstractControllerClientCommand {
    private String requestId;

    public MarkInvocationAsNotifiedCommand() {
    }

    public MarkInvocationAsNotifiedCommand(String id, String requestId) {
        super(id);
        this.requestId = requestId;
    }

    protected Object bareExecute(ClientControllerNode controller) {
        controller.markInvocationAsNotified(getId(), requestId);
        return null;
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        super.customReadObject(cs);
        requestId = cs.readString();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        super.customWriteObject(cs);
        cs.writeString(requestId);
    }
}
