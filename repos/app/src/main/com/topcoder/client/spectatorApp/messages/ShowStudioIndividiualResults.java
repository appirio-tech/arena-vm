/**

 * ShowInitial

 *

 * Description:		Notifies the spectator application to show the studio renderer


 */



package com.topcoder.client.spectatorApp.messages;



public class ShowStudioIndividiualResults implements java.io.Serializable {



    /**

     * No-arg constructor needed by customserialization

     *

     */

    public ShowStudioIndividiualResults(String caption, byte[] image) {
        this.caption = caption;
        this.image = image;
    }
    
    private byte[] image;
    private String caption;
    

    public String toString() {

    	return "(ShowStudioIndividiualResults)";

    }
    
    public String getCaption() {
        return caption;
    }
    
    public byte[] getImage() {
        return image;
    }
}





