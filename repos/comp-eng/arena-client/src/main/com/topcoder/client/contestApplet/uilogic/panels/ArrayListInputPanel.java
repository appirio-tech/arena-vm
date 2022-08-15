package com.topcoder.client.contestApplet.uilogic.panels;

import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.topcoder.client.DummySortedTableModel;
import com.topcoder.client.SortedTableModel;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;

public class ArrayListInputPanel extends TablePanel {
    private String title = "";
    private UIComponent panel;

    protected String getTableName() {
        return "arraylist_input_table";
    }

    protected String getMenuName() {
        return null;
    }

    public ArrayListInputPanel(UIPage page, String title) {
        this(page, title, new DummySortedTableModel());
    }

    public ArrayListInputPanel(UIPage page, String title, SortedTableModel model) {
        super(null, page, model);
        this.title = title;
        panel = page.getComponent("arraylist_input_panel");
        tableCountEvent();

        // event registration
        //contestTableModel.addTableModelListener(new tml("tableChanged", "tableCountEvent", this));
        tableModel.addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    tableCountEvent();
                }
            });
    }

    public int size() {
        return tableModel.getRowCount();
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
        ((TitledBorder) panel.getProperty("Border")).setTitle(title + " -- [" + getTable().getRowCount() + "]");
        panel.performAction("revalidate");
        panel.performAction("repaint");
    }
}
