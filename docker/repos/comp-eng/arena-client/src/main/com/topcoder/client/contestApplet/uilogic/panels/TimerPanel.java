package com.topcoder.client.contestApplet.uilogic.panels;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.CommonData;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.contestant.view.HeartbeatListener;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.round.RoundType;


public class TimerPanel implements HeartbeatListener {
    private static final String DEFAULT_VALUE = "00:00:00";
    public static final int CLOCK_MODE = 1;
    public static final int COUNTDOWN_MODE = 2;
    public static final int SYSTEST_MODE = 3;

    private final NumberFormat systestFormat = new DecimalFormat("0.00");
    private final NumberFormat doubleDigitFormat = new DecimalFormat("00");
    private final DateFormat clockFormat = new SimpleDateFormat("h:mm:ss a z");

    private int testsDone;
    private int totalSystests;

    //    private int currentPhase;

    private UIComponent timerTitle = null;
    private UIComponent timer = null;
    private UIPage page;
    private UIComponent timerPanel;

    private ContestApplet contestApplet = null;

    // 4/15/03 - Pops - Added default mode
    private int mode = TimerPanel.CLOCK_MODE;

    //TODO: This is a quick fix. It should be refactored and move it to the model
    private static Map warned = Collections.synchronizedMap(new HashMap());

    private RoundModel roundModel;

    public TimerPanel(ContestApplet ca, UIPage page) {
        this(ca, page, "timer_panel", "timer_title", "timer");
    }

    protected TimerPanel(ContestApplet ca, UIPage page, String timerPanelName, String timerTitleName, String timerName) {
        contestApplet = ca;
        this.page = page;
        timerTitle = page.getComponent(timerTitleName);
        timer = page.getComponent(timerName);
        timerPanel = page.getComponent(timerPanelName);
        ca.getModel().addHeartbeatListener(this);
        timerTitle.setProperty("Text", "TOPCODER TIME");
        timer.setProperty("Text", DEFAULT_VALUE);
        setMode(CLOCK_MODE);
    }

    public void setMode(int mode) {
        if (mode != CLOCK_MODE && mode != COUNTDOWN_MODE && mode != SYSTEST_MODE) {
            throw new IllegalArgumentException("Invalid mode: " + mode);
        }
        if (mode == COUNTDOWN_MODE && roundModel == null) {
            throw new IllegalStateException("Cannot start countdown with null roundModel!");
        }
        this.mode = mode;
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

    public void setTitle(String name) {
        timerTitle.setProperty("Text", name);
    }

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
        timer.setProperty("Text", text);
        timer.performAction("repaint");
    }

    private StringBuffer countdownBuffer = new StringBuffer();

    private void countdown() {
        if (roundModel == null) {
            throw new IllegalStateException("Missing round model: " + roundModel);
        } else {
            try {
                roundModel = contestApplet.getModel().getRound(roundModel.getRoundID().longValue());
            } catch (Exception e) {
                return;
            }
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
        timer.setProperty("Text", countdownBuffer.toString());
        timer.performAction("repaint");
    }

    private void warn() {
        if (contestApplet.getModel().isLoggedIn() && !isWarned()) {
            setWarned();
            String msg = "Your time has expired";
            if (roundModel.getRoundType() == RoundType.LONG_ROUND_TYPE && CommonData.showSystemTestsPerCoder(contestApplet.getCompanyName())) {
                msg = msg + "\n\nTo view these system test results, select \"System Test Results\" on the summary screen\n";
            }
            Common.showMessage("Alert", msg, contestApplet.getMainFrame());
        }
    }

    private void setWarned() {
        warned.put(buildWarnKey(), Boolean.TRUE);
    }

    private String buildWarnKey() {
        return roundModel.getRoundID()+"|"+contestApplet.getModel().getCurrentUser();
    }

    private boolean isWarned() {
        return warned.get(buildWarnKey()) == Boolean.TRUE;
    }

    public void tick() {
        switch (mode) {
        case CLOCK_MODE:
            //long the_time = contestApplet.getModel().getServerTime();
            //String text = clockFormat.format(new Date(the_time));
            timer.setProperty("Text", clockFormat.format(new Date(contestApplet.getModel().getServerTime())));
            //timer.setText(text);
            //System.out.println(" (" + the_time + ") tick: server time = " + text); 
            timer.performAction("repaint");
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

    public void setVisible(boolean on) {
        timerPanel.setProperty("visible", Boolean.valueOf(on));
    }
}
