/*
 * MaintenanceManagerImpl
 * 
 * Created 09/18/2006
 */
package com.topcoder.farm.controller.maintenance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.controller.api.AdminControllerNode;
import com.topcoder.farm.controller.api.ControllerNode;
import com.topcoder.shared.netCommon.resettabletask.ResettableTaskRunner;
import com.topcoder.shared.netCommon.resettabletask.ResettableTimerTask;

/**
 * Maintenance manager simple implementation. <p>
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class MaintenanceManagerImpl implements MaintenanceManager {
    private Log log = LogFactory.getLog(MaintenanceManagerImpl.class);
    private AdminControllerNode controller;
    private Object taskRunnerMutex = new Object();
    private ResettableTaskRunner taskRunner;

    public MaintenanceManagerImpl(ControllerNode ctrl, int removalInterval, int reassignInterval) {
        this.controller = ctrl;
        taskRunner = new ResettableTaskRunner();
        ResettableTimerTask taskRemoval = new ResettableTimerTask(removalInterval) {
                    protected boolean doAction() {
                        try {
                            controller.purgeInvocationsAndSharedObjects();
                        } catch (Exception e) {
                            log.error("Maintenance tasks failed",e);
                        }
                        return false;
                    }};
        taskRunner.registerTask("cleanUp-Invocations",  taskRemoval);
        ResettableTimerTask taskReassign = new ResettableTimerTask(reassignInterval) {
            protected boolean doAction() {
                try {
                    controller.scheduleDroppedAssignations();
                } catch (Exception e) {
                    log.error("Maintenance tasks failed",e);
                }
                return false;
            }};
        taskRunner.registerTask("reschedule-Invocations",  taskReassign);            
        taskRunner.start();
    }
    
    public void release() {
        synchronized (taskRunnerMutex) {
            if (taskRunner != null) {
                try {
                    taskRunner.stop();
                } catch (Exception e) {
                    /*Nothing to do*/
                }
                taskRunner = null;
            }
        }
    }
    
    protected void finalize() throws Throwable {
        release();
    }
    
    public void resetMaintenanceCountdown(String maintenanceTaskName) {
        final ResettableTaskRunner runner = taskRunner;
        if (runner != null) {
            runner.resetTask(maintenanceTaskName);
        }
    }
}
