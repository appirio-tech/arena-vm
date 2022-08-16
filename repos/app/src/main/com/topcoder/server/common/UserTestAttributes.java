/*
 * Copyright (C)  - 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.server.common;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.BatchTestResponse;
import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.server.tester.Solution;
import com.topcoder.services.tester.common.TestRequest;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.problem.SimpleComponent;

/**
 * The user test attributes class.
 * It is used to set up configuration for user testing. It contains all the information
 * to execute testing of the component with the arguments. It is also used to store
 * the result of the testing which is then sent back the to client.
 * Since the version 1.1 it supports batch testing - multiple test cases for one
 * test execution.
 * When server receives {@link BatchTestRequest} which can contain more than
 * one test arguments for testing, it encapsulates them into the the {@link #args} array in this way:
 * args: [test_arguments_1, expected_return_value_1, ..., test_arguments_n, expected_return_value_n]
 * where test_arguments_i are the arguments for the i-th testing, expected_return_value_i is the expected
 * result for the i-th testing (null if not provided).
 *
 * <p>
 * Version 1.1 (Module Assembly - TopCoder Competition Engine - Batch Test):
 * <ol>
 *      <li>Add {@link #isBatchTest} boolean field to indicate if it is batch testing. It is false
 *      by default, and in that case {@link #args} array contains arguments for single testing,
 *      otherwise it is batch testing and {@link #args} is constructed as explained in
 *      the class documentation.</li>
 *      <li>Added getter/setter for {@link #isBatchTest}, and updated {@link #customReadObject(CSReader)}
 *      and {@link #customWriteObject(CSWriter)} methods.</li>
 *      <li>Add {@link #setExpectedResult(Object)} method to set expected result of the testing.</li>
 *      <li>Updated {@link #toString()} to include {@link #isBatchTest} field.</li>
 *      <li>Add {@link #batchTestResponse} field to store the results of the batch testing.</li>
 * </ol>
 * </p>
 *
 * @author dexy
 * @version 1.1
 */
public final class UserTestAttributes implements Serializable, TestRequest {
    /**
     * The number of the elements in the batch arguments block:
     *      [test_arguments_i, expected_return_value_i, test_response_i].
     * @since 1.1
     */
    public static final int BATCH_ARGS_BLOCK_SIZE = 2;
    /**
     * The batch test flag.
     * @since 1.1
     * */
    private boolean isBatchTest;
    /** The coder id. */
    private int coderId;
    /** The location. */
    private Location location;
    /** The component. */
    private SimpleComponent component;
    /** The args. */
    private Object[] args;
    /** The result of the batch test */
    private BatchTestResponse batchTestResponse;

    /** The result value. */
    private String resultValue;
    /** The succeeded. */
    private boolean succeeded;
    /** The submit time. */
    private long submitTime;
    /** The language. */
    private int language = ContestConstants.JAVA;
    /** The supplied test. */
    private boolean suppliedTest;

    /** The expected result. */
    private Object expectedResult;

    /** The component files. */
    private ComponentFiles componentFiles;

    /** The dependency component files. */
    private List dependencyComponentFiles;

    /** The compiled web service client files. */
    private Map compiledWebServiceClientFiles;

    /** The solution. */
    private Solution solution;

    /**
     * Instantiates a new user test attributes.
     */
    public UserTestAttributes() {
        isBatchTest = false;
    }

    /**
     * Instantiates a new user test attributes.
     *
     * @param coderId the coder id
     * @param location the location
     * @param component the component
     * @param language the language
     */
    public UserTestAttributes(int coderId, Location location, SimpleComponent component, int language) {
        this.isBatchTest = false;
        this.coderId = coderId;
        this.location = location;
        this.component = component;
        this.args = null;
        this.resultValue = "";
        this.succeeded = true;
        this.submitTime = 0;
        this.language = language;
        this.suppliedTest = false;
    }

    /**
     * Instantiates a new user test attributes.
     *
     * @param coderId the coder id
     * @param location the location
     * @param component the component
     * @param language the language
     * @param expectedResult the expected result
     */
    public UserTestAttributes(int coderId, Location location, SimpleComponent component,
            int language, Object expectedResult) {
        this.isBatchTest = false;
        this.coderId = coderId;
        this.location = location;
        this.component = component;
        this.args = null;
        this.resultValue = "";
        this.succeeded = true;
        this.submitTime = 0;
        this.language = language;
        this.suppliedTest = true;
        this.expectedResult = expectedResult;
    }

    /*
    public UserTestAttributes(SimpleComponent component) {
      this.component = component;
    }
    */

    // This constructor is only for contstucting an errant attributes object
    /*
  public UserTestAttributes (String errorMessage) {
    this.succeeded = false;
    this.resultValue = errorMessage;
  }
  */

// set
    /*
    public void setCoderId(int coderId) {
      this.coderId = coderId;
    }

    public void setLocation(Location location) {
      this.location = location;
    }

    public void setProblem(Problem problem) {
      this.problem = problem;
    }
    */

    /**
     * Sets the args.
     *
     * @param args the new args
     */
    public void setArgs(Object[] args) {
        this.args = args;
    }

    /**
     * Sets the result value.
     *
     * @param resultValue the new result value
     */
    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
    }

    /**
     * Sets the succeeded.
     *
     * @param succeeded the new succeeded
     */
    public void setSucceeded(boolean succeeded) {
        this.succeeded = succeeded;
    }

    /*
  public void setLanguage(int language) {
    this.language = language;
  }
  */

// get
    /**
     * Gets the coder id.
     *
     * @return the coder id
     */
    public int getCoderId() {
        return coderId;
    }

    /**
     * Gets the location.
     *
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /* (non-Javadoc)
     * @see com.topcoder.services.tester.common.TestRequest#getComponent()
     */
    public SimpleComponent getComponent() {
        return component;
    }

    /* (non-Javadoc)
     * @see com.topcoder.services.tester.common.TestRequest#getArgs()
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * Gets the result value.
     *
     * @return the result value
     */
    public String getResultValue() {
        return resultValue;
    }

    /**
     * Gets the succeeded.
     *
     * @return the succeeded
     */
    public boolean getSucceeded() {
        return succeeded;
    }

    /**
     * Sets the submit time.
     *
     * @param in the new submit time
     */
    public void setSubmitTime(long in) {
        this.submitTime = in;
    }

    /**
     * Gets the submit time.
     *
     * @return the submit time
     */
    public long getSubmitTime() {
        return submitTime;
    }

    /**
     * Gets the language.
     *
     * @return the language
     */
    public int getLanguage() {
        return this.language;
    }

    /**
     * Supplied test.
     *
     * @return true, if successful
     */
    public boolean suppliedTest() {
        return suppliedTest;
    }

    /**
     * Sets the expected result.
     *
     * @param expectedResult the new expected result
     */
    public void setExpectedResult(Object expectedResult) {
        this.expectedResult = expectedResult;
    }

    /**
     * Gets the expected result.
     *
     * @return the expected result
     */
    public Object getExpectedResult() {
        return expectedResult;
    }

    /**
     * Checks if is supplied test.
     *
     * @return true, if is supplied test
     */
    public boolean isSuppliedTest() {
        return suppliedTest;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer str = new StringBuffer(256);
        if (!isBatchTest) {
            str.append("USER TEST ATTR...");
            str.append("\nCODER ID: " + coderId);
            str.append("\nLOCATION: " + location);
            str.append("\nPROBLEM: " + component);
            str.append("\nARGS: " + ContestConstants.makePretty(args));
            str.append("\nRESULT VALUE: " + resultValue);
        } else {
            str.append("BATCH TEST ATTR...");
            str.append("\nCODER ID: " + coderId);
            str.append("\nLOCATION: " + location);
            str.append("\nPROBLEM: " + component);
            int numTests = args.length;
            for (int itest = 0; itest < numTests; itest += BATCH_ARGS_BLOCK_SIZE) {
                str.append("\nTEST #" + (itest + 1) + ": " + args [itest]);
            }
            str.append("\nRESULT VALUE: ");
            str.append(batchTestResponse);
        }

        return str.toString();
    }


    /* (non-Javadoc)
     * @see com.topcoder.services.tester.common.TestRequest#getSolution()
     */
    public Solution getSolution() {
        return solution;
    }

    /* (non-Javadoc)
     * @see com.topcoder.services.tester.common.TestRequest#getComponentFiles()
     */
    public ComponentFiles getComponentFiles() {
        return componentFiles;
    }

    /* (non-Javadoc)
     * @see com.topcoder.services.tester.common.TestRequest#getDependencyComponentFiles()
     */
    public List getDependencyComponentFiles() {
      return dependencyComponentFiles;
    }

    /* (non-Javadoc)
     * @see com.topcoder.services.tester.common.TestRequest#getCompiledWebServiceClientFiles()
     */
    public Map getCompiledWebServiceClientFiles() {
        return compiledWebServiceClientFiles;
    }

    /**
     * Sets the compiled web service client files.
     *
     * @param compiledWebServiceClientFiles the new compiled web service client files
     */
    public void setCompiledWebServiceClientFiles(Map compiledWebServiceClientFiles) {
        this.compiledWebServiceClientFiles = compiledWebServiceClientFiles;
    }

    /**
     * Sets the component files.
     *
     * @param componentFiles the new component files
     */
    public void setComponentFiles(ComponentFiles componentFiles) {
        this.componentFiles = componentFiles;
    }

    /**
     * Sets the dependency component files.
     *
     * @param dependencyComponentFiles the new dependency component files
     */
    public void setDependencyComponentFiles(List dependencyComponentFiles) {
        this.dependencyComponentFiles = dependencyComponentFiles;
    }

    /**
     * Sets the solution.
     *
     * @param solution the new solution
     */
    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    /**
     * Checks if must validate arguments.
     * @return true by default
     * @see com.topcoder.services.tester.common.TestRequest#mustValidateArgs()
     */
    public boolean mustValidateArgs() {
        return true;
    }

    /**
     * Custom serialization reading of the object.
     *
     * @param reader custom serialization reader.
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        this.isBatchTest = reader.readBoolean();
        this.coderId = reader.readInt();
        this.location = (Location) reader.readObject();
        this.component = (SimpleComponent) reader.readObject();
        this.args = reader.readObjectArray();
        this.resultValue = reader.readString();
        this.succeeded = reader.readBoolean();
        this.submitTime = reader.readLong();
        this.language =  reader.readInt();
        this.suppliedTest = reader.readBoolean();
        this.expectedResult = reader.readObject();
        this.componentFiles = (ComponentFiles) reader.readObject();
        this.dependencyComponentFiles = reader.readArrayList();
        this.compiledWebServiceClientFiles = reader.readHashMap();
        this.solution = (Solution) reader.readObject();
        this.batchTestResponse = (BatchTestResponse) reader.readObject();
    }

    /**
     * Custom serialization writing of the object.
     *
     * @param reader custom serialization writer.
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeBoolean(this.isBatchTest);
        writer.writeInt(this.coderId);
        writer.writeObject(this.location);
        writer.writeObject(this.component);
        writer.writeObjectArray(this.args);
        writer.writeString(this.resultValue);
        writer.writeBoolean(this.succeeded);
        writer.writeLong(this.submitTime);
        writer.writeInt(this.language);
        writer.writeBoolean(this.suppliedTest);
        writer.writeObject(this.expectedResult);
        writer.writeObject(this.componentFiles);
        writer.writeList(this.dependencyComponentFiles);
        writer.writeMap(this.compiledWebServiceClientFiles);
        writer.writeObject(this.solution);
        writer.writeObject(this.batchTestResponse);
    }

    /**
     * Checks if it is batch test.
     *
     * @return true, if it is batch test
     */
    public boolean isBatchTest() {
        return isBatchTest;
    }

    /**
     * Sets the batch test.
     *
     * @param isBatchTest the new batch test
     */
    public void setBatchTest(boolean isBatchTest) {
        this.isBatchTest = isBatchTest;
    }

    /**
     * Gets the batch test response which contains the results of batch testing.
     * It is valid only if the flag isBatchTest is true.
     *
     * @return the batch test response if the isBatchTest is true and test is finished
     */
    public BatchTestResponse getBatchTestResponse() {
        return batchTestResponse;
    }

    /**
     * Sets the batch test response. It is valid only if the flag isBatchTest is true.
     *
     * @param batchTestResponse the batch test response
     */
    public void setBatchTestResponse(BatchTestResponse batchTestResponse) {
        this.batchTestResponse = batchTestResponse;
    }

//RESULT HANDLING
//        JAVA USER TEST, PYTHON USER TEST, CPP_USER TEST
//            try {
//                BeanHandler.returnTestResults(userTest, userTest.getCoderId(),
//                        ServicesConstants.USER_TEST_ACTION, submitTime);
//            } catch (Exception e) {
//                valid = false;
//                e.printStackTrace();
//            }
}
