package com.topcoder.server.listener.util;

import junit.framework.TestCase;

public final class SerialIntegerGeneratorTest extends TestCase {

    public SerialIntegerGeneratorTest(String name) {
        super(name);
    }

    public void testGetIntegerLock() {
        SerialIntegerGenerator generator = new SerialIntegerGenerator();
        Integer i1 = generator.nextNewInteger();
        assertEquals(new Integer(0), i1);
        Integer i2 = generator.getInteger(0);
        assertEquals(new Integer(0), i2);
        assertTrue(i1 == i2);
    }

    public void testRemove() {
        SerialIntegerGenerator generator = new SerialIntegerGenerator();
        assertEquals(0, generator.getSize());
        generator.nextNewInteger();
        assertEquals(1, generator.getSize());
        generator.removeInteger(new Integer(0));
        assertEquals(0, generator.getSize());
    }

}
