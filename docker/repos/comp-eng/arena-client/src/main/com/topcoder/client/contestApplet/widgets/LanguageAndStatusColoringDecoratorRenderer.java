/*
 * LanguageAndStatusColoringDecoratorRenderer
 * 
 * Created 09/21/2007
 */
package com.topcoder.client.contestApplet.widgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.Serializable;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.topcoder.client.contestApplet.common.LocalPreferences;

/**
 * @author Diego Belfer (Mural)
 * @version $Id: LanguageAndStatusColoringDecoratorRenderer.java 68218 2008-01-28 13:52:35Z mural $
 */
public abstract class LanguageAndStatusColoringDecoratorRenderer implements TableCellRenderer, Serializable {
    private TableCellRenderer renderer;
    private Color defaultForeground;

    public LanguageAndStatusColoringDecoratorRenderer(TableCellRenderer renderer, Color defaultForeground) {
        this.renderer = renderer;
        this.defaultForeground = defaultForeground != null ? defaultForeground : Color.WHITE;
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component tableCellRendererComponent = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        Integer lang = getLanguage(value, row, column);
        int componentStatus = getStatus(value, row, column);
        if (lang == null) {
            tableCellRendererComponent.setForeground(defaultForeground);
        } else {
            setupLook(LocalPreferences.resolveStatusPropertyFormat(lang.intValue(), componentStatus), tableCellRendererComponent);
        }
        return tableCellRendererComponent;
    }

    protected abstract Integer getLanguage(Object value, int row, int column);
    protected abstract int getStatus(Object value, int row, int column);

    public void setDefaultForeground(Color color) {
        defaultForeground = color;
    }

    private void setupLook(String type, Component tableCellRendererComponent) {
        // Set the color
        String keyAttribute = LocalPreferences.getKeyAttribute(type, LocalPreferences.ATTRIBUTECOLOR);
        LocalPreferences localPref = LocalPreferences.getInstance();
        tableCellRendererComponent.setForeground(localPref.getColor(keyAttribute, defaultForeground));

        // Determine the style
        boolean isBold = localPref.isTrue(LocalPreferences.getKeyAttribute(type, LocalPreferences.ATTRIBUTEBOLD));
        boolean isItalic = localPref.isTrue(LocalPreferences.getKeyAttribute(type, LocalPreferences.ATTRIBUTEITALIC));

        // Get the font
        Font curr = tableCellRendererComponent.getFont();
        
        // Is the style the same - then leave it alone
        if(isBold==curr.isBold() && isItalic==curr.isItalic()) return;

        // Set the new style
        tableCellRendererComponent.setFont(new Font(curr.getFamily(), (isBold ? Font.BOLD : Font.PLAIN) | (isItalic ? Font.ITALIC : Font.PLAIN), curr.getSize()));        
    }
}
