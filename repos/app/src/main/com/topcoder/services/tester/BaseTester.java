/*
 * Copyright (C) 2006 - 2014 TopCoder Inc., All Rights Reserved.
 */

/*
 * BaseTester
 *
 * Created 12/28/2006
 */
package com.topcoder.services.tester;

import java.io.File;

import com.topcoder.server.tester.Solution;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.services.tester.common.TestRequest;
import com.topcoder.services.tester.common.TestResult;
import com.topcoder.services.tester.invoke.FarmSolutionInvokator;
import com.topcoder.services.tester.invoke.SolutionInvocator;
import com.topcoder.services.util.datatype.BoundaryChecker;
import com.topcoder.services.util.datatype.InvalidArgumentTypeException;
import com.topcoder.services.util.datatype.InvalidArgumentValueException;
import com.topcoder.shared.problem.DataType;


/**
 * Base class for all tester.<p>
 *
 * There should be one tester by language.<p>
 *
 * <p>
 * Changes in version 1.1 (Large Input Data Time Execution Fix):
 * <ol>
 *      <li>Add {@link #DEFAULT_EXTRA_EXECUTION_TIME} field.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural)
 * @version 1.1
 */
public abstract class BaseTester {
    private final Logger log = Logger.getLogger(this.getClass());
    /**
     * <p>
     * This is extra execution time that mainly used for large input time handler.
     * it will be used by CPP and Python now.
     * </p>
     * @since 1.1
     */
    public static final String DEFAULT_EXTRA_EXECUTION_TIME = System.getProperty("com.topcoder.services.tester.BaseTester.default_extra_execution_time","5000");

    /**
     * Runs a test using the attributes provided in <code>testRequest</code>.
     *
     * @param testRequest TestRequest containing all required objects to execute the test
     * @param workingFolder The working folder where the tester should write any temporary file it requires.
     * @return The test result.
     */
    public TestResult test(TestRequest testRequest, File workingFolder) {
        TestResult result = new TestResult();
        try {
            log.info("Processing TestRequest: "+testRequest);
            Object[] args = testRequest.getArgs();
            if (testRequest.mustValidateArgs()) {
                args = checkArgs(testRequest.getSolution(), testRequest.getComponent().getParamTypes(), testRequest.getArgs());
            }
            result = doTest(testRequest, args, workingFolder);
            result.setValidatedArgs(args);
            log.info("Test result is "+result);
        } catch (InvalidArgumentTypeException e) {
            log.error("InvalidArgumentTypeException while processing test", e);
            result.setStatus(TestResult.STATUS_INVALID_ARGS);
            result.setMessage(e.getMessage());
        } catch (InvalidArgumentValueException e) {
            log.error("InvalidArgumentValueException while processing test", e);
            result.setStatus(TestResult.STATUS_INVALID_ARGS);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Exception while processing java test", e);
            result.setStatus(TestResult.STATUS_TESTER_FAILURE);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    /**
     * Executes the test<p>
     *
     * Extenders of this class must implement the test execution in this method.<p>
     *
     * @param testRequest The original test request.
     * @param args The validated and translated args that should be use for the test
     * @param workingFolder The working folder the tester should use
     * @return The result of the test.
     *
     * @throws Exception If any exception occurs while executing the test case.
     */
    protected abstract TestResult doTest(TestRequest testRequest, Object[] args, File workingFolder) throws Exception;

    /**
     * Checks args of the given test request.<p>
     *
     * @param testRequest The test request containing required information for argument validation
     * @return An object[] containing args in the proper form to invoke the test
     * @throws InvalidArgumentTypeException If the argument types don't match the the method definition
     * @throws InvalidArgumentValueException If values of the arguments are invalid.
     */
    public Object[] checkArgs(TestRequest testRequest) throws InvalidArgumentTypeException, InvalidArgumentValueException {
        return checkArgs(testRequest.getSolution(), testRequest.getComponent().getParamTypes(), testRequest.getArgs());
    }

    /**
     * Checks arguments against the given solution.<p>
     *
     * @param solution The solution used to check argument values
     * @param argTypes Argument Types required. Type of the objects provided in <code>args</code> must much these types.
     * @param args The actual arguments to check
     *
     * @return An object[] containing args in the proper form to invoke the test
     * @throws InvalidArgumentTypeException If the argument types don't match the the method definition
     * @throws InvalidArgumentValueException If values of the arguments are invalid.
     */
    public Object[] checkArgs(Solution solution, DataType[] argTypes, Object[] args) throws InvalidArgumentTypeException, InvalidArgumentValueException{
        SolutionInvocator solutionInvokator = new FarmSolutionInvokator(solution);
        args = BoundaryChecker.validate(argTypes, args, solution.getClassName(), solutionInvokator);
        return args;
    }
}
