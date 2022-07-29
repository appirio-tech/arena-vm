package com.topcoder.client.mpsqasApplet.view.defaultimpl.treetable;

import java.awt.Component;
import java.awt.event.*;
import java.awt.AWTEvent;
import javax.swing.*;
import javax.swing.event.*;
import java.util.EventObject;
import java.io.Serializable;

/**
 * A base class for CellEditors, providing default implementations for all
 * methods in the CellEditor interface and support for managing a series
 * of listeners.
 *
 * @author mitalub
 */
public class AbstractCellEditor implements CellEditor {

    protected EventListenerList listenerList = new EventListenerList();

    /**
     * Trivial implementation returns <code>null</code>.
     */
    public Object getCellEditorValue() {
        return null;
    }

    /**
     * Trivial implementation returns <code>true</code>.
     */
    public boolean isCellEditable(EventObject e) {
        return true;
    }

    /**
     * Trivial implementation returns <code>false</code>.
     */
    public boolean shouldSelectCell(EventObject anEvent) {
        return false;
    }

    /**
     * Trivial implementation returns <code>true</code>.
     */
    public boolean stopCellEditing() {
        return true;
    }

    /**
     * Does nothing.
     */
    public void cancelCellEditing() {
    }

    /**
     * Adds a CellEditorListener.
     */
    public void addCellEditorListener(CellEditorListener l) {
        listenerList.add(CellEditorListener.class, l);
    }

    /**
     * Removes a CellEditorListener.
     */
    public void removeCellEditorListener(CellEditorListener l) {
        listenerList.remove(CellEditorListener.class, l);
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.
     * @see EventListenerList
     */
    protected void fireEditingStopped() {
        Object[] listeners = listenerList.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == CellEditorListener.class) {
                ((CellEditorListener) listeners[i + 1]).editingStopped(
                        new ChangeEvent(this));
            }
        }
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.
     * @see EventListenerList
     */
    protected void fireEditingCanceled() {
        Object[] listeners = listenerList.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == CellEditorListener.class) {
                ((CellEditorListener) listeners[i + 1]).editingCanceled(
                        new ChangeEvent(this));
            }
        }
    }
}
