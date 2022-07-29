/*
 * BusRemoteInvoker
 * 
 * Created Oct 26, 2007
 */
package com.topcoder.shared.messagebus.invoker;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.topcoder.shared.messagebus.BusException;
import com.topcoder.shared.messagebus.BusMessage;
import com.topcoder.shared.messagebus.BusRequestPublisher;
import com.topcoder.shared.util.concurrent.NullFuture;
import com.topcoder.shared.util.concurrent.ResultConverterFutureDecorator;
import com.topcoder.shared.util.logging.Logger;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class BusRemoteInvoker {
    private Logger log = Logger.getLogger(getClass());
    private String moduleName;
    private BusRequestPublisher requester;
    private RequestConverter requestConverter;
    private ResponseConverter responseConverter;
    private String namespace;

    public BusRemoteInvoker(String moduleName, String namespace, BusRequestPublisher requester) {
        this.moduleName = moduleName;
        this.namespace = namespace;
        this.requester = requester;
        this.requestConverter = new RequestConverter(this.moduleName, InvokerConstants.REQUEST_MSG_TYPE, namespace);
        this.responseConverter = new ResponseConverter(this.moduleName, InvokerConstants.RESPONSE_MSG_TYPE, namespace);
    }
    
    
    public <T> Future<T> invoke(String action, Map arguments) throws RemoteInvokerException {
        return invoke(Request.REQUEST_GET_RESULT, action, arguments);
    }
    
    public Future<Void> invokeVoid(String action, Map arguments) throws RemoteInvokerException {
        return invoke(Request.REQUEST_DROP_RESULT, action, arguments);
    }

    public <T> Future<T> invokeAck(String action, Map arguments) throws RemoteInvokerException {
        return invoke(Request.REQUEST_ACK_RECEIVED, action, arguments);
    }
    
    public void invokeAsync(String action, Map arguments) throws RemoteInvokerException {
        invoke(Request.REQUEST_ASYNC, action, arguments);
    }

    private <T> Future<T> invoke(int requestType, String action, Map arguments) throws RemoteInvokerException {
        if (log.isDebugEnabled()) {
            log.debug("Generating request requestType="+requestType+", action="+action+" arguments="+arguments);
        }
        Request request = new Request(requestType, namespace, action, arguments);
        BusMessage msg = requestConverter.toMessage(request);
        try {
            if (requestType != Request.REQUEST_ASYNC) {
                Future<BusMessage> future = requester.request(msg);
                return new ResultConverterFutureDecorator<BusMessage, T>(future) {
                    protected T convertResult(BusMessage value) throws ExecutionException {
                        Response response = responseConverter.fromMessage(value);
                        if (response.isSucceeded()) {
                            return (T) response.getResult();
                        } else {
                            if (response.isException()) {
                                throw new ExecutionException(new RemoteInvocationException("Execution failed at remote location", (ExceptionData) response.getResult()));
                            } else {
                                throw new IllegalStateException("Non valid response type="+response.getResponseType());
                            }
                        }
                    } 
                };
            } else {
                requester.publish(msg);
                return new NullFuture<T>();
            }
        } catch (BusException e) {
            throw new RemoteInvokerException("Could not send request",e);
        }
    }
    
    public void release() {
        requester.close();
    }
}
