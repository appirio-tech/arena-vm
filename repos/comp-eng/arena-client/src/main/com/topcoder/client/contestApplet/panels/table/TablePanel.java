package com.topcoder.client.contestApplet.panels.table;

//import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
//import javax.swing.text.*;
import javax.swing.border.*;
//import javax.swing.event.*;
import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.client.SortedTableModel;

//import com.topcoder.client.contestApplet.listener.*;

public abstract class TablePanel extends JPanel {

    ContestApplet ca = null;

    // Table Model/View/Controller Definitions
    SortedTableModel tableModel;
    JTable contestTable;
    boolean headersVisible = true;

    // Popup Menu View Definitions
    //private  JPopupMenu contestPopup=new JPopupMenu();
    private JPopupMenu contestPopup = new JPopupMenu();

    protected TablePanel(ContestApplet ca, String title, SortedTableModel tableModel) {
        this(ca, title, tableModel, true);
    }

    TablePanel(ContestApplet ca, String title, SortedTableModel tableModel, boolean headersVisible) {
        super(new GridBagLayout());
        this.ca = ca;
        this.headersVisible = headersVisible;
        this.tableModel = tableModel;
        this.contestTable = createTable(tableModel);
        UIManager.put("MenuItem.selectionBackground", Common.HB_COLOR);

        createTablePanel(title, 0, 0);
    }

    private void setContestPopup(JPopupMenu contestPopup) {
        this.contestPopup = contestPopup;
    }

    void setContestPopup(String label, MenuItemInfo[] menuItemInfo) {
        setContestPopup(PopUpHelper.createPopupMenu(label, menuItemInfo));
    }

    private JTable createTable(TableModel model) {
        JTable table = new JTable(model);

        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);

        table.setBackground(Common.TB_COLOR);
        table.setForeground(Common.TF_COLOR);
        table.setSelectionBackground(Common.HB_COLOR);
        table.setSelectionForeground(Common.HF_COLOR);
        table.setShowGrid(false);

        table.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().setColumnMargin(0);

        setHeaders(table);

        ContestTableCellRenderer tcr = new ContestTableCellRenderer(null,12);
        table.setDefaultRenderer(String.class, tcr);

        return (table);
    }

    private void setHeaders(JTable table) {
        // set the default renderers for the headers/cells
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(new ContestTableHeaderRenderer(headersVisible,null,12));
            table.getColumnModel().getColumn(i).setCellRenderer(new ContestTableCellRenderer(null,12));
        }
    }

    private void createTablePanel(String title, int width, int height) {
        JScrollPane pane = new JScrollPane(contestTable);
        GridBagConstraints gbc = Common.getDefaultConstraints();

        gbc.insets = new Insets(-1, -1, -1, -1);
        this.setBorder(Common.getTitledBorder(title));
        this.setPreferredSize(new Dimension(width, height));
        pane.setBorder(new EmptyBorder(0, 0, 0, 0));
        pane.getViewport().setBackground(Common.TB_COLOR);
        contestTable.setPreferredScrollableViewportSize(pane.getSize());

        Common.insertInPanel(pane, this, gbc, 0, 0, 1, 1);

        // new workspace variables
        this.setBackground(Common.WPB_COLOR);
        this.setOpaque(false);
    }

    public void clear() {
        tableModel.clear();
    }

    public JTable getTable() {
        return (contestTable);
    }

    public SortedTableModel getTableModel() {
        return (tableModel);
    }

    //------------------------------------------------------------------------------
    // Event Handling
    //------------------------------------------------------------------------------

    void rightClickEvent(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            int row = ((JTable) e.getComponent()).rowAtPoint(e.getPoint());
            int col = ((JTable) e.getComponent()).columnAtPoint(e.getPoint());
            ((JTable) e.getComponent()).setRowSelectionInterval(row, row);
            ((JTable) e.getComponent()).setColumnSelectionInterval(col, col);
            showContestPopup(e);
        }
    }

    void showContestPopup(MouseEvent e) {
        contestPopup.show(e.getComponent(), e.getX(), e.getY());
    }
}
