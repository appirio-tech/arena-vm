/*
 * ControllerInitializerFinalizer
 * 
 * Created 08/03/2006
 */
package com.topcoder.farm.controller.configuration;

import org.hibernate.SessionFactory;

import com.topcoder.farm.controller.ControllerLocator;
import com.topcoder.farm.controller.api.ControllerNode;
import com.topcoder.farm.controller.dao.DAOFactory;
import com.topcoder.farm.controller.dao.hibernate.HibernateDAOFactory;
import com.topcoder.farm.controller.dao.hibernate.HibernateUtil;
import com.topcoder.farm.controller.node.ControllerNodeImpl;
import com.topcoder.farm.deployer.Deployer;
import com.topcoder.farm.deployer.DeploymentException;
import com.topcoder.farm.shared.log.LogInitializer;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ControllerInitializerFinalizer {
    
    public static void initialize(String controllerName) {
        ControllerConfiguration config = ControllerConfigurationProvider.getConfiguration();
        LogInitializer.initialize();
        verifyInstalled(controllerName, config);
        LogInitializer.reconfigure();
        DAOFactory.configureInstance(new HibernateDAOFactory());
        HibernateUtil.initialize(config.getDatabaseConfigurationFile());
        ControllerLocator.setLocalController(new ControllerNodeImpl(controllerName, config.getNodeConfiguration()));
    }

    private static void verifyInstalled(String controllerName, ControllerConfiguration config) {
        try {
            new Deployer().deploy(controllerName, config.getRootFolder()); 
        } catch (DeploymentException e) {
            throw new IllegalStateException("Could not deploy application", e);
        }
    }
    
    public static void finalize(String controllerId) {
        ControllerNode controller = ControllerLocator.getController();
        if (controller != null) {
            controller.releaseNode();
            ControllerLocator.setLocalController(null);
        }
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        if (sessionFactory!=null) {
            sessionFactory.close();
        }
    }
}
