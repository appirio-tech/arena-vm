package com.topcoder.client.contestApplet.panels.table;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;
import com.topcoder.netCommon.contest.ContestConstants;


public final class ActiveUsersTablePanel extends UserTablePanel {

//    private static final String[][] ACTIVE_USER_POPUP = { { "Info", "infoPopupEvent" },
//                                                  { "Search..", "searchPopupEvent"} };

    private final MenuItemInfo[] ACTIVE_USER_POPUP = {
        new MenuItemInfo("Info", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                infoPopupEvent();
            }
        }),
        new MenuItemInfo("Search..", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchPopupEvent();
            }
        }),
    };

    private JFrame jf = null;
    
    ////////////////////////////////////////////////////////////////////////////////
    public ActiveUsersTablePanel(ContestApplet ca, JFrame jf)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(ca, "Who's logged in");
        this.jf = jf;

        setContestPopup("User Info", ACTIVE_USER_POPUP);

        setToolTipText("List of users who are currently logged into the applet.");

        // register events
//    contestTable.addMouseListener(new ml("mouseClicked", "rightClickEvent", this));
//    contestTable.addMouseListener(new ml("mouseClicked", "doubleClickEvent", this));
//    contestTable.addMouseListener(new MouseAdapter() {
//        public void mouseClicked(MouseEvent e) {
//            rightClickEvent(e);
//            doubleClickEvent(e);
//        }
//    });
        //contestTableModel.addTableModelListener(new tml("tableChanged", "tableCountEvent", this));
        tableModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                tableCountEvent();
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////
    void tableCountEvent()
            ////////////////////////////////////////////////////////////////////////////////
    {
        ((TitledBorder) getBorder()).setTitle("Who's logged in [" + contestTable.getRowCount() + "]");
        revalidate();
        repaint();
    }

    ////////////////////////////////////////////////////////////////////////////////
    void doubleClickEvent(MouseEvent e)
            ////////////////////////////////////////////////////////////////////////////////
    {
        if(isEnabled()) {
            if ((e.getClickCount() > 1) && SwingUtilities.isLeftMouseButton(e)) {
                infoPopupEvent();
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    protected void infoPopupEvent()
            ////////////////////////////////////////////////////////////////////////////////
    {
        int index = contestTable.getSelectedRow();
        String handle = ((UserNameEntry) contestTable.getValueAt(index, 1)).getName();
        ca.setCurrentFrame(jf);
        ca.requestCoderInfo(handle, ((UserNameEntry) contestTable.getValueAt(index, 1)).getUserType());
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void searchPopupEvent()
            ////////////////////////////////////////////////////////////////////////////////
    {
        int index = contestTable.getSelectedRow();
        String handle = ((UserNameEntry) contestTable.getValueAt(index, 1)).getName();
        ca.setCurrentFrame(jf);
        ca.getInterFrame().showMessage("Fetching search results...", ContestConstants.SEARCH);
        ca.getModel().getRequester().requestSearch(handle);

    }
}
