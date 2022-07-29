package com.topcoder.client.spectatorApp.announcer.events;

/**
 * Connection event that hold information about the IP/port the announcer
 * app should connect to.  Note: this should NOT inherit from AnnouncerEvent
 * 
 * Note: must follow javabean standard to be readable by XMLDecoder
 * @author Pops
 */
public class ConnectionEvent {

	/** The host to connect to */
	private String hostName;
	
	/** The port to connect to */
	private int portNo;
	
	/** Empty constructor - javabean standard */
	public ConnectionEvent() {
	}
	
	/** Returns the host name to connect to */
	public String getHostName() {
		return hostName;
	}

	/** Set's the host name to connect to */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	/** Returns the port number to connect to*/
	public int getPortNo() {
		return portNo;
	}

	/** Sets the port number to connect to */
	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

}
