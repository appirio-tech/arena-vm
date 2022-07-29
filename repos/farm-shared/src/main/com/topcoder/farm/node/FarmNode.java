/*
 * FarmNode
 * 
 * Created 08/31/2006
 */
package com.topcoder.farm.node;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface FarmNode {
    
    /**
     * Returns the node id.
     */
    String getId();
    
    
    /**
     * Releases the node, making the node to release all resources
     * taken. 
     */
    void releaseNode();
}
