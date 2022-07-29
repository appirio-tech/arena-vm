/**
 * GUIController.java Description: Handles changes to the GUI. Be very careful
 * here - the order of events in the code has been careful construed to avoid
 * synchronization problems (without the lag that would happen if we added
 * synchronization) between the dispatching threads and the gui thread...
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.controller;

import com.topcoder.client.spectatorApp.scoreboard.view.StudioContestRenderer;
import com.topcoder.client.spectatorApp.scoreboard.view.DesignContestRenderer;
import com.topcoder.client.spectatorApp.scoreboard.view.DevelopmentContestRenderer;
import com.topcoder.client.spectatorApp.scoreboard.view.NoHeaderScreenRenderer;
import com.topcoder.client.spectatorApp.scoreboard.view.StudioIndividualResultsRenderer;
import com.topcoder.client.spectatorApp.widgets.SImage;
import com.topcoder.client.spectatorApp.CommonRoutines;
import java.util.EventObject;
import org.apache.log4j.Category;
import com.topcoder.client.spectatorApp.PhaseTracker;
import com.topcoder.client.spectatorApp.SpectatorApp;
import com.topcoder.client.spectatorApp.netClient.SpectatorEventProcessor;
import com.topcoder.client.spectatorApp.scoreboard.model.RoomManager;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.SwitchRenderer;

import java.awt.Image;
import java.awt.Toolkit;

public class GUIController {
	/** reference to the logging category */
	private static final Category cat = Category.getInstance(GUIController.class.getName());

	/** The private instance of the object */
	private static GUIController guiController = null;

	/** The room handler */
	private final RoomHandler roomHandler = new RoomHandler(this);

	/** Handler for contest info */
	private final RoundHandler roundHandler = new RoundHandler(this);

	/** The team handler */
	private final TeamHandler teamHandler = new TeamHandler(this);

	/** Handler for bio definitions */
	private final AnnouncementHandler bioHandler = new AnnouncementHandler(this);

	/** Handler for component contest */
	private final ComponentHandler componentHandler = new ComponentHandler(this);
	
	/** Handler for the phase events */
	private final PhaseHandler phaseHandler = new PhaseHandler(this);

	/** whether painting is enabled or not */
	private boolean enablePainting = true;

	/** whether painting is enabled or not */
	private SwitchRenderer switchRenderer = new SwitchRenderer("", "");

	/** Time when a switch room message came in */
	private long switchRendererTime = 0;

	/** Current renderer */
	private AnimatePanel renderer = null;

	/** The last show event */
	private EventObject showEvent = null;

	/**
	 * Private constructor - implements the singleton pattern
	 */
	private GUIController() {
		// Add the listeners
		SpectatorEventProcessor.getInstance().addRoomListener(roomHandler);
		SpectatorEventProcessor.getInstance().addRoundListener(roundHandler);
		SpectatorEventProcessor.getInstance().addTeamListener(teamHandler);
		SpectatorEventProcessor.getInstance().addAnnounceCoderListener(bioHandler);
		SpectatorEventProcessor.getInstance().addPhaseListener(phaseHandler);
		SpectatorEventProcessor.getInstance().addComponentContestListener(componentHandler);
	}

	/**
	 * Returns the singleton instance of the heartbeattimer
	 * 
	 * @return GUIController
	 */
	public static synchronized GUIController getInstance() {
		if (guiController == null) guiController = new GUIController();
		return guiController;
	}

	/** Returns the switch rendering panel */
	SwitchRenderer getSwitchRenderer() {
		return this.switchRenderer;
	}

	/** Returns the current show event */
	EventObject getShowEvent() {
		return this.showEvent;
	}

	/** Sets the current show event */
	void setShowEvent(EventObject showEvent) {
		this.showEvent = showEvent;
	}

	/** Sets the switch renderer time */
	void setSwitchTime(long time) {
		this.switchRendererTime = time;
	}

	/**
	 * Enables/Disables painting
	 * 
	 * @param enable
	 *           enables the painting
	 */
	public void enablePainting(boolean enable) {
		enablePainting = enable;
	}

	/**
	 * Returns whether painting is enabled or not
	 * 
	 * @returns whether painting is enabled or not
	 */
	public boolean isPaintEnabled() {
		return enablePainting;
	}

	/**
	 * Disposes of all the stuff
	 */
	public void dispose() {
		// Remove the listeners
		SpectatorEventProcessor.getInstance().removeRoomListener(roomHandler);
		SpectatorEventProcessor.getInstance().removeRoundListener(roundHandler);
		SpectatorEventProcessor.getInstance().removeTeamListener(teamHandler);
		SpectatorEventProcessor.getInstance().removeAnnounceCoderListener(bioHandler);
		SpectatorEventProcessor.getInstance().removePhaseListener(phaseHandler);
		SpectatorEventProcessor.getInstance().removeComponentContestListener(componentHandler);
		
		// Dispose the room manager
		RoomManager.getInstance().dispose();
		
		// Dispose of the switch room
		switchRenderer.dispose();
		
		// If the renderer exists, dispose and null out
		if (renderer != null) {
			renderer.dispose();
			renderer = null;
		}
	}

	/**
	 * Switches the current panel back to the initial render (ie no renderer)
	 */
	public void showInitialRenderer() {
		setSwitchRenderer(null);
	}
	
	public void showImage(String path) {
        Image image = Toolkit.getDefaultToolkit().getImage(path);

        if (!CommonRoutines.loadImagesFully(new Image[]{image})) {
            cat.error("Loading image from '" + path + "' failed.");
        }

        setSwitchRenderer(new SImage(image));
	}
        
    public void showStudioIndResultsRenderer(String caption, byte[] image) {
        setSwitchRenderer(new StudioIndividualResultsRenderer(caption, image));
    }
       
    public void showStudioRenderer(String[] computerNames, String path, String[] handles, String title) {
        setSwitchRenderer(new StudioContestRenderer(computerNames, path,handles, title));
	}
	
	public void showDesignRenderer(String[] computerNames, String path, String[] handles, String title) {
        setSwitchRenderer(new DesignContestRenderer(computerNames, path, handles, title));
	}
	
	public void showNoHeaderScreen(String[] computerNames, String path, String[] handles, String title) {
        setSwitchRenderer(new NoHeaderScreenRenderer(computerNames, path, handles, title));
	}
	
	public void showDevelopmentRenderer(String[] computerNames, String path, String[] handles, String title) {
        setSwitchRenderer(new DevelopmentContestRenderer(computerNames, path, handles, title));
	}

	/**
	 * Switches the renderer to the new one
	 */
	void setSwitchRenderer(AnimatePanel newRenderer) {
		// Get reference to old renderer
		AnimatePanel currentRenderer = renderer;
		// Assign the new renderer
		renderer = newRenderer;
		// Dispose the old renderer
		if (currentRenderer != null) currentRenderer.dispose();
	}

	/** Returns the team handler */
	public TeamHandler getTeamHandler() {
		return teamHandler;
	}

	/** Returns the room handler */
	public RoomHandler getRoomHandler() {
		return roomHandler;
	}

	/** Returns the component handler */
	public ComponentHandler getComponentHandler() {
		return componentHandler;
	}

	/**
	 * Sets the display for the correct panel
	 */
	public AnimatePanel getPanel() {
		// Are we in a switch room mode?
		if (switchRendererTime > 0 && PhaseTracker.getInstance().isContestOngoing()) {
			// Has the room been shown less than two seconds
			if (switchRendererTime + SpectatorApp.getInstance().getMoveDelay() >= System.currentTimeMillis()) {
				// Yep - return the renderer
				return switchRenderer;
			} else {
				// Turn off the switch room
				switchRendererTime = 0;
			}
		}
		
		// Return the current renderer
		return renderer;
	}
}
