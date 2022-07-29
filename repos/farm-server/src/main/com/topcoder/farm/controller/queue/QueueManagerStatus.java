package com.topcoder.farm.controller.queue;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * This class contains the status information of the QueueManager. <p>
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class QueueManagerStatus {
    /**
     * This timestamp indicates the moment in which the status began to be collected  
     */
    private Date startTime;
    
    /**
     * This timestamp indicates the moment in which the status ended to be collected  
     */
    private Date endTime;
    
    /**
     * Contains elements enqueue in the queue manager  
     */
    private Collection enqueueItems;
    
    /**
     * Contains status of each individual queue.
     * The key is the id of the ProcessorProperties 
     * The value contains a <code>QueueManagerItemStatus</code> with information about the queue status  
     */
    private Map<Long, QueueManagerItemStatus> queueStatus = new TreeMap();
    
    
    public Date getEndTime() {
        return endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    void setEnqueueItems(Collection enqueueItems) {
        this.enqueueItems = enqueueItems;
    }
    
    void setQueueStatus(Map<Long, QueueManagerItemStatus>  queueStatus) {
        this.queueStatus = queueStatus;
    }
    
    public Collection getEnqueueItems() {
        return enqueueItems;
    }
    
    public Map<Long, QueueManagerItemStatus> getQueueStatus() {
        return queueStatus;
    }
    
    void addQueueItem(QueueManagerItemStatus item) {
        queueStatus.put(item.getId(), item);
    }
    
    public int getSize() {
        return enqueueItems.size();
    }
    
    /**
     * Queue statius information
     * 
     * @author Diego Belfer (mural)
     * @version $Id$
     */
    public static class QueueManagerItemStatus {
        /**
         * Id of the processor properties defining this queue
         */
        private Long id;
        
        /**
         * Description of the processor properties defining this queue
         */
        private String description;
        /**
         * Number of items in the queue
         */
        private int size;
        
        /**
         * Items in the queue (in no particular order)
         */
        private List<InvocationQueueHeaderData> items;
        
        public QueueManagerItemStatus() {
        }
    
        public QueueManagerItemStatus(Long id, String description, int size, List<InvocationQueueHeaderData> items) {
            this.id = id;
            this.description = description;
            this.size = size;
            this.items = items;
        }
        
        public List<InvocationQueueHeaderData> getItems() {
            return items;
        }
        public void setItems(List<InvocationQueueHeaderData> items) {
            this.items = items;
        }
        public int getSize() {
            return size;
        }
        public void setSize(int size) {
            this.size = size;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}