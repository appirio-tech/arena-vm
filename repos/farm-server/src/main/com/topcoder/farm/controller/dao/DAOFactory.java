/*
 * DAOFactory
 * 
 * Created 08/03/2006
 */
package com.topcoder.farm.controller.dao;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public abstract class DAOFactory {
    private static DAOFactory instance;
    
    public static void configureInstance(DAOFactory  instance) {
        DAOFactory.instance = instance;
    }
    
    public static DAOFactory getInstance() {
        return instance;
    }
    
    public abstract TransactionManager getTransactionManager();
    public abstract InvocationDAO createInvocationDAO(); 
    public abstract ProcessorDAO createProcessorDAO();
    public abstract ClientDAO createClientDAO();
    public abstract SharedObjectDAO createSharedObjectDAO();
    public abstract QueueConfigDAO createQueueConfigDAO();
}
