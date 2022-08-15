package com.topcoder.utilities;

import com.topcoder.server.common.*;
import com.topcoder.server.services.*;

public class RoomAssignments {

    public RoomAssignments() {

    }


    public static void main(String[] args) {
        System.err.println("This utility is deprecated.  Use the monitor instead");
/*
		if (args.length != 7) {
			System.out.println("This program is used to assign rooms for a contest.");
<<<<<<< RoomAssignments.java
			System.out.println("Usage: java RoomAssignments <contestId> <roundId> <startingRoomId> <codersPerRoom> <type> <p> <byDivision> <isFinal> <isByRegion>");
=======
			System.out.println("Usage: java RoomAssignments <contestId> <roundId> <codersPerRoom> <ironMan> <byDivision> <isFinal> <isByRegion>");
>>>>>>> 1.5.30.1
			System.out.println("byDivision is a boolean indicating whether divisions need to be established.");
			System.out.println("isFinal is a boolean indicating whether this is a final run or not.");
			System.out.println("isByRegion is a boolean indicating whether assignments should be made also by region.");
			System.out.println("If assignments are being done based on rating and not points, enter 0 for the last round");
			return;
		}

		int contestId = (new Integer(args[0])).intValue();
		int roundId = (new Integer(args[1])).intValue();
<<<<<<< RoomAssignments.java
		int startingRoom = (new Integer(args[2])).intValue();
        int codersPerRoom = (new Integer(args[3])).intValue();
		int type = (new Integer(args[4])).intValue();
        double p = Double.parseDouble(args[5]);
        boolean byDivision = (new Boolean(args[6])).booleanValue();
		boolean isFinal = (new Boolean(args[7])).booleanValue();
		boolean isByRegion = (new Boolean(args[8])).booleanValue();
=======
        int codersPerRoom = (new Integer(args[2])).intValue();
		boolean ironMan = (new Boolean(args[3])).booleanValue();
        boolean byDivision = (new Boolean(args[4])).booleanValue();
		boolean isFinal = (new Boolean(args[5])).booleanValue();
		boolean isByRegion = (new Boolean(args[6])).booleanValue();
>>>>>>> 1.5.30.1

<<<<<<< RoomAssignments.java
		CoreServices.assignRooms(contestId, roundId, startingRoom, codersPerRoom, type, byDivision, isFinal, isByRegion,p);
=======
		CoreServices.assignRooms(contestId, roundId, codersPerRoom, ironMan, byDivision, isFinal, isByRegion);
>>>>>>> 1.5.30.1

*/
    }
}

