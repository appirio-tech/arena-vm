package com.topcoder.client.contestApplet.frames;

/*
 * SurveyDialog.java
 *
 * Created on July 10, 2000, 4:08 PM
 */

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
//import javax.swing.table.*;
//import javax.swing.event.*;
import com.topcoder.client.contestApplet.common.*;
//import com.topcoder.client.contestApplet.*;
//import com.topcoder.client.contestApplet.listener.*;
import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.client.contestApplet.panels.question.*;
import com.topcoder.netCommon.contest.*;

/**
 *
 * @author Alex Roman
 * @version
 */

public final class SurveyDialog extends JDialog {

    private JButton okButton = null;
    private JButton cancelButton = null;
    private QuestionPanel[] questions = null;
    //private ArrayList info = null;
    private ArrayList results = null;

    /**
     * Class constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    public SurveyDialog(JFrame frame, String title, String msg, String inst, ArrayList info)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(frame, title, true);

        //this.addWindowListener(new wl("windowClosing", "closeWindowEvent", this));
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeWindowEvent();
            }
        });

        JPanel back = new JPanel(new GridBagLayout());
        JScrollPane jsp = new JScrollPane(back);
        getContentPane().setLayout(new GridBagLayout());
        //getContentPane().setBackground(Common.WPB_COLOR);
        back.setBackground(Color.black);
        jsp.setBorder(Common.getTitledBorder(""));
        jsp.setOpaque(false);
        jsp.getViewport().setBackground(Color.black);

        GridBagConstraints gbc = Common.getDefaultConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;

        Common.insertInPanel(jsp, getContentPane(), gbc, 0, 0, 1, 1, 0.1, 0.1);

        gbc.fill = GridBagConstraints.NONE;
/*
    Common.insertInPanel(getRadioPanel(), back, gbc, 0, 0, 1, 1, 0.1, 0.1);
    Common.insertInPanel(getCheckboxPanel(), back, gbc, 0, 1, 1, 1, 0.1, 0.1);
    Common.insertInPanel(getShortAnswerPanel(), back, gbc, 0, 2, 1, 1, 0.1, 0.1);
    Common.insertInPanel(getLongAnswerPanel(), back, gbc, 0, 3, 1, 1, 0.1, 0.1);

    // some test data
    String question = "this is the question";
    ArrayList answers = new ArrayList(2);
    ArrayList a1 = new ArrayList(2);
    ArrayList a2 = new ArrayList(2);
    answers.add(a1);
    answers.add(a2);
    a1.add(new Integer(1));
    a1.add("this is answer number 1");
    a2.add(new Integer(2));
    a2.add("this is answer number 2");

    Question [] questions = new Question[4];
    questions[0] = new SingleChoicePanel("[1]", question, answers) ;
    questions[1] = new MultipleChoicePanel("[2]", question, answers);
    questions[2] = new ShortAnswerPanel("[3]", question);
    questions[3] = new LongAnswerPanel("[4]", question);

    Common.insertInPanel((JPanel)questions[0], back, gbc, 0, 0, 1, 1, 0.1, 0.1);
    Common.insertInPanel((JPanel)questions[1], back, gbc, 0, 1, 1, 1, 0.1, 0.1);
    Common.insertInPanel((JPanel)questions[2], back, gbc, 0, 2, 1, 1, 0.1, 0.1);
    Common.insertInPanel((JPanel)questions[3], back, gbc, 0, 3, 1, 1, 0.1, 0.1);
*/
        int index = 0;

        // intro
        JTextArea q2 = new JTextArea(inst, 8, 40);
        //JScrollPane qs2 = new JScrollPane(q2);
        //qs2.setPreferredSize(new Dimension(450,250));

        q2.setEditable(false);
        q2.setCaretPosition(0);
        q2.setLineWrap(true);
        q2.setWrapStyleWord(true);
        q2.setMargin(new Insets(5, 5, 5, 5));
        q2.setSelectedTextColor(Common.HF_COLOR);
        q2.setSelectionColor(Common.HB_COLOR);
        q2.setBackground(Common.MB_COLOR);
        q2.setForeground(Common.MF_COLOR);

        Border border = new RoundBorder(Common.LIGHT_GREY, 5, true);
        MyTitledBorder tb = new MyTitledBorder(border, "Important Notice", TitledBorder.LEFT, TitledBorder.ABOVE_TOP);
        tb.setTitleColor(Common.PT_COLOR);
        q2.setBorder(tb);
//    q2.setBorder(BorderFactory.createCompoundBorder(Common.getTitledBorder("Important Notice"),BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.white, Color.gray, Color.gray, Color.black)));
//    q2.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.white, Color.gray, Color.gray, Color.black));

        Common.insertInPanel(q2, back, gbc, 0, index++, 2, 1, 0.1, 0.1);
        //Common.insertInPanel(qs2, back, gbc, 0, index++, 2, 1, 0.1, 0.1);

        // survey
        questions = new QuestionPanel[info.size()];

        JPanel eligibilityPanel = new JPanel(new GridBagLayout());
        eligibilityPanel.setBackground(Color.black);
        eligibilityPanel.setBorder(Common.getTitledBorder("Eligibility Requirements"));

        JPanel surveyPanel = new JPanel(new GridBagLayout());
        surveyPanel.setBackground(Color.black);
        surveyPanel.setBorder(Common.getTitledBorder("Survey Question(s)"));

        int eligibilityIdx = 0;
        int surveyIdx = 0;

        for (int i = 0; i < info.size(); i++) {
            com.topcoder.netCommon.contest.Question question = (com.topcoder.netCommon.contest.Question) info.get(i);

            String questionNbr;
            if (question.getQuestionCategory() == com.topcoder.netCommon.contest.Question.ELIGIBILITY) {
                questionNbr = "[" + (eligibilityIdx + 1) + "]";
            } else {
                questionNbr = "[" + (surveyIdx + 1) + "]";
            }

            if (question.getQuestionType() == com.topcoder.netCommon.contest.Question.SINGLECHOICE) {
                questions[i] = new SingleChoicePanel(questionNbr, question);
            } else if (question.getQuestionType() == com.topcoder.netCommon.contest.Question.MULTIPLECHOICE) {
                questions[i] = new MultipleChoicePanel(questionNbr, question);
            } else if (question.getQuestionType() == com.topcoder.netCommon.contest.Question.SHORTANSWER) {
                questions[i] = new ShortAnswerPanel(questionNbr, question);
            } else if (question.getQuestionType() == com.topcoder.netCommon.contest.Question.LONGANSWER) {
                questions[i] = new LongAnswerPanel(questionNbr, question);
            } else {
                System.out.println("Question type not found.");
                continue;
            }

            if (question.getQuestionCategory() == com.topcoder.netCommon.contest.Question.ELIGIBILITY) {
                Common.insertInPanel((JPanel) questions[i], eligibilityPanel, gbc, 0, eligibilityIdx++, 1, 1, 0.1, 0.1);
            } else {
                Common.insertInPanel((JPanel) questions[i], surveyPanel, gbc, 0, surveyIdx++, 1, 1, 0.1, 0.1);
            }
        }

        // Insert the two panels
        if (eligibilityIdx > 0) Common.insertInPanel(eligibilityPanel, back, gbc, 0, index++, 2, 1, 0.1, 0.1);
        if (surveyIdx > 0) Common.insertInPanel(surveyPanel, back, gbc, 0, index++, 2, 1, 0.1, 0.1);

        // terms
        JTextArea q = new JTextArea(msg, 20, 40);
        JScrollPane qs = new JScrollPane(q);

        q.setEditable(false);
        q.setCaretPosition(0);
        q.setLineWrap(true);
        q.setWrapStyleWord(true);
        q.setMargin(new Insets(5, 5, 5, 5));
        q.setSelectedTextColor(Common.HF_COLOR);
        q.setSelectionColor(Common.HB_COLOR);
        q.setBackground(Common.MB_COLOR);
        q.setForeground(Common.MF_COLOR);

        Common.insertInPanel(qs, back, gbc, 0, index++, 2, 1, 0.1, 0.1);

        // buttons
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        //okButton.addActionListener(new al("actionPerformed", "okButtonEvent", this));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButtonEvent();
            }
        });
        //cancelButton.addActionListener(new al("actionPerformed", "cancelButtonEvent", this));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelButtonEvent();
            }
        });

        this.okButton = okButton;
        this.cancelButton = cancelButton;

        Common.insertInPanel(okButton, back, gbc, 0, index, 1, 1, 0.1, 0.1);
        Common.insertInPanel(cancelButton, back, gbc, 1, index, 1, 1, 0.1, 0.1);
        getRootPane().setPreferredSize(new Dimension(600, 400));
        getRootPane().setMinimumSize(new Dimension(600, 400));
        pack();

        setResizable(true);

        Common.setLocationRelativeTo(frame, this);
    }

/*
  ////////////////////////////////////////////////////////////////////////////////
  public JPanel getCheckboxPanel()
  ////////////////////////////////////////////////////////////////////////////////
  {
    JPanel p = new JPanel(new GridBagLayout());
    //JScrollPane s = new JScrollPane(Common.createMessagePane());
    //JTextArea q = (JTextArea)s.getViewport().getView();
    JTextArea q = new JTextArea("Please select any of the choices below.", 4, 40);
    JScrollPane s = new JScrollPane(q);
    GridBagConstraints gbc = Common.getDefaultConstraints();
    JCheckBox jrb1 = new JCheckBox("This is your first answer", false);
    JCheckBox jrb2 = new JCheckBox("This is your second answer", false);

    p.setBorder(Common.getTitledBorder("(1)"));
    p.setBackground(Color.black);
    q.setLineWrap(true);
    q.setWrapStyleWord(true);
    q.setMargin(new Insets(5,5,5,5));
    q.setSelectedTextColor(Common.HF_COLOR);
    q.setSelectionColor(Common.HB_COLOR);
    q.setBackground(Common.MB_COLOR);
    q.setForeground(Common.MF_COLOR);
    DiamondIcon w = new DiamondIcon(Color.white, true, 12, 12);
    DiamondIcon e = new DiamondIcon(Color.white, false, 12, 12);
    jrb1.setBackground(Color.black);
    jrb1.setForeground(Color.white);
    jrb1.setIcon(e);
    jrb1.setSelectedIcon(w);
    jrb2.setBackground(Color.black);
    jrb2.setForeground(Color.white);
    jrb2.setIcon(e);
    jrb2.setSelectedIcon(w);


    Common.insertInPanel(s, p, gbc, 0, 0, 1, 1, 0.1, 0.1);
    gbc.insets = new Insets(0, 20, 0, 0);
    Common.insertInPanel(jrb1, p, gbc, 0, 1, 1, 1, 0.1, 0.1);
    Common.insertInPanel(jrb2, p, gbc, 0, 2, 1, 1, 0.1, 0.1);

    return(p);
  }

  ////////////////////////////////////////////////////////////////////////////////
  public JPanel getRadioPanel()
  ////////////////////////////////////////////////////////////////////////////////
  {
    JPanel p = new JPanel(new GridBagLayout());
    JTextArea q = new JTextArea("Please select one of the choices below.", 4, 40);
    JScrollPane s = new JScrollPane(q);
    GridBagConstraints gbc = Common.getDefaultConstraints();
    ButtonGroup group = new ButtonGroup();
    JRadioButton jrb1 = new JRadioButton("This is your first answer", false);
    JRadioButton jrb2 = new JRadioButton("This is your second answer", false);
    p.setBorder(Common.getTitledBorder("(2)"));
    p.setBackground(Color.black);
    q.setLineWrap(true);
    q.setWrapStyleWord(true);
    q.setMargin(new Insets(5,5,5,5));
    q.setSelectedTextColor(Common.HF_COLOR);
    q.setSelectionColor(Common.HB_COLOR);
    q.setBackground(Common.MB_COLOR);
    q.setForeground(Common.MF_COLOR);
    jrb1.setBackground(Color.black);
    jrb1.setForeground(Color.white);
    jrb2.setBackground(Color.black);
    jrb2.setForeground(Color.white);

    group.add(jrb1);
    group.add(jrb2);

    Common.insertInPanel(s, p, gbc, 0, 0, 1, 1, 0.1, 0.1);
    gbc.insets = new Insets(0, 20, 0, 0);
    Common.insertInPanel(jrb1, p, gbc, 0, 1, 1, 1, 0.1, 0.1);
    Common.insertInPanel(jrb2, p, gbc, 0, 2, 1, 1, 0.1, 0.1);

    return(p);
  }

  ////////////////////////////////////////////////////////////////////////////////
  public JPanel getShortAnswerPanel()
  ////////////////////////////////////////////////////////////////////////////////
  {
    JPanel p = new JPanel(new GridBagLayout());
    JTextArea q = new JTextArea("Please type in a short answer.", 4, 40);
    JScrollPane s = new JScrollPane(q);
    JTextField a = new JTextField("", 40);
    GridBagConstraints gbc = Common.getDefaultConstraints();

    p.setBorder(Common.getTitledBorder("(3)"));
    p.setBackground(Color.black);
    q.setLineWrap(true);
    q.setWrapStyleWord(true);
    q.setMargin(new Insets(5,5,5,5));
    q.setSelectedTextColor(Common.HF_COLOR);
    q.setSelectionColor(Common.HB_COLOR);
    q.setBackground(Common.MB_COLOR);
    q.setForeground(Common.MF_COLOR);

    Common.insertInPanel(s, p, gbc, 0, 0, 1, 1, 0.1, 0.1);
    Common.insertInPanel(a, p, gbc, 0, 1, 1, 1, 0.1, 0.1);

    return(p);
  }

  ////////////////////////////////////////////////////////////////////////////////
  public JPanel getLongAnswerPanel()
  ////////////////////////////////////////////////////////////////////////////////
  {
    JPanel p = new JPanel(new GridBagLayout());
    JTextArea q = new JTextArea("Please type in a long answer.", 4, 40);
    JTextArea a = new JTextArea("", 4, 40);
    JScrollPane qs = new JScrollPane(q);
    JScrollPane as = new JScrollPane(a);
    GridBagConstraints gbc = Common.getDefaultConstraints();

    p.setBorder(Common.getTitledBorder("(4)"));
    p.setBackground(Color.black);
    q.setLineWrap(true);
    q.setWrapStyleWord(true);
    q.setMargin(new Insets(5,5,5,5));
    q.setSelectedTextColor(Common.HF_COLOR);
    q.setSelectionColor(Common.HB_COLOR);
    q.setBackground(Common.MB_COLOR);
    q.setForeground(Common.MF_COLOR);
    a.setLineWrap(true);
    a.setWrapStyleWord(true);

    Common.insertInPanel(qs, p, gbc, 0, 0, 1, 1, 0.1, 0.1);
    Common.insertInPanel(as, p, gbc, 0, 1, 1, 1, 0.1, 0.1);

    return(p);
  }
*/

    ////////////////////////////////////////////////////////////////////////////////
    public ArrayList showDialog()
            ////////////////////////////////////////////////////////////////////////////////
    {
        show();

        return (results);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setButtonText(String t)
            ////////////////////////////////////////////////////////////////////////////////
    {
        okButton.setText(t);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setButton2Text(String t)
            ////////////////////////////////////////////////////////////////////////////////
    {
        cancelButton.setText(t);
        cancelButton.setVisible(true);
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

        dispose();  // frees up the show() -- must be last
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

        dispose();  // frees up the show() -- must be last
    }


    /*
	public static final void main(String[] a) {
		JFrame f = new JFrame();
		f.getContentPane().add(new JTextField(""));
		f.pack();
		f.show();
		f.setLocation(300,300);

		ArrayList answers = new ArrayList();
		answers.add("Answer 1");
		answers.add("Answer 2");
		answers.add("Answer 3");
		answers.add("Answer 4");
		ArrayList quest = new ArrayList();
//		quest.add(new SurveyQuestion(1,1,"Survey1",Question.SHORTANSWER, answers));
//		quest.add(new SurveyQuestion(1,1,"Survey2",Question.LONGANSWER, answers));
//		quest.add(new SurveyQuestion(1,1,"Survey3",Question.SINGLECHOICE, answers));
//		quest.add(new SurveyQuestion(1,1,"Survey4",Question.MULTIPLECHOICE, answers));
//		quest.add(new EligibilityQuestion(1,1,"Eligible1",Question.SHORTANSWER, answers));
//		quest.add(new EligibilityQuestion(1,1,"Eligible2",Question.LONGANSWER, answers));
//		quest.add(new EligibilityQuestion(1,1,"Eligible3",Question.SINGLECHOICE, answers));
//		quest.add(new EligibilityQuestion(1,1,"Eligible4",Question.MULTIPLECHOICE, answers));
		String tt = "Notice: \nTopCoder expects that all users of the TopCoder Competition Arena will exhibit professional behavior. Any member whose chat is considered by us to be offensive in any way will be immediately removed from the Arena. If you do not plan to respect this policy, we ask that you not participate in the Arena. \n\nIn order to register for participation in this event, you must answer the survey question(s) and agree to the rules of this competition.  The results of this survey may be reported in aggregate and displayed on TopCoder's website or provided to sponsors and prospective sponsors of TopCoder.  When you have selected an answer to the survey question and read the rules, click the \"Agree\" button at the bottom.\n\n\n\n\n\n\nlkjsdf";
		SurveyDialog sd = new SurveyDialog(f, "Registration", "This is the message", tt, quest);
		sd.setButtonText("I agree");
		sd.setButton2Text("I disagree");
       	sd.showDialog();
	}
    */

}
