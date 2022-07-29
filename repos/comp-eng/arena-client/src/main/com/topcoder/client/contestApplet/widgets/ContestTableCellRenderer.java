package com.topcoder.client.contestApplet.widgets;

import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

//import javax.swing.border.*;
//import com.topcoder.client.contestApplet.common.*;

public class ContestTableCellRenderer extends DefaultTableCellRenderer {
    
    // POPS - 1/10/03 - added to enable blank display
    private boolean enableBlankDisplay = false;
    private String fontName;
    private int fontSize;

    public ContestTableCellRenderer() {
    }

    public void setFontName(String name) {
        fontName = name;
    }

    public void setFontSize(int size) {
        fontSize = size;
    }
    
    // POPS - 10/21/2001 - changed to allow setting of the alignment
    public ContestTableCellRenderer(String fontName, int fontSize) {
        this(SwingConstants.LEFT,fontName, fontSize);
    }
    
    public ContestTableCellRenderer(int alignment,String fontName, int fontSize) {
        super();
        setHorizontalAlignment(alignment);
        this.fontName = fontName;
        this.fontSize = fontSize;
    }
    
    public Component getTableCellRendererComponent(JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {
        if (table.getSelectedRow() == row && table.getSelectedColumn() == column) {
            isSelected = true;
        }
        
        // POPS - 1/10/03 - override blanks with <emtpy> if specified
        if (enableBlankDisplay && value instanceof String && ((String) value).equals("")) value = "<-- Empty -->";
        
        Component c = super.getTableCellRendererComponent(table,
                value,
                isSelected,
                false,
                row,
                column);
        
        c.setFont(new Font(fontName, c.getFont().getStyle(), fontSize));

        return (c);
    }
    
    // POPS - 1/10/03 - added to enable the display of "<empty>" if the text is empty
    public void setEnableBlankDisplay(boolean enable) {
        this.enableBlankDisplay = enable;
    }
    
    public void setValue(Object value) {
        //setForeground(Color.white);
        //setBackground(getBackground());
        
        // determine object type to before adding to the cell
        if (value instanceof String) {
            setText((String) value);
        } else if (value instanceof JLabel) {
            setIcon(((JLabel) value).getIcon());
            setText(((JLabel) value).getText());
            setForeground(((JLabel) value).getForeground());
            setFont(((JLabel) value).getFont());
        } else if (value instanceof ImageIcon) {
            setIcon((ImageIcon) value);
        } else if (value instanceof Icon) {
            setIcon((Icon) value);
        } else if (value instanceof Integer) {
            setText(((Integer) value).toString());
        } else if (value instanceof Double) {
            setText(((Double) value).toString());
        } else if (value instanceof java.util.Date) {
            setText(((java.util.Date) value).toString());
        } else if (value instanceof java.sql.Date) {
            setText(((java.sql.Date) value).toString());
        } else if (value instanceof ArrayList) {
            setText(((ArrayList) value).toString());
        } else if (value == null) {
            setText("");
        }
        
    }
}
