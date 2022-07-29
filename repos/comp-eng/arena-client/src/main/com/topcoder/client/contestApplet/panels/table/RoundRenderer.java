package com.topcoder.client.contestApplet.panels.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.topcoder.client.contestant.RoundModel;
import com.topcoder.netCommon.contestantMessages.response.data.RoundData;

/**
 * <p>Title: RoundRenderer</p>
 * <p>Description: Renders Round table elements</p>
 * @author Griffin Dorman
 */
class RoundRenderer extends JLabel implements TableCellRenderer {

    public RoundRenderer() {
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }
        setForeground(Color.white);
        if (value instanceof RoundData) {
            RoundData round = (RoundData) value;
            setText(round.getContestName());
        } else if (value instanceof RoundModel) {
            RoundModel round = (RoundModel) value;
            setText(round.getSingleName());
        } else {
            setText("#####");
        }
        return this;
    }
}

