/*
 * GetInitializationDataCommand
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.controller.command;

import com.topcoder.farm.controller.api.ClientControllerNode;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class GetInitializationDataCommand extends AbstractControllerClientCommand {

    public GetInitializationDataCommand() {
    }
    
    public GetInitializationDataCommand(String id) {
        super(id);
    }

    protected Object bareExecute(ClientControllerNode controller) throws Exception {
        return controller.getClientInitializationData(getId());
    }
}
