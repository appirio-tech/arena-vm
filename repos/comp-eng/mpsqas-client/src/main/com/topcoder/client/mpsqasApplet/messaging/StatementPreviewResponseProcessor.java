package com.topcoder.client.mpsqasApplet.messaging;

import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.Problem;

/**
 * Interface for statement preview response processors.
 *
 * @author mitalub
 */
public interface StatementPreviewResponseProcessor {

    public void processStatementPreview(ProblemComponent component);

    public void processStatementPreview(Problem problem);
}
