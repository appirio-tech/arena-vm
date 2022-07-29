/*
 * Listener
 * 
 * Created Jul 17, 2008
 */
package com.topcoder.shared.serviceevent;

import java.io.Serializable;

/**
 * @author Diego Belfer (Mural)
 * @version $Id$
 */
public interface ServiceEventHandler {
    public void eventReceived(String eventType, Serializable object);
}