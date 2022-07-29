/*
 * AsyncServiceProxyGenerator
 * 
 * Created 07/30/2007
 */
package com.topcoder.server.ejb.asyncservices;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.naming.NamingException;

import com.topcoder.server.ejb.asyncservices.AsyncServiceClientInvoker.AsyncResponseHandler;
import com.topcoder.shared.util.logging.Logger;

/**
 * Proxy generator to invoke a service method in a asynchronous manner.<p>
 * 
 * This class provides a simple way of using a already existing service in Async manner.
 * It generates a Proxy for the service interface and allows to invoke the methods asynchronously. 
 * 
 * @author Diego Belfer (mural)
 * @version $Id: AsyncServiceProxyGenerator.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class AsyncServiceProxyGenerator {
    private static final Logger log = Logger.getLogger(AsyncServiceProxyGenerator.class);
    
    /**
     * Returns a proxy for the service.<p>
     * 
     * The proxy returned will implement 2 interfaces: The one of the service <code>serviceInterfaceClass</code> and {@link AsyncServiceClientProxy}.
     * The {@link AsyncServiceClientProxy} allows customization of the timeout value and the response handler for the next invocation.<p>
     * 
     * This will create a proxy using default values for the AsyncServiceClientProxy.
     * It means : No response handler, no timeout, no response id.
     * 
     * The object returned is not thread safe.
     * 
     * @param contextURL The initial context url of the service
     * @param serviceName The service name.
     * @param homeInterfaceClass The home interface class of the service
     * @param serviceInterfaceClass The interface class of the service
     * 
     * @return A new proxy object
     * 
     * @throws NamingException If a naming exception is thrown when looking for the service.
     */
    public static Object getAsyncService(String contextURL, String serviceName, Class homeInterfaceClass, Class serviceInterfaceClass) throws NamingException {
        return getAsyncService(contextURL, serviceName, homeInterfaceClass, serviceInterfaceClass, null, -1, null);
    }
  
    
    
    /**
     * Returns a proxy for the service.<p>
     * 
     * The proxy returned will implement 2 interfaces: The one of the service <code>serviceInterfaceClass</code> and {@link AsyncServiceClientProxy}.
     * The {@link AsyncServiceClientProxy} allows customization of the timeout value and the response handler for the next invocation.<p>
     * 
     * The object returned is not thread safe.
     * 
     * @param contextURL The initial context url of the service
     * @param serviceName The service name.
     * @param homeInterfaceClass The home interface class of the service
     * @param serviceInterfaceClass The interface class of the service
     * @param handler The default handler to use
     * @param timeToLive The default time to live in milliseconds before timeout.
     * @param responseId The id used to notified the result for the next request.
     * 
     * @return A new proxy object
     * 
     * @throws NamingException If a naming exception is thrown when looking for the service.
     */
    public static Object getAsyncService(String contextURL, String serviceName, Class homeInterfaceClass, Class serviceInterfaceClass, AsyncServiceClientInvoker.AsyncResponseHandler handler, long timeToLive, Object responseId) throws NamingException {
        log.debug("creating async service proxy: "+serviceName+" class="+serviceInterfaceClass.getName());
        AsyncServiceClientInvoker clientInvoker = AsyncServiceClientProvider.getClientInvoker(contextURL);
        Object instance = Proxy.newProxyInstance(
                        serviceInterfaceClass.getClassLoader(),
                        new Class[]{serviceInterfaceClass, AsyncServiceClientProxy.class}, new AsyncInvocationHandler(clientInvoker, serviceName,  homeInterfaceClass, serviceInterfaceClass, handler, timeToLive, responseId));
        log.debug("async service proxy created");
        return instance;
    }

    
    private static class AsyncInvocationHandler implements InvocationHandler {
        private AsyncServiceClientInvoker invoker;
        private long timeToLive;
        private AsyncResponseHandler handler;
        private Object responseId;
        private String serviceName;
        private Class homeInterfaceClass;
        private Class serviceInterfaceClass;

        public AsyncInvocationHandler(AsyncServiceClientInvoker invoker, String serviceName, Class homeInterfaceClass, Class serviceInterfaceClass, AsyncServiceClientInvoker.AsyncResponseHandler handler, long timeToLive, Object responseId) {
            this.invoker = invoker;
            this.timeToLive = timeToLive;
            this.handler = handler;
            this.responseId = responseId;
            this.serviceName = serviceName;
            this.homeInterfaceClass = homeInterfaceClass;
            this.serviceInterfaceClass = serviceInterfaceClass;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getClass().equals(AsyncServiceClientProxy.class)) {
                if (method.getName().equals("setTimeToLive")) {
                    this.timeToLive = ((Long) args[0]).longValue();
                } else if (method.getName().equals("setResponseId")) {
                    this.responseId = args[0];
                } else {
                    this.handler = (AsyncResponseHandler) args[0];
                }
            } else {
                invoker.invoke(
                        serviceName, 
                        homeInterfaceClass, 
                        serviceInterfaceClass, 
                        method.getName(), 
                        method.getParameterTypes(), 
                        args, 
                        timeToLive, 
                        responseId, 
                        handler);
            }
            return null;
        }
    }
}   
