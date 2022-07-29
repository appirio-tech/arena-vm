package com.topcoder.client.contestApplet.widgets;

/**
 * ComboBoxItemWrapper.java
 *
 * Description:		Wrapper class to ensure uniqueness of combobox items regardless of toString()
 * @author			Tim "Pops" Roberts (troberts@bigfoot.com)
 * @version			1.0
 */

public class ComboBoxItemWrapper {

    protected Object obj;

    public ComboBoxItemWrapper(Object obj) {
        this.obj = obj;
    }

    public Object getObject() {
        return obj;
    }

    public String toString() {
        return obj.toString();
    }

    public boolean equals(Object other) {
        if (!(other instanceof ComboBoxItemWrapper)) return false;
        return obj.equals(((ComboBoxItemWrapper) other).getObject());
    }
}
