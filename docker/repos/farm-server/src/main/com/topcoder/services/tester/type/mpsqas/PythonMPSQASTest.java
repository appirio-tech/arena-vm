/*
 * Copyright (C)  - 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.services.tester.type.mpsqas;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.services.tester.invoke.PythonTest;
import com.topcoder.services.util.Formatter;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.util.logging.Logger;

/**
 * <p>
 * Process a Python MPSQAS Test.
 * </p>
 *
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine - Customize Execution Time and Memory Limits for SRM v1.0):
 * <ol>
 *      <li>Update {@link #processPythonMPSQASTest(MPSQASFiles mpsqasFiles)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #processPythonMPSQASTest(MPSQASFiles mpsqasFiles)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (PoC Assembly - Return Peak Memory Usage for Executing SRM Solution):
 * <ol>
 *      <li>Update {@link #processPythonMPSQASTest(MPSQASFiles)} method to include maximum memory
 *      used (in MB) in the result.</li>
 * </ol>
 * </p>
 *
 * @author savon_cn, dexy
 * @version 1.3
 */
public class PythonMPSQASTest {
    private static final Logger log = Logger.getLogger(PythonMPSQASTest.class);
    /**
     * Process a Python MPSQAS Test.
     *
     * @param mpsqasFiles the mpsqasFiles.
     *
     * @return boolean the status of execution.
     */
    public static boolean processPythonMPSQASTest(MPSQASFiles mpsqasFiles) {
        List argVals = mpsqasFiles.getArgVals();

        Object[] args = new Object[argVals.size()];
        for (int i = 0; i < argVals.size(); i++) {
            args[i] = (Object) argVals.get(i);
        }

        String className = mpsqasFiles.getClassName();
        String resultType = mpsqasFiles.getResultType().getDescription();
        String classFileDir = mpsqasFiles.getPackageName().replace('.', '/');

        String path = ServicesConstants.SOLUTIONS + classFileDir + File.separator;
        log.info("now processing MPSQAS Python test");

        try {
            File pathfile = new File(path);
            pathfile.mkdirs();
            // iterate through the class list, and write the binary
            Iterator iter = mpsqasFiles.getClassFiles().entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry me = (Map.Entry) iter.next();

                String fileName = (String) me.getKey();
                if (fileName.endsWith(".pyc")) {
                    FileOutputStream out = new FileOutputStream(new File(ServicesConstants.SOLUTIONS, fileName));
                    out.write((byte[]) me.getValue());
                    out.close();
                }
            }
        } catch (IOException e) {
            log.error("IO exception while trying to save python compiled " + className, e);
            mpsqasFiles.setExceptionText("failed to save the python compiled stuff");
            mpsqasFiles.setTestStatus(false);
            return false;
        }

        PythonTest ct = new PythonTest(resultType, args, className, path, false,
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
        mpsqasFiles.setMaxMemoryUsed((double) ct.submission_maxusedmem / 1024.0);
        DecimalFormat df = new DecimalFormat("0.000");
        // format and truncate the result into a user-readable string
        String result = Formatter.getTestResults(Double.toString((double) ct.submission_usedcpu / 1000.0),
                                                 df.format((double) ct.submission_maxusedmem / 1024.0),
                                                 ContestConstants.makePretty(ct.submission_result),
                                                 ct.submission_stdout,
                                                 stdErr);
        result = Formatter.truncate(result, stdErr);

        mpsqasFiles.setResultValue(result);
        return true;
    }
}
