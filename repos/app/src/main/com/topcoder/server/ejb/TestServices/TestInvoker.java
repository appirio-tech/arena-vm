/*
 * TestInvoker
 * 
 * Created 10/23/2006
 */
package com.topcoder.server.ejb.TestServices;

import com.topcoder.server.farm.longtester.LongTesterInvoker;

/**
 * @author Diego Belfer (mural)
 * @version $Id: TestInvoker.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class TestInvoker {
    private static LongTesterInvoker instance;

    public static synchronized LongTesterInvoker getInstance() {
        if (instance == null) {
            instance = LongTesterInvoker.configure("TestSvc", new LongContestServicesTesterHandler());
        }
        return instance;
    }
}
