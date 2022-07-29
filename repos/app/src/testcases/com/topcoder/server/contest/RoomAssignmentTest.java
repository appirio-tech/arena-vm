package com.topcoder.server.contest;

import java.util.*;

import junit.framework.TestCase;

import org.apache.log4j.Category;

import com.topcoder.server.common.User;
import com.topcoder.netCommon.contest.ContestConstants;

public final class RoomAssignmentTest extends TestCase {

    private static Category trace = Category.getInstance(RoomAssignmentTest.class);

    //int contestID = 55;
    //int roundID = 42;

    public RoomAssignmentTest(String name) {
        super(name);
    }

    public void testSimpleAssignment() {
        trace.debug("testRoomAssignment");
        IronmanRoomAssigner assigner = new IronmanRoomAssigner();

        ArrayList users = new ArrayList();
        User firstUser = new User(23, "Test User");
        //firstUser.setRating(1200);
        users.add(firstUser);
        //int baseRoomID = 500;

        Collection rooms = assigner.assignRooms(users);
        assertEquals("Number of rooms created", 1, rooms.size());
        AssignedRoom room = (AssignedRoom) rooms.iterator().next();
        assertEquals("First room division", ContestConstants.DIVISION_ONE, room.getDivisionID());
        assertEquals("Number of assigned users", 1, room.getUsers().size());

        User user = (User) room.getUsers().get(0);
        assertEquals("First user name", firstUser.getName(), user.getName());
    }

    private User createUser(String name, int id, int rating, boolean eligible, int events) {
        User u = new User(id, name);
        //u.setRating(rating);
        u.setEligible(eligible);
        //u.setNumRatedEvents(events);
        return u;
    }

    public void testRemainderUsers() {
        IronmanRoomAssigner assigner = new IronmanRoomAssigner();
        ArrayList users = new ArrayList();

        int userCount = 113;
        for (int i = 0; i < userCount; i++) {
            users.add(createUser("User : " + i, i, 1000 + i, true, 5));
        }
        Collection rooms = assigner.assignRooms(users);
        int usersAllocated = 0;
        int roomIndex = 0;
        for (Iterator allRooms = rooms.iterator(); allRooms.hasNext();) {
            AssignedRoom room = (AssignedRoom) allRooms.next();
            trace.debug("Room: " + room.getName() + " Users: " + room.getUsers().size());
            assertEquals("Div 2 room", ContestConstants.DIVISION_TWO, room.getDivisionID());
            assertTrue(room.isEligible());
            assertTrue(!room.isUnrated());
            if (roomIndex < 7) {
                assertEquals("Users in room", 9, room.getUsers().size());
            } else {
                assertEquals("Users in room", 10, room.getUsers().size());
            }
            usersAllocated += room.getUsers().size();
            roomIndex++;
        }
        assertEquals("Total users assigned", userCount, usersAllocated);
    }

    public void testAllRoomTypes() {
        IronmanRoomAssigner assigner = new IronmanRoomAssigner();
        ArrayList users = new ArrayList();

        User d1User = createUser("Div One User", 0, 1400, true, 2);
        users.add(d1User);

        User d2User = createUser("Div Two User", 1, 1199, true, 5);
        users.add(d2User);

        User d1IUser = createUser("Div One In", 2, 1550, false, 10);
        users.add(d1IUser);

        User d2IUser = createUser("Div Two In", 3, 50, false, 1);
        users.add(d2IUser);

        User unratedUser = createUser("Unrated", 4, 0, true, 0);
        users.add(unratedUser);

        User unratedIUser = createUser("UnratedI", 5, 0, false, 0);
        users.add(unratedIUser);

        Collection rooms = assigner.assignRooms(users);
        assertEquals("Num rooms created", 5, rooms.size());

        Iterator allRooms = rooms.iterator();
        AssignedRoom d1Room = (AssignedRoom) allRooms.next();
        assertEquals("Room division", ContestConstants.DIVISION_ONE, d1Room.getDivisionID());
        assertEquals("Assigned users", 1, d1Room.getUsers().size());
        assertEquals("D1 Coder", d1User.getName(), ((User) d1Room.getUsers().get(0)).getName());
        assertEquals("Room Eligible", true, d1Room.isEligible());
        assertEquals("Room unrated", false, d1Room.isUnrated());

        AssignedRoom d1IRoom = (AssignedRoom) allRooms.next();
        assertEquals("Room division", ContestConstants.DIVISION_ONE, d1IRoom.getDivisionID());
        assertEquals("Assigned users", 1, d1IRoom.getUsers().size());
        assertEquals("D1I Coder", d1IUser.getName(), ((User) d1IRoom.getUsers().get(0)).getName());
        assertEquals("Room Not Eligible", false, d1IRoom.isEligible());
        assertEquals("Room unrated", false, d1IRoom.isUnrated());

        AssignedRoom d2Room = (AssignedRoom) allRooms.next();
        assertEquals("Room division", ContestConstants.DIVISION_TWO, d2Room.getDivisionID());
        assertEquals("Assigned users", 1, d2Room.getUsers().size());
        assertEquals("D2 Coder", d2User.getName(), ((User) d2Room.getUsers().get(0)).getName());
        assertEquals("Room Eligible", true, d2Room.isEligible());
        assertEquals("Room unrated", false, d2Room.isUnrated());

        AssignedRoom d2IRoom = (AssignedRoom) allRooms.next();
        assertEquals("Room division", ContestConstants.DIVISION_TWO, d2IRoom.getDivisionID());
        assertEquals("Assigned users", 1, d2IRoom.getUsers().size());
        assertEquals("D2I Coder", d2IUser.getName(), ((User) d2IRoom.getUsers().get(0)).getName());
        assertEquals("Room Not Eligible", false, d2IRoom.isEligible());
        assertEquals("Room Unrated", false, d2IRoom.isUnrated());

        AssignedRoom unratedRoom = (AssignedRoom) allRooms.next();
        assertEquals("Room division", ContestConstants.DIVISION_TWO, unratedRoom.getDivisionID());
        assertEquals("Assigned users", 2, unratedRoom.getUsers().size());
        for (Iterator unratedUsers = unratedRoom.getUsers().iterator(); unratedUsers.hasNext();) {
            User user = (User) unratedUsers.next();
            if (user.isEligible()) {
                assertEquals("Unrated Coder", unratedUser.getName(), ((User) unratedRoom.getUsers().get(0)).getName());
            } else {
                assertEquals("UnratedI Coder", unratedIUser.getName(), ((User) unratedRoom.getUsers().get(1)).getName());
            }
        }
        assertEquals("Room Eligible", true, unratedRoom.isEligible());
        assertEquals("Room Unrated", true, unratedRoom.isUnrated());
    }


    public void testUserOrdering() {
        IronmanRoomAssigner assigner = new IronmanRoomAssigner();
        ArrayList users = new ArrayList();

        String userName = "User-";
        for (int userID = 1000; userID < 1011; userID++) {
            User user = createUser(userName + userID, userID, 1400 + userID, true, userID);
            users.add(user);
        }
        Collection rooms = assigner.assignRooms(users);
        assertEquals("Num rooms created", 2, rooms.size());

        Iterator allRooms = rooms.iterator();
        AssignedRoom firstRoom = (AssignedRoom) allRooms.next();
        trace.debug("First Room: " + firstRoom.getName());
        assertTrue(firstRoom.getDivisionID() == ContestConstants.DIVISION_ONE);
        assertTrue(!firstRoom.isUnrated());
        assertTrue(firstRoom.isEligible());

        assertEquals("First room size", 5, firstRoom.getUsers().size());
        ArrayList aUsers = firstRoom.getUsers();
        for (int i = 0; i < aUsers.size(); i++) {
            User u = (User) aUsers.get(i);
            assertEquals("User Names", userName + (1010 - i), u.getName());
        }

        AssignedRoom secondRoom = (AssignedRoom) allRooms.next();
        trace.debug("Second Room: " + secondRoom.getName());
        assertTrue(secondRoom.getDivisionID() == ContestConstants.DIVISION_ONE);
        assertTrue(!secondRoom.isUnrated());
        assertTrue(secondRoom.isEligible());

        ArrayList bUsers = secondRoom.getUsers();
        for (int i = 0; i < bUsers.size(); i++) {
            User u = (User) bUsers.get(i);
            assertEquals("User Names", userName + (1005 - i), u.getName());
        }
        assertEquals("Second room size", 6, secondRoom.getUsers().size());
    }
}
