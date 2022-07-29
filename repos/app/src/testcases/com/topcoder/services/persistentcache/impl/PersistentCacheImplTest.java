/*
 * PersistentCacheImplTest
 * 
 * Created 05/21/2007
 */
package com.topcoder.services.persistentcache.impl;

import java.io.File;
import java.util.Arrays;
import java.util.Random;

import com.topcoder.farm.test.util.MTTestCase;
import com.topcoder.server.util.FileUtil;
import com.topcoder.services.persistentcache.PersistentCache;
import com.topcoder.services.persistentcache.PersistentCacheException;

/**
 * Test case for {@link PersistentCacheImpl}
 * 
 * @author Diego Belfer (mural)
 * @version $Id: PersistentCacheImplTest.java 68012 2008-01-16 18:55:14Z thefaxman $
 */
public class PersistentCacheImplTest extends MTTestCase {
    protected static final File rootFolder = new File("/tmp/persitentcache");
    
    public PersistentCacheImplTest() {
        super();
    }

    public void testCreateAndGetVersion() throws Exception {
        removeCache();
        PersistentCache cache = buildCacheInstance();
        assertEquals(0, cache.getVersion());
    }

    public void testPutAndGet() throws Exception {
        PersistentCache cache = buildCacheInstance();
        cache.put("Key1", "Value11");
        assertEquals("Value11", cache.get("Key1"));
        cache.put("Key1", "Value1");
        assertEquals("Value1", cache.get("Key1"));
        assertEquals(1, cache.size());
    }

    public void testGetPreviousStored() throws Exception {
        PersistentCache cache = buildCacheInstance();
        assertEquals("Value1", cache.get("Key1"));
    }
    
    public void testClear() throws Exception {
        PersistentCache cache = buildCacheInstance();
        cache.clear();
        assertNull(cache.get("Key1"));
        assertEquals(0,cache.size());
        assertEquals(2, new File(rootFolder, "ID1").list().length);
    }
    
    public void testClearStored() throws Exception {
        PersistentCache cache = buildCacheInstance();
        assertNull(cache.get("Key1"));
        assertEquals(0,cache.size());
    }
    
    public void testMinimalVersion() throws Exception {
        PersistentCache cache = buildCacheInstance();
        cache.put("Key1", "Value1");
        cache.setMinimalVersion(1);
        assertNull(cache.get("Key1"));
        assertEquals(0,cache.size());
        assertEquals(1, cache.getVersion());
    }
    
    public void testMinimalVersionStored() throws Exception {
        PersistentCache cache = buildCacheInstance();
        assertNull(cache.get("Key1"));
        assertEquals(0,cache.size());
        assertEquals(1, cache.getVersion());
    }
    
    public void testClearKeepsVersion() throws Exception {
        PersistentCache cache = buildCacheInstance();
        cache.clear();
        assertNull(cache.get("Key1"));
        assertEquals(1, cache.getVersion());
    }

    public void testBigArraySet() throws Exception {
        PersistentCache cache = buildCacheInstance();
        int size = 1100000;
        Random random = new Random(1);
        int[] bigArray = new int[size];
        for (int i = 0; i < bigArray.length; i++) {
            bigArray[i] = random.nextInt();
        }
        cache.put("value", bigArray);
        assertTrue(Arrays.equals(bigArray, (int[]) cache.get("value")));
        assertEquals(1, cache.size());
    }
    
    public void testIntArraySet() throws Exception {
        PersistentCache cache = buildCacheInstance();
        int size = Short.MAX_VALUE / 2;
        Random random = new Random(1);
        int[] bigArray = new int[size];
        for (int i = 0; i < bigArray.length; i++) {
            bigArray[i] = random.nextInt();
        }
        cache.put("value", bigArray);
        assertTrue(Arrays.equals(bigArray, (int[]) cache.get("value")));
        assertEquals(1, cache.size());
    }
    
    public void testStringArraySet() throws Exception {
        PersistentCache cache = buildCacheInstance();
        int size = Short.MAX_VALUE / 2;
        Random random = new Random(1);
        String[] bigArray = new String[size];
        for (int i = 0; i < bigArray.length; i++) {
            bigArray[i] = ""+random.nextInt();
        }
        cache.put("value", bigArray);
        assertTrue(Arrays.equals(bigArray, (String[]) cache.get("value")));
        assertEquals(1, cache.size());
    }
    
    public void test2IntArraySet() throws Exception {
        PersistentCache cache = buildCacheInstance();
        int size = 10;
        Random random = new Random(1);
        int[][] bigArray = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                bigArray[i][j] = random.nextInt();
            }
        }
        cache.put("value", bigArray);
        Object got = cache.get("value");
        assertTrue(Arrays.deepEquals(bigArray, (int[][]) got));
        assertEquals(1, cache.size());
    }
    
    public void test2DoubleArraySet() throws Exception {
        PersistentCache cache = buildCacheInstance();
        int size = 10;
        Random random = new Random(1);
        double[][] bigArray = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                bigArray[i][j] = random.nextInt();
            }
        }
        cache.put("value", bigArray);
        Object got = cache.get("value");
        assertTrue(Arrays.deepEquals(bigArray, (double[][]) got));
        assertEquals(1, cache.size());
    }
    
    public void test2ObjectArraySet() throws Exception {
        PersistentCache cache = buildCacheInstance();
        int size = 10;
        Random random = new Random(1);
        Object[][] bigArray = new Object[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                bigArray[i][j] = new Integer(random.nextInt());
            }
        }
        cache.put("value", bigArray);
        Object got = cache.get("value");
        assertTrue(Arrays.deepEquals(bigArray, (Object[][]) got));
        assertEquals(1, cache.size());
    }
    
    public void test2IntObjectArraySet() throws Exception {
        PersistentCache cache = buildCacheInstance();
        int size = 10;
        Random random = new Random(1);
        Integer[][] bigArray = new Integer[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                bigArray[i][j] = new Integer(random.nextInt());
            }
        }
        cache.put("value", bigArray);
        Object got = cache.get("value");
        assertTrue(Arrays.deepEquals(bigArray, (Integer[][]) got));
        assertEquals(1, cache.size());
    }
    
    protected void removeCache() {
        FileUtil.deleteRecursive(rootFolder);
    }

    protected PersistentCache buildCacheInstance() throws PersistentCacheException {
        return new PersistentCacheImpl(new File(rootFolder, "ID1"), "ID1");
    }
}
