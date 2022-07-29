package com.topcoder.client.netClient;

import java.io.EOFException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.topcoder.client.contestant.message.MessageProcessor;
import com.topcoder.netCommon.contestantMessages.response.BaseResponse;

final class ContestResponseHandler extends Thread {
    private final Client client;
    
    /**
     * NetMessageProcessor used to handle incoming net messages
     */
    private NetMessageProcessor netProcessor;
    
    private final MessageProcessor gui;
    private final DispatchThread dispatchThread = new DispatchThread();

    private volatile boolean keepGoing = true;

    ////////////////////////////////////////////////////////////////////////////////
    ContestResponseHandler(Client client, NetMessageProcessor netProcessor, MessageProcessor gui)
    ////////////////////////////////////////////////////////////////////////////////
    {
        super("ContestResponseHandler");
        this.client = client;
        this.netProcessor = netProcessor;
        this.gui = gui;
    }

    public void run() {
        boolean lostConnection = false;

        while (keepGoing) {
            try {
                Object r = client.readObject();
                // mural - we can manage all kind of responses 
                //(or maybe not, but we don't know in this level 
                dispatchThread.processNewResponses(r);
            } catch (SocketTimeoutException e) {
                //ReadTimeOut, let's try again
                continue;
            } catch (SocketException e) {
                // Client probably just closed the socket connection... let's exit.            	
            	debug("connection lost:", e);
                lostConnection = true;
                break;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
                continue;
            } catch (EOFException e) {
            	debug("connection lost:", e);
                lostConnection = true;
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                continue;
                /*
            } catch (OptionalDataException e) {
              e.printStackTrace();
              continue;
              */
            } catch (IOException e) {
                /*
                 * When http tunnel option is in use a ChunkedInputStream is used. This implementation
                 * throws IOException when connection is abnormally closed by peer
                 */
                if (e.getMessage() != null && (e.getMessage().toLowerCase().indexOf("stream closed") > -1 || e.getMessage().toLowerCase().indexOf("stream is closed") > -1 || 
                                                e.getMessage().toLowerCase().indexOf("premature eof") > -1)) {
                	debug("connection lost:", e);
                    lostConnection = true;
                    break;
                }
                e.printStackTrace();
                continue;
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        if (keepGoing && lostConnection) {
            gui.lostConnection();
        }
    }


    void close() {
        keepGoing = false;
        dispatchThread.stopRunning();
    }

    /**
     * Method used for logging on develpment stage
     */
    void debug(String text, Object object) {
//        if (object instanceof Exception) {
//            System.out.println(text);
//            ((Exception) object).printStackTrace();
//        } else {
//            System.out.println(text + object);
//        }
    }

    private class DispatchThread extends Thread {

        private final Object lock = new Object();
        private final List responseQueue = Collections.synchronizedList(new ArrayList());
        private boolean keepGoing = true;

        private DispatchThread() {
            super("DispatchThread");
            start();
        }


        public void run() {
            synchronized (lock) {

                // Keep going...
                while (keepGoing) {

                    // If we have nothing in the response queu
                    if (responseQueue.size() == 0) {
                        // Wait for one second (or until notified)
                        // The timeout is because between the size check above
                        // and the wait - something could have come in and not
                        // have been processed - the timeout allows it to be processed
                        try {
                            lock.wait(1000);
                        } catch (Throwable t) {
                        }
                    }

                    while (responseQueue.size() > 0) {
                        try {
                            Object response = responseQueue.remove(0);
                            if (response instanceof List) {
                                List responses = (List) response;
                                for (Iterator it = responses.iterator(); it.hasNext();) {
                                    processResponse(it.next());
                                }
                            } else {
                                processResponse(response);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        
        /**
         * Process the specified response .
         *
         * @param response to be proccessed
         */
        private void processResponse(Object response) {
        	debug("received: ", response);

        	//If the netProcessor cannot process it, pass it to MessageProcessor 
            if (!netProcessor.processIncomingMessage(response)) {
                gui.receive((BaseResponse) response);
            }
        }
		
        private void processNewResponses(Object response) {
		    // Add the response to the queue
		    responseQueue.add(response);
		
		    // Notify the thread something is waiting
		    notifyIt();
		
		}
		
		private void stopRunning() {
		    // Set the flag to stop
		    keepGoing = false;
		
		    // Notify the thread
		    notifyIt();
		
		}
		
		private void notifyIt() {
		    // Grab the object monitor and notify
		    synchronized (lock) {
		        lock.notify();
		    }
		}
    }
}
