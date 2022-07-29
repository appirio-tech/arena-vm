/*
 * TestGroupFinalizedProcessor
 * 
 * Created 04/25/2006
 */
package com.topcoder.server.ejb.TestServices.longtest.event;

/**
 * Inteface used to notify test group finalization  
 * 
 * @author Diego Belfer (Mural)
 * @version $Id: TestGroupFinalizedProcessor.java 54869 2006-12-01 18:02:46Z thefaxman $
 */
public interface TestGroupFinalizedProcessor {
    
    /**
     * Called to indicate test group finalization
     * 
     * @param testGroupId testGroup finalized
     */
    public void testGroupFinalized(int testGroupId);
}