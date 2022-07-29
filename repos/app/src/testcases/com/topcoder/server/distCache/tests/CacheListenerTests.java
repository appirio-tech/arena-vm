package com.topcoder.server.distCache.tests;

import com.topcoder.server.distCache.Cache;
import com.topcoder.server.distCache.CachedValue;
import com.topcoder.server.distCache.CacheUpdateListener;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;

public class CacheListenerTests
        extends TestCase {

    public CacheListenerTests(String name) {
        super(name);
    }

    public void testNoChanges() {
        Cache cache = new Cache();
        TestListener listener = new TestListener();

        cache.setUpdateListener(listener);

        ArrayList changed = listener.getChanged();

        assertEquals("changed list should be empty", 0, changed.size());
    }


    public void testOneSet() {
        Cache cache = new Cache();
        TestListener listener = new TestListener();

        cache.setUpdateListener(listener);

        cache.update("foo", "bar", 1, 1);

        ArrayList changed = listener.getChanged();
        assertEquals("changed list size", 1, changed.size());
        assertEquals("contains foo", true, changed.contains("foo"));
    }


    // no event for unknown items
    public void testOneRemove() {
        Cache cache = new Cache();
        TestListener listener = new TestListener();

        cache.setUpdateListener(listener);

        cache.update("foo", null, 1, 1);
        cache.update("foo", null, 1, 1);
        cache.update("bar", null, 1, 1);

        ArrayList changed = listener.getChanged();
        assertEquals("changed list size", 0, changed.size());
    }

    public void testSetAndReset() {
        Cache cache = new Cache();
        TestListener listener = new TestListener();

        cache.setUpdateListener(listener);

        cache.update("foo", "bar", 1, 1);
        cache.update("foo", "baz", 1, 1);
        cache.update("foo", "boing", 1, 1);

        ArrayList changed = listener.getChanged();
        assertEquals("changed list size", 3, changed.size());

        for (int i = 0; i < changed.size(); i++) {
            assertEquals("elem#" + i, "foo", changed.get(i));
        }
    }

    public void testSetAndRemove() {
        Cache cache = new Cache();
        TestListener listener = new TestListener();

        cache.setUpdateListener(listener);

        cache.update("foo", "bar", 1, 1);
        cache.update("foo", null, 1, 1);

        ArrayList changed = listener.getChanged();
        assertEquals("changed list size", 2, changed.size());

        for (int i = 0; i < changed.size(); i++) {
            assertEquals("elem#" + i, "foo", changed.get(i));
        }
    }

    public void testSetMany() {
        Cache cache = new Cache();
        TestListener listener = new TestListener();

        cache.setUpdateListener(listener);

        cache.update("foo", "bar", 1, 1);
        cache.update("bar", "baz", 1, 1);
        cache.update("baz", "boing", 1, 1);

        ArrayList changed = listener.getChanged();
        assertEquals("changed list size", 3, changed.size());
        assertEquals("contains foo", true, changed.contains("foo"));
        assertEquals("contains bar", true, changed.contains("bar"));
        assertEquals("contains baz", true, changed.contains("baz"));
    }

    public void testSetAndUnsetMany() {
        Cache cache = new Cache();
        TestListener listener = new TestListener();

        cache.setUpdateListener(listener);

        cache.update("foo", "bar", 1, 1);
        cache.update("bar", null, 1, 1);
        cache.update("baz", "boing", 1, 1);
        cache.update("foo", null, 1, 1);

        ArrayList changed = listener.getChanged();
        Collections.sort(changed);
        assertEquals("changed list size", 3, changed.size());
        assertEquals("item 1", "baz", changed.get(0));
        assertEquals("item 2", "foo", changed.get(1));
        assertEquals("item 3", "foo", changed.get(2));


    }

    public static class TestListener
            implements CacheUpdateListener {

        ArrayList changed = new ArrayList();

        public void valueUpdated(CachedValue value) {
            changed.add(value.getKey());
        }

        ArrayList getChanged() {
            return changed;
        }
    }


}
