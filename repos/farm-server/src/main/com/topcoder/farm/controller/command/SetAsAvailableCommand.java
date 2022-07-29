/*
 * SetAsAvailableCommand
 * 
 * Created 08/03/2006
 */
package com.topcoder.farm.controller.command;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.controller.api.ProcessorControllerNode;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class SetAsAvailableCommand extends AbstractControllerProcessorCommand {
    private boolean available;

    public SetAsAvailableCommand() {
    }
    
    public SetAsAvailableCommand(String id, boolean available) {
        super(id);
        this.available = available;
    }

    protected Object bareExecute(ProcessorControllerNode controller) throws Exception {
        controller.setAsAvailable(getId(), available);
        return null;
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        super.customReadObject(cs);
        available = cs.readBoolean();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        super.customWriteObject(cs);
        cs.writeBoolean(available);
    }
}
