package com.topcoder.client.contestApplet.panels.question;

/**
 * SingleChoicePanel.java
 *
 * Created on September 27, 2001
 */

import java.util.*;
import java.awt.*;
import javax.swing.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.netCommon.contest.Question;
import com.topcoder.netCommon.contest.Answer;


/**
 * This class is a panel containing a question box, and an answer box.
 *
 * @author Alex Roman
 * @version 1.0
 */
public class SingleChoicePanel extends QuestionPanel {

    private java.util.List answers = null;
    private JRadioButton[] selections = null;
    private Question question;

    /**
     * Default Constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    public SingleChoicePanel(String title, Question question)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.question = question;

        JTextArea q = new JTextArea(question.getQuestionText(), 4, 40);
        JScrollPane s = new JScrollPane(q);
        GridBagConstraints gbc = Common.getDefaultConstraints();
        ButtonGroup group = new ButtonGroup();

        this.setLayout(new GridBagLayout());
        this.setBorder(Common.getTitledBorder(title));
        this.setBackground(Color.black);

        q.setEditable(false);
        q.setLineWrap(true);
        q.setWrapStyleWord(true);
        q.setMargin(new Insets(5, 5, 5, 5));
        q.setSelectedTextColor(Common.HF_COLOR);
        q.setSelectionColor(Common.HB_COLOR);
        q.setBackground(Common.MB_COLOR);
        q.setForeground(Common.MF_COLOR);

        int line = 0;
        Common.insertInPanel(s, this, gbc, 0, line++, 1, 1, 0.1, 0.1);
        gbc.insets = new Insets(0, 20, 0, 0);

        this.answers = question.getAnswerText();
        this.selections = new JRadioButton[answers.size()];

        // begin inserting check answers
        for (int i = 0; i < answers.size(); i++) {
            JRadioButton jrb1 = new JRadioButton((String) answers.get(i), false);
            jrb1.setBackground(Color.black);
            jrb1.setForeground(Color.white);
            Common.insertInPanel(jrb1, this, gbc, 0, line++, 1, 1, 0.1, 0.1);
            selections[i] = jrb1;
            group.add(jrb1);
        }
    }

    /**
     * Retrieve the answers.
     *
     * @return ArrayList contains the answer.
     */
    ////////////////////////////////////////////////////////////////////////////////
    public Answer getAnswer()
            ////////////////////////////////////////////////////////////////////////////////
    {
        ArrayList result = new ArrayList(answers.size());

        boolean oneFound = false;
        for (int i = 0; i < answers.size(); i++) {
            if (selections[i].isSelected()) {
                oneFound = true;
                result.add(answers.get(i));
                break;
            }
        }

        if (!oneFound) {
            JOptionPane.showMessageDialog(null, "You must provide an answer to the question: \r\n" + question.getQuestionText(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return question.getAnswer(result);
    }
}
