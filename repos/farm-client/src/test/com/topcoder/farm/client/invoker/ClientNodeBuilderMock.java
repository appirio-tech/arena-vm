/*
 * ClientNodeBuilderMock
 * 
 * Created 08/15/2006
 */
package com.topcoder.farm.client.invoker;

import com.topcoder.farm.client.api.ClientNodeMock;
import com.topcoder.farm.client.node.ClientNode;
import com.topcoder.farm.client.node.ClientNodeBuilder;

/**
 * Mock class used for testing. Creates MockClientNodes and keeps count of
 * number of ClientNodes created 
 * 
 * Impl: The counter is kept as a class variable, therefore when using multiple instances
 * of this class, the counter will be the same for all of them
 * 
 * @author Diego Belfer (Mural)
 * @version $Id$
 */
public class ClientNodeBuilderMock implements ClientNodeBuilder {
    private static int counter;

    public ClientNodeBuilderMock() {
    }

    public ClientNode buildClient(String id) {
        synchronized (ClientNodeBuilderMock.class) {
            counter++;
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        return new ClientNodeMock(id);
    }
    
    /**
     * Returns the number of clients that have been created since last reset
     */
    public synchronized static int getCounter() {
        return counter;
    }

    /**
     * Resets the counter to 0
     */
    public synchronized static void reset() {
        counter = 0;
    }
}