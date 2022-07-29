/*
 * RatingProcess.java
 *
 * Created on January 5, 2007, 7:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.shared.ratings.process;

import com.topcoder.shared.ratings.algorithm.Algorithm;
import com.topcoder.shared.ratings.loader.DataLoader;
import com.topcoder.shared.ratings.persistance.DataPersistor;

/**
 * Base class for a process helper that runs all the needed
 * steps in a rating run
 * @author rfairfax
 */
public abstract class RatingProcess {
    
    /**
     * The algorithm for this process
     */
    protected Algorithm algo;
    /**
     * The data loader for this process
     */
    protected DataLoader loader;
    
    /**
     * persistor for this process
     */
    protected DataPersistor persistor;
    
    /**
     * Runs the rating process
     */
    public abstract void runProcess();
    
}
