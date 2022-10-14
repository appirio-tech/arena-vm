package com.topcoder.server.ejb.AdminServices;

/*
 * AdminServicesTest.java
 *
 * Serves as a superclass for all tests of the AdminServices EJB.  Performs
 * common tasks such as setting up access to the EJB.
 *
 * @author John Waymouth (coderlemming)
 * 6/23/02
 */


import javax.naming.Context;
import javax.rmi.PortableRemoteObject;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.topcoder.shared.common.TCContext;


public abstract class AdminServicesTest extends TestCase {

    protected AdminServices adminServices = null;
    protected Logger log = Logger.getLogger(AdminServicesTest.class);

	/**
	 * Constructs a test case with the given name.
	 */
    public AdminServicesTest(String method) {
        super(method);
    }

    protected void setUp() {
        try {
            Context c = TCContext.getEJBContext();
            Object o = c.lookup("com.topcoder.server.ejb.AdminServicesHome");

/*            System.out.println("GOT: " + o);
            Class clazz = o.getClass();
            Class[] interfaces = clazz.getInterfaces();
            System.out.println("TARGET: " + AdminServicesHome.class);
            for (int i = 0; i < interfaces.length; i++) {
                Class anInterface = interfaces[i];
                if (anInterface.equals(AdminServicesHome.class)) {
                    System.out.println("MATCH: " + anInterface);
                }
                else {
                    System.out.println("NO MATCH" + anInterface);
                }
            }

*/
            AdminServicesHome ash = (AdminServicesHome) PortableRemoteObject.narrow(o, AdminServicesHome.class);
            adminServices = ash.create();
            c.close();
        } catch (Exception e) {
            log.error("Error setting up EJB connection!", e);
            fail("Exception while trying to set up EJB connection.");
        }
    }

    protected void tearDown() {
        adminServices = null;
    }
}
