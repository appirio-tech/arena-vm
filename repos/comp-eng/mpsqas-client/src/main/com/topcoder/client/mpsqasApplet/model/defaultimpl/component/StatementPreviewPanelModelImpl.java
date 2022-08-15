package com.topcoder.client.mpsqasApplet.model.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.model.component.StatementPreviewPanelModel;
import com.topcoder.shared.problem.Problem;
import com.topcoder.shared.problem.ProblemComponent;

/**
 * Default implementation of StatementPreviewPanelModel.
 *
 * @author mitalub
 */
public class StatementPreviewPanelModelImpl extends StatementPreviewPanelModel {

    private String preview;
    private String errors;
    private Problem problem;
    private ProblemComponent component;
    private int type;

    public void init() {
        preview = "";
        errors = "";
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getPreview() {
        return preview;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public String getErrors() {
        return errors;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblemComponent(ProblemComponent component) {
        this.component = component;
    }

    public ProblemComponent getProblemComponent() {
        return component;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
