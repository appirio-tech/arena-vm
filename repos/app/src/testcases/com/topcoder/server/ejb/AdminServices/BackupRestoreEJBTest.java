package com.topcoder.server.ejb.AdminServices;

import com.topcoder.server.AdminListener.AdminConstants;
import com.topcoder.server.common.BackupCopy;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

/**
 * EJB Tests for backup restore procedure
 *
 * @see AdminServices#backupTables(int, java.util.Set, String)
 * @see AdminServices#restoreTables(int, java.util.Set)
 * @see AdminServices#getBackupCopies(int)
 *
 * @author Giorgos Zervas
 */
public class BackupRestoreEJBTest extends AdminServicesTest {
    /**
     * Default round id on which the test will be executed
     */
    private static int roundID = 4475;

    /**
     * The logger for these tests
     */
    protected Logger log = Logger.getLogger(BackupRestoreEJBTest.class);

    /**
     * Constructs a test case with the given name
     *
     * @param method the name
     */
    public BackupRestoreEJBTest(String method) {
        super(method);
    }

    /**
     * Setup the test. Read the testdata.properties file which
     * provides the test configuration. The full path and filename
     * of the testdata.properties file can be retreived from the
     * system property with the same name.
     */
    protected void setUp() {
        super.setUp();
        String propertiesFilename = System.getProperty("testdata.properties");
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(propertiesFilename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        roundID = Integer.parseInt(properties.getProperty("roundID", String.valueOf(roundID)));
    }

    /**
     * Test backup of all tables. This involves testing whether
     * newly created backup id is stored correctly on the db, if the
     * table set to be backed up is complete, and the comment is the same.
     *
     * @throws Exception
     */
    public void testFullBackup() throws Exception {
        final String comment = "complete backup test";
        log.info(comment);
        HashSet tables = new HashSet();
        for (int i = 0; i < AdminConstants.TABLES_TO_BACKUP.length; i++)
            tables.add(AdminConstants.TABLES_TO_BACKUP[i]);

        long backupID = adminServices.backupTables(roundID, tables, comment);
        log.warn("backup created with id " + backupID);

        List backupCopies = adminServices.getBackupCopies(roundID);
        log.warn("backup copies for round " + roundID + " retreived");

        BackupCopy backupCopy = null;
        Iterator i = backupCopies.iterator();
        while (i.hasNext()) {
            BackupCopy copy = (BackupCopy) i.next();
            // every backup copy should have correct round id
            assertEquals("Backup copy " + copy.getID() + " has wrong round id " + copy.getRoundID(), roundID, copy.getRoundID());
            // and one of them should have the same backup id as the one created above
            if (copy.getID() == backupID) {
                backupCopy = copy;
            }
        }
        assertNotNull("backup copies for round " + roundID + " doesn't contain backup with id " + backupID, backupCopy);

        // now that we found the backup copy check that it contains all the tables
        String[] actualTables = backupCopy.getTableNames();
        Arrays.sort(actualTables);
        String[] expectedTables = (String[]) tables.toArray(new String[tables.size()]);
        Arrays.sort(expectedTables);

        assertEquals("backup copy " + backupID + " for round " + roundID + " does not contain same number of tables", expectedTables.length, actualTables.length);
        assertEquals("backup copy " + backupCopy.getID() + "has wrong comment", comment, backupCopy.getComment());

        for (int j = 0; j < expectedTables.length; j++)
            assertEquals("backup copy " + backupID + " for round " + roundID + " does not contain same table set", expectedTables[j], actualTables[j]);

        // now try some restores
        log.warn("attempting complete restore");
        adminServices.restoreTables(backupCopy.getID(), new TreeSet(Arrays.asList(AdminConstants.TABLES_TO_BACKUP)));

        log.warn("attempting empty restore");
        adminServices.restoreTables(backupCopy.getID(), new TreeSet());
    }

    /**
     * Test backup of half the tables. This involves testing whether
     * newly created backup id is stored correctly on the db, if the
     * table set to be backed up is complete, and the comment is the same.
     *
     * @throws Exception
     */
    public void testPartialBackup() throws Exception {
        final String comment = "partial backup test";
        log.info(comment);
        HashSet tables = new HashSet();
        for (int i = 0; i < AdminConstants.TABLES_TO_BACKUP.length; i += 2)
            tables.add(AdminConstants.TABLES_TO_BACKUP[i]);

        long backupID = adminServices.backupTables(roundID, tables, comment);
        log.warn("backup created with id " + backupID);

        List backupCopies = adminServices.getBackupCopies(roundID);
        log.warn("backup copies for round " + roundID + " retrieved");

        BackupCopy backupCopy = null;
        Iterator i = backupCopies.iterator();
        while (i.hasNext()) {
            BackupCopy copy = (BackupCopy) i.next();
            // every backup copy should have correct round id
            assertEquals("Backup copy " + copy.getID() + " has wrong round id " + copy.getRoundID(), roundID, copy.getRoundID());
            // and one of them should have the same backup id as the one created above
            if (copy.getID() == backupID) {
                backupCopy = copy;
            }
        }
        assertNotNull("backup copies for round " + roundID + " doesn't contain backup with id " + backupID, backupCopy);

        // now that we found the backup copy check that it contains all the tables
        String[] actualTables = backupCopy.getTableNames();
        Arrays.sort(actualTables);
        String[] expectedTables = (String[]) tables.toArray(new String[tables.size()]);
        Arrays.sort(expectedTables);

        assertEquals("backup copy " + backupID + " for round " + roundID + " does not contain same number of tables", expectedTables.length, actualTables.length);
        assertEquals("backup copy " + backupCopy.getID() + "has wrong comment", comment, backupCopy.getComment());

        for (int j = 0; j < expectedTables.length; j++)
            assertEquals("backup copy " + backupID + " for round " + roundID + " does not contain same table set", expectedTables[j], actualTables[j]);

        // now try some restores
        log.warn("attempting complete restore");
        adminServices.restoreTables(backupCopy.getID(), new TreeSet(Arrays.asList(AdminConstants.TABLES_TO_BACKUP)));

        log.warn("attempting empty restore");
        adminServices.restoreTables(backupCopy.getID(), new TreeSet());
    }

    /**
     * Test empty backup. This involves testing whether
     * newly created backup id is stored correctly on the db, if the
     * table set to be backed up is complete, and the comment is the same.
     * <p></p>
     * <b>NOTE:</b> This is not allowed to happen through the amdin monitor client
     * but still the code should behave correctly
     *
     * @throws Exception
     */
    public void testEmptyBackup() throws Exception {
        final String comment = "empty backup test";
        log.info(comment);
        HashSet tables = new HashSet();

        long backupID = adminServices.backupTables(roundID, tables, comment);
        log.warn("backup created with id " + backupID);

        List backupCopies = adminServices.getBackupCopies(roundID);
        log.warn("backup copies for round " + roundID + " retrieved");

        BackupCopy backupCopy = null;
        Iterator i = backupCopies.iterator();
        while (i.hasNext()) {
            BackupCopy copy = (BackupCopy) i.next();
            // every backup copy should have correct round id
            assertEquals("Backup copy " + copy.getID() + " has wrong round id " + copy.getRoundID(), roundID, copy.getRoundID());
            // and one of them should have the same backup id as the one created above
            if (copy.getID() == backupID) {
                backupCopy = copy;
            }
        }
        assertNotNull("backup copies for round " + roundID + " don't contain backup with id " + backupID, backupCopy);
        assertEquals("backup copy " + backupCopy.getID() + "has wrong comment", comment, backupCopy.getComment());

        // now that we found the backup copy check that it contains no tables
        String[] actualTables = backupCopy.getTableNames();
        assertEquals("backup copy " + backupID + " for round " + roundID + " does is not empty", 0, actualTables.length);

        // now try some restores
        log.warn("attempting complete restore");
        adminServices.restoreTables(backupCopy.getID(), new TreeSet(Arrays.asList(AdminConstants.TABLES_TO_BACKUP)));

        log.warn("attempting empty restore");
        adminServices.restoreTables(backupCopy.getID(), new TreeSet());
    }

    /**
     * Test creating a backup for a non-existing round. It should cause
     * an SQLException which we catch. No other exception
     * should happen.
     *
     * @throws Exception if any unexpected exception happens
     */
    public void testBackupNonExistingRound() throws Exception {
        boolean hasException = false;

        final String comment = "backup non existing round test";
        log.info(comment);
        HashSet tables = new HashSet();
        for (int i = 0; i < AdminConstants.TABLES_TO_BACKUP.length; i += 2)
            tables.add(AdminConstants.TABLES_TO_BACKUP[i]);

        try {
            adminServices.backupTables(-1, tables, comment);
        } catch (RemoteException e) {
            assertNotNull("Should have caused an RemoteException", e);
            log.warn("Caught RemoteException as expected: " + e.getMessage());
            assertNotNull(e.getCause());
            assertEquals(SQLException.class, e.getCause().getClass());
            hasException = true;
        }
        assertEquals(true, hasException);
    }

    /**
     * Test creating a backup with null tables. It should cause
     * an RemoteException which we catch. No other exception
     * should happen.
     *
     * @throws Exception if any unexpected exception happens
     */
    public void testBackupNullTables() throws Exception {
        boolean hasException = false;

        final String comment = "backup null tables test";
        log.info(comment);

        try {
            adminServices.backupTables(roundID, null, comment);
        } catch (RemoteException e) {
            assertNotNull("Should have caused an RemoteException exception", e);
            log.warn("Caught RemoteException as expected: " + e.getMessage());
            assertNotNull(e.getCause());
            assertEquals(IllegalArgumentException.class, e.getCause().getClass());
            hasException = true;
        }
        assertEquals(true, hasException);
    }

    /**
     * Test creating a backup with null comment. No exception should happen.
     *
     * @throws Exception if any unexpected exception happens
     */
    public void testBackupNullComment() throws Exception {
        final String comment = "backup null comment test";
        log.info(comment);
        HashSet tables = new HashSet();
        for (int i = 0; i < AdminConstants.TABLES_TO_BACKUP.length; i += 2)
            tables.add(AdminConstants.TABLES_TO_BACKUP[i]);

        adminServices.backupTables(roundID, tables, null);
    }

    /**
     * Test restoring a backup for a non-existing round. It should cause
     * an SQLException which we catch. No other exception
     * should happen.
     *
     * @throws Exception if any unexpected exception happens
     */
    public void testRestoreNonExistingRound() throws Exception {
        boolean hasException = false;

        final String comment = "restore non existing round test";
        log.info(comment);
        HashSet tables = new HashSet();
        for (int i = 0; i < AdminConstants.TABLES_TO_BACKUP.length; i += 2)
            tables.add(AdminConstants.TABLES_TO_BACKUP[i]);

        try {
            adminServices.restoreTables(-1, tables);
        } catch (RemoteException e) {
            assertNotNull("Should have caused an RemoteException", e);
            log.warn("Caught RemoteException as expected: " + e.getMessage());
            assertNotNull(e.getCause());
            assertEquals(IllegalArgumentException.class, e.getCause().getClass());
            hasException = true;
        }
        assertEquals(true, hasException);
    }

    /**
     * Test restoring a backup with null tables. It should cause
     * an RemoteException which we catch. No other exception
     * should happen.
     *
     * @throws Exception if any unexpected exception happens
     */
    public void testRestoreNullTables() throws Exception {
        boolean hasException = false;

        final String comment = "restore null tables test";
        log.info(comment);

        try {
            adminServices.restoreTables(roundID, null);
        } catch (RemoteException e) {
            assertNotNull("Should have caused an RemoteException exception", e);
            log.warn("Caught RemoteException as expected: " + e.getMessage());
            assertNotNull(e.getCause());
            assertEquals(IllegalArgumentException.class, e.getCause().getClass());
            hasException = true;
        }
        assertEquals(true, hasException);
    }

}
