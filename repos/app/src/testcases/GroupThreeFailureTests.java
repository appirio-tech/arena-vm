/**
 * GroupThreeFailureTests.java
 *
 * Test suite for failure tests for Group N3
 *  
 * @author aksonov
 */
import junit.framework.Test;
import junit.framework.TestSuite;

public class GroupThreeFailureTests {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(new TestSuite(BackupRestoreFailureTest.class));
		suite.addTest(new TestSuite(BackupRestoreServerFailureTest.class));
		suite.addTest(new TestSuite(WarehouseFailureTest.class));
		return suite;
	}
}
