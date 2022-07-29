package com.topcoder.server.listener.monitor;

import java.io.IOException;

import com.topcoder.server.AdminListener.request.AddTimeCommand;
import com.topcoder.server.AdminListener.request.AdvancePhaseRequest;
import com.topcoder.server.AdminListener.request.AllocatePrizesRequest;
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
import com.topcoder.server.AdminListener.request.GarbageCollectionRequest;
import com.topcoder.server.AdminListener.request.GenericRequest;
import com.topcoder.server.AdminListener.request.GlobalBroadcastCommand;
import com.topcoder.server.AdminListener.request.LoadRoundRequest;
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
import com.topcoder.server.AdminListener.request.RestoreRoundCommand;
import com.topcoder.server.AdminListener.request.RoomObject;
import com.topcoder.server.AdminListener.request.RoundBroadcastCommand;
import com.topcoder.server.AdminListener.request.RoundForwardCommand;
import com.topcoder.server.AdminListener.request.RoundObject;
import com.topcoder.server.AdminListener.request.SetUserStatusCommand;
import com.topcoder.server.AdminListener.request.ShowSpecResultsCommand;
import com.topcoder.server.AdminListener.request.ShutdownRequest;
import com.topcoder.server.AdminListener.request.SpecAppShowRoomRequest;
import com.topcoder.server.AdminListener.request.StartSpecAppRotationRequest;
import com.topcoder.server.AdminListener.request.StopSpecAppRotationRequest;
import com.topcoder.server.AdminListener.request.SystemTestCommand;
import com.topcoder.server.AdminListener.request.UnloadRoundRequest;
import com.topcoder.server.AdminListener.request.UserObject;
import com.topcoder.server.AdminListener.response.CommandFailedResponse;
import com.topcoder.server.AdminListener.response.CommandSucceededResponse;
import com.topcoder.server.AdminListener.response.ContestServerResponse;
import com.topcoder.server.AdminListener.response.GenericResponse;
import com.topcoder.shared.netCommon.CSHandler;


public final class MonitorCSHandler extends CSHandler {

    private static final byte BOOT_USER_COMMAND = 96;
    private static final byte MONITOR_STATS_ITEM = 97;
    private static final byte FIRST_RESPONSE = 98;
    private static final byte SHUTDOWN_REQUEST = 99;
    private static final byte DISCONNECT_REQUEST = 100;
//    private static final byte TIMER_COMMAND = 101;
    private static final byte REFRESH_REG_COMMAND = 102;
    private static final byte SYSTEM_TEST_COMMAND = 103;
    private static final byte END_CONTEST_COMMAND = 104;
    private static final byte REFRESH_PROBS_COMMAND = 105;
    private static final byte REFRESH_ROOM_COMMAND = 106;
    private static final byte RESTORE_ROUND_COMMAND = 107;
    private static final byte GLOBAL_BROADCAST_COMMAND = 108;
    private static final byte ADD_TIME_COMMAND = 109;
    private static final byte ASSIGN_ROOMS_COMMAND = 110;
    private static final byte CHAT_ITEM = 111;
    private static final byte SET_USER_STATUS_COMMAND = 112;
    private static final byte BAN_IP_COMMAND = 113;
    private static final byte ENABLE_ROUND_COMMAND = 114;
    private static final byte DISABLE_ROUND_COMMAND = 115;
    private static final byte REFRESH_ROUND_COMMAND = 116;
    private static final byte USER_OBJECT = 117;
    private static final byte CACHED_ITEM = 118;
    private static final byte REGISTRATION_OBJECT = 119;
    private static final byte PROBLEM_OBJECT = 120;
    private static final byte ROUND_OBJECT = 121;
    private static final byte ROOM_OBJECT = 122;
    private static final byte CODER_OBJECT = 123;
    private static final byte CODER_PROBLEM_OBJECT = 124;
    private static final byte REFRESH_ROOM_LISTS_COMMAND = 125;
    private static final byte CLEAR_TEST_CASES_COMMAND = 126;
    private static final byte REFRESH_BROADCASTS_COMMAND = 127;
    private static final byte CLIENT_COMMAND_REQUEST = -127;
    private static final byte GENERIC_REQUEST = -126;
    private static final byte CONTEST_SERVER_RESPONSE = -125;
    private static final byte COMMAND_FAILED_RESPONSE = -124;
    private static final byte GENERIC_RESPONSE = -123;
    private static final byte LOAD_ROUND_REQUEST = -122;
    private static final byte GARBAGE_COLLECTION_REQUEST = -121;
    private static final byte REPLAY_LISTENER_REQUEST = -120;
    private static final byte REPLAY_RECEIVER_REQUEST = -119;
//    private static final byte SET_SPECTATOR_ROOM_REQUEST=-118;
    private static final byte ADVANCE_PHASE_REQUEST = -117;
    private static final byte REGISTER_USER_REQUEST = -116;
    private static final byte COMMAND_SUCCEEDED_RESPONSE = -115;
    private static final byte ALLOCATE_PRIZES_REQUEST = -114;
    private static final byte PROBLEM_BROADCAST_COMMAND = -113;
    private static final byte ROUND_BROADCAST_COMMAND = -112;
    private static final byte UNLOAD_ROUND_REQUEST = -111;
    private static final byte START_SPEC_APP_ROTATION_COMMAND = -110;
    private static final byte STOP_SPEC_APP_ROTATION_COMMAND = -109;
    private static final byte SHOW_SPEC_APP_ROOM_COMMAND = -108;
    private static final byte CLEAR_PRACTICE_ROOMS_COMMAND = -107;
    private static final byte REFRESH_ALL_ROOMS_COMMAND = -106;
    private static final byte ROUND_FORWARD_COMMAND = -105;
    private static final byte SHOW_SPEC_RESULTS_COMMAND = -104;
    private static final byte CANCEL_SYSTEM_TEST_CASE_COMMAND = -103;
//    private static final byte RECALCULATE_SCORE_COMMAND = -105;

    protected boolean writeObjectOverride(Object object) throws IOException {
        if (object instanceof MonitorStatsItem) {
            writeByte(MONITOR_STATS_ITEM);
            customWriteObject(object);
            return true;
        }
        if (object instanceof FirstResponse) {
            writeByte(FIRST_RESPONSE);
            customWriteObject(object);
            return true;
        }
        if (object instanceof ShutdownRequest) {
            writeByte(SHUTDOWN_REQUEST);
            customWriteObject(object);
            return true;
        }
        if (object instanceof DisconnectRequest) {
            writeByte(DISCONNECT_REQUEST);
            customWriteObject(object);
            return true;
        }
        if (object instanceof ClearTestCasesCommand) {
            writeByte(CLEAR_TEST_CASES_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof RefreshRegCommand) {
            writeByte(REFRESH_REG_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof SystemTestCommand) {
            writeByte(SYSTEM_TEST_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof CancelSystemTestCaseTestingCommand) {
            writeByte(CANCEL_SYSTEM_TEST_CASE_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof EndContestCommand) {
            writeByte(END_CONTEST_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof RefreshProbsCommand) {
            writeByte(REFRESH_PROBS_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof ClearPracticeRoomsCommand) {
            writeByte(CLEAR_PRACTICE_ROOMS_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof RefreshRoomCommand) {
            writeByte(REFRESH_ROOM_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof RefreshAllRoomsCommand) {
            writeByte(REFRESH_ALL_ROOMS_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof RoundForwardCommand) {
            writeByte(ROUND_FORWARD_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof ShowSpecResultsCommand) {
            writeByte(SHOW_SPEC_RESULTS_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof RestoreRoundCommand) {
            writeByte(RESTORE_ROUND_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof GlobalBroadcastCommand) {
            writeByte(GLOBAL_BROADCAST_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof ComponentBroadcastCommand) {
            writeByte(PROBLEM_BROADCAST_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof RoundBroadcastCommand) {
            writeByte(ROUND_BROADCAST_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof AddTimeCommand) {
            writeByte(ADD_TIME_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof AssignRoomsCommand) {
            writeByte(ASSIGN_ROOMS_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof ChatItem) {
            writeByte(CHAT_ITEM);
            customWriteObject(object);
            return true;
        }
        if (object instanceof SetUserStatusCommand) {
            writeByte(SET_USER_STATUS_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof BootUserCommand) {
            writeByte(BOOT_USER_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof BanIPCommand) {
            writeByte(BAN_IP_COMMAND);
            customWriteObject(object);
            return true;
        }
//        if (object instanceof RecalculateScoreRequest) {
//            writeByte(RECALCULATE_SCORE_COMMAND);
//            customWriteObject(object);
//            return true;
//        }
        if (object instanceof EnableRoundCommand) {
            writeByte(ENABLE_ROUND_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof DisableRoundCommand) {
            writeByte(DISABLE_ROUND_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof RefreshRoundCommand) {
            writeByte(REFRESH_ROUND_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof UserObject) {
            writeByte(USER_OBJECT);
            customWriteObject(object);
            return true;
        }
        if (object instanceof CachedItem) {
            writeByte(CACHED_ITEM);
            customWriteObject(object);
            return true;
        }
        if (object instanceof RegistrationObject) {
            writeByte(REGISTRATION_OBJECT);
            customWriteObject(object);
            return true;
        }
        if (object instanceof ProblemObject) {
            writeByte(PROBLEM_OBJECT);
            customWriteObject(object);
            return true;
        }
        if (object instanceof RoundObject) {
            writeByte(ROUND_OBJECT);
            customWriteObject(object);
            return true;
        }
        if (object instanceof RoomObject) {
            writeByte(ROOM_OBJECT);
            customWriteObject(object);
            return true;
        }
        if (object instanceof CoderObject) {
            writeByte(CODER_OBJECT);
            customWriteObject(object);
            return true;
        }
        if (object instanceof CoderProblemObject) {
            writeByte(CODER_PROBLEM_OBJECT);
            customWriteObject(object);
            return true;
        }
        if (object instanceof RefreshRoomListsCommand) {
            writeByte(REFRESH_ROOM_LISTS_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof RefreshBroadcastsCommand) {
            writeByte(REFRESH_BROADCASTS_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof ClientCommandRequest) {
            writeByte(CLIENT_COMMAND_REQUEST);
            customWriteObject(object);
            return true;
        }
        if (object instanceof LoadRoundRequest) {
            writeByte(LOAD_ROUND_REQUEST);
            customWriteObject(object);
            return true;
        }
        if (object instanceof UnloadRoundRequest) {
            writeByte(UNLOAD_ROUND_REQUEST);
            customWriteObject(object);
            return true;
        }
        if (object instanceof GarbageCollectionRequest) {
            writeByte(GARBAGE_COLLECTION_REQUEST);
            customWriteObject(object);
            return true;
        }
        if (object instanceof ReplayListenerRequest) {
            writeByte(REPLAY_LISTENER_REQUEST);
            customWriteObject(object);
            return true;
        }
        if (object instanceof ReplayReceiverRequest) {
            writeByte(REPLAY_RECEIVER_REQUEST);
            customWriteObject(object);
            return true;
        }
//        if (object instanceof SetSpectatorRoomRequest) {
//            writeByte(SET_SPECTATOR_ROOM_REQUEST);
//            customWriteObject(object);
//            return true;
//        }
        if (object instanceof AdvancePhaseRequest) {
            writeByte(ADVANCE_PHASE_REQUEST);
            customWriteObject(object);
            return true;
        }
        if (object instanceof RegisterUserRequest) {
            writeByte(REGISTER_USER_REQUEST);
            customWriteObject(object);
            return true;
        }
        if (object instanceof AllocatePrizesRequest) {
            writeByte(ALLOCATE_PRIZES_REQUEST);
            customWriteObject(object);
            return true;
        }
        if (object instanceof ContestServerResponse) {
            writeByte(CONTEST_SERVER_RESPONSE);
            customWriteObject(object);
            return true;
        }
        if (object instanceof CommandSucceededResponse) {
            writeByte(COMMAND_SUCCEEDED_RESPONSE);
            customWriteObject(object);
            return true;
        }
        if (object instanceof CommandFailedResponse) {
            writeByte(COMMAND_FAILED_RESPONSE);
            customWriteObject(object);
            return true;
        }

        if (object instanceof SpecAppShowRoomRequest) {
            writeByte(SHOW_SPEC_APP_ROOM_COMMAND);
            customWriteObject(object);
            return true;
        }

        if (object instanceof StartSpecAppRotationRequest) {
            writeByte(START_SPEC_APP_ROTATION_COMMAND);
            customWriteObject(object);
            return true;
        }
        if (object instanceof StopSpecAppRotationRequest) {
            writeByte(STOP_SPEC_APP_ROTATION_COMMAND);
            customWriteObject(object);
            return true;
        }


        // These should come last so that subtypes of these classes don't magically
        // get transmuted into these types upon custom serialization.
        if (object instanceof GenericRequest) {
            writeByte(GENERIC_REQUEST);
            customWriteObject(object);
            return true;
        }
        if (object instanceof GenericResponse) {
            writeByte(GENERIC_RESPONSE);
            customWriteObject(object);
            return true;
        }
        return false;
    }

    protected Object readObjectOverride(byte type) throws IOException {
        switch (type) {
        case MONITOR_STATS_ITEM:
            MonitorStatsItem statsItem = new MonitorStatsItem();
            statsItem.customReadObject(this);
            return statsItem;
        case FIRST_RESPONSE:
            FirstResponse firstResponse = new FirstResponse();
            firstResponse.customReadObject(this);
            return firstResponse;
        case SHUTDOWN_REQUEST:
            ShutdownRequest shutdownRequest = new ShutdownRequest();
            shutdownRequest.customReadObject(this);
            return shutdownRequest;
        case DISCONNECT_REQUEST:
            DisconnectRequest disconnectRequest = new DisconnectRequest();
            disconnectRequest.customReadObject(this);
            return disconnectRequest;
        case CLEAR_TEST_CASES_COMMAND:
            ClearTestCasesCommand clearTestCasesCommand = new ClearTestCasesCommand();
            clearTestCasesCommand.customReadObject(this);
            return clearTestCasesCommand;
        case REFRESH_REG_COMMAND:
            RefreshRegCommand refreshRegCommand = new RefreshRegCommand();
            refreshRegCommand.customReadObject(this);
            return refreshRegCommand;
        case END_CONTEST_COMMAND:
            EndContestCommand endContestCommand = new EndContestCommand();
            endContestCommand.customReadObject(this);
            return endContestCommand;
        case REFRESH_PROBS_COMMAND:
            RefreshProbsCommand refreshProbsCommand = new RefreshProbsCommand();
            refreshProbsCommand.customReadObject(this);
            return refreshProbsCommand;
        case CLEAR_PRACTICE_ROOMS_COMMAND:
            ClearPracticeRoomsCommand clearCommand = new ClearPracticeRoomsCommand();
            clearCommand.customReadObject(this);
            return clearCommand;
        case REFRESH_ROOM_COMMAND:
            RefreshRoomCommand refreshRoomCommand = new RefreshRoomCommand();
            refreshRoomCommand.customReadObject(this);
            return refreshRoomCommand;
        case ROUND_FORWARD_COMMAND:
            RoundForwardCommand cmd = new RoundForwardCommand();
            cmd.customReadObject(this);
            return cmd;
        case SHOW_SPEC_RESULTS_COMMAND:
            ShowSpecResultsCommand showSpecResultsCmd = new ShowSpecResultsCommand();
            showSpecResultsCmd.customReadObject(this);
            return showSpecResultsCmd;
        case REFRESH_ALL_ROOMS_COMMAND:
            RefreshAllRoomsCommand refreshAllRoomsCommand = new RefreshAllRoomsCommand();
            refreshAllRoomsCommand.customReadObject(this);
            return refreshAllRoomsCommand;
        case RESTORE_ROUND_COMMAND:
            RestoreRoundCommand restoreRoundCommand = new RestoreRoundCommand();
            restoreRoundCommand.customReadObject(this);
            return restoreRoundCommand;
        case GLOBAL_BROADCAST_COMMAND:
            GlobalBroadcastCommand globalBroadcastCommand = new GlobalBroadcastCommand();
            globalBroadcastCommand.customReadObject(this);
            return globalBroadcastCommand;
        case PROBLEM_BROADCAST_COMMAND:
            ComponentBroadcastCommand problemBroadcastCommand = new ComponentBroadcastCommand();
            problemBroadcastCommand.customReadObject(this);
            return problemBroadcastCommand;
        case ROUND_BROADCAST_COMMAND:
            RoundBroadcastCommand roundBroadcastCommand = new RoundBroadcastCommand();
            roundBroadcastCommand.customReadObject(this);
            return roundBroadcastCommand;
        case ADD_TIME_COMMAND:
            AddTimeCommand addTimeCommand = new AddTimeCommand();
            addTimeCommand.customReadObject(this);
            return addTimeCommand;
        case ASSIGN_ROOMS_COMMAND:
            AssignRoomsCommand assignRoomsCommand = new AssignRoomsCommand();
            assignRoomsCommand.customReadObject(this);
            return assignRoomsCommand;
        case CHAT_ITEM:
            ChatItem chatItem = new ChatItem();
            chatItem.customReadObject(this);
            return chatItem;
        case SET_USER_STATUS_COMMAND:
            SetUserStatusCommand setUserStatusCommand = new SetUserStatusCommand();
            setUserStatusCommand.customReadObject(this);
            return setUserStatusCommand;
        case BOOT_USER_COMMAND:
            BootUserCommand bootUserCommand = new BootUserCommand();
            bootUserCommand.customReadObject(this);
            return bootUserCommand;
        case BAN_IP_COMMAND:
            BanIPCommand banIPCommand = new BanIPCommand();
            banIPCommand.customReadObject(this);
            return banIPCommand;
//        case RECALCULATE_SCORE_COMMAND:
//            RecalculateScoreRequest recalculateScoreCommand = new RecalculateScoreRequest();
//            recalculateScoreCommand.customReadObject(this);
//            return recalculateScoreCommand;
        case START_SPEC_APP_ROTATION_COMMAND:
            StartSpecAppRotationRequest startSpecAppRotationRequest = new StartSpecAppRotationRequest();
            startSpecAppRotationRequest.customReadObject(this);
            return startSpecAppRotationRequest;
        case STOP_SPEC_APP_ROTATION_COMMAND:
            StopSpecAppRotationRequest stopSpecAppRotationRequest = new StopSpecAppRotationRequest();
            stopSpecAppRotationRequest.customReadObject(this);
            return stopSpecAppRotationRequest;
        case SHOW_SPEC_APP_ROOM_COMMAND:
            SpecAppShowRoomRequest specAppShowRoomRequest = new SpecAppShowRoomRequest();
            specAppShowRoomRequest.customReadObject(this);
            return specAppShowRoomRequest;
        case ENABLE_ROUND_COMMAND:
            EnableRoundCommand enableRoundCommand = new EnableRoundCommand();
            enableRoundCommand.customReadObject(this);
            return enableRoundCommand;
        case DISABLE_ROUND_COMMAND:
            {
                DisableRoundCommand command = new DisableRoundCommand();
                command.customReadObject(this);
                return command;
            }
        case REFRESH_ROUND_COMMAND:
            {
                RefreshRoundCommand command = new RefreshRoundCommand();
                command.customReadObject(this);
                return command;
            }
        case SYSTEM_TEST_COMMAND:
            {
                SystemTestCommand command = new SystemTestCommand();
                command.customReadObject(this);
                return command;
            }
        case CANCEL_SYSTEM_TEST_CASE_COMMAND:
        {
            CancelSystemTestCaseTestingCommand command = new CancelSystemTestCaseTestingCommand();
            command.customReadObject(this);
            return command;
        }
        case USER_OBJECT:
            {
                UserObject object = new UserObject();
                object.customReadObject(this);
                return object;
            }
        case CACHED_ITEM:
            {
                CachedItem object = new CachedItem();
                object.customReadObject(this);
                return object;
            }
        case REGISTRATION_OBJECT:
            {
                RegistrationObject object = new RegistrationObject();
                object.customReadObject(this);
                return object;
            }
        case PROBLEM_OBJECT:
            {
                ProblemObject object = new ProblemObject();
                object.customReadObject(this);
                return object;
            }
        case ROUND_OBJECT:
            {
                RoundObject object = new RoundObject();
                object.customReadObject(this);
                return object;
            }
        case ROOM_OBJECT:
            {
                RoomObject object = new RoomObject();
                object.customReadObject(this);
                return object;
            }
        case CODER_OBJECT:
            {
                CoderObject object = new CoderObject();
                object.customReadObject(this);
                return object;
            }
        case CODER_PROBLEM_OBJECT:
            {
                CoderProblemObject object = new CoderProblemObject();
                object.customReadObject(this);
                return object;
            }
        case REFRESH_ROOM_LISTS_COMMAND:
            {
                RefreshRoomListsCommand object = new RefreshRoomListsCommand();
                object.customReadObject(this);
                return object;
            }
        case REFRESH_BROADCASTS_COMMAND:
            {
                RefreshBroadcastsCommand object = new RefreshBroadcastsCommand();
                object.customReadObject(this);
                return object;
            }
        case CLIENT_COMMAND_REQUEST:
            {
                ClientCommandRequest object = new ClientCommandRequest();
                object.customReadObject(this);
                return object;
            }
        case LOAD_ROUND_REQUEST:
            {
                LoadRoundRequest object = new LoadRoundRequest();
                object.customReadObject(this);
                return object;
            }
        case UNLOAD_ROUND_REQUEST:
            {
                UnloadRoundRequest object = new UnloadRoundRequest();
                object.customReadObject(this);
                return object;
            }
        case GARBAGE_COLLECTION_REQUEST:
            {
                GarbageCollectionRequest object = new GarbageCollectionRequest();
                object.customReadObject(this);
                return object;
            }
        case REPLAY_LISTENER_REQUEST:
            {
                ReplayListenerRequest object = new ReplayListenerRequest();
                object.customReadObject(this);
                return object;
            }
        case REPLAY_RECEIVER_REQUEST:
            {
                ReplayReceiverRequest object = new ReplayReceiverRequest();
                object.customReadObject(this);
                return object;
            }
//        case SET_SPECTATOR_ROOM_REQUEST:
//            {
//                SetSpectatorRoomRequest object = new SetSpectatorRoomRequest();
//                object.customReadObject(this);
//                return object;
//            }
        case ADVANCE_PHASE_REQUEST:
            {
                AdvancePhaseRequest object = new AdvancePhaseRequest();
                object.customReadObject(this);
                return object;
            }
        case REGISTER_USER_REQUEST:
            {
                RegisterUserRequest object = new RegisterUserRequest();
                object.customReadObject(this);
                return object;
            }
        case ALLOCATE_PRIZES_REQUEST:
            {
                AllocatePrizesRequest object = new AllocatePrizesRequest();
                object.customReadObject(this);
                return object;
            }
        case GENERIC_REQUEST:
            {
                GenericRequest object = new GenericRequest();
                object.customReadObject(this);
                return object;
            }
        case CONTEST_SERVER_RESPONSE:
            {
                ContestServerResponse object = new ContestServerResponse();
                object.customReadObject(this);
                return object;
            }
        case COMMAND_SUCCEEDED_RESPONSE:
            {
                CommandSucceededResponse object = new CommandSucceededResponse();
                object.customReadObject(this);
                return object;
            }
        case COMMAND_FAILED_RESPONSE:
            {
                CommandFailedResponse object = new CommandFailedResponse();
                object.customReadObject(this);
                return object;
            }
        case GENERIC_RESPONSE:
            {
                GenericResponse object = new GenericResponse();
                object.customReadObject(this);
                return object;
            }

        default:
            return super.readObjectOverride(type);
        }
    }

}
