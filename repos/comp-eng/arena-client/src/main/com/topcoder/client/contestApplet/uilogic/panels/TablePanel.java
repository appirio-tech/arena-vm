package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

import com.topcoder.client.SortedTableModel;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;

public abstract class TablePanel {
    protected UIComponent table;
    private UIPage page;
    protected ContestApplet ca;
    protected SortedTableModel tableModel;

    protected abstract String getTableName();

    protected abstract String getMenuName();

    protected TablePanel(ContestApplet ca, UIPage page, SortedTableModel tableModel) {
        table = page.getComponent(getTableName());
        this.page = page;
        this.tableModel = tableModel;
        table.setProperty("model", tableModel);
        this.ca = ca;
    }

    public void clear() {
        tableModel.clear();
    }

    public SortedTableModel getTableModel() {
        return tableModel;
    }

    public JTable getTable() {
        return (JTable) table.getEventSource();
    }

    protected void rightClickEvent(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            int row = ((JTable) e.getComponent()).rowAtPoint(e.getPoint());
            int col = ((JTable) e.getComponent()).columnAtPoint(e.getPoint());
            ((JTable) e.getComponent()).setRowSelectionInterval(row, row);
            ((JTable) e.getComponent()).setColumnSelectionInterval(col, col);
            showContestPopup(e);
        }
    }

    protected void showContestPopup(MouseEvent e) {
        page.getComponent(getMenuName()).performAction("show", new Object[] {e.getComponent(), new Integer(e.getX()), new Integer(e.getY())});
    }
}
