/*
* Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * ProcessorData
 * 
 * Created 07/27/2006
 */
package com.topcoder.farm.controller.model;


/**
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
 * <ol>
 *      <li>Add {@link #ip} field and getter,setter method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), savon_cn
 * @version 1.0
 */
public class ProcessorData {
    /**
     * The database internal id of this object
     */
    private Long id;
    /**
     * The name of this Processor, it is actually an external identifier
     */
    private String name;
    
    /**
     * The properties this processor has
     */
    private ProcessorProperties properties;
    
    /**
     * The maximun number of task this processor can execute simultaneously 
     */
    private int maxRunnableTasks;
    
    /**
     * If this processor is active. An active processor is a processor that could 
     * connect to a controller. Queues are created for active processor even if the processor
     * is not actually connected. If the processor is not active, when it tries to connect it will
     * received an exception if no other processor has the same properties
     */
    private boolean active;
    
    private Integer dbVersion;
    /**
     * the ip address of specific processor.
     */
    private String ip;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public ProcessorProperties getProperties() {
        return properties;
    }
    
    public void setProperties(ProcessorProperties properties) {
        this.properties = properties;
    }

    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public Integer getDbVersion() {
        return dbVersion;
    }
    public void setDbVersion(Integer dbVersion) {
        this.dbVersion = dbVersion;
    }

    public int hashCode() {
        return name.hashCode();
    }
    /**
     * Getter of the ip address.
     * @return the ip address.
     */
    public String getIp() {
        return ip;
    }
    /**
     * Setter the ip address.
     * @param ip the ip address.
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ProcessorData other = (ProcessorData) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    public int getMaxRunnableTasks() {
        return maxRunnableTasks;
    }

    public void setMaxRunnableTasks(int maxRunnableTasks) {
        this.maxRunnableTasks = maxRunnableTasks;
    }    
}
