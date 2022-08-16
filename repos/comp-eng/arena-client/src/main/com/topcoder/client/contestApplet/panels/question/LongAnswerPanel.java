package com.topcoder.client.contestApplet.panels.question;

/**
 * LongAnswerPanel.java
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
public class LongAnswerPanel extends QuestionPanel {

    private JTextArea userText = null;
    private Question question;

    /**
     * Default Constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    public LongAnswerPanel(String title, Question question)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.question = question;

        JTextArea q = new JTextArea(question.getQuestionText(), 4, 40);
        JTextArea a = new JTextArea("", 4, 40);
        JScrollPane qs = new JScrollPane(q);
        JScrollPane as = new JScrollPane(a);
        GridBagConstraints gbc = Common.getDefaultConstraints();

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
        a.setLineWrap(true);
        a.setWrapStyleWord(true);

        Common.insertInPanel(qs, this, gbc, 0, 0, 1, 1, 0.1, 0.1);
        Common.insertInPanel(as, this, gbc, 0, 1, 1, 1, 0.1, 0.1);

        userText = a;
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
        if (userText.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "You must provide an answer to the question: \r\n" + question.getQuestionText(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        ArrayList result = new ArrayList(1);
        result.add(userText.getText());
        return question.getAnswer(result);
    }
}
