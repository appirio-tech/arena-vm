package com.topcoder.client.mpsqasApplet.messaging;

import com.topcoder.netCommon.mpsqas.ProblemInformation;
import com.topcoder.netCommon.mpsqas.ComponentInformation;

/**
 * Internal Move RequestProcessor interface.
 *
 * @author mitalub
 */
public interface IMoveRequestProcessor {

    public void loadFoyerRoom();

    public void loadMovingRoom();

    public void loadLoginRoom();
}
