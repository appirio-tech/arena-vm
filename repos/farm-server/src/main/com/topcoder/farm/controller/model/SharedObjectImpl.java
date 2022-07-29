/*
 * SharedObjectImpl
 * 
 * Created 09/15/2006
 */
package com.topcoder.farm.controller.model;

import java.util.Date;

/**
 * A SharedObjectImpl is an Object stored in the farm by a client that
 * can be referenced from any invocation of the client.<p>
 * SharedObjects are automatically remove by the controllers if no invocation
 * references it. <p>
 * Client operations uses object key prefixes to operate on SharedObjects. It's 
 * recommended that clients give hierarchical unique keys to stored objects. 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class SharedObjectImpl implements SharedObject {
    /**
     * Database object id
     */
    private Long id;
    /**
     * The clientName of the owner
     */
    private String clientOwner;
    /**
     * The object key. 
     */
    private String objectKey;
    /**
     * The object stored by the client
     */
    private Object object;
    /**
     * The timestamp when this object was stored
     */
    private Date storageDate;
    
    public SharedObjectImpl() {
    }
    
    public SharedObjectImpl(String clientOwner, String objectKey, Object object) {
        this.clientOwner = clientOwner;
        this.objectKey = objectKey;
        this.object = object;
        this.storageDate = new Date();
    }
    
    /**
     * @see com.topcoder.farm.controller.model.SharedObject#getObject()
     */
    public Object getObject() {
        return object;
    }
    /**
     * @see com.topcoder.farm.controller.model.SharedObject#setObject(java.lang.Object)
     */
    public void setObject(Object object) {
        this.object = object;
    }
    /**
     * @see com.topcoder.farm.controller.model.SharedObject#getObjectKey()
     */
    public String getObjectKey() {
        return objectKey;
    }
    /**
     * @see com.topcoder.farm.controller.model.SharedObject#setObjectKey(java.lang.String)
     */
    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }
    /**
     * @see com.topcoder.farm.controller.model.SharedObject#getClientOwner()
     */
    public String getClientOwner() {
        return clientOwner;
    }
    /**
     * @see com.topcoder.farm.controller.model.SharedObject#setClientOwner(java.lang.String)
     */
    public void setClientOwner(String clientOwner) {
        this.clientOwner = clientOwner;
    }
    /**
     * @see com.topcoder.farm.controller.model.SharedObject#getId()
     */
    public Long getId() {
        return id;
    }
    /**
     * @see com.topcoder.farm.controller.model.SharedObject#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }
    /**
     * @see com.topcoder.farm.controller.model.SharedObject#getStorageDate()
     */
    public Date getStorageDate() {
        return storageDate;
    }
    /**
     * @see com.topcoder.farm.controller.model.SharedObject#setStorageDate(java.util.Date)
     */
    public void setStorageDate(Date creationDate) {
        this.storageDate = creationDate;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((clientOwner == null) ? 0 : clientOwner.hashCode());
        result = PRIME * result + ((objectKey == null) ? 0 : objectKey.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SharedObjectImpl other = (SharedObjectImpl) obj;
        if (clientOwner == null) {
            if (other.clientOwner != null)
                return false;
        } else if (!clientOwner.equals(other.clientOwner))
            return false;
        if (objectKey == null) {
            if (other.objectKey != null)
                return false;
        } else if (!objectKey.equals(other.objectKey))
            return false;
        return true;
    }
    
}
