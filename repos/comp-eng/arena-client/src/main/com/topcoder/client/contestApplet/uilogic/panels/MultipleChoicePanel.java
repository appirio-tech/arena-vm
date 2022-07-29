package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.netCommon.contest.Answer;
import com.topcoder.netCommon.contest.Question;

public class MultipleChoicePanel implements QuestionPanel {
    private Question question;
    private UIComponent panel;
    private UIPage page;
    private List answers;
    private JCheckBox[] selections;

    public MultipleChoicePanel(ContestApplet ca, String title, Question question) {
        this.question = question;
        page = ca.getCurrentUIManager().getUIPage("multiple_choice_panel", true);
        panel = page.getComponent("root_panel");
        ((TitledBorder) panel.getProperty("border")).setTitle(title);
        page.getComponent("question_text").setProperty("text", question.getQuestionText());

        GridBagConstraints gbc = Common.getDefaultConstraints();
        gbc.insets = new Insets(0, 20, 0, 0);

        this.answers = question.getAnswerText();
        this.selections = new JCheckBox[answers.size()];

        UIComponent template = page.getComponent("choice_template");

        // begin inserting check answers
        for (int i = 0; i < answers.size(); i++) {
            JCheckBox jrb1 = (JCheckBox) template.performAction("clone");
            jrb1.setText((String) answers.get(i));
            Common.insertInPanel(jrb1, (JPanel) panel.getEventSource(), gbc, 0, i + 1, 1, 1, 0.1, 0.1);
            selections[i] = jrb1;
        }
    }

    public UIComponent getPanel() {
        return panel;
    }

    public Answer getAnswer() {
        ArrayList result = new ArrayList(answers.size());

        boolean oneFound = false;
        for (int i = 0; i < answers.size(); i++) {
            if (selections[i].isSelected()) {
                oneFound = true;
                result.add(answers.get(i));
            }
        }

        if (!oneFound) {
            JOptionPane.showMessageDialog(null, "You must provide an answer to the question: \r\n" + question.getQuestionText(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        // Return the result
        return question.getAnswer(result);
    }
}
