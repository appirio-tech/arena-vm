/*
 * InvocationServicesImpl
 * 
 * Created 08/03/2006
 */
package com.topcoder.farm.controller.services;

import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.controller.dao.DAOFactory;
import com.topcoder.farm.controller.dao.InvocationDAO;
import com.topcoder.farm.controller.dao.NotFoundException;
import com.topcoder.farm.controller.dao.TransactionManager;
import com.topcoder.farm.controller.model.InvocationContext;
import com.topcoder.farm.controller.model.InvocationProperty;
import com.topcoder.farm.controller.processor.InvocationStatus;
import com.topcoder.farm.shared.invocation.Invocation;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InvocationServicesImpl implements InvocationServices {
    private Log log = LogFactory.getLog(InvocationServicesImpl.class);
    private final InvocationDAO invocationDao ;
    private final TransactionManager transactionManager;
    
    public InvocationServicesImpl() {
        invocationDao = DAOFactory.getInstance().createInvocationDAO();
        transactionManager = DAOFactory.getInstance().getTransactionManager();
    }
    
    public AssignedInvocation assignInvocationToProcessor(Long invocationId, String processorId) {
        AssignedInvocation returnValue = null;
        transactionManager.beginTransaction();
         try {
            if (invocationDao.updateStatusAsAssigned(invocationId, processorId)) {
                InvocationContext invocationCtx = invocationDao.findInvocationById(invocationId);
                fillUpObjectReferences(invocationCtx.getInvocation(), invocationCtx.getPropertiesToSet());
                returnValue = new AssignedInvocation(invocationCtx.getInvocation(), invocationCtx.getRequiredResources());
            }
            transactionManager.commit();
            return returnValue;
        } catch (RuntimeException e) {
            transactionManager.rollback();
            throw e;
        } catch (NotFoundException e) {
            transactionManager.rollback();
            log.info("The invocation was not found", e);
            return null;
        }
    }

    private void fillUpObjectReferences(Invocation invocation, Collection<InvocationProperty> propertiesToSet) {
        try {
            for (InvocationProperty property : propertiesToSet) {
                log.trace("setting " + property.getPropertyName() + " with value " + property.getSharedObject());
//                Field field = invocationClass.getDeclaredField(property.getPropertyName());
//                field.setAccessible(true);
//                field.set(invocation, property.getSharedObject().getObject());
                BeanUtils.setProperty(invocation, property.getPropertyName(), property.getSharedObject().getObject());
            }
        } catch (Exception e) {
            log.error("Error setting referenced objects to the invocation: " + invocation.getClass()+" references to set: "+propertiesToSet, e);
            throw new IllegalArgumentException("Referenced objects cannot be set to the invocation.", e);
        } 
    }
    
    /**
     * @see com.topcoder.farm.controller.services.InvocationServices#getAssignedInvocationsForProcessor(java.lang.String)
     */
    public List<InvocationStatus> getAssignedInvocationsForProcessor(String processorName) {
        transactionManager.beginTransaction();
        try {
            List<InvocationStatus> returnValue = invocationDao.findAssignedInvocationStatus(processorName);
            transactionManager.commit();
            return returnValue;
        } catch (RuntimeException e) {
            transactionManager.rollback();
            throw e;
        }
    }
}
