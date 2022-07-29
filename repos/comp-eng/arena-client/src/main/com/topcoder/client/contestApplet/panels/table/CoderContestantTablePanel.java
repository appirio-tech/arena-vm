package com.topcoder.client.contestApplet.panels.table;

//import java.util.*;
//import java.awt.*;

import java.awt.event.*;
//import javax.swing.*;
//import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.event.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestant.*;

public class CoderContestantTablePanel extends UserTablePanel {

//    public static final String [][] contestantPopup = { { "Info", "infoPopupEvent" },
//                                                  { "History", "historyPopupEvent" } };

    private MenuItemInfo[] CONTESTANT_POPUP = {
        new MenuItemInfo("Info", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                infoPopupEvent();
            }
        }),
        new MenuItemInfo("History", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                historyPopupEvent();
            }
        }),
    };

    public CoderContestantTablePanel(ContestApplet ca) {
        this(ca, CommonData.contestantHeader);
    }

    public CoderContestantTablePanel(ContestApplet ca, String[] header) {
        super(ca, "Who's assigned [0]", new UserTableModel(ca.getModel(), header) {
            protected boolean isLeader(String handle) {
                RoomModel roomModel = contestantModel.getCurrentRoom();
                if (roomModel != null && roomModel.hasLeader()) {
                    return roomModel.getLeader().getUserName().equals(handle);
                } else {
                    return false;
                }
            }

        }, true);

        setContestPopup("Contestants Info", CONTESTANT_POPUP);

        contestTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(isEnabled()) {
                    rightClickEvent(e);
                    doubleClickEvent(e);
                }
            }
        });
        tableModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                tableCountEvent();
            }
        });
        setToolTipText("Static list of registered users assigned to this room.");
        tableModel.sort(0, true);
        
        setupFonts();
    }

    private void historyPopupEvent() {
        int index = contestTable.getSelectedRow();
        String handle = ((UserNameEntry) contestTable.getValueAt(index, 1)).getName();
        ca.setCurrentFrame(ca.getMainFrame());
        ca.requestCoderHistory(handle, ca.getModel().getCurrentRoom().getRoomID().longValue(),
                ((UserNameEntry) contestTable.getValueAt(index, 1)).getUserType());
    }

    ////////////////////////////////////////////////////////////////////////////////
    void tableCountEvent()
            ////////////////////////////////////////////////////////////////////////////////
    {
        ((TitledBorder) getBorder()).setTitle("Who's assigned [" + contestTable.getRowCount() + "]");
        revalidate();
        repaint();
    }

}
