/*
 * LongProblemNotificationEvent.java
 * 
 * Created on Jun 22, 2007, 12:01:38 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.client.spectatorApp.event;

/**
 *
 * @author rfairfax
 */
public class LongProblemNotificationEvent extends RoomEvent {

    private int submissionCount;
    private int submissionTime;
    private int exampleCount;
    private int exampleTime;
    private int problemID;
    private String writer;
    
    public LongProblemNotificationEvent(Object source, int roomId, String writer, int problemID, int submissionCount, int submissionTime, int exampleCount, int exampleTime) {
        super(source, roomId);
        this.writer = writer;
        this.problemID = problemID;
        this.submissionCount = submissionCount;
        this.submissionTime = submissionTime;
        this.exampleCount = exampleCount;
        this.exampleTime = exampleTime;
    }
    
    public String getWriter() {
        return writer;
    }
    
    public int getProblemID() {
        return problemID;
    }
    
    public int getSubmissionCount() {
        return submissionCount;
    }
    
    public int getSubmissionTime() {
        return submissionTime;
    }
    
    public int getExampleCount() {
        return exampleCount;
    }
    
    public int getExampleTime() {
        return exampleTime;
    }

}
