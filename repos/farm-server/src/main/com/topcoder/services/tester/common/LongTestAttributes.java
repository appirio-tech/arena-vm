package com.topcoder.services.tester.common;
import java.io.Serializable;

import com.topcoder.shared.common.ServicesConstants;

public class LongTestAttributes implements Serializable{
    int componentID, roundID, contestID, coderID, testCaseID, serverID;
    int languageID, problemID, submissionNumber;
    String className, email, handle;
    Object[] params;
    boolean isExample;
    /**
     * Indicates type of test action
     * Expected values ServicesConstants.LONG_TEST_ACTION and ServicesConstants.LONG_SYSTEM_TEST_ACTION 
     */
    private int testAction;
    
    /**
     * @param componentID
     * @param roundID
     * @param coderID
     * @param submissionNumber
     * @param testCaseID
     * @param params
     */
    public LongTestAttributes(int componentID, int roundID, int contestID, int coderID, int testCaseID, String className, Object[] params, String email) {
        super();
        this.componentID = componentID;
        this.roundID = roundID;
        this.contestID = contestID;
        this.coderID = coderID;
        this.testCaseID = testCaseID;
        this.className = className;
        this.params = params;
        this.email = email;
        this.isExample = false;
        this.testAction = ServicesConstants.LONG_TEST_ACTION;
    }
    public boolean isExample() {
        return isExample;
    }
    public void setIsExample(boolean b) {
        this.isExample = b;
    }
    /**
     * @return Returns the coderID.
     */
    public int getCoderID() {
        return coderID;
    }
    /**
     * @return Returns the componentID.
     */
    public int getComponentID() {
        return componentID;
    }
    /**
     * @return Returns the params.
     */
    public Object[] getParams() {
        return params;
    }
    /**
     * @return Returns the roundID.
     */
    public int getRoundID() {
        return roundID;
    }
    /**
     * @return Returns the submissionNumber.
     */
    /**
     * @return Returns the testCaseID.
     */
    public int getTestCaseID() {
        return testCaseID;
    }
    /**
     * @return Returns the serverID.
     */
    public int getServerID() {
        return serverID;
    }
    /**
     * @return Returns the coder's email address.
     */
    public String getEmail() {
        return email;
    }
    
    
    /**
     * @param serverID The serverID to set.
     */
    public void setServerID(int serverID) {
        this.serverID = serverID;
    }
    public String getClassName(){
        return className;
    }
    public int getContestID(){
        return contestID;
    }
    public int getLanguageID(){
        return languageID;
    }
    public void setLanguageID(int languageID){
        this.languageID = languageID;
    }
    
    /**
     * @return The test action that originated the test request
     */
    public int getTestAction() {
        return testAction;
    }
    
    /**
     * Sets the test action that originated the test request
     */
    public void setTestAction(int testAction) {
        this.testAction = testAction;
    }
    
    /**
     * Return true if the test action is a system test action
     */
    public boolean isSystemTestAction() {
        return testAction == ServicesConstants.LONG_SYSTEM_TEST_ACTION;
    }
    /**
     * @return Returns the handle.
     */
    public String getHandle() {
        return handle;
    }
    /**
     * @param handle The handle to set.
     */
    public void setHandle(String handle) {
        this.handle = handle;
    }
    /**
     * @return Returns the problemID.
     */
    public int getProblemID() {
        return problemID;
    }
    /**
     * @param problemID The problemID to set.
     */
    public void setProblemID(int problemID) {
        this.problemID = problemID;
    }
    /**
     * @return Returns the submissionNumber.
     */
    public int getSubmissionNumber() {
        return submissionNumber;
    }
    /**
     * @param submissionNumber The submissionNumber to set.
     */
    public void setSubmissionNumber(int submissionNumber) {
        this.submissionNumber = submissionNumber;
    }
}
