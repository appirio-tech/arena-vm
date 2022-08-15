package com.topcoder.client.ui.impl.component.table;

import java.awt.GridBagConstraints;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;
import com.topcoder.client.ui.impl.component.UIAbstractComponent;

public class UITableColumn extends UIAbstractComponent {
    private TableColumn column;

    protected Object createComponent() {
        return new TableColumn();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();

        column = (TableColumn) getEventSource();
    }

    protected void addChildImpl(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        throw new UIComponentException("Table column is not a container.");
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("CellEditor".equalsIgnoreCase(name)) {
            column.setCellEditor((TableCellEditor) value);
        } else if ("CellRenderer".equalsIgnoreCase(name)) {
            column.setCellRenderer((TableCellRenderer) value);
        } else if ("HeaderRenderer".equalsIgnoreCase(name)) {
            column.setHeaderRenderer((TableCellRenderer) value);
        } else if ("HeaderValue".equalsIgnoreCase(name)) {
            column.setHeaderValue(value);
        } else if ("Identifier".equalsIgnoreCase(name)) {
            column.setIdentifier(value);
        } else if ("MaxWidth".equalsIgnoreCase(name)) {
            column.setMaxWidth(((Number) value).intValue());
        } else if ("MinWidth".equalsIgnoreCase(name)) {
            column.setMinWidth(((Number) value).intValue());
        } else if ("ModelIndex".equalsIgnoreCase(name)) {
            column.setModelIndex(((Number) value).intValue());
        } else if ("PreferredWidth".equalsIgnoreCase(name)) {
            column.setPreferredWidth(((Number) value).intValue());
        } else if ("Resizable".equalsIgnoreCase(name)) {
            column.setResizable(((Boolean) value).booleanValue());
        } else if ("Width".equalsIgnoreCase(name)) {
            column.setWidth(((Number) value).intValue());
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("CellEditor".equalsIgnoreCase(name)) {
            return column.getCellEditor();
        } else if ("CellRenderer".equalsIgnoreCase(name)) {
            return column.getCellRenderer();
        } else if ("HeaderRenderer".equalsIgnoreCase(name)) {
            return column.getHeaderRenderer();
        } else if ("HeaderValue".equalsIgnoreCase(name)) {
            return column.getHeaderValue();
        } else if ("Identifier".equalsIgnoreCase(name)) {
            return column.getIdentifier();
        } else if ("MaxWidth".equalsIgnoreCase(name)) {
            return new Integer(column.getMaxWidth());
        } else if ("MinWidth".equalsIgnoreCase(name)) {
            return new Integer(column.getMinWidth());
        } else if ("ModelIndex".equalsIgnoreCase(name)) {
            return new Integer(column.getModelIndex());
        } else if ("PreferredWidth".equalsIgnoreCase(name)) {
            return new Integer(column.getPreferredWidth());
        } else if ("Resizable".equalsIgnoreCase(name)) {
            return Boolean.valueOf(column.getResizable());
        } else if ("Width".equalsIgnoreCase(name)) {
            return new Integer(column.getWidth());
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected void addEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("propertychange".equalsIgnoreCase(name)) {
            column.addPropertyChangeListener((UIPropertyChangeListener) listener);
        } else {
            super.addEventListenerImpl(name, listener);
        }
    }

    protected void removeEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("propertychange".equalsIgnoreCase(name)) {
            column.removePropertyChangeListener((UIPropertyChangeListener) listener);
        } else {
            super.removeEventListenerImpl(name, listener);
        }
    }

    protected Object performActionImpl(String name, Object[] args) throws UIComponentException {
        if ("sizeWidthToFit".equalsIgnoreCase(name)) {
            assertNull(name, args);
            column.sizeWidthToFit();
            return null;
        } else {
            return super.performActionImpl(name, args);
        }
    }
}
