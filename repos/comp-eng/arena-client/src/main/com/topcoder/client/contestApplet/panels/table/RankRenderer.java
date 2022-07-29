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
public class RankRenderer extends JLabel implements TableCellRenderer {

    private boolean showAsIcon = true; // TODO: add user pref for this

    public RankRenderer() {
        setOpaque(true);
        setHorizontalAlignment(SwingConstants.RIGHT);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }
        if (value instanceof Integer) {
            int rank = ((Integer) value).intValue();
            if (showAsIcon) {
                setIcon(Common.getRankIcon(rank));
                setText(null);
            } else {
                setIcon(null);
                setForeground(Common.getRankColor(rank));
                if (Common.isAdmin(rank) || rank == 0) {
                    setText("");
                } else {
                    setText(value.toString());
                }
            }
        } else {
            setIcon(null);
            setForeground(Color.white);
            setText("?");
        }
        return this;
    }
}
