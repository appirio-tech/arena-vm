/*
* Copyright (C) 2007-2014 TopCoder Inc., All Rights Reserved.
*/

/*
 * JAVATester
 * 
 * Created 12/28/2007
 */
package com.topcoder.services.tester;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.server.tester.JavaComponentFiles;
import com.topcoder.services.tester.common.TestRequest;
import com.topcoder.services.tester.common.TestResult;
import com.topcoder.services.tester.invoke.CodeTester;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.ProblemCustomSettings;
import com.topcoder.shared.problem.SimpleComponent;
import com.topcoder.shared.util.logging.Logger;

/**
 * Process a java user test. (I.E. execute the users compiled code, with user
 * supplied arguments)
 *
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine - Customize Execution Time and Memory Limits for SRM v1.0):
 * <ol>
 *      <li>Update {@link #doTest(TestRequest, Object[], File)} method.</li>
 *      <li>Update {@link #test(String, String, String,DataType[], Object[], Map, int, int)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #test(String, String, String,DataType[], Object[], Map, ProblemCustomSettings)} method.</li>
 * </ol>
 * </p>
 * 
 * <p>
 * Changes in version 1.3 (Module Assembly - Return Peak Memory Usage for Executing SRM Java Solution):
 * <ol>
 *      <li>Updated {@link #buildTestResult(ArrayList)} method to set the peak memory.</li>
 * </ol>
 * </p>
 * 
 * @autor Diego Belfer (Mural), savon_cn, notpad
 * @version 1.3
 */

public class JAVATester extends BaseTester {
    private static final Logger log = Logger.getLogger(JAVATester.class);

    public JAVATester() {
    }
    /**
     * <p>test the java srm submition</p>
     * @param testRequest the test request.
     * @param args the arguments.
     * @param workFolder the working folder.
     */
    protected TestResult doTest(TestRequest testRequest, Object[] args, File workFolder) {
        SimpleComponent component = testRequest.getComponent();
        JavaComponentFiles cf= (JavaComponentFiles) testRequest.getComponentFiles();
        DataType[] argTypes = component.getParamTypes();
        String packageName = cf.getPackageName();
        String className = component.getClassName().trim();
        String methodName = component.getMethodName();
        return test(packageName, className, methodName, argTypes, args, getFilesMap(testRequest),
                testRequest.getComponent().getProblemCustomSettings());
    }
    
    /**
     * <p>test the java srm submition</p>
     * @param packageName the package name.
     * @param className the class name.
     * @param methodName the method name.
     * @param argTypes the arguments types.
     * @param args the arguments objects.
     * @param files the mapping files.
     * @param custom problem customization.
     * @return the test result.
     */
    public TestResult test(String packageName, String className, String methodName,
            DataType[] argTypes, Object[] args, Map files, ProblemCustomSettings custom) {
        ArrayList resultVal = CodeTester.test(packageName, className, methodName, argTypes,
                args, files, custom);
        return buildTestResult(resultVal);
    }
    
    /**
     * <p>Build the test result</p>
     * @param resultVal an array contains the result values.
     *
     * @return the test result.
     */
    private TestResult buildTestResult(ArrayList resultVal) {
        TestResult result = new TestResult();
        result.setStdOut((String) resultVal.get(1));
        String stdErr = (String) resultVal.get(2);
        result.setReturnValue(resultVal.get(3));
        result.setExecutionTime((long) (((Number) resultVal.get(4)).doubleValue()*1000) );
        result.setMaxMemoryUsed(((Long)resultVal.get(6)).longValue());
        if ( ((Boolean) resultVal.get(5)).booleanValue()) {
            result.setStatus(TestResult.STATUS_TIMEOUT);
            result.setMessage(stdErr);
        } else if (((Boolean) resultVal.get(0)).booleanValue()) {
            result.setStatus(TestResult.STATUS_OK);
            result.setStdErr(stdErr);
        } else  {
            result.setStatus(TestResult.STATUS_FAIL);
            result.setMessage(stdErr);
        }
        return result;
    }


    private Map getFilesMap(TestRequest testRequest) {
        // temporary hashmap for storing the classes
        Map classList = new HashMap();
        classList.putAll(testRequest.getComponentFiles().getClassMap());
        List problemFileList = testRequest.getDependencyComponentFiles();
        // stick all of the other problemfiles's classes into the hashmap
        Iterator pfIter = problemFileList.iterator();
        while (pfIter.hasNext()) {
            ComponentFiles pfTemp = (ComponentFiles) pfIter.next();
            classList.putAll(pfTemp.getClassMap());
        }
        classList.putAll(testRequest.getCompiledWebServiceClientFiles());
        log.debug("classlist: " + classList);
        return classList;
    }
}
