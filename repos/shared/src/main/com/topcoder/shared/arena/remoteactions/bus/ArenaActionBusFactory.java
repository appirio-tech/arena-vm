/*
 * ArenaActionBusFactory
 * 
 * Created Nov 6, 2007
 */
package com.topcoder.shared.arena.remoteactions.bus;

import com.topcoder.shared.arena.remoteactions.ArenaActionFactory;
import com.topcoder.shared.arena.remoteactions.ArenaActionListenerException;
import com.topcoder.shared.arena.remoteactions.ArenaActionRequestListener;
import com.topcoder.shared.arena.remoteactions.ArenaActionRequester;
import com.topcoder.shared.arena.remoteactions.ArenaActionRequesterException;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ArenaActionBusFactory extends ArenaActionFactory {
    public ArenaActionRequestListener createListener(String moduleName) throws ArenaActionListenerException {
        return new ArenaActionRequestListenerImpl(moduleName);
    }

    public ArenaActionRequester createRequester(String moduleName) throws ArenaActionRequesterException {
        return new ArenaActionRequesterImpl(moduleName);
    }
}
