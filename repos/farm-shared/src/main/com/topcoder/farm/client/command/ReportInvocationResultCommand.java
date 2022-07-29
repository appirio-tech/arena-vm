/*
 * ReportInvocationResultCommand
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.client.command;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.client.node.ClientNodeCallback;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ReportInvocationResultCommand extends AbstractClientCommand {
    private InvocationResponse response;
    
    public ReportInvocationResultCommand() {
    }

    public ReportInvocationResultCommand(InvocationResponse response) {
        this.response = response;
    }

    protected Object bareExecuteClient(ClientNodeCallback client) throws Exception {
        client.reportInvocationResult(getResponse());
        return null;
    }

    public InvocationResponse getResponse() {
        return response;
    }

    public void setResponse(InvocationResponse response) {
        this.response = response;
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        super.customReadObject(cs);
        response = (InvocationResponse) cs.readObject();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        super.customWriteObject(cs);
        cs.writeObject(response);
    }
}
