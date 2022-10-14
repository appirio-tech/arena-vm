/*
 * ProcessorInitializerFinalizer
 * 
 * Created 08/18/2006
 */
package com.topcoder.farm.processor.configuration;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.topcoder.farm.controller.remoting.net.ProcessorControllerProxy;
import com.topcoder.farm.controller.remoting.net.RemoteProxyControllerProvider;
import com.topcoder.farm.deployer.Deployer;
import com.topcoder.farm.deployer.DeploymentException;
import com.topcoder.farm.processor.ProcessorControllerLocator;
import com.topcoder.farm.shared.log.LogInitializer;
import com.topcoder.farm.shared.util.concurrent.runner.ExecutorServiceToRunnerAdapter;
import com.topcoder.farm.shared.util.concurrent.runner.Runner;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ProcessorInitializerFinalizer {
    private static Runner runner;

    public static void initialize(String processorName) {
        if (runner != null) {
            throw new IllegalStateException("Already initialized");
        }
        LogInitializer.initialize();
        ProcessorConfiguration config = ProcessorConfigurationProvider.getConfiguration();
        verifyInstalled(processorName, config);
        LogInitializer.reconfigure();
        configureController(config);
    }

   
    private static void verifyInstalled(String processorName, ProcessorConfiguration config) {
        try {
            new Deployer().deploy(processorName, config.getRootFolder()); 
        } catch (DeploymentException e) {
            throw new IllegalStateException("Could not initialize resources", e);
        }
    }

    private static void configureController(ProcessorConfiguration config) {
        runner = buildRunner(config);
        ProcessorControllerLocator.setProcessorControllerProvider(
                new RemoteProxyControllerProvider(
                        config, runner,
                        ProcessorControllerProxy.class.getName(), 
                        Integer.MAX_VALUE, 
                        config.getRegistrationTimeout(), 
                        config.getAckTimeout()));
    }
    
    private static Runner buildRunner(ProcessorConfiguration configuration) {
        return new ExecutorServiceToRunnerAdapter(
                new ThreadPoolExecutor(1, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                        new SynchronousQueue<Runnable>()));
    }

    public static void finalize(String processorName) {
        if (runner != null) {
            runner.stopAccepting();
            runner = null;
        }
        ProcessorControllerLocator.setProcessorControllerProvider(null);
    }
}
