package com.topcoder.server.common;

import java.sql.Timestamp;

import junit.framework.TestCase;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.contest.RoundRoomAssignment;

/**
 * This class tests the ContestRound object for accuracy and failure
 * 
 * Copyright (c) 2003 TopCoder, Inc.  All rights reserved.
 * 
 * @author  TCSDESIGNER
 * @version 2.0 11/03/2003
 * @since   Admin Tool 2.0
 */
public class ContestRoundTest extends TestCase {

    /**
     * this is the instance that we are testing
     */
    private ContestRound contestRound = null;
    
    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        contestRound = new ContestRound(1,1,
                ContestConstants.WEAKEST_LINK_ROUND_TYPE_ID,"Contest","Round");
    }

    /**
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        contestRound = null;
    }

    public void testContestRound() {
        // the constructor should have set a 'default' setting when created.
        RoundRoomAssignment rra = contestRound.getRoomAssignment();
        RoundRoomAssignment rra2 = new RoundRoomAssignment(1);
        
        assertTrue("check round", rra.getRoundId() == rra2.getRoundId());
        assertTrue("check coders", rra.getCodersPerRoom() == rra2.getCodersPerRoom());
        assertTrue("check type ", rra.getType() == rra2.getType());
        assertTrue("check division", rra.isByDivision() == rra2.isByDivision());
        assertTrue("check region", rra.isByRegion() == rra2.isByRegion());
        assertTrue("check final", rra.isFinal() == rra2.isFinal());
        assertTrue("check p", rra.getP() == rra2.getP() );
    }

    /**
     * this method will test the set/get methods for the room assignement
     * start time. Tests for both accuracy and failure.
     */
    public void testSetRoomAssignmentStart() {
        Timestamp t = Timestamp.valueOf("2003-01-01 0:0:0.0");
        contestRound.setRoomAssignmentStart(t);
        assertTrue("set a valid value", contestRound.getRoomAssignmentStart().equals(t));
        try {
            contestRound.setRoomAssignmentStart(null);
            fail("cannot set a null timestamp");
        } catch( IllegalArgumentException e ) {}
    }

    /**
     * this method will test the set/get methods for the room assignement
     * end time. Tests for both accuracy and failure.
     */
    public void testSetRoomAssignmentEnd() {
        Timestamp t = Timestamp.valueOf("2003-01-01 0:0:0.1");
        contestRound.setRoomAssignmentEnd(t);
        assertTrue("set a valid value", contestRound.getRoomAssignmentEnd().equals(t));
        try {
            contestRound.setRoomAssignmentEnd(null);
            fail("cannot set a null timestamp");
        } catch( IllegalArgumentException e ) {}
    }

    /**
     * This method will test the set/get for the room assignment data
     */
    public void testSetRoomAssignment() {
        RoundRoomAssignment rra = new RoundRoomAssignment(1,10,
                ContestConstants.IRON_MAN_SEEDING,true, false, false, 1.2);
        
        contestRound.setRoomAssignment(rra);
        RoundRoomAssignment rra2 = contestRound.getRoomAssignment();
        assertTrue( "check set/get ", rra2 != null);
        assertTrue("check round", rra.getRoundId() == rra2.getRoundId());
        assertTrue("check coders", rra.getCodersPerRoom() == rra2.getCodersPerRoom());
        assertTrue("check type ", rra.getType() == rra2.getType());
        assertTrue("check division", rra.isByDivision() == rra2.isByDivision());
        assertTrue("check region", rra.isByRegion() == rra2.isByRegion());
        assertTrue("check final", rra.isFinal() == rra2.isFinal());
        assertTrue("check p", rra.getP() == rra2.getP() );

        try {
            contestRound.setRoomAssignment(null);
            fail("cannot set a null room assignment");
        } catch( IllegalArgumentException e ) {}
    }
}
