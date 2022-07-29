package com.topcoder.client.contestApplet.widgets;

/**
 * JLabelListCellRenderer.java
 *
 * Description:		List cell renderer to render a list of JLabels
 * @author			Tim "Pops" Roberts (troberts@bigfoot.com)
 * @version			1.0
 */

import java.awt.*;
import javax.swing.*;
//import javax.swing.table.*;
//import javax.swing.border.*;
import com.topcoder.client.contestApplet.common.*;

public class JLabelListCellRenderer extends JLabel implements ListCellRenderer {
    private Color selectedBackground = Common.HB_COLOR;
    private Color background = Common.BG_COLOR;

    public JLabelListCellRenderer() {
        this.setOpaque(true);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public Component getListCellRendererComponent(JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
            ////////////////////////////////////////////////////////////////////////////////
    {

        JLabel label = (JLabel) value;

        if (isSelected) {
            setBackground(selectedBackground);
        } else {
            setBackground(background);
        }

        setForeground(label.getForeground());
        setText(label.getText());
        //list.setBorder(BorderFactory.createLineBorder(Color.white, 1));
        return this;
    }

    public void setSelectedBackground(Color color) {
        selectedBackground = color;
    }

    public void setUnselectedBackground(Color color) {
        background = color;
    }
}
