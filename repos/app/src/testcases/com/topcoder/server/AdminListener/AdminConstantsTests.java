package com.topcoder.server.AdminListener;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;
import com.topcoder.security.policy.GenericPermission;

/**
 * TestCase for AdminConstants class. This class will test the new functionality
 * that was added for AdminTool 2.0. Specifically, the mapping from function id
 * to 'string' permissions for the new security schema will be tested.
 *
 * Copyright (c) 2003 TopCoder, Inc.  All rights reserved.
 *
 * @author  TCSDESIGNER
 * @version 2.0 11/03/2003
 * @since   Admin Tool 2.0
 */
public class AdminConstantsTests extends TestCase {

    /**
     * the logger used by this test case
     */
    Logger testLogger = Logger.getLogger(AdminConstantsTests.class);

    /**
     * test case constructor
     * @param s name of test case
     */
    public AdminConstantsTests(String s) {
        super(s);
    }

    /**
     * This method will return the suite for this test case
     * @return
     */
    public static Test suite() {
        return new TestSuite(AdminConstantsTests.class);
    }

    /**
     * some test permission (security man.)
     */
    private GenericPermission perm = null;
    /**
     * some test permission (new id)
     */
    private GenericPermission sperm = null;
    /**
     * some test permission (bad)
     */
    private GenericPermission badperm = null;

    /**
     * creates 3 permissions 2 good ones and 1 bad
     */
    public void setUp() {
        sperm = new GenericPermission(
            "com.topcoder.client.contestMonitor.REQUEST_SECURITY_MANAGEMENT");
        perm = new GenericPermission(
            "com.topcoder.client.contestMonitor.REQUEST_NEW_ID");
        badperm = new GenericPermission(
            "com.topcoder.server.contestMonitor");
       }

    /**
     * clear off the params
     */
    protected void tearDown() throws Exception {
        perm = null;
        sperm = null;
        badperm = null;
    }

    /**
     * tests the accuracy and failure conditions for the methods added
     * for AdminTool 2.0 to handle the permissions mapping for the new
     * security schema
     */
    public void testGetPermission() {
        assertTrue("-1 is not a valid function id", AdminConstants.getPermission(-1) == null );
        assertTrue("security function", sperm.equals(AdminConstants.getPermission(AdminConstants.REQUEST_SECURITY_MANAGEMENT)));
        assertTrue("new id", perm.equals(AdminConstants.getPermission(AdminConstants.REQUEST_NEW_ID)));
       }

    /**
     * tests the accuracy and failure conditions for the methods added
     * for AdminTool 2.0 to handle the permissions mapping for the new
     * security schema
     */
    public void testIsNonRoundSpecificPermission() {
        assertTrue("check new id permission", AdminConstants.isNonRoundSpecificPermission(perm));
        assertTrue("check security permission", AdminConstants.isNonRoundSpecificPermission(perm));
        assertFalse("check for bad non round function", AdminConstants.
                                isNonRoundSpecificPermission(badperm));
        try {
            AdminConstants.isNonRoundSpecificPermission(null);
            fail("should have thrown exception for bad id");
        } catch( IllegalArgumentException ex ) {
        }
    }

    /**
     * tests all the new security permissions (accuracy) and one for failure
     */
    public void testAllNewPermissions() {
        try {
            AdminConstants.isNonRoundSpecificPermission(AdminConstants.getPermission(-1));
            fail("should have thrown exception for bad id");
        } catch( IllegalArgumentException ex ) {
        }
       assertTrue("test perm ", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_ADD_TIME)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.ROUND_INDEPENDENT_FUNCTION)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_BLOB_SEARCH)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_OBJECT_LOAD)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_TEXT_LOAD)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_TEXT_SEARCH)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_DISABLE_ROUND)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_ENABLE_ROUND)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_REFRESH_ROUND)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_RESTORE_ROUND)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_REGISTER_USER)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_UNREGISTER_USER)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_REFRESH_PROBLEMS)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_REFRESH_REGISTRATION)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_REFRESH_ROOM)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
       		   AdminConstants.getPermission(AdminConstants.REQUEST_REFRESH_ALL_ROOMS)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_REFRESH_ROOM_LISTS)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_ASSIGN_ROOMS)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_ADD_TIME)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_ADVANCE_CONTEST_PHASE)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_REFRESH_BROADCASTS)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_CREATE_SYSTEM_TESTS)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_CONSOLIDATE_TEST_CASES)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_CLEAR_TEST_CASES)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_SYSTEM_TEST)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_CANCEL_SYSTEM_TEST_CASE)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_END_CONTEST)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_ALLOCATE_PRIZES)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_RUN_RATINGS)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_INSERT_PRACTICE_ROOM)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_ANNOUNCE_ADVANCING_CODERS)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_ADVANCE_WL_CODERS)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_DISCONNECT_CLIENT)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_LOAD_ROUND)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_UNLOAD_ROUND)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_SHUTDOWN)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_GARBAGE_COLLECTION)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_START_REPLAY_LISTENER)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_START_REPLAY_RECEIVER)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_FORWARDING)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_ADMIN_FORWARDING)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_SPEC_APP_START_ROTATE)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_SPEC_APP_STOP_ROTATE)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_SPEC_APP_SHOW_ROOM)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_RESTART_EVENT_TOPIC_LISTENER)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_CACHED_CODER)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_CACHED_CODER_PROBLEM)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_CACHED_PROBLEM)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_CACHED_REGISTRATION)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_CACHED_ROOM)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_CACHED_ROUND)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_CACHED_USER)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_BAN_IP)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_GRANT_ADMIN_AUTHORITY)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_REVOKE_ADMIN_AUTHORITY)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_SET_USER_STATUS)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_CONTEST_MANAGEMENT)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_MODERATED_CHAT)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_LOGGING)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_SEND_GLOBAL_BROADCAST)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_SEND_COMPONENT_BROADCAST)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_SEND_ROUND_BROADCAST)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_CHAT_VIEW)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_NEW_ID)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_SECURITY_MANAGEMENT)));

       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_BACKUP_TABLES)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_RESTORE_TABLES)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_SET_ROUND_TERMS)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_RESTART_COMPILERS)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_RESTART_TESTERS)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_RESTART_ALL)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_WAREHOUSE_LOAD_AGGREGATE)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_WAREHOUSE_LOAD_CODER)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_WAREHOUSE_LOAD_EMPTY)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_WAREHOUSE_LOAD_RANK)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_WAREHOUSE_LOAD_REQUESTS)));
       assertTrue("test perm", AdminConstants.isNonRoundSpecificPermission(
               AdminConstants.getPermission(AdminConstants.REQUEST_WAREHOUSE_LOAD_ROUND)));
    }

}
