package com.topcoder.client.spectatorApp.messages;

import java.io.Serializable;
import com.topcoder.client.spectatorApp.CommonRoutines;

public class IgnorePhaseChange implements Serializable {
	
	private boolean ignore;
	
	public IgnorePhaseChange() {		
	}
	
	public IgnorePhaseChange(boolean ignore) {
		this.ignore = ignore;
	}

	public boolean isIgnore() {
		return ignore;
	}

	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}
	
	@Override
	public String toString() {
		return "(IgnorePhaseChange) [" + ignore + "]";
	}
}
