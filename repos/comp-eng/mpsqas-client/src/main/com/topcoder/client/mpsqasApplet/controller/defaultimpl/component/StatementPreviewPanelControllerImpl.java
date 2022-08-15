package com.topcoder.client.mpsqasApplet.controller.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.controller.component.StatementPreviewPanelController;
import com.topcoder.client.mpsqasApplet.model.component.StatementPreviewPanelModel;
import com.topcoder.client.mpsqasApplet.view.component.StatementPreviewPanelView;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.view.component.ComponentView;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.common.ResponseClassTypes;
import com.topcoder.client.mpsqasApplet.messaging.StatementPreviewResponseProcessor;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.util.Watchable;
import com.topcoder.client.render.*;
import com.topcoder.netCommon.mpsqas.MessageConstants;
import com.topcoder.shared.problem.*;
import com.topcoder.shared.language.*;

import java.util.ArrayList;

/**
 * Default implementation of StatementPreviewPanelController .
 *
 * @author mitalub
 */
public class StatementPreviewPanelControllerImpl
        extends StatementPreviewPanelController
        implements StatementPreviewResponseProcessor {

    StatementPreviewPanelModel model;
    StatementPreviewPanelView view;

    public void init() {
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.PREVIEW_PROBLEM_STATEMENT);
    }

    public void close() {
        MainObjectFactory.getResponseHandler().unregisterResponseProcessor(this,
                ResponseClassTypes.PREVIEW_PROBLEM_STATEMENT);
    }

    public void setModel(ComponentModel model) {
        this.model = (StatementPreviewPanelModel) model;
    }

    public void setView(ComponentView view) {
        this.view = (StatementPreviewPanelView) view;
    }

    public void processGenerate() {
        MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                "Sending statement to server to generate preview...", false);

        if (model.getType() == MessageConstants.PROBLEM_STATEMENT) {
            MainObjectFactory.getProblemRequestProcessor().generatePreview(
                    model.getProblem());
        } else {
            MainObjectFactory.getProblemRequestProcessor().generatePreview(
                    model.getProblemComponent());
        }
    }

    public void processStatementPreview(ProblemComponent component) {
        if (component.isValid()) {
            if (view.getLanguage() != null) {
                String html = "";
                try {
                    html = new ProblemComponentRenderer(component).toHTML(view.getLanguage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                model.setPreview(html);

            } else {
                String html = "";
                try {
                    html = new ProblemComponentRenderer(component).toHTML(new JavaLanguage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                model.setPreview(html);
            }
        } else {
            model.setPreview("Errors.");
        }

        StringBuffer errors = new StringBuffer();
        ArrayList messages;

        messages = component.getMessages();
        for (int i = 0; i < messages.size(); i++) {
            errors.append(((ProblemMessage) messages.get(i)).getMessage());
            errors.append("\n\n");
        }
        model.setErrors(errors.toString());
        model.notifyWatchers(UpdateTypes.PREVIEW_CONTENTS);
    }

    public void processStatementPreview(Problem problem) {
        if (problem.isValid()) {
            if (view.getLanguage() != null) {
                String html = "";
                try {
                    html = new ProblemRenderer(problem).toHTML(view.getLanguage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                model.setPreview(html);
            } else {
                String html = "";
                try {
                    html = new ProblemRenderer(problem).toHTML(new JavaLanguage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                model.setPreview(html);
            }
        } else {
            model.setPreview("Errors.");
        }

        StringBuffer errors = new StringBuffer();
        ArrayList messages;

        for (int j = 0; j < problem.getProblemComponents().length; j++) {
            messages = problem.getProblemComponents()[j].getMessages();
            for (int i = 0; i < messages.size(); i++) {
                errors.append(((ProblemMessage) messages.get(i)).getMessage());
                errors.append("\n\n");
            }
        }
        model.setErrors(errors.toString());
        model.notifyWatchers(UpdateTypes.PREVIEW_CONTENTS);
    }

    /**
     * Passes the notification to the component model's watchers.
     */
    public void update(Watchable w, Object arg) {
        model.notifyWatchers(arg);
    }
}
