package com.topcoder.client.contestApplet.panels.question;

/**
 * QuestionPanel.java
 *
 * Created on September 27, 2001
 */

import java.util.*;
import javax.swing.JPanel;

import com.topcoder.netCommon.contest.Answer;

/**
 * This class is responsible for managing the list of room leaders during an active
 * competition.
 *
 * @author Alex Roman
 * @version 1.0
 */
public abstract class QuestionPanel extends JPanel {

    /**
     * Retrieve the answers.
     *
     * @return ArrayList contains the answer.
     */
    ////////////////////////////////////////////////////////////////////////////////
    public abstract Answer getAnswer();
    ////////////////////////////////////////////////////////////////////////////////
}
