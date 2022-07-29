/**
 * HeaderPanel.java Description: Header panel to the scoreboard rooms
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.view;


import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import com.topcoder.client.spectatorApp.Constants;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.HeartBeatTimer;
import com.topcoder.client.spectatorApp.PhaseTracker;
import com.topcoder.client.spectatorApp.SpectatorApp;
import com.topcoder.client.spectatorApp.event.TimerEvent;
import com.topcoder.client.spectatorApp.event.TimerListener;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.LayeredPanel;
import com.topcoder.client.spectatorApp.widgets.SDividerLine;
import com.topcoder.client.spectatorApp.widgets.SImage;
import com.topcoder.client.spectatorApp.widgets.SMargin;
import com.topcoder.client.spectatorApp.widgets.SDesignDividerLine;

import com.topcoder.client.spectatorApp.widgets.STextField;
import java.awt.Color;

public class DesignHeaderPanel extends LayeredPanel implements AnimatePanel {

	/** Font used for the room title */
	private Font titleFont = FontFactory.getInstance().getFont(FontFactory.HANDEL_GOTHIC_BOLD, Font.PLAIN, 36);

        private static final String TIME = "TIME REMAINING";
        
	/** Layer for the background */
	private static final int DIVIDER_LAYER = 0;

	/** Layer for the message */
	private static final int IMAGE_LAYER = 1;

	/** Layer for the title */
	private static final int TITLE_LAYER = 2;

	/** Layer for the phase title */
	private static final int TIME_LAYER = 3;

	/** Layer for the timer layer */
	private static final int TIMER_LAYER = 4;

	/**
	 * The layer for the background /** Constructor
	 * 
	 * @param roomTitle
	 *           the title of the room
	 */
	public DesignHeaderPanel(String roomTitle) {
		SDesignDividerLine dividerLine = new SDesignDividerLine(true);
                SImage rightBackground = new SImage(Toolkit.getDefaultToolkit().getImage(SpectatorApp.class.getResource("tourneyLogoDesign.png")));
		SMargin titleMargin = new SMargin(new STextField(roomTitle, titleFont), new Insets(0, 0, 0, 0));
		SMargin timeMargin = new SMargin(new STextField(TIME, new Color(255,255,255), titleFont), new Insets(0, 0, 0, 0));
		
		// Add our layers
		addLayer(dividerLine);
		addLayer(rightBackground);
		addLayer(titleMargin);
		addLayer(timeMargin);
		addLayer(new TimerPanel());
		
		// Set the room title
		setRoomTitle(roomTitle);
	}

	/**
	 * Gets the room title
	 * 
	 * @return the room title
	 */
	public String getRoomTitle() {
		SMargin margin = (SMargin) getLayer(TITLE_LAYER);
		return ((STextField) margin.getWrappedPanel()).getText();
	}

	/**
	 * Sets the room title
	 * 
	 * @param roomTitle
	 *           the room title
	 */
	public void setRoomTitle(String roomTitle) {
		// Set the room title
		SMargin margin = (SMargin) getLayer(TITLE_LAYER);
		((STextField) margin.getWrappedPanel()).setText(roomTitle);
		
		// Figure out the minimum width
		int width = getLayer(TITLE_LAYER).getWidth() + getLayer(IMAGE_LAYER).getWidth();
		
		// Figure out the minimum height
		SDesignDividerLine dividerLine = (SDesignDividerLine) getLayer(DIVIDER_LAYER);
		int height = dividerLine.getDividerHeight();
		
		// Create the backbuffer
		setSize(width, height);
	}

	/**
	 * Sets the size of the panel
	 */
	public void setSize(int width, int height) {
		// Set the overal size (override the height with the background image's
		// height)
		super.setSize(width, height);
		
		// Get all the layers
		SDesignDividerLine dividerLine = (SDesignDividerLine) getLayer(DIVIDER_LAYER);
		AnimatePanel image = getLayer(IMAGE_LAYER);
		AnimatePanel title = getLayer(TITLE_LAYER);
		AnimatePanel time = getLayer(TIME_LAYER);
		AnimatePanel timer = getLayer(TIMER_LAYER);
		
		// Set the divider full width/height and subtract the divider line
		dividerLine.setSize(width, height);
		
		// Set the image to the right side
		image.setPosition(25, 18);
		
                int y = (dividerLine.getHeight() / 2) - (title.getHeight() / 2);
		// Set the title on the left centered vertically
		title.setPosition(image.getX() + image.getWidth() + 10, y);
		
		// Set the phase, time and timer to the image's position and size
		time.setPosition(width - 50 - timer.getWidth() - time.getWidth(), y);
		
		// Drop the timer down (note: can't use height because it's all upper
		// case)
		timer.setPosition(width - 50 - timer.getWidth() + 15, y);
	}

	/**
	 * Paints the panel
	 * 
	 * @param g2D
	 *           the graphics to paint with
	 * @returns the area that is volatile
	 */
	class TimerPanel extends LayeredPanel implements TimerListener {

		private STextField timerDesc;

		public TimerPanel() {
			// Create the desc fields
			timerDesc = new STextField(titleFont);
			// Add our phase/timer to the layers
			addLayer(timerDesc);
			// Set their positions
			timerDesc.setPosition(0, 0);
			// Register ourselves as a heartbeat listener
			HeartBeatTimer.getInstance().addTimerListener(this);
			// Force an update to get the text
			timerUpdate(new TimerEvent(this, HeartBeatTimer.getInstance().getTimeLeft()));
			// Set our size
			setSize(timerDesc.getWidth(), timerDesc.getHeight());
		}

		/** Disposes the timer lister */
		public void dispose() {
			HeartBeatTimer.getInstance().removeTimerListener(this);
		}

		/** Updates the text */
		public void timerUpdate(TimerEvent evt) {
			// Set the text
			timerDesc.setText(formatTime(evt.getTimeLeft()));
                        
                        //forced studio end time
		}

		/**
		 * Sets the time left. This method will build the text displayed.
		 * 
		 * @param timeLeft
		 *           the time left (in seconds)
		 */
		private final String formatTime(int timeLeft) {
			// If less then zero time - make it zero
			if (timeLeft < 0) timeLeft = 0;
			// Get the seconds
			int hh = timeLeft / 3600;
			timeLeft = timeLeft % 3600;
			// Get the minutes
			int mm = timeLeft / 60;
			timeLeft = timeLeft % 60;
			// Get the seconds
			int ss = timeLeft;
			// Create the timer buffer
			StringBuffer timerText = new StringBuffer(10);
			// If we have hours - add them to the buffer
			// Otherwise - suppress them fully
			if (hh > 0) {
				timerText.append(hh);
				timerText.append(":");
			}
			// Add the minutes with a leading zero if necessary
			if (mm < 10) timerText.append("0");
			timerText.append(mm);
			timerText.append(":");
			// Add the seconds with a leading zero if necessary
			if (ss < 10) timerText.append("0");
			timerText.append(ss);
			// Return the time left as a string
			return timerText.toString();
		}
	}
}
