/**
 * @author Michael Cervantes (emcee)
 * @since Apr 30, 2002
 *
 * This test case makes a set of assumptions indicated below.
 * If any of these assumptions become bad, please update this
 * test case accordingly.
 *
 * Assumptions:
 * <li>Theres a listener @ 65.112.118.207:5003
 * <li>"td","foo" is a valid admin account
 * <li>"td2","foo" is an ivalid admin account
 * <li>"dmwright","foo" is a valid coder account,
 *        he has rating of 2630,
 *        and he's competed in 77 rated events.
 * <li>
 *  */
package com.topcoder.client.contestant;

import junit.framework.*;

import java.util.*;

import com.topcoder.netCommon.contest.*;
import com.topcoder.netCommon.contestantMessages.*;
import com.topcoder.netCommon.contestantMessages.response.data.*;
import com.topcoder.netCommon.contestantMessages.response.*;
//import com.topcoder.shared.netCommon.messages.*;
import com.topcoder.client.contestant.view.*;
import com.topcoder.client.contestant.message.Requester;
import com.topcoder.client.contestant.impl.ContestantImpl;

public class ContestantTest extends TestCase implements ContestantView {

    public ContestantTest(String name) {
        super(name);
    }


    public static Test suite() {
        return new TestSuite(ContestantTest.class);
    }

    final static String host = "65.112.118.207";
    final static int port = 5003;
    final long TIMEOUT = ContestConstants.TIMEOUT_MILLIS;
    final long PARTIAL_TIMEOUT = TIMEOUT / 30;
    final String coderHandle = "dmwright";
    final String coderPw = "foo";
    final String adminHandle = "dok";
    final String adminPw = "foo";
    final String invalidHandle = "dok2";
    final String invalidPw = "foo2";


    // broadcast tests
    final int nlisteners = 5;
    final String roundName = "Round 1";
    final String message = "testing 1 2 3...";
    final int roundID = 3510;
    final int problemID = 115;
    final String className = "Shaky";
    final String methodSig = "int isShaky(int param1);";
    final String returnType = "int";
    final int division = 1;
    final int pointValue = 250;


    // broadcast instance variables
    Object broadcastLock = new Object();
    Collection broadcasts;
    Object broadcastRefreshLock = new Object();
    AdminBroadcast bc[] = new AdminBroadcast[nlisteners];
    int nreceived;


    // standard instance variables
    ContestantImpl c;

    protected void setUp() {
        c = new ContestantImpl(false);
        // TODO emcee fix this


        c.init(host, port, "", this, activeUsersView, registeredUsersView, registeredUsersView, registeredUsersView, teamsView, availableUsersView, memberUsersView, menuView, null, new EventService() {
            public void invokeLater(Runnable runnable) {
                runnable.run();
            }
        }, "");

        bc = new AdminBroadcast[nlisteners];
        nreceived = 0;
        broadcasts = null;
    }


    protected void tearDown() {
        System.out.println("Tearing down");
        logoff();
    }


    private void login(String username, String pw) {
        login(username, pw, true);
    }

    private void login(String username, String pw, boolean expectSuccess) {
        try {
            c.login(username, pw.toCharArray(), username);
        } catch (LoginException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertEquals(c.isLoggedIn(), expectSuccess);
    }

    private void guestLogin(boolean expectSuccess) {
        try {
            c.guestLogin();
        } catch (LoginException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertEquals(c.isLoggedIn(), expectSuccess);
    }


    public void testSuccesfulLogin() {
        login(adminHandle, adminPw, true);
    }

    public void testUnsuccesfulLogin() {
        login(adminHandle, invalidPw, false);
        logoff();
        login(invalidHandle, adminPw, false);
    }


    UserInfo logoffTestUserInfo;

    public void testUserInfoAdmin() {
        UserInfo full = new UserInfo(adminHandle, false, true, true, 0, 0, -1, false, "");
        UserInfo empty = new UserInfo();
        Assert.assertEquals(empty, c.getUserInfo());
        login(adminHandle, adminPw);
        full.setLastLogin(c.getUserInfo().getLastLogin());
        Assert.assertEquals(full, c.getUserInfo());
        logoffTestUserInfo = full;
        logoff();
        logoffTestUserInfo = null;
        Assert.assertEquals(empty, c.getUserInfo());
    }

    public void testUserInfoCoder() {
        // obviously, if this data changes the tests will fail
        UserInfo full = new UserInfo(coderHandle,
                false, false, false, 0, 77, 2630, false, "");
        UserInfo empty = new UserInfo();
        Assert.assertEquals(empty, c.getUserInfo());
        login(coderHandle, coderPw);
        full.setLastLogin(c.getUserInfo().getLastLogin());
        Assert.assertEquals(full, c.getUserInfo());
//    assertEquals(coderHandle,c.getUserInfo().getHandle());
        logoffTestUserInfo = full;
        logoff();
        logoffTestUserInfo = null;
        Assert.assertEquals(empty, c.getUserInfo());
    }


    public void testUserInfoGuest() {
        UserInfo full = new UserInfo("", true, false, false, 0, 0, 0, false, "");
        UserInfo empty = new UserInfo();
        Assert.assertEquals(empty, c.getUserInfo());
        guestLogin(true);
        Assert.assertTrue(c.getUserInfo().getHandle().startsWith("guest"));
        full.setHandle(c.getUserInfo().getHandle());
        Assert.assertEquals(full, c.getUserInfo());
        logoffTestUserInfo = full;
        logoff();
        logoffTestUserInfo = null;
        Assert.assertEquals(empty, c.getUserInfo());
    }


    private void logoff() {
        c.logoff();
    }





    //
    // Broadcast tests
    //

    class MyBroadcastListener implements BroadcastListener {

        public MyBroadcastListener(int x) {
            this.x = x;
        }

        int x;

        public void newBroadcast(AdminBroadcast _bc) {
            synchronized (broadcastLock) {
                bc[x] = _bc;
                nreceived++;
            }
        }

        public void refreshBroadcasts() {
        }

        public void readBroadcast(AdminBroadcast bc) {
        }
    }

    //
    // Test the administrative broadcasts feature
    //
    public void doBroadcastTest(String type, String msg) throws InterruptedException {
        login(coderHandle, coderPw);
        for (int i = 0; i < nlisteners; i++)
            c.getBroadcastManager().addBroadcastListener(new MyBroadcastListener(i), false);
        ContestantTest admin = new ContestantTest("");
        admin.setUp();
        admin.login(adminHandle, adminPw);
        Requester r = admin.c.getRequester();
        // TODO fix this to use real room ID
        r.requestChatMessage(-1, "/admin broadcast " + type + " " + msg, -1);
        synchronized (broadcastLock) {
            for (long tw = 0;
                 nreceived < nlisteners && tw < TIMEOUT;
                 tw += PARTIAL_TIMEOUT)
                broadcastLock.wait(PARTIAL_TIMEOUT);
        }
        Assert.assertTrue("Expected " + nlisteners + " listeners to be notified of broadcast",
                nreceived == nlisteners);
    }


    public void doGetBroadcastsTest(AdminBroadcast bc) throws InterruptedException {
        c.getBroadcastManager().addBroadcastListener(new BroadcastListener() {
            public void newBroadcast(AdminBroadcast bc) {
            }

            public void refreshBroadcasts() {
                synchronized (broadcastRefreshLock) {
                    broadcasts = c.getBroadcastManager().getBroadcasts();
                }
            }

            public void readBroadcast(AdminBroadcast bc) {
            }
        }, false);
        Requester r = c.getRequester();
        r.requestGetAdminBroadcast();
        synchronized (broadcastRefreshLock) {
            for (long tw = 0;
                 broadcasts == null && tw < TIMEOUT;
                 tw += PARTIAL_TIMEOUT)
                broadcastRefreshLock.wait(PARTIAL_TIMEOUT);
        }
        Assert.assertTrue("Expected broadcast refresh",
                broadcasts != null);
        Assert.assertTrue("Expected broadcast refresh to include " + bc,
                broadcasts.contains(bc));
    }


    public void testGlobalBroadcast() throws InterruptedException {
        AdminBroadcast template = new AdminBroadcast(
                System.currentTimeMillis(),
                message
        );
        doBroadcastTest("global", message);
        System.out.println("Time diff: " + (bc[0].getTime() - template.getTime()));
        template.setTime(bc[0].getTime());
        Assert.assertEquals("Bad broadcast", bc[0], template);
        doGetBroadcastsTest(bc[0]);
    }

    public void testProblemBroadcast() throws InterruptedException {
        ComponentBroadcast template = new ComponentBroadcast(
                0,
                message,
                roundID,
                roundName,
                problemID,
                division,
                pointValue,
                returnType,
                methodSig,
                className
        );

        long time = System.currentTimeMillis();
        doBroadcastTest("problem", "" + problemID + " " + message);
        System.out.println("Time diff: " + (bc[0].getTime() - time));
        template.setTime(bc[0].getTime());
        System.out.println(bc[0]);
        System.out.println(template);
        Assert.assertEquals("Bad broadcast", bc[0], template);
        doGetBroadcastsTest(bc[0]);
    }


    public void testRoundBroadcast() throws InterruptedException {
        RoundBroadcast template = new RoundBroadcast(
                0,
                message,
                roundID,
                roundName
        );
        long time = System.currentTimeMillis();
        doBroadcastTest("round", "" + roundID + " " + message);
        System.out.println("Time diff: " + (bc[0].getTime() - time));
        template.setTime(bc[0].getTime());
        Assert.assertEquals("Bad broadcast", bc[0], template);
        doGetBroadcastsTest(bc[0]);
    }

    public void loggingOff() {
        System.out.println("loggingOff...");
        // Test that the user data is preserved during the logoff process
        if (logoffTestUserInfo != null)
            Assert.assertEquals(logoffTestUserInfo, c.getUserInfo());
    }

    public void closingConnectionEvent() {
        System.out.println("Closing connection..");
    }

    LeaderListener leaderBoardView =
            new LeaderListener() {
                public void updateLeader(RoomModel room) {
                    System.out.println("New leader: " + room.getLeader());
                }
            };

    MenuView menuView =
            new MenuView() {
                ////////////////////////////////////////////////////////////////////////////////
                public void createActiveContestsMenu(ArrayList contests, ArrayList contestStati, ArrayList ids)
                        ////////////////////////////////////////////////////////////////////////////////
                {
                    System.out.println("createActiveContestsMenu...");
                    System.out.println("Active contests:");
                    for (int i = 0; i < contests.size(); i++) {
                        System.out.println(contests.get(i));
                    }
                }

                ////////////////////////////////////////////////////////////////////////////////
                public void createActiveRoomsMenu(ArrayList rooms, ArrayList ids)
                        ////////////////////////////////////////////////////////////////////////////////
                {
                    System.out.println("createActiveRoomsMenu...");
                    System.out.println("Active rooms:");
                    for (int i = 0; i < rooms.size(); i++) {
                        System.out.println(rooms.get(i));
                    }
                }

                public void updatePracticeRounds(Contestant model) {
                }

                ////////////////////////////////////////////////////////////////////////////////
                public void modifyActiveContestsMenu(String contestName, String status)
                        ////////////////////////////////////////////////////////////////////////////////
                {
                    System.out.println("modifyActiveContestsMenu: " + contestName + " " + status + "...");
                }

                ////////////////////////////////////////////////////////////////////////////////
                public void createLobbyMenu(ArrayList lobbies, ArrayList lobbyStati, ArrayList ids)
                        ////////////////////////////////////////////////////////////////////////////////
                {
                    System.out.println("createLobbyMenu...");
                    System.out.println("Lobby rooms:");
                    for (int i = 0; i < lobbies.size(); i++) {
                        System.out.println(lobbies.get(i) + " " + lobbyStati.get(i));
                    }
                }

                ////////////////////////////////////////////////////////////////////////////////
                public void modifyLobbyMenu(String lobby, String status)
                        ////////////////////////////////////////////////////////////////////////////////
                {
                    System.out.println("modifyLobbyMenu: " + lobby + " " + status + "...");
                }

                public void createActiveChatMenu(ArrayList chats, ArrayList chatStati, ArrayList ids) {
                    System.out.println("createActiveChatMenu...");
                    System.out.println("Moderated Chat rooms:");
                    for (int i = 0; i < chats.size(); i++) {
                        System.out.println(chats.get(i) + " " + chatStati.get(i));
                    }
                }

                public void modifyActiveChatMenu(String chat, String status) {
                    System.out.println("modifyActiveChatMenu: " + chat + " " + status + "...");
                }

                public void createActiveRoomsMenu(RoomData[] rooms) {
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

    UserListListener availableUsersView = new UserListListener() {
        public void updateUserList(UserListItem[] items) {
        }
    };

    UserListListener memberUsersView = new UserListListener() {
        public void updateUserList(UserListItem[] items) {
        }
    };

    public void lostConnectionEvent() {
        System.out.println("lostCon4nection...");
    }

    public void popup(int type, String title, String msg) {
        System.out.println("POPUP: " + type + " - " + title + ":\n\t" + msg);
    }

    public void popup(int type, int type2, String title,
            String msg, ArrayList al, Object o) {
        System.out.println("POPUP: " + type + " - " + title + ":\n\t" + msg);
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
    
    public void visitedPracticeResponse(CreateVisitedPracticeResponse response) {
        
    }


    /* (non-Javadoc)
     * @see com.topcoder.client.contestant.view.ContestantView#importantMessage(com.topcoder.netCommon.contestantMessages.response.ImportantMessageResponse)
     */
    public void importantMessage(ImportantMessageResponse response) {
        // TODO Auto-generated method stub
        
    }


    /* (non-Javadoc)
     * @see com.topcoder.client.contestant.view.ContestantView#importantMessageSummry(com.topcoder.netCommon.contestantMessages.response.GetImportantMessagesResponse)
     */
    public void importantMessageSummry(GetImportantMessagesResponse response) {
        // TODO Auto-generated method stub
        
    }


    /* (non-Javadoc)
     * @see com.topcoder.client.contestant.view.ContestantView#loadPlugins()
     */
    public void loadPlugins() {
        // TODO Auto-generated method stub
        
    }


    /* (non-Javadoc)
     * @see com.topcoder.client.contestant.view.ContestantView#practiceSystestResult(com.topcoder.netCommon.contestantMessages.response.PracticeSystemTestResultResponse)
     */
    public void practiceSystestResult(PracticeSystemTestResultResponse response) {
        // TODO Auto-generated method stub
        
    }


    /* (non-Javadoc)
     * @see com.topcoder.client.contestant.view.ContestantView#reconnectFailedEvent()
     */
    public void reconnectFailedEvent() {
        // TODO Auto-generated method stub
        
    }


    /* (non-Javadoc)
     * @see com.topcoder.client.contestant.view.ContestantView#setConnectionStatus(boolean)
     */
    public void setConnectionStatus(boolean on) {
        // TODO Auto-generated method stub
        
    }


    /* (non-Javadoc)
     * @see com.topcoder.client.contestant.view.ContestantView#showLongTestResults(com.topcoder.netCommon.contestantMessages.response.LongTestResultsResponse)
     */
    public void showLongTestResults(LongTestResultsResponse response) {
        // TODO Auto-generated method stub
        
    }


    /* (non-Javadoc)
     * @see com.topcoder.client.contestant.view.ContestantView#showProblemStatement(com.topcoder.client.contestant.ProblemModel)
     */
    public void showProblemStatement(ProblemModel problem) {
        // TODO Auto-generated method stub
        
    }


    /* (non-Javadoc)
     * @see com.topcoder.client.contestant.view.ContestantView#showSubmissionHistory(com.topcoder.netCommon.contestantMessages.response.SubmissionHistoryResponse)
     */
    public void showSubmissionHistory(SubmissionHistoryResponse response) {
        // TODO Auto-generated method stub
        
    }


    /* (non-Javadoc)
     * @see com.topcoder.client.contestant.view.ContestantView#startPracticeSystest(com.topcoder.netCommon.contestantMessages.response.PracticeSystemTestResponse)
     */
    public void startPracticeSystest(PracticeSystemTestResponse response) {
        // TODO Auto-generated method stub
        
    }


    /* (non-Javadoc)
     * @see com.topcoder.client.contestant.view.ContestantView#visitedPracticeList(com.topcoder.netCommon.contestantMessages.response.CreateVisitedPracticeResponse)
     */
    public void visitedPracticeList(CreateVisitedPracticeResponse response) {
        // TODO Auto-generated method stub
        
    }
    
    public void showCoderHistory(CoderHistoryResponse response) {
    }


}
