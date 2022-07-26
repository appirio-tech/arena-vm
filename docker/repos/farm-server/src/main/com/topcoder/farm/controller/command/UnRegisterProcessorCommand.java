/*
 * UnRegisterProcessorCommand
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.controller.command;

import com.topcoder.farm.controller.api.ProcessorControllerNode;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class UnRegisterProcessorCommand extends AbstractControllerProcessorCommand {
    
    public UnRegisterProcessorCommand() {
    }
    
    public UnRegisterProcessorCommand(String id) {
        super(id);
    }
    
    public Object bareExecute(ProcessorControllerNode node) throws Exception {
        node.unregisterProcessor(getId());
        return null;
    }
}
