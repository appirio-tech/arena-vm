/*
* Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * QueueManager
 * 
 * Created 08/01/2006
 */
package com.topcoder.farm.controller.queue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.controller.model.MatchProcessException;
import com.topcoder.farm.controller.model.ProcessorProperties;
import com.topcoder.farm.controller.queue.services.QueueServices;

/**
 * The QueueManager class is responsible for managing all invocation queues
 * on the controller.
 * 
 * Impl: 
 * This QueueManager contains a PriorityQueue for each active ProcessorProperties
 * provided by the QueueServices. Each queue contains all pending invocations that can 
 * be executed on any processor whose properties are equal to ProcessorProperties for which the queue 
 * was created for.
 * 
 * During initialization of the QueueManager, queues are initialized with all the invocations provided by
 * the QueueServices. After initialization, all new invocations received by the controller must be reported 
 * to the QueueManager using the method <code>enqueue</code>. 
 *
 * The comparator class used for queue sorting can be configured using the 
 * system property {@link QueueManager#QUEUE_COMPARATOR_CLASS_KEY}. 
 *
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
 * <ol>
 *      <li>Update {@link #getQueueForProcessor(ProcessorProperties processor)} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.0
 */
public class QueueManager {
    /**
     * Key used to allow comparator configuration
     */
    private static final String QUEUE_COMPARATOR_CLASS_KEY = "com.topcoder.farm.controller.queue.comparator_class";

    /**
     * Log instance for this class
     */
    private Log log = LogFactory.getLog(QueueManager.class);
    
    /**
     * The services used for obtaining ProcessorProperties and pending invocations 
     */
    private QueueServices services;
    
    /**
     * This list contains all Pairs(ProcessorProperties,PriorityQueue) that this QueueManager
     * is handling 
     */
    private List<Pair> queues;
    
    
    /**
     * Map containing all enqueue items
     */
    private Map<Long, InvocationQueueHeaderData> enqueueItems = new ConcurrentHashMap<Long, InvocationQueueHeaderData>();
    
    /**
     * Creates a new QueueManager
     * 
     * @param services Services to use for QueueManager initialization 
     */
    public QueueManager(QueueServices services) {
        this.services = services;
        initialize();
    }

    
    /**
     * Initializes this QueueManager.
     * 
     * Impl: This method makes the QueueManager to create a queue for each active ProcessorProperties.
     *       And to add all invocations provided by the QueueServices to the matching queues.  
     *
     */
    public void initialize() {
        createQueues();
        fillQueues();
    }

    /**
     * Creates a queue for each ProcessorProperties and associates them.
     * 
     */
    private void createQueues() {
        Comparator<InvocationQueueHeaderData> comparator = resolveComparator();
        Set<ProcessorProperties> activeProperties = services.getActiveProcessorProperties();
        queues = new ArrayList<Pair>(activeProperties.size());
        if (log.isInfoEnabled()) {
            log.info("Creating queues for "+activeProperties.size()+" active processor properties");
        }        
        for (ProcessorProperties properties : activeProperties) {
            queues.add(new Pair(properties, new PriorityBlockingQueue<InvocationQueueHeaderData>(10, comparator)));
        }
    }

    private Comparator<InvocationQueueHeaderData> resolveComparator() {
        String className = System.getProperty(QUEUE_COMPARATOR_CLASS_KEY);
        if (className != null) {
            try {
                return  (Comparator<InvocationQueueHeaderData>) Class.forName(className).newInstance();
            } catch (Exception e) {
                log.warn("Invalid comparator class: " + className, e);
            }
        }
        log.info("Using default comparator in QueueManager");
        return new DefaultQueueComparator();
    }
    
    /**
     * Load queues with all invocation reported by the QueueServices. 
     * if an Invocation could not be added to at least one queue, the invocation
     * is discared.
     */
    private void fillQueues() {
         Iterator<InvocationQueueData> pendingInvocationHeaders = services.getPendingInvocationHeaders();
         
         for (; pendingInvocationHeaders.hasNext();) {
             InvocationQueueData invocation = pendingInvocationHeaders.next();
             try {
                enqueue(invocation);
            } catch (UnavailableProcessorForRequirementsException e) {
                log.warn("Invocation could not be enqueue on any queue. InvocationId="+invocation.getHeader().getId());
            }
         }
    }
    
    /**
     * Loads queues with all invocations reported by the QueueServices which were assigned to the given processor
     * and are not solved yet
     * 
     * @return a list containing Ids of all enqueued invocations
     */
    public List<Long> rescheduleAssignedInvocation(String processorName) {
        Iterator<InvocationQueueData> pendingInvocationHeaders = services.getPendingAssignedInvocationHeaders(processorName);
        List<Long> ids = processAssignedInvocations(pendingInvocationHeaders);
        if (log.isInfoEnabled()) {
            log.info(""+ids.size()+" invocations were rescheduled for processor="+processorName);
        }
        return ids;
    }

    /**
     * Loads queues with all invocations reported by the QueueServices which were assigned to the a processor
     * and for which the asssignation ttl has timeout  
     * 
     * @return a list containing Ids of all enqueued invocations
     */
    public List<Long> scheduleDroppedAssignations() {
        Iterator<InvocationQueueData> pendingInvocationHeaders = services.getPendingAssignedInvocationHeaders();
        List<Long> ids = processAssignedInvocations(pendingInvocationHeaders);
        if (log.isInfoEnabled()) {
            log.info(""+ids.size()+" invocations were rescheduled due to assignation ttl time out");
        }        
        return ids;
    }

    private List<Long> processAssignedInvocations(Iterator<InvocationQueueData> pendingInvocationHeaders) {
        List<Long> ids = new LinkedList<Long>();
        for (; pendingInvocationHeaders.hasNext();) {
            InvocationQueueData invocation = pendingInvocationHeaders.next();
            try {
                if (enqueue(invocation)) {
                    ids.add(new Long(invocation.getHeader().getId()));
                }
            } catch (UnavailableProcessorForRequirementsException e) {
                log.warn("Invocation could not be enqueue on any queue. InvocationId="+invocation.getHeader().getId());
            }
        }
        return ids;
    }
    
    /**
     * Returns an InvocationIdDequeuer for the specified processor.
     * This method cannot be invoked before initialize method.
     * 
     * @param processor The processor data
     * 
     * @return The InvocationIdDequeuer for the processor
     * @throws IllegalStateException if this QueueManager is not handling or cannot manage a 
     *                               queue for the processor
     */
    public InvocationIdDequeuer getQueueForProcessor(ProcessorProperties processor) {
        for (Pair queue : queues) {
            if (processor.equals(queue.properties)) {
                return new TaskObtainerImpl(queue.queue);
            }
        }
        throw new IllegalStateException("The specified processor ("+ processor.getName() +") contains inactive processor properties");
    }
    
    
    /**
     * Enqueue the Invocation for processing.
     * This method cannot be invoked before initialize method.
     * 
     * @param data The InvocationData to enqueue on the queue manager.
     * 
     * @throws UnavailableProcessorForRequirementsException If the requeriments specified are not matched by 
     *          any of the processorProperties availables
     * 
     * @return true if the invocation was enqueued
     */
    public boolean enqueue(InvocationQueueData data) throws UnavailableProcessorForRequirementsException {
        if (log.isDebugEnabled()) {
            log.debug("Enqueuing invocation "+data.getHeader().getId());
        }
        Long id = new Long(data.getHeader().getId());
        if (enqueueItems.containsKey(id)) {
            log.info("Invocation id=" + id+ " already enqueued");
            return false;
        } else {
            boolean assigned = false;
            enqueueItems.put(id, data.getHeader());
            for (Pair queue : queues) {
                try {
                    if (queue.properties.match(data.getRequirements())) {
                        queue.queue.offer(data.getHeader());
                        assigned = true;
                    }
                } catch (MatchProcessException e) {
                    log.error("Error processing requeriments of invocationId="+id, e);
                }
            }
            if (!assigned) {
                enqueueItems.remove(id);
                throw new UnavailableProcessorForRequirementsException("Invocation "+id+" cannot be processed in any active processor.");
            }
            if (log.isDebugEnabled()) {
                log.debug("Enqueued invocation "+data.getHeader().getId());
            }
            return true;
        }
    }
    
    /**
     * This method attempts to take the <code>data</code> instance removed from the <code>queue</code>.
     * If it is able to do it, remove the element from all queues.
     * 
     * @param data the element remove from the queue that is trying to be taken by the caller 
     * @param queue The queue from which <code>data</code> was polled
     * 
     * @return true if the <code>data</code> could be taken
     */
    public boolean tryToAssign(InvocationQueueHeaderData data, BlockingQueue<InvocationQueueHeaderData> queue) {
        if (enqueueItems.remove(new Long(data.getId())) == null) {
            return false;
        }
        if (log.isDebugEnabled()) {
            log.debug("Assigning invocation="+data.getId()+" removing invocation from queues");
        }
        for (Pair queuePair : queues) {
            if (queuePair.queue != queue) {
                if (queuePair.queue.remove(data)) {
                    if (log.isTraceEnabled()) {
                        log.trace("Inv id="+data.getId()+"     removed from queue id="+queuePair.properties.getId());
                    }
                } else {
                    if (log.isTraceEnabled()) {
                        log.trace("Inv id="+data.getId()+" NOT removed from queue id="+queuePair.properties.getId());
                    }
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Done removing");
        }
        if (data.getDropDate() < System.currentTimeMillis()) {
            if (log.isInfoEnabled()) {
                log.info("Dropping invocation due to timeout id="+data.getId());
            }            
            return false;
        }
        return true;
    }

    /**
     * Removes from all queues, the invocations referred in the idsToRemove set  
     * 
     * @param idsToRemove Ids of the invocations to remove from the queues
     */
    public void removeAll(Collection<Long> idsToRemove) {
        if (log.isInfoEnabled()) {
            log.info("Removing "+idsToRemove.size()+" items from all active queues");
        }
        Collection<Long> ids = null;
        if (idsToRemove instanceof Set) {
            ids  = idsToRemove;
        } else {
            ids = new HashSet(idsToRemove);
        }
        HashMap<Long,InvocationQueueHeaderData> itemsToRemove = new HashMap(enqueueItems);
        enqueueItems.keySet().removeAll(ids);
        itemsToRemove.keySet().retainAll(ids);
        //Workaround to solve slow performance on queue
        Collection<InvocationQueueHeaderData> items = itemsToRemove.values();
        for (Pair queuePair : queues) {
            BlockingQueue<InvocationQueueHeaderData> queue = queuePair.queue;
            if (items.size()<10) {
                for (InvocationQueueHeaderData data : items) {
                    queue.remove(data);
                }
            } else {
                HashSet<InvocationQueueHeaderData> enqueuedItems = new HashSet<InvocationQueueHeaderData>(queue.size()+100);
                queue.drainTo(enqueuedItems);
                HashSet itemsSet = new HashSet(items);
                enqueuedItems.removeAll(itemsSet);
                queue.addAll(enqueuedItems);
            }
        }
        if (log.isInfoEnabled()) {
            log.info("Removed "+idsToRemove.size()+" items from all active queues");
        }
    }
    
    
    
    /**
     * Returns the Status of this Queue Manager
     *  
     * Note: No special synchronization is used to obtain the status 
     * of the manager. Hence, queue status and enqueue items could have
     * small differences. E.g. one item has been removed from one queue
     * but it has not been removed from the others.
     * 
     * @param dumpQueueItems Indicates if queue items should be reported in the status (High cost)
     * @return The status
     */ 
    public QueueManagerStatus getStatus(boolean dumpQueueItems) {
        QueueManagerStatus status = new QueueManagerStatus();
        status.setStartTime(new Date());
        for (Pair p : queues) {
            int size;
            List<InvocationQueueHeaderData> items = null;
            if (dumpQueueItems) {
                items = new ArrayList<InvocationQueueHeaderData>(p.queue);
                size = items.size();
            } else {
                size = p.queue.size();
            }
            status.addQueueItem(new QueueManagerStatus.QueueManagerItemStatus(p.properties.getId(), p.properties.getDescription(), size, items));
        }
        Set<Long> items = new HashSet<Long>(enqueueItems.keySet());
        status.setEndTime(new Date());
        status.setEnqueueItems(Collections.unmodifiableCollection(items));
        return status;
    }
    
    /**
     * Return the number of items enqueued in this QueueManager
     */
    public int size() {
        return enqueueItems.size();
    }

    public void release() {
        enqueueItems.clear();
        queues.clear();
    }

    
    /**
     * Simple Pair class that contains ProcessorProperties and Queue associated to those properties
     */
    private static class Pair {
        ProcessorProperties properties;
        BlockingQueue<InvocationQueueHeaderData> queue;
        
        public Pair(ProcessorProperties properties, BlockingQueue<InvocationQueueHeaderData> queue) {
            this.properties = properties;
            this.queue = queue;
        }
    }
    
    
    public class TaskObtainerImpl implements InvocationIdDequeuer {
        BlockingQueue<InvocationQueueHeaderData> queue;

        public TaskObtainerImpl(BlockingQueue<InvocationQueueHeaderData> queue) {
            this.queue = queue;
        }

        public Long dequeueInvocationId() throws InterruptedException {
            while (true) {
                InvocationQueueHeaderData data = queue.take();
                if (tryToAssign(data, queue)) {
                    return new Long(data.getId());
                }
            }
        }
    }


}
