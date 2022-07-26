/*
 * ClientData
 * 
 * Created 08/29/2006
 */
package com.topcoder.farm.controller.model;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ClientData {
    private static final long DEF_TTL = 30000;
    private static final long DEF_ASSIGN_TTL = 30000;
    /**
     * The database internal id of this object
     */
    private Long id;
    /**
     * The name of the client. It is actually the external id
     */
    private String name;
    /**
     * Priority used for request received from this client
     */
    private int priority;
    
    /**
     * The TimeToLive for the requests received from this client 
     */
    private long ttl = DEF_TTL;

    /**
     * The TimeToLive of one assignation for the requests received from this client 
     */
    private long assignationTtl = DEF_ASSIGN_TTL;

    private Integer dbVersion;
    
    public ClientData() {
    }
    
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
    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }
    public long getTtl() {
        return ttl;
    }
    public void setTtl(long ttl) {
        this.ttl = ttl;
    }
    public long getAssignationTtl() {
        return assignationTtl;
    }
    public void setAssignationTtl(long assignTtl) {
        this.assignationTtl = assignTtl;
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

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ClientData other = (ClientData) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
