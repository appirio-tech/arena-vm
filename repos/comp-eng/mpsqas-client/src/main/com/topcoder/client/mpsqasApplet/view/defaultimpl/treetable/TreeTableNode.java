package com.topcoder.client.mpsqasApplet.view.defaultimpl.treetable;

import javax.swing.tree.TreeNode;

/**
 * Interface for a TreeTableNode, which is an extension of TreeNode to contain
 * columns of information about the children.
 *
 * @author mitalub
 */
public interface TreeTableNode extends TreeNode {

    /**
     * Returns the element in the column with index <code>column</code>.
     */
    public Object getValueInColumn(int column);
}
