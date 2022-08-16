/**
 * 
 */
package com.topcoder.client.spectatorApp.announcer.tabs;

import com.topcoder.client.spectatorApp.announcer.events.AnnouncerEvent;
import java.io.Serializable;

/**
 * @author ndean
 *
 */
public class ScheduledEvent implements Cloneable, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public AnnouncerEvent contEvent;
	public String notes;
	public int time;
	
	public ScheduledEvent(){
		contEvent=null;
		notes="";
		time=0;
	}
	
	public ScheduledEvent(AnnouncerEvent ae,int t){
		contEvent=ae;
		time=t;
	}

	public AnnouncerEvent getContEvent() {
		return contEvent;
	}

	public String toString(){
		return contEvent.toString() + ":" + time;
	}
	
	public void setContEvent(AnnouncerEvent contEvent) {
		this.contEvent = contEvent;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
}
