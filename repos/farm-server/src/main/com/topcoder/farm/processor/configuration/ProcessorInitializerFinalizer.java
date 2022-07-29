/*
 * ProcessorInitializerFinalizer
 * 
 * Created 08/18/2006
 */
package com.topcoder.farm.processor.configuration;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
@Deprecated
public class ProcessorInitializerFinalizer {
//    private static Runner runner;
//
//    public static void initialize(String processorName) {
//        if (runner != null) {
//            throw new IllegalStateException("Already initialized");
//        }
//        LogInitializer.initialize();
//        ProcessorConfiguration config = ProcessorConfigurationProvider.getConfiguration();
//        verifyInstalled(processorName, config);
//        LogInitializer.reconfigure();
//        configureController(config);
//    }
//
//   
//    private static void verifyInstalled(String processorName, ProcessorConfiguration config) {
//        try {
//            new Deployer().deploy(processorName, config.getRootFolder()); 
//        } catch (DeploymentException e) {
//            throw new IllegalStateException("Could not initialize resources", e);
//        }
//    }
//
//    private static void configureController(ProcessorConfiguration config) {
//        runner = buildRunner(config);
//        ProcessorControllerLocator.setProcessorControllerProvider(
//                new RemoteProxyControllerProvider(
//                        config, runner,
//                        ProcessorControllerProxy.class.getName(), 
//                        5, 
//                        config.getRegistrationTimeout(), 
//                        config.getAckTimeout()));
//    }
//    
//    private static Runner buildRunner(ProcessorConfiguration configuration) {
//        return new ExecutorServiceToRunnerAdapter(
//                new ThreadPoolExecutor(1, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
//                        new SynchronousQueue<Runnable>()));
//    }
//
//    public static void finalize(String processorName) {
//        if (runner != null) {
//            runner.stopAccepting();
//            runner = null;
//        }
//        ProcessorControllerLocator.setProcessorControllerProvider(null);
//    }
}
