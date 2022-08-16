package com.topcoder.server.listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.Vector;

import junit.framework.TestCase;

import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.shared.netCommon.SimpleCSHandler;
import com.topcoder.shared.netCommon.SimpleCSHandlerFactory;

public final class VmstatTest extends TestCase {

    private static final int PORT = AcceptHandlerTest.PORT + 600;
    private static final ListenerFactory[] FACTORY = {
        new NBIOListenerFactory(),
    };

    private int port_id = PORT;

    public VmstatTest(String name) {
        super(name);
    }

    private static String[] strtok(String s) {
        Vector t = new Vector();
        StringTokenizer tk = new StringTokenizer(s);
        while (tk.hasMoreTokens()) {
            t.addElement(tk.nextToken());
        }
        String[] a = new String[t.size()];
        for (int i = 0; i < t.size(); i++) {
            a[i] = t.elementAt(i).toString();
        }
        return a;
    }

    private void checkIdleValue(String msg, int n, int minExp, double avgExp, int maxExp) {
        try {
            Process process = Runtime.getRuntime().exec("vmstat -n 1 " + (n + 1));
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            int sum = 0;
            int min = 101;
            int max = -1;
            for (int i = 0; ; i++) {
                String s = reader.readLine();
                if (s == null) {
                    break;
                }
                if (i >= 3) {
                    String a[] = strtok(s);
                    int len = a.length;
                    int us = Integer.parseInt(a[len - 3]);
                    sum += us;
                    min = Math.min(min, us);
                    max = Math.max(max, us);
                }
            }
            double avg = (double) sum / n;
            assertTrue(msg + ", min: " + min + ", expected: " + minExp, min <= minExp);
            assertTrue(msg + ", avg: " + avg + ", expected: " + avgExp, avg <= avgExp);
            assertTrue(msg + ", max: " + max + ", expected: " + maxExp, max <= maxExp);
        } catch (IOException e) {
            fail("" + e);
        }
    }

    private void testZeroClients(ListenerFactory factory, int port) {
        ListenerInterfaceTest.TestProcessor processor = new ListenerInterfaceTest.TestProcessor(null, null);
        ListenerInterface controller = factory.createListener(port, processor, new SimpleCSHandlerFactory());
        try {
            controller.start();
        } catch (IOException e) {
            fail();
        }
        checkIdleValue("zeroClients", 3, 0, 1, 2);
        controller.stop();
    }

    public void testZeroClients() {
        for (int i = 0; i < FACTORY.length; i++) {
            testZeroClients(FACTORY[i], port_id++);
        }
    }

    private void testZeroClientsAfter(ListenerFactory factory, int port) {
        Object lock = new Object();
        ListenerInterfaceTest.TestProcessor processor = new ListenerInterfaceTest.TestProcessor(null, lock);
        ListenerInterface controller = factory.createListener(port, processor, new SimpleCSHandlerFactory());
        try {
            controller.start();
        } catch (IOException e) {
            fail();
        }
        ClientSocket socket = null;
        try {
            socket = new ClientSocket(AcceptHandlerTest.HOST, port, new SimpleCSHandler());
            synchronized (lock) {
                for (; ;) {
                    Collection set = processor.getConnections();
                    if (set.size() > 0) {
                        break;
                    }
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        fail();
                    }
                }
            }
            socket.close();
        } catch (IOException e) {
            fail();
        }
        checkIdleValue("zeroClientsAfter", 3, 0, 1, 2);
        controller.stop();
    }

    public void testZeroClientsAfter() {
        for (int i = 0; i < FACTORY.length; i++) {
            testZeroClientsAfter(FACTORY[i], port_id++);
        }
    }

}
