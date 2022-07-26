package com.topcoder.client.spectatorApp.announcer.events;

import java.util.Arrays;
import com.topcoder.client.spectatorApp.messages.ShowScreen;

public class ShowScreenEvent extends AnnouncerEvent {

	private int[] screens;
	
	public ShowScreenEvent() {
	}
	
	@Override
	public Object getMessage() {
		return new ShowScreen(screens);
	}

	@Override
	public void validateEvent() throws Exception {
	}

	public int[] getScreens() {
		return screens;
	}

	public void setScreens(int[] screens) {
		this.screens = screens;
		Arrays.sort(screens);
	}
	
}
