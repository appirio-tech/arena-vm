/*
 * User: Michael Cervantes
 * Date: Aug 16, 2002
 * Time: 1:02:18 AM
 */
package com.topcoder.client.contestApplet.uilogic.panels.table;

import java.util.Collection;
import java.util.Iterator;

import com.topcoder.client.SortElement;
import com.topcoder.client.SortedTableModel;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestant.Contestant;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.view.LeaderListener;
import com.topcoder.netCommon.contestantMessages.response.data.LeaderboardItem;

public class LeaderboardTableModel extends SortedTableModel implements LeaderListener {

    private Contestant model;

    public LeaderboardTableModel(Contestant model) {
        super(new String[]{
            "Room", "R", "User", "Seed", "Score", ""
        }, new Class[]{
            Integer.class,
            Integer.class,
            UserNameEntry.class,
            Integer.class,
            String.class,
            Character.class
        });
        addSortElement(new SortElement(ROOM, false));
        addSortElement(new SortElement(RATING, false));
        addSortElement(new SortElement(HANDLE, false));
        addSortElement(new SortElement(SEED, false));
        addSortElement(new SortElement(SCORE, false));
        addSortElement(new SortElement(CLOSERACE, false));
        this.model = model;
    }


    private static final int ROOM = 0;
    private static final int RATING = 1;
    private static final int HANDLE = 2;
    private static final int SEED = 3;
    private static final int SCORE = 4;
    private static final int CLOSERACE = 5;

    public LeaderboardItem getLeaderboardItem(int row) {
        return (LeaderboardItem) super.get(row);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        LeaderboardItem leaderboardItem = getLeaderboardItem(rowIndex);
        switch (columnIndex) {
        case ROOM:
            RoomModel room = model.getRoom(leaderboardItem.getRoomID());
            if (!room.hasLeader()) {
                return null;
            }
            return room.getRoomNumber();
        case RATING:
            return new Integer(leaderboardItem.getUserRating());
        case HANDLE:
            return new UserNameEntry(leaderboardItem.getUserName(), leaderboardItem.getUserRating(), true, -1);
        case SEED:
            return new Integer(leaderboardItem.getSeed());
        case SCORE:
            return Common.formatScore(leaderboardItem.getPoints() / 100);
        case CLOSERACE:
            return leaderboardItem.isCloseRace() ? "*" : "";
        default:
            throw new IllegalArgumentException("Invalid column: " + columnIndex);
        }
    }


    public int compare(Object o1, Object o2) {
        LeaderboardItem leader1 = (LeaderboardItem) o1;
        LeaderboardItem leader2 = (LeaderboardItem) o2;

        for (Iterator it = getSortListIterator(); it.hasNext();) {
            SortElement sortElem = (SortElement) it.next();
            int col = sortElem.getColumn();
            int sign = sortElem.isOpposite() ? -1 : 1;
            int diff = 0;
            switch (col) {
            case ROOM:
                RoomModel r1 = model.getRoom(leader1.getRoomID());
                RoomModel r2 = model.getRoom(leader2.getRoomID());
                diff = r1.getRoomNumber().intValue() - r2.getRoomNumber().intValue();
                if (diff != 0) {
                    return sign * diff;
                }
                break;
            case RATING:
                diff = leader1.getUserRating() - leader2.getUserRating();
                if (diff != 0) {
                    return sign * diff;
                }
                break;
            case HANDLE:
                diff = compareStrings(leader1.getUserName(), leader2.getUserName());
                if (diff != 0) {
                    return sign * diff;
                }
                break;
            case SEED:
                diff = leader1.getSeed() - leader2.getSeed();
                if (diff != 0) {
                    return sign * diff;
                }
                break;
            case SCORE:
                double d = leader1.getPoints() - leader2.getPoints();
                if (d != 0) {
                    return sign * (d > 0 ? 1 : -1);
                }
                break;
            case CLOSERACE:
                if (leader1.isCloseRace() && !leader2.isCloseRace()) {
                    return -sign;
                }
                if (!leader1.isCloseRace() && leader2.isCloseRace()) {
                    return sign;
                }
                break;
            default:
                throw new IllegalStateException("not implemented, column=" + sortElem);
            }
        }
        return 0;
//        throw new IllegalStateException("problem with sorting, o1="+o1+", o2"+o2);
    }


    public void update(Collection leaderboardItems) {
        clear();
        for (Iterator it = leaderboardItems.iterator(); it.hasNext();) {
            LeaderboardItem item = (LeaderboardItem) it.next();
            RoomModel room = model.getRoom(item.getRoomID());
            room.addLeaderListener(this);
        }
        super.update(leaderboardItems);
    }

    public void clear() {
        for (Iterator it = super.getItemList().iterator(); it.hasNext();) {
            LeaderboardItem item = (LeaderboardItem) it.next();
            try {
            	RoomModel room = model.getRoom(item.getRoomID());
            	room.removeLeaderListener(this);
            } catch (IllegalArgumentException iae) {
                it.remove();
                processItemListChanged();
            }
        }
        super.clear();
    }

    public void updateLeader(RoomModel room) {
        fireTableDataChanged();
    }
}
