/*
 * CellRendererFactory
 * 
 * Created 06/13/2007
 */
package com.topcoder.client.contestApplet.widgets;

import java.text.Format;

import javax.swing.JLabel;
import javax.swing.table.TableCellRenderer;

/**
 * @author Diego Belfer (mural)
 * @version $Id: CellRendererFactory.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class CellRendererFactory {
    
    public static TableCellRenderer apply(TableCellRenderer renderer, Format format) {
        return new FormatTableCellRenderer(renderer, format);
    }

    public static TableCellRenderer apply(TableCellRenderer renderer, Format format, int aling) {
        return apply(apply(renderer, aling), format);
    }

    public static TableCellRenderer apply(TableCellRenderer renderer, int aling) {
        if (renderer instanceof JLabel) {
            ((JLabel) renderer).setHorizontalAlignment(aling);
        }
        return renderer;
    }
    
    public static TableCellRenderer create(String fontName, int fontSize, Format format) {
        return apply(create(fontName, fontSize), format);
    }
    
    public static TableCellRenderer create(String fontName, int fontSize, Format format, int aling) {
        return apply(create(fontName, fontSize, aling), format);
    }

    public static TableCellRenderer create(String fontName, int fontSize, int aling) {
        return apply(create(fontName, fontSize),aling);
    }

    public static TableCellRenderer create(String fontName, int fontSize) {
        return new ContestTableCellRenderer(fontName, fontSize);
    }
}
