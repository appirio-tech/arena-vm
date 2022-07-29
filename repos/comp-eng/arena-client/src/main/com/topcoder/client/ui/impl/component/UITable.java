package com.topcoder.client.ui.impl.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.util.EventObject;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.plaf.TableUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;

public class UITable extends UISwingComponent {
    private JTable component;

    protected Object createComponent() {
        return new JTable();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();
        component = (JTable) getEventSource();
    }

    protected void addChildImpl(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        if (component.getEventSource() instanceof TableColumn) {
            this.component.addColumn((TableColumn) component.getEventSource());
        } else if (component.getEventSource() instanceof JTableHeader) {
            JTableHeader header = (JTableHeader) component.getEventSource();
            header.setColumnModel(this.component.getColumnModel());
            this.component.setTableHeader(header);
        } else {
            throw new UIComponentException("Table only allows column as child.");
        }
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("AutoCreateColumnsFromModel".equalsIgnoreCase(name)) {
            component.setAutoCreateColumnsFromModel(((Boolean) value).booleanValue());
        } else if ("AutoResizeMode".equalsIgnoreCase(name)) {
            component.setAutoResizeMode(((Number) value).intValue());
        } else if ("CellEditor".equalsIgnoreCase(name)) {
            component.setCellEditor((TableCellEditor) value);
        } else if ("CellSelectionEnabled".equalsIgnoreCase(name)) {
            component.setCellSelectionEnabled(((Boolean) value).booleanValue());
        } else if ("ColumnModel".equalsIgnoreCase(name)) {
            component.setColumnModel((TableColumnModel) value);
        } else if ("ColumnSelectionAllowed".equalsIgnoreCase(name)) {
            component.setColumnSelectionAllowed(((Boolean) value).booleanValue());
        } else if ("ColumnMargin".equalsIgnoreCase(name)) {
            component.getColumnModel().setColumnMargin(((Number) value).intValue());
        } else if ("DragEnabled".equalsIgnoreCase(name)) {
            component.setDragEnabled(((Boolean) value).booleanValue());
        } else if ("EditingColumn".equalsIgnoreCase(name)) {
            component.setEditingColumn(((Number) value).intValue());
        } else if ("EditingRow".equalsIgnoreCase(name)) {
            component.setEditingRow(((Number) value).intValue());
        } else if ("GridColor".equalsIgnoreCase(name)) {
            component.setGridColor((Color) value);
        } else if ("model".equalsIgnoreCase(name)) {
            component.setModel((TableModel) value);
        } else if ("PreferredScrollableViewportSize".equalsIgnoreCase(name)) {
            component.setPreferredScrollableViewportSize((Dimension) value);
        } else if ("RowHeight".equalsIgnoreCase(name)) {
            component.setRowHeight(((Number) value).intValue());
        } else if ("RowMargin".equalsIgnoreCase(name)) {
            component.setRowMargin(((Number) value).intValue());
        } else if ("RowSelectionAllowed".equalsIgnoreCase(name)) {
            component.setRowSelectionAllowed(((Boolean) value).booleanValue());
        } else if ("SelectionBackground".equalsIgnoreCase(name)) {
            component.setSelectionBackground((Color) value);
        } else if ("SelectionForeground".equalsIgnoreCase(name)) {
            component.setSelectionForeground((Color) value);
        } else if ("SelectionMode".equalsIgnoreCase(name)) {
            component.setSelectionMode(((Number) value).intValue());
        } else if ("SelectionModel".equalsIgnoreCase(name)) {
            component.setSelectionModel((ListSelectionModel) value);
        } else if ("ShowGrid".equalsIgnoreCase(name)) {
            component.setShowGrid(((Boolean) value).booleanValue());
        } else if ("ShowHorizontalLines".equalsIgnoreCase(name)) {
            component.setShowHorizontalLines(((Boolean) value).booleanValue());
        } else if ("ShowVerticalLines".equalsIgnoreCase(name)) {
            component.setShowVerticalLines(((Boolean) value).booleanValue());
        } else if ("SurrendersFocusOnKeystroke".equalsIgnoreCase(name)) {
            component.setSurrendersFocusOnKeystroke(((Boolean) value).booleanValue());
        } else if ("TableHeader".equalsIgnoreCase(name)) {
            component.setTableHeader((JTableHeader) value);
        } else if ("ui".equalsIgnoreCase(name)) {
            component.setUI((TableUI) value);
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("AutoCreateColumnsFromModel".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.getAutoCreateColumnsFromModel());
        } else if ("AutoResizeMode".equalsIgnoreCase(name)) {
            return new Integer(component.getAutoResizeMode());
        } else if ("CellEditor".equalsIgnoreCase(name)) {
            return component.getCellEditor();
        } else if ("CellSelectionEnabled".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.getCellSelectionEnabled());
        } else if ("ColumnCount".equalsIgnoreCase(name)) {
            return new Integer(component.getColumnCount());
        } else if ("ColumnModel".equalsIgnoreCase(name)) {
            return component.getColumnModel();
        } else if ("ColumnSelectionAllowed".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.getColumnSelectionAllowed());
        } else if ("ColumnMargin".equalsIgnoreCase(name)) {
            return new Integer(component.getColumnModel().getColumnMargin());
        } else if ("DragEnabled".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.getDragEnabled());
        } else if ("EditingColumn".equalsIgnoreCase(name)) {
            return new Integer(component.getEditingColumn());
        } else if ("EditingRow".equalsIgnoreCase(name)) {
            return new Integer(component.getEditingRow());
        } else if ("EditorComponent".equalsIgnoreCase(name)) {
            return component.getEditorComponent();
        } else if ("GridColor".equalsIgnoreCase(name)) {
            return component.getGridColor();
        } else if ("Model".equalsIgnoreCase(name)) {
            return component.getModel();
        } else if ("PreferredScrollableViewportSize".equalsIgnoreCase(name)) {
            return component.getPreferredScrollableViewportSize();
        } else if ("RowCount".equalsIgnoreCase(name)) {
            return new Integer(component.getRowCount());
        } else if ("RowHeight".equalsIgnoreCase(name)) {
            return new Integer(component.getRowHeight());
        } else if ("RowMargin".equalsIgnoreCase(name)) {
            return new Integer(component.getRowMargin());
        } else if ("RowSelectionAllowed".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.getRowSelectionAllowed());
        } else if ("SelectedColumn".equalsIgnoreCase(name)) {
            return new Integer(component.getSelectedColumn());
        } else if ("SelectedColumnCount".equalsIgnoreCase(name)) {
            return new Integer(component.getSelectedColumnCount());
        } else if ("SelectedColumns".equalsIgnoreCase(name)) {
            return component.getSelectedColumns();
        } else if ("SelectedRow".equalsIgnoreCase(name)) {
            return new Integer(component.getSelectedRow());
        } else if ("SelectedRowCount".equalsIgnoreCase(name)) {
            return new Integer(component.getSelectedRowCount());
        } else if ("SelectedRows".equalsIgnoreCase(name)) {
            return component.getSelectedRows();
        } else if ("SelectionBackground".equalsIgnoreCase(name)) {
            return component.getSelectionBackground();
        } else if ("SelectionForeground".equalsIgnoreCase(name)) {
            return component.getSelectionForeground();
        } else if ("SelectionModel".equalsIgnoreCase(name)) {
            return component.getSelectionModel();
        } else if ("ShowHorizontalLines".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.getShowHorizontalLines());
        } else if ("ShowVerticalLines".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.getShowVerticalLines());
        } else if ("SurrendersFocusOnKeystroke".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.getSurrendersFocusOnKeystroke());
        } else if ("TableHeader".equalsIgnoreCase(name)) {
            return component.getTableHeader();
        } else if ("Editing".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isEditing());
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected Object performActionImpl(String name, Object[] args) throws UIComponentException {
        if ("addColumn".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {TableColumn.class});
            component.addColumn((TableColumn) args[0]);
            return null;
        } else if ("addColumnSelectionInterval".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Number.class});
            component.addColumnSelectionInterval(((Number) args[0]).intValue(), ((Number) args[1]).intValue());
            return null;
        } else if ("addRowSelectionInterval".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Number.class});
            component.addRowSelectionInterval(((Number) args[0]).intValue(), ((Number) args[1]).intValue());
            return null;
        } else if ("changeSelection".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Number.class, Boolean.class, Boolean.class});
            component.changeSelection(((Number) args[0]).intValue(), ((Number) args[1]).intValue(),
                                      ((Boolean) args[2]).booleanValue(), ((Boolean) args[3]).booleanValue());
            return null;
        } else if ("clearSelection".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.clearSelection();
            return null;
        } else if ("columnAtPoint".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Point.class});
            return new Integer(component.columnAtPoint((Point) args[0]));
        } else if ("convertColumnIndexToModel".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            return new Integer(component.convertColumnIndexToModel(((Number) args[0]).intValue()));
        } else if ("convertColumnIndexToView".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            return new Integer(component.convertColumnIndexToView(((Number) args[0]).intValue()));
        } else if ("editCellAt".equalsIgnoreCase(name)) {
            if ((args != null) && (args.length == 2)) {
                assertArgs(name, args, new Class[] {Number.class, Number.class});
                component.editCellAt(((Number) args[0]).intValue(), ((Number) args[1]).intValue());
            } else {
                assertArgs(name, args, new Class[] {Number.class, Number.class, EventObject.class});
                component.editCellAt(((Number) args[0]).intValue(), ((Number) args[1]).intValue(), (EventObject) args[2]);
            }

            return null;
        } else if ("getCellEditor".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Number.class});
            return component.getCellEditor(((Number) args[0]).intValue(), ((Number) args[1]).intValue());
        } else if ("getCellRect".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Number.class, Boolean.class});
            return component.getCellRect(((Number) args[0]).intValue(), ((Number) args[1]).intValue(),
                                         ((Boolean) args[2]).booleanValue());
        } else if ("getCellRenderer".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Number.class});
            return component.getCellRenderer(((Number) args[0]).intValue(), ((Number) args[1]).intValue());
        } else if ("getColumn".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Object.class});
            return component.getColumn(args[0]);
        } else if ("getColumnClass".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            return component.getColumnClass(((Number) args[0]).intValue());
        } else if ("getColumnName".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            return component.getColumnName(((Number) args[0]).intValue());
        } else if ("getDefaultEditor".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Class.class});
            return component.getDefaultEditor((Class) args[0]);
        } else if ("getDefaultRenderer".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Class.class});
            return component.getDefaultRenderer((Class) args[0]);
        } else if ("getValueAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Number.class});
            return component.getValueAt(((Number) args[0]).intValue(), ((Number) args[1]).intValue());
        } else if ("isCellEditable".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Number.class});
            return Boolean.valueOf(component.isCellEditable(((Number) args[0]).intValue(), ((Number) args[1]).intValue()));
        } else if ("isCellSelected".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Number.class});
            return Boolean.valueOf(component.isCellSelected(((Number) args[0]).intValue(), ((Number) args[1]).intValue()));
        } else if ("isColumnSelected".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            return Boolean.valueOf(component.isColumnSelected(((Number) args[0]).intValue()));
        } else if ("isRowSelected".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            return Boolean.valueOf(component.isRowSelected(((Number) args[0]).intValue()));
        } else if ("moveColumn".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Number.class});
            component.moveColumn(((Number) args[0]).intValue(), ((Number) args[1]).intValue());
            return null;
        } else if ("removeColumn".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {TableColumn.class});
            component.removeColumn((TableColumn) args[0]);
            return null;
        } else if ("removeColumnSelectionInterval".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Number.class});
            component.removeColumnSelectionInterval(((Number) args[0]).intValue(), ((Number) args[1]).intValue());
            return null;
        } else if ("removeRowSelectionInterval".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Number.class});
            component.removeRowSelectionInterval(((Number) args[0]).intValue(), ((Number) args[1]).intValue());
            return null;
        } else if ("removeEditor".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.removeEditor();
            return null;
        } else if ("rowAtPoint".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Point.class});
            return new Integer(component.rowAtPoint((Point) args[0]));
        } else if ("selectAll".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.selectAll();
            return null;
        } else if ("setColumnSelectionInterval".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Number.class});
            component.setColumnSelectionInterval(((Number) args[0]).intValue(), ((Number) args[1]).intValue());
            return null;
        } else if ("setDefaultEditor".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Class.class, TableCellEditor.class});
            component.setDefaultEditor((Class) args[0], (TableCellEditor) args[1]);
            return null;
        } else if ("setDefaultRenderer".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Class.class, TableCellRenderer.class});
            component.setDefaultRenderer((Class) args[0], (TableCellRenderer) args[1]);
            return null;
        } else if ("setRowHeight".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Number.class});
            component.setRowHeight(((Number) args[0]).intValue(), ((Number) args[1]).intValue());
            return null;
        } else if ("setRowSelectionInterval".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Number.class});
            component.setRowSelectionInterval(((Number) args[0]).intValue(), ((Number) args[1]).intValue());
            return null;
        } else if ("setValueAt".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Object.class, Number.class, Number.class});
            component.setValueAt(args[0], ((Number) args[1]).intValue(), ((Number) args[2]).intValue());
            return null;
        } else {
            return super.performActionImpl(name, args);
        }
    }
}
