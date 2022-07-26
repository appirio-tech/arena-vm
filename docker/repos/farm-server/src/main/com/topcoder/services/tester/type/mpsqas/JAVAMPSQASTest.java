/*
 * Copyright (C) 2002 - 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.services.tester.type.mpsqas;

/**
 * JAVAMPSQASTest.java
 *
 * Created on Jul 31, 2002
 */

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.topcoder.server.common.ServerContestConstants;
import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.services.tester.invoke.CodeTester;
import com.topcoder.services.util.Formatter;
import com.topcoder.services.util.datatype.ArgumentCloner;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.util.logging.Logger;

/**
 * Process a Java MPSQAS test.
 *
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine - Customize Execution Time and Memory Limits for SRM v1.0):
 * <ol>
 *      <li>Update {@link #processJavaMPSQASTest(MPSQASFiles mpsqasFiles)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #processJavaMPSQASTest(MPSQASFiles mpsqasFiles)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (PoC Assembly - Return Peak Memory Usage for Executing SRM Solution):
 * <ol>
 *      <li>Update {@link #processJavaMPSQASTest(MPSQASFiles)} method to include maximum memory
 *      used (in MB) in the result (it's 'N/A' now).</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (Module Assembly - Return Peak Memory Usage for Executing SRM Java Solution):
 * <ol>
 *      <li>Updated {@link #processJavaMPSQASTest(MPSQASFiles)} method to include maximum memory
 *      used (in MB) in the result.</li>
 * </ol>
 * </p>
 * 
 * @author Steven Fuller, dexy, notpad
 * @version 1.4
 */
public class JAVAMPSQASTest {

    private static final Logger log = Logger.getLogger(JAVAMPSQASTest.class);

    /**
     * Process a Java MPSQAS Test.
     *
     * @param mpsqasFiles          MPSQASFiles
     *
     * @return boolean          Status of execution
     */
    public static boolean processJavaMPSQASTest(MPSQASFiles mpsqasFiles) {
        boolean valid = true;

        List argTypesList = mpsqasFiles.getArgTypes();
        DataType[] argTypes = (DataType[]) argTypesList.toArray(new DataType[argTypesList.size()]);
        List args = mpsqasFiles.getArgVals();

        String packageName = mpsqasFiles.getPackageName();
        String className = mpsqasFiles.getClassName();
        String methodName = mpsqasFiles.getMethodName();
        //String resultType = mpsqasFiles.getResultType();

        Map classList = mpsqasFiles.getLoadableClassFiles();

        log.info("now processing MPSQAS Java test");
        if (log.isDebugEnabled()) {
	        log.debug("Classname: "+packageName+"."+className);
    	    log.debug("Method: "+methodName);
        	log.debug("Args: "+args);
		}

        try {
            ArrayList resultVal = CodeTester.test(packageName, className,
                    methodName, argTypes, ArgumentCloner.cloneArgs(args.toArray()),
                    classList, mpsqasFiles.getProblemCustomSettings());

            mpsqasFiles.setTestStatus(((Boolean) resultVal.get(0)).booleanValue());
            mpsqasFiles.setStdOut((String) resultVal.get(1));
            mpsqasFiles.setStdErr((String) resultVal.get(2));
            mpsqasFiles.setExceptionText(null);
            mpsqasFiles.setResult(resultVal.get(3));
            mpsqasFiles.setExecutionTime(((Double) resultVal.get(4)).doubleValue());
            long peakMemory = ((Long) resultVal.get(6)).longValue();
            if (peakMemory >= 0) {
                mpsqasFiles.setMaxMemoryUsed((double) peakMemory / 1024.0);
            } else {
                mpsqasFiles.setMaxMemoryUsed(-1.0);
            }
            
            // format and truncate the result into a user-readable string
            DecimalFormat df = new DecimalFormat("0.000");
            String maxMemUsed = (peakMemory >= 0) ? df.format((double) peakMemory / 1024.0)
                    : "N/A";
            String result = Formatter.getTestResults(resultVal.get(4).toString(),
                    maxMemUsed,
                    ServerContestConstants.makePretty(resultVal.get(3)),
                    resultVal.get(1).toString(),
                    resultVal.get(2).toString());

            // truncate result before returning it to the user.
            result = Formatter.truncate(result, resultVal.get(2).toString());

            mpsqasFiles.setResultValue(result);
        } catch (Exception e) {
            valid = false;
            mpsqasFiles.setTestStatus(false);
            mpsqasFiles.setExceptionText(e.toString());
        }

        return valid;
    }

}
