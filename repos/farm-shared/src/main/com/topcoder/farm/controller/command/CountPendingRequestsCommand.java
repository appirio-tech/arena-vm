/*
 * CountPendingRequestsCommand
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
public class CountPendingRequestsCommand extends AbstractControllerClientCommand {
    private String prefix = null;

    public CountPendingRequestsCommand() {
        super();
    }

    public CountPendingRequestsCommand(String id) {
        super(id);
    }
    
    public CountPendingRequestsCommand(String id, String prefix) {
        super(id);
        this.prefix = prefix;
    }

    protected Object bareExecute(ClientControllerNode controller) {
        if (prefix == null) {
            return controller.countPendingRequests(getId());
        } else {
            return controller.countPendingRequests(getId(), prefix);
        }
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
