package com.topcoder.server;

//import java.util.*;

import junit.framework.*;

import com.topcoder.server.contest.*;

public class ServerTestSuite {

    public static void main(String[] args) {
        TestSuite suite = new TestSuite("Server Tests");
        //suite.addTest( new TestSuite( RoomAssignmentTest.class ) );
        suite.addTest(new TestSuite(PrizeAllocatorTest.class));
        junit.textui.TestRunner.run(suite);
    }
}
