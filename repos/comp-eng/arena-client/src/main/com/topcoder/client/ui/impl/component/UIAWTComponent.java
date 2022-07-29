package com.topcoder.client.ui.impl.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;

public abstract class UIAWTComponent extends UIAbstractComponent {
    private Component component;

    protected void initialize() throws UIComponentException {
        super.initialize();

        component = (Component) getEventSource();
    }

    protected void destroyImpl() {
        component.removeNotify();
        super.destroyImpl();
    }

    protected void addChildImpl(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        throw new UIComponentException("AWT Component is not a container.");
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("enabled".equalsIgnoreCase(name)) {
            component.setEnabled(((Boolean) value).booleanValue());
        } else if ("visible".equalsIgnoreCase(name)) {
            component.setVisible(((Boolean) value).booleanValue());
        } else if ("background".equalsIgnoreCase(name)) {
            component.setBackground((Color) value);
        } else if ("foreground".equalsIgnoreCase(name)) {
            component.setForeground((Color) value);
        } else if ("font".equalsIgnoreCase(name)) {
            component.setFont((Font) value);
        } else if ("focusable".equalsIgnoreCase(name)) {
            component.setFocusable(((Boolean) value).booleanValue());
        } else if ("location".equalsIgnoreCase(name)) {
            component.setLocation((Point) value);
        } else if ("size".equalsIgnoreCase(name)) {
            component.setSize((Dimension) value);
        } else if ("cursor".equalsIgnoreCase(name)) {
            component.setCursor((Cursor) value);
        } else if ("name".equalsIgnoreCase(name)) {
            component.setName((String) value);
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("enabled".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isEnabled());
        } else if ("visible".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isVisible());
        } else if ("alignmentx".equalsIgnoreCase(name)) {
            return new Float(component.getAlignmentX());
        } else if ("alignmenty".equalsIgnoreCase(name)) {
            return new Float(component.getAlignmentY());
        } else if ("background".equalsIgnoreCase(name)) {
            return component.getBackground();
        } else if ("foreground".equalsIgnoreCase(name)) {
            return component.getForeground();
        } else if ("font".equalsIgnoreCase(name)) {
            return component.getFont();
        } else if ("focusable".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isFocusable());
        } else if ("location".equalsIgnoreCase(name)) {
            return component.getLocation();
        } else if ("locationonscreen".equalsIgnoreCase(name)) {
            return component.getLocationOnScreen();
        } else if ("name".equalsIgnoreCase(name)) {
            return component.getName();
        } else if ("parent".equalsIgnoreCase(name)) {
            return component.getParent();
        } else if ("focusowner".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isFocusOwner());
        } else if ("graphics".equalsIgnoreCase(name)) {
            return component.getGraphics();
        } else if ("showing".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isShowing());
        } else if ("maximumsize".equalsIgnoreCase(name)) {
            return component.getMaximumSize();
        } else if ("minimumsize".equalsIgnoreCase(name)) {
            return component.getMinimumSize();
        } else if ("preferredsize".equalsIgnoreCase(name)) {
            return component.getPreferredSize();
        } else if ("size".equalsIgnoreCase(name)) {
            return component.getSize();
        } else if ("cursor".equalsIgnoreCase(name)) {
            return component.getCursor();
        } else if ("x".equalsIgnoreCase(name)) {
            return new Integer(component.getX());
        } else if ("y".equalsIgnoreCase(name)) {
            return new Integer(component.getY());
        } else if ("width".equalsIgnoreCase(name)) {
            return new Integer(component.getWidth());
        } else if ("height".equalsIgnoreCase(name)) {
            return new Integer(component.getHeight());
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected void addEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("propertychange".equalsIgnoreCase(name)) {
            component.addPropertyChangeListener((UIPropertyChangeListener) listener);
        } else if ("component".equalsIgnoreCase(name)) {
            component.addComponentListener((UIComponentListener) listener);
        } else if ("focus".equalsIgnoreCase(name)) {
            component.addFocusListener((UIFocusListener) listener);
        } else if ("hierarchybounds".equalsIgnoreCase(name)) {
            component.addHierarchyBoundsListener((UIHierarchyBoundsListener) listener);
        } else if ("hierarchy".equalsIgnoreCase(name)) {
            component.addHierarchyListener((UIHierarchyListener) listener);
        } else if ("inputmethod".equalsIgnoreCase(name)) {
            component.addInputMethodListener((UIInputMethodListener) listener);
        } else if ("key".equalsIgnoreCase(name)) {
            component.addKeyListener((UIKeyListener) listener);
        } else if ("mouse".equalsIgnoreCase(name)) {
            component.addMouseListener((UIMouseListener) listener);
        } else if ("mousemotion".equalsIgnoreCase(name)) {
            component.addMouseMotionListener((UIMouseMotionListener) listener);
        } else if ("mousewheel".equalsIgnoreCase(name)) {
            component.addMouseWheelListener((UIMouseWheelListener) listener);
        } else {
            super.addEventListenerImpl(name, listener);
        }
    }

    protected void removeEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("propertychange".equalsIgnoreCase(name)) {
            component.removePropertyChangeListener((UIPropertyChangeListener) listener);
        } else if ("component".equalsIgnoreCase(name)) {
            component.removeComponentListener((UIComponentListener) listener);
        } else if ("focus".equalsIgnoreCase(name)) {
            component.removeFocusListener((UIFocusListener) listener);
        } else if ("hierarchybounds".equalsIgnoreCase(name)) {
            component.removeHierarchyBoundsListener((UIHierarchyBoundsListener) listener);
        } else if ("hierarchy".equalsIgnoreCase(name)) {
            component.removeHierarchyListener((UIHierarchyListener) listener);
        } else if ("inputmethod".equalsIgnoreCase(name)) {
            component.removeInputMethodListener((UIInputMethodListener) listener);
        } else if ("key".equalsIgnoreCase(name)) {
            component.removeKeyListener((UIKeyListener) listener);
        } else if ("mouse".equalsIgnoreCase(name)) {
            component.removeMouseListener((UIMouseListener) listener);
        } else if ("mousemotion".equalsIgnoreCase(name)) {
            component.removeMouseMotionListener((UIMouseMotionListener) listener);
        } else if ("mousewheel".equalsIgnoreCase(name)) {
            component.removeMouseWheelListener((UIMouseWheelListener) listener);
        } else {
            super.removeEventListenerImpl(name, listener);
        }
    }

    protected Object performActionImpl(String name, Object[] args) throws UIComponentException {
        if ("repaint".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.repaint();
            return null;
        } else if ("requestfocus".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.requestFocus();
            return null;
        } else if ("transferfocus".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.transferFocus();
            return null;
        } else if ("transferfocusbackward".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.transferFocusBackward();
            return null;
        } else if ("transferfocusupcycle".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.transferFocusUpCycle();
            return null;
        } else if ("invalidate".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.invalidate();
            return null;
        } else if ("getfontmetrics".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {Font.class});
            return component.getFontMetrics((Font) args[0]);
        } else {
            return super.performActionImpl(name, args);
        }
    }
}
