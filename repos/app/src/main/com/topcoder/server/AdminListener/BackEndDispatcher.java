package com.topcoder.server.AdminListener;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import com.topcoder.server.AdminListener.request.BackEndChangeRoundRequest;
import com.topcoder.server.AdminListener.request.BackEndLoginRequest;
import com.topcoder.server.AdminListener.request.BackEndRefreshAccessRequest;
import com.topcoder.server.AdminListener.request.BackEndRoundAccessRequest;
import com.topcoder.server.AdminListener.request.BackupTablesRequest;
import com.topcoder.server.AdminListener.request.BlobColumnRequest;
import com.topcoder.server.AdminListener.request.ClearCacheRequest;
import com.topcoder.server.AdminListener.request.ClientCommandRequest;
import com.topcoder.server.AdminListener.request.ContestManagementRequest;
import com.topcoder.server.AdminListener.request.CreateSystestsRequest;
import com.topcoder.server.AdminListener.request.GenerateTemplateCommand;
import com.topcoder.server.AdminListener.request.GetBackupCopiesRequest;
import com.topcoder.server.AdminListener.request.GetPrincipalsRequest;
import com.topcoder.server.AdminListener.request.GetRoundProblemComponentsRequest;
import com.topcoder.server.AdminListener.request.ImportantMessagesRequest;
import com.topcoder.server.AdminListener.request.InsertPracticeRoomRequest;
import com.topcoder.server.AdminListener.request.ObjectSearchRequest;
import com.topcoder.server.AdminListener.request.ObjectUpdateRequest;
import com.topcoder.server.AdminListener.request.RecalculateScoreRequest;
import com.topcoder.server.AdminListener.request.RestartServiceRequest;
import com.topcoder.server.AdminListener.request.RestoreTablesRequest;
import com.topcoder.server.AdminListener.request.RunRatingsRequest;
import com.topcoder.server.AdminListener.request.RunSeasonRatingsRequest;
import com.topcoder.server.AdminListener.request.SecurityCheck;
import com.topcoder.server.AdminListener.request.SecurityManagementRequest;
import com.topcoder.server.AdminListener.request.ServerReplySecurityCheck;
import com.topcoder.server.AdminListener.request.SetForumIDRequest;
import com.topcoder.server.AdminListener.request.SetRoundTermsRequest;
import com.topcoder.server.AdminListener.request.TextColumnRequest;
import com.topcoder.server.AdminListener.request.TextSearchRequest;
import com.topcoder.server.AdminListener.request.TextUpdateRequest;
import com.topcoder.server.AdminListener.request.WarehouseLoadRequest;
import com.topcoder.server.AdminListener.response.BackEndResponse;
import com.topcoder.server.AdminListener.response.BlobColumnResponse;
import com.topcoder.server.AdminListener.response.ChangeRoundResponse;
import com.topcoder.server.AdminListener.response.CommandFailedResponse;
import com.topcoder.server.AdminListener.response.CommandSucceededResponse;
import com.topcoder.server.AdminListener.response.InsufficientRightsResponse;
import com.topcoder.server.AdminListener.response.LoginResponse;
import com.topcoder.server.AdminListener.response.ObjectSearchResponse;
import com.topcoder.server.AdminListener.response.ObjectUpdateResponse;
import com.topcoder.server.AdminListener.response.RefreshAccessResponse;
import com.topcoder.server.AdminListener.response.RoundAccessResponse;
import com.topcoder.server.AdminListener.response.SecurityManagementAck;
import com.topcoder.server.AdminListener.response.TextColumnResponse;
import com.topcoder.server.AdminListener.response.TextSearchResponse;
import com.topcoder.server.AdminListener.response.TextUpdateResponse;
import com.topcoder.server.AdminListener.response.VettedServerResponse;
import com.topcoder.server.AdminListener.response.WarehouseLoadAck;
import com.topcoder.server.ejb.AdminServices.AdminServices;
import com.topcoder.server.ejb.AdminServices.AdminServicesLocator;
import com.topcoder.server.ejb.ProblemServices.ProblemServices;
import com.topcoder.server.ejb.ProblemServices.ProblemServicesLocator;
import com.topcoder.server.util.QueueReaderThread;
import com.topcoder.server.util.TCLinkedQueue;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.util.SimpleResourceBundle;

/**
 * Updated for AdminTool 2.0
 * <p>Changed the receivedVettedClientRequest method to handle the new
 * SecurityManagementRequest requests. It calls the ContestManagementProcessor
 * perform the actual request.
 *
 * @author TCDEVELOPER
 */
public class BackEndDispatcher implements QueueReaderThread.Client {

    private AdminServices adminServices;
    private ProblemServices problemServices;
    private AdminProcessor processor;
    private QueueReaderThread reader;
    private ContestManagementProcessor contestManagementProcessor;

    private static final Logger log = Logger.getLogger(BackEndDispatcher.class);

    BackEndDispatcher(TCLinkedQueue backEndQueue, AdminProcessor processor,
            ContestManagementProcessor contestManagementProcessor) throws Exception {
        this.processor = processor;
        this.contestManagementProcessor = contestManagementProcessor;
        // Get the EJB handle
        try {
            log.info("Opening database connection");
            adminServices = AdminServicesLocator.getService();
            problemServices = ProblemServicesLocator.getService();
            reader = new QueueReaderThread(backEndQueue, this, "Back end queue reader");
            contestManagementProcessor.setAdminServices(adminServices);
        } catch (Exception e) {
            log.error("Error getting EJB connection", e);
            throw e;
        }
    }

    void start() {
        reader.start();
    }

    void stop() {
        reader.stop();
    }

    private void sendResponseToClient(int clientId, Object response) {
        // Place the response on the queue to go back to the sender.
        BackEndResponse br = new BackEndResponse(clientId, response);
        processor.receive(AdminConstants.BACK_END_CONNECTION_ID, br);
    }

    public void receivedQueueItem(Object request) {
        // The calls to the ejb go here based on request contents.
        // Actually, this function delegates to other functions which do call the ejb.
        log.info("Backend processing: " + request);
        if (request instanceof ClientCommandRequest) {
            receivedVettedClientRequest((ClientCommandRequest) request);
        } else if (request instanceof SecurityCheck) {
            checkClientRequestAccess((SecurityCheck) request);
        } else if (request instanceof ServerReplySecurityCheck) {
            checkServerReplyAccess((ServerReplySecurityCheck) request);
        } else if (request instanceof BackEndLoginRequest) {
            receivedLoginRequest((BackEndLoginRequest) request);
        } else if (request instanceof BackEndRoundAccessRequest) {
            receivedRoundAccessRequest((BackEndRoundAccessRequest) request);
        } else if (request instanceof BackEndChangeRoundRequest) {
            receivedChangeRoundRequest((BackEndChangeRoundRequest) request);
        } else if (request instanceof BackEndRefreshAccessRequest) {
            receivedRefreshAccessRequest((BackEndRefreshAccessRequest) request);
        } else {
            log.error("Unsupported request type found in back end queue: " + request.getClass().toString());
        }
    }

    /**
     * This method was updated for AdminTool 2.0 to process the 
     * new SecurityManagementRequest requests.
     * 
     * @param request - client request
     */
    private void receivedVettedClientRequest(ClientCommandRequest request) {
        Object requestObject = request.getRequestObject();
        Object responseObject = null;
        try {
            if (requestObject instanceof ContestManagementRequest) {
                responseObject = contestManagementProcessor.processRequest((ContestManagementRequest) requestObject);
            } else if (requestObject instanceof CreateSystestsRequest) {
                responseObject = createSystestsFromChallenges((CreateSystestsRequest) requestObject);
            } else if (requestObject instanceof RunRatingsRequest) {
                responseObject = adminServices.runRatings((RunRatingsRequest) requestObject);
            } else if (requestObject instanceof RunSeasonRatingsRequest) {
                responseObject = adminServices.runSeasonRatings((RunSeasonRatingsRequest) requestObject);
            } else if (requestObject instanceof InsertPracticeRoomRequest) {
                responseObject = adminServices.insertPracticeRooms((InsertPracticeRoomRequest) requestObject);
            } else if (requestObject instanceof ObjectSearchRequest) {
                // Failure should return an ObjectSearchResponse
                try {
                    responseObject = adminServices.runObjectSearch((ObjectSearchRequest) requestObject);
                } catch (Exception e) {
                    log.error("Error running object search", e);
                    responseObject = new ObjectSearchResponse(false, "Error running object search: " + e, null);
                }
            } else if (requestObject instanceof BlobColumnRequest) {
                // Failure should return a BlobColumnResponse
                try {
                    responseObject = adminServices.getBlobColumnMetadata();
                } catch (Exception e) {
                    log.error("Error getting blob column metadata", e);
                    responseObject = new BlobColumnResponse(false, null);
                }
            } else if (requestObject instanceof ObjectUpdateRequest) {
                // Failure should return an ObjectUpdateResponse
                try {
                    responseObject = adminServices.updateDBObject((ObjectUpdateRequest) requestObject);
                } catch (Exception e) {
                    log.error("Error updating object", e);
                    responseObject = new ObjectUpdateResponse(false, e.toString());
                }
            } else if (requestObject instanceof TextSearchRequest) {
                // Failure should return an ObjectSearchResponse
                try {
                    responseObject = adminServices.runTextSearch((TextSearchRequest) requestObject);
                } catch (Exception e) {
                    log.error("Error running object search", e);
                    responseObject = new TextSearchResponse(false, "Error running object search: " + e, null);
                }
            } else if (requestObject instanceof TextColumnRequest) {
                // Failure should return a BlobColumnResponse
                try {
                    responseObject = adminServices.getTextColumnMetadata();
                } catch (Exception e) {
                    log.error("Error getting text column metadata", e);
                    responseObject = new TextColumnResponse(false, null);
                }
            } else if (requestObject instanceof TextUpdateRequest) {
                // Failure should return an ObjectUpdateResponse
                try {
                    responseObject = adminServices.updateDBText((TextUpdateRequest) requestObject);
                } catch (Exception e) {
                    log.error("Error updating text", e);
                    responseObject = new TextUpdateResponse(false, e.toString());
                }
            } else if (requestObject instanceof SetForumIDRequest) {
                SetForumIDRequest forumRequest = (SetForumIDRequest) requestObject;
                responseObject = adminServices.setForumID(forumRequest.getRoundID(), forumRequest.getForumID());
            } else if (requestObject instanceof SecurityManagementRequest) {
                // Failure should return an SecurityManagementAck with exception
                log.info("Backend dispatcher received SecurityManagementRequest" );
                log.info("operation = " + ((SecurityManagementRequest)requestObject).
                        getOperation() + " target = " +  
                        ((SecurityManagementRequest)requestObject).getTarget());
                try {
                    responseObject = new SecurityManagementAck(); 
                    contestManagementProcessor.processSecurityRequest((SecurityManagementRequest) requestObject);
                } catch (Exception e) {
                    log.error("Error in security", e);
                    responseObject = new SecurityManagementAck(e);
                }
                /* TODO: move this crap out of here into a processor */
            } else if (requestObject instanceof BackupTablesRequest) {
                BackupTablesRequest req = (BackupTablesRequest)requestObject;
                responseObject = contestManagementProcessor.processBackupTables(req.getRoundID(), req.getTables(), req.getComment());
            } else if (requestObject instanceof SetRoundTermsRequest) {
                SetRoundTermsRequest req = (SetRoundTermsRequest)requestObject;
                responseObject = contestManagementProcessor.processSetRoundTerms(req.getRoundID(), req.getProperties());
            } else if (requestObject instanceof ImportantMessagesRequest) {
                responseObject = contestManagementProcessor.processImportantMessagesRequest((ImportantMessagesRequest)requestObject);
            } else if (requestObject instanceof RestartServiceRequest) {
                responseObject = contestManagementProcessor.processRestartService(((RestartServiceRequest) requestObject).getRequestType(), ((RestartServiceRequest) requestObject).getRestartMode());
            } else if (requestObject instanceof WarehouseLoadRequest) {
                WarehouseLoadRequest req = (WarehouseLoadRequest) requestObject;
                try {
                    SimpleResourceBundle bundle = SimpleResourceBundle.getBundle("dwload");
                    String className = bundle.getString("" + req.getRequestID());
                    responseObject = contestManagementProcessor.processWarehouseLoad(className, req.getParams(), req.getRequestID());
                } catch (MissingResourceException e) {
                    responseObject = new WarehouseLoadAck(e);
                } 
            } else if (requestObject instanceof ClearCacheRequest) {
                responseObject = contestManagementProcessor.processClearCache();
            } else if (requestObject instanceof GenerateTemplateCommand) {
                responseObject = contestManagementProcessor.processGenerateTemplate(((GenerateTemplateCommand) requestObject).getRoundID());
            } else if (requestObject instanceof GetBackupCopiesRequest) {
                responseObject = contestManagementProcessor.processGetBackupCopies(((GetBackupCopiesRequest) requestObject).getRoundID());
            } else if (requestObject instanceof RestoreTablesRequest) {
                RestoreTablesRequest req = (RestoreTablesRequest) requestObject;
                responseObject = contestManagementProcessor.processRestoreTables(req.getBackupID(), req.getTables());
            } else if (requestObject instanceof RecalculateScoreRequest) {
                int roundId = ((RecalculateScoreRequest)requestObject).getRoundId();
                String handle = ((RecalculateScoreRequest)requestObject).getHandle();
                responseObject = contestManagementProcessor.processRecalculateScore(roundId, handle);
            } else if (requestObject instanceof GetPrincipalsRequest) {
                responseObject = contestManagementProcessor.processGetPrincipals(
                    ((GetPrincipalsRequest)requestObject).getType());
            } else if (requestObject instanceof GetRoundProblemComponentsRequest) {
                GetRoundProblemComponentsRequest req = (GetRoundProblemComponentsRequest) requestObject;
                if (req.isGlobal())
                    responseObject = contestManagementProcessor.processGetRoundProblemComponents(req.getRoundID());
                else
                    responseObject = contestManagementProcessor.processGetRoundProblemComponents(req.getRoundID(), req.getProblemID(), req.getDivisionID());
             }  else {
                log.error("Unsupported client request type found in back end queue: " + requestObject.getClass().toString());
                return;
            }
        } catch (Exception e) {
            responseObject = new CommandFailedResponse("Command failed with EJB exception: " + e.toString());
            e.printStackTrace(System.out);
        }
        if (responseObject != null) {
            sendResponseToClient(request.getSenderId(), responseObject);
        }
    }

    private Object createSystestsFromChallenges(CreateSystestsRequest request) {
        try {
            // todo HACK - fix the user ID issue
            ArrayList stupidArrayListReturnValue = problemServices.refreshTestCases(request.getRoundID(), 132456);
            Boolean retCode = (Boolean) stupidArrayListReturnValue.get(0);
            if (retCode.booleanValue()) {
                return new CommandSucceededResponse("Test cases refreshed succesfully");
            } else {
                return new CommandFailedResponse((String) stupidArrayListReturnValue.get(1));
            }
        } catch (Exception e) {
            log.error(e);
            return new CommandFailedResponse(e.getMessage());
        }
    }

    private void checkClientRequestAccess(SecurityCheck request) {
        try {
            log.debug("CHECKING:" + request.getRequestObject());
            boolean authorized = adminServices.checkClientRequestAccess(request);
            if (authorized) {
                ClientCommandRequest vettedCommand = new ClientCommandRequest(request.getSenderId(), request.getRequestObject());
                processor.receive(AdminConstants.BACK_END_CONNECTION_ID, vettedCommand);
            } else {
                sendResponseToClient(request.getSenderId(),
                        new InsufficientRightsResponse("You have insufficient rights to perform this operation"));
            }
        } catch (Exception e) {
            log.error(e);
            sendResponseToClient(request.getSenderId(),
                    new InsufficientRightsResponse("EJB invocation failed with the following message\n: " + e));
        }
    }

    private void checkServerReplyAccess(ServerReplySecurityCheck request) {
        try {
            List allowedConnections = adminServices.checkServerReplyAccess(request);
            VettedServerResponse response = new VettedServerResponse(allowedConnections, request.getResponseObject());
            processor.receive(AdminConstants.BACK_END_CONNECTION_ID, response);
        } catch (Exception e) {
            log.error("Error applying security to server reply", e);
        }
    }

    private void receivedLoginRequest(BackEndLoginRequest request) {
        try {
            log.debug("Back end login started:" + request);
            LoginResponse response = adminServices.processLoginRequest(request);
            sendResponseToClient(request.getSenderId(), response);
        } catch (Exception e) {
            log.error(e);
            sendResponseToClient(request.getSenderId(),
                    new CommandFailedResponse("EJB invocation failed with the following message: " + e.toString()));
        }
    }

    private void receivedRoundAccessRequest(BackEndRoundAccessRequest request) {
        try {
            RoundAccessResponse response = adminServices.processRoundAccessRequest(request);
            sendResponseToClient(request.getSenderId(), response);
        } catch (Exception e) {
            log.error(e);
            sendResponseToClient(request.getSenderId(),
                    new CommandFailedResponse("EJB invocation failed with the following message: " + e.toString()));
        }
    }

    private void receivedChangeRoundRequest(BackEndChangeRoundRequest request) {
        try {
            ChangeRoundResponse response = adminServices.processChangeRoundRequest(request);
            sendResponseToClient(request.getSenderId(), response);
        } catch (Exception e) {
            log.error(e);
            sendResponseToClient(request.getSenderId(),
                    new CommandFailedResponse("EJB invocation failed with the following message: " + e.toString()));
        }
    }

    private void receivedRefreshAccessRequest(BackEndRefreshAccessRequest request) {
        try {
            RefreshAccessResponse response = adminServices.processRefreshAccessRequest(request);
            sendResponseToClient(request.getSenderId(), response);
        } catch (Exception e) {
            log.error(e);
            sendResponseToClient(request.getSenderId(),
                    new CommandFailedResponse("EJB invocation failed with the following message: " + e.toString()));
        }
    }
}

