package com.topcoder.utilities;

import java.io.*;
import java.util.*;
//import java.net.*;
import java.sql.*;

//import javax.naming.*;
//import javax.ejb.*;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.common.ApplicationServer;
import com.topcoder.shared.util.DBMS;
import com.topcoder.server.common.*;


public class TCVerify {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("This program is used to help verify that a round is setup properly.");
            System.out.println("Usage: java com.topcoder.utilities.TCVerify <round_id> ");
            return;
        }

        int round_id = Integer.parseInt(args[0]);

        ArrayList roundProblems;
        ArrayList problem_ids = new ArrayList(3);
        ArrayList roundTestCases;
        VerifyContest contest;
        VerifyRound round;
        ArrayList roundSegments;
        ArrayList conflictingContests;
        ArrayList rooms;
        VerifyProblem probAttributes;
        ArrayList roundQuestions;
        long termsTime;

        try {

            roundProblems = getRoundProblems(round_id);
            printRoundProblems(roundProblems);

            for (int i = 0; i < roundProblems.size(); i++) {
                probAttributes = (VerifyProblem) roundProblems.get(i);
                problem_ids.add(new Integer(probAttributes.getProblemId()));
            }
            roundTestCases = getRoundTestCases(problem_ids);
            printRoundTestCases(roundTestCases);

            contest = getRoundContest(round_id);
            printRoundContest(contest);

            round = getRoundInformation(round_id);
            printRoundInformation(round);

            roundSegments = getRoundSegments(round_id);
            printRoundSegments(roundSegments);

            conflictingContests = getConflictingContests(contest.getContestId());
            printConflictingContests(conflictingContests);

            rooms = getAdminRoomInformation(round_id);
            printAdminRoomInformation(rooms);

            termsTime = getTermsModifyDate();
            printTermsModifyDate(termsTime);

            roundQuestions = getRoundQuestions(round_id);
            printRoundQuestions(roundQuestions);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*****************************************************************************************
     * Retrieves all round problem characteristics from the
     * ROUND_PROBLEM, PROBLEM, DIFFICULTY and DATA_TYPE tables for a round
     *
     * @param - round_id - int that uniquely identifies a Round
     * @exception RemoteException
     * @return ArrayList of VerifyProblem attributes
     *****************************************************************************************
     **/
    public static ArrayList getRoundProblems(int round_id) throws Exception {
        System.out.println("In getRoundProblemsLocal");

        ArrayList roundProblems = new ArrayList(6);

        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        VerifyProblem problem;
        String problem_text;
        Object blobObject = new Object();
        ArrayList paramTypes;

        StringBuffer txtGetRoundProblems = new StringBuffer();
        txtGetRoundProblems.append(" SELECT rr.problem_id, rr.submit_order, rr.division_id, ").
                append("        rr.difficulty_id, rr.points, p.method_name, p.class_name, ").
                append("        dt.data_type_desc, p.status, d.difficulty_level, ").
                append("        p.problem_text, p.param_types, p.result_type_id ").
                append(" FROM   round_problem rr, problem p, difficulty d, data_type dt ").
                append(" WHERE  rr.round_id = ? AND ").
                append("        rr.problem_id = p.problem_id AND ").
                append("        rr.difficulty_id = d.difficulty_id AND ").
                append("        p.result_type_id = dt.data_type_id ").
                append(" ORDER  BY rr.division_id, rr.difficulty_id ");


        try {
            conn = DBMS.getDirectConnection();

            ps = conn.prepareStatement(txtGetRoundProblems.toString());
            ps.setInt(1, round_id);

            rs = ps.executeQuery();

            while (rs.next()) {
                problem = new VerifyProblem();
                problem.setProblemId(rs.getInt(1));
                problem.setSubmitOrder(rs.getInt(2));
                problem.setDivisionId(rs.getInt(3));
                problem.setDifficultyLevelId(rs.getInt(4));
                problem.setPoints(rs.getFloat(5));
                problem.setMethodName(rs.getString(6));
                problem.setClassName(rs.getString(7));
                problem.setResultType(rs.getString(8));
                problem.setStatus(rs.getInt(9));
                problem.setDifficultyLevel(rs.getString(10));
                problem.setResultTypeId(rs.getInt(13));
                try {
                    problem_text = DBMS.getTextString(rs, 11);
                } catch (Exception tce) {
                    problem_text = "";
                }

                try {
                    blobObject = DBMS.getBlobObject(rs, 12);
                } catch (Exception tce) {
                }

                if (blobObject instanceof ArrayList) {
                    paramTypes = (ArrayList) blobObject;
                } else {
                    paramTypes = new ArrayList();
                }

                problem.setParamTypes(paramTypes);

                roundProblems.add(problem);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception ignore) {
            }
        }

        return roundProblems;

    }

    /**
     * This method is used to display round problem characteristics in a
     * meaningful manner.
     *
     * @param roundProblems - ArrayList of VerifyProblemAttruibutes
     * @author ademich
     */
    public static void printRoundProblems(ArrayList roundProblems) throws Exception {

        VerifyProblem probAttributes;
        ArrayList param_types = null;
        String paramType = "";
        StringBuffer output = new StringBuffer(400);

        for (int i = 0; i < roundProblems.size(); i++) {
            probAttributes = (VerifyProblem) roundProblems.get(i);

            output.append("\n***********************************\n").
                    append("Problem Id: " + probAttributes.getProblemId()).
                    append("\nClass Name: " + probAttributes.getClassName()).
                    append("\nMethod Name: " + probAttributes.getMethodName()).
                    append("\nResult Type: " + probAttributes.getResultType());
            param_types = probAttributes.getParamTypes();
            output.append("\nParam Types: ");
            for (int j = 0; j < param_types.size(); j++) {
                output.append("\n\t" + param_types.get(j));
            }
            output.append("\nDivision: " + probAttributes.getDivisionId());
            output.append("\nDifficulty Level: " + probAttributes.getDifficultyLevel());
            output.append("\nPoints: " + probAttributes.getPoints());
            output.append("\n***********************************\n");
        }


        System.out.println(output.toString());
    }

    /**
     * This method is used to retrieve system test cases for a given set of problems. The
     * test case characteristics will be retrieved from the SYSTEM_TEST_CASE.
     *
     * @param problem_ids - ArrayList of ints (problem_ids)
     * @return roundTestCases - ArrayList of ArrayLists of TestCase.
     * @author ademich
     */

    public static ArrayList getRoundTestCases(ArrayList problem_ids) throws Exception {
        ArrayList roundTestCases = new ArrayList(3);

        ArrayList testCaseArrList;
        for (int i = 0; i < problem_ids.size(); i++) {
            //testCaseArrList = pServices.getTestCases( ((Integer)problem_ids.get(i)).intValue() );
            testCaseArrList = getTestCases(((Integer) problem_ids.get(i)).intValue());
            roundTestCases.add(testCaseArrList);
        }

        return roundTestCases;

    }


    /**
     * This method is used to display round test case characteristics in a
     * meaningful manner.
     *
     * @param roundTestCases - ArrayList of ArrayLists of TestCase
     * @author ademich
     */

    public static void printRoundTestCases(ArrayList roundTestCases) throws Exception {

        TestCase testCaseAttributes;
        TestCaseArg testCaseArgAttributes;
        ExpectedResult expResultAttributes;
        ArrayList testCaseArrList;
        ArrayList testCaseArgs = null;
        String paramType = "";
        StringBuffer output = new StringBuffer(400);

        for (int i = 0; i < roundTestCases.size(); i++) {
            testCaseArrList = (ArrayList) roundTestCases.get(i);

            output.append("\n***********************************\n");

            for (int j = 0; j < testCaseArrList.size(); j++) {
                testCaseAttributes = (TestCase) testCaseArrList.get(j);
                if (j == 0) {
                    output.append("Problem Id: " + testCaseAttributes.getComponentId());
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
                output.append("\n\tExpexted Result Type: " + ContestConstants.makePretty(expResultAttributes.getResultType()));
                output.append("\n\tExpexted Result Value: " + ContestConstants.makePretty(expResultAttributes.getResultValue()) + "\n");

            }
            output.append("\n***********************************\n");
        }


        System.out.println(output.toString());

    }

    /**
     * This method is used to display contest characteristics in a
     * meaningful manner.
     *
     * @param contest - VerifyContest object
     * @author ademich
     */

    public static void printRoundContest(VerifyContest contest) throws Exception {


        StringBuffer output = new StringBuffer(400);

        output.append("\n******************************************************************\n").
                append("Contest Information \n");

        output.append("Contest Id: " + contest.getContestId()).
                append("\nContest Name: " + contest.getContestName()).
                append("\nStart Time: " + contest.getStartDate()).
                append("\nEnd Time: " + contest.getEndDate()).
                append("\nStatus: " + contest.getStatus());

        output.append("\n******************************************************************\n");


        System.out.println(output.toString());

    }


    /**
     * This method is used to display round characteristics in a
     * meaningful manner.
     *
     * @param round - VerifyRound object
     * @author ademich
     */
    public static void printRoundInformation(VerifyRound round) throws Exception {


        StringBuffer output = new StringBuffer(400);
        String invitational;
        if (round.getInvitational() == 0) {
            invitational = "No";
        } else if (round.getInvitational() == 1) {
            invitational = "Yes";
        } else {
            invitational = "UNDEFINED";
        }

        output.append("\n******************************************************************\n").
                append("Round Information \n");

        output.append("Round Id: " + round.getRoundId()).
                append("\nRound Name: " + round.getRoundName()).
                append("\nRound Type: " + round.getRoundTypeDesc()).
                append("\nInvitational: " + invitational).
                append("\nRegistration Limit: " + round.getRegistrationLimit()).
                append("\nStatus: " + round.getStatus());

        output.append("\n******************************************************************\n");


        System.out.println(output.toString());

    }

    /**
     * This method is used to display round segment characteristics in a
     * meaningful manner.
     *
     * @param roundSegments - ArrayList of RoundSegment
     * @author ademich
     */

    public static void printRoundSegments(ArrayList roundSegments) throws Exception {

        RoundSegment roundSegmentAttributes;
        ArrayList param_types = null;
        String paramType = "";
        StringBuffer output = new StringBuffer(400);

        output.append("\n******************************************************************\n").
                append("Active Round Segments\n");

        if (roundSegments.size() < 1) {
            output.append("\n***********************************\n").
                    append("There are no scheduled Round Segments").
                    append("\n***********************************\n");
        }

        for (int i = 0; i < roundSegments.size(); i++) {
            roundSegmentAttributes = (RoundSegment) roundSegments.get(i);

            output.append("\n***********************************\n");
            output.append("Round Id: " + roundSegmentAttributes.getRoundId()).
                    append("\nRound Segment: " + roundSegmentAttributes.getSegmentDesc()).
                    append("\nStart Time: " + roundSegmentAttributes.getStart()).
                    append("\nEnd Time: " + roundSegmentAttributes.getEnd()).
                    append("\n***********************************\n");
        }

        output.append("\n******************************************************************\n");


        System.out.println(output.toString());

    }


    /**
     * This method is used to display conflicting contest characteristics in a
     * meaningful manner.
     *
     * @param contests - ArrayList of VerifyContest
     * @author ademich
     */

    public static void printConflictingContests(ArrayList contests) throws Exception {

        VerifyContest contest;
        StringBuffer output = new StringBuffer(400);

        output.append("\n******************************************************************\n").
                append("Conflicting Contests\n");

        if (contests.size() > 0) {
            output.append("\n***********************************\n").
                    append("**** WARNING THERE ARE CONFLICTING CONTESTS, VERY VERY BAD! ****").
                    append("\n***********************************\n");

            for (int i = 0; i < contests.size(); i++) {
                contest = (VerifyContest) contests.get(i);

                output.append("\n***********************************\n");
                output.append("Contest Id: " + contest.getContestId()).
                        append("\nContest Name: " + contest.getContestName()).
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


        System.out.println(output.toString());

    }

    /**
     * This method is used to display admin room characteristics in a
     * meaningful manner.
     *
     * @param round - VerifyRoom object
     * @author ademich
     */
    public static void printAdminRoomInformation(ArrayList rooms) throws Exception {


        StringBuffer output = new StringBuffer(400);
        VerifyRoom room;

        output.append("\n******************************************************************\n").
                append("Admin Room Information\n");

        if (rooms.size() < 1) {
            output.append("\n***********************************\n").
                    append("*** WARNING - No Admin Room Created ***").
                    append("\n***********************************\n");
        }

        for (int i = 0; i < rooms.size(); i++) {
            room = (VerifyRoom) rooms.get(i);

            output.append("\n***********************************\n");
            output.append("Room Id: " + room.getRoomId()).
                    append("\nRound Id: " + room.getRoundId()).
                    append("\nRoom Name: " + room.getRoomName()).
                    append("\n***********************************\n");
        }

        output.append("\n******************************************************************\n");


        System.out.println(output.toString());

    }

    /**
     * This method is used to retrieve the last modification time of
     * the contest terms file, terms.txt.
     *
     * @return termsTime - long value representing the last modify time in milliseconds.
     * @author ademich
     */
    public static long getTermsModifyDate() throws Exception {
        File terms;
        long termsTime;

        try {
            terms = new File(ApplicationServer.IAGREE);
            termsTime = terms.lastModified();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error retrieving bean handles: " + e.getMessage());
        }

        return termsTime;

    }

    /**
     * This method is used to display a time and check if the time is
     * equal to the current day.
     * If BufferedOutputStream is null then the results are outputted to standard out.
     *
     * @param roundProblems - ArrayList of ProblemAttruibutes
     * @author ademich
     */
    public static void printTermsModifyDate(long termsTime) throws Exception {

        GregorianCalendar toDay = new GregorianCalendar();
        GregorianCalendar modifyDate = new GregorianCalendar();
        java.util.Date termsModifyDate = new java.util.Date(termsTime);
        modifyDate.setTime(termsModifyDate);
        StringBuffer output = new StringBuffer(400);

        output.append("\n***********************************\n");

        if (toDay.get(Calendar.DAY_OF_YEAR) != modifyDate.get(Calendar.DAY_OF_YEAR)) {
            output.append("*** WARNING TERMS MODIFY DAY IS NOT TODAY!! ***\n");
        }

        output.append("Terms last modify time: " + termsModifyDate);

        output.append("\n***********************************\n");

        System.out.println(output.toString());

    }


    /**
     * This method is used to display survey characteristics in a
     * meaningful manner.
     *
     * @param survey - Survey
     * @author ademich
     */
    public static void printRoundQuestions(ArrayList roundQuestions) throws Exception {

        StringBuffer output = new StringBuffer(400);
        Question questionAttr;
        Answer answerAttr;

        output.append("\n******************************************************************\n").
                append("Round Questions");

        if (roundQuestions.size() == 0) {
            output.append("\n***********************************\n").
                    append("There are no questions for this round").
                    append("\n***********************************\n");
        } else {
            for (int i = 0; i < roundQuestions.size(); i++) {
                questionAttr = (Question) roundQuestions.get(i);
                output.append("\n\nQuestion Id: " + questionAttr.getQuestionId());
                output.append("\nQuestion Text: " + questionAttr.getQuestionText());
                output.append("\nKeyword: " + questionAttr.getKeyword());
                output.append("\nQuestion Type: " + questionAttr.getQuestionTypeDesc());
                output.append("\nQuestion Style: " + questionAttr.getQuestionStyleDesc());

                ArrayList questionAnswers = questionAttr.getAnswers();

                for (int j = 0; j < questionAnswers.size(); j++) {
                    answerAttr = (Answer) questionAnswers.get(j);
                    if (questionAttr.getQuestionTypeId() == 2) {
                        output.append("\n\t " + j + ":" + answerAttr.getAnswerText() + "\t correct: " + answerAttr.getCorrect());
                    } else {
                        output.append("\n\t " + j + ":" + answerAttr.getAnswerText());
                    }
                }
            }

            output.append("\n******************************************************************\n");
        }

        System.out.println(output.toString());

    }


    /**************************************************************************************/

    public static ArrayList getTestCases(int problem_id) {

        ArrayList testCases = new ArrayList();
        TestCase testCaseAttr = null;
        ExpectedResult expectedResultAttr = null;

        ArrayList testCaseArgs = null;

        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        Object expBlobObject = null;
        int test_case_id = 0;
        int testOrder = 0;
        ArrayList blobObject = null;

        StringBuffer txtGetTestCases = new StringBuffer(150);
        txtGetTestCases.append(" SELECT problem_id, test_case_id, args, expected_result ").
                append(" FROM system_test_case ").
                append(" WHERE problem_id = ? ").
                append(" ORDER BY test_case_id ");


        try {
            conn = DBMS.getDirectConnection();

            ps = conn.prepareStatement(txtGetTestCases.toString());
            ps.setInt(1, problem_id);

            rs = ps.executeQuery();

            while (rs.next()) {
                testCaseAttr = new TestCase();
                testCaseAttr.setComponentId(rs.getInt(1));
                test_case_id = rs.getInt(2);
                testCaseAttr.setTestCaseId(test_case_id);
                testCaseAttr.setTestOrder(testOrder);

                try {
                    blobObject = (ArrayList) DBMS.getBlobObject(rs, 3);
                    expBlobObject = DBMS.getBlobObject(rs, 4);
                } catch (Exception tce) {
                }

                testCaseArgs = buildTestCaseArgs(blobObject, problem_id, test_case_id);

                testCaseAttr.setTestCaseArgs(testCaseArgs);

                expectedResultAttr = buildExpectedResult(expBlobObject, problem_id, test_case_id);

                testCaseAttr.setExpectedResult(expectedResultAttr);

                testCases.add(testCaseAttr);
                testOrder++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ignore) {
            }
        }

        return testCases;

    }


    /*****************************************************************************************/

    private static ArrayList buildTestCaseArgs(ArrayList blobObject, int problem_id, int test_case_id) {
        TestCaseArg testCaseArgAttr = null;
        ArrayList argValues = null;
        ArrayList testCaseArgs = new ArrayList();
        String argType = "";
        String arrayType = "";

        for (int i = 0; i < blobObject.size(); i++) {
            argValues = new ArrayList();
            testCaseArgAttr = new TestCaseArg();
            testCaseArgAttr.setProblemId(problem_id);
            testCaseArgAttr.setTestCaseId(test_case_id);
            testCaseArgAttr.setArgPosition(i);

            if (blobObject.get(i) instanceof Integer) {
                testCaseArgAttr.setArgType("Integer");
                argValues.add((Integer) blobObject.get(i));
                testCaseArgAttr.setArgValue(argValues);
            } else if (blobObject.get(i) instanceof Double) {
                testCaseArgAttr.setArgType("Double");
                argValues.add((Double) blobObject.get(i));
                testCaseArgAttr.setArgValue(argValues);
            } else if (blobObject.get(i) instanceof String) {
                testCaseArgAttr.setArgType("String");
                argValues.add((String) blobObject.get(i));
                testCaseArgAttr.setArgValue(argValues);
            } else if (blobObject.get(i) instanceof Float) {
                testCaseArgAttr.setArgType("Float");
                argValues.add((Float) blobObject.get(i));
                testCaseArgAttr.setArgValue(argValues);
            } else if (blobObject.get(i) instanceof Boolean) {
                testCaseArgAttr.setArgType("Boolean");
                argValues.add((Boolean) blobObject.get(i));
                testCaseArgAttr.setArgValue(argValues);
            } else if (blobObject.get(i) instanceof Long) {
                testCaseArgAttr.setArgType("Long");
                argValues.add((Long) blobObject.get(i));
                testCaseArgAttr.setArgValue(argValues);
            } else if (blobObject.get(i) instanceof Character) {
                testCaseArgAttr.setArgType("Character");
                argValues.add((Character) blobObject.get(i));
                testCaseArgAttr.setArgValue(argValues);
            } else if (blobObject.get(i) instanceof ArrayList) {
                testCaseArgAttr.setArgType("ArrayList");
                argValues = (ArrayList) blobObject.get(i);
                testCaseArgAttr.setArgValue(argValues);
                testCaseArgAttr.setArgListTypes(getArgTypes(argValues, "ArrayList"));
            } else if (blobObject.get(i).getClass().isArray()) {
                arrayType = blobObject.get(i).getClass().getComponentType().toString();
                if (arrayType.equals("int")) {
                    testCaseArgAttr.setArgType("int[]");
                    argValues.add((int[]) blobObject.get(i));
                    testCaseArgAttr.setArgValue(argValues);
                } else if (arrayType.equals("class java.lang.String")) {
                    testCaseArgAttr.setArgType("String[]");
                    argValues.add((String[]) blobObject.get(i));
                    testCaseArgAttr.setArgValue(argValues);
                } else if (arrayType.equals("double")) {
                    testCaseArgAttr.setArgType("double[]");
                    argValues.add((double[]) blobObject.get(i));
                    testCaseArgAttr.setArgValue(argValues);
                } else if (arrayType.equals("float")) {
                    testCaseArgAttr.setArgType("float[]");
                    argValues.add((float[]) blobObject.get(i));
                    testCaseArgAttr.setArgValue(argValues);
                } else if (arrayType.equals("boolean")) {
                    testCaseArgAttr.setArgType("boolean[]");
                    argValues.add((boolean[]) blobObject.get(i));
                    testCaseArgAttr.setArgValue(argValues);
                } else if (arrayType.equals("long")) {
                    testCaseArgAttr.setArgType("long[]");
                    argValues.add((long[]) blobObject.get(i));
                    testCaseArgAttr.setArgValue(argValues);
                } else if (arrayType.equals("char")) {
                    testCaseArgAttr.setArgType("char[]");
                    argValues.add((char[]) blobObject.get(i));
                    testCaseArgAttr.setArgValue(argValues);
                } else if (arrayType.equals("byte")) {
                    testCaseArgAttr.setArgType("byte[]");
                    argValues.add((byte[]) blobObject.get(i));
                    testCaseArgAttr.setArgValue(argValues);
                } else if (arrayType.equals("short")) {
                    testCaseArgAttr.setArgType("short[]");
                    argValues.add((short[]) blobObject.get(i));
                    testCaseArgAttr.setArgValue(argValues);
                }
            }

            testCaseArgs.add(testCaseArgAttr);

        }

        return testCaseArgs;

    }


    /*****************************************************************************************/

    private static ExpectedResult buildExpectedResult(Object blobObject,
            int problem_id, int test_case_id) {

        ExpectedResult expectedResultAttr = new ExpectedResult();
        ArrayList argValues = new ArrayList();
        String argType = "";
        String arrayType = "";

        expectedResultAttr.setProblemId(problem_id);
        expectedResultAttr.setTestCaseId(test_case_id);

        if (blobObject instanceof Integer) {
            expectedResultAttr.setResultType("Integer");
            argValues.add(new Integer(blobObject.toString()));
            expectedResultAttr.setResultValue(argValues);
        } else if (blobObject instanceof Double) {
            expectedResultAttr.setResultType("Double");
            argValues.add(new Double(blobObject.toString()));
            expectedResultAttr.setResultValue(argValues);
        } else if (blobObject instanceof String) {
            expectedResultAttr.setResultType("String");
            argValues.add(new String(blobObject.toString()));
            expectedResultAttr.setResultValue(argValues);
        } else if (blobObject instanceof Float) {
            expectedResultAttr.setResultType("Float");
            argValues.add(new Float(blobObject.toString()));
            expectedResultAttr.setResultValue(argValues);
        } else if (blobObject instanceof Boolean) {
            expectedResultAttr.setResultType("Boolean");
            argValues.add(new Boolean(blobObject.toString()));
            expectedResultAttr.setResultValue(argValues);
        } else if (blobObject instanceof Long) {
            expectedResultAttr.setResultType("Long");
            argValues.add(new Long(blobObject.toString()));
            expectedResultAttr.setResultValue(argValues);
        } else if (blobObject instanceof Character) {
            expectedResultAttr.setResultType("Character");
            argValues.add(new Character((blobObject.toString()).charAt(0)));
            expectedResultAttr.setResultValue(argValues);
        } else if (blobObject instanceof ArrayList) {
            expectedResultAttr.setResultType("ArrayList");
            argValues = (ArrayList) blobObject;
            expectedResultAttr.setResultValue(argValues);
            expectedResultAttr.setArgListTypes(getArgTypes(argValues, "ArrayList"));
        } else if (blobObject.getClass().isArray()) {
            arrayType = blobObject.getClass().getComponentType().toString();
            if (arrayType.equals("int")) {
                expectedResultAttr.setResultType("int[]");
                argValues.add((int[]) blobObject);
                expectedResultAttr.setResultValue(argValues);
            } else if (arrayType.equals("class java.lang.String")) {
                expectedResultAttr.setResultType("String[]");
                argValues.add((String[]) blobObject);
                expectedResultAttr.setResultValue(argValues);
            } else if (arrayType.equals("double")) {
                expectedResultAttr.setResultType("double[]");
                argValues.add((double[]) blobObject);
                expectedResultAttr.setResultValue(argValues);
            } else if (arrayType.equals("float")) {
                expectedResultAttr.setResultType("float[]");
                argValues.add((float[]) blobObject);
                expectedResultAttr.setResultValue(argValues);
            } else if (arrayType.equals("boolean")) {
                expectedResultAttr.setResultType("boolean[]");
                argValues.add((boolean[]) blobObject);
                expectedResultAttr.setResultValue(argValues);
            } else if (arrayType.equals("long")) {
                expectedResultAttr.setResultType("long[]");
                argValues.add((long[]) blobObject);
                expectedResultAttr.setResultValue(argValues);
            } else if (arrayType.equals("char")) {
                expectedResultAttr.setResultType("char[]");
                argValues.add((char[]) blobObject);
                expectedResultAttr.setResultValue(argValues);
            } else if (arrayType.equals("byte")) {
                expectedResultAttr.setResultType("byte[]");
                argValues.add((byte[]) blobObject);
                expectedResultAttr.setResultValue(argValues);
            } else if (arrayType.equals("short")) {
                expectedResultAttr.setResultType("short[]");
                argValues.add((short[]) blobObject);
                expectedResultAttr.setResultValue(argValues);
            }
        }

        return expectedResultAttr;

    }


    private static ArrayList getArgTypes(ArrayList arrList, String type) {

        ArrayList Matrix2DArrList = null;
        ArrayList arrTypeList = new ArrayList();

        int repeatVal = 0;

        if (type.equals("ArrayList")) {
            repeatVal = arrList.size();
        } else if (type.equals("Matrix2D")) {
            Matrix2DArrList = getArgTypes((ArrayList) arrList.get(0), "firstType");
            arrTypeList.add((String) Matrix2DArrList.get(0));
        } else if (type.equals("firstType")) {
            repeatVal = 1;
        }

        for (int i = 0; i < repeatVal; i++) {

            if (arrList.get(i) instanceof Integer) {
                arrTypeList.add("Integer");
            } else if (arrList.get(i) instanceof Double) {
                arrTypeList.add("Double");
            } else if (arrList.get(i) instanceof String) {
                arrTypeList.add("String");
            } else if (arrList.get(i) instanceof Float) {
                arrTypeList.add("Float");
            } else if (arrList.get(i) instanceof Boolean) {
                arrTypeList.add("Boolean");
            } else if (arrList.get(i) instanceof Long) {
                arrTypeList.add("Long");
            } else if (arrList.get(i) instanceof Character) {
                arrTypeList.add("Character");
            }

        }

        return arrTypeList;

    }

    //////////////////////////////////////////////////////////////////////////
    public static VerifyContest getRoundContest(int round_id) throws Exception
            //////////////////////////////////////////////////////////////////////////
    {

        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuffer sqlStr = new StringBuffer(120);
        VerifyContest contest = new VerifyContest();

        try {
            conn = DBMS.getDirectConnection();

            sqlStr.append(" SELECT c.contest_id, c.name, c.start_date, c.end_date, c.status ").
                    append(" FROM round r, contest c");
            sqlStr.append(" WHERE r.round_id = ? and r.contest_id = c.contest_id ");

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, round_id);
            rs = ps.executeQuery();

            if (rs.next()) {
                contest.setContestId(rs.getInt(1));
                contest.setContestName(rs.getString(2));
                contest.setStartDate(rs.getTimestamp(3));
                contest.setEndDate(rs.getTimestamp(4));
                contest.setStatus(rs.getString(5));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception ignore) {
            }
        }

        return contest;
    }

    //////////////////////////////////////////////////////////////////////////
    public static VerifyRound getRoundInformation(int round_id) throws Exception
            //////////////////////////////////////////////////////////////////////////
    {

        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuffer sqlStr = new StringBuffer(120);
        VerifyRound round = new VerifyRound();

        try {
            conn = DBMS.getDirectConnection();

            sqlStr.append(" SELECT r.name, r.invitational, r.registration_limit, r.status, ").
                    append(" r.round_type_id, rt.round_type_desc ").
                    append(" FROM round r, round_type_lu rt").
                    append(" WHERE r.round_id = ? AND ").
                    append("       r.round_type_id = rt.round_type_id ");

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, round_id);
            rs = ps.executeQuery();

            if (rs.next()) {
                round.setRoundId(round_id);
                round.setRoundName(rs.getString(1));
                round.setInvitational(rs.getInt(2));
                round.setRegistrationLimit(rs.getInt(3));
                round.setStatus(rs.getString(4));
                round.setRoundTypeId(rs.getInt(5));
                round.setRoundTypeDesc(rs.getString(6));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception ignore) {
            }
        }

        return round;
    }


    //////////////////////////////////////////////////////////////////////////
    public static ArrayList getRoundSegments(int round_id) throws Exception
            //////////////////////////////////////////////////////////////////////////
    {

        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuffer sqlStr = new StringBuffer(120);
        ArrayList roundSegments = new ArrayList();
        RoundSegment roundSegmentAttributes;

        try {
            conn = DBMS.getDirectConnection();

            sqlStr.append(" SELECT rs.start_time, rs.end_time, rs.segment_id, s.segment_desc ").
                    append(" FROM round_segment rs, segment s");
            sqlStr.append(" WHERE rs.round_id = ? and rs.status = 'F' and rs.segment_id = s.segment_id ");

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, round_id);
            rs = ps.executeQuery();

            while (rs.next()) {
                roundSegmentAttributes = new RoundSegment(0, 0);
                roundSegmentAttributes.setRoundId(round_id);
                roundSegmentAttributes.setStart(rs.getTimestamp(1));
                roundSegmentAttributes.setEnd(rs.getTimestamp(2));
                roundSegmentAttributes.setSegmentId(rs.getInt(3));
                roundSegmentAttributes.setSegmentDesc(rs.getString(4));

                roundSegments.add(roundSegmentAttributes);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception ignore) {
            }
        }

        return roundSegments;
    }

    //////////////////////////////////////////////////////////////////////////
    public static ArrayList getConflictingContests(int contest_id) throws Exception
            //////////////////////////////////////////////////////////////////////////
    {

        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuffer sqlStr = new StringBuffer(120);
        ArrayList contests = new ArrayList();
        VerifyContest contest;

        try {
            conn = DBMS.getDirectConnection();

            sqlStr.append(" SELECT contest_id, name, start_date, end_date, status ").
                    append(" FROM contest ").
                    append(" WHERE (start_date BETWEEN (SELECT start_date FROM contest WHERE contest_id = ?) AND ").
                    append("                           (SELECT end_date FROM contest WHERE contest_id = ?) OR ").
                    append("        end_date BETWEEN (SELECT start_date FROM contest WHERE contest_id = ?) AND ").
                    append("                         (SELECT end_date FROM contest WHERE contest_id = ?)) AND ").
                    append("       contest_id != ? ").
                    append(" ORDER BY contest_id ");

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, contest_id);
            ps.setInt(2, contest_id);
            ps.setInt(3, contest_id);
            ps.setInt(4, contest_id);
            ps.setInt(5, contest_id);
            rs = ps.executeQuery();

            while (rs.next()) {
                contest = new VerifyContest();
                contest.setContestId(rs.getInt(1));
                contest.setContestName(rs.getString(2));
                contest.setStartDate(rs.getTimestamp(3));
                contest.setEndDate(rs.getTimestamp(4));
                contest.setStatus(rs.getString(5));
                contests.add(contest);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception ignore) {
            }
        }

        return contests;
    }

    //////////////////////////////////////////////////////////////////////////
    public static ArrayList getAdminRoomInformation(int round_id) throws Exception
            //////////////////////////////////////////////////////////////////////////
    {

        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuffer sqlStr = new StringBuffer(120);
        VerifyRoom room;
        ArrayList rooms = new ArrayList();

        try {
            conn = DBMS.getDirectConnection();

            sqlStr.append(" SELECT room_id, name ").
                    append(" FROM room  ").
                    append(" WHERE round_id = ? AND ").
                    append("       room_type_id = 1 ");

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, round_id);
            rs = ps.executeQuery();

            while (rs.next()) {
                room = new VerifyRoom();
                room.setRoundId(round_id);
                room.setRoomId(rs.getInt(1));
                room.setRoomName(rs.getString(2));
                rooms.add(room);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception ignore) {
            }
        }

        return rooms;
    }


    ////////////////////////////////////////////////////////////////////////////////
    public static ArrayList getRoundQuestions(int round_id) throws Exception {

        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList roundQuestions = new ArrayList(2);
        Question questionAttr;
        int question_id;


        StringBuffer sqlStr = new StringBuffer(300);

        sqlStr.append(" SELECT rq.question_id, q.question_text, q.status_id, ").
                append(" q.keyword, q.question_type_id, q.question_style_id, ").
                append(" qt.question_type_desc, qs.question_style_desc ").
                append(" FROM round_question rq, question q, question_type qt, ").
                append(" question_style qs ").
                append(" WHERE rq.round_id = ? AND ").
                append("       rq.question_id = q.question_id AND ").
                append("       q.question_type_id = qt.question_type_id AND ").
                append("       q.question_style_id = qs.question_style_id ").
                append(" ORDER BY question_id ");


        try {
            conn = DBMS.getDirectConnection();

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, round_id);

            rs = ps.executeQuery();

            while (rs.next()) {
                questionAttr = new Question();
                question_id = rs.getInt(1);
                questionAttr.setQuestionId(question_id);
                questionAttr.setQuestionText(rs.getString(2));
                questionAttr.setStatusId(rs.getInt(3));
                questionAttr.setKeyword(rs.getString(4));
                questionAttr.setQuestionTypeId(rs.getInt(5));
                questionAttr.setQuestionStyleId(rs.getInt(6));
                questionAttr.setQuestionTypeDesc(rs.getString(7));
                questionAttr.setQuestionStyleDesc(rs.getString(8));
                questionAttr.setAnswers(getQuestionAnswers(question_id));
                roundQuestions.add(questionAttr);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ignore) {
            }
        }

        return roundQuestions;

    }

    public static ArrayList getQuestionAnswers(int question_id) throws Exception {

        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList answers = new ArrayList(10);
        Answer answerAttr;


        StringBuffer sqlStr = new StringBuffer(200);

        sqlStr.append(" SELECT answer_id, answer_text, sort_order, correct ").
                append(" FROM answer ").
                append(" WHERE question_id = ? ").
                append(" ORDER BY answer_id ");


        try {
            conn = DBMS.getDirectConnection();

            ps = conn.prepareStatement(sqlStr.toString());
            ps.setInt(1, question_id);

            rs = ps.executeQuery();

            while (rs.next()) {
                answerAttr = new Answer();
                answerAttr.setAnswerId(rs.getInt(1));
                answerAttr.setAnswerText(rs.getString(2));
                answerAttr.setSortOrder(rs.getInt(3));
                answerAttr.setCorrect(rs.getInt(4));
                answerAttr.setQuestionId(question_id);
                answers.add(answerAttr);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ignore) {
            }
        }

        return answers;

    }


}
