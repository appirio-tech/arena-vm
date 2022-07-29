/*
 * MaintenanceManager
 * 
 * Created 09/18/2006
 */
package com.topcoder.farm.controller.maintenance;

/**
 * MaintenanceManager class is responsible for running maintenance tasks
 * in the controller.<p>
 * 
 * These task could be:
 *  <li> Removing notified and dropped invocations from the database
 *  <li> Removing old unreferenced shared objects   
 *   
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface MaintenanceManager {
    /**
     * Reset the timer countdown that MaintenanceManager uses
     * to run schedulled tasks.
     * Only the counter associated to the given tasks name is reset.
     * 
     * @param maintenanceTaskName The name of the maintenance task to reset  
     */
    public void resetMaintenanceCountdown(String maintenanceTaskName);

    /**
     * Release this MaintenanceManager instance.
     */
    public void release();
}