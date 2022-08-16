package com.topcoder.server.ejb.AdminServices;

import java.util.HashSet;

import javax.naming.Context;
import javax.naming.NamingException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;

import com.topcoder.shared.common.TCContext;
import com.topcoder.shared.util.DBMS;

/**
 * TestCase for AdminServices EJB.
 *
 * Copyright (c) 2003 TopCoder, Inc.  All rights reserved.
 * 
 * @author  TCSDESIGNER
 * @version 2.0 11/03/2003
 * @since   Admin Tool 2.0
 */

public final class AdminServicesTest2 extends TestCase {

    private static Logger log = Logger.getLogger(AdminServicesTest2.class);

    private static AdminServices service = null;
    private static Context ctx = null;

    public static Test suite()
    {
        return new TestSuite(AdminServicesTest2.class);
    }

    /**
     * setup the test cases by creating the services bean 
     */
    public void setUp() {
        Context ctx = null;
        AdminServicesHome home = null;
        try {
            ctx = TCContext.getEJBContext();
        } catch (NamingException ex) {
            fail("Error getting EJB Context");
        }
        try {
            home = (AdminServicesHome)
                ctx.lookup("com.topcoder.server.ejb.AdminServicesHome");
            
        } catch (NamingException ex) {
            fail("Error getting EJB Context1");
        }
        try {
            service = home.create();
        } catch (javax.ejb.CreateException ce ) {
            fail("Error creating bean2");
        } catch (java.rmi.RemoteException re ) {
            fail("Error remoting the bean2");
        }
        
    }

    /**
     * clear globals
     */
    public void tearDown() {
        service = null;
        ctx = null;
    }
    
    /**
     * gets two id's and verifies they are different (accuracy)
     */
    public static void testSequence() {

        try {
            int id = service.getNewID( DBMS.CONTEST_SEQ );
            log.info("id = " + id);
            int id2 = service.getNewID( DBMS.CONTEST_SEQ );
            log.info("id = " + id2);
            assertTrue("ids cannot be same", id != id2);
        } catch (Exception aex) {
            fail("Sequence " + aex.toString());
        }
    }

    /**
     * creates a series of ID's then makes sure they are all unique.
     */
    public static void testIDGenerator() {
        HashSet set = new HashSet();
        try {
            for (int i = 0; i < 10; i++) {
                int seqId = service.getNewID( DBMS.CONTEST_SEQ );
                set.add(new Integer(seqId));
            }
            assertTrue("set contains unique items", set.size() == 10);
        } catch (Exception e) {
            fail("IDGeneratorl exception " + e);
        }

    }

}

