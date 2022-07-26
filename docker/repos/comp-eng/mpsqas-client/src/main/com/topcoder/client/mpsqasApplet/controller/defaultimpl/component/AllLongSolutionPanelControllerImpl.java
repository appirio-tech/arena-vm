/*
 * AllLongSolutionPanelControllerImpl
 * 
 * Created 28/04/2006
 */
package com.topcoder.client.mpsqasApplet.controller.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.netCommon.mpsqas.MessageConstants;

/**
 * Implementation of AllSolutionPanelController that extends  AllSolutionPanelControllerImpl
 * overwriting necessary methods
 * 
 * @author Diego Belfer (mural)
 * @version $Id: AllLongSolutionPanelControllerImpl.java 45114 2006-05-10 16:30:47Z thefaxman $
 */
public class AllLongSolutionPanelControllerImpl
        extends AllSolutionPanelControllerImpl {

    public AllLongSolutionPanelControllerImpl() {
    }
    
    /**
     * Runs system tests for all solutions
     */
    public void processSystemTestAll() {
        MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                "System testing all...", false);
        MainObjectFactory.getSolutionRequestProcessor().systemTest(MessageConstants.TEST_ALL);
    }

}
