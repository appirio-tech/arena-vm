/*
 * HibernateUtil
 *
 * Created 08/02/2006
 */
package com.topcoder.farm.controller.dao.hibernate;

import java.io.InputStream;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class HibernateUtil {
    public static final String PROD_HIBERNATE_CFG_XML = "/conf/hibernate.cfg.xml";
    private static SessionFactory sessionFactory;

    public static void initialize() {
        initialize(PROD_HIBERNATE_CFG_XML);
    }

    public static void initialize(String cfgFileName) {
        sessionFactory = getConfiguration(cfgFileName).buildSessionFactory();
    }

    private static Configuration getConfiguration(String cfgFileName) {
    	final InputStream in = HibernateUtil.class.getResourceAsStream(cfgFileName);
    	if (in != null) {
    		return new Configuration() {
                protected InputStream getConfigurationInputStream(String resource) throws HibernateException {
                    return in;
                }
            }.configure(cfgFileName);
    	} else {
    		return new Configuration().configure(cfgFileName);
    	}
    	
    	
        
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void createDB(String cfgFileName) {
        new SchemaExport(getConfiguration(cfgFileName)).create(true, true);
    }

    public static void updateDBSchema(String cfgFileName) {
        new SchemaUpdate(getConfiguration(cfgFileName)).execute(true, false);
    }
}
