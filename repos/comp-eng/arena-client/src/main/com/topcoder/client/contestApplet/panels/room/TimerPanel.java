package com.topcoder.client.contestApplet.panels.room;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.widgets.ImageIconPanel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.contestant.view.HeartbeatListener;
import com.topcoder.netCommon.contest.ContestConstants;



public class TimerPanel extends ImageIconPanel implements HeartbeatListener {
    private static final String DEFAULT_VALUE = "00:00:00";
    public static final int CLOCK_MODE = 1;
    public static final int COUNTDOWN_MODE = 2;
    public static final int SYSTEST_MODE = 3;

    private final NumberFormat systestFormat = new DecimalFormat("0.00");
    private final NumberFormat doubleDigitFormat = new DecimalFormat("00");
    private final DateFormat clockFormat = new SimpleDateFormat("h:mm:ss a z");

    private int testsDone;
    private int totalSystests;
    private JLabel timerTitle = null;
    private JLabel timer = null;

    private ContestApplet contestApplet = null;
    
    // 4/15/03 - Pops - Added default mode
    private int mode = TimerPanel.CLOCK_MODE;

    private boolean warned = false;

    private RoundModel roundModel;

    protected TimerPanel(ContestApplet contestApplet, LayoutManager lm, ImageIcon imageIcon) {
        super(lm, imageIcon);
        this.contestApplet = contestApplet;
        contestApplet.getModel().addHeartbeatListener(this);
    }

    public TimerPanel(ContestApplet ca) {
        // place the image in the background of the panel
        super(new GridBagLayout(), Common.getImage("timer.gif", ca));
        this.contestApplet = ca;
        // set the size
        setMinimumSize(new Dimension(165, 72));
        setPreferredSize(new Dimension(165, 72));
        // create the timer
        createTimer(0, 0);
        ca.getModel().addHeartbeatListener(this);
    }

    public void setMode(int mode) {
        if (mode != CLOCK_MODE && mode != COUNTDOWN_MODE && mode != SYSTEST_MODE) {
            throw new IllegalArgumentException("Invalid mode: " + mode);
        }
        if (mode == COUNTDOWN_MODE && roundModel == null) {
            throw new IllegalStateException("Cannot start countdown with null roundModel!");
        }
        this.mode = mode;
//        resetName();
    }


    public void setRoundModel(RoundModel roundModel) {
        if (roundModel != this.roundModel) {
            resetOnRoundChanged();
        }
        this.roundModel = roundModel;
    }

    private void resetOnRoundChanged() {
        testsDone = 0;
        totalSystests = 0;
    }

    public void unsetRoundModel() {
        setMode(TimerPanel.CLOCK_MODE);
        this.roundModel = null;
    }

    protected void createTimer(int shiftX, int shiftY) {
        // set grid bag constraints based on image positioning
        GridBagConstraints gbc = Common.getDefaultConstraints();
        gbc.fill = GridBagConstraints.NONE;

        // create components
        timerTitle = new JLabel("TOPCODER TIME");
        timerTitle.setForeground(Common.TIMER_COLOR);
        timerTitle.setFont(new Font("SansSerif", Font.PLAIN, 10));

        timer = new JLabel(DEFAULT_VALUE);
        timer.setForeground(Common.TIMER_COLOR);
        timer.setFont(new Font("SansSerif", Font.PLAIN, 12));

        gbc.insets = new Insets(30 + shiftY, 37 + shiftX, 3, 15);
        Common.insertInPanel(timerTitle, this, gbc, 0, 0, 1, 1, 0.1, 0.1);
        gbc.insets = new Insets(2, 37 + shiftX, 15, 15);
        Common.insertInPanel(timer, this, gbc, 0, 1, 1, 1, 0.1, 0.1);
        setToolTipText("The Contest Timer counts down the different phases of a competition.");
        setMode(CLOCK_MODE);
    }

//    public void setPhase(int phase) {
//        currentPhase=phase;
////        resetName();
//    }

//    private void resetName() {
//        String title = null;
//        switch (mode) {
//            case CLOCK_MODE:
//                title = "TOPCODER TIME";
//                break;
//            case SYSTEST_MODE:
//                title = "SYSTEM TESTING";
//                break;
//            case COUNTDOWN_MODE:
//                switch (currentPhase) {
//                    case ContestConstants.REGISTRATION_PHASE    : title = "REGISTRATION"; break;
//                    case ContestConstants.ALMOST_CONTEST_PHASE    : title = ("STARTS IN"); break;
//                    case ContestConstants.CODING_PHASE            : title = ("CODING"); break;
//                    case ContestConstants.INTERMISSION_PHASE      : title = ("INTERMISSION"); break;
//                    case ContestConstants.CHALLENGE_PHASE         : title = ("CHALLENGE"); break;
//                    case ContestConstants.PENDING_SYSTESTS_PHASE  : title = ("PENDING SYSTESTS"); break;
//                    case ContestConstants.SYSTEM_TESTING_PHASE    : title = ("SYSTEM TESTING"); break;
//                    case ContestConstants.CONTEST_COMPLETE_PHASE  : title = ("CONTEST COMPLETE"); break;
//                    case ContestConstants.MODERATED_CHATTING_PHASE: title = ("MODERATED CHAT"); break;
//                    default:
//                        throw new IllegalArgumentException("Invalid phase for countdown mode (" + currentPhase + ").");
//                }
//                break;
//            default:
//                throw new IllegalStateException("Invalid timer mode: " + mode);
//        }
//        setTitle(title);
//    }


    public void setTitle(String name) {
        timerTitle.setText(name);
    }

//    public void updateTimer(String time) {
//        if (currentPhase==ContestConstants.SYSTEM_TESTING_PHASE) {
//            updateSystestProgress();
//        } else {
//            timer.setText(time);
//        }
//    }

    public void updateSystestProgress(int _testsDone, int _totalSystests) {
        testsDone = _testsDone;
        totalSystests = _totalSystests;
    }

    private void updateSystestProgress() {
        String text;
        if (totalSystests == 0) {
            text = DEFAULT_VALUE;
        } else {
            double percent = 100. * testsDone / totalSystests;
            String percentStr = systestFormat.format(percent);
            text = "" + percentStr + "% of " + totalSystests;
        }
        timer.setText(text);
        timer.repaint();
    }

    private StringBuffer countdownBuffer = new StringBuffer();

    private void countdown() {
        if (roundModel == null) {
            throw new IllegalStateException("Missing round model: " + roundModel);
        }
        countdownBuffer.setLength(0);
        int secondsLeftInPhase = roundModel.getSecondsLeftInPhase();
        //Added this Expired to help the long round slow people
        if (roundModel.getRoundProperties().usesPerUserCodingTime()
                && secondsLeftInPhase == 0
                && roundModel.getPhase().intValue() == ContestConstants.CODING_PHASE) {
            countdownBuffer.append("EXPIRED");
            warn();
        }  else {
            countdownBuffer.append(doubleDigitFormat.format(secondsLeftInPhase / 3600));
            countdownBuffer.append(':');
            secondsLeftInPhase %= 3600;
            countdownBuffer.append(doubleDigitFormat.format(secondsLeftInPhase / 60));
            countdownBuffer.append(':');
            countdownBuffer.append(doubleDigitFormat.format(secondsLeftInPhase % 60));
        }
        timer.setText(countdownBuffer.toString());
        timer.repaint();
    }

    private void warn() {
        //boolean warned = false;
        if (!warned) {
            Common.showMessage("Alert", "Your time has expired", this);
            warned = true;
        }

    }

    public void tick() {
        switch (mode) {
        case CLOCK_MODE:
            //long the_time = contestApplet.getModel().getServerTime();
            //String text = clockFormat.format(new Date(the_time));
            timer.setText(clockFormat.format(new Date(contestApplet.getModel().getServerTime())));
            //timer.setText(text);
            //System.out.println(" (" + the_time + ") tick: server time = " + text); 
            timer.repaint();
            break;
        case COUNTDOWN_MODE:
            countdown();
            break;
        case SYSTEST_MODE:
            updateSystestProgress();
            break;
        default:
            // 4/15/03 - Pops - commented out the exception because
            // it kills the timer thread if the mode is incorrect
            // which forces the user to have to logout & log back
            // in for the timer to ever work again...
            //throw new IllegalStateException("Invalid mode: " + mode);
        }
    }

    public boolean isRoundModelInitialized() {
        return roundModel != null;
    }

}
