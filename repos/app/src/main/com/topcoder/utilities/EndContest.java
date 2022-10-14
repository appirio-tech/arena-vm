package com.topcoder.utilities;

import com.topcoder.server.services.*;
import com.topcoder.server.common.ContestRound;

public class EndContest {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("USAGE: java EndContest <roundID>");
            return;
        }

        int roundID = new Integer(args[0]).intValue();
        CoreServices.endContest(roundID);
    }
}
