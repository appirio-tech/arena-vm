package com.topcoder.client.spectatorApp.scoreboard.model;

/**
 * Room.java Description: The room model
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
import java.util.List;

public class Room {
	/** Identifier of the room */
	private int roomID;

	/** Type of room */
	private int roomType;

	/** Title of the room */
	private String roomTitle;

	/** The round the room is part of */
	private int roundID;

	/** RoomData type for a lobby room */
	public final static int LOBBY = 1;

	/** RoomData type for a scoreboard room */
	public final static int SCOREBOARD = 2;

	/** RoomData type for a coding room */
	public final static int CODING = 3;

	/** List of coders assigned to the room */
	private List coders;

	/** List of problems assigned to the room */
	private List problems;

	/** The point tracking model for the room */
	private ScoreboardPointTracker pointTracker;

	/** The coder tracking model for the room */
	private ScoreboardCoderTracker coderTracker;

	/**
	 * Constructor of a Room
	 * 
	 * @param roomID the unique identifier of a room
	 * @param roomType the type of room
	 * @param roomTitle the title of the room
	 * @param roundID the round the room is part of
	 * @param coders the coders assigned to the room (should be all Coder
	 *           objects)
	 * @param problems the problems assigned to the room (should be all Problem
	 *           objects)
	 * @see com.topcoder.client.netCommon.messages.CoderData
	 * @see com.topcoder.client.netCommon.messages.ProblemData
	 * @see java.util.List
	 */
	public Room(int roomID, int roomType, String roomTitle, int roundID, List coders, List problems) throws InstantiationException {
		// Save the room information
		this.roomID = roomID;
		this.roomType = roomType;
		this.roomTitle = roomTitle;
		this.roundID = roundID;
		
		// Create the point tracker for the room
		pointTracker = new ScoreboardPointTracker(roomID, coders, problems);
		
		// Create the point tracker for the room
		coderTracker = new ScoreboardCoderTracker(roomID, coders, problems);
		
		// Get the parent round
		Round round = RoundManager.getInstance().getRound(roundID);
		if (round == null) throw new InstantiationException("Unknown roundID: " + roundID);
		
		// Get the contest
		Contest contest = round.getContest();
		if (contest == null) throw new InstantiationException("Unknown contestID: " + round.getContestID());
	}

	/**
	 * Disposes of any resources used
	 */
	public void dispose() {
		pointTracker.dispose();
		coderTracker.dispose();
	}

	/**
	 * Sets the room's winner
	 * 
	 * @param handle player whom won the room
	 */
	public void setWinner(String handle) {
		pointTracker.setWinner(handle);
	}

	/**
	 * Returns the coderTracker.
	 * 
	 * @return ScoreboardCoderTracker
	 */
	public ScoreboardCoderTracker getCoderTracker() {
		return coderTracker;
	}

	/**
	 * Returns the pointTracker.
	 * 
	 * @return ScoreboardPointTracker
	 */
	public ScoreboardPointTracker getPointTracker() {
		return pointTracker;
	}

	/**
	 * Returns the roomID.
	 * 
	 * @return int
	 */
	public int getRoomID() {
		return roomID;
	}

	/**
	 * Returns the roomTitle.
	 * 
	 * @return String
	 */
	public String getRoomTitle() {
		return roomTitle;
	}

	/**
	 * Returns the roomType.
	 * 
	 * @return int
	 */
	public int getRoomType() {
		return roomType;
	}

	/**
	 * Returns the roundID.
	 * 
	 * @return int
	 */
	public int getRoundID() {
		return roundID;
	}

	/**
	 * Returns the round associated with the room
	 * 
	 * @return Round
	 */
	public Round getRound() {
		return RoundManager.getInstance().getRound(roundID);
	}
}
