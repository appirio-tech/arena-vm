/*
* Copyright (C) - 2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * ProcessorMain
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.processor;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.controller.exception.NotAllowedToRegisterException;
import com.topcoder.farm.processor.api.ProcessorIdHelper;
import com.topcoder.farm.processor.api.ProcessorNode;
import com.topcoder.farm.processor.commandline.ProcessorCommandLineProcessor;
import com.topcoder.farm.processor.configuration.ProcessorConfiguration;
import com.topcoder.farm.processor.configuration.ProcessorConfigurationProvider;
import com.topcoder.farm.processor.configuration.ProcessorInitializerFinalizer;
import com.topcoder.farm.processor.node.ProcessorNodeBuilder;
import com.topcoder.farm.shared.commandline.CommandLineInterpreter;
import com.topcoder.farm.shared.net.connection.remoting.RemotingException;

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
     * Time in ms to wait before trying to start again
     * after an attempt to get a connection failed
     */
    private static final int START_RETRY_INTERVAL = 10000;

    /**
     * The id of this processor group.
     * @since HandShake Change v1.0
     */
    private String groupId;
    
    /**
     * The id of this Processor
     */
    private String processorId;
    
    /**
     * The processor node object that manages farm node complexity
     */
    private ProcessorNode processorNode;
    

    /**
     * Thread used for reading and executing keyboard input commands
     */
    private Thread threadInput;

    /**
     * Creates a new processor 
     * 
     * @param processorId Id of this processor 
     */
    public ProcessorMain(String groupId) {
        this.groupId = groupId; 
    }

    /**
     * Runs the processor application
     */
    public void run() {
        try {
        	while (true) {
            	try {
            		start();
            		break;
            	} catch (RemotingException e) {
            		log.error("Remoting exception to controller, will retry after " + START_RETRY_INTERVAL, e);
            		try {
            	        ProcessorInitializerFinalizer.finalize(groupId);
            		} catch (Exception ignore) {
            			// ignore
            		}
            	    Thread.sleep(START_RETRY_INTERVAL);	
            	}
        	}
            try {
                processorNode.waitForShutdown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }
    
    /**
     * Starts the processor application
     */
    public void start() throws NotAllowedToRegisterException {
        log.info("Starting processor");
        ProcessorInitializerFinalizer.initialize(groupId);
        log.info("The processor's group id is: " + groupId);
        ProcessorInvocationRunner processorInvocationRunner = buildInvocationRunner();
        processorNode = new ProcessorNodeBuilder().buildProcessor(groupId, processorInvocationRunner, buildListener());
        processorId = processorNode.getId();
        attemptToSaveNewId(processorId);
        threadInput = new CommandLineInterpreter(new ProcessorCommandLineProcessor(this));
        threadInput.start();
        processorNode.setAsAvailable();
        log.info("Processor started");
    }

    /**
     * Stops the processor application
     */
    public void stop() {
        log.info("Stopping processor");
        if (processorNode != null) {
            processorNode.releaseNode();
            processorNode = null;
        }
        ProcessorInitializerFinalizer.finalize(groupId);
        log.info("Processor stopped");
    }
    
    /**
     * Shutdown the processor application
     */
    public void shutdown() {
        final ProcessorNode node = processorNode;
        if (node != null) {
            node.shutdown();
        }
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
        System.exit(0);
    }

    private static String resolveProcessorId(String newId) {
        if (ProcessorIdHelper.isIdForTemplatedProcessor(newId)) {
            String oldId  = attempToLoadPreviousId();
            if (oldId != null && !oldId.isEmpty() && ProcessorIdHelper.isIdForTemplatedProcessor(oldId)) {
                String newTemplateName = ProcessorIdHelper.extractTemplateName(newId);
                String oldTemplateName =  ProcessorIdHelper.extractTemplateName(oldId);
                if (newTemplateName.equals(oldTemplateName)) {
                    return oldId;
                }
            }
        }
        attemptToSaveNewId(newId);
        return newId;
    }

    private static void attemptToSaveNewId(String newId) {
        try {
            FileUtils.writeStringToFile(new File("lastProcessorId"), newId);
        } catch (IOException e) {
            log.error("Failed to store processor id into disk", e);
        }
    }

    private static String attempToLoadPreviousId() {
        try {
            return FileUtils.readFileToString(new File("lastProcessorId"));
        } catch (IOException e) {
            return null;
        }
    }

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
    
    
    private ProcessorInvocationRunner buildInvocationRunner() {
        ProcessorConfiguration cfg = ProcessorConfigurationProvider.getConfiguration();
        ProcessorInvocationRunner processorInvocationRunner = new ProcessorInvocationRunner(new File(cfg.getRootFolder()), new File(cfg.getWorkFolder()));
        System.setProperty("user.dir", cfg.getRootFolder());
        System.setProperty("invocation.root.folder", cfg.getRootFolder());
        return processorInvocationRunner;
    }
    
    private ProcessorNode.Listener buildListener() {
        return new ProcessorNode.Listener() {
            public void nodeDisconnected(String cause) {
                System.out.println("DISCONNECTED: " + cause);
            }
        };
    }

}
