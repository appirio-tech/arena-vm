/**
 * Class ContestRoom
 *
 * Author: Hao Kung
 *
 * Description: This class will contain information about a room during a contest
 */
package com.topcoder.server.common;



public class ContestRoom extends BaseCodingRoom {
    private static final PointResolver RESOLVER = new PointResolver() {
        public int getPoints(Coder coder) {
            return coder.getPoints();
        }
    };
    public ContestRoom(int id, String name, Round contest, int divisionId, int type, int ratingType) {
        super(id, name, contest, divisionId, type, ratingType);
    }

    protected PointResolver getPointResolver() {
        return RESOLVER;
    }
}
