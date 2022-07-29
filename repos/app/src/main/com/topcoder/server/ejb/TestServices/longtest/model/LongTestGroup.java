/*
 * LongTestGroup
 * 
 * Created 04/19/2006
 */
package com.topcoder.server.ejb.TestServices.longtest.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * @author Diego Belfer (mural)
 * @version $Id: LongTestGroup.java 54869 2006-12-01 18:02:46Z thefaxman $
 */
public class LongTestGroup implements Serializable {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_COMPLETED = 2;
    public static final int STATUS_CANCELLED = 3;
    
    private int id;
    private int codeType;
    private int status = STATUS_PENDING;
    private int componentId;
    private Integer submissionId;
    private Integer solutionId;
    private int pendingTests = 0;
    private List testCases = null;
    private Date statusDate = null;
    
    public int getComponentId() {
        return componentId;
    }
    
    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getPendingTests() {
        return pendingTests;
    }
    
    public void setPendingTests(int pendingTests) {
        this.pendingTests = pendingTests;
    }
    
    public Integer getSolutionId() {
        return solutionId;
    }
    
    public void setSolutionId(Integer solutionId) {
        this.solutionId = solutionId;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public Integer getSubmissionId() {
        return submissionId;
    }
    
    public void setSubmissionId(Integer submissionId) {
        this.submissionId = submissionId;
    }
    
    public int getCodeType() {
        return codeType;
    }
    
    public void setCodeType(int type) {
        this.codeType = type;
    }

    public List getTestCases() {
        return testCases;
    }

    public void setTestCases(List testCases) {
        this.testCases = testCases;
    }
    
    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date endDate) {
        this.statusDate = endDate;
    }
    
    public String getStatusAsString() {
        return getStatusAsString(getStatus());
    }
    
    public static String getStatusAsString(int statusId) {
        switch (statusId) {
            case STATUS_PENDING :
                return "pending";
            case STATUS_COMPLETED :
                return "completed";
            case STATUS_CANCELLED :
                return "cancelled";
        }
        return "unknown";
    }
}
