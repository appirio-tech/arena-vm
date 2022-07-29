/*
 * ArenaProcessor
 * 
 * Created May 28, 2008
 */
package com.topcoder.server.listener;

import com.topcoder.server.listener.monitor.ArenaMonitor;

/**
 * @autor Diego Belfer (Mural)
 * @version $Id: ArenaProcessor.java 71140 2008-06-10 05:24:27Z dbelfer $
 */
public interface ArenaProcessor extends ProcessorInterface {
    public void setArenaMonitor(ArenaMonitor monitor);
}
