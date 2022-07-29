package com.topcoder.client.contestApplet.panels.table;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * <p>Title: BroadcastMessageRenderer</p>
 * <p>Description: </p>
 * @author Walter Mundt
 */
class BroadcastMessageRenderer extends JLabel implements TableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
        Color fgColor;
        try {
            String messageType = (String) table.getValueAt(row, 0);
            fgColor = BroadcastSummaryPanel.getColorForType(messageType);
        } catch (ClassCastException e) {
            fgColor = Color.white;
        }
        setForeground(fgColor);
        setText(value.toString());
        return this;
    }
}
