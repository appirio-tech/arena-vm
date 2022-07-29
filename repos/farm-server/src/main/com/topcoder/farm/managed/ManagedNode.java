/*
 * ManagedNode
 * 
 * Created 08/18/2006
 */
package com.topcoder.farm.managed;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface ManagedNode {

    /**
     * Request the element to shutdown gently
     */
    public void shutdown();
}
