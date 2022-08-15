/*
 * Author: Michael Cervantes (emcee)
 * Date: Jul 4, 2002
 * Time: 3:41:27 AM
 *
 * This code is based on td's TCVerify utility
 */
package com.topcoder.server.AdminListener;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.common.ExpectedResult;
import com.topcoder.server.common.TestCase;
import com.topcoder.server.common.TestCaseArg;
import com.topcoder.server.contest.AnswerData;
import com.topcoder.server.contest.ContestData;
import com.topcoder.server.contest.ProblemData;
import com.topcoder.server.contest.QuestionData;
import com.topcoder.server.contest.RoundComponentData;
import com.topcoder.server.contest.RoundData;
import com.topcoder.server.contest.RoundProblemData;
import com.topcoder.server.contest.RoundRoomAssignment;
import com.topcoder.server.contest.RoundSegmentData;
import com.topcoder.server.ejb.AdminServices.AdminServices;

class RoundVerifier {

    protected AdminServices adminServices;

    RoundVerifier(AdminServices adminServices) {
        this.adminServices = adminServices;
    }

    String verify(int roundID) throws RemoteException, SQLException {
        Collection roundProblems;
        ContestData contest;
        RoundData round;
        RoundSegmentData roundSegments;
        Collection conflictingContests;
        Collection roundQuestions;
        Map answers = new HashMap();

        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        roundProblems = adminServices.getAssignedProblems(roundID);
        printComponentsAndTestCases(out, roundProblems, roundID);

        round = adminServices.getRound(roundID);
        contest = adminServices.getContest(round.getContest().getId());

        printRoundContest(out, contest);
        printRoundInformation(out, round);

        roundSegments = round.getSegments();
        printRoundSegments(out, roundSegments);

        conflictingContests = adminServices.getConflictingContests(contest.getId());
        printConflictingContests(out, conflictingContests);

        printAdminRoomInformation(out, round);

        roundQuestions = adminServices.getQuestions(roundID);
        for (Iterator it = roundQuestions.iterator(); it.hasNext();) {
            QuestionData questionData = (QuestionData) it.next();
            int questionID = questionData.getId();
            answers.put(new Integer(questionID), adminServices.getAnswers(questionID));
        }
        printRoundQuestions(out, roundQuestions, answers);
        return sw.toString();
    }

    protected void printComponentsAndTestCases(PrintWriter out, Collection roundProblems, int roundID) throws SQLException, RemoteException {
        StringBuffer output = new StringBuffer(400);
        List paramTypes;
        Collection roundTestCases = new ArrayList();

        for (Iterator it = roundProblems.iterator(); it.hasNext();) {
            RoundProblemData roundProblemData = (RoundProblemData) it.next();
            ProblemData problemData = roundProblemData.getProblemData();

            output.append("\n***********************************\n");
            output.append("Problem Id: " + problemData.getId());
            output.append("\nProblem Name: " + problemData.getName());
            output.append("\nProblem Type: " + problemData.getType());
            output.append("\nProblem Status: " + problemData.getStatus());
            output.append("\nComponents:");

            Collection components = adminServices.getRoundProblemComponents(roundID, problemData.getId(), roundProblemData.getDivision().getId());
            for (Iterator ci = components.iterator(); ci.hasNext();) {
                RoundComponentData roundComponentData = (RoundComponentData) ci.next();
                output.append("\n***************\n").
                        append("Component Id: " + roundComponentData.getComponentData().getId()).
                        append("\nClass Name: " + roundComponentData.getComponentData().getClassName()).
                        append("\nMethod Name: " + roundComponentData.getComponentData().getMethodName()).
                        append("\nResult Type: " + roundComponentData.getComponentData().getResultType());
                paramTypes = roundComponentData.getComponentData().getParamTypes();
                output.append("\nParam Types: ");
                for (int j = 0; j < paramTypes.size(); j++) {
                    output.append("\n\t" + paramTypes.get(j));
                }
                output.append("\nDivision: " + roundComponentData.getDivision());
                output.append("\nDifficulty Level: " + roundComponentData.getDifficulty());
                output.append("\nPoints: " + roundComponentData.getPointValue());
                output.append("\n***************\n");
                Collection testCases = adminServices.getTestCases(roundComponentData.getComponentData().getId());
                roundTestCases.add(testCases);

            }
        }
        out.println(output.toString());
        printRoundTestCases(out, roundTestCases);
    }

    /**
     * This method is used to display round test case characteristics in a
     * meaningful manner.
     *
     * @param roundTestCases - ArrayList of ArrayLists of TestCase
     */
    protected void printRoundTestCases(PrintWriter out, Collection roundTestCases) {
        TestCase testCaseAttributes;
        TestCaseArg testCaseArgAttributes;
        ExpectedResult expResultAttributes;
        ArrayList testCaseArrList;
        ArrayList testCaseArgs = null;
        StringBuffer output = new StringBuffer(400);

        for (Iterator it = roundTestCases.iterator(); it.hasNext();) {
            testCaseArrList = (ArrayList) it.next();

            output.append("\n***********************************\n");

            for (int j = 0; j < testCaseArrList.size(); j++) {
                testCaseAttributes = (TestCase) testCaseArrList.get(j);
                if (j == 0) {
                    output.append("Component Id: " + testCaseAttributes.getComponentId());
                }
                output.append("\n\tTest Case Id: " + testCaseAttributes.getTestCaseId());

                testCaseArgs = testCaseAttributes.getTestCaseArgs();
                output.append("\n\tTest Case Args: ");
                for (int k = 0; k < testCaseArgs.size(); k++) {
                    testCaseArgAttributes = (TestCaseArg) testCaseArgs.get(k);
                    output.append("\n\t\tArg Type: " + testCaseArgAttributes.getArgType());
                    output.append("\n\t\tArg Value: " + ContestConstants.makePretty(testCaseArgAttributes.getArgValue()));
                }

                expResultAttributes = testCaseAttributes.getExpectedResult();
                output.append("\n\tExpected Result Type: " + ContestConstants.makePretty(expResultAttributes.getResultType()));
                output.append("\n\tExpected Result Value: " + ContestConstants.makePretty(expResultAttributes.getResultValue()) + "\n");

            }
            output.append("\n***********************************\n");
        }

        out.println(output.toString());
    }

    protected void printRoundContest(PrintWriter out, ContestData contest) {
        StringBuffer output = new StringBuffer(400);

        output.append("\n******************************************************************\n").
                append("Contest Information \n");

        output.append("Contest Id: " + contest.getId()).
                append("\nContest Name: " + contest.getName()).
                append("\nStart Time: " + contest.getStartDate()).
                append("\nEnd Time: " + contest.getEndDate()).
                append("\nStatus: " + contest.getStatus());

        output.append("\n******************************************************************\n");

        out.println(output.toString());
    }

    /**
     * This method is used to display round characteristics in a
     * meaningful manner.
     *
     * @param round - VerifyRound object
     */
    protected void printRoundInformation(PrintWriter out, RoundData round) {
        StringBuffer output = new StringBuffer(400);
        String invitational;
        switch (round.getInvitationType()) {
        case ContestConstants.NORMAL_INVITATIONAL:
            invitational = "Normal";
            break;
        case ContestConstants.NEGATE_INVITATIONAL:
            invitational = "Negate";
            break;
        default:
            invitational = "No";
            break;
        }
        output.append("\n******************************************************************\n").
                append("Round Information \n");

        output.append("Round Id: " + round.getId()).
                append("\nRound Name: " + round.getName()).
                append("\nRound Type: " + round.getType()).
                append("\nInvitational: " + invitational).
                append("\nRegistration Limit: " + round.getRegistrationLimit()).
                append("\nStatus: " + round.getStatus());

        output.append("\n******************************************************************\n");
        
        // the algorithm strings below should be in ContestConstants like the 
        // PHASE_NAMES ones are. That would avoid duplication. 
        // Do this at integration time.
        RoundRoomAssignment rra = round.getRoomAssignment();
        String type = 
            rra.getType() == ContestConstants.RANDOM_SEEDING ? "Random" :
            rra.getType() == ContestConstants.IRON_MAN_SEEDING ? " Iron Man" :
            rra.getType() == ContestConstants.NCAA_STYLE ? " NCAA Style" :
            rra.getType() == ContestConstants.EMPTY_ROOM_SEEDING ? "Empty Room" :
            rra.getType() == ContestConstants.WEEKEST_LINK_SEEDING ? "Weakest Link" :
            "BAD BAD BAD - no known seeding";
        output.append("Round Room Assignment information: " + round.getId()).
                append("\nCoders per room: " + rra.getCodersPerRoom()).
                append("\nType: " + type).
                append("\nIs by Division: " + (rra.isByDivision() ? "Yes" : "No")).
                append("\nIs by Region: " + (rra.isByRegion() ? "Yes" : "No")).
                append("\nIs Final: " + (rra.isFinal() ? "Yes" : "No")).
                append("\np: " + rra.getP());

        output.append("\n******************************************************************\n");
        out.println(output.toString());
    }

    /**
     * This method is used to display round segment characteristics in a
     * meaningful manner.
     *
     * @param roundSegments - ArrayList of RoundSegment
     * ademich
     */
    protected void printRoundSegments(PrintWriter out, RoundSegmentData roundSegments) {
        StringBuffer output = new StringBuffer(400);

        output.append("\n******************************************************************\n").
                append("Active Round Segments\n");


        output.append("\n***********************************\n").
                append("\nRound Segment: Registration").
                append("\nStart Time: " + roundSegments.getRegistrationStart()).
                append("\nLength: " + roundSegments.getRegistrationLength()).
                append("\nStatus: " + roundSegments.getRegistrationStatus()).
                append("\n***********************************\n");

        output.append("\n***********************************\n").
                append("\nRound Segment: Coding Phase").
                append("\nStart Time: " + roundSegments.getCodingStart()).
                append("\nLength: " + roundSegments.getCodingLength()).
                append("\nStatus: " + roundSegments.getCodingStatus()).
                append("\n***********************************\n");

        output.append("\n***********************************\n").
                append("\nRound Segment: Intermission Phase").
                append("\nLength: " + roundSegments.getIntermissionLength()).
                append("\nStatus: " + roundSegments.getIntermissionStatus()).
                append("\n***********************************\n");

        output.append("\n***********************************\n").
                append("\nRound Segment: Challenge Phase").
                append("\nLength: " + roundSegments.getChallengeLength()).
                append("\nStatus: " + roundSegments.getChallengeStatus()).
                append("\n***********************************\n");

        output.append("\n***********************************\n").
                append("\nRound Segment: System Test Phase").
                append("\nStatus: " + roundSegments.getSystemTestStatus()).
                append("\n***********************************\n");

        output.append("\n******************************************************************\n");

        out.println(output.toString());
    }

    /**
     * This method is used to display conflicting contest characteristics in a
     * meaningful manner.
     *
     * @param contests - ArrayList of VerifyContest
     */
    protected void printConflictingContests(PrintWriter out, Collection contests) {
        ContestData contest;
        StringBuffer output = new StringBuffer(400);

        output.append("\n******************************************************************\n").
                append("Conflicting Contests\n");

        if (contests.size() > 0) {
            output.append("\n***********************************\n").
                    append("**** WARNING THERE ARE CONFLICTING CONTESTS, VERY VERY BAD! ****").
                    append("\n***********************************\n");

            for (Iterator it = contests.iterator(); it.hasNext();) {
                contest = (ContestData) it.next();
                output.append("\n***********************************\n");
                output.append("Contest Id: " + contest.getId()).
                        append("\nContest Name: " + contest.getName()).
                        append("\nStart Time: " + contest.getStartDate()).
                        append("\nEnd Time: " + contest.getEndDate()).
                        append("\nStatus: " + contest.getStatus());
            }

        } else {
            output.append("\n***********************************\n").
                    append("There are no conflicting contests").
                    append("\n***********************************\n");
        }


        output.append("\n******************************************************************\n");


        out.println(output.toString());

    }

    protected void printAdminRoomInformation(PrintWriter out, RoundData round) {
        StringBuffer output = new StringBuffer(400);
        output.append("\n******************************************************************\n").
                append("Admin Room Information\n");

        if (round.getAdminRoomID() == 0) {
            output.append("\n***********************************\n").
                    append("*** WARNING - No Admin Room Created ***").
                    append("\n***********************************\n");
        } else {
            output.append("\n***********************************\n");
            output.append("Room Id: " + round.getAdminRoomID()).
                    append("\nRoom Name: " + round.getAdminRoomName()).
                    append("\n***********************************\n");
        }

        output.append("\n******************************************************************\n");

        out.println(output.toString());
    }

    protected void printRoundQuestions(PrintWriter out, Collection roundQuestions, Map answers) {
        StringBuffer output = new StringBuffer(400);
        QuestionData questionAttr;
        AnswerData answerAttr;

        output.append("\n******************************************************************\n").
                append("Round Questions");

        if (roundQuestions.size() == 0) {
            output.append("\n***********************************\n").
                    append("There are no questions for this round").
                    append("\n***********************************\n");
        } else {
            for (Iterator it = roundQuestions.iterator(); it.hasNext();) {
                questionAttr = (QuestionData) it.next();

                output.append("\n\nQuestion Id: " + questionAttr.getId());
                output.append("\nQuestion Text: " + questionAttr.getText());
                output.append("\nKeyword: " + questionAttr.getKeyword());
                output.append("\nQuestion Type: " + questionAttr.getType());
                output.append("\nQuestion Style: " + questionAttr.getStyle());

                Collection questionAnswers = (Collection) answers.get(new Integer(questionAttr.getId()));

                int j = 0;
                for (Iterator it2 = questionAnswers.iterator(); it2.hasNext(); j++) {
                    answerAttr = (AnswerData) it2.next();
                    if (questionAttr.getType().getId() == 2) {
                        output.append("\n\t " + j + ":" + answerAttr.getText() + "\t correct: " + answerAttr.isCorrect());
                    } else {
                        output.append("\n\t " + j + ":" + answerAttr.getText());
                    }
                }
            }

            output.append("\n******************************************************************\n");
        }

        out.println(output.toString());
    }
}
