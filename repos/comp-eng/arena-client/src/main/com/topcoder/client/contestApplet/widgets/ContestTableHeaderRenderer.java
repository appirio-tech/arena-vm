package com.topcoder.client.contestApplet.widgets;

import com.topcoder.client.contestApplet.common.Common;

import javax.swing.*;
import javax.swing.table.*;
//import javax.swing.table.TableModel;
import java.awt.*;

public final class ContestTableHeaderRenderer extends JLabel implements TableCellRenderer {

    private boolean exists;
    private String fontName;
    private int fontSize;

    // POPS - 10/21/2001 - changed to allow setting of the alignment
//    private int justify;

    ////////////////////////////////////////////////////////////////////////////////
    public ContestTableHeaderRenderer(String fontName, int fontSize)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this(false, SwingConstants.LEFT,fontName,fontSize);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ContestTableHeaderRenderer(boolean exists,String fontName, int fontSize)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this(exists, SwingConstants.LEFT,fontName,fontSize);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ContestTableHeaderRenderer(boolean exists, int justify,String fontName, int fontSize)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.exists = exists;
//        this.justify = justify;
        if (exists) {
            setForeground(Common.THF_COLOR);
            setBackground(Common.THB_COLOR);
            setOpaque(true);
            setHorizontalAlignment(justify);
            setHorizontalTextPosition(SwingConstants.LEADING);
            setIconTextGap(1);
        }
        this.fontName = fontName;
        this.fontSize = fontSize;
        
        this.setFont(new Font(fontName, this.getFont().getStyle(), fontSize));
    }

    ////////////////////////////////////////////////////////////////////////////////
    public Component getTableCellRendererComponent(JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column)
            ////////////////////////////////////////////////////////////////////////////////
    {
        if (exists) {
            setText((value == null) ? "" : value.toString());
//            TableModel tableModel = table.getModel();
//            if (tableModel instanceof SortedContestTableModel) {
//                SortedContestTableModel sortedModel = (SortedContestTableModel)tableModel;
//                if (sortedModel.getSortColumn() == column) {
//                    setIcon(Common.getSortIcon(sortedModel.getSortOrder()));
//                } else {
//                    setIcon(null);
//                }
//            }
        }
        return (this);
    }
}
