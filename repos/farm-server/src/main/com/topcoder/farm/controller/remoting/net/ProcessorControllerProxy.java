/*
* Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * ProcessorControllerProxy
 * 
 * Created 07/21/2006
 */
package com.topcoder.farm.controller.remoting.net;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.controller.api.ProcessorControllerNode;
import com.topcoder.farm.controller.command.GetProcessorInitializationDataCommand;
import com.topcoder.farm.controller.command.RegisterProcessorCommand;
import com.topcoder.farm.controller.command.ReportInvocationFeedbackCommand;
import com.topcoder.farm.controller.command.ReportInvocationResultCommand;
import com.topcoder.farm.controller.command.SetAsAvailableCommand;
import com.topcoder.farm.controller.command.UnRegisterProcessorCommand;
import com.topcoder.farm.controller.exception.NotAllowedToRegisterException;
import com.topcoder.farm.managed.command.ManagedCommand;
import com.topcoder.farm.processor.api.ProcessorInvocationFeedback;
import com.topcoder.farm.processor.api.ProcessorInvocationRequest;
import com.topcoder.farm.processor.api.ProcessorInvocationResponse;
import com.topcoder.farm.processor.api.ProcessorNodeCallback;
import com.topcoder.farm.satellite.SatelliteNodeCallback;
import com.topcoder.farm.shared.net.connection.api.ConnectionFactory;
import com.topcoder.farm.shared.util.concurrent.runner.Runner;

/**
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
 * <ol>
 *      <li>Update {@link #registerProcessor(String groupId, ProcessorNodeCallback processor)} method.</li>
 *      <li>Update {@link #reRegisterProcessor(String, ProcessorNodeCallback, int)} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.0
 */
public class ProcessorControllerProxy extends AbstractControllerProxy implements ProcessorControllerNode {
    public ProcessorControllerProxy(ConnectionFactory factory, Runner requestRunner, int registrationTimeout, int ackTimeout) {
        super(factory, requestRunner, registrationTimeout, ackTimeout);
    }

    public Object processRequest(Object object) throws Exception {
        if (object instanceof ManagedCommand) {
            ManagedCommand msg = (ManagedCommand) object;
            return msg.execute((ProcessorNodeCallback) getNodeCallback());
        } else {
            return super.processRequest(object);
        }
    }
    /**
     * <p>
     * register processor by group id.
     * </p>
     * @param groupId the group id
     * @param processor the processor callback.
     * @throws NotAllowedToRegisterException if the controller rejects registration for the processor
     */
    public String registerProcessor(String groupId, ProcessorNodeCallback processorRef) throws NotAllowedToRegisterException {
        init();
        try {
            setNodeCallback(processorRef);
            RegisterProcessorCommand command = new RegisterProcessorCommand(groupId, -1);
            String regId = (String)invokeSync(command);
            setRegisteredId(regId);
            setRegistered();
            return regId;
        } catch (RuntimeException e) {
            release();
            throw e;
        } catch (NotAllowedToRegisterException e) {
            release();
            throw e;
        } catch (Exception e) {
            release();
            handleUnexpectedException(e);
        }
        return null;
    }

    protected void reRegister() {
        ((ProcessorNodeCallback) getNodeCallback()).forceReRegistration();
    }
    /**
     * <p>
     * reRegister processor by group id.
     * </p>
     * @param groupId the group id
     * @param processor the processor callback.
     * @param currentLoad the current load task nums.
     * @throws NotAllowedToRegisterException if the controller rejects registration for the processor
     */
    public String reRegisterProcessor(String groupId, ProcessorNodeCallback processor, int currentLoad) throws NotAllowedToRegisterException {
        try {
            RegisterProcessorCommand command = new RegisterProcessorCommand(groupId, currentLoad);
            String processorId = (String)invokeSync(command);
            return processorId;
        } catch (RuntimeException e) {
            throw e;
        } catch (NotAllowedToRegisterException e) {
            throw e;
        } catch (Exception e) {
            handleUnexpectedException(e);
        }
        return null;
    }
    
    public void unregisterProcessor(String id) {
        try {
            invokeSync(new UnRegisterProcessorCommand(id));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            handleUnexpectedException(e);
        } finally {
            release();
        }
    }

    public Object getProcessorInitializationData(String id) {
        try {
            assertRegistered();
            return invokeSync(new GetProcessorInitializationDataCommand(id));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            handleUnexpectedException(e);
            return null;
        }
    }

    public void reportInvocationResult(String id, ProcessorInvocationResponse response) {
        try {
            assertRegistered();
            invoke(new ReportInvocationResultCommand(id, response));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            handleUnexpectedException(e);
        }
    }
    
    @Override
    public void reportInvocationFeedback(String id, ProcessorInvocationFeedback feedback) {
        try {
            assertRegistered();
            invoke(new ReportInvocationFeedbackCommand(id, feedback));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            handleUnexpectedException(e);
        }
    }
    
    public void setAsAvailable(String id, boolean available) {
        try {
            assertRegistered();
            invoke(new SetAsAvailableCommand(id, available));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            handleUnexpectedException(e);
        }
    }

    protected SatelliteNodeCallbackSkeleton buildSkeletonCallback(SatelliteNodeCallback callBack) {
        return new ProcessorNodeCallbackSkeleton((ProcessorNodeCallback) callBack);
    }
    
    class ProcessorNodeCallbackSkeleton extends SatelliteNodeCallbackSkeleton implements ProcessorNodeCallback   {
        public ProcessorNodeCallbackSkeleton(ProcessorNodeCallback realCallback) {
            super(realCallback);
        }

        public void processInvocationRequest(ProcessorInvocationRequest request) {
            ((ProcessorNodeCallback) getRealCallback()).processInvocationRequest(request);
        }

        public void shutdown() {
            ((ProcessorNodeCallback) getRealCallback()).shutdown();
        }
        
        public void forceReRegistration() {
            ((ProcessorNodeCallback) getRealCallback()).forceReRegistration();
        }
    }
}
