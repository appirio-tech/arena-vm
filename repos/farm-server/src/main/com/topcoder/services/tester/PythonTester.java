/*
 * Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.services.tester;

import java.io.File;

import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.services.tester.common.TestRequest;
import com.topcoder.services.tester.common.TestResult;
import com.topcoder.services.tester.invoke.PythonTest;
import com.topcoder.shared.language.JavaLanguage;

/**
 * <p>
 * Changes in version 1.0 (TC Competition Engine - C++ and Python Customization Support for SRM v1.0):
 * <ol>
 *      <li>Update {@link #doTest(TestRequest testRequest, Object[] args, File workFolder)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine - Customize Execution Time and Memory Limits for SRM v1.0):
 * <ol>
 *      <li>Update {@link #doTest(TestRequest, Object[], File)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #doTest(TestRequest, Object[], File)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (PoC Assembly - Return Peak Memory Usage for Executing SRM Solution):
 * <ol>
 *      <li>Update {@link #buildResult(PythonTest)} to include the maximum memory used.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (Python3 support):
 * <ol>
 *      <li>Added {@link #python3} fields.</li>
 *      <li>Updated constructor to take <code>python3</code> parameter.</li>
 *      <li>Updated {@link #doTest(TestRequest, Object[], File)} method.</li>
 * </ol>
 * </p>
 *
 * @author Diego Belfer (mural), savon_cn, dexy, liuliquan
 * @version 1.4
 */
public class PythonTester extends BaseTester {

    private final boolean python3;
    public PythonTester(boolean python3) {
        this.python3 = python3;
    }
    /**
     * <p>
     * do the Python SRM Test
     * </p>
     * @param testRequest the test request.
     * @param args the test arguments.
     * @param workFolder the working folder.
     */
    protected TestResult doTest(TestRequest testRequest, Object[] args, File workFolder) {
        String resultType = testRequest.getComponent().getReturnType(JavaLanguage.ID);
        String className = testRequest.getComponent().getClassName();

        ComponentFiles problemFiles = testRequest.getComponentFiles();
        /* write out the executable */

        problemFiles.storeClasses(workFolder.getAbsolutePath());
        String fullComponentPath = new File(workFolder, problemFiles.getClassesDir()).getAbsolutePath();
        PythonTest test_results = new PythonTest(resultType, args, className, fullComponentPath, false,
                testRequest.getComponent().getProblemCustomSettings(), this.python3);

        return buildResult(test_results);
    }

    /**
     * Builds the result.
     *
     * @param test_results the test results
     * @return the test result
     */
    private TestResult buildResult(PythonTest test_results) {
        TestResult result = new TestResult();
        if (!test_results.tester_success) {
            result.setStatus(TestResult.STATUS_TESTER_FAILURE);
            result.setMessage(test_results.tester_errlog);
        } else {
            result.setExecutionTime(test_results.submission_usedcpu);
            result.setMaxMemoryUsed(test_results.submission_maxusedmem);
            result.setStdOut(test_results.submission_stdout);
            result.setStdErr(test_results.submission_stderr);
            result.setReturnValue(test_results.submission_result);
            result.setStackTrace(test_results.crash_backtrace);
            if (test_results.isTimeout()) {
                result.setStatus(TestResult.STATUS_TIMEOUT);
                result.setMessage(test_results.epitaph());
            } else {
                if (test_results.submission_exitval != 0) {
                    result.setStatus(TestResult.STATUS_FAIL);
                    result.setMessage(test_results.epitaph());
                }
            }
        }
        return result;
    }
}
