package com.topcoder.client.ui.impl.component.table;

import java.awt.GridBagConstraints;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.plaf.TableHeaderUI;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;
import com.topcoder.client.ui.impl.component.UISwingComponent;

public class UITableHeader extends UISwingComponent {
    private JTableHeader header;

    protected Object createComponent() {
        return new JTableHeader();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();

        header = (JTableHeader) getEventSource();
    }

    protected void addChildImpl(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        throw new UIComponentException("Table header is not a container.");
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("ColumnModel".equalsIgnoreCase(name)) {
            header.setColumnModel((TableColumnModel) value);
        } else if ("DefaultRenderer".equalsIgnoreCase(name)) {
            header.setDefaultRenderer((TableCellRenderer) value);
        } else if ("DraggedColumn".equalsIgnoreCase(name)) {
            header.setDraggedColumn((TableColumn) value);
        } else if ("DraggedDistance".equalsIgnoreCase(name)) {
            header.setDraggedDistance(((Number) value).intValue());
        } else if ("ReorderingAllowed".equalsIgnoreCase(name)) {
            header.setReorderingAllowed(((Boolean) value).booleanValue());
        } else if ("ResizingAllowed".equalsIgnoreCase(name)) {
            header.setResizingAllowed(((Boolean) value).booleanValue());
        } else if ("ResizingColumn".equalsIgnoreCase(name)) {
            header.setResizingColumn((TableColumn) value);
        } else if ("Table".equalsIgnoreCase(name)) {
            header.setTable((JTable) value);
        } else if ("UI".equalsIgnoreCase(name)) {
            header.setUI((TableHeaderUI) value);
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("ColumnModel".equalsIgnoreCase(name)) {
            return header.getColumnModel();
        } else if ("DefaultRenderer".equalsIgnoreCase(name)) {
            return header.getDefaultRenderer();
        } else if ("DraggedColumn".equalsIgnoreCase(name)) {
            return header.getDraggedColumn();
        } else if ("DraggedDistance".equalsIgnoreCase(name)) {
            return new Integer(header.getDraggedDistance());
        } else if ("ReorderingAllowed".equalsIgnoreCase(name)) {
            return Boolean.valueOf(header.getReorderingAllowed());
        } else if ("ResizingAllowed".equalsIgnoreCase(name)) {
            return Boolean.valueOf(header.getResizingAllowed());
        } else if ("ResizingColumn".equalsIgnoreCase(name)) {
            return header.getResizingColumn();
        } else if ("Table".equalsIgnoreCase(name)) {
            return header.getTable();
        } else if ("UI".equalsIgnoreCase(name)) {
            return header.getUI();
        } else {
            return getPropertyImpl(name);
        }
    }

    protected Object performActionImpl(String name, Object[] args) throws UIComponentException {
        if ("resizeAndRepaint".equalsIgnoreCase(name)) {
            assertNull(name, args);
            header.resizeAndRepaint();
            return null;
        } else {
            return super.performActionImpl(name, args);
        }
    }
}
