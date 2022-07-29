package com.topcoder.server.common;

import java.io.Serializable;
import java.util.ArrayList;

public final class ExpectedResult implements Serializable {

    private int problemId;
    private int testCaseId;
    private String resultType;
    private ArrayList resultValue;
    private ArrayList argListTypes;
    private int Matrix2DRows;
    private String Matrix2DType;
    private String modified;


    public ExpectedResult() {
        problemId = 0;
        testCaseId = 0;
        resultType = "";
        resultValue = new ArrayList();
        argListTypes = new ArrayList();
        Matrix2DRows = 0;
        Matrix2DType = "";
        modified = "";
    }

    // set
    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public void setTestCaseId(int testCaseId) {
        this.testCaseId = testCaseId;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public void setResultValue(ArrayList resultValue) {
        this.resultValue = resultValue;
    }

    public void setArgListTypes(ArrayList argListTypes) {
        this.argListTypes = argListTypes;
    }

    public void setMatrix2DRows(int Matrix2DRows) {
        this.Matrix2DRows = Matrix2DRows;
    }

    public void setMatrix2DType(String Matrix2DType) {
        this.Matrix2DType = Matrix2DType;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    // get
    public int getProblemId() {
        return problemId;
    }

    public int getTestCaseId() {
        return testCaseId;
    }

    public String getResultType() {
        return resultType;
    }

    public ArrayList getResultValue() {
        return resultValue;
    }

    public ArrayList getArgListTypes() {
        return argListTypes;
    }

    public int getMatrix2DRows() {
        return Matrix2DRows;
    }

    public String getMatrix2DType() {
        return Matrix2DType;
    }

    public String getModified() {
        return modified;
    }


    /**
     *
     * @param ArrayList of values
     * @return String of values separated by commas
     */
    private static String buildCommaString(ArrayList values) {
        StringBuffer resultBuffer = new StringBuffer();

        for (int i = 0; i < values.size(); i++) {
            resultBuffer.append(((Object) values.get(i)).toString());

            if ((i + 1) != values.size()) {
                resultBuffer.append(",");
            }
        }

        return resultBuffer.toString();
    }
}
