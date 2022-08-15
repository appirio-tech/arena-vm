package com.topcoder.server.AdminListener;

import javax.naming.Context;
import javax.naming.NamingException;

import junit.framework.TestCase;

import com.topcoder.security.TCSubject;
import com.topcoder.server.AdminListener.request.ContestManagementRequest;
import com.topcoder.server.AdminListener.request.GetNewIDRequest;
import com.topcoder.server.AdminListener.request.GetPrincipalsRequest;
import com.topcoder.server.AdminListener.request.SaveRoundRoomAssignmentRequest;
import com.topcoder.server.AdminListener.request.SecurityManagementRequest;
import com.topcoder.server.AdminListener.response.GetNewIDResponse;
import com.topcoder.server.AdminListener.response.GetPrincipalsResponse;
import com.topcoder.server.AdminListener.response.SaveRoundRoomAssignmentAck;
import com.topcoder.server.AdminListener.response.SecurityManagementAck;
import com.topcoder.server.contest.RoundRoomAssignment;
import com.topcoder.server.ejb.AdminServices.AdminServices;
import com.topcoder.server.ejb.AdminServices.AdminServicesHome;
import com.topcoder.shared.common.TCContext;
import com.topcoder.shared.util.DBMS;

/**
 * TestCase for ContestManagementProcessor class. This class will test the 
 * new functionality that was added for AdminTool 2.0. Specifically, the 
 * processing of the new requests.
 *
 * Copyright (c) 2003 TopCoder, Inc.  All rights reserved.
 * 
 * @author  TCSDESIGNER
 * @version 2.0 11/03/2003
 * @since   Admin Tool 2.0
 */
public class ContestManagementProcessorTest extends TestCase {

    /**
     * the class we are testing
     */
    private ContestManagementProcessor processor = null;
    
    /**
     * we need admin services also
     */
    private static AdminServices service = null;
    private static Context ctx = null;
    
    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        processor = new ContestManagementProcessor();
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
        // install it into the processor
        processor.setAdminServices(service);
    }

    /**
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        processor = null;
    }

    /**
     * test the processRequest method
     */
    public void testProcessRequestGetNewID() {
        GetNewIDRequest req = new GetNewIDRequest(DBMS.ROUND_SEQ);
        GetNewIDResponse rep = null;
        
        Object o = processor.processRequest(req);
        assertTrue( "check type", o instanceof GetNewIDResponse );
        rep = (GetNewIDResponse)o;
        assertTrue("got a new id" , rep.getNewId() != 0 );
        
        // check for unhandled requests
        ContestManagementRequest test = null;
        assertTrue("should return null", processor.processRequest(test) == null);
    }

    /**
     * test the SaveRoundRoomAssignement request
     */
    public void testProcessRequestSaveRoundRoomAssignment() {
        RoundRoomAssignment rra = new RoundRoomAssignment(1);
        SaveRoundRoomAssignmentRequest req = new SaveRoundRoomAssignmentRequest(rra);
        SaveRoundRoomAssignmentAck rep = null;
        
        Object o = processor.processRequest(req);
        assertTrue( "check type", o instanceof SaveRoundRoomAssignmentAck );
        rep = (SaveRoundRoomAssignmentAck)o;
        assertTrue("check round id" , rep.getRoundID() == 1 );
    }
}
