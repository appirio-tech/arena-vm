/**
 * Contest.java Description: Contest script
 * 
 * @author Tim "Pops" Roberts
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
import com.topcoder.shared.netCommon.messages.spectator.ShowRoom;
import com.topcoder.shared.netCommon.messages.spectator.TimerUpdate;

public class Contest {
	private static final Category cat = Category.getInstance(SimulatorProcessor.class.getName());

	public SimulatorProcessor processor;

	public int currentPhase;

	public int currentTime;

	public int PRE = 1;

	public int REG = 5;

	public int COD = 120;

	public int INT = 10;

	public int CHL = 60;

	public int SYS = 60;

	public Timer timer = new Timer();

	public Room[] rooms = new Room[8];

	public Contest() {}

	public Contest(SimulatorProcessor processor) {
		this.processor = processor;
		cat.info("Contest starting...");
		cat.info("Sending contests...");
		new TheContest(1, "Tim's special Contest");
		cat.info("Sending rounds...");
		new TheRound(ContestConstants.SRM_ROUND_TYPE_ID, 1, "Tim's Round", 1);
		cat.info("Sending rooms...");
		rooms[0] = new Room1(1, RoomData.SCOREBOARD, "Room1", 1);
		rooms[1] = new Room2(2, RoomData.SCOREBOARD, "Room2", 1);
		rooms[2] = new Room3(3, RoomData.SCOREBOARD, "Room3", 1);
		rooms[3] = new Room4(4, RoomData.SCOREBOARD, "Room4", 1);
		rooms[4] = new Room1(5, RoomData.SCOREBOARD, "Room5", 1);
		rooms[5] = new Room2(6, RoomData.SCOREBOARD, "Room6", 1);
		rooms[6] = new Room3(7, RoomData.SCOREBOARD, "Room7", 1);
		rooms[7] = new Room4(8, RoomData.SCOREBOARD, "Room8", 1);
		// Showroom every 4 seconds once
		// timer.schedule(new ShowRoom(), (PRE+REG)*1000);
		timer.schedule(new ShowRoomHandler(), (PRE + REG) * 1000, 10000);
		// Registration
		int T = PRE * 1000;
		timer.schedule(new Phase(ContestConstants.REGISTRATION_PHASE, REG), T); // 30
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
		// System testing
		T = (PRE + REG + COD + INT + CHL) * 1000;
		timer.schedule(new Phase(ContestConstants.SYSTEM_TESTING_PHASE, 0), T); // 30 second
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

	class Room1 extends Room {
		private ArrayList coders = new ArrayList();

		private ArrayList problems = new ArrayList();

		private int P1 = 1;

		private String CODER1 = "Logan", CODER2 = "NDBronson", CODER3 = "Stevevai", CODER4 = "Ambrose";

		public int[] getCoders() {
			return new int[] { 1, 2, 3, 4, 55 };
		}

		public Room1(int roomID, int roomType, String roomTitle, int roundID) {
			RoomData roomData = new RoomData(roomID, roomType, roomTitle, roundID);
			coders.add(new CoderRoomData(1, CODER1, 3100, 1));
			coders.add(new CoderRoomData(2, CODER2, 2800, 2));
			coders.add(new CoderRoomData(3, CODER3, 1601, 3));
			coders.add(new CoderRoomData(4, CODER4, 1199, 4));
			coders.add(new CoderRoomData(55, "Howdy", 20, 5));
			problems.add(new ProblemData(P1, 200));
			processor.sendEvent(new DefineRoom(roomData, coders, problems));
			// Coding round
			int T = (PRE + REG) * 1000;
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER2, P1, CODER2, COD), T);
			timer.schedule(new Problem(roomData, ProblemEvent.COMPILING, CODER2, P1, CODER2, COD - 1), T + 1000);
			timer.schedule(new Problem(roomData, ProblemEvent.COMPILING, CODER2, P1, CODER2, COD - 4), T + 4000);
			timer.schedule(new Problem(roomData, ProblemEvent.TESTING, CODER2, P1, CODER2, COD - 9), T + 9000);
			timer.schedule(new Problem(roomData, ProblemEvent.TESTING, CODER2, P1, CODER2, COD - 16), T + 16000);
			timer.schedule(new Problem(roomData, ProblemEvent.TESTING, CODER2, P1, CODER2, COD - 15), T + 15000);
			// timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING,
			// CODER1, P1, CODER1, COD-5, ProblemResult.SUCCESSFUL, 100.00),
			// T+5000);
			// timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING,
			// CODER2, P1, CODER2, COD-5, ProblemResult.SUCCESSFUL, 200.00),
			// T+5000);
			// timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING,
			// CODER3, P1, CODER3, COD-5, ProblemResult.SUCCESSFUL, 300.00),
			// T+5000);
			// timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING,
			// CODER4, P1, CODER4, COD-5, ProblemResult.SUCCESSFUL, 400.00),
			// T+5000);
			timer.schedule(new Problem(roomData, ProblemEvent.SUBMITTING, CODER2, P1, CODER2, COD - 26), T + 26000);
			timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING, CODER2, P1, CODER2, COD - 27, ProblemResult.SUCCESSFUL, 160.00), T + 27000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER4, P1, CODER4, COD - 92), T + 92000);
			timer.schedule(new Problem(roomData, ProblemEvent.SUBMITTING, CODER4, P1, CODER4, COD - 112), T + 112000);
			timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING, CODER4, P1, CODER4, COD - 112, ProblemResult.SUCCESSFUL, 189.14), T + 112000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER3, P1, CODER3, COD - 3), T + 3000);
			timer.schedule(new Problem(roomData, ProblemEvent.SUBMITTING, CODER3, P1, CODER3, COD - 15), T + 15000);
			timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING, CODER3, P1, CODER3, COD - 16, ProblemResult.SUCCESSFUL, 189.14), T + 16000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER1, P1, CODER1, COD - 5), T + 5000);
			timer.schedule(new Problem(roomData, ProblemEvent.SUBMITTING, CODER1, P1, CODER1, COD - 10), T + 10000);
			timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING, CODER1, P1, CODER1, COD - 11, ProblemResult.SUCCESSFUL, 198.46), T + 11000);
			// Challenge
			T = (PRE + REG + COD + INT) * 1000;
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER3, P1, CODER4, CHL - 1), T + 1000);
			timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, CODER3, P1, CODER4, CHL - 14), T + 14000);
			timer.schedule(new Problem(roomData, ProblemEvent.CHALLENGING, CODER3, P1, CODER4, CHL - 15), T + 15000);
			timer.schedule(new TheResult(roomData, ProblemEvent.CHALLENGING, CODER3, P1, CODER4, CHL - 16, ProblemResult.SUCCESSFUL, 50), T + 16000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER2, P1, CODER4, CHL - 20), T + 20000);
			timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, CODER2, P1, CODER4, CHL - 30), T + 30000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER1, P1, CODER4, CHL - 40), T + 40000);
			timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, CODER1, P1, CODER4, CHL - 45), T + 45000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER1, P1, CODER2, CHL - 2), T + 2000);
			timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, CODER1, P1, CODER2, CHL - 17), T + 17000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER4, P1, CODER2, CHL - 25), T + 25000);
			timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, CODER4, P1, CODER2, CHL - 35), T + 35000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER3, P1, CODER2, CHL - 50), T + 50000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER4, P1, CODER1, CHL - 2), T + 2000);
			timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, CODER4, P1, CODER1, CHL - 23), T + 23000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER3, P1, CODER1, CHL - 30), T + 30000);
			timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, CODER3, P1, CODER1, CHL - 40), T + 40000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER2, P1, CODER1, CHL - 44), T + 44000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER2, P1, CODER3, CHL - 3), T + 3000);
			timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, CODER2, P1, CODER3, CHL - 10), T + 10000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER1, P1, CODER3, CHL - 12), T + 12000);
			timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, CODER1, P1, CODER3, CHL - 30), T + 30000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER4, P1, CODER3, CHL - 33), T + 33000);
			timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, CODER4, P1, CODER3, CHL - 43), T + 43000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER4, P1, CODER3, CHL - 55), T + 55000);
			T = (PRE + REG + COD + INT + CHL) * 1000;
			timer.schedule(new SysResult(roomData), T + 20000);
		}

		class SysResult extends TimerTask {
			RoomData roomData;

			public SysResult(RoomData roomData) {
				this.roomData = roomData;
			}

			public void run() {
				MessagePacket pack = new MessagePacket();
				pack.add(new TimerUpdate(0));
				pack.add(new Phase(ContestConstants.CONTEST_COMPLETE_PHASE, 0).getMsg());
				pack.add(new TheResult(roomData, ProblemEvent.SYSTEMTESTING, CODER2, P1, CODER2, 0, ProblemResult.SUCCESSFUL, 160.00).getMsg());
				pack.add(new TheResult(roomData, ProblemEvent.SYSTEMTESTING, CODER4, P1, CODER4, 0, ProblemResult.SUCCESSFUL, 189.14).getMsg());
				pack.add(new TheResult(roomData, ProblemEvent.SYSTEMTESTING, CODER1, P1, CODER1, 0, ProblemResult.SUCCESSFUL, 198.46).getMsg());
				processor.sendEvent(pack);
				processor.sendEvent(new RoomWinner(roomData, new CoderRoomData(2, CODER2, 3100, 1)));
			}
		}
	}

	class Room2 extends Room {
		private ArrayList coders = new ArrayList();

		private ArrayList problems = new ArrayList();

		private int P1 = 22;

		private String CODER1 = "Pops", CODER2 = "Wyzmo", CODER3 = "Steven", CODER4 = "UCF[Pawn]";

		public int[] getCoders() {
			return new int[] { 5, 6, 7, 8 };
		}

		public Room2(int roomID, int roomType, String roomTitle, int roundID) {
			RoomData roomData = new RoomData(roomID, roomType, roomTitle, roundID);
			coders.add(new CoderRoomData(6, CODER2, 1659, 21));
			coders.add(new CoderRoomData(8, CODER4, 1398, 22));
			coders.add(new CoderRoomData(5, CODER1, 983, 23));
			coders.add(new CoderRoomData(7, CODER3, 757, 24));
			problems.add(new ProblemData(P1, 500));
			processor.sendEvent(new DefineRoom(roomData, coders, problems));
			// Coding round
			int T = (PRE + REG) * 1000;
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER2, P1, CODER2, COD), T);
			timer.schedule(new Problem(roomData, ProblemEvent.SUBMITTING, CODER2, P1, CODER2, COD - 86), T + 86000);
			timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING, CODER2, P1, CODER2, COD - 87, ProblemResult.SUCCESSFUL, 320.33), T + 87000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER4, P1, CODER4, COD - 62), T + 62000);
			timer.schedule(new Problem(roomData, ProblemEvent.SUBMITTING, CODER4, P1, CODER4, COD - 89), T + 89000);
			timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING, CODER4, P1, CODER4, COD - 99, ProblemResult.SUCCESSFUL, 278.38), T + 99000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER3, P1, CODER3, COD - 100), T + 100000);
			timer.schedule(new Problem(roomData, ProblemEvent.SUBMITTING, CODER3, P1, CODER3, COD - 115), T + 115000);
			timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING, CODER3, P1, CODER3, COD - 116, ProblemResult.SUCCESSFUL, 220.93), T + 116000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER1, P1, CODER1, COD - 50), T + 50000);
			timer.schedule(new Problem(roomData, ProblemEvent.SUBMITTING, CODER1, P1, CODER1, COD - 60), T + 60000);
			timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING, CODER1, P1, CODER1, COD - 61, ProblemResult.SUCCESSFUL, 163.95), T + 61000);
			// Challenge
			T = (PRE + REG + COD + INT) * 1000;
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER2, P1, CODER4, CHL - 1), T + 1000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER1, P1, CODER2, CHL - 2), T + 2000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER3, P1, CODER1, CHL - 2), T + 2000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER2, P1, CODER3, CHL - 3), T + 3000);
			timer.schedule(new Problem(roomData, ProblemEvent.CHALLENGING, CODER3, P1, CODER2, CHL - 30), T + 30000);
			timer.schedule(new TheResult(roomData, ProblemEvent.CHALLENGING, CODER3, P1, CODER2, CHL - 31, ProblemResult.SUCCESSFUL, 50), T + 31000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER1, P1, CODER4, CHL - 20), T + 20000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER4, P1, CODER2, CHL - 35), T + 35000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER4, P1, CODER1, CHL - 40), T + 40000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER1, P1, CODER3, CHL - 53), T + 53000);
			timer.schedule(new Problem(roomData, ProblemEvent.CHALLENGING, CODER1, P1, CODER3, CHL - 56), T + 56000);
			timer.schedule(new TheResult(roomData, ProblemEvent.CHALLENGING, CODER1, P1, CODER3, CHL - 57, ProblemResult.SUCCESSFUL, 50), T + 57000);
			timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, CODER2, P1, CODER4, CHL - 9), T + 9000);
			timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, CODER1, P1, CODER2, CHL - 23), T + 23000);
			timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, CODER2, P1, CODER1, CHL - 35), T + 35000);
			timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, CODER2, P1, CODER3, CHL - 50), T + 50000);
			T = (PRE + REG + COD + INT + CHL) * 1000;
			timer.schedule(new SysResult(roomData), T + 20000);
		}

		class SysResult extends TimerTask {
			RoomData roomData;

			public SysResult(RoomData roomData) {
				this.roomData = roomData;
			}

			public void run() {
				MessagePacket pack = new MessagePacket();
				pack.add(new TimerUpdate(0));
				pack.add(new Phase(ContestConstants.CONTEST_COMPLETE_PHASE, 0).getMsg());
				pack.add(new TheResult(roomData, ProblemEvent.SYSTEMTESTING, CODER2, P1, CODER2, 0, ProblemResult.SUCCESSFUL, 320.33).getMsg());
				pack.add(new TheResult(roomData, ProblemEvent.SYSTEMTESTING, CODER4, P1, CODER4, 0, ProblemResult.SUCCESSFUL, 278.38).getMsg());
				processor.sendEvent(pack);
			}
		}
	}

	class Room3 extends Room {
		private ArrayList coders = new ArrayList();

		private ArrayList problems = new ArrayList();

		private int P1 = 32, P2 = 33, P3 = 34;

		private String CODER1 = "GT", CODER2 = "dok", CODER3 = "Mike", CODER4 = "Jack";

		public int[] getCoders() {
			return new int[] { 9, 10, 11, 12 };
		}

		public Room3(int roomID, int roomType, String roomTitle, int roundID) {
			RoomData roomData = new RoomData(roomID, roomType, roomTitle, roundID);
			coders.add(new CoderRoomData(10, CODER2, 1100, 31));
			coders.add(new CoderRoomData(12, CODER4, 3999, 32));
			coders.add(new CoderRoomData(9, CODER1, 1245, 33));
			coders.add(new CoderRoomData(11, CODER3, 2200, 34));
			problems.add(new ProblemData(P1, 250));
			problems.add(new ProblemData(P2, 450));
			problems.add(new ProblemData(P3, 1050));
			processor.sendEvent(new DefineRoom(roomData, coders, problems));
			// Coding round
			int T = (PRE + REG) * 1000;
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER2, P1, CODER2, COD), T);
			timer.schedule(new Problem(roomData, ProblemEvent.SUBMITTING, CODER2, P1, CODER2, COD - 86), T + 86000);
			timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING, CODER2, P1, CODER2, COD - 87, ProblemResult.SUCCESSFUL, 120.33), T + 87000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER2, P2, CODER2, COD - 93), T + 93000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER2, P3, CODER2, COD - 105), T + 105000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER4, P1, CODER4, COD - 1), T + 1000);
			timer.schedule(new Problem(roomData, ProblemEvent.SUBMITTING, CODER4, P1, CODER4, COD - 20), T + 20000);
			timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING, CODER4, P1, CODER4, COD - 21, ProblemResult.SUCCESSFUL, 78.38), T + 21000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER4, P3, CODER4, COD - 23), T + 23000);
			timer.schedule(new Problem(roomData, ProblemEvent.SUBMITTING, CODER4, P3, CODER4, COD - 80), T + 80000);
			timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING, CODER4, P3, CODER4, COD - 81, ProblemResult.SUCCESSFUL, 78.38), T + 81000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER4, P2, CODER4, COD - 86), T + 86000);
			timer.schedule(new Problem(roomData, ProblemEvent.SUBMITTING, CODER4, P2, CODER4, COD - 105), T + 105000);
			timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING, CODER4, P2, CODER4, COD - 106, ProblemResult.SUCCESSFUL, 78.38), T + 106000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER3, P1, CODER3, COD - 5), T + 5000);
			timer.schedule(new Problem(roomData, ProblemEvent.SUBMITTING, CODER3, P1, CODER3, COD - 15), T + 15000);
			timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING, CODER3, P1, CODER3, COD - 16, ProblemResult.SUCCESSFUL, 20.93), T + 16000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER3, P3, CODER3, COD - 25), T + 25000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER3, P3, CODER3, COD - 40), T + 40000);
			timer.schedule(new Problem(roomData, ProblemEvent.SUBMITTING, CODER3, P3, CODER3, COD - 115), T + 115000);
			timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING, CODER3, P3, CODER3, COD - 116, ProblemResult.SUCCESSFUL, 20.93), T + 116000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER1, P3, CODER1, COD - 5), T + 5000);
			timer.schedule(new Problem(roomData, ProblemEvent.SUBMITTING, CODER1, P3, CODER1, COD - 60), T + 60000);
			timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING, CODER1, P3, CODER1, COD - 61, ProblemResult.SUCCESSFUL, 78.38), T + 6100);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER1, P2, CODER1, COD - 75), T + 75000);
			timer.schedule(new Problem(roomData, ProblemEvent.SUBMITTING, CODER1, P2, CODER1, COD - 99), T + 99000);
			timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING, CODER1, P2, CODER1, COD - 100, ProblemResult.SUCCESSFUL, 78.38), T + 100000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER1, P1, CODER1, COD - 102), T + 102000);
			timer.schedule(new Problem(roomData, ProblemEvent.SUBMITTING, CODER1, P1, CODER1, COD - 117), T + 117000);
			timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING, CODER1, P1, CODER1, COD - 118, ProblemResult.SUCCESSFUL, 163.95), T + 118000);
			// Challenge
			T = (PRE + REG + COD + INT) * 1000;
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER2, P1, CODER4, CHL - 1), T + 1000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER1, P1, CODER2, CHL - 2), T + 2000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER3, P1, CODER1, CHL - 2), T + 2000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER2, P1, CODER3, CHL - 3), T + 3000);
			timer.schedule(new Problem(roomData, ProblemEvent.CHALLENGING, CODER3, P1, CODER2, CHL - 30), T + 30000);
			timer.schedule(new TheResult(roomData, ProblemEvent.CHALLENGING, CODER3, P1, CODER2, CHL - 31, ProblemResult.SUCCESSFUL, 50), T + 31000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER1, P1, CODER4, CHL - 20), T + 20000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER4, P1, CODER2, CHL - 35), T + 35000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER4, P1, CODER1, CHL - 40), T + 40000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER1, P1, CODER3, CHL - 53), T + 53000);
			timer.schedule(new Problem(roomData, ProblemEvent.CHALLENGING, CODER1, P1, CODER3, CHL - 56), T + 56000);
			timer.schedule(new TheResult(roomData, ProblemEvent.CHALLENGING, CODER1, P1, CODER3, CHL - 57, ProblemResult.SUCCESSFUL, 50), T + 57000);
			timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, CODER2, P1, CODER4, CHL - 9), T + 9000);
			timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, CODER1, P1, CODER2, CHL - 23), T + 23000);
			timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, CODER2, P1, CODER1, CHL - 35), T + 35000);
			timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, CODER2, P1, CODER3, CHL - 50), T + 50000);
			T = (PRE + REG + COD + INT + CHL) * 1000;
			timer.schedule(new SysResult(roomData), T + 20000);
		}

		class SysResult extends TimerTask {
			RoomData roomData;

			public SysResult(RoomData roomData) {
				this.roomData = roomData;
			}

			public void run() {
				MessagePacket pack = new MessagePacket();
				pack.add(new TimerUpdate(0));
				pack.add(new Phase(ContestConstants.CONTEST_COMPLETE_PHASE, 0).getMsg());
				pack.add(new TheResult(roomData, ProblemEvent.SYSTEMTESTING, CODER2, P1, CODER2, 0, ProblemResult.SUCCESSFUL, 120.33).getMsg());
				pack.add(new TheResult(roomData, ProblemEvent.SYSTEMTESTING, CODER4, P1, CODER4, 0, ProblemResult.SUCCESSFUL, 78.38).getMsg());
				processor.sendEvent(pack);
				processor.sendEvent(new RoomWinner(roomData, new CoderRoomData(10, CODER2, 0, 0)));
				processor.sendEvent(new RoomWinner(roomData, new CoderRoomData(12, CODER4, 0, 0)));
			}
		}
	}

	class Room4 extends Room {
		private ArrayList coders = new ArrayList();

		private ArrayList problems = new ArrayList();

		private int P1 = 41;

		private String CODER2 = "Curly", CODER3 = "Moe", CODER4 = "Larry";

		public int[] getCoders() {
			return new int[] { 13, 14, 15 };
		}

		public Room4(int roomID, int roomType, String roomTitle, int roundID) {
			RoomData roomData = new RoomData(roomID, roomType, roomTitle, roundID);
			coders.add(new CoderRoomData(13, CODER2, 1, 41));
			coders.add(new CoderRoomData(15, CODER4, 1650, 42));
			coders.add(new CoderRoomData(14, CODER3, 1499, 44));
			problems.add(new ProblemData(P1, 4500));
			processor.sendEvent(new DefineRoom(roomData, coders, problems));
			// Coding round
			int T = (PRE + REG) * 1000;
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER2, P1, CODER2, COD - 10), T + 10000);
			timer.schedule(new Problem(roomData, ProblemEvent.SUBMITTING, CODER2, P1, CODER2, COD - 110), T + 110000);
			timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING, CODER2, P1, CODER2, COD - 111, ProblemResult.SUCCESSFUL, 2375.23), T + 111000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER4, P1, CODER4, COD - 22), T + 22000);
			timer.schedule(new Problem(roomData, ProblemEvent.SUBMITTING, CODER4, P1, CODER4, COD - 75), T + 75000);
			timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING, CODER4, P1, CODER4, COD - 76, ProblemResult.SUCCESSFUL, 3323.22), T + 76000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER3, P1, CODER3, COD - 50), T + 50000);
			timer.schedule(new Problem(roomData, ProblemEvent.SUBMITTING, CODER3, P1, CODER3, COD - 99), T + 99000);
			timer.schedule(new TheResult(roomData, ProblemEvent.SUBMITTING, CODER3, P1, CODER3, COD - 100, ProblemResult.SUCCESSFUL, 3933.57), T + 100000);
			// Challenge
			T = (PRE + REG + COD + INT) * 1000;
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER3, P1, CODER4, CHL - 1), T + 1000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER3, P1, CODER2, CHL - 2), T + 2000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER2, P1, CODER3, CHL - 3), T + 3000);
			timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, CODER3, P1, CODER4, CHL - 16), T + 16000);
			timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, CODER3, P1, CODER2, CHL - 44), T + 44000);
			timer.schedule(new Problem(roomData, ProblemEvent.CLOSED, CODER2, P1, CODER3, CHL - 46), T + 46000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER2, P1, CODER4, CHL - 17), T + 17000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER3, P1, CODER2, CHL - 45), T + 45000);
			timer.schedule(new Problem(roomData, ProblemEvent.OPENED, CODER4, P1, CODER3, CHL - 48), T + 47000);
			T = (PRE + REG + COD + INT + CHL) * 1000;
			timer.schedule(new SysResult(roomData), T + 20000);
		}

		class SysResult extends TimerTask {
			RoomData roomData;

			public SysResult(RoomData roomData) {
				this.roomData = roomData;
			}

			public void run() {
				MessagePacket pack = new MessagePacket();
				pack.add(new TimerUpdate(0));
				pack.add(new Phase(ContestConstants.CONTEST_COMPLETE_PHASE, 0).getMsg());
				pack.add(new TheResult(roomData, ProblemEvent.SYSTEMTESTING, CODER2, P1, CODER2, 0, ProblemResult.SUCCESSFUL, 2375.23).getMsg());
				pack.add(new TheResult(roomData, ProblemEvent.SYSTEMTESTING, CODER3, P1, CODER3, 0, ProblemResult.SUCCESSFUL, 3323.22).getMsg());
				pack.add(new TheResult(roomData, ProblemEvent.SYSTEMTESTING, CODER4, P1, CODER4, 0, ProblemResult.SUCCESSFUL, 3833.57).getMsg());
				pack.add(new RoomWinner(roomData, new CoderRoomData(13, CODER2, 4, 0)));
				processor.sendEvent(pack);
				processor.sendEvent(new RoomWinner(roomData, new CoderRoomData(13, CODER2, 4, 0)));
			}
		}
	}

	abstract class Room {
		public abstract int[] getCoders();
	}

	class ShowRoomHandler extends TimerTask {
		int room = 1;

		public ShowRoomHandler() {}

		public void run() {
			processor.sendEvent(new ShowRoom(new RoomData(room, 0, "", 1), rooms[room - 1].getCoders()));
			room++;
			if (room > rooms.length) room = 1;
		}

		public Message getMsg() {
			return new ShowRoom(new RoomData(room, 0, "", 1), rooms[room - 1].getCoders());
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
}
/* @(#)Contest.java */
