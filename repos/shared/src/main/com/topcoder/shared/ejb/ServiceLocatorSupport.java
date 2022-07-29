/*
 * ServiceLocatorSupport
 *
 * Created 03/26/2007
 */
package com.topcoder.shared.ejb;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import com.topcoder.shared.util.TCContext;
import com.topcoder.shared.util.logging.Logger;

/**
 * Support class for Service Locators. </p>
 *
 * It returns a proxy for the service and recreate the service in case any RemoteException
 * is thrown while calling one of the service methods. <p>
 *
 * Only one instance of the service is held by this Locator, and it is used for every call.<p>
 *
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ServiceLocatorSupport {
    private final Logger log = Logger.getLogger(ServiceLocatorSupport.class);

    /**
     * The home interface class of the service
     */
    private final Class homeInterfaceClass;
    /**
     * The jndi name where the home can be found
     */
    private final String jndiName;
    /**
     * The proxy returned to all getService calls.
     */
    private final Object proxiedServices;

    /**
     * Cached create method for performance improvements
     */
    private final Method homeCreateMethod;

    /**
     * Indicates if a new instance of the service must be obtained.
     */
    private volatile boolean mustReload = true;

    /**
     * The home instance
     */
    private Object home;

    /**
     * The real service where to delegate calls.
     */
    private Object services;

    /**
     * The initial context URL
     */
    private String contextURL;

    /**
     * Creates a new Service locator.
     *
     * @param interfaceClass The interface class of the Service
     * @param homeInterfaceClass The home interface of the service. It must declare a method create
     *                           with no arguments to obtain the service.
     * @param jndiName The jndi name where to find the Home
     * @param contextURL The initial context URL.
     */
    public ServiceLocatorSupport(Class interfaceClass, Class homeInterfaceClass, String jndiName, String contextURL) {
        this.homeInterfaceClass = homeInterfaceClass;
        this.jndiName = jndiName;
        this.contextURL = contextURL;
        this.proxiedServices = Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass}, new ServiceFailureDetection());
        try {
            this.homeCreateMethod = homeInterfaceClass.getMethod("create", new Class[]{});
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Missing create method in Home interface", e);
        }
    }

    /**
     * Returns the service instance.
     *
     * @return The service
     *
     * @throws NamingException
     * @throws RemoteException
     * @throws CreateException
     */
    public Object getService() throws NamingException, RemoteException, CreateException {
        checkServiceLoaded();
        return proxiedServices;
    }

    private void checkServiceLoaded() throws NamingException, CreateException, RemoteException {
        if (mustReload || services == null) {
            createServiceInstance();
        }
    }

    private synchronized void createServiceInstance() throws NamingException, RemoteException, CreateException {
        if (mustReload || services == null) {
            Object h = home;
            if (h != null) {
                try {
                    services = callCreate(h);
                    mustReload = false;
                    return;
                } catch (Exception e) {
                    log.info("Home was not null, but service creation failed. Generating new home", e);
                }
            }
            h = getHome();
            services = callCreate(h);
            home = h;
            mustReload = false;
        }
    }

    private Object callCreate(Object h) throws CreateException, RemoteException {
        try {
            log.info("Creating new instance using home "+ homeInterfaceClass.getName());
            Object serviceObject = homeCreateMethod.invoke(h, new Object[]{});
            log.info("Creation succeeded");
            return serviceObject;
        } catch (IllegalArgumentException e) {
            //Cannot happen
            throw e;
        } catch (IllegalAccessException e) {
            //should not happen
            throw new RuntimeException("Unexpected exception",e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof CreateException) {
                throw (CreateException) e.getTargetException();
             } else if (e.getTargetException() instanceof RemoteException) {
                 throw (RemoteException) e.getTargetException();
             } else {
                 throw new RuntimeException("Unexpected exception",e);
             }
        }
    }

    private Object getHome() throws NamingException {
        log.info("Creating home: "+ homeInterfaceClass.getName());
        Context ctx = getContext();
        try {
            Object objRef =  ctx.lookup(jndiName);
            if (Remote.class.isAssignableFrom(homeInterfaceClass))
                return PortableRemoteObject.narrow(objRef, homeInterfaceClass);
             else
                return objRef;
        } finally {
            ctx.close();
        }
    }

    /**
     * Returns the initial context to use for finding the Home
     *
     * @return The context
     *
     * @throws NamingException
     */
    protected InitialContext getContext() throws NamingException {
        return TCContext.getInitial(contextURL);
    }

    private class ServiceFailureDetection implements InvocationHandler {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                checkServiceLoaded();
                return method.invoke(services, args);
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof RemoteException) {
                    log.info(e.getTargetException().getClass().getName()+" when calling proxied method. home="+ homeInterfaceClass.getName());
                    mustReload = true;
                };
                throw e.getTargetException();
            }
        }
    }

}
