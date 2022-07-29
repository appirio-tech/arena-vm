/*
 * Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.client.mpsqasApplet.server.defaultimpl;

import com.topcoder.client.mpsqasApplet.server.ResponseHandler;
import com.topcoder.client.mpsqasApplet.messaging.message.*;
import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.shared.problem.*;

import java.util.*;
import java.lang.reflect.*;

/**
 * <p>
 * Processes incoming messages from the server.  Different component that
 * implement com.topcoder.client.mpsqasApplet.messaging.*ResponseProcessor
 * can register themselves with the ResponseHandler to receive information
 * about certain types of responses.  When a response comes in, all
 * components which have registered for the type of response are notified
 * of the message.
 * </p>
 *
 * <p>
 * <strong>Change log:</strong>
 * </p>
 *
 * <p>
 * Version 1.1 (Release Assembly - Dynamic Round Type List For Long and Individual Problems):
 * <ol>
 * <li>
 * Updated {@link #processLoginResponse(LoginResponse, Set)} to support
 * {@link com.topcoder.netCommon.mpsqas.LookupValues} parameter.
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong><br/>
 * This class is mutable and not thread-safe.
 * </p>
 *
 * @author mitalub, TCSASSEMBLER
 * @version 1.1
 */
public class ResponseHandlerImpl implements ResponseHandler {

    /**
     * HashMap of Class -> Set where Class is the Class of a type
     * of response and the Set is a Set of response processors for the
     * class.
     */
    private HashMap responseProcessors;

    /** Initializes the HashMap of Sets of response processors. */
    public void init() {
        responseProcessors = new HashMap();
    }

    /**
     * Registers a response processor to be interested in a certain class
     * of response.
     */
    public void registerResponseProcessor(Object responseProcessor,
            Class responseType) {
        Set set = (Set) responseProcessors.get(responseType);
        if (set == null) {
            set = new HashSet();
            responseProcessors.put(responseType, set);
        }
        set.add(responseProcessor);
    }

    /**
     * Unregisters a response process for a certain class of response.
     */
    public void unregisterResponseProcessor(Object responseProcessor,
            Class responseType) {
        Set set = (Set) responseProcessors.get(responseType);
        if (set != null) {
            set.remove(responseProcessor);
        }
    }

    /**
     * Takes a message and finds all response processors interested in
     * the message.  It then calls the processing method for the message with
     * the message and the list of interested processors as parameters.
     * The processing method name must be:  <br>
     *
     * <code>process</code> + (the name of the message class) <br>
     *
     * So, for example, if the message is a LoginResponse, the process
     * method signature must be: <br>
     *
     * <code>void processLoginResponse(LoginResponse message, Set processors)
     * </code>
     */
    public synchronized void processMessage(Message message) {
        Set types = responseProcessors.keySet();
        Set interested = new HashSet();
        Class type;

        //get set of interested response processors
        for (Iterator i = types.iterator(); i.hasNext();) {
            type = (Class) i.next();
            if (type.isInstance(message)) {
                interested.addAll((Set) responseProcessors.get(type));
            }
        }

        //call the process method for the message.
        try {
            Class[] classes = {message.getClass(), Set.class};
            String processMethodName = classes[0].getName();

            //prepend "process" and remove the package
            processMethodName = "process" + processMethodName.substring(
                    processMethodName.lastIndexOf(".") + 1);

            //invoke the method
            Method method = this.getClass().getMethod(processMethodName, classes);
            method.invoke(this, new Object[]{message, interested});
        } catch (Exception e) {
            System.out.println("No processing method for " + message);
            e.printStackTrace();
        }
    }

    /**
     * Calls the method specified by <code>method</code> and
     * <code>classes</code> on all Objects in <code>processors</code>
     * with the specified args.
     */
    private void notifyProcessors(Set processors, String method,
            Class[] classes, Object[] args) {
        Method methodO = null;
        Object o = null;
        for (Iterator i = processors.iterator(); i.hasNext();) {
            try {
                o = i.next();
                methodO = o.getClass().getMethod(method, classes);
                methodO.invoke(o, args);
            } catch (Exception e) {
                System.out.println("Error invoking method on " + o +
                        " for response processing.");
                e.printStackTrace();
            }
        }
    }

    public void processViewTeamProblemMoveResponse(ViewTeamProblemMoveResponse
            message, Set processors) {
        notifyProcessors(processors, "loadViewTeamProblemRoom",
                new Class[]{ProblemInformation.class, boolean.class},
                new Object[]{message.getProblem(),
                             new Boolean(message.isStatementEditable())});
    }
    
    public void processViewLongProblemMoveResponse(ViewLongProblemMoveResponse
            message, Set processors) {
        notifyProcessors(processors, "loadViewLongProblemRoom",
                new Class[]{ProblemInformation.class, boolean.class},
                new Object[]{message.getProblem(),
                             new Boolean(message.isStatementEditable())});
    }

    public void processFoyerMoveResponse(FoyerMoveResponse message,
            Set processors) {
        notifyProcessors(processors, "loadFoyerRoom",
                new Class[]{ArrayList.class},
                new Object[]{message.getProblems()});
    }

    public void processApplicationRoomMoveResponse(
            ApplicationRoomMoveResponse message, Set processors) {
        notifyProcessors(processors, "loadApplicationRoom",
                new Class[]{int.class},
                new Object[]{new Integer(message.getApplicationType())});
    }

    public void processMainApplicationMoveResponse(
            MainApplicationMoveResponse message, Set processors) {
        notifyProcessors(processors, "loadMainApplicationRoom",
                new Class[]{ArrayList.class},
                new Object[]{message.getApplications()});
    }

    public void processMainProblemMoveResponse(
            MainProblemMoveResponse message, Set processors) {
        notifyProcessors(processors, "loadMainProblemRoom",
                new Class[]{HashMap.class},
                new Object[]{message.getProblems()});
    }

    public void processMainUserMoveResponse(
            MainUserMoveResponse message, Set processors) {
        notifyProcessors(processors, "loadMainUserRoom",
                new Class[]{ArrayList.class},
                new Object[]{message.getUsers()});
    }

    public void processUpcomingContestsMoveResponse(
            UpcomingContestsMoveResponse message, Set processors) {
        notifyProcessors(processors, "loadMainContestRoom",
                new Class[]{ArrayList.class},
                new Object[]{message.getContests()});
    }

    public void processViewApplicationMoveResponse(
            ViewApplicationMoveResponse message, Set processors) {
        notifyProcessors(processors, "loadViewApplicationRoom",
                new Class[]{ApplicationInformation.class},
                new Object[]{message.getApplication()});
    }

    public void processViewContestMoveResponse(
            ViewContestMoveResponse message, Set processors) {
        notifyProcessors(processors, "loadViewContestRoom",
                new Class[]{ContestInformation.class},
                new Object[]{message.getContest()});
    }

    public void processViewProblemMoveResponse(
            ViewProblemMoveResponse message, Set processors) {
        notifyProcessors(processors, "loadViewProblemRoom",
                new Class[]{ProblemInformation.class, boolean.class},
                new Object[]{message.getProblem(),
                             new Boolean(message.isStatementEditable())});
    }
    
    public void processGeneratePaymentResponse(
            GeneratePaymentResponse message, Set processors) {
        notifyProcessors(processors, "processPaymentResponse",
                new Class[]{ArrayList.class, ArrayList.class},
                new Object[]{message.getWriterPayments(),
                             message.getTesterPayments()});
    }

    public void processViewUserMoveResponse(
            ViewUserMoveResponse message, Set processors) {
        notifyProcessors(processors, "loadViewUserRoom",
                new Class[]{UserInformation.class},
                new Object[]{message.getUser()});
    }

    public void processMovingMoveResponse(
            MovingMoveResponse message, Set processors) {
        notifyProcessors(processors, "loadMovingRoom", new Class[0], new Object[0]);
    }

    public void processLoginMoveResponse(
            LoginMoveResponse message, Set processors) {
        notifyProcessors(processors, "loadLoginRoom", new Class[0], new Object[0]);
    }

    public void processViewWebServiceMoveResponse(ViewWebServiceMoveResponse
            message, Set processors) {
        notifyProcessors(processors, "loadWebServiceRoom",
                new Class[]{WebServiceInformation.class, boolean.class},
                new Object[]{message.getWebService(),
                             new Boolean(message.isEditable())});
    }

    public void processViewComponentMoveResponse(
            ViewComponentMoveResponse message, Set processors) {
        notifyProcessors(processors, "loadViewComponentRoom",
                new Class[]{ComponentInformation.class,
                            boolean.class},
                new Object[]{message.getComponent(),
                             new Boolean(message.isStatementEditable())});
    }

    public void processMainTeamProblemMoveResponse(
            MainTeamProblemMoveResponse message, Set processors) {
        notifyProcessors(processors, "loadMainTeamProblemRoom",
                new Class[]{HashMap.class},
                new Object[]{message.getProblems()});
    }
    
    public void processMainLongProblemMoveResponse(
            MainLongProblemMoveResponse message, Set processors) {
        notifyProcessors(processors, "loadMainLongProblemRoom",
                new Class[]{HashMap.class},
                new Object[]{message.getProblems()});
    }

    public void processCreateProblemResponse(
            CreateProblemResponse message, Set processors) {
        notifyProcessors(processors, "loadEmptyViewProblemRoom",
                new Class[0], new Object[0]);
    }

    public void processCreateTeamProblemResponse(
            CreateTeamProblemResponse message, Set processors) {
        notifyProcessors(processors, "loadEmptyViewTeamProblemRoom",
                new Class[0], new Object[0]);
    }
    
    public void processCreateLongProblemResponse(
            CreateLongProblemResponse message, Set processors) {
        notifyProcessors(processors, "loadEmptyViewLongProblemRoom",
                new Class[0], new Object[0]);
    }

    /**
     * Processes login response.
     *
     * @param message Login response message.
     * @param processors Processors to notify.
     */
    public void processLoginResponse(LoginResponse message, Set processors) {
        String method;
        Class[] classes;
        Object[] objects;
        if (message.isSuccess()) {
            method = "processAcceptedLogin";
            classes = new Class[]{boolean.class, boolean.class, boolean.class, LookupValues.class};
            objects = new Object[]{new Boolean(message.isAdmin()),
                                   new Boolean(message.isWriter()),
                                   new Boolean(message.isTester()),
                                   message.getLookupValues()};
        } else {
            method = "processRefusedLogin";
            classes = new Class[]{String.class};
            objects = new Object[]{message.getErrorMessage()};
        }
        notifyProcessors(processors, method, classes, objects);
    }

    public void processNewStatusMessage(NewStatusMessage message,
            Set processors) {
        notifyProcessors(processors, "processNewMessage",
                new Class[]{String.class, boolean.class},
                new Object[]{message.getMessage(),
                             new Boolean(message.isUrgent())});
    }

    public void processPingResponse(PingResponse message, Set processors) {
        notifyProcessors(processors, "processPing", new Class[0], new Object[0]);
    }

    public void processNewCorrespondenceResponse(NewCorrespondenceResponse
            message, Set processors) {
        notifyProcessors(processors, "processNewCorrespondence",
                new Class[]{Correspondence.class},
                new Object[]{message.getCorrespondence()});
    }

    public void processArgEntryResponse(ArgEntryResponse message, Set processors) {
        notifyProcessors(processors, "getArgs",
                new Class[]{DataType[].class, int.class},
                new Object[]{message.getDataTypes(),
                             new Integer(message.getTestType())});
    }

    public void processPopupResponse(PopupResponse message, Set processors) {
        notifyProcessors(processors, "popupMessage",
                new Class[]{String.class},
                new Object[]{message.getMessage()});
    }

    public void processApplicationReplyResponse(ApplicationReplyResponse message,
            Set processors) {
        notifyProcessors(processors, "processApplicationReply",
                new Class[]{boolean.class, String.class},
                new Object[]{new Boolean(message.isSuccess()),
                             message.getMessage()});
    }

    public void processProblemModifiedResponse(ProblemModifiedResponse message,
            Set processors) {
        notifyProcessors(processors, "processProblemModified",
                new Class[]{String.class},
                new Object[]{message.getModifierName()});
    }

    public void processPreviewProblemStatementResponse(
            PreviewProblemStatementResponse message, Set processors) {
        if (message.getType() == MessageConstants.PROBLEM_STATEMENT) {
            notifyProcessors(processors, "processStatementPreview",
                    new Class[]{Problem.class},
                    new Object[]{message.getProblem()});
        } else {
            notifyProcessors(processors, "processStatementPreview",
                    new Class[]{ProblemComponent.class},
                    new Object[]{message.getComponent()});
        }
    }

    public void processNewProblemIdStructureResponse(
            NewProblemIdStructureResponse message, Set processors) {
        notifyProcessors(processors, "processNewIdStructure",
                new Class[]{ProblemIdStructure.class},
                new Object[]{message.getProblemIdStructure()});
    }

    public void processNewComponentIdStructureResponse(
            NewComponentIdStructureResponse message, Set processors) {
        notifyProcessors(processors, "processNewIdStructure",
                new Class[]{ComponentIdStructure.class},
                new Object[]{message.getComponentIdStructure()});
    }

    public void processGenerateJavaDocResponse(
            GenerateJavaDocResponse message, Set processors) {
        notifyProcessors(processors, "processJavaDocUpdate",
                new Class[]{String.class},
                new Object[]{message.getHtml()});
    }

    public void processExchangeKeyResponse(ExchangeKeyResponse
            message, Set processors) {
        notifyProcessors(processors, "processExchangeKey",
                new Class[]{byte[].class},
                new Object[]{message.getKey()});
    }
}
