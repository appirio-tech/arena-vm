package com.topcoder.client.spectatorApp.messages;

import java.io.Serializable;
import com.topcoder.client.spectatorApp.CommonRoutines;

public class ShowScreen implements Serializable {
	
	private int[] Screens;
	
	public ShowScreen() {		
	}
	
	public ShowScreen(int[] Screens) {
		this.Screens = Screens;
	}

	public int[] getScreens() {
		return Screens;
	}

	public void setScreens(int[] Screens) {
		this.Screens = Screens;
	}
	
	@Override
	public String toString() {
		return "(ShowScreen) [" + CommonRoutines.prettyPrint(Screens) + "]";
	}
}
