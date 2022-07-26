/*
 * MarathonRatingProcess.java
 *
 * Created on January 5, 2007, 8:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.shared.ratings.process;

import com.topcoder.shared.ratings.algorithm.AlgorithmQubits;
import com.topcoder.shared.ratings.loader.MarathonDataLoader;
import com.topcoder.shared.ratings.model.RatingData;
import com.topcoder.shared.ratings.persistance.MarathonDataPersistor;
import com.topcoder.shared.util.logging.Logger;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Process helper object for rating a marathon round
 * @author rfairfax
 */
public class MarathonRatingProcess extends RatingProcess {
    
    private int roundId;
    private Connection conn;
    
    private Logger log = Logger.getLogger(MarathonRatingProcess.class);
    
    /**
     * Creates a new instance of MarathonRatingProcess
     * @param roundId the round to rate
     * @param conn the db connection to use
     */
    public MarathonRatingProcess(int roundId, Connection conn) {
        this.roundId = roundId;
        this.conn = conn;
        this.algo = new AlgorithmQubits();
        this.loader = new MarathonDataLoader(roundId, conn);
        this.persistor = new MarathonDataPersistor(roundId, conn);
    }

    /**
     * Loads all data, rates the round, then saves data to the DB
     */
    public void runProcess() {
        log.info("Starting run for round " + roundId);
        
        log.debug("Getting Data");
        RatingData[] data = loader.loadData();
        log.debug("Data loaded (" + data.length + ")");
        
        //split the data into provisional and non-provisional groups
        ArrayList provDataList = new ArrayList();
        //the provisional group is everyone at first
        provDataList.addAll(Arrays.asList(data));
        
        RatingData[] provData = (RatingData[])provDataList.toArray(new RatingData[0]);
        
        //non prov data removes non-rated people
        ArrayList nonprovDataList = new ArrayList();
        for(int i = 0; i < data.length; i++) {
            if(data[i].getNumRatings() != 0)
                nonprovDataList.add(data[i]);
        }
        
        RatingData[] nonprovData = (RatingData[])nonprovDataList.toArray(new RatingData[0]);
        
        //run the algorithm (provisional)        
        algo.setRatingData(provData);
        algo.runRatings();
        provData = algo.getRatingData();

        log.debug("Algorithm Run (provisional)");
        
        //remove non-prov coders
        provDataList = new ArrayList();
        for(int i = 0; i < provData.length; i++) {
            if(provData[i].getNumRatings() == 1)
                provDataList.add(provData[i]);
        }
        
        RatingData[] provDataFiltered = (RatingData[])provDataList.toArray(new RatingData[0]);;
        
        //persist
        persistor.persistData(provDataFiltered);
        log.debug("Data Saved (provisional)");
        
        //run the algorithm (provisional)        
        algo.setRatingData(nonprovData);
        algo.runRatings();
        nonprovData = algo.getRatingData();

        log.debug("Algorithm Run (non-provisional)");
        
        //persist
        persistor.persistData(nonprovData);
        log.debug("Data Saved (non-provisional)");
        
        
    }
    
}
