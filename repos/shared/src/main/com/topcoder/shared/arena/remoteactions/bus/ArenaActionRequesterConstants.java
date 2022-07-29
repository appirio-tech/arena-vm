/*
 * ArenaActionRequesterConstants
 * 
 * Created Nov 5, 2007
 */
package com.topcoder.shared.arena.remoteactions.bus;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ArenaActionRequesterConstants {
    public static final String DATA_BUS_REQ_CONFIG_KEY = "ArenaActionReqCfg";
    public static final String DATA_BUS_RESP_CONFIG_KEY = "ArenaActionResCfg";
    public static final String NAMESPACE = "arenaactions";
    
    static final String ACTION_BROADCAST = "broadcast";
    static final String ACTION_BROADCAST_ARG_ROUND_ID = "roundId";
    static final String ACTION_BROADCAST_ARG_MESSAGE = "message";
}
