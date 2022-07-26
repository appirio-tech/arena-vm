/**
 * BackupRestoreFailureTest.java 
 *
 * This test class provides a series of failure tests for Backup/Restore functionality
 *
 * @author aksonov
 */


import java.sql.Timestamp;

import junit.framework.TestCase;

import com.topcoder.server.AdminListener.request.BackupTablesRequest;
import com.topcoder.server.AdminListener.request.RestoreTablesRequest;
import com.topcoder.server.common.BackupCopy;

public class BackupRestoreFailureTest extends TestCase{

    /**
     *  Tests BackupTablesRequest.addTableName(tableName:String) with null string
     */
    public void testBackupTablesSecondNull() {
        try {
            new BackupTablesRequest(1).addTableName(null);
            fail("IllegalArgumentException should be thrown!");
        } catch (IllegalArgumentException e){}

    }

    /**
     *  Tests BackupTablesRequest.addTableName(tableName:String) with empty string
     */
    public void testBackupTablesSecondEmpty() {
        try {
            new BackupTablesRequest(1).addTableName("");
            fail("IllegalArgumentException should be thrown!");
        } catch (IllegalArgumentException e){}
    }

    /**
     *  Tests RestoreTablesRequest.addTableName(tableName:String) with null string
     */
    public void testRestoreTablesSecondNull() {
        try {
            new RestoreTablesRequest(1).addTableName(null);
            fail("IllegalArgumentException should be thrown!");
        } catch (IllegalArgumentException e){}
    }

    /**
     *  Tests RestoreTablesRequest.addTableName(tableName:String) with empty string
     */
    public void testRestoreTablesSecondEmpty() throws Exception {
        try {
            new RestoreTablesRequest(1).addTableName("");
            fail("IllegalArgumentException should be thrown!");
        } catch (IllegalArgumentException e){}
    }


    /**
     *  Tests constructor BackupCopy(in id:int, in roundID:int, created:Timestamp, comment:String)  with negative id
     */
    public void testBackupCopyIdNegative()  {
        try {
            new BackupCopy(-1, 1, new Timestamp(0), "test");
            fail("IllegalArgumentException should be thrown!");
        } catch (IllegalArgumentException e){}
    }

    /**
     *  Tests constructor BackupCopy(in id:int, in roundID:int, created:Timestamp, comment:String)  with negative roundID
     */
    public void testBackupCopyRoundIdNegative() {
        try {
            new BackupCopy(1, -1, new Timestamp(0), "test");
            fail("IllegalArgumentException should be thrown!");
        } catch (IllegalArgumentException e){}
    }

    /**
     *  Tests constructor BackupCopy(in id:int, in roundID:int, created:Timestamp, comment:String)  with null "created" parameter
     */
    public void testBackupCopyCreatedNull()  {
        try {
            new BackupCopy(1, 1, null, "test");
            fail("IllegalArgumentException should be thrown!");
        } catch (IllegalArgumentException e){}
    }


    /**
     *  Tests BackupCopy.addTableName(table: String)  with null string
     */
    public void testBackupCopyAddTableNull()  {
        try {
            new BackupCopy(1, 1, new Timestamp(0), "test").addTableName(null);
            fail("IllegalArgumentException should be thrown!");
        } catch (IllegalArgumentException e){}
    }


    /**
     *  Tests BackupCopy.addTableName(table: String)  with empty string
     */
    public void testBackupCopyAddTableEmpty()  {
        try {
            new BackupCopy(1, 1, new Timestamp(0), "test").addTableName("");
            fail("IllegalArgumentException should be thrown!");
        } catch (IllegalArgumentException e){}
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(BackupRestoreFailureTest.class);
    }

}
