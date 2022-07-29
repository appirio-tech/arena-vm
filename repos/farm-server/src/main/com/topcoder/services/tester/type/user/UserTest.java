/*
* Copyright (C) 2006 - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.services.tester.type.user;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import com.topcoder.netCommon.contestantMessages.response.BatchTestResponse;
import com.topcoder.netCommon.contestantMessages.response.data.BatchTestResult;
import com.topcoder.server.common.UserTestAttributes;
import com.topcoder.services.tester.BaseTester;
import com.topcoder.services.tester.TesterFactory;
import com.topcoder.services.tester.common.TestResult;
import com.topcoder.services.tester.invoke.FarmSolutionInvokator;
import com.topcoder.services.util.Formatter;
import com.topcoder.shared.util.logging.Logger;

/**
 * Process a user test. (I.E. execute the users compiled code, with user
 * supplied arguments)
 *
 * <p>
 * Changes in version 1.1 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *     <li>Updated {@link #generateResultString(UserTestAttributes, TestResult)} to handle comparison result.
 * </ol>
 * </p>
 *
 * @author Diego Belfer (Mural), gevak
 * @version 1.1
 */
public class UserTest {
	/**
     * The maximum length of the messages (error, standard output/error, stack trace, ...)
     * returned to the client.
     * @since 1.2
     */
    private static final int MAXIMUM_RETURN_MESSAGE_LENGTH = 400;

    private static final Logger log = Logger.getLogger(UserTest.class);
    private File workFolder;

    public UserTest(File workFolder) {
        this.workFolder = workFolder;
    }

    /**
     * Process the user test.
     *
     * @param userTest the user test
     * @return the user test with the results of the testing
     */
    public UserTestAttributes process(UserTestAttributes userTest) {
        if (!userTest.isBatchTest()) {
            try {
                log.info("PROCESSING USER TEST: coder id: "
                        + userTest.getCoderId() + " ProblemComponent Id: "
                        + userTest.getComponent().getComponentID());
                log.info("Usertest Args: " + Arrays.asList(userTest.getArgs()));

                TestResult testResult = doTest(userTest);
                String result = generateResultString(userTest, testResult);
                userTest.setSucceeded(true);
                userTest.setResultValue(result);
            } catch (Exception e) {
                userTest.setSucceeded(false);
                userTest.setResultValue(e.getMessage());
                log.error("Exception processing user test: ", e);
            } finally {
                log.info("UserTest Status: " + userTest.getSucceeded());
                log.info("UserTest Return:\n" + userTest.getResultValue());
            }
        } else {
            BatchTestResponse batchTestResponse = new BatchTestResponse();
            Object [] batchArgs = userTest.getArgs();
            int numTests = batchArgs.length / UserTestAttributes.BATCH_ARGS_BLOCK_SIZE;
            userTest.setSucceeded(true);
            userTest.setResultValue("" + numTests);
            UserTestAttributes tmpUserTest = new UserTestAttributes(userTest.getCoderId(),
                        userTest.getLocation(), userTest.getComponent(), userTest.getLanguage());
            tmpUserTest.setSolution(userTest.getSolution());
            tmpUserTest.setCompiledWebServiceClientFiles(userTest.getCompiledWebServiceClientFiles());
            tmpUserTest.setComponentFiles(userTest.getComponentFiles());
            tmpUserTest.setDependencyComponentFiles(userTest.getDependencyComponentFiles());
            tmpUserTest.setSubmitTime(userTest.getSubmitTime());

            for (int itest = 0; itest < numTests; itest++) {
                BatchTestResult batchTestResult = new BatchTestResult();
                Object[] args = (Object []) batchArgs [UserTestAttributes.BATCH_ARGS_BLOCK_SIZE * itest];
                Object expResult = batchArgs [UserTestAttributes.BATCH_ARGS_BLOCK_SIZE * itest + 1];
//                ArrayList<String> tmpResult = new ArrayList<String>();
                tmpUserTest.setArgs(args);
                if (expResult != null) {
                    tmpUserTest.setExpectedResult(expResult);
                }
                try {
                    log.info("PROCESSING BATCH USER TEST: coder id: "
                            + userTest.getCoderId() + " ProblemComponent Id: "
                            + userTest.getComponent().getComponentID() + " Test #" + (itest + 1));
                    log.info("Usertest Args: " + Arrays.asList(args));
                    if (expResult != null) {
                        log.info("Result: " + expResult);
                    }
                    TestResult testResult = doTest(tmpUserTest);
                    batchTestResult.setSuccess(true);
                    batchTestResult.setErrorMessage("");
                    batchTestResult.setStatus(testResult.getStatus());
                    batchTestResult.setMessage(testResult.getMessage());
                    batchTestResult.setExecutionTime(testResult.getExecutionTime());
                    batchTestResult.setPeakMemoryUsed(testResult.getMaxMemoryUsed());
                    /*
                    if (testResult.getReturnValue() != null) {
                        batchTestResult.setReturnValue(ServerContestConstants.makePretty(testResult.getReturnValue()));
                    } else {
                        batchTestResult.setReturnValue("");
                    }
                    */
                    batchTestResult.setReturnValue(testResult.getReturnValue());
                    if (expResult != null) {
                        batchTestResult.setCorrectExample(new FarmSolutionInvokator(userTest.getSolution())
                            .compare(tmpUserTest.getArgs(), tmpUserTest.getExpectedResult(),
                                    testResult.getReturnValue())
                            .length() == 0 ? "true" : "false");
                    } else {
                        batchTestResult.setCorrectExample("");
                    }
                    batchTestResult.setStdOut(getFormattedContent(testResult.getStdOut(), MAXIMUM_RETURN_MESSAGE_LENGTH));
                    batchTestResult.setStdErr(getFormattedContent(testResult.getStdErr(), MAXIMUM_RETURN_MESSAGE_LENGTH));
                    batchTestResult.setStacktrace(getFormattedContent(testResult.getStackTrace(), MAXIMUM_RETURN_MESSAGE_LENGTH));
                } catch (Exception e) {
                    batchTestResult.setSuccess(false);
                    batchTestResult.setErrorMessage(e.getMessage());
                    log.error("Exception processing user batch test: ", e);
                } finally {
                    log.info("UserTest Status: " + tmpUserTest.getSucceeded());
                    log.info("UserTest Return:\n" + tmpUserTest.getResultValue());
                }
                batchTestResponse.addResult(batchTestResult);
            }
            userTest.setBatchTestResponse(batchTestResponse);
        }
        return userTest;
    }

    /**
     * Executes the test and returns the test results.
     *
     * @param userTest the user test attributes
     * @return returns the test result
     * @since 1.2
     */
    private TestResult doTest(UserTestAttributes userTest) {
        BaseTester tester = TesterFactory.getTester(userTest.getComponentFiles().getLanguageId());
        TestResult testResult = tester.test(userTest, workFolder);
        if (testResult.isSystemFailure()) {
            try {
                //We need to keep the work folder for future analysis. So, we need to copy it with.
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                File destinationFolder = new File(workFolder.getParentFile(),
                        "errors" + File.separatorChar + workFolder.getName() + "-" + format.format(new Date()));
                destinationFolder.mkdirs();
                log.info("System Failure: folder-> " + destinationFolder.getAbsolutePath());
                FileUtils.copyDirectory(workFolder, destinationFolder);
            } catch (Exception e) {
                log.error("Could not create back up folder on system failure:", e);
            }
        }
        return testResult;
    }

    /**
     * Returns formatted content. If it too long it cuts it to specified length.
     *
     * @param content the string content to be formatted
     * @param maximumLength maximum length of the returned formatted content
     * @return if the content is null returns the empty string, otherwise
     *          if the content has length smaller than maximumLength returns the original
     *          content, otherwise returns the first maximumLength characters of the content
     * @since 1.2
     */
    private String getFormattedContent(String content, int maximumLength) {
        if (content == null) return "";
        if (content.length() > maximumLength) {
            return content.substring(0, maximumLength);
        }
        return content;
    }

    /**
     * Generates result string.
     *
     * @param userTest User test data.
     * @param testResult Test result data.
     * @return String representation of the test outcome.
     */
    private String generateResultString(UserTestAttributes userTest, TestResult testResult) {
        String result;
        if (userTest.isSuppliedTest()) {
            result = Formatter.formatTestResults(testResult, new FarmSolutionInvokator(userTest.getSolution()).compare(
                    userTest.getArgs(), userTest.getExpectedResult(), testResult.getReturnValue()));
        } else {
            result = Formatter.formatTestResults(testResult);
        }
        return Formatter.truncate(result,testResult.getStdErr());
    }
}
