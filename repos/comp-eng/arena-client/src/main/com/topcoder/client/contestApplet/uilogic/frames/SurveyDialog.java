package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.uilogic.panels.LongAnswerPanel;
import com.topcoder.client.contestApplet.uilogic.panels.MultipleChoicePanel;
import com.topcoder.client.contestApplet.uilogic.panels.QuestionPanel;
import com.topcoder.client.contestApplet.uilogic.panels.ShortAnswerPanel;
import com.topcoder.client.contestApplet.uilogic.panels.SingleChoicePanel;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIWindowAdapter;
import com.topcoder.netCommon.contest.Answer;

public class SurveyDialog implements FrameLogic {
    private UIPage page;
    private UIComponent dialog;
    private UIComponent okButton = null;
    private UIComponent cancelButton = null;
    private QuestionPanel[] questions = null;
    private ArrayList results = null;

    public UIComponent getFrame() {
        return dialog;
    }

    public SurveyDialog(ContestApplet ca, JFrame frame, String title, String msg, String inst, ArrayList info) {
        page = ca.getCurrentUIManager().getUIPage("survey_dialog", true);
        dialog = page.getComponent("root_dialog", false);
        dialog.setProperty("Owner", frame);
        dialog.setProperty("title", title);
        dialog.create();
        dialog.addEventListener("Window", new UIWindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    closeWindowEvent();
                }
            });
        page.getComponent("instruction_text").setProperty("text", inst);
        page.getComponent("term_text").setProperty("text", msg);

        questions = new QuestionPanel[info.size()];
        UIComponent eligibilityPanel = page.getComponent("eligibility_panel");
        UIComponent surveyPanel = page.getComponent("survey_panel");
        int eligibilityIdx = 0;
        int surveyIdx = 0;
        GridBagConstraints gbc = Common.getDefaultConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.NONE;

        for (int i = 0; i < info.size(); i++) {
            com.topcoder.netCommon.contest.Question question = (com.topcoder.netCommon.contest.Question) info.get(i);

            String questionNbr;
            if (question.getQuestionCategory() == com.topcoder.netCommon.contest.Question.ELIGIBILITY) {
                questionNbr = "[" + (eligibilityIdx + 1) + "]";
            } else {
                questionNbr = "[" + (surveyIdx + 1) + "]";
            }

            if (question.getQuestionType() == com.topcoder.netCommon.contest.Question.SINGLECHOICE) {
                questions[i] = new SingleChoicePanel(ca, questionNbr, question);
            } else if (question.getQuestionType() == com.topcoder.netCommon.contest.Question.MULTIPLECHOICE) {
                questions[i] = new MultipleChoicePanel(ca, questionNbr, question);
            } else if (question.getQuestionType() == com.topcoder.netCommon.contest.Question.SHORTANSWER) {
                questions[i] = new ShortAnswerPanel(ca, questionNbr, question);
            } else if (question.getQuestionType() == com.topcoder.netCommon.contest.Question.LONGANSWER) {
                questions[i] = new LongAnswerPanel(ca, questionNbr, question);
            } else {
                System.out.println("Question type not found.");
                continue;
            }

            if (question.getQuestionCategory() == com.topcoder.netCommon.contest.Question.ELIGIBILITY) {
                gbc.gridy = eligibilityIdx++;
                eligibilityPanel.addChild(questions[i].getPanel(), gbc);
            } else {
                gbc.gridy = surveyIdx++;
                surveyPanel.addChild(questions[i].getPanel(), gbc);
            }
        }

        // Insert the two panels
        if (eligibilityIdx == 0) eligibilityPanel.setProperty("visible", Boolean.FALSE);
        if (surveyIdx == 0) surveyPanel.setProperty("visible", Boolean.FALSE);

        okButton = page.getComponent("ok_button");
        cancelButton = page.getComponent("cancel_button");
        okButton.addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    okButtonEvent();
                }
            });
        cancelButton.addEventListener("Action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cancelButtonEvent();
                }
            });
        dialog.performAction("pack");
        Common.setLocationRelativeTo(frame, (Component) dialog.getEventSource());
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ArrayList showDialog()
        ////////////////////////////////////////////////////////////////////////////////
    {
        dialog.performAction("show");

        return (results);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setButtonText(String t)
        ////////////////////////////////////////////////////////////////////////////////
    {
        okButton.setProperty("Text", t);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setButton2Text(String t)
        ////////////////////////////////////////////////////////////////////////////////
    {
        cancelButton.setProperty("Text", t);
        cancelButton.setProperty("Visible", Boolean.TRUE);
    }


    ////////////////////////////////////////////////////////////////////////////////
    private void okButtonEvent()
        ////////////////////////////////////////////////////////////////////////////////
    {
        // some test results
        ArrayList results = new ArrayList(questions.length + 1);

        results.add(new Boolean(true));
        for (int i = 0; i < questions.length; i++) {
            // Get the answer to the question
            Answer theAnswer = questions[i].getAnswer();

            // If we received null - validation error - simply return
            if (theAnswer == null) {
                System.out.println("here");
                return;
            }

            // Add teh answer to the results
            results.add(theAnswer);
        }
        //System.out.println("results : " + results);
        this.results = results;

        dialog.performAction("dispose");  // frees up the show() -- must be last
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void closeWindowEvent()
        ////////////////////////////////////////////////////////////////////////////////
    {
        cancelButtonEvent();
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void cancelButtonEvent()
        ////////////////////////////////////////////////////////////////////////////////
    {
        ArrayList results = new ArrayList(1);
        results.add(new Boolean(false));
        this.results = results;

        dialog.performAction("dispose");  // frees up the show() -- must be last
    }
}
