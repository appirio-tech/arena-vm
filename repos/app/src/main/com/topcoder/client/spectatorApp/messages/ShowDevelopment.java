/**

 * ShowInitial

 *

 * Description:		Notifies the spectator application to show the development screen renderer


 */



package com.topcoder.client.spectatorApp.messages;



public class ShowDevelopment extends ShowScreenAbstract {



    /**

     * No-arg constructor needed by customserialization

     *

     */

    public ShowDevelopment(String[] computerNames, String path, String[] handles, String time, String title) {
        super(computerNames, path, handles, time, title);
    }

    public String toString() {

    	return "(ShowDevelopment)";

    }
}
