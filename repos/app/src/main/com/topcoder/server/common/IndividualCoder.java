/**
 * Class Coder
 *
 * Author: Hao Kung
 *
 * Description: This class holds the data for a coder
 */
package com.topcoder.server.common;

import java.util.ArrayList;
import java.util.HashMap;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.util.logging.Logger;

public class IndividualCoder extends BaseCoder {
    private static Logger log = Logger.getLogger(IndividualCoder.class);

    public IndividualCoder(int userID, String uname, int div, Round contest, int roomID, int rating, int language) {
        super(userID, uname, div, contest, roomID, rating, language);
        addDivisionComponents(contest);
    }

    private void addDivisionComponents(Round contest) {
        int divisionID = getDivisionID();
        int id = getID();
        int language = getLanguage();
        ArrayList components = contest.getDivisionComponents(divisionID);
        m_componentIDs = new ArrayList();
        m_components = new HashMap();

        if (log.isDebugEnabled()) {
            log.debug("addDivisionComponents: DivID = " + divisionID + " Components = " + components);
        }

        if (components != null) {
            m_components = new HashMap();
            for (int i = 0; i < components.size(); i++) {
                Long componentID = new Long(((Integer) components.get(i)).longValue());
                m_componentIDs.add(componentID);
                int pointValue = contest.getRoundComponentPointVal( componentID.intValue(), divisionID);
                BaseCoderComponent cc = newCoderComponent(id, componentID.intValue(), pointValue);
                cc.setLanguage(language);
                m_components.put(componentID, cc);
            }
        } else {
            throw new IllegalStateException("No components found for division: " + divisionID + " in addDivisionComponents()");
        }
    }


    public BaseCoderComponent newCoderComponent(int coderId, int componentId, int pointValue) {
        return new CoderComponent(coderId, componentId, pointValue);
    }

    // called in a practice room to clear your stuff
    public void clearData() {
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
    }

    //added 2-20 rfairfax
    public void clearProblem(Long componentId)
    {
        BaseCoderComponent oldComponent = (CoderComponent) m_components.get(componentId);
        int pntVal = oldComponent.getEarnedPoints();
        BaseCoderComponent newComponent =  newCoderComponent(getID(), oldComponent.getComponentID(), oldComponent.getPointValue());
        newComponent.setLanguage(getLanguage());
        m_components.put(componentId, newComponent);

        setPoints(this.getPoints() - pntVal); 
        //TODO: need to clear history ?
    }

    public void setOpenedComponent(long componentID) {
        BaseCoderComponent problem = getComponent(componentID);
        if (problem.getStatus() == ContestConstants.NOT_OPENED) {
            problem.setStatus(ContestConstants.LOOKED_AT);
        }
    }

    public boolean isComponentOpened(long id) {
        // prolly want to remember that this dude opened this problem somewhere
        BaseCoderComponent problem = getComponent(id);
        return problem.getStatus() != ContestConstants.NOT_OPENED;
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

    private HashMap m_components = new HashMap();
    private ArrayList m_componentIDs = new ArrayList();

    public int getNumComponents() {
        return m_components.size();
    }

    public BaseCoderComponent getComponent(long id) {
        Long key = new Long(id);
        if (m_components.containsKey(key)) {
            if (log.isDebugEnabled()) {
                log.debug("Returning component id: " + id);
            }
            return (BaseCoderComponent) m_components.get(key);
        } else {
            throw new IllegalArgumentException("Invalid component ID: " + id);
        }
    }

    public long[] getComponentIDs() {
        Object[] ids = m_componentIDs.toArray();
        long[] ret = new long[ids.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = ((Long) ids[i]).longValue();
        }
        return ret;
    }

    public String toString() {
        return super.toString() +
                ", component=" + (m_components);
    }


    protected final String getViewKey(String userName, int probId) {
        return userName + "|" + probId;
    }

    // what problems are he looking at
    private HashMap m_viewing = new HashMap();

    public BaseCoderComponent getViewedComponent(int viewerID, String userName, int probId) {
        return (BaseCoderComponent) m_viewing.get(getViewKey(userName, probId));
    }

    public void addViewedComponent(int viewerID, BaseCoderComponent view, String userName) {
        if (log.isDebugEnabled()) {
            log.debug(getName() + " is viewing " + userName + "'s " + view);
        }
        m_viewing.put(getViewKey(userName, view.getComponentID()), view);
        view.addViewer(getName());
        if (log.isDebugEnabled()) {
            log.debug("After add ViewMap: " + m_viewing);
        }

    }

    public void closeViewedComponent(int viewerID, String userName, int probId) {
        if (log.isDebugEnabled()) {
            log.debug("Before close ViewMap: " + m_viewing);
        }
        BaseCoderComponent view = getViewedComponent(getID(), userName, probId);
        if (view != null) view.removeViewer(getName());
        m_viewing.remove(getViewKey(userName, probId));
    }

    public int getUserIDForComponent(int componentID) {
        return getID();
    }

    //	public int getProblemIndex( int id ) {
//	  for (int i=0;i<m_problems.length;i++) {
//		  //CoderComponent p;
//		  if (m_problems[i].getProblemID() == id) {
//			  return i;
//		  }
//	  }
//	  s_trace.error("Unable to find problem, returning 0 for index");
//	  return 0;
//	}

//	public CoderComponent getProblemByID( int id ) {
//	  for (int i=0;i<m_problems.length;i++) {
//	    //CoderComponent p;
//	    if (m_problems[i].getProblemID() == id) {
//	      return m_problems[i];
//	    }
//	  }
//	  s_trace.error("Unable to find problem, returning NULL, probably very bad!");
//	  return null;
//	}
}
