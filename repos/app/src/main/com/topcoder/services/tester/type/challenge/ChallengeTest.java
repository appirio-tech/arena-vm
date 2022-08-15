/*
* Copyright (C) 2006 - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.services.tester.type.challenge;

import java.io.File;

import com.topcoder.server.common.ChallengeAttributes;
import com.topcoder.server.common.ServerContestConstants;
import com.topcoder.services.tester.BaseTester;
import com.topcoder.services.tester.TesterFactory;
import com.topcoder.services.tester.common.TestResult;
import com.topcoder.services.tester.invoke.FarmSolutionInvokator;
import com.topcoder.services.tester.invoke.SolutionInvocator;
import com.topcoder.shared.util.logging.Logger;

/**
 * Process a challenge. (I.E. execute the defendants compiled code, with
 * the challengers supplied arguments)
 *
 * <p>
 * Changes in version 1.1 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *     <li>Updated {@link #process(ChallengeAttributes)} to handle comparison result.
 * </ol>
 * </p>
 *
 * @author Diego Belfer (Mural), gevak
 * @version 1.1
 */
public class ChallengeTest {
    private Logger log = Logger.getLogger(ChallengeTest.class);
    private File workFolder;

    public ChallengeTest() {
    }

    public ChallengeTest(File workFolder) {
        this.workFolder= workFolder;
    }

    /**
     * Processes a challenge.
     *
     * @param chal Challenge attributes.
     * @return Resulting challenge attributes.
     */
    public ChallengeAttributes process(ChallengeAttributes chal) {
        log.info("PROCESSING CHALLENGE: Coder "
                + chal.getChallengerId() + " is challenging component "
                + chal.getComponent().getComponentID() + " of coder "
                + chal.getDefendantId());
        log.info("Challenge Args: " + chal.getArgs());

        try {
            chal.clearResult();

            BaseTester tester = TesterFactory.getTester(chal.getLanguage());

            String className = chal.getComponent().getClassName().trim();

            TestResult result = tester.test(chal, workFolder);

            //Challenge uses validated args on it is returned.
            chal.setValidatedArgs(result.getValidatedArgs());

            //We need the expected result even if the submission failed or timeout
            Object expectedResult = chal.getExpectedResult();
            //FIXME we should verify the args are valid before running on the solution
            if (expectedResult == null) {
                expectedResult = new FarmSolutionInvokator(chal.getSolution()).callSolutionMethod(className, SolutionInvocator.SOLVE, result.getValidatedArgs());
                chal.setExpectedResult(expectedResult);
            }

            if (result.isTimeout()) {
                chal.setResultCode(ChallengeAttributes.RESULT_TIMEOUT);
                chal.setResultValue(result.getMessage());
                chal.setMessage(result.getMessage());
                return chal;
            }

            if (!result.isSuccess()) {
                if (result.isSystemFailure() || result.isInvalidArgs()) {
                    chal.setResultCode(ChallengeAttributes.RESULT_SYSTEM_FAILURE);
                    chal.setResultValue("System Error!");
                    chal.setMessage("The system failed to process your request: " + result.getMessage() + ".");
                } else {
                    chal.setResultCode(ChallengeAttributes.RESULT_EXCEPTION);
                    chal.setMessage(result.getMessage());
                    chal.setResultValue(result.getMessage());
                }
                return chal;
            }
            if (expectedResult == null) {
                log.error("Writer solution returned null as expected result!!!. Reporting System Failure.");
                chal.setResultCode(ChallengeAttributes.RESULT_SYSTEM_FAILURE);
                chal.setResultValue("System Error!");
                chal.setMessage("The system failed to process your request: Expected result is NULL. Please notify admins.");
                return chal;
            }
            Object resultObj = result.getReturnValue();
            chal.setResultValue(ServerContestConstants.makePretty(resultObj));

            String comparisonResult = new FarmSolutionInvokator(chal.getSolution()).compare(
                    chal.getArgs(), expectedResult, resultObj);
            if (comparisonResult.length() == 0) {
                // Answer is correct - challenge failed
                chal.setResultCode(ChallengeAttributes.RESULT_CORRECT);
                // Make sure they are of the same class
                if (!((expectedResult.getClass().getName()).equals(resultObj.getClass().getName()))) {
                    chal.setResultCode(ChallengeAttributes.RESULT_EXCEPTION);
                    chal.setResultValue("Returned object is of incorrect type!");
                }
            } else {
                chal.setResultCode(ChallengeAttributes.RESULT_INCORRECT);
                chal.setMessage(comparisonResult);
                chal.setCheckAnswerResponse(comparisonResult);
            }
        } catch (Exception e) {
            log.error("Exception processing challenge", e);
            chal.setResultCode(ChallengeAttributes.RESULT_SYSTEM_FAILURE);                   // only notify the challenger
            chal.setMessage(e.getMessage());
        } finally {
            log.info("Challenge Result Code: " + chal.getResultCode());
            log.info("Challenge Return:\n" + chal.getResultValue());
            log.info("Challenge Message:\n" + chal.getMessage());
        }
        return chal;
    }
}
