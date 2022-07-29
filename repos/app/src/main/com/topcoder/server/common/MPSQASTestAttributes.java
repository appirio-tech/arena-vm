package com.topcoder.server.common;

import java.io.Serializable;
import java.util.ArrayList;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.problem.Problem;

public final class MPSQASTestAttributes implements Serializable {

    private int coderId;
    private Location location;
    private Problem problem;
    private ArrayList args; // args[0] = vector of types, args[1] = vector of test values
    private Object result;
    private String resultValue;
    private long submitTime;
    private long executionTime;
    private int language = ContestConstants.JAVA;

    public MPSQASTestAttributes(int coderId, Location location, Problem problem, int language) {
        this.coderId = coderId;
        this.location = location;
        this.problem = problem;
        this.args = new ArrayList();
        this.result = null;
        this.resultValue = "";
        this.submitTime = 0;
        this.executionTime = 0;
        this.language = language;
    }

    public MPSQASTestAttributes(Problem problem) {
        this.problem = problem;
    }

// set
    public void setCoderId(int coderId) {
        this.coderId = coderId;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    /*
  public void setProblem(Problem problem) {
    this.problem = problem;
  }
  */

    public void setArgs(ArrayList args) {
        this.args = args;
    }

    /**
     *  Set the result.
     *
     *  @param result         Object returned by the tested class.
     */
    public void setResult(Object result) {
        this.result = result;
    }

    /**
     *  Sets the "result value" (textual representation of the output object,
     *  standard output, standard error, etc.).
     *
     *  @param resultValue        textual representation of the class's output.
     */
    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
    }

    public void setSubmitTime(long in) {
        this.submitTime = in;
    }

    /**
     *  Sets the execution time (time taken by the tested class).
     *
     *  @param executionTime      execution time for the test.
     */
    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

// get
    public int getCoderId() {
        return coderId;
    }

    public Location getLocation() {
        return location;
    }

    public Problem getProblem() {
        return problem;
    }

    public ArrayList getArgs() {
        return args;
    }

    /**
     *  Returns the result object.
     *
     *  @return       Object returned by the tested class.
     */
    public Object getResult() {
        return result;
    }

    /**
     *  Returns a textual representation of the class output.
     *
     *  @return       textual representation of the class's output.
     */
    public String getResultValue() {
        return resultValue;
    }

    /**
     *  Returns the execution time.
     *
     *  @return       execution time.
     */
    public long getExecutionTime() {
        return executionTime;
    }

    public long getSubmitTime() {
        return submitTime;
    }

    public int getLanguage() {
        return this.language;
    }

    public String toString() {
        StringBuffer str = new StringBuffer(256);
        str.append("MPSQAS TEST ATTR...");
        str.append("\nCODER ID: " + coderId);
        str.append("\nLOCATION: " + location);
        str.append("\nPROBLEM: " + problem);
        str.append("\nARGS: " + args);
        str.append("\nRESULT VALUE: " + resultValue);

        return str.toString();
    }
}
