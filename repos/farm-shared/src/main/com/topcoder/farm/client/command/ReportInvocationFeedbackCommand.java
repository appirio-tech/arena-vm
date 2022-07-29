/*
 * ReportInvocationResultCommand
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.client.command;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.client.node.ClientNodeCallback;
import com.topcoder.farm.shared.invocation.InvocationFeedback;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ReportInvocationFeedbackCommand extends AbstractClientCommand {
    private static final long serialVersionUID = 1L;
    private InvocationFeedback feedback;
    
    public ReportInvocationFeedbackCommand() {
    }

    public ReportInvocationFeedbackCommand(InvocationFeedback feedback) {
        this.feedback = feedback;
    }

    protected Object bareExecuteClient(ClientNodeCallback client) throws Exception {
        client.reportInvocationFeedback(feedback);
        return null;
    }

    public InvocationFeedback getFeedback() {
        return feedback;
    }

    public void setFeedback(InvocationFeedback feedback) {
        this.feedback = feedback;
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        super.customReadObject(cs);
        feedback = (InvocationFeedback) cs.readObject();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        super.customWriteObject(cs);
        cs.writeObject(feedback);
    }
}
