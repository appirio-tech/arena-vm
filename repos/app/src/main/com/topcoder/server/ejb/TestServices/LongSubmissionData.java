/*
 * LongSubmissionData
 * 
 * Created 06/14/2007
 */
package com.topcoder.server.ejb.TestServices;

import java.io.Serializable;
import java.util.Date;

/**
 * @autor Diego Belfer (Mural)
 * @version $Id: LongSubmissionData.java 66703 2007-10-25 20:46:59Z thefaxman $
 */
public class LongSubmissionData implements Serializable {
    private int  number;
    private Date timestamp;
    private int  languageId;
    private double score;
    private boolean hasPendingTests;
    private String text;
    
    public LongSubmissionData() {
    }
    
    public LongSubmissionData(int number, Date timestamp, int languageId, double score, boolean hasPendingTests) {
       this(number, timestamp, languageId, score, hasPendingTests, null);
    }
    
    public LongSubmissionData(int number, Date timestamp, int languageId, double score, boolean hasPendingTests, String text) {
        this.number = number;
        this.timestamp = timestamp;
        this.languageId = languageId;
        this.score = score;
        this.hasPendingTests = hasPendingTests;
        this.text = text;
    }

    public double getScore() {
        return score;
    }
    
    public void setScore(double score) {
        this.score = score;
    }
    
    public int getLanguageId() {
        return languageId;
    }

    public int getNumber() {
        return number;
    }

    public Date getTimestamp() {
        return timestamp;
    }
    public boolean hasPendingTests() {
        return hasPendingTests;
    }
    
    public void setHasPendingTests(boolean hasPendingTests) {
        this.hasPendingTests = hasPendingTests;
    }

    public String getText() {
        return text;
    }
}