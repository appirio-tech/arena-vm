/*
 * DataLoader.java
 *
 * Created on January 4, 2007, 9:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.shared.ratings.loader;

import com.topcoder.shared.ratings.model.RatingData;

/**
 * Interface for class responsible for loading
 * all needed data from the DB or manual source
 *
 * @author rfairfax
 */
public interface DataLoader {
    
    /**
     * Loads any data needed from the DB
     * 
     * Any parameters needed for this operation are expected to be set by this point,
     * usually via a constructor
     * @return array of loaded data
     */
    RatingData[] loadData();
    
}
