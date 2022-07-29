/*
* Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * RegisterProcessorCommand
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.controller.command;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.controller.api.ProcessorControllerNode;
import com.topcoder.farm.controller.exception.NotAllowedToRegisterException;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
 * <ol>
 *      <li>Update {@link #bareExecute(ProcessorControllerNode controller)} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.0
 */
public class RegisterProcessorCommand extends AbstractControllerProcessorCommand {
    private int currentLoad;

    public RegisterProcessorCommand() {
    }
    
    public RegisterProcessorCommand(String id, int currentLoad) {
        super(id);
        this.currentLoad = currentLoad;
    }

    /**
     * <p>
     * execute the current register command
     * </p>
     * @param controller the controller node.
     * @throws NotAllowedToRegisterException if the controller rejects registration for the processor
     */
    protected Object bareExecute(ProcessorControllerNode controller) throws NotAllowedToRegisterException {
        String processorId = null;
        if (currentLoad < 0) {
            processorId = controller.registerProcessor(getId(), null);
        } else {
            processorId = controller.reRegisterProcessor(getId(), null, currentLoad);
        }
        return processorId;
    }

    public int getCurrentLoad() {
        return currentLoad;
    }

    public void setCurrentLoad(int currentLoad) {
        this.currentLoad = currentLoad;
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        super.customReadObject(cs);
        currentLoad = cs.readInt();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        super.customWriteObject(cs);
        cs.writeInt(currentLoad);
    }
}
