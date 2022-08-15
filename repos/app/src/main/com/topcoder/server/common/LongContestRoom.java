/*
 * LongContestRoom
 * 
 * Created 05/30/2007
 */
package com.topcoder.server.common;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.services.CoreServices;

/**
 * @author Diego Belfer (mural)
 * @version $Id: LongContestRoom.java 67272 2007-12-04 21:00:35Z thefaxman $
 */
public class LongContestRoom extends BaseCodingRoom {
    private final PointResolver RESOLVER = new PointResolver() {
        public int getPoints(Coder coder) {
            Round round = CoreServices.getContestRound(getRoundID());
            if (round.getPhase() >= ContestConstants.CONTEST_COMPLETE_PHASE) {
                return coder.getFinalPoints();
            } else  {
                return coder.getPoints();
            }
        }
    };
    
    public LongContestRoom(int id, String name, Round contest, int divisionId, int type, int ratingType) {
        super(id, name, contest, divisionId, type, ratingType);
    }

    protected PointResolver getPointResolver() {
        return RESOLVER;
    }
}
