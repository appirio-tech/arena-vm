package com.topcoder.client.contestApplet.widgets;

import java.awt.*;
import javax.swing.*;
//import javax.swing.table.*;
//import javax.swing.border.*;
import com.topcoder.client.contestApplet.common.*;

public class ContestListCellRenderer extends JLabel implements ListCellRenderer {

    JList internalList = null;
    Color selectedForeground = Common.HF_COLOR;
    Color selectedBackground = Common.HB_COLOR;
    Color foreground = Common.TF_COLOR;
    Color background = Common.TB_COLOR;

    ////////////////////////////////////////////////////////////////////////////////
    public ContestListCellRenderer()
            ////////////////////////////////////////////////////////////////////////////////
    {
        setOpaque(true);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public Component getListCellRendererComponent(JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
            ////////////////////////////////////////////////////////////////////////////////
    {
        if ((internalList != list) && (list != null)) {
            internalList = list;
            //list.setBorder(new LineBorder(Color.white));
            list.setSelectionForeground(foreground);
            list.setSelectionBackground(background);
        }

        if (isSelected) {
            setForeground(selectedForeground);
            setBackground(selectedBackground);
        } else {
            setForeground(foreground);
            setBackground(background);
        }

        if (value != null) {
            setText(value.toString());
        }

        return this;
    }

    public void setUnselectedBackground(Color color) {
        background = color;
    }

    public void setUnselectedForeground(Color color) {
        foreground = color;
    }

    public void setSelectedBackground(Color color) {
        selectedBackground = color;
    }

    public void setSelectedForeground(Color color) {
        selectedForeground = color;
    }
}
