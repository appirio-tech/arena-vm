package com.topcoder.client.mpsqasApplet.common;

import com.topcoder.client.mpsqasApplet.messaging.message.*;
import com.topcoder.netCommon.mpsqas.communication.message.*;

/**
 * Classes for the different responses.
 */
public class ResponseClassTypes {

    public static Class
            APPLICATION_REPLY = ApplicationReplyResponse.class,
    APPLICATION_ROOM_MOVE = ApplicationRoomMoveResponse.class,
    COMPARE_SOLUTIONS = CompareSolutionsResponse.class,
    FOYER_MOVE = FoyerMoveResponse.class,
    LOGIN = LoginResponse.class,
    MAIN_APPLICATION_MOVE = MainApplicationMoveResponse.class,
    MAIN_PROBLEM_MOVE = MainProblemMoveResponse.class,
    MAIN_USER_MOVE = MainUserMoveResponse.class,
    MOVE = MoveResponse.class,
    NEW_CORRESPONDENCE = NewCorrespondenceResponse.class,
    PING = PingResponse.class,
    PREVIEW_PROBLEM_STATEMENT = PreviewProblemStatementResponse.class,
    PROBLEM_MODIFIED = ProblemModifiedResponse.class,
    UPCOMING_CONTESTS_MOVE = UpcomingContestsMoveResponse.class,
    VIEW_APPLICATION_MOVE = ViewApplicationMoveResponse.class,
    VIEW_CONTEST_MOVE = ViewContestMoveResponse.class,
    VIEW_PROBLEM_MOVE = ViewProblemMoveResponse.class,
    VIEW_USER_MOVE = ViewUserMoveResponse.class,
    NEW_STATUS = NewStatusMessage.class,
    ARG_ENTRY = ArgEntryResponse.class,
    LOGIN_MOVE = LoginMoveResponse.class,
    MAIN_TEAM_PROBLEM_MOVE = MainTeamProblemMoveResponse.class,
    MAIN_LONG_PROBLEM_MOVE = MainLongProblemMoveResponse.class,
    MOVING_MOVE = MovingMoveResponse.class,
    POPUP = PopupResponse.class,
    NEW_PROBLEM_ID_STRUCTURE = NewProblemIdStructureResponse.class,
    NEW_COMPONENT_ID_STRUCTURE = NewComponentIdStructureResponse.class,
    GENERATE_JAVA_DOC = GenerateJavaDocResponse.class,
    EXCHANGE_KEY = ExchangeKeyResponse.class,
    PAYMENT = GeneratePaymentResponse.class;
    
}
