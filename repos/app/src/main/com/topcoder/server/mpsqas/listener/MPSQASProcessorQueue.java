package com.topcoder.server.mpsqas.listener;

import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.server.mpsqas.listener.impl.MPSQASProcessorPeer;

import java.util.LinkedList;

import org.apache.log4j.Logger;

public class MPSQASProcessorQueue
        extends Thread {

    protected LinkedList messageQueue = new LinkedList();
    private boolean connectionLost = false;

    class MessagePair {

        private MessageProcessor processor;
        private MPSQASProcessorPeer peer;

        public MessagePair(MessageProcessor processor, MPSQASProcessorPeer peer) {
            this.processor = processor;
            this.peer = peer;
        }
    }

    public MPSQASProcessorQueue() {
    }

    private Object lock = new Object();
    
    public void run() {
        while (!isInterrupted()) {
            try {
                synchronized(lock) {
                    lock.wait(300);
                }
                MessagePair pair = null;
                synchronized(messageQueue){
                    if (messageQueue.size() == 0){
                        if(connectionLost){
                            Logger.getLogger(getClass()).info("Connection Lost, killing queue");
                            return;
                        }
                        continue;
                    }
                    pair = (MessagePair) messageQueue.removeFirst();
                }
                Logger.getLogger(getClass()).info("Processing message: " + pair.processor);
                try {
                    pair.processor.process(pair.peer);
                } catch (Exception ex) {
                    Logger.getLogger(getClass()).error("MPSQASProcessorQueue.run: error processing message", ex);
                }
            } catch (InterruptedException e) {
                Logger.getLogger(getClass()).error("MPSQASProcessorQueue.run: interrupted", e);
            }
        }
    }

    public void add(MessageProcessor processor, MPSQASProcessorPeer peer) {
        synchronized (messageQueue) {
            messageQueue.add(new MessagePair(processor, peer));
            synchronized(lock) {
                lock.notifyAll();
            }
        }
    }
    public void connectionLost(){
        connectionLost = true;
    }
}
