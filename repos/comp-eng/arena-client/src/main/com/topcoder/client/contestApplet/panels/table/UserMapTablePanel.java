/*
 * UserMapTablePanel.java
 *
 * Created on June 30, 2002, 5:22 PM
 */

package com.topcoder.client.contestApplet.panels.table;

import java.util.*;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.netCommon.contestantMessages.response.data.*;

/**
 *
 * @author  Matthew P. Suhocki (msuhocki)
 */
public class UserMapTablePanel extends UserTablePanel {

    private HashMap teamMap = null;
    private String currentTeam = null;

    /** Creates a new instance of UserMapTablePanel */
    public UserMapTablePanel(ContestApplet ca, String title) {
        super(ca, title);
        teamMap = new HashMap();
    }

    public void addToUserList(UserListItem item) {
        ArrayList team = (ArrayList) teamMap.get(item.getTeamName());

        if (team == null) {
            team = new ArrayList();
            teamMap.put(item.getTeamName(), team);
        }

        throw new UnsupportedOperationException("TODO - FIX THIS");
//        if (item.getTeamName().equals(currentTeam))
//            super.addToUserList(item);
//
//        for (int i=0; i<team.size(); i++) {
//            if (((UserListItem)team.get(i)).getUserName().equals(item.getUserName())) {
//                team.set(i, item);
//                return;
//            }
//        }
//
//        team.add(item);
    }

    public void addToUserList(UserListItem[] items) {
        for (int i = 0; i < items.length; i++) {
            addToUserList(items[i]);
        }
    }

    public void removeFromUserList(UserListItem item) {
        throw new UnsupportedOperationException("TODO - FIX THIS");
//        ArrayList team = (ArrayList)teamMap.get(item.getTeamName());
//
//        if (team==null) return;
//
//        if (item.getTeamName().equals(currentTeam))
//            super.removeFromUserList(item);
//
//        for (int i=0; i<team.size(); i++) {
//            if (((UserListItem)team.get(i)).getUserName().equals(item.getUserName())) {
//                team.remove(i);
//            }
//        }
    }

    public void setTeam(String newTeam) {
        ArrayList team = (ArrayList) teamMap.get(newTeam);

        if (newTeam.equals(currentTeam))
            return;
        else
            currentTeam = newTeam;

        clear();

        if (team == null) return;

        super.updateUserList((UserListItem[]) team.toArray(new UserListItem[0]));
    }


    /**
     * Removes the selected user from this UTP and adds it to another UTP.
     *
     * @author Matthew P. Suhocki
     *
     * @param utp     User table panel to which to move selected user
     */
    // hopefully temporary
    public void moveSelectedToUTP(UserMapTablePanel utp) {
        int index = getTable().getSelectedRow();
        if (0 > index || index >= contestTable.getRowCount()) return;
        UserListItem user = (UserListItem) getTableModel().get(index);
        ArrayList team = (ArrayList) teamMap.get(currentTeam);
        String sname = user.getUserName();
        if (team == null) return;
        for (int i = 0; i < team.size(); i++) {
            UserListItem item = (UserListItem) team.get(i);
            if (item.getUserName().equals(sname)) {
                utp.addToUserList((UserListItem) team.get(i));
                removeFromUserList((UserListItem) team.get(i));
            }
        }
        //removeFromUserList(new UserListItem(((UserNameEntry)row.get(1)).getName(), 0, currentTeam));
        //utp.getTableModel().addRow(row);
    }

}
