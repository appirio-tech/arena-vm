package com.topcoder.client.contestApplet.widgets;

import com.topcoder.client.contestApplet.common.Common;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.util.Map;
import java.util.HashMap;

/**
 * <p>Title: BroadcastMessageRenderer</p>
 * <p>Description: </p>
 * @author Walter Mundt
 */
public class BroadcastMessageRenderer extends JLabel implements TableCellRenderer {
    private Map colorMap = new HashMap();

    public void setColors(String colors) {
        colorMap.clear();
        String[] color = colors.split(",");
        for (int i=0;i<color.length;++i) {
            String[] values = color[i].split("=");
            if (values.length != 2) {
                throw new IllegalArgumentException("The color spec is invalid.");
            }

            colorMap.put(values[0], Color.decode(values[1]));
        }
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        try {
            String messageType = (String) table.getValueAt(row, 0);
            Color fgColor = (Color) colorMap.get(messageType);
            if (fgColor != null) {
                setForeground(fgColor);
            }
        } catch (ClassCastException e) {
        }
        setText(value.toString());
        return this;
    }
}
