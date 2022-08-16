/*
 * AsyncServiceClientProvider
 * 
 * Created 07/30/2007
 */
package com.topcoder.server.ejb.asyncservices;

import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.TCContext;
import com.topcoder.shared.util.VMUtil;

/**
 * @author Diego Belfer (mural)
 * @version $Id: AsyncServiceClientProvider.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class AsyncServiceClientProvider {
    private static Map invokers = new HashMap();
    
    public static synchronized AsyncServiceClientInvoker getClientInvoker(String hostURL) throws NamingException {
        AsyncServiceClientInvoker invoker = (AsyncServiceClientInvoker) invokers.get(hostURL); 
        if (invoker == null) {
            invoker = new AsyncServiceClientInvoker(
                    TCContext.getInitial(hostURL), 
                    DBMS.JMS_FACTORY, 
                    "queue/asyncServiceRequests", "queue/asyncServiceTo"+VMUtil.getVMInstanceId()+"Responses", 
                    new AsyncServiceClientInvoker.AsyncResponseHandler() {
                            public void timeout(Object responseId) {
                                System.out.println("Timeout :"+responseId);
                            }
                        
                            public void succeeded(Object responseId, Object object) {
                                System.out.println("succeeded :"+responseId +" result: "+object);        
                            }
                        
                            public void invocationFailed(Object responseId, Exception e) {
                                System.out.println("invocationFailed :"+responseId);
                        
                            }
                        
                            public void exceptionThrown(Object responseId, Exception e) {
                                System.out.println("exception :"+responseId);
                                e.printStackTrace(System.out);
                            }
                        
                            public void asyncServiceFailure(Object responseId) {
                                System.out.println("Failure :"+responseId);
                        
                            }
                    }
            );
            invokers.put(hostURL, invoker);
        }
        return invoker;
        
    }
}
