/*
 * AsyncServiceInvoker
 * 
 * Created 07/28/2007
 */
package com.topcoder.server.ejb.asyncservices;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.CreateException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.topcoder.shared.ejb.ServiceLocatorSupport;

/**
 * This class is responsible for invoking the proper service and  method.<p>
 * 
 * The service locator for each service is kept in a cache for performance purposes.
 * 
 * The default InitialContext is used for searching the Service.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: AsyncServiceInvoker.java 70168 2008-05-03 22:36:47Z mural $
 */
class AsyncServiceInvoker {
    private static final Map locators = new ConcurrentHashMap();
    
    /**
     * Invokes a method on a service.
     * 
     * @param jndiName The jndi name of the service
     * @param homeInterfaceClass The home interface class of the Service
     * @param serviceInterfaceClass The service interface class
     * @param methodName The method to invoke
     * @param paramTypes The paramTypes of the method
     * @param args The actual arguments to use
     * 
     * @return The value returned by the invocation.
     * 
     * @throws InvocationTargetException If the service thrown an exception. 
     * @throws ServiceDefinitionException If the method could not be invoked
     */
    public Object invoke(String jndiName, Class homeInterfaceClass, Class serviceInterfaceClass, String methodName, Class[] paramTypes, Object[] args) throws InvocationTargetException, ServiceDefinitionException {
        Object service = null;
        Method method =  null;
        try {
            service = getService(jndiName, homeInterfaceClass, serviceInterfaceClass);
            method = serviceInterfaceClass.getMethod(methodName, paramTypes);
        } catch (Exception e) {
            throw new ServiceDefinitionException("Could not obtain service/method", e);
        }
        
        try {
            return method.invoke(service, args);
        } catch (InvocationTargetException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceDefinitionException("Method invocation failed.", e);
        }
    }

    private Object getService(String jndiName, Class homeInterfaceClass, Class serviceInterfaceClass) throws RemoteException, NamingException, CreateException {
        String locatorKey = homeInterfaceClass.getName()+"/"+serviceInterfaceClass.getName()+"@"+jndiName;
        ServiceLocatorSupport locator = (ServiceLocatorSupport) locators.get(locatorKey);
        if (locator == null) {
            locator = createLocator(homeInterfaceClass, serviceInterfaceClass, jndiName);
            locators.put(locatorKey, locator);
        } 
        return locator.getService();
    }
    
    private ServiceLocatorSupport createLocator(Class homeInterfaceClass, Class serviceInterfaceClass, String jndiName) {
        ServiceLocatorSupport locator = new ServiceLocatorSupport(serviceInterfaceClass, homeInterfaceClass, jndiName, null) {
            protected InitialContext getContext() throws NamingException {
                return new InitialContext();
            }
        };
        return locator;
    }
}
