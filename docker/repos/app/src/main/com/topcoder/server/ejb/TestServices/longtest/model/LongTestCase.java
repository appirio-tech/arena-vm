/*
 * LongTestCase
 * 
 * Created 04/19/2006
 */
package com.topcoder.server.ejb.TestServices.longtest.model;

import java.io.Serializable;

/**
 * This class represents a long test case.
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id: LongTestCase.java 54869 2006-12-01 18:02:46Z thefaxman $
 */
public class LongTestCase implements Serializable {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_COMPLETED = 2;
    public static final int STATUS_CANCELLED = 3;
    
    private int id;
    private int groupId;
    private String arg = null;
    private Integer systemTestId = null;
    private int status = STATUS_PENDING;
    private LongTestCaseResult result;

    public String getArg() {
        return arg;
    }

    public void setArg(String arg) {
        this.arg = arg;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LongTestCaseResult getResult() {
        return result;
    }

    public void setResult(LongTestCaseResult result) {
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Integer getSystemTestId() {
        return systemTestId;
    }

    public void setSystemTestId(Integer systemTestId) {
        this.systemTestId = systemTestId;
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
