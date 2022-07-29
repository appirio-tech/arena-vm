/*
 * ConnectionStatusMonitorTest
 * 
 * Created 03/17/2006
 */
package com.topcoder.server.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

/**
 * Test case for ConnectionStatusMonitor
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ConnectionStatusMonitorTest extends TestCase {
    private static final int WT_SWITCH = 50;
    private static final int KEEPALIVE_TIMEOUT = 100;
    private static final int SCAN_INTERVAL = 400;
    private static final int TIME_BEFORE_1SCAN_BUT_ACTIVE = SCAN_INTERVAL - 2 * KEEPALIVE_TIMEOUT + WT_SWITCH;
    private static final int TIME_AFTER_2SCAN_WITH_1SCAN_WAITED = 2*SCAN_INTERVAL-TIME_BEFORE_1SCAN_BUT_ACTIVE+WT_SWITCH;
    
    private List inactiveConns = new ArrayList();
    private ConnectionStatusMonitor monitor;
    
    public ConnectionStatusMonitorTest() {
        monitor = new ConnectionStatusMonitor(SCAN_INTERVAL, KEEPALIVE_TIMEOUT);
        monitor.setListener(new ConnectionStatusMonitor.Listener() {
            public void inactiveConnectionsDetected(List inactiveConnections) {
                inactiveConns.add(inactiveConnections);
            }
        });
    }
    
    protected void setUp() throws Exception {
        inactiveConns = new ArrayList();
    }

    public void testOnlyValuesInWindow() throws Exception {
        monitor.start();
        try {
            monitor.newConnectionRegistered(new Integer(1));
            monitor.newConnectionRegistered(new Integer(2));
            Thread.sleep(TIME_BEFORE_1SCAN_BUT_ACTIVE);
            monitor.newConnectionRegistered(new Integer(3));
            Thread.sleep(SCAN_INTERVAL-TIME_BEFORE_1SCAN_BUT_ACTIVE+WT_SWITCH);
        } finally {
            monitor.stop();
        }
        verify(new int[]{1,2});
    }

    public void testScanStartAfterSpecifiedInterval() throws Exception {
        monitor.start();
        try {
            monitor.newConnectionRegistered(new Integer(1));
            monitor.newConnectionRegistered(new Integer(2));
            Thread.sleep(WT_SWITCH);
            monitor.newConnectionRegistered(new Integer(3));
            Thread.sleep(SCAN_INTERVAL);
        } finally {
            monitor.stop();
        }
        verify(new int[]{1,2,3});
    }

    public void testLastTimeGreaterThanMinIncludeIn2Scan() throws Exception {
        monitor.start();
        try {
            monitor.newConnectionRegistered(new Integer(1));
            Thread.sleep(TIME_BEFORE_1SCAN_BUT_ACTIVE);
            monitor.newConnectionRegistered(new Integer(2));
            Thread.sleep(TIME_AFTER_2SCAN_WITH_1SCAN_WAITED);
        } finally {
            monitor.stop();
        }
        verify(new int[]{1}, new int[] {2});
    }
    
    public void testRequestReceived() throws Exception {
        monitor.start();
        try {
            monitor.newConnectionRegistered(new Integer(1));
            monitor.newConnectionRegistered(new Integer(2));
            monitor.newConnectionRegistered(new Integer(3));
            Thread.sleep(TIME_BEFORE_1SCAN_BUT_ACTIVE);
            monitor.requestReceived(new Integer(1));
            Thread.sleep(TIME_AFTER_2SCAN_WITH_1SCAN_WAITED);
        } finally {
            monitor.stop();
        }
        verify(new int[]{2,3}, new int[] {1});
    }

    public void testRequestReceivedForUnexistentConnection() throws Exception {
        monitor.start();
        try {
            monitor.newConnectionRegistered(new Integer(2));
            monitor.newConnectionRegistered(new Integer(3));
            Thread.sleep(TIME_BEFORE_1SCAN_BUT_ACTIVE);
            monitor.requestReceived(new Integer(1));
            Thread.sleep(TIME_AFTER_2SCAN_WITH_1SCAN_WAITED);
        } finally {
            monitor.stop();
        }
        verify(new int[]{2,3});
    }
    public void testConnectionClosed() throws Exception {
        monitor.start();
        try {
            monitor.newConnectionRegistered(new Integer(1));
            monitor.newConnectionRegistered(new Integer(2));
            Thread.sleep(TIME_BEFORE_1SCAN_BUT_ACTIVE);
            monitor.newConnectionRegistered(new Integer(3));
            monitor.connectionClosed(new Integer(1));
            Thread.sleep(TIME_AFTER_2SCAN_WITH_1SCAN_WAITED);
        } finally {
            monitor.stop();
        }
        verify(new int[]{2}, new int[] {3});
    }
    
    public void testConnectionClosedForUnexistentConnection() throws Exception {
        monitor.start();
        try {
            monitor.newConnectionRegistered(new Integer(2));
            Thread.sleep(TIME_BEFORE_1SCAN_BUT_ACTIVE);
            monitor.newConnectionRegistered(new Integer(3));
            monitor.connectionClosed(new Integer(1));
            Thread.sleep(TIME_AFTER_2SCAN_WITH_1SCAN_WAITED);
        } finally {
            monitor.stop();
        }
        verify(new int[]{2}, new int[] {3});
    }
    private void verify(int[] cnns) {
        List list = toList(cnns);
        verify(new Object[]{list});
    }

    /**
     * @param cnns
     * @return
     */
    private List toList(int[] cnns) {
        List list = new ArrayList(cnns.length);
        for (int i = 0; i < cnns.length; i++) {
            list.add( new Integer(cnns[i]));
            
        }
        return list;
    }

    private void verify(int[] cnns1, int[] cnns2) {
        verify(new Object[]{toList(cnns1), toList(cnns2)});
    }

    private void verify(Object[] listOfCnns) {
        List expected = Arrays.asList(listOfCnns);
        assertEquals(expected, inactiveConns);
        
    }
}
