/*
 * RequestPendingResponsesCommand
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.controller.command;

import com.topcoder.farm.controller.api.ClientControllerNode;
import com.topcoder.farm.controller.exception.ClientNotListeningException;
import com.topcoder.farm.controller.exception.UnregisteredClientException;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class RequestPendingResponsesCommand extends AbstractControllerClientCommand {

    public RequestPendingResponsesCommand() {
    }

    public RequestPendingResponsesCommand(String id) {
        super(id);
    }

    protected Object bareExecute(ClientControllerNode controller) throws UnregisteredClientException, ClientNotListeningException {
        controller.deliverPendingResponses(getId());
        return null;
    }
}
