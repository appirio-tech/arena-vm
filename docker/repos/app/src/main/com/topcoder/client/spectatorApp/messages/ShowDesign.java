/**

 * ShowInitial

 *

 * Description:		Notifies the spectator application to show the design renderer


 */



package com.topcoder.client.spectatorApp.messages;



public class ShowDesign extends ShowScreenAbstract {



    /**

     * No-arg constructor needed by customserialization

     *

     */

    public ShowDesign(String[] computerNames, String path, String[] handles, String time, String title) {
        super(computerNames, path, handles, time, title);
    }

    public String toString() {

    	return "(ShowDesign)";

    }
}
