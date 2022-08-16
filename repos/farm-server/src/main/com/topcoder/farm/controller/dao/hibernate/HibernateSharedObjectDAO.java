/*
 * HibernateSharedObjectDAO
 * 
 * Created 09/18/2006
 */
package com.topcoder.farm.controller.dao.hibernate;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.classic.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.exception.ConstraintViolationException;

import com.topcoder.farm.controller.dao.DuplicateKeyException;
import com.topcoder.farm.controller.dao.NotFoundException;
import com.topcoder.farm.controller.dao.ReferencedObjectException;
import com.topcoder.farm.controller.dao.SharedObjectDAO;
import com.topcoder.farm.controller.model.SharedObject;
import com.topcoder.farm.controller.model.SharedObjectImpl;
import com.topcoder.farm.shared.util.LRUCache;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class HibernateSharedObjectDAO implements SharedObjectDAO {
    private final Map<CacheKey, Long> cache = Collections.synchronizedMap(new LRUCache(2000, 30000));

    public SharedObject create(SharedObject data) throws DuplicateKeyException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Serializable id = null;
        try {
            id = session.save(data);
            //session.flush();
        } catch (ConstraintViolationException e) {
            throw new DuplicateKeyException(e);
        }
        data = (SharedObject) session.load(SharedObjectImpl.class, id);
        cache.put(new CacheKey(data.getClientOwner(), data.getObjectKey()), data.getId());
        return data;
    }
    
    public SharedObject findById(Long id) throws NotFoundException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        SharedObject result = (SharedObject) session.get(SharedObjectImpl.class, id);
        if (result == null) {
            throw new NotFoundException("SharedObjectImpl with id="+id);
        }
        return result;
    }
    
    public SharedObject findByClientKey(String clientOwner, String objectKey) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        CacheKey cacheKey = new CacheKey(clientOwner, objectKey);
        Long id = cache.get(cacheKey);
        if (id == null) {
            Query query = session.getNamedQuery("com.topcoder.farm.controller.model.SharedObject.idByClientKey");
            query.setString("clientOwner", clientOwner);
            query.setString("objectKey", objectKey);
            id=(Long) query.uniqueResult();
            cache.put(cacheKey, id);
        }
        return (SharedObject) session.load(SharedObjectImpl.class, id); 
    }
    
    
    public int deleteAllForClient(String clientOwner) throws ReferencedObjectException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            Query query = session.getNamedQuery("com.topcoder.farm.controller.model.SharedObject.deleteByClientOwner");
            query.setString("clientOwner", clientOwner);
//          this can be avoided. The DB would fail anyway
//          cache.clear();
            return query.executeUpdate();
        } catch (ConstraintViolationException e) {
            throw new ReferencedObjectException(e); 
        }
    }
    
    /**
     * @see com.topcoder.farm.controller.dao.SharedObjectDAO#deleteForClient(java.lang.String, java.lang.String)
     */
    public int deleteForClient(String clientOwner, String objectKeyPrefix) throws ReferencedObjectException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            Query query = session.getNamedQuery("com.topcoder.farm.controller.model.SharedObject.deleteByClientKeyPrefix");
            query.setString("clientOwner", clientOwner);
            query.setString("prefix", MatchMode.START.toMatchString(objectKeyPrefix));
//          this can be avoided. The DB would fail anyway
//          cache.clear();
            return query.executeUpdate();
        } catch (ConstraintViolationException e) {
            throw new ReferencedObjectException(e); 
        }
    }

    /**
     * @see com.topcoder.farm.controller.dao.SharedObjectDAO#countByClientKey(String, String)
     */
    public int countByClientKey(String clientOwner, String objectKeyPrefix) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.SharedObject.countByClientKeyPrefix");
        query.setString("clientOwner", clientOwner);
        query.setString("prefix", MatchMode.START.toMatchString(objectKeyPrefix));
        return ((Number) query.uniqueResult()).intValue();
    }

    /**
     * @see com.topcoder.farm.controller.dao.SharedObjectDAO#deleteUnreferencedOldObjects(Date)
     */
    public int deleteUnreferencedOldObjects(Date maxStorageDate) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.SharedObject.idsOfUnreferencedObjects");
        query.setTimestamp("maxStorageDate", maxStorageDate);
        List list = query.list();
        if (list.size() > 0) {
//            cache.clear();
            query = session.getNamedQuery("com.topcoder.farm.controller.model.SharedObject.deleteUnreferencedById");
            for (Iterator<Long> it = list.iterator(); it.hasNext();) {
                Long idx = it.next();
                query.setLong("id", idx.longValue());
                query.executeUpdate();
            }
//            cache.clear();
        }
        return list.size();
    }
    
    
    /**
     * @see com.topcoder.farm.controller.dao.SharedObjectDAO#deleteUnreferenced()
     */
    public int deleteUnreferenced() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.SharedObject.idsOfUnreferencedObjects");
        final int MIN_STORAGE = 1000;
        query.setTimestamp("maxStorageDate", new Date(System.currentTimeMillis() - MIN_STORAGE));
        List list = query.list();
        if (list.size() > 0) {
//            cache.clear();
            query = session.getNamedQuery("com.topcoder.farm.controller.model.SharedObject.deleteUnreferencedById");
            for (Iterator<Long> it = list.iterator(); it.hasNext();) {
                Long idx = it.next();
                query.setLong("id", idx.longValue());
                query.executeUpdate();
            }
//            cache.clear();
        }
        return list.size();
    }
    
    
    private static class CacheKey {
        String clientOwner;
        String objectKey;
        int hashCode;
        
        public CacheKey(String clientOwner, String objectKey) {
            this.clientOwner = clientOwner;
            this.objectKey = objectKey;
            this.hashCode = this.clientOwner.hashCode() << 2 & this.objectKey.hashCode();
        }
        
        public int hashCode() {
            return hashCode;
        }
        
        public boolean equals(Object obj) {
            final CacheKey other = (CacheKey) obj;
            return objectKey.equals(other.objectKey) && clientOwner.equals(other.clientOwner) ;
        }
    }
}
