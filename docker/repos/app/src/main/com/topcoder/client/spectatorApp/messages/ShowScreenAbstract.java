/**

 * ShowInitial

 *

 * Description:		Notifies the spectator application to show the screens.
 * It is the base class of ShowStudio/ShowDesign/ShowDevelopment.


 */



package com.topcoder.client.spectatorApp.messages;



public abstract class ShowScreenAbstract implements java.io.Serializable {



    /**

     * No-arg constructor needed by customserialization

     *

     */

    protected ShowScreenAbstract(String[] computerNames, String path, String[] handles, String time, String title) {
        this.computerNames = computerNames;
        this.path = path;
        this.handles = handles;
        this.time = time;
        this.title = title;
    }
    
    private String[] computerNames;
    private String[] handles;
    private String path;
    private String time;
    private String title;
    

    public String toString() {

    	return "(ShowScreenAbstract)";

    }
    
    public String getTime() {
        return time;
    }
    
    public String getPath() {
        return path;
    }

    public String[] getComputerNames() {
        return computerNames;
    }
    
    public String[] getHandles() {
        return handles;
    }
    
    public String getTitle() {
        return title;
    }
}





