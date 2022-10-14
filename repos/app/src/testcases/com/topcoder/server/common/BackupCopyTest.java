package com.topcoder.server.common;

import junit.framework.TestCase;

import java.sql.Timestamp;
import java.util.TreeSet;
import java.util.Iterator;

/**
 * Test the BackupCopy class for failure and accuracy
 */
public class BackupCopyTest extends TestCase {

    /**
     * Test that with valid argument the BackupCopy class
     * behaves accurately
     */
    public void testAccuracy() {
        int roundID = 1;
        int backupID = 2;
        String comment = "comment";
        Timestamp created = new Timestamp(System.currentTimeMillis());
        TreeSet tableNames = new TreeSet();
        tableNames.add("a");
        tableNames.add("b");
        tableNames.add("c");
        tableNames.add("d");

        BackupCopy backupCopy = new BackupCopy(backupID, roundID, created, comment);
        Iterator i = tableNames.iterator();
        while (i.hasNext())
            backupCopy.addTableName((String) i.next());

        assertEquals(backupID, backupCopy.getID());
        assertEquals(roundID, backupCopy.getRoundID());
        assertEquals(comment, backupCopy.getComment());
        assertEquals(created, backupCopy.getCreatedDate());
        assertEquals("a", backupCopy.getTableNames()[0]);
        assertEquals("b", backupCopy.getTableNames()[1]);
        assertEquals("c", backupCopy.getTableNames()[2]);
        assertEquals("d", backupCopy.getTableNames()[3]);
    }

    /**
     * Test of illegal argument negative backup id
     */
    public void testIllegalBackupId() {
        boolean hasException = false;

        try {
            new BackupCopy(-1, 1, new Timestamp(System.currentTimeMillis()), "comment");
        } catch (IllegalArgumentException e) {
            assertNotNull("Should have thrown IllegalArgumentException", e);
            hasException = true;
        }
        assertTrue(hasException);
    }

    /**
     * Test of illegal argument negative round id
     */
    public void testIllegalRoundId() {
        boolean hasException = false;

        try {
            new BackupCopy(1, -1, new Timestamp(System.currentTimeMillis()), "comment");
        } catch (IllegalArgumentException e) {
            assertNotNull("Should have thrown IllegalArgumentException", e);
            hasException = true;
        }
        assertTrue(hasException);

    }

    /**
     * Test of illegal argument negative null created timestamp
     */
    public void testNullTimestamp() {
        boolean hasException = false;

        try {
            new BackupCopy(1, 1, null, "comment");
        } catch (IllegalArgumentException e) {
            assertNotNull("Should have thrown IllegalArgumentException", e);
            hasException = true;
        }
        assertTrue(hasException);
    }

    /**
     * Test of argument null comment. This should work fine.
     */
    public void testNullComment() {
        boolean hasException = false;

        try {
            new BackupCopy(1, 1, new Timestamp(System.currentTimeMillis()), null);
        } catch (IllegalArgumentException e) {
            assertNull("Should not have thrown IllegalArgumentException", e);
            hasException = true;
        }
        assertTrue(!hasException);
    }

    /**
     * Test of illegal argument null tableName to backupCopy.addTableName(String)
     */
    public void testAddNullTableName() {
        boolean hasException = false;

        try {
            BackupCopy backupCopy = new BackupCopy(1, 1, new Timestamp(System.currentTimeMillis()), "comment");
            backupCopy.addTableName(null);
        } catch (IllegalArgumentException e) {
            assertNotNull("Should have thrown IllegalArgumentException", e);
            hasException = true;
        }
        assertTrue(hasException);
    }

    /**
     * Test of illegal argument null tableName to backupCopy.addTableName(String)
     */
    public void testAddEmptyTableName() {
        boolean hasException = false;

        try {
            BackupCopy backupCopy = new BackupCopy(1, 1, new Timestamp(System.currentTimeMillis()), "comment");
            backupCopy.addTableName("");
        } catch (IllegalArgumentException e) {
            assertNotNull("Should have thrown IllegalArgumentException", e);
            hasException = true;
        }
        assertTrue(hasException);
    }

    /**
     * Test of illegal argument tableName consisting of just spaces to backupCopy.addTableName(String)
     */
    public void testAddOnlySpacesTableName() {
        boolean hasException = false;

        try {
            BackupCopy backupCopy = new BackupCopy(1, 1, new Timestamp(System.currentTimeMillis()), "comment");
            backupCopy.addTableName("   ");
        } catch (IllegalArgumentException e) {
            assertNotNull("Should have thrown IllegalArgumentException", e);
            hasException = true;
        }
        assertTrue(hasException);
    }
}
