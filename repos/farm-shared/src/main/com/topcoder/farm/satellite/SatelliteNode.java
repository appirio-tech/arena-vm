/*
 * SatelliteNode
 * 
 * Created 07/18/2006
 */
package com.topcoder.farm.satellite;

import com.topcoder.farm.node.FarmNode;


/**
 * A SatelliteNode is an entity which connects to a controller node
 * of the farm, makes requests to it, and receives notifications from the controller.
 * 
 * A satellite must register with the controller to interact with it. Registration
 * is required because controller need to keep track of all nodes interacting with it
 * to be able to inform nodes about shutdown, updates, notications. 
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface SatelliteNode extends FarmNode {
}
