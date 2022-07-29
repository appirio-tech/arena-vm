package com.topcoder.server.contest;

import com.topcoder.netCommon.contest.ContestConstants;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

/**
 * TestCase for RoundRoomAssignment class
 *
 * Copyright (c) 2003 TopCoder, Inc.  All rights reserved.
 * 
 * @author  TCSDESIGNER
 * @version 2.0 11/03/2003
 * @since   Admin Tool 2.0
 */
public final class RoundRoomAssignmentTest extends TestCase {

    /** 
     * this method is not needed by this test case
     */
    public void setUp() {
    }

    /** 
     * this method is not needed by this test case
     */
    public void tearDown() {
    }

    /**
     * tests the basic functionality of the object constructor.
     */
    public static void testConstructor() {
       RoundRoomAssignment rra = new RoundRoomAssignment(1);
       try {
          rra = new RoundRoomAssignment( -1 );
          fail( "should have thrown an exception for bad round id" );
       } catch( IllegalArgumentException ie ) {
       }

       try {
          rra = new RoundRoomAssignment( -1, 100, ContestConstants.IRON_MAN_SEEDING,
                                        true, true, true, 0.123 );
          fail( "should have thrown an exception for bad round id" );
       } catch( IllegalArgumentException ie ) {
       }
       try {
           rra = new RoundRoomAssignment( 1, 100, -1,
                   true, true, true, 0.123 );
           fail( "should have thrown an exception for bad round type" );
       } catch( IllegalArgumentException ie ) {
       }
       
       try {
           rra = new RoundRoomAssignment( 1, -1, ContestConstants.IRON_MAN_SEEDING,
                   true, true, true, 0.123 );
           fail( "should have thrown an exception for bad codersPerRoom" );
       } catch( IllegalArgumentException ie ) {
       }
       try {
           rra = new RoundRoomAssignment( 1, 0, ContestConstants.IRON_MAN_SEEDING,
                   true, true, true, -0.123 );
           fail( "should have thrown an exception for bad 'p'" );
       } catch( IllegalArgumentException ie ) {
       }
       
    }
    /**
     * tests the basic functionality of the object setters
     */
    public static void testSetters() {
       RoundRoomAssignment rra = new RoundRoomAssignment(1);
       rra.setType(ContestConstants.RANDOM_SEEDING );
       assertTrue( "check type" , ContestConstants.RANDOM_SEEDING == rra.getType());

       // test the type
       try {
          rra.setType( -1 );
          fail( "should have thrown an exception for bad type" );
       } catch( IllegalArgumentException ie ) {
       }
       rra.setType( ContestConstants.RANDOM_SEEDING );
       assertTrue( "getType-random" , rra.getType() == ContestConstants.RANDOM_SEEDING );
       rra.setType( ContestConstants.IRON_MAN_SEEDING );
       assertTrue( "getType-ironman" , rra.getType() == ContestConstants.IRON_MAN_SEEDING );
       rra.setType( ContestConstants.NCAA_STYLE );
       assertTrue( "getType-ncaa" , rra.getType() == ContestConstants.NCAA_STYLE );
       rra.setType( ContestConstants.EMPTY_ROOM_SEEDING );
       assertTrue( "getType-empty" , rra.getType() == ContestConstants.EMPTY_ROOM_SEEDING );
       rra.setType( ContestConstants.WEEKEST_LINK_SEEDING );
       assertTrue( "getType-week" , rra.getType() == ContestConstants.WEEKEST_LINK_SEEDING );

       // test coders per room
       try {
           rra.setCodersPerRoom(-1);
           fail( "should have thrown an exception for bad coders per room" );
       } catch( IllegalArgumentException ie ) {
       }

       // test 'p'
       try {
           rra.setP(-1.0);
           fail( "should have thrown an exception for bad 'p'" );
       } catch( IllegalArgumentException ie ) {
       }
       
    }

    /**
     * tests the basic functionality of the object getters
     */
    public static void testGetters() {
       RoundRoomAssignment rra = new RoundRoomAssignment(1);

       rra.setCodersPerRoom( 3 );
       rra.setType(ContestConstants.NCAA_STYLE );
       rra.setByDivision( false );
       rra.setByRegion( true );
       rra.setFinal( false );
       rra.setP( 23.77 );
    
       assertTrue( "getId" , rra.getRoundId() == 1 );
       assertTrue( "getCodersPerRoom" , rra.getCodersPerRoom() == 3 );
       assertTrue( "getType" , ContestConstants.NCAA_STYLE == rra.getType());
       assertTrue( "isByDivision" , rra.isByDivision() == false );
       assertTrue( "isByRegion" , rra.isByRegion() == true );
       assertTrue( "isFinal" , rra.isFinal() == false );
       assertTrue( "getP" , rra.getP() == 23.77 );

       rra = new RoundRoomAssignment( 1, 100, ContestConstants.IRON_MAN_SEEDING,
               true, true, true, 0.123 );
       assertTrue( "getId" , rra.getRoundId() == 1 );
       assertTrue( "getCodersPerRoom" , rra.getCodersPerRoom() == 100 );
       assertTrue( "getType" , rra.getType() == ContestConstants.IRON_MAN_SEEDING);
       assertTrue( "isByDivision" , rra.isByDivision() == true );
       assertTrue( "isByRegion" , rra.isByRegion() == true );
       assertTrue( "isFinal" , rra.isFinal() == true );
       assertTrue( "getP" , rra.getP() == 0.123 );
       
    }
}

