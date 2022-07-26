package com.topcoder.client.contestApplet.panels.table;

import com.topcoder.client.contestApplet.common.Common;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * <p>Title: DateRenderer</p>
 * <p>Description: Renders Long values as dates</p>
 * @author Walter Mundt
 */
class DateRenderer extends JLabel implements TableCellRenderer {

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
        if (value instanceof Long) {
            setText(Common.formatTime(((Long) value).longValue()));
        } else {
            setText("#####");
        }
        return this;
    }
}
