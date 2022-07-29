/*
 * LanguageColoringDecoratorRenderer
 * 
 * Created 06/13/2007
 */
package com.topcoder.client.contestApplet.widgets;

import java.awt.Component;
import java.io.Serializable;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * @autor Diego Belfer (Mural)
 * @version $Id: ValueTransformDecoratorCellRenderer.java 67962 2008-01-15 15:57:53Z mural $
 */
public abstract class ValueTransformDecoratorCellRenderer implements TableCellRenderer, Serializable {
    private TableCellRenderer renderer;
    
    public ValueTransformDecoratorCellRenderer(TableCellRenderer renderer) {
        this.renderer = renderer;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        value = transform(value, row, column);
        return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

    protected abstract Object transform(Object value, int row, int column);
}
