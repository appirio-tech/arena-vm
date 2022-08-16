package com.topcoder.client.contestApplet.panels.table;

import com.topcoder.client.contestApplet.common.CommonData;
import com.topcoder.client.contestApplet.*;

/**
 * Just a CoderContestantTablePanel with different headers
 *
 * @author mitalub
 */
public final class TeamContestantTablePanel extends CoderContestantTablePanel {

    public TeamContestantTablePanel(ContestApplet ca) {
        super(ca, CommonData.teamContestantHeader);
    }
}
