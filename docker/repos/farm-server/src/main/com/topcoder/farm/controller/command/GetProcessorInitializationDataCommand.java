/*
 * GetInitializationDataCommand
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.controller.command;

import com.topcoder.farm.controller.api.ProcessorControllerNode;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class GetProcessorInitializationDataCommand extends AbstractControllerProcessorCommand {

    public GetProcessorInitializationDataCommand() {
    }
    
    public GetProcessorInitializationDataCommand(String id) {
        super(id);
    }

    protected Object bareExecute(ProcessorControllerNode controller) throws Exception {
        return controller.getProcessorInitializationData(getId());
    }
}
