package com.topcoder.client.mpsqasApplet.view.defaultimpl.treetable;

import javax.swing.tree.*;
import javax.swing.event.*;

/**
 * A default implementation of the TreeTableModel. Handles listeners,
 * columns, and data.
 *
 * @author mitalub
 */
public class DefaultTreeTableModel implements TreeTableModel {

    private Object[] columnNames;
    private Object root;
    private EventListenerList listenerList;

    /**
     * Stores the root of the TreeTable and the names of the columns in the
     * TreeTable. Note <code>root</code> must be an instance of a
     * <code>TreeTableNode</code> for the  model to function correctly.
     */
    public DefaultTreeTableModel(Object root, Object[] columnNames) {
        this.listenerList = new EventListenerList();
        this.root = root;
        this.columnNames = columnNames;
    }

    /**
     * Sets the root of the TreeTable.
     */
    public void setRoot(Object root) {
        this.root = root;
        Object[] path = {root};
        fireTreeStructureChanged(this, path, null, null);
    }

    /**
     * Returns the root of the TreeTable.
     */
    public Object getRoot() {
        return root;
    }

    /**
     * Determines if a node in the TreeTable is a leaf (has 0 children).
     * Note <code>node</code> must be an instance of a
     * <code>TreeTableNode</code> for the model to function correctly.
     */
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    /**
     * does nothing..
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    /**
     * Returns the index of <code>child</code> in <code>parent</code>'s child
     * list, or -1 if <code>child</code> is not a child of <code>parent</code>.
     * Note <code>parent</code> must be an instance of a
     * <code>TreeTableNode</code> for the model to function correctly.
     */
    public int getIndexOfChild(Object parent, Object child) {
        for (int i = 0; i < getChildCount(parent); i++) {
            if (getChild(parent, i).equals(child)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Adds a listener to the tree model.
     */
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    /**
     * Removes a listener from the tree model.
     */
    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     */
    protected void fireTreeNodesChanged(Object source, Object[] path,
            int[] childIndices, Object[] children) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
            }
        }
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     */
    protected void fireTreeNodesInserted(Object source, Object[] path,
            int[] childIndices, Object[] children) {

        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
            }
        }
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     */
    protected void fireTreeNodesRemoved(Object source, Object[] path,
            int[] childIndices, Object[] children) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
            }
        }
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     */
    protected void fireTreeStructureChanged(Object source, Object[] path,
            int[] childIndices, Object[] children) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
            }
        }
    }

    /**
     * Returns the class of the element in the column with index
     * <code>column</code>.
     */
    public Class getColumnClass(int column) {
        if (column == 0)
            return TreeTableModel.class;
        else
            return getValueAt(root, column).getClass();
    }

    /**
     * By default, make the column with the Tree in it the only editable one.
     * Making this column editable causes the JTable to forward mouse
     * and keyboard events in the Tree column to the underlying JTree.
     * Note: Assumes the column with the Tree in it is column 0.
     */
    public boolean isCellEditable(Object node, int column) {
        return column == 0;
    }

    /**
     *  does nothing.
     */
    public void setValueAt(Object aValue, Object node, int column) {
    }

    /**
     * Returns the number of columns.
     */
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Returns the name of the column with index <code>column</code>.
     */
    public String getColumnName(int column) {
        return columnNames[column].toString();
    }

    /**
     * Returns the child of <code>parent</code> at index <code>index</code> in the
     * <code>parent</code>'s child list.
     * Note <code>parent</code> must be an instance of <code>TreeTableNode</code>.
     */
    public Object getChild(Object parent, int index) {
        return ((TreeTableNode) parent).getChildAt(index);
    }

    /**
     * Returns the number of children of <code>parent</code>.
     * Note <code>parent</code> must be an instance of a
     * <code>TreeTableNode</code> for the model to function correctly.
     */
    public int getChildCount(Object parent) {
        return ((TreeTableNode) parent).getChildCount();
    }

    /**
     * Returns the value in the column with index <code>column</code> of
     * <code>node</code>.  Note <code>node</code> must be an instance of a
     * <code>TreeTableNode</code> for the model to function correctly.
     */
    public Object getValueAt(Object node, int column) {
        return ((TreeTableNode) node).getValueInColumn(column);
    }
}
