/**

 * ShowInitial

 *

 * Description:		Notifies the spectator application to show the studio renderer


 */



package com.topcoder.client.spectatorApp.messages;



public class ShowNoHeaderScreen extends ShowScreenAbstract {



    /**

     * No-arg constructor needed by customserialization

     *

     */

    public ShowNoHeaderScreen(String[] computerNames, String path, String[] handles, String time, String title) {
        super(computerNames, path, handles, time, title);
    }

    public String toString() {

    	return "(ShowNoHeaderScreen)";

    }
}
