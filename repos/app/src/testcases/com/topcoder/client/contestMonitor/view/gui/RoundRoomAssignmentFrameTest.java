package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.server.contest.Region;
import com.topcoder.server.contest.RoundData;
import com.topcoder.server.contest.RoundRoomAssignment;
import com.topcoder.server.contest.RoundType;
import com.topcoder.netCommon.contest.ContestConstants;

import javax.swing.*;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

/**
 * TestCase for RoundRoomAssignmentFrame class
 *
 * Copyright (c) 2003 TopCoder, Inc.  All rights reserved.
 * 
 * @author  TCSDESIGNER
 * @version 2.0 11/03/2003
 * @since   Admin Tool 2.0
 */
public final class RoundRoomAssignmentFrameTest extends TestCase {

    private static Logger log = Logger.getLogger(RoundRoomAssignmentFrameTest.class);


    public void setUp() {

    }

    public void tearDown() {
    }

    /**
     * tests the basic functionality of the object constructor.
     */
    public static void testFrame() {

       Runnable r = new Runnable() {
          public void run() {
           JFrame frame = new JFrame("Test");
           JDialog dlg = new JDialog(frame);
           frame.setSize(100,100);
           frame.setVisible( true );
           dlg.setSize(100, 100);
           dlg.setVisible( true );

           RoundData rnd = new RoundData(null, 10, "Test Round", new RoundType( 1, "Rnd1"), "open", 500, 0, "", new Region());
        
           rnd.setRoomAssignment(new RoundRoomAssignment(1, 10, ContestConstants.IRON_MAN_SEEDING,true,false,true,1.234));
           RoundRoomAssignmentFrame rraf = new RoundRoomAssignmentFrame(null,dlg);
           rraf.display( rnd );
           }};
       Thread test = new Thread( r );
       test.start();
       try {
         test.join();
         Thread.currentThread().sleep(20000);
       } catch ( Throwable t ) {}
    }

}

