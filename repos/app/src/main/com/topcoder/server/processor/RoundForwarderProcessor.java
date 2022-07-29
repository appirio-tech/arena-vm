/*
 * RoundForwarderProcessor.java
 *
 * Created on September 28, 2006, 5:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.server.processor;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.CreateChallengeTableResponse;
import com.topcoder.server.common.ActionEvent;
import com.topcoder.server.common.BaseCoderComponent;
import com.topcoder.server.common.BaseCodingRoom;
import com.topcoder.server.common.Coder;
import com.topcoder.server.common.CoderComponent;
import com.topcoder.server.common.CoderFactory;
import com.topcoder.server.common.CoderHistory;
import com.topcoder.server.common.ContestEvent;
import com.topcoder.server.common.ContestRoom;
import com.topcoder.server.common.ForwarderContestRoom;
import com.topcoder.server.common.ForwarderContestRound;
import com.topcoder.server.common.ForwarderRound;
import com.topcoder.server.common.LeaderBoard;
import com.topcoder.server.common.LeaderEvent;
import com.topcoder.server.common.LongCoderComponent;
import com.topcoder.server.common.PhaseEvent;
import com.topcoder.server.common.Rating;
import com.topcoder.server.common.Registration;
import com.topcoder.server.common.Round;
import com.topcoder.server.common.RoundComponent;
import com.topcoder.server.common.RoundFactory;
import com.topcoder.server.common.TCEvent;
import com.topcoder.server.common.User;
import com.topcoder.server.ejb.TestServices.LongCoderHistory;
import com.topcoder.server.ejb.TestServices.LongSubmissionData;
import com.topcoder.server.services.CoreServices;
import com.topcoder.server.services.EventService;
import com.topcoder.shared.netCommon.messages.Message;
import com.topcoder.shared.netCommon.messages.spectator.CoderRoomData;
import com.topcoder.shared.netCommon.messages.spectator.DefineContest;
import com.topcoder.shared.netCommon.messages.spectator.DefineRoom;
import com.topcoder.shared.netCommon.messages.spectator.DefineRound;
import com.topcoder.shared.netCommon.messages.spectator.PhaseChange;
import com.topcoder.shared.netCommon.messages.spectator.ProblemData;
import com.topcoder.shared.netCommon.messages.spectator.ProblemEvent;
import com.topcoder.shared.netCommon.messages.spectator.ProblemResult;
import com.topcoder.shared.netCommon.messages.spectator.SystemTestHistoryData;
import com.topcoder.shared.netCommon.messages.spectator.TimerUpdate;
import com.topcoder.shared.problem.SimpleComponent;
import com.topcoder.shared.util.Formatters;
import com.topcoder.shared.util.logging.Logger;

/**
 *
 * @author rfairfax
 */
public final class RoundForwarderProcessor {
    
    private static Logger trace = Logger.getLogger(RoundForwarderProcessor.class);
    
    /** Creates a new instance of RoundForwarderProcessor */
    private RoundForwarderProcessor() {
    }
    
    public static void dispatchForwardedRequest(Integer connectionID, Message request) {
        if(trace.isDebugEnabled())
            trace.debug("Forwardered to us: " + request);
        
        if(request instanceof DefineContest) {
            defineContest((DefineContest)request);
        } else if(request instanceof DefineRound) {
            defineRound((DefineRound)request, connectionID);
        } else if(request instanceof DefineRoom) {
            defineRoom((DefineRoom)request);
        } else if(request instanceof TimerUpdate) {
            timerUpdate((TimerUpdate)request, connectionID);
        } else if(request instanceof PhaseChange) {
            phaseChange((PhaseChange)request, connectionID);
        } else if(request instanceof SystemTestHistoryData) {
            systestHistoryData((SystemTestHistoryData)request);
        } else if(request instanceof ProblemResult) {
            problemResult((ProblemResult)request, connectionID);
        } else if(request instanceof ProblemEvent) {
            problemEvent((ProblemEvent)request, connectionID);
        } else {
            trace.error("Unknown Request Object: " + request);
        }
    }
    
    private static void systestHistoryData(SystemTestHistoryData data) {
        synchronized(systestLock) {
            systestHistory.add(data);
        }
    }
    
    private static void problemResult(ProblemResult event, Integer connectionID) {
        Round contest = CoreServices.getContestRound(event.getRoom().getRoundID());
        if(contest.isLongContestRound())
            longProblemResult(event, connectionID);
        else
            srmProblemResult(event, connectionID);
    }
    
    private static void longProblemResult(ProblemResult event, Integer connectionID) {
        //need to do: testing, submitting, check for processed in both cases
        if(event.getProblemEventType() == ProblemEvent.TESTING) {
            User u = CoreServices.getUser(event.getProblemWriter());

            BaseCodingRoom room = CoreServices.getContestRoom(event.getRoom().getRoomID(), true);
            try {
                Coder coder = room.getCoder(u.getID());
                LongCoderComponent currentComponent = (LongCoderComponent)coder.getComponent(event.getProblemID());

                String programText = event.getProgramText();
                //This should be always FALSE 
                boolean pending = event.getResult() != ProblemResult.PROCESSED;

                currentComponent.setExampleSubmittedProgramText(programText);
                currentComponent.setExampleSubmittedLanguage(event.getLanguage());
                if(pending) {
                    currentComponent.setExampleSubmittedTime(System.currentTimeMillis());
                    currentComponent.setExampleSubmissionCount(currentComponent.getExampleSubmissionCount() + 1);
                    currentComponent.setStatus(ContestConstants.NOT_CHALLENGED);
                } else {
                    currentComponent.setStatus(ContestConstants.SYSTEM_TEST_SUCCEEDED);
                }
                
                if (room.updateLeader()) {
                    LeaderBoard board = CoreServices.getLeaderBoard(coder.getRoundID(), true);
                    try {
                        board.updateLeader(room);
                    } finally {
                        CoreServices.saveToCache(board.getCacheKey(), board);
                    }
                    EventService.sendGlobalEvent(new LeaderEvent(room));
                }
                
                //add history entry
                updateSubmission(room.getRoundID(), u.getID(), 
                        event.getProgramText(), event.getLanguage(), 0,
                        currentComponent.getExampleSubmissionCount(), true, pending);
                
                if(!pending) {
                    ContestEvent evt = new ContestEvent(room.getRoomID(), 
                        ContestEvent.TEST_COMPLETED, null, u.getID(), 0, 
                        currentComponent.getComponentID(), null);
                    EventService.sendGlobalEvent(evt);
                }
                //ContestEvent event = new ContestEvent(room.getRoomID(), ContestEvent.SCORES_UPDATED, null, RequestProcessor.INVALID_USER, 0, scores.getComponentId(), null);
                //EventService.sendGlobalEvent(event);
            } finally {
                CoreServices.saveToCache(room.getCacheKey(), room);
                CoreServices.releaseLock(room.getCacheKey());
            } 
        } else if(event.getProblemEventType() == ProblemEvent.SUBMITTING) {
            User u = CoreServices.getUser(event.getProblemWriter());

            BaseCodingRoom room = CoreServices.getContestRoom(event.getRoom().getRoomID(), true);
            try {
                Coder coder = room.getCoder(u.getID());
                LongCoderComponent currentComponent = (LongCoderComponent)coder.getComponent(event.getProblemID());

                String programText = event.getProgramText();
                //This can be SUCCESSFUL on submit ok, or when rescored. Processed when tests for the submission ended (pending score)
                boolean pending = event.getResult() != ProblemResult.PROCESSED;

                currentComponent.setSubmittedProgramText(programText);
                currentComponent.setSubmittedLanguage(event.getLanguage());
                if(pending ) { 
                    //new
                    if(event.getSubmissionNumber() > currentComponent.getSubmissionCount()) {
                        currentComponent.setSubmittedTime(System.currentTimeMillis());
                        currentComponent.setSubmissionCount(currentComponent.getSubmissionCount() + 1);
                        currentComponent.setStatus(ContestConstants.NOT_CHALLENGED);
                    } else {
                        pending = currentComponent.getStatus() != ContestConstants.SYSTEM_TEST_SUCCEEDED;
                        currentComponent.setSubmittedValue((int)(event.getResultValue()));
                    }
                } else {
                    currentComponent.setStatus(ContestConstants.SYSTEM_TEST_SUCCEEDED);
                    currentComponent.setSubmittedValue((int)(event.getResultValue()));
                }
                
                if (room.updateLeader()) {
                    LeaderBoard board = CoreServices.getLeaderBoard(coder.getRoundID(), true);
                    try {
                        board.updateLeader(room);
                    } finally {
                        CoreServices.saveToCache(board.getCacheKey(), board);
                    }
                    EventService.sendGlobalEvent(new LeaderEvent(room));
                }
                
                //add history entry
                boolean exist = updateSubmission(room.getRoundID(), u.getID(), 
                        event.getProgramText(), event.getLanguage(), event.getResultValue(),
                        event.getSubmissionNumber(), false, pending);
                
                if(!pending) {
                    ContestEvent evt = new ContestEvent(room.getRoomID(), 
                        ContestEvent.TEST_COMPLETED, null, u.getID(), 0, 
                        currentComponent.getComponentID(), null);
                    EventService.sendGlobalEvent(evt);
                    
                    ContestEvent evt2 = new ContestEvent(room.getRoomID(), ContestEvent.SCORES_UPDATED, null, RequestProcessor.INVALID_USER, 0, currentComponent.getComponentID(), null);
                    EventService.sendGlobalEvent(evt2);
                } else if (!exist) {
                    // FIX: if there is multiple update score message, report only the first one as a submission
                    String message = "System> " + coder.getName() + " has made a full submission\n";
                    int eventType = ContestEvent.SUBMIT_COMPONENT;
                    ContestEvent evt = new ContestEvent(room.getRoomID(), eventType, message, coder.getID(), -1, currentComponent.getComponentID(), null);
                    EventService.sendGlobalEvent(evt);

                }
                
            } finally {
                CoreServices.saveToCache(room.getCacheKey(), room);
                CoreServices.releaseLock(room.getCacheKey());
            } 
        }
    }
    
    private static void srmProblemResult(ProblemResult event, Integer connectionID) {
        if(event.getProblemEventType() == ProblemEvent.COMPILING) {
            User user = CoreServices.getUser(event.getProblemWriter());
            
            BaseCodingRoom room = CoreServices.getContestRoom(event.getRoom().getRoomID(), true);
            try {
                Coder coder = room.getCoder(user.getID());
                BaseCoderComponent coderComponent = coder.getComponent(event.getProblemID());
                Round contestRound = CoreServices.getContestRound(room.getRoundID());

                if (event.getResult() == ProblemResult.SUCCESSFUL) {
                    if (coderComponent.getStatus() == ContestConstants.LOOKED_AT) 
                        coderComponent.setStatus(ContestConstants.COMPILED_UNSUBMITTED);
                }
            } finally {
                CoreServices.saveToCache(room.getCacheKey(), room);
                CoreServices.releaseLock(room.getCacheKey());
            }
        } else if(event.getProblemEventType() == ProblemEvent.SUBMITTING) {
            if(event.getResult() == ProblemResult.SUCCESSFUL) {
                User u = CoreServices.getUser(event.getProblemWriter());
                BaseCodingRoom room = CoreServices.getContestRoom(event.getRoom().getRoomID(), true);
                
                //existingSubmission
                try {
                    Coder coder = room.getCoder(u.getID());
                    BaseCoderComponent coderProblem = coder.getComponent(event.getProblemID());
                    Round contest = CoreServices.getContestRound(event.getRoom().getRoundID());
                    
                    //check history
                    if(coder.getHistory().existingSubmission(String.valueOf(contest.getRoundComponentPointVal(event.getProblemID(), room.getDivisionID())),
                            (int)event.getResultValue())) {
                        return;
                    }                    
                    int totalPoints = 0;
                    long[] componentIDs = coder.getComponentIDs();
                    int numProblems = componentIDs.length;
                    for (int i = 0; i < numProblems; i++) {
                        double points = coder.getComponent(componentIDs[i]).getSubmittedValue();
                        if (coderProblem.getComponentID() != componentIDs[i]) {
                            totalPoints += points;
                        }
                    }

                    coder.setPoints(totalPoints + (int)event.getResultValue()); 
                    coderProblem.setSubmittedValue((int)event.getResultValue()); 
                    coderProblem.setSubmittedProgramText(event.getProgramText());
                    coderProblem.setSubmittedLanguage(event.getLanguage());
                    coderProblem.setSubmittedTime(System.currentTimeMillis());
                    coderProblem.setStatus(ContestConstants.NOT_CHALLENGED);
                    if (room.updateLeader()) {
                        LeaderBoard board = CoreServices.getLeaderBoard(coder.getRoundID(), true);
                        try {
                            board.updateLeader(room);
                        } finally {
                            CoreServices.saveToCache(board.getCacheKey(), board);
                        }
                        EventService.sendGlobalEvent(new LeaderEvent(room));
                    }
                    
                    String trimmedPointVal = Formatters.getDoubleString((int)event.getResultValue());

                    //add history entry
                    coder.getHistory().addSubmission(String.valueOf(contest.getRoundComponentPointVal(event.getProblemID(), room.getDivisionID())),
                            new Date(System.currentTimeMillis()), (int)event.getResultValue());
                    
                    String message = "";
                    message = "System> " + u.getName() + " has submitted the " + contest.getRoundComponentPointVal(event.getProblemID(), room.getDivisionID()) +
                                "-point problem for " + trimmedPointVal + " points.\n";
                    String status = trimmedPointVal + " points";

                    ContestEvent evt = new ContestEvent(room.getRoomID(), ContestEvent.SUBMIT_COMPONENT, message,
                            u.getID(), -1, coderProblem.getComponentID(), status);
                    evt.setTotalPoints(coder.getPoints());
                    evt.setSubmissionPoints(Formatters.getDouble(event.getResultValue()).intValue());

                    evt.setEventTime(System.currentTimeMillis());
                    evt.setLanguage(coderProblem.getLanguage());
                    EventService.sendGlobalEvent(evt);
                } finally {
                    CoreServices.saveToCache(room.getCacheKey(), room);
                    CoreServices.releaseLock(room.getCacheKey());
                }
                
                
            }
        } else if(event.getProblemEventType() == ProblemEvent.CHALLENGING) {
             ContestRoom room = (ContestRoom) CoreServices.getContestRoom(event.getRoom().getRoomID(), true);
             Round round = CoreServices.getContestRound(event.getRoom().getRoundID());
             try {
                User challengerUser = CoreServices.getUser(event.getSourceCoder());
                User defendentUser = CoreServices.getUser(event.getProblemWriter());
                
                Coder challengerCoder = room.getCoder(challengerUser.getID());
                Coder defendantCoder = room.getCoder(defendentUser.getID());
                
                boolean challengeSucceeded = (event.getResult() == ProblemResult.SUCCESSFUL);
            
                CoderComponent challengedComponent = (CoderComponent) defendantCoder.getComponent(event.getProblemID());
                
                //todo: check history
                if(challengerCoder.getHistory().hasChallenged(defendentUser.getID(), event.getProblemID(), event.getArgs()))
                    return;
                
                int coderPointChange = -1 * ContestConstants.EASY_CHALLENGE * 100 / 2;
                int defendantPointChange = challengedComponent.getSubmittedValue();
                int status = ContestConstants.CHALLENGE_FAILED;
                
                if (challengeSucceeded) {
                    coderPointChange = ContestConstants.EASY_CHALLENGE  * 100;
                    defendantCoder.setPoints(defendantCoder.getPoints() - defendantPointChange);
                    status = ContestConstants.CHALLENGE_SUCCEEDED;
                    challengedComponent.setSuccesfullyChallengedTime(System.currentTimeMillis());
                    challengedComponent.setChallenger(challengerCoder.getName());
                } else {
                    defendantPointChange = 0;
                }
                challengerCoder.setPoints(challengerCoder.getPoints() + coderPointChange);
                challengedComponent.setStatus(status);
                
                StringBuffer message = new StringBuffer(challengerCoder.getName());
                if (challengeSucceeded) {
                    message.append(" successfully challenged ");
                } else {
                    message.append(" unsuccessfully challenged ");
                }
                message.append(defendantCoder.getName());
                message.append("'s ");

                message.append(challengedComponent.getPointValue());
                message.append("-point problem.\n");
                
                //add history, TODO: args
                CoderHistory coderHistory = challengerCoder.getHistory();
                CoderHistory defendantHistory = defendantCoder.getHistory();
                
                coderHistory.addChallenge(message.toString(), new java.sql.Date(System.currentTimeMillis()),
                    coderPointChange, challengedComponent.getComponentID(), defendantCoder.getID(), true, new Object[0]);

                defendantHistory.addChallenge(message.toString(), new java.sql.Date(System.currentTimeMillis()),
                    -1 * defendantPointChange, challengedComponent.getComponentID(), challengerCoder.getID(), false, new Object[0]);
                
                ContestEvent evt = new ContestEvent(room.getRoomID(), ContestEvent.CHALLENGE_COMPONENT, "System> " + message.toString(),
                    defendantCoder.getID(), -1, challengedComponent.getComponentID(),
                    challengedComponent.getStatusString());
                evt.setChallengeSuccess(challengeSucceeded);
                evt.setTotalPoints(defendantCoder.getPoints());
                evt.setChallengerTotalPoints(challengerCoder.getPoints());
                evt.setChallengerID(challengerCoder.getID());
                evt.setChallengerName(challengerCoder.getName());
                evt.setEventTime(System.currentTimeMillis());
                EventService.sendGlobalEvent(evt);
                
                // Send out leader update
                if (room.updateLeader()) {
                    LeaderBoard board = CoreServices.getLeaderBoard(round.getRoundID(), true);
                    try {
                        board.updateLeader(room);
                    } finally {
                        CoreServices.saveToCache(board.getCacheKey(), board);
                    }
                    EventService.sendGlobalEvent(new LeaderEvent(room));
                }
                
             } finally {
                 CoreServices.saveToCache(room.getCacheKey(), room);
                 CoreServices.releaseLock(room.getCacheKey());
             }
        } else if(event.getProblemEventType() == ProblemEvent.SYSTEMTESTING) {
            //store the result for later
            synchronized(systestLock) {
                systestResults.add(event);
            }
        } else {
            trace.error("Unknown result type: " + event.getProblemEventType());
        }
    }
    
    private static Object systestLock = new Object();
    private static List systestResults = new ArrayList();
    private static List systestHistory = new ArrayList();
    
    public static void displaySystests() {
        List tests = null;
        List history = null;
        
        List roomsToUpdate = new ArrayList();
        List roundsToUpdate = new ArrayList();
        
        synchronized(systestLock) {
            tests = systestResults;
            history = systestHistory;
            systestHistory = new ArrayList();
            systestResults = new ArrayList();
        }
        
        for(Iterator i = history.iterator(); i.hasNext();) {
            SystemTestHistoryData hist = (SystemTestHistoryData)i.next();
            BaseCodingRoom room = CoreServices.getContestRoom(hist.getRoom().getRoomID(), true);
            try {
                User user = CoreServices.getUser(hist.getCoder());
                Coder coder = room.getCoder(user.getID());
                
                coder.getHistory().addTest(hist.getProblemID(), hist.getTimestamp(), 
                        hist.getDeductAmt(), hist.getProblemVal(), hist.getArgs(), 
                        hist.getResults(), hist.isSucceeded());

                trace.debug("New History Item");
            } finally {
                CoreServices.saveToCache(room.getCacheKey(), room);
                CoreServices.releaseLock(room.getCacheKey());
            }
        }
        
        for(Iterator i = tests.iterator(); i.hasNext();) {
            ProblemResult result = (ProblemResult)i.next();
            BaseCodingRoom room = CoreServices.getContestRoom(result.getRoom().getRoomID(), true);
            if(!roomsToUpdate.contains(new Integer(room.getRoomID()))) {
                roomsToUpdate.add(new Integer(room.getRoomID()));
                if(!roundsToUpdate.contains(new Integer(room.getRoundID()))) {
                    roundsToUpdate.add(new Integer(room.getRoundID()));
                }
            }
            try {
                User user = CoreServices.getUser(result.getProblemWriter());
                Coder coder = room.getCoder(user.getID());
                BaseCoderComponent component = coder.getComponent(result.getProblemID());
                
                //set status, points
                int deductAmount = 0;
                
                if(result.getResult() == ProblemResult.SUCCESSFUL) {
                    component.setStatus(ContestConstants.SYSTEM_TEST_SUCCEEDED);
                } else {
                    component.setStatus(ContestConstants.SYSTEM_TEST_FAILED);
                    deductAmount = -1 * component.getSubmittedValue();
                    component.setSubmittedValue(0);
                }
                coder.setPoints(coder.getPoints() + deductAmount);

                //leaderboard
                
            } finally {
                CoreServices.saveToCache(room.getCacheKey(), room);
                CoreServices.releaseLock(room.getCacheKey());
            }
        }
        
        //send out room updates for every room
        for(Iterator i = roomsToUpdate.iterator(); i.hasNext();) {
            int roomID = ((Integer)i.next()).intValue();
            BaseCodingRoom room = CoreServices.getContestRoom(roomID, false);
            ArrayList allResponses = new ArrayList();

            CreateChallengeTableResponse response = ResponseProcessor.createChallengeTable(room, roomID, room.getType());
            allResponses.add(response);
            Iterator connectionIDs = Processor.getConnectionIDs(TCEvent.ROOM_TARGET, roomID);
            ResponseProcessor.process(connectionIDs, allResponses);
            // Now send message to watch room if necessary
            ArrayList watchConnections = Processor.getWatchConnections(roomID);
            if (watchConnections != null && watchConnections.size() > 0) {
                allResponses = new ArrayList(1);
                response = ResponseProcessor.createChallengeTable(room, roomID, ContestConstants.WATCH_ROOM);
                allResponses.add(response);
                ResponseProcessor.process(watchConnections.iterator(), allResponses);
            }
            
            if (room.updateLeader()) {
                LeaderBoard board = CoreServices.getLeaderBoard(room.getRoundID(), true);
                try {
                    board.updateLeader((BaseCodingRoom) room);
                } finally {
                    CoreServices.saveToCache(board.getCacheKey(), board);
                }
            }
        }
        
        //leaderboard updates
        for(Iterator i = roundsToUpdate.iterator(); i.hasNext();) {
            int roundID = ((Integer)i.next()).intValue();
            
            Round contest = CoreServices.getContestRound(roundID);
            LeaderBoard leaderBoard = CoreServices.getLeaderBoard(contest.getRoundID(), false);

            ArrayList allResponses = new ArrayList();
            allResponses.add(ResponseProcessor.createLeaderBoardResponse(leaderBoard));
            Iterator connectionIDs = Processor.getConnectionIDs(TCEvent.ALL_TARGET, ActionEvent.END_CONTEST);
            ResponseProcessor.process(connectionIDs, allResponses);
        }
        
        //broadcast
        String msg = "The system tester has completed its testing. You can view the final results for any coding room by opening the summary window for that room.";
        try {
            AdminBroadcastManager.getInstance().doBroadcast(msg, ContestConstants.BROADCAST_TYPE_ADMIN_GENERIC, ContestConstants.BROADCAST_ROUND_ALL, -1, -1);
        } catch (Exception e) {
            trace.error("Bad admin broadcast: ", e);
        }
    }
    
    private static void longProblemEvent(ProblemEvent event, Integer connectionID) {
        if(event.getProblemEventType() == ProblemEvent.OPENED) {
            openComponent(event, connectionID, true);
        } else if(event.getProblemEventType() == ProblemEvent.COMPILING) {
            //should not ever happen
        } else if(event.getProblemEventType() == ProblemEvent.CLOSED) {
            //not used by long events
        } else if(event.getProblemEventType() == ProblemEvent.SUBMITTING) {
            //nothing here, wait for the result
        } else if(event.getProblemEventType() == ProblemEvent.CHALLENGING) {
            //nothing here, wait for the result
        } else if(event.getProblemEventType() == ProblemEvent.SAVING) {
            saveComponent(event, connectionID);
        } else if(event.getProblemEventType() == ProblemEvent.TESTING) {
            //test submission started, update text and set status
            User u = CoreServices.getUser(event.getProblemWriter());

            BaseCodingRoom room = CoreServices.getContestRoom(event.getRoom().getRoomID(), true);
            LongCoderComponent currentComponent;
            Coder coder;
            try {
                coder = room.getCoder(u.getID());
                currentComponent = (LongCoderComponent)coder.getComponent(event.getProblemID());

                String programText = event.getProgramText();

                currentComponent.setExampleSubmittedProgramText(programText);
                currentComponent.setExampleSubmittedLanguage(event.getLanguage());
                currentComponent.setExampleSubmittedTime(System.currentTimeMillis());
                currentComponent.setExampleSubmissionCount(currentComponent.getExampleSubmissionCount() + 1);
                currentComponent.setStatus(ContestConstants.NOT_CHALLENGED);
            } finally {
                CoreServices.saveToCache(room.getCacheKey(), room);
                CoreServices.releaseLock(room.getCacheKey());
            } 
            
            if (room.updateLeader()) {
                LeaderBoard board = CoreServices.getLeaderBoard(coder.getRoundID(), true);
                try {
                    board.updateLeader(room);
                } finally {
                    CoreServices.saveToCache(board.getCacheKey(), board);
                }
                EventService.sendGlobalEvent(new LeaderEvent(room));
            }

            //add history entry
            updateSubmission(room.getRoundID(), u.getID(), 
                    event.getProgramText(), event.getLanguage(), 0,
                    currentComponent.getExampleSubmissionCount(), true, true);

            String message = "System> " + coder.getName() + " has made an example submission\n";
            //bug - test doesn't update
            int eventType = ContestEvent.TEST_COMPONENT;
            ContestEvent evt = new ContestEvent(room.getRoomID(), eventType, message, coder.getID(), -1, currentComponent.getComponentID(), null);
            EventService.sendGlobalEvent(evt);

            
        } else {
            trace.error("Unknown problem event");
        }
    }

    //(int number, Date timestamp, int languageId, double score, boolean hasPendingTests)
    private static boolean updateSubmission(int roundId, int coderId, String text, int language, double score, int number, boolean example, boolean pending) {
        trace.info("ADDING " + (example ? "EXAMPLE" : "FULL") + " " + number + "," + language);
        //get history
        LongCoderHistory hist = getCoderHistory(roundId, coderId);
        //get list
        LongSubmissionData[] data = example ? hist.getExampleSubmissions() : hist.getFullSubmissions();
        //find item
        int id = -1;
        for(int i = 0; i < data.length; i++) {
            if(data[i].getNumber() == number) {
                id = i;
                break;
            }
        }
        boolean exist = (id != -1);
        if(id == -1) {
            //resize
            LongSubmissionData[] newData = new LongSubmissionData[data.length+1];
            for(int i = 0; i < data.length; i++) {
                newData[i] = data[i];
            }
            newData[data.length] = new LongSubmissionData(number, new Date(System.currentTimeMillis()),
                    language, score, pending, text);
            id = data.length;
            data = newData;
            if(example)
                hist.setExampleSubmissions(data);
            else
                hist.setFullSubmissions(data);
        }
        
        //update
        // FIX: is it possible that the processed message received first and than the update score message?
        if (data[id].hasPendingTests()) {
            data[id].setHasPendingTests(pending);
        }
        data[id].setScore(score/100.0);
        return exist;
    }
    
    public static LongCoderHistory getCoderHistory(int roundId, int coderId) {
        LongCoderHistory ret = (LongCoderHistory) longCoderHistoryMap.get(roundId + "," + coderId);
        if(ret == null) {
            ret = new LongCoderHistory();
            ret.setExampleSubmissions(new LongSubmissionData[0]);
            ret.setFullSubmissions(new LongSubmissionData[0]);
            longCoderHistoryMap.put(roundId + "," + coderId, ret);
        }
        return ret;
    }
    
    private static void problemEvent(ProblemEvent event, Integer connectionID) {
        Round contest = CoreServices.getContestRound(event.getRoom().getRoundID());
        if(contest.isLongContestRound())
            longProblemEvent(event, connectionID);
        else
            srmProblemEvent(event, connectionID);
    }
    
    private static void openComponent(ProblemEvent event, Integer connectionID, boolean name) {
        //open, same for both
        User user = CoreServices.getUser(event.getSourceCoder());

        if(event.getSourceCoder().equals(event.getProblemWriter())) {
            BaseCodingRoom room = CoreServices.getContestRoom(event.getRoom().getRoomID(), true);
            Round contest = CoreServices.getContestRound(room.getRoundID());
            Coder coder = null;
            boolean update = false;
            try {
                coder = room.getCoder(user.getID());

                if(!coder.isComponentOpened(event.getProblemID())) {
                    update = true;
                    coder.setOpenedComponent(event.getProblemID());
                    CoreServices.saveToCache(room.getCacheKey(), room);
                }

                //TODO: currently opened?
            } finally {
                CoreServices.releaseLock(room.getCacheKey());
            }

            SimpleComponent comp = CoreServices.getSimpleComponent(event.getProblemID());

            BaseCoderComponent coderComponent = coder.getComponent(event.getProblemID());

            // send the event out
            StringBuffer message = new StringBuffer(50);
            message.append("System> ");
            message.append(user.getName());
            message.append(" is opening the ");

            if(name) {
                message.append(comp.getClassName());
                message.append(" problem.\n");
            } else {
                message.append(contest.getRoundComponentPointVal(event.getProblemID(), room.getDivisionID()));
                message.append("-point problem.\n");
            }

            if(update) {
                ContestEvent ce = new ContestEvent(room.getRoomID(), ContestEvent.OPEN_COMPONENT, message.toString(),
                        user.getID(), comp.getProblemID(), comp.getComponentID(), coderComponent.getStatusString());
                ce.setChallengerName(event.getSourceCoder());
                ce.setEventTime(System.currentTimeMillis());
                EventService.sendGlobalEvent(ce);
            } else {
                EventService.sendRoomSystemMessage(event.getRoom().getRoomID(), message.toString());
            }
        } else {
            //chalenge phase
            StringBuffer message = new StringBuffer("System> ");
            message.append(event.getSourceCoder());
            message.append(" is viewing the source of ");
            message.append(event.getProblemWriter());
            message.append("'s ");

            RoundComponent component = CoreServices.getRoundComponent(event.getRoom().getRoundID(), event.getProblemID(), ContestConstants.DIVISION_ONE);
            message.append(component.getPointVal());
            message.append("-point problem.\n");

            EventService.sendRoomSystemMessage(event.getRoom().getRoomID(), message.toString());
        }

    }
    
    private static void saveComponent(ProblemEvent event, Integer connectionID) {
        User u = CoreServices.getUser(event.getProblemWriter());

        BaseCodingRoom room = CoreServices.getContestRoom(event.getRoom().getRoomID(), true);
        try {
            Coder coder = room.getCoder(u.getID());
            BaseCoderComponent currentComponent = coder.getComponent(event.getProblemID());

            String programText = event.getProgramText();

            currentComponent.setProgramText(programText);
            currentComponent.setLanguage(event.getLanguage());
        } finally {
            CoreServices.saveToCache(room.getCacheKey(), room);
            CoreServices.releaseLock(room.getCacheKey());
        } 
    }
    
    private static void srmProblemEvent(ProblemEvent event, Integer connectionID) {
        //FIXME mural review THIS
        if(event.getProblemEventType() == ProblemEvent.OPENED) {
            openComponent(event, connectionID, false);
        } else if(event.getProblemEventType() == ProblemEvent.CLOSED) {
            //TODO: currently opened?
            
            Round contest = CoreServices.getContestRound(event.getRoom().getRoundID());
            
            if(!event.getSourceCoder().equals(event.getProblemWriter())) {
                //need to send out a close message
                User user = CoreServices.getUser(event.getSourceCoder(), false);
                User other = CoreServices.getUser(event.getProblemWriter(), false);
                
                StringBuffer message = new StringBuffer("System> ");
                message.append(user.getName());
                message.append(" closed ");
                
                if (other != null) {
                    message.append(other.getName());
                    message.append("'s ");
                } else {
                    message.append("someone's");
                }
                
                BaseCodingRoom room = CoreServices.getContestRoom(event.getRoom().getRoomID(), false);
                
                message.append(contest.getRoundComponentPointVal(event.getProblemID(), room.getDivisionID()));
                message.append("-point problem.\n");
                
                EventService.sendRoomSystemMessage(event.getRoom().getRoomID(), message.toString());
            }
        } else if(event.getProblemEventType() == ProblemEvent.COMPILING) {
            
            int activePhase;
            User u = CoreServices.getUser(event.getProblemWriter());
            
            BaseCodingRoom room = CoreServices.getContestRoom(event.getRoom().getRoomID(), true);
            try {
                Coder coder = room.getCoder(u.getID());
                BaseCoderComponent currentComponent = coder.getComponent(event.getProblemID());
                
                String programText = event.getProgramText();

                Round contestRound = CoreServices.getContestRound(room.getRoundID());

                activePhase = contestRound.getPhase();
                currentComponent.setProgramText(programText);
                currentComponent.setLanguage(event.getLanguage());
                
                RoundComponent component = CoreServices.getRoundComponent(contestRound.getRoundID(), currentComponent.getComponentID(), coder.getDivisionID());
                if (currentComponent.getStatus() == ContestConstants.LOOKED_AT) {
                    ContestEvent evt = new ContestEvent(room.getRoomID(), ContestEvent.COMPILE_COMPONENT, null,
                            u.getID(), component.getComponent().getProblemID(),
                            component.getComponent().getComponentID(),
                            currentComponent.getStatusString());
                    evt.setChallengerName(coder.getName());
                    evt.setEventTime(System.currentTimeMillis());
                    EventService.sendGlobalEvent(evt);
                }
                
                StringBuffer message = new StringBuffer();
                message.append("System> ");
                message.append(u.getName());
                message.append(" is compiling the ");
                message.append(component.getPointVal());
                message.append("-point problem.\n");

                EventService.sendRoomSystemMessage(event.getRoom().getRoomID(), message.toString());

            } finally {
                CoreServices.saveToCache(room.getCacheKey(), room);
                CoreServices.releaseLock(room.getCacheKey());
            } 
        } else if(event.getProblemEventType() == ProblemEvent.SAVING) {
            saveComponent(event, connectionID);
        } else if(event.getProblemEventType() == ProblemEvent.TESTING) {
            User u = CoreServices.getUser(event.getSourceCoder());
            RoundComponent component = CoreServices.getRoundComponent(event.getRoom().getRoundID(), event.getProblemID(), ContestConstants.DIVISION_ONE);
            
            StringBuffer message = new StringBuffer();
            message.append("System> ");
            message.append(u.getName());
            message.append(" is testing the ");
            message.append(component.getPointVal());
            message.append("-point problem.\n");

            EventService.sendRoomSystemMessage(event.getRoom().getRoomID(), message.toString());
        } else if(event.getProblemEventType() == ProblemEvent.SUBMITTING) {
            //nothing here, wait for the result
        } else if(event.getProblemEventType() == ProblemEvent.CHALLENGING) {
            //nothing here, wait for the result
        } else {
            trace.error("Unknown problem event");
        }
    }
    
    private static void timerUpdate(TimerUpdate timer, Integer connectionID) {
        int roundID = getConnectionRound(connectionID);
        Round cr = CoreServices.getContestRound(roundID, true);
        try {
            ((ForwarderRound)cr).setNextPhase(timer.getTimeLeft());
        } finally {
            CoreServices.saveToCache(cr.getCacheKey(), cr);
            CoreServices.releaseLock(cr.getCacheKey());
        }
        
        //Send out update if not loaded
        boolean update = false;
        
        synchronized(loadedRounds) {
            if(loadedRounds.containsKey(connectionID)) {
                update = true;
            }
        }
        
        if(update) {
            PhaseEvent event = new PhaseEvent(cr.getContestID(), roundID, cr.getPhase(), CoreServices.getLobbyStatus(), false);
            EventService.sendGlobalEvent(event);
        }
    }
    
    private static void phaseChange(PhaseChange phase, Integer connectionID) {
        //Phase Change
        int roundID = getConnectionRound(connectionID);
        Round cr = CoreServices.getContestRound(roundID, true);
        boolean load = false;
        int newPhase = phase.getPhaseID();
        int oldPhase = cr.getPhase();
        
        synchronized(loadedRounds) {
            if(!loadedRounds.containsKey(connectionID)) {
                load = true;
                loadedRounds.put(connectionID, connectionID);
            }
        }
        
        try {
            if(newPhase != oldPhase) {
                switch (newPhase) {
                    case ContestConstants.STARTS_IN_PHASE:
                        cr.beginCountdownPhase();
                        break;
                    case ContestConstants.REGISTRATION_PHASE:
                        cr.beginRegistrationPhase();
                        break;
                    case ContestConstants.ALMOST_CONTEST_PHASE:
                        cr.endRegistrationPhase();
                        break;
                    case ContestConstants.CODING_PHASE:
                        cr.beginCodingPhase();
                        break;
                    case ContestConstants.INTERMISSION_PHASE:
                        cr.endCodingPhase();
                        break;
                    case ContestConstants.CHALLENGE_PHASE:
                        cr.beginChallengePhase();
                        break;
                    case ContestConstants.PENDING_SYSTESTS_PHASE:
                        cr.endChallengePhase();
                        break;
                    case ContestConstants.SYSTEM_TESTING_PHASE:
                        cr.beginTestingPhase();
                        break;
                    case ContestConstants.CONTEST_COMPLETE_PHASE:
                        cr.endTestingPhase();
                        break;
                    case ContestConstants.INACTIVE_PHASE:
                        cr.finishContest();
                        break;
                    default:
                        trace.error("Unknown phase: " + newPhase);
                        //return;
                    }

                //this is total time, may need to set this later
                if(!load)
                    ((ForwarderRound)cr).setNextPhase(phase.getTimeAllocated());
            }
        } finally {
            CoreServices.saveToCache(cr.getCacheKey(), cr);
            CoreServices.releaseLock(cr.getCacheKey());
        }
        
        
        if(!load) {
            if(newPhase == oldPhase)
                return;
            
            if (trace.isDebugEnabled()) {
                trace.debug("Broadcasting Phase Change: " + newPhase);
            }
            PhaseEvent event = new PhaseEvent(cr.getContestID(), roundID, newPhase, CoreServices.getLobbyStatus());
            EventService.sendGlobalEvent(event);
        }
        
        if(load) {
            //setup leaderboard
            CoreServices.setupLeaderBoardIfEmpty(cr);
            
            ResponseProcessor.loadContestRound(Processor.getConnectionIDs(TCEvent.ALL_TARGET, -1), cr);
            
            ResponseProcessor.refreshActiveContestRoomLists();
            
            ActionEvent event = new ActionEvent(TCEvent.ALL_TARGET, cr.getContestID(), ActionEvent.ENABLE_CONTEST);
            event.setRoundID(roundID);
            EventService.sendGlobalEvent(event);

        }
        
    }
    
    private static int getConnectionRound(Integer connectionID) {
        return ((Integer)connectionRounds.get(connectionID)).intValue();
    }
    
    private static void setConnectionRound(Integer connectionID, int roundID) {
        connectionRounds.put(connectionID, new Integer(roundID));
    }
    
    private static final Map connectionRounds = new ConcurrentHashMap();
    
    private static final Map loadedRounds = new ConcurrentHashMap();
    
    private static final Map contestMap = new ConcurrentHashMap();
    
    private static final Map longCoderHistoryMap = new ConcurrentHashMap();
        
    private static void defineContest(DefineContest request) {
        //contests don't have built-in data objects, so we stash the name until
        //DefineRound comes in
        contestMap.put(new Integer(request.getContestID()), request.getContestName());
    }
    
    private static void defineRoom(DefineRoom request) {
        //add / update the room, send out data updates
        
        //get the round
        int roundID = request.getRoom().getRoundID();
        Round cr = CoreServices.getContestRoundByKey(ForwarderContestRound.getCacheKey(roundID),true);
        int contestID = cr.getContestID();
        try {
            //setup components
            List problems = request.getAssignedProblems();
            
            ArrayList points = new ArrayList();
            ArrayList components = new ArrayList();
                
            for(int i = 0; i < problems.size(); i++) {
                ProblemData pd = (ProblemData)problems.get(i);
                
                points.add(new Integer(pd.getPointValue()));
                components.add(new Integer(pd.getProblemID()));
            }
            
            cr.setDivisionComponents(ContestConstants.DIVISION_ONE, components, points);
            
            int roomID = request.getRoom().getRoomID();
            
            BaseCodingRoom room = null;
            room = CoreServices.getContestRoomByKey(ForwarderContestRoom.getCacheKey(roomID), true);
            if(room == null) {
                //create int id, String name, ContestRound contest, int divisionId, int type, int ratingType
                room = new ForwarderContestRoom(roomID, request.getRoom().getRoomTitle(),
                        cr, ContestConstants.DIVISION_ONE, request.getRoom().getRoomType(),
                        cr.isLongContestRound() ? Rating.MM : Rating.ALGO);
            }
            
            //add room
            boolean found = false;
            for(Iterator i = cr.getAllRoomIDs(); i.hasNext();) {
                Integer id = (Integer)i.next();
                if(id.intValue() == roomID) {
                    found = true;
                    break;
                }                
            }
            
            Registration reg = CoreServices.getRegistrationFromCache(roundID, true);
            
            try {
                //add people
                List coders = request.getAssignedCoders();
                for(int i = 0; i < coders.size(); i++) {
                    CoderRoomData c = (CoderRoomData)coders.get(i);
                    if(!room.isUserAssigned(c.getCoderID())) {
                        //add them
                        Coder cdr = CoderFactory.createCoder(c.getCoderID(), c.getHandle(), ContestConstants.DIVISION_ONE,
                                cr,roomID,c.getRank(),1);
                        room.addCoder(cdr);
                        reg.register(CoreServices.getUser(c.getCoderID()));
                    }
                }
            } finally {
                CoreServices.saveToCache(reg.getCacheKey(), reg);
                CoreServices.releaseLock(reg.getCacheKey());
                
                CoreServices.saveToCache(room.getCacheKey(), room);
                if (trace.isDebugEnabled()) {
                    trace.debug("KEY IS: " + room.getCacheKey());
                }
                CoreServices.releaseLock(room.getCacheKey());
            }
            
            if(!found) {
                cr.addRoomID(roomID);
                //trace.debug("ADDING ROOM: " + roomID);
            }
            
        } finally {
            CoreServices.saveToCache(cr.getCacheKey(), cr);
            CoreServices.releaseLock(cr.getCacheKey());
        }
        
        //send out an update to clients
        //ActionEvent event = new ActionEvent(TCEvent.ALL_TARGET, contestID, ActionEvent.FORWARDED_ROUND_UPDATE);
        //event.setRoundID(roundID);
        //EventService.sendGlobalEvent(event);
    }
    
    private static void defineRound(DefineRound request, Integer connectionID) {
        //create a new ContestRound, populate it in the CoreServices cache
        Round newRound = null;
        boolean define = false;
        
        int contestID = request.getContestID();
        int roundID = request.getRoundID();
        
        setConnectionRound(connectionID, roundID);
        
        String roundName = request.getRoundName();
        String contestName = (String)contestMap.get(new Integer(contestID));
        boolean isLongRound = ContestConstants.isLongRoundType(new Integer(request.getRoundType()));
        
        newRound = CoreServices.getContestRoundByKey(ForwarderContestRound.getCacheKey(roundID), true);
        if(newRound == null) {
            define = true;
            newRound = RoundFactory.newRound(contestID, roundID, 
                isLongRound ? ContestConstants.FORWARDER_LONG_ROUND_TYPE_ID : ContestConstants.FORWARDER_ROUND_TYPE_ID, 
                contestName, roundName);
        }
        
        
        ArrayList points = new ArrayList();
        ArrayList components = new ArrayList();
        
        newRound.setDivisionComponents(ContestConstants.DIVISION_ONE, components, points);
        newRound.setDivisionComponents(ContestConstants.DIVISION_TWO, components, points);
        newRound.setDivisionComponents(ContestConstants.DIVISION_ADMIN, components, points);
        
        try {
            CoreServices.saveToCache(newRound.getCacheKey(), newRound);
        } finally {
            CoreServices.releaseLock(newRound.getCacheKey());
        }
        
        //setup registration, if needed
        Registration r = CoreServices.getRegistrationFromCache(roundID, true);
        try {
            if(r == null) {
                r = new Registration(roundID, isLongRound ? Rating.MM : Rating.ALGO);
            }
            CoreServices.saveToCache(r.getCacheKey(), r);
        } finally {
            if(r != null)
                CoreServices.releaseLock(r.getCacheKey());
        }
        CoreServices.addActiveContest(roundID);
        
        //TODO: setup spectator room
        
    }
    
}
