package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.CommonData;
import com.topcoder.client.contestApplet.uilogic.panels.table.UserNameEntry;
import com.topcoder.client.contestApplet.uilogic.panels.table.UserTableModel;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIMouseAdapter;

public class HSRegistrantsTablePanel extends UserTablePanel {
    private JFrame jf = null;
    private UIComponent totalLabel;
    private UIComponent newbieLabel;

    protected String getTablePanelName() {
        return "hs_registrants_table_panel";
    }

    protected String getTableName() {
        return "hs_registrants_table";
    }

    protected String getMenuName() {
        return "hs_registrants_table_menu";
    }

    public HSRegistrantsTablePanel(ContestApplet ca, UIPage page, JFrame jf) {
        super(ca, page, new UserTableModel(ca.getModel(), CommonData.registrantsHeader), "hs_registrants_user_renderer", "hs_registrants_header_renderer");
        page.getComponent("hs_registrants_table_menu_info").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    infoPopupEvent();
                }
            });
        this.jf = jf;
        totalLabel = page.getComponent("hs_total_label");
        newbieLabel = page.getComponent("hs_newbie_label");
        userTableModel.addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    tableCountEvent();
                }
            });
        table.addEventListener("Mouse", new UIMouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    doubleClickEvent(e);
                }
            });
        userTableModel.sort(0, false);
    }
    public int getRowCount()
    {
        return ((Integer) table.getProperty("RowCount")).intValue();
    }

    ////////////////////////////////////////////////////////////////////////////////
    protected void tableCountEvent()
        ////////////////////////////////////////////////////////////////////////////////
    {

        //((TitledBorder) getBorder()).setTitle("Who's registered [" + contestTable.getRowCount() + "]");
        ((TitledBorder) tablePanel.getProperty("Border")).setTitle("");

        if(totalLabel != null)
        {
            totalLabel.setProperty("Text", "Total registered [" + getRowCount() + "]");
            newbieLabel.setProperty("Text", "New [" + getNewCount() + "]");
        }
        tablePanel.performAction("revalidate");
        tablePanel.performAction("repaint");
    }

    public int getNewCount()
    {
        int count = 0;

        for(int i = 0; i < getRowCount(); i++ )
        {
            if( ((UserNameEntry) table.performAction("getValueAt", new Object[] {new Integer(i), new Integer(1)})).getRank() == 0 )
            {
                count++;

            }
        }

        return count;
    }

    ////////////////////////////////////////////////////////////////////////////////
    protected void doubleClickEvent(MouseEvent e)
        ////////////////////////////////////////////////////////////////////////////////
    {
    int r = ((JTable) e.getComponent()).rowAtPoint(e.getPoint());
        int c = ((JTable) e.getComponent()).columnAtPoint(e.getPoint());
        ((JTable) e.getComponent()).setRowSelectionInterval(r, r);
        ((JTable) e.getComponent()).setColumnSelectionInterval(c, c);
    
        if(isPanelEnabled()) {
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
        int index = ((Integer) table.getProperty("SelectedRow")).intValue();
        String handle = ((UserNameEntry) table.performAction("getValueAt", new Object[] {new Integer(index), new Integer(1)})).getName();
        ca.setCurrentFrame(jf);
        ca.requestCoderInfo(handle, ((UserNameEntry) table.performAction("getValueAt", new Object[] {new Integer(index), new Integer(1)})).getUserType());
    }
}
