package com.topcoder.server.ejb.AdminServices;

/*
 * AccessTest.java
 *
 * This test class provides a series of tests that exercise the command access
 * functionality of the AdminServices ejb.
 *
 * This test suite expects the database to be set up as described below.
 *
 * @author John Waymouth (coderlemming)
 *
 */


import com.topcoder.server.ejb.AdminServices.*;
import com.topcoder.server.AdminListener.request.*;
import com.topcoder.server.AdminListener.response.*;


public class AccessTest extends AdminServicesTest {

    /** The contest that will be used for tests. */
    // protected static final int testContest = 3552; unused
    /** The round that will be used for tests. */
    protected static final int testRound = 3552;

    /** This user is expected to be unable to login to the monitor or
     run any commands on any round.  However, he should have
     rights to run commands, were he given access to any rounds.
     He should have an entry in group_monitor_function_xref for
     create system tests. */
    protected static final int unprivilegedUser = 112902; // Logan
    /** The unprivilegedUser's handle. */
    protected static final String unprivilegedHandle = "Logan";

    /** This user is expected to be able to login and have full access
     to every admin command. */
    protected static final int adminUser = 139514; // td

    /** This user is expected to be able to login and have access to run
     the testContest */
    protected static final int guestAdminUser = 265504; // coderlemming

    /** This user is expected to be able to have guest admin access to
     a round different from testRound */
    protected static final int otherGuestAdminUser = 152347; // ZorbaTHut has access to 3551


    public AccessTest(String method) {
        super(method);
    }

    /**
     *  This test ensures that a user without access to a command fails an
     *  access check
     */
    public void testAccessFail() throws Exception {
        CreateSystestsRequest csr = new CreateSystestsRequest(testRound);
        SecurityCheck check = new SecurityCheck(unprivilegedUser, unprivilegedUser, (Object) csr);

        boolean passed = adminServices.checkClientRequestAccess(check);

        // should fail
        assertEquals(false, passed);
    }

    /**
     *  This test ensures that a user with access to a command passes an
     *  access check
     */
    public void testAccessPass() throws Exception {
        CreateSystestsRequest csr = new CreateSystestsRequest(testRound);
        SecurityCheck check = new SecurityCheck(guestAdminUser, guestAdminUser, (Object) csr);

        boolean passed = adminServices.checkClientRequestAccess(check);

        // should pass
        assertEquals(true, passed);
    }

    /**
     *  This test ensures that an administrator has access to a command
     *  without being given explicit access.
     */
    public void testAdminAccess() throws Exception {
        CreateSystestsRequest csr = new CreateSystestsRequest(testRound);
        SecurityCheck check = new SecurityCheck(adminUser, adminUser, (Object) csr);

        boolean passed = adminServices.checkClientRequestAccess(check);

        // should pass
        assertEquals(true, passed);
    }

    /**
     *  This test checks to make sure that a guest admin for a previous round
     *  is not accidentally given access to change to the test round,
     *  or to execute a command on the test round.
     */
    public void testOldGuestAdmin() throws Exception {
        BackEndChangeRoundRequest crr = new BackEndChangeRoundRequest(otherGuestAdminUser, otherGuestAdminUser, testRound);
        ChangeRoundResponse response = adminServices.processChangeRoundRequest(crr);

        assertEquals("Testing whether old admin has access to this round", false, response.getSucceeded());

        // now also test a command
        CreateSystestsRequest csr = new CreateSystestsRequest(testRound);
        SecurityCheck check = new SecurityCheck(otherGuestAdminUser, otherGuestAdminUser, (Object) csr);

        boolean passed = adminServices.checkClientRequestAccess(check);

        assertEquals("Testing whether old admin has access to a command on this round", false, passed);
    }
}
