/*
 * GetEnqueuedRequestsCommand
 * 
 * Created 09/20/2006
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
public class GetEnqueuedRequestsCommand extends AbstractControllerClientCommand {
    private String prefix = null;

    public GetEnqueuedRequestsCommand() {
        super();
    }

    public GetEnqueuedRequestsCommand(String id, String prefix) {
        super(id);
        this.prefix = prefix;
    }

    protected Object bareExecute(ClientControllerNode controller) {
        return controller.getEnqueuedRequests(getId(), prefix);
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        super.customReadObject(cs);
        prefix = cs.readString();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        super.customWriteObject(cs);
        cs.writeString(prefix);
    }
}
