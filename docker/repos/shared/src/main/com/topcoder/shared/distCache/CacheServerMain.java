package com.topcoder.shared.distCache;

import com.topcoder.shared.util.logging.Logger;

/**
 * @author orb
 * @version  $Revision$
 */
public class CacheServerMain {
    private static Logger log = Logger.getLogger(CacheServerMain.class);

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        log.debug("CACHE STARTING");
        int mode = CacheServer.MODE_PRIMARY;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-primary")) {
                mode = CacheServer.MODE_PRIMARY;

            } else if (args[i].equals("-secondary")) {
                mode = CacheServer.MODE_SECONDARY;

            } else {
                log.info("INVALID ARGUMENT: " + args[i]);
                return;
            }
        }
        CacheServer server = new CacheServer();
        server.setMode(mode);
        server.startCache();
    }


}
