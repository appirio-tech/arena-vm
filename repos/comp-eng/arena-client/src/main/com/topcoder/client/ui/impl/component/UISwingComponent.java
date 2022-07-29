package com.topcoder.client.ui.impl.component;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Point;
import javax.swing.JComponent;
import javax.swing.border.Border;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;

public abstract class UISwingComponent extends UIContainer {
    private JComponent component;

    protected void initialize() throws UIComponentException {
        super.initialize();
        component = (JComponent) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("alignmentx".equalsIgnoreCase(name)) {
            component.setAlignmentX(((Number) value).floatValue());
        } else if ("alignmenty".equalsIgnoreCase(name)) {
            component.setAlignmentY(((Number) value).floatValue());
        } else if ("autoscrolls".equalsIgnoreCase(name)) {
            component.setAutoscrolls(((Boolean) value).booleanValue());
        } else if ("border".equalsIgnoreCase(name)) {
            component.setBorder((Border) value);
        } else if ("maximumsize".equalsIgnoreCase(name)) {
            component.setMaximumSize((Dimension) value);
        } else if ("minimumsize".equalsIgnoreCase(name)) {
            component.setMinimumSize((Dimension) value);
        } else if ("preferredsize".equalsIgnoreCase(name)) {
            component.setPreferredSize((Dimension) value);
        } else if ("preferredwidth".equalsIgnoreCase(name)) {
            Dimension dim = component.getPreferredSize();
            dim.width = ((Number) value).intValue();
            component.setPreferredSize(dim);
        } else if ("preferredheight".equalsIgnoreCase(name)) {
            Dimension dim = component.getPreferredSize();
            dim.height = ((Number) value).intValue();
            component.setPreferredSize(dim);
        } else if ("minimumwidth".equalsIgnoreCase(name)) {
            Dimension dim = component.getMinimumSize();
            dim.width = ((Number) value).intValue();
            component.setMinimumSize(dim);
        } else if ("minimumheight".equalsIgnoreCase(name)) {
            Dimension dim = component.getMinimumSize();
            dim.height = ((Number) value).intValue();
            component.setMinimumSize(dim);
        } else if ("maximumwidth".equalsIgnoreCase(name)) {
            Dimension dim = component.getMaximumSize();
            dim.width = ((Number) value).intValue();
            component.setMaximumSize(dim);
        } else if ("maximumheight".equalsIgnoreCase(name)) {
            Dimension dim = component.getMaximumSize();
            dim.height = ((Number) value).intValue();
            component.setMaximumSize(dim);
        } else if ("tooltiptext".equalsIgnoreCase(name)) {
            component.setToolTipText((String) value);
        } else if ("opaque".equalsIgnoreCase(name)) {
            component.setOpaque(((Boolean) value).booleanValue());
        } else if ("requestfocusenabled".equalsIgnoreCase(name)) {
            component.setRequestFocusEnabled(((Boolean) value).booleanValue());
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("autoscrolls".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.getAutoscrolls());
        } else if ("border".equalsIgnoreCase(name)) {
            return component.getBorder();
        } else if ("tooltiptext".equalsIgnoreCase(name)) {
            return component.getToolTipText();
        } else if ("opaque".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isOpaque());
        } else if ("requestfocusenabled".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isRequestFocusEnabled());
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected void addEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("ancestor".equalsIgnoreCase(name)) {
            component.addAncestorListener((UIAncestorListener) listener);
        } else if ("vetoablechange".equalsIgnoreCase(name)) {
            component.addVetoableChangeListener((UIVetoableChangeListener) listener);
        } else {
            super.addEventListenerImpl(name, listener);
        }
    }

    protected void removeEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("ancestor".equalsIgnoreCase(name)) {
            component.removeAncestorListener((UIAncestorListener) listener);
        } else if ("vetoablechange".equalsIgnoreCase(name)) {
            component.removeVetoableChangeListener((UIVetoableChangeListener) listener);
        } else {
            super.removeEventListenerImpl(name, listener);
        }
    }

    protected Object performActionImpl(String name, Object[] args) throws UIComponentException {
        if ("grabfocus".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.grabFocus();
            return null;
        } else if ("updateui".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.updateUI();
            return null;
        } else if ("revalidate".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.revalidate();
            return null;
        } else {
            return super.performActionImpl(name, args);
        }
    }
}
