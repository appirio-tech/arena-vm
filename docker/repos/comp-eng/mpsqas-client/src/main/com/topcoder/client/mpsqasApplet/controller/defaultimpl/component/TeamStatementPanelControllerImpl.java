package com.topcoder.client.mpsqasApplet.controller.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.common.OpenMinSizeConstraint;
import com.topcoder.client.mpsqasApplet.common.OpenMaxSizeConstraint;
import com.topcoder.client.mpsqasApplet.common.OpenValidValuesConstraint;
import com.topcoder.client.mpsqasApplet.model.defaultimpl.component.TeamStatementPanelModelImpl;
import com.topcoder.client.mpsqasApplet.controller.component.TeamStatementPanelController;
import com.topcoder.client.mpsqasApplet.model.component.TeamStatementPanelModel;
import com.topcoder.client.mpsqasApplet.view.component.TeamStatementPanelView;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.view.component.ComponentView;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.util.Watchable;
import com.topcoder.netCommon.mpsqas.ProblemInformation;
import com.topcoder.shared.problem.*;

import java.util.*;

/**
 * Default implementation of TeamStatementPanelController .
 *
 * @author mitalub
 */
public class TeamStatementPanelControllerImpl
        extends TeamStatementPanelController {

    public final static int[] PARTS =
            {TeamStatementPanelModelImpl.DEFINITION,
             TeamStatementPanelModelImpl.INTRODUCTION};

    private TeamStatementPanelModel model;
    private TeamStatementPanelView view;

    public void init() {
    }

    public void close() {
    }

    public void setModel(ComponentModel model) {
        this.model = (TeamStatementPanelModel) model;
    }

    public void setView(ComponentView view) {
        this.view = (TeamStatementPanelView) view;
    }

    /**
     * Stores all the fields in the problem statement because focus changed and
     * the user may have changed something.  Keeps model up to date with view.
     */
    public void processStatementChange() {
        model.getProblemInformation().setName(view.getProblemName());
        model.getProblemInformation().setProblemText(view.getIntroduction());
    }

    /**
     * Makes the currently shown problem statement part match the selected
     * part in the combo box.
     */
    public void processPartSelection() {
        int index = view.getSelectedPart();
        model.setCurrentPart(PARTS[index]);
        model.notifyWatchers(UpdateTypes.STATEMENT_PART_CHANGE);
    }

    /**
     * Passes the notification to the component model's watchers.
     */
    public void update(Watchable w, Object arg) {
        model.notifyWatchers(arg);
    }
}
