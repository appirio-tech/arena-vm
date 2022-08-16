/*
 * Test
 * 
 * Created 06/29/2006
 */
package com.topcoder.farm.test.dev;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.topcoder.farm.client.ClientConfiguration;
import com.topcoder.farm.client.invoker.FarmFactory;
import com.topcoder.farm.client.invoker.FarmInvoker;
import com.topcoder.farm.controller.api.InvocationRequest;
import com.topcoder.farm.shared.invocation.InvocationRequirements;
import com.topcoder.farm.test.dev.TestInvocation;
import com.topcoder.farm.test.integ.IntegConstants;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class Test {
    private static FarmFactory factory;

    public static void main(String[] args) throws Exception {
        //207 con 50 clients *100 tareas

        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setAddresses(new InetSocketAddress[] {new InetSocketAddress(InetAddress.getLocalHost(), IntegConstants.CONTROLLER_CLIENT_PORT)});
        FarmFactory.configure(configuration);
        factory = FarmFactory.getInstance();
        try {
//            FarmInvoker invoker = factory.getInvoker("CL1");
//            invoker.cancelPendingRequests("");
            
            long st = System.currentTimeMillis();
            for (int i = 0; i < 1; i++) {
                runTest();
            }
            System.in.read();
            System.out.println(System.currentTimeMillis()-st);
        } finally {
            FarmFactory.releaseInstance();
        }
    }


    private static void runTest() throws InterruptedException {
        int size = 1;
        int tsize = 1;
        XThread[][] ts = new XThread[size][tsize];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < tsize; j++) {
                ts[i][j] = new XThread("CL"+(i+1), j);
            }
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < tsize; j++) {
                ts[i][j].start();
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < tsize; j++) {
                ts[i][j].join();
            }
        }
//        Thread.sleep(1000);
//        for (int i = 0; i < size; i++) {
//            FarmInvoker invoker = factory.getInvoker("TTT-X"+i);
//            invoker.getClientNode().requestPendingResponses();
//        }

    }

    
    static InvocationRequest buildInvocationRequest(String suffix) {
        InvocationRequest request = new InvocationRequest();
        request.setId(suffix);
        request.setRequirements(new InvocationRequirements());
        request.setInvocation(new TestInvocation(Thread.currentThread().getName()+"-"+suffix));
        return request;
    }
    
    public static class XThread extends Thread {
        private static final int TASK_SIZE = 200;
        private String name;
        private int tid;
        
        public XThread(String name, int threadNum) {
            super(name+"_"+threadNum);
            this.name = name;
            this.tid = threadNum;
        }

        public void run() {
            StringBuffer sb = new StringBuffer(5000);
            for(int j =0; j< 500; j ++) {
                sb.append("1234567890");
            }
            String value = sb.toString();
            try {
                FarmInvoker invoker = factory.getInvoker(name);
                for(int j =0; j< 500; j ++) {
                    invoker.storeSharedObject("OO"+j, value);
                    for (int i = 0; i < TASK_SIZE; i++) {
                        InvocationRequest request = buildInvocationRequest("II"+i+"-"+j+"-TD"+tid);
                        request.addSharedObjectRef("test", "OO"+j);
                        invoker.scheduleInvocation(request);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
