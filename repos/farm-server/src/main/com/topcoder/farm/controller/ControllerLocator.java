/*
 * ControllerLocator
 * 
 * Created 07/06/2006
 */
package com.topcoder.farm.controller;

import com.topcoder.farm.controller.api.ControllerNode;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ControllerLocator {
    private static ControllerNode controller;

    public static void setLocalController(ControllerNode controller) {
        ControllerLocator.controller = controller; 
    }
    
    public static ControllerNode getController() {
        return ControllerLocator.controller;
    }
}
