/*
* Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * ClientEmulator
 *
 * Created 08/17/2006
 */
package com.topcoder.farm;

import com.topcoder.farm.shared.invocation.InvocationRequirements;

/**
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
 * <ol>
 *      <li>Update {@link #processors} field.</li>
 *      <li>Update {@link #findActiveProcessors()} method.</li>
 *      <li>Update {@link #existsAvailableProcessorFor(InvocationRequirements requirements)} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.0
 */
public class ClientEmulator {
//
//    private static final int MAX_TASK_SIZE = 10;
//    private static final int CLIENT_PORT = IntegConstants.CONTROLLER_CLIENT_PORT;
//    private static final int TIME_TO_WAIT_FOR_SYNC = 5000;
//
//    private int numOfClients;
//    private int minClInstances;
//    private int distance;
//    private String clientPrefix;
//    private Random random = new Random(5000);
//    private Map<String, Handler> handlers = new HashMap();
//    private InvocationDAO invDAO;
//    /**
//     * <p>
//     * the processors.
//     * </p>
//     */
//    private List<ProcessorProperties> processors;
//
//    public static void main(String[] args) {
//        ClientEmulator emulator = new ClientEmulator();
//        try {
//            emulator.run("CL", 5, 10, 15);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void run(String clientPrefix,
//                            int numOfClients,
//                            int minClInstances,
//                            int maxClInstances) throws Exception {
//
//        this.numOfClients = numOfClients;
//        this.minClInstances = minClInstances;
//        this.distance = maxClInstances - minClInstances + 1;
//        this.clientPrefix = clientPrefix;
//        ClientConfiguration configuration = new ClientConfiguration();
//        configuration.setAddresses(new InetSocketAddress[] {new InetSocketAddress(InetAddress.getLocalHost(), CLIENT_PORT)});
//        FarmFactory.configure(configuration);
//        try {
//            runTest();
//        } finally {
//            FarmFactory.releaseInstance();
//        }
//        validateResults();
//    }
//
//    private void validateResults() {
//        Collection errors = new ArrayList();
//        HibernateUtil.initialize(ControllerRunner.HIBERNATE_CFG);
//        DAOFactory.configureInstance(new HibernateDAOFactory());
//        processors = findActiveProcessors();
//        for (Handler h : handlers.values()) {
//            for (RequestData requestData : h.requestEvents.values()) {
//                LinkedList<Event> events = requestData.events;
//                InvocationData data = findInvocationData(h.clientId, requestData.request.getId());
//                if (events.size() == 0) {
//                    errors.add(new ErrorData("Invalid number of events", requestData));
//                    continue;
//                }
//                Event event = events.get(0);
//                if (requestData.requestType == Event.INVOCATION_SYNC) {
//                    if (events.size() == 1) {
//                        if (event.type == Event.EXCEPTION) {
//                            verifyException(errors, requestData, event, data);
//                            continue;
//                        }
//                        if (event.type != Event.RESPONSE_SYNC) {
//                            errors.add(new ErrorData("SYNC: 1 event and not response-sync", requestData));
//                            continue;
//                        }
//                        checkValidResponse(errors, requestData, (InvocationResponse)event.value, data);
//                    } else {
//                        if (events.size() > 2) {
//                            errors.add(new ErrorData("SYNC: more than 2 events", requestData));
//                            continue;
//                        }
//                        if (event.ex == null || !TimeoutException.class.equals(event.ex.getClass())) {
//                            errors.add(new ErrorData("SYNC: 2 events and first is not timeout", requestData));
//                            continue;
//                        }
//                        event = events.get(1);
//                        checkValidResponse(errors, requestData, (InvocationResponse)event.value, data);
//                    }
//                } else {
//                    if (events.size() > 1) {
//                        errors.add(new ErrorData("ASYNC: more than 1 events", requestData));
//                        continue;
//                    }
//                    if (events.size() == 1) {
//                        if (event.type == Event.EXCEPTION) {
//                            verifyException(errors, requestData, event, data);
//                            continue;
//                        }
//                        if (event.type != Event.RESPONSE_ASYNC) {
//                            errors.add(new ErrorData("ASYNC: 1 event and not response-async", requestData));
//                            continue;
//                        }
//                        checkValidResponse(errors, requestData, (InvocationResponse)event.value, data);
//                    }
//                }
//            }
//        }
//        System.out.println(errors);
//    }
//    /**
//     * <p>
//     * find all the active processors.
//     * </p>
//     * @return the active processor list.
//     */
//    private List<ProcessorProperties> findActiveProcessors() {
//        DAOFactory.getInstance().getTransactionManager().beginTransaction();
//        try {
//            return DAOFactory.getInstance().createProcessorDAO().findActiveProcessors();
//        } finally {
//            DAOFactory.getInstance().getTransactionManager().rollback();
//        }
//    }
//
//    /**
//     * @param errors
//     * @param requestData
//     * @param event
//     * @param data
//     */
//    private void verifyException(Collection errors, RequestData requestData, Event event,
//            InvocationData data) {
//
//        if (InvalidRequirementsException.class.equals(event.ex.getClass())) {
//            if (existsAvailableProcessorFor(requestData.request.getRequirements())) {
//                errors.add(new ErrorData("InvalidRequirementsException but processor exists", requestData));
//            }
//        } else if (TimeoutException.class.equals(event.ex.getClass())) {
//            if (requestData.requestType != Event.INVOCATION_SYNC) {
//                errors.add(new ErrorData("TimeoutException for non sync request", requestData));
//            } else {
//                if (data.getStatus() == InvocationData.STATUS_NOTIFIED ||
//                        data.getStatus() == InvocationData.STATUS_SOLVED) {
//
//                }
//            }
//        }
//    }
//
//    private void checkValidResponse(Collection errors, RequestData requestData,
//            InvocationResponse response, InvocationData data) {
//
//    }
//
//    /**
//     * <p>
//     * to check if the exist available processor to meet the invocation requirement.
//     * </p>
//     * @param requirements
//     * @return
//     */
//    private boolean existsAvailableProcessorFor(InvocationRequirements requirements) {
//        for (ProcessorProperties  p : processors) {
//            try {
//                if (p.match(requirements)) {
//                    return true;
//                }
//            } catch (MatchProcessException e) {
//            }
//        }
//        return false;
//    }
//
//
//    private InvocationData findInvocationData(String clientId, String requestId) {
//        DAOFactory.getInstance().getTransactionManager().beginTransaction();
//        try {
//            invDAO = DAOFactory.getInstance().createInvocationDAO();
//            InvocationData data;
//            try {
//                data = invDAO.findByClientKey(clientId, requestId);
//            } catch (RuntimeException e) {
//                e.printStackTrace();
//                return null;
//            }
//            return data;
//        } finally {
//            DAOFactory.getInstance().getTransactionManager().rollback();
//        }
//    }
//
//    private void runTest() throws InterruptedException {
//        FarmFactory instance = FarmFactory.getInstance();
//
//        XThread[][] ts = new XThread[numOfClients][];
//        for (int i = 0; i < numOfClients; i++) {
//            String id = clientPrefix+(i+1);
//            int tsize = minClInstances + nextInt(distance);
//            Handler handler = new Handler(id);
//            handlers.put(id, handler);
//            instance.configureHandler(id, handler);
//            ts[i] = new XThread[tsize];
//            for (int j = 0; j < tsize; j++) {
//                ts[i][j] = new XThread(id, i, j, MAX_TASK_SIZE, handler);
//            }
//        }
//
//        for (int i = 0; i < numOfClients; i++) {
//            for (int j = 0; j < ts[i].length; j++) {
//                ts[i][j].start();
//            }
//        }
//        for (int i = 0; i < numOfClients; i++) {
//            for (int j = 0; j < ts[i].length; j++) {
//                ts[i][j].join();
//            }
//        }
//        Thread.sleep(35000);
//        System.out.println("TIME-END");
//    }
//
//    private synchronized int nextInt(int maxValue) {
//        return random.nextInt(maxValue);
//    }
//
//
//    static InvocationRequest buildInvocationRequest(int procType, int memIndex, int osTypeIndex,
//            int invocationId, String clientId, int threadIndex, String text, int timeToWait,
//            RuntimeException exception) {
//
//        InvocationRequest request = new InvocationRequest();
//        request.setId(""+invocationId);
//        InvocationRequirements req = buildRequirements(procType, memIndex, osTypeIndex);
//        request.setRequirements(req);
//        request.setInvocation(new TestInvocation(""+invocationId, clientId, text, timeToWait, exception));
//        return request;
//    }
//
//    private static InvocationRequirements buildRequirements(int procType, int memIndex, int osTypeIndex) {
//        InvocationRequirements req = new InvocationRequirements();
//        Expression exp = BooleanExpression.TRUE;
//        if (procType != 0) {
//            exp = Expressions.eq(TestDataPropertiesHelper.PROCESSOR_TYPE, TestDataPropertiesHelper.PROC_TYPES[procType-1]);
//        }
//        if (memIndex != 0) {
//            exp = Expressions.and(exp, Expressions.ge(TestDataPropertiesHelper.MEMORY, TestDataPropertiesHelper.MEMORY_SIZES[memIndex-1]));
//        }
//        if (osTypeIndex != 0) {
//            exp = Expressions.and(exp, Expressions.eq(TestDataPropertiesHelper.OS_NAME, TestDataPropertiesHelper.OS_NAMES[osTypeIndex-1]));
//        }
//        req.setFilterExpression(exp);
//        return req;
//    }
//
//    public class XThread extends Thread {
//        private String id;
//        private FarmFactory factory;
//        private int taskSize;
//        private int threadIndexForClient;
//        private Handler handler;
//        private Random random;
//        //private int clientInstanceIndex;
//
//        public XThread(String name, int clientInstanceIndex, int threadIndexForClient, int taskSize, Handler handler) {
//            super(name+"-"+threadIndexForClient);
//            this.id = name;
//            this.factory = FarmFactory.getInstance();
//            this.taskSize = taskSize;
//            this.threadIndexForClient = threadIndexForClient;
//            this.handler = handler;
//            this.random = new Random(nextInt(Integer.MAX_VALUE));
//            //this.clientInstanceIndex = clientInstanceIndex;
//        }
//
//        public void run() {
//            try {
//                FarmInvoker invoker = factory.getInvoker(id);
//                int startIdAt = threadIndexForClient*MAX_TASK_SIZE+1;
//                for (int i = 0; i < taskSize; i++) {
//                    int time = 0;
//                    RuntimeException exception = null;
//                    int procType = random.nextInt(3);
//                    int memIndex = random.nextInt(3);
//                    int osTypeIndex = random.nextInt(3);
//                    InvocationRequest request = buildInvocationRequest(procType, memIndex, osTypeIndex, startIdAt+i, id, threadIndexForClient, "None", time, exception);
//                    try {
//                        if (random.nextInt(5) == 0) {
//                            handler.addSyncInvocation(request);
//                            AsyncInvocationResponse response = invoker.scheduleInvocationSync(request);
//                            InvocationResponse responseValue = response.get(TIME_TO_WAIT_FOR_SYNC);
//                            handler.addSyncResult(responseValue);
//                        } else {
//                            handler.addInvocation(request);
//                            invoker.scheduleInvocation(request);
//                        }
//                    } catch (Exception e) {
//                        handler.addExceptionInvoking(request, e);
//                    }
//                    Thread.sleep(50);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            System.out.println("Exiting thread "+id+"-"+threadIndexForClient);
//        }
//    }
//
//
//    public static class Handler implements InvocationResultHandler {
//        String clientId;
//        Map<Object, RequestData>  requestEvents = new HashMap();
////        Map resultEvents = Collections.synchronizedMap(new HashMap());
////        List invocations = Collections.synchronizedList(new LinkedList());
////        List duplicateEvents = new LinkedList();
//
//        public Handler(String clientId) {
//            this.clientId = clientId;
//        }
//
//        public void addExceptionInvoking(InvocationRequest request, Exception e) {
//            addResultEvent(request.getId(), new Event(Event.EXCEPTION, request, e));
//        }
//
//        public void addSyncResult(InvocationResponse response) {
//            addResultEvent(response.getRequestId(), new Event(Event.RESPONSE_SYNC, response));
//        }
//
//        public boolean handleResult(InvocationResponse response) {
//            addResultEvent(response.getRequestId(), new Event(Event.RESPONSE_ASYNC, response));
//            return true;
//        }
//
//        public void addSyncInvocation(InvocationRequest request) {
//            requestEvents.put(request.getId(), new RequestData(Event.INVOCATION_SYNC, request));
//            //invocations.add(new Event(Event.INVOCATION_SYNC, request));
//        }
//
//        public void addInvocation(InvocationRequest request) {
//            requestEvents.put(request.getId(), new RequestData(Event.INVOCATION_ASYNC, request));
//            //invocations.add(new Event(Event.INVOCATION_ASYNC, request));
//        }
//
//        private void addResultEvent(Object id, Event e) {
//            RequestData data = requestEvents.get(id);
//            data.addEvent(e);
////            Object object = resultEvents.put(id, event);
////            if (object != null) {
////                duplicateEvents.add(object);
////            }
//        }
//
//        public String getClientId() {
//            return clientId;
//        }
//
//    }
//
//    public static class Event {
//        public static final int RESPONSE_ASYNC = 1;
//        public static final int RESPONSE_SYNC = 2;
//        public static final int INVOCATION_SYNC = 3;
//        public static final int INVOCATION_ASYNC = 4;
//        public static final int EXCEPTION = 5;
//
//        int type;
//        Object value;
//        Exception ex;
//        Date date;
//
//        public Event(int type, Object value) {
//            this.type = type;
//            this.value = value;
//            this.date = new Date();
//        }
//        public Event(int type, Object value, Exception ex) {
//            this.type = type;
//            this.value = value;
//            this.date = new Date();
//            this.ex = ex;
//        }
//
//        public String toString() {
//            return "event{type="+type+", value="+value+", date="+date + ", ex="+ex+"}";
//        }
//    }
//
//
//    public static class RequestData {
//        int requestType;
//        InvocationRequest request;
//        LinkedList<Event> events = new LinkedList<Event>();
//
//        public RequestData(int type, InvocationRequest request) {
//            this.requestType = type;
//            this.request = request;
//        }
//
//        public synchronized void addEvent(Event e) {
//            events.add(e);
//        }
//
//        public String toString() {
//            return "requestdata{requestType=" + requestType + ", requeriments="
//                    + request.getRequirements() + ", request=" + request
//                    + ", events=" + events + "}";
//        }
//    }
//
//    public static class ErrorData {
//        String msg;
//        RequestData data;
//
//        public ErrorData(String msg, RequestData data) {
//            super();
//            this.msg = msg;
//            this.data = data;
//        }
//
//        public String toString() {
//            return "(msg="+msg+", data="+data+"}";
//        }
//    }
//
}
