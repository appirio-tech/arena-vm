/*
 * AbstractControllerProcessorCommand
 * 
 * Created 07/18/2006
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
public abstract class AbstractControllerProcessorCommand implements ProcessorControllerCommand {
    private String id;
    
    public AbstractControllerProcessorCommand() {
    }

    public AbstractControllerProcessorCommand(String id) {
        this.id = id;
    }

    protected abstract Object bareExecute(ProcessorControllerNode controller) throws Exception;
    
    public Object execute(Object controller) throws Exception {
        return bareExecute((ProcessorControllerNode) controller);
    }

    protected String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        id = cs.readString();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        cs.writeString(id);
    }
}
