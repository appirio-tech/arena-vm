package com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable;

import javax.swing.table.*;
import java.awt.*;
import javax.swing.*;

/**
 * CellRenderer is a cell renderer which does not give focus to the cells.
 * (no boxes around cells when clicked.
 *
 * @author mitalub
 */
public class CellRenderer extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        if (value instanceof Boolean) {
            JCheckBox checkBox = new JCheckBox();
            checkBox.setBackground(super.getTableCellRendererComponent(table,
                    value, isSelected, false, row, column).getBackground());
            checkBox.setHorizontalAlignment(SwingConstants.CENTER);
            checkBox.setSelected(((Boolean) value).booleanValue());
            return checkBox;
        }

        return super.getTableCellRendererComponent(table, value, isSelected,
                false, row, column);
    }
}
