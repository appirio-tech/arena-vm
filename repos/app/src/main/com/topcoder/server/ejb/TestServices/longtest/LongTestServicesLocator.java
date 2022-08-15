/*
 * LongTestServicesLocator
 * 
 * Created 04/11/2006
 */
package com.topcoder.server.ejb.TestServices.longtest;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import com.topcoder.server.ejb.TestServices.TestServicesLocator;


/**
 * Helper class to obtain the LongTestServices 
 *
 * @author Diego Belfer (mural)
 * @version $Id: LongTestServicesLocator.java 54869 2006-12-01 18:02:46Z thefaxman $
 */
public class LongTestServicesLocator {
    
    public static LongTestServices getService() throws NamingException, RemoteException, CreateException {
        return TestServicesLocator.getService();
    }
}
