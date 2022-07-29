package com.topcoder.netCommon.mpsqas;

/**
 * A generic class corresponding to a String / int combination.
 * Can be used to display a String corresponding to an integer id
 * while keeping track of the id.
 *
 * @author mitalub
 */
public class HiddenValue {

    public HiddenValue(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public String toString() {
        return text;
    }

    public int getValue() {
        return value;
    }

    public String getFullString() {
        return text + ": " + value;
    }

    public void reset(String text, int value) {
        this.text = text;
        this.value = value;
    }

    private String text;
    private int value;
}
