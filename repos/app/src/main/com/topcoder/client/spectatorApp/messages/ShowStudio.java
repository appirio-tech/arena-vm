/**

 * ShowInitial

 *

 * Description:		Notifies the spectator application to show the studio renderer


 */



package com.topcoder.client.spectatorApp.messages;



public class ShowStudio extends ShowScreenAbstract {



    /**

     * No-arg constructor needed by customserialization

     *

     */

    public ShowStudio(String[] computerNames, String path, String[] handles, String time, String title) {
        super(computerNames, path, handles, time, title);
    }

    public String toString() {

    	return "(ShowStudio)";

    }
}
