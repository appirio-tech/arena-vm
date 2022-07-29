package com.topcoder.client.spectatorApp.announcer.events;


import java.text.DateFormat;
import java.util.Locale;
import java.text.ParseException;
import com.topcoder.client.spectatorApp.messages.ShowStudio;


/**

 * Event to show the initial renderer.

 * 

 * This event will implement the javabean standard since it is read by the XMLDecoder

 * 

 * @author Pops

 */

public abstract class AbstractShowScreenEvent extends AnnouncerEvent {



	/** Empty constructor as defined by the javabean standard */

	protected AbstractShowScreenEvent() {

	}

	
        private String[] computerNames;
        private String[] handles;
        private String time;
        private String path;
        private String name;
        
        public String[] getComputerNames() {
            return computerNames;
        }
        
        public void setComputerNames(String[] computerNames) {
            this.computerNames = computerNames;
        }

        public String[] getHandles() {
            return handles;
        }
        
        public void setHandles(String[] handles) {
            this.handles = handles;
        }

        public String getPath() {
            return path;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public void setPath(String path) {
            this.path = path;
        }
        
        public void setTime(String time) {
            this.time = time;
        }
        
        public String getTime() {
            return time;
        }

	/** Nothing to validate! */

	public void validateEvent() throws Exception {
            //System.out.println(computerNames.length);
            if(computerNames == null)
                throw new Exception("Must specify computer names");
            if(handles == null)
                throw new Exception("Must specify handles");
            if(computerNames.length != handles.length)
                throw new Exception("Must specify the same number of computer names and handles");
            if(path == null || path.trim().equals(""))
                throw new Exception("Invalid path");
                
            // Validate the time
            try {
                DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.US);
                format.parse(time);
            } catch (ParseException e) {
                throw new Exception("Invalid time");
            }
        }
}
