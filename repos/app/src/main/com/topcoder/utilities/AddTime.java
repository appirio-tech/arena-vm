package com.topcoder.utilities;

import com.topcoder.server.services.*;
import com.topcoder.server.common.Round;

public class AddTime {

    public static void main(String[] args) {
        if (args.length < 5) {
            System.out.println("USAGE: java AddTime <constestID> <roundID> <minutes> <seconds> <phase> <addtostart>");
            System.out.println("phase can be either 1-5");
            System.out.println("0 to add time to round start time");
            System.out.println("1 to add time to the coding phase end time");
            System.out.println("2 to add time to the intermission phase end time");
            System.out.println("3 to add time to just the challenge phase end time");
            return;
        }

        int contestID = new Integer(args[0]).intValue();
        int roundID = new Integer(args[1]).intValue();

        int phase = new Integer(args[4]).intValue();
        if ((phase < 1) || (phase > 5)) {
            System.out.println("phase must be either 1-5");
            return;
        }

        int minutes = new Integer(args[2]).intValue();
        int seconds = new Integer(args[3]).intValue();
        boolean addtostart = (args.length > 3);

        try {

            Round contest = CoreServices.getContestRound(roundID);
            CoreServices.addTimeToContestRound(contest, minutes, seconds, phase, addtostart);

            System.out.println("\n***********************************************");
            System.out.println("PLEASE BOUNCE THE CONTEST TIMER AFTER ADDING TIME");
            System.out.println("\n***********************************************");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
