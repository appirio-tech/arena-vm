/*
 * SimpleCSHandlerFactory
 * 
 * Created May 28, 2008
 */
package com.topcoder.shared.netCommon;


/**
 * @autor Diego Belfer (Mural)
 * @version $Id$
 */
public class SimpleCSHandlerFactory implements CSHandlerFactory {
    
    public CSHandler newInstance() {
        return new SimpleCSHandler();
    }
}
