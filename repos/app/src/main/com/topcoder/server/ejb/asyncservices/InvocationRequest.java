/*
 * InvocationRequest
 * 
 * Created Jul 26, 2007
 */
package com.topcoder.server.ejb.asyncservices;

import java.io.Serializable;

/**
 * An async invocation request.
 * 
 * @autor Diego Belfer (Mural)
 * @version $Id: InvocationRequest.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class InvocationRequest implements Serializable {
    private String jndiName;
    private boolean mustSendAckWhenFinished;
    private boolean mustSendResult = true;
    private Object[] args;
    private Class[] paramTypes;
    private String methodName;
    private Class serviceInterfaceClass;
    private Class homeInterfaceClass;
    
    public InvocationRequest() {
    }
    
    public InvocationRequest(String jndiName, Class homeInterfaceClass, Class serviceInterfaceClass, String methodName, Class[] paramTypes, Object[] args) {
        this.jndiName = jndiName;
        this.homeInterfaceClass = homeInterfaceClass;
        this.serviceInterfaceClass = serviceInterfaceClass;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.args = args;
    }

    public String getJndiName() {
        return jndiName;
    }

    public boolean mustSendAckWhenFinished() {
        return mustSendAckWhenFinished;
    }

    public boolean mustSendResult() {
        return mustSendResult;
    }
    
    public Object[] getArgs() {
        return args;
    }

    public Class[] getParamTypes() {
        return paramTypes;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class getServiceInterfaceClass() {
        return serviceInterfaceClass;
    }

    public Class getHomeInterfaceClass() {
        return homeInterfaceClass;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public void setHomeInterfaceClass(Class homeInterfaceClass) {
        this.homeInterfaceClass = homeInterfaceClass;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setMustSendAckWhenFinished(boolean mustSendAckWhenFinished) {
        this.mustSendAckWhenFinished = mustSendAckWhenFinished;
    }

    public void setMustSendResult(boolean mustSendResult) {
        this.mustSendResult = mustSendResult;
    }

    public void setParamTypes(Class[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public void setServiceInterfaceClass(Class serviceInterfaceClass) {
        this.serviceInterfaceClass = serviceInterfaceClass;
    }
}