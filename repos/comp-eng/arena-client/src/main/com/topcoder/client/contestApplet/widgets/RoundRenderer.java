package com.topcoder.client.contestApplet.widgets;

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
public class RoundRenderer extends JLabel implements TableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }
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
