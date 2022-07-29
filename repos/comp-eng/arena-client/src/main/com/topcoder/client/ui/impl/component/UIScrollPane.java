package com.topcoder.client.ui.impl.component;

import java.awt.Component;
import java.awt.GridBagConstraints;
import javax.swing.border.Border;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.plaf.ScrollPaneUI;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;

public class UIScrollPane extends UISwingComponent {
    private JScrollPane component;

    protected Object createComponent() {
        return new JScrollPane();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();
        component = (JScrollPane) getEventSource();
    }

    protected void addChildImpl(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        if (component.getEventSource() instanceof JViewport) {
            this.component.setViewport((JViewport) component.getEventSource());
        } else if (component.getEventSource() instanceof Component) {
            this.component.setViewportView((Component) component.getEventSource());
        } else {
            throw new UIComponentException("This container does not support " + component.getClass() + ".");
        }
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("columnheader".equalsIgnoreCase(name)) {
            component.setColumnHeader((JViewport) value);
        } else if ("columnheaderview".equalsIgnoreCase(name)) {
            component.setColumnHeaderView((Component) value);
        } else if ("corner_lowerleft".equalsIgnoreCase(name)) {
            component.setCorner(JScrollPane.LOWER_LEFT_CORNER, (Component) value);
        } else if ("corner_lowerright".equalsIgnoreCase(name)) {
            component.setCorner(JScrollPane.LOWER_RIGHT_CORNER, (Component) value);
        } else if ("corner_upperleft".equalsIgnoreCase(name)) {
            component.setCorner(JScrollPane.UPPER_LEFT_CORNER, (Component) value);
        } else if ("corner_upperleft".equalsIgnoreCase(name)) {
            component.setCorner(JScrollPane.UPPER_RIGHT_CORNER, (Component) value);
        } else if ("horizontalscrollbar".equalsIgnoreCase(name)) {
            component.setHorizontalScrollBar((JScrollBar) value);
        } else if ("horizontalscrollbarpolicy".equalsIgnoreCase(name)) {
            component.setHorizontalScrollBarPolicy(((Number) value).intValue());
        } else if ("rowheader".equalsIgnoreCase(name)) {
            component.setRowHeader((JViewport) value);
        } else if ("rowheaderview".equalsIgnoreCase(name)) {
            component.setRowHeaderView((Component) value);
        } else if ("ui".equalsIgnoreCase(name)) {
            component.setUI((ScrollPaneUI) value);
        } else if ("verticalscrollbar".equalsIgnoreCase(name)) {
            component.setVerticalScrollBar((JScrollBar) value);
        } else if ("verticalscrollbarpolicy".equalsIgnoreCase(name)) {
            component.setVerticalScrollBarPolicy(((Number) value).intValue());
        } else if ("viewport".equalsIgnoreCase(name)) {
            component.setViewport((JViewport) value);
        } else if ("viewportborder".equalsIgnoreCase(name)) {
            component.setViewportBorder((Border) value);
        } else if ("viewportview".equalsIgnoreCase(name)) {
            component.setViewportView((Component) value);
        } else if ("wheelscrollingenabled".equalsIgnoreCase(name)) {
            component.setWheelScrollingEnabled(((Boolean) value).booleanValue());
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("columnheader".equalsIgnoreCase(name)) {
            return component.getColumnHeader();
        } else if ("corner_lowerleft".equalsIgnoreCase(name)) {
            return component.getCorner(JScrollPane.LOWER_LEFT_CORNER);
        } else if ("corner_lowerright".equalsIgnoreCase(name)) {
            return component.getCorner(JScrollPane.LOWER_RIGHT_CORNER);
        } else if ("corner_upperleft".equalsIgnoreCase(name)) {
            return component.getCorner(JScrollPane.UPPER_LEFT_CORNER);
        } else if ("corner_upperright".equalsIgnoreCase(name)) {
            return component.getCorner(JScrollPane.UPPER_RIGHT_CORNER);
        } else if ("horizontalscrollbar".equalsIgnoreCase(name)) {
            return component.getHorizontalScrollBar();
        } else if ("horizontalscrollbarpolicy".equalsIgnoreCase(name)) {
            return new Integer(component.getHorizontalScrollBarPolicy());
        } else if ("rowheader".equalsIgnoreCase(name)) {
            return component.getRowHeader();
        } else if ("ui".equalsIgnoreCase(name)) {
            return component.getUI();
        } else if ("verticalscrollbar".equalsIgnoreCase(name)) {
            return component.getVerticalScrollBar();
        } else if ("verticalscrollbarpolicy".equalsIgnoreCase(name)) {
            return new Integer(component.getVerticalScrollBarPolicy());
        } else if ("viewport".equalsIgnoreCase(name)) {
            return component.getViewport();
        } else if ("viewportborder".equalsIgnoreCase(name)) {
            return component.getViewportBorder();
        } else if ("viewportborderbounds".equalsIgnoreCase(name)) {
            return component.getViewportBorderBounds();
        } else if ("wheelscrollingenabled".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isWheelScrollingEnabled());
        } else {
            return super.getPropertyImpl(name);
        }
    }
}
