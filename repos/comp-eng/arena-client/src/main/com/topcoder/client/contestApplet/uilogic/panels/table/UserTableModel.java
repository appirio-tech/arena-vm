/*
* User: Michael Cervantes
* Date: Aug 16, 2002
* Time: 6:00:33 PM
*/
package com.topcoder.client.contestApplet.uilogic.panels.table;

import com.topcoder.netCommon.contestantMessages.response.data.*;
import com.topcoder.client.contestant.*;
import com.topcoder.client.SortedTableModel;
import com.topcoder.client.SortElement;

import java.util.*;

public class UserTableModel extends SortedTableModel {

    protected final Contestant contestantModel;

    public UserTableModel(Contestant contestantModel, String[] headers) {
        super(headers, new Class[]{
            Integer.class,
            String.class,
            String.class
        });
        addSortElement(new SortElement(0, true));
        addSortElement(new SortElement(1, false));
        addSortElement(new SortElement(2, false));
        addSortElement(new SortElement(3, false));
        this.contestantModel = contestantModel;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        UserListItem user = getUser(rowIndex);
        switch (columnIndex) {
        case 0:
            return new Integer(user.getUserRating());
        case 1:
            return new UserNameEntry(user.getUserName(), user.getUserRating(), isLeader(user.getUserName()), user.getUserType());
        case 2:
            return user.getCountryName();
        case 3:
            return user.getTeamName();
        default:
            throw new IllegalArgumentException("Bad column: " + columnIndex);
        }
    }

    public UserListItem getUser(int rowIndex) {
        return (UserListItem) get(rowIndex);
    }

    public int compare(Object o1, Object o2) {
        UserListItem u1 = (UserListItem) o1;
        UserListItem u2 = (UserListItem) o2;

        for (Iterator it = getSortListIterator(); it.hasNext();) {
            SortElement sortElement = (SortElement) it.next();
            int col = sortElement.getColumn();
            int sign = sortElement.isOpposite() ? 1 : -1;
            switch (col) {
            case 0:
            	{
                    int diff = u1.getUserRating() - u2.getUserRating();
                    if (diff != 0) return sign * diff;
                    break;
                }
            case 1:
                {
                    int diff = compareStrings(u1.getUserName(), u2.getUserName());
                    if (diff != 0) return sign * diff;
                    break;
                }
            case 2:
	            {
	                int diff = compareStrings(u1.getCountryName(), u2.getCountryName());
	                if (diff != 0) return sign * diff;
	                break;
	            }
            case 3:
	            {
	                int diff = compareStrings(u1.getTeamName(), u2.getTeamName());
	                if (diff != 0) return sign * diff;
	                break;
	            }
            default:
                throw new IllegalStateException("Bad column: " + col);
            }
        }

        return 0;
    }

    public void add(Object item) {
        super.remove(item);
        super.add(item);
    }

    protected boolean isLeader(String handle) {
        return contestantModel.isRoomLeader(handle);
    }
}
