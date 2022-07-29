package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;
import java.util.*;

/**
 *
 * @author Logan Hanks
 */
public class UpcomingContestsMoveResponse
        extends MoveResponse {

    private ArrayList contests;

    public UpcomingContestsMoveResponse() {
        this(new ArrayList());
    }

    public UpcomingContestsMoveResponse(ArrayList contests) {
        this.contests = contests;
    }

    public ArrayList getContests() {
        return contests;
    }

    public void addContest(ContestInformation contest) {
        contests.add(contest);
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeArrayList(contests);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        contests = reader.readArrayList();
    }
}
