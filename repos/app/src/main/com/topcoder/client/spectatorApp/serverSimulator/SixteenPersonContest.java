/*
 * Contest.java Description: Contest script for a single room @author Tim "Pops"
 * Roberts
 * 
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.serverSimulator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Category;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.messages.Message;
import com.topcoder.shared.netCommon.messages.MessagePacket;
import com.topcoder.shared.netCommon.messages.spectator.CoderRoomData;
import com.topcoder.shared.netCommon.messages.spectator.DefineContest;
import com.topcoder.shared.netCommon.messages.spectator.DefineRoom;
import com.topcoder.shared.netCommon.messages.spectator.DefineRound;
import com.topcoder.shared.netCommon.messages.spectator.PhaseChange;
import com.topcoder.shared.netCommon.messages.spectator.ProblemData;
import com.topcoder.shared.netCommon.messages.spectator.ProblemEvent;
import com.topcoder.shared.netCommon.messages.spectator.ProblemResult;
import com.topcoder.shared.netCommon.messages.spectator.RoomData;
import com.topcoder.shared.netCommon.messages.spectator.RoomWinner;
import com.topcoder.shared.netCommon.messages.spectator.TimerUpdate;

public class SixteenPersonContest extends Contest {
	private static final Category cat = Category.getInstance(SimulatorProcessor.class.getName());

	public SimulatorProcessor processor;

	public int currentPhase;

	public int currentTime;

	public int PRE = 1;

	public int REG = 5;

	public int COD = 60*2;

	public int INT = 10;

	public int CHL = 60;

	public int SYS = 60;

	public Timer timer = new Timer();

	public Room room;

	public SixteenPersonContest() {}

	public SixteenPersonContest(SimulatorProcessor processor) {
		this.processor = processor;
		cat.info("Contest starting...");
		cat.info("Sending contests...");
		new TheContest(1, "Tim's special Contest");
		cat.info("Sending rounds...");
		new TheRound(ContestConstants.SRM_ROUND_TYPE_ID, 1, "Tim's Round", 1);
		cat.info("Sending rooms...");
		room = new Room3(1, RoomData.SCOREBOARD, "Room1", 1);
		// Registration
		int T = PRE * 1000;
		timer.schedule(new Phase(ContestConstants.REGISTRATION_PHASE, REG), T); // 30 seconds phase
		// Coding
		T = (PRE + REG) * 1000;
		timer.schedule(new Phase(ContestConstants.CODING_PHASE, COD), T); // 2 minute phase
		// intermission
		T = (PRE + REG + COD) * 1000;
		timer.schedule(new Phase(ContestConstants.INTERMISSION_PHASE, INT), T); // 30 second intermission
		// Challenge
		T = (PRE + REG + COD + INT) * 1000;
		timer.schedule(new Phase(ContestConstants.CHALLENGE_PHASE, CHL), T); // 60 second intermission
		// System testing
		T = (PRE + REG + COD + INT + CHL) * 1000;
		timer.schedule(new Phase(ContestConstants.SYSTEM_TESTING_PHASE, SYS), T); // 30 second intermission
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

	class Room3 extends Room {
		private ArrayList coders = new ArrayList();

		private ArrayList problems = new ArrayList();

		public int[] getCoders() {
			int[] rc = new int[coders.size()];
			for (int x = 0; x < coders.size(); x++)
				rc[x] = ((Coder) coders.get(x)).coderID;
			return rc;
		}

		public Room3(int roomID, int roomType, String roomTitle, int roundID) {
			RoomData roomData = new RoomData(roomID, roomType, roomTitle, roundID);
			Coder c1 = new Coder(1, "gt494", 1, 1);
			Coder c2 = new Coder(2, "dok", 2, 2);
			Coder c3 = new Coder(3, "Mike", 3, 3);
			Coder c4 = new Coder(4, "Jack", 4, 4);
			Coder c5 = new Coder(5, "TheFaxman", 5, 5);
			Coder c6 = new Coder(6, "thorgan", 6, 6);
			Coder c7 = new Coder(7, "mess", 7, 7);
			Coder c8 = new Coder(8, "mmorris", 8, 99);
                        Coder c9 = new Coder(9, "mktong", 1, 1);
			Coder c10 = new Coder(10, "FogleBird", 2, 2);
			Coder c11 = new Coder(11, "John Dethridge", 3, 3);
			Coder c12 = new Coder(12, "ntrefz", 4, 4);
			Coder c13 = new Coder(13, "thx1138", 5, 5);
			Coder c14 = new Coder(14, "ivern", 6, 6);
			Coder c15 = new Coder(15, "AFaxman", 7, 7);
			Coder c16 = new Coder(16, "Bot", 8, 99);
			ProblemID p1 = new ProblemID(33, 250);
			ProblemID p2 = new ProblemID(34, 500);
			ProblemID p3 = new ProblemID(35, 1150);
			coders.add(c1.getCoderRoomData());
			coders.add(c2.getCoderRoomData());
			coders.add(c3.getCoderRoomData());
			coders.add(c4.getCoderRoomData());
			coders.add(c5.getCoderRoomData());
			coders.add(c6.getCoderRoomData());
			coders.add(c7.getCoderRoomData());
			coders.add(c8.getCoderRoomData());
                        coders.add(c9.getCoderRoomData());
			coders.add(c10.getCoderRoomData());
			coders.add(c11.getCoderRoomData());
			coders.add(c12.getCoderRoomData());
			coders.add(c13.getCoderRoomData());
			coders.add(c14.getCoderRoomData());
			coders.add(c15.getCoderRoomData());
			coders.add(c16.getCoderRoomData());
			problems.add(p1.getProblemData());
			problems.add(p2.getProblemData());
			problems.add(p3.getProblemData());
			processor.sendEvent(new DefineRoom(roomData, coders, problems));
			ArrayList resultList = new ArrayList();
			resultList.add(c1.scheduleCoding(roomData, p1, 10, 10, true));
			resultList.add(c1.scheduleCoding(roomData, p2, 23, 40, false));
			resultList.add(c1.scheduleCoding(roomData, p2, 100, -1, false));
			resultList.add(c2.scheduleCoding(roomData, p2, 3, 110, true));
			resultList.add(c3.scheduleCoding(roomData, p1, 5, 45, true));
			resultList.add(c3.scheduleCoding(roomData, p3, 60, 50, false));
			resultList.add(c4.scheduleCoding(roomData, p1, 8, 20, true));
			resultList.add(c4.scheduleCoding(roomData, p2, 33, 40, false));
			resultList.add(c4.scheduleCoding(roomData, p3, 81, -1, true));
			resultList.add(c5.scheduleCoding(roomData, p1, 15, 20, false));
			resultList.add(c5.scheduleCoding(roomData, p2, 30, -1, true));
			resultList.add(c6.scheduleCoding(roomData, p1, 13, -1, false));
			resultList.add(c6.scheduleCoding(roomData, p2, 59, -1, false));
			resultList.add(c6.scheduleCoding(roomData, p2, 80, -1, false));
			resultList.add(c7.scheduleCoding(roomData, p3, 4, 100, true));
			resultList.add(c8.scheduleCoding(roomData, p3, 2, 60, true));
			resultList.add(c8.scheduleCoding(roomData, p2, 70, 20, true));
			resultList.add(c8.scheduleCoding(roomData, p1, 100, 15, false));
                        resultList.add(c9.scheduleCoding(roomData, p1, 12, 10, true));
			resultList.add(c9.scheduleCoding(roomData, p2, 33, 40, false));
			resultList.add(c10.scheduleCoding(roomData, p2, 3, 10, true));
			//resultList.add(c11.scheduleCoding(roomData, p1, 15, 45, true));
         //resultList.add(c11.scheduleCoding(roomData, p2, 5, 15, true));
			resultList.add(c12.scheduleCoding(roomData, p1, 8, 24, true));
			resultList.add(c12.scheduleCoding(roomData, p2, 33, 43, false));
			resultList.add(c13.scheduleCoding(roomData, p1, 15, 19, false));
			resultList.add(c14.scheduleCoding(roomData, p1, 13, -1, false));
			resultList.add(c14.scheduleCoding(roomData, p2, 78, -1, false));
			resultList.add(c14.scheduleCoding(roomData, p2, 80, -1, false));
			resultList.add(c15.scheduleCoding(roomData, p3, 4, 100, true));
			resultList.add(c16.scheduleCoding(roomData, p3, 12, 60, true));
			resultList.add(c16.scheduleCoding(roomData, p2, 73, 20, true));
			c1.scheduleChallenge(roomData, p1, c3, 10, 10, -1);
			c1.scheduleChallenge(roomData, p1, c5, 23, 30, -1);
			c2.scheduleChallenge(roomData, p1, c1, 5, 3, -1);
			c2.scheduleChallenge(roomData, p1, c3, 10, 10, -1);
			c2.scheduleChallenge(roomData, p1, c2, 30, 8, -1);
			c2.scheduleChallenge(roomData, p2, c4, 43, 15, -1);
			c3.scheduleChallenge(roomData, p2, c2, 8, 15, 1);
			c4.scheduleChallenge(roomData, p1, c3, 13, 23, -1);
			c4.scheduleChallenge(roomData, p1, c8, 48, 10, -1);
			c5.scheduleChallenge(roomData, p2, c8, 40, 10, 0);
			c5.scheduleChallenge(roomData, p2, c4, 50, 5, -1);
			c6.scheduleChallenge(roomData, p3, c3, 3, 30, 0);
			c6.scheduleChallenge(roomData, p2, c4, 47, 10, -1);
			//c11.scheduleChallenge(roomData, p3, c8, 30, 8, 1);
			c7.scheduleChallenge(roomData, p1, c5, 30, 8, 0);
			c8.scheduleChallenge(roomData, p1, c3, 10, 10, -1);
			c11.scheduleChallenge(roomData, p2, c16, 11, 5, 1);
			c11.scheduleChallenge(roomData, p3, c16, 17, 7, 1);
			int T = (PRE + REG + COD + INT + CHL) * 1000;
			timer.schedule(new SysResult(roomData, resultList, coders), T);
		}

		class SysResult extends TimerTask {
			RoomData roomData;

			ArrayList results;

			ArrayList coders;

			public SysResult(RoomData roomData, ArrayList results, ArrayList coders) {
				this.roomData = roomData;
				this.results = results;
				this.coders = coders;
			}

			public void run() {
				MessagePacket pack = new MessagePacket();
				double maxValue = -1;
				CoderRoomData winner = null;
				// Note: this should be changed to add up the scores
				// plus the challenges to see who wins - don't care
				// because all we want is someone who wins - don't care who
				pack.add(new TimerUpdate(0));
				pack.add(new Phase(ContestConstants.CONTEST_COMPLETE_PHASE, 0).getMsg());
				for (int x = 0; x < results.size(); x++) {
					TheResult result = (TheResult) results.get(x);
					if (result == null) continue;
					if (result.pts > maxValue) {
						maxValue = result.pts;
						for (int y = 0; y < coders.size(); y++) {
							if (((CoderRoomData) coders.get(y)).getHandle().equals(result.writer)) {
								winner = (CoderRoomData) coders.get(y);
								break;
							}
						}
					}
					pack.add(result.getMsg());
				}
				processor.sendEvent(pack);
				processor.sendEvent(new RoomWinner(roomData, winner));
			}
		}
	}

	abstract class Room {
		public abstract int[] getCoders();
	}

	class ProblemID {
		int problemID;

		int value;

		public ProblemID(int problemID, int value) {
			this.problemID = problemID;
			this.value = value;
		}

		int getProblemID() {
			return problemID;
		}

		int getValue() {
			return value;
		}

		ProblemData getProblemData() {
			return new ProblemData(problemID, value);
		}
	}

	class Coder {
		int coderID;

		String handle;

		int rank;

		int seed;

		public Coder(int coderID, String handle, int rank, int seed) {
			this.coderID = coderID;
			this.handle = handle;
			this.rank = rank;
			this.seed = seed;
		}

		public TheResult scheduleCoding(RoomData roomData, ProblemID problemID, int time, int elapsed, boolean good) {
			int T = (PRE + REG) * 1000;
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, handle, problemID.getProblemID(), handle, COD - time), T + time * 1000);
			double value = 0;
			if (elapsed > 0) {
				value = calcTotal(problemID.getValue(), elapsed, COD);
				timer.schedule(new Problem(roomData, ProblemEvent.SUBMITTING, handle, problemID.getProblemID(), handle, COD - (time + elapsed)), T + (time * 1000 + elapsed * 1000));
				timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING, handle, problemID.getProblemID(), handle, COD - (time + elapsed) + 1, ProblemResult.SUCCESSFUL, value), T
							+ (time * 1000 + elapsed * 1000) + 1000);
				return new TheResult(roomData, ProblemEvent.SYSTEMTESTING, handle, problemID.getProblemID(), handle, 0, (good ? ProblemResult.SUCCESSFUL : ProblemResult.FAILED), (good ? value : 0.0));
			}
			return null;
		}

		public void scheduleChallenge(RoomData roomData, ProblemID problemID, Coder writer, int time, int elapsed, int challenges) {
			int T = (PRE + REG + COD + INT) * 1000;
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, writer.handle, problemID.getProblemID(), handle, CHL - time), T + time * 1000);
			switch (challenges) {
			case -1: {
				timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, writer.handle, problemID.getProblemID(), handle, CHL - (time + elapsed)), T + (time * 1000 + elapsed * 1000));
				break;
			}
			case 0: {
				timer.schedule(new Problem(roomData, ProblemEvent.CHALLENGING, writer.handle, problemID.getProblemID(), handle, CHL - (time + elapsed)), T + (time * 1000 + elapsed * 1000));
				timer.schedule(new TheResult(roomData, ProblemEvent.CHALLENGING, writer.handle, problemID.getProblemID(), handle, CHL - (time + elapsed) + 1, ProblemResult.FAILED, 50), T
							+ (time * 1000 + elapsed * 1000) + 1000);
				timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, writer.handle, problemID.getProblemID(), handle, CHL - (time + elapsed) + 2), T + (time * 1000 + elapsed * 1000) + 2000);
				break;
			}
			case 1: {
				timer.schedule(new Problem(roomData, ProblemEvent.CHALLENGING, writer.handle, problemID.getProblemID(), handle, CHL - (time + elapsed)), T + (time * 1000 + elapsed * 1000));
				timer.schedule(new TheResult(roomData, ProblemEvent.CHALLENGING, writer.handle, problemID.getProblemID(), handle, CHL - (time + elapsed) + 1, ProblemResult.SUCCESSFUL, 50), T
							+ (time * 1000 + elapsed * 1000) + 1000);
				timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, writer.handle, problemID.getProblemID(), handle, CHL - (time + elapsed) + 2), T + (time * 1000 + elapsed * 1000) + 2000);
				break;
			}
			}
		}

		CoderRoomData getCoderRoomData() {
			return new CoderRoomData(coderID, handle, rank, seed);
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

	public int calcTotal(int valueOfProblem, int elapsedTime, int phaseLength) {
		return (int) (100 * (valueOfProblem * (.3 + .7 / (10.0 * Math.pow((double) elapsedTime / (double) phaseLength, 2.0) + 1))));
	}
}
/* @(#)Contest.java */
