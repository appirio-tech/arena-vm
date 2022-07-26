/*
 * ShutdownCommand
 * 
 * Created 08/04/2006
 */
package com.topcoder.farm.managed.command;

import com.topcoder.farm.managed.ManagedNode;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ShutdownCommand extends AbstractManagedCommand {
    public ShutdownCommand() {
    }
    
    protected Object bareExecute(ManagedNode element) throws Exception {
        element.shutdown();
        return null;
    }
}
