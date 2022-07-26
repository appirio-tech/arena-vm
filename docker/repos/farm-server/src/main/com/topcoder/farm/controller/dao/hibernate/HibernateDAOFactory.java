/*
 * HibernateDAOFactory
 * 
 * Created 08/03/2006
 */
package com.topcoder.farm.controller.dao.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.controller.dao.ClientDAO;
import com.topcoder.farm.controller.dao.DAOFactory;
import com.topcoder.farm.controller.dao.InvocationDAO;
import com.topcoder.farm.controller.dao.ProcessorDAO;
import com.topcoder.farm.controller.dao.QueueConfigDAO;
import com.topcoder.farm.controller.dao.SharedObjectDAO;
import com.topcoder.farm.controller.dao.TransactionManager;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class HibernateDAOFactory extends DAOFactory {

    public InvocationDAO createInvocationDAO() {
        return new HibernateInvocationDAO();
    }

    public ProcessorDAO createProcessorDAO() {
        return new HibernateProcessorDAO();
    }

    public ClientDAO createClientDAO() {
        return new HibernateClientDAO();
    }

    public SharedObjectDAO createSharedObjectDAO() {
        return new HibernateSharedObjectDAO();
    }
    
    public TransactionManager getTransactionManager() {
        return new HibernateTransactionManager();
    }
    
    public QueueConfigDAO createQueueConfigDAO() {
    	return new HibernateQueueConfigDAO();
    }
    
    public static class HibernateTransactionManager implements TransactionManager {
        private Log log = LogFactory.getLog(HibernateTransactionManager.class);
        
        public void beginTransaction() {
            log.debug("beginTransaction");
            HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
            log.debug("beginTransaction Succeeded");

        }

        public void commit() {
            log.debug("commit");
            HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
            log.debug("commit Succeded");

        }

        public void rollback() {
            log.debug("rollback");
            HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
            log.debug("rollback Succeeded");
        }
    }
}
