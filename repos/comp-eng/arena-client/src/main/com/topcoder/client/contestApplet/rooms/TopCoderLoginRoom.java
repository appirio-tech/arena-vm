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
import com.topcoder.client.contestApplet.common.HyperLinkLoader;
import com.topcoder.client.ui.UIComponent;

/**
 * Default TopCoder Login room. <p>
 * 
 * @autor Diego Belfer (Mural)
 * @version $Id: TopCoderLoginRoom.java 60987 2007-05-14 20:54:48Z thefaxman $
 */
public final class TopCoderLoginRoom extends BaseLoginRoom {

	public TopCoderLoginRoom(ContestApplet parent) {
		super(parent);

		UIComponent legalesePane = page.getComponent("legalese");

		legalesePane.addEventListener("hyperlink", new HyperLinkLoader(ca.getAppletContext()));
	}
	
	protected String getSponsorCompany() {
		return ca.getSponsorName();
	}
	
	protected URL getRegistrationURL() throws MalformedURLException {
		return new URL(Common.URL_REG);
	}
	
	protected String getLegaleseText() {
		return "<html><body style=\"color:white;font-family:Arial;font-size:10px;\">Any use of the Topcoder Arena, including the practice area, is limited to personal, " +
        "non-commercial or educational purposes only.  If you wish to utilize the Topcoder Arena, " +
        "or any Topcoder information, including statistical information, for commercial purposes, including, " +
        "but not limited to, recruiting, testing or training, please contact Topcoder by email: " +
        "support@topcoder.com.  By logging into arena, you indicate your agreement " +
        "to these terms as well as those specified in the <a href=\"https://www.topcoder.com/policy/terms-and-conditions\" style=\"text-decoration: none; color: #CCFF99\">Topcoder Terms of Service</a> on our website.</body></html>";
	}
}
