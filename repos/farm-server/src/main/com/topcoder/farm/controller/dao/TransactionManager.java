/*
 * TransactionManager
 * 
 * Created 08/03/2006
 */
package com.topcoder.farm.controller.dao;

/**
 * Transaction manager interface that allows to manage transaction in the
 * services without knowledge of DAO implementation
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface TransactionManager {
    /**
     * Begins a new transaction.
     */
    public void beginTransaction();
    
    /**
     * Commits the current transaction.
     */
    public void commit();
    
    
    /**
     * Rollbacks the current transaction if one is active.
     */
    public void rollback();
}
