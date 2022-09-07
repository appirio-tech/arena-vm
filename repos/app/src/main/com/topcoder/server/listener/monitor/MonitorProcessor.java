/*
 * Copyright (C) ~2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.listener.monitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;

import com.topcoder.server.AdminListener.AdminConstants;
import com.topcoder.server.AdminListener.request.AddTimeCommand;
import com.topcoder.server.AdminListener.request.AdvancePhaseRequest;
import com.topcoder.server.AdminListener.request.AdvanceWLCodersRequest;
import com.topcoder.server.AdminListener.request.AllocatePrizesRequest;
import com.topcoder.server.AdminListener.request.AnnounceAdvancingCodersRequest;
import com.topcoder.server.AdminListener.request.ApprovedQuestionCommand;
import com.topcoder.server.AdminListener.request.AssignRoomsCommand;
import com.topcoder.server.AdminListener.request.BanIPCommand;
import com.topcoder.server.AdminListener.request.BootUserCommand;
import com.topcoder.server.AdminListener.request.CancelSystemTestCaseTestingCommand;
import com.topcoder.server.AdminListener.request.ClearPracticeRoomsCommand;
import com.topcoder.server.AdminListener.request.ClearTestCasesCommand;
import com.topcoder.server.AdminListener.request.ClientCommandRequest;
import com.topcoder.server.AdminListener.request.CoderObject;
import com.topcoder.server.AdminListener.request.CoderProblemObject;
import com.topcoder.server.AdminListener.request.ComponentBroadcastCommand;
import com.topcoder.server.AdminListener.request.DisableRoundCommand;
import com.topcoder.server.AdminListener.request.DisconnectRequest;
import com.topcoder.server.AdminListener.request.EnableRoundCommand;
import com.topcoder.server.AdminListener.request.EndContestCommand;
import com.topcoder.server.AdminListener.request.EndHSContestCommand;
import com.topcoder.server.AdminListener.request.GarbageCollectionRequest;
import com.topcoder.server.AdminListener.request.GlobalBroadcastCommand;
import com.topcoder.server.AdminListener.request.LoadRoundRequest;
import com.topcoder.server.AdminListener.request.MonitorSetupRequest;
import com.topcoder.server.AdminListener.request.ProblemObject;
import com.topcoder.server.AdminListener.request.RefreshAllRoomsCommand;
import com.topcoder.server.AdminListener.request.RefreshBroadcastsCommand;
import com.topcoder.server.AdminListener.request.RefreshProbsCommand;
import com.topcoder.server.AdminListener.request.RefreshRegCommand;
import com.topcoder.server.AdminListener.request.RefreshRoomCommand;
import com.topcoder.server.AdminListener.request.RefreshRoomListsCommand;
import com.topcoder.server.AdminListener.request.RefreshRoundCommand;
import com.topcoder.server.AdminListener.request.RegisterUserRequest;
import com.topcoder.server.AdminListener.request.RegistrationObject;
import com.topcoder.server.AdminListener.request.ReplayListenerRequest;
import com.topcoder.server.AdminListener.request.ReplayReceiverRequest;
import com.topcoder.server.AdminListener.request.RestartEventTopicListenerRequest;
import com.topcoder.server.AdminListener.request.RestoreRoundCommand;
import com.topcoder.server.AdminListener.request.RoomObject;
import com.topcoder.server.AdminListener.request.RoundBroadcastCommand;
import com.topcoder.server.AdminListener.request.RoundForwardCommand;
import com.topcoder.server.AdminListener.request.RoundIDCommand;
import com.topcoder.server.AdminListener.request.RoundObject;
import com.topcoder.server.AdminListener.request.SetAdminForwardingAddressRequest;
import com.topcoder.server.AdminListener.request.SetForwardingAddressRequest;
import com.topcoder.server.AdminListener.request.SetUserStatusCommand;
import com.topcoder.server.AdminListener.request.ShowSpecResultsCommand;
import com.topcoder.server.AdminListener.request.ShutdownRequest;
import com.topcoder.server.AdminListener.request.SpecAppShowRoomRequest;
import com.topcoder.server.AdminListener.request.StartSpecAppRotationRequest;
import com.topcoder.server.AdminListener.request.StopSpecAppRotationRequest;
import com.topcoder.server.AdminListener.request.SystemTestCommand;
import com.topcoder.server.AdminListener.request.UnloadRoundRequest;
import com.topcoder.server.AdminListener.request.UnregisterUserRequest;
import com.topcoder.server.AdminListener.request.UpdatePlaceCommand;
import com.topcoder.server.AdminListener.request.UserObject;
import com.topcoder.server.AdminListener.response.CommandFailedResponse;
import com.topcoder.server.AdminListener.response.CommandResponse;
import com.topcoder.server.AdminListener.response.CommandSucceededResponse;
import com.topcoder.server.AdminListener.response.ContestServerResponse;
import com.topcoder.server.common.Round;
import com.topcoder.server.listener.ListenerInterface;
import com.topcoder.server.listener.ListenerMain;
import com.topcoder.server.listener.ProcessorInterface;
import com.topcoder.server.messaging.TopicMessagePublisher;
import com.topcoder.server.processor.AdminBroadcastManager;
import com.topcoder.server.processor.AdminCommands;
import com.topcoder.server.processor.BadBroadcastException;
import com.topcoder.server.services.CoreServices;
import com.topcoder.server.util.TCLinkedQueue;
import com.topcoder.shared.common.ApplicationServer;
import com.topcoder.shared.common.TCContext;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.StoppableThread;
import com.topcoder.shared.util.logging.Logger;

/**
 * Process all Monitor Requests
 * <p>
 * Version 1.1 - Module Assembly - Web Socket Listener - Porting Round Load Related Events
 * <ol>
 *      <li>Updated {@link #send(Object, int, int)} to send response to web if it is web connection</li>
 * </ol>
 * </p>
 * @author  TCSASSEMBLER
 * @version 1.1
 */
public final class MonitorProcessor implements ProcessorInterface, StoppableThread.Client {

    private static final Logger log = Logger.getLogger(MonitorProcessor.class);

    private final Thread shutdownHook;
    private final Collection connections = Collections.synchronizedSet(new HashSet());
    private final MonitorDataHandler dataHandler;
    private final ListenerInterface listener;
    private final StoppableThread thread = new StoppableThread(this, "MonitorProcessor");
    private final TCLinkedQueue queue = new TCLinkedQueue();

    private ListenerInterface controller;
    private boolean isStopping;

    public MonitorProcessor(Thread shutdownHook, MonitorDataHandler dataHandler, ListenerInterface listener) {
        this.shutdownHook = shutdownHook;
        this.dataHandler = dataHandler;
        this.listener = listener;
    }

    private int getConnectionsSize() {
        return connections.size();
    }

    public void setListener(ListenerInterface controller) {
        this.controller = controller;
    }

    public void start() {
        thread.start();
        dataHandler.start();
    }

    public void stop() {
        dataHandler.stop();
        try {
            thread.stopThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void newConnection(int connection_id, String remoteIP) {
        synchronized (connections) {
            connections.add(new Integer(connection_id));
            controller.send(connection_id, new ContestServerResponse(AdminConstants.RECIPIENT_ALL, dataHandler.getFirstResponse()));
        }
    }

    static final int ALL_ADMIN_LISTENERS = -1;

    private class AdminListenerRequest {

        private int connectionID;
        private Object request;

        public AdminListenerRequest(int connectionID, Object request) {
            this.connectionID = connectionID;
            this.request = request;
        }

        public int getConnectionID() {
            return connectionID;
        }

        public Object getRequest() {
            return request;
        }
    }

    public void receive(int connection_id, Object request) {
        queue.put(new AdminListenerRequest(connection_id, request));
    }

    public void cycle() throws InterruptedException {

        int connectionID = -1;
        int recipientID = -1;
        boolean isClientRequest = false;
        try {
            AdminListenerRequest adminListenerRequest = (AdminListenerRequest) queue.take();

            // get connection ID for the admin listener
            // that sent this request
            connectionID = adminListenerRequest.getConnectionID();

            // Extract the actual request and the ID for the monitor client
            Object request = adminListenerRequest.getRequest();
            recipientID = AdminConstants.RECIPIENT_ALL;
            if (request instanceof ClientCommandRequest) {
                ClientCommandRequest clientRequest = (ClientCommandRequest) request;
                recipientID = clientRequest.getSenderId();
                request = clientRequest.getRequestObject();
                isClientRequest = true;
            }

            if (log.isDebugEnabled()) {
                log.debug("Received request of type " + request.getClass().toString());
            }

            // Disregard the request if round ID does not match current round or no round loaded
            if (request instanceof RoundIDCommand) {
                RoundIDCommand rc = (RoundIDCommand) request;
                int roundID = rc.getRoundID();
                Round round = CoreServices.getContestRound(roundID);
                if (round == null) {
                    send(cfr("Command could not be executed because the round does not exist."),
                            connectionID, recipientID);
                    return;
                }
                if (!(request instanceof LoadRoundRequest || request instanceof UpdatePlaceCommand) && !round.isActive()) {
                    send(cfr("Round-specific command could not be executed because it is not for an active contest round."),
                            connectionID, recipientID);
                    return;
                }
            }

            if (request instanceof ShutdownRequest) {
                synchronized (this) {
                    if (!isStopping) {
                        shutdownHook.start();
                        isStopping = true;
                    }
                }
                send(csr("Shutdown request executed"), connectionID, recipientID);
            } else if (request instanceof DisconnectRequest) {
                int connId = ((DisconnectRequest) request).getConnId();
                listener.shutdown(connId, true);
                send(csr("Disconnect client request executed"), connectionID, recipientID);
            } else if (request instanceof MonitorSetupRequest) {
                send(dataHandler.getFirstResponse(), connectionID, recipientID);
            } else if (request instanceof ClearTestCasesCommand) {
                AdminCommands.clearTestCases();
                send(csr("Clear test cases request processed"), connectionID, recipientID);
            } else if (request instanceof RefreshRegCommand) {
                AdminCommands.refreshRegistration();
                send(csr("Refresh registration request processed"), connectionID, recipientID);
            } else if (request instanceof SystemTestCommand) {
                SystemTestCommand command = (SystemTestCommand) request;
                int roundID = command.getRoundID();
                int coderID = command.getCoderID();
                int problemID = command.getProblemID();
                boolean failOnFirstBadTest = command.isFailOnFirstBadTest();
                boolean reference = command.isReference();
                AdminCommands.systemTest(roundID, coderID, problemID, failOnFirstBadTest, reference);

                if(coderID == 0 && problemID == 0 && !reference) {
                    //start queue checker, weblogic breaks this at the moment
                    //new TestingQueueWatcher(connectionID, recipientID).start();
                }

                send(csr("System test request processed"), connectionID, recipientID);
            } else if (request instanceof CancelSystemTestCaseTestingCommand) {
                CancelSystemTestCaseTestingCommand command = (CancelSystemTestCaseTestingCommand) request;
                AdminCommands.cancelSystemTestCaseTesting(command.getRoundID(), command.getTestCaseId());
                send(csr("System test case testing cancelled"), connectionID, recipientID);
            } else if (request instanceof EndContestCommand) {
                AdminCommands.endContest(((EndContestCommand) request).getRoundID());
                send(csr("End contest request processed"), connectionID, recipientID);
            } else if (request instanceof EndHSContestCommand) {
                AdminCommands.endHSContest(((EndHSContestCommand) request).getRoundID());
                send(csr("End HS contest request processed"), connectionID, recipientID);
            } else if (request instanceof UpdatePlaceCommand) {
                AdminCommands.updatePlace(((UpdatePlaceCommand) request).getRoundID());
                send(csr("Update place request processed"), connectionID, recipientID);
            } else if (request instanceof RefreshProbsCommand) {
                int roundID = ((RefreshProbsCommand) request).getRoundID();
                AdminCommands.refreshRoundProblems(roundID);
                send(csr("Refresh round problems request processed"), connectionID, recipientID);
            } else if (request instanceof RefreshRoomCommand) {
                int roomID = ((RefreshRoomCommand) request).getRoomID();
                AdminCommands.refreshRoom(roomID);
                send(csr("Refresh room request processed"), connectionID, recipientID);
            } else if (request instanceof RefreshAllRoomsCommand) {
            	int roundID = ((RefreshAllRoomsCommand) request).getRoundID();
                AdminCommands.refreshAllRooms(roundID);
                send(csr("Refresh all rooms request processed"), connectionID, recipientID);
            } else if (request instanceof RoundForwardCommand) {
                String host = ((RoundForwardCommand)request).getHost();
                int port = ((RoundForwardCommand)request).getPort();
                boolean enable = ((RoundForwardCommand)request).isEnable();
                String user = ((RoundForwardCommand)request).getUser();
                String password = ((RoundForwardCommand)request).getPassword();
                AdminCommands.roundForwarding(host, port, enable, user, password);
                send(csr("Round forwarding thread " + (enable ? "started" : "stopped")), connectionID, recipientID);
            } else if (request instanceof ShowSpecResultsCommand) {
                AdminCommands.showSpecResults();
                send(csr("Results displayed"), connectionID, recipientID);
            } else if (request instanceof RestoreRoundCommand) {
                int roundID = ((RestoreRoundCommand) request).getRoundID();
                AdminCommands.restoreRound(roundID);
                send(csr("Restore round request processed"), connectionID, recipientID);
            } else if (request instanceof GlobalBroadcastCommand) {
                GlobalBroadcastCommand gbc = (GlobalBroadcastCommand) request;
                CommandResponse response =
                        tryGenericBroadcast(gbc.getMessage());
                send(response, connectionID, recipientID);
            } else if (request instanceof ComponentBroadcastCommand) {
                ComponentBroadcastCommand pbc = (ComponentBroadcastCommand) request;
                CommandResponse response =
                        tryComponentBroadcast(pbc.getMessage(), pbc.getRoundID(), pbc.getProblemId());
                send(response, connectionID, recipientID);
            } else if (request instanceof RoundBroadcastCommand) {
                RoundBroadcastCommand rbc = (RoundBroadcastCommand) request;
                CommandResponse response =
                        tryRoundBroadcast(rbc.getMessage(), rbc.getRoundID());
                send(response, connectionID, recipientID);
            } else if (request instanceof RefreshBroadcastsCommand) {
                int roundID = ((RefreshBroadcastsCommand) request).getRoundID();
                if (roundID == 0)
                    roundID = -2;
                CoreServices.refreshBroadcastCache(roundID);
                send(csr("Refresh broadcasts request processed"), connectionID, recipientID);
            } else if (request instanceof AddTimeCommand) {
                AddTimeCommand addTimeCommand = (AddTimeCommand) request;
                int minutes = addTimeCommand.getMinutes();
                int seconds = addTimeCommand.getSeconds();
                int phase = addTimeCommand.getPhase();
                int roundID = addTimeCommand.getRoundID();
                boolean addToStart = addTimeCommand.isAddToStart();
                AdminCommands.addTime(roundID, minutes, seconds, phase, addToStart);
                send(csr("Add time request processed"), connectionID, recipientID);
            } else if (request instanceof AssignRoomsCommand) {
                AssignRoomsCommand assignRoomsCommand = (AssignRoomsCommand) request;
                int codersPerRoom = assignRoomsCommand.getCodersPerRoom();
                int type = assignRoomsCommand.getType();
                boolean isByDivision = assignRoomsCommand.isByDivision();
                boolean isFinal = assignRoomsCommand.isFinal();
                boolean isByRegion = assignRoomsCommand.isByRegion();
                double p = assignRoomsCommand.getP();
                AdminCommands.assignRooms(assignRoomsCommand.getRoundID(), codersPerRoom, type, isByDivision, isFinal, isByRegion, p);
                send(csr("Assign rooms request processed"), connectionID, recipientID);
            } else if (request instanceof SetUserStatusCommand) {
                SetUserStatusCommand setUserStatusCommand = (SetUserStatusCommand) request;
                String handle = setUserStatusCommand.getHandle();
                boolean isActiveStatus = setUserStatusCommand.isActiveStatus();
                AdminCommands.setUserStatus(handle, isActiveStatus);
                send(csr("Set user status request processed"), connectionID, recipientID);
            } else if (request instanceof BanIPCommand) {
                BanIPCommand banIPCommand = (BanIPCommand) request;
                String ipAddress = banIPCommand.getIpAddress();
                listener.banIP(ipAddress);
                send(csr("Ban IP request processed"), connectionID, recipientID);
//            } else if (request instanceof RecalculateScoreRequest) {
//            	RecalculateScoreRequest recalculateScoreCommand = (RecalculateScoreRequest)request;
//
//                send(csr("Recalculate score request processed"), connectionID, recipientID);
            } else if (request instanceof ClearPracticeRoomsCommand) {
                String result = AdminCommands.clearPracticeRooms(((ClearPracticeRoomsCommand)request).getType());
                send(csr(result), connectionID, recipientID);
            } else if (request instanceof EnableRoundCommand) {
                EnableRoundCommand enableRoundCommand = (EnableRoundCommand) request;
                int roundID = enableRoundCommand.getRoundID();
                AdminCommands.enableContestRound(roundID);
                send(csr("Enable round request processed"), connectionID, recipientID);
            } else if (request instanceof DisableRoundCommand) {
                DisableRoundCommand command = (DisableRoundCommand) request;
                int roundID = command.getRoundID();
                AdminCommands.disableContestRound(roundID);
                send(csr("Disable round request processed"), connectionID, recipientID);
            } else if (request instanceof RefreshRoundCommand) {
                RefreshRoundCommand command = (RefreshRoundCommand) request;
                int roundID = command.getRoundID();
                AdminCommands.refreshContestRound(roundID);
                send(csr("Refresh round request processed"), connectionID, recipientID);
            } else if (request instanceof UserObject) {
                UserObject object = (UserObject) request;
                String handle = object.getHandle();
                String cachedObject = CoreServices.getUserStringFromCache(handle);
                sendCachedObjectString(cachedObject, "User, handle=" + handle, connectionID, recipientID);
            } else if (request instanceof RegistrationObject) {
                RegistrationObject object = (RegistrationObject) request;
                int eventID = object.getEventID();
                Object cachedObject = CoreServices.getRegistrationFromCache(eventID);
                sendCachedObject(cachedObject, "Registration, eventID=" + eventID, connectionID, recipientID);
            } else if (request instanceof ProblemObject) {
                ProblemObject object = (ProblemObject) request;
                int problemID = object.getProblemID();
                Object cachedObject = CoreServices.getProblemFromCache(problemID);
                sendCachedObject(cachedObject, "Problem, problemID=" + problemID, connectionID, recipientID);
            } else if (request instanceof RoundObject) {
                RoundObject object = (RoundObject) request;
                int contestID = object.getContestID();
                int roundID = object.getRoundID();
                Object cachedObject = CoreServices.getContestRoundFromCache(roundID);
                sendCachedObject(cachedObject, "ContestRound, contestID=" + contestID + ", roundID=" + roundID, connectionID, recipientID);
            } else if (request instanceof RoomObject) {
                RoomObject object = (RoomObject) request;
                int roomID = object.getRoomID();
                Object cachedObject = CoreServices.getRoomFromCache(roomID);
                sendCachedObject(cachedObject, "Room, roomID=" + roomID, connectionID, recipientID);
            } else if (request instanceof CoderObject) {
                CoderObject object = (CoderObject) request;
                int roomID = object.getRoomID();
                int coderID = object.getCoderID();
                String cachedObject = CoreServices.getCoderStringFromCache(roomID, coderID);
                sendCachedObjectString(cachedObject, "Coder, roomID=" + roomID + ", coderID=" + coderID, connectionID, recipientID);
            } else if (request instanceof CoderProblemObject) {
                throw new UnsupportedOperationException("This needs to be updated to speak in terms of component ID's instead of indices");
//            CoderProblemObject object=(CoderProblemObject) request;
//            int roomID=object.getRoomID();
//            int coderID=object.getCoderID();
//            int problemIndex=object.getProblemIndex();
//            String cachedObject=CoreServices.getCoderProblemStringFromCache(roomID,coderID,problemIndex);
//            sendCachedObjectString(cachedObject,"CoderProblem, roomID="+roomID+", coderID="+coderID+
//                    ", problemIndex="+problemIndex,connectionID,recipientID);
            } else if (request instanceof RefreshRoomListsCommand) {
                RefreshRoomListsCommand command = (RefreshRoomListsCommand) request;
                boolean practice = command.isPractice();
                boolean activeContest = command.isActiveContest();
                boolean lobbies = command.isLobbies();
                AdminCommands.refreshRoomLists(practice, activeContest, lobbies);
                send(csr("Refresh room lists request processed"), connectionID, recipientID);
            } else if (request instanceof ApprovedQuestionCommand) { //added by SYHAAS 2002-05-16
                ApprovedQuestionCommand aqc = (ApprovedQuestionCommand) request;
                AdminCommands.broadcastApprovedQuestion(aqc);
                send(csr("Question broadcast request processed"), connectionID, recipientID);
            } else if (request instanceof LoadRoundRequest) {
                try {
                    AdminCommands.loadContestRound(((LoadRoundRequest) request).getRoundID());
                    send(csr("Contest round loaded."), connectionID, recipientID);
                } catch (IllegalStateException i) {
                    log.error("Failed to load round " + ((LoadRoundRequest) request).getRoundID() +
                            ":", i);
                    send(cfr("Contest round load failed."), connectionID, recipientID);
                }
            } else if (request instanceof UnloadRoundRequest) {
                try {
                    AdminCommands.unloadContestRound(((UnloadRoundRequest) request).getRoundID());
                    send(csr("Contest round unloaded."), connectionID, recipientID);
                } catch (IllegalStateException i) {
                    log.error("Failed to unload round " + ((LoadRoundRequest) request).getRoundID() +
                            ":", i);
                    send(cfr("Contest round unload failed."), connectionID, recipientID);
                }
            } else if (request instanceof GarbageCollectionRequest) {
                String results = AdminCommands.runGarbageCollection();
                send(csr(results), connectionID, recipientID);
            } else if (request instanceof ReplayListenerRequest) {
                AdminCommands.startReplayListener();
                send(csr("Replay listener started"), connectionID, recipientID);
            } else if (request instanceof ReplayReceiverRequest) {
                AdminCommands.startReplayReceiver();
                send(csr("Replay receiver started"), connectionID, recipientID);
            }
//            else if (request instanceof SetSpectatorRoomRequest) {
//                SetSpectatorRoomRequest ssrr = (SetSpectatorRoomRequest) request;
////                CoreServices.setSpectatorRoomId(ssrr.getRoomId());
//                send(csr("Set spectator room request processed"), connectionID, recipientID);
//            }
            else if (request instanceof AdvancePhaseRequest) {
                AdvancePhaseRequest apr = (AdvancePhaseRequest) request;
                AdminCommands.advancePhase(apr.getRoundID(), apr.getPhaseId());
                send(csr("Advance contest phase request processed"), connectionID, recipientID);
            } else if (request instanceof RegisterUserRequest) {
                RegisterUserRequest rur = (RegisterUserRequest) request;
                String handle = rur.getHandle();
                int roundId = rur.getRoundID();
                boolean atLeast18 = rur.getAtLeast18();
                CommandResponse response = CoreServices.registerCoderByHandle(handle, roundId, atLeast18);
                send(response, connectionID, recipientID);
            } else if (request instanceof UnregisterUserRequest) {
                UnregisterUserRequest urur = (UnregisterUserRequest) request;
                String handle = urur.getHandle();
                int roundId = urur.getRoundID();
                CommandResponse response = CoreServices.unregisterCoderByHandle(handle, roundId);
                send(response, connectionID, recipientID);
            } else if (request instanceof AllocatePrizesRequest) {
                AllocatePrizesRequest apr = (AllocatePrizesRequest) request;
                int roundId = apr.getRoundID();
                boolean shouldCommit = apr.getShouldCommit();
                Collection prizes = CoreServices.allocatePrizes(roundId, shouldCommit);
                if (prizes == null) {
                    send(cfr("Prize allocation failed; check log for details."), connectionID, recipientID);
                }
                Iterator it = prizes.iterator();
                log.info("Prize allocations for round " + roundId + " with commit=" + shouldCommit);
                while (it.hasNext()) {
                    log.info(it.next().toString());
                }
                send(csr("Prizes allocated; check contest server log for details"), connectionID, recipientID);
            } else if (request instanceof StartSpecAppRotationRequest) {
                log.info("Executing start spec app rotation command");
                AdminCommands.startSpecAppRotation(((StartSpecAppRotationRequest) request).getRotationDelay());
                send(csr("Spec app rotation mode activated"), connectionID, recipientID);
            } else if (request instanceof StopSpecAppRotationRequest) {
                log.info("Executing stop spec app rotation command");
                AdminCommands.stopSpecAppRotation();
                send(csr("Spec app rotation mode deactivated"), connectionID, recipientID);
            } else if (request instanceof SpecAppShowRoomRequest) {
                log.info("Executing show spec app room command");
                AdminCommands.showSpecAppRoom(((SpecAppShowRoomRequest) request).getRoomID());
                send(csr("Spec app room set"), connectionID, recipientID);
            } else if (request instanceof AnnounceAdvancingCodersRequest) {
                log.info("Executing announce advancers command");
                AnnounceAdvancingCodersRequest advancingCodersRequest = (AnnounceAdvancingCodersRequest) request;
                AdminCommands.announceAdvancingCoders(advancingCodersRequest.getRoundID(), advancingCodersRequest.getNumAdvancing());
                send(csr("Announced coders"), connectionID, recipientID);
            } else if (request instanceof RestartEventTopicListenerRequest) {
                log.info("Executing Recycle Event Topic Listener");
                AdminCommands.recycleEventTopicListener();
                send(csr("Event Topic Listener Recycled "), connectionID, recipientID);
            } else if (request instanceof SetForwardingAddressRequest) {
                log.info("Setting forwarding address");
                SetForwardingAddressRequest setForwardingAddressRequest = ((SetForwardingAddressRequest) request);
                send(csr("Forwarding Adress set!"), connectionID, recipientID);
                AdminCommands.setForwardingAddress(setForwardingAddressRequest.getAddress());
            } else if (request instanceof SetAdminForwardingAddressRequest) {
                log.info("Setting forwarding address");
                send(csr("Admin Forwarding Adress set!"), connectionID, recipientID);
            } else if (request instanceof AdvanceWLCodersRequest) {
                AdvanceWLCodersRequest advanceWLCodersRequest = (AdvanceWLCodersRequest) request;
                int roundID = advanceWLCodersRequest.getRoundID();
                int targetRoundId = advanceWLCodersRequest.getTargetRoundId();
                log.info("AdvanceWLCodersRequest: " + roundID + " " + targetRoundId);
                AdminCommands.advanceWLCoders(roundID, targetRoundId);
                send(csr("AdvanceWLCodersRequest processed"), connectionID, recipientID);
            } else if (request instanceof BootUserCommand) {
                BootUserCommand bootUserRequest = (BootUserCommand) request;
                log.info("Booting user");
                AdminCommands.bootUser(bootUserRequest.getHandle());
                send(csr("BootUserRequest processed"), connectionID, recipientID);
            } else {
                error("not implemented, type=" + request);
            }
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error processing request", e);
            if (isClientRequest) {
                send(cfr("Request failed with error: " + e), connectionID, recipientID);
            }
        }
    }

    private Enumeration queueEn;

    //this class handles watching a queue for testing completion
    private class TestingQueueWatcher extends Thread {

        private int connID;
        private int recipID;

        public TestingQueueWatcher(int connectionID, int recipientID) {
            connID = connectionID;
            recipID = recipientID;
        }

        public void run() {
            //establish queue connection, wait for emptiness
            Context ctx;

            try {
                ctx = TCContext.getJMSContext();
            } catch(Exception e) {
                send(cfr(e.toString()), connID, recipID);
                return;
            }

            //lookup the queue connection factory
            QueueConnectionFactory qfact;

            try {
                qfact = (QueueConnectionFactory) ctx.lookup(ApplicationServer.JMS_FACTORY);
            } catch(Exception e) {
                send(cfr(e.toString()), connID, recipID);
                return;
            }

            //get the queue connection, queue, and browser
            QueueConnection qconn;
            try {
                qconn = qfact.createQueueConnection();
            } catch(Exception e) {
                send(cfr(e.toString()), connID, recipID);
                return;
            }

            QueueSession qsess;
            Queue queue;
            QueueBrowser browser;
            try {
                qsess = qconn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
                queue = (Queue) ctx.lookup(DBMS.TESTING_QUEUE);
                browser = qsess.createBrowser(queue);
            } catch(Exception e) {
                send(cfr(e.toString()), connID, recipID);
                return;
            } finally {
                try {
                    qconn.close();
                } catch(Exception e) {
                }

            }

            try {

                boolean empty = false;
                do {
                    queueEn = null;
                    try {
                        log.info("INSPECTING QUEUE");
                        Thread.sleep(5000);
                    } catch (Exception e) {
                    }
                    queueEn = browser.getEnumeration();

                    if(!queueEn.hasMoreElements()) {
                        empty = true;
                    }
                } while(!empty);

            } catch(Exception e) {
                send(cfr(e.toString()), connID, recipID);
                return;
            } finally {
                try {
                    qconn.close();
                    browser.close();
                    qsess.close();
                } catch(Exception e) {
                }
            }

            send(csr("Testing Phase 1 is complete"), connID, recipID);

            //send message to testers to resart in reference mode

            try {

                TopicMessagePublisher restartServicePublisher = new TopicMessagePublisher(ApplicationServer.JMS_FACTORY, DBMS.RESTART_TOPIC);
                restartServicePublisher.setPersistent(true);
                restartServicePublisher.setFaultTolerant(false);

                HashMap props = new HashMap();
                props.put("serviceType", new Integer(AdminConstants.REQUEST_RESTART_TESTERS));
                props.put("exitCode", new Integer(3)); //switch to ref
                restartServicePublisher.pubMessage(props);
            } catch (Exception e) {
                send(cfr(e.toString()), connID, recipID);
                return;
            }

            //wait a few seconds, then start looking at the restart queue
            try {
                Thread.sleep(5000);
            } catch(Exception e) {
            }

            //get the queue connection, queue, and browser
            try {
                qconn = qfact.createQueueConnection();
            } catch(Exception e) {
                send(cfr(e.toString()), connID, recipID);
                return;
            }

            try {
                qsess = qconn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
                queue = (Queue) ctx.lookup(DBMS.REFERENCE_TESTING_QUEUE);
                browser = qsess.createBrowser(queue);
            } catch(Exception e) {
                send(cfr(e.toString()), connID, recipID);
                return;
            } finally {
                try {
                    qconn.close();
                } catch(Exception e) {
                }

            }

            try {
                boolean empty = false;
                do {
                    queueEn = null;
                    try {
                        log.info("INSPECTING QUEUE");
                        Thread.sleep(5000);
                    } catch (Exception e) {
                    }
                    queueEn = browser.getEnumeration();

                    if(!queueEn.hasMoreElements()) {
                        empty = true;
                    }
                } while(!empty);

            } catch(Exception e) {
                send(cfr(e.toString()), connID, recipID);
                return;
            } finally {
                try {
                    qconn.close();
                    browser.close();
                    qsess.close();
                } catch(Exception e) {
                }
            }

            send(csr("Testing Phase 2 is complete"), connID, recipID);

            try {

                TopicMessagePublisher restartServicePublisher = new TopicMessagePublisher(ApplicationServer.JMS_FACTORY, DBMS.RESTART_TOPIC);
                restartServicePublisher.setPersistent(true);
                restartServicePublisher.setFaultTolerant(false);

                HashMap props = new HashMap();
                props.put("serviceType", new Integer(AdminConstants.REQUEST_RESTART_TESTERS));
                props.put("exitCode", new Integer(2)); //switch to testers
                restartServicePublisher.pubMessage(props);
            } catch (Exception e) {
                send(cfr(e.toString()), connID, recipID);
                return;
            }
        }
    }

    private CommandResponse tryGenericBroadcast(String message) {
        try {
            AdminBroadcastManager.getInstance().sendGenericBroadcast(-1, message);
            return csr("Broadcast message sent");
        } catch (BadBroadcastException bbe) {
            StringWriter sw = new StringWriter();
            bbe.printStackTrace(new PrintWriter(sw));
            log.error("Bad admin broadcast: " + sw);
            return cfr("Bad admin broadcast:\n" + sw);
        }
    }

    private CommandResponse tryRoundBroadcast(String message, int roundId) {
        try {
            AdminBroadcastManager.getInstance().sendRoundBroadcast(-1, roundId, message);
            return csr("Broadcast message sent");
        } catch (BadBroadcastException bbe) {
            StringWriter sw = new StringWriter();
            bbe.printStackTrace(new PrintWriter(sw));
            log.error("Bad admin broadcast: " + sw);
            return cfr("Bad admin broadcast:\n" + sw);
        }
    }

    private CommandResponse tryComponentBroadcast(String message, int roundId, int componentId) {
        try {
            AdminBroadcastManager.getInstance().sendComponentBroadcast(-1, roundId, componentId, message);
            return csr("Broadcast message sent");
        } catch (BadBroadcastException bbe) {
            StringWriter sw = new StringWriter();
            bbe.printStackTrace(new PrintWriter(sw));
            log.error("Bad admin broadcast: " + sw);
            return cfr("Bad admin broadcast:\n" + sw);
        }
    }

    private CommandFailedResponse cfr(String s) {
        return new CommandFailedResponse(s);
    }

    private CommandSucceededResponse csr(String s) {
        return new CommandSucceededResponse(s);
    }

    private void sendCachedObject(Object cachedObject, String request, int connectionID, int recipientID) {
        String s = cachedObject == null ? "not in cache" : cachedObject.toString();
        sendCachedObjectString(s, request, connectionID, recipientID);
    }

    private void sendCachedObjectString(String cachedObjectString, String request, int connectionID, int recipientID) {
        String s = request + ": " + cachedObjectString;
        send(new CachedItem(s), connectionID, recipientID);
    }

    void send() {
        if (getConnectionsSize() <= 0) {
            return;
        }
        Object response = dataHandler.getResponse();
        send(response, ALL_ADMIN_LISTENERS, AdminConstants.RECIPIENT_ALL);
    }

    public void send(Object response, int connectionID, int recipientID) {
        if (getConnectionsSize() <= 0) {
            return;
        }
        if (response == null) {
            return;
        }
        ContestServerResponse serverResponse = new ContestServerResponse(recipientID, response);
        if (ListenerMain.getSocketConnector() != null && ListenerMain.getSocketConnector().isConnected(connectionID)){
            ListenerMain.getSocketConnector().write(connectionID, response);
        } else if (connectionID == ALL_ADMIN_LISTENERS) {
            synchronized (connections) {
                for (Iterator it = connections.iterator(); it.hasNext();) {
                    Integer id = (Integer) it.next();
                    controller.send(id.intValue(), serverResponse);
                }
            }
        } else {
            controller.send(connectionID, serverResponse);
        }
    }

    public void lostConnection(int connection_id) {
        connections.remove(new Integer(connection_id));
    }

    public void lostConnectionTemporarily(int connection_id) {
        lostConnection(connection_id);
    }

    private static void error(String msg) {
        log.error(msg);
    }

}
