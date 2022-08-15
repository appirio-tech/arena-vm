/*
 * ReportInvocationResultCommand
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.controller.command;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.controller.api.ProcessorControllerNode;
import com.topcoder.farm.processor.api.ProcessorInvocationResponse;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ReportInvocationResultCommand extends AbstractControllerProcessorCommand {
    private ProcessorInvocationResponse response;

    public ReportInvocationResultCommand() {
    }
    
    public ReportInvocationResultCommand(String id, ProcessorInvocationResponse response) {
        super(id);
        this.response = response;
    }

    protected Object bareExecute(ProcessorControllerNode controller) throws Exception {
        controller.reportInvocationResult(getId(), response);
        return null;
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        super.customReadObject(cs);
        response = (ProcessorInvocationResponse) cs.readObject();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        super.customWriteObject(cs);
        cs.writeObject(response);
    }
}
