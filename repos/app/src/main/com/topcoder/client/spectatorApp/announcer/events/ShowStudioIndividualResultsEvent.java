package com.topcoder.client.spectatorApp.announcer.events;



import com.topcoder.client.spectatorApp.messages.ShowStudio;
import com.topcoder.client.spectatorApp.messages.ShowStudioIndividiualResults;


/**

 * Event to show the initial renderer.

 * 

 * This event will implement the javabean standard since it is read by the XMLDecoder

 * 

 * @author Pops

 */

public class ShowStudioIndividualResultsEvent extends AnnouncerEvent {



	/** Empty constructor as defined by the javabean standard */

	public ShowStudioIndividualResultsEvent() {

	}

	
        private String caption;
        private String imageName;
        
        public String getImageName() {
            return imageName;
        }
        
        public void setImageName(String imageName) {
            this.imageName = imageName;
        }

        public String getCaption() {
            return caption;
        }
        
        public void setCaption(String caption) {
            this.caption = caption;
        }

	/** Returns the ShowInitial message */

	public Object getMessage() {
            try {

                return new ShowStudioIndividiualResults(caption, getImage(imageName));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
	}

	

	/** Nothing to validate! */

	public void validateEvent() throws Exception {
            //System.out.println(computerNames.length);
            if(caption == null)
                throw new Exception("Invalid caption");
        }

	

}

