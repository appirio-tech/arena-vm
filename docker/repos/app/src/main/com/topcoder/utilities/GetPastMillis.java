package com.topcoder.utilities;

import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: gtsipol
 * Date: Nov 5, 2003
 * Time: 9:21:54 AM
 * To change this template use Options | File Templates.
 */
public class GetPastMillis {

    public GetPastMillis () {
    }

    public static long getCurrentTimeMillis() {

         return System.currentTimeMillis();
    }

    public static void getTwoWeeksAgo() {

        //long milliTime = 28 * 24 * 60 * 60 * 1000;
        //long milliTimeAgo = System.currentTimeMillis() - milliTime;
        Calendar now = Calendar.getInstance();
        //now.setTimeInMillis(milliTimeAgo);
        now.set(2003, 11, 7);
        //System.out.println(" 2 Weeks: "+milliTime);
        System.out.println("Weeks ago: "+now.getTime());
        //System.out.println(" 2 Weeks ago Millis: "+milliTimeAgo);
        System.out.println("Weeks ago Millis (getTime): "+now.getTimeInMillis());
        Calendar newCal = Calendar.getInstance();
        newCal.setTimeInMillis(now.getTimeInMillis());
        System.out.println("Validation: "+newCal.getTime());


    }

    public static void main (String[] args) {

        System.out.println ("Current Milli Time is: "+getCurrentTimeMillis());
        //System.out.println ("Current Milli Time is: "+getCurrentTimeMillis());
        getTwoWeeksAgo();


    }
}
