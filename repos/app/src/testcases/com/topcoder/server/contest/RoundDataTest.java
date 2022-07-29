package com.topcoder.server.contest;

import junit.framework.TestCase;

/**
 * This class tests the RoundData class
 * 
 * Copyright (c) 2003 TopCoder, Inc.  All rights reserved.
 * 
 * @author  TCSDESIGNER
 * @version 2.0 11/03/2003
 * @since   Admin Tool 2.0
 */
public class RoundDataTest extends TestCase {

    private RoundData rd = null;
    private ContestData cd = null;
    private RoundRoomAssignment rra = null;
    private RoundRoomAssignment rra2 = null;
    
    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        cd = new ContestData(1);
        rd = new RoundData(cd, 1);
        rra = new RoundRoomAssignment(1);
        rra2 = new RoundRoomAssignment(1);
    }

    /**
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        cd = null;
    }

    /**
     * method to test for void RoundData(ContestData, int)
     */
    public void testRoundDataConstructor() {
        rra = rd.getRoomAssignment();
        assertTrue("check round id", rd.getId() == 1);
        assertTrue("default coders", rra.getCodersPerRoom() == rra2.getCodersPerRoom() );
        assertTrue("default type", rra.getType() == rra2.getType() );
        assertTrue("default by division", rra.isByDivision() == rra2.isByDivision() );
        assertTrue("default by region", rra.isByRegion() == rra2.isByRegion() );
        assertTrue("default final", rra.isFinal() == rra2.isFinal() );
        assertTrue("default p", rra.getP() == rra2.getP() );
        
    }

    /**
     * method to test for set/getRoomAssignment methods
     */
    public void testSetRoomAssignment() {
        rd.setRoomAssignment(rra);
        rra2 = rd.getRoomAssignment();
        assertTrue("same coders", rra.getCodersPerRoom() == rra2.getCodersPerRoom() );
        assertTrue("same type", rra.getType() == rra2.getType() );
        assertTrue("same by division", rra.isByDivision() == rra2.isByDivision() );
        assertTrue("same by region", rra.isByRegion() == rra2.isByRegion() );
        assertTrue("same final", rra.isFinal() == rra2.isFinal() );
        assertTrue("same p", rra.getP() == rra2.getP() );
    }

}
