package com.topcoder.server.contest;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.common.Coder;
import com.topcoder.server.common.TeamCoder;
import com.topcoder.server.common.TeamContestRoom;
import com.topcoder.shared.util.Formatters;
import com.topcoder.shared.util.logging.Logger;

public class TeamPrizeAllocator extends PrizeAllocator {

    private static Logger log = Logger.getLogger(TeamPrizeAllocator.class);

    /**
     * Total prize money allocated per contest.
     */
    static final double TOTAL_PRIZE[] = new double[]{
        4800.0, // div1
        200.0  // div2
    };

    int DIV_ONE_WINNERS = 12;
    int DIV_TWO_WINNERS = 1;

    public TeamPrizeAllocator() {
    }

    /**
     * Rounds the score to two decimal places using the Formatters util class.
     */
    private double roundPoints(double points) {
        return Formatters.getDouble(points).doubleValue();
    }


    /**
     * Returns a collection of PrizeRooms corresponding to the rooms passed in.
     */
    public Collection allocatePrizes(Collection rooms) {
        HashMap prizeRooms = createPrizeRooms(rooms);
        ArrayList divOneTeams = new ArrayList();
        ArrayList divTwoTeams = new ArrayList();
        for (Iterator allRooms = rooms.iterator(); allRooms.hasNext();) {
            TeamContestRoom room = (TeamContestRoom) allRooms.next();
            for (Iterator it = room.getAllCoders(); it.hasNext();) {
                Coder coder = (Coder) it.next();
                if (coder.isEligible()) {
                    if (room.getDivisionID() == ContestConstants.DIVISION_ONE)
                        divOneTeams.add(coder);
                    else
                        divTwoTeams.add(coder);
                }
            }
        }

        Comparator sortByPoints = new Comparator() {
            public int compare(Object o1, Object o2) {
                double d = roundPoints(((Coder) o1).getPoints()) - roundPoints(((Coder) o2).getPoints());
                return d == 0 ? 0 : d < 0 ? 1 : -1;
            }
        };
        Collections.sort(divOneTeams, sortByPoints);
        Collections.sort(divTwoTeams, sortByPoints);

        allocateDivisionPrizes(divOneTeams, TOTAL_PRIZE[0], DIV_ONE_WINNERS, prizeRooms);
        allocateDivisionPrizes(divTwoTeams, TOTAL_PRIZE[1], DIV_TWO_WINNERS, prizeRooms);

        Collection result = sortRooms(prizeRooms.values());
        if (log.isInfoEnabled()) {
            log.info("Prize rooms:");
            for (Iterator allRooms = result.iterator(); allRooms.hasNext();) {
                log.info(allRooms.next());
            }
        }
        return result;
    }

    private void allocateDivisionPrizes(ArrayList teams, double totalDivisionPrize, int numDivisionWinnders, HashMap prizeRooms) {
        double prizePerTeam = totalDivisionPrize / numDivisionWinnders;
        log.info("Allocating " + Formatters.getDoubleString(prizePerTeam) + " per team");
        // TODO handle eligibility?
        for (int i = 0; i < DIV_ONE_WINNERS; i++) {
            TeamCoder teamCoder = (TeamCoder) teams.get(i);
            PrizeRoom prizeRoom = (PrizeRoom) prizeRooms.get(new Integer(teamCoder.getRoomID()));
            double prizePerTeamMember = prizePerTeam / teamCoder.getMemberCoders().size();
            for (Iterator it = teamCoder.getMemberCoders().iterator(); it.hasNext();) {
                Coder memberCoder = (Coder) it.next();
                prizeRoom.addPrizeWinner(new PrizeWinner(memberCoder.getID(), memberCoder.getName(), i + 1, prizePerTeamMember, memberCoder.isEligible()));
            }
        }
    }
}
