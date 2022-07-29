/*
 * ControllerNodeConfiguration
 * 
 * Created 11/16/2006
 */
package com.topcoder.farm.controller.configuration;

/**
 * ControllerNode configuration <p>
 * 
 * Contains parameters used by ControllerNode. <p>
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ControllerNodeConfiguration {
    /**
     * Time in ms between removal intervals. Dropped invocations and unreferenced shared objects must
     * be removed from the farm. A maintenance tasks is run every <code>maintenanceRemovalInterval</code>.
     */
    private int maintenanceRemovalInterval;
    /**
     * Time in ms between reassignations intervals. Invocations that were assigned to a processor, for which
     * assing ttl has expired and no response was received from the processor, should be reschedule for processing
     */
    private int maintenanceReassignInterval;
    /**
     * Specifies the time to wait for processors and clients disconnect from the controller after a disconnect message
     * due to shutdown was sent to them. (In Ms)
     */
    private int timeBeforeShutdown;
    /**
     * Maximun number of records that should be removed from the farm at once. Large numbers can generated
     * a big number of locks. 
     */
    private int maxBulkSize;
    
    /**
     * Max number of milliseconds a shared object should be hold in the farm with no invocation referencing it 
     * prior to be removed.
     */
    private int sharedObjectStorageTime;
    
    public int getMaintenanceReassignInterval() {
        return maintenanceReassignInterval;
    }
    public void setMaintenanceReassignInterval(int maintenanceReassignInterval) {
        this.maintenanceReassignInterval = maintenanceReassignInterval;
    }
    public int getMaintenanceRemovalInterval() {
        return maintenanceRemovalInterval;
    }
    public void setMaintenanceRemovalInterval(int maintenanceRemovalInterval) {
        this.maintenanceRemovalInterval = maintenanceRemovalInterval;
    }
    public int getTimeBeforeShutdown() {
        return timeBeforeShutdown;
    }
    public void setTimeBeforeShutdown(int timeBeforeShutdown) {
        this.timeBeforeShutdown = timeBeforeShutdown;
    }
    public int getMaxBulkSize() {
        return maxBulkSize;
    }
    public void setMaxBulkSize(int maxBulkSize) {
        this.maxBulkSize = maxBulkSize;
    }
    public int getSharedObjectStorageTime() {
        return sharedObjectStorageTime;
    }
    public void setSharedObjectStorageTime(int sharedObjectStorageTime) {
        this.sharedObjectStorageTime = sharedObjectStorageTime;
    }
}
