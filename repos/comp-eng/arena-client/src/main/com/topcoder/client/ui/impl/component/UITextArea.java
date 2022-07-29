package com.topcoder.client.ui.impl.component;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;

public class UITextArea extends UITextComponent {
    private JTextArea component;

    protected Object createComponent() {
        return new JTextArea();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();
        component = (JTextArea) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("columns".equalsIgnoreCase(name)) {
            component.setColumns(((Number) value).intValue());
        } else if ("linewrap".equalsIgnoreCase(name)) {
            component.setLineWrap(((Boolean) value).booleanValue());
        } else if ("rows".equalsIgnoreCase(name)) {
            component.setRows(((Number) value).intValue());
        } else if ("tabsize".equalsIgnoreCase(name)) {
            component.setTabSize(((Number) value).intValue());
        } else if ("wrapstyleword".equalsIgnoreCase(name)) {
            component.setWrapStyleWord(((Boolean) value).booleanValue());
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("columns".equalsIgnoreCase(name)) {
            return new Integer(component.getColumns());
        } else if ("linecount".equalsIgnoreCase(name)) {
            return new Integer(component.getLineCount());
        } else if ("linewrap".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.getLineWrap());
        } else if ("rows".equalsIgnoreCase(name)) {
            return new Integer(component.getRows());
        } else if ("tabsize".equalsIgnoreCase(name)) {
            return new Integer(component.getTabSize());
        } else if ("wrapstyleword".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.getWrapStyleWord());
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected Object performActionImpl(String name, Object[] args) throws UIComponentException {
        if ("append".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {String.class});
            component.append((String) args[0]);
            return null;
        } else if ("lineendoffset".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            try {
                return new Integer(component.getLineEndOffset(((Number) args[0]).intValue()));
            } catch (BadLocationException e) {
                throw new UIComponentException("The location given is invalid.", e);
            }
        } else if ("lineofoffset".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            try {
                return new Integer(component.getLineOfOffset(((Number) args[0]).intValue()));
            } catch (BadLocationException e) {
                throw new UIComponentException("The location given is invalid.", e);
            }
        } else if ("linestartoffset".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Number.class});
            try {
                return new Integer(component.getLineStartOffset(((Number) args[0]).intValue()));
            } catch (BadLocationException e) {
                throw new UIComponentException("The location given is invalid.", e);
            }
        } else if ("insert".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {String.class, Number.class});
            component.insert((String) args[0], ((Number) args[1]).intValue());
            return null;
        } else if ("replacerange".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {String.class, Number.class, Number.class});
            component.replaceRange((String) args[0], ((Number) args[1]).intValue(), ((Number) args[2]).intValue());
            return null;
        } else {
            return super.performActionImpl(name, args);
        }
    }
}
