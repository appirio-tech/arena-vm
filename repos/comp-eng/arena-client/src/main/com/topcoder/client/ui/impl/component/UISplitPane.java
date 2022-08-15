package com.topcoder.client.ui.impl.component;

import java.awt.Component;
import java.awt.GridBagConstraints;
import javax.swing.JSplitPane;
import javax.swing.plaf.SplitPaneUI;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;

public class UISplitPane extends UISwingComponent {
    private JSplitPane component;

    protected Object createComponent() {
        JSplitPane pane = new JSplitPane();
        pane.setLeftComponent(null);
        pane.setRightComponent(null);
        return pane;
    }

    protected void initialize() throws UIComponentException {
        super.initialize();

        component = (JSplitPane) getEventSource();
    }

    protected void addChildImpl(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        if (component.getEventSource() instanceof Component) {
            Component comp = (Component) component.getEventSource();
            if (this.component.getLeftComponent() == null) {
                this.component.setLeftComponent(comp);
            } else {
                this.component.setRightComponent(comp);
            }
        } else {
            throw new UIComponentException("Split pane does not support the component.");
        }
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("BottomComponent".equalsIgnoreCase(name)) {
            component.setBottomComponent((Component) value);
        } else if ("ContinuousLayout".equalsIgnoreCase(name)) {
            component.setContinuousLayout(((Boolean) value).booleanValue());
        } else if ("DividerLocationPercentage".equalsIgnoreCase(name)) {
            component.setDividerLocation(((Number) value).doubleValue());
        } else if ("DividerLocation".equalsIgnoreCase(name)) {
            component.setDividerLocation(((Number) value).intValue());
        } else if ("DividerSize".equalsIgnoreCase(name)) {
            component.setDividerSize(((Number) value).intValue());
        } else if ("LastDividerLocation".equalsIgnoreCase(name)) {
            component.setLastDividerLocation(((Number) value).intValue());
        } else if ("LeftComponent".equalsIgnoreCase(name)) {
            component.setLeftComponent((Component) value);
        } else if ("OneTouchExpandable".equalsIgnoreCase(name)) {
            component.setOneTouchExpandable(((Boolean) value).booleanValue());
        } else if ("Orientation".equalsIgnoreCase(name)) {
            component.setOrientation(((Number) value).intValue());
        } else if ("ResizeWeight".equalsIgnoreCase(name)) {
            component.setResizeWeight(((Number) value).doubleValue());
        } else if ("RightComponent".equalsIgnoreCase(name)) {
            component.setRightComponent((Component) value);
        } else if ("TopComponent".equalsIgnoreCase(name)) {
            component.setTopComponent((Component) value);
        } else if ("UI".equalsIgnoreCase(name)) {
            component.setUI((SplitPaneUI) value);
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("BottomComponent".equalsIgnoreCase(name)) {
            return component.getBottomComponent();
        } else if ("DividerLocation".equalsIgnoreCase(name)) {
            return new Integer(component.getDividerLocation());
        } else if ("DividerSize".equalsIgnoreCase(name)) {
            return new Integer(component.getDividerSize());
        } else if ("LastDividerLocation".equalsIgnoreCase(name)) {
            return new Integer(component.getLastDividerLocation());
        } else if ("LeftComponent".equalsIgnoreCase(name)) {
            return component.getLeftComponent();
        } else if ("MaximumDividerLocation".equalsIgnoreCase(name)) {
            return new Integer(component.getMaximumDividerLocation());
        } else if ("MinimumDividerLocation".equalsIgnoreCase(name)) {
            return new Integer(component.getMinimumDividerLocation());
        } else if ("Orientation".equalsIgnoreCase(name)) {
            return new Integer(component.getOrientation());
        } else if ("ResizeWeight".equalsIgnoreCase(name)) {
            return new Double(component.getResizeWeight());
        } else if ("RightComponent".equalsIgnoreCase(name)) {
            return component.getRightComponent();
        } else if ("TopComponent".equalsIgnoreCase(name)) {
            return component.getTopComponent();
        } else if ("UI".equalsIgnoreCase(name)) {
            return component.getUI();
        } else if ("ContinuousLayout".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isContinuousLayout());
        } else if ("OneTouchExpandable".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isOneTouchExpandable());
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected Object performActionImpl(String name, Object[] args) throws UIComponentException {
        if ("resetToPreferredSizes".equalsIgnoreCase(name)) {
            assertNull(name, args);
            component.resetToPreferredSizes();
            return null;
        } else {
            return super.performActionImpl(name, args);
        }
    }
}
