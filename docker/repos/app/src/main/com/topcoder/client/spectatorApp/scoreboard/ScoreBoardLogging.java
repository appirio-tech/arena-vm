/**
 * ScoreBoardLogging.java
 *
 * Description:		A scoreboard room
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard;

import org.apache.log4j.Category;

import com.topcoder.client.spectatorApp.scoreboard.model.CoderMoveEvent;
import com.topcoder.client.spectatorApp.scoreboard.model.CoderMoveListener;
import com.topcoder.client.spectatorApp.scoreboard.model.PointValueChangeEvent;
import com.topcoder.client.spectatorApp.scoreboard.model.PointValueChangeListener;
import com.topcoder.client.spectatorApp.scoreboard.model.StatusChangeEvent;
import com.topcoder.client.spectatorApp.scoreboard.model.StatusChangeListener;
import com.topcoder.client.spectatorApp.scoreboard.model.TotalChangeEvent;
import com.topcoder.client.spectatorApp.scoreboard.model.TotalChangeListener;


public class ScoreBoardLogging implements PointValueChangeListener, StatusChangeListener, TotalChangeListener, CoderMoveListener {

    /** reference to the logging category */
    private static final Category cat = Category.getInstance(ScoreBoardLogging.class.getName());

    public void updateTotal(TotalChangeEvent e) {
        cat.info("(UpdateTotal)[" + e.toString());
    }

    public void updatePointValue(PointValueChangeEvent e) {
        cat.info("(UpdatePointValue)[" + e.toString());
    }

    public void updateStatus(StatusChangeEvent e) {
        cat.info("(UpdateStatus)[" + e.toString());
    }

    public void problemOpened(CoderMoveEvent e) {
        cat.info("(ProblemOpened)[" + e.toString());
    }

    public void problemClosed(CoderMoveEvent e) {
        cat.info("(ProblemClosed)[" + e.toString());
    }

}


/* @(#)ScoreBoardLogging.java */
