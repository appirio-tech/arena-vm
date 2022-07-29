/*
 * TestProcess
 * 
 * Created Jun 18, 2008
 */
package com.topcoder.client.testerApplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import com.topcoder.client.connectiontype.ConnectionType;

/**
 * @autor Diego Belfer (Mural)
 * @version $Id: TestProcess.java 71581 2008-07-09 22:36:59Z dbelfer $
 */
public abstract class TestProcess {
    private String host;
    private int port;
    private String tunnel;
    private boolean ssl;
    private int threadNum;
    private int packetSize;
    private int batches;
    private int messages;
    private ConnectionType[] types;


    public TestProcess(String host, int port, String tunnel, boolean ssl, int threadNum, int packetSize, int batches, int messages, ConnectionType[] types) {
        super();
        this.host = host;
        this.port = port;
        this.tunnel = tunnel;
        this.ssl = ssl;
        this.threadNum = threadNum;
        this.packetSize = packetSize;
        this.batches = batches;
        this.messages = messages; 
        this.types = types;
    }

    public void runTest() {
        try {
            TestThread[] testers = new TestThread[threadNum];
            ArrayList tests = new ArrayList();
            for (int i = 0; i < types.length; ++i) {
                tests.clear();
                appendLog("Testing " + types[i] + ", packet size=" + packetSize + ", threads=" + threadNum
                        + ", batch=" + batches);
                types[i].select();
                ConnectionType type = types[i];
                for (int k = 0; k < batches; ++k) {
                    for (int j = 0; j < testers.length; ++j) {
                        testers[j] = new TestThread("Tester " + j, packetSize, ssl, type, messages);
                        testers[j].start();
                    }
                    for (int j = 0; j < testers.length; ++j) {
                        try {
                            testers[j].join();
                        } catch (InterruptedException e) {
                        }
                        tests.add(testers[j]);
                    }
                }
                type.unselect();
                Collections.sort(tests);
                double avg = 0.0;
                for (Iterator iter = tests.iterator(); iter.hasNext();) {
                    TestThread test = (TestThread) iter.next();
                    test.close();
                    avg += ((double) test.getRunTime()) / threadNum / batches;
                }
                double med = (tests.size() % 2 != 0)
                        ? ((TestThread) tests.get(tests.size() / 2)).getRunTime()
                        : (((TestThread) tests.get(tests.size() / 2 - 1)).getRunTime() / 2.0 + ((TestThread) tests
                                .get(tests.size() / 2)).getRunTime() / 2.0);
                appendLog("  Round-trip time:");
                appendLog("    Min: " + ((TestThread) tests.get(0)).getRunTime() + "ms");
                appendLog("    Max: " + ((TestThread) tests.get(tests.size() - 1)).getRunTime() + "ms");
                appendLog("    Avg: " + Math.floor(avg * 100.0) / 100.0 + "ms");
                appendLog("    Med: " + Math.floor(med * 100.0) / 100.0 + "ms");
                appendLog("--------------------------");
            }
        } finally {
            
        }
    }

    private synchronized void appendLog(String text) {
        bareAppendLog(text);
    }
    
    protected abstract void bareAppendLog(String text);

    private class TestThread extends Thread implements Comparable {
        private String name;
        private int length;
        private long time = -1;
        private boolean ssl;
        private Tester tester;
        private ConnectionType type;
        private int messages;

        public int compareTo(Object o) {
            TestThread other = (TestThread) o;
            if (time == other.time)
                return 0;
            if (time < other.time)
                return -1;
            else
                return 1;
        }

        public TestThread(String name, int length, boolean ssl, ConnectionType type, int messages) {
            this.name = name;
            this.length = length;
            this.ssl = ssl;
            this.type = type;
            this.messages = messages; 
        }

        public long getRunTime() {
            return time;
        }

        public void close() {
            if (tester != null) {
                try {
                    tester.close();
                } catch (IOException e) {
                }
                tester = null;
            }
        }

        public void run() {
            tester = null;
            long startTime = new Date().getTime();
            try {
                tester = new Tester(host, port, tunnel, type, ssl);
                tester.sendAndWait(length, messages);
            } catch (Exception e) {
                appendLog("ERROR: " + name + ": " + e.toString());
            } finally {
                time = new Date().getTime() - startTime;
            }
        }
    }

}
