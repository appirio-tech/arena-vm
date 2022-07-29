package com.topcoder.client.ui.impl.component;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.border.Border;
import javax.swing.JViewport;
import javax.swing.plaf.ViewportUI;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;

public class UIViewport extends UISwingComponent {
    private JViewport component;

    protected Object createComponent() {
        return new JViewport();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();
        component = (JViewport) getEventSource();
    }

    protected void addChildImpl(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        if (component.getEventSource() instanceof Component) {
            this.component.setView((Component) component.getEventSource());
        } else {
            throw new UIComponentException("This container does not support " + component.getClass() + ".");
        }
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("ExtentSize".equalsIgnoreCase(name)) {
            component.setExtentSize((Dimension) value);
        } else if ("ScrollMode".equalsIgnoreCase(name)) {
            component.setScrollMode(((Number) value).intValue());
        } else if ("UI".equalsIgnoreCase(name)) {
            component.setUI((ViewportUI) value);
        } else if ("View".equalsIgnoreCase(name)) {
            component.setView((Component) value);
        } else if ("ViewPosition".equalsIgnoreCase(name)) {
            component.setViewPosition((Point) value);
        } else if ("ViewSize".equalsIgnoreCase(name)) {
            component.setViewSize((Dimension) value);
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("ExtentSize".equalsIgnoreCase(name)) {
            return component.getExtentSize();
        } else if ("ScrollMode".equalsIgnoreCase(name)) {
            return new Integer(component.getScrollMode());
        } else if ("View".equalsIgnoreCase(name)) {
            return component.getView();
        } else if ("ViewPosition".equalsIgnoreCase(name)) {
            return component.getViewPosition();
        } else if ("ViewRect".equalsIgnoreCase(name)) {
            return component.getViewRect();
        } else if ("ViewSize".equalsIgnoreCase(name)) {
            return component.getViewSize();
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected void addEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("change".equalsIgnoreCase(name)) {
            component.addChangeListener((UIChangeListener) listener);
        } else {
            super.addEventListenerImpl(name, listener);
        }
    }

    protected void removeEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("change".equalsIgnoreCase(name)) {
            component.removeChangeListener((UIChangeListener) listener);
        } else {
            super.removeEventListenerImpl(name, listener);
        }
    }

    protected Object performActionImpl(String name, Object[] args) throws UIComponentException {
        if ("scrollRectToVisible".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Rectangle.class});
            component.scrollRectToVisible((Rectangle) args[0]);
            return null;
        } else if ("toViewCoordinates".equalsIgnoreCase(name)) {
            if (args.length == 1 && args[0] instanceof Dimension) {
                return component.toViewCoordinates((Dimension) args[0]);
            } else {
                assertArgs(name, args, new Class[] {Point.class});
                return component.toViewCoordinates((Point) args[0]);
            }
        } else {
            return super.performActionImpl(name, args);
        }
    }
}
