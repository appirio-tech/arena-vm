/*
* Copyright (C) 2007 - 2014 TopCoder Inc., All Rights Reserved.
*/

/*
 * DotNetExternalTesterInvoker
 *
 * Created 01/12/2007
 */
package com.topcoder.services.tester.invoke;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.topcoder.farm.deployer.process.ProcessRunner;
import com.topcoder.farm.deployer.process.ProcessRunner.ProcessRunResult;
import com.topcoder.farm.deployer.process.ProcessRunnerException;
import com.topcoder.farm.deployer.process.ProcessTimeoutException;
import com.topcoder.io.serialization.basictype.impl.BasicTypeDataInputImpl;
import com.topcoder.io.serialization.basictype.impl.BasicTypeDataOutputImpl;
import com.topcoder.services.tester.common.TestResult;
import com.topcoder.services.util.Formatter;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.ProblemCustomSettings;
import com.topcoder.shared.util.encoding.Base64Encoding;
import com.topcoder.shared.util.logging.Logger;

/**
 * DotNetExternalTesterInvoker is responsible for launching and invoking the external
 * .NET tester process.<p>
 *
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine - Customize Execution Time and Memory Limits for SRM v1.0):
 * <ol>
 *      <li>Update {@link #test(String, String, String, DataType[], Object[], DataType, String,
 *           int, int)} method.</li>
 *      <li>Update {@link #buildTestResult(Object[] values, int executionTimeLimit)} method</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #test(String, String, String, DataType[], Object[], DataType, String,
 *           ProblemCustomSettings custom)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (Module Assembly - Return Peak Memory Usage for Executing SRM DotNet Solution):
 * <ol>
 *      <li>Update {@link #buildTestResult(Object[], int)} method to include maximum memory
 *          used (in MB) in the result.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (Update DotNet TestProcess Code for x64 environment v1.0):
 * <ol>
 *      <li>Update {@link #test(String, String, String, DataType[], Object[], DataType, String,
 *           int, int)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.5 (TopCoder Competition Engine - Stack Size Configuration For SRM Problems v1.0):
 * <ol>
 *      <li>Updated {@link #test(String, String, String, DataType[], Object[], DataType, String, ProblemCustomSettings)}
 *          method to pass stack limit to tester.</li>
 * </ol>
 * </p>
 *
 * @author Diego Belfer (mural), savon_cn, dexy, Selena
 * @version 1.5
 */
public class DotNetExternalTesterInvoker {
    /*
     * The tester application is invoked using the following args:
     *      assemblyName className methodName.
     *
     * argument types and actual arguments are serialized, base64 encoded and write to the process stdin.
     *
     * If the tester finalizes properly,  its exit code is 0 and Object[] is serialized and
     * base 64 encoded in the process standard output.
     * If the tester failed to execute the test the exit code -1 and the stderr contains failure
     * description.
     */

    private static final Logger log = Logger.getLogger(DotNetExternalTesterInvoker.class);
    private static final int TIME_TO_WAIT = 10000;

    /**
     * Invokes the external tester in order to execute the test case.<p>
     *
     * The method <code>methodName</code> of the class <code>className</code> residing in
     * the assembly <code>assemblyName</code> is invoked with arguments <code>args</code><p>.
     *
     * All required files should be accessible by the external tester process<p>
     *
     * @param assemblyFile The assemblyFile. Path to the assembly file containing the class definition.
     *                     Path is relative to the working folder.
     * @param className The className of the to load
     * @param methodName The methodName of the method to invoke
     * @param argTypes The argument types of the method declaration
     * @param args The actual arguments to use for method invocation
     * @param resultType The return type of the method
     * @param workDir An string containig the working directory to use.
     * @param custom problem customization.
     *
     * @return The test result with all results and outputs produced by the tester/submission.
     * @throws DotNetExternalTesterInvokerException if there was a problem executing the external process.
     *                                              The exception message contains more detailed information
     */
    public static TestResult test(String assemblyFile, String className, String methodName, DataType[] argTypes,
            Object[] args, DataType resultType, String workDir,
            ProblemCustomSettings custom) throws DotNetExternalTesterInvokerException {

        try {
            int executionTimeLimit = custom.getExecutionTimeLimit();
            int memLimit = custom.getMemLimit();
            int stackLimit = custom.getStackLimit();
            String[] commandLine =
                new String[] {ServicesConstants.DOTNET_TESTER, assemblyFile, className, methodName,
                              Integer.toString(ServicesConstants.MAX_RESULT_LENGTH),
                              String.valueOf(executionTimeLimit), String.valueOf(memLimit), String.valueOf(stackLimit)};
            File folder = new File(workDir);
            ProcessRunner runner = new ProcessRunner(commandLine, folder, executionTimeLimit + TIME_TO_WAIT);
            InputStream stdIn = buildStdIn(argTypes, args, resultType);
            ProcessRunResult result = runner.run(stdIn);
            if (result.getExitCode() == 0) {
                Object[] values = obtainSerializedResults(result.getStdOut());
                return buildTestResult(values, executionTimeLimit);
            }
            return buildTestResultOnFail(result.getStdErr());
        } catch (ProcessRunnerException e) {
            throw new DotNetExternalTesterInvokerException("Could not execute external tester application", e);
        } catch (ProcessTimeoutException e) {
            throw new DotNetExternalTesterInvokerException("Timeout waiting for External tester to complete", e);
        } catch (IOException e) {
            throw new DotNetExternalTesterInvokerException("Problem serializing args/result to/from External tester",
                                                            e);
        }
    }

    private static TestResult buildTestResultOnFail(String stdErr) {
        TestResult result = new TestResult();

        //I hate to do this like this, but sometimes the whole .NET process
        //runs out of memory because of the way we limit things.
        //This should be treated as a code failure, not as a tester failure
        if (stdErr.startsWith("\nUnhandled Exception: OutOfMemoryException.")) {
            result.setStatus(TestResult.STATUS_FAIL);
            result.setMessage("The code exceeded the memory limit");
            return result;
        } else if (stdErr.startsWith("\nProcess is terminated due to StackOverflowException")) {
            result.setStatus(TestResult.STATUS_FAIL);
            result.setMessage("Unhandled Exception: StackOverflowException");
            return result;
        } else if (stdErr.startsWith("Required method")) {
            //method not found
            result.setStatus(TestResult.STATUS_FAIL);
            result.setMessage(stdErr);
            return result;
        } else if (stdErr.startsWith("\nReturned array or string exceeded limit")) {
            // Array or string too long
            result.setStatus(TestResult.STATUS_FAIL);
            result.setMessage(stdErr);
            return result;
        }

        result.setStatus(TestResult.STATUS_TESTER_FAILURE);
        result.setMessage(stdErr);
        return result;
    }
    /**
     * <p>build the test result.</p>
     * @param values the test result values.
     * @param executionTimeLimit the execution time limit.
     * @return the test result.
     */
    private static TestResult buildTestResult(Object[] values, int executionTimeLimit) {
        TestResult result = new TestResult();
        int resultCode = ((Number) values[0]).intValue();
        result.setExecutionTime(((Number) values[1]).longValue());
        result.setMaxMemoryUsed(((Number) values [2]).longValue());
        result.setReturnValue(values [3]);
        result.setStdOut((String) values [4]);
        result.setStdErr((String) values [5]);
        result.setStackTrace((String) values [6]);

        if (resultCode == 1) {
            result.setStatus(TestResult.STATUS_TIMEOUT);
            result.setMessage("The code execution time exceeded the "
                            + Formatter.getExecutionTimeLimitPresent(executionTimeLimit) + " second time limit.");
        } else {
            if (resultCode == -1) {
                result.setStatus(TestResult.STATUS_FAIL);
                result.setMessage("The code threw an exception.");
            }
        }
        return result;
    }

    private static InputStream buildStdIn(DataType[] argTypes, Object[] args, DataType resultType) throws IOException {
        DotNetCSHandler serializer = new DotNetCSHandler();
        byte[] types = CType.convert(argTypes);
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        serializer.setDataOutput(new BasicTypeDataOutputImpl(data));
        serializer.writeObjectArray(new Object[] {types, args});
        String encodedData = Base64Encoding.encode64(data.toByteArray());
        if (log.isDebugEnabled()) {
            log.debug("Generated stdIn: " + encodedData);
        }
        return new ByteArrayInputStream(encodedData.getBytes());
    }

    private static Object[] obtainSerializedResults(String stdOut) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Result received: " + stdOut);
        }
        DotNetCSHandler serializer = new DotNetCSHandler();
        byte[] data = Base64Encoding.decode64(stdOut);
        serializer.setDataInput(new BasicTypeDataInputImpl(new ByteArrayInputStream(data), data.length));
        return serializer.readObjectArray();
    }
}
