/*
 * BusFactory
 * 
 * Created 10/01/2007
 */
package com.topcoder.shared.messagebus;



/**
 * Abstract Factory class for Bus related objects.<p>
 * 
 * This abstract factory must be configured using the method {@link BusFactory#configureFactory(BusFactory)} before
 * calling {@link BusFactory#getFactory()} method. <p>
 * 
 * In addition, users should release the factory once they have finished using the 
 * bus services. This will release resources taken by all object created using this factory. <p> 
 *  
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public abstract class BusFactory {
    private static BusFactory factory = null;
    
    public static BusFactory getFactory() {
        if (factory == null) {
            throw new IllegalStateException("The Bus factory is not configured");
        }
        return factory;
    }
    
    public static void configureFactory(BusFactory factory) {
        BusFactory.factory = factory;
    }

    /**
     * Creates a new BusPublisher. <p>
     * 
     * A BusPublisher just publishes BusMessages into the bus. The destination bus is determined 
     * by the configuration obtained using the configurationKey and the module name.
     *  
     * @param configurationKey The configuration key for the publisher to be created
     * @param moduleName The module name creating the publisher.
     * @return The newly created BusPublisher
     * 
     * @throws BusFactoryException if the publisher could not be created
     */
    public abstract BusPublisher createPublisher(String configurationKey, String moduleName) throws BusFactoryException;
    
    /**
     * Creates a new BusListener. <p>
     * 
     * A BusListener just listens for incoming BusMessages from the bus. The source bus is determined 
     * by the configuration obtained using the configurationKey and the module name.
     *  
     * @param configurationKey The configuration key for the listener to be created
     * @param moduleName The module name creating the listener.
     * @return The newly created BusListener
     * 
     * @throws BusFactoryException if the listener could not be created
     */
    public abstract BusListener createListener(String configurationKey, String moduleName) throws BusFactoryException;
    
    //Unsupported yet
    public abstract BusPollListener createPollListener(String configurationKey, String moduleName) throws BusFactoryException;

    /**
     * Creates a new BusRequestPublisher. <p>
     * 
     * A BusRequestPublisher publishes BusMessages into the bus and provides a mean 
     * for waiting for a response to a published message. The destination bus and the source bus is determined 
     * by the configuration obtained using the configurationKey and the module name.
     *  
     * @param configurationKey The configuration key for the publisher to be created
     * @param moduleName The module name creating the publisher.
     * @return The newly created BusRequestPublisher
     * 
     * @throws BusFactoryException if the publisher could not be created
     */
    public abstract BusRequestPublisher createRequestPublisher(String configurationKey, String moduleName) throws BusFactoryException;
    
    /**
     * Creates a new BusRequestListener. <p>
     * 
     * A BusRequestListener listens for incoming messages from the bus provides a mean 
     * for sending a response for a particular message. The destination bus and the source bus is determined 
     * by the configuration obtained using the configurationKey and the module name.
     *  
     * @param configurationKey The configuration key for the listener to be created
     * @param moduleName The module name creating the listener.
     * @return The newly created BusRequestListener
     * 
     * @throws BusFactoryException if the listener could not be created
     */
    public abstract BusRequestListener createRequestListener(String configurationKey, String moduleName) throws BusFactoryException;
    
    /**
     * Releases the bus factory and all the underlying bus resources taken by created objects.
     * (Connections, Threads, etc). 
     */
    public abstract void release();
}
