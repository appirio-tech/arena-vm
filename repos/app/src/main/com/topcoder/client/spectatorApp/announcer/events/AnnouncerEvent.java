package com.topcoder.client.spectatorApp.announcer.events;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.announcer.properties.PropertyPanel;
import com.topcoder.client.spectatorApp.announcer.properties.PropertyPanelFactory;

/**
 * The event representation
 */
public abstract class AnnouncerEvent {
	/** The event title */
	private String title;

	/** Send the round information */
	public abstract Object getMessage();

	/**
	 * Returns the title of this event
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of the event
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/** Return the title of the event */
	public String toString() {
		return title;
	}

	/** Validates the event after it was read */
	public abstract void validateEvent() throws Exception;

	/**
	 * Returns the property panel that can represent this event.
	 * 
	 * @return the property panel
	 */
	public PropertyPanel getPropertyPanel() {
		return PropertyPanelFactory.getPropertyPanel(this);
	}

	/**
	 * Helper method to retrieve the image specified by a Filename as an array of
	 * bytes
	 * 
	 * @param filename
	 *           the Filename to load the image data from
	 * @return return the image data as an array of bytes. Will be null if an
	 *         error occured.
	 */
	protected static byte[] getImage(String filename) throws Exception {
		if (filename.trim().length() == 0) return null;
		try {
			// Create the file
			File file = new File(filename);
			if (!file.exists()) {
				throw new Exception("File '" + filename + "' does not exist");
			}
			// Create an array to hold it
			byte[] imageData = new byte[(int) file.length()];
			// Read it into the array
			new FileInputStream(file).read(imageData);
			// Verify its a good file
			Image temp = Toolkit.getDefaultToolkit().createImage(imageData);
			if (temp == null) {
				throw new Exception("The image " + filename + " does not appear to be a valid image format (jpg, gif, png)");
			}
			// Make double sure we loaded it
			if (!CommonRoutines.loadImagesFully(new Image[] { temp })) {
				throw new Exception("The image " + filename + " could not be loaded for who knows what reason");
			}
			return imageData;
		} catch (IOException e) {
			throw e;
		}
	}
}
