package com.topcoder.client.ui.impl.component;

import java.io.IOException;
import java.net.URL;
import javax.swing.text.EditorKit;
import javax.swing.JEditorPane;

import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;

public class UIEditorPane extends UITextComponent {
    private JEditorPane component;

    protected Object createComponent() {
        return new JEditorPane();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();
        component = (JEditorPane) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("contenttype".equalsIgnoreCase(name)) {
            component.setContentType((String) value);
        } else if ("editorkit".equalsIgnoreCase(name)) {
            component.setEditorKit((EditorKit) value);
        } else if ("editorkitforcontenttype".equalsIgnoreCase(name)) {
            Object[] args = (Object[]) value;
            if ((args == null) || (args.length != 2)) {
                throw new UIComponentException("The property '" + name + "' needs two values.");
            }

            component.setEditorKitForContentType((String) args[0], (EditorKit) args[1]);
        } else if ("page".equalsIgnoreCase(name)) {
            try {
                if (value instanceof URL) {
                    component.setPage((URL) value);
                } else {
                    component.setPage((String) value);
                }
            } catch (IOException e) {
                throw new UIComponentException("I/O error occured.", e);
            }
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("contenttype".equalsIgnoreCase(name)) {
            return component.getContentType();
        } else if ("editorkit".equalsIgnoreCase(name)) {
            return component.getEditorKit();
        } else if ("page".equalsIgnoreCase(name)) {
            return component.getPage();
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected void addEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("hyperlink".equalsIgnoreCase(name)) {
            component.addHyperlinkListener((UIHyperlinkListener) listener);
        } else {
            super.addEventListenerImpl(name, listener);
        }
    }

    protected void removeEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("hyperlink".equalsIgnoreCase(name)) {
            component.removeHyperlinkListener((UIHyperlinkListener) listener);
        } else {
            super.removeEventListenerImpl(name, listener);
        }
    }

    protected Object performActionImpl(String name, Object[] args) throws UIComponentException {
        if ("scrolltoreference".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {String.class});
            component.scrollToReference((String) args[0]);
            return null;
        } else {
            return super.performActionImpl(name, args);
        }
    }
}
