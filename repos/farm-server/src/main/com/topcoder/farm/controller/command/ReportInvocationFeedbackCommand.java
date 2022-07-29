/*
 * ReportInvocationResultCommand
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.controller.command;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.controller.api.ProcessorControllerNode;
import com.topcoder.farm.processor.api.ProcessorInvocationFeedback;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * @author Diego Belfer (mural)
 * @version $Id: ReportInvocationResultCommand.java 52649 2006-10-05 21:50:50Z mural $
 */
public class ReportInvocationFeedbackCommand extends AbstractControllerProcessorCommand {
    private static final long serialVersionUID = 1L;
    private ProcessorInvocationFeedback feedback;

    public ReportInvocationFeedbackCommand() {
    }
    
    public ReportInvocationFeedbackCommand(String id, ProcessorInvocationFeedback feedback) {
        super(id);
        this.feedback = feedback;
    }

    protected Object bareExecute(ProcessorControllerNode controller) throws Exception {
        controller.reportInvocationFeedback(getId(), feedback);
        return null;
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        super.customReadObject(cs);
        feedback = (ProcessorInvocationFeedback) cs.readObject();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        super.customWriteObject(cs);
        cs.writeObject(feedback);
    }
}
