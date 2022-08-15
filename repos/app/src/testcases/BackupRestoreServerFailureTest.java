/**
 * BackupRestoreFailureTest.java 
 *
 * This test class provides a series of failure tests for Backup/Restore functionality
 * Note that thrown exceptions are RemoteException instances because tested objects are EJB objects.
 * RemoteException should contain ServerException with IllegalArgumentException inside.
 *
 * @author aksonov
 * 
 * Copyright é 2003, TopCoder, Inc. All rights reserved
 */

import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.util.HashSet;

import com.topcoder.server.ejb.AdminServices.AdminServicesTest;

public class BackupRestoreServerFailureTest extends AdminServicesTest {

    public BackupRestoreServerFailureTest(String method) {
        super(method);
    }
    /**
     *  Tests restoreTables(in backupID:int, tableNames:Set) method with second null parameter
     */
    public void testRestoreTablesSecondNull() throws Exception {
        try {
            adminServices.restoreTables(1, null);
        } catch (RemoteException e){
			assertNotNull(e.getCause());
			assertEquals(ServerException.class, e.getCause().getClass());
			assertNotNull(e.getCause().getCause());
			assertEquals(IllegalArgumentException.class, e.getCause().getCause().getClass());
            return;
        }
        fail("IllegalArgumentException should be thrown!");
    }

    /**
     *  Tests backupTables(in roundID:int, tableNames:Set, comment:String) with second null parameter
     */
    public void testBackupTablesSecondNull() throws Exception {
        try {
            adminServices.backupTables(1, null, "");
        } catch (RemoteException e){
        	assertNotNull(e.getCause());
        	assertEquals(ServerException.class, e.getCause().getClass());
			assertNotNull(e.getCause().getCause());
			assertEquals(IllegalArgumentException.class, e.getCause().getCause().getClass());
            return;
        }
		fail("IllegalArgumentException should be thrown!");
    }

	/**
	 *  Tests backupTables(in roundID:int, tableNames:Set, comment:String) with one null table
	 */
	public void testBackupTablesNullTable() throws Exception {
		try {
			HashSet set = new HashSet();
			set.add(null);
			adminServices.backupTables(1, set, null);
		} catch (RemoteException e){
			assertNotNull(e.getCause());
			assertEquals(ServerException.class, e.getCause().getClass());
			assertNotNull(e.getCause().getCause());
			assertEquals(IllegalArgumentException.class, e.getCause().getCause().getClass());
			return;
		}
		fail("IllegalArgumentException should be thrown!");
	}

}
