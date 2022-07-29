/*
 * LongCoderComponent
 * 
 * Created 05/31/2007
 */
package com.topcoder.server.common;

import com.topcoder.netCommon.contest.ContestConstants;

/**
 * @author Diego Belfer (mural)
 * @version $Id: LongCoderComponent.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class LongCoderComponent extends BaseCoderComponent {
    private int submissionCount;
    private int exampleSubmissionCount;
    private String exampleSubmittedProgramText;
    private int exampleSubmittedLanguage;
    private long exampleSubmittedTime;
    
    public LongCoderComponent(int coderID, int componentID) {
        super(coderID, componentID, 0);
    }
    
    public boolean isWritable() {
        return getStatus() == ContestConstants.LOOKED_AT || getStatus() ==  ContestConstants.NOT_CHALLENGED;
    }

    public int getExampleSubmissionCount() {
        return exampleSubmissionCount;
    }

    public void setExampleSubmissionCount(int exampleSubmissionCount) {
        this.exampleSubmissionCount = exampleSubmissionCount;
    }

    public int getSubmissionCount() {
        return submissionCount;
    }

    public void setSubmissionCount(int submissionCount) {
        this.submissionCount = submissionCount;
    }

    public int getExampleSubmittedLanguage() {
        return exampleSubmittedLanguage;
    }

    public void setExampleSubmittedLanguage(int exampleSubmittedLanguage) {
        this.exampleSubmittedLanguage = exampleSubmittedLanguage;
    }

    public String getExampleSubmittedProgramText() {
        return exampleSubmittedProgramText;
    }

    public void setExampleSubmittedProgramText(String exampleSubmittedProgramText) {
        this.exampleSubmittedProgramText = exampleSubmittedProgramText;
    }

    public long getExampleSubmittedTime() {
        return exampleSubmittedTime;
    }

    public void setExampleSubmittedTime(long exampleSubmittedTime) {
        this.exampleSubmittedTime = exampleSubmittedTime;
    }
    
    public int getEarnedPoints() {
        return super.getSubmittedValue();
    }
    
    public void updateFrom(LongCoderComponent source) {
        super.updateFrom(source);
        this.submissionCount = source.submissionCount;
        this.exampleSubmissionCount = source.exampleSubmissionCount;
        this.exampleSubmittedProgramText = source.exampleSubmittedProgramText;
        this.exampleSubmittedLanguage = source.exampleSubmittedLanguage;
        this.exampleSubmittedTime = source.exampleSubmittedTime;
    }
}
