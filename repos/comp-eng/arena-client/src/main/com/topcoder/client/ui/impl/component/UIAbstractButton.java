package com.topcoder.client.ui.impl.component;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.plaf.ButtonUI;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;

public abstract class UIAbstractButton extends UISwingComponent {
    private AbstractButton component;

    protected void initialize() throws UIComponentException {
        super.initialize();

        component = (AbstractButton) getEventSource();
    }

    protected void addChildImpl(UIComponent component, GridBagConstraints contraints) throws UIComponentException {
        throw new UIComponentException("Button is not a container.");
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("action".equalsIgnoreCase(name)) {
            component.setAction((Action) value);
        } else if ("actioncommand".equalsIgnoreCase(name)) {
            component.setActionCommand((String) value);
        } else if ("borderpainted".equalsIgnoreCase(name)) {
            component.setBorderPainted(((Boolean) value).booleanValue());
        } else if ("contentareafilled".equalsIgnoreCase(name)) {
            component.setContentAreaFilled(((Boolean) value).booleanValue());
        } else if ("disabledicon".equalsIgnoreCase(name)) {
            component.setDisabledIcon((Icon) value);
        } else if ("disabledselectedicon".equalsIgnoreCase(name)) {
            component.setDisabledSelectedIcon((Icon) value);
        } else if ("displayedmnemonicindex".equalsIgnoreCase(name)) {
            component.setDisplayedMnemonicIndex(((Number) value).intValue());
        } else if ("enabled".equalsIgnoreCase(name)) {
            component.setEnabled(((Boolean) value).booleanValue());
        } else if ("focuspainted".equalsIgnoreCase(name)) {
            component.setFocusPainted(((Boolean) value).booleanValue());
        } else if ("horizontalalignment".equalsIgnoreCase(name)) {
            component.setHorizontalAlignment(((Number) value).intValue());
        } else if ("horizontaltextposition".equalsIgnoreCase(name)) {
            component.setHorizontalTextPosition(((Number) value).intValue());
        } else if ("icon".equalsIgnoreCase(name)) {
            component.setIcon((Icon) value);
        } else if ("icontextgap".equalsIgnoreCase(name)) {
            component.setIconTextGap(((Number) value).intValue());
        } else if ("margin".equalsIgnoreCase(name)) {
            component.setMargin((Insets) value);
        } else if ("mnemonic".equalsIgnoreCase(name)) {
            if (value instanceof Character) {
                component.setMnemonic(((Character) value).charValue());
            } else {
                component.setMnemonic(((Number) value).intValue());
            }
        } else if ("multiclickthreshhold".equalsIgnoreCase(name)) {
            component.setMultiClickThreshhold(((Number) value).longValue());
        } else if ("pressedicon".equalsIgnoreCase(name)) {
            component.setPressedIcon((Icon) value);
        } else if ("rolloverenabled".equalsIgnoreCase(name)) {
            component.setRolloverEnabled(((Boolean) value).booleanValue());
        } else if ("rollovericon".equalsIgnoreCase(name)) {
            component.setRolloverIcon((Icon) value);
        } else if ("rolloverselectedicon".equalsIgnoreCase(name)) {
            component.setRolloverSelectedIcon((Icon) value);
        } else if ("selected".equalsIgnoreCase(name)) {
            component.setSelected(((Boolean) value).booleanValue());
        } else if ("selectedicon".equalsIgnoreCase(name)) {
            component.setSelectedIcon((Icon) value);
        } else if ("text".equalsIgnoreCase(name)) {
            component.setText((String) value);
        } else if ("ui".equalsIgnoreCase(name)) {
            component.setUI((ButtonUI) value);
        } else if ("verticalalignment".equalsIgnoreCase(name)) {
            component.setVerticalAlignment(((Number) value).intValue());
        } else if ("verticaltextposition".equalsIgnoreCase(name)) {
            component.setVerticalTextPosition(((Number) value).intValue());
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("action".equalsIgnoreCase(name)) {
            return component.getAction();
        } else if ("actioncommand".equalsIgnoreCase(name)) {
            return component.getActionCommand();
        } else if ("disabledicon".equalsIgnoreCase(name)) {
            return component.getDisabledIcon();
        } else if ("disabledselectedicon".equalsIgnoreCase(name)) {
            return component.getDisabledSelectedIcon();
        } else if ("displayedmnemonicindex".equalsIgnoreCase(name)) {
            return new Integer(component.getDisplayedMnemonicIndex());
        } else if ("horizontalalignment".equalsIgnoreCase(name)) {
            return new Integer(component.getHorizontalAlignment());
        } else if ("horizontaltextposition".equalsIgnoreCase(name)) {
            return new Integer(component.getHorizontalTextPosition());
        } else if ("icon".equalsIgnoreCase(name)) {
            return component.getIcon();
        } else if ("icontextgap".equalsIgnoreCase(name)) {
            return new Integer(component.getIconTextGap());
        } else if ("margin".equalsIgnoreCase(name)) {
            return component.getMargin();
        } else if ("mnemonic".equalsIgnoreCase(name)) {
            return new Integer(component.getMnemonic());
        } else if ("model".equalsIgnoreCase(name)) {
            return component.getModel();
        } else if ("multiclickthreshhold".equalsIgnoreCase(name)) {
            return Long.valueOf(component.getMultiClickThreshhold());
        } else if ("pressedicon".equalsIgnoreCase(name)) {
            return component.getPressedIcon();
        } else if ("rollovericon".equalsIgnoreCase(name)) {
            return component.getRolloverIcon();
        } else if ("rolloverselectedicon".equalsIgnoreCase(name)) {
            return component.getRolloverSelectedIcon();
        } else if ("selectedicon".equalsIgnoreCase(name)) {
            return component.getSelectedIcon();
        } else if ("selectedobjects".equalsIgnoreCase(name)) {
            return component.getSelectedObjects();
        } else if ("text".equalsIgnoreCase(name)) {
            return component.getText();
        } else if ("verticalalignment".equalsIgnoreCase(name)) {
            return new Integer(component.getVerticalAlignment());
        } else if ("verticaltextposition".equalsIgnoreCase(name)) {
            return new Integer(component.getVerticalTextPosition());
        } else if ("borderpainted".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isBorderPainted());
        } else if ("contentareafilled".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isContentAreaFilled());
        } else if ("focuspainted".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isFocusPainted());
        } else if ("rolloverenabled".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isRolloverEnabled());
        } else if ("selected".equalsIgnoreCase(name)) {
            return Boolean.valueOf(component.isSelected());
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected void addEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("action".equalsIgnoreCase(name)) {
            component.addActionListener((UIActionListener) listener);
        } else if ("change".equalsIgnoreCase(name)) {
            component.addChangeListener((UIChangeListener) listener);
        } else if ("item".equalsIgnoreCase(name)) {
            component.addItemListener((UIItemListener) listener);
        } else {
            super.addEventListenerImpl(name, listener);
        }
    }

    protected void removeEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        if ("action".equalsIgnoreCase(name)) {
            component.removeActionListener((UIActionListener) listener);
        } else if ("change".equalsIgnoreCase(name)) {
            component.removeChangeListener((UIChangeListener) listener);
        } else if ("item".equalsIgnoreCase(name)) {
            component.removeItemListener((UIItemListener) listener);
        } else {
            super.removeEventListenerImpl(name, listener);
        }
    }

    protected Object performActionImpl(String name, Object[] args) throws UIComponentException {
        if ("doclick".equalsIgnoreCase(name)) {
            if (args == null) {
                component.doClick();
            } else {
                assertArgs(name, args, new Class[] {Number.class});
                component.doClick(((Number) args[0]).intValue());
            }

            return null;
        } else {
            return super.performActionImpl(name, args);
        }
    }
}
