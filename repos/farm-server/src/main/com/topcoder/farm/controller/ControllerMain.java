/*
 * ControllerMain
 * 
 * Created 06/26/2006
 */
package com.topcoder.farm.controller;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.controller.api.ControllerNode;
import com.topcoder.farm.controller.commandline.ControllerCommandLineProcessor;
import com.topcoder.farm.controller.configuration.ControllerConfiguration;
import com.topcoder.farm.controller.configuration.ControllerConfigurationProvider;
import com.topcoder.farm.controller.configuration.ControllerInitializerFinalizer;
import com.topcoder.farm.controller.remoting.net.ConnectionController;
import com.topcoder.farm.shared.commandline.CommandLineInterpreter;

/**
 * This is the main class that starts farm controller
 * application. 
 * It is responsible for creating and configurating ControllerNodeImpl
 * and starts and stops ConnnectionController for clients and processors
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ControllerMain {
    private Log log  = LogFactory.getLog(ControllerMain.class);
    /**
     * The id of this Controller
     */
    private String controllerId;
    
    /**
     * client ConnectionController. This instance binds connections reported by the
     * client ConnectionListener to a ControllerSkeleton (Client) object for that connection.
     */
    private ConnectionController clientConnectionControllerBinder;
    
    /**
     * processor ConnectionController. This instance binds connections reported by the
     * processor ConnectionListener to a ControllerSkeleton (Processor) object for that connection.
     */
    private ConnectionController processorConnectionControllerBinder;

    /**
     * Thread used to read standard input and to process 
     * type commands
     */
    private Thread threadInput;
    private ObjectName mbeanName;
    private JMXConnectorServer jmxConnector;

    /**
     * Creates a new controller 
     * 
     * @param controllerId Id of this controller 
     */
    public ControllerMain(String controllerId) {
        this.controllerId = controllerId;
    }

    /**
     * Starts controller application.
     */
    public void start() throws IOException {
        log.info("Controller starting");
        threadInput = new CommandLineInterpreter(new ControllerCommandLineProcessor(this));
        threadInput.start();
        ControllerConfiguration config = ControllerConfigurationProvider.getConfiguration();
        ControllerInitializerFinalizer.initialize(controllerId);
        clientConnectionControllerBinder = new ConnectionController(controllerId, config.getClientConnectionConfiguration());
        clientConnectionControllerBinder.start();
        try {
            processorConnectionControllerBinder = new ConnectionController(controllerId, config.getProcessorConnectionConfiguration());
            processorConnectionControllerBinder.start();
        } catch (IOException e) {
            clientConnectionControllerBinder.stop();
            throw e;
        }
        try {
            log.info("Starting MBean server");
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            final String mbeanStringName = mbeanServer.getDefaultDomain()+":type=Controller";
            mbeanName = new ObjectName(mbeanStringName);
            log.info("Registering mbean as "+mbeanStringName);
            mbeanServer.registerMBean(ControllerLocator.getController(), mbeanName);
            exportForRemoting(mbeanServer); 
            log.info("MBean server started");
        } catch (Exception e) {
            log.error("MBean server registration fail", e);
        }
        log.info("Controller started");
    }
    
    /**
     * Tries to register the MBeanServer for remoting
     * @param mbeanServer The server
     */
    private void exportForRemoting(MBeanServer mbeanServer) {
        String urlString = "service:jmx:rmi:///jndi/rmi://localhost:9999/server";
        try {
            JMXServiceURL url = new JMXServiceURL(urlString);
            jmxConnector = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbeanServer);
            jmxConnector.start();
        } catch (Exception e) {
            jmxConnector = null;
            log.warn("Cannot register mbean server into RMI registry. URL="+urlString);
            log.debug(e,e);
        }        
    }

    /**
     * Stops controller application
     */
    public void stop() {
        log.info("Stopping controller");
        if (mbeanName != null) {
            try {
                if (jmxConnector != null) {
                    log.info("Stopping jmx connector");
                    jmxConnector.stop();
                }
                log.info("Unregistering jmx bean");
                ManagementFactory.getPlatformMBeanServer().unregisterMBean(mbeanName);
            } catch (Exception e) {
                log.error("MBean server unregistration fail", e);
            }
        }
        if (clientConnectionControllerBinder != null) {
            clientConnectionControllerBinder.stop();
            clientConnectionControllerBinder = null;
        }
        if (processorConnectionControllerBinder != null) {
            processorConnectionControllerBinder.stop();
            processorConnectionControllerBinder = null;
        }
        ControllerInitializerFinalizer.finalize(controllerId);
        if (threadInput != null) {
            threadInput.interrupt();
            threadInput= null;
        }
        log.info("Controller stopped");
    }
    
    
    /**
     * Makes the ControllerNodeImpl to shutdown
     */
    public void shutdown() {
        ControllerNode controller = ControllerLocator.getController();
        if (controller != null) {
            controller.shutdown();
        }
    }

    /**
     * Run the controller node application
     * @throws IOException
     */
    public void run() throws IOException {
        try {
            start();
            ControllerNode controller = ControllerLocator.getController();
            if (controller != null) {
                try {
                    controller.waitForShutdown();
                } catch (InterruptedException e) {
                }
            }
        } finally {
            stop();
        }
    }

    /**
     * Display how this class should be invoked from
     * the command line. 
     */
    private static void usage() {
        System.out.println("The name of the controller must be provided as argument");
        System.out.println("System properties: ");
        System.out.println("[required] configurationProvider.class : Class extending ControllerConfigurationProvider that will provide configuration");
        System.out.println("[optional] configuration.xml.url : If XMLConfiguraitionProvider class is used, containing the URL for the xml with the configuration");
        System.out.println("[optional] com.topcoder.commandline.io.port : port where remote console will listen, default 15658");
        System.out.println("[optional] com.topcoder.commandline.io : Console mode, [auto|console|socket] where console will listen for commands");
        System.out.println("                                         auto   : If no running on JWS use console, otherwise socket");
        System.out.println("                                         console: stdin/stdout");
        System.out.println("                                         socket: Listens for console connections on socket");
        System.exit(-1);
    }
    
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            usage();
        }
        final ControllerMain controllerMain = new ControllerMain(args[0]);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                controllerMain.shutdown();
            } 
        });
        try {
            controllerMain.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
