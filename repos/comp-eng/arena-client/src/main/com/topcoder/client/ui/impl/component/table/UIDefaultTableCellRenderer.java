package com.topcoder.client.ui.impl.component.table;

import javax.swing.table.DefaultTableCellRenderer;

import com.topcoder.client.ui.impl.component.UILabel;

public class UIDefaultTableCellRenderer extends UILabel {
    protected Object createComponent() {
        return new DefaultTableCellRenderer();
    }
}
