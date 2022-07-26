/*
 * TestDataGenerator
 *
 * Created 08/03/2006
 */
package com.topcoder.farm.test.common;

import org.hibernate.Query;
import org.hibernate.classic.Session;

import com.topcoder.farm.controller.dao.hibernate.HibernateUtil;
import com.topcoder.farm.controller.model.ClientData;
import com.topcoder.farm.controller.model.ProcessorData;
import com.topcoder.farm.controller.model.ProcessorProperties;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public abstract class DataGenerator {
    protected static final int NODDL = 0;
    protected static final int DROP_CREATE = 1;
    protected static final int UPDATE = 2;

    private String hibernateConfig;

    public DataGenerator(String hibernateConfig) {
        this.hibernateConfig = hibernateConfig;
    }

    public void generate(int action) throws Exception {
        if (action == DROP_CREATE) {
            HibernateUtil.createDB(hibernateConfig);
        } if (action == UPDATE) {
            HibernateUtil.updateDBSchema(hibernateConfig);
        }
        HibernateUtil.initialize(hibernateConfig);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        try {
            generateData(session);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        }
        HibernateUtil.getSessionFactory().close();
    }

    

    protected abstract void generateData(Session session) throws Exception;


    protected ProcessorProperties createOrFindProcessorProperties(String description, Session session) {
        ProcessorProperties properties;
        Query query = session.createQuery("from "+ProcessorProperties.class.getName()+" where description = :name");
        query.setString("name", description);
        properties = (ProcessorProperties) query.uniqueResult();
        if (properties == null) {
            properties = new ProcessorProperties();
            properties.setDescription(description);
        }
        return properties;
    }

    protected ClientData createOrFindClient(String clientName, Session session) {
        ClientData client;
        Query query = session.createQuery("from "+ClientData.class.getName()+" where name = :name");
        query.setString("name", clientName);
        client = (ClientData) query.uniqueResult();
        if (client == null) {
            client = new ClientData();
            client.setName(clientName);
        }
        return client;
    }


    protected ProcessorData createOrFindProcessor(String processorName, Session session) {
        ProcessorData processor;
        Query query = session.createQuery("from "+ProcessorData.class.getName()+" where name = :name");
        query.setString("name", processorName);
        processor = (ProcessorData) query.uniqueResult();
        if (processor == null) {
            processor = new ProcessorData();
            processor.setName(processorName);
        }
        return processor;
    }
}
