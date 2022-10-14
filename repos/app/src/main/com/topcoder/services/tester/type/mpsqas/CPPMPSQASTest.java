/*
 * Copyright (C) 2002 - 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.services.tester.type.mpsqas;

/**
 * CPPMPSQASTest.java
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
import com.topcoder.services.tester.invoke.CPPTest;
import com.topcoder.services.util.Formatter;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.util.logging.Logger;

/**
 * Process a C++ MPSQAS Test.
 *
 * <p>
 * Changes in version 1.1 (TC Competition Engine - C++ and Python Customization Support for SRM v1.0):
 * <ol>
 *      <li>Update {@link #processCPPMPSQASTest(MPSQASFiles mpsqasFiles)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine - Customize Execution Time and Memory Limits for SRM v1.0):
 * <ol>
 *      <li>Update {@link #processCPPMPSQASTest(MPSQASFiles mpsqasFiles)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #processCPPMPSQASTest(MPSQASFiles mpsqasFiles)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (PoC Assembly - Return Peak Memory Usage for Executing SRM Solution):
 * <ol>
 *      <li>Update {@link #processCPPMPSQASTest(MPSQASFiles)} method to include maximum memory used (in MB)
 *      in the result.</li>
 * </ol>
 * </p>
 *
 * @author Steven Fuller, savon_cn, dexy
 * @version 1.4
 */
public class CPPMPSQASTest {

    private static final Logger log = Logger.getLogger(CPPMPSQASTest.class);

    /**
     * Process a C++ MPSQAS Test.
     *
     * @param mpsqasFiles       mpsqasFiles
     *
     * @return boolean          Status of execution
     */
    public static boolean processCPPMPSQASTest(MPSQASFiles mpsqasFiles) {
        List argVals = mpsqasFiles.getArgVals();

        Object[] args = new Object[argVals.size()];
        for (int i = 0; i < argVals.size(); i++) {
            args[i] = (Object) argVals.get(i);
        }

        String className = mpsqasFiles.getClassName();
        String methodName = mpsqasFiles.getMethodName();
        String resultType = mpsqasFiles.getResultType().getDescription();
        String classFileDir = mpsqasFiles.getPackageName().replace('.', '/');

        String path = ServicesConstants.SOLUTIONS + classFileDir + File.separator;

        File classFile = null;

        log.info("now processing MPSQAS C++ test");

        try {
            File pathfile = new File(path);

            pathfile.mkdirs();

            // iterate through the class list, and write the binary
            Iterator iter = mpsqasFiles.getClassFiles().entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry me = (Map.Entry) iter.next();

                String fileName = (String) me.getKey();
                if (fileName.endsWith(".o") || fileName.endsWith(".cc")) {
                    continue;
                }

                FileOutputStream out = new FileOutputStream(new File(ServicesConstants.SOLUTIONS, fileName));
                out.write((byte[]) me.getValue());
                out.close();
            }

            classFile = new File(path + className);
            if (!classFile.exists()) {
                log.error("requested c++ binary " + className + " not saved!");

                mpsqasFiles.setExceptionText("failed to save the executable");
                mpsqasFiles.setTestStatus(false);

                return false;
            }
        } catch (IOException e) {
            log.error("IO exception while trying to save binary " + className, e);

            mpsqasFiles.setExceptionText("failed to save the executable");
            mpsqasFiles.setTestStatus(false);

            return false;
        }

        CPPTest ct = new CPPTest(resultType, args, className, path, false,
                mpsqasFiles.getProblemCustomSettings());

        mpsqasFiles.setTestStatus(ct.tester_success);
        mpsqasFiles.setStdOut(ct.submission_stdout);
        mpsqasFiles.setStdErr(ct.submission_stderr);
        if (ct.tester_success) {
            mpsqasFiles.setExceptionText(null);
        } else {
            mpsqasFiles.setExceptionText(ct.tester_errlog);
        }

        /**
         * add the time exceed notification
         */
        if (ct.isTimeout()) {
            mpsqasFiles.setStdErr(ct.epitaph());
        } else {
            if (ct.submission_exitval != 0) {
                mpsqasFiles.setStdErr(ct.epitaph());
            }
        }
        String stdErr = "";
        if (mpsqasFiles.getStdErr() != null) {
            stdErr = mpsqasFiles.getStdErr();
        }
        // the actual returned object
        mpsqasFiles.setResult(ct.submission_result);

        mpsqasFiles.setExecutionTime((double) ct.submission_usedcpu / 1000.0);
        if (ct.submission_maxmemused >= 0) {
            mpsqasFiles.setMaxMemoryUsed((double) ct.submission_maxmemused / 1024.0);
        } else {
            mpsqasFiles.setMaxMemoryUsed(-1.0);

        }

        // format and truncate the result into a user-readable string
        DecimalFormat df = new DecimalFormat("0.000");
        String maxMemUsed = (ct.submission_maxmemused >= 0) ? df.format((double) ct.submission_maxmemused / 1024.0)
                                                            : "N/A";
        String result = Formatter.getTestResults(Double.toString((double) ct.submission_usedcpu / 1000.0),
                                                 maxMemUsed,
                                                 ContestConstants.makePretty(ct.submission_result),
                                                 ct.submission_stdout,
                                                 stdErr);
        result = Formatter.truncate(result, stdErr);

        mpsqasFiles.setResultValue(result);

        classFile.delete();

        return true;
    }
}
