/*
 * HardCodeConfigurationProvider
 * 
 * Created 08/30/2006
 */
package com.topcoder.farm.controller.configuration;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
class HardCodeConfigurationProvider { // extends ControllerConfigurationProvider {
//
//    protected ControllerConfiguration buildConfiguration() {
//        ControllerConfiguration cfg = new ControllerConfiguration();
//        cfg.setClientConnectionConfiguration(buildConnectionControllerConfiguration(IntegConstants.CONTROLLER_CLIENT_PORT));
//        cfg.setProcessorConnectionConfiguration(buildConnectionControllerConfiguration(IntegConstants.CONTROLLER_PROCESSOR_PORT));
//        cfg.setDatabaseConfigurationFile("hibernate.cfg.xml");
//        return cfg;
//    }
//
//    private ConnectionControllerConfiguration buildConnectionControllerConfiguration(int port) {
//        ConnectionControllerConfiguration cnnCfg = new ConnectionControllerConfiguration();
//        cnnCfg.setRunnerMinThreads(5);
//        cnnCfg.setRunnerMaxThreads(10);
//        cnnCfg.setConnectionListenerConfiguration(buildNBIOListenerCfg(port));
//        return cnnCfg;
//    }
//
//    private NBIOListenerConfiguration buildNBIOListenerCfg(int port) {
//        NBIOListenerConfiguration cfg = new NBIOListenerConfiguration();
//        cfg.setListenerType(ListenerType.NBIOListenerType);
//        cfg.setAllowedSet(false);
//        cfg.setCsHandlerFactoryClassName(FarmCSHandlerFactory.class.getName());
//        cfg.setMonitorClassName(EmptyMonitor.class.getName());
//        cfg.setIpsStringList("");
//        cfg.setNumAcceptThreads(3);
//        cfg.setNumReadThreads(5);
//        cfg.setNumWriteThreads(5);
//        cfg.setPort(port);
//        return cfg;
//    }
}
