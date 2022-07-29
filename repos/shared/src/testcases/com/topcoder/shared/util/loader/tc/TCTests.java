package com.topcoder.shared.util.loader.tc;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author dok
 * @version $Revision$ Date: 2005/01/01 00:00:00
 *          Create Date: Dec 12, 2006
 */
public class TCTests {
    public static Test suite() {
        final TestSuite suite = new TestSuite();

        suite.addTest(new TestSuite(CoderTestCase.class));
        return suite;
    }
}