/*
 * User: Michael Cervantes
 * Date: Aug 26, 2002
 * Time: 12:13:42 AM
 */
package com.topcoder.client.contestApplet.frames;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import javax.swing.JFrame;

import com.topcoder.client.contestant.RoundModel;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.data.PhaseData;

public class RoundScheduleFrame extends MessageDialog {

    public RoundScheduleFrame(RoundModel round, JFrame parent) {
        super(parent, "Schedule - " + round.getDisplayName() , buildMessage(round), false, true);
    }

    private static String buildMessage(RoundModel round) {
        if (!round.hasSchedule()) {
            throw new IllegalStateException("Expected round to have schedule: " + round);
        }
        PhaseData[] schedule = (PhaseData[]) round.getSchedule().clone();
        Arrays.sort(schedule, new Comparator() {
            public int compare(Object o1, Object o2) {
                PhaseData lhs = (PhaseData) o1;
                PhaseData rhs = (PhaseData) o2;
                long d = lhs.getStartTime() - rhs.getStartTime();
                return d == 0 ? 0 : d < 0 ? -1 : 1;
            }

        });
        String r = "";
        DateFormat fmt = new SimpleDateFormat("EEE MMM d, h:mm a z");

        for (int i = 0; i < schedule.length; i++) {
            PhaseData phaseData = schedule[i];
            String phase = null;
            switch (phaseData.getPhaseType()) {
            case ContestConstants.REGISTRATION_PHASE:
                if (!hasAssignedTime(phaseData)) continue;
                phase = "REGISTRATION";
                break;
            case ContestConstants.CODING_PHASE:
                phase = "CODING";
                break;
            case ContestConstants.INTERMISSION_PHASE:
                if (!hasAssignedTime(phaseData)) continue;
                phase = "INTERMISSION";
                break;
            case ContestConstants.CHALLENGE_PHASE:
                if (!hasAssignedTime(phaseData)) continue;
                phase = "CHALLENGE";
                break;
            case ContestConstants.SYSTEM_TESTING_PHASE:
                phase = "SYSTEM TEST";
                break;
            }
            r += fmt.format(new Date(phaseData.getStartTime())) + "  " + phase + '\n';
        }
        return r;
    }

    private static boolean hasAssignedTime(PhaseData phaseData) {
        return phaseData.getEndTime() - phaseData.getStartTime() >= 0;
    }
}
