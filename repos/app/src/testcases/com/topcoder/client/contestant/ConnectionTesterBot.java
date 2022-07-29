/**
 * @author Michael Cervantes (emcee)
 * @since Apr 30, 2002
 *
 *  */
package com.topcoder.client.contestant;

import java.util.*;
import java.io.*;

import com.topcoder.netCommon.contest.*;
import com.topcoder.netCommon.contestantMessages.response.data.*;
import com.topcoder.netCommon.contestantMessages.response.*;
//import com.topcoder.netCommon.messages.*;
import com.topcoder.client.contestant.view.*;
import com.topcoder.client.contestant.message.Requester;
import com.topcoder.client.contestant.impl.ContestantImpl;
import com.topcoder.shared.language.*;

public class ConnectionTesterBot implements ContestantView, Runnable {

    private static Object actionLock = new Object();
    private String handle;
    private RoomModel currentRoom;
    private RoundModel myRound;
    private long myRoundID;

    private Object phaseLock = new Object();

    private Requester requester;
    private static Random random = new Random();

    public ConnectionTesterBot(long myRoundID, String handle) {
        this.myRoundID = myRoundID;
        contestantModel = new ContestantImpl(false);
        // TODO emcee fix this
        contestantModel.init(host, port, "", this, activeUsersView, registeredUsersView, registeredUsersView, registeredUsersView, teamsView, availableUsersView, memberUsersView, menuView, roomManager, new EventService() {
            public void invokeLater(Runnable runnable) {
                runnable.run();
            }
        },"");
        contestantModel.getRoundViewManager().addListener(roundView);
        this.handle = handle;
        requester = contestantModel.getRequester();

        //todo
    }


    static String host = "www.topcoder.com";
    static int port = 6001;
    final long TIMEOUT_MS = 5 * 1000 * 60;
    final long PARTIAL_TIMEOUT_MS = 1000;
    final String coderPw = "xtcgz";

    // standard instance variables
    ContestantImpl contestantModel;


    private void login() throws LoginException, TimeOutException {
        contestantModel.login(handle, coderPw.toCharArray(), handle);
        contestantModel.move(ContestConstants.LOBBY_ROOM, ContestConstants.ANY_ROOM);
    }


    public void run() {
        try {
            login();
            System.out.println(new Date() + ": STARTING");
            
            boolean b = true;
            
            while(b) {
                chat("Testing Connectivity");
                sleep(5 * 60 * 1000);
            }
        } catch (Exception e) {
            System.err.println(new Date() + " Exception from client " + e);
            //e.printStackTrace();
        } finally {
            try {
                logoff();
            } catch (Exception e) {
            
            }
        }
    }

    private void chat(String msg) {
        requester.requestChatMessage(currentRoom.getRoomID().intValue(), msg + '\n', ContestConstants.GLOBAL_CHAT_SCOPE);
    }

    private static void sleep(int ms) throws InterruptedException {
        Thread.sleep(ms);
    }

    private void logoff() {
        contestantModel.logoff();
    }


    public void loggingOff() {
        System.out.println(new Date() + " loggingOff...");
    }
    
    public boolean bFirstTime = true;

    public void closingConnectionEvent() {
        if(bFirstTime) {
            bFirstTime = false;
        } else {
            System.out.println(new Date() + " Closing connection..");
        }
    }

    LeaderListener leaderBoardView =
            new LeaderListener() {
                public void updateLeader(RoomModel room) {
                    
                }
            };

    MenuView menuView =
            new MenuView() {
                public void createActiveContestsMenu(ArrayList contests, ArrayList contestStati, ArrayList ids) {
                    
                    
                }

                public void createActiveRoomsMenu(ArrayList rooms, ArrayList ids) {
                    
                }

                public void updatePracticeRounds(Contestant model) {
                }

                public void modifyActiveContestsMenu(String contestName, String status) {
                    
                }

                public void createLobbyMenu(ArrayList lobbies, ArrayList lobbyStati, ArrayList ids) {
                    
                }

                public void modifyLobbyMenu(String lobby, String status) {
                    
                }

                public void createActiveChatMenu(ArrayList chats, ArrayList chatStati, ArrayList ids) {
                    
                    
                    
                }

                public void modifyActiveChatMenu(String chat, String status) {
                    
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
        System.out.println(new Date() + " lostConnection...");
    }

    public void popup(int type, String title, String msg) {
        System.out.println(new Date() + " POPUP: " + type + " - " + title + ":\n\t" + msg);
    }

    public void popup(int type, int type2, String title,
            String msg, ArrayList al, Object o) {
        System.out.println(new Date() + " POPUP: " + type + " - " + title + ":\n\t" + msg);
    }


    private RoomViewManager roomManager = new RoomViewManager() {
        public void setCurrentRoom(RoomModel room) {
            currentRoom = room;
        }
	    public void addRoom(RoomModel room) {}
    	public void removeRoom(RoomModel room) {}
    	public void clearRooms() {}
    };

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

        public void newActiveRound(RoundModel roundModel) {
        }

        public void clearRoundList() {
        }

    };

    public static void main(String[] args) throws Exception {
        host = args[0];
        port = Integer.parseInt(args[1]);
        Thread t;
       
        t = new Thread(new ConnectionTesterBot(0, "google1"));
        t.start();
        boolean quit = false;
        while(!quit) {
            while(t.isAlive()) {
                Thread.sleep(30 * 1000);
            }
            t = new Thread(new ConnectionTesterBot(0, "google1"));
            t.start();
        }
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


    public void startPracticeSystest(PracticeSystemTestResponse response) {
    }


    public void showCoderHistory(CoderHistoryResponse response) {
    }
}
