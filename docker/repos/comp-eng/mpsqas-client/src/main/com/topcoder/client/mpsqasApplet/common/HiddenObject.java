package com.topcoder.client.mpsqasApplet.common;

/**
 * An Object which holds an Object and a String pair.  toString returns
 * the String and getObject returns the Object.  This is useful in tables
 * to store Objects in cells while being able to specify the String being
 * displayed.
 *
 * @author mitalub
 */
public class HiddenObject {

    protected String string;
    protected Object object;

    public HiddenObject(String string, Object object) {
        this.string = string;
        this.object = object;
    }

    public String getString() {
        return string;
    }

    public String toString() {
        return string;
    }

    public Object getObject() {
        return object;
    }
}
