package com.topcoder.client.spectatorApp.controller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Category;

import com.topcoder.client.spectatorApp.PhaseTracker;
import com.topcoder.client.spectatorApp.event.RoomAdapter;
import com.topcoder.client.spectatorApp.event.ShowRoomEvent;
import com.topcoder.client.spectatorApp.scoreboard.model.Contest;
import com.topcoder.client.spectatorApp.scoreboard.model.Room;
import com.topcoder.client.spectatorApp.scoreboard.model.RoomManager;
import com.topcoder.client.spectatorApp.scoreboard.model.Round;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.netCommon.contest.ContestConstants;

/** Class handling the show room messages */
class RoomHandler extends RoomAdapter {
	/** reference to the logging category */
	private static final Category cat = Category.getInstance(RoomHandler.class.getName());

	/** Parent controller */
	private final GUIController controller;

	/** Constructor */
	RoomHandler(GUIController controller) {
		this.controller = controller;
	}

	public void showRoom(ShowRoomEvent evt) {
		// Get the switch renderer object
		Object someObject = controller.getSwitchRenderer().getSomeObject();
		boolean roomMatch = false;
		if (someObject != null && someObject instanceof ShowRoomEvent) {
			ShowRoomEvent roomEvent = (ShowRoomEvent) someObject;
			// Check the room and coder matching
			roomMatch = roomEvent.getRoomID() == evt.getRoomID();
		}
		
		// If both the room and coder match - return
		if (roomMatch) return;
		
		// Get the room associated with the roomID
		Room room = RoomManager.getInstance().getRoom(evt.getRoomID());
		if (room == null) {
			cat.info("RoomID: " + evt.getRoomID() + " has not been defined yet");
			return;
		}
		
		// Get the parent round
		Round round = room.getRound();
		if (round == null) {
			cat.info("RoundID: " + room.getRoundID() + " has not been defined yet");
			return;
		}
		
		// Get the contest
		Contest contest = round.getContest();
		if (contest == null) {
			cat.info("ContestID: " + round.getContestID() + " has not been defined yet");
			return;
		}
		
		// Determine the new renderer
		AnimatePanel newRenderer = getRenderer(PhaseTracker.getInstance().getPhaseID(), contest, room);
		
		// Not found...
		if (newRenderer == null) {
			// Save the show room event
			controller.setShowEvent(evt);
			return;
		}
		
		// Room switch?
		if (someObject != null && !roomMatch) {
			controller.getSwitchRenderer().setMoveMessage(room.getRoomTitle(), contest.getContestName(), "Moving to " + room.getRoomTitle());
			controller.setSwitchTime(System.currentTimeMillis());
		}
		
		// Store the object into the switchRenderer
		controller.getSwitchRenderer().setSomeObject(evt);
		
		// Switch the renderer
		controller.setSwitchRenderer(newRenderer);
		
		// Set the last show event
		controller.setShowEvent(evt);
	}

	/** Returns the panel for 4 or less coders */
	public AnimatePanel getRenderer(int phaseID, Contest contest, Room room) {
		switch (phaseID) {
			// Coding phase
			case ContestConstants.CODING_PHASE:
                                //cat.info("TYPE: " + room.getRound().getRoundType());
				return getPanelClass("CodingRenderer", room.getRound().getRoundType(), new Object[] {room.getRoomTitle(), contest.getContestName(), room.getPointTracker()});
				
			// Intermission phase
			case ContestConstants.INTERMISSION_PHASE:
				return getPanelClass("IntermissionRenderer", room.getRound().getRoundType(), new Object[] {room.getRoomTitle(), contest.getContestName(), room.getPointTracker()});
				
			// Challenge phase
			case ContestConstants.CHALLENGE_PHASE:
				return getPanelClass("ChallengeRenderer", room.getRound().getRoundType(), new Object[] {room.getRoomTitle(), contest.getContestName(), room.getPointTracker(), room.getCoderTracker()});
				
			// System testing phase
			case ContestConstants.SYSTEM_TESTING_PHASE:
				return getPanelClass("SystemTestRenderer", room.getRound().getRoundType(), new Object[] {room.getRoomTitle(), contest.getContestName(), room.getPointTracker()});
				
			// End contest phase
			case ContestConstants.CONTEST_COMPLETE_PHASE:
				return getPanelClass("SystemTestDoneRenderer", room.getRound().getRoundType(), new Object[] {room.getRoomTitle(), contest.getContestName(), room.getPointTracker()});
		}
		
		// Unknown
		return null;
	}

	private AnimatePanel getPanelClass(String className, int roundTypeID,  Object[] parms)
	{
		// Get the prefix for the specific class
		String prefix = System.getProperty("com.topcoder.client.spectatorApp.controller.prefix." + className, null);
		
		// If not found, get the generic prefix
		if (prefix == null) {
			prefix = System.getProperty("com.topcoder.client.spectatorApp.controller.prefix.generic", "Compressed");
		}
        
        if(ContestConstants.isLongRoundType(new Integer(roundTypeID))) {
            className = "Long" + className;
        }

		// Create the name we are looking for
		String fullName = "com.topcoder.client.spectatorApp.scoreboard.view." + prefix + className;
		try {
			// Load the class
			Class clazz = this.getClass().getClassLoader().loadClass(fullName);
			
			// Create the class parameters
			Class[] c = new Class[parms.length];
			for (int x = 0; x < parms.length; x++) c[x] = parms[x].getClass();
			
			// Find the constructor
			Constructor clazzConstructor = clazz.getConstructor(c);
			if (clazzConstructor == null) {
				cat.warn("Could not find a matching constructor for " + fullName);
				return null;
			} else {
				// Create the instance and return it
				return (AnimatePanel) clazzConstructor.newInstance(parms);
			}
		} catch (ClassNotFoundException e) {
			cat.warn("Could not load the class " + fullName);
			logError(e);
			return null;
		} catch (SecurityException e) {
			cat.warn("Could not load the class " + fullName);
			logError(e);
			return null;
		} catch (NoSuchMethodException e) {
			cat.warn("Could not load the class " + fullName);
			logError(e);
			return null;
		} catch (IllegalArgumentException e) {
			cat.warn("Could not load the class " + fullName);
			logError(e);
			return null;
		} catch (InstantiationException e) {
			cat.warn("Could not load the class " + fullName);
			logError(e);
			return null;
		} catch (IllegalAccessException e) {
			cat.warn("Could not load the class " + fullName);
			logError(e);
			return null;
		} catch (InvocationTargetException e) {
			cat.warn("Could not load the class " + fullName);
			logError(e);
			return null;
		}
	}
	private void logError(Throwable e) {
		if (e == null) return;
		cat.error(e.toString(), e);
//		if (e.getMessage() != null) cat.error(e.getMessage());
//		for(StackTraceElement elem : e.getStackTrace()) {
//			cat.error(elem.toString());
//		}
		logError(e.getCause());
	}
}
