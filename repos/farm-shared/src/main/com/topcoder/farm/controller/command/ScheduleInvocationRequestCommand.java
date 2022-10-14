/*
 * ScheduleInvocationRequestCommand
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.controller.command;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.controller.api.ClientControllerNode;
import com.topcoder.farm.controller.api.InvocationRequest;
import com.topcoder.farm.controller.exception.DuplicatedIdentifierException;
import com.topcoder.farm.controller.exception.InvalidRequirementsException;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ScheduleInvocationRequestCommand extends AbstractControllerClientCommand {
    private InvocationRequest request;

    public ScheduleInvocationRequestCommand() {
    }
    
    public ScheduleInvocationRequestCommand(String id, InvocationRequest request) {
        super(id);
        this.request = request;
    }

    protected Object bareExecute(ClientControllerNode controller) throws InvalidRequirementsException, DuplicatedIdentifierException {
        controller.scheduleInvocation(getId(), request);
        return null;
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        super.customReadObject(cs);
        request = (InvocationRequest) cs.readObject();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        super.customWriteObject(cs);
        cs.writeObject(request);
    }
}
