package com.topcoder.client.ui.impl.component;

import java.awt.GridBagConstraints;
import java.util.Vector;
import java.util.Iterator;
import javax.swing.Action;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.ComboBoxUI;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;

public class UIComboBox extends UISwingComponent {
    private JComboBox component;

    protected Object createComponent() {
        return new JComboBox();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();

        component = (JComboBox) getEventSource();
    }

    protected void addChildImpl(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        throw new UIComponentException("Combo box is not a container.");
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("action".equalsIgnoreCase(name)) {
            component.setAction((Action) value);
        } else if ("actioncommand".equalsIgnoreCase(name)) {
            component.setActionCommand((String) value);
        } else if ("editable".equalsIgnoreCase(name)) {
            component.setEditable(((Boolean) value).booleanValue());
        } else if ("editor".equalsIgnoreCase(name)) {
            component.setEditor((ComboBoxEditor) value);
        } else if ("enabled".equalsIgnoreCase(name)) {
            component.setEnabled(((Boolean) value).booleanValue());
        } else if ("keyselectionmanager".equalsIgnoreCase(name)) {
            component.setKeySelectionManager((JComboBox.KeySelectionManager) value);
        } else if ("lightweightpopupenabled".equalsIgnoreCase(name)) {
            component.setLightWeightPopupEnabled(((Boolean) value).booleanValue());
        } else if ("maximumrowcount".equalsIgnoreCase(name)) {
            component.setMaximumRowCount(((Number) value).intValue());
        } else if ("model".equalsIgnoreCase(name)) {
            component.setModel((ComboBoxModel) value);
        } else if ("popupvisible".equalsIgnoreCase(name)) {
            component.setPopupVisible(((Boolean) value).booleanValue());
        } else if ("prototypedisplayvalue".equalsIgnoreCase(name)) {
            component.setPrototypeDisplayValue(value);
        } else if ("renderer".equalsIgnoreCase(name)) {
            component.setRenderer((ListCellRenderer) value);
        } else if ("selectedindex".equalsIgnoreCase(name)) {
            component.setSelectedIndex(((Number) value).intValue());
        } else if ("selecteditem".equalsIgnoreCase(name)) {
            component.setSelectedItem(value);
        } else if ("ui".equalsIgnoreCase(name)) {
            component.setUI((ComboBoxUI) value);
        } else if ("items".equalsIgnoreCase(name)) {
            if (value instanceof Object[]) {
                Object[] items = (Object[]) value;
                for (int i = 0; i < items.length; ++i) {
                    component.addItem(items[i]);
                }
            } else {
                Vector items = (Vector) value;
                for (Iterator iter = items.iterator(); iter.hasNext();) {
                    component.addItem(iter.next());
                }
            }
            component.setUI(component.getUI());
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("action".equalsIgnoreCase(name)) {
            return component.getAction();
        } else if ("actioncommand".equalsIgnoreCase(name)) {
            return component.getActionCommand();
        } else if ("editor".equalsIgnoreCase(name)) {
            return component.getEditor();
        } else if ("itemcount".equalsIgnoreCase(name)) {
            return new Integer(component.getItemCount());
        } else if ("keyselectionmanager".equalsIgnoreCase(name)) {
            return component.getKeySelectionManager();
        } else if ("maximumrowcount".equalsIgnoreCase(name)) {
            return new Integer(component.getMaximumRowCount());
        } else if ("model".equalsIgnoreCase(name)) {
            return component.getModel();
        } else if ("prototypedisplayvalue".equalsIgnoreCase(name)) {
            return component.getPrototypeDisplayValue();
        } else if ("renderer".equalsIgnoreCase(name)) {
            return component.getRenderer();
        } else if ("selectedindex".equalsIgnoreCase(name)) {
            return new Integer(component.getSelectedIndex());
        } else if ("selecteditem".equalsIgnoreCase(name)) {
            return component.getSelectedItem();
        } else if ("selectedobjects".equalsIgnoreCase(name)) {
            return component.getSelectedObjects();
        } else if ("ui".equalsIgnoreCase(name)) {
            return component.getUI();
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected void addEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("action".equalsIgnoreCase(name)) {
            component.addActionListener((UIActionListener) listener);
        } else if ("item".equalsIgnoreCase(name)) {
            component.addItemListener((UIItemListener) listener);
        } else if ("popupmenu".equalsIgnoreCase(name)) {
            component.addPopupMenuListener((UIPopupMenuListener) listener);
        } else {
            super.addEventListenerImpl(name, listener);
        }
    }

    protected void removeEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("action".equalsIgnoreCase(name)) {
            component.removeActionListener((UIActionListener) listener);
        } else if ("item".equalsIgnoreCase(name)) {
            component.removeItemListener((UIItemListener) listener);
        } else if ("popupmenu".equalsIgnoreCase(name)) {
            component.removePopupMenuListener((UIPopupMenuListener) listener);
        } else {
            super.removeEventListenerImpl(name, listener);
        }
    }

    protected Object performActionImpl(String name, Object[] args) throws UIComponentException {
        if ("additem".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Object.class});
            component.addItem(args[0]);
            return null;
        } else if ("configureeditor".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {ComboBoxEditor.class, Object.class});
            component.configureEditor((ComboBoxEditor) args[0], args[1]);
            return null;
        } else if ("hidepopup".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.hidePopup();
            return null;
        } else if ("insertitemat".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Object.class, Number.class});
            component.insertItemAt(args[0], ((Number) args[1]).intValue());
            return null;
        } else if ("removeallitems".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.removeAllItems();
            return null;
        } else if ("removeitem".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Object.class});
            component.removeItem(args[0]);
            return null;
        } else if ("removeitemat".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            component.removeItemAt(((Number) args[0]).intValue());
            return null;
        } else if ("selectwithkeychar".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Character.class});
            return Boolean.valueOf(component.selectWithKeyChar(((Character) args[0]).charValue()));
        } else if ("showpopup".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.showPopup();
            return null;
        } else if ("getitemat".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            return component.getItemAt(((Number) args[0]).intValue());
        } else {
            return super.performActionImpl(name, args);
        }
    }
}
