/*
* Copyright (C) 2007-2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * DotNetTester
 *
 * Created 01/12/2007
 */
package com.topcoder.services.tester;

import java.io.File;

import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.services.tester.common.TestRequest;
import com.topcoder.services.tester.common.TestResult;
import com.topcoder.services.tester.invoke.DotNetExternalTesterInvoker;
import com.topcoder.services.tester.invoke.DotNetExternalTesterInvokerException;
import com.topcoder.shared.problem.DataType;

/**
 * Process a .NET test. (I.E. execute the users compiled code, with user
 * supplied arguments)
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
 * @autor Diego Belfer (Mural), savon_cn
 * @version 1.2
 */
public class DotNetTester extends BaseTester {

    public DotNetTester() {
    }
    /**
     * <p>test the .net srm submission</p>
     * @param testRequest the test request.
     * @param args the arguments.
     * @param workFolder the working folder.
     * @throws DotNetExternalTesterInvokerException
     *          if any error occur during invoke dotNet external program.
     */
    protected TestResult doTest(TestRequest testRequest, Object[] args, File workFolder) throws DotNetExternalTesterInvokerException {
        String className = testRequest.getComponent().getClassName().trim();
        DataType resultType = testRequest.getComponent().getReturnType();
        DataType[] argTypes = testRequest.getComponent().getParamTypes();
        String methodName = testRequest.getComponent().getMethodName();
        ComponentFiles problemFiles = testRequest.getComponentFiles();
        /* write out the executable */
        problemFiles.storeClasses(workFolder.getAbsolutePath());
        String fullComponentPath = new File(workFolder, problemFiles.getClassesDir()).getAbsolutePath();
        String assemblyFile = className+".dll";
        TestResult results = DotNetExternalTesterInvoker.test(assemblyFile, className, methodName, argTypes,
                args, resultType,  fullComponentPath, testRequest.getComponent().getProblemCustomSettings());
        return buildResult(results);
    }

    private TestResult buildResult(TestResult results) {
        return results;
    }
}
