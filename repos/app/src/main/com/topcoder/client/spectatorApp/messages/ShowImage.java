package com.topcoder.client.spectatorApp.messages;

import java.io.Serializable;

public class ShowImage implements Serializable {
	
	private String path;
	
	public ShowImage() {		
	}
	
	public ShowImage(String path) {
		this.path = path;
	}

	public String getImagePath() {
		return path;
	}

	public void setImagePath(String path) {
		this.path = path;
	}
	
	@Override
	public String toString() {
		return "(ShowImage) [" + path + "]";
	}
}
