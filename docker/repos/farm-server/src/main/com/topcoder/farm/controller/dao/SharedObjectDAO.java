/*
 * SharedObjectDAO
 * 
 * Created 09/15/2006
 */
package com.topcoder.farm.controller.dao;

import java.util.Date;

import com.topcoder.farm.controller.model.SharedObject;

/**
 * DAO interface for handling SharedObjectImpl
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface SharedObjectDAO {

    /**
     * Creates a new SharedObjectImpl
     * 
     * @param data The instance to persists
     * @return the persisted instance, the instance returned may differ from <code>data</code>
     * @throws DuplicateKeyException the Object key already exists for the same clientOwner
     */
    public SharedObject create(SharedObject data) throws DuplicateKeyException;

    /**
     * Returns the SharedObjectImpl instance stored with the specified id 
     * 
     * @param id Id of the persisted instance
     * 
     * @return The persisted instance whose id equals <code>id</code>
     * @throws NotFoundException if no SharedObjectImpl exists for the given Id 
     */
    public SharedObject findById(Long id) throws NotFoundException;

    
    /**
     * Returns the SharedObjectImpl objects stored for the given client name with 
     * matching the give objectKey <p>
     * 
     * @param clientOwner The name of the client
     * @param objectKey The client key of the Object 
     * 
     * @return The sharedObject or null if not found
     */
    public SharedObject findByClientKey(String clientOwner, String objectKey);
    
    /**
     * Returns the number of shared object owned by clientOwner with a key prefixed with
     * objectKeyPrefix
     * 
     * @param clientOwner The name of the client
     * @param objectKeyPrefix The key prefix of the Object 
     * 
     * @return the number of shared objects with key prfixed buif the shared object is stored in the repository
     */
    public int countByClientKey(String clientOwner, String objectKeyPrefix);
    
    /**
     * Deletes all SharedObjects stored for the given client. 
     * 
     * @param clientOwner The name of the client 
     * 
     * @return The number of sharedObjects removed
     * @throws ReferencedObjectException If at least one of the objects is referenced by an invocation
     */
    public int deleteAllForClient(String clientOwner) throws ReferencedObjectException;
    
    
    /**
     * Deletes all SharedObjects stored for the given client whose key is prefixed with . 
     * 
     * @param clientOwner The name of the client 
     * @param objectKeyPrefix The client key prefix to use for matching Object keys 
     * 
     * @return The number of sharedObjects removed
     * @throws ReferencedObjectException If at least one of the objects is referenced by an invocation
     */
    public int deleteForClient(String clientOwner, String objectKeyPrefix) throws ReferencedObjectException;

    /**
     * Deletes all sharedobject that are not referenced from any invocation
     * and that have been created before the given date
     * 
     * @param maxStorageDate max storage date    
     * @return The number of shared objects deleted
     */
    public int deleteUnreferencedOldObjects(Date maxStorageDate);
    
    
    /**
     * Deletes all sharedobject stored that are not referenced from any invocation
     * 
     * @return The number of shared objects deleted
     */
    public int deleteUnreferenced();

}
