/*
* Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * HibernateProcessorDAO
 * 
 * Created 08/03/2006
 */
package com.topcoder.farm.controller.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;

import com.topcoder.farm.controller.dao.ProcessorDAO;
import com.topcoder.farm.controller.model.ProcessorData;
import com.topcoder.farm.controller.model.ProcessorProperties;

/**
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
 * <ol>
 *      <li>Update {@link #findByName(String name)} method to return <code>ProcessorProperties</code>.</li>
 *      <li>Update {@link #findActiveProcessors()} method to return <code>ProcessorProperties</code>.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.0
 */
public class HibernateProcessorDAO implements ProcessorDAO {
    /**
     * Returns the a list containing all active processors
     * 
     * @return The list of active processors
     */
    public List<ProcessorProperties> findActiveProcessors() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Criteria criteria = session.createCriteria(ProcessorProperties.class);
        List result = criteria.list();
        return result;
    }
    /**
     * Returns the processor with the given name
     *  
     * @param name The name of the processor
     * 
     * @return The processor or <code>null</code> if not found
     */
    public ProcessorProperties findByName(String name) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Criteria criteria = session.createCriteria(ProcessorProperties.class);
        criteria.add(Restrictions.naturalId().set("name", name));
        return (ProcessorProperties) criteria.uniqueResult();
    }
    /**
     * Returns the processor with the given name
     *  
     * @param name The name of the processor
     * @param ip the ip address of the processor
     *        if ip = NONE, we only query by name.
     * @return The processor or <code>null</code> if not found
     */
    public ProcessorData findByNameAndIP(String name, String ip) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Criteria criteria = session.createCriteria(ProcessorData.class);
        if (ip != null && ip.equals("NONE")) {
            criteria.add(Restrictions.eq("name", name));
        } else {
            criteria.add(Restrictions.and(Restrictions.eq("name", name),
                        Restrictions.eq("ip", ip)));
        }
        return (ProcessorData) criteria.uniqueResult();
    }
}
