package com.topcoder.client.ui.impl.component;

import java.awt.GridBagConstraints;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;

public class UIButtonGroup extends UIAbstractComponent {
    private ButtonGroup group;

    protected Object createComponent() {
        return new ButtonGroup();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();
        group = (ButtonGroup) getEventSource();
    }

    protected void addChildImpl(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        if (component.getEventSource() instanceof AbstractButton) {
            group.add((AbstractButton) component.getEventSource());
        } else {
            throw new UIComponentException("Button group can only have buttons.");
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("buttoncount".equalsIgnoreCase(name)) {
            return new Integer(group.getButtonCount());
        } else if ("Elements".equalsIgnoreCase(name)) {
            return group.getElements();
        } else if ("Selection".equalsIgnoreCase(name)) {
            return group.getSelection();
        } else {
            return super.getPropertyImpl(name);
        }
    }

    protected Object performActionImpl(String name, Object[] args) throws UIComponentException {
        if ("add".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {AbstractButton.class});
            group.add((AbstractButton) args[0]);
            return null;
        } else if ("isSelected".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {ButtonModel.class});
            return Boolean.valueOf(group.isSelected((ButtonModel) args[0]));
        } else if ("remove".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {AbstractButton.class});
            group.remove((AbstractButton) args[0]);
            return null;
        } else if ("setSelected".equalsIgnoreCase(name)) {
            assertArgs(name, args, new Class[] {ButtonModel.class, Boolean.class});
            group.setSelected((ButtonModel) args[0], ((Boolean) args[1]).booleanValue());
            return null;
        } else {
            return super.performActionImpl(name, args);
        }
    }
}
