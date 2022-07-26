/*
 * FarmFactoryService
 * 
 * Created 11/15/2006
 */
package com.topcoder.server.farm.jboss;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.topcoder.farm.controller.configuration.ApplicationContextProvider;
import com.topcoder.shared.util.logging.Logger;


/**
 * @author Diego Belfer (mural)
 * @version $Id: FarmFactoryService.java 70823 2008-05-27 20:49:33Z dbelfer $
 */
// ONLY USED TO INITIALIZE SPRING NOW
public class FarmFactoryService implements FarmFactoryServiceMBean {
    /**
     * Category for logging.
     */
    private static final Logger log = Logger.getLogger(FarmFactoryService.class);
    
//    private Thread monitorThread = null;
    
    public void start() {
        log.info("Configuring farm factory");
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("server-applicationContext.xml");
		ApplicationContextProvider.setContext(applicationContext);
		
//		ControllerConfiguration config = ControllerConfigurationProvider.getConfiguration();
//		try {
//			//ControllerInitializerFinalizer.initialize("CT-MAIN");
//			
//			//ControllerConfiguration config = ControllerConfigurationProvider.getConfiguration();
//	        //LogInitializer.initialize();
//	        //verifyInstalled(controllerName, config);
//	        //LogInitializer.reconfigure();
//	        DAOFactory.configureInstance(new HibernateDAOFactory());
//	        HibernateUtil.initialize(config.getDatabaseConfigurationFile());
//	        //ControllerLocator.setLocalController(new ControllerNodeImpl("CT-MAIN", config.getNodeConfiguration()));
//			
//		} catch (Throwable t) {
//			log.error("Unable to initialize hibernate: " + t.getMessage(), t);
//		}
        
		
        //CodeServicesConfig.init();
//        try {
//            FarmFactoryProvider.getConfiguredFarmFactory();
//            monitorThread = new Thread() {
//                public void run() {
//                    boolean failed = true;
//                    int count = 0;
//                    while (failed && !Thread.currentThread().isInterrupted()) {
//                        failed = false;
//                        try {
//                            com.topcoder.server.ejb.TestServices.TestInvoker.getInstance();
//                            com.topcoder.server.ejb.TestServices.TestInvoker.getInstance().ensureListeningResults();
//                        } catch (Exception e) {
//                            failed = true;
//                            if (count % 10 == 0) {
//                                log.info("Failed to start TestServices.TestInvoker: " + e.getMessage());
//                            }
//                        }
//                        try {
//                            com.topcoder.server.ejb.TestServices.longtest.TestInvoker.getInstance();
//                            com.topcoder.server.ejb.TestServices.longtest.TestInvoker.getInstance().ensureListeningResults();
//                        } catch (Exception e) {
//                            failed = true;
//                            if (count % 10 == 0) {
//                                log.info("Failed to start TestServices.longtest.TestInvoker: " + e.getMessage());
//                            }
//                        }
//                        if (failed) {
//                            try {
//                                Thread.sleep(5000);
//                            } catch (InterruptedException e) {
//                                Thread.currentThread().interrupt();
//                            }
//                        }
//                        count++;
//                    }
//                }
//            };
//            monitorThread.start();
//        } catch (Exception e) {
//            log.error("Exception trying to start farm factory", e);
//            
//        }
    }
    
//    public void stop() {
//        log.info("Stopping all farm invokers");
//        try {
//            if (monitorThread!=null && monitorThread.isAlive()) {
//                monitorThread.interrupt();
//            }
//            if (FarmFactory.isConfigured()) {
//                Thread thread = new Thread() {
//                    public void run() {
//                        FarmFactory.releaseInstance();
//                    }
//                };
//                thread.start();
//                try {
//                    thread.join(500);
//                } catch (Exception e) {
//                }
//                if (thread.isAlive()) {
//                    thread.interrupt();
//                }
//            }
//        } catch (Exception e) {
//            log.error("Exception stopping farm factory", e);
//        }
//    }
//
//    public void restartInvoker(String invokerName) throws Exception {
//        FarmFactory factory = FarmFactory.getInstance();
//        factory.releaseInvoker(invokerName);
//        factory.getInvoker(invokerName);
//    }
//    
//    public void releaseInvoker(String invokerName) throws Exception {
//        FarmFactory factory = FarmFactory.getInstance();
//        factory.releaseInvoker(invokerName);
//    }
//
//    public int invokersCount() throws Exception {
//        return FarmFactory.getInstance().invokersCount();
//    }
//
//    public List listInvokers() throws Exception {
//        return FarmFactory.getInstance().invokerNames();
//        
//    }
}
