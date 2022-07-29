/*
 * ArenaActionProcessor
 * 
 * Created Nov 6, 2007
 */
package com.topcoder.server.processor;

import com.topcoder.shared.arena.remoteactions.ArenaActionFactory;
import com.topcoder.shared.arena.remoteactions.ArenaActionListenerException;
import com.topcoder.shared.arena.remoteactions.ArenaActionRequestListener;
import com.topcoder.shared.util.logging.Logger;

/**
 * @author Diego Belfer (mural)
 * @version $Id: ArenaActionProcessor.java 68469 2008-02-15 13:41:08Z mural $
 */
public class ArenaActionProcessor {
    private static Logger log = Logger.getLogger(ArenaActionProcessor.class);
    private static ArenaActionRequestListener listener;
    
    public static void init() {
        if (listener != null) {
            return;
        }
        try {
            buildListener();
        } catch (Exception e) {
            log.error("Could not initialize ArenaActionListener", e);
        }
    }

    private static void buildListener() throws ArenaActionListenerException {
        log.info("Building Arena Action Listener" );
        listener = ArenaActionFactory.getFactory().createListener("engine");
        listener.setHandler(new ArenaActionRequestListener.Handler() {
            public void onBroadcast(int roundId, String message) throws BadBroadcastException {
                log.info("BroadcastAction: roundId=" + roundId + " message="+message);
                AdminBroadcastManager.getInstance().sendRoundBroadcast(-1, roundId, message);
            }
        });
        log.info("Arena Action Listener has been built" );
    }

    public static void start() {
        if (listener != null) {
            log.info("Starting Arena Action Listener" );
            try {
                listener.start();
            } catch (Exception e) {
                log.error("Could not start arena action listener", e);
            }
        }
    }

    public static void stop() {
        if (listener != null) {
            log.info("Stopping Arena Action Listener" );
            listener.stop();
        }
    }
}
