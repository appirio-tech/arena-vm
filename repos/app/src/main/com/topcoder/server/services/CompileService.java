/**
 * Class CompileService
 *
 * Author: Hao Kung
 *
 * Description: This class will contain all the static methods for use by
 * anyone who wants to send a compile message to the actual Compilers using JMS
 */

package com.topcoder.server.services;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import com.topcoder.netCommon.contest.round.RoundProperties;
import com.topcoder.server.common.ContestRound;
import com.topcoder.server.common.Results;
import com.topcoder.server.common.Submission;
import com.topcoder.server.common.SubmitResults;
import com.topcoder.server.ejb.DBServices.DBServicesLocator;
import com.topcoder.server.ejb.TestServices.TestServices;
import com.topcoder.server.ejb.TestServices.TestServicesLocator;
import com.topcoder.shared.util.logging.Logger;

// remove these once we can
//import common.contest.attr.Submission;
//import com.topcoder.netCommon.contest.ContestConstants;

public final class CompileService {
    /**
     * Category for logging.
     */
    private static final Logger s_trace = Logger.getLogger(CompileService.class);

    /* Static initialization block for the topic stuff */
    static {
        s_trace.debug("Initializing CompileServices...");
        try {
            ejbServerIsUp();
        } catch (Exception e) {
            error("", e);
        }
        s_trace.debug("CompileService initialization end...");
    }

    static void ejbServerIsUp() throws NamingException, CreateException, RemoteException {
        TestServicesLocator.getService();
        DBServicesLocator.getService();
    }

    private CompileService() {
    }

    static void init() {
    }


    /**
     * The returnResults method is for putting a submission object message on the topic
     * so that the results of compilation can be transferred back to the coder.
     *
     * @param sub              Submission object containing the users submission info
     * @param appletServerId   The applet server id whence the compile request came
     * @param socketServerId   The socket server id whence the compile request came
     * @return Submission	    Submission object containing compileStatus and compileError
     */
    /*
	////////////////////////////////////////////////////////////////////////////////
	public static void returnResults(Submission sub, int appletServerId, int socketServerId, long submitTime)
	////////////////////////////////////////////////////////////////////////////////
	throws RemoteException
	{
		s_trace.debug("In CompileServicesBean.returnResults()...");

		try {
			HashMap props = new HashMap();
			props.put("pendingAction", "C");
			props.put("appletServerId", new Integer(appletServerId));
			props.put("socketServerId", new Integer(socketServerId));
			props.put("type", new Integer(ContestConstants.COMPILE));
			props.put("submitTime", new Long(submitTime));

			m_msgPub.pubMessage(props, sub);
		} catch (Exception e) {
            error("",e);
		}

	}
    */

    /**
     * This method is called from the ejbRemove method, which is responsible for
     * cleaning up any open connections or free up any other system resources
     * that are no longer needed.
     */
    /*
	////////////////////////////////////////////////////////////////////////////////
	public static void cleanUp()
	////////////////////////////////////////////////////////////////////////////////
	{
		s_trace.debug("Cleaning Up CompileServicesBean...");
		if (m_msgPub != null) {
			m_msgPub.close();
			m_msgPub = null;
		}
		if (m_msgSender != null) {
			m_msgSender.close();
			m_msgSender = null;
		}
	}
    */

    private static void error(Object message, Throwable t) {
        s_trace.error(message, t);
    }

    public static SubmitResults replaySubmit(Submission sub) {
        info("Replay submit");
        try {
            return getTestService().replaySubmit(sub);
        } catch (Exception e) {
            s_trace.error("Error in replay submit", e);
        }
        return new SubmitResults(false, "", 0);
    }

    /**
     * Submits a problem
     */
    public static SubmitResults submit(Submission sub, ContestRound round) {
        try {
            long codingLength = round.getCodingLength();
            info("submit(), roundId=" + round.getRoundID() + ", codingLength=" + codingLength);
            RoundProperties props = round.getRoundProperties();
            SubmitResults results = getTestService().submitProblem(sub, codingLength, props.usesPerUserCodingTime());
            return results;
        } catch (Exception e) {
            s_trace.error("Exception in submit", e);
            return new SubmitResults(false, e.getMessage(), 0);
        }
    }

    /**
     * Saves a problem to the DB
     */
    public static Results saveComponent(int contestId, int roundId, long componentId,
            int coderId, String programText, int languageID) {
        info("saveComponent");
        try {
            // update the DB
            return getTestService().saveComponent(contestId, roundId, componentId, coderId, programText, languageID);
        } catch (Exception e) {
            s_trace.error("Failed to saveComponent", e);
        }
        return new Results(false, "Failed to save.");
    }

    /*
    public static final void replayCompile(Submission sub) {
         int  coderId  =  sub.getCoderId( );
         User user = CoreServices.getUser( coderId, false );
         boolean isTeam =  CoreServices.getProblem(sub.getComponent().getProblemID()).getProblemTypeID() == ContestConstants.TEAM_PROBLEM_TYPE_ID;

         // first send out the compile message here
         StringBuffer message = new StringBuffer();
         message.append( "System> " );
         message.append( user.getName() );
         message.append( " is compiling the " );
         if(isTeam) {
              message.append(sub.getComponent().getClassName());
              message.append(" component.\n" );
         } else {
              message.append( sub.getRoundPointVal() );
              message.append( "-point problem.\n" );
         }
         s_trace.debug( "Sending compile room message" );
         EventService.sendRoomSystemMessage( user.getRoomID(), message.toString() );

         // NOTE: We are assuming coderOpenProblem happened already!!!
         // this will take care of saving it to the db
         recordCompileStatus(sub);
    }
    */

    private static TestServices getTestService() throws RemoteException, NamingException, CreateException {
        return TestServicesLocator.getService();
    }

    private static void info(Object message) {
        s_trace.info(message);
    }
}
