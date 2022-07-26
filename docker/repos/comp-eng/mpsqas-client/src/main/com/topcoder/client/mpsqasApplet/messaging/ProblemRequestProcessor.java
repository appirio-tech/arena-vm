package com.topcoder.client.mpsqasApplet.messaging;

import java.util.ArrayList;

import com.topcoder.netCommon.mpsqas.ComponentInformation;
import com.topcoder.netCommon.mpsqas.ProblemInformation;
import com.topcoder.shared.problem.Problem;
import com.topcoder.shared.problem.ProblemComponent;

/**
 * Interface for Problem Request Processor.
 *
 * @author mitalub
 */
public interface ProblemRequestProcessor {

    public void submitProblem(ProblemInformation problemInformation);

    public void saveStatement(ProblemComponent component);

    public void saveStatement(Problem problem);

    public void generatePreview(ProblemComponent component);

    public void generatePreview(Problem problem);

    public void saveAdminProblemInfo(int status, int primarySolutionId,
            ArrayList testerIds);
    
    public void generateWriterPayment(int coderId, double amount, int roundId);
    
    public void generateTesterPayment(int coderId, double amount, int roundId);

    public void submitPendingReply(boolean accepted, String message);

    public void saveComponent(ComponentInformation info);

    public void cancelTests();
}
