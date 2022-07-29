/*
 * ElapsedTimeFormat
 * 
 * Created 07/19/2007
 */
package com.topcoder.shared.i18n.format;

import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Elapsed Time format<p>
 * 
 * Formats a long  value and formats it as 'h hours, m minutes, s seconds and ms milliseconds'  
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ElapsedTimeFormat extends BaseFormat {

    public ElapsedTimeFormat(Locale locale) {
        super(locale);
    }

    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        long remaining = ((Number) obj).longValue();
        long ms = remaining % 1000;
        remaining = (remaining - ms) / 1000;
        long seconds = remaining % 60;
        remaining = (remaining - seconds) / 60;
        long minutes = remaining % 60;
        remaining = (remaining - minutes) / 60;
        long hours = remaining;
        ArrayList list = new ArrayList(5);
        if (hours > 0) {
            if (hours == 1) {
                list.add("1 hour");
            } else {
                list.add(hours+" hours");
            }
        }
        if (minutes > 0) {
            if (minutes == 1) {
                list.add("1 minute");
            } else {
                list.add(minutes+" minutes");
            }
        }
        if (seconds > 0) {
            if (seconds == 1) {
                list.add("1 second");
            } else {
                list.add(seconds+" seconds");
            }
        }
        /* for now this is too verbose - rfairfax 
        if (ms > 0) {
            if (ms == 1) {
                list.add("1 millisecond");
            } else {
                list.add(ms+" milliseconds");
            }
        }*/
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                if (i == list.size() -1) {
                    toAppendTo.append(" and ");
                } else {
                    toAppendTo.append(", ");
                }
            }
            toAppendTo.append(list.get(i));
        }
        return toAppendTo;
    }
}
