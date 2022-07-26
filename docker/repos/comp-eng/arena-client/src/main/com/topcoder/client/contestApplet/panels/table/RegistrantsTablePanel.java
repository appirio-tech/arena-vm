package com.topcoder.client.contestApplet.panels.table;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.CommonData;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;

public final class RegistrantsTablePanel extends UserTablePanel {

    private JFrame jf = null;
    public JLabel totalLabel;
    public JLabel div1Label;
    public JLabel div2Label;
    public JLabel newbieLabel;
    
    public RegistrantsTablePanel(ContestApplet ca, JFrame jf) {
        super(ca, "Who's registered", new UserTableModel(ca.getModel(), CommonData.registrantsHeader), true);
        this.jf = jf;

        setContestPopup("User Info", USER_POPUP);

        //contestTableModel.addTableModelListener(new tml("tableChanged", "tableCountEvent", this));
    
        tableModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                tableCountEvent();
            }
        });
        setToolTipText("List of users currently registered for the competition.");

        totalLabel = new JLabel("TESTING");
        totalLabel.setForeground(Common.THF_COLOR);
        totalLabel.setBackground(Common.THB_COLOR);
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD));

        div1Label = new JLabel("DIV1");
        div1Label.setForeground(Common.THF_COLOR);
        div1Label.setBackground(Common.THB_COLOR);
        div1Label.setFont(totalLabel.getFont().deriveFont(Font.BOLD));

        div2Label = new JLabel("DIV2");
        div2Label.setForeground(Common.THF_COLOR);
        div2Label.setBackground(Common.THB_COLOR);
        div2Label.setFont(totalLabel.getFont().deriveFont(Font.BOLD));

        newbieLabel = new JLabel("NEWBIE");
        newbieLabel.setForeground(Common.THF_COLOR);
        newbieLabel.setBackground(Common.THB_COLOR);
        newbieLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD));

        // register events
        //contestTable.addMouseListener(new MouseListener("mouseClicked", "doubleClickEvent", this));
        contestTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                doubleClickEvent(e);
            }
        });
        tableModel.sort(0, false);
    }

    public int getRowCount()
    {
        return contestTable.getRowCount();
    }

    ////////////////////////////////////////////////////////////////////////////////
    void tableCountEvent()
            ////////////////////////////////////////////////////////////////////////////////
    {

        //((TitledBorder) getBorder()).setTitle("Who's registered [" + contestTable.getRowCount() + "]");
        ((TitledBorder) getBorder()).setTitle("");

        if(totalLabel != null)
        {
            totalLabel.setText("Total registered [" + contestTable.getRowCount() + "]");
            div1Label.setText("Div 1 [" + getDiv1Count() + "]");
            div2Label.setText("Div 2 [" + getDiv2Count() + "]");
            newbieLabel.setText("New [" + getNewCount() + "]");
        }
        revalidate();
        repaint();
    }

    public int getDiv1Count()
    {
        int count = 0;

        for(int i = 0; i < contestTable.getRowCount(); i++ )
        {
            if( ((UserNameEntry) contestTable.getValueAt(i, 1)).getRank() >= 1200 )
            {
                count++;

            }
        }

        return count;
    }

    public int getDiv2Count()
    {
        int count = 0;

        for(int i = 0; i < contestTable.getRowCount(); i++ )
        {
            if( ((UserNameEntry) contestTable.getValueAt(i, 1)).getRank() < 1200 && ((UserNameEntry) contestTable.getValueAt(i, 1)).getRank() != 0 )
            {
                count++;

            }
        }

        return count;
    }

    public int getNewCount()
    {
        int count = 0;

        for(int i = 0; i < contestTable.getRowCount(); i++ )
        {
            if( ((UserNameEntry) contestTable.getValueAt(i, 1)).getRank() == 0 )
            {
                count++;

            }
        }

        return count;
    }

    ////////////////////////////////////////////////////////////////////////////////
    void doubleClickEvent(MouseEvent e)
    ////////////////////////////////////////////////////////////////////////////////
    {
    	int r = ((JTable) e.getComponent()).rowAtPoint(e.getPoint());
        int c = ((JTable) e.getComponent()).columnAtPoint(e.getPoint());
        ((JTable) e.getComponent()).setRowSelectionInterval(r, r);
        ((JTable) e.getComponent()).setColumnSelectionInterval(c, c);
    	
        if(isEnabled()) {
            if (SwingUtilities.isRightMouseButton(e)) {
                showContestPopup(e);
            } else if ((e.getClickCount() == 2) && SwingUtilities.isLeftMouseButton(e)) {
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
}
