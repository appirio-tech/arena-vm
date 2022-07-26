package com.topcoder.client.contestApplet.uilogic.views;

import java.util.ArrayList;

import com.topcoder.client.contestApplet.uilogic.frames.FrameLogic;
import com.topcoder.client.contestant.CoderComponent;

public interface ChallengeViewLogic extends SourceViewerListener {
    void doChallenge(String writer, CoderComponent coderComponent, FrameLogic parentFrame);
    void setOldArgs(ArrayList args, int compID);
}
