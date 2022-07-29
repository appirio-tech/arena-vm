package com.topcoder.server.ejb.AdminServices;

import org.apache.log4j.Logger;

import java.util.Properties;
import java.util.Hashtable;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.rmi.RemoteException;
import java.rmi.ServerException;

/**
 * EJB failure tests for warehouse load procedure
 *
 * @see AdminServices#loadWarehouseData(String, java.util.Hashtable)
 *
 * @author Giorgos Zervas
 */
public class WarehouseLoadEJBTest extends AdminServicesTest {
    /**
     * The logger for these tests
     */
    protected Logger log = Logger.getLogger(WarehouseLoadEJBTest.class);

    /**
     * Constructs a test case with the given name
     *
     * @param method the name
     */
    public WarehouseLoadEJBTest(String method) {
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
    }

    /**
     * Test performing a warehouse load for null class. It should cause
     * an RemoteException which we catch. No other exception
     * should happen.
     *
     * @throws Exception
     */
    public void testWarehouseLoadNullClass() throws Exception {
        boolean hasException = false;

        try {
            adminServices.loadWarehouseData(null, new Hashtable(), 0);
        } catch (RemoteException e) {
            assertNotNull("Should have caused an RemoteException exception", e);
            log.warn("Caught RemoteException as expected: " + e.getMessage());
            assertNotNull(e.getCause());
            assertEquals(IllegalArgumentException.class, e.getCause().getClass());
            hasException = true;
        }
        assertEquals(true, hasException);    }

    /**
     * Test performing a warehouse load for non-existent class. It should cause
     * an RemoteException which we catch. No other exception
     * should happen.
     *
     * @throws Exception
     */
    public void testWarehouseLoadNonExistentClass() throws Exception {
        boolean hasException = false;

        try {
            adminServices.loadWarehouseData("com.topcoder.utilities.dwload.TCNonExistent", new Hashtable(), 0);
        } catch (ClassNotFoundException e) {
            assertNotNull("Should have caused an ClassNotFoundException exception", e);
            log.warn("Caught ClassNotFoundException as expected: " + e.getMessage());            
            hasException = true;
        }
        assertEquals(true, hasException);    }

    /**
     * Test performing a warehouse load with null params. It should cause
     * an RemoteException which we catch. No other exception
     * should happen.
     *
     * @throws Exception
     */
    public void testWarehouseLoadNullParams() throws Exception {
        boolean hasException = false;

        try {
            adminServices.loadWarehouseData("com.topcoder.utilities.dwload.TCLoadEmpty", null, 0);
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
