/*
 * Copyright (C) 2001 - 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.services.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.topcoder.server.common.ServerContestConstants;
import com.topcoder.services.tester.common.TestResult;

/**
 * This class contains a set of output formatters.
 *
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine - Customize Execution Time and Memory Limits for SRM v1.0):
 * <ol>
 *      <li>Add {@link #getExecutionTimeLimitPresent(long executionTimeLimit)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *     <li>Updated {@link #formatTestResults(TestResult, String)} to handle comparison result.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (PoC Assembly - Return Peak Memory Usage for Executing SRM Solution):
 * <ol>
 *     <li> Add {@link #getTestResults(String, String, Object, String, String)} to include
 *     maximum memory used.</li>
 *     <li> Add {@link #getTestResults(String, String, Object, String, String, boolean)} to
 *     include maximum memory used.</li>
 *     <li> Update {@link #formatTestResults(TestResult)} to include maximum memory used.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (Return Peak Memory Usage for Marathon Match Cpp v1.0):
 * <ol>
 *      <li>Add {@link #getPeakMemoryUsedInfo(long peakMemoryUsed)} method.</li>
 *      <li>Add {@link #formatPeakMemoryUsed(double d)} method.</li>
 *      <li>Update {@link #formatTestResults(TestResult result)} method.</li>
 *      <li>Update {@link #getTestResults(String t, long p, Object ret, String so, String se)} method.</li>
 *      <li>Update {@link #getTestResults(String t, long p, Object ret, String so, String se, boolean success)} method.</li>
 * </ol>
 * </p>
 * @author Alex Roman, savon_cn, gevak, dexy, TCSASSEMBLER
 * @version 1.4
 */
public final class Formatter {

    private Formatter() {
    }

    /** longest user-generated string we should retain anywhere */
    public static final int MAX_USER_STRING = 8000;

    /**
     * Truncate a string to the given length.  We seem to do this a lot for logging, printing,
     * returning things to the user.
     * */
    public static String truncate(String s) {
        final int max = MAX_USER_STRING;
        if (s.length() <= max) {
            return s;
        }
        return s.substring(0, max) + " ... <truncated>";
    }
    /**
     * <p>get the execution time limit presentation</p>
     * @param executionTimeLimit the execution time limit.
     * @return the execution time limit presentation.
     * @since 1.1
     */
    public static String getExecutionTimeLimitPresent(long executionTimeLimit) {
        String ret = "";
        double timeWithSecond = (double)executionTimeLimit/1000;
        NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
        format.setMinimumFractionDigits(3);
        format.setMaximumFractionDigits(3);
        format.setMaximumIntegerDigits(10);
        format.setMinimumIntegerDigits(1);
        ret = format.format(timeWithSecond);
        return ret;
    }
        /** Truncate a string to the given length.  We seem to do this a lot for logging, printing, returing things to the user....
         * Added new method to also append Error Stream*/
    public static String truncate(String s, String se) {
        final int max = MAX_USER_STRING;
        if (s.length() <= max) {
            return s;
        }
        return s.substring(0, max) +
                " ... <truncated> \n\n"+
                "Standard Error:\n" + se;
    }


    /**
     * Build a string containing the stdout and stderr. Truncante the stdout if it exceeds
     * MAX_USER_STRING
     *
     * @param s  Standard output
     * @param se Standard Error
     * @return the built String. Never null
     */
    public static String truncateOutErr(String s, String se) {
        final int max = MAX_USER_STRING;
        StringBuffer sb = new StringBuffer(200);
        if (s != null) {
            if (s.length() > max) {
                sb.append(s.substring(0, max));
                sb.append(" ... <truncated>");;
            } else {
                sb.append(s);
            }
            sb.append("\n");
        }
        if (se != null) {
            sb.append("\nStandard Error:\n");
            sb.append(se);
        }
        return sb.toString();
    }
    /**
     * Get peak memory used information.
     * @param peakMemoryUsed the peak memory used in KB.
     * @return the peak memory used in MB.
     * @since 1.4
     */
    public static String getPeakMemoryUsedInfo(long peakMemoryUsed) {
        if (peakMemoryUsed < 0) {
            return "N/A";
        }
        double peakMB = ((double) peakMemoryUsed) / 1024;
        return formatPeakMemoryUsed(peakMB) + "MB";
    }
    /**
     * getTestResults formats the output that is returned back to the user.
     *
     * @param t                The execution time.
     * @param p                The long value of peak memory used.
     * @param ret              The method return value.
     * @param so               The standard output.
     * @param se               The standard error.
     * @return String          The formatted string.
     */
    public static String getTestResults(String t, long p, Object ret, String so, String se) {
        return ("Execution Time: " + t + "s\n\n" +
                "Peak Memory Used: " + getPeakMemoryUsedInfo(p) + "\n\n" +
                "Return Value:\n" + ret + "\n\n" +
                "Standard Output:\n" + so + "\n\n" +
                "Standard Error:\n" + se);
    }

    /**
     * getTestResults formats the output that is returned back to the user.
     *
     * @param t                The execution time.
     * @param m                The maximum memory used (in MB) or N/A if not used.
     * @param ret              The method return value.
     * @param so               The standard output.
     * @param se               The standard error.
     * @return String          The formatted string.
     * @since 1.3
     */
    public static String getTestResults(String t, String m, Object ret, String so, String se) {
        return ("Execution Time: " + t + "s\n\n" +
                "Peak memory used: " + m + "MB\n\n" +
                "Return Value:\n" + ret + "\n\n" +
                "Standard Output:\n" + so + "\n\n" +
                "Standard Error:\n" + se);
    }
    /**
     * Format the peak memory used.
     * @param d the peak memory used.
     * @return the formatted peak memory used.
     */
    public static String formatPeakMemoryUsed(double d) {
        DecimalFormat df = new DecimalFormat("0.000");
        return df.format(d);
    }
    /**
     * getTestResults formats the output that is returned back to the user.
     * This version of the function returns if the return value is correct.
     *
     * @param t                The execution time.
     * @param p                The long value of peak memory used.
     * @param ret              The method return value.
     * @param so               The standard output.
     * @param se               The standard error.
     * @param success          If the return value was correct.
     * @return String          The formatted string.
     */
    public static String getTestResults(String t, long p, Object ret, String so, String se, boolean success) {
        return "Correct Return Value: " + (success ? "Yes" : "No") + "\n\n" +
                getTestResults(t, p, ret, so, se);
    }

    /**
     * getTestResults formats the output that is returned back to the user.
     * This version of the function returns if the return value is correct.
     *
     * @param t                The execution time.
     * @param m                The memory used (in MB).
     * @param ret              The method return value.
     * @param so               The standard output.
     * @param se               The standard error.
     * @param success          If the return value was correct.
     * @return String          The formatted string.
     * @since 1.3
     */
    public static String getTestResults(String t, String m, Object ret, String so, String se, boolean success) {
        return "Correct Return Value: " + (success ? "Yes" : "No") + "\n\n" +
                getTestResults(t, m, ret, so, se);
    }

    /**
     * Format test results.
     *
     * @param result the result
     * @return the string
     */
    public static String formatTestResults(TestResult result) {
        StringBuffer sb = new StringBuffer();
        DecimalFormat df = new DecimalFormat("0.000");
        sb.append("Execution Time: ").append(df.format(result.getExecutionTime() / 1000.0)).append("s\n\n");
        long memoryUsed = result.getMaxMemoryUsed();
        sb.append("Peak memory used: ");
        sb.append(getPeakMemoryUsedInfo(memoryUsed)).append("\n\n");
        if (result.isSuccess()) {
            sb.append("Return Value:\n").append(ServerContestConstants.makePretty(result.getReturnValue())).append("\n\n");
        } else {
            if (result.getMessage() != null && result.getMessage().length() > 0) {
                if (result.isSystemFailure()) {
                    sb.append("TESTER FAILED: ");
                }
                sb.append(result.getMessage()).append("\n\n");
            }
        }
        sb.append("Standard Output:\n");
        if (result.getStdOut() != null && result.getStdOut().length() > 0) {
            sb.append(result.getStdOut());
        }
        sb.append("\n\n").append("Standard Error:\n");
        if (result.getStdErr() != null && result.getStdErr().length() > 0) {
            sb.append(result.getStdErr());
        }
        sb.append("\n\n");
        if (result.getStackTrace() != null && result.getStackTrace().length() > 0) {
            sb.append("Stack Trace:\n").append(result.getStackTrace()).append("\n\n");
        }
        return sb.toString();
    }

    /**
     * Formats test result.
     *
     * @param result Test result.
     * @param comparisonResult Comparison result message.
     * @return Textual representation of the test result.
     */
    public static String formatTestResults(TestResult result, String comparisonResult) {
        return "Correct Return Value: "
                + (comparisonResult.length() == 0 ? "Yes" : ("No\n\nAnswer check result: \n" + comparisonResult))
                + "\n\n" + formatTestResults(result);
    }
}
