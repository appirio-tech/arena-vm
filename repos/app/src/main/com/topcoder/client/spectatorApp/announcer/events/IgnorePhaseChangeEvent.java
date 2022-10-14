package com.topcoder.client.spectatorApp.announcer.events;

import com.topcoder.client.spectatorApp.messages.IgnorePhaseChange;

public class IgnorePhaseChangeEvent extends AnnouncerEvent {

	private boolean ignore;
	
	public IgnorePhaseChangeEvent() {
	}
	
	@Override
	public Object getMessage() {
		return new IgnorePhaseChange(ignore);
	}

	@Override
	public void validateEvent() throws Exception {
	}

	public boolean isIgnore() {
		return ignore;
	}

	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}
	
}
