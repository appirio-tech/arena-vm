/*
 * FarmFactory
 * 
 * Created 06/24/2006
 */
package com.topcoder.farm.client.invoker;


/**
 * FarmFactory is the access point to the Farm.
 * 
 * This class is responsible for providing to clients the necessary
 * invokers that should be used for making request to the farm and 
 * for receiving response from it.
 * 
 * Invokers are created the first time they are required and are keept
 * in memory for reuse.
 * 
 * This class is Thread-Safe
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
@Deprecated
public class FarmFactory {
//    private Log log = LogFactory.getLog(FarmFactory.class);
//    
//    /**
//     * The unique instance of this class.
//     * It's set when Far,Factory is configured using <code>configure</code> method
//     */
//    private static FarmFactory instance;
//    
//    /**
//     * This map contains all invokers that have been created by this Factory
//     */
//    private Map invokers = Collections.synchronizedMap(new HashMap());
//    
//    /**
//     * This map contains all handlers configured for specific Invokers 
//     */
//    private Map configuredHandlers = Collections.synchronizedMap(new HashMap());
//    
//
//    /**
//     * This map contains all feedback handlers configured for specific Invokers 
//     */
//    private Map configuredFeedbackHandlers = Collections.synchronizedMap(new HashMap());
//
//    /**
//     * This map contains configurations for specific Invokers, it should be accessed while
//     * synchronized on invokers 
//     */
//    private Map configurations = new HashMap();
//    
//    /**
//     * Locks map to allow fine grained lock during Invoker creation
//     */
//    private Map locks = new HashMap();
//    
//    /**
//     * This is the builder used to create ClientNodes of the farm
//     */
//    private ClientNodeBuilder builder;
//    
//    /**
//     * The runner that is going to be used by ClientNodes to process results from the Farm
//     */
//    private Runner runner;
//
//    /**
//     * Flag indicating if this FarmFactory has been released
//     */
//    private boolean released = false;
//
//    /**
//     * Prefix used to name all invokers
//     */
//    private String invokersPrefix;
//    
//    
//    /**
//     * Configures the factory.
//     * 
//     * This must be the first method called when using this class.
//     * If the factory was not configured, getInvoker will throw exception
//     *  
//     * @param farmConfiguration The configuration used to configure the factory
//     * @throws InvalidConfigurationException If the specified configuration is invalid 
//     * @throws IllegalStateException if the factory was already configured
//     */
//    public static synchronized void configure(ClientConfiguration farmConfiguration) throws InvalidConfigurationException {
//        if (instance == null) {
//            instance = new FarmFactory(farmConfiguration);
//        } else {
//            throw new IllegalStateException("Farm factory is already configured");
//        }
//    }
//    
//    /**
//     * Returns the instance of this class.
//     * Configured must be called before any invocation to this method
//     *  
//     * @return the instance
//     */
//    public static synchronized FarmFactory getInstance() {
//        if (instance == null) {
//            throw new IllegalStateException("FarmFactory has not been configured yet");
//        }
//        return instance;
//    }
//    
//    /**
//     * @return true if the factory is configured
//     */
//    public static synchronized boolean isConfigured() {
//        return (instance != null);
//    }
//    
//    private FarmFactory(ClientConfiguration farmConfiguration) throws InvalidConfigurationException {
//        invokersPrefix = farmConfiguration.getInvokersPrefix(); 
//        runner = new ThreadPoolRunner("CLPool", farmConfiguration.getProcessorThreadPoolSize());
//        builder = buildClientNodeBuilder(farmConfiguration);
//        ClientControllerLocator.setClientControllerProvider(
//                new RemoteProxyControllerProvider(
//                        farmConfiguration,
//                        runner,
//                        ClientControllerProxy.class.getName(),
//                        Integer.MAX_VALUE, 
//                        farmConfiguration.getRegistrationTimeout(), 
//                        farmConfiguration.getAckTimeout()));
//    }
//
//    /**
//     * Gets the FarmInvoker for the specified client id.
//     *  
//     * @param id Client identification in the farm.
//     * @return The FarmInvoker for the give client id.
//     * @throws NotAllowedToRegisterException If the controller does not allow the client with the given Id to register 
//     * @throws FarmException If any error ocurrs trying to get the invoker with the given Id
//     */
////    public FarmInvoker getInvoker(String id) throws NotAllowedToRegisterException, FarmException {
////        if (released) {
////            throw new IllegalStateException("The FarmFactory have been released");
////        }
////        id = prefixId(id);
////        FarmInvoker invoker = (FarmInvoker) invokers.get(id);
////        if (invoker == null) {
////            Lock lock = lock(id);
////            if (lock.haveLock()) {
////                try {
////                    invoker = buildAndSetInvoker(id);
////                } finally {
////                    lock.unlock();
////                }
////            }
////        }
////        return invoker;
////    }
//
//    /**
//     * Returns number of invokers currently in use
//     *  
//     * @return the number of invokers
//     */
//    public int invokersCount() {
//        return invokers.size();
//    }
//    
//    /**
//     * Returns the name of all invokers in use
//     *  
//     * @return the a String list with the names
//     */
//    public List invokerNames() {
//        return new ArrayList(invokers.keySet());
//    }
//
//    
//    /**
//     * Builds and sets in the invokers map a farm invoker for the given Id
//     * 
//     * The lock for the given id must be hold before invoking this method
//     * 
//     * @param id The client id
//     * @return The invoker.
//     */
////    private FarmInvoker buildAndSetInvoker(String id) throws NotAllowedToRegisterException, FarmException {
////        FarmInvoker invoker = (FarmInvoker) invokers.get(id);
////        if (invoker == null) {
////            invoker = new FarmInvoker(buildClientNode(id), getConfiguredHandler(id), getConfiguredFeedbackHandler(id));
////            InvokerConfiguration config = null;
////            boolean mustRelease = false;
////            synchronized (invokers) {
////                if (!released) {
////                    invokers.put(id, invoker);
////                    config = (InvokerConfiguration) configurations.get(id);
////                } else { 
////                    mustRelease = true;
////                }
////            }
////            if (mustRelease) {
////                try {
////                    invoker.release();
////                    return null;
////                } catch (Exception e) {
////                }
////            } else {
////                if (config == null || config.isCancelOnRegistration()) {
////                    invoker.getClientNode().cancelPendingRequests();
////                } else if (config.isDeliverOnRegistration()) {
////                    invoker.getClientNode().requestPendingResponses();
////                }
////            }
////        }
////        return invoker;
////    }
//
//    /**
//     * Releases the unique factory instance
//     */
//    public static synchronized void releaseInstance() {
//        if (instance != null) {
//            instance.release();
//            instance = null;
//        }
//    }
//
//    /**
//     * Configures the given handler for handling responses for the client with the given name.<p>
//     * 
//     * Only one handler can be configured for a client. Handlers must be configured before 
//     * a invoker for the same client be retrieved using {@link #getInvoker(String) getInvoker} 
//     * method.<br>
//     * Handlers will be notified of incoming responses if scheduleInvocation was used 
//     * to make the invocation or if scheduleInvocationSync was used but the AsyncInvocationResponse 
//     * timeout.<p>
//     * 
//     * It's recommended to configure handlers only for clients that are used for async scheduling.<p>
//     * 
//     * @param id The client name
//     * @param handler The handler to configure
//     * @return The previous configured handler, <code>null</code> if none was configured 
//     * @see InvocationResultHandler
//     */
//    public InvocationResultHandler configureHandler(String id, InvocationResultHandler handler) {
//        synchronized (invokers) {
//            if (released) throw new IllegalStateException("FarmFactory was released");
//            id = prefixId(id);
//            return (InvocationResultHandler) configuredHandlers.put(id, handler);
//        }
//    }
//    
//    /**
//     * Configures the given handler for handling feedback for the client with the given name.<p>
//     * 
//     * Only one feedback handler can be configured for a client. Handlers must be configured before 
//     * a invoker for the same client be retrieved using {@link #getInvoker(String) getInvoker} 
//     * method.<br>
//     * Handlers will be notified of incoming feedback.<p>
//     * 
//     * @param id The client name
//     * @param handler The handler to configure
//     * @return The previous configured handler, <code>null</code> if none was configured 
//     * @see InvocationResultHandler
//     */
////    public InvocationFeedbackHandler configureFeedbackHandler(String id, InvocationFeedbackHandler handler) {
////        synchronized (invokers) {
////            if (released) throw new IllegalStateException("FarmFactory was released");
////            id = prefixId(id);
////            return (InvocationFeedbackHandler) configuredFeedbackHandlers.put(id, handler);
////        }
////    }
//    
//    /**
//     * Removes from the set of configured handlers, the handler configured for the client 
//     * with the given name.<p>
//     * 
//     * This method does not release the handler once it was set in a Invoker.
//     * 
//     * @param id the name of the client for which the handler must be unconfigured.
//     * @return The configured handler, if there was any 
//     */
//    public InvocationResultHandler unconfigureHandler(String id) {
//        synchronized (invokers) {
//            if (released) throw new IllegalStateException("FarmFactory was released");
//            id = prefixId(id);
//            return (InvocationResultHandler) configuredHandlers.remove(id);
//        }
//    }
//
//    /**
//     * Removes from the set of configured handlers, the feedback handler configured for the client 
//     * with the given name.<p>
//     * 
//     * This method does not release the handler once it was set in a Invoker.
//     * 
//     * @param id the name of the client for which the handler must be unconfigured.
//     * @return The configured handler, if there was any 
//     */
//    public InvocationFeedbackHandler unconfigureFeedbackHandler(String id) {
//        synchronized (invokers) {
//            if (released) throw new IllegalStateException("FarmFactory was released");
//            id = prefixId(id);
//            return (InvocationFeedbackHandler) configuredFeedbackHandlers.remove(id);
//        }
//    }
//    
//    /**
//     * Sets an specific configuration for client with the given name.<p>
//     * 
//     * @param id The name of the  client
//     * @param configuration The configuration to use
//     */
//    public void configureInvoker(String id, InvokerConfiguration configuration) {
//        synchronized (invokers) {
//            if (released) throw new IllegalStateException("FarmFactory was released");
//            id = prefixId(id);
//            configurations.put(id, configuration);
//        }
//    }
//    
//    /**
//     * Returns true if the given client name was configured using the 
//     * method {@link FarmFactory#configureInvoker(String, InvokerConfiguration)}.<p>
//     * 
//     * @param id The name of the  client
//     * @return true if it is configured
//     */
//    public boolean isConfiguredInvoker(String id) {
//        synchronized (invokers) {
//            id = prefixId(id);
//            return configurations.containsKey(id);
//        }
//    }
//    
//    /**
//     * Releases the invoker with the given id if it exists.
//     * 
//     * @param id The id of the invoker to release
//     */
//    public void releaseInvoker(String id) {
////        id = prefixId(id);
////        if (invokers.containsKey(id)) {
////            Lock lock = lock(id);
////            if (lock.haveLock()) {
////                try {
////                    FarmInvoker invoker = (FarmInvoker) invokers.remove(id);
////                    if (invoker != null) {
////                        invoker.release();
////                    }
////                } finally {
////                    lock.unlock();
////                }
////            }
////        }
//    }
//    
//    private InvocationResultHandler getConfiguredHandler(String id) {
//        InvocationResultHandler handler = (InvocationResultHandler) configuredHandlers.get(id);
//        if (handler == null) {
//            handler = buildDefaultHandler(id);
//        }
//        return handler;
//    }
//    
//    
//    private InvocationFeedbackHandler getConfiguredFeedbackHandler(String id) {
//        InvocationFeedbackHandler handler = (InvocationFeedbackHandler) configuredFeedbackHandlers.get(id);
//        if (handler == null) {
//            handler = buildDefaultFeedbackHandler(id);
//        }
//        return handler;
//    }
//
//    private LogInvocationResultHandler buildDefaultHandler(String id) {
//        return new LogInvocationResultHandler( "Discarding response, nobody listening", id);
//    }
//    
//    
//    private LogInvocationResultHandler buildDefaultFeedbackHandler(String id) {
//        return new LogInvocationResultHandler( "Discarding feedback, nobody listening", id);
//    }
//
//    private ClientNode buildClientNode(String clientIdentification) throws NotAllowedToRegisterException, FarmException {
//        try {
//            return builder.buildClient(clientIdentification);
//        } catch (RuntimeException e) {
//            throw new FarmException("Could not create client node for clientId="+clientIdentification, e);
//        }
//    }
//    
//    /**
//     * Acquires the Lock for the give Object and returns it 
//     * This method will block until the lock be obtained.
//     * 
//     * @param object The object whose lock want to be acquired
//     * @return The lock object
//     */
//    private Lock lock(String object) {
//        Lock lock;
//        synchronized (locks) {
//            lock = (Lock) locks.get(object);
//            if (lock == null) {
//                lock = new Lock();
//                locks.put(object, lock);
//            }
//        }
//        lock.lock();
//        return lock;
//    }
//    
//    private ClientNodeBuilder buildClientNodeBuilder(ClientConfiguration farmConfiguration) throws InvalidConfigurationException {
//        try {
//            return (ClientNodeBuilder) Class.forName(farmConfiguration.getClientNodeBuilderClassName()).newInstance();
//        } catch (Exception e) {
//            throw new InvalidConfigurationException("Could not create instance for: "+farmConfiguration.getClientNodeBuilderClassName(), e);
//        }
//    }
//    
//    /**
//     * Release this FarmFactory, stopping all processing threads and
//     * and releasing all invokers 
//     */
//    private void release() {
//        synchronized (invokers) {
//            if (!released) {
//                this.released = true;
//                runner.stopAccepting();
//                for (Iterator it = invokers.values().iterator(); it.hasNext();) {
//                    try {
//                        FarmInvoker invoker = (FarmInvoker) it.next();
////                        invoker.release();
//                    } catch (Exception e) {
//                        log.error("Exception trying to release invoker",e);
//                    }
//                }
//                invokers.clear();
//                locks.clear();
//                configuredHandlers.clear();
//                configuredFeedbackHandlers.clear();
//                runner = null;
//            }
//        }
//    }
//    
//    private String prefixId(String id) {
//        return invokersPrefix+id;
//    }
}
