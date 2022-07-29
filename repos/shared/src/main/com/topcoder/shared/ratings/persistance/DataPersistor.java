/*
 * DataPersistor.java
 *
 * Created on January 5, 2007, 8:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.shared.ratings.persistance;

import com.topcoder.shared.ratings.model.RatingData;

/**
 * Interface for saving ratings data to the DB
 * @author rfairfax
 */
public interface DataPersistor {
    /**
     * saves data to the db
     * @param data new rating data
     */
    void persistData(RatingData[] data);
}
