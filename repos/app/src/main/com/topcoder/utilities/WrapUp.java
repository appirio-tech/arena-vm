package com.topcoder.utilities;

import java.util.Collection;
import java.util.Iterator;

import com.topcoder.server.common.*;
import com.topcoder.server.services.CoreServices;


/**
 * Allocates prizes for players in a contest by calling into the CoreServices layer.
 */
public class WrapUp {

    /**
     * Invokes CoreServices to allocate prizes for the specified contest.  If the second parameter, commit
     * is true, then the prize data is also saved to the database.
     * Takes two parameters <round id> and <commit>
     */
    public static void main(String args[]) {
        int numArgs = args.length;
        if (numArgs != 2) {
            System.out.println("SYNTAX: java com.topcoder.utilities.WrapUp <round id> <commit>");
            return;
        }

        int roundId = new Integer(args[0]).intValue();
        boolean commit = (new Boolean(args[1])).booleanValue();

        try {
            Collection prizeRooms = CoreServices.allocatePrizes(roundId, commit);
            System.out.println("Prize Allocations for Round: " + roundId + " Commit = " + commit);
            System.out.println("=================================================");
            for (Iterator allRooms = prizeRooms.iterator(); allRooms.hasNext();) {
                System.out.println(allRooms.next());
            }
            System.out.println("=================================================");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

