/*
 * ProcessInvocationRequestCommand
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.processor.command;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.processor.api.ProcessorInvocationRequest;
import com.topcoder.farm.processor.api.ProcessorNodeCallback;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ProcessInvocationRequestCommand extends AbstractProcessorCommand {
    private ProcessorInvocationRequest request;

    public ProcessInvocationRequestCommand() {
    }
    
    public ProcessInvocationRequestCommand(ProcessorInvocationRequest request) {
        this.request = request;
    }

    protected Object bareExecuteProcessor(ProcessorNodeCallback processor) throws Exception {
        processor.processInvocationRequest(request);
        return null;
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        super.customReadObject(cs);
        request = (ProcessorInvocationRequest) cs.readObject();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        super.customWriteObject(cs);
        cs.writeObject(request);
    }
}
