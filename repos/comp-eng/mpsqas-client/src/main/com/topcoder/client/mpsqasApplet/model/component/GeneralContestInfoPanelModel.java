package com.topcoder.client.mpsqasApplet.model.component;

import com.topcoder.netCommon.mpsqas.ContestInformation;

/**
 * Interface for the model for the GeneralContestInfoPanel.
 *
 * @author mitalub
 */
public abstract class GeneralContestInfoPanelModel extends ComponentModel {

    public abstract void setContestInformation(ContestInformation contestInfo);

    public abstract ContestInformation getContestInformation();
}
