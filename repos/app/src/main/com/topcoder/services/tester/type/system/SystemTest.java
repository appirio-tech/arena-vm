/*
 * Copyright (C) 2006 - 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.services.tester.type.system;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.common.ChallengeAttributes;
import com.topcoder.server.common.SystemTestAttributes;
import com.topcoder.services.tester.BaseTester;
import com.topcoder.services.tester.TesterFactory;
import com.topcoder.services.tester.common.TestResult;
import com.topcoder.services.tester.invoke.FarmSolutionInvokator;
import com.topcoder.shared.util.logging.Logger;


/**
 * Process a system test.
 *
 * <p>
 * Changes in version 1.1 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *     <li>Updated {@link #process(ChallengeAttributes)} to handle comparison result.
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (PoC Assembly - Return Peak Memory Usage for Executing SRM Solution):
 * <ol>
 *     <li>Update {@link #process(SystemTestAttributes)} method to add maximum memory used (in KB)
 *     to system test attribute.</li>
 * </ol>
 * </p>
 *
 * @author Diego Belfer (Mural), gevak, dexy
 * @version 1.2
 */
public class SystemTest {
    private Logger log = Logger.getLogger(SystemTest.class);
    private File workFolder;

    public SystemTest(File workFolder) {
        this.workFolder = workFolder;
    }

    /**
     * Processes system test.
     *
     * @param chal Test attributes.
     * @return Resulting test attributes.
     */
    public SystemTestAttributes process(SystemTestAttributes attr) {
        try {
            log.info("SYSTEM TEST: coder " + attr.getSubmission().getCoderID() +
                    ", component id " + attr.getComponent().getComponentID()+ ", testCaseId "+attr.getTestCaseId());

            log.info("Running system test case on "+attr.getComponentFiles().getClassesDir());
            BaseTester tester = TesterFactory.getTester(attr.getComponentFiles().getLanguageId());
            TestResult result = tester.test(attr, workFolder);

            if (result.isTimeout()) {
                attr.setResultCode(SystemTestAttributes.RESULT_TIMEOUT);
                attr.setMessage(result.getMessage());
                return attr;
            }

            if (!result.isSuccess()) {
                if (result.isSystemFailure()) {
                    attr.setResultCode(SystemTestAttributes.RESULT_SYSTEM_FAILURE);
                    try {
                        //We need to keep the work folder for future analisys. So, we need to copy it with.
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                        File destinationFolder = new File(workFolder.getParentFile(), "errors"+File.separatorChar+workFolder.getName()+"-"+format.format(new Date()));
                        destinationFolder.mkdirs();
                        log.info("System Failure: folder-> "+destinationFolder.getAbsolutePath());
                        FileUtils.copyDirectory(workFolder, destinationFolder);
                    } catch (Exception e) {
                        log.error("Could not create back up folder on system failure:" ,e);
                    }
                } else {
                    attr.setResultCode(SystemTestAttributes.RESULT_EXCEPTION);
                }
                attr.setMessage(result.getMessage());
                return attr;
            }

            Object resultObj = result.getReturnValue();
            if (resultObj == null) resultObj = "null";
            attr.setResultValue(resultObj);
            attr.setExecTime(result.getExecutionTime());
            attr.setMaxMemoryUsed(result.getMaxMemoryUsed());
            String comparisonResult = new FarmSolutionInvokator(attr.getSolution()).compare(
                    attr.getArgs(), attr.getExpectedResult(), resultObj);
            if (comparisonResult.length() == 0) {
                attr.setResultCode(SystemTestAttributes.RESULT_CORRECT);
            } else {
                attr.setResultCode(SystemTestAttributes.RESULT_INCORRECT);
                attr.setCheckAnswerResponse(comparisonResult);
                if (attr.isPractice()) {
                    StringBuffer returnBuf = new StringBuffer(200);
                    returnBuf.append("Failed system test #").append(attr.getTestCaseIndex()).append(" with args: ");
                    returnBuf.append(ContestConstants.makePretty(attr.getArgs()));
                    returnBuf.append("\n    EXPECTED: " + ContestConstants.makePretty(attr.getExpectedResult()));
                    returnBuf.append("\n    RECEIVED: " + ContestConstants.makePretty(result.getReturnValue()) + "\n");
                    returnBuf.append("\nAnswer checking result:\n");
                    returnBuf.append(comparisonResult);
                    returnBuf.append("\n");
                    attr.setMessage(returnBuf.toString());
                }
            }
        } catch (Exception e) {
            log.error("Exception while processing System Test", e);
            attr.setResultCode(SystemTestAttributes.RESULT_SYSTEM_FAILURE);
            attr.setMessage(e.getMessage());
        } finally {
            log.info("System Test Result Code: "+attr.getResultCode());
            log.info("System Test Message:\n" + attr.getMessage());
            log.info("System Test Result:\n" + attr.getResultValue());
        }
        return attr;
    }
}
