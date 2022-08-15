package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import com.topcoder.shared.problem.DataType;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import com.topcoder.netCommon.mpsqas.HiddenValue;

/**
 * AppletConstants
 *
 * A set of constants used by the applet's GUI.
 *
 * @author mitalub
 */
public class GUIConstants {

    /** title of window */
    public static String MAIN_WINDOW_TITLE = "MPSQAS";

    /**
     * buildConstraints assigns a set of constraints to a GridBagConstraints
     * object.
     *
     * @param gbc   The GridBagConstraints
     * @param gx    Value to assign to gridx
     * @param gy    Value to assign to gridy
     * @param gw    Value to assign to gridwidth
     * @param gh    Value to assign to gridheight
     * @param wx    Value to assign to weightx
     * @param wy    Value to assign to weighty
     */
    public static void buildConstraints(GridBagConstraints gbc, int gx, int gy,
            int gw, int gh, int wx, int wy) {
        gbc.gridx = gx;
        gbc.gridy = gy;
        gbc.gridwidth = gw;
        gbc.gridheight = gh;
        gbc.weightx = wx;
        gbc.weighty = wy;
    }

    /**
     * Compares two table objects for sorting.
     */
    public static int compareForColumnSort(Object o1, Object o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;

        int result = 0;
        if (o1 instanceof HiddenValue && o2 instanceof HiddenValue) {
            HiddenValue h1 = (HiddenValue) o1;
            HiddenValue h2 = (HiddenValue) o2;
            if (h1.getValue() < h2.getValue()) {
                result = -1;
            }
            if (h1.getValue() > h2.getValue()) {
                result = 1;
            }
        } else if (o1 instanceof Double && o2 instanceof Double) {
            double d1 = ((Double) o1).doubleValue();
            double d2 = ((Double) o2).doubleValue();
            if (d1 < d2) {
                result = -1;
            }
            if (d2 < d1) {
                result = 1;
            }
        } else if (o1 instanceof Integer && o2 instanceof Integer) {
            int i1 = ((Integer) o1).intValue();
            int i2 = ((Integer) o2).intValue();
            if (i1 < i2) {
                result = -1;
            }
            if (i2 < i1) {
                result = 1;
            }
        } else {
            String s1 = (o1.toString()).toLowerCase();
            String s2 = (o2.toString()).toLowerCase();
            result = s1.compareTo(s2);
        }
        return result;
    }

    /**
     * Returns the recommended width (as a percent between 0 and 1) of the
     * text field for the specified arg type.
     */
    public static double getTextFieldWidth(DataType type) {
        if (type.getDimension() > 0)
            return 1; //arrays are long
        else if (type.getBaseName().equals("String"))
            return .5; //strings in middle
        else
            return .2; //all else is short
    }
}
