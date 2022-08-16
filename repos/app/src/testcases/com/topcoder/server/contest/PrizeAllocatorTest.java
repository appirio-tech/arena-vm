package com.topcoder.server.contest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.log4j.Category;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.common.Coder;
import com.topcoder.server.common.ContestRoom;
import com.topcoder.server.common.IndividualCoder;
import com.topcoder.server.common.Rating;
import com.topcoder.server.common.Round;
import com.topcoder.server.common.RoundFactory;

public class PrizeAllocatorTest extends TestCase {

    private static Category trace = Category.getInstance(PrizeAllocatorTest.class.getName());

    public PrizeAllocatorTest(String name) {
        super(name);
    }

    private Coder createCoder(int id, String name, int div, Round round, int roomID, int rating,
            boolean eligible, boolean attended, int points) {
        Coder c = new IndividualCoder(id, name, div, round, roomID, rating, ContestConstants.JAVA);
        c.setEligible(eligible);
        c.setAttended(attended);
        c.setPoints(points);
        c.setOldRating(rating);
        return c;
    }

    public void testSimpleAllocation() {
        PrizeAllocator allocator = new PrizeAllocator();
        ArrayList rooms = new ArrayList();
        Collection prizeRooms = allocator.allocatePrizes(rooms);
        assertTrue(prizeRooms.size() == 0);

        int roomID = 20000;
        Round round = RoundFactory.newRound(0, 0, ContestConstants.SRM_ROUND_TYPE_ID, "TestContest", "TestRound");
        ContestRoom firstRoom = newContestRoom(roomID, "Room 1", round, ContestConstants.DIVISION_ONE, ContestConstants.CODER_ROOM);

        Coder winner = createCoder(0, "Winner", ContestConstants.DIVISION_ONE, round, roomID, 1500, true, true, 100);
        firstRoom.addCoder(winner);
        rooms.add(firstRoom);
        prizeRooms = allocator.allocatePrizes(rooms);
        assertTrue(prizeRooms.size() == 1);
        PrizeRoom prizeRoom = (PrizeRoom) prizeRooms.iterator().next();
        assertEquals("Room name", firstRoom.getName(), prizeRoom.getName());
        assertEquals("Room winners", 1, prizeRoom.getPrizeWinners().size());
        PrizeWinner prizeWinner = (PrizeWinner) prizeRoom.getPrizeWinners().iterator().next();
        double prizeMoney = ((PrizeAllocator.TOTAL_PRIZE - PrizeAllocator.CHARITY_PRIZE) * PrizeAllocator.DIV_ONE_PRIZE_ALLOCATIONS[0]);
        assertTrue("Prize Money", prizeMoney == prizeWinner.getPrize());
    }

    public void testDivisionTies() {
        trace.debug("testDivisionTies");
        PrizeAllocator allocator = new PrizeAllocator();
        ArrayList rooms = new ArrayList();
        int roomID = 21000;
        Round round = RoundFactory.newRound(0, 0, ContestConstants.SRM_ROUND_TYPE_ID, "TestContest", "TestRound");
        ContestRoom firstRoom = newContestRoom(roomID, "Room 1", round, ContestConstants.DIVISION_ONE, ContestConstants.CODER_ROOM);

        for (int i = 0; i < 3; i++) {
            Coder coder = createCoder(i, "Winner: " + i, ContestConstants.DIVISION_ONE, round, roomID, 1500 + i, true, true, 0);
            firstRoom.addCoder(coder);
        }
        rooms.add(firstRoom);

        double divisionMoney = PrizeAllocator.TOTAL_PRIZE - PrizeAllocator.CHARITY_PRIZE;
        Collection prizeRooms = allocator.allocatePrizes(rooms);
        assertTrue(prizeRooms.size() == 1);
        PrizeRoom prizeRoom = (PrizeRoom) prizeRooms.iterator().next();
        assertTrue(prizeRoom.getPrizeWinners().size() == 0);

        Coder coder1 = getNthCoder(firstRoom, 0);
        Coder coder2 = getNthCoder(firstRoom, 1);
        Coder coder3 = getNthCoder(firstRoom, 2);
        // 1 + 2 tie, 3rd
        coder1.setPoints(5165);
        coder1.setAttended(true);
        coder2.setPoints(102523);
        coder2.setAttended(true);
        coder3.setPoints(102523);
        coder3.setAttended(true);

        prizeRooms = allocator.allocatePrizes(rooms);
        assertTrue(prizeRooms.size() == 1);
        prizeRoom = (PrizeRoom) prizeRooms.iterator().next();
        assertTrue(prizeRoom.getPrizeWinners().size() == 3);
        double firstPrize = Math.round((divisionMoney * (PrizeAllocator.DIV_ONE_PRIZE_ALLOCATIONS[0] + PrizeAllocator.DIV_ONE_PRIZE_ALLOCATIONS[1])) / 2);
        for (Iterator prizeWinners = prizeRoom.getPrizeWinners().iterator(); prizeWinners.hasNext();) {
            PrizeWinner winner = (PrizeWinner) prizeWinners.next();
            if (winner.getName().equals(coder1.getName())) {
                double thirdPrize = Math.round(divisionMoney * PrizeAllocator.DIV_ONE_PRIZE_ALLOCATIONS[2]);
                assertTrue("Third place prize", winner.getPrize() == thirdPrize);
            } else if (winner.getName().equals(coder2.getName())) {
                assertTrue("First place tie", winner.getPrize() == firstPrize);
            } else if (winner.getName().equals(coder3.getName())) {
                assertTrue("First place tie", winner.getPrize() == firstPrize);
            } else
                assertTrue(false);
        }

        // 1, 2 + 3 tie
        coder1.setPoints(115165);
        coder1.setAttended(true);
        coder2.setPoints(102523);
        coder2.setAttended(true);
        coder3.setPoints(102523);
        coder3.setAttended(true);

        prizeRooms = allocator.allocatePrizes(rooms);
        assertTrue(prizeRooms.size() == 1);
        prizeRoom = (PrizeRoom) prizeRooms.iterator().next();
        assertTrue(prizeRoom.getPrizeWinners().size() == 3);
        firstPrize = Math.round((divisionMoney * (PrizeAllocator.DIV_ONE_PRIZE_ALLOCATIONS[0])));
        double secondPrize = Math.round((divisionMoney * (PrizeAllocator.DIV_ONE_PRIZE_ALLOCATIONS[1] + PrizeAllocator.DIV_ONE_PRIZE_ALLOCATIONS[2])) / 2);
        for (Iterator prizeWinners = prizeRoom.getPrizeWinners().iterator(); prizeWinners.hasNext();) {
            PrizeWinner winner = (PrizeWinner) prizeWinners.next();
            if (winner.getName().equals(coder1.getName())) {
                assertTrue("First place prize", winner.getPrize() == firstPrize);
            } else if (winner.getName().equals(coder2.getName())) {
                assertTrue("Second place tie", winner.getPrize() == secondPrize);
            } else if (winner.getName().equals(coder3.getName())) {
                assertTrue("Second place tie", winner.getPrize() == secondPrize);
            } else
                assertTrue(false);
        }

        // 1 + 2 + 3 tie
        coder1.setPoints(102523);
        coder1.setAttended(true);
        coder2.setPoints(102523);
        coder2.setAttended(true);
        coder3.setPoints(102523);
        coder3.setAttended(true);

        prizeRooms = allocator.allocatePrizes(rooms);
        assertTrue(prizeRooms.size() == 1);
        prizeRoom = (PrizeRoom) prizeRooms.iterator().next();
        assertTrue(prizeRoom.getPrizeWinners().size() == 3);
        firstPrize = Math.round(divisionMoney / 3);
        for (Iterator prizeWinners = prizeRoom.getPrizeWinners().iterator(); prizeWinners.hasNext();) {
            PrizeWinner winner = (PrizeWinner) prizeWinners.next();
            if (winner.getName().equals(coder1.getName())) {
                assertTrue("First place tie", winner.getPrize() == firstPrize);
            } else if (winner.getName().equals(coder2.getName())) {
                assertTrue("First place tie", winner.getPrize() == firstPrize);
            } else if (winner.getName().equals(coder3.getName())) {
                assertTrue("First place tie", winner.getPrize() == firstPrize);
            } else
                assertTrue(false);
        }
    }

    public void testRoomSplit() {
        PrizeAllocator allocator = new PrizeAllocator();
        ArrayList rooms = new ArrayList();
        int roomID = 21000;
        Round round = RoundFactory.newRound(0, 0, ContestConstants.SRM_ROUND_TYPE_ID, "TestContest", "TestRound");
        ContestRoom firstRoom = newContestRoom(roomID, "Room 1", round, ContestConstants.DIVISION_TWO, ContestConstants.CODER_ROOM);
        for (int i = 0; i < 10; i++) {
            Coder coder = createCoder(i, "CoderA: " + i, ContestConstants.DIVISION_TWO, round, roomID, 200, true, true, i);
            firstRoom.addCoder(coder);
        }
        firstRoom.setUnrated(false);
        rooms.add(firstRoom);

        roomID++;
        ContestRoom secondRoom = newContestRoom(roomID, "Room 2", round, ContestConstants.DIVISION_TWO, ContestConstants.CODER_ROOM);
        for (int i = 0; i < 10; i++) {
            Coder coder = createCoder(i, "CoderB: " + i, ContestConstants.DIVISION_TWO, round, roomID, 100, true, true, i);
            secondRoom.addCoder(coder);
        }
        secondRoom.setUnrated(false);
        rooms.add(secondRoom);

        Collection prizeRooms = allocator.allocatePrizes(rooms);
        assertTrue(prizeRooms.size() == 2);
        Iterator allRooms = prizeRooms.iterator();
        PrizeRoom prizeRoom = (PrizeRoom) allRooms.next();
        PrizeRoom prizeRoom2 = (PrizeRoom) allRooms.next();
        assertTrue(prizeRoom.getName().equals(firstRoom.getName()));
        assertTrue(prizeRoom2.getName().equals(secondRoom.getName()));
        assertTrue(3 == prizeRoom.getPrizeWinners().size());
        assertTrue(3 == prizeRoom2.getPrizeWinners().size());
        double divisionMoney = PrizeAllocator.TOTAL_PRIZE - PrizeAllocator.CHARITY_PRIZE;
        double firstRoomPoints = 0;
        for (int i = 0; i < firstRoom.getNumCoders(); i++) {
            Coder c = getNthCoder(firstRoom, i);
            firstRoomPoints += Math.pow(10, (double) c.getRating() / 1900);
            trace.debug("Added coder with rating: " + c.getRating() + " total = " + firstRoomPoints);
        }
        trace.debug("FirstRoom Total = " + firstRoomPoints);

        double secondRoomPoints = 0;
        for (int i = 0; i < secondRoom.getNumCoders(); i++) {
            Coder c = getNthCoder(secondRoom, i);
            secondRoomPoints += Math.pow(10, (double) c.getRating() / 1900);
            trace.debug("Added coder with rating: " + c.getRating() + " total = " + secondRoomPoints);
        }
        trace.debug("SecondRoom Total = " + secondRoomPoints);
        double totalRoomPoints = firstRoomPoints + secondRoomPoints;
        double firstRoomShare = divisionMoney * (firstRoomPoints / totalRoomPoints);

        double secondRoomShare = divisionMoney * (secondRoomPoints / totalRoomPoints);
        Iterator firstWinners = prizeRoom.getPrizeWinners().iterator();
        PrizeWinner winner1 = (PrizeWinner) firstWinners.next();
        assertEquals((int) Math.round(firstRoomShare * PrizeAllocator.DIV_ONE_PRIZE_ALLOCATIONS[0]), (int) winner1.getPrize());
        PrizeWinner winner2 = (PrizeWinner) firstWinners.next();
        assertEquals((int) Math.round(firstRoomShare * PrizeAllocator.DIV_ONE_PRIZE_ALLOCATIONS[1]), (int) winner2.getPrize());
        PrizeWinner winner3 = (PrizeWinner) firstWinners.next();
        assertEquals((int) Math.round(firstRoomShare * PrizeAllocator.DIV_ONE_PRIZE_ALLOCATIONS[2]), (int) winner3.getPrize());

        Iterator secondWinners = prizeRoom2.getPrizeWinners().iterator();
        winner1 = (PrizeWinner) secondWinners.next();
        assertEquals((int) Math.round(secondRoomShare * PrizeAllocator.DIV_ONE_PRIZE_ALLOCATIONS[0]), (int) winner1.getPrize());
        winner2 = (PrizeWinner) secondWinners.next();
        assertEquals((int) Math.round(secondRoomShare * PrizeAllocator.DIV_ONE_PRIZE_ALLOCATIONS[1]), (int) winner2.getPrize());
        winner3 = (PrizeWinner) secondWinners.next();
        assertEquals((int) Math.round(secondRoomShare * PrizeAllocator.DIV_ONE_PRIZE_ALLOCATIONS[2]), (int) winner3.getPrize());

    }

    public void testTDPrizeSplit() {
        trace.debug("testTDRoomSplit");
        PrizeAllocator allocator = new PrizeAllocator();
        ArrayList rooms = new ArrayList();
        Round round = RoundFactory.newRound(0, 0, ContestConstants.SRM_ROUND_TYPE_ID, "TestContest", "TestRound");
        ContestRoom firstRoom = newContestRoom(37, "Room 1", round, ContestConstants.DIVISION_ONE, ContestConstants.CODER_ROOM);
        rooms.add(firstRoom);
        ContestRoom secondRoom = newContestRoom(38, "Room 2", round, ContestConstants.DIVISION_ONE, ContestConstants.CODER_ROOM);
        rooms.add(secondRoom);
        ContestRoom thirdRoom = newContestRoom(39, "Room 3", round, ContestConstants.DIVISION_TWO, ContestConstants.CODER_ROOM);
        rooms.add(thirdRoom);
        HashMap contestRooms = new HashMap();
        for (int i = 0; i < rooms.size(); i++) {
            ContestRoom cr = (ContestRoom) rooms.get(i);
            contestRooms.put(new Integer(cr.getRoomID()), cr);
        }

        int[] ratings = new int[]{2687, 2613, 2497, 2449, 2263, 2144, 1787, 1625, 1521, 1466, 1223, 138};
        int[] points = new int[]{0, 0, 5000, 5000, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] roomIDs = new int[]{37, 37, 37, 37, 37, 38, 38, 38, 38, 38, 38, 39};
        boolean[] attendeds = new boolean[]{false, true, true, true, true, false, false, false, false, false, false, false};
        for (int i = 0; i < ratings.length; i++) {
            int div = ContestConstants.DIVISION_TWO;
            if (ratings[i] >= 1200) div = ContestConstants.DIVISION_ONE;
            Coder c = createCoder(i, "Coder: " + i, div, round, roomIDs[i], ratings[i], true, attendeds[i], points[i]);
            ContestRoom cr = (ContestRoom) contestRooms.get(new Integer(roomIDs[i]));
            cr.addCoder(c);
        }
        Collection prizeRooms = allocator.allocatePrizes(rooms);
        assertTrue(prizeRooms.size() == 3);
        Iterator allRooms = prizeRooms.iterator();
        PrizeRoom prizeRoom = (PrizeRoom) allRooms.next();
        assertTrue(prizeRoom.getName().equals(firstRoom.getName()));
        assertTrue(2 == prizeRoom.getPrizeWinners().size());

        double totalPrize = PrizeAllocator.TOTAL_PRIZE - PrizeAllocator.CHARITY_PRIZE;
        double sharedPrize = (PrizeAllocator.DIV_ONE_PRIZE_ALLOCATIONS[0] + PrizeAllocator.DIV_ONE_PRIZE_ALLOCATIONS[1]) * totalPrize / 2.0;

        PrizeWinner winner = (PrizeWinner) prizeRoom.getPrizeWinners().iterator().next();
        assertEquals("Tie for first", (int) sharedPrize, (int) winner.getPrize());
        winner = (PrizeWinner) prizeRoom.getPrizeWinners().iterator().next();
        assertEquals("Tie for first", (int) sharedPrize, (int) winner.getPrize());

        prizeRoom = (PrizeRoom) allRooms.next();
        assertTrue(0 == prizeRoom.getPrizeWinners().size());
        prizeRoom = (PrizeRoom) allRooms.next();
        assertTrue(0 == prizeRoom.getPrizeWinners().size());
    }

    public void testTDPrizeSplit2() {
        trace.debug("testTDRoomSplit");
        PrizeAllocator allocator = new PrizeAllocator();
        ArrayList rooms = new ArrayList();
        Round round = RoundFactory.newRound(0, 0, ContestConstants.SRM_ROUND_TYPE_ID, "TestContest", "TestRound");
        ContestRoom firstRoom = newContestRoom(37, "Room 1", round, ContestConstants.DIVISION_ONE, ContestConstants.CODER_ROOM);
        rooms.add(firstRoom);

        int[] ratings = new int[]{2244, 1575, 1500, 1207};
        int[] points = new int[]{9989, 0, 4987, -10000};
        boolean[] attendeds = new boolean[]{true, true, true, true};
        for (int i = 0; i < ratings.length; i++) {
            int div = ContestConstants.DIVISION_TWO;
            if (ratings[i] >= 1200) div = ContestConstants.DIVISION_ONE;
            Coder c = createCoder(i, "Coder: " + i, div, round, firstRoom.getRoomID(), ratings[i], true, attendeds[i], points[i]);
            firstRoom.addCoder(c);
        }
        Collection prizeRooms = allocator.allocatePrizes(rooms);
        assertTrue(prizeRooms.size() == 1);
        Iterator allRooms = prizeRooms.iterator();
        PrizeRoom prizeRoom = (PrizeRoom) allRooms.next();
        assertTrue(prizeRoom.getName().equals(firstRoom.getName()));
        assertTrue(2 == prizeRoom.getPrizeWinners().size());

        double totalPrize = PrizeAllocator.TOTAL_PRIZE - PrizeAllocator.CHARITY_PRIZE;

        Iterator winners = prizeRoom.getPrizeWinners().iterator();
        PrizeWinner winner = (PrizeWinner) winners.next();
        assertEquals("First prize", (int) (totalPrize * PrizeAllocator.DIV_ONE_PRIZE_ALLOCATIONS[0]), (int) winner.getPrize());
        winner = (PrizeWinner) winners.next();
        assertEquals("Second prize", (int) (totalPrize * PrizeAllocator.DIV_ONE_PRIZE_ALLOCATIONS[1]), (int) winner.getPrize());
    }

    public void testTDRoomSplit() {
        PrizeAllocator allocator = new PrizeAllocator();
        ArrayList rooms = new ArrayList();
        Round round = RoundFactory.newRound(0, 0, ContestConstants.SRM_ROUND_TYPE_ID, "TestContest", "TestRound");
        ContestRoom firstRoom = newContestRoom(37, "Room 1", round, ContestConstants.DIVISION_ONE, ContestConstants.CODER_ROOM);
        rooms.add(firstRoom);
        ContestRoom secondRoom = newContestRoom(38, "Room 2", round, ContestConstants.DIVISION_ONE, ContestConstants.CODER_ROOM);
        rooms.add(secondRoom);
        ContestRoom thirdRoom = newContestRoom(39, "Room 3", round, ContestConstants.DIVISION_TWO, ContestConstants.CODER_ROOM);
        rooms.add(thirdRoom);
        ContestRoom fourthRoom = newContestRoom(40, "Room 4", round, ContestConstants.DIVISION_TWO, ContestConstants.CODER_ROOM);
        fourthRoom.setUnrated(true);
        rooms.add(fourthRoom);
        HashMap contestRooms = new HashMap();
        for (int i = 0; i < rooms.size(); i++) {
            ContestRoom cr = (ContestRoom) rooms.get(i);
            contestRooms.put(new Integer(cr.getRoomID()), cr);
        }

        int[] ratings = new int[]{2687, 2613, 2497, 2449, 2263, 2144, 1787, 1625, 1521, 1466, 1223, 0, 0};
        int[] points = new int[]{0, 0, 0, 5000, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] roomIDs = new int[]{37, 37, 37, 37, 37, 38, 38, 38, 38, 38, 38, 39, 40};
        for (int i = 0; i < ratings.length; i++) {
            int div = ContestConstants.DIVISION_TWO;
            if (ratings[i] >= 1200) div = ContestConstants.DIVISION_ONE;
            Coder c = createCoder(i, "Coder: " + i, div, round, roomIDs[i], ratings[i], true, true, points[i]);
            ContestRoom cr = (ContestRoom) contestRooms.get(new Integer(roomIDs[i]));
            cr.addCoder(c);
        }
        Collection prizeRooms = allocator.allocatePrizes(rooms);
        assertTrue(prizeRooms.size() == 4);
        Iterator allRooms = prizeRooms.iterator();
        PrizeRoom prizeRoom = (PrizeRoom) allRooms.next();
        assertTrue(prizeRoom.getName().equals(firstRoom.getName()));
        assertTrue(1 == prizeRoom.getPrizeWinners().size());

        double totalPrize = PrizeAllocator.TOTAL_PRIZE - PrizeAllocator.CHARITY_PRIZE - PrizeAllocator.UNRATED_PRIZE;
        int totalDivOne = 0;
        int totalDivTwo = 0;
        HashMap roomPointTotals = new HashMap();
        double totalDivOnePoints = 0.0;
        for (int i = 0; i < rooms.size(); i++) {
            ContestRoom cr = (ContestRoom) rooms.get(i);
            if (cr.getDivisionID() == ContestConstants.DIVISION_ONE && cr.isEligible() && !cr.isUnrated()) {
                totalDivOne += cr.getNumCoders();
                double roomPoints = 0.0;
                for (int j = 0; j < cr.getNumCoders(); j++) {
                    Coder c = getNthCoder(cr, j);
                    roomPoints += Math.pow(10.0, (double) c.getRating() / 1900.0);
                }
                totalDivOnePoints += roomPoints;
                roomPointTotals.put(new Integer(cr.getRoomID()), new Double(roomPoints));
            } else if (cr.getDivisionID() == ContestConstants.DIVISION_TWO && cr.isEligible() && !cr.isUnrated()) {
                totalDivTwo += cr.getNumCoders();
            }
        }
        int totalCoders = totalDivOne + totalDivTwo;
        double divOneWeight = 0.8 * ((double) totalDivOne / totalCoders);
        double divTwoWeight = 0.2 * ((double) totalDivTwo / totalCoders);
        double totalWeight = divOneWeight + divTwoWeight;
        double divOnePrize = totalPrize * (divOneWeight / totalWeight);
        trace.debug("DivOnePrize = " + divOnePrize);
        double winnerRoomPoints = ((Double) roomPointTotals.get(new Integer(prizeRoom.getRoomID()))).doubleValue();
        double roomPrizeTotal = divOnePrize * winnerRoomPoints / totalDivOnePoints;
        PrizeWinner winner = (PrizeWinner) prizeRoom.getPrizeWinners().iterator().next();
        assertEquals("Split", (int) Math.round(roomPrizeTotal * PrizeAllocator.DIV_ONE_PRIZE_ALLOCATIONS[0]), (int) winner.getPrize());

        prizeRoom = (PrizeRoom) allRooms.next();
        assertTrue(0 == prizeRoom.getPrizeWinners().size());
        prizeRoom = (PrizeRoom) allRooms.next();
        assertTrue(0 == prizeRoom.getPrizeWinners().size());
        prizeRoom = (PrizeRoom) allRooms.next();
        assertTrue(0 == prizeRoom.getPrizeWinners().size());
    }

    public void testDivisionSplit() {
        PrizeAllocator allocator = new PrizeAllocator();
        ArrayList rooms = new ArrayList();
        int roomID = 21000;

        int divOneCoders = 0;
        int divTwoCoders = 0;
        Round round = RoundFactory.newRound(0, 0, ContestConstants.SRM_ROUND_TYPE_ID, "TestContest", "TestRound");
        ContestRoom firstRoom = newContestRoom(roomID, "Room 1", round, ContestConstants.DIVISION_ONE, ContestConstants.CODER_ROOM);
        for (int i = 0; i < 4; i++) {
            Coder coder = createCoder(i, "CoderA " + i, ContestConstants.DIVISION_ONE, round, roomID, 1600, true, true, 1 + i);
            firstRoom.addCoder(coder);
        }
        divOneCoders += firstRoom.getNumCoders();
        rooms.add(firstRoom);

        roomID++;
        ContestRoom secondRoom = newContestRoom(roomID, "Room 1", round, ContestConstants.DIVISION_TWO, ContestConstants.CODER_ROOM);
        for (int i = 0; i < 8; i++) {
            Coder coder = createCoder(10 + i, "CoderB " + i, ContestConstants.DIVISION_ONE, round, roomID, 900, true, true, 1 + i);
            secondRoom.addCoder(coder);
        }
        divTwoCoders += secondRoom.getNumCoders();
        rooms.add(secondRoom);

        roomID++;
        ContestRoom thirdRoom = newContestRoom(roomID, "Room 2", round, ContestConstants.DIVISION_TWO, ContestConstants.CODER_ROOM);
        Coder coder4 = createCoder(3, "Coder 3", ContestConstants.DIVISION_TWO, round, roomID, 0, true, true, 1);
        thirdRoom.setUnrated(true);
        thirdRoom.addCoder(coder4);
        rooms.add(thirdRoom);

        Collection prizeRooms = allocator.allocatePrizes(rooms);
        double totalPrize = PrizeAllocator.TOTAL_PRIZE - PrizeAllocator.CHARITY_PRIZE - PrizeAllocator.UNRATED_PRIZE;


        int totalDivCoders = divOneCoders + divTwoCoders;
        double percentageFromDivOne = (double) divOneCoders / totalDivCoders;
        double weightedDivOne = percentageFromDivOne * PrizeAllocator.DIVISION_ONE_ALLOCATION;
        double percentageFromDivTwo = (double) divTwoCoders / totalDivCoders;
        double weightedDivTwo = percentageFromDivTwo * (1.0 - PrizeAllocator.DIVISION_ONE_ALLOCATION);
        double totalWeights = weightedDivOne + weightedDivTwo;

        double expectedDivOne = totalPrize * (weightedDivOne / totalWeights);
        double expectedDivTwo = totalPrize - expectedDivOne;

        double divOnePrizes = 0.0;
        double divTwoPrizes = 0.0;
        double unratedPrizes = 0.0;
        for (Iterator allPrizeRooms = prizeRooms.iterator(); allPrizeRooms.hasNext();) {
            PrizeRoom prizeRoom = (PrizeRoom) allPrizeRooms.next();
            for (Iterator prizeWinners = prizeRoom.getPrizeWinners().iterator(); prizeWinners.hasNext();) {
                PrizeWinner winner = (PrizeWinner) prizeWinners.next();
                if (prizeRoom.isUnrated()) {
                    unratedPrizes += winner.getPrize();
                } else if (prizeRoom.getDivision() == ContestConstants.DIVISION_ONE) {
                    divOnePrizes += winner.getPrize();
                } else if (prizeRoom.getDivision() == ContestConstants.DIVISION_TWO) {
                    divTwoPrizes += winner.getPrize();
                } else
                    assertTrue(false);
            }
        }
        assertEquals("Unrated prize", (int) PrizeAllocator.UNRATED_PRIZE, (int) unratedPrizes);
        // Need to check the abs difference due to rounding on prize allocation.  Max error is the number
        // of winners, which is 3 per division since only one room in each division for this test.
        assertTrue("Div one prizes", Math.abs(expectedDivOne - divOnePrizes) <= 3.0);
        assertTrue("Div two prizes", Math.abs(expectedDivTwo - divTwoPrizes) <= 3.0);
    }

    public void testUnratedAllocation() {
        PrizeAllocator allocator = new PrizeAllocator();
        ArrayList rooms = new ArrayList();
        int roomID = 20000;
        Round round = RoundFactory.newRound(0, 0, ContestConstants.SRM_ROUND_TYPE_ID, "TestContest", "TestRound");
        ContestRoom firstRoom = newContestRoom(roomID, "Room 1", round, ContestConstants.DIVISION_TWO, ContestConstants.CODER_ROOM);
        firstRoom.setUnrated(true);
        Coder winner = createCoder(0, "Winner", ContestConstants.DIVISION_TWO, round, roomID, 0, true, true, 10000);
        firstRoom.addCoder(winner);
        rooms.add(firstRoom);
        Collection prizeRooms = allocator.allocatePrizes(rooms);
        assertTrue(prizeRooms.size() == 1);
        PrizeRoom prizeRoom = (PrizeRoom) prizeRooms.iterator().next();
        assertEquals("Room name", firstRoom.getName(), prizeRoom.getName());
        assertEquals("Room winners", 1, prizeRoom.getPrizeWinners().size());
        PrizeWinner prizeWinner = (PrizeWinner) prizeRoom.getPrizeWinners().iterator().next();
        double prizeMoney = 100.0;
        assertTrue("Prize Money", prizeMoney == prizeWinner.getPrize());

        roomID++;
        ContestRoom secondRoom = newContestRoom(roomID, "Room 2", round, ContestConstants.DIVISION_ONE, ContestConstants.CODER_ROOM);
        Coder winner2 = createCoder(1, "Winner2", ContestConstants.DIVISION_ONE, round, roomID, 1600, true, true, 50000);
        for (int i = 2; i < 10; i++) {
            Coder looser = createCoder(i, "Loser: " + i, ContestConstants.DIVISION_ONE, round, roomID, 1500 + i, true, true, 0);
            secondRoom.addCoder(looser);
        }
        secondRoom.addCoder(winner2);
        rooms.add(secondRoom);

        prizeRooms = allocator.allocatePrizes(rooms);
        assertTrue(prizeRooms.size() == 2);
        Iterator allPrizeRooms = prizeRooms.iterator();
        prizeRoom = (PrizeRoom) allPrizeRooms.next();
        PrizeRoom prizeRoom2 = (PrizeRoom) allPrizeRooms.next();
        if (prizeRoom.getName().equals(secondRoom.getName())) {
            PrizeRoom temp = prizeRoom;
            prizeRoom = prizeRoom2;
            prizeRoom2 = temp;
        }
        assertEquals("Room winners", 1, prizeRoom.getPrizeWinners().size());
        assertEquals("Room2 winners", 1, prizeRoom2.getPrizeWinners().size());
        prizeWinner = (PrizeWinner) prizeRoom.getPrizeWinners().iterator().next();
        assertTrue("Winner prize", 100.0 == prizeWinner.getPrize());
        PrizeWinner prizeWinner2 = (PrizeWinner) prizeRoom2.getPrizeWinners().iterator().next();
        prizeMoney = ((PrizeAllocator.TOTAL_PRIZE - PrizeAllocator.CHARITY_PRIZE - PrizeAllocator.UNRATED_PRIZE) * PrizeAllocator.DIV_ONE_PRIZE_ALLOCATIONS[0]);
        assertTrue("Winner2 prize", prizeMoney == prizeWinner2.getPrize());
    }

    public void testUnattendedSpots() {
        PrizeAllocator allocator = new PrizeAllocator();
        ArrayList rooms = new ArrayList();
        int roomID = 21000;

        int divOneCoders = 0;
        int divTwoCoders = 0;
        Round round = RoundFactory.newRound(0, 0, ContestConstants.SRM_ROUND_TYPE_ID, "TestContest", "TestRound");
        ContestRoom firstRoom = newContestRoom(roomID, "Room 1", round, ContestConstants.DIVISION_ONE, ContestConstants.CODER_ROOM);
        for (int i = 0; i < 4; i++) {
            Coder coder = createCoder(i, "CoderA " + i, ContestConstants.DIVISION_ONE, round, roomID, 1600, true, true, 1 + i);
            firstRoom.addCoder(coder);
        }
        divOneCoders += firstRoom.getNumCoders();
        rooms.add(firstRoom);

        roomID++;
        ContestRoom secondRoom = newContestRoom(roomID, "Room 1", round, ContestConstants.DIVISION_TWO, ContestConstants.CODER_ROOM);
        for (int i = 0; i < 8; i++) {
            Coder coder = createCoder(10 + i, "CoderB " + i, ContestConstants.DIVISION_ONE, round, roomID, 900, true, true, 1 + i);
            secondRoom.addCoder(coder);
        }
        getNthCoder(secondRoom, 0).setAttended(false);
        divTwoCoders += secondRoom.getNumCoders() - 1;
        rooms.add(secondRoom);

        roomID++;
        ContestRoom thirdRoom = newContestRoom(roomID, "Room 2", round, ContestConstants.DIVISION_TWO, ContestConstants.CODER_ROOM);
        Coder coder4 = createCoder(3, "Coder 3", ContestConstants.DIVISION_TWO, round, roomID, 0, true, true, 1);
        thirdRoom.setUnrated(true);
        thirdRoom.addCoder(coder4);
        rooms.add(thirdRoom);

        Collection prizeRooms = allocator.allocatePrizes(rooms);
        double totalPrize = PrizeAllocator.TOTAL_PRIZE - PrizeAllocator.CHARITY_PRIZE - PrizeAllocator.UNRATED_PRIZE;


        int totalDivCoders = divOneCoders + divTwoCoders;
        double percentageFromDivOne = (double) divOneCoders / totalDivCoders;
        double weightedDivOne = percentageFromDivOne * PrizeAllocator.DIVISION_ONE_ALLOCATION;
        double percentageFromDivTwo = (double) divTwoCoders / totalDivCoders;
        double weightedDivTwo = percentageFromDivTwo * (1.0 - PrizeAllocator.DIVISION_ONE_ALLOCATION);
        double totalWeights = weightedDivOne + weightedDivTwo;

        double expectedDivOne = totalPrize * (weightedDivOne / totalWeights);
        double expectedDivTwo = totalPrize - expectedDivOne;

        double divOnePrizes = 0.0;
        double divTwoPrizes = 0.0;
        double unratedPrizes = 0.0;
        for (Iterator allPrizeRooms = prizeRooms.iterator(); allPrizeRooms.hasNext();) {
            PrizeRoom prizeRoom = (PrizeRoom) allPrizeRooms.next();
            for (Iterator prizeWinners = prizeRoom.getPrizeWinners().iterator(); prizeWinners.hasNext();) {
                PrizeWinner winner = (PrizeWinner) prizeWinners.next();
                if (prizeRoom.isUnrated()) {
                    unratedPrizes += winner.getPrize();
                } else if (prizeRoom.getDivision() == ContestConstants.DIVISION_ONE) {
                    divOnePrizes += winner.getPrize();
                } else if (prizeRoom.getDivision() == ContestConstants.DIVISION_TWO) {
                    divTwoPrizes += winner.getPrize();
                } else
                    assertTrue(false);
            }
        }
        assertEquals("Unrated prize", (int) PrizeAllocator.UNRATED_PRIZE, (int) unratedPrizes);
        // Need to check the abs difference due to rounding on prize allocation.  Max error is the number
        // of winners, which is 3 per division since only one room in each division for this test.
        assertTrue("Div one prizes", Math.abs(expectedDivOne - divOnePrizes) <= 3.0);
        assertTrue("Div two prizes", Math.abs(expectedDivTwo - divTwoPrizes) <= 3.0);
    }

    public void testUnratedTies() {
        PrizeAllocator allocator = new PrizeAllocator();
        ArrayList rooms = new ArrayList();
        int roomID = 20000;
        Round round = RoundFactory.newRound(0, 0, ContestConstants.SRM_ROUND_TYPE_ID, "TestContest", "TestRound");
        ContestRoom firstRoom = newContestRoom(roomID, "Room 1", round, ContestConstants.DIVISION_TWO, ContestConstants.CODER_ROOM);
        firstRoom.setUnrated(true);
        int coderID = 0;
        Coder winner = createCoder(coderID++, "Winner", ContestConstants.DIVISION_TWO, round, roomID, 0, true, true, 10000);
        firstRoom.addCoder(winner);
        for (int i = 0; i < 5; i++) {
            Coder loser = createCoder(coderID++, "Loser: " + i, ContestConstants.DIVISION_TWO, round, roomID, 0, true, true, 5000 + i);
            firstRoom.addCoder(loser);
        }
        Coder ineligibleWinner = createCoder(coderID++, "Ineligible Winner", ContestConstants.DIVISION_TWO, round, roomID, 0, false, true, 20000);
        firstRoom.addCoder(ineligibleWinner);

        rooms.add(firstRoom);

        roomID++;
        ContestRoom secondRoom = newContestRoom(roomID, "Room 2", round, ContestConstants.DIVISION_TWO, ContestConstants.CODER_ROOM);
        secondRoom.setUnrated(true);
        Coder winner2 = createCoder(coderID++, "Winner 2", ContestConstants.DIVISION_TWO, round, roomID, 0, true, true, winner.getPoints());
        secondRoom.addCoder(winner2);
        Coder winner3 = createCoder(coderID++, "Winner 3", ContestConstants.DIVISION_TWO, round, roomID, 0, true, true, winner.getPoints());
        secondRoom.addCoder(winner3);
        for (int i = 0; i < 8; i++) {
            Coder loser = createCoder(coderID++, "Loser: " + i, ContestConstants.DIVISION_TWO, round, roomID, 0, true, true, 6000 + i);
            secondRoom.addCoder(loser);
        }
        rooms.add(secondRoom);

        Collection prizeRooms = allocator.allocatePrizes(rooms);
        assertTrue(prizeRooms.size() == 2);
        Iterator allPrizeRooms = prizeRooms.iterator();
        PrizeRoom prizeRoom = (PrizeRoom) allPrizeRooms.next();
        PrizeRoom prizeRoom2 = (PrizeRoom) allPrizeRooms.next();

        if (prizeRoom2.getName().equals(firstRoom.getName())) {
            PrizeRoom temp = prizeRoom;
            prizeRoom = prizeRoom2;
            prizeRoom2 = temp;
        }
        assertEquals("Room2 name", secondRoom.getName(), prizeRoom2.getName());
        assertEquals("Room winners", 1, prizeRoom.getPrizeWinners().size());
        assertEquals("Room2 winners", 2, prizeRoom2.getPrizeWinners().size());
        PrizeWinner prizeWinner = (PrizeWinner) prizeRoom.getPrizeWinners().iterator().next();
        Iterator room2Winners = prizeRoom2.getPrizeWinners().iterator();
        PrizeWinner prizeWinner2 = (PrizeWinner) room2Winners.next();
        PrizeWinner prizeWinner3 = (PrizeWinner) room2Winners.next();
        double prizeMoney = (int) Math.round(100.0 / 3.0);
        assertTrue("Prize Money", prizeMoney == prizeWinner.getPrize());
        assertTrue("Prize Money2", prizeMoney == prizeWinner2.getPrize());
        assertTrue("Prize Money3", prizeMoney == prizeWinner3.getPrize());
    }

    private ContestRoom newContestRoom(int roomID, String string, Round round, int division, int room) {
        return new ContestRoom(roomID, string, round, division, room, Rating.ALGO);
    }

    private static Coder getNthCoder(ContestRoom contestRoom, int index) {
        return contestRoom.getNthCoder(index);
    }

}
