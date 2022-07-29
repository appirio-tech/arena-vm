/*
 * AbstractControllerClientCommand
 * 
 * Created 07/18/2006
 */
package com.topcoder.farm.client.command;

import com.topcoder.farm.client.node.ClientNodeCallback;
import com.topcoder.farm.satellite.SatelliteNodeCallback;
import com.topcoder.farm.satellite.command.AbstractSatelliteCommand;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public abstract class AbstractClientCommand extends AbstractSatelliteCommand {

    protected Object bareExecute(SatelliteNodeCallback client) throws Exception {
        return bareExecuteClient((ClientNodeCallback) client);
    }

    protected abstract Object bareExecuteClient(ClientNodeCallback node) throws Exception ;
}
