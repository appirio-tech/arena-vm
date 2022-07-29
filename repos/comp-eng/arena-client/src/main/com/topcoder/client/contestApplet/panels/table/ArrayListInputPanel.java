package com.topcoder.client.contestApplet.panels.table;


import java.util.*;
//import java.awt.*;
//import java.awt.event.*;
import javax.swing.*;
//import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.event.*;

import com.topcoder.client.DummySortedTableModel;
import com.topcoder.client.SortedTableModel;

//import com.topcoder.client.contestApplet.common.*;
//import com.topcoder.client.contestApplet.*;
//import com.topcoder.client.contestApplet.widgets.*;
//import com.topcoder.client.contestApplet.listener.*;

public final class ArrayListInputPanel extends TablePanel {

    private String title = "";

    public ArrayListInputPanel(String title) {
        this(title, new DummySortedTableModel());
    }

    public ArrayListInputPanel(String title, SortedTableModel model) {
        super(null, title + " -- [0]", model, false);
        this.title = title;

        // event registration
        //contestTableModel.addTableModelListener(new tml("tableChanged", "tableCountEvent", this));
        tableModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                tableCountEvent();
            }
        });

        contestTable.setRowSelectionAllowed(true);
        contestTable.setColumnSelectionAllowed(true);
        contestTable.setCellSelectionEnabled(true);

        contestTable.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        //contestTable.getTableHeader().setReorderingAllowed(true);

        setToolTipText("Array Input Dialog");
    }

    public void addElement(String element) {
        tableModel.add(element);
    }

    public void removeElement(int row) {
        tableModel.remove(row);
    }

    public void clear() {
        tableModel.clear();
    }

    private void tableCountEvent() {
        ((TitledBorder) getBorder()).setTitle(title + " -- [" + contestTable.getRowCount() + "]");
        revalidate();
        repaint();
    }
}
