/*
* Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * QueueServicesImpl
 * 
 * Created 08/03/2006
 */
package com.topcoder.farm.controller.queue.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.topcoder.farm.controller.dao.DAOFactory;
import com.topcoder.farm.controller.dao.InvocationDAO;
import com.topcoder.farm.controller.dao.ProcessorDAO;
import com.topcoder.farm.controller.dao.TransactionManager;
import com.topcoder.farm.controller.model.InvocationHeaderTO;
import com.topcoder.farm.controller.model.ProcessorProperties;
import com.topcoder.farm.controller.queue.InvocationQueueData;
import com.topcoder.farm.controller.queue.InvocationQueueHeaderData;

/**
 * Simple Implementation of the QueueServices interface
 *
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
 * <ol>
 *      <li>Update {@link #getActiveProcessorProperties()} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.0
 */
public class QueueServicesImpl implements QueueServices {
    private InvocationDAO invocationDao;
    private ProcessorDAO processorDao;
    private TransactionManager txMng;
   
    
    /**
     * Creates a new QueueServicesImpl using the  DAO objects and 
     * TransactionManager provided by DAOFactory configured
     */
    public QueueServicesImpl() {
        invocationDao = DAOFactory.getInstance().createInvocationDAO();
        processorDao = DAOFactory.getInstance().createProcessorDAO();
        txMng = DAOFactory.getInstance().getTransactionManager();
    }
    
    /**
     * <p>
     * get active processor properties.
     * </p>
     * @see QueueServices#getActiveProcessorProperties()
     * @return the processor properties sets.
     */
    public Set<ProcessorProperties> getActiveProcessorProperties() {
        txMng.beginTransaction();
        try {
            Set<ProcessorProperties> results = new HashSet<ProcessorProperties>();
            List<ProcessorProperties> activeProcessors = processorDao.findActiveProcessors();
            for (ProcessorProperties data : activeProcessors) {
                results.add(data);
            }
            txMng.commit();
            return results;
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        }
    }

    /**
     * @see QueueServices#getPendingInvocationHeaders()
     */
    public Iterator<InvocationQueueData> getPendingInvocationHeaders() {
        List<InvocationHeaderTO> invocations = null;
        txMng.beginTransaction();
        try {
            invocations = invocationDao.findPendingInvocations();
            txMng.commit();
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        }
        List<InvocationQueueData> result = new ArrayList<InvocationQueueData>(invocations.size());
        for (InvocationHeaderTO headerTO : invocations) {
            if (headerTO.getAssignAttempts() < InvocationDAO.MAX_ASSIGN_ATTEMPTS)
            result.add(buildQueueData(headerTO));
        }
        return result.iterator();
    }
    
    /**
     * @see QueueServices#getPendingAssignedInvocationHeaders(String)
     */
    public Iterator<InvocationQueueData> getPendingAssignedInvocationHeaders(String processorName) {
        List<InvocationHeaderTO> invocations = null;
        txMng.beginTransaction();
        try {
            invocations = invocationDao.findPendingAssignedInvocations(processorName);
            txMng.commit();
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        }
        List<InvocationQueueData> result = new ArrayList<InvocationQueueData>(invocations.size());
        for (InvocationHeaderTO headerTO : invocations) {
            txMng.beginTransaction();
            try {
                if (invocationDao.updateStatusAsPendingIfAssignedToProcessor(headerTO.getId(), processorName))  {
                    if (headerTO.getAssignAttempts() < InvocationDAO.MAX_ASSIGN_ATTEMPTS) {
                        result.add(buildQueueData(headerTO));
                    }
                }
                txMng.commit();
            } catch (RuntimeException e) {
                txMng.rollback();
                throw e;
            }
        }
        return result.iterator();
    }

    /**
     * @see QueueServices#getPendingAssignedInvocationHeaders(String)
     */
    public Iterator<InvocationQueueData> getPendingAssignedInvocationHeaders() {
        List<InvocationHeaderTO> invocations = null;
        txMng.beginTransaction();
        try {
            invocations = invocationDao.findPendingAssignedInvocations();
            txMng.commit();
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        }
        List<InvocationQueueData> result = new ArrayList<InvocationQueueData>(invocations.size());
        for (InvocationHeaderTO headerTO : invocations) {
            txMng.beginTransaction();
            try {
                if (invocationDao.updateStatusAsPendingIfAssignationTimeout(headerTO.getId()))  {
                    if (headerTO.getAssignAttempts() < InvocationDAO.MAX_ASSIGN_ATTEMPTS) {
                        result.add(buildQueueData(headerTO));
                    }
                }
                txMng.commit();
            } catch (RuntimeException e) {
                txMng.rollback();
                throw e;
            }
        }
        return result.iterator();
    }
    

    private InvocationQueueData buildQueueData(InvocationHeaderTO headerTO) {
        return new InvocationQueueData(
                    new InvocationQueueHeaderData(
                            headerTO.getId(), 
                            headerTO.getReceivedDate(), 
                            headerTO.getDropDate(), 
                            headerTO.getPriority()), 
                            headerTO.getRequirements());
    }

}
