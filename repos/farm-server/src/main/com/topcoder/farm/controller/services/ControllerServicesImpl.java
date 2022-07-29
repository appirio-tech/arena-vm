/*
 * ControllerServicesImpl
 *
 * Created 08/02/2006
 */
package com.topcoder.farm.controller.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.topcoder.farm.controller.api.InvocationRequest;
import com.topcoder.farm.controller.api.InvocationRequestRef;
import com.topcoder.farm.controller.api.InvocationRequestSummaryItem;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.controller.configuration.ControllerConfiguration;
import com.topcoder.farm.controller.configuration.ControllerConfigurationProvider;
import com.topcoder.farm.controller.dao.ClientDAO;
import com.topcoder.farm.controller.dao.DAOFactory;
import com.topcoder.farm.controller.dao.DuplicateKeyException;
import com.topcoder.farm.controller.dao.InvocationDAO;
import com.topcoder.farm.controller.dao.InvocationHeader;
import com.topcoder.farm.controller.dao.NotFoundException;
import com.topcoder.farm.controller.dao.QueueConfigDAO;
import com.topcoder.farm.controller.dao.ReferencedObjectException;
import com.topcoder.farm.controller.dao.SharedObjectDAO;
import com.topcoder.farm.controller.dao.TransactionManager;
import com.topcoder.farm.controller.dao.hibernate.HibernateDAOFactory;
import com.topcoder.farm.controller.dao.hibernate.HibernateUtil;
import com.topcoder.farm.controller.exception.DuplicatedIdentifierException;
import com.topcoder.farm.controller.exception.SharedObjectReferencedException;
import com.topcoder.farm.controller.model.ClientData;
import com.topcoder.farm.controller.model.InvocationData;
import com.topcoder.farm.controller.model.QueueConfig;
import com.topcoder.farm.controller.model.SharedObject;
import com.topcoder.farm.controller.model.SharedObjectImpl;
import com.topcoder.farm.shared.invocation.InvocationResult;
import com.topcoder.farm.shared.util.Pair;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
@Service
public class ControllerServicesImpl implements ControllerServices {
    private Log log = LogFactory.getLog(ControllerServicesImpl.class);
    private InvocationDAO invocationDao;
    private ClientDAO clientDao;
    private SharedObjectDAO sharedObjectDao;
    private TransactionManager txMng;
    private QueueConfigDAO queueConfigDao;
    private int maxBulkSize = 100;
    private int maxSharedObjectStorageTime = 1800000;

	public ControllerServicesImpl() {
		// TODO: these should be autowired to the constructor
		
		ControllerConfiguration config = ControllerConfigurationProvider.getConfiguration();
		try {
	        DAOFactory.configureInstance(new HibernateDAOFactory());
	        HibernateUtil.initialize(config.getDatabaseConfigurationFile());

			invocationDao = DAOFactory.getInstance().createInvocationDAO();
			clientDao = DAOFactory.getInstance().createClientDAO();
			sharedObjectDao = DAOFactory.getInstance().createSharedObjectDAO();
			queueConfigDao = DAOFactory.getInstance().createQueueConfigDAO();
			txMng = DAOFactory.getInstance().getTransactionManager();
		} catch (Throwable t) {
			log.error("Unable to initialize hibernate: " + t.getMessage(), t);
		}
	}
    
    public ControllerServicesImpl(int maxBulkSize, int maxSharedObjectStorageTime) {
    	this();
//        log.info("Initializing ControllerServices");
//        invocationDao = DAOFactory.getInstance().createInvocationDAO();
//        clientDao = DAOFactory.getInstance().createClientDAO();
//        sharedObjectDao = DAOFactory.getInstance().createSharedObjectDAO();
//        txMng = DAOFactory.getInstance().getTransactionManager();
        this.maxBulkSize = maxBulkSize;
        this.maxSharedObjectStorageTime = maxSharedObjectStorageTime;
    }

    public InvocationData storeInvocationRequest(String clientName, InvocationRequest request) throws DuplicatedIdentifierException {
        log.info("Scheduling invocation request: " + request + " for client: " + clientName );
        txMng.beginTransaction();
        try {
            ClientData clientData = clientDao.findByName(clientName);
            log.debug("Got client");
            InvocationData data = buildInvocationData(clientName, request);
            addSharedObjectReferences(clientName, request, data);
            data.setPriority(clientData.getPriority()+request.getPriority());
            data.setDropDate(generateDropDate(data, clientData));
            data.setAssignationTtl(clientData.getAssignationTtl());
            log.debug("Creating invocation");
            InvocationData result = invocationDao.create(data);
            txMng.commit();
            return result;
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        } catch (DuplicateKeyException e) {
            txMng.rollback();
            throw new DuplicatedIdentifierException("An invocation with id="+request.getId()+" already exists in the farm");
        }

    }
    
	public List<QueueConfig> getQueueConfigs() {
		txMng.beginTransaction();
		try {
			List<QueueConfig> configs = queueConfigDao.getQueueConfigs();
			txMng.commit();
			return configs;
		} catch (RuntimeException rte) {
			txMng.rollback();
			throw rte;
		}

	}

    private void addSharedObjectReferences(String clientName, InvocationRequest request, InvocationData inv) {
        if (log.isDebugEnabled()) {
            log.debug("addSharedObjectReferences: client="+clientName+" id="+request.getId());
        }
        for (Iterator it = request.getSharedObjectRefs().entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Entry) it.next();
            String propertyNameToSet = (String) entry.getKey();
            String sharedObjectName = (String) entry.getValue();
            SharedObject shared = sharedObjectDao.findByClientKey(clientName, sharedObjectName);
            inv.addPropertyToSet(propertyNameToSet, shared);
        }
        log.debug("addSharedObjectReferences done");
    }


    public Set<Long> cancelPendingRequests(String clientName) {
        log.info("Cancelling all requests for : " + clientName);
        txMng.beginTransaction();
        try {
            Set<Long> result = invocationDao.deleteForClientName(clientName);
            sharedObjectDao.deleteAllForClient(clientName);
            txMng.commit();
            return result;
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        } catch (ReferencedObjectException e) {
            txMng.rollback();
            log.error("ERROR: An invocation was added while Cancelling pendings request", e);
            throw new IllegalStateException("Multiple requests have been received for the client");
        }
    }


    public Collection<Long> cancelAllRequests() {
        log.info("Cancelling all requests");
        List<Long> result;

        //We remove one by one, short transactions.. allowing better concurrency
        txMng.beginTransaction();
        try {
            log.debug("Obtaining all invocation ids");
            result = invocationDao.getAllIds();
            txMng.commit();
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        }

        bulkDeleteInvocations(result);

        txMng.beginTransaction();
        try {
            sharedObjectDao.deleteUnreferenced();
            txMng.commit();
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        }
        return result;

    }

    public void bulkDeleteInvocations(Collection<Long> result) {
        ArrayList blockToDelete = new ArrayList(maxBulkSize);
        for (Iterator it= result.iterator(); it.hasNext();) {
            blockToDelete.clear();
            for (int i =0; it.hasNext() && i < maxBulkSize; i++) {
                blockToDelete.add(it.next());
            }
            txMng.beginTransaction();
            try {
                log.debug("Deleting invocation block: "+blockToDelete.size());
                invocationDao.deleteByIds(blockToDelete);
                txMng.commit();
            } catch (RuntimeException e) {
                txMng.rollback();
                log.warn("Could not delete invocations in list with ids="+blockToDelete,e);
            }
        }
    }

    public  List<Pair<Long, InvocationResponse>> getPendingResponses(String clientName) {
        log.info("Obtaining all pending responses for : " + clientName);
        txMng.beginTransaction();
        try {
            List<Pair<Long,InvocationResponse>> result = invocationDao.getPendingResponsesForClientName(clientName);
            txMng.commit();
            return result;
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        }
    }

    public boolean reportProcessorInvocationResponse(Long invocationId, InvocationResult result) {
        log.info("Saving response processorRequestId="+invocationId+" result="+result);
        txMng.beginTransaction();
        try {
            boolean updated = invocationDao.updateResultOfInvocation(invocationId, result);
            txMng.commit();
            return updated;
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        }
    }

    public Pair<String, InvocationResponse> generateClientResponse(Long invocationId) {
        log.info("Generating client response for invocationId="+invocationId);
        Pair<String, InvocationResponse> response = null;
        txMng.beginTransaction();
        try {
            response = invocationDao.getResponseById(invocationId);
            txMng.commit();
            return response;
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        } catch (NotFoundException e) {
            txMng.rollback();
            log.info("Could not generate response for Invocation. Returning null.", e);
            return null;
        }
    }
    
    
    public InvocationHeader getInvocationHeader(Long invocationId) {
        log.info("Obtaining invocation header for invocationId="+invocationId);
        InvocationHeader response = null;
        txMng.beginTransaction();
        try {
            response = invocationDao.getHeaderById(invocationId);
            txMng.commit();
            return response;
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        } catch (NotFoundException e) {
            txMng.rollback();
            log.info("Could not obtain header for Invocation. Returning null.", e);
            return null;
        }
    }

    public void setInvocationAsNotified(String clientId, String requestId) {
        log.info("Setting invocation as notified clientId="+clientId+" requestId "+requestId);
        txMng.beginTransaction();
        try {
            invocationDao.updateStatusAsNotified(clientId, requestId);
            txMng.commit();
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        }
    }


    /**
     * @see com.topcoder.farm.controller.services.ControllerServices#storeSharedObject(java.lang.String, java.lang.String, java.lang.Object)
     */
    public void storeSharedObject(String clientId, String sharedObjectKey, Object sharedObject) throws DuplicatedIdentifierException {
        log.info("Storing shared object.");
        txMng.beginTransaction();
        try {
            SharedObject data = new SharedObjectImpl(clientId, sharedObjectKey, sharedObject);
            sharedObjectDao.create(data);
            txMng.commit();
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        } catch (DuplicateKeyException e) {
            txMng.rollback();
            throw new DuplicatedIdentifierException("A shared object with key="+sharedObjectKey+" already exists in the farm");
        }
    }


    /**
     * @see com.topcoder.farm.controller.services.ControllerServices#countSharedObjects(java.lang.String, java.lang.String)
     */
    public Integer countSharedObjects(String clientId, String objectKeyPrefix) {
        log.info("Counting shared objects.");
        txMng.beginTransaction();
        try {
            Integer result = new Integer(sharedObjectDao.countByClientKey(clientId, objectKeyPrefix));
            txMng.commit();
            return result;
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        }
    }

    /**
     * @see com.topcoder.farm.controller.services.ControllerServices#removeSharedObjects(java.lang.String, java.lang.String)
     */
    public void removeSharedObjects(String clientId, String objectKeyPrefix) throws SharedObjectReferencedException {
        log.info("Removing shared objects.");
        txMng.beginTransaction();
        try {
            sharedObjectDao.deleteForClient(clientId, objectKeyPrefix);
            txMng.commit();
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        } catch (ReferencedObjectException e) {
            log.debug("Trying to delete a referenced shared object", e);
            txMng.rollback();
            throw new SharedObjectReferencedException("An attemp to delete a referenced object was made");
        }
    }

    private Date generateDropDate(InvocationData data, ClientData clientData) {
        long receiveTime = data.getReceivedDate().getTime();
        long ttl = clientData.getTtl();
        if (ttl <= 0 || ttl > InvocationData.MAX_TTL_TIME_MS) {
            ttl = InvocationData.MAX_TTL_TIME_MS;
        }
        return new Date(receiveTime+ttl);
    }

    private InvocationData buildInvocationData(String clientName, InvocationRequest request) {
        InvocationData data = new InvocationData();
        data.setClientName(clientName);
        data.setClientRequestId(request.getId());
        data.setClientAttachment(request.getAttachment());
        data.setPriority(1);
        data.setInvocation(request.getInvocation());
        data.setRequirements(request.getRequirements());
        data.setRequiredResources(request.getRequiredResources());
        data.setAsPending();
        //DropDate is not set here
        return data;
    }

    /**
     * @see com.topcoder.farm.controller.services.ControllerServices#purgeInvocations()
     */
    public void purgeInvocations() {
        log.info("Purging invocations");
        int size = 0;
        do {
            txMng.beginTransaction();
            try {
                size = invocationDao.deleteDroppedOrNotified(maxBulkSize);
                log.info("Purged "+size+" invocations");
                txMng.commit();
            } catch (RuntimeException e) {
                txMng.rollback();
                throw e;
            }
        } while (size == maxBulkSize);
    }

    /**
     * @see com.topcoder.farm.controller.services.ControllerServices#purgeUnusedSharedObjects()
     */
    public void purgeUnusedSharedObjects() {
        log.info("Purging shared objects");
        txMng.beginTransaction();
        try {
            int size = sharedObjectDao.deleteUnreferencedOldObjects(new Date(System.currentTimeMillis() - maxSharedObjectStorageTime));
            txMng.commit();
            log.info("Purged "+size+" shared objects");
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        }
    }

    /**
     * @see com.topcoder.farm.controller.services.ControllerServices#cancelPendingRequests(java.lang.String, java.lang.String)
     */
    public Set<Long> cancelPendingRequests(String clientName, String requestIdPrefix) {
        log.info("Cancelling all requests for : " + clientName+" using prefix: "+requestIdPrefix);
        Set<Long> result = null;
        txMng.beginTransaction();
        try {
            result = invocationDao.getIdsForClientKey(clientName, requestIdPrefix);
            txMng.commit();
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        }

        bulkDeleteInvocations(result);

        return result;
    }

    /**
     * @see com.topcoder.farm.controller.services.ControllerServices#countPendingRequests(java.lang.String)
     */
    public Integer countPendingRequests(String clientName) {
        log.info("Counting pending request for client");
        txMng.beginTransaction();
        try {
            int cnt = invocationDao.countForClientName(clientName);
            txMng.commit();
            return new Integer(cnt);
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        }
    }

    /**
     * @see com.topcoder.farm.controller.services.ControllerServices#countPendingRequests(java.lang.String, java.lang.String)
     */
    public Integer countPendingRequests(String clientName, String requestIdPrefix) {
        log.info("Counting pending request for client using prefix");
        txMng.beginTransaction();
        try {
            int cnt = invocationDao.countForClientKey(clientName, requestIdPrefix);
            txMng.commit();
            return new Integer(cnt);
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        }
    }

    /**
     * @see com.topcoder.farm.controller.services.ControllerServices#getEnqueuedRequests(java.lang.String, java.lang.String)
     */
    public List<InvocationRequestRef> getEnqueuedRequests(String clientName, String requestIdPrefix) {
        log.info("Obtaining list of enqueued requests for client");
        txMng.beginTransaction();
        try {
            List<InvocationRequestRef> list = invocationDao.findRefByClientKey(clientName, requestIdPrefix, InvocationData.STATUS_PENDING);
            txMng.commit();
            return list;
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        }
    }

    /**
     * @see com.topcoder.farm.controller.services.ControllerServices#getPendingRequests(java.lang.String, java.lang.String)
     */
    public List<InvocationRequestRef> getPendingRequests(String clientName, String requestIdPrefix) {
        log.info("Obtaining list of pending requests for client");
        txMng.beginTransaction();
        try {
            List<InvocationRequestRef> list = invocationDao.findRefByClientKey(clientName, requestIdPrefix);
            txMng.commit();
            return list;
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        }
    }

    /**
     * @see com.topcoder.farm.controller.services.ControllerServices#getEnqueuedRequestsSummary(java.lang.String, java.lang.String, java.lang.String, int)
     */
    public List<InvocationRequestSummaryItem> getEnqueuedRequestsSummary(String clientName, String requestIdPrefix, String delimiter, int delimiterCount) {
        log.info("Obtaining summary list of enqueued requests for client");
        txMng.beginTransaction();
        try {
            List<InvocationRequestSummaryItem> list = invocationDao.generateSummaryByClientKey(clientName, requestIdPrefix, delimiter, delimiterCount);
            txMng.commit();
            return list;
        } catch (RuntimeException e) {
            txMng.rollback();
            throw e;
        }
    }


	public int getMaxBulkSize() {
		return maxBulkSize;
	}


	public void setMaxBulkSize(int maxBulkSize) {
		this.maxBulkSize = maxBulkSize;
	}


	public int getMaxSharedObjectStorageTime() {
		return maxSharedObjectStorageTime;
	}


	public void setMaxSharedObjectStorageTime(int maxSharedObjectStorageTime) {
		this.maxSharedObjectStorageTime = maxSharedObjectStorageTime;
	}
    
    
}
