package com.topcoder.client.mpsqasApplet.view.defaultimpl.tablemodel;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

import com.topcoder.client.mpsqasApplet.view.defaultimpl.icon.*;

/**
 * A renderer for table header buttons.  Depending on the state of the column
 * (selected, sorted up, sorted down) an up or down beveled arrow is drawn
 * next to the table name to show how the table is sorted.
 *
 * @author mitalub
 */
public class SortButtonRenderer extends JButton implements TableCellRenderer {

    public SortButtonRenderer() {
        pushedColumn = -1;
        state = new Hashtable();

        setMargin(new Insets(0, 0, 0, 0));
        setHorizontalTextPosition(LEFT);
        setIcon(new BlankIcon());

        downButton = new JButton();
        downButton.setMargin(new Insets(0, 0, 0, 0));
        downButton.setHorizontalTextPosition(LEFT);
        downButton.setIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, true));
        downButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, true));

        upButton = new JButton();
        upButton.setMargin(new Insets(0, 0, 0, 0));
        upButton.setHorizontalTextPosition(LEFT);
        upButton.setIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, true));
        upButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, true));
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        JButton button = this;
        Object obj = state.get(new Integer(column));
        if (obj != null) {
            if (((Integer) obj).intValue() == DOWN) {
                button = downButton;
            } else {
                button = upButton;
            }
        }
        button.setText((value == null) ? "" : value.toString());
        return button;
    }

    public void setPressedColumn(int col) {
        pushedColumn = col;
    }

    public void setSelectedColumn(int col) {
        if (col < 0) return;
        Integer value = null;
        Object obj = state.get(new Integer(col));
        if (obj == null) {
            value = new Integer(DOWN);
        } else {
            if (((Integer) obj).intValue() == DOWN) {
                value = new Integer(UP);
            } else {
                value = new Integer(DOWN);
            }
        }
        state.clear();
        state.put(new Integer(col), value);
    }

    public int getState(int col) {
        int retValue;
        Object obj = state.get(new Integer(col));
        if (obj == null) {
            retValue = NONE;
        } else {
            if (((Integer) obj).intValue() == DOWN) {
                retValue = DOWN;
            } else {
                retValue = UP;
            }
        }
        return retValue;
    }

    public static final int NONE = 0;
    public static final int DOWN = 1;
    public static final int UP = 2;

    int pushedColumn;
    Hashtable state;
    JButton downButton,upButton;
}
