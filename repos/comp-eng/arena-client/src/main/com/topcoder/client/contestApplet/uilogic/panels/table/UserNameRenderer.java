package com.topcoder.client.contestApplet.uilogic.panels.table;

import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestant.Contestant;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * <p>Title: UserNameRenderer</p>
 * <p>Description: Renders UserNameEntry table elements</p>
 * @author Walter Mundt
 */
public class UserNameRenderer extends JLabel implements TableCellRenderer {

    private Contestant model;
    private Color currentUserBackground;

    public UserNameRenderer() {
        model = null;
    }

    public void setModel(Contestant model) {
        this.model = model;
    }

    public void setCurrentUserBackground(Color color) {
        currentUserBackground = color;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }
        if (value instanceof UserNameEntry) {
            UserNameEntry user = (UserNameEntry) value;
            setForeground(Common.getRankColor(user.getRank()));
            setText(user.getName());
            if (Common.isAdmin(user.getRank())) {
                setFont(getFont().deriveFont(Font.BOLD | Font.ITALIC));
            } else if (user.isLeader()) {
                setFont(getFont().deriveFont(Font.BOLD));
            } else {
                setFont(getFont().deriveFont(Font.PLAIN));
            }
            if (!isSelected && user.getName().equals(model.getCurrentUser())) {
                setBackground(currentUserBackground);
            }
        } else {
            setText("#####");
            setFont(getFont().deriveFont(Font.PLAIN));
            setForeground(Color.red);
        }
        return this;
    }
}
