/*
 * LongTestRequest
 * 
 * Created 04/12/2006
 */
package com.topcoder.services.tester.common;

import java.io.Serializable;

import com.topcoder.server.tester.ComponentFiles;

/**
 * Bean containing all necessary information that a tester requires 
 * to do a test on a solution/submission
 *  
 * @author Diego Belfer (mural)
 * @version $Id: LongTestRequest.java 46205 2006-05-24 08:47:09Z thefaxman $
 */
public class LongTestRequest implements Serializable {
    public static final int CODE_TYPE_SOLUTION = 0;
    public static final int CODE_TYPE_SUBMISSION = 1;
    private int requestId;
    private String argument;
    private String className;
    private ComponentFiles componentFiles;
    private int codeType;
    private int componentID;
    
    /**
     * @return Returns the argument.
     */
    public String getArgument() {
        return argument;
    }
    /**
     * @param argument The argument to set.
     */
    public void setArgument(String argument) {
        this.argument = argument;
    }
    /**
     * @return Returns the className.
     */
    public String getClassName() {
        return className;
    }
    /**
     * @param className The className to set.
     */
    public void setClassName(String className) {
        this.className = className;
    }
    /**
     * @return Returns the componentFiles.
     */
    public ComponentFiles getComponentFiles() {
        return componentFiles;
    }
    /**
     * @param componentFiles The componentFiles to set.
     */
    public void setComponentFiles(ComponentFiles componentFiles) {
        this.componentFiles = componentFiles;
    }
    /**
     * @return Returns the requestId.
     */
    public int getRequestId() {
        return requestId;
    }
    /**
     * @param requestId The requestId to set.
     */
    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }
    /**
     * @return Returns the codeType.
     */
    public int getCodeType() {
        return codeType;
    }
    /**
     * @param codeType The codeType to set.
     */
    public void setCodeType(int testType) {
        this.codeType = testType;
    }

    public int getComponentId() {
        return componentID;
    }
    
    public void setComponentId(int componentID) {
        this.componentID = componentID;
    }
}
