package com.topcoder.server.distCache.tests;

import com.topcoder.server.distCache.CachedValue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

import junit.framework.*;

public class CachedValueTests
        extends TestCase {

    public CachedValueTests(String name) {
        super(name);
    }

    public void testCreateBasic() {
        CachedValue cached = new CachedValue("foo", "bar");

        assertEquals("key", "foo", cached.getKey());
        assertEquals("value", "bar", cached.getValue());
    }

    public void testGetSet() {
        CachedValue cached = new CachedValue("foo", "bar");

        for (int i = 0; i < 10; i++) {
            long time = System.currentTimeMillis();
            cached.setKey("foo." + i);
            cached.setValue(new Integer(i));
            cached.setPriority(i);
            cached.setLastUsed(time);
            cached.setVersion(i);

            assertEquals("key", "foo." + i, cached.getKey());
            assertEquals("val", new Integer(i), cached.getValue());
            assertEquals("prio", i, cached.getPriority());
            assertEquals("version", i, cached.getVersion());
            assertEquals("last", time, cached.getLastUsed());
        }
    }

    public void testIncrementVersion() {
        CachedValue cached = new CachedValue("foo", "bar");

        cached.setVersion(4);
        assertEquals("version", 4, cached.getVersion());
        cached.bumpVersion();
        assertEquals("version", 5, cached.getVersion());
        cached.bumpVersion();
        assertEquals("version", 6, cached.getVersion());
        cached.bumpVersion();
        cached.bumpVersion();
        assertEquals("version", 8, cached.getVersion());
    }

    public void testTimeCompare() {
        CachedValue cached1 = new CachedValue("foo.1", "bar");
        CachedValue cached2 = new CachedValue("foo.2", "bar");
        CachedValue cached3 = new CachedValue("foo.3", "bar");
        CachedValue cached4 = new CachedValue("foo.4", "bar");
        CachedValue cached5 = new CachedValue("foo.5", "bar");
        CachedValue cached6 = new CachedValue("foo.6", "bar");


        cached1.setLastUsed(4000);
        cached2.setLastUsed(2000);
        cached3.setLastUsed(3000);
        cached4.setLastUsed(2000);
        cached5.setLastUsed(2000);
        cached6.setLastUsed(3000);

        cached1.setPriority(8);
        cached2.setPriority(7);
        cached3.setPriority(6);
        cached4.setPriority(5);
        cached5.setPriority(5);
        cached6.setPriority(3);

        cached4.bumpVersion();
        cached4.bumpVersion();
        cached5.bumpVersion();

        ArrayList list = new ArrayList();
        list.add(cached1);
        list.add(cached2);
        list.add(cached3);
        list.add(cached4);
        list.add(cached5);
        list.add(cached6);

        Collections.sort(list, new CachedValue.TimeComparator());

        assertEquals("elem0", cached5, list.get(0));
        assertEquals("elem1", cached4, list.get(1));
        assertEquals("elem2", cached2, list.get(2));
        assertEquals("elem3", cached6, list.get(3));
        assertEquals("elem4", cached3, list.get(4));
        assertEquals("elem5", cached1, list.get(5));
    }

    public void testPriorityCompare() {
        CachedValue cached1 = new CachedValue("foo.1", "bar");
        CachedValue cached2 = new CachedValue("foo.2", "bar");
        CachedValue cached3 = new CachedValue("foo.3", "bar");
        CachedValue cached4 = new CachedValue("foo.4", "bar");
        CachedValue cached5 = new CachedValue("foo.5", "bar");
        CachedValue cached6 = new CachedValue("foo.6", "bar");


        cached1.setLastUsed(4000);
        cached2.setLastUsed(2000);
        cached3.setLastUsed(3000);
        cached4.setLastUsed(2000);
        cached5.setLastUsed(2000);
        cached6.setLastUsed(3000);

        cached1.setPriority(8);
        cached2.setPriority(7);
        cached3.setPriority(3);
        cached4.setPriority(5);
        cached5.setPriority(5);
        cached6.setPriority(7);

        cached4.bumpVersion();
        cached4.bumpVersion();
        cached5.bumpVersion();

        ArrayList list = new ArrayList();
        list.add(cached1);
        list.add(cached2);
        list.add(cached3);
        list.add(cached4);
        list.add(cached5);
        list.add(cached6);

        Collections.sort(list, new CachedValue.PriorityComparator());

        assertEquals("elem0", cached3, list.get(0));
        assertEquals("elem1", cached5, list.get(1));
        assertEquals("elem2", cached4, list.get(2));
        assertEquals("elem3", cached2, list.get(3));
        assertEquals("elem4", cached6, list.get(4));
        assertEquals("elem5", cached1, list.get(5));
    }

}
