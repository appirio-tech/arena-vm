/*
 * RoundEventListenerImpl
 * 
 * Created Oct 1, 2007
 */
package com.topcoder.shared.arena.remoteactions.bus;

import java.util.Map;
import java.util.concurrent.Executor;

import com.topcoder.shared.arena.remoteactions.ArenaActionListenerException;
import com.topcoder.shared.arena.remoteactions.ArenaActionRequestListener;
import com.topcoder.shared.messagebus.BusFactory;
import com.topcoder.shared.messagebus.BusRequestListener;
import com.topcoder.shared.messagebus.invoker.BusRemoteInvocationListener;
import com.topcoder.shared.util.logging.Logger;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ArenaActionRequestListenerImpl implements ArenaActionRequestListener {
    private Logger log = Logger.getLogger(this.getClass());
    private BusRemoteInvocationListener listener;
    private Handler handler;
    private String moduleName;
    
    public ArenaActionRequestListenerImpl(String moduleName) throws ArenaActionListenerException {
        this.moduleName = moduleName;
        try {
            BusRequestListener busListener = BusFactory.getFactory().createRequestListener(ArenaActionRequesterConstants.DATA_BUS_RESP_CONFIG_KEY, moduleName);
            listener = new BusRemoteInvocationListener(moduleName, ArenaActionRequesterConstants.NAMESPACE, busListener);
        } catch (Exception e) {
            throw new ArenaActionListenerException("Could not create underlying provider", e);
        }
        registerActions();
    }

    private void registerActions() {
        listener.registerActionProcessor(ArenaActionRequesterConstants.NAMESPACE, ArenaActionRequesterConstants.ACTION_BROADCAST, 
                new BusRemoteInvocationListener.ActionProcessor() {
                    public Object process(String action, Map namedArguments) throws Exception {
                        return doBroadcast(namedArguments);
                    }
                });
    }
    
    public void start() throws ArenaActionListenerException {
        try {
            listener.start();
        } catch (Exception e) {
            throw new ArenaActionListenerException("Could not start underlying service",e);
        }
    }
    
    public void stop() {
        listener.stop();
    }

    public void setHandler(Handler h) throws ArenaActionListenerException {
        this.handler = h;
    }

    public String getModuleName() {
        return moduleName;
    }
    
    public Executor setRunner(Executor runner) {
        return listener.setRunner(runner);
    }
    
    /*-----------------------------------------------------------------------------------------------
     * Actions
     *-----------------------------------------------------------------------------------------------*/
    private Object doBroadcast(Map namedArguments) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Received broadcast action");
        }
        Handler h = handler;
        if (h != null) {
            int roundId = ((Integer) namedArguments.get(ArenaActionRequesterConstants.ACTION_BROADCAST_ARG_ROUND_ID)).intValue();
            String message = (String) namedArguments.get(ArenaActionRequesterConstants.ACTION_BROADCAST_ARG_MESSAGE);
            h.onBroadcast(roundId, message);
        } else {
            throw new IllegalStateException("No handler was configured for handling request");
        }
        return null;
    }
}
