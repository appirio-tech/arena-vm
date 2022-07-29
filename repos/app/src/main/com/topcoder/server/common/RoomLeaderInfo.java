/*
 * RoomLeaderInfo
 * 
 * Created Oct 22, 2007
 */
package com.topcoder.server.common;

import java.io.Serializable;

/**
 * @author Diego Belfer (mural)
 * @version $Id: RoomLeaderInfo.java 67272 2007-12-04 21:00:35Z thefaxman $
 */
public class RoomLeaderInfo implements Serializable {
    private Coder coder;
    private int seed;
    private double points;
    private boolean closeContest;
    
    protected RoomLeaderInfo() {
    }
    
    public RoomLeaderInfo(Coder coder, int seed, double points, boolean closeContest) {
        this.coder = coder;
        this.seed = seed;
        this.points = points;
        this.closeContest = closeContest;
    }
    
    public Coder getCoder() {
        return coder;
    }
    public int getSeed() {
        return seed;
    }
    public double getPoints() {
        return points;
    }
    public boolean isCloseContest() {
        return closeContest;
    }
}
