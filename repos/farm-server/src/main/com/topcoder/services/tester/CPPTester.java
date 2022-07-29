/*
 * Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.services.tester;

import java.io.File;

import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.services.tester.common.TestRequest;
import com.topcoder.services.tester.common.TestResult;
import com.topcoder.services.tester.invoke.CPPTest;
import com.topcoder.shared.language.JavaLanguage;

/**
 * Process a C++ test. (I.E. execute the users compiled code, with user
 * supplied arguments)
 *
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
 *      <li>Update {@link #buildResult(CPPTest)} method to include maximum memory used (in KB) in the result.</li>
 * </ol>
 * </p>
 *
 * @version 1.3
 * @author Diego Belfer (Mural), savon_cn, dexy
 */
public class CPPTester extends BaseTester {

    /**
     * Instantiates a new CPP tester.
     */
    public CPPTester() {
    }

    /**
     * <p>
     * do the CPP SRM Test
     * </p>.
     *
     * @param testRequest the test request.
     * @param args the test arguments.
     * @param workFolder the working folder.
     * @return the test result
     */
    protected TestResult doTest(TestRequest testRequest, Object[] args, File workFolder) {
        String className = testRequest.getComponent().getClassName().trim();
        String resultType = testRequest.getComponent().getReturnType(JavaLanguage.ID);
        ComponentFiles problemFiles = testRequest.getComponentFiles();
        /* write out the executable */
        problemFiles.storeClasses(workFolder.getAbsolutePath());
        String fullComponentPath = new File(workFolder, problemFiles.getClassesDir()).getAbsolutePath();
        CPPTest test_results = new CPPTest(resultType, args, className, fullComponentPath,
            false, testRequest.getComponent().getProblemCustomSettings());
        return buildResult(test_results);
    }

    /**
     * Builds the result of CPP testing.
     *
     * @param test_results result of the testing of CPP solution
     * @return the test result
     */
    private TestResult buildResult(CPPTest test_results) {
        TestResult result = new TestResult();
        if (!test_results.tester_success) {
            result.setStatus(TestResult.STATUS_TESTER_FAILURE);
            result.setMessage(test_results.tester_errlog);
        } else {
            result.setExecutionTime(test_results.submission_usedcpu);
            result.setMaxMemoryUsed(test_results.submission_maxmemused);
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
