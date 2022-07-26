/*
* Copyright (C) - 2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * ProcessorMain
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.processor;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.appirio.commons.mq.monitor.QueueMonitor;
import com.topcoder.farm.controller.configuration.ApplicationContextProvider;
import com.topcoder.farm.controller.configuration.EnvironmentConfig;
import com.topcoder.farm.controller.exception.NotAllowedToRegisterException;
import com.topcoder.farm.processor.monitor.ProcessorMonitorWorkListener;

/**
 * 
 * Note: <p>
 * Set the working dir to the processor root folder. <p>
 * Defines the system property invocation.root.folder with the processorRootFolder <p>
 * 
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
 * <ol>
 *      <li>Update {@link #ProcessorMain(String)} method of naming convention.</li>
 *      <li>Add {@link #groupId} field.</li>
 *      <li>Update {@link #start()} method of naming convention.</li>
 *      <li>Update {@link #stop()} method of naming convention.</li>
 *      <li>Update {@link #main(String[] args)} method.</li>
 *      <li>Update {@link #resolveGroupId(String newId)} method.</li>
 *      <li>Update {@link #attemptToSaveNewId(String newId)} method.</li>
 *      <li>Update {@link #attempToLoadPreviousId()} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.0
 */
public class ProcessorMain {
private static Log log  = LogFactory.getLog(ProcessorMain.class);
    
    /**
     * The id of this processor group.
     * @since HandShake Change v1.0
     */
//    private String groupId;
    
    /**
     * The id of this Processor
     */
//    private String processorId;
    
    /**
     * The processor node object that manages farm node complexity
     */
//    private ProcessorNode processorNode;
    

    /**
     * Thread used for reading and executing keyboard input commands
     */
//    private Thread threadInput;
    
//    private Thread monitorThread;

    /**
     * Creates a new processor 
     * 
     * @param processorId Id of this processor 
     */
    public ProcessorMain(String groupId) {
//        this.groupId = groupId; 
    }

    /**
     * Runs the processor application
     */
    public void run() {
        try {
        	//testParser();
        	
            start();
            
//            if (monitorThread != null) {
//            	monitorThread.join();
//            }
            
//            try {
//                processorNode.waitForShutdown();
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            stop();
        }
    }
    
    /**
     * Starts the processor application
     */
    public void start() throws NotAllowedToRegisterException {
        log.info("Starting processor");
        
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("processor-applicationContext.xml");
        ApplicationContextProvider.setContext(applicationContext);
        
        EnvironmentConfig env = applicationContext.getBean(EnvironmentConfig.class);
        ProcessorConfig config = applicationContext.getBean(ProcessorConfig.class);
        
        QueueMonitor mon = ApplicationContextProvider.getContext().getBean(QueueMonitor.class);
		ProcessorMonitorWorkListener listener = ApplicationContextProvider.getContext().getBean(ProcessorMonitorWorkListener.class);
		listener.setDefaultTimeout(config.getDefaultTimeout());
		List<String> queues = config.getMonitoredQueues();
		// need to turn logical queue name into a fully qualified name
		String[] queueNames = new String[queues.size()];
		String queuePrefix = env.getPrefix() + env.getAppServiceName() + '-';
		for (int i=0; i < queueNames.length; i++) {
			queueNames[i] = queuePrefix + queues.get(i);
		}
		log.info("Monitoring queues " + Arrays.toString(queueNames));
		// monitor in a separate thread but don't use separate threads for the listener so that
		// we process messages serially
		mon.monitor(200L, 1, true, false, listener, queueNames);        
        log.info("Processor started");
    }

    /**
     * Stops the processor application
     */
    public void stop() {
//        log.info("Stopping processor");
//        if (processorNode != null) {
//            processorNode.releaseNode();
//            processorNode = null;
//        }
//        ProcessorInitializerFinalizer.finalize(groupId);
//        log.info("Processor stopped");
    }
    
    /**
     * Shutdown the processor application
     */
    public void shutdown() {
//        final ProcessorNode node = processorNode;
//        if (node != null) {
//            node.shutdown();
//        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
        }
        try {
            new ProcessorMain(args[0]).run();
        } catch (Exception e) {
            e.printStackTrace(); 
        }
//        System.exit(0);
    }

//    private static String resolveProcessorId(String newId) {
//        if (ProcessorIdHelper.isIdForTemplatedProcessor(newId)) {
//            String oldId  = attempToLoadPreviousId();
//            if (oldId != null && !oldId.isEmpty() && ProcessorIdHelper.isIdForTemplatedProcessor(oldId)) {
//                String newTemplateName = ProcessorIdHelper.extractTemplateName(newId);
//                String oldTemplateName =  ProcessorIdHelper.extractTemplateName(oldId);
//                if (newTemplateName.equals(oldTemplateName)) {
//                    return oldId;
//                }
//            }
//        }
//        attemptToSaveNewId(newId);
//        return newId;
//    }

//    private static void attemptToSaveNewId(String newId) {
//        try {
//            FileUtils.writeStringToFile(new File("lastProcessorId"), newId);
//        } catch (IOException e) {
//            log.error("Failed to store processor id into disk", e);
//        }
//    }
//
//    private static String attempToLoadPreviousId() {
//        try {
//            return FileUtils.readFileToString(new File("lastProcessorId"));
//        } catch (IOException e) {
//            return null;
//        }
//    }

    /**
     * Display how this class should be invoked from
     * the command line. 
     */
    private static void usage() {
        System.out.println("The group name of the processor must be provided as argument");
        System.out.println("System properties: ");
        System.out.println("[required] configurationProvider.class : Class extending ProcessorConfigurationProvider that will provide configuration");
        System.out.println("[optional] configuration.xml.url : If XMLConfiguraitionProvider class is used, containing the URL for the xml with the configuration");
    }
    
    
//    private ProcessorInvocationRunner buildInvocationRunner() {
//        ProcessorConfiguration cfg = ProcessorConfigurationProvider.getConfiguration();
//        ProcessorInvocationRunner processorInvocationRunner = new ProcessorInvocationRunner(new File(cfg.getRootFolder()), new File(cfg.getWorkFolder()));
//        System.setProperty("user.dir", cfg.getRootFolder());
//        System.setProperty("invocation.root.folder", cfg.getRootFolder());
//        return processorInvocationRunner;
//    }
//    
//    private ProcessorNode.Listener buildListener() {
//        return new ProcessorNode.Listener() {
//            public void nodeDisconnected(String cause) {
//                System.out.println("DISCONNECTED: " + cause);
//            }
//        };
//    }
    

}
