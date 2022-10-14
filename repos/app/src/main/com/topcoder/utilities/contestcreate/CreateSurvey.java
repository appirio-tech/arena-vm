package com.topcoder.utilities.contestcreate;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.IdGeneratorClient;

public class CreateSurvey
        extends Creator {

    static final String TABLE_SURVEY = "SURVEY";
    static final String TABLE_QUESTION = "QUESTION";
    static final String TABLE_ANSWER = "ANSWER";
    static final String TABLE_SURVEY_QUESTION = "SURVEY_QUESTION";
    static final String TABLE_ROUND_QUESTION = "ROUND_QUESTION";
    //static final String TABLE_ROUND           = "ROUND";

    Connection _dbconn = null;

    CreateSurvey(Properties props) {
        super(props);
    }

    void load(Connection conn) {
        _dbconn = conn;

        Integer roundid = extractInteger("round_id");
        int surveyid = createOrUpdateSurvey(roundid);
        int questionid = findSurveyQuestionId(surveyid);

        //Survey Question
        createOrUpdateSurveyQuestion(questionid);
        createSurveyQuestionAnswers(questionid);
        createOrUpdateSurveyQuestion(surveyid, questionid);
        createOrUpdateRoundQuestion(roundid.intValue(), questionid);

        //Eligiblity Question
        questionid = findEligibleQuestionId(roundid.intValue());
        createOrUpdateEligibleQuestion(questionid);
        createEligibleQuestionAnswers(questionid);
        createOrUpdateRoundQuestion(roundid.intValue(), questionid);

    }


    int createOrUpdateSurvey(Integer surveyid) {
        LoadRow survey = new LoadRow(TABLE_SURVEY);

        survey.addKey("survey_id", surveyid);
        survey.add("name", extractString("survey_name"));
        survey.add("start_date", extractDate("survey_start"));
        survey.add("end_date", addMinutes(extractDate("survey_start"),
                extractInteger("survey_length")));
        survey.add("status_id", extractString("survey_status_id"));
        survey.add("text", extractString("survey_text"));

        survey.apply(_dbconn);

        return (surveyid != null) ? surveyid.intValue() : 0;
    }


    int findSurveyQuestionId(int surveyid) {
        try {
            // dont worry about freeing - this is a throwaway section
            String sql = "select question_id from " + TABLE_SURVEY_QUESTION +
                    " where survey_id = ? "; //and keyword = ?";
            PreparedStatement st = _dbconn.prepareStatement(sql);
            st.setInt(1, surveyid);
            //st.setString(2, extractString("survey_question_keyword"));

            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding question id: " + e.getMessage());
        }

        try {
            return IdGeneratorClient.getSeqIdAsInt(DBMS.SURVEY_SEQ);
        } catch (Exception e) {
            // GRRR!  never throw "Exception"
            throw new RuntimeException("DBMS.getSeqId() failed: " + e.getMessage());
        }
    }

    int findEligibleQuestionId(int roundid) {
        try {

            String sql = "SELECT rq.question_id FROM " + TABLE_ROUND_QUESTION + " rq, " + TABLE_QUESTION + " q" +
                    " WHERE rq.round_id = ? AND rq.question_id = q.question_id AND " +
                    " q.question_type_id = " + ContestConstants.ELIGIBLE_QUESTION;
            PreparedStatement st = _dbconn.prepareStatement(sql);
            st.setInt(1, roundid);

            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding question id: " + e.getMessage());
        }

        try {
            return IdGeneratorClient.getSeqIdAsInt(DBMS.SURVEY_SEQ);
        } catch (Exception e) {
            // GRRR!  never throw "Exception"
            throw new RuntimeException("DBMS.getSeqId() failed: " + e.getMessage());
        }
    }

    void createOrUpdateSurveyQuestion(int questionid) {
        LoadRow question = new LoadRow(TABLE_QUESTION);

        question.addKey("question_id", new Integer(questionid));
        question.add("question_text", extractString("survey_question_text"));
        question.add("keyword", extractString("survey_question_keyword"));
        question.add("question_type_id", String.valueOf(ContestConstants.SURVEY_QUESTION));
        question.add("question_style_id", extractInteger("survey_question_style_id"));
        question.add("status_id", extractInteger("survey_question_status_id"));


        question.apply(_dbconn);
    }

    void createOrUpdateEligibleQuestion(int questionid) {
        LoadRow question = new LoadRow(TABLE_QUESTION);

        question.addKey("question_id", new Integer(questionid));
        question.add("question_text", extractString("eligible_question_text"));
        question.add("keyword", extractString("eligible_question_keyword"));
        question.add("question_type_id", String.valueOf(ContestConstants.ELIGIBLE_QUESTION));
        question.add("question_style_id", extractInteger("eligible_question_style_id"));
        question.add("status_id", extractInteger("eligible_question_status_id"));


        question.apply(_dbconn);
    }

    void createSurveyQuestionAnswers(int questionid) {
        MultiLoadRow answers = new MultiLoadRow();

        LoadRow keyinfo = new LoadRow(TABLE_ANSWER);
        keyinfo.addKey("question_id", new Integer(questionid));
        answers.setKeyInfo(keyinfo);

        String[] answerlist = extractList("survey_answers");
        for (int i = 0; i < answerlist.length; i++) {
            String[] words = ListParser.split(answerlist[i]);

            if (words.length != 2) {
                System.err.println("survey_answers list item " + i +
                        " does not contain 2 elements");
            }

            try {
                String answertext = words[0];
                int sortorder = Integer.parseInt(words[1]);
                int answerid = IdGeneratorClient.getSeqIdAsInt(DBMS.SURVEY_SEQ);

                LoadRow row = new LoadRow(TABLE_ANSWER);
                row.addKey("question_id", new Integer(questionid));
                row.add("answer_id", new Integer(answerid));
                row.add("answer_text", answertext);
                row.add("sort_order", new Integer(sortorder));

                answers.addRow(row);

            } catch (Exception e) {
                System.err.println("survey_answers list item " + i +
                        " contains invalid values");
            }
        }

        answers.apply(_dbconn);
    }

    void createEligibleQuestionAnswers(int questionid) {
        MultiLoadRow answers = new MultiLoadRow();

        LoadRow keyinfo = new LoadRow(TABLE_ANSWER);
        keyinfo.addKey("question_id", new Integer(questionid));
        answers.setKeyInfo(keyinfo);

        String[] answerlist = extractList("eligible_answers");
        for (int i = 0; i < answerlist.length; i++) {
            String[] words = ListParser.split(answerlist[i]);

            if (words.length != 3) {
                System.err.println("eligible_answers list item " + i +
                        " does not contain 3 elements");
            }

            try {
                String answertext = words[0];
                int sortorder = Integer.parseInt(words[1]);
                int answerid = IdGeneratorClient.getSeqIdAsInt(DBMS.SURVEY_SEQ);
                int correct = Integer.parseInt(words[2]);

                LoadRow row = new LoadRow(TABLE_ANSWER);
                row.addKey("question_id", new Integer(questionid));
                row.add("answer_id", new Integer(answerid));
                row.add("answer_text", answertext);
                row.add("sort_order", new Integer(sortorder));
                row.add("correct", new Integer(correct));

                answers.addRow(row);

            } catch (Exception e) {
                System.err.println("eligible_answers list item " + i +
                        " contains invalid values");
            }
        }

        answers.apply(_dbconn);
    }

    void createOrUpdateSurveyQuestion(int surveyid, int questionid) {
        LoadRow survey_question = new LoadRow(TABLE_SURVEY_QUESTION);

        survey_question.addKey("survey_id", new Integer(surveyid));
        survey_question.addKey("question_id", new Integer(questionid));

        survey_question.apply(_dbconn);
    }

    void createOrUpdateRoundQuestion(int roundid, int questionid) {
        LoadRow round_question = new LoadRow(TABLE_ROUND_QUESTION);

        round_question.addKey("round_id", new Integer(roundid));
        round_question.addKey("question_id", new Integer(questionid));

        round_question.apply(_dbconn);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("ARGS: [property file]");
            return;
        }

        String propfilename = args[0];
        Properties props = new Properties();

        try {
            System.out.println("Reading survey properties...");
            props.load(new FileInputStream(propfilename));
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        CreateSurvey creator = new CreateSurvey(props);
        Connection dbconn = null;

        try {
            System.out.println("Connecting to database...");
            dbconn = DBMS.getDirectConnection();
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        creator.load(dbconn);
    }
}
