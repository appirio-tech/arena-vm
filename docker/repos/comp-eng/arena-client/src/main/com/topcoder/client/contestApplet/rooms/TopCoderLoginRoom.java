/*
 * TopCoderLoginRoom
 * 
 * Created 04/19/2007
 */
package com.topcoder.client.contestApplet.rooms;

import java.net.MalformedURLException;
import java.net.URL;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;

/**
 * Default TopCoder Login room. <p>
 * 
 * @autor Diego Belfer (Mural)
 * @version $Id: TopCoderLoginRoom.java 60987 2007-05-14 20:54:48Z thefaxman $
 */
public final class TopCoderLoginRoom extends BaseLoginRoom {

	public TopCoderLoginRoom(ContestApplet parent) {
		super(parent);
	}
	
	protected String getSponsorCompany() {
		return ca.getSponsorName();
	}
	
	protected URL getRegistrationURL() throws MalformedURLException {
		return new URL(Common.URL_REG);
	}
	
	protected String getLegaleseText() {
		return "Any use of the TopCoder Arena, including the practice area, is limited to personal, " +
        "non-commercial or educational purposes only.  If you wish to utilize the TopCoder Arena, " +
        "or any TopCoder information, including statistical information, for commercial purposes, including, " +
        "but not limited to, recruiting, testing or training, please contact TopCoder by email: " +
        "support@topcoder.com.  By logging into the arena, you indicate your agreement " +
        "to these terms as well as those specified in the TopCoder Terms of Service on our website.";
	}
}
