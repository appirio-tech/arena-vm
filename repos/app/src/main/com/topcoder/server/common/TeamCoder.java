/**
 * Class TeamCoder
 *
 * Author: Hao Kung
 *
 * Description: This class holds the data for an entire Team during a team com
 */
package com.topcoder.server.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.topcoder.netCommon.contest.ComponentAssignmentData;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.data.ComponentLabel;
import com.topcoder.server.services.CoreServices;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.problem.ProblemComponent;


public final class TeamCoder extends BaseCoder {

    private static Logger log = Logger.getLogger(TeamCoder.class);

    private Integer captainID;
    private Map memberCoders = new HashMap();
    private ComponentAssignmentData cad;
    private ArrayList m_componentIDs;
    private HashMap m_components;
    /** hashmap of componentId -> set of user ids who have opened it */
    private HashMap openedComponents;

    public TeamCoder(Team team, Round contest, int division, int roomID, int rating, int language) {
        super(team.getID(), team.getName(), division, contest, roomID, rating, language);
        setHistory(new TeamCoderHistory());
        captainID = new Integer(team.getCaptainID());
        cad = new ComponentAssignmentData(team.getID(), contest.getRoundID());
        openedComponents = new HashMap();
        addDivisionComponents(contest);
    }

    private void addDivisionComponents(Round contest) {
        int divisionID = getDivisionID();
        int id = getID();
        int language = getLanguage();
        ArrayList components = contest.getDivisionComponents(divisionID);
        m_componentIDs = new ArrayList();
        m_components = new HashMap();

        if (components != null) {
            m_components = new HashMap();
            for (int i = 0; i < components.size(); i++) {
                Long componentID = new Long(((Integer) components.get(i)).longValue());
                m_componentIDs.add(componentID);
                ProblemComponent component = CoreServices.getComponent(componentID.intValue());
                int pointValue = contest.getRoundComponentPointVal(component.getComponentId(), divisionID);
                BaseCoderComponent cc = newCoderComponent(id, component.getComponentId(), pointValue);
                cc.setLanguage(language);
                m_components.put(componentID, cc);
            }
        } else {
            throw new IllegalStateException("No components found for division: " + divisionID + " in addDivisionComponents()");
        }
    }

    public void setComponentAssignmentData(ComponentAssignmentData cad) {
        this.cad = cad;
    }

    public ComponentAssignmentData getComponentAssignmentData() {
        return cad;
    }

    public void addMemberCoder(Coder coder) {
        memberCoders.put(new Integer(coder.getID()), coder);
    }

    public Coder getMemberCoder(int coderID) {
        Integer key = new Integer(coderID);
        if (memberCoders.containsKey(key)) {
            return (Coder) memberCoders.get(key);
        } else {
            throw new IllegalArgumentException("Invalid member coder ID: " + coderID);
        }
    }

    public boolean isMemberCoder(int coderID) {
        Integer key = new Integer(coderID);
        return memberCoders.containsKey(key);
    }

    public Collection getMemberCoders() {
        return new Vector(memberCoders.values());
    }

    // called in a practice room to clear your stuff
    public synchronized void clearData() {
        for (Iterator it = memberCoders.values().iterator(); it.hasNext();) {
            Coder coder = (Coder) it.next();
            coder.clearData();
        }
        Object[] componentIDs = m_components.keySet().toArray();
        for (int i = 0; i < componentIDs.length; i++) {
            Long componentID = (Long) componentIDs[i];
            BaseCoderComponent oldComponent = (BaseCoderComponent) m_components.get(componentID);
            BaseCoderComponent newComponent = newCoderComponent(getID(), oldComponent.getComponentID(), oldComponent.getPointValue());
            newComponent.setLanguage(getLanguage());
            m_components.put(componentID, newComponent);
        }
        setHistory(new CoderHistory());
        setPoints(0);
        openedComponents = new HashMap();
    }

    //added 2-20 rfairfax
    public synchronized void clearProblem(Long componentId) {
        for (Iterator it = memberCoders.values().iterator(); it.hasNext();) {
            Coder coder = (Coder) it.next();
            coder.clearProblem(componentId);
        }

        BaseCoderComponent oldComponent = (BaseCoderComponent) m_components.get(componentId);
        int pntVal = oldComponent.getEarnedPoints();
        BaseCoderComponent newComponent = newCoderComponent(getID(), oldComponent.getComponentID(), oldComponent
                .getPointValue());
        newComponent.setLanguage(getLanguage());
        m_components.put(componentId, newComponent);

        setPoints(this.getPoints() - pntVal);
        // TODO: need to clear history ?

        openedComponents.put(componentId, null);
    }

    public int getNumComponents() {
        return m_componentIDs.size();
    }

    public BaseCoderComponent getComponent(long componentID) {
        Long key = new Long(componentID);
        if (m_components.containsKey(key)) {
            return (BaseCoderComponent) m_components.get(key);
        } else {
            throw new IllegalArgumentException("Invalid component ID: " + componentID);
        }
    }

    public long getEarliestComponentOpenTime() {
        long[] compIds = getComponentIDs();
        long compOpenTime = 0;
        long currOpenTime = 0;
        for (int i = 0; i < compIds.length; i++){
            if (isComponentOpened(compIds[i])) {
                BaseCoderComponent component = getComponent(compIds[i]);
                currOpenTime = component.getOpenedTime();
                if (compOpenTime == 0) compOpenTime = currOpenTime;
                if (currOpenTime < compOpenTime ) {
                    compOpenTime = currOpenTime;
                }
            }
        }

        return compOpenTime;
    }

    public boolean hasOpenedComponents() {
        boolean hasOpenedComponent = false;
        long[] compIds = getComponentIDs();

        for (int i = 0; i < compIds.length; i++){
            if (isComponentOpened(compIds[i])) {
                hasOpenedComponent = true;
            }
        }
        return hasOpenedComponent;
    }
    /** Set the status in the CoderComponent and add the user to the hash of people who have opened the component. */
    public void setOpenedComponent(long componentID) {
        int assignedUserID = cad.getAssignedUserForComponent((int) componentID);
        BaseCoderComponent problem = getComponent(componentID);

        if (openedComponents.get(new Long(componentID)) == null) {
            openedComponents.put(new Long(componentID), new HashSet());
        }
        ((HashSet) openedComponents.get(new Long(componentID))).add(new Integer(assignedUserID));

        if (problem.getStatus() == ContestConstants.NOT_OPENED) {
            problem.setStatus(ContestConstants.LOOKED_AT);
        }
    }

    /**
     * Sets the opened components hash map.
     */
    public void setOpenedComponents(HashMap openedComponents) {
        this.openedComponents = openedComponents;
    }

    /** Return true if this component has been opened by the assigned user */
    public boolean isComponentOpened(long componentID) {
        int assignedUserID = cad.getAssignedUserForComponent((int) componentID);
        if (openedComponents.get(new Long(componentID)) == null) {
            return false;
        }
        return ((HashSet) openedComponents.get(new Long(componentID))).contains(new Integer(assignedUserID));
    }

    protected final String getViewKey(int viewerID, String userName, int probId) {
        return viewerID + "|" + userName + "|" + probId;
    }

    // what problems is he looking at
    private HashMap m_viewing = new HashMap();

    public BaseCoderComponent getViewedComponent(int viewerID, String userName, int probId) {
        return (BaseCoderComponent) m_viewing.get(getViewKey(viewerID, userName, probId));
    }

    public void addViewedComponent(int viewerID, BaseCoderComponent view, String userName) {
        log.info(getName() + " is viewing " + userName + "'s " + view);
        m_viewing.put(getViewKey(viewerID, userName, view.getComponentID()), view);
        view.addViewer(CoreServices.getUser(viewerID, false).getName());
        if (log.isDebugEnabled()) {
            log.debug("After add ViewMap: " + m_viewing);
        }

    }

    public void closeViewedComponent(int viewerID, String userName, int probId) {
        if (log.isDebugEnabled()) {
            log.debug("Before close ViewMap: " + m_viewing);
        }
        BaseCoderComponent view = getViewedComponent(getID(), userName, probId);
        if (view != null) view.removeViewer(CoreServices.getUser(viewerID, false).getName());
        m_viewing.remove(getViewKey(viewerID, userName, probId));
    }

    public int getDefendantID() {
        return captainID.intValue();
    }

    public int getUserIDForComponent(int componentID) {
        return cad.getAssignedUserForComponent(componentID);
    }

    public boolean isComponentAssigned(long componentID) {
        int[] assignedComponents = cad.getAssignedComponents();
        for (int i = 0; i < assignedComponents.length; i++) {
            if (assignedComponents[i] == componentID) {
                return true;
            }
        }
        return false;
    }

    public boolean isCaptain(int userID) {
        return userID == captainID.intValue();
    }

    public long[] getComponentIDs() {
        Object[] ids = m_componentIDs.toArray();
        long[] ret = new long[ids.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = (long) ((Long) ids[i]).longValue();
        }
        return ret;
    }

    /**
     * Returns the component labels for the components the user id is assigned to.
     */
    public ComponentLabel[] getComponentLabels(int userID) {
        ArrayList componentIDs = new ArrayList();
        int[] allComponentIDs = cad.getAssignedComponents();
        for (int i = 0; i < allComponentIDs.length; i++) {
            if (cad.getAssignedUserForComponent(allComponentIDs[i]) == userID) {
                componentIDs.add(new Integer(allComponentIDs[i]));
            }
        }

        ComponentLabel[] labels = new ComponentLabel[componentIDs.size()];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = getComponentLabel(((Integer) componentIDs.get(i)).intValue(), getRoundID(), getDivisionID());
        }
        return labels;
    }

    private ComponentLabel getComponentLabel(int componentID, int roundID, int divisionID) {
        Round contestRound = CoreServices.getContestRound(roundID);
        return contestRound.getComponentLabel(divisionID, componentID);
    }

    public String toString() {
        return "TeamCoder[BaseCoder=" + super.toString() + "]";
    }

    public BaseCoderComponent newCoderComponent(int id, int componentId, int pointValue) {
        return new CoderComponent(id, componentId, pointValue);
    }
}
