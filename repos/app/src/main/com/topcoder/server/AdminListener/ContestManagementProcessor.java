/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 12, 2002
 * Time: 2:01:34 PM
 */
package com.topcoder.server.AdminListener;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;

import com.topcoder.security.GeneralSecurityException;
import com.topcoder.security.TCSubject;
import com.topcoder.server.AdminListener.request.AddAnswerRequest;
import com.topcoder.server.AdminListener.request.AddContestRequest;
import com.topcoder.server.AdminListener.request.AddMessageRequest;
import com.topcoder.server.AdminListener.request.AddQuestionRequest;
import com.topcoder.server.AdminListener.request.AddRoundRequest;
import com.topcoder.server.AdminListener.request.ContestManagementRequest;
import com.topcoder.server.AdminListener.request.DeleteAnswerRequest;
import com.topcoder.server.AdminListener.request.DeleteContestRequest;
import com.topcoder.server.AdminListener.request.DeleteQuestionRequest;
import com.topcoder.server.AdminListener.request.DeleteRoundRequest;
import com.topcoder.server.AdminListener.request.GetAllContestsRequest;
import com.topcoder.server.AdminListener.request.GetAllImportantMessagesRequest;
import com.topcoder.server.AdminListener.request.GetAnswersRequest;
import com.topcoder.server.AdminListener.request.GetNewIDRequest;
import com.topcoder.server.AdminListener.request.GetProblemsRequest;
import com.topcoder.server.AdminListener.request.GetQuestionsRequest;
import com.topcoder.server.AdminListener.request.GetQueueInfoRequest;
import com.topcoder.server.AdminListener.request.GetRoundsRequest;
import com.topcoder.server.AdminListener.request.ImportantMessagesRequest;
import com.topcoder.server.AdminListener.request.ModifyAnswerRequest;
import com.topcoder.server.AdminListener.request.ModifyContestRequest;
import com.topcoder.server.AdminListener.request.ModifyMessageRequest;
import com.topcoder.server.AdminListener.request.ModifyQuestionRequest;
import com.topcoder.server.AdminListener.request.ModifyRoundRequest;
import com.topcoder.server.AdminListener.request.SaveRoundRoomAssignmentRequest;
import com.topcoder.server.AdminListener.request.SecurityManagementRequest;
import com.topcoder.server.AdminListener.request.SetComponentsRequest;
import com.topcoder.server.AdminListener.request.SetRoundEventsRequest;
import com.topcoder.server.AdminListener.request.SetRoundLanguagesRequest;
import com.topcoder.server.AdminListener.request.SetRoundSegmentsRequest;
import com.topcoder.server.AdminListener.request.SetSurveyRequest;
import com.topcoder.server.AdminListener.request.VerifyRoundRequest;
import com.topcoder.server.AdminListener.response.AddAnswerAck;
import com.topcoder.server.AdminListener.response.AddContestAck;
import com.topcoder.server.AdminListener.response.AddImportantMessageAck;
import com.topcoder.server.AdminListener.response.AddQuestionAck;
import com.topcoder.server.AdminListener.response.AddRoundAck;
import com.topcoder.server.AdminListener.response.BackupTablesAck;
import com.topcoder.server.AdminListener.response.ClearCacheAck;
import com.topcoder.server.AdminListener.response.ContestManagementAck;
import com.topcoder.server.AdminListener.response.ContestManagementSetupMessage;
import com.topcoder.server.AdminListener.response.DeleteAnswerAck;
import com.topcoder.server.AdminListener.response.DeleteContestAck;
import com.topcoder.server.AdminListener.response.DeleteQuestionAck;
import com.topcoder.server.AdminListener.response.DeleteRoundAck;
import com.topcoder.server.AdminListener.response.GenerateTemplateAck;
import com.topcoder.server.AdminListener.response.GetAllContestsAck;
import com.topcoder.server.AdminListener.response.GetAllImportantMessagesAck;
import com.topcoder.server.AdminListener.response.GetAnswersAck;
import com.topcoder.server.AdminListener.response.GetBackupCopiesResponse;
import com.topcoder.server.AdminListener.response.GetNewIDResponse;
import com.topcoder.server.AdminListener.response.GetPrincipalsResponse;
import com.topcoder.server.AdminListener.response.GetProblemsAck;
import com.topcoder.server.AdminListener.response.GetQuestionsAck;
import com.topcoder.server.AdminListener.response.GetQueueInfoResponse;
import com.topcoder.server.AdminListener.response.GetRoundProblemComponentsAck;
import com.topcoder.server.AdminListener.response.GetRoundsAck;
import com.topcoder.server.AdminListener.response.ModifyAnswerAck;
import com.topcoder.server.AdminListener.response.ModifyContestAck;
import com.topcoder.server.AdminListener.response.ModifyMessageAck;
import com.topcoder.server.AdminListener.response.ModifyQuestionAck;
import com.topcoder.server.AdminListener.response.ModifyRoundAck;
import com.topcoder.server.AdminListener.response.RecalculateScoreAck;
import com.topcoder.server.AdminListener.response.RestartServiceAck;
import com.topcoder.server.AdminListener.response.RestoreTablesAck;
import com.topcoder.server.AdminListener.response.SaveRoundRoomAssignmentAck;
import com.topcoder.server.AdminListener.response.SecurityManagementAck;
import com.topcoder.server.AdminListener.response.SetComponentsAck;
import com.topcoder.server.AdminListener.response.SetRoundEventsAck;
import com.topcoder.server.AdminListener.response.SetRoundLanguagesAck;
import com.topcoder.server.AdminListener.response.SetRoundSegmentsAck;
import com.topcoder.server.AdminListener.response.SetRoundTermsAck;
import com.topcoder.server.AdminListener.response.SetSurveyAck;
import com.topcoder.server.AdminListener.response.VerifyRoundAck;
import com.topcoder.server.AdminListener.response.WarehouseLoadAck;
import com.topcoder.server.AdminListener.security.SecurityFacade;
import com.topcoder.shared.common.ApplicationServer;
import com.topcoder.shared.common.TCContext;
import com.topcoder.server.contest.AnswerData;
import com.topcoder.server.contest.ContestData;
import com.topcoder.server.contest.ImportantMessageData;
import com.topcoder.server.contest.RoundEventData;
import com.topcoder.server.contest.RoundLanguageData;
import com.topcoder.server.contest.QuestionData;
import com.topcoder.server.contest.RoundData;
import com.topcoder.server.contest.RoundRoomAssignment;
import com.topcoder.server.contest.RoundSegmentData;
import com.topcoder.server.contest.SurveyData;
import com.topcoder.server.ejb.AdminServices.AdminServices;
import com.topcoder.server.messaging.TopicMessagePublisher;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.util.DBMS;
import java.util.Map;


/**
 * Updated for AdminTool 2.0
 * <br>Added new method processSecurityRequest() to handle SecurityManagementRequest
 * requests. <br>Added processGetNewID to handle requests for new sequence id's.
 * <br>Added new method processGetPrincipals to get the security groups and roles.
 * <br>Changed the processRequest method to handle new requests.
 * <br>Support for processing of newly defined SaveRoundRoomAssignmentRequest
 * <br>added : new private processSaveRoundRoomAssignment(RoundRoomAssignment)
 * method added and processRequest() method is modified to process new type of request.
 *
 * <p>
 * Changes in version 1.0 (TopCoder Competition Engine - Event Support For Registration v1.0):
 * <ol>
 * <li>Added {@link #processSetRoundEvents(RoundEventData eventData)}  method.</li>
 * </ol>
 * </p>

 * @author TCDEVELOPER
 */
class ContestManagementProcessor {
    
    private static final Logger log = Logger.getLogger(ContestManagementProcessor.class);
    
    private AdminServices adminServices;
    private TopicMessagePublisher restartServicePublisher;
    
    /**
     * the SecurityFacade object used to process security requests.
     */
    private SecurityFacade securityFacade;
    
    /**
     * this constructor creates a SecurityFacade for use with security requests
     */
    ContestManagementProcessor() {
        securityFacade = new SecurityFacade();
        try {
            log.info("Initializing RestartService...");
            restartServicePublisher = new TopicMessagePublisher(ApplicationServer.JMS_FACTORY, DBMS.RESTART_TOPIC);
            restartServicePublisher.setPersistent(true);
            restartServicePublisher.setFaultTolerant(false);
            log.info("Initialized RestartService");
        } catch (Exception e) {
            log.fatal("Failed to initialize RestartService", e);
        }
    }
    
    void setAdminServices(AdminServices adminServices) {
        this.adminServices = adminServices;
    }
    
    ContestManagementAck getContestManagementSetupMessage() {
        ContestManagementSetupMessage ret = new ContestManagementSetupMessage();
        log.debug("Assembling contest management setup message...");
        try {
            ret.setRoundTypes(adminServices.getRoundTypes());
            ret.setSeasons(adminServices.getSeasons());
            ret.setRegions(adminServices.getRegions());
            ret.setProblemStatusTypes(adminServices.getProblemStatusTypes());
            ret.setDifficultyLevels(adminServices.getDifficultyLevels());
            ret.setDivisions(adminServices.getDivisions());
            ret.setSurveyStatusTypes(adminServices.getSurveyStatusTypes());
            ret.setQuestionTypes(adminServices.getQuestionTypes());
            ret.setQuestionStyles(adminServices.getQuestionStyles());
            ret.setLanguages(adminServices.getLanguages());
            
        } catch (RemoteException e) {
            log.error(e);
            return new ContestManagementSetupMessage(e);
        } catch (SQLException e) {
            log.error(e);
            return new ContestManagementSetupMessage(e);
        }
        return ret;
    }
    
    Object processImportantMessagesRequest(ImportantMessagesRequest request) {
        Object response = null;
        if (request instanceof GetAllImportantMessagesRequest) {
            response = processGetAllImportantMessages();
        } else if (request instanceof AddMessageRequest) {
            response = processAddMessage(((AddMessageRequest) request).getMessage());
        } else if (request instanceof ModifyMessageRequest) {
            ModifyMessageRequest req = (ModifyMessageRequest) request;
            response = processModifyMessage(req.getId(), req.getMessage());
        } else {
            log.error("Unrecognized request: " + request);
        }
        return response;
    }
    
    /**
     * This method should be modified to include processing of newly defined
     * requests, that are : <code>GetNewIDRequest</code>, <code>
     * GetRegisteredCodersRequest</code>, <code>WarehouseLoadRequest</code>,
     * <code>SetRoundTermsRequest</code>, <code>SaveRoundRoomAssignmentRequest
     * </code>, <code>BackupTablesRequest</code>, <code>GetBackupCopiesRequest
     * </code>, <code>RestartServiceRequest</code>.<p>
     * These requests should be processed as other requests are processed :
     * appropriate private method like <code>processGetNewID(int)</code>
     * should be invoked to get the response for this request.
     *
     * @see com.topcoder.server.AdminListener.request.GetNewIDRequest
     * @see WarehouseLoadRequest
     * @see com.topcoder.server.AdminListener.request.SetRoundTermsRequest
     * @see com.topcoder.server.AdminListener.request.SaveRoundRoomAssignmentRequest
     * @see BackupTablesRequest
     * @see com.topcoder.server.AdminListener.request.RestartServiceRequest
     * @see GetBackupCopiesRequest
     * @see com.topcoder.server.AdminListener.request.GetPrincipalsRequest
     * @see com.topcoder.server.AdminListener.request.SecurityManagementRequest
     */
    Object processRequest(ContestManagementRequest request) {
        Object response = null;
        if (request instanceof GetAllContestsRequest) {
            response = processGetAllContests();
        } else if (request instanceof AddContestRequest) {
            response = processAddContest(((AddContestRequest) request).getContest());
        } else if (request instanceof ModifyContestRequest) {
            ModifyContestRequest req = (ModifyContestRequest) request;
            response = processModifyContest(req.getId(), req.getContest());
        } else if (request instanceof DeleteContestRequest) {
            DeleteContestRequest req = (DeleteContestRequest) request;
            response = processDeleteContest(req.getId());
        } else if (request instanceof GetRoundsRequest) {
            GetRoundsRequest req = (GetRoundsRequest) request;
            response = processGetRounds(req.getContestId());
        } else if (request instanceof AddRoundRequest) {
            AddRoundRequest req = (AddRoundRequest) request;
            response = processAddRound(req.getRound());
        } else if (request instanceof ModifyRoundRequest) {
            ModifyRoundRequest req = (ModifyRoundRequest) request;
            response = processModifyRound(req.getId(), req.getRound());
        } else if (request instanceof DeleteRoundRequest) {
            DeleteRoundRequest req = (DeleteRoundRequest) request;
            response = processDeleteRound(req.getId());
        } else if (request instanceof SetRoundSegmentsRequest) {
            SetRoundSegmentsRequest req = (SetRoundSegmentsRequest) request;
            response = processSetRoundSegments(req.getSegments());
        } else if (request instanceof SetRoundLanguagesRequest) {
            SetRoundLanguagesRequest req = (SetRoundLanguagesRequest) request;
            response = processSetRoundLanguages(req.getLanguages());
        } else if (request instanceof SetRoundEventsRequest) {
            SetRoundEventsRequest req = (SetRoundEventsRequest) request;
            response = processSetRoundEvents(req.getEventData());
        } else if (request instanceof SetSurveyRequest) {
            SetSurveyRequest req = (SetSurveyRequest) request;
            response = processSetSurvey(req.getSurvey());
        } else if (request instanceof GetProblemsRequest) {
            GetProblemsRequest req = (GetProblemsRequest) request;
            response = processGetProblems(req.getRoundID());
        } else if (request instanceof SetComponentsRequest) {
            SetComponentsRequest req = (SetComponentsRequest) request;
            response = processSetProblems(req.getRoundID(), req.getComponents());
        } else if (request instanceof GetQuestionsRequest) {
            GetQuestionsRequest req = (GetQuestionsRequest) request;
            response = processGetQuestions(req.getRoundID());
        } else if (request instanceof AddQuestionRequest) {
            AddQuestionRequest req = (AddQuestionRequest) request;
            response = processAddQuestion(req.getRoundID(), req.getQuestion());
        } else if (request instanceof ModifyQuestionRequest) {
            response = processModifyQuestion(((ModifyQuestionRequest) request).getQuestion());
        } else if (request instanceof DeleteQuestionRequest) {
            response = processDeleteQuestion(((DeleteQuestionRequest) request).getQuestionID());
        } else if (request instanceof GetAnswersRequest) {
            GetAnswersRequest req = (GetAnswersRequest) request;
            response = processGetAnswers(req.getQuestionID());
        } else if (request instanceof AddAnswerRequest) {
            AddAnswerRequest req = (AddAnswerRequest) request;
            response = processAddAnswer(req.getQuestionID(), req.getAnswer());
        } else if (request instanceof ModifyAnswerRequest) {
            response = processModifyAnswer(((ModifyAnswerRequest) request).getAnswer());
        } else if (request instanceof DeleteAnswerRequest) {
            response = processDeleteAnswer(((DeleteAnswerRequest) request).getAnswerID());
        } else if (request instanceof VerifyRoundRequest) {
            response = processVerifyRound(((VerifyRoundRequest) request).getRoundID());
        } else if (request instanceof GetNewIDRequest) {
            response = processGetNewID(((GetNewIDRequest) request).getSequenceID());
        } else if (request instanceof SaveRoundRoomAssignmentRequest) {
            response = processSaveRoundRoomAssignment(
                    ((SaveRoundRoomAssignmentRequest)request).getDetails());
        } else {
            log.error("Unrecognized request: " + request);
        }
        return response;
    }
    
    private GetQueueInfoResponse processGetQueueInfo(GetQueueInfoRequest request) {
        //get the context
        Context ctx;
        
        try {
            ctx = TCContext.getJMSContext();
        } catch(Exception e) {
            return new GetQueueInfoResponse(e);
        }
        
        //lookup the queue connection factory
        QueueConnectionFactory qfact;
        
        try {
            qfact = (QueueConnectionFactory) ctx.lookup(ApplicationServer.JMS_FACTORY);
        } catch(Exception e) {
            return new GetQueueInfoResponse(e);
        }
        
        //get the queue connection, queue, and browser
        QueueConnection qconn;
        try {
            qconn = qfact.createQueueConnection();
        } catch(Exception e) {
            return new GetQueueInfoResponse(e);
        }
        
        QueueSession qsess;
        Queue queue;
        QueueBrowser browser;
        try {
            qsess = qconn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            queue = (Queue) ctx.lookup(request.getQueueName());
            browser = qsess.createBrowser(queue);
        } catch(Exception e) {
            return new GetQueueInfoResponse(e);
        } finally {
            try {
                qconn.close();
            } catch(Exception e) {
            }
        }
        
        StringBuffer sb = new StringBuffer();
        sb.append("Queue Name: " + request.getQueueName() + "\n");
        
        //calc size, class content
        HashMap classes = new HashMap();
        int size = 0;
        try {
            Enumeration en = browser.getEnumeration();
            
            while(en.hasMoreElements()) {
                Object o = ((ObjectMessage)en.nextElement()).getObject();
                String cls = o.getClass().getName();
                
                //cls = o.getClass().get
                
                if(classes.containsKey(cls)) {
                    classes.put(cls, new Integer(((Integer)classes.get(cls)).intValue() + 1));
                } else {
                    classes.put(cls, new Integer(1));
                }
                size++;
            }
            
        } catch(Exception e) {
            return new GetQueueInfoResponse(e);
        } finally {
            try {
                qconn.close();
            } catch(Exception e) {
            }
        }
        
        
        sb.append("Queue Size: " + size + "\n");
        sb.append("Classes: \n");
        for(Iterator i = classes.keySet().iterator(); i.hasNext();) {
            String val = (String)i.next();
            sb.append(val + ": " + ((Integer)classes.get(val)).intValue() + "\n");
        }
        
        return new GetQueueInfoResponse(sb.toString());
    }
    
    private GetAllContestsAck processGetAllContests() {
        log.debug("Processing get all contests...");
        Collection contests = null;
        try {
            contests = adminServices.getAllContests();
        } catch (Exception e) {
            log.error(e);
            return new GetAllContestsAck(e);
        }
        
        return new GetAllContestsAck(contests);
    }
    
    private GetAllImportantMessagesAck processGetAllImportantMessages() {
        log.debug("Processing get all messages...");
        Collection messages = null;
        try {
            messages = adminServices.getAllImportantMessages();
        } catch (Exception e) {
            log.error(e);
            return new GetAllImportantMessagesAck(e);
        }
        
        return new GetAllImportantMessagesAck(messages);
    }
    
    /**
     * Gets the response for request for new ID generated by specified
     * sequence. Uses Admin Services EJB to get the new ID and wraps received
     * ID with GetNewIDResponse object.
     *
     * @param  sequence an ID of sequence that should be used to generate
     *         new ID
     * @return a GetNewIDResponse object containing the int ID that was
     *         generated by specified sequence
     * @since  Admin Tool 2.0
     * @see    GetNewIDResponse
     * @see    AdminServices#getNewID(int)
     */
    private GetNewIDResponse processGetNewID(String sequence) {
        int id = 0;
        if (log.isInfoEnabled())
            log.info("Requesting new id  ID=" + sequence);
        try {
            id = adminServices.getNewID(sequence);
        } catch (Exception e) {
            log.error(e);
            return new GetNewIDResponse(e);
        }
        
        return new GetNewIDResponse(sequence, id);
    }
    
    /**
     * Gets the response for request for list of TCPrincipals of requested type.
     * Uses SecurityFacade to get the Collection of requested principals.
     *
     * Because this object does not have accese to the current TCSubject for
     * the logged in user and it is not passed in the getPrincipals method,
     * we have to pass a 'dummy' requestor. The current security implementation
     * does not check this right now anyway. Future designs should include
     * the ability for this method to know the current TCSubject that is
     * making the request.
     *
     * @param  type a type of requested principals, either AdminConstants.GROUP_PRINCIPALS
     *         or AdminConstants.ROLE_PRINCIPALS
     * @return a GetPrincipalsResponse object containing the Collection
     *         of TCPrincipals objects of requested type
     * @since  Admin Tool 2.0
     * @see    com.topcoder.server.AdminListener.response.GetPrincipalsResponse
     * @see    SecurityFacade
     */
    public GetPrincipalsResponse processGetPrincipals(int type) {
        GetPrincipalsResponse ret = null;
        //TCSubject requester = new TCSubject(0);
        if (log.isInfoEnabled())
            log.info("get principals called for type = " + type );
        try {
            Collection list = null;
            if(type == AdminConstants.ROLE_PRINCIPALS ) {
                list = securityFacade.getRoles(new TCSubject(0));
            } else {
                list = securityFacade.getGroups(new TCSubject(0));
            }
            ret = new GetPrincipalsResponse(type, list );
            
        } catch( GeneralSecurityException gex ) {
            ret = new GetPrincipalsResponse( gex );
        }
        return ret;
    }
    
    /**
     * Performs a security schema management operation represented by specified
     * SecurityManagementRequest. Redirects the request to SecurityFacade
     * for processing.
     *
     * @param request a SecurityManagementRequest containing all necessary data to perfrom the requested operation.
     * @return a SecurityManagementAck indicating about the fulfilment of request
     * @throws IllegalArgumentException if given request is null.
     * @since Admin Tool 2.0
     * @see SecurityManagementAck
     * @see SecurityFacade#processSecurityRequest
     */
    public SecurityManagementAck processSecurityRequest(SecurityManagementRequest request) {
        
        SecurityManagementAck ret = new SecurityManagementAck();
        log.info("security request. operation= " + request.getOperation() + " target=" +
                request.getTarget() + " subject id = " + request.getUserId());
        try {
            securityFacade.processSecurityRequest(request);
        } catch( GeneralSecurityException gex ) {
            ret = new SecurityManagementAck(gex);
        }
        
        return ret;
    }
    
    private AddContestAck processAddContest(ContestData contest) {
        if (log.isInfoEnabled())
            log.info("Adding contest " + contest);
        try {
            adminServices.addContest(contest);
        } catch (Exception e) {
            log.error(e);
            return new AddContestAck(e);
        }
        
        return new AddContestAck();
    }
    
    private ModifyContestAck processModifyContest(int id, ContestData contest) {
        if (log.isInfoEnabled())
            log.info("Modifying contest #" + id + ", new data=" + contest);
        try {
            adminServices.modifyContest(id, contest);
        } catch (Exception e) {
            log.error(e);
            return new ModifyContestAck(e);
        }
        return new ModifyContestAck();
    }
    
    private AddImportantMessageAck processAddMessage(ImportantMessageData message) {
        if (log.isInfoEnabled())
            log.info("Adding message " + message);
        try {
            adminServices.addMessage(message);
        } catch (Exception e) {
            log.error(e);
            return new AddImportantMessageAck(e);
        }
        
        return new AddImportantMessageAck();
    }
    
    private ModifyMessageAck processModifyMessage(int id, ImportantMessageData message) {
        if (log.isInfoEnabled())
            log.info("Modifying message #" + id + ", new data=" + message);
        try {
            adminServices.modifyMessage(id, message);
        } catch (Exception e) {
            log.error(e);
            return new ModifyMessageAck(e);
        }
        return new ModifyMessageAck();
    }
    
    private DeleteContestAck processDeleteContest(int id) {
        if (log.isInfoEnabled())
            log.info("Deleting contest #" + id);
        try {
            int nrounds = adminServices.getNumRounds(id);
            if (nrounds > 0) {
                throw new Exception("This contest has " + nrounds + " rounds associated with it.  Please delete them first.");
            }
            adminServices.deleteContest(id);
            return new DeleteContestAck();
        } catch (Exception e) {
            log.error(e);
            return new DeleteContestAck(e);
        }
    }
    
    
    private GetRoundsAck processGetRounds(int contestID) {
        log.debug("Processing get all rounds request");
        Collection rounds = null;
        try {
            rounds = adminServices.getRounds(contestID);
        } catch (Exception e) {
            log.error(e);
            return new GetRoundsAck(e);
        }
        
        return new GetRoundsAck(rounds);
    }
    
    /**
     * This method is invoked from processRequest() when a GetRoundProblemComponentsRequest
     * is received which contains only a roundID (i.e. request.isGlobal() == true)
     *
     * @param roundID
     */
    public GetRoundProblemComponentsAck processGetRoundProblemComponents(int roundID) {
        log.debug("Processing get all component information for round " + roundID);
        Collection components = null;
        try {
            components = adminServices.getRoundProblemComponents(roundID);
        } catch (Exception e) {
            log.error(e);
            return new GetRoundProblemComponentsAck(e);
        }
        
        return new GetRoundProblemComponentsAck(components);
    }
    
    public GetRoundProblemComponentsAck processGetRoundProblemComponents(int roundID, int problemID, int divisionID) {
        log.debug("Processing get all component information for round " + roundID + ", problem " + problemID + ", division " + divisionID);
        Collection components = null;
        try {
            components = adminServices.getRoundProblemComponents(roundID, problemID, divisionID);
            System.out.println("components = " + components);
        } catch (Exception e) {
            log.error(e);
            return new GetRoundProblemComponentsAck(e);
        }
        
        return new GetRoundProblemComponentsAck(components);
    }
    
    private AddRoundAck processAddRound(RoundData round) {
        if (log.isInfoEnabled())
            log.info("Adding Round " + round);
        try {
            adminServices.addRound(round);
        } catch (Exception e) {
            log.error(e);
            return new AddRoundAck(e);
        }
        
        return new AddRoundAck();
    }
    
    private ModifyRoundAck processModifyRound(int id, RoundData round) {
        if (log.isInfoEnabled())
            log.info("Modifying Round #" + id + ", new data=" + round);
        try {
            adminServices.modifyRound(id, round);
        } catch (Exception e) {
            log.error(e);
            return new ModifyRoundAck(e);
        }
        return new ModifyRoundAck();
    }
    
    private DeleteRoundAck processDeleteRound(int id) {
        if (log.isInfoEnabled())
            log.info("Deleting Round #" + id);
        try {
            adminServices.deleteRound(id);
        } catch (Exception e) {
            log.error(e);
            return new DeleteRoundAck(e);
        }
        return new DeleteRoundAck();
    }
    
    private SetRoundSegmentsAck processSetRoundSegments(RoundSegmentData segments) {
        if (log.isInfoEnabled()) {
            log.info("Setting round segment data for #" + segments.getRoundId());
        }
        try {
            adminServices.setRoundSegments(segments);
        } catch (Exception e) {
            log.error(e);
            return new SetRoundSegmentsAck(e);
        }
        return new SetRoundSegmentsAck();
    }
    
    private SetRoundLanguagesAck processSetRoundLanguages(RoundLanguageData languages) {
        if (log.isInfoEnabled()) {
            log.info("Setting round languages data for #" + languages.getRoundId());
        }
        try {
            adminServices.setRoundLanguages(languages);
        } catch (Exception e) {
            log.error(e);
            return new SetRoundLanguagesAck(e);
        }
        return new SetRoundLanguagesAck();
    }
    /**
     * <p>
     * process the set round events action.
     * </p>
     * @param eventData
     *         the round event data.
     * @return the set round event ack.
     */
    private SetRoundEventsAck processSetRoundEvents(RoundEventData eventData) {
        if (log.isInfoEnabled()) {
            log.info("Setting round events data for #" + eventData.getRoundId());
        }
        try {
            adminServices.setRoundEvents(eventData);
        } catch (Exception e) {
            log.error(e);
            return new SetRoundEventsAck(e);
        }
        return new SetRoundEventsAck();
    }
    
    private SetSurveyAck processSetSurvey(SurveyData survey) {
        if (log.isInfoEnabled()) {
            log.info("Setting survey data for #" + survey.getId());
        }
        try {
            adminServices.setSurvey(survey);
        } catch (Exception e) {
            log.error(e);
            return new SetSurveyAck(e);
        }
        return new SetSurveyAck();
    }
    
    
    private GetProblemsAck processGetProblems(int roundID) {
        if (log.isDebugEnabled())
            log.debug("Processing get all problems request for round #" + roundID);
        Collection problems = null;
        Collection assignedProblems = null;
        try {
            problems = adminServices.getProblems();
            assignedProblems = adminServices.getAssignedProblems(roundID);
        } catch (Exception e) {
            log.error(e);
            return new GetProblemsAck(e);
        }
        
        return new GetProblemsAck(problems, assignedProblems);
    }
    
    private SetComponentsAck processSetProblems(int roundID, Collection problems) {
        if (log.isInfoEnabled()) {
            log.info("Setting problems for round #" + roundID);
        }
        try {
            adminServices.setComponents(roundID, problems);
        } catch (Exception e) {
            log.error(e);
            return new SetComponentsAck(e);
        }
        return new SetComponentsAck();
    }
    
    private GetQuestionsAck processGetQuestions(int roundID) {
        if (log.isInfoEnabled()) {
            log.info("Getting questions for round #" + roundID);
        }
        try {
            return new GetQuestionsAck(
                    adminServices.getQuestions(roundID)
                    );
        } catch (Exception e) {
            log.error(e);
            return new GetQuestionsAck(e);
        }
    }
    
    private AddQuestionAck processAddQuestion(int roundID, QuestionData question) {
        if (log.isInfoEnabled()) {
            log.info("Adding question " + question + " to round #" + roundID);
        }
        try {
            return new AddQuestionAck(
                    adminServices.addQuestion(roundID, question)
                    );
        } catch (Exception e) {
            log.error(e);
            return new AddQuestionAck(e);
        }
    }
    
    private ModifyQuestionAck processModifyQuestion(QuestionData question) {
        if (log.isInfoEnabled()) {
            log.info("Modifying question " + question);
        }
        try {
            adminServices.modifyQuestion(question);
        } catch (Exception e) {
            log.error(e);
            return new ModifyQuestionAck(e);
        }
        return new ModifyQuestionAck();
    }
    
    
    private DeleteQuestionAck processDeleteQuestion(int questionID) {
        if (log.isInfoEnabled()) {
            log.info("Deleting question #" + questionID);
        }
        try {
            adminServices.deleteQuestion(questionID);
        } catch (Exception e) {
            log.error(e);
            return new DeleteQuestionAck(e);
        }
        return new DeleteQuestionAck();
    }
    
    private GetAnswersAck processGetAnswers(int questionID) {
        if (log.isInfoEnabled()) {
            log.info("Getting answers for question #" + questionID);
        }
        try {
            return new GetAnswersAck(
                    adminServices.getAnswers(questionID)
                    );
        } catch (Exception e) {
            log.error(e);
            return new GetAnswersAck(e);
        }
    }
    
    private AddAnswerAck processAddAnswer(int questionID, AnswerData answer) {
        if (log.isInfoEnabled()) {
            log.info("Adding answer " + answer + " to question #" + questionID);
        }
        try {
            return new AddAnswerAck(
                    adminServices.addAnswer(questionID, answer)
                    );
        } catch (Exception e) {
            log.error(e);
            return new AddAnswerAck(e);
        }
    }
    
    private ModifyAnswerAck processModifyAnswer(AnswerData answer) {
        if (log.isInfoEnabled()) {
            log.info("Modifying answer " + answer);
        }
        try {
            adminServices.modifyAnswer(answer);
        } catch (Exception e) {
            log.error(e);
            return new ModifyAnswerAck(e);
        }
        return new ModifyAnswerAck();
    }
    
    
    private DeleteAnswerAck processDeleteAnswer(int answerID) {
        if (log.isInfoEnabled()) {
            log.info("Deleting answer #" + answerID);
        }
        try {
            adminServices.deleteAnswer(answerID);
        } catch (Exception e) {
            log.error(e);
            return new DeleteAnswerAck(e);
        }
        return new DeleteAnswerAck();
    }
    
    private VerifyRoundAck processVerifyRound(int roundID) {
        try {
            return new VerifyRoundAck(
                    new RoundVerifier(adminServices).verify(roundID)
                    );
        } catch (Exception e) {
            log.error(e);
            return new VerifyRoundAck(e);
        }
    }
    
    
    /**
     * Process the request to create backup copies of specified tables. Uses
     * Admin Services EJB to perform the action.
     *
     * @param  roundID an ID of requested round
     * @param  tableNames a Set of String table names that should be backed up
     * @return a BackupTablesAck indicating about successful or unsuccessful
     *         fulfilment of request
     * @since  Admin Tool 2.0
     * @see    AdminServices#backupTables(int, Set, String)
     */
    public BackupTablesAck processBackupTables(int roundID, Set tableNames, String comment) {
        if (log.isInfoEnabled()) {
            log.info("Backup tables " + tableNames.toString() + " for round " + roundID + ", " + comment);
        }
        try {
            adminServices.backupTables(roundID, tableNames, comment);
        } catch (Exception e) {
            log.error(e);
            return new BackupTablesAck(e);
        }
        return new BackupTablesAck();
    }
    
    /**
     * Gets the response for request for list of existing backup copies for
     * specified round. Uses Admin Services EJB to get the List of
     * of BackupCopy objects representing existing backup copies for
     * specified round and wraps it with GetBackupCopiesResponse object.
     *
     * @return a GetBackupCopiesResponse object containing the List
     *         of BackupCopy objects
     * @since  Admin Tool 2.0
     * @see    GetBackupCopiesResponse
     * @see    AdminServices#getBackupCopies(int)
     */
    public GetBackupCopiesResponse processGetBackupCopies(int roundID) {
        if (log.isInfoEnabled()) {
            log.info("Get backup tables for round"  + roundID);
        }
        List copies = null;
        try {
            copies = adminServices.getBackupCopies(roundID);
        } catch (Exception e) {
            log.error(e);
            return new GetBackupCopiesResponse(e);
        }
        return new GetBackupCopiesResponse(roundID, copies);
    }
    
    /**
     * Process the request to restore specified tables from specified backup
     * copy. Uses Admin Services EJB to perform the action.
     *
     * @param  backupID an ID of requested backup copy to restore tables from
     * @param  tableNames a Set of String table names that should be restored
     *         from specified backup copy
     * @return a RestoreTablesAck indicating about successful or unsuccessful
     *         fulfilment of request
     * @throws IllegalArgumentException if given Set is null
     * @since  Admin Tool 2.0
     * @see    AdminServices#restoreTables(int, Set)
     */
    public RestoreTablesAck processRestoreTables(int backupID, Set tableNames) {
        if (log.isInfoEnabled()) {
            log.info("Restore tables " + tableNames.toString() + " from backup with id " + backupID);
        }
        try {
            adminServices.restoreTables(backupID, tableNames);
        } catch (Exception e) {
            log.error(e);
            return new RestoreTablesAck(e);
        }
        return new RestoreTablesAck();
    }
    
    public ClearCacheAck processClearCache() {
        try {
            adminServices.clearCache();
        } catch (Exception e) {
            log.error(e);
            return new ClearCacheAck(e);
        }
        return new ClearCacheAck();
    }
    
    public GenerateTemplateAck processGenerateTemplate(int roundID) {
        try {
            String message = "";
            message = adminServices.generateTemplate(roundID);
            return new GenerateTemplateAck(message);
        } catch (Exception e) {
            log.error(e);
            return new GenerateTemplateAck(e);
        }
    }
    
    
    /**
     * Gets the response to request to perform warehouse load process. Uses
     * Admin Services EJB to execute a load process represented by specified
     * class name and configured with specified params.
     *
     * @param className a fully-qualified name of class that should be used to
     * perform a warehouse load
     * @param params a Hashtable mapping parameter names to parameter values
     * @return a WarehouseLoadAck object indicating that warehouse load process
     * has finished
     * @since Admin Tool 2.0
     * @see WarehouseLoadAck
     * @see AdminServices#loadWarehouseData(String, Hashtable)
     */
    public WarehouseLoadAck processWarehouseLoad(String className, Map params, int type) {
        if (log.isInfoEnabled()) {
            log.info("WarehouseLoad " + params.toString() + " for class " + className);
        }
        try {
            adminServices.loadWarehouseData(className,  params, type);
        } catch (Exception e) {
            log.error(e);
            return new WarehouseLoadAck(e);
        }
        return new WarehouseLoadAck();
    }
    
    /**
     * Performs the request to set the terms for specified round. Uses
     * Admin Services EJB to perform the action.
     *
     * @param  roundID an ID of round to set terms to
     * @param  params a Hashtable mapping parameter names to parameter values
     * @return a SetRoundTermsAck indicating about successful or unsuccessful
     *         fulfilment of request
     * @since  Admin Tool 2.0
     * @see    AdminServices#setRoundTerms(int, Hashtable)
     */
    public SetRoundTermsAck processSetRoundTerms(int roundID,
            Map params) {
        try {
            adminServices.setRoundTerms(roundID, params);
            SetRoundTermsAck ack = new SetRoundTermsAck(roundID);
            return ack;
        } catch (Exception e) {
            return new SetRoundTermsAck(e);
        }
    }
    
    public RecalculateScoreAck processRecalculateScore(int roundId, String handle) {
    	try {
    		adminServices.recalculateScore(roundId, handle);
    		return new RecalculateScoreAck(roundId, handle);
    	}
    	catch (Exception e) {
    		return new RecalculateScoreAck(e);
    	}
    }
    
    /**
     * Processes the request to restart specified service. Publishes a message
     * to newly defined JMS Topic. To do so uses newly defined instance
     * variable <code>restartServicePublisher</code> to publish a message with
     * one additional property named "serviceType" of type int that is set
     * with value of passed argument.
     *
     * @param  serviceType
     * @return a RestartServiceAck indicating about successful or unsuccessful
     *         fulfilment of request
     * @since  Admin Tool 2.0
     */
    public RestartServiceAck processRestartService(int serviceType, int exitCode) {
        try {
            HashMap props = new HashMap();
            props.put("serviceType", new Integer(serviceType));
            props.put("exitCode", new Integer(exitCode));
            restartServicePublisher.pubMessage(props);
            log.info("Publishing message to restart serviceType:" + serviceType);
            return new RestartServiceAck();
        } catch (Exception e) {
            log.error(e);
            return new RestartServiceAck(e);
        }
    }
    
    /**
     * Process the request to save details of room assignment algorithm for
     * some round.  Uses Admin Services EJB to perform the action.
     *
     * @param  details a RoundRoomAssignment instance containing the details
     *         of round room assignment algorithm that need to be saved.
     * @return a SaveRoundRoomAssignmentAck indicating about successful or
     *         unsuccessful fulfilment of request
     * @since  Admin Tool 2.0
     * @see    AdminServices#saveRoundRoomAssignment(com.topcoder.server.contest.RoundRoomAssignment)
     */
    private SaveRoundRoomAssignmentAck
            processSaveRoundRoomAssignment(RoundRoomAssignment details) {
        if (log.isInfoEnabled()) {
            log.info("Setting room assignement data #" + details.getRoundId());
        }
        try {
            adminServices.saveRoundRoomAssignment(details);
        } catch (Exception e) {
            log.error(e);
            return new SaveRoundRoomAssignmentAck(e);
        }
        return new SaveRoundRoomAssignmentAck(details.getRoundId());
    }
}
