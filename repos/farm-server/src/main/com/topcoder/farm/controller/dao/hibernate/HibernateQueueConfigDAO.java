package com.topcoder.farm.controller.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.classic.Session;

import com.topcoder.farm.controller.dao.QueueConfigDAO;
import com.topcoder.farm.controller.model.QueueConfig;

public class HibernateQueueConfigDAO implements QueueConfigDAO {

	@SuppressWarnings("unchecked")
	@Override
	public List<QueueConfig> getQueueConfigs() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Criteria criteria = session.createCriteria(QueueConfig.class);
        return criteria.list();
	}
	
}
