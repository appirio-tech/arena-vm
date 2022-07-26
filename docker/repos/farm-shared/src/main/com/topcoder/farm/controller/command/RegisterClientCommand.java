/*
 * RegisterClientCommand
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.controller.command;

import com.topcoder.farm.controller.api.ClientControllerNode;
import com.topcoder.farm.controller.exception.NotAllowedToRegisterException;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class RegisterClientCommand extends AbstractControllerClientCommand {
    public RegisterClientCommand() {
    }
    
    public RegisterClientCommand(String id) {
        super(id);
    }

    protected Object bareExecute(ClientControllerNode controller) throws NotAllowedToRegisterException {
        controller.registerClient(getId(), null);
        return null;
    }
}
