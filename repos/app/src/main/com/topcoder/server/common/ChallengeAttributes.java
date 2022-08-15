/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.server.common;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.List;
import java.util.Map;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.server.tester.Solution;
import com.topcoder.services.tester.common.TestRequest;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.ExternalizableHelper;
import com.topcoder.shared.problem.Problem;
import com.topcoder.shared.problem.SimpleComponent;

/**
 * <p>DTO for challenge attributes.</p>
 *
 * <p>
 * Changes in version 1.1 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *     <li>Added {@link #checkAnswerResponse} field and added/updated all corresponding methods.</li>
 * </ol>
 * </p>
 *
 * @author gevak
 * @version 1.1
 */
public final class ChallengeAttributes
        implements Externalizable, TestRequest {

    // unintuitively, this also represents a checkData failure (invalid args)
    public static final byte RESULT_SYSTEM_FAILURE = -1;
    public static final byte RESULT_CORRECT = 0;
    public static final byte RESULT_INCORRECT = 1;
    public static final byte RESULT_EXCEPTION = 2;
    public static final byte RESULT_TIMEOUT = 3;

    public boolean isSuccesfulChallenge() {
        return resultCode > 0;
    }

    public boolean isSystemFailure() {
        return resultCode == RESULT_SYSTEM_FAILURE;
    }
    public boolean isExceptionResult() {
        return resultCode == RESULT_EXCEPTION;
    }
    public boolean isTimeOut() {
        return resultCode == RESULT_TIMEOUT;
    }

    private int challengerId;
    private String chalUsername;
    private int defendantId;
    private String defUsername;
    private Location location;
    private SimpleComponent component;
    private Problem problem;
    //private int problemId;
    private int componentId;
    private Object[] args;
    private Object resultValue;
    private Object expectedResult;
    private byte resultCode = 0;
    private long submitTime;
    private int pointValue;
    private int penaltyValue;
    private int chalValue;
    private String msg;
    private int language;
    private int m_componentMaxPointVal = 0;
    private String m_history = null;
    private Solution solution;
    private List dependencyComponentFiles;
    private ComponentFiles componentFiles;
    private Map compiledWebServiceClientFiles;
    private boolean exclusiveExecution;
    private boolean validatedArgsSet;

    /**
     * Check answer response.
     *
     * @since 1.1
     */
    private String checkAnswerResponse;

    public ChallengeAttributes() {
    }

    public ChallengeAttributes(RoundComponent component, int language, Problem problem) {
        //this.language = ContestConstants.JAVA;

        // not holding onto round problem for now, might want to in the future
        this.problem = problem;
        this.component = component.getComponent();
        this.chalUsername = "";
        this.defUsername = "";
        this.args = null;
        this.resultValue = "";
        this.msg = "";
        this.chalValue = ContestConstants.EASY_CHALLENGE * 100;
        this.language = language;
        this.resultValue = null;
        this.expectedResult = null;
        //this.problemId = this.component.getProblemID();
        this.componentId = this.component.getComponentID();
        //this.resultValueType = null;
        //this.expectedResultType = null;
        this.resultCode = 0;
        m_componentMaxPointVal = component.getPointVal();
    }

    // This constructor is only for contstucting an errant attributes object
    /*
    public ChallengeAttributes (String errorMessage) {
    this.succeeded = 0;
    this.resultValue = errorMessage;
    this.chalValue = ContestConstants.EASY_CHALLENGE;
    }
    */

// set
    public void setChallengerId(int challengerId) {
        this.challengerId = challengerId;
    }

    public void setChalUsername(String in) {
        this.chalUsername = in;
    }

    public void setDefendantId(int defendantId) {
        this.defendantId = defendantId;
    }

    public void setDefUsername(String in) {
        this.defUsername = in;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setArgs(Object[] args) {
        this.validatedArgsSet = false;
        this.args = args;
    }

    public void setValidatedArgs(Object[] args) {
        this.validatedArgsSet = true;
        this.args = args;
    }

    public void setResultValue(Object resultValue) {
        this.resultValue = resultValue;
    }

    public void setExpectedResult(Object expectedResult) {
        this.expectedResult = expectedResult;
    }

    /*
    public void setResultValueType(String resultValueType) {
    this.resultValueType = resultValueType;
    }

    public void setExpectedResultType(String expectedResultType) {
    this.expectedResultType = expectedResultType;
    }
    */

    public void setResultCode(int resultCode) {
        switch (resultCode) {
        case RESULT_CORRECT:
        case RESULT_INCORRECT:
        case RESULT_EXCEPTION:
        case RESULT_TIMEOUT:
        case RESULT_SYSTEM_FAILURE:
            break;
        default:
            throw new IllegalArgumentException("Bad result code: " + resultCode);
        }
        this.resultCode = (byte) resultCode;
    }

    public void setSubmitTime(long submitTime) {
        this.submitTime = submitTime;
    }

    public void setPointValue(int value) {
        this.pointValue = value;
        this.penaltyValue = ContestConstants.UNSUCCESSFUL_CHALLENGE * 100;
    }

    /*
    public void setChallengerPoints(double challengerPoints) {
    this.challengerPoints = challengerPoints;
    }

    public void setDefendantPoints(double defendantPoints) {
    this.defendantPoints = defendantPoints;
    }
    */

    public void setChalValue(int value) {
        this.chalValue = value;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    public void setLanguage(int language) {
        this.language = language;
    }


    public int getChallengerId() {
        return challengerId;
    }

    private String getChalUsername() {
        return chalUsername;
    }

    public int getDefendantId() {
        return defendantId;
    }

    public String getDefUsername() {
        return defUsername;
    }

    public Location getLocation() {
        return location;
    }

    public SimpleComponent getComponent() {
        return component;
    }

    public int getComponentId() {
        return componentId;
    }

    public void setComponentId(int id) {
        componentId = id;
    }

    /*
    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }
    */

    public void setComponentID(int id) {
        setComponentId(id);
    }

    public int getComponentID() {
        return getComponentId();
    }

    public Object[] getArgs() {
        return args;
    }

    public Object getResultValue() {
        return resultValue;
    }

    public Object getExpectedResult() {
        return expectedResult;
    }

    /*
    public String getResultValueType() {
    return resultValueType;
    }

    public String getExpectedResultType() {
    return expectedResultType;
    }
    */

    public int getResultCode() {
        return resultCode;
    }

    public long getSubmitTime() {
        return submitTime;
    }

    public int getPointValue() {
        return pointValue;
    }

    /*
    public double getDefendantPoints() {
    return this.defendantPoints;
    }

    public double getChallengerPoints() {
    return this.challengerPoints;
    }
    */

    public int getChalValue() {
        return this.chalValue;
    }

    public int getPenaltyValue() {
        return this.penaltyValue;
    }

    public String getMessage() {
        return msg;
    }

    public int getLanguage() {
        return language;
    }

    public String getChalText() {
        String retVal = "CHAL INFO - CHLNGR: " + challengerId +
                " DFNDNT: " + defendantId +
                " PROB: " + component +
                " SUCC: " + resultCode;
        return retVal;
    }

    public void setChallengeHistoryMessage(String msg) {
        m_history = msg;
    }

    public String getChallengeHistoryMessage() {
        if (m_history == null) {
            StringBuffer message = new StringBuffer(getChalUsername());
            if (isSuccesfulChallenge()) {
                message.append(" successfully challenged ");
            } else {
                message.append(" unsuccessfully challenged ");
            }
            message.append(getDefUsername());
            message.append("'s ");

            boolean isTeam = problem.getProblemTypeID() == ContestConstants.TEAM_PROBLEM_TYPE_ID;
            if (isTeam) {
                message.append(problem.getName());
                message.append(" problem.\n");
            } else {
                message.append(m_componentMaxPointVal);
                message.append("-point problem.\n");
            }
            return message.toString();
        }
        return m_history;
    }

    /**
     * Gets textual respresentation of this object.
     *
     * @return Textual representation.
     */
    public String toString() {
        StringBuffer str = new StringBuffer(500);
        str.append("CHALLENGE ATTRIBUTES OBJECT:");
        str.append("\nchallenger: " + chalUsername);
        str.append("\ndefendant: " + defUsername);
        str.append("\nproblem: " + component.getProblemID());
        str.append("\ncomponent: " + component.getComponentID());
        str.append("\nargs: " + args);
        str.append("\nresultValue: " + resultValue);
        str.append("\nexpectedResult: " + expectedResult);
        str.append("\nresultCode: " + resultCode);
        str.append("\nmsg: " + msg + "\n");
        str.append("\ncheckAnswerResponse: " + checkAnswerResponse + "\n");
        return str.toString();
    }


    public Solution getSolution() {
        return solution;
    }

    public ComponentFiles getComponentFiles() {
        return componentFiles;
    }

    public List getDependencyComponentFiles() {
      return dependencyComponentFiles;
    }

    public Map getCompiledWebServiceClientFiles() {
        return compiledWebServiceClientFiles;
    }

    public void setCompiledWebServiceClientFiles(Map compiledWebServiceClientFiles) {
        this.compiledWebServiceClientFiles = compiledWebServiceClientFiles;
    }

    public void setComponentFiles(ComponentFiles componentFiles) {
        this.componentFiles = componentFiles;
    }

    public void setDependencyComponentFiles(List dependencyComponentFiles) {
        this.dependencyComponentFiles = dependencyComponentFiles;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    public boolean isExclusiveExecution() {
        return exclusiveExecution;
    }

    public void setExclusiveExecution(boolean exclusiveExecution) {
        this.exclusiveExecution = exclusiveExecution;
    }

    /**
     * Clear all information generated during test challenge execution.
     */
    public void clearResult() {
        setResultCode(RESULT_CORRECT);
        setResultValue(null);
    }

    public boolean mustValidateArgs() {
        return !validatedArgsSet;
    }

    /**
     * Sets check answer response.
     *
     * @param checkAnswerResponse Check answer response.
     * @since 1.1
     */
    public void setCheckAnswerResponse(String checkAnswerResponse) {
        this.checkAnswerResponse = checkAnswerResponse;
    }

    /**
     * Gets check answer response.
     *
     * @return Check answer response.
     * @since 1.1
     */
    public String getCheckAnswerResponse() {
        return checkAnswerResponse;
    }

    /**
     * Performs de-serialization.
     *
     * @param reader Reader.
     *
     * @throws IOException If any I/O error occurs.
     * @throws ObjectStreamException If any stream error occurs.
     **/
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        challengerId = reader.readInt();
        chalUsername = reader.readString();
        defendantId = reader.readInt();
        defUsername = reader.readString();
        location = (Location) reader.readObject();
        component = (SimpleComponent) reader.readObject();
        problem = (Problem) reader.readObject();
        componentId = reader.readInt();
        args = reader.readObjectArray();
        resultValue = reader.readObject();
        expectedResult = reader.readObject();
        resultCode = reader.readByte();
        submitTime = reader.readLong();
        pointValue = reader.readInt();
        penaltyValue = reader.readInt();
        chalValue = reader.readInt();
        msg = reader.readString();
        language = reader.readInt();
        m_componentMaxPointVal = reader.readInt();
        m_history = reader.readString();
        solution = (Solution) reader.readObject();
        dependencyComponentFiles = reader.readArrayList();
        componentFiles = (ComponentFiles) reader.readObject();
        compiledWebServiceClientFiles = reader.readHashMap();
        exclusiveExecution = reader.readBoolean();
        validatedArgsSet = reader.readBoolean();
        checkAnswerResponse = reader.readString();
    }

    /**
     * Performs serialization.
     *
     * @param writer Writer.
     *
     * @throws IOException If any I/O error occurs.
     **/
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(challengerId);
        writer.writeString(chalUsername);
        writer.writeInt(defendantId);
        writer.writeString(defUsername);
        writer.writeObject(location);
        writer.writeObject(component);
        writer.writeObject(problem);
        writer.writeInt(componentId);
        writer.writeObjectArray(args);
        writer.writeObject(resultValue);
        writer.writeObject(expectedResult);
        writer.writeByte(resultCode);
        writer.writeLong(submitTime);
        writer.writeInt(pointValue);
        writer.writeInt(penaltyValue);
        writer.writeInt(chalValue);
        writer.writeString(msg);
        writer.writeInt(language);
        writer.writeInt(m_componentMaxPointVal);
        writer.writeString(m_history);
        writer.writeObject(solution);
        writer.writeList(dependencyComponentFiles);
        writer.writeObject(componentFiles);
        writer.writeMap(compiledWebServiceClientFiles);
        writer.writeBoolean(exclusiveExecution);
        writer.writeBoolean(validatedArgsSet);
        writer.writeString(checkAnswerResponse);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        ExternalizableHelper.writeExternal(out, this);
    }

    public void readExternal(ObjectInput in) throws IOException {
        ExternalizableHelper.readExternal(in, this);
    }

//RESULT HANDLING
//        JAVA CPP PYTHON
//        /**
//         * return the results to the users
//         */
//        if (!chal.isSystemFailure()) {
//            String error = BeanHandler.recordChallengeResults(chal);
//            if (!error.equals(""))  // It means someone has beaten them to it
//            {
//                chal.setResultCode(ChallengeAttributes.RESULT_SYSTEM_FAILURE);  // to prevent from being broadcast to everyone.
//                chal.setMessage(error);
//            }
//        }
//
//        try {
//            chal.setExpectedResult(ContestConstants.makePretty(chal.getExpectedResult()));
//            BeanHandler.returnTestResults(chal, chal.getChallengerId(),
//                    ServicesConstants.CHALLENGE_TEST_ACTION, submitTime);
//        } catch (Exception e) {
//            valid = false;
//            e.printStackTrace();
//        }
}
