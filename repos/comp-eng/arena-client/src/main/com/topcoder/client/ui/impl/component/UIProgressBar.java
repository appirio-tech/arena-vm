package com.topcoder.client.ui.impl.component;

import java.awt.Color;
import java.awt.GridBagConstraints;
import javax.swing.BoundedRangeModel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.plaf.ProgressBarUI;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;

public class UIProgressBar extends UISwingComponent {
    private JProgressBar component;

    protected Object createComponent() {
        return new JProgressBar();
    }

    protected void initialize() {
        super.initialize();
        component = (JProgressBar) getEventSource();
    }

    protected void addChildImpl(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        throw new UIComponentException("Progress bar is not a container.");
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("BorderPainted".equalsIgnoreCase(name)) {
            component.setBorderPainted(((Boolean) value).booleanValue());
        } else if ("Indeterminate".equalsIgnoreCase(name)) {
            component.setIndeterminate(((Boolean) value).booleanValue());
        } else if ("Maximum".equalsIgnoreCase(name)) {
            component.setMaximum(((Number) value).intValue());
        } else if ("Minimum".equalsIgnoreCase(name)) {
            component.setMinimum(((Number) value).intValue());
        } else if ("Model".equalsIgnoreCase(name)) {
            component.setModel((BoundedRangeModel) value);
        } else if ("Orientation".equalsIgnoreCase(name)) {
            component.setOrientation(((Number) value).intValue());
        } else if ("String".equalsIgnoreCase(name)) {
            component.setString((String) value);
        } else if ("StringPainted".equalsIgnoreCase(name)) {
            component.setStringPainted(((Boolean) value).booleanValue());
        } else if ("UI".equalsIgnoreCase(name)) {
            component.setUI((ProgressBarUI) value);
        } else if ("Value".equalsIgnoreCase(name)) {
            component.setValue(((Number) value).intValue());
        } else if ("SelectionBackground".equalsIgnoreCase(name)) {
            UIManager.put("ProgressBar.selectionBackground", (Color) value);
            component.setUI(component.getUI());
        } else if ("SelectionForeground".equalsIgnoreCase(name)) {
            UIManager.put("ProgressBar.selectionForeground", (Color) value);
            component.setUI(component.getUI());
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("Maximum".equalsIgnoreCase(name)) {
            return new Integer(component.getMaximum());
        } else if ("Minimum".equalsIgnoreCase(name)) {
            return new Integer(component.getMinimum());
        } else if ("Model".equalsIgnoreCase(name)) {
            return component.getModel();
        } else if ("Orientation".equalsIgnoreCase(name)) {
            return new Integer(component.getOrientation());
        } else if ("PercentComplete".equalsIgnoreCase(name)) {
            return new Double(component.getPercentComplete());
        } else if ("String".equalsIgnoreCase(name)) {
            return component.getString();
        } else if ("UI".equalsIgnoreCase(name)) {
            return component.getUI();
        } else if ("Value".equalsIgnoreCase(name)) {
            return new Integer(component.getValue());
        } else if ("BorderPainted".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isBorderPainted());
        } else if ("Indeterminate".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isIndeterminate());
        } else if ("StringPainted".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isStringPainted());
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected void addEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("Change".equalsIgnoreCase(name)) {
            component.addChangeListener((UIChangeListener) listener);
        } else {
            super.addEventListenerImpl(name, listener);
        }
    }

    protected void removeEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("Change".equalsIgnoreCase(name)) {
            component.removeChangeListener((UIChangeListener) listener);
        } else {
            super.removeEventListenerImpl(name, listener);
        }
    }
}
