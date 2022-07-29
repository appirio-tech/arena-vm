/*
 * LongCoderHistory
 * 
 * Created 06/08/2007
 */
package com.topcoder.server.ejb.TestServices;

import java.io.Serializable;


/**
 * Contains submission history for a Coder during a Long Contest Round.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: LongCoderHistory.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class LongCoderHistory implements Serializable {
    private LongSubmissionData[] fullSubmissions;
    private LongSubmissionData[] exampleSubmissions;

    public LongCoderHistory() {
    }

    /**
     * Returns an array containing Example submissions. From the  most recent submissions
     * to the oldest one.
     * 
     * @return a non null array
     */
    public LongSubmissionData[] getExampleSubmissions() {
        return exampleSubmissions;
    }

    public void setExampleSubmissions(LongSubmissionData[] exampleSubmissions) {
        this.exampleSubmissions = exampleSubmissions;
    }

    /**
     * Returns an array containing Full submissions. From the  most recent submissions
     * to the oldest one.
     * 
     * @return a non null array
     */
    public LongSubmissionData[] getFullSubmissions() {
        return fullSubmissions;
    }

    public void setFullSubmissions(LongSubmissionData[] fullSubmissions) {
        this.fullSubmissions = fullSubmissions;
    }
}