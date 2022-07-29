package com.topcoder.server.distCache;

import org.apache.log4j.Category;

public class CacheServerMain {

    static Category log = Category.getInstance("com.server.topcoder.distCache");

    public static void main(String[] args) {
        CacheServer server = new CacheServer();

        // log.debug("CACHE STARTING");

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-primary")) {
                server.setMode(CacheServer.MODE_PRIMARY);

            } else if (args[i].equals("-secondary")) {
                server.setMode(CacheServer.MODE_SECONDARY);

            } else {
                System.out.println("INVALID ARGUMENT: " + args[i]);
                return;
            }
        }

        server.startCache();
    }


}
