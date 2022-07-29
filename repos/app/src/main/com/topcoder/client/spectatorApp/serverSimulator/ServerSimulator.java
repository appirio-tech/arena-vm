/**
 * ServerSimulator.java
 *
 * Description:		Simulates the server (or atleast attempts to)
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.serverSimulator;

import com.topcoder.server.listener.ListenerMain;

class ServerSimulator {

    public static void main(String[] a) {

        ListenerMain.main(new String[]{a[0], "com.topcoder.client.spectatorApp.serverSimulator.SimulatorProcessor", "0", "SimpleListenerFactory"});
//		ListenerMain.main(new String[] { a[0], "SimulatorProcessor", "0", "SimpleListenerFactory"});
    }

}


/* @(#)ServerSimulator.java */
