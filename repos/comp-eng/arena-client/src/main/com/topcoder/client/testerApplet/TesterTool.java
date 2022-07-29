/*
 * TesterTool
 * 
 * Created Jun 18, 2008
 */
package com.topcoder.client.testerApplet;

import com.topcoder.client.connectiontype.ConnectionType;

/**
 * @autor Diego Belfer (Mural)
 * @version $Id: TesterTool.java 71653 2008-07-12 05:06:37Z dbelfer $
 */
public class TesterTool {
    public static void main(String[] args) {
        if (args.length != 9) {
            usage();
        }
        
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        boolean ssl = "true".equals(args[2]);
        int threadNum = Integer.parseInt(args[3]);
        int packetSize = Integer.parseInt(args[4]);
        int batches = Integer.parseInt(args[5]);
        int messages = Integer.parseInt(args[6]);
        String typesSel =args[7];
        String tunnel = args[8];
        ConnectionType[] types = null;
        if (typesSel.trim().equals("*")) {
            types = ConnectionType.getAvailableTypes();
        } else {
            String[] ids = typesSel.split(",");
            types = new ConnectionType[ids.length];
            for (int i = 0; i < ids.length; i++) {
                types[i] = ConnectionType.getById(ids[i]);
                
            }
        }
        new TestProcess(host, port, tunnel, ssl, threadNum, packetSize, batches, messages, types) {
            protected void bareAppendLog(String text) {
                log(text);
            }
        }.runTest();
    }

    private static void usage() {
        System.out.println("args: host port ssl? threadNum packetSize batches messagesPerBatch (*|csvOfConnectionTypes) tunnel");
        System.out.println("ConnectionTypes: ");
        ConnectionType[] types = ConnectionType.getTypes();
        for (int i = 0; i < types.length; i++) {
            ConnectionType t = types[i];
            System.out.println("    " + (t.isAvailable() ? "  " : "*")+ t.getId() + " - " + t.getDescription());
        }
        System.exit(-1);
    }

    protected static void log(String text) {
        System.out.println(text);
    }
}
