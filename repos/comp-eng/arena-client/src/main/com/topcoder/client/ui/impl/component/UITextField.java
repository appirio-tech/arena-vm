package com.topcoder.client.ui.impl.component;

import java.awt.Rectangle;
import javax.swing.Action;
import javax.swing.JTextField;

import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;

public class UITextField extends UITextComponent {
    private JTextField component;

    protected Object createComponent() {
        return new JTextField();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();
        component = (JTextField) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("action".equalsIgnoreCase(name)) {
            component.setAction((Action) value);
        } else if ("actioncommand".equalsIgnoreCase(name)) {
            component.setActionCommand((String) value);
        } else if ("columns".equalsIgnoreCase(name)) {
            component.setColumns(((Number) value).intValue());
        } else if ("horizontalalignment".equalsIgnoreCase(name)) {
            component.setHorizontalAlignment(((Number) value).intValue());
        } else if ("scrolloffset".equalsIgnoreCase(name)) {
            component.setScrollOffset(((Number) value).intValue());
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("action".equalsIgnoreCase(name)) {
            return component.getAction();
        } else if ("columns".equalsIgnoreCase(name)) {
            return new Integer(component.getColumns());
        } else if ("horizontalalignment".equalsIgnoreCase(name)) {
            return new Integer(component.getHorizontalAlignment());
        } else if ("horizontalvisibility".equalsIgnoreCase(name)) {
            return component.getHorizontalVisibility();
        } else if ("scrolloffset".equalsIgnoreCase(name)) {
            return new Integer(component.getScrollOffset());
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected void addEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("action".equalsIgnoreCase(name)) {
            component.addActionListener((UIActionListener) listener);
        } else {
            super.addEventListenerImpl(name, listener);
        }
    }

    protected void removeEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("action".equalsIgnoreCase(name)) {
            component.removeActionListener((UIActionListener) listener);
        } else {
            super.removeEventListenerImpl(name, listener);
        }
    }

    protected Object performActionImpl(String name, Object[] args) throws UIComponentException {
        if ("scrollrecttovisible".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Rectangle.class});
            component.scrollRectToVisible((Rectangle) args[0]);
            return null;
        } else {
            return super.performActionImpl(name, args);
        }
    }
}
