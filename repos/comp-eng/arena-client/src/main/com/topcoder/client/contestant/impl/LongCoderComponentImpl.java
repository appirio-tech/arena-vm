/*
 * LongCoderComponentImpl
 * 
 * Created 06/13/2007
 */
package com.topcoder.client.contestant.impl;

import com.topcoder.client.contestant.Coder;
import com.topcoder.client.contestant.view.EventService;
import com.topcoder.shared.language.BaseLanguage;
import com.topcoder.shared.language.Language;

/**
 * @author Diego Belfer (mural)
 * @version $Id: LongCoderComponentImpl.java 67962 2008-01-15 15:57:53Z mural $
 */
public class LongCoderComponentImpl extends CoderComponentImpl {

    private int submissionCount;
    private long lastSubmissionTime;
    private int exampleSubmissionCount;
    private long exampleLastSubmissionTime;
    private int exampleLastLanguage;
    private Integer sourceLanguage;

    public LongCoderComponentImpl() {
    }

    public LongCoderComponentImpl(ProblemComponentModelImpl component, int languageID, int points, int status,
            Coder coder, EventService eventService) {
        super(component, languageID, points, status, coder, null, eventService);
    }
    
    public LongCoderComponentImpl(ProblemComponentModelImpl component, int languageID, int points, int status,
            Coder coder, EventService eventService, int submissionCount, long lastSubmissionTime, 
            int exampleSubmissionCount, long exampleLastSubmissionTime, int exampleLastLanguage) {
        super(component, languageID, points, status, coder, null, eventService);
        this.submissionCount = submissionCount;
        this.lastSubmissionTime = lastSubmissionTime;
        this.exampleSubmissionCount = exampleSubmissionCount;
        this.exampleLastSubmissionTime = exampleLastSubmissionTime;
        this.exampleLastLanguage = exampleLastLanguage;
    }

    public int getExampleLastLanguage() {
        return exampleLastLanguage;
    }

    public void setExampleLastLanguage(int exampleLastLanguage) {
        this.exampleLastLanguage = exampleLastLanguage;
    }

    public long getExampleLastSubmissionTime() {
        return exampleLastSubmissionTime;
    }

    public void setExampleLastSubmissionTime(long exampleLastSubmissionTime) {
        this.exampleLastSubmissionTime = exampleLastSubmissionTime;
    }

    public int getExampleSubmissionCount() {
        return exampleSubmissionCount;
    }

    public void setExampleSubmissionCount(int exampleSubmissionCount) {
        this.exampleSubmissionCount = exampleSubmissionCount;
    }

    public long getLastSubmissionTime() {
        return lastSubmissionTime;
    }

    public void setLastSubmissionTime(long lastSubmissionTime) {
        this.lastSubmissionTime = lastSubmissionTime;
    }

    public int getSubmissionCount() {
        return submissionCount;
    }

    public void setSubmissionCount(int submissionCount) {
        this.submissionCount = submissionCount;
    }
    
    protected void setSourceCodeLanguage(int languageID) {
        sourceLanguage = new Integer(languageID);
    }
    
    public synchronized Language getSourceCodeLanguage() {
        if(sourceLanguage==null ) return null;
        return BaseLanguage.getLanguage(sourceLanguage.intValue());
    }

}
