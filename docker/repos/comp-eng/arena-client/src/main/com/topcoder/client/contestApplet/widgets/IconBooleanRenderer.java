package com.topcoder.client.contestApplet.widgets;

import java.awt.Component;
import java.awt.Color;
import javax.swing.table.TableCellRenderer;
import javax.swing.JLabel;
import javax.swing.JTable;

public class IconBooleanRenderer extends JLabel implements TableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }

        if (value instanceof Boolean) {
            boolean val = ((Boolean)value).booleanValue();
            setEnabled(val);
        } else {
            setForeground(Color.white);
            setText("?");
        }

        return this;
    }
}
