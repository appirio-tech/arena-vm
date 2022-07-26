/**
 * @author Michael Cervantes (emcee)
 * @since Apr 30, 2002
 *
 *  */
package com.topcoder.client.contestant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import com.topcoder.client.connectiontype.ConnectionType;
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
import com.topcoder.shared.problem.DataValue;
import com.topcoder.shared.problem.TestCase;


/**
 * Contest Bot created for testing SRM migration to farm architecture. <p>
 *
 * It is base in {@link ContestBot} code.
 *
 * @autor Diego Belfer (Mural)
 * @version $Id: FarmContestBot.java 71574 2008-07-09 20:40:39Z dbelfer $
 */
public class FarmContestBot implements ContestantView, Runnable {
    private static final boolean debug_mode = false;
    private static final int PART_TIME = 480;
    //Number of times to execute an action
    private static double USER_LOAD = Double.parseDouble(System.getProperty("USER_LOAD"));
    
    private static final int[] COMPILE_VALUES = new int[] {1,2,2,2,3,3,5,7,8,10};
    private static final Range COMPILE_COUNT = new Range(0, 9);  //Number of times a component is compiled
    private static final int[] USER_TEST_VALUES = new int[] {2,3,3,4,5};
    private static final Range USER_TEST_COUNT = new Range(0, 4); //Number of tests for each correct compilation


    //Time to wait before executing an action
    private static final Range BEFORE_LOGIN = new Range(1,PART_TIME*USER_LOAD); //4 mins
    //Wait until registration phase (barrier)
    private static final Range BEFORE_REGISTER = new Range(1,PART_TIME*USER_LOAD); //Max time registration 4 minutes
    private static final Range BEFORE_ENTER = new Range(1,PART_TIME*USER_LOAD); //After registering and room assigned. wait
    //Wait until coding phase (barrier)
    private static final Range AFTER_E_CODING = new Range(1,10*USER_LOAD);
    private static final Range BEFORE_OPEN_COMPONENT = new Range(1,30*USER_LOAD);
    private static final Range BEFORE_FIRST_COMPILE = new Range(20,10*60*USER_LOAD);
    private static final Range BETWEEN_COMPILE = new Range(2,120*USER_LOAD);   //1200
    private static final Range BETWEEN_USER_TESTS = new Range(2,10*USER_LOAD); //150
    private static final Range BEFORE_SUBMIT = new Range(5, 20);
    private static final int[] RESUBMITS_VALUES = new int[] {960,992,998,1000};
    private static final Range RESUBMITS_COUNT = new Range(0,1000);
    private static final Range BEFORE_CLOSE = new Range(5,20);
    //Wait until challenge phase (barrier)
    private static final Range AFTER_E_CHALLENGE = new Range(2, 10);
    private static final Range NUM_CHALLENGES = new Range(1,3);
    private static final Range USER_MUST_CHALLENGE = new Range(1,2); //User challenges if 1 is picked (50%)
    private static final Range BEFORE_CHALLENGE_COMPONENT = new Range(5,30*USER_LOAD);
    private static final Range BEFORE_CHALLENGE = new Range(20,120*USER_LOAD); //After viewing the problem
    //Wait until end-contest phase (barrier)
    private static final Range BEFORE_EXIT = new Range(1, 60);

    private static final int[] INCORRECT_LIMIT  = new int[] {90, 70, 40};
    private static final Range INCORRECT_SUBMIT = new Range(1, 100);

    private static final int BETWEEN_EXAMPLE_RATE = 30*60;
    private static final int BETWEEN_NONEXAMPLE_RATE = 4*60*60;
    private static final Range LONG_RESUBMITS_COUNT = new Range(3, 30);
    private static final Range LONG_INVALID_SUBMIT  = new Range(1, 10);
    private static final Range LONG_NONEXAMPLE_SUBMIT = new Range(1, 5);
    private static final Range LONG_AFTER_NONCOMPILABLE_SUBMIT = new Range(5, 120);
    private static final Range LONG_AFTER_EXAMPLE_SUBMIT = new Range(BETWEEN_EXAMPLE_RATE, BETWEEN_EXAMPLE_RATE+600*USER_LOAD);
    private static final Range LONG_AFTER_NONEXAMPLE_SUBMIT = new Range(BETWEEN_EXAMPLE_RATE, BETWEEN_EXAMPLE_RATE+600*USER_LOAD);;

    private static final Range SOME = new Range(1,5);
    private static final long TIMEOUT_MS = 5 * 1000 * 60;
    private static final long PARTIAL_TIMEOUT_MS = 2000;
    private static final String coderPw = "password";
    
    private static final Range cnnType = new Range(0,9);
    private static final ConnectionType[] types = new ConnectionType[] {ConnectionType.DIRECT, ConnectionType.DIRECT,ConnectionType.DIRECT, ConnectionType.DIRECT,ConnectionType.DIRECT,
                                                                        ConnectionType.TUNNEL_CHUNKED, ConnectionType.TUNNEL_CHUNKED, ConnectionType.TUNNEL_CHUNKED,
                                                                        ConnectionType.TUNNEL_NOT_CHUNKED, ConnectionType.TUNNEL_NOT_CHUNKED};

    



    private static Map correctSolutions = new HashMap();
    private static Map incorrectSolutions = new HashMap();
    private static Map invalidSolutions = new HashMap();
    private static Map challenges = new HashMap();
    private static Map factors = new HashMap();
    private static String resourcesFolder;
    private static String host;
    private static int port;
    private static boolean register;



    /*
     * Instance fields
     */
    private String handle;
    private RoomModel currentRoom;
    private RoundModel myRound;
    private long myRoundID;
    private Random random = new Random();
    private Object phaseLock = new Object();
    private ContestantImpl contestantModel;
    private Requester requester;

    private WaitFlag responseWaiter = new WaitFlag();
    private long startTime;
    private long longLastFullSubmit = 0;


    public static void main(String[] args) throws Exception {
        System.out.println("args ->   host port rootpath roundId offset #clients register tunnelLocation");
        host = args[0];
        port = Integer.parseInt(args[1]);
        resourcesFolder = args[2];
        long roundID = Long.parseLong(args[3]);
        int offset = Integer.parseInt(args[4]);
        int numClients = Integer.parseInt(args[5]);
        int maxClientIndex = numClients+offset;
        register = Boolean.parseBoolean(args[6]);
        
        String tunnelLocation = args[7]; //"http://63.118.154.180:8083/servlets-examples/tunnel?host~63.118.154.180&port~5001";
        
        //Build Language solutions factors
        factors.put(new Integer(1), new Integer(30));
        factors.put(new Integer(3), new Integer(60));
        factors.put(new Integer(4), new Integer(9));
        factors.put(new Integer(5), new Integer(1));

        addSolutions(roundID);
        List threads = new ArrayList(numClients);
        String[] clients = getClients(new File(resourcesFolder, "R"+roundID+"/"+"users.txt").getAbsolutePath());
        for (int j = offset; j < maxClientIndex; j++) {
            System.out.println("STARTING THREAD " + j);
            Thread thread = new Thread(new FarmContestBot(roundID, clients[j], tunnelLocation));
            threads.add(thread);
            thread.start();
        }
        for (Iterator it = threads.iterator(); it.hasNext(); ) {
            Thread thread = (Thread) it.next();
            thread.join();
        }
    }

    public FarmContestBot(long myRoundID, String handle, String tunnelLocation) {
        this.myRoundID = myRoundID;
        contestantModel = new ContestantImpl(false);
        // TODO emcee fix this
        contestantModel.init(
                host, port, tunnelLocation, this, activeUsersView, registeredUsersView, hsRegisteredUsersView, mmRegisteredUsersView, teamsView, availableUsersView, memberUsersView, menuView, roomManager, new EventService() {
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
                Object[][] chals = loadChallenges(new File(componentFolder, "challenges-"+componentId+".obj"));
                correctSolutions.put(componentId, correctSols);
                incorrectSolutions.put(componentId, incorrectSols);
                invalidSolutions.put(componentId, invalidSols);
                challenges.put(componentId, chals);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object[][] loadChallenges(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
        if (!file.exists()) return new String[][]{};
        ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
        Object[][] challenges = (Object[][]) is.readObject();
        is.close();
        return challenges;
    }

    private static Solution[] loadSolutions(int type, File folder) throws IOException {
        String[] files = folder.list();
        List sols = new LinkedList();
        for (int i = 0; i < files.length; i++) {
            String file = files[i];
            if (!file.startsWith("S")) continue;
            int languageId = Integer.parseInt(file.substring(1,2));
            Solution solution = new Solution(type, languageId, FileUtil.getStringContents(new File(folder, file))+"\n");
            int factor = ((Integer)factors.get(new Integer(languageId))).intValue();
            for(int j = 0; j < factor; j++) {
                sols.add(solution);
            }
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
                log("BAD ROUND", e);
                return;
            }
            if (register) {
                register();
                if (!myRound.getRoundType().isLongRound()) {
                    return;
                }
            }
            moveToCoderRoom();
            if (myRound.getRoundType().isLongRound()) {
                doLongCodingPhase();
            } else {
                doCodingPhase();
                doChallengePhase();
            }
            log("waiting end contest event");
            waitOnPhase(ContestConstants.CONTEST_COMPLETE_PHASE, true);
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
        ConnectionType connectionType = types[cnnType.rand()];
        log("Connection type "+connectionType);
        synchronized (ConnectionType.DIRECT) {
            contestantModel.setConnectionType(connectionType);
            contestantModel.login(handle, coderPw.toCharArray(), handle);
        }
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


    private boolean register() throws InterruptedException {
        log("Waiting Registration event");
        waitOnPhase(ContestConstants.REGISTRATION_PHASE, false);
        log("Registration event!");
        //sleep(BEFORE_REGISTER);
        log("Registering");
        clearWaiter();
        requester.requestRegister(myRoundID, new ArrayList());
        if (!waitWaiter("register")) {
            log("WARN - Registration response not received");
        }
        return true;
    }

    private void moveToCoderRoom() throws InterruptedException, TimeOutException {
        long waited = 0;
        log("Waiting Almost contest phase");
        while (!myRound.hasCoderRooms() && waited < TIMEOUT_MS) {
            synchronized (phaseLock) {
                phaseLock.wait(PARTIAL_TIMEOUT_MS);
                waited += PARTIAL_TIMEOUT_MS;
            }
        }
        if (!myRound.hasCoderRooms()) {
            throw new TimeOutException("Timed out waiting for rooms...");
        }
        //sleep(BEFORE_ENTER);
        log("Moving to contest room");
        contestantModel.move(ContestConstants.CONTEST_ROOM, myRoundID);
    }


    private void doCodingPhase() throws Exception {
        log("Waiting CODING event");
        waitOnPhase(ContestConstants.CODING_PHASE, true);
        log("CODING event!!!");
        sleep(AFTER_E_CODING);
        chat("Coding start...");
        ProblemModel problems[] = myRound.getProblems(currentRoom.getDivisionID());
        for (int i = 0; i < problems.length; i++) {
            ProblemModel problem = problems[i];
            ProblemComponentModel componentModel = problem.getComponents()[0];
            long problemId = problem.getProblemID().longValue();
            long componentID = componentModel.getID().longValue();
            sleep(BEFORE_OPEN_COMPONENT);
//                requester.requestCoderProblem(problem.getProblemID().longValue());
            log("Opening for coding compId="+componentID);
            requester.requestOpenComponentForCoding(componentID);
            sleep(BEFORE_FIRST_COMPILE);
            int compiles = COMPILE_VALUES[COMPILE_COUNT.rand()];
            for (int z = 0; z < compiles; z++) {
                log("Compile loop "+z+"/"+compiles);
                doPlayWithASolution(problemId, componentID, 0);
            }
            //sleep(BEFORE_SUBMIT);

            int maxResubmits = indexLastGreater(RESUBMITS_VALUES, RESUBMITS_COUNT.rand())+1;
            for (int z = 0; z < maxResubmits; z++) {
                log("Submit loop "+z+"/"+maxResubmits);
                doPlayWithASolution(problemId, componentID, INCORRECT_SUBMIT.rand() > INCORRECT_LIMIT[i] ? 2 : 1);
                log("Submiting component");
                clearWaiter();
                requester.requestSubmitCode(componentID);
                if (!waitWaiter("submit")) {
                    log("WARN - Don't receive response for submit");
                }
            }
            chat("End with component #" + componentID + "...");
            sleep(BEFORE_CLOSE);
            log("Closing component");
            requester.requestCloseComponent(componentID, handle);
        }
        sleep(SOME);
        chat("Done...");
    }

    
    
    private int indexLastGreater(int[] values, int value) {
        for (int i = 0; i < values.length; i++) {
            if (value < values[i]) {
                return i;
            }
        }
        return 0;
    }

    private void doLongCodingPhase() throws Exception {
        log("Waiting CODING event");
        waitOnPhase(ContestConstants.CODING_PHASE, true);
        log("CODING event!!!");
        sleep(AFTER_E_CODING);
        chat("Coding start...");
        ProblemModel problems[] = myRound.getProblems(currentRoom.getDivisionID());
        for (int i = 0; i < problems.length; i++) {
            ProblemModel problem = problems[i];
            ProblemComponentModel componentModel = problem.getComponents()[0];
            //long problemId = problem.getProblemID().longValue();
            long componentID = componentModel.getID().longValue();
            sleep(BEFORE_OPEN_COMPONENT);
//                requester.requestCoderProblem(problem.getProblemID().longValue());
            log("Opening for coding compId="+componentID);
            requester.requestOpenComponentForCoding(componentID);
            sleep(BEFORE_FIRST_COMPILE);
            //sleep(BEFORE_SUBMIT);


            int maxResubmits = LONG_RESUBMITS_COUNT.rand();
            for (int z = 0; z < maxResubmits; z++) {
                log("Submit loop "+z+"/"+maxResubmits);
                int submitType = LONG_INVALID_SUBMIT.rand() == 1 ? 2 : 1;
                Solution solution = getSolution(componentID, submitType);
                boolean example = LONG_NONEXAMPLE_SUBMIT.rand() != 1;
                if (!example && longLastFullSubmit + BETWEEN_NONEXAMPLE_RATE > System.currentTimeMillis()) {
                    log("Cannot full submit, last full submit:"+longLastFullSubmit);
                    example = true;
                }
                clearWaiter();
                requester.requestSubmitLong(componentID, solution.code, solution.langID, example);
                if (!waitWaiter("submit")) {
                    log("WARN - Don't receive response for submit");
                } else {
                    if (!example) {
                        longLastFullSubmit = System.currentTimeMillis();
                    }
                }
                if (submitType == 2) {
                    sleep(LONG_AFTER_NONCOMPILABLE_SUBMIT);
                } else if (example) {
                    sleep(LONG_AFTER_EXAMPLE_SUBMIT);
                } else {
                    sleep(LONG_AFTER_NONEXAMPLE_SUBMIT);
                }
            }
            Solution solution = getSolution(componentID, 1);
            clearWaiter();
            requester.requestSubmitLong(componentID, solution.code, solution.langID, false);
            if (!waitWaiter("submit")) {
                log("WARN - Don't receive response for submit");
            }

            chat("End with component #" + componentID + "...");
            sleep(BEFORE_CLOSE);
            log("Closing component");
            requester.requestCloseComponent(componentID, handle);
        }
        sleep(SOME);
        chat("Done...");
    }
    
    private boolean waitWaiter(String method) throws InterruptedException {
        boolean result = responseWaiter.await(30000);
        long st = System.currentTimeMillis();
        log("TS|"+method+"|"+(st-startTime)+"|"+contestantModel.getConnectionType());
        return result;
    }

    private void clearWaiter() {
        responseWaiter.clear();
        startTime = System.currentTimeMillis();
    }

    private void doPlayWithASolution(long problemId, long componentID, int type) throws Exception {
        Solution solution = null;
        solution = getSolution(componentID, type);
        sleep(BETWEEN_COMPILE);
        log("Compiling solution type="+type);
        clearWaiter();
        requester.requestCompile(solution.code, solution.langID, componentID);

        if (waitWaiter("compile") && solution.type != 3) {
            doSomeUserTests(problemId, componentID);
        } else if (!responseWaiter.isSet()) {
            log("WARN - Don't receive response for compilation");
        }
    }

    private void doSomeUserTests(long problemId, long componentID) throws Exception {
        int userTests = USER_TEST_VALUES[USER_TEST_COUNT.rand()];
        log("User tests "+userTests);
        for (int i = 0; i < userTests; i++) {
            log("User testing "+i+"/"+userTests);
            ArrayList args = selectExampleTest(problemId, componentID);
            if (args != null) {
                if (i > 0) {
                    sleep(BETWEEN_USER_TESTS);
                } else {
                    sleep(SOME);
                }
                clearWaiter();
                requester.requestTest(args, componentID);
                if (!waitWaiter("usertest")) {
                    log("WARN - Don't receive response for userTest");
                }
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

    private ArrayList selectChallengeTest(long problemId, long componentID) throws Exception {
        ProblemModel problem = myRound.getProblem(currentRoom.getDivisionID(), new Long(problemId) );
        ProblemComponentModel model = problem.getComponents()[0];
        Object[][]  chals = (Object[][]) challenges.get(new Long(componentID));
        if (chals == null || chals.length == 0) return null;
        DataValue[] values = DataValue.convertObjectsToDataValues(chals[random.nextInt(chals.length)], model.getParamTypes());
        ArrayList inputArgs = new ArrayList(values.length);
        for (int j = 0; j < values.length; j++) {
            DataValue value = values[j];
            inputArgs.add(value.encode());
        }
        return loadExample(model.getParamTypes(), (String[]) inputArgs.toArray(new String[inputArgs.size()]));
    }


    private ArrayList loadExample(ProblemComponentModel probComponent, int idx)
    {
        DataType[] params = probComponent.getParamTypes();

        //
        // load the specified example
        //
        String[] input = probComponent.getTestCases()[idx].getInput();
        return loadExample(params, input);
    }

    private ArrayList loadExample(DataType[] params, String[] input)
    {
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
    private static final int START = 0;
    private static final int IN_QUOTE = 1;
    private static final int ESCAPE = 2;

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
        int ms = 0;
        if (debug_mode) {
            ms = 1000;
        } else {
            ms = time.rand()*1000;
        }
        log("Sleeping ms:" +ms);
        Thread.sleep(ms);
    }

    private void doChallengePhase() throws Exception {
        log("Waiting challenge event");
        waitOnPhase(ContestConstants.CHALLENGE_PHASE, true);
        log("challenge event!!");
        sleep(AFTER_E_CHALLENGE);
        if (USER_MUST_CHALLENGE.rand() != 1) {
            return;
        }
        chat("Challenge start...");
        Coder[] coders = currentRoom.getCoders();
        if (coders.length > 1) {
            int maxChallenges = NUM_CHALLENGES.rand();
            int retries = maxChallenges*3;

            for (int challengeCount = 0; challengeCount < maxChallenges && retries > 0; retries--) {
                int defendant = random.nextInt(coders.length);
                Coder coder = coders[defendant];
                if (handle.equals(coder.getHandle())) {
                    continue;
                }
                CoderComponent[] components = coder.getComponents();
                if (components.length == 0) {
                    continue;
                }
                int component = random.nextInt(components.length);
                if (components[component].getStatus().intValue() != 130 && components[component].getStatus().intValue() != 131) {
                    continue;
                }
                challengeCount++;
                long problemId = components[component].getComponent().getProblem().getProblemID().longValue();
                long componentID = components[component].getComponent().getID().longValue();
                String defendantHandle = coder.getHandle();
                sleep(BEFORE_CHALLENGE_COMPONENT);
                log("requestChallengeComponent "+componentID);
                requester.requestChallengeComponent(componentID, false, currentRoom.getRoomID().longValue(), defendantHandle);

                sleep(BEFORE_CHALLENGE);
                log("requestChallenge"+componentID);
                clearWaiter();
                ArrayList challengeArgs = selectChallengeTest(problemId, componentID);
                requester.requestChallenge(defendantHandle, componentID, challengeArgs);
                if (!waitWaiter("challenge")) {
                    log("WARN - Don't receive response for challenge");
                }
                sleep(BEFORE_CLOSE);
                log("Closing challenge");
                requester.requestCloseComponent(componentID, defendantHandle);
            }
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
        log("Pick solution type = "+type);
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
    UserListListener mmRegisteredUsersView = new UserListListener() {
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
        if (msg != null && msg.contains("Resubmission")) {
            ArrayList data = new ArrayList();
            data.add(o);
            requester.requestPopupGeneric(ContestConstants.SUBMIT_PROBLEM, 0, data);
            return;
        }
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


    private void waitOnPhase(int phase, boolean strict) throws InterruptedException {
        long waited = 0;
        while (myRound.getPhase().intValue() < phase && waited <= TIMEOUT_MS) {
            synchronized (phaseLock) {
                phaseLock.wait(PARTIAL_TIMEOUT_MS);
            }
        }
        if (myRound.getPhase().intValue() > phase && strict) {
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
            if (max < min) {
                max = min;
            }
            this.max = max;

        }

        public Range(int min, double max) {
            this(min, (int) Math.ceil(max));

        }

        public synchronized int rand() {
            if (max == min) return max;
            return random.nextInt(max - min + 1)+min;
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
