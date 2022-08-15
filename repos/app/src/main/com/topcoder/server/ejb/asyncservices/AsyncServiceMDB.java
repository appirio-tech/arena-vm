/*
 * AsyncServiceMDB
 * 
 * Created 07/25/2007
 */
package com.topcoder.server.ejb.asyncservices;

import java.lang.reflect.InvocationTargetException;

import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.naming.InitialContext;

import com.topcoder.shared.util.logging.Logger;

/**
 * Message driven bean responsible for handling asynchronous service calls.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: AsyncServiceMDB.java 74113 2008-12-29 19:34:43Z dbelfer $
 */
public class AsyncServiceMDB implements MessageListener, MessageDrivenBean  {
    private static final Logger log = Logger.getLogger(AsyncServiceMDB.class);
    private static final AsyncServiceInvoker invoker = new AsyncServiceInvoker();
    private MessageDrivenContext ctx;
    private QueueConnection conn;
    private QueueSession session;
    
    public void onMessage(Message srcMsg) {
        if (log.isDebugEnabled()) {
            log.debug("Processing new incoming message: "+srcMsg);
        }
        try {
            InvocationRequest invocation = extractInvocation(srcMsg);
            InvocationResponse response = processInvocation(srcMsg, invocation);
            if (response != null) {
                sendResponse(srcMsg, response);
            }
        } catch (JMSException e) {
            log.error("Exception while sending response" , e);
            log.error(srcMsg);
            throw new EJBException("Exception while sending response, forcing MDB removal");
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Message processed");
            }
        }
    }

    private InvocationResponse processInvocation(Message srcMsg, InvocationRequest invocation) {
        try {
            String jndiName = invocation.getJndiName();
            Class homeInterfaceClass = invocation.getHomeInterfaceClass();
            Class serviceInterfaceClass = invocation.getServiceInterfaceClass();
            String methodName = invocation.getMethodName();
            Class[] paramTypes = invocation.getParamTypes();
            Object[] args = invocation.getArgs();
            if (log.isDebugEnabled()) {
                log.debug("Invoking jndiName="+jndiName+", homeClass="+homeInterfaceClass+", serviceClass="+serviceInterfaceClass+", methodName="+methodName+", paramCount="+paramTypes.length);
            }
            Object result = invoker.invoke(jndiName, homeInterfaceClass, serviceInterfaceClass, methodName, paramTypes, args);
            if (invocation.mustSendResult()) {
                return buildResultResponse(srcMsg, invocation, result);
            } else if (invocation.mustSendAckWhenFinished()){
                return buildAckResponse(srcMsg, invocation);
            }
            return null;
        } catch (InvocationTargetException e) {
            return buildTargetExceptionResponse(srcMsg, invocation,e );
        } catch (ServiceDefinitionException e) {
            return buildServiceDefinitionExceptionResponse(srcMsg, invocation, e);
        } 
    }

    private InvocationRequest extractInvocation(Message srcMsg) {
        try {
            ObjectMessage src = (ObjectMessage) srcMsg;
            return (InvocationRequest) src.getObject();
        } catch (Exception e) {
            log.error("Exception while receiving async call", e);
            log.error(srcMsg);
            buildAsyncServiceExceptionResponse(srcMsg, null, e);
            throw new EJBException("Failed during reception, forcing MDB removal");
        }
    }
    
    private InvocationResponse buildAckResponse(Message srcMsg, InvocationRequest invocation) {
        return new InvocationResponse(InvocationResponse.TYPE_ACK, null);
    }

    private InvocationResponse buildResultResponse(Message srcMsg, InvocationRequest invocation, Object result) {
        return new InvocationResponse(InvocationResponse.TYPE_RETURN_VALUE, result);
    }

    private InvocationResponse buildTargetExceptionResponse(Message srcMsg, InvocationRequest invocation, InvocationTargetException e) {
        if (invocation.mustSendResult() || invocation.mustSendAckWhenFinished()) {
            return new InvocationResponse(InvocationResponse.TYPE_TARGET_EXCEPTION, (Exception) e.getTargetException());
        }
        return null;
    }

    private InvocationResponse buildAsyncServiceExceptionResponse(Message srcMsg, InvocationRequest invocation, Exception e) {
        return new InvocationResponse(InvocationResponse.TYPE_ASYNC_SERVICE_EXCEPTION, e.getMessage());
    }

    private InvocationResponse buildServiceDefinitionExceptionResponse(Message srcMsg, InvocationRequest invocation, Exception e)  {
        return new InvocationResponse(InvocationResponse.TYPE_SEVICE_DEFINITION_ERROR, e);
    }

    private void sendResponse(Message srcMsg, InvocationResponse response) throws JMSException {
        Queue replyQueue = (Queue) srcMsg.getJMSReplyTo();
        if (log.isDebugEnabled()) {
            log.debug("sending response to queue="+replyQueue+". resultType="+response.getResultType()+" result="+response.getResult());
        }
        QueueSender sender =  session.createSender(replyQueue);
        ObjectMessage message = session.createObjectMessage();
        message.setJMSCorrelationID(srcMsg.getJMSMessageID());
        message.setObject(response);
        sender.send(message);
    }

    public void ejbCreate() {
        try {
            InitialContext iniCtx = new InitialContext();
            QueueConnectionFactory qcf = (QueueConnectionFactory) iniCtx.lookup("java:comp/env/jms/QueueConnectionFactory");
            conn = qcf.createQueueConnection();
            session = conn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
            conn.start();
        } catch (Exception e) {
            throw new EJBException("Failed to initialize connection factory for AsyncService", e);
        }
    }

    public void ejbRemove() throws EJBException {
        if (ctx != null) {
            ctx = null;
        }
        try {
            if (session != null) {
                session.close();
            }
        } catch (Exception e) {
            log.error("Falied to close session", e);
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch(JMSException e) {
            log.error("Falied to close connection", e);
        }
    }

    public void setMessageDrivenContext(MessageDrivenContext ctx) throws EJBException {
        this.ctx = ctx;
    }
}
