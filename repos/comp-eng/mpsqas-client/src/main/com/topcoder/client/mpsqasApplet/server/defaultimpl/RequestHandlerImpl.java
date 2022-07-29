package com.topcoder.client.mpsqasApplet.server.defaultimpl;

import com.topcoder.client.mpsqasApplet.server.RequestHandler;
import com.topcoder.client.mpsqasApplet.server.PortHandler;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.messaging.*;
import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.shared.problem.*;
import com.topcoder.shared.language.Language;

import java.util.ArrayList;
import java.util.HashMap;
import java.security.GeneralSecurityException;

/**
 * Implements all the applet's Request Processor interfaces, giving
 * implementations to methods allowing the applet to make requests
 * to the server.
 *
 * @author mitalub
 */
public class RequestHandlerImpl implements RequestHandler,
        LoginRequestProcessor,
        MoveRequestProcessor,
        ApplicationRequestProcessor,
        UserRequestProcessor,
        ContestRequestProcessor,
        PingRequestProcessor,
        SolutionRequestProcessor,
        CorrespondenceRequestProcessor,
        ProblemRequestProcessor,
        WebServiceRequestProcessor {


    private PortHandler portHandler;

    /**
     * Stores the port handler.
     */
    public void init() {
        portHandler = MainObjectFactory.getPortHandler();
    }

    //LoginRequestProcessor methods ----------------------------------

    /**
     * Makes and sends a LoginRequest.
     */
    public void requestLogin(String handle, String password) {
        try {
            portHandler.sendMessage(new LoginRequest(handle, MainObjectFactory.getEncryptionHandler().sealObject(password)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Encrypting password failed", e);
        }
    }

    //MoveRequestProcessor methods ----------------------------------

    /**
     * Makes and sends a ViewProblemMoveRequest.
     */
    public void viewProblem(int problemId) {
        putUpLoading();
        portHandler.sendMessage(new ViewProblemMoveRequest(problemId));
    }

    /**
     * Makes and sends a ViewContestMoveRequest.
     */
    public void viewContest(int contestId) {
        putUpLoading();
        portHandler.sendMessage(new ViewContestMoveRequest(contestId));
    }

    /**
     * Makes and sends a ViewUserMoveRequest.
     */
    public void viewUser(int userId) {
        putUpLoading();
        portHandler.sendMessage(new ViewUserMoveRequest(userId));
    }

    /**
     * Makes and sends a ViewApplicationMoveRequest.
     */
    public void viewApplication(int applicationId) {
        putUpLoading();
        portHandler.sendMessage(new ViewApplicationMoveRequest(applicationId));
    }

    /**
     * Makes and sends an ApplicationRoomMoveRequest for the WRITER_APPLICATION.
     */
    public void loadWriterApplication() {
        putUpLoading();
        portHandler.sendMessage(new ApplicationRoomMoveRequest(
                MessageConstants.WRITER_APPLICATION));
    }

    /**
     * Makes and sends an ApplicationRoomMoveRequest for the TESTER_APPLICATION.
     */
    public void loadTesterApplication() {
        putUpLoading();
        portHandler.sendMessage(new ApplicationRoomMoveRequest(
                MessageConstants.TESTER_APPLICATION));
    }

    /**
     * Makes and sends a FoyerMoveRequest.
     */
    public void loadFoyerRoom() {
        putUpLoading();
        portHandler.sendMessage(new FoyerMoveRequest());
    }

    /**
     * Makes and sends a MainApplicationMoveRequest.
     */
    public void loadMainApplicationRoom() {
        putUpLoading();
        portHandler.sendMessage(new MainApplicationMoveRequest());
    }

    /**
     * Makes and sends a MainProblemMoveRequest.
     */
    public void loadMainProblemRoom() {
        putUpLoading();
        portHandler.sendMessage(new MainProblemMoveRequest());
    }

    /**
     * Makes and sends a MainUserMoveRequest.
     */
    public void loadMainUserRoom() {
        putUpLoading();
        portHandler.sendMessage(new MainUserMoveRequest());
    }

    /**
     * Makes and sends an UpcomingContestsMoveRequest.
     */
    public void loadMainContestRoom() {
        putUpLoading();
        portHandler.sendMessage(new UpcomingContestsMoveRequest());
    }

    /**
     * Makes and sends a PendingApprovalMoveRequest.
     */
    public void loadPendingApprovalRoom() {
        putUpLoading();
        portHandler.sendMessage(new PendingApprovalMoveRequest());
    }

    /**
     * Makes and sends a TeamPendingApprovalMoveRequest.
     */
    public void loadTeamPendingApprovalRoom() {
        putUpLoading();
        portHandler.sendMessage(new TeamPendingApprovalMoveRequest());
    }
    
    /**
     * Makes and sends a LongPendingApprovalMoveRequest.
     */
    public void loadLongPendingApprovalRoom() {
        putUpLoading();
        portHandler.sendMessage(new LongPendingApprovalMoveRequest());
    }

    /**
     * Makes and sends nothing, no request yet.
     */
    public void loadAllProblems() {
        putUpLoading();
        portHandler.sendMessage(new ViewAllProblemsRequest());
    }

    public void loadAllTeamProblems() {
        putUpLoading();
        portHandler.sendMessage(new ViewAllTeamProblemsRequest());
    }
    
    public void loadAllLongProblems() {
        putUpLoading();
        portHandler.sendMessage(new ViewAllLongProblemsRequest());
    }

    public void createProblem() {
        putUpLoading();
        portHandler.sendMessage(new CreateProblemRequest());
    }

    public void createTeamProblem() {
        putUpLoading();
        portHandler.sendMessage(new CreateTeamProblemRequest());
    }
    
    public void createLongProblem() {
        putUpLoading();
        portHandler.sendMessage(new CreateLongProblemRequest());
    }

    /**
     * Makes and sends a DirectionalMoveRequest.
     */
    public void moveRelative(int distance) {
        putUpLoading();
        portHandler.sendMessage(new DirectionalMoveRequest(distance));
    }

    public void jump(String pattern){
        putUpLoading();
        portHandler.sendMessage(new JumpRequest(pattern));
    }

    public void loadMainTeamProblemRoom() {
        putUpLoading();
        portHandler.sendMessage(new MainTeamProblemMoveRequest());
    }

    public void viewTeamProblem(int problemId) {
        putUpLoading();
        portHandler.sendMessage(new ViewTeamProblemMoveRequest(problemId));
    }
    
    public void loadMainLongProblemRoom() {
        putUpLoading();
        portHandler.sendMessage(new MainLongProblemMoveRequest());
    }

    public void viewLongProblem(int problemId) {
        putUpLoading();
        portHandler.sendMessage(new ViewLongProblemMoveRequest(problemId));
    }

    public void viewComponent(int componentId) {
        putUpLoading();
        portHandler.sendMessage(new ViewComponentMoveRequest(componentId));
    }

    public void viewWebService(int webServiceId) {
        putUpLoading();
        portHandler.sendMessage(new ViewWebServiceMoveRequest(webServiceId));
    }

    //ApplicationRequestProcessor methods -----------------------------

    /**
     * Makes and sends a SubmitApplicationRequest.
     */
    public void sendApplication(String contents) {
        portHandler.sendMessage(new SubmitApplicationRequest(contents));
    }

    /**
     * Makes and sends an ApplicationReplyRequest.
     */
    public void sendReply(boolean accepted, String message) {
        portHandler.sendMessage(new ApplicationReplyRequest(accepted, message));
    }

    //UserRequestProcessor methods --------------------------------------

    /**
     * Makes and sends a SubmitPaymentRequest.
     */
    public void payUsers(ArrayList users) {
        portHandler.sendMessage(new SubmitPaymentRequest(users));
    }

    public void savePendingPayments(HashMap payments) {
        portHandler.sendMessage(new SavePendingPaymentsRequest(payments));
    }

    //ContestRequestProcessor methods ---------------------------------

    /**
     * Makes and sends a VerifyContestRequest.
     */
    public void verifyContest() {
        portHandler.sendMessage(new VerifyContestRequest());
    }

    /**
     * Makes and sends nothing, no request yet.
     */
    public void scheduleProblems(ArrayList scheduledProblems) {
    }

    //PingRequestProcessor methods -----------------------------------

    /**
     * Makes and sends a PingRequest.
     */
    public void pingServer() {
        portHandler.sendMessage(new PingRequest());
    }

    //SolutionRequestProcessor methods -----------------------------

    /**
     * Makes and sends a CompileRequest.
     */
    public void compile(HashMap codeFiles, Language language) {
        portHandler.sendMessage(new CompileRequest(codeFiles, language));
    }

    /**
     * Makes and sends a TestRequest.
     */
    public void test(Object[] args, int testType) {
        portHandler.sendMessage(new TestRequest(args, testType));
    }
    
    /**
     * Makes and sends a CancelTestsRequest.
     */
    public void cancelTests() {
        portHandler.sendMessage(new CancelTestsRequest());
        
    }

    /**
     * Makes and sends a SystemTestRequest.
     */
    public void systemTest(int testType) {
        portHandler.sendMessage(new SystemTestRequest(testType));
        
    }
    
    //CorrespondenceRequestProcessor methods --------------------------
    /**
     * Makes and sends a SendCorrespondenceRequest
     */
    public void sendCorrespondence(Correspondence correspondence) {
        portHandler.sendMessage(new SendCorrespondenceRequest(correspondence));
    }

    //ProblemRequestProcessor methods ----------------------------
    public void submitProblem(ProblemInformation problemInformation) {
        portHandler.sendMessage(new SubmitProblemRequest(problemInformation));
    }

    public void saveStatement(ProblemComponent component) {
        portHandler.sendMessage(new SaveProblemStatementRequest(component));
    }

    public void saveStatement(Problem problem) {
        portHandler.sendMessage(new SaveProblemStatementRequest(problem));
    }

    public void saveAdminProblemInfo(int status, int primarySolutionId,
            ArrayList testerIds) {
        portHandler.sendMessage(new SaveAdminInformationRequest(status,
                primarySolutionId, testerIds));
    }

    public void generatePreview(ProblemComponent component) {
        portHandler.sendMessage(new PreviewProblemStatementRequest(
                component));
    }

    public void generatePreview(Problem problem) {
        portHandler.sendMessage(new PreviewProblemStatementRequest(
                problem));
    }

    public void submitPendingReply(boolean accepted, String message) {
        portHandler.sendMessage(new PendingReplyRequest(accepted, message));
    }

    public void saveComponent(ComponentInformation info) {
        portHandler.sendMessage(new SaveComponentRequest(info));
    }

    /**
     * Makes and sends a CompareSolutionsRequest.
     */
    public void systemTestAll() {
        portHandler.sendMessage(new CompareSolutionsRequest());
    }

    public void deployWebService(WebServiceInformation webService) {
        portHandler.sendMessage(new DeployWebServiceRequest(webService));
    }

    public void generateJavaDocs() {
        portHandler.sendMessage(new GenerateJavaDocRequest());
    }

    //Helper methods

    /**
     * Requests that the applet put up the Moving Room.
     */
    private void putUpLoading() {
        MainObjectFactory.getIMoveRequestProcessor().loadMovingRoom();
    }

    public void generateWriterPayment(int coderId, double amount, int roundId) {
        GeneratePaymentRequest request = new GeneratePaymentRequest(coderId, 
                amount, GeneratePaymentRequest.WRITER_PAYMENT);
        request.setRoundID(roundId);
        portHandler.sendMessage(request);
    }

    public void generateTesterPayment(int coderId, double amount, int roundId) {
        GeneratePaymentRequest request = new GeneratePaymentRequest(coderId, 
                amount, GeneratePaymentRequest.TESTER_PAYMENT);
        request.setRoundID(roundId);
        portHandler.sendMessage(request);
    }
}
