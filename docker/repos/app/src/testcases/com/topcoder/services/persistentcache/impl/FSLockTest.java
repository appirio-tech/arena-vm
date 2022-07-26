/*
 * FSLockTest
 * 
 * Created 05/21/2007
 */
package com.topcoder.services.persistentcache.impl;

import java.io.File;

import com.topcoder.farm.deployer.process.ProcessRunner;
import com.topcoder.farm.deployer.process.ProcessRunnerException;
import com.topcoder.farm.deployer.process.ProcessTimeoutException;
import com.topcoder.farm.deployer.process.ProcessRunner.ProcessRunResult;
import com.topcoder.farm.test.util.MTTestCase;
import com.topcoder.services.persistentcache.impl.FSLock;

/**
 * Test case for {@link FSLock}
 * 
 * @author Diego Belfer (mural)
 * @version $Id: FSLockTest.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class FSLockTest extends MTTestCase {
    private static File lockFile = new File("/tmp/fslock.lock");
    private static File lockFile1 = new File("/tmp/fslock1.lock");

    public void testBasicLockUnlock() throws Exception {
        FSLock lock = new FSLock(lockFile);
        lock.lock();
        lock.unlock();
    }
    
    public void testNonReentrantLock() throws Exception {
        FSLock lock = new FSLock(lockFile);
        lock.lock();
        try {
            lock.lock();
            fail("Expected exception");
        } catch (Exception e) {
        }
        lock.unlock();
    }
    
    public void test2ThreadFailsLockOnSameInstance() throws Exception {
        final FSLock lock = new FSLock(lockFile);
        lock.lock();
        run(new Runnable() {
            public void run() {
                lock.lock();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.unlock();
            }
        });
        startTiming();
        startAll();
        Thread.sleep(200);
        lock.unlock();
        waitAll();
        endTiming();
        assertTrue(getTiming() >= 400 && getTiming() < 500);
    }
    
    
    public void test2ThreadFailsLockOnDiffInstance() throws Exception {
        FSLock lock = new FSLock(lockFile);
        lock.lock();
        run(new Runnable() {
            public void run() {
                FSLock lock = new FSLock(lockFile);
                lock.lock();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.unlock();
            }
        });
        startTiming();
        startAll();
        Thread.sleep(200);
        lock.unlock();
        waitAll();
        endTiming();
        assertTrue(getTiming() >= 400 && getTiming() < 500);
    }
    
    
    public void test2ThreadSucceededLockOnDiffFiles() throws Exception {
        FSLock lock = new FSLock(lockFile);
        lock.lock();
        run(new Runnable() {
            public void run() {
                FSLock lock = new FSLock(lockFile1);
                lock.lock();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.unlock();
            }
        });
        startTiming();
        startAll();
        Thread.sleep(200);
        lock.unlock();
        waitAll();
        endTiming();
        assertTrue(getTiming() >= 200 && getTiming() < 300);
    }
    
    
    public void test2VMs() throws Exception {
        final Long[] results = new Long[2];
        run(new VMTester(results, 0));
        run(new VMTester(results, 1));
        startAllAndWait();
        assertTrue(results[0] != null && results[0].longValue() >= 2000);
        assertTrue(results[1] != null && results[1].longValue() >= 2000);
        assertTrue(results[0].longValue() + results[1].longValue() >= 3000);
        
        
    }
    
    private final class VMTester implements Runnable {
        private int index;
        private Long[] results;
        
        public VMTester(Long[] results, int index) {
            this.results = results;
            this.index = index;
        }
        
        public void run() {
            ProcessRunner runner = new ProcessRunner(new String[] {"java","-cp", System.getProperty("java.class.path"), TestFSTest.class.getName()});
            try {
                ProcessRunResult result = runner.run(null);
                System.out.println(result.getStdOut());
                System.out.println(result.getStdErr());
                String[] strings = result.getStdErr().split(" ");
                if (strings.length == 2) {
                    long time = Long.parseLong(strings[1]) - Long.parseLong(strings[0]);
                    results[index] = new Long(time);
                }
            } catch (ProcessTimeoutException e) {
                e.printStackTrace();
            } catch (ProcessRunnerException e) {
                e.printStackTrace();
            }
        }
    }

    public static class TestFSTest {
        public static void main(String[] args) throws InterruptedException {
            FSLock lock = new FSLock(lockFile);
            System.err.print(System.currentTimeMillis()+" ");
            lock.lock();
            Thread.sleep(2000);
            lock.unlock();
            System.err.print(System.currentTimeMillis());
        }
    }
}
