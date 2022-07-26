/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.client.contestApplet.widgets.ui;

import java.awt.Color;
import javax.swing.ComboBoxModel;

import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.impl.component.UITextField;
import com.topcoder.client.contestApplet.widgets.LookupJLabelTextField;

/**
 * The lookup label text field.
 *
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine - Fix Private Chat Problem version 1.0):
 * <ol>
 *      <li>Update {@link #setPropertyImpl(String name, Object value)} method.</li>
 * </ol>
 * </p>
 * @author savon_cn
 * @version 1.1
 *
 */
public class UILookupJLabelTextField extends UITextField {
    private LookupJLabelTextField field;

    protected Object createComponent() {
        return new LookupJLabelTextField();
    }

    protected void initialize() throws UIComponentException {
        super.initialize();

        field = (LookupJLabelTextField) getEventSource();
    }
    /**
     * Set the property.
     * @param name the property name.
     * @param value the property value.
     */
    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("uncommittedcolor".equalsIgnoreCase(name)) {
            field.setUnCommittedColor((Color) value);
        } else if ("appendending".equalsIgnoreCase(name)) {
            field.setAppendEnding(((Boolean) value).booleanValue());
        } else if ("model".equalsIgnoreCase(name)) {
            field.setModel((ComboBoxModel) value);
        } else if ("removeCaretHandler".equalsIgnoreCase(name)) {
            field.removeCaretHandler();
        } else if ("addCaretHandler".equalsIgnoreCase(name)) {
            field.addCaretHandler();
        } else {
            super.setPropertyImpl(name, value);
        }
    }
}
