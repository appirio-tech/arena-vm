package com.topcoder.client.contestApplet.rooms;

/*
* LoginRoom.java
*
* Created on July 12, 2000, 4:08 PM
*/

import java.net.MalformedURLException;
import java.net.URL;

import com.topcoder.client.contestApplet.ContestApplet;

/**
 *
 * @author  Alex Roman
 * @version
 */

public final class VeriSignLoginRoom extends BaseLoginRoom {

    public VeriSignLoginRoom(ContestApplet parent) {
        super(parent);
    }
    
	protected String getSponsorCompany() {
		return ca.getCompanyName();
	}
	
	public void create() {
	    getPage().getComponent("register_label").setProperty("text", "Forgot your password ?");
	    getPage().getComponent("register").setProperty("text", "Click here to retrieve it");
	    getPage().getComponent("register").setProperty("tooltipText", "Retreive your password");
	    super.create();
	}

        //TODO: Forgot your password ...
	protected URL getRegistrationURL() throws MalformedURLException {
		return new URL("http://www.topcoder.com/pl/?module=VICCCredentials&cm=17942");
	}
	
	protected String getLegaleseText() {
		return "";
	}
}