package com.topcoder.client.contestMonitor.view.gui.menu;

import junit.framework.TestCase;

/**
 * Testcases for class ProblemListField
 * @author Giorgos Zervas
 * @see ProblemListField
 */
public class ProblemListFieldTest extends TestCase {
    /**
     * Test providing null parameter to constructor method
     */
    public void testConstructorNullParam() {
        boolean hasException = false;
        try {
            new ProblemListField(null);
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
            hasException = true;
        }
        assertEquals(true, hasException);
    }
}
