package com.topcoder.client.mpsqasApplet.model.component;

import com.topcoder.shared.problem.*;

/**
 * abstract class for statement preview panel models.
 *
 * @author mitalub
 */
public abstract class StatementPreviewPanelModel extends ComponentModel {

    public abstract void setPreview(String preview);

    public abstract String getPreview();

    public abstract void setErrors(String errors);

    public abstract String getErrors();

    public abstract void setType(int type);

    public abstract int getType();

    public abstract void setProblem(Problem problem);

    public abstract Problem getProblem();

    public abstract void setProblemComponent(ProblemComponent component);

    public abstract ProblemComponent getProblemComponent();
}
