/*
 * FormatTableCellRenderer
 * 
 * Created 06/13/2007
 */
package com.topcoder.client.contestApplet.widgets;

import java.text.Format;

import javax.swing.table.TableCellRenderer;

/**
 * @author Diego Belfer (mural)
 * @version $Id: FormatTableCellRenderer.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class FormatTableCellRenderer extends ValueTransformDecoratorCellRenderer {
    private Format format;
    
    public FormatTableCellRenderer(TableCellRenderer renderer, Format format) {
        super(renderer);
        this.format = format;
    }

    protected Object transform(Object value, int row, int column) {
        if (value == null) {
            return null;
        }
        return format.format(value);
    }
}
