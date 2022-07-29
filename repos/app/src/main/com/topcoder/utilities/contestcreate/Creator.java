package com.topcoder.utilities.contestcreate;

import java.util.*;

import java.text.SimpleDateFormat;
import java.text.ParseException;

public class Creator {

    private Properties _props;

    Creator(Properties props) {
        _props = props;
    }

    // --------------------------------------------------
    String extractString(String propname) {
        return (String) _props.get(propname);
    }


    Integer extractInteger(String propname) {
        Integer intval = null;
        String text = (String) _props.get(propname);

        if (text != null) {
            try {
                intval = Integer.valueOf(text.trim());
            } catch (NumberFormatException e) {
                System.out.println("cannot parse property " + propname +
                        " value " + text);
            }
        }

        return intval;
    }

    Date extractDate(String propname) {
        String text = (String) _props.get(propname);
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy:HH:mm");

        Date date = null;
        if (text != null) {
            try {
                date = format.parse(text);
            } catch (ParseException e) {
                System.out.println("Can't parse date " + text);
                date = new Date();
            }
        }

        return date;
    }

    Date addMinutes(Date time, Integer minutes) {
        if ((time == null) || (minutes == null)) {
            return time;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.add(Calendar.MINUTE, minutes.intValue());
        return cal.getTime();
    }

    String[] extractList(String propname) {
        String[] res = null;

        String text = (String) _props.get(propname);

        if (text != null) {
            try {
                res = ListParser.parseList(text);
            } catch (ParseException e) {
                System.err.println("Problem parsing list propert " + propname +
                        e.getMessage());

            }
        }

        return (res == null) ? new String[0] : res;
    }


}
