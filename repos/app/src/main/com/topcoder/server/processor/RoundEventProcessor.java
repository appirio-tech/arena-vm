/*
 * RoundEventProcessor
 * 
 * Created Oct 2, 2007
 */
package com.topcoder.server.processor;

import java.util.Iterator;

import com.topcoder.netCommon.contest.round.RoundType;
import com.topcoder.server.services.CoreServices;
import com.topcoder.shared.round.events.RoundCreatedEvent;
import com.topcoder.shared.round.events.RoundDeletedEvent;
import com.topcoder.shared.round.events.RoundEvent;
import com.topcoder.shared.round.events.RoundEventException;
import com.topcoder.shared.round.events.RoundEventFactory;
import com.topcoder.shared.round.events.RoundEventListener;
import com.topcoder.shared.round.events.RoundModifiedEvent;
import com.topcoder.shared.round.events.RoundModifiedEvent.RegistrationModification;
import com.topcoder.shared.util.logging.Logger;

/**
 * @author Diego Belfer (mural)
 * @version $Id: RoundEventProcessor.java 67962 2008-01-15 15:57:53Z mural $
 */
public class RoundEventProcessor {
    private static Logger log = Logger.getLogger(RoundEventProcessor.class);
    private static RoundEventListener listener;
    
    public static void init() {
        if (listener != null) {
            return;
        }
        try {
            buildListener();
        } catch (Exception e) {
            log.error("Could not initialize Round Event listener", e);
        }
    }

    private static void buildListener() throws RoundEventException {
        log.info("Building Round Event Listener" );
        listener = RoundEventFactory.getFactory().createListener("engine");
        listener.setHandler(new RoundEventListener.Handler() {
        
            public boolean handle(RoundEvent event) {
                return false;
            }
        
            public boolean handle(RoundModifiedEvent event) {
                if (mustProcess(event)) {
                    log.info("Processing round modified event: "+event);
                    boolean needsReload = false;
                    for (Iterator it = event.getModifications().iterator(); it.hasNext();) {
                        RoundModifiedEvent.RoundModification change = (RoundModifiedEvent.RoundModification) it.next();
                        if (change.getType() == RegistrationModification.ID) {
                            RegistrationModification regChange = ((RegistrationModification) change);
                            Processor.handleRegistrationChanged(event.getRoundId(), regChange.getAddedCoders(), regChange.getRemovedCoders());;
                        } else {
                            needsReload = true;
                        }
                    }
                    if (needsReload) {
                        Processor.reloadContestRoundIfNotStarted(event.getRoundId());
                    }
                }
                return true;
            }
        
            public boolean handle(RoundDeletedEvent event) {
                if (mustProcess(event)) {
                    log.info("Processing round deleted event: "+event);
                    Processor.unloadContestRound(event.getRoundId());
                } 
                return true;
            }
        
            public boolean handle(RoundCreatedEvent event) {
                if (mustProcess(event)) {
                    log.info("Processing round created event: "+event);
                    Processor.loadContestRound(event.getRoundId());
                }
                return true;
            }

            private boolean mustProcess(RoundEvent event) {
                //Currently only Education rounds use the events
                RoundType type = resolveType(event);
                boolean result = RoundType.EDUCATION_ALGO_ROUND_TYPE.equals(type);
                if (!result) {
                    if (log.isDebugEnabled()) {
                        log.debug("Received event but it will be not processed :" + event);
                    }
                }
                return result;
            }

            private RoundType resolveType(RoundEvent event) {
                RoundType type;
                if (event.isRoundTypeIdSet()) {
                    type = RoundType.get(event.getRoundTypeId());
                } else {
                    type = CoreServices.getContestRound(event.getRoundId()).getRoundType();
                }
                return type;
            }
        });
        log.info("Round Event Listener has been built" );
    }

    public static void start() {
        log.info("Starting Round Event Listener" );
        try {
            listener.start();
        } catch (Exception e) {
            log.error("Could not start round event listener", e);
        }
    }

    public static void stop() {
        log.info("Stopping Round Event Listener" );
        listener.stop();
    }
}
