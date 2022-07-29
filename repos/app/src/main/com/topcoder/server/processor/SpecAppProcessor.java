/*
* User: Michael Cervantes
* Date: Aug 31, 2002
* Time: 2:57:28 AM
*/
package com.topcoder.server.processor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.common.BaseCoderComponent;
import com.topcoder.server.common.BaseCodingRoom;
import com.topcoder.server.common.ChallengeAttributes;
import com.topcoder.server.common.Coder;
import com.topcoder.server.common.CoderComponent;
import com.topcoder.server.common.CoderHistory;
import com.topcoder.server.common.LongCoderComponent;
import com.topcoder.server.common.LongContestRoom;
import com.topcoder.server.common.Round;
import com.topcoder.server.common.RoundComponent;
import com.topcoder.server.common.User;
import com.topcoder.server.common.WeakestLinkData;
import com.topcoder.server.common.WeakestLinkRound;
import com.topcoder.server.common.WeakestLinkTeam;
import com.topcoder.server.listener.ListenerInterface;
import com.topcoder.server.services.CoreServices;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.netCommon.messages.Message;
import com.topcoder.shared.netCommon.messages.MessagePacket;
import com.topcoder.shared.netCommon.messages.spectator.CoderData;
import com.topcoder.shared.netCommon.messages.spectator.CoderRoomData;
import com.topcoder.shared.netCommon.messages.spectator.DefineContest;
import com.topcoder.shared.netCommon.messages.spectator.DefineRoom;
import com.topcoder.shared.netCommon.messages.spectator.DefineRound;
import com.topcoder.shared.netCommon.messages.spectator.DefineWeakestLinkTeam;
import com.topcoder.shared.netCommon.messages.spectator.LongProblemEvent;
import com.topcoder.shared.netCommon.messages.spectator.PhaseChange;
import com.topcoder.shared.netCommon.messages.spectator.ProblemData;
import com.topcoder.shared.netCommon.messages.spectator.ProblemEvent;
import com.topcoder.shared.netCommon.messages.spectator.ProblemResult;
import com.topcoder.shared.netCommon.messages.spectator.RoomData;
import com.topcoder.shared.netCommon.messages.spectator.RoomWinner;
import com.topcoder.shared.netCommon.messages.spectator.ShowRoom;
import com.topcoder.shared.netCommon.messages.spectator.ShowWeakestLinkTeam;
import com.topcoder.shared.netCommon.messages.spectator.SpectatorLoginResult;
import com.topcoder.shared.netCommon.messages.spectator.SystemTestHistoryData;
import com.topcoder.shared.netCommon.messages.spectator.TimerUpdate;
import com.topcoder.shared.netCommon.messages.spectator.WeakestLinkElimination;
import com.topcoder.shared.netCommon.messages.spectator.WeakestLinkVote;
import com.topcoder.shared.util.concurrent.ConcurrentHashSet;

class SpecAppProcessor {

    private static final Logger log = Logger.getLogger(SpecAppProcessor.class);

    private ListenerInterface listener;

    private Set connectionSet = new ConcurrentHashSet();
    
    //active, connected forwarders
    private Set forwardingThreads = new ConcurrentHashSet();
    
    //all forwarders
    private Set roundForwarders = new ConcurrentHashSet();

    SpecAppProcessor() {
    }
    
    void addForwarder(String host, int port, String user, String password) {
        String key = "Forwarder." + host + ":" + port;
        synchronized(roundForwarders) {
            for(Iterator i = roundForwarders.iterator(); i.hasNext();) {
                Object o = i.next();
                if(o.toString().equals(key))
                    return;
            }
        }
        
        SpecAppForwardingThread thread = new SpecAppForwardingThread(host, port, this, user, password);
        roundForwarders.add(thread);
        thread.start();
    }
    
    void removeForwarder(String host, int port) {
        String key = "Forwarder." + host + ":" + port;
        SpecAppForwardingThread thread = null;
        synchronized(roundForwarders) {
            for(Iterator i = roundForwarders.iterator(); i.hasNext();) {
                Object o = i.next();
                if(o.toString().equals(key)) {
                    thread = (SpecAppForwardingThread)o;
                    break;
                }
            }
        }
        
        if(thread != null) {
            roundForwarders.remove(thread);
            thread.stop();
        }
    }
    
    void forwarderShutdown(SpecAppForwardingThread thread) {
        forwardingThreads.remove(thread);
    }
    
    void forwarderLoginSuccess(SpecAppForwardingThread thread) {
        MessagePacket snapshotPacket = new MessagePacket();
        MessagePacket openEvents = new MessagePacket();
        MessagePacket submitEvents = new MessagePacket();
        MessagePacket challengeEvents = new MessagePacket();
        MessagePacket systestEvents = new MessagePacket();

        loadSnapshot(snapshotPacket, openEvents, submitEvents, challengeEvents, systestEvents);
        send(thread, snapshotPacket);
        send(thread, openEvents);
        send(thread, submitEvents);
        send(thread, challengeEvents);
        send(thread, systestEvents);
        
        forwardingThreads.add(thread);
        
    }

    void setListener(ListenerInterface listener) {
        this.listener = listener;
    }

//    private List history = Collections.synchronizedList(new LinkedList());

    void spectatorLoginSuccess(Integer connectionID, User user) {
        if (log.isInfoEnabled()) {
            log.info("specLoginSuccess, connectionID = " + connectionID + ", user = " + user);
        }
        try {
            addConnection(connectionID);

            MessagePacket snapshotPacket = new MessagePacket();
            snapshotPacket.add(new SpectatorLoginResult(Integer.toString(user.getID()), RequestProcessor.sealObject(connectionID, "No pass"), true, ""));   // login success

            MessagePacket openEvents = new MessagePacket();
            MessagePacket submitEvents = new MessagePacket();
            MessagePacket challengeEvents = new MessagePacket();
            MessagePacket systestEvents = new MessagePacket();

            loadSnapshot(snapshotPacket, openEvents, submitEvents, challengeEvents, systestEvents);
            send(connectionID, snapshotPacket);
            send(connectionID, openEvents);
            send(connectionID, submitEvents);
            send(connectionID, challengeEvents);
            send(connectionID, systestEvents);
        } catch (Exception e) {
            // Send an error if an exception occurs.
            log.error("Error processing spectator login response", e);
            throw new SpecAppProcessorException(e);
        }
    }

    private void loadSnapshot(MessagePacket definitionPacket, MessagePacket openEvents, MessagePacket submitEvents, MessagePacket challengeEvents, MessagePacket systestEvents) {
        log.info("Loading snapshot for spectator..");
        Round contestRounds[] = getActiveRounds();

        for (int i = 0; i < contestRounds.length; i++) {
            loadRound(contestRounds[i], definitionPacket, openEvents, submitEvents, challengeEvents, systestEvents);
        }

//        synchronized (history) {
//            mp.addAll(history);
//        }
    }

    private void loadRound(Round contestRound, MessagePacket definitionPacket, MessagePacket openEvents, MessagePacket submitEvents, MessagePacket challengeEvents, MessagePacket systestEvents) {
        definitionPacket.add(new DefineContest(contestRound.getContestID(), contestRound.getContestName()));
        definitionPacket.add(new DefineRound(contestRound.getRoundID(), contestRound.getRoundTypeId(), contestRound.getRoundName(), contestRound.getContestID()));
        // Per scoreboard app need to send the define team messages prior to define room messages
        try {
            if (contestRound instanceof WeakestLinkRound) {
                WeakestLinkData wld = CoreServices.loadWeakestLinkData(contestRound.getRoundID());
                WeakestLinkTeam teams[] = wld.getTeams();
                for (int i = 0; i < teams.length; i++) {
                    DefineWeakestLinkTeam dwlt = new DefineWeakestLinkTeam(teams[i].getTeamId(), teams[i].getName(), teams[i].getCoderIds(), contestRound.getRoundID());
                    if (log.isDebugEnabled()) {
                        log.debug("Sending to scoreboard: " + dwlt);
                    }
                    definitionPacket.add(dwlt);
                }
            }
        } catch (Exception e) {
            log.error("Unable to retrieve weakest link team data", e);
        }
        loadRooms(contestRound, definitionPacket, openEvents, submitEvents, challengeEvents, systestEvents);
    }

    private void loadRooms(Round contestRound, MessagePacket definitionPacket, MessagePacket openEventsList, MessagePacket submitEvents, MessagePacket challengeEvents, MessagePacket systestEvents) {
        for (Iterator roomIterator = contestRound.getAllRoomIDs(); roomIterator.hasNext();) {
            Integer roomID = (Integer) roomIterator.next();
            BaseCodingRoom room = getRoom(new Long(roomID.longValue()));
            if (room.isAdminRoom()) {
                continue;
            }
            RoomData roomData = new RoomData(roomID.intValue(), room.getType(), room.getName(), room.getRoundID());
            Iterator roomCoders = room.getAllCoders();
            ArrayList coders = new ArrayList();
            int seed = 1;
            while (roomCoders.hasNext()) {
                Coder coder = (Coder) roomCoders.next();
                CoderRoomData crd = new CoderRoomData(coder.getID(), coder.getName(), coder.getRating(), seed++);
                coders.add(crd);
                loadOpenEvents(contestRound, roomData, coder, openEventsList);
                loadSubmitEvents(contestRound, roomData, coder, submitEvents);
                loadChallengeEvents(contestRound, roomData, coder, challengeEvents);
                loadSystestEvents(roomData, coder, systestEvents);
            }
            int divID = room.getDivisionID();

            ArrayList components = new ArrayList(6);
            ArrayList componentIDs = room.getComponents();
            for (int j = 0; j < componentIDs.size(); j++) {
                int componentID = ((Integer) componentIDs.get(j)).intValue();
                RoundComponent roundComponent = getRoundComponent(contestRound, componentID, divID);
                components.add(new ProblemData(componentID, roundComponent.getPointVal()));
            }

            DefineRoom load = new DefineRoom(roomData, coders, components);
            definitionPacket.add(load);
        }

        definitionPacket.add(new TimerUpdate(getTranslatedEventTime(contestRound.getNextEventTime(), System.currentTimeMillis())));

        definitionPacket.add(getSpectatorPhaseChange(contestRound));
    }

    private void loadOpenEvents(Round contestRound, RoomData roomData, Coder coder, MessagePacket openEvents) {
        if (log.isDebugEnabled()) {
            log.debug("Loading open events for coder " + coder);
        }
        long componentIDs[] = coder.getComponentIDs();
        for (int i = 0; i < componentIDs.length; i++) {
            BaseCoderComponent coderComponent = coder.getComponent(componentIDs[i]);
            if (coderComponent.isOpened()) {
                if (log.isDebugEnabled()) {
                    log.debug("Adding component: " + coderComponent);
                }
                openEvents.add(new ProblemEvent(
                        roomData,
                        ProblemEvent.OPENED,
                        coderComponent.getComponentID(),
                        coder.getName(),
                        coder.getName(),
                        getTranslatedEventTime(contestRound.getCodingEnd(), coderComponent.getOpenedTime())
                ));
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Skipping component: " + coderComponent);
                }
            }
        }
    }

    private void loadSubmitEvents(Round contestRound, RoomData roomData, Coder coder, MessagePacket submitEvents) {
        if (log.isDebugEnabled()) {
            log.debug("Adding submit events for coder: " + coder);
        }
        long componentIDs[] = coder.getComponentIDs();
        for (int i = 0; i < componentIDs.length; i++) {
            BaseCoderComponent coderComponent = coder.getComponent(componentIDs[i]);
            if (coderComponent.isSubmitted()) {
                if (log.isDebugEnabled()) {
                    log.debug("Adding component: " + coderComponent);
                }
                submitEvents.add(new ProblemEvent(
                        roomData,
                        ProblemEvent.SUBMITTING,
                        coderComponent.getComponentID(),
                        coder.getName(),
                        coder.getName(),
                        getTranslatedEventTime(contestRound.getCodingEnd(), coderComponent.getSubmittedTime())
                ));
                //fix me later for regular rounds
                int submissionNumber = 0;
                if(coderComponent instanceof LongCoderComponent) {
                    submissionNumber = ((LongCoderComponent)coderComponent).getSubmissionCount();
                }
                submitEvents.add(new ProblemResult(
                        roomData,
                        ProblemEvent.SUBMITTING,
                        coderComponent.getComponentID(),
                        coder.getName(),
                        coder.getName(),
                        getTranslatedEventTime(contestRound.getCodingEnd(), coderComponent.getSubmittedTime()),
                        ProblemResult.SUCCESSFUL,
                        coderComponent.getSubmittedValue(),
                        coderComponent.getSubmittedProgramText(),
                        coderComponent.getSubmittedLanguage(),
                        submissionNumber
                ));
                
                if(coderComponent instanceof LongCoderComponent) {
                    LongCoderComponent longComp = (LongCoderComponent)coderComponent;
                    //send the processed updated
                    submitEvents.add(new ProblemResult(
                            roomData,
                            ProblemEvent.SUBMITTING,
                            coderComponent.getComponentID(),
                            coder.getName(),
                            coder.getName(),
                            getTranslatedEventTime(contestRound.getCodingEnd(), coderComponent.getSubmittedTime()),
                            ProblemResult.PROCESSED,
                            coderComponent.getSubmittedValue(),
                            coderComponent.getSubmittedProgramText(),
                            coderComponent.getSubmittedLanguage(),
                            submissionNumber
                    ));
                    
                    //send number of submissions
                    submitEvents.add(new LongProblemEvent(
                            roomData,
                            coder.getName(),
                            coderComponent.getComponentID(),
                            longComp.getSubmissionCount(),
                            getTranslatedEventTime(contestRound.getCodingEnd(), longComp.getSubmittedTime()),
                            longComp.getExampleSubmissionCount(),
                            getTranslatedEventTime(contestRound.getCodingEnd(), longComp.getExampleSubmittedTime())
                            ));
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Skipping component: " + coderComponent);
                }
            }
        }
    }

    private void loadChallengeEvents(Round contestRound, RoomData roomData, Coder coder, MessagePacket challengeEvents) {
        if (contestRound.isLongContestRound()) {
            //Not challenges are allowed on Long rounds
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("Loading challenge events for " + coder);
        }
        long componentIDs[] = coder.getComponentIDs();
        for (int i = 0; i < componentIDs.length; i++) {
            CoderComponent coderComponent = (CoderComponent) coder.getComponent(componentIDs[i]);
            if (coderComponent.isChallenged()) {
                if (log.isDebugEnabled()) {
                    log.debug("Adding component: " + coderComponent);
                }
                challengeEvents.add(new ProblemEvent(
                        roomData,
                        ProblemEvent.CHALLENGING,
                        coderComponent.getComponentID(),
                        coder.getName(),
                        coderComponent.getChallenger(),
                        getTranslatedEventTime(contestRound.getChallengeEnd(), coderComponent.getSuccesfullyChallengedTime())
                ));
                
                                
                challengeEvents.add(new ProblemResult(
                        roomData,
                        ProblemEvent.CHALLENGING,
                        coderComponent.getComponentID(),
                        coder.getName(),
                        coderComponent.getChallenger(),
                        getTranslatedEventTime(contestRound.getChallengeEnd(), coderComponent.getSuccesfullyChallengedTime()),
                        ProblemResult.SUCCESSFUL,
                        ContestConstants.EASY_CHALLENGE,
                        coderComponent.getChallengeArgs()
                ));
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Skipping component: " + coderComponent);
                }
            }
        }
        // Start of GT Add - Needed to get Failed Challanges on Spec restart
        ArrayList failedChallenges = (ArrayList) coder.getHistory().getFailedChallanges();
        for (int x = 0; x < failedChallenges.size(); x++) {
            ChallengeAttributes ca = (ChallengeAttributes) failedChallenges.get(x);
            if (log.isDebugEnabled()) {
                log.debug("Adding component: " + ca.getComponentId());
            }
            challengeEvents.add(new ProblemEvent(
                    roomData,
                    ProblemEvent.CHALLENGING,
                    ca.getComponentId(),
                    //coder.getName(),
                    //CoreServices.getUser(ca.getChallengerId()).getName(),
                    CoreServices.getUser(ca.getChallengerId()).getName(),
                    coder.getName(),
                    getTranslatedEventTime(contestRound.getChallengeEnd(), ca.getSubmitTime())
            ));
            challengeEvents.add(new ProblemResult(
                    roomData,
                    ProblemEvent.CHALLENGING,
                    ca.getComponentId(),
                    //coder.getName(),
                    //CoreServices.getUser(ca.getChallengerId()).getName(),
                    CoreServices.getUser(ca.getChallengerId()).getName(),
                    coder.getName(),
                    getTranslatedEventTime(contestRound.getChallengeEnd(), ca.getSubmitTime()),
                    ProblemResult.FAILED,
                    ContestConstants.EASY_CHALLENGE,
                    ca.getArgs()
            ));
        }
        //END of GT ADD

    }

    private void loadSystestEvents(RoomData roomData, Coder coder, MessagePacket systestEvents) {
        log.debug("Loading systest results for room #" + roomData.getRoomID() + ", coder " + coder.getName());
        long componentIDs[] = coder.getComponentIDs();
        for (int i = 0; i < componentIDs.length; i++) {
            BaseCoderComponent coderComponent = coder.getComponent(componentIDs[i]);
            if (coderComponent.isSystemTested()) {
                if (log.isDebugEnabled()) {
                    log.debug("Adding component: " + coderComponent);
                }
                
                //add history items
                for(Iterator it = coder.getHistory().getSystemTests().iterator(); it.hasNext();) {
                    CoderHistory.TestData data = (CoderHistory.TestData)it.next();
                    SystemTestHistoryData hist = new SystemTestHistoryData(
                            roomData,
                            coderComponent.getComponentID(),
                            coder.getName(),
                            data.getTimestamp(),
                            data.getDeductAmt(),
                            data.getProblemVal(),
                            data.getArgs(),
                            data.getResults(),
                            data.isSucceeded());
                    
                    systestEvents.add(hist);
                }

                ProblemResult result = new ProblemResult(
                        roomData,
                        ProblemEvent.SYSTEMTESTING,
                        coderComponent.getComponentID(),
                        coder.getName(),
                        coder.getName(),
                        0,
                        coderComponent.getStatus() == ContestConstants.SYSTEM_TEST_SUCCEEDED ?
                        ProblemResult.SUCCESSFUL : ProblemResult.FAILED,
                        coderComponent.getSubmittedValue()
                );
                if (log.isDebugEnabled()) {
                    log.debug("Adding event: " + result);
                }
                systestEvents.add(result);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Skipping component: " + coderComponent);
                }
            }
        }
    }


    private static int getTranslatedEventTime(Timestamp phaseEnd, long eventTime) {
        if(eventTime == 0)
            return 0;
        if (phaseEnd != null) {
            long left = phaseEnd.getTime() - eventTime;
            return left < 0 ? 0 : (int) left / 1000;
        } else {
            return 0;
        }
    }


    protected RoundComponent getRoundComponent(Round contestRound, int componentID, int divisionID) {
        RoundComponent roundComponent = CoreServices.getRoundComponent(contestRound.getRoundID(), componentID, divisionID);
        return roundComponent;
    }

    protected BaseCodingRoom getRoom(Long roomID) {
        BaseCodingRoom room = (BaseCodingRoom) CoreServices.getRoom(roomID.intValue(), false);
        return room;
    }

    protected Round[] getActiveRounds() {
        return CoreServices.getAllActiveRounds();
    }

    protected Round getRound(long roundID) {
        return CoreServices.getContestRound((int) roundID);
    }

    private void broadcast(Message msg) {
        broadcast(new MessagePacket(msg));
    }

    private void broadcast(MessagePacket mp) {
        Collection connections = null;
        synchronized (connectionSet) {
            connections = new LinkedList(connectionSet);
        }
        for (Iterator iterator = connections.iterator(); iterator.hasNext();) {
            Integer connectionID = (Integer) iterator.next();
            send(connectionID, mp);
        }
        
        //forwarders
        synchronized(forwardingThreads) {
            connections = new LinkedList(forwardingThreads);
        }
        for (Iterator iterator = connections.iterator(); iterator.hasNext();) {
            SpecAppForwardingThread thread = (SpecAppForwardingThread) iterator.next();
            send(thread, mp);
        }
//        history.addAll(mp.getMessages());
    }

    private void send(SpecAppForwardingThread thread, MessagePacket mp) {
        thread.writeObject(mp);
    }
    
    private void send(Integer connectionID, MessagePacket mp) {
        if (listener != null) {
            listener.send(connectionID.intValue(), mp);
        } else {
            throw new IllegalStateException("Null listener");
        }
    }


    void processSystests(Round contestRound, BaseCodingRoom room) {
        log.info("Processing systests for - room: " + room.getRoomID());
        MessagePacket systemTestEvents = new MessagePacket();
        Iterator coders = room.getAllCoders();
        RoomData rd = new RoomData(room.getRoomID(), room.getType(), room.getName(), room.getRoundID());
        while (coders.hasNext()) {
            Coder coder = (Coder) coders.next();
            if (log.isDebugEnabled()) {
                log.debug("Loading systest events for: " + coder);
            }
            loadSystestEvents(rd, coder, systemTestEvents);
        }
        log.info("Sending Spectator end contest message packet: " + systemTestEvents);
        broadcast(systemTestEvents);
    }

    void endContest(Round contestRound) {
        log.info("Sending end contest message");
        broadcast(new PhaseChange(ContestConstants.CONTEST_COMPLETE_PHASE, 0));
    }

    private void addConnection(Integer connectionID) {
        log.info("Adding connection #" + connectionID + " to list of spectators");
        connectionSet.add(connectionID);
    }

    void removeConnection(Integer connectionID) {
        log.info("Removing connection #" + connectionID + " from list of spectators");
        connectionSet.remove(connectionID);
    }

    void processPhaseEvent(Round contestRound) {
        PhaseChange phaseChange = getSpectatorPhaseChange(contestRound);
        log.info("Broadcasting spectator phase change: " + phaseChange);
        broadcast(phaseChange);
    }

    void openComponent(Round round, BaseCodingRoom room, Coder sourceCodeWriter, Coder sourceCodeViewer, BaseCoderComponent component) {
        if (log.isDebugEnabled()) {
            log.debug(
                    "Sending open component event for " +
                    "round #" + round.getRoundID() +
                    ", room #" + room.getRoomID() +
                    ", sourceCodeViewer " + sourceCodeViewer.getName() +
                    ", sourceCodeWriter " + sourceCodeWriter.getName() +
                    ", component #" + component.getComponentID()
            );
        }
        RoomData roomData = getRoomData(room);
        broadcast(new ProblemEvent(roomData, ProblemEvent.OPENED, component.getComponentID(), sourceCodeWriter.getName(), sourceCodeViewer.getName(),
                getTranslatedEventTime(round.getNextEventTime(), System.currentTimeMillis())));
    }


    void closeComponent(Round round, BaseCodingRoom room, Coder sourceCodeWriter, Coder sourceCodeViewer, BaseCoderComponent component) {
        if (log.isDebugEnabled()) {
            log.debug(
                    "Sending close component event for " +
                    "round #" + round.getRoundID() +
                    ", room #" + room.getRoomID() +
                    ", sourceCodeViewer " + sourceCodeViewer.getName() +
                    ", sourceCodeWriter " + sourceCodeWriter.getName() +
                    ", component #" + component.getComponentID()
            );
        }
        RoomData roomData = getRoomData(room);
        broadcast(new ProblemEvent(roomData, ProblemEvent.CLOSED, component.getComponentID(), sourceCodeWriter.getName(), sourceCodeViewer.getName(),
                getTranslatedEventTime(round.getNextEventTime(), System.currentTimeMillis())));
    }
    
    void closeOwnComponent(Round round, BaseCodingRoom room, Coder sourceCodeWriter, int component_id) {
        if (log.isDebugEnabled()) {
            log.debug(
                    "Sending close component event for " +
                    "round #" + round.getRoundID() +
                    ", room #" + room.getRoomID() +
                    ", sourceCodeWriter " + sourceCodeWriter.getName() +
                    ", component #" + component_id
            );
        }
        RoomData roomData = getRoomData(room);
        broadcast(new ProblemEvent(roomData, ProblemEvent.CLOSED, component_id, sourceCodeWriter.getName(), sourceCodeWriter.getName(),
                getTranslatedEventTime(round.getNextEventTime(), System.currentTimeMillis())));
    }


    void compilingComponent(Round round, BaseCodingRoom room, Coder coder, BaseCoderComponent component, String text, int language) {
        if (log.isDebugEnabled()) {
            log.debug(
                    "Sending compile event for " +
                    "round #" + round.getRoundID() +
                    ", room #" + room.getRoomID() +
                    ", coder " + coder.getName() +
                    ", component #" + component.getComponentID()
            );
        }
        //fix me later for regular rounds
        int submissionNumber = 0;
        if(component instanceof LongCoderComponent) {
            submissionNumber = ((LongCoderComponent)component).getSubmissionCount();
        }
        RoomData roomData = getRoomData(room);
        broadcast(new ProblemEvent(roomData, ProblemEvent.COMPILING, component.getComponentID(), coder.getName(), coder.getName(),
                getTranslatedEventTime(round.getCodingEnd(), now()), text, language, submissionNumber));
    }
    
    void processedComponent(Round round,BaseCodingRoom room, Coder coder, LongCoderComponent component, boolean example) {
        if(example) {
            RoomData roomData = getRoomData(room);
            broadcast(new ProblemResult(roomData, ProblemEvent.TESTING, component.getComponentID(), coder.getName(), coder.getName(),
                getTranslatedEventTime(round.getCodingEnd(), now()), ProblemResult.PROCESSED, component.getSubmittedValue(),
                component.getExampleSubmittedProgramText(), component.getExampleSubmittedLanguage(), component.getExampleSubmissionCount()));
        } else {
            RoomData roomData = getRoomData(room);
            broadcast(new ProblemResult(roomData, ProblemEvent.SUBMITTING, component.getComponentID(), coder.getName(), coder.getName(),
                getTranslatedEventTime(round.getCodingEnd(), now()), ProblemResult.PROCESSED, component.getSubmittedValue(),
                component.getSubmittedProgramText(), component.getSubmittedLanguage(), component.getSubmissionCount()));
        }
        
    }
    
    void savingComponent(Round round, BaseCodingRoom room, Coder coder, BaseCoderComponent component, String text, int language) {
        if (log.isDebugEnabled()) {
            log.debug(
                    "Sending save event for " +
                    "round #" + round.getRoundID() +
                    ", room #" + room.getRoomID() +
                    ", coder " + coder.getName() +
                    ", component #" + component.getComponentID()
            );
        }
        RoomData roomData = getRoomData(room);
        //fix me later for regular rounds
        int submissionNumber = 0;
        if(component instanceof LongCoderComponent) {
            submissionNumber = ((LongCoderComponent)component).getSubmissionCount();
        }
        broadcast(new ProblemEvent(roomData, ProblemEvent.SAVING, component.getComponentID(), coder.getName(), coder.getName(),
                getTranslatedEventTime(round.getCodingEnd(), now()), text, language, submissionNumber));
    }

    void compiledComponent(Round round, BaseCodingRoom room, Coder coder, BaseCoderComponent component, boolean succesful) {
        if (log.isDebugEnabled()) {
            log.debug(
                    "Sending compile result for " +
                    "round #" + round.getRoundID() +
                    ", room #" + room.getRoomID() +
                    ", coder " + coder.getName() +
                    ", component #" + component.getComponentID() +
                    ", succesful " + succesful
            );
        }
        RoomData roomData = getRoomData(room);
        broadcast(new ProblemResult(roomData, ProblemEvent.COMPILING, component.getComponentID(), coder.getName(), coder.getName(),
                getTranslatedEventTime(round.getCodingEnd(), now()),
                succesful ? ProblemResult.SUCCESSFUL : ProblemResult.FAILED,
                0
        ));
    }
    
    void updateLongScores(Round round, BaseCodingRoom baseRoom, int componentID) {
            RoomData roomData = getRoomData(baseRoom);
            LongContestRoom room = (LongContestRoom) baseRoom;
            int[] coders = room.getCoderIDs();
            for (int i = 0; i < coders.length; i++) {
                Coder coder = room.getCoder(coders[i]);
                int score = coder.getPoints();
                BaseCoderComponent component = coder.getComponent(componentID);
                //fix me later for regular rounds
                int submissionNumber = 0;
                if(component instanceof LongCoderComponent) {
                    submissionNumber = ((LongCoderComponent)component).getSubmissionCount();
                }
                if(submissionNumber != 0)
                    broadcast(new ProblemResult(roomData, ProblemEvent.SUBMITTING, component.getComponentID(), coder.getName(), coder.getName(),
                        getTranslatedEventTime(round.getCodingEnd(), now()), ProblemResult.SUCCESSFUL, component.getSubmittedValue(),
                        component.getSubmittedProgramText(), component.getSubmittedLanguage(), submissionNumber));
            }
    }

    void submittingLongComponent(Round round, BaseCodingRoom room, Coder coder, BaseCoderComponent component) {
        RoomData roomData = getRoomData(room);
        LongCoderComponent longComp = (LongCoderComponent)component;
        broadcast(new LongProblemEvent(
                            roomData,
                            coder.getName(),
                            component.getComponentID(),
                            longComp.getSubmissionCount(),
                            getTranslatedEventTime(round.getCodingEnd(), longComp.getSubmittedTime()),
                            longComp.getExampleSubmissionCount(),
                            getTranslatedEventTime(round.getCodingEnd(), longComp.getExampleSubmittedTime())
                            ));
    }
    
    void submittingComponent(Round round, BaseCodingRoom room, Coder coder, BaseCoderComponent component) {
        if (log.isDebugEnabled()) {
            log.debug(
                    "Sending submit event for " +
                    "round #" + round.getRoundID() +
                    ", room #" + room.getRoomID() +
                    ", coder " + coder.getName() +
                    ", component #" + component.getComponentID()
            );
        }
        RoomData roomData = getRoomData(room);
        broadcast(new ProblemEvent(roomData, ProblemEvent.SUBMITTING, component.getComponentID(), coder.getName(), coder.getName(),
                getTranslatedEventTime(round.getCodingEnd(), now())));
    }

    void submittedComponent(Round round, BaseCodingRoom room, Coder coder, BaseCoderComponent component) {
        if (log.isDebugEnabled()) {
            log.debug(
                    "Sending submit result for " +
                    "round #" + round.getRoundID() +
                    ", room #" + room.getRoomID() +
                    ", coder " + coder.getName() +
                    ", component #" + component.getComponentID() +
                    ", points " + component.getEarnedPoints()
            );
        }
        //fix me later for regular rounds
        int submissionNumber = 0;
        if(component instanceof LongCoderComponent) {
            submissionNumber = ((LongCoderComponent)component).getSubmissionCount();
        }
        RoomData roomData = getRoomData(room);
        broadcast(new ProblemResult(roomData, ProblemEvent.SUBMITTING, component.getComponentID(), coder.getName(), coder.getName(),
                getTranslatedEventTime(round.getCodingEnd(), now()), ProblemResult.SUCCESSFUL, component.getSubmittedValue(),
                component.getSubmittedProgramText(), component.getSubmittedLanguage(), submissionNumber));
    }

    void testingComponent(Round round, BaseCodingRoom room, Coder coder, BaseCoderComponent component) {
        if (log.isDebugEnabled()) {
            log.debug(
                    "Sending test event for " +
                    "round #" + round.getRoundID() +
                    ", room #" + room.getRoomID() +
                    ", coder " + coder.getName() +
                    ", component #" + component.getComponentID()
            );
        }
        //fix me later for regular rounds
        int submissionNumber = 0;
        String text = "";
        int language = 0;
        if(component instanceof LongCoderComponent) {
            LongCoderComponent comp = ((LongCoderComponent)component);
            submissionNumber = comp.getExampleSubmissionCount();
            text = comp.getExampleSubmittedProgramText();
            language = comp.getExampleSubmittedLanguage();
        }
        RoomData roomData = getRoomData(room);
        broadcast(new ProblemEvent(roomData, ProblemEvent.TESTING, component.getComponentID(), coder.getName(), coder.getName(),
                getTranslatedEventTime(round.getCodingEnd(), now()), text, language, submissionNumber));
    }

    void challengingComponent(Round round, BaseCodingRoom room, Coder sourceCodeWriter, Coder sourceCodeViewer, BaseCoderComponent component) {
        if (log.isDebugEnabled()) {
            log.debug(
                    "Sending challenge event for " +
                    "round #" + round.getRoundID() +
                    ", room #" + room.getRoomID() +
                    ", sourceCodeWriter " + sourceCodeWriter.getName() +
                    ", sourceCodeViewer " + sourceCodeViewer.getName() +
                    ", component #" + component.getComponentID()
            );
        }
        RoomData roomData = getRoomData(room);
        broadcast(new ProblemEvent(
                roomData,
                ProblemEvent.CHALLENGING,
                component.getComponentID(),
                sourceCodeWriter.getName(),
                sourceCodeViewer.getName(),
                getTranslatedEventTime(round.getNextEventTime(), System.currentTimeMillis())));
    }

    void challengedComponent(Round round, BaseCodingRoom room, Coder sourceCodeWriter, Coder sourceCodeViewer, CoderComponent component) {
        if (log.isDebugEnabled()) {
            log.debug(
                    "Sending challenge result for " +
                    "round #" + round.getRoundID() +
                    ", room #" + room.getRoomID() +
                    ", sourceCodeWriter " + sourceCodeWriter.getName() +
                    ", sourceCodeViewer " + sourceCodeViewer.getName() +
                    ", succesful " + component.isChallenged() +
                    ", component #" + component.getComponentID()
            );
        }
        RoomData roomData = getRoomData(room);
        broadcast(new ProblemResult(
                roomData,
                ProblemEvent.CHALLENGING,
                component.getComponentID(),
                sourceCodeWriter.getName(),
                sourceCodeViewer.getName(),
                getTranslatedEventTime(round.getNextEventTime(), System.currentTimeMillis()),
                component.isChallenged() ? ProblemResult.SUCCESSFUL : ProblemResult.FAILED,
                ContestConstants.EASY_CHALLENGE,
                component.getChallengeArgs()
        ));
    }

    private long now() {
        return System.currentTimeMillis();
    }


//    void sendSpectatorContestEvent(long roomID, ContestEvent event) {
//        ContestRoom room = getRoom(new Long(roomID));
//        RoomData rd = getRoomData(room);
//        User user = getUser(event.getUserID());
//        ContestRound contestRound = getRound(room.getRoundID());
//        long now = System.currentTimeMillis();
//
//        switch (event.getAction()) {
//            case ContestEvent.CLOSE_COMPONENT:
//            case ContestEvent.CLEAR_PRACTICER:
////            trace.info("CLEAR_PRACTICER ignored for specs");
//                break;
//            case ContestEvent.SUBMIT_COMPONENT:
//                broadcast(new ProblemResult(rd, ProblemEvent.SUBMITTING, event.getComponentID(), user.getName(), event.getChallengerName(),
//                    getTranslatedEventTime(contestRound.getNextEventTime(),now),ProblemResult.SUCCESSFUL));
//                break;
//            case ContestEvent.CHALLENGE_COMPONENT:
//                //	Processor.sendToSpectators(new ProblemEvent( rd, ProblemEvent.CHALLENGING, event.getProblemID(), user.getName(), event.getChallengerName(),
//                //						     event.getPhaseTimeLeft() ) );
//                break;
//            case ContestEvent.TEST_COMPONENT:
//                broadcast(new ProblemEvent(rd, ProblemEvent.TESTING, event.getProblemID(), user.getName(), event.getChallengerName(),
//                    getTranslatedEventTime(contestRound.getNextEventTime(),now)));
//                break;
//        }
//    }

    private RoomData getRoomData(BaseCodingRoom room) {
        RoomData rd = new RoomData(room.getRoomID(), RoomData.SCOREBOARD, room.getName(), room.getRoundID());
        return rd;
    }

    protected User getUser(long userID) {
        return CoreServices.getUser((int) userID, false);
    }


    private static PhaseChange getSpectatorPhaseChange(Round contestRound) {
        int phaseID = contestRound.getPhase();
        long now = System.currentTimeMillis();
        PhaseChange phaseChange;
        
        switch (phaseID) {
        case ContestConstants.STARTS_IN_PHASE:
            phaseChange = new PhaseChange(phaseID, getTranslatedEventTime(contestRound.getRegistrationStart(), now));
            break;
        case ContestConstants.REGISTRATION_PHASE:
        case ContestConstants.ALMOST_CONTEST_PHASE:
            phaseChange = new PhaseChange(ContestConstants.REGISTRATION_PHASE, getTranslatedEventTime(contestRound.getCodingStart(), now));
            break;
        case ContestConstants.CODING_PHASE:
            phaseChange = new PhaseChange(phaseID, getTranslatedEventTime(contestRound.getCodingEnd(), now));
            break;
        case ContestConstants.INTERMISSION_PHASE:
            phaseChange = new PhaseChange(phaseID, getTranslatedEventTime(contestRound.getChallengeStart(), now));
            break;
        case ContestConstants.CHALLENGE_PHASE:
            phaseChange = new PhaseChange(phaseID, getTranslatedEventTime(contestRound.getChallengeEnd(), now));
            break;
        case ContestConstants.VOTING_PHASE:
            phaseChange = new PhaseChange(phaseID, getTranslatedEventTime(contestRound.getVotingEnd(), now));
            break;
        case ContestConstants.TIE_BREAKING_VOTING_PHASE:
            phaseChange = new PhaseChange(phaseID, getTranslatedEventTime(contestRound.getTieBreakingVotingEnd(), now));
            break;
        case ContestConstants.PENDING_SYSTESTS_PHASE:
        case ContestConstants.SYSTEM_TESTING_PHASE:
            phaseChange = new PhaseChange(ContestConstants.SYSTEM_TESTING_PHASE, 0);
            break;
        case ContestConstants.INACTIVE_PHASE:
        case ContestConstants.CONTEST_COMPLETE_PHASE:
            phaseChange = new PhaseChange(ContestConstants.CONTEST_COMPLETE_PHASE, 0);
            break;
        default:
            throw new IllegalArgumentException("Unknown phase (" + phaseID + ").");
        }
        return phaseChange;
    }

    void timerUpdate() {
        Round contestRounds[] = getActiveRounds();
        for (int i = 0; i < contestRounds.length; i++) {
            Round contestRound = contestRounds[i];
            broadcast(new TimerUpdate(getTranslatedEventTime(contestRound.getNextEventTime(), System.currentTimeMillis())));
        }
    }

    void showRoom(int connectionID, BaseCodingRoom room, int coderIDs[]) {
        log.info("Showing room: " + room.getName() + " on connection " + connectionID);
        RoomData rd = new RoomData(room.getRoomID(), room.getType(), room.getName(), room.getRoundID());
        MessagePacket mp = new MessagePacket(new ShowRoom(rd, coderIDs));
        send(new Integer(connectionID), mp);
    }

    void broadcastShowRoom(BaseCodingRoom room, int coderIDs[]) {
        log.info("Broadcast showing room: " + room.getName());
        RoomData rd = new RoomData(room.getRoomID(), room.getType(), room.getName(), room.getRoundID());
        MessagePacket mp = new MessagePacket(new ShowRoom(rd, coderIDs));
        broadcast(mp);
    }

    void showWeakestLinkTeam(int connectionID, int teamID, int coderIDs[]) {
        log.info("Showing weakest link team: " + teamID + " on connection " + connectionID);
        MessagePacket mp = new MessagePacket(new ShowWeakestLinkTeam(teamID, coderIDs));
        broadcast(mp);
    }

    void assignRooms(Round round) {
        log.info("Procesing room assignments for spectator apps");
        MessagePacket packet = new MessagePacket();
        loadRooms(round, packet, new MessagePacket(), new MessagePacket(), new MessagePacket(), new MessagePacket());
        broadcast(packet);
    }

    void loadRound(Round round) {
        MessagePacket definitionPacket = new MessagePacket();
        MessagePacket openEvents = new MessagePacket();
        MessagePacket submitEvents = new MessagePacket();
        MessagePacket challengeEvents = new MessagePacket();
        MessagePacket systestEvents = new MessagePacket();
        loadRound(round, definitionPacket, openEvents, submitEvents, challengeEvents, systestEvents);
        broadcast(definitionPacket);
    }

    void announceAdvancingCoders(long roundID, int numAdvancing) {
        Round round = getRound(roundID);
        Set allCoders = new TreeSet(new Comparator() {
            public int compare(Object o1, Object o2) {
                Coder c1 = (Coder) o1;
                Coder c2 = (Coder) o2;
                double d = c1.getPoints() - c2.getPoints();
                if (d == 0) {
                    return 0;
                } else {
                    return d < 0 ? 1 : -1;
                }
            }
        });

        for (Iterator roomsIterator = round.getAllRoomIDs(); roomsIterator.hasNext();) {
            BaseCodingRoom room = getRoom(new Long(roomsIterator.next().toString()));
            for (Iterator coderIterator = room.getAllCoders(); coderIterator.hasNext();) {
                allCoders.add(coderIterator.next());
            }
        }

        Iterator coderIterator = allCoders.iterator();
        for (int i = 0; i < numAdvancing && coderIterator.hasNext(); i++) {
            Coder coder = (Coder) coderIterator.next();
            BaseCodingRoom room = getRoom(new Long(coder.getRoomID()));
            broadcast(new RoomWinner(getRoomData(room), new CoderData(coder.getID(), coder.getName(), coder.getRating())));
        }
    }

    void announceWeakestLinkVote(int voterID, int victimID) {
        broadcast(new WeakestLinkVote(voterID, victimID));
    }

    void announceWeakestLinkElimination(int victimID) {
        broadcast(new WeakestLinkElimination(victimID));
    }
}

