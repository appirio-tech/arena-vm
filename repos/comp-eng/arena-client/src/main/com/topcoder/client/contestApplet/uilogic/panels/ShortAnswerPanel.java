package com.topcoder.client.contestApplet.uilogic.panels;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.netCommon.contest.Answer;
import com.topcoder.netCommon.contest.Question;

public class ShortAnswerPanel implements QuestionPanel {
    private Question question;
    private UIComponent panel;
    private UIPage page;
    private UIComponent userText;

    public ShortAnswerPanel(ContestApplet ca, String title, Question question) {
        this.question = question;
        page = ca.getCurrentUIManager().getUIPage("short_answer_panel", true);
        panel = page.getComponent("root_panel");
        ((TitledBorder) panel.getProperty("border")).setTitle(title);
        userText = page.getComponent("answer_text");
        page.getComponent("question_text").setProperty("text", question.getQuestionText());
    }

    public UIComponent getPanel() {
        return panel;
    }

    public Answer getAnswer() {
        if (userText.getProperty("Text").toString().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "You must provide an answer to the question: \r\n" + question.getQuestionText(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        ArrayList result = new ArrayList(1);
        result.add(userText.getProperty("Text").toString());
        return question.getAnswer(result);
    }
}
