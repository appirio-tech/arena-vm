package com.topcoder.client.ui.impl.component;

import java.awt.Component;
import java.awt.GridBagConstraints;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.plaf.LabelUI;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;

public class UILabel extends UISwingComponent {
    private JLabel component;

    protected Object createComponent() {
        return new JLabel();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();
        component = (JLabel) getEventSource();
    }

    protected void addChildImpl(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        throw new UIComponentException("Label is not a container.");
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("text".equalsIgnoreCase(name)) {
            component.setText((String) value);
        } else if ("icon".equalsIgnoreCase(name)) {
            component.setIcon((Icon) value);
        } else if ("disabledicon".equalsIgnoreCase(name)) {
            component.setDisabledIcon((Icon) value);
        } else if ("displayedmnemonic".equalsIgnoreCase(name)) {
            if (value instanceof Character) {
                component.setDisplayedMnemonic(((Character) value).charValue());
            } else {
                component.setDisplayedMnemonic(((Number) value).intValue());
            }
        } else if ("displayedmnemonicindex".equalsIgnoreCase(name)) {
            component.setDisplayedMnemonicIndex(((Number) value).intValue());
        } else if ("horizontalalignment".equalsIgnoreCase(name)) {
            component.setHorizontalAlignment(((Number) value).intValue());
        } else if ("horizontaltextposition".equalsIgnoreCase(name)) {
            component.setHorizontalTextPosition(((Number) value).intValue());
        } else if ("icontextgap".equalsIgnoreCase(name)) {
            component.setIconTextGap(((Number) value).intValue());
        } else if ("labelfor".equalsIgnoreCase(name)) {
            component.setLabelFor((Component) value);
        } else if ("ui".equalsIgnoreCase(name)) {
            component.setUI((LabelUI) value);
        } else if ("verticalalignment".equalsIgnoreCase(name)) {
            component.setVerticalAlignment(((Number) value).intValue());
        } else if ("verticaltextposition".equalsIgnoreCase(name)) {
            component.setVerticalTextPosition(((Number) value).intValue());
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("text".equalsIgnoreCase(name)) {
            return component.getText();
        } else if ("icon".equalsIgnoreCase(name)) {
            return component.getIcon();
        } else if ("disabledicon".equalsIgnoreCase(name)) {
            return component.getDisabledIcon();
        } else if ("displayedmnemonic".equalsIgnoreCase(name)) {
            return new Integer(component.getDisplayedMnemonic());
        } else if ("displayedmnemonicindex".equalsIgnoreCase(name)) {
            return new Integer(component.getDisplayedMnemonicIndex());
        } else if ("horizontalalignment".equalsIgnoreCase(name)) {
            return new Integer(component.getHorizontalAlignment());
        } else if ("horizontaltextposition".equalsIgnoreCase(name)) {
            return new Integer(component.getHorizontalTextPosition());
        } else if ("icontextgap".equalsIgnoreCase(name)) {
            return new Integer(component.getIconTextGap());
        } else if ("labelfor".equalsIgnoreCase(name)) {
            return component.getLabelFor();
        } else if ("ui".equalsIgnoreCase(name)) {
            return component.getUI();
        } else if ("verticalalignment".equalsIgnoreCase(name)) {
            return new Integer(component.getVerticalAlignment());
        } else if ("verticaltextposition".equalsIgnoreCase(name)) {
            return new Integer(component.getVerticalTextPosition());
        } else {
            return super.getPropertyImpl(name);
        }
    }
}
