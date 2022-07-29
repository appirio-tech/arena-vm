/*
 * LongCodeViewer
 * 
 * Created 06/14/2007
 */
package com.topcoder.client.contestApplet.uilogic.components;

import javax.swing.JFrame;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.uilogic.frames.FrameLogic;
import com.topcoder.client.contestApplet.uilogic.frames.SourceViewer;
import com.topcoder.client.contestApplet.uilogic.views.SourceViewerListener;
import com.topcoder.client.contestant.CoderComponent;
import com.topcoder.client.contestant.Contestant;
import com.topcoder.client.contestant.ProblemModel;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.language.BaseLanguage;
import com.topcoder.shared.language.JavaLanguage;

/**
 * @author Diego Belfer (mural)
 * @version $Id: LongCodeViewer.java 67261 2007-12-04 16:49:51Z thefaxman $
 */
public class LongCodeViewer implements SourceViewerListener {
    private ContestApplet ca;
    private Contestant model;
    private JFrame frame = null;
    private CoderComponent currentComponent;
    private ProblemModel currentProblemInfo;
    private SourceViewer src;
        
    private int roundId;
    private String handle;
    private int componentId;
    private boolean example;
    private int submissionNumber;
    private boolean pretty;
        
    private final CoderComponent.Listener myCoderComponentListener = new CoderComponent.Listener() {
            public void coderComponentEvent(CoderComponent coderComponent) {
                if (coderComponent.hasSourceCode()) {
                    if (currentProblemInfo == null) {
                        throw new IllegalStateException("Missing problem info");
                    }
                    src.clear();
                    src.setCode(coderComponent.getSourceCode(), coderComponent.getSourceCodeLanguage());
                    src.setTitle(
                                 coderComponent.getCoder().getHandle() + "'s " +
                                 coderComponent.getComponent().getClassName() + " " +
                                 (example ? "Example" : "Full") +" submission #"+submissionNumber+" (" +
                                 coderComponent.getSourceCodeLanguage().getName() +
                            ")"
                                 );
                } else {
                    //they tried to open a problem in a bad room, take the listener off
                    sourceViewerClosing();
                }
            }
        };

    private final ProblemModel.Listener myProblemModelListener = new ProblemModel.Listener() {
            public void updateProblemModel(final ProblemModel problemModel) {
                if (problemModel.hasProblemStatement()) {
                    ca.getInterFrame().hideMessage();
                    if (src == null) {
                        throw new IllegalStateException("Source viewer not initialized!");
                    }
                    src.setTitle(problemModel.getName());
                    src.show();
                    src.setProblem(problemModel);
                    src.setCoderComponent(currentComponent);
                    src.setWriter(currentComponent.getCoder().getHandle());
                    //empty out code, for default display
                    src.setCode("", BaseLanguage.getLanguage(JavaLanguage.ID));
                } else {
                    throw new IllegalStateException("Missing statement for problem " + problemModel);
                }
            }

            public void updateProblemModelReadOnly(ProblemModel problem) {
            }
        };

    public LongCodeViewer(ContestApplet ca, JFrame cr, int roundId, String handle, int componentId, boolean example, int submissionNumber, boolean pretty) {
        this.ca = ca;
        this.model = ca.getModel();
        this.frame = cr;
        this.roundId = roundId;
        this.handle = handle;
        this.componentId = componentId;
        this.example = example;
        this.submissionNumber = submissionNumber;
        this.pretty = pretty;
        this.currentComponent = model.getRound(roundId).getRoomByCoder(handle).getCoder(handle).getComponent(new Long(componentId));
    }

    public void show() {
        if (currentComponent.getStatus().intValue() > ContestConstants.NOT_OPENED) {
            createNewSourceViewer();
            currentProblemInfo = currentComponent.getComponent().getProblem();
            ca.setCurrentFrame(frame);
            // So we are notified when the stmt + code is updated
            currentComponent.addListener(myCoderComponentListener);
            currentProblemInfo.addListener(myProblemModelListener);
            ca.setCurrentFrame(frame);
            ca.getInterFrame().showMessage(
                                           "Fetching problem...",
                                           frame, ContestConstants.GET_SOURCE_CODE_REQUEST);
            ca.getModel().getRequester().requestSourceCode(getRoundId(), getHandle(), getComponentId(), isExample(), getSubmissionNumber(), isPretty());
        }
    }

    /**
     * Temporary fix to the focus problem. If focus is lost in the window,
     * normally the user would have to log out the browser and reload the
     * applet. Now the user just has to reload the problem, and a new coding
     * window will get created.
     */
    private void createNewSourceViewer() {
        close();
        src = new SourceViewer(ca, false, true);
        src.setPanel(this);
            
    }

    public void close() {
        if (src != null) {
            sourceViewerClosing();  // TODO - do we need to call this here?
            src.hide();
            src.dispose();
            src = null;
        }
    }

    public void sourceViewerClosing() {
        if (currentProblemInfo != null) {
            currentProblemInfo.removeListener(myProblemModelListener);
            currentProblemInfo = null;
        }
        if (currentComponent != null) {
            currentComponent.removeListener(myCoderComponentListener);
            currentComponent = null;
        }
    }

    public boolean isExample() {
        return example;
    }

    public String getHandle() {
        return handle;
    }

    public boolean isPretty() {
        return pretty;
    }

    public int getRoundId() {
        return roundId;
    }

    public int getSubmissionNumber() {
        return submissionNumber;
    }

    public int getComponentId() {
        return componentId;
    }

}
