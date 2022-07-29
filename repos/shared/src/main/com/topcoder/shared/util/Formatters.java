package com.topcoder.shared.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * @author unknown
 * @version  $Id$
 */
public class Formatters {
    private static DecimalFormat s_doubleFormatter = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));

    public static synchronized Double getDouble(double d) {
        return new Double(s_doubleFormatter.format(d));
    }

    public static synchronized String getDoubleString(double d) {
        return s_doubleFormatter.format(d);
    }
    
    public static synchronized String getDoubleString(int d) {
        //for new score formatting only
        double f = (double)d / 100.0;
        return s_doubleFormatter.format(f);
    }
}
