package com.topcoder.shared.util.loader.tc;

import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.loader.DataRetriever;
import com.topcoder.shared.util.loader.Launcher;
import com.topcoder.shared.util.loader.Query;
import com.topcoder.shared.util.loader.Queue;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * @author dok
 * @version $Revision$ Date: 2005/01/01 00:00:00
 *          Create Date: Dec 11, 2006
 */
public class TCLauncher implements Launcher {

    private Map connections = null;
    private List retrievers = null;
    private List configurations = null;
    private Map processingQueues = null;
    private boolean lastRetrieverFinished = false;
    private final Integer lock = new Integer(1);

    public void setConnections(Map connections) {
        this.connections = connections;
    }

    public void setRetrievers(List retrievers) {
        this.retrievers = retrievers;

    }

    public void setConfigurations(List configurations) {
        this.configurations = configurations;
    }

    public void run() throws Exception {
        log.debug("run called");
        if (connections == null) {
            throw new RuntimeException("setConnections(Map) must be called before attempting to call run()");
        }

        if (retrievers == null) {
            throw new RuntimeException("setRetrievers(List) must be called before attempting to call run()");
        }
        if (configurations == null) {
            throw new RuntimeException("setConfigurations(List) must be called before attempting to call run()");
        }
        if (retrievers.size() != configurations.size()) {
            throw new RuntimeException("We have to have one configuration for each retriever, " +
                    "there were " + retrievers.size() + " retrievers and " + configurations.size() + " configurations.");
        }

        HashMap retrieverConnections = new HashMap();
        HashMap queueConnections = new HashMap();



        //set up a maps of queues so that we can have one queue per database that we need to work with
        Map.Entry me;
        processingQueues = new HashMap();
        for (Iterator it = connections.entrySet().iterator(); it.hasNext();) {
            log.debug("setting up connections");
            me = (Map.Entry) it.next();
            processingQueues.put(me.getKey(), new Queue());
            retrieverConnections.put(me.getKey(), ((DataSource)me.getValue()).getConnection());
            queueConnections.put(me.getKey(), ((DataSource)me.getValue()).getConnection());
        }

        ProcessingThread pt = new ProcessingThread(queueConnections);
        pt.start();
        //pt.join();
/*
        StatusThread t = new StatusThread((Queue)processingQueues.get(Launcher.DW));
        log.debug("is my thread a daemon: " + t.isDaemon());
        t.start();

        t.join();
*/


        //go through and execute all the retrievers
        int i = 0;
        String className;
        for (Iterator it = retrievers.iterator(); it.hasNext(); i++) {
            className = (String) it.next();
            if (log.isDebugEnabled()) {
                log.debug("calling " + className);
            }
            callRetriever(className, (Properties) configurations.get(i), retrieverConnections);
        }

        pt.printStatus();
        synchronized (lock) {
            lastRetrieverFinished = true;
        }
        pt.printStatus();
        log.debug("last retriever is now finished");
        pt.printStatus();

    }



    /*private class StatusThread extends Thread {

        private Queue q;
        public StatusThread(Queue q) {
            this.q= q;
        }

        public void run() {
            while (true) {
                log.debug(q.size() + " items in the queue");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    
                }
            }
        }
    }
*/

    private class ProcessingThread extends Thread {
        private int queriesExecuted = 0;
        private int rowsAffected = 0;
        private boolean foundItem;

        private Map connections;
        public ProcessingThread(Map connections) {
            this.connections = connections;
        }

        public void run() {
            log.debug("thread run called");
            boolean goOn = true;
            while (goOn) {
                Queue curr;
                foundItem = false;
                Map.Entry me;
                for (Iterator it = processingQueues.entrySet().iterator(); it.hasNext();) {
                    me = (Map.Entry) it.next();
                    curr = (Queue) me.getValue();
                    foundItem |= !curr.isEmpty();
                    if (!curr.isEmpty()) {
                        try {
                            processQueue(curr, (Connection) connections.get(me.getKey()));
                        } catch (SQLException e) {
                            DBMS.printSqlException(true, e);
                            throw new RuntimeException(e);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                synchronized (lock) {
                    goOn = !lastRetrieverFinished || foundItem;
                    //log.debug("goOn = " + goOn);
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        void printStatus() {
            log.debug("lasttretrieverfinished " + lastRetrieverFinished + " foundItem: " + foundItem);
        }

        private void processQueue(Queue q, Connection conn) throws SQLException {
            log.debug("processQueue called ");

            List a;
            synchronized (q) {
                a = q.popAll();
            }
            Query curr;
            Query last = null;
            PreparedStatement ps = null;
            Object[] args;
            int[] results;
            for (Iterator it = a.iterator(); it.hasNext();) {
                curr = (Query) it.next();
                if (last == null || !curr.getQuery().equals(last.getQuery())) {
                    if (ps != null) {
                        log.debug("before execute");
                        results = ps.executeBatch();
                        log.debug("after execute");
                        queriesExecuted += results.length;
                        for (int i = 0; i < results.length; i++) {
                            rowsAffected += results[i];
                        }
                        ps.clearParameters();
                    }
                    ps = getPreparedStatement(curr.getQuery(), conn);
                    args = curr.getArgs();
                    for (int i = 0; i < args.length; i++) {
                        ps.setObject(i + 1, args[i]);
                    }
                    ps.addBatch();

                } else {
                    args = curr.getArgs();
                    for (int i = 0; i < args.length; i++) {
                        ps.setObject(i + 1, args[i]);
                    }
                    ps.addBatch();
                }
                last = curr;
            }
            if (ps!=null) {
                log.debug("before execute");
                results = ps.executeBatch();
                log.debug("after execute");
                queriesExecuted += results.length;
                for (int i = 0; i < results.length; i++) {
                    rowsAffected += results[i];
                }
                ps.clearParameters();
            }

            log.info("processed " + queriesExecuted + " queries affecting " + rowsAffected + " rows.");
        }

        HashMap preparedStatements = new HashMap();

        private PreparedStatement getPreparedStatement(String query, Connection conn) throws SQLException {
            PreparedStatement ps;
            if (preparedStatements.containsKey(query)) {
                log.debug(query + " was in the cache");
                ps = (PreparedStatement) preparedStatements.get(query);
            } else {
                log.debug(query + " was not in the cache adding");
                ps = conn.prepareStatement(query);
                preparedStatements.put(query, ps);
            }
            return ps;

        }


    }

    private void callRetriever(String clazz, Properties p, Map connections) throws Exception {
        if (clazz == null || clazz.length() == 0) {
            throw new IllegalArgumentException("No class specified");
        }

        Class loadme = Class.forName(clazz);
        DataRetriever retriever = (DataRetriever) loadme.newInstance();
        retriever.setConfiguration(p);
        String target = p.getProperty(TARGET_DB);
        retriever.setSourceDatabase((Connection) connections.get(p.getProperty(SOURCE_DB)));
        retriever.setTargetDatabase((Connection) connections.get(target));
        retriever.registerTargetProcessingQueue((Queue) processingQueues.get(target));
        retriever.run();
    }
}

