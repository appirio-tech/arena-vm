/*
 * RegisterClientCommand
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.controller.command;

import com.topcoder.farm.controller.api.ClientControllerNode;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class UnRegisterClientCommand extends AbstractControllerClientCommand {
    
    public UnRegisterClientCommand() {
    }
    
    public UnRegisterClientCommand(String id) {
        super(id);
    }
    
    public Object bareExecute(ClientControllerNode node) throws Exception {
        node.unregisterClient(getId());
        return null;
    }
}
