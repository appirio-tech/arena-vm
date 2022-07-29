package com.topcoder.server.mpsqas.listener;

import com.topcoder.server.ejb.MPSQASServices.MPSQASServices;
import com.topcoder.netCommon.mpsqas.ContestInformation;

import java.util.ArrayList;

import com.topcoder.shared.util.logging.*;

/**
 * Watches for the end of contests and performs post contest checks.
 *
 * @author mitalub
 */
public class ContestWatcher implements Runnable {

    private static final Logger logger = Logger.getLogger(
            ContestWatcher.class);

    private ArrayList contests = new ArrayList();

    //ms
    private static final int INTERVAL = 600000;

    private MPSQASServices services;
    private Thread thread;

    public void init(MPSQASServices services) {
        this.services = services;
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    /**
     * Every INTERVAL ms, checks to see if the list of contests has changed.
     */
    public void run() {
        ArrayList contestInfos;
        ArrayList newIds;
        while (!thread.isInterrupted()) {
            try {
                contestInfos = services.getContests(-1);
                newIds = new ArrayList();
                for (int i = 0; i < contestInfos.size(); i++) {
                    newIds.add(new Integer(((ContestInformation) contestInfos.get(i))
                            .getRoundId()));
                }

                for (int i = 0; i < contests.size(); i++) {
                    if (newIds.indexOf(contests.get(i)) == -1) {
                        logger.info("It appears contest with id = " + contests.get(i)
                                + " is over.  Calling wrapUpContest in bean...");
                        services.wrapUpContest(((Integer) contests.get(i)).intValue());
                    }
                }

                contests = newIds;
            } catch (Exception e) {
                logger.error("Error checking for past contests.", e);
            }

            try {
                Thread.sleep(INTERVAL);
            } catch (Exception e) {
            }
        }
    }
}
