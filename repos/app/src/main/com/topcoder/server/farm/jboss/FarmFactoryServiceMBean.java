/*
 * FarmFactoryService
 * 
 * Created 11/15/2006
 */
package com.topcoder.server.farm.jboss;

import java.util.List;


/**
 * @author Diego Belfer (mural)
 * @version $Id: FarmFactoryServiceMBean.java 54869 2006-12-01 18:02:46Z thefaxman $
 */
public interface FarmFactoryServiceMBean {
    int invokersCount() throws Exception;
    List listInvokers() throws Exception;
    void restartInvoker(String invokerName) throws Exception;
    void releaseInvoker(String invokerName) throws Exception; 
    void start() throws Exception;
    void stop();
}
