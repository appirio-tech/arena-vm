/**
 * TeamContest.java Description: Contest script for a team
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.serverSimulator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.messages.Message;
import com.topcoder.shared.netCommon.messages.spectator.CoderData;
import com.topcoder.shared.netCommon.messages.spectator.CoderRoomData;
import com.topcoder.shared.netCommon.messages.spectator.DefineContest;
import com.topcoder.shared.netCommon.messages.spectator.DefineRoom;
import com.topcoder.shared.netCommon.messages.spectator.DefineRound;
import com.topcoder.shared.netCommon.messages.spectator.DefineWeakestLinkTeam;
import com.topcoder.shared.netCommon.messages.spectator.PhaseChange;
import com.topcoder.shared.netCommon.messages.spectator.ProblemData;
import com.topcoder.shared.netCommon.messages.spectator.ProblemEvent;
import com.topcoder.shared.netCommon.messages.spectator.ProblemResult;
import com.topcoder.shared.netCommon.messages.spectator.RoomData;
import com.topcoder.shared.netCommon.messages.spectator.ShowRoom;
import com.topcoder.shared.netCommon.messages.spectator.ShowWeakestLinkTeam;
import com.topcoder.shared.netCommon.messages.spectator.TimerUpdate;
import com.topcoder.shared.netCommon.messages.spectator.WeakestLinkElimination;
import com.topcoder.shared.netCommon.messages.spectator.WeakestLinkVote;

public class TeamContest extends Contest {
	private static final Logger cat = Logger.getLogger(SimulatorProcessor.class.getName());

//	public SimulatorProcessor processor;
//
//	public int currentPhase;
//
//	public int currentTime;
//
//	public int PRE = 1;
//
//	public int REG = 5;
//
//	public int COD = 120;
//
//	public int INT = 10;
//
//	public int CHL = 60;

	public int VOTE = 60;

	public int VOTETIE = 30;

//	public Timer timer = new Timer();

	public Room[] rooms = new Room[4];

	public int CODERSIZE = 5;

	public TheTeam[] teams;

	public TeamContest(SimulatorProcessor processor) {
		this.processor = processor;
		cat.info("Contest starting...");
		cat.info("Sending contests...");
		new TheContest(1, "Tim's special Contest");
		cat.info("Sending rounds...");
		new TheRound(ContestConstants.SRM_ROUND_TYPE_ID, 1, "Tim's Round", 1);
		cat.info("Sending teams...");
		teams = new TheTeam[16];
		int[][] coders = new int[16][5];
		int idx1 = 0;
		int idx2 = 0;
		for (int room = 1; room < 5; room++) {
			for (int coder = 0; coder < 20; coder++) {
				coders[idx1][idx2] = room * 100 + coder;
				idx1++;
				if (idx1 >= 16) {
					idx1 = 0;
					idx2++;
					if (idx2 >= 5) idx2 = 0;
				}
			}
		}
		for (int teamID = 0; teamID < 16; teamID++) {
			teams[teamID] = new TheTeam(teamID + 1, coders[teamID], "Team " + (teamID + 1));
		}
		cat.info("Sending rooms...");
		Random random = new Random(100);
		rooms[0] = new Room(random, 1, RoomData.SCOREBOARD, "Room1", 1, new String[] { "Logan", "NDBronson", "Stevevai", "Ambrose", "Blah", "MicroFilm", "Hiccup", "DeeDee", "MeToo", "MicroSoft",
					"Coke Rocks", "Dexter", "OracleGuru", "M$", "Pepse Sucks", "Mendark", "SunSucks", "TC", "Buffet", "Samuria Jack" });
		rooms[1] = new Room(random, 2, RoomData.SCOREBOARD, "Room2", 1, new String[] { "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "C10", "C11", "C12", "C13", "C14", "C15", "C16", "C17",
					"C18", "C19", "C20" });
		rooms[2] = new Room(random, 3, RoomData.SCOREBOARD, "Room3", 1, new String[] { "C31", "C32", "C33", "C34", "C35", "C36", "C37", "C38", "C39", "C310", "C311", "C312", "C313", "C314", "C315",
					"C316", "C317", "C318", "C319", "C320" });
		rooms[3] = new Room(random, 4, RoomData.SCOREBOARD, "Room4", 1, new String[] { "C41", "C42", "C43", "C44", "C45", "C46", "C47", "C48", "C49", "C410", "C411", "C412", "C413", "C414", "C415",
					"C416", "C417", "C418", "C419", "C420" });
		// Showroom every 4 seconds once
		// timer.schedule(new ShowRoom(), (PRE+REG)*1000);
		timer.schedule(new ShowRoomHandler(), (PRE + REG + 2) * 1000, 10000);
		// Registration
		int T = PRE * 1000;
		timer.schedule(new Phase(ContestConstants.REGISTRATION_PHASE, REG), T); // 30
																							// seconds
																							// phase
		// Coding
		T = (PRE + REG) * 1000;
		timer.schedule(new Phase(ContestConstants.CODING_PHASE, COD), T); // 2 minute phase
		// intermission
		T = (PRE + REG + COD) * 1000;
		timer.schedule(new Phase(ContestConstants.INTERMISSION_PHASE, INT), T); // 30
																							// second
																							// intermission
		// Challenge
		T = (PRE + REG + COD + INT) * 1000;
		timer.schedule(new Phase(ContestConstants.CHALLENGE_PHASE, CHL), T); // 60 second
																						// intermission
		// Voting Phase
		T = (PRE + REG + COD + INT + CHL) * 1000;
		timer.schedule(new Phase(ContestConstants.VOTING_PHASE, VOTE), T); // 30 second
																					// intermission
		// Voting Tie Phase
		T = (PRE + REG + COD + INT + CHL + VOTE) * 1000;
		timer.schedule(new Phase(ContestConstants.TIE_BREAKING_VOTING_PHASE, VOTETIE), T); // 30
																									// second
																									// intermission
		// End contest
		T = (PRE + REG + COD + INT + CHL + VOTE + VOTETIE) * 1000;
		timer.schedule(new Phase(ContestConstants.CONTEST_COMPLETE_PHASE, 0), T); // 30 second
																					// intermission
	}

	class TheContest {
		public TheContest(int contestID, String contestName) {
			processor.sendEvent(new DefineContest(contestID, contestName));
		}
	}

	class TheRound {
		public TheRound(int roundType, int roundID, String roundName, int contestID) {
			processor.sendEvent(new DefineRound(roundID, roundType, roundName, contestID));
		}
	}

	class TheTeam {
		int teamID;

		int[] coders;

		int[] votesFor;

		public TheTeam(int teamID, int[] coders, String name) {
			this.teamID = teamID;
			this.coders = coders;
			votesFor = new int[coders.length];
			processor.sendEvent(new DefineWeakestLinkTeam(teamID, name, coders, 1));
		}

		public int getMaxVoteFor() {
			int max = -1;
			for (int x = 0; x < votesFor.length; x++) {
				max = Math.max(max, votesFor[x]);
			}
			return max;
		}

		public int getTeamID() {
			return teamID;
		}

		public int[] getCoders() {
			return coders;
		}

		public int getVotesFor(int coderID) {
			for (int x = 0; x < coders.length; x++) {
				if (coders[x] == coderID) return votesFor[x];
			}
			return -1;
		}

		public void voteFor(int victimID) {
			int x = indexOf(victimID);
			if (x < 0) return;
			votesFor[x]++;
		}

		public int indexOf(int coderID) {
			for (int x = 0; x < coders.length; x++) {
				if (coderID == coders[x]) return x;
			}
			return -1;
		}
	}

	class Room {
		private ArrayList coders = new ArrayList();

		private ArrayList problems = new ArrayList();

		private RoomData roomData;

		public RoomData getRoomData() {
			return roomData;
		}

		public int[] getCoders() {
			int[] coderID = new int[coders.size()];
			for (int x = 0; x < coders.size(); x++) {
				coderID[x] = ((CoderData) coders.get(x)).getCoderID();
			}
			return coderID;
		}

		public CoderRoomData getCoder(int x) {
			return (CoderRoomData) coders.get(x);
		}

		public int getCoderCount() {
			return coders.size();
		}

		public int getProblemID(int x) {
			return ((ProblemData) problems.get(x)).getProblemID();
		}

		public int getProblemValue(int x) {
			return ((ProblemData) problems.get(x)).getPointValue();
		}

		public Room(Random random, int roomID, int roomType, String roomTitle, int roundID, String[] coderHandles) {
			roomData = new RoomData(roomID, roomType, roomTitle, roundID);
			for (int x = 0; x < coderHandles.length; x++) {
				coders.add(new CoderRoomData(roomID * 100 + x, coderHandles[x], random.nextInt(3500), roomID * 100 + x));
			}
			problems.add(new ProblemData(roomID * 100 + 1, 250));
			problems.add(new ProblemData(roomID * 100 + 2, 500));
			problems.add(new ProblemData(roomID * 100 + 3, 1000));
			processor.sendEvent(new DefineRoom(roomData, coders, problems));
			// Create the handlers
			for (Iterator itr = coders.iterator(); itr.hasNext();) {
				CoderData coderData = (CoderData) itr.next();
				new ACoderHandler(random, this, coderData);
			}
		}
	}

	class ShowRoomHandler extends TimerTask {
		int room = 1;

		int coder = 0;

		int team = 0;

		boolean tieSent = false;

		public ShowRoomHandler() {}

		public void run() {
			if (currentPhase == ContestConstants.VOTING_PHASE || currentPhase == ContestConstants.TIE_BREAKING_VOTING_PHASE || currentPhase == ContestConstants.CONTEST_COMPLETE_PHASE) {
				if (currentPhase == ContestConstants.TIE_BREAKING_VOTING_PHASE && !tieSent) {
					tieSent = true;
					for (int x = 0; x < teams.length; x++) {
						int maxVotes = teams[x].getMaxVoteFor();
						int[] coderID = teams[x].getCoders();
						for (int y = 0; y < coderID.length; y++) {
							if (maxVotes == teams[x].getVotesFor(coderID[y])) {
								processor.sendEvent(new WeakestLinkElimination(coderID[y]));
								break;
							}
						}
					}
				}
				processor.sendEvent(new ShowWeakestLinkTeam(teams[team].getTeamID(), teams[team].getCoders()));
				team++;
				if (team >= teams.length) team = 0;
			} else {
				int[] theCoders = rooms[room - 1].getCoders();
				int[] codersToShow = new int[coder + CODERSIZE < theCoders.length ? CODERSIZE : theCoders.length - coder];
				System.arraycopy(theCoders, coder, codersToShow, 0, codersToShow.length);
				processor.sendEvent(new ShowRoom(new RoomData(room, 0, "", 1), codersToShow));
				if (coder + CODERSIZE >= theCoders.length) {
					room++;
					coder = 0;
				} else {
					coder += CODERSIZE;
				}
				if (room > rooms.length) room = 1;
			}
		}
	}

	class Phase extends TimerTask {
		int phase;

		int time;

		public Phase(int phase, int time) {
			this.phase = phase;
			this.time = time;
		}

		public void run() {
			currentPhase = phase;
			currentTime = time;
			processor.sendEvent(new PhaseChange(phase, time));
			processor.sendEvent(new TimerUpdate(time));
		}

		public Message getMsg() {
			return new PhaseChange(phase, time);
		}
	}

	class Problem extends TimerTask {
		int type, problemID, time;

		String writer, source;

		RoomData room;

		public Problem(RoomData room, int type, String writer, int problemID, String source, int time) {
			this.room = room;
			this.type = type;
			this.writer = writer;
			this.problemID = problemID;
			this.source = source;
			this.time = time;
		}

		public void run() {
			processor.sendEvent(new TimerUpdate(time));
			processor.sendEvent(new ProblemEvent(room, type, problemID, writer, source, time));
		}
	}

	class TheVote extends TimerTask {
		int coderID, victimID;

		public TheVote(int coderID, int victimID) {
			this.coderID = coderID;
			this.victimID = victimID;
		}

		public void run() {
			processor.sendEvent(new WeakestLinkVote(coderID, victimID));
		}
	}

	class TheResult extends TimerTask {
		int type, problemID, time, rc;

		String writer, source;

		RoomData room;

		double pts;

		public TheResult(RoomData room, int type, String writer, int problemID, String source, int time, int rc, double pts) {
			this.room = room;
			this.type = type;
			this.writer = writer;
			this.problemID = problemID;
			this.source = source;
			this.time = time;
			this.rc = rc;
			this.pts = pts;
		}

		public void run() {
			processor.sendEvent(new ProblemResult(room, type, problemID, writer, source, time, rc, pts));
		}

		public Message getMsg() {
			return new ProblemResult(room, type, problemID, writer, source, time, rc, pts);
		}
	}

	class ACoderHandler {
		public ACoderHandler(Random random, Room room, CoderData coderData) {
			// Coding phase
			boolean[] C = new boolean[COD];
			boolean[] SUBMITTED = new boolean[3];
			for (int x = 0; x < 3; x++) {
				int P = random.nextInt(3);
				if (SUBMITTED[P]) continue;
				int CT = random.nextInt(COD);
				int CLT = random.nextInt(COD) + 1;
				int SUB = 0;
				int stage = 0;
				if (random.nextDouble() > .5) SUB = random.nextInt(CLT);
				for (int y = CT; y < CLT && y < COD && y < CLT - SUB; y++) {
					if (C[y]) break;
					if (y >= CT + SUB && stage == 1) {
						timer.schedule(new TheResult(room.getRoomData(), ProblemEvent.SUBMITTING, coderData.getHandle(), room.getProblemID(P), coderData.getHandle(), COD - y, ProblemResult.SUCCESSFUL,
									(((int) (room.getProblemValue(P) * random.nextDouble() * 100)) / 100) + 1), (y + PRE + REG) * 1000);
						C[y] = true;
						break;
					}
					if (stage == 0) {
						if (random.nextDouble() < .2 || y == CT) {
							stage = 1;
							C[y] = true;
							timer.schedule(new Problem(room.getRoomData(), ProblemEvent.OPENED, coderData.getHandle(), room.getProblemID(P), coderData.getHandle(), COD - y), (y + PRE + REG) * 1000);
						}
					} else if (stage == 1) {
						if (random.nextDouble() < .2) {
							stage = 0;
							timer.schedule(new Problem(room.getRoomData(), ProblemEvent.CLOSED, coderData.getHandle(), room.getProblemID(P), coderData.getHandle(), COD - y), (y + PRE + REG) * 1000);
						}
					}
				}
			}
			// Challenge phase
			int y = 0;
			while (y < CHL) {
				int l = random.nextInt(CHL / 4) + 1;
				if (y + l >= CHL) break;
				int p = random.nextInt(3);
				int c = random.nextInt(room.getCoderCount());
				if (room.getCoder(c).getCoderID() == coderData.getCoderID()) continue;
				timer
							.schedule(new Problem(room.getRoomData(), ProblemEvent.OPENED, room.getCoder(c).getHandle(), room.getProblemID(p), coderData.getHandle(), CHL - y),
										(y + PRE + REG + COD + INT) * 1000);
				if (random.nextDouble() < .1) {
					timer.schedule(new TheResult(room.getRoomData(), ProblemEvent.CHALLENGING, room.getCoder(c).getHandle(), room.getProblemID(p), coderData.getHandle(), CHL - (y + l), random
								.nextBoolean() ? ProblemResult.SUCCESSFUL : ProblemResult.FAILED, 50), (y + l + PRE + REG + COD + INT) * 1000);
				}
				y += l;
			}
			// Voting phase
			while (true) {
				TheTeam myTeam = null;
				for (int x = 0; x < teams.length; x++) {
					if (teams[x].indexOf(coderData.getCoderID()) >= 0) {
						myTeam = teams[x];
						break;
					}
				}
				if (myTeam == null || myTeam.getCoders() == null) {
					cat.info("Big problems:" + coderData.getCoderID());
					break;
				}
				int c = random.nextInt(myTeam.getCoders().length);
				int[] codersss = myTeam.getCoders();
				if (codersss[c] == coderData.getCoderID()) continue;
				myTeam.voteFor(codersss[c]);
				int t = random.nextInt(VOTE / 2);
				// timer.schedule(new TheVote(coderData.getCoderID(),
				// room.getCoder(c).getCoderID()), (t+CHL+PRE+REG+COD+INT)*1000);
				timer.schedule(new TheVote(coderData.getCoderID(), codersss[c]), (t + CHL + PRE + REG + COD + INT) * 1000);
				break;
			}
		}
	}
}
/* @(#)Contest.java */
