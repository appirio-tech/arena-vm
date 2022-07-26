/*
 * ControllerNode
 * 
 * Created 08/31/2006
 */
package com.topcoder.farm.controller.api;

import com.topcoder.farm.managed.LocalManagedNode;
import com.topcoder.farm.node.FarmNode;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface ControllerNode extends ProcessorControllerNode, 
        ClientControllerNode, AdminControllerNode, FarmNode, 
        LocalManagedNode {

}
