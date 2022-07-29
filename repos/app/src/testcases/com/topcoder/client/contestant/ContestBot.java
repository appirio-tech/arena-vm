/**
 * @author Michael Cervantes (emcee)
 * @since Apr 30, 2002
 *
 *  */
package com.topcoder.client.contestant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import com.topcoder.client.contestant.impl.ContestantImpl;
import com.topcoder.client.contestant.message.Requester;
import com.topcoder.client.contestant.view.ContestantView;
import com.topcoder.client.contestant.view.EventService;
import com.topcoder.client.contestant.view.LeaderListener;
import com.topcoder.client.contestant.view.MenuView;
import com.topcoder.client.contestant.view.PhaseListener;
import com.topcoder.client.contestant.view.RoomViewManager;
import com.topcoder.client.contestant.view.RoundView;
import com.topcoder.client.contestant.view.TeamListView;
import com.topcoder.client.contestant.view.UserListListener;
import com.topcoder.farm.shared.util.concurrent.WaitFlag;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.CoderHistoryResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateVisitedPracticeResponse;
import com.topcoder.netCommon.contestantMessages.response.GetImportantMessagesResponse;
import com.topcoder.netCommon.contestantMessages.response.ImportantMessageResponse;
import com.topcoder.netCommon.contestantMessages.response.LongTestResultsResponse;
import com.topcoder.netCommon.contestantMessages.response.NoBadgeIdResponse;
import com.topcoder.netCommon.contestantMessages.response.PracticeSystemTestResponse;
import com.topcoder.netCommon.contestantMessages.response.PracticeSystemTestResultResponse;
import com.topcoder.netCommon.contestantMessages.response.RoundStatsResponse;
import com.topcoder.netCommon.contestantMessages.response.SubmissionHistoryResponse;
import com.topcoder.netCommon.contestantMessages.response.VoteResponse;
import com.topcoder.netCommon.contestantMessages.response.VoteResultsResponse;
import com.topcoder.netCommon.contestantMessages.response.WLMyTeamInfoResponse;
import com.topcoder.netCommon.contestantMessages.response.WLTeamsInfoResponse;
import com.topcoder.netCommon.contestantMessages.response.data.TeamListInfo;
import com.topcoder.netCommon.contestantMessages.response.data.UserListItem;
import com.topcoder.server.util.FileUtil;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.TestCase;

public class ContestBot implements ContestantView, Runnable {
    //Number of times to execute an action
    private static final Range COMPILE_COUNT = new Range(1, 10);  //Number of times a component is compiled
    private static final Range USER_TEST_COUNT = new Range(1, 5); //Number of tests for each correct compilation


    //Time to wait before executing an action
    private static final Range BEFORE_LOGIN = new Range(1,480);
    //Wait until registration phase (barrier)
    private static final Range BEFORE_REGISTER = new Range(1,480); //Max time registration 8 minutes
    private static final Range BEFORE_ENTER = new Range(1,480); //After registering and room assigned. wait
    //Wait until coding phase (barrier)
    private static final Range AFTER_E_CODING = new Range(1,10);
    private static final Range BEFORE_OPEN_COMPONENT = new Range(1,30);
    private static final Range BEFORE_FIRST_COMPILE = new Range(20,10*60);
    private static final Range BETWEEN_COMPILE = new Range(10,120);
    private static final Range BETWEEN_USER_TESTS = new Range(10,30);
    private static final Range BEFORE_SUBMIT = new Range(5, 20);
    private static final Range BEFORE_CLOSE = new Range(5,20);
    //Wait until challenge phase (barrier)
    private static final Range AFTER_E_CHALLENGE = new Range(2, 10);
    private static final Range USER_MUST_CHALLENGE = new Range(1,5); //User challenges if 1 is picked (20)
    private static final Range BEFORE_CHALLENGE_COMPONENT = new Range(5,30);
    private static final Range BEFORE_CHALLENGE = new Range(20,120); //After viewing the problem
    //Wait until end-contest phase (barrier)
    private static final Range BEFORE_EXIT = new Range(1, 60);

    private static final Range SOME = new Range(1,5);
    private static final long TIMEOUT_MS = 5 * 1000 * 60;
    private static final long PARTIAL_TIMEOUT_MS = 1000;
    private static final String coderPw = "password";

    private static Map correctSolutions = new HashMap();
    private static Map incorrectSolutions = new HashMap();
    private static Map invalidSolutions = new HashMap();
    private static String resourcesFolder;
    private static String host;
    private static int port;


    /*
     * Instance fields
     */
    private Object actionLock = new Object();
    private String handle;
    private RoomModel currentRoom;
    private RoundModel myRound;
    private long myRoundID;
    private Random random = new Random();
    private Object phaseLock = new Object();
    private ContestantImpl contestantModel;
    private Requester requester;

    private WaitFlag responseWaiter = new WaitFlag();


    public static void main(String[] args) throws Exception {
        System.out.println("args ->   host port rootpath roundId offset #clients");
        host = args[0];
        port = Integer.parseInt(args[1]);
        String resourceFolder = args[2];
        long roundID = Long.parseLong(args[3]);
        int offset = Integer.parseInt(args[4]);
        int numClients = Integer.parseInt(args[5]);
        int maxClientIndex = numClients+offset;
        addSolutions(roundID);
        List threads = new ArrayList(numClients);
        String[] clients = getClients(new File(resourceFolder, "R"+roundID+"/"+"users.txt").getAbsolutePath());
        for (int j = offset; j < maxClientIndex; j++) {
            System.out.println("STARTING THREAD " + j);
            Thread thread = new Thread(new ContestBot(roundID, clients[j]));
            threads.add(thread);
            thread.start();
        }
        for (Iterator it = threads.iterator(); it.hasNext(); ) {
            Thread thread = (Thread) it.next();
            thread.join();
        }
    }

    public ContestBot(long myRoundID, String handle) {
        this.myRoundID = myRoundID;
        contestantModel = new ContestantImpl(false);
        // TODO emcee fix this
        contestantModel.init(host, port, "", this, activeUsersView, registeredUsersView, hsRegisteredUsersView, hsRegisteredUsersView, teamsView, availableUsersView, memberUsersView, menuView, roomManager, new EventService() {
            public void invokeLater(Runnable runnable) {
                runnable.run();
            }
        },"");
        contestantModel.getRoundViewManager().addListener(roundView);
        this.handle = handle;
        requester = contestantModel.getRequester();
    }

    private static void addSolutions(long roundID)  {
        try {
            File roundFolder = new File(resourcesFolder, "R"+roundID);
            String[] componentsFolders = roundFolder.list();
            for (int i = 0; i < componentsFolders.length; i++) {
                if (!componentsFolders[i].startsWith("C")) continue;
                Long componentId = Long.valueOf(componentsFolders[i].substring(1));
                File componentFolder = new File(roundFolder, componentsFolders[i]);
                Solution[] correctSols = loadSolutions(1, new File(componentFolder, "correct"));
                Solution[] incorrectSols = loadSolutions(2, new File(componentFolder, "incorrect"));
                Solution[] invalidSols = loadSolutions(3, new File(componentFolder, "invalid"));
                correctSolutions.put(componentId, correctSols);
                incorrectSolutions.put(componentId, incorrectSols);
                invalidSolutions.put(componentId, invalidSols);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static Solution[] loadSolutions(int type, File folder) throws IOException {
        String[] files = folder.list();
        List sols = new LinkedList();
        for (int i = 0; i < files.length; i++) {
            String file = files[i];
            if (!file.startsWith("S")) continue;
            int languageId = Integer.parseInt(file.substring(1,2));
            sols.add(new Solution(type, languageId, FileUtil.getStringContents(new File(folder, file))+"\n"));
        }
        return (Solution[]) sols.toArray(new Solution[sols.size()]);
    }

    /*
     *
     * Main method of each client
     *
     */
    public void run() {
        try {
            try {
                sleep(BEFORE_LOGIN);
                login();
            } catch (Exception e) {
                log("BAD ROUND");
                return;
            }
            register();
            moveToCoderRoom();
            doCodingPhase();
            doChallengePhase();
            log("waiting end contest event");
            waitOnPhase(ContestConstants.CONTEST_COMPLETE_PHASE);
            log("end contest event!!!");
            sleep(BEFORE_EXIT);
            chat("Bye!");
        } catch (Exception e) {
            log("Exception from client " + handle, e);
        } finally {
            logoff();
        }
    }


    public void setConnectionStatus(boolean on) {

    }

    public void reconnectFailedEvent() {

    }

    private void login() throws LoginException, TimeOutException {
        contestantModel.login(handle, coderPw.toCharArray(), handle);
        contestantModel.move(ContestConstants.LOBBY_ROOM, ContestConstants.ANY_ROOM);

        RoundModel[] rounds = contestantModel.getActiveRounds();
        for (int i = 0; i < rounds.length; i++) {
            if (rounds[i].getRoundID().longValue() == myRoundID) {
                myRound = rounds[i];
                myRound.addPhaseListener(phaseListener);
                return;
            }
        }
        throw new IllegalStateException("Couldn't find round #" + myRoundID);
    }




    private void chat(String msg) {
        requester.requestChatMessage(currentRoom.getRoomID().intValue(), msg + '\n', ContestConstants.GLOBAL_CHAT_SCOPE);
    }


    private void register() throws InterruptedException {
        log("Waiting Registration event");
        waitOnPhase(ContestConstants.REGISTRATION_PHASE);
        log("Registration event!");
        sleep(BEFORE_REGISTER);
        log("Registering");
        requester.requestRegister(myRoundID, new ArrayList());

    }

    private void moveToCoderRoom() throws InterruptedException, TimeOutException {
        long waited = 0;
        while (!myRound.hasCoderRooms() && waited < TIMEOUT_MS) {
            synchronized (phaseLock) {
                phaseLock.wait(PARTIAL_TIMEOUT_MS);
                waited += PARTIAL_TIMEOUT_MS;
            }
        }
        if (!myRound.hasCoderRooms()) {
            throw new TimeOutException("Timed out waiting for rooms...");
        }
        sleep(BEFORE_ENTER);
        log("Moving to contest room");
        synchronized (actionLock) {
            contestantModel.move(ContestConstants.CONTEST_ROOM, myRoundID);
        }
    }


    private void doCodingPhase() throws Exception {
        log("Waiting CODING event");
        waitOnPhase(ContestConstants.CODING_PHASE);
        log("CODING event!!!");
        sleep(AFTER_E_CODING);
        chat("Coding start...");
        ProblemModel problems[] = myRound.getProblems(currentRoom.getDivisionID());
        for (int i = 0; i < problems.length; i++) {
            ProblemModel problem = problems[i];
            ProblemComponentModel componentModel = problem.getComponents()[0];
            long problemId = problem.getProblemID().longValue();
            long componentID = componentModel.getID().longValue();
            synchronized (actionLock) {
                sleep(BEFORE_OPEN_COMPONENT);
//                requester.requestCoderProblem(problem.getProblemID().longValue());
                log("Opening for coding compId="+componentID);
                requester.requestOpenComponentForCoding(componentID);
            }
            sleep(BEFORE_FIRST_COMPILE);
            int compiles = COMPILE_COUNT.rand();
            for (int z = 0; z < compiles; z++) {
                log("Compile loop "+z+"/"+compiles);
                doPlayWithASolution(problemId, componentID, 0);
            }
            doPlayWithASolution(problemId, componentID, 1);
            chat("Compiled #" + componentID + "...");

            synchronized (actionLock) {
                sleep(BEFORE_SUBMIT);
                log("Submiting component");
                requester.requestSubmitCode(componentID);
            }
            sleep(BEFORE_CLOSE);
            log("Closing component");
            requester.requestCloseComponent(componentID, handle);
        }
        sleep(SOME);
        chat("Done...");
    }

    private void doPlayWithASolution(long problemId, long componentID, int type) throws Exception {
        synchronized (actionLock) {
            Solution solution = null;
            solution = getSolution(componentID, type);
            sleep(BETWEEN_COMPILE);
            log("Compiling");
            responseWaiter.clear();
            requester.requestCompile(solution.code, solution.langID, componentID);
            if (responseWaiter.await(30000) && solution.type != 3) {
                doSomeUserTests(problemId, componentID);
            }
        }
    }

    private void doSomeUserTests(long problemId, long componentID) throws Exception {
        int userTests = USER_TEST_COUNT.rand();
        log("User tests "+userTests);
        for (int i = 0; i < userTests; i++) {
            log("User testing "+i+"/"+userTests);
            ArrayList args = selectExampleTest(problemId, componentID);
            if (args != null) {
                sleep(BETWEEN_USER_TESTS);
                responseWaiter.clear();
                requester.requestTest(args, componentID);
                responseWaiter.await(30000);
            }
        }
    }


    private ArrayList selectExampleTest(long problemId, long componentID) {
        ProblemModel problem = myRound.getProblem(currentRoom.getDivisionID(), new Long(problemId) );
        ProblemComponentModel model = problem.getComponents()[0];
        TestCase[] testCases = model.getTestCases();
        if (testCases == null || testCases.length == 0) return null;
        return loadExample(model, random.nextInt(testCases.length));
    }

    private ArrayList loadExample(ProblemComponentModel probComponent, int idx)
    {
        DataType[] params = probComponent.getParamTypes();

        //
        // load the specified example
        //
        String[] input = probComponent.getTestCases()[idx].getInput();
        ArrayList args = new ArrayList(input.length);
        for(int i = 0; i < input.length; i++)
        {
            //
            // check to see what the input is
            //
            if (params[i].getDescription().equals("ArrayList") ||
                    params[i].getDescription().startsWith("vector") ||
                    params[i].getDescription().endsWith("[]"))
            {
                args.add(bracketParse(input[i]));
            }
            else
            {
                if(params[i].getDescription().equalsIgnoreCase("string"))
                {
                    //
                    // use the bracketParse method to parse out any escape
                    // characters and wrapping quotes.
                    //
                    ArrayList al = bracketParse(input[i]);
                    args.add(al.get(0));
                }
                else if(params[i].getDescription().equals("char"))
                {
                    //
                    // erase the single quotes around the character
                    //
                    args.add(input[i].substring(1, input[i].length() - 1));
                }
                else
                {
                    args.add(input[i]);
                }
            }
        }
        return args;
    }

    /**
     * Parse an array-type argument that is enclosed with braces.  Each
     * element of the array is separated by a comma.
     *
     * Note: this is copied from ArrayListInputdialog.java.
     *
     * @param text The input string to be parsed.
     *
     * @return An array list containing all the values parsed from the string.
     */
    private final int START = 0;
    private final int IN_QUOTE = 1;
    private final int ESCAPE = 2;
    private ArrayList bracketParse(String text)
    {
        ArrayList result = new ArrayList();
        text = text.trim();
                //
                // modified 4/9/2003 by schveiguy
                //
                // fix bug where empty array causes exception
                //
                if(text.length() > 0 && text.charAt(0) == '{') text = text.substring(1);
        if (text.length() > 0 && text.charAt(text.length() - 1) == '}') text = text.substring(0, text.length() - 1);
                if(text.length() == 0)
                    return result;
        int state = START;
        StringBuffer buf = new StringBuffer(50);
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            switch (state) {
            case ESCAPE:
                switch (ch) {
                case '\\':
                    buf.append('\\');
                    break;
                case '"':
                    buf.append('"');
                    break;
                default: //we'll just assume it was a mistake, problems really should not use tabs, line feeds, etc.
                    buf.append('\\');
                    buf.append(ch);
                }
                state = IN_QUOTE;
                break;
            case IN_QUOTE:
                switch (ch) {
                case '\\':
                    state = ESCAPE;
                    break;
                case '"':
                    String param = buf.toString();
                    buf.delete(0, buf.length());
                    state = START;
                    if (param.length() > 50) {
                        continue;
                    }
                    result.add(param);
                    break;
                default:
                    buf.append(ch);
                    break;
                }
                break;
            case START:
                if (Character.isWhitespace(ch)) {
                    if (buf.length() > 0) {
                        String param = buf.toString().trim();
                        buf.delete(0, buf.length());
                        if (param.length() > 50) {
                            continue;
                        }
                        result.add(param);
                    }
                    continue;
                }
                switch (ch) {
                case '"':
                    if (buf.length() > 0) {
                        buf.append('"');
                    } else {
                        state = IN_QUOTE;
                    }
                    break;
                case ',':
                    if (buf.length() > 0 || (i == 0) || (i > 0 && text.charAt(i - 1) == ',')) {
                        String param = buf.toString().trim();
                        buf.delete(0, buf.length());
                        if (param.length() > 50) {
                            continue;
                        }
                        result.add(param);
                    }
                    break;
                default:
                    buf.append(ch);
                }
            }
        }
        if (buf.length() > 0 || text.charAt(text.length() - 1) == ',') {
            String param = buf.toString().trim();
            buf.delete(0, buf.length());
            if (param.length() > 50) {
            } else {
                result.add(param);
            }
        }
        //
        // return the array list containing the values
        //
        return result;
    }

    private void sleep(Range time) throws InterruptedException {
        int ms = time.rand()*1000;
        log("Sleeping ms:" +ms);
        Thread.sleep(ms);
    }

    private void doChallengePhase() throws InterruptedException {
        log("Waiting challenge event");
        waitOnPhase(ContestConstants.CHALLENGE_PHASE);
        log("challenge event!!");
        sleep(AFTER_E_CHALLENGE);
        if (USER_MUST_CHALLENGE.rand() != 1) {
            return;
        }
        chat("Challenge start...");
        Coder[] coders = currentRoom.getCoders();
        for (int i = 0; i < 3; i++) {
            int defendant = random.nextInt(coders.length);
            CoderComponent[] components = coders[defendant].getComponents();
            int component = random.nextInt(components.length);
            long problemId = components[component].getComponent().getProblem().getProblemID().longValue();
            long componentID = components[component].getComponent().getID().longValue();
            String defendantHandle = coders[defendant].getHandle();
            sleep(BEFORE_CHALLENGE_COMPONENT);
            log("requestChallengeComponent "+componentID);
            requester.requestChallengeComponent(componentID, false, currentRoom.getRoomID().longValue(), defendantHandle);
            synchronized (actionLock) {
                sleep(BEFORE_CHALLENGE);
                log("requestChallenge"+componentID);
                responseWaiter.clear();
                requester.requestChallenge(defendantHandle, componentID, selectExampleTest(problemId, componentID));
                responseWaiter.await(30000);
            }
            sleep(BEFORE_CLOSE);
            log("Closing challenge");
            requester.requestCloseComponent(componentID, defendantHandle);
        }
        sleep(SOME);
        chat("Done...");
    }


    private Solution getSolution(long componentID, int type) {
        Long key = new Long(componentID);
        Solution[] solutions = null;
        if (type == 0) {
            type = random.nextInt(3) + 1;
        }
        if (type == 1) {
            solutions = (Solution[]) correctSolutions.get(key);
        } else if (type == 2) {
            solutions = (Solution[]) incorrectSolutions.get(key);
        } else {
            solutions = (Solution[]) invalidSolutions.get(key);
        }
        return pickSolution(solutions);
    }

    private Solution pickSolution(Solution[] solutions) {
        return solutions[random.nextInt(solutions.length)];
    }

    private void logoff() {
        contestantModel.logoff();
    }


    public void loggingOff() {
        log("loggingOff...");
    }

    public void closingConnectionEvent() {
        log("Closing connection..");
    }

    LeaderListener leaderBoardView =
            new LeaderListener() {
                public void updateLeader(RoomModel room) {
                    log("New leader: " + room.getLeader());
                }
            };

    MenuView menuView =
            new MenuView() {
                public void updatePracticeRounds(Contestant model) {
                }

                public void createLobbyMenu(ArrayList lobbies, ArrayList lobbyStati, ArrayList ids) {
                    log("createLobbyMenu...");
                    log("Lobby rooms:");
                    for (int i = 0; i < lobbies.size(); i++) {
                        log(lobbies.get(i) + " " + lobbyStati.get(i));
                    }
                }

                public void modifyLobbyMenu(String lobby, String status) {
                    log("modifyLobbyMenu: " + lobby + " " + status + "...");
                }

                public void createActiveChatMenu(ArrayList chats, ArrayList chatStati, ArrayList ids) {
                    log("createActiveChatMenu...");
                    log("Moderated Chat rooms:");
                    for (int i = 0; i < chats.size(); i++) {
                        log(chats.get(i) + " " + chatStati.get(i));
                    }
                }

                public void modifyActiveChatMenu(String chat, String status) {
                    log("modifyActiveChatMenu: " + chat + " " + status + "...");
                }
            };

    UserListListener activeUsersView = new UserListListener() {
        public void updateUserList(UserListItem[] items) {
        }
    };

    TeamListView teamsView = new TeamListView() {
        public void updateTeamList(TeamListInfo item) {
        }
    };

    UserListListener registeredUsersView = new UserListListener() {
        public void updateUserList(UserListItem[] items) {
        }

    };

    UserListListener hsRegisteredUsersView = new UserListListener() {
        public void updateUserList(UserListItem[] items) {
        }

    };

    UserListListener availableUsersView = new UserListListener() {
        public void updateUserList(UserListItem[] items) {
        }
    };

    UserListListener memberUsersView = new UserListListener() {
        public void updateUserList(UserListItem[] items) {
        }
    };

    public void lostConnectionEvent() {
        log("lostCon4nection...");
    }

    private void log(Object string) {
        System.out.println("{"+handle+"} "+System.currentTimeMillis()+" ["+Thread.currentThread().getName()+"] "+string);
    }

    private void log(String string, Exception e) {
        log("EXCEPTION : "+string);
        e.printStackTrace(System.out);
        log("EXCEPTION------------------------");
    }


    public void popup(int type, String title, String msg) {
        log("POPUP: " + type + " - " + title + ":\n\t" + msg);
        responseWaiter.set();
    }

    public void popup(int type, int type2, String title,
            String msg, ArrayList al, Object o) {
        log("POPUP: " + type + " - " + title + ":\n\t" + msg);
        responseWaiter.set();
    }


    private RoomViewManager roomManager = new RoomViewManager() {
        public void setCurrentRoom(RoomModel room) {
            currentRoom = room;
        }
	    public void addRoom(RoomModel room) {}
    	public void removeRoom(RoomModel room) {}
    	public void clearRooms() {}
    };


    private void waitOnPhase(int phase) throws InterruptedException {
        long waited = 0;
        while (myRound.getPhase().intValue() < phase && waited <= TIMEOUT_MS) {
            synchronized (phaseLock) {
                phaseLock.wait(PARTIAL_TIMEOUT_MS);
            }
        }
        if (myRound.getPhase().intValue() > phase) {
            throw new IllegalStateException("Missed phase: " + phase + ", current phase: " + myRound.getPhase());
        }
    }

    private PhaseListener phaseListener = new PhaseListener() {
        public void phaseEvent(int phase, RoundModel roundModel) {
            synchronized (phaseLock) {
                phaseLock.notifyAll();
            }
        }

        public void updateSystestProgress(int completed, int total, RoundModel roundModel) {
        }

        public void enableRound(RoundModel round) {
        }

        public void roomListEvent(RoundModel round) {
            synchronized (phaseLock) {
                phaseLock.notifyAll();
            }
        }
    };

    private RoundView roundView = new RoundView() {
        public void setContestantModel(Contestant model) {
        }

        public void updateActiveRoundList(Contestant model) {
        }

        public void clearRoundList() {
        }

    };

    private static String[] getClients(String fileName) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(fileName));
        String s = null;
        Vector r = new Vector();
        while ((s = in.readLine()) != null) {
            r.add(s);
        }
        return (String[]) r.toArray(new String[r.size()]);
    }

    public void vote(VoteResponse voteResponse) {
    }

    public void voteResults(VoteResultsResponse voteResultsResponse) {
    }

    public void roundStatsResponse(RoundStatsResponse roundStatsResponse) {
    }

    public void noBadgeId(NoBadgeIdResponse noBadgeIdResponse) {
    }

    public void wlMyTeamInfoResponse(WLMyTeamInfoResponse wlTeamInfoResponse) {
    }

    public void wlTeamsInfoResponse(WLTeamsInfoResponse wlTeamsInfoResponse) {
    }

    public void visitedPracticeList(CreateVisitedPracticeResponse response) {
    }

    public void importantMessage(ImportantMessageResponse response) {
    }

    public void importantMessageSummry(GetImportantMessagesResponse response) {
    }

    public void practiceSystestResult(PracticeSystemTestResultResponse response) {
    }

    public void startPracticeSystest(PracticeSystemTestResponse response) {
    }

    private static class Range {
        private static Random random = new Random();
        int max;
        int min;

        public Range(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public synchronized int rand() {
            return random.nextInt(max - min)+min;
        }
    }


    private static class Solution {
        int langID;
        int type;
        String code;

        public Solution(int type, int langID, String code) {
            this.type = type;
            this.langID = langID;
            this.code = code;
        }
    }


    public void showSubmissionHistory(SubmissionHistoryResponse response) {
    }

    public void showLongTestResults(LongTestResultsResponse response) {
    }

    public void showProblemStatement(ProblemModel problem) {
    }

    public void loadPlugins() {
    }
    
    public void showCoderHistory(CoderHistoryResponse response) {
    }

}
