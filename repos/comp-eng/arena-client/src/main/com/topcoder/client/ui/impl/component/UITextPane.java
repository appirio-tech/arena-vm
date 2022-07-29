package com.topcoder.client.ui.impl.component;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;

public class UITextPane extends UIEditorPane {
    private JTextPane component;

    protected Object createComponent() {
        return new JTextPane();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();
        component = (JTextPane) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("LogicalStyle".equalsIgnoreCase(name)) {
            component.setLogicalStyle((Style) value);
        } else if ("StyledDocument".equalsIgnoreCase(name)) {
            component.setStyledDocument((StyledDocument) value);
        } else if ("CharacterAttributes".equalsIgnoreCase(name)) {
            component.setCharacterAttributes((AttributeSet) value, false);
        } else if ("CharacterAttributesReplace".equalsIgnoreCase(name)) {
            component.setCharacterAttributes((AttributeSet) value, true);
        } else if ("ParagraphAttributes".equalsIgnoreCase(name)) {
            component.setParagraphAttributes((AttributeSet) value, false);
        } else if ("ParagraphAttributesReplace".equalsIgnoreCase(name)) {
            component.setParagraphAttributes((AttributeSet) value, true);
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("LogicalStyle".equalsIgnoreCase(name)) {
            return component.getLogicalStyle();
        } else if ("CharacterAttributes".equalsIgnoreCase(name)) {
            return component.getCharacterAttributes();
        } else if ("InputAttributes".equalsIgnoreCase(name)) {
            return component.getInputAttributes();
        } else if ("ParagraphAttributes".equalsIgnoreCase(name)) {
            return component.getParagraphAttributes();
        } else if ("StyledDocument".equalsIgnoreCase(name)) {
            return component.getStyledDocument();
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected Object performActionImpl(String name, Object[] args) throws UIComponentException {
        if ("addStyle".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {String.class, Style.class});
            return component.addStyle((String) args[0], (Style) args[1]);
        } else if ("getStyle".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {String.class});
            return component.getStyle((String) args[0]);
        } else if ("insertComponent".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Component.class});
            component.insertComponent((Component) args[0]);
            return null;
        } else if ("insertIcon".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Icon.class});
            component.insertIcon((Icon) args[0]);
            return null;
        } else {
            return super.performActionImpl(name, args);
        }
    }
}
