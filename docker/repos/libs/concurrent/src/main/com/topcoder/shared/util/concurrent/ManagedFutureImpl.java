/*
 * ManagedFutureImpl
 * 
 * Created Oct 25, 2007
 */
package com.topcoder.shared.util.concurrent;



/**
 * Managed future implementation extends FutureImpl to provide Managed versions of a FutureImpl.<p>
 * 
 * Objects tracking this future will be notified when a value/exception is set for the future or when 
 * the future is canceled. In addition a notification will be made if the future is not ready and it is
 * being finalized. <p>
 * 
 *  
 * @author Diego Belfer (Mural)
 * @version $Id$
 * @param <T> The result type returned by {@link ManagedFutureImpl} gets method.
 * @param <W> The type of the Id of the Future.
 */
public class ManagedFutureImpl<T,W> extends FutureImpl<T>{
    private static final FutureHandler NULL_HANDLER = new NullFutureHandlerImplementation();
    private W id;
    private FutureHandler<W> handler;
    
    /**
     * Creates a new ManagedFutureImpl using a Null FutureHandler and no ID
     */
    public ManagedFutureImpl() {
        this.handler = NULL_HANDLER;
    }
    
    /**
     * Creates a new ManagedFutureImpl with the given <code>id</code> and the given
     * <code>handler</code>
     * 
     * @param id The id to be used to notify the handler when this future is ready.
     * @param handler The handler who will received ready notification.
     */
    public ManagedFutureImpl(W id, FutureHandler<W> handler) {
        this.id = id;
        this.handler = handler;
    }
    
    protected void finalize() throws Throwable {
        instanceReady();
    }

    protected void instanceReady() {
        try{
            if (handler != null) {
                handler.futureReady(id);
            }
        } catch (Exception e) {
        }
        handler = null;
    }
    
    /**
     * FutureHandler receives a notification when the Future instance is ready,
     * this means a value or exception was set or the future was canceled.  
     * 
     * In addition the handler will be called when the instance is finalized if  
     * it was not already ready. 
     * 
     * @param <W> The type of the Id of the Future.
     */
    public interface FutureHandler<W> {

        /**
         * This method is called when the value is set, an exception is set or the future is canceled. 
         * Once this method is called, get methods will return immediately. 
         * 
         * @param id The id of the future that is read. 
         */
        void futureReady(W id);
        
        /**
         * This method is called when is being canceled. This method provides a way to the handler to cancel any
         * process running on behalf of the future.<p>
         * 
         * {@link FutureHandler#futureReady(Object)} will be called immediately after this method is called.
         * 
         * @param id The id of the future being canceled 
         * @param mayInterrupt If the background tasks should be interrupted if they are running.
         * @return true if the background tasks were properly canceled, false otherwise.
         */
        boolean cancel(W id, boolean mayInterrupt);
    }
    
    private static final class NullFutureHandlerImplementation implements ManagedFutureImpl.FutureHandler {
        public void futureReady(Object id) {
        }
        public boolean cancel(Object id, boolean mayInterrupt) {
            return false;
        }
    }
}