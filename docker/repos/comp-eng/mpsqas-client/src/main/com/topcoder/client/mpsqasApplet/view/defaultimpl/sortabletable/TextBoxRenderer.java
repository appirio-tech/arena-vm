/*
 * TextBoxRenderer.java
 *
 * Created on December 12, 2006, 10:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable;

import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * An editable JTextField editor
 * @author rfairfax
 */
public class TextBoxRenderer extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
            JFormattedTextField component = null;
            
            component = new JFormattedTextField(new DecimalFormat("$#,##0.00"));
            component.setValue(value);
            
            return component;
            
    }
}
