package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.server.common.ServerContestConstants;
import com.topcoder.server.mpsqas.room.MainProblemRoom;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.message.JumpRequest;
import com.topcoder.netCommon.mpsqas.communication.message.ViewProblemMoveResponse;
import com.topcoder.netCommon.mpsqas.communication.message.ViewLongProblemMoveResponse;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.netCommon.mpsqas.MessageConstants;
import com.topcoder.netCommon.mpsqas.ApplicationConstants;
import com.topcoder.netCommon.mpsqas.ProblemInformation;
import com.topcoder.shared.problem.ProblemComponent;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 *
 * @author Logan Hanks
 */
public class JumpRequestImpl
        extends JumpRequest
        implements MessageProcessor, MainProblemRoom {

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        if (!mpeer.isLoggedIn()) {
            Logger logger = Logger.getLogger(getClass());

            logger.error("Unauthorized user attempted upcoming contests room move");
            mpeer.sendErrorMessage("Not logged in");
            return;
        }

        mpeer.moveToNewRoom(this);
    }

    public void enter(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;

        try {
            ArrayList problems = new ArrayList();
            if (mpeer.isAdmin()) {
                problems.addAll(mpeer.getProblemServices().getSingleProblems(MessageConstants.ALL_PROBLEMS, mpeer.getUserId()));
                problems.addAll(mpeer.getProblemServices().getLongProblems(MessageConstants.ALL_PROBLEMS, mpeer.getUserId()));
            }else{
                if (mpeer.isTester()) {
                    problems.addAll(mpeer.getProblemServices().getSingleProblems(MessageConstants.USER_TESTING_PROBLEMS, mpeer.getUserId()));
                    problems.addAll(mpeer.getProblemServices().getLongProblems(MessageConstants.USER_TESTING_PROBLEMS, mpeer.getUserId()));
                }
                if (mpeer.isWriter()) {
                    problems.addAll(mpeer.getProblemServices().getSingleProblems(MessageConstants.USER_WRITTEN_PROBLEMS, mpeer.getUserId()));
                    problems.addAll(mpeer.getProblemServices().getLongProblems(MessageConstants.USER_WRITTEN_PROBLEMS, mpeer.getUserId()));
                }
            }
            String name = null;
            int problemID = -1;
            for(int i = 0; i<problems.size(); i++){
                ProblemInformation pi = (ProblemInformation)problems.get(i);
                ProblemComponent pc = pi.getProblemComponents()[0];
                String cn = pc.getClassName();
                if(cn.matches(getPattern()) && (name == null || cn.compareTo(name) > 0)){
                    name = cn;
                    problemID = pi.getProblemId();
                }
            }
            if(problemID == -1){
                mpeer.sendErrorMessage("No match found for "+getPattern());
                return;
            }
            ProblemInformation p = mpeer.getProblemServices()
                    .getProblemInformation(problemID,
                            mpeer.getUserId());
            if (p == null) {
                mpeer.sendErrorMessage("Could not retrieve problem information. "
                        + "Do you have permission to view it?");
            } else {
                mpeer.setCurrentProblemId(problemID);
                mpeer.setCurrentComponentId(p.getProblemComponents()[0]
                        .getComponentId());
                if (p.getProblemTypeID() == ServerContestConstants.SINGLE_PROBLEM) {
                    ViewProblemMoveResponse response = new ViewProblemMoveResponse(p,
                            p.getUserType() != ApplicationConstants.PROBLEM_TESTER);
                    mpeer.sendMessage(response);
                } else if (p.getProblemTypeID() == ServerContestConstants.LONG_PROBLEM) {
                    ViewLongProblemMoveResponse response = new ViewLongProblemMoveResponse(p,
                            p.getUserType() != ApplicationConstants.PROBLEM_TESTER);
                    mpeer.sendMessage(response);
                }
            }
        } catch (Exception e) {
            mpeer.sendErrorMessage("Error retrieving pattern", e);
        }
    }
}

