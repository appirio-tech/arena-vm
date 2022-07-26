package com.topcoder.server.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Category;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.AdminBroadcast;
import com.topcoder.netCommon.contestantMessages.ComponentBroadcast;
import com.topcoder.netCommon.contestantMessages.RoundBroadcast;
import com.topcoder.netCommon.contestantMessages.response.BaseResponse;
import com.topcoder.netCommon.contestantMessages.response.GetAdminBroadcastResponse;
import com.topcoder.netCommon.contestantMessages.response.SingleBroadcastResponse;
import com.topcoder.server.common.ResponseEvent;
import com.topcoder.server.common.Round;
import com.topcoder.server.common.TCEvent;
import com.topcoder.server.common.User;
import com.topcoder.server.services.CoreServices;
import com.topcoder.server.services.EventService;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.ProblemComponent;

/**
 * <p>Title: AdminBroadcastManager</p>
 * <p>Description: Handles admin broadcast logic.</p>
 * @author EtherMage
 */


public class AdminBroadcastManager {

    private Category s_trace = Category.getInstance(AdminBroadcastManager.class.getName());

    // ******* Methods extracted to allow for unit testing ******
    protected void sendResponses(int target_type, int target, BaseResponse[] responses) {
        for (int r = 0; r < responses.length; r++) {
            if (responses[r] == null) continue;
            ResponseEvent responseEvent = new ResponseEvent(target_type, target, responses[r]);
            EventService.sendGlobalEvent(responseEvent);
        }
    }

    protected Round getRound(int roundID) {
        return CoreServices.getContestRound(roundID);
    }

    protected long getCurrentTime() {
        return CoreServices.getCurrentDBTime();
    }

    protected ProblemComponent getComponent(int componentID) {
        return CoreServices.getComponent(componentID);
    }

    protected void cacheNewBroadcast(AdminBroadcast broadcast, int senderID) {
        CoreServices.addNewBroadcast(broadcast, senderID);
    }

    protected void sendBroadcastsTo(Integer connectionID, ArrayList broadcasts) {
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("Sending GET_ADMIN_BROADCAST_RS to connection #" + connectionID);
        }
        GetAdminBroadcastResponse broadcastResponse = new GetAdminBroadcastResponse(broadcasts);
        ResponseProcessor.process(connectionID, broadcastResponse);
    }

    protected Collection getBroadcasts(Set roundIDs) {
        return CoreServices.getBroadcasts(roundIDs);
    }

    protected boolean isConnectionRegistered(int roundID, Integer connectionID) {
        return Processor.isConnectionRegistered(roundID, connectionID);
    }

    protected User getUser(int user_id) {
        return CoreServices.getUser(user_id, false);
    }


    public void sendGenericBroadcast(int senderID, String broadcastMessage) throws BadBroadcastException {
        doBroadcast(broadcastMessage, ContestConstants.BROADCAST_TYPE_ADMIN_GENERIC, -1, -1, senderID);
    }

    public void sendRoundBroadcast(int senderID, int roundID, String broadcastMessage) throws BadBroadcastException {
        doBroadcast(broadcastMessage, ContestConstants.BROADCAST_TYPE_ADMIN_ROUND, roundID, -1, senderID);
    }

    public void sendComponentBroadcast(int senderID, int roundID, int componentID, String broadcastMessage) throws BadBroadcastException {
        doBroadcast(broadcastMessage, ContestConstants.BROADCAST_TYPE_ADMIN_COMPONENT, roundID, componentID, senderID);
    }

    /**
     * John Waymouth & Mike Cervantes: we had to change the signature to accept a roundID and a componentID, because
     * multiple rounds can now be run at once.
     */
    protected void doBroadcast(String broadcastMessage, int broadcastType, int roundID, int componentID, int senderID) throws BadBroadcastException {
        int target_type;
        BaseResponse[] responses;
        int target;
        switch (broadcastType) {
        case ContestConstants.BROADCAST_TYPE_ADMIN_GENERIC:
            {
                responses = new BaseResponse[]{createBroadcast(broadcastMessage, broadcastType, null, componentID, -1, -1, senderID, true)};
                target_type = TCEvent.ALL_TARGET;
                target = -1;
                break;
            }
        case ContestConstants.BROADCAST_TYPE_ADMIN_ROUND:
            {
                Round round = CoreServices.getContestRound(roundID);
                if (round == null) {
                    throw new BadBroadcastException("Bad round ID: " + roundID);
                } else if (!round.isActive()) {
                    throw new BadBroadcastException("Broadcast failed: round " + roundID + " is not active.");
                }
                responses = new BaseResponse[]{createBroadcast(broadcastMessage, broadcastType, round, componentID, -1, -1, senderID, true)};
                target_type = TCEvent.ROUND_TARGET;
                target = roundID;
                break;
            }
        case ContestConstants.BROADCAST_TYPE_ADMIN_COMPONENT:
            {
                Round cr = CoreServices.getContestRound(roundID);
                if (cr == null) {
                    throw new BadBroadcastException("Bad round ID: " + roundID);
                } else if (!cr.isActive()) {
                    throw new BadBroadcastException("Broadcast failed: round " + roundID + " is not active.");
                }
                boolean foundComponent = false;
                SingleBroadcastResponse div1Broadcast = null;
                BaseResponse div2Broadcast = null;
                List componentIDs = cr.getDivisionComponents(1);
                if (componentIDs.contains(new Integer(componentID))) {
                    foundComponent = true;
                    div1Broadcast = createBroadcast(broadcastMessage, broadcastType, cr, componentID, -1, 1, senderID, true);
                }
                componentIDs = cr.getDivisionComponents(2);
                if (componentIDs.contains(new Integer(componentID))) {
                    foundComponent = true;
                    div2Broadcast = createBroadcast(broadcastMessage, broadcastType, cr, componentID, -1, 2, senderID, true);
                }
                if (!foundComponent) {
                    throw new BadBroadcastException("Component ID " + componentID + " not found in round " + roundID);
                }
                responses = new BaseResponse[]{div1Broadcast, div2Broadcast};
                target_type = TCEvent.ROUND_TARGET;
                target = roundID;
                break;
            }
        default:
            throw new BadBroadcastException("Bad broadcast type!");
        }
        sendResponses(target_type, target, responses);
    }

    public void populateRoundBroadcast(RoundBroadcast roundBroadcast) {
        Round round = getRound(roundBroadcast.getRoundID());
        if (round == null) {
            throw new IllegalStateException("Bad round ID in populateRoundBroadcast!");
        }
        roundBroadcast.setRoundName(round.getDisplayName());
    }

    public void populateComponentBroadcast(ComponentBroadcast componentBroadcast, int division) {
        populateRoundBroadcast(componentBroadcast);
        ProblemComponent component = getComponent(componentBroadcast.getComponentID());
        if (component == null) {
            throw new IllegalStateException("Bad component ID in populateProblemBroadcast!");
        }
        Round contest = getRound(componentBroadcast.getRoundID());
        if (contest == null) {
            throw new IllegalStateException("Bad round ID in populateProblemBroadcast!");
        }

        componentBroadcast.setClassName(component.getClassName());
        String methodSignature = component.getReturnType(ContestConstants.JAVA) + " " + component.getMethodName() + "(";
        DataType[] paramTypes = component.getParamTypes();
        String[] paramNames = component.getParamNames();

        //TODO fix this so it sends out the DataType object
        for (int i = 0; i < paramTypes.length; i++) {
            DataType paramType = paramTypes[i];
            String paramName = paramNames[i];
            methodSignature += paramType.getDescriptor(ContestConstants.JAVA) + " " + paramName + ", ";
        }
        methodSignature = methodSignature.substring(0, methodSignature.length() - 2) + ");";
        componentBroadcast.setMethodSignature(methodSignature);
        componentBroadcast.setPointValue(contest.getRoundComponentPointVal(componentBroadcast.getComponentID(), division));
        componentBroadcast.setDivision(division);
        componentBroadcast.setReturnType(component.getReturnType(ContestConstants.JAVA));
    }

    /**
     * Creates a broadcast Response event to be sent for a specific admin broadcast.  If cacheBroadcast is true, adds
     * the broadcast to the database.
     */
    protected SingleBroadcastResponse createBroadcast(String message, int broadcastType, Round round, int componentID, long broadcastTime, int division,
            int senderID, boolean cacheBroadcast) {
        AdminBroadcast broadcast = null;
        switch (broadcastType) {
        case ContestConstants.BROADCAST_TYPE_ADMIN_GENERIC:
            broadcast = new AdminBroadcast();
            break;
        case ContestConstants.BROADCAST_TYPE_ADMIN_ROUND:
            {
                RoundBroadcast rb = new RoundBroadcast();
                rb.setRoundID(round.getRoundID());
                populateRoundBroadcast(rb);
                broadcast = rb;
                break;
            }
        case ContestConstants.BROADCAST_TYPE_ADMIN_COMPONENT:
            {
                ComponentBroadcast componentBroadcast = new ComponentBroadcast();
                componentBroadcast.setRoundID(round.getRoundID());
                componentBroadcast.setComponentID(componentID);
                populateComponentBroadcast(componentBroadcast, division);
                broadcast = componentBroadcast;
                break;
            }
        }
        ;
        if (broadcast == null) {
            throw new IllegalArgumentException("Bad parameters to createBroadcast!");
        }
        broadcast.setMessage(message);
        broadcast.setTime(broadcastTime == -1 ? getCurrentTime() : broadcastTime);
        if (cacheBroadcast) {
            cacheNewBroadcast(broadcast, senderID);
        }
        if (broadcast.getType() == ContestConstants.BROADCAST_TYPE_ADMIN_COMPONENT) {
            ComponentBroadcast componentBroadcast = (ComponentBroadcast) broadcast;
            if (componentBroadcast.getPointValue() == -1) return null;
        }
        return new SingleBroadcastResponse(broadcast);
    }

    public void sendRecentBroadcasts(Integer connectionID, int user_id) {
        User user = getUser(user_id);
        if (user == null)
            throw new IllegalArgumentException("Bad user ID " + user_id + "in sendRecentBroadcasts()!");
        Set roundIDs = null;
        if (!user.isLevelTwoAdmin()) {
            roundIDs = new HashSet();
            Round[] contests = CoreServices.getAllActiveRounds();
            for (int i = 0; i < contests.length; i++) {
                Round contest = contests[i];
                if (contest != null) {
                    int currentRoundID = contest.getRoundID();
                    if (isConnectionRegistered(currentRoundID, connectionID)) {
                        roundIDs.add(new Integer(currentRoundID));
                    } // else -> leaves at -1, for generic broadcasts only
                }
            }
        }
        ArrayList broadcasts = new ArrayList(getBroadcasts(roundIDs));

        for (int i = 0; i < broadcasts.size(); i++) {
            AdminBroadcast adminBroadcast = (AdminBroadcast) broadcasts.get(i);
            if (adminBroadcast.getType() == ContestConstants.BROADCAST_TYPE_ADMIN_COMPONENT) { // only divI PB's stored
                ComponentBroadcast componentBroadcast = (ComponentBroadcast) adminBroadcast;
                if (componentBroadcast.getPointValue() == -1) {
                    broadcasts.remove(i);
                    i--;
                }
                if (componentBroadcast.getDivision() == 2) continue; // div2's, we just generated, skip 'em
                ComponentBroadcast newBroadcast = new ComponentBroadcast(componentBroadcast);
                populateComponentBroadcast(newBroadcast, 2); // generate div2 version from div1
                broadcasts.add(newBroadcast);
            }
        }
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("sendRecentBroadcasts: " + broadcasts.size() + " items for round " + roundIDs + " sent.");
        }
        sendBroadcastsTo(connectionID, broadcasts);
    }

    protected static final AdminBroadcastManager instance = new AdminBroadcastManager();

    public static AdminBroadcastManager getInstance() {
        return instance;
    }
}
