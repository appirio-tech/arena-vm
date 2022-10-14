package com.topcoder.client.mpsqasApplet.view.defaultimpl.treetable;

import java.awt.Toolkit;
import java.awt.Insets;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.EventObject;

/**
 * The simple TreeTable component, by using a JTree as a renderer (and editor)
 * for the cells in a particular column in the JTable.
 *
 * @author mitalub
 */
public class TreeTable extends JTable {

    private TreeTableCellRenderer tree;
    private TreeTableModel treeTableModel;

    private String LEAF_ICON = "tree-leaf.jpg";
    private String EXPAND_ICON = "tree-expand.jpg";
    private String COMPACT_ICON = "tree-compact.jpg";
/*
  private String LEAF_ICON =
 "com/topcoder/client/mpsqasApplet/view/defaultimpl/treetable/tree-leaf.jpg";
  private String EXPAND_ICON =
 "com/topcoder/client/mpsqasApplet/view/defaultimpl/treetable/tree-expand.jpg";
  private String COMPACT_ICON =
 "com/topcoder/client/mpsqasApplet/view/defaultimpl/treetable/tree-compact.jpg";
*/

    /**
     * Construct the TreeTable.
     *
     * @param root The root of the TreeTable.
     * @param columnNames The names of the columns in the TreeTable.
     */
    public TreeTable(Object root, Object[] columnNames) {
        super();

        treeTableModel = new DefaultTreeTableModel(root, columnNames);

        // Create the tree. It will be used as a renderer and editor.
        tree = new TreeTableCellRenderer(treeTableModel);

        // Install a tableModel representing the visible rows in the tree.
        super.setModel(new TreeTableModelAdapter(treeTableModel, tree));

        // Force the JTable and JTree to share their row selection models.
        ListToTreeSelectionModelWrapper selectionWrapper =
                new ListToTreeSelectionModelWrapper();
        tree.setSelectionModel(selectionWrapper);
        setSelectionModel(selectionWrapper.getListSelectionModel());

        //allow only single selection
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Install the tree editor renderer and editor.
        setDefaultRenderer(TreeTableModel.class, tree);
        setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());

        setShowGrid(false);
        getTableHeader().setReorderingAllowed(false);
        setIntercellSpacing(new Dimension(0, 0));

        if (tree.getRowHeight() < 1) {
            setRowHeight(18);
        }

        //Put the focus-less cell renderer in as all the columns cell renderers,
        //except the first, which gets the JTree as the renderer
        for (int i = 1; i < columnNames.length; i++) {
            columnModel.getColumn(i).setCellRenderer(new NoFocusCellRenderer());
            columnModel.getColumn(i).setHeaderRenderer(new HeaderRenderer());
        }
        columnModel.getColumn(0).setHeaderRenderer(new HeaderRenderer());
    }

    /**
     * Construct the TreeTable.
     *
     * @param root The root of the TreeTable.
     * @param columnNames The names of the columns in the TreeTable.
     * @param columnWidths The widths of the columns in the TreeTable.
     */
    public TreeTable(Object root, Object[] columnNames, int[] columnWidths) {
        this(root, columnNames);
        for (int i = 0; i < columnWidths.length; i++) {
            getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
    }

    /**
     * Updates the root of the tree, allowing for the data in the tree to
     * change.
     */
    public void updateRoot(Object root) {
        treeTableModel.setRoot(root);
    }

    /**
     * Sets the selected node the be the last one in the passed
     * TreePath.
     */
    public void setSelectionPath(TreePath path) {
        tree.setSelectionPath(path);
        int row = tree.getRowForPath(path);
        clearSelection();
        if (row != -1) {
            setRowSelectionInterval(row, row);
        }
    }

    /**
     * Fully expands the tree so all nodes and children are showing.
     */
    public void fullyExpand() {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        //Recursive visit all nodes and expand all leaves
        visitTreeNode(new TreeNode[0], root);
        ((AbstractTableModel) getModel()).fireTableDataChanged();
    }

    private void visitTreeNode(TreeNode[] nodes, TreeNode node) {
        //Add current node to the path
        TreeNode[] newNodes = new TreeNode[nodes.length + 1];
        System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
        newNodes[nodes.length] = node;
        int count = node.getChildCount();

        //If current node is a leaf then expand tree to show it
        if (count == 0) {
            TreePath path = new TreePath(newNodes);
            tree.setSelectionPath(path);
        } else //Else visit all children
            for (int i = 0; i < count; i++) {
                visitTreeNode(newNodes, node.getChildAt(i));
            }
        return;
    }

    /**
     * Overridden to message super and forward the method to the tree.
     * Since the tree is not actually in the component hieachy it will
     * never receive this unless we forward it in this manner.
     */
    public void updateUI() {
        super.updateUI();
        if (tree != null) {
            tree.updateUI();
        }

        // Use the tree's default foreground and background colors in the
        // table.
        LookAndFeel.installColorsAndFont(this, "Tree.background",
                "Tree.foreground", "Tree.font");
    }

    /**
     * Workaround for BasicTableUI anomaly. Make sure the UI never tries to
     * paint the editor. The UI currently uses different techniques to
     * paint the renderers and editors and overriding setBounds() below
     * is not the right thing to do for an editor. Returning -1 for the
     * editing row in this case, ensures the editor is never painted.
     */
    public int getEditingRow() {
        return (getColumnClass(editingColumn) == TreeTableModel.class) ?
                -1 : editingRow;
    }

    /**
     * Overridden to pass the new rowHeight to the tree.
     */
    public void setRowHeight(int rowHeight) {
        super.setRowHeight(rowHeight);
        if (tree != null && tree.getRowHeight() != rowHeight) {
            tree.setRowHeight(getRowHeight());
        }
    }

    /**
     * Returns the <code>TreeTableModel</code>.
     */
    public TreeTableModel getTreeTableModel() {
        return treeTableModel;
    }

    /**
     * Returns the <code>TreeTableCellRenderer</code>.  Warning: methods called
     * directly on the <code>JTree<code> may have unpredictable results when
     * using the <code>JTree</code> as the <code>TreeTableCellRenderer</code>
     * in the <code>TreeTable</code>.
     */
    public JTree getTree() {
        return tree;
    }

    /**
     * The renderer used to display the tree nodes, a JTree.
     */
    public class TreeTableCellRenderer extends JTree implements TableCellRenderer {

        /** Last table/tree row asked to renderer. */
        private int visibleRow;

        /** Constructor stores the model in the super class. */
        public TreeTableCellRenderer(TreeModel model) {
            super(model);

            DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
            renderer.setClosedIcon(new ImageIcon(
                    Toolkit.getDefaultToolkit().getImage(TreeTable.class
                    .getResource(EXPAND_ICON))));
//              ClassLoader.getSystemResource(EXPAND_ICON)));
            renderer.setOpenIcon(new ImageIcon(
                    Toolkit.getDefaultToolkit().getImage(TreeTable.class
                    .getResource(COMPACT_ICON))));
//              ClassLoader.getSystemResource(COMPACT_ICON)));
            renderer.setLeafIcon(new ImageIcon(
                    Toolkit.getDefaultToolkit().getImage(TreeTable.class
                    .getResource(LEAF_ICON))));
//              ClassLoader.getSystemResource(LEAF_ICON)));
            setCellRenderer(renderer);
        }

        /**
         * updateUI is overridden to set the colors of the Tree's renderer
         * to match that of the table.
         */
        public void updateUI() {
            super.updateUI();

            //Make the tree's cell renderer use the table's cell selection colors.
            TreeCellRenderer tcr = getCellRenderer();
            if (tcr instanceof DefaultTreeCellRenderer) {
                DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer) tcr);
                dtcr.setTextSelectionColor(UIManager.getColor(
                        "Table.selectionForeground"));
                dtcr.setBackgroundSelectionColor(UIManager.getColor(
                        "Table.selectionBackground"));
            }
        }

        /**
         * Sets the row height of the tree, and forwards the row height to the
         * table.
         */
        public void setRowHeight(int rowHeight) {
            if (rowHeight > 0) {
                super.setRowHeight(rowHeight);
                if (TreeTable.this != null &&
                        TreeTable.this.getRowHeight() != rowHeight) {
                    TreeTable.this.setRowHeight(getRowHeight());
                }
            }
        }

        /**
         * Keeps the height of the tree, ignoring the height opposed on it by the
         * table.
         */
        public void setBounds(int x, int y, int w, int h) {
            super.setBounds(x, 0, w, TreeTable.this.getHeight());
        }

        /** Shifts the visible row into the position to be painted and paints it. */
        public void paint(Graphics g) {
            g.translate(0, -visibleRow * getRowHeight());
            super.paint(g);
        }

        /**
         * Returns this as the <code>TableCellRendererComponent</code>.
         * First sets the background color.
         */
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }

            visibleRow = row;
            return this;
        }
    }

    /**
     * The editor used to interact with tree nodes, a JTree.
     */
    public class TreeTableCellEditor extends AbstractCellEditor
            implements TableCellEditor {

        /** Returns the tree that is the <code>TableCellEditorComponent</code>. */
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int r, int c) {
            return tree;
        }

        /**
         * Overridden to return false, and if the event is a mouse event
         * it is forwarded to the tree.<p>
         *
         * By returning false, all keyboard actions are
         * implemented in terms of the table. (By returning true, the
         * tree would get a chance to do something with the keyboard
         * events)
         * By returning false this also has the added benefit that clicking
         * outside of the bounds of the tree node, but still in the tree
         * column will select the row, whereas if this returned true
         * that wouldn't be the case.
         *
         * <p>By returning false we are also enforcing the policy that
         * the tree will never be editable (at least by a key sequence).
         */
        public boolean isCellEditable(EventObject e) {
            if (e instanceof MouseEvent) {
                for (int counter = getColumnCount() - 1; counter >= 0;
                     counter--) {
                    if (getColumnClass(counter) == TreeTableModel.class) {
                        MouseEvent me = (MouseEvent) e;
                        MouseEvent newME = new MouseEvent(tree, me.getID(), me.getWhen(),
                                me.getModifiers(), me.getX() -
                                getCellRect(0, counter, true).x,
                                me.getY(), me.getClickCount(), me.isPopupTrigger());
                        tree.dispatchEvent(newME);
                        break;
                    }
                }
            }
            return false;
        }
    }

    /**
     * ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel
     * to listen for changes in the ListSelectionModel it maintains. Once
     * a change in the ListSelectionModel happens, the paths are updated
     * in the DefaultTreeSelectionModel.
     */
    class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel {

        /** Set to true when we are updating the ListSelectionModel. */
        protected boolean updatingListSelectionModel;

        public ListToTreeSelectionModelWrapper() {
            super();
            getListSelectionModel().addListSelectionListener
                    (createListSelectionListener());
        }

        /**
         * Returns the list selection model. ListToTreeSelectionModelWrapper
         * listens for changes to this model and updates the selected paths
         * accordingly.
         */
        ListSelectionModel getListSelectionModel() {
            return listSelectionModel;
        }

        /**
         * This is overridden to set <code>updatingListSelectionModel</code>
         * and message super. This is the only place DefaultTreeSelectionModel
         * alters the ListSelectionModel.
         */
        public void resetRowSelection() {
            if (!updatingListSelectionModel) {
                updatingListSelectionModel = true;
                try {
                    super.resetRowSelection();
                } finally {
                    updatingListSelectionModel = false;
                }
            }

            // Notice how we don't message super if
            // updatingListSelectionModel is true. If
            // updatingListSelectionModel is true, it implies the
            // ListSelectionModel has already been updated and the
            // paths are the only thing that needs to be updated.
        }

        /**
         * Creates and returns an instance of ListSelectionHandler.
         */
        protected ListSelectionListener createListSelectionListener() {
            return new ListSelectionHandler();
        }

        /**
         * If <code>updatingListSelectionModel</code> is false, this will
         * reset the selected paths from the selected rows in the list
         * selection model.
         */
        protected void updateSelectedPathsFromSelectedRows() {
            if (!updatingListSelectionModel) {
                updatingListSelectionModel = true;
                try {
                    int min = listSelectionModel.getMinSelectionIndex();
                    int max = listSelectionModel.getMaxSelectionIndex();

                    clearSelection();
                    if (min != -1 && max != -1) {
                        for (int counter = min; counter <= max; counter++) {
                            if (listSelectionModel.isSelectedIndex(counter)) {
                                TreePath selPath = tree.getPathForRow(counter);
                                if (selPath != null) {
                                    addSelectionPath(selPath);
                                }
                            }
                        }
                    }
                } finally {
                    updatingListSelectionModel = false;
                }
            }
        }

        /**
         * Class responsible for calling updateSelectedPathsFromSelectedRows
         * when the selection of the list changse.
         */
        class ListSelectionHandler implements ListSelectionListener {

            public void valueChanged(ListSelectionEvent e) {
                updateSelectedPathsFromSelectedRows();
            }
        }
    }

    /**
     * A cell renderer which does not give focus to the cells.
     * (no boxes around cells when clicked)
     *
     * @author mitalub
     */
    class NoFocusCellRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return super.getTableCellRendererComponent(table, value, isSelected,
                    false, row, column);
        }
    }

    /**
     * A JButton to use as the column headers to match the sortable table.
     */
    class HeaderRenderer extends JButton implements TableCellRenderer {

        public HeaderRenderer() {
            setBorder(new EtchedBorder());
            setMargin(new Insets(0, 0, 0, 0));
            setHorizontalTextPosition(LEFT);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JButton button = this;
            button.setText((value == null) ? "" : value.toString());
            return button;
        }
    }
}
