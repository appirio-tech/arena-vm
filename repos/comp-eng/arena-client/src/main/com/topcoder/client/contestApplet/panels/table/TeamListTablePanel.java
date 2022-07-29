/*
 * TeamListTablePanel.java
 *
 * Created on May 30, 2002, 3:59 PM
 */

package com.topcoder.client.contestApplet.panels.table;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.TableColumnModel;

import com.topcoder.client.contestant.view.TeamListView;
import com.topcoder.netCommon.contestantMessages.response.data.*;
import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.widgets.*;

/**
 * @author   Matthew P. Suhocki (msuhocki)
 * @version
 */
public final class TeamListTablePanel extends TablePanel implements TeamListView {

    /*private final MenuItemInfo[] USER_POPUP={
        new MenuItemInfo("Info",new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                infoPopupEvent();
            }
        }),
    };*/

    ////////////////////////////////////////////////////////////////////////////////
    public TeamListTablePanel(ContestApplet ca)
            ////////////////////////////////////////////////////////////////////////////////
    {
//        super(ca, "Teams", CommonData.teamInfoHeader, true);
        super(ca, "Teams", new ContestTableModel(
                CommonData.teamInfoHeader,
                new Class[]{
                    String.class, String.class, Integer.class, Integer.class, String.class
                }
        ), true);
        //setContestPopup("Contestants Info",USER_POPUP);

        TableColumnModel columnModel = contestTable.getColumnModel();

        // Team
        columnModel.getColumn(0).setWidth(150);
        columnModel.getColumn(0).setMinWidth(150);
        //columnModel.getColumn(0).setMaxWidth(150);

        // Captain
        columnModel.getColumn(1).setWidth(150);
        columnModel.getColumn(1).setMinWidth(150);
        //columnModel.getColumn(1).setMaxWidth(150);

        // Available
        columnModel.getColumn(2).setWidth(100);
        columnModel.getColumn(2).setMinWidth(100);
        columnModel.getColumn(2).setMaxWidth(100);

        // Members
        columnModel.getColumn(3).setWidth(100);
        columnModel.getColumn(3).setMinWidth(100);
        columnModel.getColumn(3).setMaxWidth(100);


        contestTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                doubleClickEvent(e);
            }
        });
    }


    private ArrayList getTableRow(TeamListInfo info) {
        JLabel cname = new JLabel(info.getCaptainName());
        cname.setForeground(Common.getRankColor(info.getCaptainRank()));

        JLabel tname = new JLabel(info.getTeamName());
        tname.setIcon(Common.getRankIcon(info.getTeamRank()));
        tname.setForeground(Common.getRankColor(info.getTeamRank()));

        ArrayList row = new ArrayList();
        row.add(tname);
        row.add(cname);
        row.add(new Integer(info.getAvailable()));
        row.add(new Integer(info.getMembers()));
        row.add(info.getStatus());

        return row;
    }

    public void updateTeamList(TeamListInfo info) {
        ArrayList row = getTableRow(info);
        for (int i = 0; i < getTableModel().getRowCount(); i++) {
            if (((JLabel) ((ArrayList) getTableModel().get(i)).get(0)).getText().equals(info.getTeamName())) {
                ((ContestTableModel) getTableModel()).updateRow(i, row);
                return;
            }
        }
        getTableModel().add(row);
    }

    public String getSelectedTeam() {
        if (contestTable.getSelectedRow() < 0) return "";
        return ((JLabel) ((ArrayList) getTableModel().get(contestTable.getSelectedRow())).get(0)).getText();
    }

    private void doubleClickEvent(MouseEvent e) {
        if ((e.getClickCount() > 1) && SwingUtilities.isLeftMouseButton(e)) {
            infoPopupEvent();
        }
    }


    private void infoPopupEvent() {
//        int index = contestTable.getSelectedRow();
        //String handle = ((JLabel)contestTable.getValueAt(index, 1)).getText();
        ca.setCurrentFrame(ca.getMainFrame());
        //ca.requestWatch(index);
    }

    public void updateTeam(TeamListInfo row) {
    }
}
