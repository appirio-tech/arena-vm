/*
 * Copyright (C) 2002 - 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.services.tester.type.mpsqas;

/**
 * DotNetMPSQASTest.java
 *
 * Created on Jul 31, 2002
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.services.tester.common.TestResult;
import com.topcoder.services.tester.invoke.DotNetExternalTesterInvoker;
import com.topcoder.services.tester.invoke.DotNetExternalTesterInvokerException;
import com.topcoder.services.util.Formatter;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.util.logging.Logger;

/**
 * Process a C#/VB.Net MPSQAS Test.
 *
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine - Customize Execution Time and Memory Limits for SRM v1.0):
 * <ol>
 *      <li>Update {@link #processDotNetMPSQASTest(MPSQASFiles mpsqasFiles)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #processDotNetMPSQASTest(MPSQASFiles mpsqasFiles)} method.</li>
 * </ol>
 * </p>
 *
 *
 * <p>
 * Changes in version 1.3 (Module Assembly - Return Peak Memory Usage for Executing SRM DotNet Solution):
 * <ol>
 *      <li>Update {@link #processDotNetMPSQASTest(MPSQASFiles)} method to include maximum memory
 *      used (in MB) in the result.</li>
 * </ol>
 * </p>
 *
 * @author visualage, savon_cn, dexy
 * @version 1.3
 */
public class DotNetMPSQASTest {

    private static final Logger log = Logger.getLogger(DotNetMPSQASTest.class);

    /**
     * Process a C#/VB.Net MPSQAS Test.
     *
     * @param mpsqasFiles       mpsqasFiles
     *
     * @return boolean          Status of execution
     */
    public static boolean processDotNetMPSQASTest(MPSQASFiles mpsqasFiles) {
        List argVals = mpsqasFiles.getArgVals();

        Object[] args = new Object[argVals.size()];
        for (int i = 0; i < argVals.size(); i++) {
            args[i] = argVals.get(i);
        }

        List argTypesList = mpsqasFiles.getArgTypes();
        DataType[] argTypes = (DataType[]) argTypesList.toArray(new DataType[argTypesList.size()]);
        String className = mpsqasFiles.getClassName();
        String methodName = mpsqasFiles.getMethodName();
        DataType resultType = mpsqasFiles.getResultType();
        String classFileDir = mpsqasFiles.getPackageName().replace('.', File.separator.charAt(0));

        String path = ServicesConstants.SOLUTIONS + classFileDir + File.separator;

        File classFile = null;

        log.info("now processing MPSQAS .Net test");

        try {
            File pathfile = new File(path);

            pathfile.mkdirs();

            // iterate through the class list, and write the binary
            Iterator iter = mpsqasFiles.getClassFiles().entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry me = (Map.Entry) iter.next();

                String fileName = (String) me.getKey();
                if (!fileName.endsWith(".dll")) {
                    continue;
                }

                FileOutputStream out = new FileOutputStream(new File(ServicesConstants.SOLUTIONS, fileName));
                out.write((byte[]) me.getValue());
                out.close();
            }

            classFile = new File(path + className + ".dll");
            if (!classFile.exists()) {
                log.error("requested .Net library " + className + " not saved!");

                mpsqasFiles.setExceptionText("failed to save the executable");
                mpsqasFiles.setTestStatus(false);

                return false;
            }
        } catch (IOException e) {
            log.error("IO exception while trying to save library " + className, e);

            mpsqasFiles.setExceptionText("failed to save the executable");
            mpsqasFiles.setTestStatus(false);

            return false;
        }

        try {
            TestResult result = DotNetExternalTesterInvoker.test(classFile.getPath(), className, methodName, argTypes,
                    args, resultType, path, mpsqasFiles.getProblemCustomSettings());

            mpsqasFiles.setTestStatus(result.isSuccess());
            mpsqasFiles.setStdOut(result.getStdOut());
            mpsqasFiles.setStdErr(result.getStdErr());
            if (result.isSuccess()) {
                mpsqasFiles.setExceptionText(null);
            } else {
                mpsqasFiles.setExceptionText(result.getStackTrace());
            }

            /**
             * handle the time exceed notification
             */
            if (result.getStatus() == TestResult.STATUS_TIMEOUT ||
                    result.getStatus() == TestResult.STATUS_FAIL) {
                mpsqasFiles.setStdErr(result.getMessage());
            }
            String stdErr = "";
            if (mpsqasFiles.getStdErr() != null) {
                stdErr = mpsqasFiles.getStdErr();
            }
            // the actual returned object
            mpsqasFiles.setResult(result.getReturnValue());

            mpsqasFiles.setExecutionTime(result.getExecutionTime() / 1000.0);
            mpsqasFiles.setMaxMemoryUsed(result.getMaxMemoryUsed() / 1024.0);

            DecimalFormat df = new DecimalFormat("0.000");
            String maxMemUsed = (result.getMaxMemoryUsed() >= 0) ? df.format(mpsqasFiles.getMaxMemoryUsed())
                                                                : "N/A";

            // format and truncate the result into a user-readable string
            String resultStr = Formatter.getTestResults(Double.toString(result.getExecutionTime() / 1000.0),
                                                        maxMemUsed,
                                                        ContestConstants.makePretty(result.getReturnValue()),
                                                        result.getStdOut(),
                                                        stdErr);
            resultStr = Formatter.truncate(resultStr, stdErr);

            mpsqasFiles.setResultValue(resultStr);
        } catch (DotNetExternalTesterInvokerException e) {
            log.error("DotNet tester fails.", e);
            mpsqasFiles.setTestStatus(false);
            mpsqasFiles.setStdErr(e.toString());
            return false;
        } finally {
            classFile.delete();
        }

        return true;
    }
}
