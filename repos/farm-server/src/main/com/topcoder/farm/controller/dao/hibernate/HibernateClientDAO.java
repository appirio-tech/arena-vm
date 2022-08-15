/*
 * HibernateClientDAO
 * 
 * Created 08/29/2006
 */
package com.topcoder.farm.controller.dao.hibernate;

import org.hibernate.Criteria;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;

import com.topcoder.farm.controller.dao.ClientDAO;
import com.topcoder.farm.controller.model.ClientData;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class HibernateClientDAO implements ClientDAO {
    public ClientData findByName(String name) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Criteria criteria = session.createCriteria(ClientData.class);
        criteria.add(Restrictions.naturalId().set("name", name));
        return (ClientData) criteria.uniqueResult();
    }
}
