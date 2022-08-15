/*
* Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * MockDAOFactory
 *
 * Created 08/09/2006
 */
package com.topcoder.farm.controller.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.topcoder.farm.controller.api.InvocationRequestRef;
import com.topcoder.farm.controller.api.InvocationRequestSummaryItem;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.controller.dao.ClientDAO;
import com.topcoder.farm.controller.dao.DAOFactory;
import com.topcoder.farm.controller.dao.InvocationDAO;
import com.topcoder.farm.controller.dao.InvocationHeader;
import com.topcoder.farm.controller.dao.NotFoundException;
import com.topcoder.farm.controller.dao.ProcessorDAO;
import com.topcoder.farm.controller.dao.SharedObjectDAO;
import com.topcoder.farm.controller.dao.TransactionManager;
import com.topcoder.farm.controller.model.ClientData;
import com.topcoder.farm.controller.model.InvocationContext;
import com.topcoder.farm.controller.model.InvocationData;
import com.topcoder.farm.controller.model.InvocationHeaderTO;
import com.topcoder.farm.controller.model.InvocationProperty;
import com.topcoder.farm.controller.model.ProcessorData;
import com.topcoder.farm.controller.model.ProcessorProperties;
import com.topcoder.farm.controller.model.SharedObject;
import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.farm.shared.invocation.InvocationResult;
import com.topcoder.farm.shared.util.Pair;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
 * <ol>
 *      <li>Update {@link #createProcessorDAO()} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.0
 */
public class MockDAOFactory extends DAOFactory {
    private int requiredResourcesForTasks = 1;



    public InvocationDAO createInvocationDAO() {
        return new InvocationDAO() {

            public boolean updateStatusAsNotified(String id, String reqId) {
                return true;
            }

            public boolean updateStatusAsAssigned(Long invocationId, String processorId) {
                return true;
            }
            public boolean updateStatusAsPendingIfAssignationTimeout(Long id) {
                return true;
            }
            public boolean updateStatusAsPendingIfAssignedToProcessor(Long invocationId, String processorName) {
                return true;
            }

            public boolean updateResultOfInvocation(Long invocationId,
                    InvocationResult result) {
                return true;
            }

            public List<Pair<Long, InvocationResponse>> getPendingResponsesForClientName(
                    String clientId) {
                return new ArrayList<Pair<Long,InvocationResponse>>();
            }

            public List<InvocationHeaderTO> findPendingInvocations() {
                return new ArrayList<InvocationHeaderTO>();
            }

            public List<InvocationHeaderTO> findPendingAssignedInvocations(String processorName) {
                return new ArrayList<InvocationHeaderTO>();
            }

            public List<InvocationHeaderTO> findPendingAssignedInvocations() {
                return new ArrayList<InvocationHeaderTO>();
            }

            public InvocationContext findInvocationById(Long invocationId) {
                return new InvocationContext(new Invocation() {
                    public Object run(com.topcoder.farm.shared.invocation.InvocationContext context) {
                        return null;
                    }

                    public void customReadObject(CSReader reader) {

                    }

                    public void customWriteObject(CSWriter writer) {
                    }
                }, new HashSet<InvocationProperty>(), requiredResourcesForTasks);
            }

            public InvocationData findById(Long id) {
                return null;
            }

//            public List<InvocationData> findByClientName(String clientId) {
//                return new ArrayList<InvocationData>();
//            }

            public Set<Long> deleteForClientName(String id) {
                return new HashSet<Long>();
            }

            public InvocationData create(InvocationData data) {
                return data;
            }

            public InvocationData findByClientKey(String clientId, String id) {
                return null;
            }

            public int deleteDroppedOrNotified(int maxSize) {
                return 0;
            }

            public int countForClientKey(String name, String requestIdPrefix) {
                return 0;
            }

            public int countForClientName(String name) {
                return 0;
            }

            public Set<Long> deleteForClientKey(String name, String requestIdPrefix) {
                return null;
            }

            public List<InvocationRequestRef> findRefByClientKey(String clientName, String requestIdPrefix, int invStatus) {
                return null;
            }

            public List<InvocationRequestRef> findRefByClientKey(String clientName, String requestIdPrefix) {
                return null;
            }

            public Pair<String, InvocationResponse> getResponseById(Long id)  {
                return null;
            }

            public void deleteByIds(Collection<Long> ids) {
            }

            public List<InvocationRequestSummaryItem> generateSummaryByClientKey(String clientName, String requestIdPrefix, String delimiter, int delimiterCount) {
                return null;
            }

            public List<Long> getAllIds() {
                return null;
            }

            public List<InvocationStatus> findAssignedInvocationStatus(String processorName) {
                return null;
            }

            public Set<Long> getIdsForClientKey(String id, String requestIdPrefix) {
                return null;
            }

            @Override
            public InvocationHeader getHeaderById(Long id) throws NotFoundException {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }
    /**
     * <p>
     * build a mock processor dao.
     * </p>
     */
    public ProcessorDAO createProcessorDAO() {
        return new ProcessorDAO() {
            /**
             * <p>
             * find a processor by its name.
             * </p>
             * @param name the processor name
             * @return the processor data.
             */
            public ProcessorProperties findByName(String name) {
                ProcessorProperties data = new ProcessorProperties();
                data.setName(name);
                try {
                    data.setMaxRunnableTasks(Integer.parseInt(name));
                } catch (Exception e) {
                    data.setMaxRunnableTasks(1);
                }
                return data;
            }
            /**
             * <p>
             * find all the active processor list.
             * </p>
             * @return all the active processor data.
             */
            public List<ProcessorProperties> findActiveProcessors() {
                return null;
            }
            /**
             * Returns the processor with the given name
             *  
             * @param name The name of the processor
             * @param ip the ip address of the processor
             * @return The processor or <code>null</code> if not found
             */
            public ProcessorData findByNameAndIP(String name, String ip) {
                ProcessorData data = new ProcessorData();
                data.setName(name);
                data.setIp(ip);
                data.setProperties(findByName(name));
                data.setActive(true);
                return data;
            }
        };
    }

    public ClientDAO createClientDAO() {
        return new ClientDAO() {

            public ClientData findByName(String name) {
                ClientData data = new ClientData();
                data.setPriority(1);
                data.setTtl(120000);
                return data;
            }

        };
    }

    public TransactionManager getTransactionManager() {
        return new TransactionManager() {

            public void rollback() {
            }

            public void commit() {
            }

            public void beginTransaction() {
            }
        };
    }


    public SharedObjectDAO createSharedObjectDAO() {
        return new SharedObjectDAO() {

            public SharedObject findById(Long id)  {
                return null;
            }

            public SharedObject findByClientKey(String clientOwner, String objectKey) {
                return null;
            }

            public int countByClientKey(String clientOwner, String objectKey) {
                return 0;
            }

            public int deleteAllForClient(String clientOwner) {
                return 0;
            }

            public SharedObject create(SharedObject data) {
                data.setId(new Long(1));
                return data;
            }

            public int deleteUnreferencedOldObjects(Date maxStorageDate) {
                return 0;
            }

            public int deleteForClient(String clientOwner, String objectKeyPrefix) {
                return 0;
            }

            public int deleteUnreferenced() {
                return 0;
            }
        };
    }

    public void setRequiredResourcesForTasks(int requiredResourcesForTasks) {
        this.requiredResourcesForTasks = requiredResourcesForTasks;
    }
}
