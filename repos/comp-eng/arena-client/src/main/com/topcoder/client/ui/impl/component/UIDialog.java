package com.topcoder.client.ui.impl.component;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuBar;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;

public class UIDialog extends UIAWTDialog {
    private JDialog component;

    protected Object createComponent() {
        JDialog dialog;

        if (properties.get("owner") instanceof Dialog) {
            dialog = new JDialog((Dialog) properties.get("owner"));
        } else {
            dialog = new JDialog((Frame) properties.get("owner"));
        }

        dialog.getContentPane().setLayout(new GridBagLayout());

        return dialog;
    }

    protected void initialize() throws UIComponentException {
        try {
            super.initialize();
        } catch (ClassCastException e) {
            throw new UIComponentException("The owner must be a frame or a dialog.");
        }

        component = (JDialog) getEventSource();
    }

    protected void addChildImpl(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        if (component.getEventSource() instanceof Component) {
            this.component.getContentPane().add((Component) component.getEventSource(), constraints);
        } else {
            throw new UIComponentException("JDialog does not support component class " + component.getClass() + ".");
        }
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("defaultlookandfeeldecorated".equalsIgnoreCase(name)) {
            component.setDefaultLookAndFeelDecorated(((Boolean) value).booleanValue());
        } else if ("defaultcloseoperation".equalsIgnoreCase(name)) {
            component.setDefaultCloseOperation(((Number) value).intValue());
        } else if ("jmenubar".equalsIgnoreCase(name)) {
            component.setJMenuBar((JMenuBar) value);
        } else if ("defaultbutton".equalsIgnoreCase(name)) {
            component.getRootPane().setDefaultButton((JButton) value);
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("contentpane".equalsIgnoreCase(name)) {
            return component.getContentPane();
        } else if ("defaultcloseoperation".equalsIgnoreCase(name)) {
            return new Integer(component.getDefaultCloseOperation());
        } else if ("defaultlookandfeeldecorated".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isDefaultLookAndFeelDecorated());
        } else if ("jmenubar".equalsIgnoreCase(name)) {
            return component.getJMenuBar();
        } else if ("rootpane".equalsIgnoreCase(name)) {
            return component.getRootPane();
        } else {
            return super.getPropertyImpl(name);
        }
    }
}
