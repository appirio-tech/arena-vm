package com.topcoder.client.contestApplet.widgets.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import javax.swing.ComboBoxModel;
import javax.swing.border.Border;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.impl.component.UIAbstractComponent;
import com.topcoder.client.contestApplet.widgets.LookupJLabelComboBoxEditor;

public class UILookupJLabelComboBoxEditor extends UIAbstractComponent {
    private LookupJLabelComboBoxEditor editor;

    protected Object createComponent() {
        return new LookupJLabelComboBoxEditor();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();
        editor = (LookupJLabelComboBoxEditor) getEventSource();
    }

    protected void addChildImpl(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        throw new UIComponentException("ComboBox editor is not a container.");
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("foreground".equalsIgnoreCase(name)) {
            editor.setForeground((Color) value);
        } else if ("background".equalsIgnoreCase(name)) {
            editor.setBackground((Color) value);
        } else if ("border".equalsIgnoreCase(name)) {
            editor.setBorder((Border) value);
        } else if ("model".equalsIgnoreCase(name)) {
            editor.setModel((ComboBoxModel) value);
        } else {
            super.setPropertyImpl(name, value);
        }
    }
}
