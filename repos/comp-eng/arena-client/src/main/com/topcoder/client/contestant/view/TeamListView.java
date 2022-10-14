/**
 * Created on June 26, 2002
 *
 */
package com.topcoder.client.contestant.view;

import com.topcoder.netCommon.contestantMessages.response.data.TeamListInfo;

/**
 * Defines an interface which is notified when the list of teams is updated.
 *
 * @author  Matthew P. Suhocki (msuhocki)
 * @version $Id: TeamListView.java 72032 2008-07-30 06:28:49Z qliu $
 */
public interface TeamListView {
    /**
     * Called when the list of team is updated. It is used in team rounds.
     * 
     * @param row the list of team.
     */
    void updateTeamList(TeamListInfo row);
}


