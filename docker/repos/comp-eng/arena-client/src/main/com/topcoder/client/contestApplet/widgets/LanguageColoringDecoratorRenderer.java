/*
 * LanguageColoringDecoratorRenderer
 * 
 * Created 06/13/2007
 */
package com.topcoder.client.contestApplet.widgets;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.shared.language.CPPLanguage;
import com.topcoder.shared.language.CSharpLanguage;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.language.PythonLanguage;
import com.topcoder.shared.language.VBLanguage;

/**
 * @autor Diego Belfer (Mural)
 * @version $Id: LanguageColoringDecoratorRenderer.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public abstract class LanguageColoringDecoratorRenderer implements TableCellRenderer {
    private LocalPreferences localPref = LocalPreferences.getInstance();
    private TableCellRenderer renderer;
    
    public LanguageColoringDecoratorRenderer(TableCellRenderer renderer) {
        this.renderer = renderer;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component tableCellRendererComponent = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        Integer lang = getLanguage(value, row, column);
        Color color;
        if (lang == null) {
            color = Common.CODER_GREEN;
        } else {
            switch (lang.intValue()) {
                case JavaLanguage.ID : 
                    color = getColor(LocalPreferences.SUMMARYJAVAPOINTS);
                    break;
                case CPPLanguage.ID :
                    color = getColor(LocalPreferences.SUMMARYCPPPOINTS);
                    break;
                case CSharpLanguage.ID :
                    color = getColor(LocalPreferences.SUMMARYCSHARPPOINTS);
                    break;
                case VBLanguage.ID : 
                    color = getColor(LocalPreferences.SUMMARYVBPOINTS);
                    break;
                case PythonLanguage.ID: 
                    color = getColor(LocalPreferences.SUMMARYPYTHONPOINTS);
                    break;
                default:
                    color = Common.CODER_GREEN;
                    break;
            }
        }
        tableCellRendererComponent.setForeground(color);
        return tableCellRendererComponent;
    }

    protected abstract Integer getLanguage(Object value, int row, int column);

    private Color getColor(String type) {
        return localPref.getColor(LocalPreferences.getKeyAttribute(type, LocalPreferences.ATTRIBUTECOLOR));
    }
}
