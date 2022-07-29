/*
 * HibernateInvocationDAO
 *
 * Created 08/02/2006
 */
package com.topcoder.farm.controller.dao.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.classic.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.exception.ConstraintViolationException;

import com.topcoder.farm.controller.api.InvocationRequestRef;
import com.topcoder.farm.controller.api.InvocationRequestSummaryItem;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.controller.dao.DuplicateKeyException;
import com.topcoder.farm.controller.dao.InvocationDAO;
import com.topcoder.farm.controller.dao.InvocationHeader;
import com.topcoder.farm.controller.dao.NotFoundException;
import com.topcoder.farm.controller.model.InvocationContext;
import com.topcoder.farm.controller.model.InvocationData;
import com.topcoder.farm.controller.model.InvocationHeaderTO;
import com.topcoder.farm.controller.processor.InvocationStatus;
import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.farm.shared.invocation.InvocationResult;
import com.topcoder.farm.shared.util.Pair;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class HibernateInvocationDAO implements InvocationDAO {

    public InvocationData create(InvocationData data) throws DuplicateKeyException {
        //Notify deletes invocation now
        //tryToDeleteIfNotified(data.getClientName(), data.getClientRequestId());
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Serializable id;
        try {
            id = session.save(data);
            //session.flush();
        } catch (ConstraintViolationException e) {
            throw new DuplicateKeyException(e);
        }
        data = (InvocationData) session.load(InvocationData.class, id);
        return data;
    }

    public InvocationData findById(Long id) throws NotFoundException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        InvocationData result = (InvocationData) session.get(InvocationData.class, id);
        if (result == null) {
            throw new NotFoundException("InvocationData with id="+id);
        }
        return result;
    }

    public InvocationData findByClientKey(String clientName, String requestId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.byClientKey");
        query.setString("clientName", clientName);
        query.setString("clientRequestId", requestId);
        return (InvocationData) query.uniqueResult();
    }

    public Set<Long> deleteForClientName(String id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.idsByClientName");
        query.setString("clientName", id);
        List<Long> list = query.list();
        query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.deleteByClientName");
        query.setString("clientName", id);
        query.executeUpdate();
        return new HashSet(list);
    }

    public Set<Long> deleteForClientKey(String id, String requestIdPrefix) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        String likePrefix = MatchMode.START.toMatchString(requestIdPrefix);
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.idsByClientKeyPrefix");
        query.setString("clientName", id);
        query.setString("prefix", likePrefix);
        query.setReadOnly(true);
        List<Long> list = query.list();
        query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.deleteByClientKeyPrefix");
        query.setString("clientName", id);
        query.setString("prefix", likePrefix);
        query.executeUpdate();
        return new HashSet(list);
    }

    public Set<Long> getIdsForClientKey(String id, String requestIdPrefix) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        String likePrefix = MatchMode.START.toMatchString(requestIdPrefix);
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.idsByClientKeyPrefix");
        query.setString("clientName", id);
        query.setString("prefix", likePrefix);
        query.setReadOnly(true);
        List<Long> list = query.list();
        return new HashSet(list);
    }

    public int countForClientName(String clientName) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.countPendingByClientName");
        query.setString("clientName", clientName);
        query.setInteger("status", InvocationData.STATUS_NOTIFIED);
        query.setReadOnly(true);
        return ((Number)query.uniqueResult()).intValue();
    }

    public int countForClientKey(String id, String requestIdPrefix) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        String likePrefix = MatchMode.START.toMatchString(requestIdPrefix);
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.countPendingByClientKeyPrefix");
        query.setString("clientName", id);
        query.setString("prefix", likePrefix);
        query.setInteger("status", InvocationData.STATUS_NOTIFIED);
        query.setReadOnly(true);
        return ((Number)query.uniqueResult()).intValue();
    }


    public List<Pair<Long, InvocationResponse>> getPendingResponsesForClientName(String clientName) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.pendingResponsesByClientName");
        query.setString("clientName", clientName);
        query.setInteger("status", InvocationData.STATUS_SOLVED);
        query.setDate("currentDate", new Date());
        List result = query.list();
        List<Pair<Long, InvocationResponse>> responseList = new ArrayList(result.size());
        for (Iterator it = result.iterator(); it.hasNext();) {
            Object[] fields = (Object[]) it.next();
            responseList.add(new Pair<Long, InvocationResponse>(
                                    (Long) fields[0],
                                    new InvocationResponse((String) fields[1], fields[2], (InvocationResult) fields[3])));
        }
        return responseList;
    }

    public Pair<String, InvocationResponse> getResponseById(Long id) throws NotFoundException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.responseById");
        query.setLong("invocationId", id.longValue());
        query.setInteger("status", InvocationData.STATUS_SOLVED);
        Object[] object = (Object[]) query.uniqueResult();
        if (object == null) {
            throw new NotFoundException("Id="+id);
        }
        return new Pair<String, InvocationResponse>(
                (String) object[0],
                new InvocationResponse((String) object[1], object[2],  (InvocationResult)object[3]));
    }
    
    public InvocationHeader getHeaderById(Long id) throws NotFoundException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.headerById");
        query.setLong("invocationId", id.longValue());
        Object[] object = (Object[]) query.uniqueResult();
        if (object == null) {
            throw new NotFoundException("Id="+id);
        }
        return new InvocationHeader((String) object[0], (String) object[1], object[2]);
    }

    public boolean updateResultOfInvocation(Long invocationId, InvocationResult result) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.updateResultById");
        query.setParameter("result", result, Hibernate.custom(CustomSerializableUserType.class));
        query.setLong("invocationId", invocationId.longValue());
        query.setInteger("status1", InvocationData.STATUS_PENDING);
        query.setInteger("status2", InvocationData.STATUS_ASSIGNED);
        query.setInteger("newStatus", InvocationData.STATUS_SOLVED);
        query.setTimestamp("solveDate", new Date());
        return query.executeUpdate() > 0;
    }

    public boolean updateStatusAsAssigned(Long invocationId, String processorId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.updateAsAssignedById");
        query.setLong("invocationId", invocationId.longValue());
        query.setInteger("newStatus", InvocationData.STATUS_ASSIGNED);
        query.setInteger("status", InvocationData.STATUS_PENDING);
        query.setString("assignedProceesor", processorId);
        query.setTimestamp("dateValue" , new Date());
        return query.executeUpdate() > 0;
    }

    public boolean updateStatusAsPendingIfAssignationTimeout(Long invocationId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.updateAsPendingIfAssignationTimeoutById");
        query.setLong("invocationId", invocationId.longValue());
        query.setInteger("newStatus", InvocationData.STATUS_PENDING);
        query.setInteger("status", InvocationData.STATUS_ASSIGNED);
        query.setTimestamp("dateValue" , new Date());
        return query.executeUpdate() > 0;
    }

    public boolean updateStatusAsPendingIfAssignedToProcessor(Long invocationId, String processorName) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.updateAsPendingIfAssignedToProcessor");
        query.setLong("invocationId", invocationId.longValue());
        query.setInteger("newStatus", InvocationData.STATUS_PENDING);
        query.setInteger("status", InvocationData.STATUS_ASSIGNED);
        query.setString("processorName" , processorName);
        return query.executeUpdate() > 0;
    }

    public boolean updateStatusAsNotified(String clientName, String requestId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.deleteByClientKey");
        query.setString("clientName", clientName);
        query.setString("requestId", requestId);
//        query.executeUpdate();
//        query.setInteger("newStatus", InvocationData.STATUS_NOTIFIED);
//        query.setInteger("status", InvocationData.STATUS_SOLVED);
//        query.setTimestamp("dateValue" , new Date());
        return query.executeUpdate() > 0;
    }

    public List<InvocationHeaderTO> findPendingInvocations() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        String className = InvocationHeaderTO.class.getName();
        Query query = session.createQuery("select new "+className+"(id, receivedDate, dropDate, priority, assignAttempts, requirements) from InvocationData where status = :status");
        query.setInteger("status", InvocationData.STATUS_PENDING);
        query.setReadOnly(true);
        List result = query.list();
        return result;
    }

    public List<InvocationHeaderTO> findPendingAssignedInvocations(String processorName) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        String className = InvocationHeaderTO.class.getName();
        Query query = session.createQuery("select new "+className+"(id, receivedDate, dropDate, priority, assignAttempts, requirements) from InvocationData " +
                                          "  where status = :status and assignedProcessor = :processorName");
        query.setInteger("status", InvocationData.STATUS_ASSIGNED);
        query.setString("processorName", processorName);
        query.setReadOnly(true);
        List result = query.list();
        return result;
    }

    public List<InvocationHeaderTO> findPendingAssignedInvocations() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        String className = InvocationHeaderTO.class.getName();
        Query query = session.createQuery("select new "+className+"(id, receivedDate, dropDate, priority, assignAttempts, requirements) from InvocationData " +
                                          "  where status = :status and ageless(:maxDate, assignDate, assignationTtl) = TRUE");
        query.setInteger("status", InvocationData.STATUS_ASSIGNED);
        query.setTimestamp("maxDate" , new Date());
        query.setReadOnly(true);
        List result = query.list();
        return result;
    }

    public InvocationContext findInvocationById(Long id) throws NotFoundException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.findInvocationById");
        query.setLong("id", id.longValue());
        query.setReadOnly(true);

        Object[] results = (Object[]) query.uniqueResult();
        Invocation invocation = (Invocation) results[0];
        int requiredResources = ((Integer)results[1]).intValue();
        if (invocation == null) {
            throw new NotFoundException("InvocationData with id="+id);
        }
        query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.propertiesForInvocation");
        query.setLong("id", id.longValue());
        query.setReadOnly(true);
        List list = query.list();
        return new InvocationContext(invocation, list, requiredResources);
    }

    /**
     * @see com.topcoder.farm.controller.dao.InvocationDAO#deleteDroppedOrNotified(int)
     */
    public int deleteDroppedOrNotified(int sizeOfDelete) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.idsDroppedOrNotified");
        Date currentDate = new Date();
        query.setTimestamp("currentDate", currentDate);
        query.setInteger("status", InvocationData.STATUS_NOTIFIED);
        query.setInteger("pendingStatus" , InvocationData.STATUS_PENDING);
        query.setInteger("maxAttempts", MAX_ASSIGN_ATTEMPTS);
        query.setMaxResults(sizeOfDelete);
        List ids = query.list();
        if (ids.size() > 0) {
            query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.deleteByIds");
            query.setParameterList("ids", ids);
            query.executeUpdate();
        }
        return ids.size();
    }

    /**
     * Implementation Note:
     * This implementation returns items in the same order that
     * {@link com.topcoder.farm.controller.queue.DefaultQueueComparator} does.
     *
     * @see com.topcoder.farm.controller.dao.InvocationDAO#findRefByClientKey(String, String, int)
     */
    public List<InvocationRequestRef> findRefByClientKey(String clientName, String requestIdPrefix, int invocationStatus) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.invocationRefsByClientKeyPrefix");
        query.setString("clientName", clientName);
        query.setString("prefix", MatchMode.START.toMatchString(requestIdPrefix));
        query.setInteger("status", invocationStatus);
        return query.list();
    }

    /**
     * Implementation Note:
     * This implementation returns items in the same order that
     * {@link com.topcoder.farm.controller.queue.DefaultQueueComparator} does.
     *
     * @see com.topcoder.farm.controller.dao.InvocationDAO#findRefByClientKey(String, String)
     */
    public List<InvocationRequestRef> findRefByClientKey(String clientName, String requestIdPrefix) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.allInvocationRefsByClientKeyPrefix");
        query.setString("clientName", clientName);
        query.setString("prefix", MatchMode.START.toMatchString(requestIdPrefix));
        return query.list();
    }

    /**
     * This implementation returns items in the same order that
     * {@link com.topcoder.farm.controller.queue.DefaultQueueComparator} does
     * @see com.topcoder.farm.controller.dao.InvocationDAO#generateSummaryByClientKey(java.lang.String, java.lang.String, java.lang.String, int)
     */
    public List<InvocationRequestSummaryItem> generateSummaryByClientKey(String clientName, String requestIdPrefix, String delimiter, int delimiterCount) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.invocationSummaryByClientKeyPrefix");
        query.setString("clientName", clientName);
        query.setString("prefix", MatchMode.START.toMatchString(requestIdPrefix));
        query.setInteger("status", InvocationData.STATUS_PENDING);
        query.setString("delimiter", delimiter);
        query.setInteger("delimiterCount", delimiterCount);
        List<Object[]> list = query.list();
        List<InvocationRequestSummaryItem> result = new ArrayList<InvocationRequestSummaryItem>(list.size());
        for (Object[] objects : list) {
            result.add(new InvocationRequestSummaryItem(
                            (String) objects[0],
                            ((Number) objects[1]).intValue(),
                            (Date) objects[2],
                            (Date) objects[3],
                            ((Number) objects[4]).intValue()));
        }
        return result;
    }

    public List<Long> getAllIds() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.ids");
        return query.list();
    }

    public void deleteByIds(Collection<Long> ids) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("com.topcoder.farm.controller.model.InvocationData.deleteByIds");
        query.setParameterList("ids", ids);
        query.executeUpdate();
    }

    public List<InvocationStatus> findAssignedInvocationStatus(String processorName) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        String className = InvocationStatus.class.getName();
        Query query = session.createQuery("select new "+className+"(" +
                "  id, receivedDate, dropDate, assignDate, assignationTtl, " +
                "   assignedProcessor, priority, requiredResources, clientName,  " +
                "   clientRequestId) from InvocationData " +
                " where status = :status and assignedProcessor = :processorName");
        query.setInteger("status", InvocationData.STATUS_ASSIGNED);
        query.setString("processorName", processorName);
        query.setReadOnly(true);
        return query.list();
    }
}
