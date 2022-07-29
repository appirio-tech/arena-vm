/*
 * MPSQASTestResult
 * 
 * Created 04/25/2006
 */
package com.topcoder.server.ejb.MPSQASServices.event;

import java.io.Serializable;

/**
 * MPSQAS test result. Object containing 
 * Test Result String for a test group scheduled in
 * the MPSQASServices
 *  
 * @author Diego Belfer (mural)
 * @version $Id: MPSQASTestResult.java 45114 2006-05-10 16:30:47Z thefaxman $
 */
public class MPSQASTestResult implements Serializable {
    
    /**
     * Id of user who scheduled the test group 
     */
    private int userId;
    
    /**
     * Text representation of the test result
     */
    private String resultText;

    
    public MPSQASTestResult() {
    }
    
    
    public MPSQASTestResult(int userId, String resultText) {
        this.userId = userId;
        this.resultText = resultText;
    }
    
    /**
     * @return Returns the resultText.
     */
    public String getResultText() {
        return resultText;
    }
    /**
     * @param resultText The resultText to set.
     */
    public void setResultText(String resultText) {
        this.resultText = resultText;
    }
    /**
     * @return Returns the userId.
     */
    public int getUserId() {
        return userId;
    }
    /**
     * @param userId The userId to set.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
}
