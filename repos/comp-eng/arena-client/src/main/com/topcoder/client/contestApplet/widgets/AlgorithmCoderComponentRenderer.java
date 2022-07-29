/*
 * AlgorithmCoderComponentRenderer
 * 
 * Created Jan 4, 2008
 */
package com.topcoder.client.contestApplet.widgets;

import java.awt.Component;
import java.io.Serializable;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.topcoder.client.contestant.CoderComponent;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.netCommon.contest.ResultDisplayType;

/**
 * @autor Diego Belfer (Mural)
 * @version $Id: AlgorithmCoderComponentRenderer.java 67962 2008-01-15 15:57:53Z mural $
 */
public class AlgorithmCoderComponentRenderer implements TableCellRenderer, Serializable {
    private LanguageAndStatusColoringDecoratorRenderer colorRenderer;
    private ComponentResultDisplayRenderer statusRenderer;
    
    public AlgorithmCoderComponentRenderer(ContestTableCellRenderer baseRenderer, RoundModel roundModel) {
        statusRenderer = new ComponentResultDisplayRenderer(roundModel, baseRenderer);
        colorRenderer = new LanguageAndStatusColoringDecoratorRenderer(statusRenderer, baseRenderer.getForeground()) {
            protected Integer getLanguage(Object value, int row, int column) {
                try {
                    return ((CoderComponent) value).getLanguageID();
                } catch (RuntimeException e) {
                    return null;
                }
            }
            protected int getStatus(Object value, int row, int column) {
                try {
                    return ((CoderComponent) value).getStatus().intValue();
                } catch (RuntimeException e) {
                    return 0;
                }
            }
        };
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        return colorRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

    public ResultDisplayType getDisplayType() {
        return statusRenderer.getDisplayType();
    }

    public void setDisplayType(ResultDisplayType displayType) {
        statusRenderer.setDisplayType(displayType);
    }

    public void toggleDisplayTypeForComponent(CoderComponent component, ResultDisplayType[] resultDisplayTypes) {
        statusRenderer.toggleDisplayTypeForComponent(component, resultDisplayTypes);
    }

    public void setModel(RoundModel model) {
        statusRenderer.setModel(model);
    }
}
