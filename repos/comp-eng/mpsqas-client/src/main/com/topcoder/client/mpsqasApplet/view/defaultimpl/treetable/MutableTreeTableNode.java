package com.topcoder.client.mpsqasApplet.view.defaultimpl.treetable;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * An extension of the DefaultMutableTreeNode for use in a TreeTable,
 * contains information for use in all the columns in addition to the
 * value of the node.
 *
 * @author mitalub
 */
public class MutableTreeTableNode extends DefaultMutableTreeNode
        implements TreeTableNode {

    private Object[] data;

    /**
     * Stores the information about the TreeTableNode as an Array of Objects
     * where <code>data[x]</code> is the data in the <code>x</code> column
     * and <code>data[0]</code> is the Object identifying the node.
     */
    public MutableTreeTableNode(Object[] data) {
        super(data == null ? null : data[0]);
        this.data = data;
    }

    /**
     * Returns the Object in the column with index <code>column</code>.
     */
    public Object getValueInColumn(int column) {
        return data[column];
    }
}
