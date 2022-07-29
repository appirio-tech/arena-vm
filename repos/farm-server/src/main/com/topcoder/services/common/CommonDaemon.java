/*
 * Copyright (C) - 2012 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.services.common;

import com.topcoder.server.common.ChallengeAttributes;
import com.topcoder.server.common.Location;
import com.topcoder.server.common.Submission;
import com.topcoder.server.common.UserTestAttributes;
import com.topcoder.server.tester.LongSubmission;
import com.topcoder.shared.util.logging.Logger;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * <p>
 * Version 1.1 (TC Competition Engine Code Execution Time Issue) change notes:
 *  <ul>
 *      <li>Make {@link #OBJECT_SIZE_LIMIT} configurable.</li>
 *  </ul>
 * </p>
 * 
 * @author TCSASSEMBER
 * @version 1.1
 */
public final class CommonDaemon {

    private static final Logger logger = Logger.getLogger(CommonDaemon.class);

    /**
     * Represents the max compiled object size.
     */
    public static final int OBJECT_SIZE_LIMIT = getMaxObjectSize();
    public static final String SIZE_LIMIT_MESSAGE = "Your compiled binary exceeds the current system limit of "+ OBJECT_SIZE_LIMIT +" bytes";

    private CommonDaemon() {
    }

    public static String getPackageName(Submission sub) {
        Location location = sub.getLocation();
        return "u" + sub.getCoderID() +
                ".c" + location.getContestID() +
                ".r" + location.getRoundID() +
                ".p" + sub.getComponent().getComponentID();
    }
    public static String getPackageName(LongSubmission sub) {
        return "u" + sub.getCoderID() +
                ".c" + sub.getContestID() +
                ".r" + sub.getRoundID() +
                ".p" + sub.getComponentID();
    }

    /////////////////////////////////////////////////////////////////////////////////
    public static String getPackageName(ChallengeAttributes chal) {
        /////////////////////////////////////////////////////////////////////////////////
        return "u" + chal.getDefendantId() +
                ".c" + chal.getLocation().getContestID() +
                ".r" + chal.getLocation().getRoundID() +
                ".p" + chal.getComponent().getComponentID();
    }

    /////////////////////////////////////////////////////////////////////////////////
    public static String getPackageName(UserTestAttributes userTest) {
        /////////////////////////////////////////////////////////////////////////////////
        return "u" + userTest.getCoderId() +
                ".c" + userTest.getLocation().getContestID() +
                ".r" + userTest.getLocation().getRoundID() +
                ".p" + userTest.getComponent().getComponentID();
    }

    /*
    ///////////////////////////////////////////////////////////////////////////////////
    public static String getSolutionPackage(int componentId) {
    ///////////////////////////////////////////////////////////////////////////////////
      return "solutions.p" + componentId;
    }
    */
    public static boolean checkObjectSize(Object obj) {

        boolean valid = true;
        try {
            ByteArrayOutputStream byteout = new ByteArrayOutputStream();
            ObjectOutputStream    objout = new ObjectOutputStream(byteout);
            objout.writeObject(obj);
            objout.close();
            info("GT: and the size is... "+byteout.size());
            if (byteout.size() > OBJECT_SIZE_LIMIT) {
                valid = false;
            }

        } catch (java.io.IOException ioe) {
            error("IOE Error", ioe);
        }

        return valid;
    }

    private static void info(String message) {
        logger.info(message);
    }
    private static void error(String message, Throwable obj) {
        logger.error(message, obj);
    }
    
    /**
     * Gets the max compiled object size.
     * 
     * @return the max compiled object size.
     * @since 1.1
     */
    private static int getMaxObjectSize() {
        try {
            return Integer.parseInt(System.getProperty("com.topcoder.services.common.CommonDaemon.objectSize"));
        } catch (Exception e) {
            return 1000 * 1000;
        }
    }
}
