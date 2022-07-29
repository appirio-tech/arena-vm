package com.topcoder.server.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.sql.Timestamp;

import org.apache.log4j.Category;

//import com.topcoder.server.common.ServerContestConstants;
import com.topcoder.server.ejb.DBServices.*;
import com.topcoder.shared.util.StoppableThread;

final class ConnectionArchiver implements StoppableThread.Client {

    private static final long DELAY = 4000;
    private static final Category cat = Category.getInstance(ConnectionArchiver.class);

    private final DBServices db;
    private final SynchronizedLinkedList addList = new SynchronizedLinkedList();
    private final SynchronizedLinkedList removeList = new SynchronizedLinkedList();
    private final StoppableThread thread = new StoppableThread(this, "ConnectionArchiver");
    private final String serverType = "S";

    private int serverID;

    ConnectionArchiver(DBServices db) {
        this.db = db;
        serverID = CoreServices.getServerID();
/*        try {									//server id is now in CoreServices
            serverID=db.getNextServerID();
        } catch (RemoteException e) {
            e.printStackTrace();
            serverID=-2;
        }
        info("serverID="+serverID);
*/
    }

    void start() {
        info("starting");
        thread.start();
    }

    public void cycle() throws InterruptedException {
        Thread.sleep(DELAY);
        processLists();
    }

    void stop() {
        try {
            thread.stopThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        processLists();
        info("stopped");
    }

    private void processLists() {
        try {
            Collection coll = process(addList);
            if (coll != null) {
                info("adding " + coll.size() + " connections");
                addConnections(coll);
            }
            coll = process(removeList);
            if (coll != null) {
                info("removing " + coll.size() + " connections");
                removeConnections(coll);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (DBServicesException e) {
            e.printStackTrace();
        }
    }

    private void addConnections(Collection coll) throws DBServicesException, RemoteException {
        for (Iterator it = coll.iterator(); it.hasNext();) {
            AddConnectionRequest r = (AddConnectionRequest) it.next();
            db.addConnection(r.ip, r.serverType, r.serverID, r.connID, r.coderID, r.userName, r.timestamp);
        }
    }

    private void removeConnections(Collection coll) throws DBServicesException, RemoteException {
        for (Iterator it = coll.iterator(); it.hasNext();) {
            RemoveConnectionRequest r = (RemoveConnectionRequest) it.next();
            db.removeConnection(r.serverType, r.serverID, r.connID, r.timestamp);
        }
    }

    private Collection process(SynchronizedLinkedList list) {
        if (list.size() <= 0) {
            return null;
        }
        Collection coll = new ArrayList(list.size());
        while (list.size() > 0) {
            coll.add(list.removeFirst());
        }
        return coll;
    }

    private Timestamp getTimestamp() {
        /*
        try {
            Timestamp timestamp=ServerContestConstants.getCurrentTimestamp();
            if (timestamp!=null) {
                error("timestamp == null");
                return timestamp;
            }
        } catch (Exception e) {
            error("",e);
        }
        */
        return new Timestamp(System.currentTimeMillis());
    }

    void add(String ip, int connID, int coderID, String userName) {
        addList.add(new AddConnectionRequest(ip, serverType, serverID, connID, coderID, userName, getTimestamp()));
    }

    void remove(int connId) {
        removeList.add(new RemoveConnectionRequest(serverType, serverID, connId, getTimestamp()));
    }

    private static void info(String msg) {
        cat.info(msg);
    }

}
