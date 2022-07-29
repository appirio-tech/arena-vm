package com.topcoder.utilities;

import com.topcoder.server.services.*;
import com.topcoder.server.common.*;

import java.sql.Timestamp;
import java.util.*;

public final class CreateContest {

    public static void createContest(String name, int cid) {
        // for now just have a real short contest
        Calendar cal = new GregorianCalendar();
        cal.add(cal.MINUTE, 3); // coding starts 3 minutes
        Timestamp codestart = new Timestamp(cal.getTime().getTime());
        cal.add(cal.MINUTE, 2); // coding ends 2 minute
        Timestamp codeend = new Timestamp(cal.getTime().getTime());
        cal.add(cal.MINUTE, 1); // intermission ends 1 minute
        Timestamp intend = new Timestamp(cal.getTime().getTime());
        cal.add(cal.MINUTE, 2); // challenge ends 1 minute
        long chalEndTime = cal.getTime().getTime();
        Timestamp chalEnd = new Timestamp(chalEndTime);

        //System.out.println(intend.toGMTString());
        System.out.println(intend.toString());
        System.out.println(new Date(intend.getTime()).toString());

        Round contest = RoundFactory.newRound(cid, cid, -1, name, name);
        contest.setCodingStart(codestart);
        contest.setCodingEnd(codeend);
        contest.setIntermissionEnd(intend);
        contest.setChallengeEnd(chalEnd);
        CoreServices.createContest(contest);
    }

    public static void main(String[] args) {
        int cid = Integer.parseInt(args[0]);
        createContest("Haos Contest " + cid, cid);
    }
}
