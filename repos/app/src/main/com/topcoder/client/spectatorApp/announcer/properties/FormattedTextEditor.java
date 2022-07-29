package com.topcoder.client.spectatorApp.announcer.properties;

import java.awt.Component;
import java.text.ParseException;

import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;

/**
 * A cell editor that supports the JFormattedTextField
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class FormattedTextEditor extends DefaultCellEditor {
	/**
	 * Constructs the formatter from the passed field
	 * @param textField the formatted text field
	 */
	public FormattedTextEditor(JFormattedTextField textField) {
		super(textField);
	}

	/**
	 * Returns the cell editor component
	 */
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		JFormattedTextField ftf = (JFormattedTextField)super.getTableCellEditorComponent(table, value, isSelected, row, column);
		ftf.setValue(value);
		return ftf;
	}

	/**
	 * Gets the cell editor value
	 */
	public Object getCellEditorValue() {
		return ((JFormattedTextField)getComponent()).getValue();
	}
	
	/**
	 * Stops the editing on the column
	 */
	public boolean stopCellEditing() {
		JFormattedTextField ftf = (JFormattedTextField)getComponent();
		if(ftf.isValid()) try { ftf.commitEdit(); } catch (ParseException e) {}
		return super.stopCellEditing();
	}
}
