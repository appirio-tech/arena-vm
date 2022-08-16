package com.topcoder.client.ui.impl.component;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import javax.swing.plaf.TextUI;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import javax.swing.text.NavigationFilter;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;

public abstract class UITextComponent extends UISwingComponent {
    private JTextComponent component;

    protected void initialize() throws UIComponentException {
        super.initialize();
        component = (JTextComponent) getEventSource();
    }

    protected void addChildImpl(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        throw new UIComponentException("Text component is not a container.");
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("caret".equalsIgnoreCase(name)) {
            component.setCaret((Caret) value);
        } else if ("caretcolor".equalsIgnoreCase(name)) {
            component.setCaretColor((Color) value);
        } else if ("caretposition".equalsIgnoreCase(name)) {
            component.setCaretPosition(((Number) value).intValue());
        } else if ("disabledtextcolor".equalsIgnoreCase(name)) {
            component.setDisabledTextColor((Color) value);
        } else if ("document".equalsIgnoreCase(name)) {
            component.setDocument((Document) value);
        } else if ("dragenabled".equalsIgnoreCase(name)) {
            component.setDragEnabled(((Boolean) value).booleanValue());
        } else if ("editable".equalsIgnoreCase(name)) {
            component.setEditable(((Boolean) value).booleanValue());
        } else if ("focusaccelerator".equalsIgnoreCase(name)) {
            component.setFocusAccelerator(((Character) value).charValue());
        } else if ("highlighter".equalsIgnoreCase(name)) {
            component.setHighlighter((Highlighter) value);
        } else if ("keymap".equalsIgnoreCase(name)) {
            component.setKeymap((Keymap) value);
        } else if ("margin".equalsIgnoreCase(name)) {
            component.setMargin((Insets) value);
        } else if ("navigationfilter".equalsIgnoreCase(name)) {
            component.setNavigationFilter((NavigationFilter) value);
        } else if ("selectedtextcolor".equalsIgnoreCase(name)) {
            component.setSelectedTextColor((Color) value);
        } else if ("selectioncolor".equalsIgnoreCase(name)) {
            component.setSelectionColor((Color) value);
        } else if ("selectionend".equalsIgnoreCase(name)) {
            component.setSelectionEnd(((Number) value).intValue());
        } else if ("selectionstart".equalsIgnoreCase(name)) {
            component.setSelectionStart(((Number) value).intValue());
        } else if ("text".equalsIgnoreCase(name)) {
            component.setText((String) value);
        } else if ("ui".equalsIgnoreCase(name)) {
            component.setUI((TextUI) value);
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("actions".equalsIgnoreCase(name)) {
            return component.getActions();
        } else if ("caret".equalsIgnoreCase(name)) {
            return component.getCaret();
        } else if ("caretcolor".equalsIgnoreCase(name)) {
            return component.getCaretColor();
        } else if ("caretposition".equalsIgnoreCase(name)) {
            return new Integer(component.getCaretPosition());
        } else if ("disabledtextcolor".equalsIgnoreCase(name)) {
            return component.getDisabledTextColor();
        } else if ("document".equalsIgnoreCase(name)) {
            return component.getDocument();
        } else if ("dragenabled".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.getDragEnabled());
        } else if ("focusaccelerator".equalsIgnoreCase(name)) {
            return Character.valueOf(component.getFocusAccelerator());
        } else if ("highlighter".equalsIgnoreCase(name)) {
            return component.getHighlighter();
        } else if ("keymap".equalsIgnoreCase(name)) {
            return component.getKeymap();
        } else if ("margin".equalsIgnoreCase(name)) {
            return component.getMargin();
        } else if ("navigationfilter".equalsIgnoreCase(name)) {
            return component.getNavigationFilter();
        } else if ("selectedtext".equalsIgnoreCase(name)) {
            return component.getSelectedText();
        } else if ("selectedtextcolor".equalsIgnoreCase(name)) {
            return component.getSelectedTextColor();
        } else if ("selectioncolor".equalsIgnoreCase(name)) {
            return component.getSelectionColor();
        } else if ("selectionstart".equalsIgnoreCase(name)) {
            return new Integer(component.getSelectionStart());
        } else if ("selectionend".equalsIgnoreCase(name)) {
            return new Integer(component.getSelectionEnd());
        } else if ("text".equalsIgnoreCase(name)) {
            return component.getText();
        } else if ("ui".equalsIgnoreCase(name)) {
            return component.getUI();
        } else if ("editable".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isEditable());
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected void addEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("caret".equalsIgnoreCase(name)) {
            component.addCaretListener((UICaretListener) listener);
        } else {
            super.addEventListenerImpl(name, listener);
        }
    }

    protected void removeEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("caret".equalsIgnoreCase(name)) {
            component.removeCaretListener((UICaretListener) listener);
        } else {
            super.removeEventListenerImpl(name, listener);
        }
    }

    protected Object performActionImpl(String name, Object[] args) throws UIComponentException {
        if ("copy".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.copy();
            return null;
        } else if ("cut".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.cut();
            return null;
        } else if ("paste".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.paste();
            return null;
        } else if ("selectall".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.selectAll();
            return null;
        } else if ("movecaretposition".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            component.moveCaretPosition(((Number) args[0]).intValue());
            return null;
        } else if ("read".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Reader.class, Object.class});
            try {
                component.read((Reader) args[0], args[1]);
            } catch (IOException e) {
                throw new UIComponentException("I/O error occured.", e);
            }
            return null;
        } else if ("replaceselection".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {String.class});
            component.replaceSelection((String) args[0]);
            return null;
        } else if ("select".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class, Number.class});
            component.select(((Number) args[0]).intValue(), ((Number) args[1]).intValue());
            return null;
        } else if ("viewtomodel".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Point.class});
            return new Integer(component.viewToModel((Point) args[0]));
        } else if ("write".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Writer.class});
            try {
                component.write((Writer) args[0]);
            } catch (IOException e) {
                throw new UIComponentException("I/O error occured.", e);
            }
            return null;
        } else {
            return super.performActionImpl(name, args);
        }
    }
}
