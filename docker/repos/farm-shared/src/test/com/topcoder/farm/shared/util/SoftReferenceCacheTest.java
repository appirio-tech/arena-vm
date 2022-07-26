/*
 * SoftReferenceCacheTest
 *
 * Created 03/29/2007
 */
package com.topcoder.farm.shared.util;

import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit Test for SoftReferenceCache
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class SoftReferenceCacheTest {

    /**
     * What we put is what we get
     */
	@Test
    public void testPutAndGet() throws Exception {
        SoftReferenceCache cache = new SoftReferenceCache();
        Object obj1 = getNewObject();
        Object obj2 = getNewObject();
        cache.put("1", obj1);
        cache.put("2", obj2);
        Assert.assertEquals(obj1, cache.get("1"));
        Assert.assertEquals(obj2, cache.get("2"));
        Assert.assertEquals(2, cache.size());
    }

    /**
     * Unreachable value is removed after a GC.
     */
    @Ignore
    @Test
    public void testPutAndGetNoReachable() throws Exception {
        SoftReferenceCache cache = new SoftReferenceCache();
        Object obj2 = getNewObject();
        cache.put("1", getNewObject());
        cache.put("2", obj2);
        forceCollect();
        Assert.assertEquals(null, cache.get("1"));
        Assert.assertEquals(obj2, cache.get("2"));
        Assert.assertEquals(1, cache.size());
    }

    /**
     * Unreachable value is removed after a GC.
     */
    @Test
    @Ignore
    public void testPutAndContains() throws Exception {
        SoftReferenceCache cache = new SoftReferenceCache();
        Object obj2 = getNewObject();
        cache.put("1", getNewObject());
        cache.put("2", obj2);
        forceCollect();
        Assert.assertFalse(cache.containsKey("1"));
        Assert.assertTrue(cache.containsKey("2"));
    }

    /**
     * Removes should remove the value
     */
    @Test
    @Ignore
    public void testPutAndRemove() throws Exception {
        SoftReferenceCache cache = new SoftReferenceCache();
        Object obj2 = getNewObject();
        cache.put("1", getNewObject());
        cache.put("2", obj2);
        forceCollect();
        cache.remove("2");
        Assert.assertFalse(cache.containsKey("1"));
        Assert.assertFalse(cache.containsKey("2"));
        Assert.assertEquals(0, cache.size());
    }


    /**
     * Clear should remove everything
     */
    @Test
    public void testClear() throws Exception {
        SoftReferenceCache cache = new SoftReferenceCache();
        Object obj2 = getNewObject();
        cache.put("1", getNewObject());
        cache.put("2", obj2);
        forceCollect();
        cache.clear();
        Assert.assertFalse(cache.containsKey("1"));
        Assert.assertFalse(cache.containsKey("2"));
        Assert.assertEquals(0, cache.size());
    }

    /**
     * Keyset should return only keys with values associated
     */
    @Test
    @Ignore
    public void testKeySet() throws Exception {
        SoftReferenceCache cache = new SoftReferenceCache();
        Object obj2 = getNewObject();
        cache.put("1", getNewObject());
        cache.put("2", obj2);
        forceCollect();
        @SuppressWarnings("rawtypes")
		Set set = cache.keySet();
        Assert.assertTrue(set.contains("2"));
        Assert.assertEquals(1, set.size());
    }

    // TODO: this is a flawed way to attempt to have something garbage collected
    private void forceCollect() throws InterruptedException {
        for (int i = 0; i < 200; i++) {
            int[] x = new int[1000000];
            System.gc();
            x[0]++;
        }
        Thread.sleep(100);
    }

    private Object getNewObject() {
        return new Object();
    }
}
