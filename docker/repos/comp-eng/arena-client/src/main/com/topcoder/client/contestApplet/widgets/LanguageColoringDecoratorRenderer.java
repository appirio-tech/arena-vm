/*
 * Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.client.contestApplet.widgets;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.shared.language.*;

/**
 * The language color renderer.
 *
 * <p>
 * Changes in version 1.1 (Python3 Support):
 * <ol>
 *      <li>Updated {@link #getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int)} method.</li>
 * </ol>
 * </p>
 *
 * @author Diego Belfer (Mural), liuliquan
 * @version 1.1
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
                case Python3Language.ID: 
                    color = getColor(LocalPreferences.SUMMARYPYTHON3POINTS);
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
