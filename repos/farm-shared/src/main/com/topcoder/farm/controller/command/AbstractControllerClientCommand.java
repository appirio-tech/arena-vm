/*
 * AbstractControllerClientCommand
 * 
 * Created 07/18/2006
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
public abstract class AbstractControllerClientCommand implements ClientControllerCommand {
    private String id;
    
    public AbstractControllerClientCommand() {
    }

    public AbstractControllerClientCommand(String id) {
        this.id = id;
    }

    protected abstract Object bareExecute(ClientControllerNode controller) throws Exception;
    
    public Object execute(Object controller) throws Exception {
        return bareExecute((ClientControllerNode) controller);
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
