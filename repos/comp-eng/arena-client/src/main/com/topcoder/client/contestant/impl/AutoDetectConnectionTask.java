/*
 * AutoDetectConnectionTask
 * 
 * Created 04/19/2007
 */
package com.topcoder.client.contestant.impl;

import com.topcoder.client.connectiontype.ConnectionType;
import com.topcoder.client.contestant.Contestant.StatusListener;



/**
 * Tasks used for connection type detection.<p>
 * 
 * It allows cancellation.<p>
 * 
 * @author Diego Belfer (mural)
 * @version $Id: AutoDetectConnectionTask.java 71575 2008-07-09 20:40:55Z dbelfer $
 */
class AutoDetectConnectionTask {
    private volatile boolean cancelled;
    private ContestantImpl contestant;
    private StatusListener listener;
    private TestConnectionTypeThread testerThread;
    
    public AutoDetectConnectionTask(ContestantImpl contestant, StatusListener listener) {
        this.contestant = contestant;
        this.listener = listener;
    }

    /**
     * Runs autodetect process and returns the selected type if any was found.
     * 
     * @return The selected connection type, <code>null</code> if none or cancelled.
     */
    public ConnectionType autoDetect() {
        ConnectionType selected = null;
        try {
            ConnectionType[] types = ConnectionType.getAvailableTypes();
            for (int i = 0; !cancelled && selected == null && i < types.length; i++) {
                ConnectionType type = types[i];
                listener.updateStatus(type.getName()+" connection: connecting...");
                type.select();
                testerThread = null;
                try {
                    if (contestant.openConnection(type) && !cancelled) {
                        testerThread = new TestConnectionTypeThread(type);
                        testerThread.start();
                        testerThread.join(5000);
                        if (testerThread.isSuccessful()) {
                            selected = type;
                        } else {
                            listener.updateStatus(type.getName()+" connection: Failed!");
                            Thread.sleep(500);
                        }
                    } else {
                        listener.updateStatus(type.getName()+" connection: Failed!");
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    try {
                        if (testerThread != null) {
                            testerThread.stopTest();
                        }
                        contestant.closeConnection();
                    } catch (Exception e) {
                        selected = null;
                    }
                    type.unselect();
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return selected;
    }
    
    /**
     * Cancels the current autodetect process. Cancellation is not immediate
     * and may take several seconds. Despite of that this methods returns immediatly.
     */
    public void cancel() {
        cancelled = true;
        final TestConnectionTypeThread thread = testerThread;
        if (thread!=null) {
            thread.stopTest();
        }
    }
    
    private final class TestConnectionTypeThread extends Thread {
        private final ConnectionType typeToTest;
        private volatile boolean stopped = false;
        private volatile boolean successful = false;
        
        public TestConnectionTypeThread(ConnectionType typeToTest) {
            super("AutoDetect-"+typeToTest.getName());
            setDaemon(true);
            this.typeToTest = typeToTest;
        }
        
        public void stopTest() {
            stopped = true;
            this.interrupt();
        }

        public void run() {
            if (stopped) {
                return;
            }
            try {
                String version = contestant.executeGetCurrentAppletVersion(typeToTest);
                if (!stopped && !"".equals(version)) {
                    version = contestant.executeGetCurrentAppletVersion(typeToTest);
                    if (!"".equals(version)) {
                        successful = true;
                    }
                }
            } catch (Exception e) {
                successful = false;
            }
        }

        protected boolean isSuccessful() {
            return successful;
        }
    }
}
