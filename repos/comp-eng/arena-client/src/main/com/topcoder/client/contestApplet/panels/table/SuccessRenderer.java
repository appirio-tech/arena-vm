package com.topcoder.client.contestApplet.panels.table;

import com.topcoder.client.contestApplet.common.Common;
import javax.swing.table.TableCellRenderer;
import javax.swing.*;
import java.awt.*;

/**
 * <p>Title: RankRenderer</p>
 * <p>Description: Renders rank icons directly from Integer values.</p>
 * @author Walter Mundt
 */
class SuccessRenderer implements TableCellRenderer {

    private Object baseClass = null;
    public SuccessRenderer(Object baseClass) {
        this.baseClass = baseClass;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
        JLabel lbl = new JLabel();
        lbl.setOpaque(true);
        lbl.setHorizontalAlignment(SwingConstants.LEFT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));
        if (isSelected) {
            lbl.setBackground(table.getSelectionBackground());
        } else {
            lbl.setBackground(table.getBackground());
        }
        if (value instanceof Boolean) {
            boolean val = ((Boolean)value).booleanValue();
            if(val)
                lbl.setIcon(Common.getImage("greencheck.png", baseClass));
            else
                lbl.setIcon(Common.getImage("fail.gif", baseClass));
        } else {
            lbl.setIcon(null);
            lbl.setForeground(Color.white);
            lbl.setText("?");
        }
        return lbl;
    }
}
