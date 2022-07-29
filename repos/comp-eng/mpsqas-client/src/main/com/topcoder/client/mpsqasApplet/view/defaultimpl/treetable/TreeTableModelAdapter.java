package com.topcoder.client.mpsqasApplet.view.defaultimpl.treetable;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

/**
 * This is a wrapper class takes a TreeTableModel and implements
 * the table model interface. All of the event dispatching support
 * provided by the AbstractTableModel.
 *
 * @author mitalub
 */
public class TreeTableModelAdapter extends AbstractTableModel {

    JTree tree;
    TreeTableModel treeTableModel;

    /**
     * Constructor stores the <code>JTree</code> and <code>TreeTableModel</code>
     * for the <code>TreeTable</code>.
     */
    public TreeTableModelAdapter(TreeTableModel treeTableModel, JTree tree) {
        this.tree = tree;
        this.treeTableModel = treeTableModel;

        tree.addTreeExpansionListener(new TreeExpansionListener() {
            // Don't use fireTableRowsInserted() here;
            // the selection model would get  updated twice.
            public void treeExpanded(TreeExpansionEvent event) {
                fireTableDataChanged();
            }

            public void treeCollapsed(TreeExpansionEvent event) {
                fireTableDataChanged();
            }
        });

        // Install a TreeModelListener that can update the table when
        // tree changes.
        treeTableModel.addTreeModelListener(new TreeModelListener() {
            public void treeNodesChanged(TreeModelEvent e) {
                fireTableDataChanged();
            }

            public void treeNodesInserted(TreeModelEvent e) {
                fireTableDataChanged();
            }

            public void treeNodesRemoved(TreeModelEvent e) {
                fireTableDataChanged();
            }

            public void treeStructureChanged(TreeModelEvent e) {
                fireTableDataChanged();
            }
        });
    }

    /**
     * Calls the <code>TreeTableModel</code> to determine the number of columns
     * in the table.
     */
    public int getColumnCount() {
        return treeTableModel.getColumnCount();
    }

    /**
     * Calls the <code>TreeTableModel</code> to determine the name of the column
     * with index <code>column</code>.
     */
    public String getColumnName(int column) {
        return treeTableModel.getColumnName(column);
    }

    /**
     * Calls the <code>TreeTableModel</code> to determine the class of the column
     * with index <code>column</code>.
     */
    public Class getColumnClass(int column) {
        return treeTableModel.getColumnClass(column);
    }

    /**
     * Returns number of rows in the TableTree.
     */
    public int getRowCount() {
        return tree.getRowCount();
    }

    /**
     * Returns the node corresponding to the <code>row</code>th row in the
     * TableTree.
     */
    protected Object nodeForRow(int row) {
        TreePath treePath = tree.getPathForRow(row);
        return treePath.getLastPathComponent();
    }

    /**
     * Calls the <code>TreeTableModel</code> to determin the value in a cell.
     */
    public Object getValueAt(int row, int column) {
        return treeTableModel.getValueAt(nodeForRow(row), column);
    }

    /**
     * Calls the <code>TreeTableModel</code> to determine if a cell is editable.
     */
    public boolean isCellEditable(int row, int column) {
        return treeTableModel.isCellEditable(nodeForRow(row), column);
    }

    /**
     * Calls the <code>TreeTableModel</code> to set the value in a certain cell.
     */
    public void setValueAt(Object value, int row, int column) {
        treeTableModel.setValueAt(value, nodeForRow(row), column);
    }
}
