package com.topcoder.server.broadcaster;

import java.net.InetAddress;
import java.net.UnknownHostException;

import junit.framework.TestCase;

public class BroadcasterPointTest extends TestCase {

    public BroadcasterPointTest(String name) {
        super(name);
    }

    public void testStartAndStop_MITLocal() {
        MITLocalServer localServer = new MITLocalServer();
        MITLocalClient localClient = new MITLocalClient();
        localServer.start();
        localClient.start();
        localClient.stop();
        localServer.stop();
    }

    public void testSendOneMessage_MITLocal() {
        MITLocalServer localServer = new MITLocalServer();
        MITLocalClient localClient = new MITLocalClient();
        localServer.start();
        localClient.start();
        String msg = "hello";
        localClient.send(msg);
        try {
            Object msg2 = localServer.receive();
            assertEquals(msg, msg2);
        } catch (InterruptedException e) {
            fail();
        }
        localClient.stop();
        localServer.stop();
    }

    public void testStartAndStop_ExodusLocal() {
        ExodusLocalServer localServer = new ExodusLocalServer();
        ExodusLocalClient localClient = new ExodusLocalClient();
        localServer.start();
        localClient.start();
        localClient.stop();
        localServer.stop();
    }

    public void testSendOneMessage_ExodusLocal() {
        ExodusLocalServer localServer = new ExodusLocalServer();
        ExodusLocalClient localClient = new ExodusLocalClient();
        localServer.start();
        localClient.start();
        String msg = "hello";
        localServer.send(msg);
        try {
            Object msg2 = localClient.receive();
            assertEquals(msg, msg2);
        } catch (InterruptedException e) {
            fail();
        }
        localClient.stop();
        localServer.stop();
    }

    public void testSendOneMessage() {
        int remotePort = 8201;
        Broadcaster mitBroadcaster = new MITBroadcaster(remotePort);
        mitBroadcaster.start();
        String host = null;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            fail();
        }
        Broadcaster exodusBroadcaster = new ExodusBroadcaster(host, remotePort);
        exodusBroadcaster.start();
        LocalClient mitLocalClient = new MITLocalClient();
        mitLocalClient.start();
        LocalClient exodusLocalClient = new ExodusLocalClient();
        exodusLocalClient.start();
        String msg = "hello";
        mitLocalClient.send(msg);
        try {
            Object msg2 = exodusLocalClient.receive();
            assertEquals(msg, msg2);
        } catch (InterruptedException e) {
            fail();
        }
        exodusLocalClient.stop();
        mitLocalClient.stop();
        exodusBroadcaster.stop();
        mitBroadcaster.stop();
    }

}
