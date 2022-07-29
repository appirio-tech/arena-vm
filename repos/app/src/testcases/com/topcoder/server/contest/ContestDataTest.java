package com.topcoder.server.contest;

import junit.framework.TestCase;
import org.apache.log4j.Logger;

/**
 * TestCase for ContestData class
 *
 * Copyright (c) 2003 TopCoder, Inc.  All rights reserved.
 * 
 * @author  TCSDESIGNER
 * @version 2.0 11/03/2003
 * @since   Admin Tool 2.0
 */
public final class ContestDataTest extends TestCase {
    private ContestData da = null;
    
    public void setUp() {
        da = new ContestData( 1 );
    }

    public void tearDown() {
        da = null;
    }

    /**
     * tests the basic functionality of the object.
     */
    public void testConstructor() {
       assertTrue( "testing constructor", 1 == da.getId());
       da.setId( 123 );
       assertTrue( "testing setId", 123 == da.getId());
    }

}

