package com.topcoder.server.services;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.data.PracticeTestResultData;
import com.topcoder.server.common.ChallengeAttributes;
import com.topcoder.server.common.Location;
import com.topcoder.server.common.Submission;
import com.topcoder.server.common.SystemTestAttributes;
import com.topcoder.server.common.UserTestAttributes;
import com.topcoder.server.farm.tester.srm.SRMTesterInvoker;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.util.logging.Logger;

/**
 * SRMTesterInvoker.SRMTesterHandler implementation containing current logic for
 * handling SRM tests results.
 * <p>
 * 
 * Implementation was extracted from many classes:
 * <li>com.topcoder.services.message.handler.TestMessageHandler
 * <li>com.topcoder.services.tester.type.system.(Language)SystemTest
 * <li>com.topcoder.services.tester.type.user.(Language)UserTest
 * <li>com.topcoder.services.tester.type.challenge.(Language)ChallengeTest
 * 
 * <p>
 * Changes in version 1.1 (PoC Assembly - TopCoder Competition Engine - Support
 * Custom Output Checker):
 * <ol>
 * <li>Updated {@link #handlePracticeSystemTestResult(SystemTestAttributes)} to
 * handle check answer response.</li>
 * <li>Updated {@link #handleRealSystemTestResult(SystemTestAttributes)} to
 * handle check answer response.</li>
 * </ol>
 * </p>
 * 
 * @author gevak
 * @version 1.1
 */
public class SRMTestHandler implements SRMTesterInvoker.SRMTesterHandler {
	private final Logger log = Logger.getLogger(SRMTestHandler.class);

	public void reportUserTestResult(UserTestAttributes userTest) {
		log.debug("Reporting SRM user test result: "+ userTest.getCoderId());
		EventService.sendTestResults(userTest.getCoderId(), ServicesConstants.USER_TEST_ACTION, userTest,
				userTest.getSubmitTime());
	}

	public void reportChallengeTestResult(ChallengeAttributes chal) {
		try {
			if (chal.isTimeOut() && !chal.isExclusiveExecution()) {
				if (log.isDebugEnabled()) {
					log.debug("Rescheduling challenge test due to timeout: " + chal);
				}
				chal.setExclusiveExecution(true);
				SRMTestScheduler.srmTester.challengeTest(chal);
			} else {
				if (!chal.isSystemFailure()) {
					String error = TestService.recordChallengeResults(chal);
					if (!error.equals("")) // It means someone has beaten them
											// to it
					{
						chal.setResultCode(ChallengeAttributes.RESULT_SYSTEM_FAILURE); // to
																						// prevent
																						// from
																						// being
																						// broadcast
																						// to
																						// everyone.
						chal.setMessage(error);
					} else {
						Location location = chal.getLocation();
						if (chal.isSuccesfulChallenge()
								&& !ContestConstants.isPracticeRoomType(CoreServices.getRoom(location.getRoomID())
										.getType()) && SRMTestScheduler.isAutoSystemTestsEnabled()) {
							SRMTestScheduler.cancelAndRemoveSystemTestsOnSubmission(chal.getDefendantId(),
									chal.getLocation(), chal.getComponentId());
							SRMTestScheduler.addChallengeAsSystemTestCase(chal);
						}
					}
				}
				EventService.sendTestResults(chal.getChallengerId(), ServicesConstants.CHALLENGE_TEST_ACTION, chal,
						chal.getSubmitTime());
			}
		} catch (Exception e) {
			log.error("Exception processing Challenge Response", e);
		}
	}

	public void reportSystemTestResult(SystemTestAttributes attr) {
		try {
			if (attr.isTimeOut() && !attr.isExclusiveExecution()) {
				if (log.isDebugEnabled()) {
					log.debug("Rescheduling system test due to timeout: " + attr);
				}
				attr.setExclusiveExecution(true);
				SRMTestScheduler.srmTester.systemTest(attr);
			} else {
				int roundID = attr.getSubmission().getLocation().getRoundID();
				boolean mustStopSystemTestsOnFailure = CoreServices.getContestRound(roundID).getRoundProperties()
						.mustStopSystemTestsOnFailure();
				if (!attr.isCorrect()) {
					if (mustStopSystemTestsOnFailure) {
						log.info("Aborting system tests due to test " + attr);
						Submission submission = attr.getSubmission();
						SRMTestScheduler.cancelSystemTestsOnSubmission(submission.getCoderID(), submission
								.getLocation(), submission.getComponentID(), attr.isPractice(),
								new Integer(attr.getSystemTestVersion()));
					}
				}
				if (attr.isPractice()) {
					handlePracticeSystemTestResult(attr);
				} else {
					handleRealSystemTestResult(attr);
				}
			}
		} catch (Exception e) {
			log.error("Could not handle System Test Result: ", e);
			log.error("SystemTestAttributes : " + attr);
		}
	}

	/**
	 * Handles practice system test result.
	 * 
	 * @param attr
	 *            System test attributes.
	 */
	private void handlePracticeSystemTestResult(SystemTestAttributes attr) {
		Submission submission = attr.getSubmission();
		PracticeTestResultData data = new PracticeTestResultData(submission.getRoundID(), submission.getLocation()
				.getRoomID(), submission.getComponentID(), attr.getTestCaseIndex(), attr.isCorrect(),
				attr.getMessage(), attr.getArgs(), attr.getExpectedResult(), attr.getResultValue(), attr.getExecTime(),
				attr.getMaxMemoryUsed(), attr.getCheckAnswerResponse());

		EventService.sendTestResults(attr.getSubmission().getCoderID(), ServicesConstants.PRACTICE_TEST_ACTION, data,
				-1);
	}

	/**
	 * Handles real system test result.
	 * 
	 * @param attr
	 *            System test attributes.
	 */
	private void handleRealSystemTestResult(SystemTestAttributes attr) {
		Submission submission = attr.getSubmission();
		Location location = submission.getLocation();
		int failure = 0;
		if (!attr.isCorrect()) {
			if (attr.isIncorrect()) {
				failure = ServicesConstants.FAILURE_INCORRECT_RESULT;
			} else {
				if (attr.isSystemFailure()) {
					failure = ServicesConstants.FAILURE_SYSTEM_ERROR;
				} else if (attr.isException()) {
					failure = ServicesConstants.FAILURE_EXCEPTION;
				} else {
					failure = ServicesConstants.FAILURE_TIMEOUT;
				}
			}
		}
		SRMTestScheduler.recordSystemTestResult(location.getContestID(), submission.getCoderID(),
				submission.getRoundID(), attr.getComponent().getComponentID(), attr.getTestCaseId(),
				attr.getResultValue(), attr.isPassed(), attr.getExecTime(), failure, attr.getSystemTestVersion(),
				attr.getCheckAnswerResponse());
	}
}
