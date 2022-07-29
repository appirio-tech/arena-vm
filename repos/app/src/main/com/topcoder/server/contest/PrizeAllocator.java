package com.topcoder.server.contest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Category;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.common.Coder;
import com.topcoder.server.common.ContestRoom;
import com.topcoder.shared.util.Formatters;

public class PrizeAllocator {

    private static Category trace = Category.getInstance(PrizeAllocator.class.getName());

    /**
     * Total prize money allocated per contest.
     */
    public static final double TOTAL_PRIZE = 5000.0;

    /**
     * Amount of prize money given to charity each contest.
     */
    public static final double CHARITY_PRIZE = 0.0;

    /**
     * Amount of prize money given to the highest score for an unrated user.
     */
    public static final double UNRATED_PRIZE = 0.0;

    /**
     * Percentage of remaining prize money allocated to Division One.
     */
    static final double DIVISION_ONE_ALLOCATION = 0.7;

    /**
     * Allocations of the prize money per room for each place from first on down.
     */
    static final double[] DIV_ONE_PRIZE_ALLOCATIONS = new double[]{.5,.3,.2};
    static final double[] DIV_TWO_PRIZE_ALLOCATIONS = new double[]{.6,.4};

    public static final boolean USING_INELIGIBLE_SEPARATION = false;

    public PrizeAllocator() {
    }

    /**
     * Returns a collection of Coders who are tied for the highest unrated scores
     * in the contest.  Coders must have a score > 0 to be considered.
     */
    public Collection highestUnratedCoders(Collection rooms) {
        Collection highScorers = new ArrayList();
        // Find the maxScore for unrated coders.
        double maxScore = Double.MIN_VALUE;
        for (Iterator allRooms = rooms.iterator(); allRooms.hasNext();) {
            ContestRoom room = (ContestRoom) allRooms.next();
            if (room.isUnrated() && room.isEligible()) {
                LinkedList sortedCoders;
                if (USING_INELIGIBLE_SEPARATION) {
                    sortedCoders = sortCoders(room);
                } else {
                    sortedCoders = sortAllCoders(room);
                }

                if (sortedCoders.size() > 0) {
                    Coder leader = (Coder) sortedCoders.get(0);
                    double leaderPoints = roundPoints(leader.getPoints());
                    if (leaderPoints > 0 && (maxScore == Double.MIN_VALUE || leaderPoints > maxScore)) {
                        maxScore = leaderPoints;
                    }
                }
            }
        }
        if (maxScore != Double.MIN_VALUE) {   // Get all the unrated coders with this score.
            for (Iterator allRooms = rooms.iterator(); allRooms.hasNext();) {
                ContestRoom room = (ContestRoom) allRooms.next();
                if (room.isUnrated() && room.isEligible()) {
                    LinkedList sortedCoders;
                    if (USING_INELIGIBLE_SEPARATION) {
                        sortedCoders = sortCoders(room);
                    } else {
                        sortedCoders = sortAllCoders(room);
                    }

                    for (Iterator roomCoders = sortedCoders.iterator(); roomCoders.hasNext();) {
                        Coder coder = (Coder) roomCoders.next();
                        if (roundPoints(coder.getPoints()) == maxScore) {
                            highScorers.add(coder);
                        }
                    }
                }
            }
        }
        return highScorers;
    }

    /**
     * Creates and initializes a PrizeRoom for each room passed in.
     */
    protected HashMap createPrizeRooms(Collection rooms) {
        HashMap prizeRooms = new HashMap();
        for (Iterator allRooms = rooms.iterator(); allRooms.hasNext();) {
            ContestRoom room = (ContestRoom) allRooms.next();
            prizeRooms.put(new Integer(room.getRoomID()), new PrizeRoom(room));
        }
        return prizeRooms;
    }

    /**
     * Sorts the rooms by division and then by room name.
     */
    protected Collection sortRooms(Collection rooms) {
        ArrayList roomList = new ArrayList(rooms);
        Comparator roomComparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                int comparison = 0;
                if (o1 instanceof PrizeRoom && o2 instanceof PrizeRoom) {
                    PrizeRoom room1 = (PrizeRoom) o1;
                    PrizeRoom room2 = (PrizeRoom) o2;
                    if (room1.getDivision() != room2.getDivision()) {
                        if (room1.getDivision() == ContestConstants.DIVISION_ONE)
                            return -1;
                        else
                            return 1;
                    }
                    if (room1.getRoomNumber() != -1 && room2.getRoomNumber() != -1) {
                        return room1.getRoomNumber() - room2.getRoomNumber();
                    }
                    return room1.getName().compareTo(room2.getName());
                }
                return comparison;
            }
        };

        Collections.sort(roomList, roomComparator);
        return roomList;
    }

    /**
     * Allocates the given prize money for the division for each room in the division.
     */
    protected void allocateDivisionPrizes(HashMap prizeRooms, ArrayList divisionRooms, double divisionMoney) {
        // Calculate the total ratings points for eligible rooms in the division
        //double totalRating = 0;
        //for (int i = 0; i < divisionRooms.size(); i++) {
            //ContestRoom room = (ContestRoom) divisionRooms.get(i);
            //PrizeRoom prizeRoom = (PrizeRoom) prizeRooms.get(new Integer(room.getRoomID()));
            //totalRating += prizeRoom.getTotalRatingPoints();
        //}
        
        double prizePerRoom = divisionMoney / (divisionRooms.size() * 1.0);

        if (divisionMoney == 0.0) return;

        trace.debug("allocateDivisionPrizes. NumRooms = " + divisionRooms.size() + " Prize = " + divisionMoney);
        for (int i = 0; i < divisionRooms.size(); i++) {   // For each room allocate its share of the prize money based on its share of the rating points
            ContestRoom room = (ContestRoom) divisionRooms.get(i);
            PrizeRoom prizeRoom = (PrizeRoom) prizeRooms.get(new Integer(room.getRoomID()));
            double roomTotalPrize = prizePerRoom; //divisionMoney * (prizeRoom.getTotalRatingPoints() / totalRating);
            allocateRoomPrizes(room, prizeRoom, roomTotalPrize, room.getDivisionID());
        }
    }

    /**
     * Returns a sorted list of all the eligible coders in the room by points and filtered
     * for only coders with a positive point value.
     */
    private LinkedList sortCoders(ContestRoom room) {
        LinkedList coders = new LinkedList();
        Iterator allCoders = room.getAllCoders();
        while (allCoders.hasNext()) {
            Coder coder = (Coder) allCoders.next();
            if (coder.isEligible() && roundPoints(coder.getPoints()) > 0) coders.add(coder);
        }
        Comparator coderComparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                int comparison = 0;
                if (o1 instanceof Coder && o2 instanceof Coder) {
                    Coder c1 = (Coder) o1;
                    Coder c2 = (Coder) o2;
                    if (roundPoints(c2.getPoints()) == roundPoints(c1.getPoints())) return 0;
                    if (roundPoints(c2.getPoints()) > roundPoints(c1.getPoints())) return 1;
                    return -1;
                }
                return comparison;
            }
        };
        Collections.sort(coders, coderComparator);
        return coders;
    }

    /**
     * Returns a sorted list of all coders (eligible and ineligible in the room by points and filtered
     * for only coders with a positive point value.
     */
    private LinkedList sortAllCoders(ContestRoom room) {
        LinkedList coders = new LinkedList();
        Iterator allCoders = room.getAllCoders();
        while (allCoders.hasNext()) {
            Coder coder = (Coder) allCoders.next();
            if (roundPoints(coder.getPoints()) > 0) coders.add(coder);
        }
        Comparator coderComparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                int comparison = 0;
                if (o1 instanceof Coder && o2 instanceof Coder) {
                    Coder c1 = (Coder) o1;
                    Coder c2 = (Coder) o2;
                    if (roundPoints(c2.getPoints()) == roundPoints(c1.getPoints())) return 0;
                    if (roundPoints(c2.getPoints()) > roundPoints(c1.getPoints())) return 1;
                    return -1;
                }
                return comparison;
            }
        };
        Collections.sort(coders, coderComparator);
        return coders;
    }

    /**
     * Rounds the score to two decimal places using the Formatters util class.
     */
    private double roundPoints(double points) {
        return Formatters.getDouble(points).doubleValue();
    }

    /**
     * Allocates prizes for one room to the coders that placed.
     */
    private void allocateRoomPrizes(ContestRoom room, PrizeRoom prizeRoom, double totalPrize, int divisionId) {
        trace.debug("Allocating room prizes for room: " + prizeRoom.getName() + " prize = " + totalPrize);

        LinkedList coders;
        if (USING_INELIGIBLE_SEPARATION) {
            coders = sortCoders(room);
        } else {
            coders = sortAllCoders(room);
        }

        int prizeIndex = 0;
        
        double[] prizeAllocations;
        if(divisionId == 1)
            prizeAllocations = DIV_ONE_PRIZE_ALLOCATIONS;
        else
            prizeAllocations = DIV_TWO_PRIZE_ALLOCATIONS;
        
        while (prizeIndex < prizeAllocations.length && coders.size() > 0) {
            double placePoints = roundPoints(((Coder) coders.getFirst()).getPoints());
            trace.debug("Looking for all coders with: " + placePoints + " at place: " + prizeIndex);
            LinkedList winners = new LinkedList();
            while (coders.size() > 0 && roundPoints(((Coder) coders.getFirst()).getPoints()) == placePoints) {
                winners.add(coders.removeFirst());
            }

            double prizeAllocation = 0.0;
            for (int i = prizeIndex; (i < prizeIndex + winners.size()) && (i < prizeAllocations.length); i++) {
                prizeAllocation += prizeAllocations[i];
            }
            double prizeForTiedWinners = totalPrize * prizeAllocation;
            int prizePerWinner = (int) Math.round(prizeForTiedWinners / winners.size());
            trace.debug("Place = " + prizeIndex + " Winners = " + winners.size() + " Prize = " + prizePerWinner);
            for (Iterator allWinners = winners.iterator(); allWinners.hasNext();) {
                Coder winner = (Coder) allWinners.next();
                prizeRoom.addPrizeWinner(new PrizeWinner(winner.getID(), winner.getName(), prizeIndex + 1, prizePerWinner, winner.isEligible()));
            }
            prizeIndex += winners.size();
        }
    }

    /**
     * Returns the number of eligible Coders assigned to the room that actually attended.
     */
    protected int getNumEligibleAttendedCoders(ContestRoom room) {
        int count = 0;
        for (int i = 0; i < room.getNumCoders(); i++) {
            Coder coder = room.getNthCoder(i);
            if (coder.isEligible() && coder.getAttended()) count++;
        }
        return count;
    }

    /**
     * Returns the number of total Coders (eligible or ineligible) assigned to the room that actually attended.
     */
    protected int getNumAttendedCoders(ContestRoom room) {
        int count = 0;
        for (int i = 0; i < room.getNumCoders(); i++) {
            Coder coder = room.getNthCoder(i);
            if (coder.getAttended()) count++;
        }
        return count;
    }

    /**
     * Returns a collection of PrizeRooms corresponding to the rooms passed in.
     */
    public Collection allocatePrizes(Collection rooms) {
        HashMap prizeRooms = createPrizeRooms(rooms);

        // Count coders per division and divide up rooms
        int divisionOneCoders = 0;
        int divisionTwoCoders = 0;
        ArrayList divisionOneEligibleRooms = new ArrayList();
        ArrayList divisionTwoEligibleRooms = new ArrayList();
        boolean unratedCodersPlaying = false;
        for (Iterator allRooms = rooms.iterator(); allRooms.hasNext();) {
            ContestRoom room = (ContestRoom) allRooms.next();
            room.updateLeader(); // Just in case
            trace.debug("Dividing room: " + room.getName() + " E = " + room.isEligible() + " U = " + room.isUnrated() + " D = " + room.getDivisionID());
            if (room.isEligible() && !room.isUnrated()) {
                if (room.getDivisionID() == ContestConstants.DIVISION_ONE) {
                    if (USING_INELIGIBLE_SEPARATION) {
                        divisionOneCoders += getNumEligibleAttendedCoders(room);
                    } else {
                        divisionOneCoders += getNumAttendedCoders(room);
                    }
                    divisionOneEligibleRooms.add(room);
                } else if (room.getDivisionID() == ContestConstants.DIVISION_TWO) {
                    if (USING_INELIGIBLE_SEPARATION) {
                        divisionTwoCoders += getNumEligibleAttendedCoders(room);
                    } else {
                        divisionTwoCoders += getNumAttendedCoders(room);
                    }
                    divisionTwoEligibleRooms.add(room);
                }
            }
            if (room.isEligible() && room.isUnrated()) {
                if (USING_INELIGIBLE_SEPARATION) {
                    if (getNumEligibleAttendedCoders(room) > 0) unratedCodersPlaying = true;
                } else {
                    if (getNumAttendedCoders(room) > 0) unratedCodersPlaying = true;
                }
            }
        }
        trace.debug("D1 Rooms = " + divisionOneEligibleRooms.size() + " D2 Rooms = " + divisionTwoEligibleRooms.size());
        double prizeMoneyLeft = TOTAL_PRIZE - CHARITY_PRIZE;
        if (unratedCodersPlaying) prizeMoneyLeft -= UNRATED_PRIZE;

        //Collection unratedWinners = highestUnratedCoders(rooms);
        //if (!unratedWinners.isEmpty()) {   // split the prize amongst all unratedWinners
          //  int prizePerWinner = (int) Math.round(UNRATED_PRIZE / unratedWinners.size());

            // Give each winner the prize money.
//            for (Iterator allWinners = unratedWinners.iterator(); allWinners.hasNext();) {
  //              Coder winner = (Coder) allWinners.next();
    //            PrizeRoom winnerRoom = (PrizeRoom) prizeRooms.get(new Integer(winner.getRoomID()));
      //          winnerRoom.addPrizeWinner(new PrizeWinner(winner.getID(), winner.getName(), 1, prizePerWinner, winner.isEligible()));
        //    }
        //}

        int totalCoders = divisionOneCoders + divisionTwoCoders;
        if (totalCoders == 0) {   // Unlikely to have no eligible winners in div 1 or 2, but just in case.
            return sortRooms(prizeRooms.values());
        }

        // Calculate the percentage of prize money going to division one based on participation.
        //double divisionOneWeight = (divisionOneCoders * DIVISION_ONE_ALLOCATION) /
          //      (DIVISION_ONE_ALLOCATION * divisionOneCoders + (1 - DIVISION_ONE_ALLOCATION) * divisionTwoCoders);
        double divisionOneWeight = DIVISION_ONE_ALLOCATION;

        double divisionOnePrizeMoney = prizeMoneyLeft * divisionOneWeight;
        double divisionTwoPrizeMoney = prizeMoneyLeft - divisionOnePrizeMoney;

        allocateDivisionPrizes(prizeRooms, divisionOneEligibleRooms, divisionOnePrizeMoney);
        allocateDivisionPrizes(prizeRooms, divisionTwoEligibleRooms, divisionTwoPrizeMoney);

        Collection result = sortRooms(prizeRooms.values());
        trace.debug("Outputting all rooms");
        for (Iterator allRooms = result.iterator(); allRooms.hasNext();) {
            trace.debug(allRooms.next());
        }
        return result;
    }
}
